/**
 * @(#)ArithmeticMultinomial.java 1.0 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;


import java.util.ListIterator;

import java.util.Collections;
import java.util.Arrays;
import orbital.util.Setops;
import orbital.util.Utility;

import orbital.logic.functor.Functionals;
import orbital.logic.functor.Predicates;
import orbital.math.functional.Operations;
import orbital.algorithm.Combinatorical;

class ArithmeticMultinomial/*<R implements Arithmetic>*/ extends AbstractMultinomial {
    //private static final long serialVersionUID = 0;
    /**
     * The coefficients in R.
     * @serial
     */
    private Tensor coefficients;
    /**
     * Caches the degree value.
     * @see #degree()
     */
    private transient int degree;
    public ArithmeticMultinomial(int[] dimensions) {
 	coefficients = Values.newInstance(dimensions);
	this.CONSTANT_TERM = new int[dimensions.length];
	Arrays.fill(CONSTANT_TERM, 0);
    }
    public ArithmeticMultinomial(Object coefficients) {
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

    protected Multinomial/*<R>*/ newInstance(int[] dimensions) {
	return new ArithmeticMultinomial(dimensions);
    }

    public final int degreeValue() {
	return degree;
    }
    /**
     * Implementation calculating the degree of a polynomial,
     * given its coefficients.
     * @internal optimizable by far, start with big indices, not with 0,...,0
     */
    private int degreeImpl(Tensor coefficients) {
	int d = java.lang.Integer.MIN_VALUE;
	for (Combinatorical index = Combinatorical.getPermutations(coefficients.dimensions()); index.hasNext(); ) {
	    final int[] i = index.next();
	    final Arithmetic vi = coefficients.get(i);
	    if (vi != null && !vi.norm().equals(Values.ZERO)) {
		final int sum = ((Integer)Operations.sum.apply(Values.valueOf(i))).intValue();
		if (sum > d)
		    d = sum;
	    }
	}
	return d;
    }
	
    public int numberOfVariables() {
	return coefficients.rank();
    }

    public int[] dimensions() {
	//@todo we should restrict the dimensions to the non-ZERO part.
	return coefficients.dimensions();
    }
    
    private void set(Object coefficients) {
	if (coefficients == null)
	    throw new IllegalArgumentException("illegal coefficients array: " + coefficients);
 	Tensor t = Values.tensor(coefficients);
	if (Setops.some(t.iterator(), Functionals.bindSecond(Predicates.equal, null)))
	    throw new IllegalArgumentException("illegal coefficients: containing null");
	this.coefficients = t;
	this.degree = degreeImpl(this.coefficients);
	this.CONSTANT_TERM = new int[dimensions().length];
	Arrays.fill(CONSTANT_TERM, 0);
    }

    public Arithmetic/*>R<*/ get(int[] i) {
	Utility.pre(i.length == numberOfVariables(), "illegal number of indices (" + i.length + " indices) for a coefficient of a polynomial with " + numberOfVariables() + " variables");
	for (int k = 0; k < i.length; k++)
	    if (i[k] >= dimensions()[k])
		return get(CONSTANT_TERM).zero();
	return coefficients.get(i);
    }
	
    public void set(int[] i, Arithmetic/*>R<*/ vi) {
	if (vi == null)
	    throw new IllegalArgumentException("illegal coefficient value: " + vi);
	final Integer oldDegree = degree();
	coefficients.set(i, vi);
	if (oldDegree.compareTo(Operations.sum.apply(Values.valueOf(i))) <= 0)
	    this.degree = degreeImpl(coefficients);
    }

    Tensor tensorViewOfCoefficients() {
	return coefficients;
    }
}
