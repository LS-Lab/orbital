/**
 * @(#)DelegateIterator.java 1.2 2004-01-07 Andre Platzer
 * 
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Iterator;
import java.io.Serializable;

/**
 * A DelegateIterator that works as a delegator to iterators.
 * It implements the <code>java.util.Iterator</code> interface itself, and so
 * a DelegateIterator is an iterator delegating to a specified implementation
 * Iterator.
 * 
 * @structure delegate delegatee:java.util.Iterator
 * @structure implements java.util.Iterator
 * @structure implements java.io.Serializable
 * @version 1.2, 2004-01-07
 * @author  Andr&eacute; Platzer
 * @see java.util.Iterator
 */
public class DelegateIterator/*<A>*/ implements Iterator/*<A>*/, Serializable {
    //private static final long serialVersionUID = 0;

    /**
     * Extend to create a Iterator delegating nowhere.
     * For delegatee will be set to <code>null</code>, this object will throw
     * NullPointerExceptions in almost every method.
     */
    private DelegateIterator() {
	delegatee = null;
    }

    /**
     * Extend to create an Iterator delegating to an implementation Iterator.
     * @param delegatee the implementation-Iterator to that Iterator operations are delegated.
     */
    protected DelegateIterator(Iterator/*<A>*/ delegatee) {
	this.delegatee = delegatee;
    }

    // delegation operations

    /**
     * Contains the delegatee Iterator to which operations are be delegated.
     * @serial serialization of the collection delegated to.
     */
    private Iterator/*<A>*/ delegatee = null;

    /**
     * Get the delegatee Iterator to which operations are delegated.
     * @return the implementation-Iterator that Iterator operations are delegated to.
     */
    protected Iterator/*<A>*/ getDelegatee() {
	return this.delegatee;
    } 

    /**
     * Set the delegatee Iterator to which operations are delegated.
     * @param delegatee the implementation-Iterator that Iterator operations are delegated to.
     */
    protected void setDelegatee(Iterator/*<A>*/ delegatee) {
	this.delegatee = delegatee;
    } 

    // Delegated operations.

    public boolean hasNext() {
	return getDelegatee().hasNext();
    } 

    public Object next() {
	return getDelegatee().next();
    } 

    public void remove() {
	getDelegatee().remove();
    } 
}
