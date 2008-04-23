/**
 * @(#)QueuedIterator.java 0.9 2000/08/09 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Iterator;
import java.io.Serializable;
import java.util.Collection;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.NoSuchElementException;

import orbital.logic.functor.BinaryFunction;

/**
 * QueuedIterator is an iterator that queues elements added to it and provides
 * an iterator view to them.
 * <p>
 * Much like queues, elements added are returned in FIFO order.</p>
 * <p>
 * The List implementation part of this class provides an interface to the internal data queue
 * maintained.
 * Objects will be returned (via iterator view) from the head of it, and therefore objects appended
 * to it are returned in FIFO order.</p>
 * 
 * @structure forwards List to super
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @internal see orbital.util.Queue
 */
public class QueuedIterator extends DelegateList implements Iterator, Serializable {
    /**
     * Create a new queued iterator.
     * @param synchronizedQueue whether to use {@link java.util.Collections#synchronizedList(java.util.List)} on the buffering queue.
     */
    public QueuedIterator(boolean synchronizedQueue) {
        super(synchronizedQueue ? Collections.synchronizedList(new LinkedList()) : new LinkedList());
    }
    /**
     * Create a new non-synchronized queued iterator.
     */
    public QueuedIterator() {
        this(false);
    }

    // iterator implementation

    /**
     * Checks whether the queued iterator currently has a next element.
     * <p>
     * <b>Note:</b> This state might change when someone added data.</p>
     * @see #isEmpty()
     */
    public boolean hasNext() {
        return !isEmpty();
    } 

    /**
     * Returns the elements added to this queued iterator in FIFO order.
     * @return the first object added which has not yet been returned.
     */
    public Object next() {
        try {
            return remove(0);
        } catch (IndexOutOfBoundsException x) {
            throw new NoSuchElementException();
        } 
    } 

    /**
     * Not supported.
     * @throws UnsupportedOperationException on every call.
     */
    public void remove() {
        throw new UnsupportedOperationException("senseless, has already been removed from the queue on the call to next");
    } 

    // additional enqueueing methods

    /**
     * Add all objects of an iterator to this queued iterator.
     * @see orbital.logic.functor.Functionals#foldRight(BinaryFunction, Object, Iterator)
     */
    public boolean addAll(Iterator i) {
        boolean changed = false;
        while (i.hasNext())
            changed |= add(i.next());
        return changed;
    } 
}
