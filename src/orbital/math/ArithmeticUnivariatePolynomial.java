/**
 * @(#)ArithmeticUnivariatePolynomial.java 1.0 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;


import java.util.ListIterator;

import java.util.Collections;
import java.util.Arrays;
import orbital.util.Setops;

import orbital.logic.functor.Functionals;
import orbital.logic.functor.Predicates;

class ArithmeticUnivariatePolynomial/*<R implements Arithmetic>*/ extends AbstractUnivariatePolynomial {
    private static final long serialVersionUID = -7008637791438268097L;
    /**
     * The coefficients in R.
     * @serial
     */
    private Arithmetic/*>R<*/ coefficients[];
    /**
     * Caches the degree value.
     * @see #degree()
     */
    private transient int degree;
    public ArithmeticUnivariatePolynomial(int degree) {
        this.coefficients =
	    degree < 0
	    ? new Arithmetic/*>R<*/[0]
	    : new Arithmetic[degree + 1];
	this.degree = java.lang.Integer.MIN_VALUE;
    }
    public ArithmeticUnivariatePolynomial(Arithmetic/*>R<*/ coefficients[]) {
        set(coefficients);
    }
  
    /**  
     * Sustain transient variable initialization when deserializing.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
	in.defaultReadObject();
	// Recalculate redundant transient cache fields.
	set(coefficients);
    }

    protected UnivariatePolynomial/*<R>*/ newInstance(int degree) {
	return new ArithmeticUnivariatePolynomial(degree);
    }

    public final int degreeValue() {
	return degree;
    }
    /**
     * Implementation calculating the degree of a polynomial,
     * given its coefficients.
     */
    private int degreeImpl(Arithmetic/*>R<*/[] coefficients) {
	for (int i = coefficients.length - 1; i >= 0; i--)
	    //@internal we can allow skipping null here, since set(R[]) and set(int,R)
	    // check for null. However after new ArithmeticPolynomial(int) there may still be
	    // some null values, until all have been set
	    if (coefficients[i] != null && !coefficients[i].norm().equals(Values.ZERO))
		return i;
	return java.lang.Integer.MIN_VALUE;
    }
	
    private void set(Arithmetic/*>R<*/ coefficients[]) {
	if (coefficients == null)
	    throw new IllegalArgumentException("illegal coefficients array: " + coefficients);
	if (Setops.some(Arrays.asList(coefficients), Functionals.bindSecond(Predicates.equal, null)))
	    throw new IllegalArgumentException("illegal coefficients: containing null");
 	this.coefficients = coefficients;
	this.R_ZERO = coefficients.length > 0 ? coefficients[0].zero() : Values.ZERO;
	this.degree = degreeImpl(coefficients);
    }

    public Arithmetic/*>R<*/ get(int i) {
	if (i <= degreeValue() && i >= coefficients.length)
	    throw new ArrayIndexOutOfBoundsException(coefficients.length + "=<" + i + "=<" + degreeValue() + "=" + degreeImpl(coefficients));
	return i <= degreeValue() ? coefficients[i] : R_ZERO;
    }
	
    public void set(int i, Arithmetic/*>R<*/ vi) {
	if (vi == null)
	    throw new IllegalArgumentException("illegal coefficient value: " + vi);
	final int oldDegree = degreeValue();
	if (i >= coefficients.length)
	    throw new UnsupportedOperationException("setting coefficients beyond the degree not (always) supported");
	coefficients[i] = vi;
	this.R_ZERO = coefficients.length > 0 ? coefficients[0].zero() : Values.ZERO;
	if (i >= oldDegree)
	    this.degree = degreeImpl(coefficients);
    }

    public Arithmetic/*>R<*/[] getCoefficients() {
	if (degreeValue() < 0)
	    return new Arithmetic/*>R<*/[0];
	return (Arithmetic/*>R<*/[]) coefficients.clone();
    } 

    Tensor tensorViewOfCoefficients() {
	return Values.getDefaultInstance().tensor(coefficients);
    }
}
