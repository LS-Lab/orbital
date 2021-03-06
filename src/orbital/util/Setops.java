/**
 * @(#)Setops.java 1.0 2000/02/17 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Comparator;
import orbital.logic.functor.Predicate;
import orbital.logic.functor.BinaryPredicate;
import orbital.logic.functor.BinaryFunction;
import orbital.logic.functor.Function;

import orbital.logic.functor.Functionals;
import orbital.logic.functor.Predicates;

import orbital.util.ReverseComparator;
import orbital.algorithm.Combinatorical;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.TreeSet;
import orbital.util.Pair;
import java.util.NoSuchElementException;

/**
 * Contains utility methods for common set operations and more general collection operations.
 * 
 * <p> The selection methods ({@link #select(Function, Collection,
 * Predicate, Comparator, boolean)}) encapsulate a generalization of
 * queries over Collections.  These queries are build just like data
 * queries over tables with SQL.  In a selection query, a Collection
 * is filtered to obtain the desired subset of data which matches the
 * criteria, with the order being induced by a Comparator.</p> <p>
 * With its highly flexible bulk-style data-processing operations,
 * {@link orbital.logic.functor.Functionals} is a worthwhile and
 * extremely powerful supplement to {@link Setops}.</p>
 *
 * @stereotype Utilities
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.util.Utility
 * @see orbital.algorithm.evolutionary.Selectors
 * @see java.util.Collections
 * @see orbital.logic.functor.Functionals
 * @see <a href="http://www.sql.org">Structured Query Language (SQL)</a>
 */
public final class Setops {

    /**
     * prevent instantiation - final static utility class
     */
    private Setops() {}

    /**
     * Points to java.util.RandomAccess in case that class is available at runtime.
     */
    private static final Class randomAccessClass = possiblyClassForName("java.util.RandomAccess");
    /**
     * Points to java.util.LinkedHashSet in case that class is available at runtime.
     */
    private static final Class linkedHashSetClass = possiblyClassForName("java.util.LinkedHashSet");
    private static Class possiblyClassForName(String name) {
        try {
            return Class.forName(name);
        }
        catch (ClassNotFoundException priorToJDK1_4) {
            return null;
        }
    }

    /**
     * An iterator over the empty collection.
     */
    public static final Iterator EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();

    /**
     * A list iterator over the empty list.
     */
    public static final Iterator EMPTY_LIST_ITERATOR = Collections.EMPTY_LIST.listIterator();

    /**
     * Return the first object in a collection that satisfies the specified predicate.
     * @return the first object in a collection that satisfies the specified predicate, or null if no such object exists.
     */
    public static /*<A>*/ Object/*>A<*/ find(Collection/*<A>*/ coll, Predicate/*<A>*/ found) {
        return find(coll.iterator(), found);
    }
    public static /*<A>*/ Object/*>A<*/ find(Iterator/*<A>*/ i, Predicate/*<A>*/ found) {
        while (i.hasNext()) {
            Object/*>A<*/ o = (Object/*>A<*//*__*/) i.next();
            if (found.apply(o))
                return o;
        } 
        return null;
    } 

    /**
     * Return any element of a collection that satisfies the specified predicate.
     * This method can be used to express don't care nondeterminisms in algorithms.
     * @return any object in the collection that satisfies the specified predicate, or <code>null</code> if no such object exists.
     * @see #any(Collection)
     */
    public static /*<A>*/ Object/*>A<*/ epsilon(Collection/*<A>*/ coll, Predicate/*<A>*/ found) {
        return find(coll.iterator(), found);
    }

    /**
     * Counts the number of objects in a collection that satisfy the specified predicate.
     */
    public static /*<A>*/ int count(Collection/*<A>*/ coll, Predicate/*<A>*/ cond) {
        return count(coll.iterator(), cond);
    }
    public static /*<A>*/ int count(Iterator/*<A>*/ i, Predicate/*<A>*/ cond) {
        int count = 0;
        while (i.hasNext()) {
            Object/*>A<*/ o = (Object/*>A<*//*__*/) i.next();
            if (cond.apply(o))
                count++;
        } 
        return count;
    } 

