/**
 * @(#)KeyValuePair.java 1.0 1999/11/06 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.io.Serializable;

import orbital.util.Utility;

/**
 * This class is a KeyValuePair that contains a pair &lang;key,value&rang; each of which are objects.
 * An instance of KeyValuePair is mainly identified by its key value.
 * <p>
 * For equality and similar methods, only the key is considered.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Pair
 * @see java.util.Map.Entry
 */
public class KeyValuePair implements Comparable/*<Pair<A, B>>*/, Serializable {
    private static final long serialVersionUID = 5966210221949749252L;
    public KeyValuePair() {
        this(null, null);
    }
    /**
     * Create a new pair &lang;key, value&rang;.
     */
    public KeyValuePair(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * The key for this pair.
     * @serial
     */
    protected Object key;
    /**
     * The value of this pair.
     * @serial
     */
    protected Object value;

    /**
     * Get/Set-Methods.
     */
    public Object getKey() {
        return key;
    } 
    public void setKey(Object key) {
        this.key = key;
    } 

    public Object getValue() {
        return value;
    } 
    public void setValue(Object value) {
        this.value = value;
    } 

    /**
     * Compare two KeyValuePair objects according to their keys.
     * This will only work for Comparable keys and throw an exception otherwise.
     * @param o the (KeyValuePair) object to be compared to.
     * @return <code>key.compareTo(o)</code>, when key implements <tt>Comparable</tt>.
     * @throws ClassCastException when neither of the keys in the KeyValuePair objects compared implements <tt>Comparable</tt>,
     * or o is not even a KeyValuePair.
     */
    public int compareTo(Object o) throws ClassCastException {
        if (o instanceof KeyValuePair) {
            KeyValuePair b = (KeyValuePair) o;
            return ((Comparable) key).compareTo(b.key);
        } 
        throw new ClassCastException("keys are not Comparable");
    } 

    /**
     * Checks two KeyValuePair objects for equal keys.
     */
    public boolean equals(Object o) {
        return (o instanceof KeyValuePair) && Utility.equals(key, ((KeyValuePair) o).key);
    } 

    public int hashCode() {
        return Utility.hashCode(getKey());
    } 

    public String toString() {
        return "<" + (
                      java.util.logging.Logger.global.isLoggable(java.util.logging.Level.FINEST)
                      ? getKey().getClass().getName() + "@" + getKey()
                      : getKey()
                      ) + "|" + getValue() + ">";
    } 
}
