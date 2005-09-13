/**
 * @(#)DelegateList.java 0.9 2000/09/05 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.List;
import java.util.ListIterator;

import java.util.Collection;

/**
 * A DelegateList that works as a delegator to lists.
 * <p>
 * This class simply implements List and extends DelegateCollection, since
 * a DelegateCollection can also be used as a delegate for Lists due
 * to the identical interfaces differing only by semantics.</p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class DelegateList/*<A>*/ extends DelegateCollection/*<A>*/ implements List/*<A>*/ {
    private static final long serialVersionUID = 6248806460318686880L;
    /**
     * Extend to create a List delegating to an implementation List.
     * @param delegatee the implementation-List to that List operations are delegated.
     */
    protected DelegateList(List/*<A>*/ delegatee) {
	super(delegatee);
    }

    protected void setDelegatee(List/*<A>*/ delegatee) {
	super.setDelegatee(delegatee);
    }

    protected void setDelegatee(Collection/*<A>*/ delegatee) {
	if (delegatee instanceof List/*<A>*/)
	    setDelegatee((List/*<A>*/) delegatee);
	else
	    throw new IllegalArgumentException("ListDelegatee requires List instance for DelegateLists");
    }

    public void add(int index, Object/*>A<*/ element) {
	((List/*<A>*/) getDelegatee()).add(index, element);
    }

    public boolean addAll(int index, Collection/*<A>*/ c) {
	return ((List/*<A>*/) getDelegatee()).addAll(index, c);
    }
	
    public Object/*>A<*/ get(int index) {
	return ((List/*<A>*/) getDelegatee()).get(index);
    }
	
    public int indexOf(Object/*>A<*/ o) {
	return ((List/*<A>*/) getDelegatee()).indexOf(o);
    }
	
    public int lastIndexOf(Object/*>A<*/ o) {
	return ((List/*<A>*/) getDelegatee()).lastIndexOf(o);
    }
	
    public ListIterator/*<A>*/ listIterator() {
	return ((List/*<A>*/) getDelegatee()).listIterator();
    }

    public ListIterator/*<A>*/ listIterator(int index) {
	return ((List/*<A>*/) getDelegatee()).listIterator(index);
    }

    public Object/*>A<*/ remove(int index) {
	return ((List/*<A>*/) getDelegatee()).remove(index);
    }

    public Object/*>A<*/ set(int index, Object/*>A<*/ element) {
	return ((List/*<A>*/) getDelegatee()).set(index, element);
    }

    public List/*<A>*/ subList(int fromIndex, int toIndex) {
	return ((List/*<A>*/) getDelegatee()).subList(fromIndex, toIndex);
    }
}