    /**
     * Checks whether all objects in a collection satisfy the specified predicate.
     * @return true if all objects satisfy the predicate, false if one does not.
     *  Returns an optimized version of <code>Functionals.map(and, Functionals.map(Functionals.asFunction(found), i))</code>.
     * @see <a href="{@docRoot}/Patterns/Design/Iterator.html">Internal Iterator</a>
     * @see orbital.logic.functor.Functionals
     * @todo document banana application @see Operations.andAll
     */
    public static /*<A>*/ boolean all(Collection/*<A>*/ coll, Predicate/*<A>*/ found) {
        return all(coll.iterator(), found);
    }
    public static /*<A>*/ boolean all(Iterator/*<A>*/ i, Predicate/*<A>*/ found) {
        // optimized version of return Functionals.map(and, Functionals.map(Functionals.asFunction(found), i))
        while (i.hasNext())
            if (!found.apply((/*__*/Object/*>A<*/)i.next()))
                return false;
        return true;
    } 
    /**
     * Checks whether all corresponding pairs of objects in two collection satisfy the specified predicate.
     * @return true if all objects satisfy the predicate, false if one does not.
     *  Returns an optimized version of <code>Functionals.map(and, Functionals.map(Functionals.asFunction(found), i), j)</code>.
     * @see <a href="{@docRoot}/Patterns/Design/Iterator.html">Internal Iterator</a>
     * @see orbital.logic.functor.Functionals
     * @todo document banana application @see Operations.andAll
     */
    public static /*<A1, A2>*/ boolean all(Collection/*<A1>*/ a, Collection/*<A2>*/ b, BinaryPredicate/*<A1, A2>*/ found) {
        return a.size() == b.size()
            && all(a.iterator(), b.iterator(), found);
    }
    public static /*<A1, A2>*/ boolean all(Iterator/*<A1>*/ i, Iterator/*<A2>*/ j, BinaryPredicate/*<A1, A2>*/ found) {
        // optimized version of return Functionals.map(and, Functionals.map(Functionals.asFunction(found), i, j))
        while (i.hasNext() && j.hasNext())
            if (!found.apply((/*__*/Object/*>A1<*/) i.next(), (/*__*/Object/*>A2<*/) j.next()))
                return false;
        if (i.hasNext() || j.hasNext())
            throw new IndexOutOfBoundsException("argument iterators must have same length");
        else
                return true;
    } 
    /**
     * Checks whether some objects (at least one) in a collection satisfy the specified predicate.
     * @return true if at least one objects satisfies the predicate, false if none does.
     *  Returns an optimized version of return <code>Functionals.map(or, Functionals.map(Functionals.asFunction(found), i))</code>.
     * @see <a href="{@docRoot}/Patterns/Design/Iterator.html">Internal Iterator</a>
     * @see orbital.logic.functor.Functionals
     * @todo document banana application @see Operations.orSome
     */
    public static /*<A>*/ boolean some(Collection/*<A>*/ coll, Predicate/*<A>*/ found) {
        return some(coll.iterator(), found);
    }
    public static /*<A>*/ boolean some(Iterator/*<A>*/ i, Predicate/*<A>*/ found) {
        // optimized version of return Functionals.map(or, Functionals.map(Functionals.asFunction(found), i))
        while (i.hasNext())
            if (found.apply((/*__*/Object/*>A<*/) i.next()))
                return true;
        return false;
    } 
    public static /*<A1, A2>*/ boolean some(Collection/*<A1>*/ a, Collection/*<A2>*/ b, BinaryPredicate/*<A1, A2>*/ found) {
        return some(a.iterator(), b.iterator(), found);
    }
    public static /*<A1, A2>*/ boolean some(Iterator/*<A1>*/ i, Iterator/*<A2>*/ j, BinaryPredicate/*<A1, A2>*/ found) {
        // optimized version of return Functionals.map(or, Functionals.map(Functionals.asFunction(found), i, j))
        while (i.hasNext() && j.hasNext())
            if (found.apply((/*__*/Object/*>A1<*/) i.next(), (/*__*/Object/*>A2<*/) j.next()))
                return true;
        if (i.hasNext() || j.hasNext())
            throw new IndexOutOfBoundsException("argument iterators must have same length");
        else
                return false;
    } 

    // arg min / arg max

    /**
     * Get the minimum argument.
     * @param choices the available choices M.
     * @param f the evaluation function f:M&rarr;<b>R</b>.
     * @return argmin<sub>a&isin;M</sub> f(a) with minimum f-value in choices.
     * @preconditions choices.hasNext()
     * @postconditions RES = argmin<sub>a'&isin;M</sub> f(a'), i.e. &forall;a'&isin;M f(RES)&le;f(a')
     * @throws NoSuchElementException if !choices.hasNext()
     * @see orbital.math.functional.Operations#inf
     * @see java.util.Collections#min(Collection,Comparator)
     * @see orbital.logic.functor.Functionals#foldLeft(BinaryFunction, Object,Object[])
     * @see orbital.algorithm.template.EvaluativeAlgorithm.EvaluationComparator
     */
    public static final /*<A>*/ Object/*>A<*/ argmin(Iterator/*<A>*/ choices, Function/*<A,Comparable>*/ f) {
        // search for minimum f in choices
        // current best choice of choices
        Object best = choices.next();
        // f(best)
        Comparable bestValue = (Comparable) f.apply(best);
        while (choices.hasNext()) {
            final Object o = choices.next();
            final Comparable value = (Comparable) f.apply(o);
            if (value.compareTo(bestValue) < 0) {
                bestValue = value;
                best = o;
            }
            assert true : "invariant: " + bestValue + "=< f-value of each choice seen so far & " + f.apply(best) + "==" + bestValue;
        }

        // return the best choice
        return best;
    }

