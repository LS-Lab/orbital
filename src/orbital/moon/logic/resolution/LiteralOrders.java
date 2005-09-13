/**
 * @(#)LiteralOrders.java 1.2 2004-01-21 Andre Platzer
 *
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;


import orbital.logic.imp.*;
import orbital.util.*;
import java.util.*;
import orbital.logic.functor.*;

import orbital.moon.logic.ClassicalLogic;
import orbital.moon.logic.UniqueSymbol;
import orbital.logic.sign.*;
import orbital.logic.sign.Expression.Composite;
import orbital.logic.sign.type.*;
import orbital.logic.trs.Substitution;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Provides literal L-orders.  <p id="theory"> An <dfn>L-order</dfn>
 * is an irreflexive, transitive relation on literals which is
 * substitutive, i.e. all substitutions are monotonic:
 * <center>&forall;&sigma;&isin;SUB (p&lt;q &imply; p&sigma;&lt;q&sigma;)</center>
 * L-orders are always partial because unifiable literals are incomparable.
 * </p>
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class LiteralOrders {
    /**
     * The symbols of the logical junctors.
     */
    private static final Function NOT;
    static {
        //@note assuming the symbols and notation of ClassicalLogic, here
        final Logic logic = new ClassicalLogic();
        final Signature core = logic.coreSignature();
        // we also avoid creating true formulas, it's (more or less) futile
        //@xxx we need some valid non-null arguments.
        final Formula B = (Formula) logic.createAtomic(new SymbolBase("B", SymbolBase.BOOLEAN_ATOM));
        Formula[] arguments = {B};
        Symbol NOTs = core.get("~", arguments);
        assert NOTs != null : "operators in core signature";
        NOT = (Function) ((Formula)logic.createAtomic(NOTs)).apply(logic.coreInterpretation());
    }

    /**
     * L-Order based on term-depths. <p> This implementation compares
     * for maximal overall term depth in favour of maximum individual
     * variable depths.  <center>p&lt;q :&hArr; &tau;(p)&lt;&tau;(q)
     * &and; &forall;x&isin;Var(p)
     * &tau;(x,p)&lt;&tau;(x,q)</center></p> where &tau;(p) is the
     * maximal term depth in d, and &tau;(x,p) is the maximum depth of
     * the occurrences of x in p.
     * @internal using this order results in resolvents that tend to shrink in term depth.
     * @fixme does not always lead to the right results: ~r(_x97105,_x97106) < r(s1435,_x97106)
     */
    public static final Comparator DEPTH_ORDER = new Comparator() {
            public int compare(Object _p, Object _q) throws IncomparableException {
                final Formula p = (Formula)_p;
                final Formula q = (Formula)_q;
                //@todo 28 could cache depths for the formulas during the computation of getMaximalLiterals, instead of re-evaluating the depths over and over again. Furthermore, those depths can help to realise a quicker occurs-check.
                final Map depthsP = computeMaximalDepths(p);
                final Map depthsQ = computeMaximalDepths(q);
                int order = ((Integer)depthsP.get(MAXIMAL_OVERALL_DEPTH)).intValue()
                    - ((Integer)depthsQ.get(MAXIMAL_OVERALL_DEPTH)).intValue();
                if (order < 0) {
                    return compareSmaller(p, depthsP, q, depthsQ);
                } else if (order > 0) {
                    return -compareSmaller(q, depthsQ, p, depthsP);
                } else {
                    throw new IncomparableException("incomparable L-orders because of equal maximal term depths", p, q);
                }
            }

            /**
             * @returns whether &forall;x&isin;Var(p)
             * &tau;(x,p)&lt;&tau;(x,q).
             * @preconditions depthsP.get(MAXIMAL_OVERALL_DEPTH) < depthsQ.get(MAXIMAL_OVERALL_DEPTH)
             *  &and; depthsP.equals(computeMaximalDepths(p))
             *  &and; depthsQ.equals(computeMaximalDepths(q))
             */
            private int compareSmaller(Formula p, Map depthsP,
                                       Formula q, Map depthsQ) throws IncomparableException {
                for (Iterator i = depthsP.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry e = (Map.Entry) i.next();
                    Integer tauxq = (Integer)depthsQ.get(e.getKey());
                    if (tauxq != null && ((Integer)e.getValue()).compareTo(tauxq) < 0) {
                        continue;
                    } else {
                        //@internal besides, the order of p and q might be wrong
                        throw new IncomparableException("incomparable because of different variable occurrence depths", p, q);
                    }
                }
                return -1;
            }
        };

    /**
     * Computes the maximal term depths of variables occuring in f.
     * @see #MAXIMAL_OVERALL_DEPTH
     */
    private static Map/*_<Symbol,Integer>_*/ computeMaximalDepths(Formula f) {
        Map depths = new HashMap();
        computeMaximalDepthsHelper(f, depths, 0);
        return depths;
    }
    /**
     * Special trick symbol which is only used for encoding the
     * maximal overall term depth &tau;(p) of any term in the values
     * returned from {@link #computeMaximalDepths(Formula)}.
     * @invariants &forall;x (x!=MAXIMAL_OVERALL_DEPTH &imply; &not;MAXIMAL_OVERALL_DEPTH.equals(x))
     */
    private static final Symbol MAXIMAL_OVERALL_DEPTH = new UniqueSymbol("any_depth_trick", Types.TRUTH, null, false);
    private static final void computeMaximalDepthsHelper(Formula f, Map/*_<Symbol,Integer>_*/ depths, int currentDepth) {
        if (f instanceof Composite) {
            // true decomposition case
            final Composite c = (Composite) f;
            final Object    g = c.getCompositor();
            //@internal ignore negations, since we are just an A-order, not a proper L-order
            int newDepth = g == NOT ? currentDepth : currentDepth + 1;
            if (g instanceof Formula) {
                computeMaximalDepthsHelper((Formula)g,
                    depths,
                    newDepth);
            }
            final Collection xs = Utility.asCollection(c.getComponent());
            for (Iterator xi = xs.iterator(); xi.hasNext(); ) {
                computeMaximalDepthsHelper((Formula)xi.next(),
                    depths,
                    newDepth);
            }
        } else {
            {
                Integer depth = (Integer)depths.get(MAXIMAL_OVERALL_DEPTH);
                if (depth == null || depth.intValue() < currentDepth) {
                    // memorize the maximal depth of any term
                    depths.put(MAXIMAL_OVERALL_DEPTH, new Integer(currentDepth));
                }
            }

            Set v = f.getFreeVariables();
            assert v.size() <= 1 : "atomic expressions have at most one (free) variable";
            if (v.isEmpty()) {
                // f is no atomic variable
                return;
            }
            Symbol s = (Symbol)v.iterator().next();
            assert f.equals(new ClassicalLogic().createAtomic(s)) : "assume that only atomic expressions are noncomposed";
            if (s.isVariable()) {
                Integer depth = (Integer)depths.get(s);
                if (depth == null || depth.intValue() < currentDepth) {
                    // memorize the maximal depth of an occurrence of s
                    depths.put(s, new Integer(currentDepth));
                }
            }
        }
    }


    /**
     * Lexicographic L-Order.
     * <p> This implementation compares according to
     * f(t<sub>1</sub>,...,t<sub>n</sub>) &lt; g(s<sub>1</sub>,...,s<sub>m</sub>)
     * :&hArr; f&lt;g &or; (f=g &and; &forall;i=1,...,n t<sub>i</sub>&lt;s<sub>i</sub>)
     *  &and; Vars(f(t<sub>1</sub>,...,t<sub>n</sub>))&sube;Vars(g(s<sub>1</sub>,...,s<sub>m</sub>))
     * </p>
     * @todo also check documentation
     */
    private static final Comparator LEXICOGRAPHIC_ORDER = null;
}
