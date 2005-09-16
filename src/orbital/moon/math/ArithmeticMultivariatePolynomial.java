/**
 * @(#)ArithmeticMultivariatePolynomial.java 1.1 2002/08/21 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;


import java.util.ListIterator;

import java.util.Collections;
import java.util.Arrays;
import orbital.util.Setops;
import orbital.util.Utility;

import orbital.logic.functor.Functionals;
import orbital.logic.functor.Predicates;
import orbital.math.functional.Operations;
import orbital.algorithm.Combinatorical;

/**
 * Implementation of polynomials in R[N<sup>n</sup>] with a dense tensor of coefficients.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
class ArithmeticMultivariatePolynomial/*<R extends Arithmetic>*/
    extends AbstractMultivariatePolynomial/*<R>*/ {
    private static final long serialVersionUID = -6317707373482862125L;
    /**
     * The coefficients in R.
     * @serial
     */
    private Tensor/*<R>*/ coefficients;
    /**
     * Caches the degree value.
     * @see #degree()
     */
    private transient int degree;
    public ArithmeticMultivariatePolynomial(int[] dimensions) {
        coefficients = Values.getDefaultInstance().newInstance(dimensions);
        this.CONSTANT_TERM = new int[dimensions.length];
        Arrays.fill(CONSTANT_TERM, 0);
    }
    public ArithmeticMultivariatePolynomial(Tensor/*<R>*/ coefficients) {
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

    protected Polynomial/*<R,Vector<Integer>>*/ newInstance(int[] dimensions) {
        return new ArithmeticMultivariatePolynomial(dimensions);
    }

    public final int degreeValue() {
        return degree;
    }
    /**
     * Implementation calculating the degree of a polynomial,
     * given its coefficients.
     * @internal optimizable by far, start with big indices, not with 0,...,0
     */
    private int degreeImpl(Tensor/*<R>*/ coefficients) {
        int d = java.lang.Integer.MIN_VALUE;
        for (Combinatorical index = Combinatorical.getPermutations(coefficients.dimensions()); index.hasNext(); ) {
            final int[] i = index.next();
            final Arithmetic vi = coefficients.get(i);
            if (vi != null && !vi.norm().equals(Values.ZERO)) {
                final int sum = ((Integer)Operations.sum.apply(Values.getDefaultInstance().valueOf(i))).intValue();
                if (sum > d)
                    d = sum;
            }
        }
        return d;
    }
        
    public Object indexSet() {
        return Values.getDefaultInstance().valueOf(coefficients.rank());
    }

    public int[] dimensions() {
        //@todo we should restrict the dimensions to the non-ZERO part.
        return coefficients.dimensions();
    }
    
    private void set(Object coefficients) {
        if (coefficients == null)
            throw new IllegalArgumentException("illegal coefficients array: " + coefficients);
        set(Values.getDefaultInstance().tensor(coefficients));
    }
    private void set(Tensor/*<R>*/ coefficients) {
        if (coefficients == null)
            throw new IllegalArgumentException("illegal coefficients array: " + coefficients);
        if (Setops.some(coefficients.iterator(), Functionals.bindSecond(Predicates.equal, null)))
            throw new IllegalArgumentException("illegal coefficients: containing null");
        this.coefficients = coefficients;
        this.degree = degreeImpl(coefficients);
        this.CONSTANT_TERM = new int[dimensions().length];
        Arrays.fill(CONSTANT_TERM, 0);
    }

    /**
     * Converts an index (exponent) from Vector<Integer> to int[].
     */
    static final int[] convertIndex(Arithmetic indexAsVector) {
        Vector/*<Integer>*/ index = (Vector) indexAsVector;
        int[] i = new int[index.dimension()];
        for (int k = 0; k < i.length; k++)
            i[k] = ((Integer)index.get(k)).intValue();
        return i;
    }
    public final Arithmetic/*>R<*/ get(Arithmetic i) {
        return get(convertIndex(i));
    }
    public Arithmetic/*>R<*/ get(int[] i) {
        Utility.pre(i.length == ((Integer)indexSet()).intValue(), "illegal number of indices (" + i.length + " indices) for a coefficient of a polynomial with " + indexSet() + " variables");
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
        if (oldDegree.compareTo(Operations.sum.apply(Values.getDefaultInstance().valueOf(i))) <= 0)
            this.degree = degreeImpl(coefficients);
    }
    public final void set(Arithmetic i, Arithmetic/*>R<*/ vi) {
        set(convertIndex(i), vi);
    }

    Tensor/*<R>*/ tensorViewOfCoefficients() {
        return coefficients;
    }
}