    /**
     * Get the maximum argument.
     * @param choices the available choices M.
     * @param f the evaluation function f:M&rarr;<b>R</b>.
     * @return argmax<sub>a&isin;M</sub> f(a) with maximum f-value in choices.
     * @preconditions choices.hasNext()
     * @postconditions RES = argmax<sub>a'&isin;M</sub> f(a'), i.e. &forall;a'&isin;M f(RES)&ge;f(a')
     * @throws NoSuchElementException if !choices.hasNext()
     * @see orbital.math.functional.Operations#sup
     * @see java.util.Collections#max(Collection,Comparator)
     * @see orbital.logic.functor.Functionals#foldLeft(BinaryFunction, Object, Object[])
     * @see orbital.algorithm.template.EvaluativeAlgorithm.EvaluationComparator
     */
    public static final /*<A>*/ Object/*>A<*/ argmax(Iterator/*<A>*/ choices, Function/*<A,Comparable>*/ f) {
        // search for maximum f in choices
        // current best choice of choices
        Object best = choices.next();
        // f(best)
        Comparable bestValue = (Comparable) f.apply(best);
        while (choices.hasNext()) {
            final Object o = choices.next();
            final Comparable value = (Comparable) f.apply(o);
            if (value.compareTo(bestValue) > 0) {
                bestValue = value;
                best = o;
            }
            assert true : "invariant: " + bestValue + ">= f-value of each choice seen so far & " + f.apply(best) + "==" + bestValue;
        }

        // return the best choice
        return best;
    }


    // set operations

    /**
     * union of two collections.
     */
    public static final /*_<A>_*/ BinaryFunction/*_<Collection<A>,Collection<A>,Collection<A>>_*/ union =
        new BinaryFunction/*_<Collection<A>,Collection<A>,Collection<A>>_*/() {
            public Object/*_>Collection<A><_*/ apply(Object/*_>Collection<A><_*/ a, Object/*_>Collection<A><_*/ b) {
                return union((Collection)a,(Collection)b);
            }
            public String toString() { return "\u222A"; }
        };
    /**
     * n-ary union of a list of collections.
     * Returns the union of all collections contained in the argument list.
     */
    public static final /*_<A>_*/ Function/*_<Collection<Collection<A>>,Collection<A>>_*/ unionFold =
        new Function/*_<Collection<Collection<A>>,Collection<A>>_*/() {
            private SortedSet/*_<A>_*/ EMPTY_SORTED_SET = Collections.unmodifiableSortedSet(new TreeSet());
            public Object/*_>Collection<A><_*/ apply(Object/*_>Collection<Collection<A>><_*/ a) {
                //@internal using foldRight instead of foldLeft here avoids the dynamic type problem of EMPTY_SORTED_SET in case of incomparable elements
                return Functionals.foldRight(union, EMPTY_SORTED_SET, Utility.asIterator(a));
            }
            public String toString() { return "\u22C3"; }
        };
    /**
     * Returns the union of two collections.
     * @return a &cup; b.
     * @postconditions RES has same type as a
     */
    public static /*<A>*/ Collection/*<A>*/ union(Collection/*<A>*/ a, Collection/*<A>*/ b) {
        Collection/*<A>*/ c = newCollectionLike(a);
        c.addAll(a);
        c.addAll(b);
        return c;
    } 
    public static /*<A>*/ Set/*<A>*/ union(Set/*<A>*/ a, Set/*<A>*/ b) {
        return (Set/*<A>*/) union((Collection/*<A>*/) a, (Collection/*<A>*/) b);
    } 
    public static /*<A>*/ SortedSet/*<A>*/ union(SortedSet/*<A>*/ a, SortedSet/*<A>*/ b) {
        return (SortedSet/*<A>*/) union((Collection/*<A>*/) a, (Collection/*<A>*/) b);
    } 
    public static /*<A>*/ Iterator/*<A>*/ union(Iterator/*<A>*/ a, Iterator/*<A>*/ b) {
        return new SequenceIterator(Arrays.asList(new Iterator[] {a, b}));
    } 

    
    /**
     * intersection of two collections.
     */
    public static final /*_<A>_*/ BinaryFunction/*_<Collection<A>,Collection<A>,Collection<A>>_*/ intersection
        = new BinaryFunction/*_<Collection<A>,Collection<A>,Collection<A>>_*/() {
            public Object/*_>Collection<A><_*/ apply(Object/*_>Collection<A><_*/ a, Object/*_>Collection<A><_*/ b) {
                return intersection((Collection)a,(Collection)b);
            }
            public String toString() { return "\u2229"; }
        };
    /**
     * n-ary intersection of a list of collections.
     * Returns the intersection of all collections contained in the argument list.
     */
    public static final /*_<A>_*/ Function/*_<Collection<Collection<A>>,Collection<A>>_*/ intersectionFold =
        new Function/*_<Collection<Collection<A>>,Collection<A>>_*/() {
            public Object/*_>Collection<A><_*/ apply(Object/*_>Collection<Collection<A>><_*/ a) {
                //@internal the trick intersecting with the first collection is less performant but allowed since the empty intersection is the universe and hence undefined in Java
                return Functionals.foldLeft(intersection, Utility.asIterator(a).next(), Utility.asIterator(a));
            }
            public String toString() { return "\u22C2"; }
        };
    /**
     * Returns the intersection of two collections.
     * @return a &cap; b.
     * @postconditions RES has same type as a
     */
    public static /*<A>*/ Collection/*<A>*/ intersection(Collection/*<A>*/ a, Collection/*<A>*/ b) {
        Collection/*<A>*/ c = newCollectionLike(a);
        c.addAll(a);
        c.retainAll(b);
        return c;
    } 
    public static /*<A>*/ Set/*<A>*/ intersection(Set/*<A>*/ a, Set/*<A>*/ b) {
        return (Set/*<A>*/) intersection((Collection/*<A>*/) a, (Collection/*<A>*/) b);
    } 
    public static /*<A>*/ SortedSet/*<A>*/ intersection(SortedSet/*<A>*/ a, SortedSet/*<A>*/ b) {
        return (SortedSet/*<A>*/) intersection((Collection/*<A>*/) a, (Collection/*<A>*/) b);
    } 

