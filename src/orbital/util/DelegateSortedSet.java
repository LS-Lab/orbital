/**
 * @(#)DelegateSortedSet.java 0.9 2000/09/05 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.SortedSet;
import java.util.Comparator;

import java.util.Collection;

/**
 * A DelegateSortedSet that works as a delegator to sets.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class DelegateSortedSet/*<A>*/ extends DelegateSet/*<A>*/ implements SortedSet/*<A>*/ {
    private static final long serialVersionUID = 1729676522058590201L;
    /**
     * Extend to create a SortedSet delegating to an implementation SortedSet.
     * @param delegatee the implementation-SortedSet to that SortedSet operations are delegated.
     */
    protected DelegateSortedSet(SortedSet/*<A>*/ delegatee) {
        super(delegatee);
    }

    protected void setDelegatee(SortedSet/*<A>*/ delegatee) {
        super.setDelegatee(delegatee);
    }

    protected void setDelegatee(Collection/*<A>*/ delegatee) {
        if (delegatee instanceof SortedSet)
            setDelegatee((SortedSet/*<A>*/) delegatee);
        else
            throw new IllegalArgumentException("setDelegatee requires SortedSet instance for DelegateSortedSets");
    }

    // delegate SortedSet getDelegatee()

    public Comparator comparator() {
        return ((SortedSet) getDelegatee()).comparator();
    }

    public Object/*>A<*/ first() {
        return ((SortedSet/*<A>*/) getDelegatee()).first();
    }

    public Object/*>A<*/ last() {
        return ((SortedSet/*<A>*/) getDelegatee()).last();
    }

    public SortedSet/*<A>*/ headSet(Object/*>A<*/ to) {
        return ((SortedSet/*<A>*/) getDelegatee()).headSet(to);
    }

    public SortedSet/*<A>*/ tailSet(Object/*>A<*/ from) {
        return ((SortedSet/*<A>*/) getDelegatee()).tailSet(from);
    }

    public SortedSet/*<A>*/ subSet(Object/*>A<*/ from, Object/*>A<*/ to) {
        return ((SortedSet/*<A>*/) getDelegatee()).subSet(from, to);
    }
}
