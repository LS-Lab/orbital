/**
 * @(#)RVector.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.awt.Point;
import java.util.Iterator;

import java.util.Arrays;
import orbital.util.Utility;

/**
 * Represents a real mathematical Vector in <b>R</b><sup>n</sup> with <code>double</code> values.
 * <p>
 * The components <code>v<sub>i</sub> in <b>R</b></code> are double-values so this is a fast implementation.
 * </p>
 * We could implement a new version, that will fall-back to more general arithmetic vector whenever necessary but this in turn consumes a little
 * time for conversion. f.ex. if an operation is performed with complex numbers or an arithmetic vector.
 * </p>
 * 
 * @structure composite D:double[] unidirectional
 * @version 1.0, 2000/08/08
 * @author  Andr&eacute; Platzer
 * @internal RVector is not a Vector<Real> because of its fall-back behaviour to Vector<Arithmetic>
 */
class RVector extends AbstractVector implements Cloneable {

    /**
     * Gets zero Vector, with all elements set to <code>0</code>.
     */
    public static final Vector ZERO(int length) {
	return CONST(length, 0);
    } 

    /**
     * Gets base Vector <code>i</code>, with all elements set to <code>0</code> except element <code>i</code> set to <code>1</code>.
     * These <code>e<sub>i</sub></code> are the standard base of <code><b>R</b><sup>n</sup></code>:
     * <code>&forall;x&isin;<b>R</b><sup>n</sup> &exist;! x<sub>k</sub>&isin;<b>R</b>: x = x<sub>1</sub>*e<sub>1</sub>+...+x<sub>n</sub>*e<sub>n</sub></code>.
     */
    public static final Vector BASE(int length, int e_i) {
	RVector base = new RVector(length);
	Arrays.fill(base.D, 0);
	base.D[e_i] = 1;
	return base;
    } 

    /**
     * Gets a constant Vector, with all elements set to <code>c</code>.
     */
    public static final Vector CONST(int length, double c) {
	RVector constant = new RVector(length);
	Arrays.fill(constant.D, c);
	return constant;
    } 

    /**
     * contains the vector double data elements
     * @serial
     */
    protected double D[];

    /**
     * creates a new Vector with dimension length.
     */
    public RVector(int length) {
	D = new double[length];
    }

    /**
     * creates a new empty Vector with dimension <code>0</code>.
     */
    public RVector() {
	this(0);
    }

    /**
     * creates a new Vector from an array of doubles.
     * @todo could we forget about cloning v?
     */
    public RVector(double values[]) {
	D = (double[]) values/*.clone()*/;
    }
    public RVector(Real values[]) {
	D = new double[values.length];
	for (int i = 0; i < D.length; i++)
	    D[i] = values[i].doubleValue();
    }
    public RVector(Point p) {
	this(2);
	D[0] = p.x;
	D[1] = p.y;
    }

    protected Vector newInstance(int dim) {
	return new RVector(dim);
    } 


    public final int dimension() {
	return D.length;
    } 

    public Arithmetic get(int i) {
	validate(i);
	return Values.valueOf(D[i]);
    } 
    public double getDoubleValue(int i) {
	validate(i);
	return D[i];
    } 
    public void set(int i, double vi) {
	validate(i);
	//do not do modCount++;
	D[i] = vi;
    } 
    public void set(int i, Arithmetic vi) {
	set(i, ((Real) vi).doubleValue());
    } 

    protected void set(Arithmetic[] v) {
	modCount++;
	D = new double[v.length];
	for (int i = 0; i < v.length; i++)
	    set(i, v[i]);
    }

    public Vector add(Vector b) {
	if (!(b instanceof RVector))
	    // fall-back to more general operation
	    return new ArithmeticVector(toArray()).add(b);
	Utility.pre(dimension() == b.dimension(), "Vector A+B only defined for same size");
    	RVector bb = (RVector)b;
    	RVector ret = new RVector(dimension());
    	// component-wise
    	for (int i = 0; i < dimension(); i++)
	    ret.D[i] = D[i] + bb.D[i];
    	return ret;
    }

    public Vector subtract(Vector b) {
	if (!(b instanceof RVector))
	    // fall-back to more general operation
	    return new ArithmeticVector(toArray()).subtract(b);
	Utility.pre(dimension() == b.dimension(), "Vector A-B only defined for same size");
      	RVector bb = (RVector)b;
      	RVector ret = new RVector(dimension());
      	// component-wise
      	for (int i = 0; i < dimension(); i++)
	    ret.D[i] = D[i] - bb.D[i];
      	return ret;
    }
	 
    public Vector scale(double s) {
	RVector ret = new RVector(dimension());

	// component-wise
	for (int i = 0; i < dimension(); i++)
	    ret.D[i] = D[i] * s;
	return ret;
    } 

    public Arithmetic scale(Arithmetic b) {
	if (!Real.isa.apply(b))
	    // fall-back to more general multiplication
	    return new ArithmeticVector(toArray()).scale(b);
	return scale(((Real)b).doubleValue());
    }

    public Arithmetic multiply(Vector b) {
	if (!(b instanceof RVector))
	    // fall-back to more general multiplication
	    return new ArithmeticVector(toArray()).multiply(b);
	Utility.pre(dimension() == b.dimension(), "vectors for dot-product must have equal dimension");
	RVector bb = (RVector) b;
	double  ret = 0;
	for (int i = 0; i < dimension(); i++)
	    ret += D[i] * bb.D[i];
	return Values.valueOf(ret);
    } 

    /**
     * @pre dimension() == 3 && dimension() == b.dimension()
     */
    public Vector cross(Vector b) {
	if (!(b instanceof RVector))
	    // fall-back to more general cross
	    return new ArithmeticVector(toArray()).cross(b);
	Utility.pre(dimension() == 3 && dimension() == b.dimension(), "domain of cross-product is 3D");
	RVector bb = (RVector) b;
	return new RVector(new double[] {
	    getDoubleValue(1) * bb.getDoubleValue(2) - getDoubleValue(2) * bb.getDoubleValue(1),
	    getDoubleValue(2) * bb.getDoubleValue(0) - getDoubleValue(0) * bb.getDoubleValue(2),
	    getDoubleValue(0) * bb.getDoubleValue(1) - getDoubleValue(1) * bb.getDoubleValue(0)
	});
    } 


    /**
     * Returns an array containing all the elements in this vector.
     */
    public double[] toDoubleArray() {
	return (double[]) D.clone();
    } 

}
