/**
 * @(#)Setops.java 1.0 2000/02/17 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Comparator;
import orbital.logic.functor.Predicate;
import orbital.logic.functor.BinaryPredicate;
import orbital.logic.functor.Function;

import orbital.logic.functor.Functionals;
import orbital.logic.functor.Predicates;

import orbital.util.ReverseComparator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import orbital.util.Pair;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Contains utility methods for common set operations and more general collection operations.
 * 
 * <p>
 * The selection methods ({@link #select(Function, Collection, Predicate, Comparator, boolean)}) encapsulate a generalization of queries over Collections.
 * These queries are build just like data queries over tables with SQL.
 * In a selection query, a Collection is filtered to obtain the desired subset of data
 * which matches the criteria, with the order being induced by a Comparator.</p>
 *
 * @stereotype &laquo;Utilities&raquo;
 * @version 1.0, 2000/08/15
 * @author  Andr&eacute; Platzer
 * @see orbital.util.Utility
 * @see orbital.algorithm.evolutionary.Selectors
 * @see java.util.Collections
 * @see <a href="http://www.sql.org">Structured Query Language (SQL)</a>
 */
public final class Setops {

    /**
     * prevent instantiation - final static utility class
     */
    private Setops() {}

    /**
     * Return the first object in a collection that satisfies the specified predicate.
     * @return the first object in a collection that satisfies the specified predicate, or null if no such object exists.
     */
    public static /*<A>*/ Object/*>A<*/ find(Collection/*_<A>_*/ coll, Predicate/*<A>*/ found) {
	return find(coll.iterator(), found);
    }
    public static /*<A>*/ Object/*>A<*/ find(Iterator/*_<A>_*/ i, Predicate/*<A>*/ found) {
	while (i.hasNext()) {
	    Object/*>A<*/ o = (Object/*>A<*//*__*/) i.next();
	    if (found.apply(o))
		return o;
	} 
	return null;
    } 

