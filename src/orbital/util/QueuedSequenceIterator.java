/**
 * @(#)QueuedSequenceIterator.java 0.7 2001/08/01 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * A queued SequenceIterator represents the (mutable) logical concatenation of other iterators.
 * It starts out with a list of iterators and reads from the first one until the end is reached,
 * whereupon it reads from the second one, and so on, until the end is reached on the last of the
 * contained iterators.
 * <p>
 * QueuedSequenceIterator is effectively a combination of SequenceIterator and QueuedIterator.
 * It is introduced separately, since its construction is necessarily less performant than
 * the unmodifiable version, SequenceIterator.</p>
 *
 * @version 0.9, 2001/08/01
 * @author  Andr&eacute; Platzer
 * @invariants current &isin; iterators && (!hasNext() xor next() &isin; RES(current))
 * @see SequenceIterator
 * @see QueuedIterator
 * @todo could we reunite this with SequenceIterator and QueuedIterator?
 */
public class QueuedSequenceIterator implements Iterator/*<A>*/, Serializable {
    /**
     * The list of iterators whose elements we return.
     * @serial this class is serializable if and only if all its content iterators are serializable.
     */
    private final List/*<Iterator<A>>*/ iterators;
    /**
     * The current iterator in <code>iterators</code> whose elements we return.
     * @serial
     */
    private Iterator/*<A>*/	   current;
    /**
     * The iterator in <code>iterators</code> used to return the last element.
     * @serial
     */
    private Iterator/*<A>*/	   lastUsed;
    /**
     * Create a new sequence iterator over an iterator of iterators.
     * @param iterators is an iterator over iterators whose elements this SequenceIterator will provide,
     *  one after one.
     */
    public QueuedSequenceIterator(Iterator/*<Iterator<A>>*/ iterators) {
        this(Setops.asList(iterators));
    }
    /**
     * Create a new sequence iterator over a list of iterators.
     * <p>
     * Note that modifying iterators will result in a ConcurrentModificationException at runtime,
     * as per general contract of list iterators.</p>
     * @param iterators is a list of iterators whose elements this SequenceIterator will provide,
     *  one after one.
     */
    public QueuedSequenceIterator(List/*<Iterator<A>>*/ iterators) {
        this.iterators = iterators instanceof LinkedList ? iterators : new LinkedList(iterators);
    }
    /**
     * Create a new sequence iterator over an array of iterators.
     * @param iterators is an array of iterators whose elements this SequenceIterator will provide,
     *  one after one.
     */
    public QueuedSequenceIterator(Iterator/*<A>*/ iterators[]) {
        this(Arrays.asList(iterators));
    }

    public boolean hasNext() {
    	while (true) {
	    if (current != null && current.hasNext())
		return true;
	    if (!iterators.isEmpty()) {
		if ((current = (Iterator) iterators.remove(0)) == null)
		    throw new NullPointerException("null is not an iterator");
	    } else
		return false;
    	}
    }
    public Object/*>A<*/ next() {
    	while (true) {
	    if (current != null && current.hasNext()) {
		lastUsed = current;
		return current.next();
	    }
	    if (!iterators.isEmpty()) {
		if ((current = (Iterator) iterators.remove(0)) == null)
		    throw new NullPointerException("null is not an iterator");
	    } else
		throw new NoSuchElementException();
    	}
    }
    public void remove() {
    	if (lastUsed != null)
	    lastUsed.remove();
    	else
	    throw new IllegalStateException();
    }
    
    // some List methods for adding iterators
    
    /**
     * Append an iterator to this queued sequence iterator.
     */
    public boolean add(Object/*>Iterator<A><*/ o) {
    	return iterators.add(o);
    }

    /**
     * Insert an iterator at the specified position into this queued sequence iterator.
     */
    public void add(int index, Object/*>Iterator<A><*/ o) {
    	iterators.add(index, o);
    	if (index == 0) {
	    // exchange current iterator
	    // "push" the rest of the current iteration for later use
	    iterators.add(1, current);
	    // use o as the new current iteration
	    current = (Iterator) o;
    	}
    }
    
}