    /**
     * Returns the complement of a collection in a universal set.
     * Is the same as the difference universal &#8726; a.
     * @return a<sup>&#8705;</sup>, a collection of all elements that are in universal, but not in a.
     * @postconditions RES has same type as universal
     */
    public static /*<A>*/ Collection/*<A>*/ complement(Collection/*<A>*/ universal, Collection/*<A>*/ a) {
        Collection/*<A>*/ c = newCollectionLike(universal);
        c.addAll(universal);
        c.removeAll(a);
        return c;
    } 
    public static /*<A>*/ Set/*<A>*/ complement(Set/*<A>*/ universal, Set/*<A>*/ a) {
        return (Set/*<A>*/) complement((Collection/*<A>*/) universal, a);
    } 
    public static /*<A>*/ SortedSet/*<A>*/ complement(SortedSet/*<A>*/ universal, SortedSet/*<A>*/ a) {
        return (SortedSet/*<A>*/) complement((Collection/*<A>*/) universal, a);
    } 

    /**
     * Returns the difference of one collection to another.
     * @return a &#8726; b = complement(a,b) = b<sup>&#8705;</sup> relative to a.
     * @postconditions RES has same type as a
     */
    public final static /*<A>*/ Collection/*<A>*/ difference(Collection/*<A>*/ a, Collection/*<A>*/ b) {
        return complement(a, b);
    } 
    public final static /*<A>*/ Set/*<A>*/ difference(Set/*<A>*/ a, Set/*<A>*/ b) {
        return complement(a, b);
    } 
    public final static /*<A>*/ SortedSet/*<A>*/ difference(SortedSet/*<A>*/ a, SortedSet/*<A>*/ b) {
        return complement(a, b);
    } 

    /**
     * Returns the symmetric difference of two collections.<br />
     * a &Delta; b := (a&#8726;b) &cup; (b&#8726;a)
     * @return a collection of all elements which are unique to either of the collections.
     * @postconditions RES has same type as a
     */
    public static /*<A>*/ Collection/*<A>*/ symmetricDifference(Collection/*<A>*/ a, Collection/*<A>*/ b) {
        return union(complement(a, b), complement(b, a));
    } 
    public static /*<A>*/ Set/*<A>*/ symmetricDifference(Set/*<A>*/ a, Set/*<A>*/ b) {
        return union(complement(a, b), complement(b, a));
    } 
    public static /*<A>*/ SortedSet/*<A>*/ symmetricDifference(SortedSet/*<A>*/ a, SortedSet/*<A>*/ b) {
        return union(complement(a, b), complement(b, a));
    } 

    /**
     * Returns the cross product (or cartesian product) of two collections.<br />
     * a &times; b = {(x,y) &brvbar; x&isin;a &and; y&isin;b}
     * @return a collection of all tupels in a &times; b as {@link orbital.util.Pair} objects.
     * @see #outer(BinaryFunction, Collection, Collection)
     * @todo rewrite pure functional
     */
    public static /*<A, B>*/ Collection/*<Pair<A, B>>*/ cross(Collection/*<A>*/ a, Collection/*<B>*/ b) {
        Collection/*<Pair<A, B>>*/ r = new ArrayList/*<Pair<A, B>>*/(a.size() * b.size());
        for (Iterator/*<A>*/ i = a.iterator(); i.hasNext(); ) {
            Object/*>A<*/ e = (Object/*>A<*//*__*/) i.next();
            for (Iterator/*<B>*/ j = b.iterator(); j.hasNext(); )
                r.add(new Pair/*<A, B>*/(e, (Object/*>B<*//*__*/) j.next()));
        } 
        return r;
    } 
    public static /*<A, B>*/ Iterator/*<Pair<A, B>>*/ cross(Iterator/*<A>*/ a, Iterator/*<B>*/ b) {
        return cross(asList(a), asList(b)).iterator();
    }

