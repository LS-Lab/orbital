/*
 * @(#)Sequence.java 0.9 1998/04/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.deprecated;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Represents an abstraction of a sequence of elements.
 * 
 * <p>Method names have now been adapted to fit those of a {@link java.util.ListIterator}.
 * So those can easily be extended to be a whole sequence.
 * </p>
 * 
 * @version 0.9, 2000/01/18
 * @author  Andr&eacute; Platzer
 * @see java.util.List
 * @see java.util.Iterator
 * @see java.util.ListIterator
 * @see <a href="{@docRoot}/DesignPatterns/iterator.html">Iterator Pattern</a>
 * @invariant hasNext() <=> SUCCEEDS(next())
 * && hasPrevious() <=> SUCCEEDS(previous())
 */
public
interface Sequence extends ListIterator {

	/**
	 * Returns the element with index in the Sequence.
	 * @return the element at the specified index from the current position.
	 * @throws java.util.NoSuchElementException if element with index is not contained.
	 */
	//Object element(int offset) throws NoSuchElementException;

	/**
	 * Tests if this Sequence has more Elements.
	 * @pre true
	 * @post RES == (count()!=0)
	 */
	boolean hasNext();

	/**
	 * Tests if this Sequence has a previous element.
	 * @pre true
	 * @post RES == (cursor>0)
	 */
	boolean hasPrevious();

	/**
	 * Returns the next Element in the Sequence not yet returned by
	 * element(i) or next().
	 * @throws java.util.NoSuchElementException   if prerequisite hasNext() is not satisifed.
	 * @pre hasNext()
	 * @post count()==OLD(count())-1
	 */
	Object next() throws NoSuchElementException;

	/**
	 * Returns the previous element in the sequence already returned by
	 * next().
	 * @throws java.util.NoSuchElementException   if prerequisite hasPrevious() is not satisifed.
	 * @pre hasPrevious()
	 * @post count()==OLD(count())+1
	 */
	Object previous() throws NoSuchElementException;

	// modification operations

	/*
	 * //TODO: javadoc
	 * Inserts the specified element into the list (optional operation).  The
	 * element is inserted immediately before the next element that would be
	 * returned by <tt>next</tt>, if any, and after the next element that
	 * would be returned by <tt>previous</tt>, if any.
	 * 
	 * @param o the element to insert.
	 * @throws UnsupportedOperationException if the <tt>add</tt> method is
	 * not supported by this list iterator.
	 * @throws ClassCastException if the class of the specified element
	 * prevents it from being added to this Set.
	 * @throws IllegalArgumentException if some aspect of this element
	 * prevents it from being added to this Collection.
	 * @post count()==OLD(count())+1
	 */
	void add(Object o);

	/**
	 * Remove the Element from the Sequence that was last returned by next() or previous() or element(int).
	 * @pre hasNext() || hasPrevious()
	 * @post count()==OLD(count())-1
	 * @return the object that has been removed.
	 */
	void remove();

	void set(Object o);
}
