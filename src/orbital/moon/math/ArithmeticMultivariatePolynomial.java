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
import java.util.Iterator;

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
     * tags a dirty degree, i.e., when the degree cache is possibly out of sync
     */
    private static final int DIRTY = java.lang.Integer.MIN_VALUE + 10; 
    /**
     * The coefficients in R.
     * @serial
     */
    private Tensor/*<R>*/ coefficients;
    /**
     * Caches the degree value.
     * @see #degree()
     */
    private transient int degree = DIRTY;
    public ArithmeticMultivariatePolynomial(int[] dimensions, ValueFactory valueFactory) {
        super(valueFactory);
        if (dimensions.length == 0)
                throw new IllegalArgumentException("Empty polynomial ring without variables is not supported: specify non-empty list of dimensions instead.");
        coefficients = valueFactory.newInstance(dimensions);
        this.CONSTANT_TERM = new int[dimensions.length];
        Arrays.fill(CONSTANT_TERM, 0);
    }
    public ArithmeticMultivariatePolynomial(Tensor/*<R>*/ coefficients) {
        super(coefficients.valueFactory());
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
        return new ArithmeticMultivariatePolynomial(dimensions, valueFactory());
    }

    public final int degreeValue() {
        if (degree == DIRTY) {
                this.degree = degreeImpl(tensorViewOfCoefficients());
        }
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
            if (vi != null && !vi.isZero()) {
                final int sum = ((Integer)Operations.sum.apply(vi.valueFactory().valueOf(i))).intValue();
                if (sum > d)
                    d = sum;
            }
        }
        return d;
    }
        
    public Object indexSet() {
        return valueFactory().valueOf(coefficients.rank());
    }
    
    public Iterator entries() {
    	return coefficients.entries();
    }
    
    public int rank() {
        return coefficients.rank();
    }

    public int[] dimensions() {
        return coefficients.dimensions();
    }
    
    private void set(Object coefficients) {
        if (coefficients == null)
            throw new IllegalArgumentException("illegal coefficients array: " + coefficients);
        set(valueFactory().tensor(coefficients));
    }
    private void set(Tensor/*<R>*/ coefficients) {
        if (coefficients == null)
            throw new IllegalArgumentException("illegal coefficients array: " + coefficients);
        if (Setops.some(coefficients.iterator(), Functionals.bindSecond(Predicates.equal, null)))
            throw new IllegalArgumentException("illegal coefficients: containing null");
        this.coefficients = coefficients;
        this.degree = DIRTY;//degreeImpl(coefficients);
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
        if (i.length != ((Integer)indexSet()).intValue()) {
                throw new IllegalArgumentException("illegal number of indices (" + i.length + " indices) for a coefficient of a polynomial with " + indexSet() + " variables");
        }
        for (int k = 0; k < i.length; k++)
            if (i[k] >= dimensions()[k])
                return get(CONSTANT_TERM).zero();
        return coefficients.get(i);
    }
        
    public void set(int[] i, Arithmetic/*>R<*/ vi) {
        if (vi == null)
            throw new IllegalArgumentException("illegal coefficient value: " + vi);
        final int oldDegree = degree;
        coefficients.set(i, vi);
        final int newPotentialDegree = ((Integer)Operations.sum.apply(vi.valueFactory().valueOf(i))).intValue();
        if (vi.isZero()
                        ? oldDegree == newPotentialDegree
                        : oldDegree < newPotentialDegree) {
            // update degree if index is higher than degree and nonzero (because it might raise)
                // or equal and we reset to zero (because it might drop)
                //@todo delta-degrees can be optimized faster by exploiting that we know the old degree where to start 
            this.degree = DIRTY;//degreeImpl(coefficients);
        }
    }
    public final void set(Arithmetic i, Arithmetic/*>R<*/ vi) {
        set(convertIndex(i), vi);
    }

    Tensor/*<R>*/ tensorViewOfCoefficients() {
        return coefficients;
    }

    protected void setZero() {
        int[] olddim = null;
        assert (olddim = dimensions()) != null;
        this.coefficients = (Tensor)coefficients.zero();
        this.degree = java.lang.Integer.MIN_VALUE;
        assert Utility.equalsAll(dimensions(), olddim) : "dimensions don't change by setting to zero " + MathUtilities.format(dimensions()) + " was " + MathUtilities.format(olddim);
    }
}
