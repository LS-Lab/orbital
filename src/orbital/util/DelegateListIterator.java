/**
 * @(#)DelegateListIterator.java 1.2 2004-01-07 Andre Platzer
 * 
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * A DelegateListIterator that works as a delegator to iterators.
 * It implements the <code>java.util.ListIterator</code> interface itself, and so
 * a DelegateListIterator is an iterator delegating to a specified implementation
 * ListIterator.
 * 
 * @structure delegate delegatee:java.util.ListIterator
 * @structure implements java.util.ListIterator
 * @structure implements java.io.Serializable
 * @version 1.2, 2004-01-07
 * @author  Andr&eacute; Platzer
 * @see java.util.ListIterator
 */
public class DelegateListIterator/*<A>*/ extends DelegateIterator implements ListIterator/*<A>*/ {
    //private static final long serialVersionUID = 0;

    /**
     * Extend to create an ListIterator delegating to an implementation ListIterator.
     * @param delegatee the implementation-ListIterator to that ListIterator operations are delegated.
     */
    protected DelegateListIterator(ListIterator/*<A>*/ delegatee) {
	super(delegatee);
    }

    // delegation operations

    protected void setDelegatee(ListIterator/*<A>*/ delegatee) {
	super.setDelegatee(delegatee);
    }

    protected void setDelegatee(Iterator/*<A>*/ delegatee) {
	if (delegatee instanceof ListIterator/*<A>*/)
	    setDelegatee((ListIterator/*<A>*/) delegatee);
	else
	    throw new IllegalArgumentException("setDelegatee requires ListIterator instance for DelegateListIterators");
    }

    // Delegated operations.

    public boolean hasPrevious() {
	return ((ListIterator)getDelegatee()).hasPrevious();
    } 

    public Object previous() {
	return ((ListIterator)getDelegatee()).previous();
    } 

    public void add(Object o) {
	((ListIterator)getDelegatee()).add(o);
    } 

    public void set(Object o) {
	((ListIterator)getDelegatee()).set(o);
    } 

    public int nextIndex() {
	return ((ListIterator)getDelegatee()).nextIndex();
    } 

    public int previousIndex() {
	return ((ListIterator)getDelegatee()).previousIndex();
    } 
}
