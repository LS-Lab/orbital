/**
 * @(#)ArithmeticVector.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import java.awt.Point;
import java.util.Iterator;

/**
 * Represents a general mathematical Vector in A<sup>n</sup> with Arithmetic values.
 * <p>
 * The components v<sub>i</sub> in A are Arithmetic objects.</p>
 * 
 * @structure composite D:R[] unidirectional
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
class ArithmeticVector/*<R implements Arithmetic>*/ extends AbstractVector/*<R>*/ {
    private static final long serialVersionUID = 8981505445903028808L;
    /**
     * contains the vector arithmetic data elements
     * @serial
     */
    Arithmetic/*>R<*/ D[];

    /**
     * creates a new Vector with dimension length.
     */
    public ArithmeticVector(int length) {
	D = new Arithmetic/*>R<*/[length];
    }

    /**
     * creates a new empty Vector with dimension <code>0</code>.
     */
    public ArithmeticVector() {
	this(0);
    }

    /**
     * creates a new Vector from an array of Arithmetic values.
     * @todo could we forget about cloning v?
     */
    public ArithmeticVector(Arithmetic/*>R<*/ values[]) {
	D = (Arithmetic/*>R<*/[]) values/*.clone()*/;
    }

    protected Vector/*<R>*/ newInstance(int dim) {
	return new ArithmeticVector/*<R>*/(dim);
    } 

    public final int dimension() {
	return D.length;
    }

    //@todo perhaps iterator() could also return Arrays.asList(D).iterator()

    public Arithmetic/*>R<*/ get(int i) {
	validate(i);
	return D[i];
    } 
    public void set(int i, Arithmetic/*>R<*/ vi) {
	validate(i);
	//do not do modCount++;
	D[i] = vi;
    } 
    protected void set(Arithmetic/*>R<*/[] v) {
	modCount++;
	D = v;
    }

    public Object clone() {
	return new ArithmeticVector(toArray());
    } 

    /**
     * Returns an array containing all the elements in this vector.
     */
    public Arithmetic/*>R<*/[] toArray() {
	return (Arithmetic/*>R<*/[]) D.clone();
    } 
}