    /**
     * Returns the n-ary cross product (or cartesian product) of n collections.<br />
     * &times;<sub>i=1,...,n</sub> a<sub>i</sub> = &prod;<sub>i=1,...,n</sub> a<sub>i</sub> = {(x<sub>i</sub>)<sub>i=1,...,n</sub> &brvbar; &forall;i=1,...,n x<sub>i</sub>&isin;a<sub>i</sub>}
     * <p>
     * Implemented as an iterative unrolling of a recursion.</p>
     * @param a the list &lang;a<sub>1</sub>,...,a<sub>n</sub>&rang; of collections a<sub>i</sub> to choose from.
     * @return a collection of all n-tupels in &times;<sub>i=1,...,n</sub> a<sub>i</sub> as {@link java.util.List} objects.
     * @see #outer(BinaryFunction, Collection, Collection)
     * @see "Axiom of Choice (for infinite case)"
     */
    public static /*<A>*/ Collection/*<List<A>>*/ cross(List/*<Collection<A>>*/ a) {
        // n-ary cross product of the elements in optionLists
        List r = new LinkedList();
        r.add(Collections.EMPTY_LIST);
        for (Iterator i = a.iterator(); i.hasNext(); ) {
            Collection options = (Collection) i.next();
            List           r_ = new ArrayList(r.size() * options.size());
            // do only iterate over current content of r, not regarding further adjustments r_
            for (Iterator k = r.iterator(); k.hasNext(); ) {
                List x = (List) k.next();
                for (Iterator j = options.iterator(); j.hasNext(); ) {
                    List   x2 = new ArrayList(x.size() + 1);
                    x2.addAll(x);
                    x2.add(j.next());
                    r_.add(x2);
                }
            }
            assert r_.size() == r.size() * options.size() : "cross product performs combinatorical increase";
            r = r_;
        }
        return r;
    }

    /**
     * Returns the powerset of a set, i.e. the set of all subsets.<br />
     * &weierp;(S) := {E &sube; S}
     */
    public static /*<A>*/ Set/*<Set<A>>*/ powerset(Set/*<A>*/ s) {
        // list version of the set s (in arbitrary order)
        final Combinatorical c = Combinatorical.getPermutations(s.size(), 2, true);
        final Set/*<Set<A>>*/ p = new LinkedHashSet/*<Set<A>>*/(c.count());
        while (c.hasNext()) {
            int[] choose = c.next();
            Set/*<A>*/ e = (Set)newCollectionLike(s);
            int index = 0;
            for (Iterator/*<A>*/ i = s.iterator(); i.hasNext(); ) {
                Object/*>A<*/ x = i.next();
                if (choose[index++] == 1) {
                    e.add(x);
                }
            }
            p.add(e);
        } 
        return p;
    } 

    
    //

    /**
     * Get <em>any</em> object of a collection.
     * This method can be used to express don't care nondeterminisms in algorithms.
     * So if an alogrithm does not depend upon the exact order in which elements are returned,
     * you can specifiy this by using:<pre>
     * <span class="comment">// expresses don't care nondeterminism</span>
     * <span class="Class">Object</span> o <span class="operator">=</span> <span class="Orbital">Setops</span>.any(someCollection);
     * </pre>
     * @see #epsilon(Collection,Predicate)
     */
    public static /*<A>*/ Object/*>A<*/ any(Collection/*<A>*/ coll) {
        Iterator/*<A>*/ it = coll.iterator();
        return (Object/*>A<*//*__*/) it.next();
    } 

    /**
     * Returns a list filled with the elements in the iterator.
     * <p>
     * This method works somewhat like java.util.Arrays.asList(Object[]) but
     * is not backed by the iterator.</p>
     * @see java.util.Arrays#asList(Object[])
     */
    public static /*<A>*/ List/*<A>*/ asList(Iterator/*<A>*/ it) {
        List/*<A>*/ r = new LinkedList/*<A>*/();
        while (it.hasNext())
            r.add(it.next());
        return r;
    } 

    /**
     * Returns a set filled with the elements in the iterator.  <p>
     * Except for set notation, this method works somewhat like
     * java.util.Arrays.asList(Object[]) but is not backed by the
     * iterator.</p>
     * @see java.util.Arrays#asList(Object[])
     */
    public static /*<A>*/ Set/*<A>*/ asSet(Iterator/*<A>*/ it) {
        Set/*<A>*/ r = new LinkedHashSet/*<A>*/();
        while (it.hasNext())
            r.add(it.next());
        return r;
    } 

    /**
     * Get a new instance of an empty collection of the same type as the one specified.
     * <p>
     * If no such collection could be instantiated, a similar collection is used.</p>
     */
    public static /*<A,B>*/ Collection/*<B>*/ newCollectionLike(Collection/*<A>*/ c) {
        try {
            if (c instanceof SortedSet) {
                // skip and let the special handler below take care of the comparator
            } else {
                return (Collection/*<A>*/) c.getClass().newInstance();
            }
        }
        catch (InstantiationException trial) {}
        catch (IllegalAccessException trial) {} 
        // find a rather similar collection type
        if (c instanceof java.util.SortedSet) {
            return new java.util.TreeSet/*<A>*/(((SortedSet)c).comparator());
        } else if (c instanceof java.util.Set) {
            return linkedHashSetClass != null && linkedHashSetClass.isInstance(c)
                ? (Set) new java.util.LinkedHashSet/*<A>*/(c.size())
                : (Set) new java.util.HashSet/*<A>*/();
        } else if (c instanceof java.util.List) {
            return randomAccessClass != null && randomAccessClass.isInstance(c)
                ? (List) new java.util.ArrayList/*<A>*/(c.size())
                : (List) new java.util.LinkedList/*<A>*/();
        } else {
            throw new IllegalArgumentException("unknown collection type " + c.getClass() + " could not be instantiated");
        }
    } 