    /**
     * Counts the number of objects in a collection that satisfy the specified predicate.
     */
    public static /*<A>*/ int count(Collection/*_<A>_*/ coll, Predicate/*<A>*/ cond) {
	return count(coll.iterator(), cond);
    }
    public static /*<A>*/ int count(Iterator/*_<A>_*/ i, Predicate/*<A>*/ cond) {
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
     * @see <a href="{@docRoot}/Patterns/Design/InternalIterator.html">Internal Iterator</a>
     * @see orbital.logic.functor.Functionals
     * @todo document banana application @see Operations.andAll
     */
    public static /*<A>*/ boolean all(Collection/*_<A>_*/ coll, Predicate/*<A>*/ found) {
	return all(coll.iterator(), found);
    }
    public static /*<A>*/ boolean all(Iterator/*_<A>_*/ i, Predicate/*<A>*/ found) {
	// optimized version of return Functionals.map(and, Functionals.map(Functionals.asFunction(found), i))
	while (i.hasNext())
	    if (!found.apply((/*__*/Object/*>A<*/)i.next()))
		return false;
	return true;
    } 
    public static /*<A1, A2>*/ boolean all(Collection/*_<A1>_*/ a, Collection/*_<A2>_*/ b, BinaryPredicate/*<A1, A2>*/ found) {
	return a.size() == b.size()
	    && all(a.iterator(), b.iterator(), found);
    }
    public static /*<A1, A2>*/ boolean all(Iterator/*_<A1>_*/ i, Iterator/*_<A2>_*/ j, BinaryPredicate/*<A1, A2>*/ found) {
	// optimized version of return Functionals.map(and, Functionals.map(Functionals.asFunction(found), i, j))
	while (i.hasNext() && j.hasNext())
	    if (!found.apply((/*__*/Object/*>A1<*/) i.next(), (/*__*/Object/*>A2<*/) j.next()))
		return false;
	return !(i.hasNext() || j.hasNext());
    } 
    /**
     * Checks whether some objects (at least one) in a collection satisfy the specified predicate.
     * @return true if at least one objects satisfies the predicate, false if none does.
     *  Returns an optimized version of return <code>Functionals.map(or, Functionals.map(Functionals.asFunction(found), i))</code>.
     * @see <a href="{@docRoot}/Patterns/Design/InternalIterator.html">Internal Iterator</a>
     * @see orbital.logic.functor.Functionals
     * @todo document banana application @see Operations.orSome
     */
    public static /*<A>*/ boolean some(Collection/*_<A>_*/ coll, Predicate/*<A>*/ found) {
	return some(coll.iterator(), found);
    }
    public static /*<A>*/ boolean some(Iterator/*_<A>_*/ i, Predicate/*<A>*/ found) {
	// optimized version of return Functionals.map(or, Functionals.map(Functionals.asFunction(found), i))
	while (i.hasNext())
	    if (found.apply((/*__*/Object/*>A<*/) i.next()))
		return true;
	return false;
    } 
    public static /*<A1, A2>*/ boolean some(Collection/*_<A1>_*/ a, Collection/*_<A2>_*/ b, BinaryPredicate/*<A1, A2>*/ found) {
	return some(a.iterator(), b.iterator(), found);
    }
    public static /*<A1, A2>*/ boolean some(Iterator/*_<A1>_*/ i, Iterator/*_<A2>_*/ j, BinaryPredicate/*<A1, A2>*/ found) {
	// optimized version of return Functionals.map(or, Functionals.map(Functionals.asFunction(found), i, j))
	while (i.hasNext() && j.hasNext())
	    if (found.apply((/*__*/Object/*>A1<*/) i.next(), (/*__*/Object/*>A2<*/) j.next()))
		return true;
	return false;
    } 

    // arg min / arg max

    /**
     * Get the minimum argument.
     * @param choices the available choices M.
     * @param f the evaluation function f:M&rarr;<b>R</b>.
     * @return argmin<sub>a&isin;M</sub> f(a) with minimum f-value in choices.
     * @pre choices.hasNext()
     * @post RES = argmin<sub>a'&isin;M</sub> f(a'), i.e. &forall;a'&isin;M f(RES)&le;f(a')
     * @throws NoSuchElementException if !choices.hasNext()
     * @see orbital.math.functional.Operations#inf
     * @see java.util.Collections#min(Collection,Comparator)
     * @see orbital.logic.functor.Functionals#foldLeft
     * @see orbital.algorithm.template.EvaluativeAlgorithm.EvaluationComparator
     */
    public static final Object/*_>A<_*/ argmin(Iterator/*_<A>_*/ choices, Function/*<A,Comparable>*/ f) {
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
     * @pre choices.hasNext()
     * @post RES = argmax<sub>a'&isin;M</sub> f(a'), i.e. &forall;a'&isin;M f(RES)&ge;f(a')
     * @throws NoSuchElementException if !choices.hasNext()
     * @see orbital.math.functional.Operations#sup
     * @see java.util.Collections#max(Collection,Comparator)
     * @see orbital.logic.functor.Functionals#foldLeft
     * @see orbital.algorithm.template.EvaluativeAlgorithm.EvaluationComparator
     */
    public static final Object/*_>A<_*/ argmax(Iterator/*_<A>_*/ choices, Function/*<A,Comparable>*/ f) {
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
     * Returns the union of two collections.
     * @return a &cup; b.
     * @post RES has same type as a
     */
    public static /*_<A>_*/ Collection/*_<A>_*/ union(Collection/*_<A>_*/ a, Collection/*_<A>_*/ b) {
	Collection/*_<A>_*/ c = newCollectionLike(a);
	c.addAll(a);
	c.addAll(b);
	return c;
    } 
    public static /*_<A>_*/ Set/*_<A>_*/ union(Set/*_<A>_*/ a, Set/*_<A>_*/ b) {
	return (Set/*_<A>_*/) union((Collection/*_<A>_*/) a, (Collection/*_<A>_*/) b);
    } 
    public static /*_<A>_*/ SortedSet/*_<A>_*/ union(SortedSet/*_<A>_*/ a, SortedSet/*_<A>_*/ b) {
	return (SortedSet/*_<A>_*/) union((Collection/*_<A>_*/) a, (Collection/*_<A>_*/) b);
    } 

    /**
     * Returns the intersection of two collections.
     * @return a &cap; b.
     * @post RES has same type as a
     */
    public static /*_<A>_*/ Collection/*_<A>_*/ intersection(Collection/*_<A>_*/ a, Collection/*_<A>_*/ b) {
	Collection/*_<A>_*/ c = newCollectionLike(a);
	c.addAll(a);
	c.retainAll(b);
	return c;
    } 
    public static /*_<A>_*/ Set/*_<A>_*/ intersection(Set/*_<A>_*/ a, Set/*_<A>_*/ b) {
	return (Set/*_<A>_*/) intersection((Collection/*_<A>_*/) a, (Collection/*_<A>_*/) b);
    } 
    public static /*_<A>_*/ SortedSet/*_<A>_*/ intersection(SortedSet/*_<A>_*/ a, SortedSet/*_<A>_*/ b) {
	return (SortedSet/*_<A>_*/) intersection((Collection/*_<A>_*/) a, (Collection/*_<A>_*/) b);
    } 

    /**
     * Returns the complement of a collection in a universal set.
     * Is the same as the difference universal &#8726; a.
     * @return a<sup>&#8705;</sup>, a collection of all elements that are in universal, but not in a.
     * @post RES has same type as universal
     */
    public static /*_<A>_*/ Collection/*_<A>_*/ complement(Collection/*_<A>_*/ universal, Collection/*_<A>_*/ a) {
	Collection/*_<A>_*/ c = newCollectionLike(universal);
	c.addAll(universal);
	c.removeAll(a);
	return c;
    } 
    public static /*_<A>_*/ Set/*_<A>_*/ complement(Set/*_<A>_*/ universal, Set/*_<A>_*/ a) {
	return (Set/*_<A>_*/) complement((Collection/*_<A>_*/) universal, a);
    } 
    public static /*_<A>_*/ SortedSet/*_<A>_*/ complement(SortedSet/*_<A>_*/ universal, SortedSet/*_<A>_*/ a) {
	return (SortedSet/*_<A>_*/) complement((Collection/*_<A>_*/) universal, a);
    } 

    /**
     * Returns the difference of one collection to another.
     * @return a &#8726; b = complement(a,b) = b<sup>&#8705;</sup> relative to a.
     * @post RES has same type as a
     */
    public final static /*_<A>_*/ Collection/*_<A>_*/ difference(Collection/*_<A>_*/ a, Collection/*_<A>_*/ b) {
	return complement(a, b);
    } 
    public final static /*_<A>_*/ Set/*_<A>_*/ difference(Set/*_<A>_*/ a, Set/*_<A>_*/ b) {
	return complement(a, b);
    } 
    public final static /*_<A>_*/ SortedSet/*_<A>_*/ difference(SortedSet/*_<A>_*/ a, SortedSet/*_<A>_*/ b) {
	return complement(a, b);
    } 

    /**
     * Returns the symmetric difference of two collections.<br />
     * a &Delta; b = (a&&#8726;b) &cup; (b&#8726;a)
     * @return a collection of all elements which are unique to either of the collections.
     * @post RES has same type as a
     */
    public static /*_<A>_*/ Collection/*_<A>_*/ symmetricDifference(Collection/*_<A>_*/ a, Collection/*_<A>_*/ b) {
	return union(complement(a, b), complement(b, a));
    } 
    public static /*_<A>_*/ Set/*_<A>_*/ symmetricDifference(Set/*_<A>_*/ a, Set/*_<A>_*/ b) {
	return union(complement(a, b), complement(b, a));
    } 
    public static /*_<A>_*/ SortedSet/*_<A>_*/ symmetricDifference(SortedSet/*_<A>_*/ a, SortedSet/*_<A>_*/ b) {
	return union(complement(a, b), complement(b, a));
    } 

    /**
     * Returns the cross product (or cartesian product) of two collections.<br />
     * a &times; b = {(x,y) &brvbar; x&isin;a &and; y&isin;b}
     * @return a collection of all tupels in a &times; b as {@link orbital.util.Pair} objects.
     * @see #outer(BinaryFunction, Collection, Collection)
     * @todo rewrite pure functional
     */
    public static /*<A, B>*/ Collection/*_<Pair<A, B>>_*/ cross(Collection/*_<A>_*/ a, Collection/*_<B>_*/ b) {
	Collection/*_<Pair<A, B>>_*/ r = new ArrayList/*_<Pair<A, B>>_*/(a.size() * b.size());
	for (Iterator/*_<A>_*/ i = a.iterator(); i.hasNext(); ) {
	    Object/*>A<*/ e = (Object/*>A<*//*__*/) i.next();
	    for (Iterator j/*_<B>_*/ = b.iterator(); j.hasNext(); )
		r.add(new Pair/*<A, B>*/(e, (Object/*>B<*//*__*/) j.next()));
	} 
	return r;
    } 
    public static /*<A, B>*/ Iterator/*_<Pair<A, B>>_*/ cross(Iterator/*_<A>_*/ a, Iterator/*_<B>_*/ b) {
	return cross(asList(a), asList(b)).iterator();
    }

    /**
     * Returns the n-ary cross product (or cartesian product) of n collections.<br />
     * &times;<sub>i=1,...,n</sub> a<sub>i</sub> = {(x<sub>i</sub>)<sub>i=1,...,n</sub> &brvbar; &forall;i=1,...,n x<sub>i</sub>&isin;a<sub>i</sub>}
     * <p>
     * Implemented as an iterative unrolling of a recursion.</p>
     * @return a collection of all n-tupels in &times;<sub>i=1,...,n</sub> a<sub>i</sub> as {@link java.util.List} objects.
     * @see #outer(BinaryFunction, Collection, Collection)
     */
    public static Collection cross(List/*_<Collection<A>>_*/ a) {
    	// n-ary cross product of the elements in optionLists
    	List r = new LinkedList();
    	r.add(Collections.EMPTY_LIST);
    	for (Iterator i = a.iterator(); i.hasNext(); ) {
	    Collection options = (Collection) i.next();
	    List	   r_ = new ArrayList(r.size() * options.size());
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
     * Get <em>any</em> object of a collection.
     * This method can be used to express don't care nondeterminisms in algorithms.
     * So if an alogrithm does not depend upon the exact order in which elements are returned,
     * you can specifiy this by using:<pre>
     * <span class="comment">// expresses don't care nondeterminism</span>
     * <span class="Class">Object</span> o <span class="operator">=</span> <span class="Orbital">Setops</span>.any(someCollection);
     * </pre>
     */
    public static /*<A>*/ Object/*>A<*/ any(Collection/*_<A>_*/ coll) {
	Iterator/*_<A>_*/ it = coll.iterator();
	return (Object/*>A<*//*__*/) it.next();
    } 

    /**
     * Returns a list filled with the elements in the iterator.
     * <p>
     * This method works somewhat like java.util.Arrays.asList(Object[]) but
     * is not backed by the iterator.</p>
     * @see java.util.Arrays#asList(Object[])
     */
    public static /*_<A>_*/ List/*_<A>_*/ asList(Iterator/*_<A>_*/ it) {
	List/*_<A>_*/ r = new LinkedList/*_<A>_*/();
	while (it.hasNext())
	    r.add(it.next());
	return r;
    } 

    /**
     * Get a new instance of an empty collection of the same type as the one specified.
     * <p>
     * If no such collection could be instantiated, a similar collection is used.</p>
     */
    public static /*_<A>_*/ Collection/*_<A>_*/ newCollectionLike(Collection/*_<A>_*/ c) {
	try {
	    if (c instanceof SortedSet)
		// skip and let the special handler below take care of the comparator
		;
	    else
		return (Collection/*_<A>_*/) c.getClass().newInstance();
	}
	catch (InstantiationException trial) {}
	catch (IllegalAccessException trial) {} 
	// find a rather similar collection type
	if (c instanceof java.util.SortedSet)
	    return new java.util.TreeSet/*_<A>_*/(((SortedSet)c).comparator());
	else if (c instanceof java.util.Set)
	    return new java.util.HashSet/*_<A>_*/();
	else if (c instanceof java.util.List)
	    //@todo JDK1.4 if (instanceof java.util.RandomAccess) return new java.util.ArrayList();
	    return new java.util.LinkedList/*_<A>_*/();
	else
	    throw new IllegalArgumentException("unknown collection type " + c.getClass() + " could not be instantiated");
    } 

    /**
     * Returns an unmodifiable view of the specified iterator.
     * <p>
     * Query operations on the returned iterator "read through" to the specified iterator,
     * and attempts to modify the returned iterator result in an UnsupportedOperationException.
     */
    public static Iterator/*_<A>_*/ unmodifiableIterator(final Iterator/*_<A>_*/ i) {
	return new Iterator/*_<A>_*/() {
    		public boolean hasNext() {return i.hasNext();}
    		public Object/*_>A<_*/ next() 	 {return i.next();}
    		public void remove() {
    		    throw new UnsupportedOperationException();
		}
	    };
    }
    public static ListIterator/*_<A>_*/ unmodifiableListIterator(final ListIterator/*_<A>_*/ i) {
	return new ListIterator/*_<A>_*/() {
    		public boolean hasNext() {return i.hasNext();}
    		public boolean hasPrevious() {return i.hasPrevious();}
    		public Object/*_>A<_*/ next() 	 {return i.next();}
    		public Object/*_>A<_*/ previous() 	 {return i.previous();}
    		public int nextIndex() 	 {return i.nextIndex();}
    		public int previousIndex() 	 {return i.previousIndex();}
    		public void remove() {
    		    throw new UnsupportedOperationException();
		}
    		public void add(Object/*_>A<_*/ o) {
    		    throw new UnsupportedOperationException();
		}
    		public void set(Object/*_>A<_*/ o) {
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
    public static Collection/*_<A>_*/ unmodifiableCollectionLike(final Collection/*_<A>_*/ c) {
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
    public static void copy(ListIterator dest, Iterator src) {
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
    //public static Collection/*_<A>_*/ bubbleSort(Collection/*_<A>_*/ c, Comparator/*_<A>_*/ comp) {
    /*	boolean		inorder;
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
	Object t = o_el;		// swap
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
     * @pre isSorted(x) && isSorted(y)
     * @post isSorted(RES) && RES = a &cup; b
     * @todo see #mergeSort(Collection, Comparator)
     */
    public static List/*_<A>_*/ merge(List/*_<A>_*/ x, List/*_<A>_*/ y, Comparator/*_<A>_*/ comp) {
	return merge(x.iterator(), y.iterator(), comp, new ArrayList(x.size() + y.size()));
    }
    /**
     * Merge two iterator-views according to the order induced by a comparator.
     * @pre isSorted(x) && isSorted(y)
     * @post isSorted(RES) && RES = a &cup; b
     */
    public static List/*_<A>_*/ merge(Iterator/*_<A>_*/ x, Iterator/*_<A>_*/ y, Comparator/*_<A>_*/ comp) {
	return merge(x, y, comp, new LinkedList());
    }
    /**
     * @pre isSorted(x) && isSorted(y)
     * @post isSorted(RES) && RES = a &cup; b
     */
    private static List/*_<A>_*/ merge(Iterator/*_<A>_*/ x, Iterator/*_<A>_*/ y, Comparator/*_<A>_*/ comp, List/*_<A>_*/ r) {
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
     * @pre sorted(l)
     * @post sorted(l) &and; object&isin;l
     */
    public static final void insert(List l, Object object) {
	assert Utility.sorted(l, null) : "@pre";
	final Comparable c = (Comparable)object;
	final ListIterator i = l.listIterator();
	while (i.hasNext()) {
	    Object o = i.next();
	    if (c.compareTo(o) <= 0) {
		i.previous();
		break;
	    }
	}
	i.add(c);
	assert Utility.sorted(l, null) && l.indexOf(object) >= 0: "@post";
    }

    /**
     * Returns a reverse view of a list.
     * @see <a href="{@docRoot}/Patterns/Design/Decorator.html">Decorator</a>
     */
//     public static final List reverse(final List l) {
// 	return new AbstractSequentialList() {
// 		// implementation of java.util.List interface

// 		/**
// 		 * Transforms an index of the reversed list to an index of the original list.
// 		 */		
// 		private int trafo(int index) {
// 		    return size() - 1 - index;
// 		}

// 		/**
// 		 * Transforms an index of the original list back to an index of the reversed list.
// 		 */		
// 		private int reverseTrafo(int index) {
// 		    return trafo(index);
// 		}

// 		public int indexOf(Object param1)
// 		{
// 		    return reverseTrafo(l.indexOf(param1));
// 		}

// 		public int lastIndexOf(Object param1)
// 		{
// 		    return reverseTrafo(l.lastIndexOf(param1));
// 		}

// 		public boolean addAll(int param1, Collection param2)
// 		{
// 		    return l.addAll(trafo(param1), param2);
// 		}

// 		public boolean addAll(Collection param1)
// 		{
// 		    return l.addAll(trafo(0), param2);
// 		}
// 		public boolean add(Object param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return false;
// 		}
// 		public void add(int param1, Object param2)
// 		{
// 		    // TODO: implement this java.util.List method
// 		}

// 		public Object get(int param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}
// 		public boolean contains(Object param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return false;
// 		}

// 		public int size()
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return 0;
// 		}

// 		public Object[] toArray()
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}

// 		public Object[] toArray(Object[] param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}

// 		public Iterator iterator()
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}

// 		public boolean remove(Object param1)
// 		{
// 		    return l.remove(param1);
// 		}

// 		public Object remove(int param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}
// 		public void clear()
// 		{
// 		    l.clear();
// 		}

// 		public boolean isEmpty()
// 		{
// 		    return l.isEmpty();
// 		}

// 		public Object set(int param1, Object param2)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}

// 		public boolean containsAll(Collection param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return false;
// 		}

// 		public boolean removeAll(Collection param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return false;
// 		}

// 		public boolean retainAll(Collection param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return false;
// 		}

// 		public List subList(int param1, int param2)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}

// 		public ListIterator listIterator(int param1)
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}

// 		public ListIterator listIterator()
// 		{
// 		    // TODO: implement this java.util.List method
// 		    return null;
// 		}

// 	    };
//     }

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
     * @param what states what data in the collection is requested. All if null. See Also {@link Filters}.
     * @param where states what predicate is checked as condition for selecting data elements. None if null.
     * @param orderBy states how to sort every two data elements. No sorting if null.
     * @param asc whether to use ascending order, or descending.
     *  If false, orderBy comparator will be used reverse.
     * @return a filter that selects the specified data from the source of data it is applied upon.
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @see Filters
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
	List sel = new LinkedList();	// selection of Elements with suited Adjectives

	// for each in FROM
	for (Iterator i = from.iterator(), c = wherePredicates.iterator(); i.hasNext(); ) {

	    // SELECT element
	    Object	  el = i.next();

	    Predicate where = (Predicate) c.next();

	    // WHERE associated Adjective suits
	    if (where == null || where.apply(el))
		sel.add(el);
	} 

	// RESULTSET
	return (what == null ? sel : (Collection) what.apply(sel));	   // filter the Data Collection of the selected Elements
    } 
}
