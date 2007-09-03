/**
 * @(#)Arithmetic.java 1.0 1999/08/16 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

/**
 * An abstract arithmetic implementation for groups and fields that defines subtraction
 * in terms of the minus and division in terms of the inverse.
 * <p>
 * This class is useful as a base class for Arithmetic objects, but
 * the performance may be lower than a direct implementation of all methods.
 * </p>
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
abstract class AbstractArithmetic implements Arithmetic {
    public boolean equals(Object o, Real tolerance) {
        return Metric.INDUCED.distance(this, (Arithmetic)o).compareTo(tolerance) <= 0;
    }

    public boolean isZero() {
    	return equals(zero());
    }
    public boolean isOne() {
    	return equals(one());
    }


    /**
     * Subtracts an arithmetic object from this returning the result.
     * @return this+(&minus;b).
     */
    public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
        return add(b.minus());
    } 

    /**
     * Divides this by an arithmetic object returning the result.
     * @return this&sdot;(b<sup>&minus;1</sup>).
     * @todo which order in case of non-commutative?
     */
    public Arithmetic divide(Arithmetic b) throws ArithmeticException, UnsupportedOperationException {
        return multiply(b.inverse());
    } 

    /**
     * Returns the power of an arithmetic object to this base (natural power).
     * <p>
     * This method is only implemented for scalar integer numbers.
     * Overwrite to provide additional behaviour.
     * </p>
     * @throws UnsupportedOperationException if b is not a scalar integer.
     *  Since other implementations are not well-defined in the general case without
     *  using the {@link orbital.math.functional.Functions#exp exponential function}.
     * @todo improve performance by "Square and Power"
     */
    public Arithmetic power(Arithmetic b) throws ArithmeticException, UnsupportedOperationException {
        if (!(b instanceof Scalar))
            throw new UnsupportedOperationException("default power only implemented for scalar numbers");
        Integer s;
        try {
            assert b instanceof Scalar;
            s = (Integer) Values.getDefaultInstance().narrow((Scalar) b);
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("default power only implemented for scalar integer numbers");
        }
	if (s.intValue() == 0) {
	    return one();
	} else {
	    Arithmetic r = this;
	    int        n = Math.abs(s.intValue());
	    for (int i = 1; i < n; i++)
		r = r.multiply(this);
	    if (s.intValue() < 0)
		r = r.inverse();
	    return r;
	}
    } 
}