    /**
     * Returns an unmodifiable view of the specified iterator.
     * <p>
     * Query operations on the returned iterator "read through" to the specified iterator,
     * and attempts to modify the returned iterator result in an UnsupportedOperationException.
     */
    public static /*<A>*/ Iterator/*<A>*/ unmodifiableIterator(final Iterator/*<A>*/ i) {
        return new Iterator/*<A>*/() {
                public boolean hasNext() {return i.hasNext();}
                public Object/*>A<*/ next()    {return i.next();}
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
    }
    public static /*<A>*/ ListIterator/*<A>*/ unmodifiableListIterator(final ListIterator/*<A>*/ i) {
        return new ListIterator/*<A>*/() {
                public boolean hasNext() {return i.hasNext();}
                public boolean hasPrevious() {return i.hasPrevious();}
                public Object/*>A<*/ next()    {return i.next();}
                public Object/*>A<*/ previous()        {return i.previous();}
                public int nextIndex()   {return i.nextIndex();}
                public int previousIndex()       {return i.previousIndex();}
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                public void add(Object/*>A<*/ o) {
                    throw new UnsupportedOperationException();
                }
                public void set(Object/*>A<*/ o) {
                    throw new UnsupportedOperationException();
                }
            };
    }
    /**
     * Returns an unmodifiable view of the same type as the collection specified.
     * @see #newCollectionLike(Collection)
     * @see Collections#unmodifiableCollection(Collection)
     * @see Collections#unmodifiableList(List)
     * @see Collections#unmodifiableSet(Set)
     * @see Collections#unmodifiableSortedSet(SortedSet)
     */
    public static /*<A>*/ Collection/*<A>*/ unmodifiableCollectionLike(final Collection/*<A>*/ c) {
        // find a rather similar collection type
        if (c instanceof java.util.SortedSet)
            return Collections.unmodifiableSortedSet((SortedSet)c);
        else if (c instanceof java.util.Set)
            return Collections.unmodifiableSet((Set)c);
        else if (c instanceof java.util.List)
            return Collections.unmodifiableList((List)c);
        else
            return Collections.unmodifiableCollection(c);
    }

    /**
     * Copies all of the elements from one list-iterator into another.
     * After the operation, the index of each copied element in the
     * destination list-iterator will be identical to its index in the
     * source list-iterator. The destination list-iterator must be at
     * least as long as the source list-iterator. If it is longer, the
     * remaining elements in the destination list-iterator are
     * unaffected.  This method runs in linear time.
     * <p>
     * Of course the list-iterators will be at different positions when this method finishes.</p>
     * @param dest The destination list-iterator.
     * @param src The source list-iterator. 
     * @throws IndexOutOfBoundsException if the destination list-iterator is too small to contain the entire source List. 
     * @throws UnsupportedOperationException if the destination list-iterator does not support the set operation.
     * @see Collections#copy(List,List)
     */
    public static /*<A>*/ void copy(ListIterator/*<A>*/ dest, Iterator/*<? extends A>*/ src) {
        while (src.hasNext()) {
            try {
                dest.next();
            }
            catch (NoSuchElementException ex) {
                //@internal that's better than querying dest.hasNext() in order to let growing ListIterators (like Polynomial.iterator()) have a chance of growing on demand.
                // but also see Functionals.mapInto
                throw new IndexOutOfBoundsException("destination ListIterator has less storage than source iterator");
            }
            dest.set(src.next());
        }
    }

    /**
     * Sort a collection according to the order induced by a comparator, with bubble sort.
     */
    //public static Collection/*<A>*/ bubbleSort(Collection/*<A>*/ c, Comparator/*<A>*/ comp) {
    /*  boolean         inorder;
        Collection ord_c;
        do {
        inorder = true;
        ord_c = new ArrayList(c.size());

        Iterator i = c.iterator();
        if (!i.hasNext())
        break;
        Object o_el;
        for (o_el = i.next(); i.hasNext(); ) {
        Object el = i.next();

        if (comp.compare(el, o_el) < 0) {
        Object t = o_el;                // swap
        o_el = el;
        el = t;
        inorder = false;
        }
        ord_c.add(o_el);

        o_el = el;
        }
        ord_c.add(o_el);

        c = ord_c;
        } while (!inorder);

        return c;
        }*/

    /**
     * Merge two collections according to the order induced by a comparator.
     * @preconditions isSorted(x) && isSorted(y)
     * @postconditions isSorted(RES) && RES = a &cup; b
     * @todo see #mergeSort(Collection, Comparator)
     */
    public static /*<A>*/ List/*<A>*/ merge(List/*<A>*/ x, List/*<A>*/ y, Comparator/*<A>*/ comp) {
        return merge(x.iterator(), y.iterator(), comp, new ArrayList(x.size() + y.size()));
    }
    /**
     * Merge two iterator-views according to the order induced by a comparator.
     * @preconditions isSorted(x) && isSorted(y)
     * @postconditions isSorted(RES) && RES = a &cup; b
     */
    public static /*<A>*/ List/*<A>*/ merge(Iterator/*<A>*/ x, Iterator/*<A>*/ y, Comparator/*<A>*/ comp) {
        return merge(x, y, comp, new LinkedList());
    }
    /**
     * @preconditions isSorted(x) && isSorted(y)
     * @postconditions isSorted(RES) && RES = a &cup; b
     */
    private static /*<A>*/ List/*<A>*/ merge(Iterator/*<A>*/ x, Iterator/*<A>*/ y, Comparator/*<A>*/ comp, List/*<A>*/ r) {
        if (x.hasNext() && y.hasNext()) {
            Object   ox = x.next();
            Object   oy = y.next();
            // merge both lists as long as both contain data
            while (true) {
                if (comp.compare(ox, oy) <= 0) {
                    r.add(ox);
                    if (x.hasNext())
                        ox = x.next();
                    else {
                        r.add(oy);
                        break;
                    }
                } else {
                    r.add(oy);
                    if (y.hasNext())
                        oy = y.next();
                    else {
                        r.add(ox);
                        break;
                    }
                }
            }
        }
        assert !(x.hasNext() && y.hasNext()) : "at most one iterator has data left";
        // append the single array that still does contain data
        while (x.hasNext())
            r.add(x.next());
        while (y.hasNext())
            r.add(y.next());
        return r;
    }

    /**
     * insert object into l such that l is still sorted.
     * Special case of merge.
     * @preconditions sorted(l)
     * @postconditions sorted(l) &and; object&isin;l
     */
    public static final /*<A extends Comparable>*/ void insert(List/*<A>*/ l, Object/*>A<*/ object) {
        assert Utility.sorted(l, null) : "@preconditions";
        final Comparable c = (Comparable)object;
        final ListIterator/*<A>*/ i = l.listIterator();
        while (i.hasNext()) {
            Object/*>A<*/ o = i.next();
            if (c.compareTo(o) <= 0) {
                i.previous();
                break;
            }
        }
        i.add(c);
        assert Utility.sorted(l, null) && l.contains(object) : "@postconditions";
    }

    /**
     * Returns a reverse view of a list.
     * @see <a href="{@docRoot}/Patterns/Design/Decorator.html">Decorator</a>
     */
//     public static final List reverse(final List l) {
//      return new AbstractSequentialList() {
//              // implementation of java.util.List interface

//              /**
//               * Transforms an index of the reversed list to an index of the original list.
//               */             
//              private int trafo(int index) {
//                  return size() - 1 - index;
//              }

//              /**
//               * Transforms an index of the original list back to an index of the reversed list.
//               */             
//              private int reverseTrafo(int index) {
//                  return trafo(index);
//              }

//              public int indexOf(Object param1)
//              {
//                  return reverseTrafo(l.indexOf(param1));
//              }

//              public int lastIndexOf(Object param1)
//              {
//                  return reverseTrafo(l.lastIndexOf(param1));
//              }

//              public boolean addAll(int param1, Collection param2)
//              {
//                  return l.addAll(trafo(param1), param2);
//              }

//              public boolean addAll(Collection param1)
//              {
//                  return l.addAll(trafo(0), param2);
//              }
//              public boolean add(Object param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return false;
//              }
//              public void add(int param1, Object param2)
//              {
//                  // TODO: implement this java.util.List method
//              }

//              public Object get(int param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }
//              public boolean contains(Object param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return false;
//              }

//              public int size()
//              {
//                  // TODO: implement this java.util.List method
//                  return 0;
//              }

//              public Object[] toArray()
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }

//              public Object[] toArray(Object[] param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }

//              public Iterator iterator()
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }

//              public boolean remove(Object param1)
//              {
//                  return l.remove(param1);
//              }

//              public Object remove(int param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }
//              public void clear()
//              {
//                  l.clear();
//              }

//              public boolean isEmpty()
//              {
//                  return l.isEmpty();
//              }

//              public Object set(int param1, Object param2)
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }

//              public boolean containsAll(Collection param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return false;
//              }

//              public boolean removeAll(Collection param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return false;
//              }

//              public boolean retainAll(Collection param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return false;
//              }

//              public List subList(int param1, int param2)
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }

//              public ListIterator listIterator(int param1)
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }

//              public ListIterator listIterator()
//              {
//                  // TODO: implement this java.util.List method
//                  return null;
//              }

//          };
//     }

    /**
     * Converts an array of key and values to a map.
     * @param entries Contains keys and their values.
     *  Stored as an array of length-2 arrays
     *  with entries[i][0] being the key {@link String},
     *  and entries[i][1] being the value {@link Object}.
     * @see java.util.Arrays#asList(Object[])
     */
    public static final Map asMap(Object[][] entries) {
        Map map = new HashMap();
        for (int i = 0; i < entries.length; i++) {
            map.put((String)entries[i][0], entries[i][1]);
        }
        return Collections.unmodifiableMap(map);
    }

    // selection methods

    /**
     * Creates a sophisticated selection filter.
     * <p>
     * When applied upon a collection of objects the filter will perform an operation like
     * <b>ResultSet</b>:  <span class="keyword">SELECT</span> <var>whatFilter</var>
     * <span class="keyword">FROM</span> <var>ObjectCollection</var>
     * <span class="keyword">WHERE</span> <var>Predicate</var>
     * <span class="keyword">ORDER BY</span> <var>Comparator</var> <span class="keyword">ASC</span>|<span class="keyword">DESC</span>.
     * </p>
     * This is a (minor) generalization of
     * <center>
     *   <table class="equation">
     *     <tr><td>filter p</td> <td>=</td> <td><span class="bananaBracket">(|</span>&empty;,f<span class="bananaBracket">|)</span></td>)</tr>
     *     <tr><td colspan="4">Where</td></tr>
     *     <tr><td>f a as</td> <td>=</td> <td>[a|as]</td> <td>&lArr; p(a)</td></tr>
     *     <tr><td>f a as</td> <td>=</td> <td>as</td> <td>&lArr; &not;p(a)</td></tr>
     *   </table>
     * </center>
     * @param what states what data in the collection is requested. All if <code>null</code>. See Also {@link Filters}.
     * @param where states what predicate is checked as condition for selecting data elements. No condition if <code>null</code>.
     * @param orderBy states how to sort every two data elements. No sorting if <code>null</code>.
     * @param asc whether to use ascending order, or descending.
     *  If <code>false</code>, orderBy comparator will be used reverse.
     * @return a filter that selects the specified data from the source of data it is applied upon.
     *  It will apply on arguments of type {@link Collection},{@link Iterator}.
     * @xxx we always return a List.
     * @todo Utility#generalizedIteratable
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @see Filters
     * @see orbital.logic.functor.Functionals.Catamorphism
     * @see orbital.logic.functor.Functionals#filter(Predicate,Iterator)
     */
    public static final Function/**<Collection &cup; Iterator, Collection>**/ createSelection(final Function/*<Collection, Collection>*/ what,
                                                                                              final Predicate where,
                                                                                              final Comparator orderBy, final boolean asc) {
        if (what == null)
            return createSelection(Filters.all, where, orderBy, asc);
        else if (where == null)
            return createSelection(what, Functionals.onVoid(Predicates.TRUE), orderBy, asc);
        return new Function/* <Collection, Collection> */() {
                public Object/* >Collection< */ apply(Object/* >Collection< */ from) {
                    // selection of elements which the predicate was true of
                    List sel = new LinkedList();
                
                    // for each in FROM
                    for (Iterator i = (from instanceof Iterator) ?
                             (Iterator) from : ((Collection) from).iterator();
                         i.hasNext(); ) {
        
                        // SELECT element
                        Object el = i.next();
        
                        // WHERE Adjective suits
                        if (where.apply(el))
                            sel.add(el);
                    } 
        
                    // ORDER BY sort ASC|DESC
                    if (orderBy != null)
                        Collections.sort(sel, asc ? orderBy : new ReverseComparator(orderBy));
        
                    // RESULTSET
                    // filter the data collection of the selected elements
                    return what.apply(sel);
                }
            };
    } 
    public static final Function/**<Collection &cup; Iterator, Collection>**/ createSelection(final Predicate where) {
        return createSelection(null, where, null, true);
    }

    /**
     * Select filter operation.
     * <p>
     * <b>ResultSet</b>:  <span class="keyword">SELECT</span> <var>whatFilter</var>
     * <span class="keyword">FROM</span> <var>ObjectCollection</var>
     * <span class="keyword">WHERE</span> <var>Predicate</var>
     * <span class="keyword">ORDER BY</span> <var>Comparator</var> <span class="keyword">ASC</span>|<span class="keyword">DESC</span>.</p>
     * @param what states what data in the collection is requested. All if null. See Also {@link Filters}.
     * @param from sets the source of data.
     * @param where states what predicate is checked as condition for selecting data elements. None if null.
     * @param orderBy states how to sort every two data elements. No sorting if null.
     * @param asc whether to use ascending order, or descending.
     *  If false, orderBy comparator will be used reverse.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
     * @see Filters
     * @see #createSelection(Function,Predicate,Comparator,boolean)
     */
    public static final Collection select(Function/*<Collection, Collection>*/ what,
                                          Collection from,
                                          Predicate where,
                                          Comparator orderBy, boolean asc) {
        return (Collection) createSelection(what, where, orderBy, asc).apply(from);
    } 


    public static final Collection select(Function/*<Collection, Collection>*/ what, Collection from, Predicate where) {
        return select(what, from, where, (Comparator) null, true);
    } 
    public static final Collection select(Function/*<Collection, Collection>*/ what, Collection from) {
        return select(what, from, (Predicate) null);
    } 

    public static final Collection select(Function/*<Collection, Collection>*/ what, Collection from, Collection wherePredicates) {
        List sel = new LinkedList();    // selection of Elements with suited Adjectives

        // for each in FROM
        for (Iterator i = from.iterator(), c = wherePredicates.iterator(); i.hasNext(); ) {

            // SELECT element
            Object        el = i.next();

            Predicate where = (Predicate) c.next();

            // WHERE associated Adjective suits
            if (where == null || where.apply(el))
                sel.add(el);
        } 

        // RESULTSET
        return (what == null ? sel : (Collection) what.apply(sel));        // filter the Data Collection of the selected Elements
    }


    // diverse

    /**
     * Check whether the given iterator produces duplicate entries.
     */
    public static boolean hasDuplicates(Iterator i) {
        List l = Setops.asList(i);
        return new HashSet(l).size() != l.size();
    }
}
