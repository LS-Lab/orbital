/*
 * @(#)Pair.java 1.0 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.io.Serializable;

import orbital.util.Utility;

/**
 * Pair class combines two objects in one.
 * <p>
 * For equality and similar methods, both objects contained is considered.</p>
 * 
 * @version 1.0, 1996/03/04
 * @author  Andr&eacute; Platzer
 * @see KeyValuePair
 */
public class Pair/*<T1, T2>*/ extends Object implements Comparable/*_<Pair<T1, T2>>_*/, Serializable {
    private static final long serialVersionUID = 9024808570172404957L;
    /**
     * @serial
     */
    public Object/*>T1<*/ A;

    /**
     * @serial
     */
    public Object/*>T2<*/ B;

    /**
     * Create a new pair &lang;a, b&rang;.
     */
    public Pair(Object/*>T1<*/ a, Object/*>T2<*/ b) {
	A = a;
	B = b;
    }
    public Pair() {
	this(null, null);
    }

    /**
     * Checks two Pair objects for equal A and equal B.
     */
    public boolean equals(Object o) {
	if (o instanceof Pair) {
	    Pair b = (Pair) o;
	    return Utility.equals(A, b.A) && Utility.equals(B, b.B);
	} else
	    return false;
    } 
	
    public int hashCode() {
	return Utility.hashCode(A) ^ (Utility.hashCode(B) << 1);
    }

    /**
     * Compares two Pairs in favor of A (and then B).
     */
    public int compareTo(Object o) {
	Pair b = (Pair) o;
	int a = Utility.compare(A, b.A);
	return a != 0 ? a : Utility.compare(B, b.B);
    } 

    public String toString() {
	return "<" + A + "," + B + ">";
    } 

    public Object/*>T1<*/ getA() {
	return A;
    }

    public void setA(Object/*>T1<*/ newA) {
	this.A = newA;
    }

    public Object/*>T2<*/ getB() {
	return B;
    }

    public void setB(Object/*>T2<*/ newB) {
	this.B = newB;
    }

}
