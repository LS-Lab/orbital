/**
 * @(#)OrderedClauseImpl.java 1.2 2004-01-15 Andre Platzer
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
 * Implementation of a representation of a clause performing ordered
 * resolution.
 * @todo 25 The literals in the clause will be kept topologically
 * sorted according to the descending order.
 * @version 1.2, 2004-01-15
 * @author  Andr&eacute; Platzer
 */
public class OrderedClauseImpl extends IndexedClauseImpl {
    private static final Logger logger = Logger.getLogger(OrderedClauseImpl.class.getName());
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
     * The L-order applied for ordered resolution. An
     * <dfn>L-order</dfn> is an irreflexive, transitive relation on
     * literals which is substitutive, i.e. all substitutions are
     * monotic: &forall;&sigma;&isin;SUB (p&lt;q &imply;
     * p&sigma;&lt;q&sigma;). L-orders are always partial because
     * unifiable literals are incomparable. <p> This implementation
     * compares for maximal overall term depth in favour of maximum
     * individual variable depths.  <center>p&lt;q :&hArr;
     * &tau;(p)&lt;&tau;(q) &and; &forall;x&isin;Var(p)
     * &tau;(x,p)&lt;&tau;(x,q)</center></p> where &tau;(p) is the
     * maximal term depth in d, and &tau;(x,p) is the maximum depth of
     * the occurrences of x in p.
     */
    private static final Comparator order = new Comparator() {
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
     * Get all maximal elements of the given set.
     * @postconditions RES = {x&isin;S &brvbar; &not;&exist;y&isin;S y&gt;x}
     */
    private static /*<T>*/ final Set/*_<T>_*/ getMaximalElements(Collection/*_<T>_*/ S,
								 final Comparator cmp) {
	final BinaryPredicate greater_partial = new BinaryPredicate() {
		    public boolean apply(Object a, Object b) {
			try {
			    return cmp.compare(a, b) > 0;
			}
			catch (IncomparableException notgreater) {
			    return false;
			}
		    }
	    };
	//@internal here it is irrelevant whether or not we use a linked HashSet
	Set maximals = new LinkedHashSet();
	for (Iterator i = S.iterator(); i.hasNext(); ) {
	    final Object x = i.next();
	    // find a y with y > x
	    final Object y = Setops.epsilon(maximals,
					    Functionals.bindSecond(greater_partial, x));
	    if (y == null) {
		// remove all z with z < x, i.e. x > z
		maximals.removeAll(Setops.select(Filters.all,
						 maximals,
						 Functionals.bindFirst(greater_partial, x)));
		maximals.add(x);
	    }
	}
	return maximals;
    }


    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public OrderedClauseImpl(Set/*_<Formula>_*/ literals) {
	super(literals);
    }
    public OrderedClauseImpl() {}

    /**
     * Caching getMaximalElements().
     * @attribute lazy-initialization in {@link #getMaximalElements()}, thereafter update in all methods
     * @internal This is a performance advantage, since the index does not need to be established until the first use. This is similar to but simpler than the following idea: perhaps only activate indexing once this clause gets inserted into a clausalset. Up to this point behave like super does
     */
    private Set/*_<Formula>_*/ maximalElements = null;

    /**
     * Get all maximal literals of this clause.
     * @postconditions RES = {x&isin;S &brvbar; &not;&exist;y&isin;S y&gt;x}
     */
    private final Set/*_<Formula>_*/ getMaximalLiterals() {
	if (maximalElements == null) {
	    // lazy initialization
	    this.maximalElements = getMaximalElements(this, order);
	}
	return maximalElements;
    }

    /**
     * Update all information that we cache about this clause.
     */
    private final void update() {
	if (maximalElements != null) {
	    // update lazy initialized
	    this.maximalElements = getMaximalElements(this, order);
	}
    }
    
    /**
     * @todo 24 implementation could be optimized by far, since getProbableUnifiables already could project to maximal literals, instead of just checking in retrospect now
     */
    protected Clause resolventWith(Clause _G, Formula L, Formula K) {
	final OrderedClauseImpl F = this;
	final OrderedClauseImpl G = (OrderedClauseImpl)_G;
	// ordered resolution especially implies that the resolved literal is maximal in the original clauses F and G
	{
	    // @internal getResolvableLiterals and getProbableUnifiables have been optimized to return only maximal literals, so first two checks can be turned into mere assertions
	    final Set/*_<Formula>_*/ maximalF = F.getMaximalLiterals();
	    assert maximalF.contains(L) : "resolution ordered since " + L + " is in maximal elements\n\t" + maximalF + "\n  of\t" + F;
	    final Set/*_<Formula>_*/ maximalG = G.getMaximalLiterals();
	    assert maximalG.contains(K) : "resolution ordered since " + K + " is in maximal elements\n\t" + maximalG + "\n  of\t" + G;
	}
	final Pair p = super.resolventWith2(G, L, K);
	final Clause R = (Clause)p.B;
	if (R == null) {
	    return null;
	} else {
	    Formula Lmu = (Formula) ((Substitution)p.A).apply(L);
	    Set RLmu = new HashSet();
	    RLmu.add(Lmu);
	    RLmu.addAll(R);
	    final Set/*_<Formula>_*/ maximalRLmu = getMaximalElements(RLmu, order);
	    // only resolve if Lmu is maximal in resolvent
	    if (maximalRLmu.contains(Lmu)) {
		return R;
	    } else {
		logger.log(Level.FINER, "resolution not ordered since {0} is not in maximal elements\n\t{1}\n  of resolvent \t{2}", new Object[] {Lmu, maximalRLmu, R});
		return null;
	    }
	}
    }

    public Iterator/*_<Formula>_*/ getResolvableLiterals() {
	return getMaximalLiterals().iterator();
    }

    public Iterator/*_<Formula>_*/ getProbableUnifiables(Formula L) {
	// only return those maximal literals that qualify for resolution by indexing
	return Setops.union(Setops.asSet(getMaximalLiterals().iterator()),
			    Setops.asSet(super.getProbableUnifiables(L)))
	    .iterator();
    }


    // manage index in sync with the current data

    public boolean add(Object o) {
	//@todo 35 optimize implementation. We do not need to remove from index and add again, but only add (this,o) to the index. However this is noncritical, since due to lazy initialization these changes will usually only occur after the contents of this clause have stabilized.
	boolean changed = super.add(o);
	update();
	return changed;
    }

    public void clear() {
	super.clear();
	update();
    }

    public boolean remove(Object o) {
	boolean changed = super.remove(o);
	update();
	return changed;
    }

    public Iterator iterator() {
	return new DelegateIterator(super.iterator()) {
		private Object current = null;
		public Object next() {
		    return this.current = super.next();
		}
		public void remove() {
		    throw new UnsupportedOperationException("operation currently not yet supported");
		}
	    };
    }
}
