/**
 * @(#)DelegateMap.java 0.9 2000/09/24 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.io.Serializable;

import orbital.util.Utility;

/**
 * A DelegateMap that works as a delegator to maps.
 * It implements the <code>java.util.Map</code> interface itself, and so
 * a DelegateMap is a Map delegating to a specified implementation
 * Map.
 *
 * @structure delegate delegatee:java.util.Map
 * @structure implements java.util.Map
 * @structure implements java.io.Serializable
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class DelegateMap/*<A, B>*/ implements Map/*<A, B>*/, Serializable {
    private static final long serialVersionUID = -2676126214337402972L;
    /**
     * Extend to create a map delegating nowhere.
     * For delegatee will be set to <code>null</code>, this object will throw
     * NullPointerExceptions in almost every method.
     */
    private DelegateMap() {
        delegatee = null;
    }

    /**
     * Extend to create a map delegating to an implementation map.
     * @param delegatee the implementation-map to that map operations are delegated.
     */
    protected DelegateMap(Map/*<A, B>*/ delegatee) {
        this.delegatee = delegatee;
    }

    // delegation operations

    /**
     * Contains the delegatee map to which operations are be delegated.
     * @serial serialization of the map delegated to.
     */
    private Map/*<A, B>*/ delegatee = null;

    /**
     * Get the delegatee map to which operations are delegated.
     * @return the implementation-map that map operations are delegated to.
     */
    protected Map/*<A, B>*/ getDelegatee() {
        return this.delegatee;
    } 

    /**
     * Set the delegatee map to which operations are delegated.
     * @param delegatee the implementation-map that map operations are delegated to.
     */
    protected void setDelegatee(Map/*<A, B>*/ delegatee) {
        this.delegatee = delegatee;
    } 

    // Delegated operations.

    public int size() {
        return getDelegatee().size();
    }

    public boolean isEmpty() {
        return getDelegatee().isEmpty();
    }

    public boolean containsKey(Object key) {
        return getDelegatee().containsKey(key);
    }

    public boolean containsValue(Object v) {
        return getDelegatee().containsValue(v);
    }

    public Object/*>B<*/ get(Object key) {
        return getDelegatee().get(key);
    }

    public Object/*>B<*/ put(Object/*>A<*/ key, Object/*>B<*/ value) {
        return getDelegatee().put(key, value);
    }

    public Object/*>B<*/ remove(Object key) {
        return getDelegatee().remove(key);
    }

    public void putAll(Map/*<? extends A, ? extends B>*/ t) {
        getDelegatee().putAll(t);
    }

    public void clear() {
        getDelegatee().clear();
    }

    public Set/*<A>*/ keySet() {
        return getDelegatee().keySet();
    }

    public Collection/*<B>*/ values() {
        return getDelegatee().values();
    }

    public Set/*<Entry<A, B>>*/ entrySet() {
        return getDelegatee().entrySet();
    }

    public boolean equals(Object o) {
        return Utility.equals(getDelegatee(), o);
    }

    public int hashCode() {
        return Utility.hashCode(getDelegatee());
    }
    
}
