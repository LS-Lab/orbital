/**
 * @(#)Arithmetic.java 1.0 1999/08/16 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

/**
 * An abstract arithmetic implementation for groups and fields that defines subtraction
 * in terms of the minus and division in terms of the inverse.
 * <p>
 * This class is useful as a base class for Arithmetic objects, but
 * the performance may be lower than a direct implementation of all methods.
 * </p>
 * @version 1.0, 1999/08/16
 * @author  Andr&eacute; Platzer
 */
abstract class AbstractArithmetic implements Arithmetic {
    public boolean equals(Object o, Real tolerance) {
	return Metric.INDUCED.distance(this, (Arithmetic)o).compareTo(tolerance) < 0;
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
	    s = (Integer) Values.narrow((Scalar) b);
	}
	catch (ClassCastException e) {
	    throw new UnsupportedOperationException("default power only implemented for scalar integer numbers");
	}
	Arithmetic r = this;
	int	   n = Math.abs(s.intValue());
	for (int i = 0; i < n; i++)
	    r = r.multiply(this);
	if (s.intValue() < 0)
	    r = r.inverse();
	return r;
    } 
}
