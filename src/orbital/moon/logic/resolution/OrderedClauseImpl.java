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
 * @todo 11 does ordered resolution necessitate a priori factorization instead of a posteriori factorization after successful resolution (as we do it)
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class OrderedClauseImpl extends IndexedClauseImpl {
    private static final Logger logger = Logger.getLogger(OrderedClauseImpl.class.getName());

    /**
     * The L-order applied for ordered resolution.
     * @see LiteralOrders
     */
    private static final Comparator order = LiteralOrders.DEPTH_ORDER;
    
    /**
     * Get all maximal elements of the given set.
     * @postconditions RES = {x&isin;S &brvbar; &not;&exist;y&isin;S y&gt;x}
     */
    private static final /*<T>*/ Set/*_<T>_*/ getMaximalElements(final Collection/*_<T>_*/ S,
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
        //@internal actually greaterEqual_partial = greater_partial since the order is irreflexive
        final BinaryPredicate greaterEqual_partial = new BinaryPredicate() {
                    public boolean apply(Object a, Object b) {
                        try {
                            return cmp.compare(a, b) >= 0;
                        }
                        catch (IncomparableException notgreater) {
                            return false;
                        }
                    }
            };
        //@internal here it is irrelevant whether or not we use a linked HashSet
        final Set maximals = new LinkedHashSet();
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
        if (false) Setops.all(Setops.cross(S, maximals), new Predicate() {
                public boolean apply(Object o) {
                    Pair p = (Pair)o;
                    System.err.print(">>> " + p.A + "  \t>< " + p.B + "  \t");
                    try {
                        int s = order.compare(p.A, p.B);
                        System.err.println(s < 0 ? "<" : s > 0 ? ">" : "=");
                    }
                    catch (IncomparableException incomparable) {
                        System.err.println("incomparable");
                    }
                    return true;
                }
            });
        assert Setops.all(Setops.difference(new LinkedHashSet(S), maximals), new Predicate() { public boolean apply(final Object y) { return Setops.some(maximals, Functionals.bindSecond(greaterEqual_partial, y)); } }) : "all elements y of " + S + " \\ maximals have an x in maximals=" + maximals + " with x >= y";
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
        logger.log(Level.FINEST, "about to resolve literals\n ~neg\t{0}\n  in\t{2}\n with\t{1}\n  in\t{3} ...", new Object[] {L, K, F, G});
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
        return Setops.intersection(Setops.asSet(getMaximalLiterals().iterator()),
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
