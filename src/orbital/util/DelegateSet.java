/**
 * @(#)DelegateSet.java 0.9 2000/09/05 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Set;

import java.util.Collection;

/**
 * A DelegateSet that works as a delegator to sets.
 * <p>
 * This class simply implements Set and extends DelegateCollection, since
 * a DelegateCollection can also be used as a delegate for Sets due
 * to the identical interfaces differing only by semantics.</p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class DelegateSet/*<A>*/ extends DelegateCollection/*<A>*/ implements Set/*<A>*/ {
    private static final long serialVersionUID = -1014157577972565745L;
    /**
     * Extend to create a Set delegating to an implementation Set.
     * @param delegatee the implementation-Set to that Set operations are delegated.
     */
    protected DelegateSet(Set/*<A>*/ delegatee) {
	super(delegatee);
    }

    protected void setDelegatee(Set/*<A>*/ delegatee) {
	super.setDelegatee(delegatee);
    }

    protected void setDelegatee(Collection/*<A>*/ delegatee) {
	if (delegatee instanceof Set/*<A>*/)
	    setDelegatee((Set/*<A>*/) delegatee);
	else
	    throw new IllegalArgumentException("setDelegatee requires Set instance for DelegateSets");
    }
}
