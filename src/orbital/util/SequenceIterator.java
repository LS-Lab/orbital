/**
 * @(#)SequenceIterator.java 0.9 2001/06/14 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

import java.util.NoSuchElementException;
import java.util.Arrays;
import orbital.logic.functor.Functionals;

/**
 * A SequenceIterator represents the logical concatenation of other iterators.
 * It starts out with a list of iterators and reads from the first one until the end is reached,
 * whereupon it reads from the second one, and so on, until the end is reached on the last of the
 * contained iterators.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @invariants current &isin; iterators && (!hasNext() xor next() &isin; RES(current))
 * @see java.io.SequenceInputStream
 * @todo could we extend this like QueuedIterator in order to allow adding new iterators at run-time? Then we could use it for orbital.algorithm.template.DepthFirstSearch.NodeIterator
 */
public class SequenceIterator implements Iterator, Serializable {
    private static final long serialVersionUID = -5334957890325354979L;
    /**
     * The iterator over the iterators whose elements we return.
     * @serial this class is serializable if and only if all its content iterators are serializable.
     */
    private final Iterator/*<Iterator>*/ iterators;
    /**
     * The current iterator in <code>iterators</code> whose elements we return.
     * @serial
     */
    private Iterator	   current;
    /**
     * Create a new sequence iterator over an iterator of iterators.
     * @param iterators is an iterator over iterators whose elements this SequenceIterator will provide,
     *  one after one.
     */
    public SequenceIterator(Iterator/*<Iterator>*/ iterators) {
        this.iterators = iterators;
    }
    /**
     * Create a new sequence iterator over a list of iterators.
     * <p>
     * Note that modifying iterators will result in a ConcurrentModificationException at runtime,
     * as per general contract of list iterators.</p>
     * @param iterators is a list of iterators whose elements this SequenceIterator will provide,
     *  one after one.
     */
    public SequenceIterator(List/*<Iterator>*/ iterators) {
        this(iterators.iterator());
	assert Setops.all(iterators, Functionals.bindSecond(Utility.instanceOf, Iterator.class)) : iterators + " instanceof List<Iterator>";
    }
    /**
     * Create a new sequence iterator over an array of iterators.
     * @param iterators is an array of iterators whose elements this SequenceIterator will provide,
     *  one after one.
     */
    public SequenceIterator(Iterator iterators[]) {
        this(Arrays.asList(iterators));
    }

    public boolean hasNext() {
    	while (true) {
	    if (current != null && current.hasNext())
		return true;
	    if (iterators.hasNext()) {
		if ((current = (Iterator) iterators.next()) == null)
		    throw new NullPointerException("null is not an iterator");
	    } else
		return false;
    	}
    }
    public Object next() {
    	while (true) {
	    if (current != null && current.hasNext())
		return current.next();
	    if (iterators.hasNext()) {
		if ((current = (Iterator) iterators.next()) == null)
		    throw new NullPointerException("null is not an iterator");
	    } else
		throw new NoSuchElementException();
    	}
    }
    public void remove() {
    	if (current != null)
	    current.remove();
    	else
	    throw new IllegalStateException();
    }
}
