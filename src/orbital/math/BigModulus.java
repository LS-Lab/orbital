/*
 * @(#)BigModulus.java 0.9 1998/11/27 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.math.BigInteger;

/**
 * Encapsulates a BigModulus class &ntilde;=[n]&isin;<b>Z</b>/m<b>Z</b> (mod m).
 * The values are permanetly transformed with <code>z %= m</code>.
 * The values are BigIntegers, while the modulus is never changed, unless setModulus() is called explicitly.
 * BigModulus numbers are a true commutative ring, they are a field if and only if m is prime.
 * <p>
 * <h3>Calicula Modulus <code>mod m</code> in <b>Z</b>/m<b>Z</b></h3>
 * <ul>
 * <li><code>v in <b>Z</b>/m<b>Z</b> = [0,m)</code></li>
 * <li>add: <code>a + b =<sub>m</sub> c</code></li>
 * <li>multiply: <code>a * b =<sub>m</sub> c</code></li>
 * <li>power: <code>a<sup>b</sup> =<sub>m</sub> c</code></li>
 * <li>inverse: <code>a<sup>-1</sup> =<sub>m</sub> c</code> is defined if and only if m is prime
 *    <span class="@todo">or a and m are relative prime, i.e. gcd(a,m)=1</span>.</li>
 * </ul>Derived Calculations<ul>
 * <li>subtract: <code>a - b =<sub>m</sub> c</code></li>
 * <li>divide: <code>a / b =<sub>m</sub> c</code> is defined if and only if m is prime.</li>
 * </ul></p>
 * 
 * <p>Note that operations with different modulus values are semantically undefined.</p>
 * <p>
 * <i><b>Note:</b> This class may be made private. Then it will only be accessible by
 * a factory method in {@link Values}.</i>
 * Essentially BigModulus is only a combination
 * of those concepts accessible from {@link Values}, anyway.
 * <pre>
 * <span class="comment">// old form of</span>
 * BigModulus aclass = <span class="keyword">new</span> BigInteger(a, m);
 * <span class="comment">// corresponds to new form</span>
 * <span class="Orbital">Quotient</span> aclass = <span class="Orbital">Values</span>.quotient(<span class="Orbital">Values</span>.valueOf(a), <span class="Orbital">Values</span>.valueOf(m));
 * <span class="comment">// which (with Java Generics) equals the form</span>
 * <span class="Orbital">Quotient</span>&lt;<span class="Orbital">Integer</span>&gt; aclass = <span class="Orbital">Values</span>.quotient(<span class="Orbital">Values</span>.valueOf(a), <span class="Orbital">Values</span>.valueOf(m));
 * </pre>
 * </p>
 * 
 * @version 0.9, 27/11/98
 * @author  Andr&eacute; Platzer
 * @see java.math.BigInteger
 * @deprecated Since Orbital1.1 use quotient instead.
 */
public class BigModulus extends AbstractScalar implements Quotient {

    /**
     * The modulus value that this BigModulus is calculated in.
     * This value is constant and never changed, except for explicit calls to <tt>setModulus()</tt>.
     * All operations on a BigModulus (add, subtract, pow,...) will return a BigModulus with its modulus unchanged.
     * @serial
     * @see #setModulus(java.math.BigInteger)
     */
    protected BigInteger modulus;

    /**
     * The underlying BigInteger value. This value will only range from
     * 0 to m-1.
     * @serial
     */
    protected BigInteger value;

    public BigModulus(BigInteger val, BigInteger mod) {
	this.modulus = mod;
	this.value = val == null ? null : val.mod(modulus);
    }

    // order
    public int compareTo(Object o) {
	BigModulus b = (BigModulus) o;
	if (!modulus.equals(b.modulus))	 // @todo is this necessary?
	    throw new ClassCastException("different modulus");
	return value.compareTo(b.value);
    } 

    /**
     * Returns true if x is a BigModulus whose value is equal to this number
     * and whose modulus is equals to this modulus.
     */
    public boolean equals(Object x) {
	if (!(x instanceof BigModulus))
	    return false;
	BigModulus arg = (BigModulus) x;
	return value.equals(arg.value) && modulus.equals(arg.modulus);
    } 

    public int hashCode() {
	return value.hashCode() ^ modulus.hashCode();
    } 

    public Real norm() {
	return Values.valueOf(value.abs());
    } 

    // get/set Methods.

    /**
     * Get the modulus value of this BigModulus.
     */
    public BigInteger getModulus() {
	return modulus;
    } 

    /**
     * Set the modulus value of this BigModulus.
     * This method is the only one that changes the modulus value. No other does.
     * Additionally, BigModulus-objects returned by all other methods in this class
     * will return an unchanged modulus value.
     * @see #modulus
     */
    protected void setModulus(BigInteger mod) {
	modulus = mod;
	value = value == null ? null : value.mod(modulus);
    } 

    /**
     * Get the value of this BigModulus.
     */
    public BigInteger getValue() {
	return value;
    } 

    public Arithmetic representative() {
	return Values.valueOf(getValue());
    }

    public orbital.logic.functor.Function getQuotientOperator() {
	return new orbital.logic.functor.Function() {
		public Object apply(Object val) {
		    return ((BigInteger)val).mod(modulus);
		}
	    };
    }

    /**
     * Returns a BigModulus whose value is (this + val).
     */
    public BigModulus add(BigModulus val) throws ArithmeticException {
	if (!modulus.equals(val.modulus))
	    throw new ArithmeticException("different modulus");
	return new BigModulus(value.add(val.value), modulus);
    } 

    /**
     * Returns a BigModulus whose value is (this - val).
     */
    public BigModulus subtract(BigModulus val) {
	if (!modulus.equals(val.modulus))
	    throw new ArithmeticException("different modulus");
	return new BigModulus(value.subtract(val.value), modulus);
    } 

    /**
     * Returns a BigModulus whose value is (this * val).
     */
    public BigModulus multiply(BigModulus val) {
	if (!modulus.equals(val.modulus))
	    throw new ArithmeticException("different modulus");
	return new BigModulus(value.multiply(val.value), modulus);
    } 

    /**
     * Returns a BigModulus whose value is (this / val).  Throws an
     * ArithmeticException if val == 0.
     */
    public BigModulus divide(BigModulus val) throws ArithmeticException {
	if (!modulus.equals(val.modulus))
	    throw new ArithmeticException("different modulus");
	return new BigModulus(value.divide(val.value), modulus);
    } 

    /**
     * Returns a BigModulus whose value is (this ** exponent) mod m.  (If
     * exponent == 1, the returned value is (this mod m).  If exponent < 0,
     * the returned value is the modular multiplicative inverse of
     * (this ** -exponent).)  Throws an ArithmeticException if m <= 0.
     */
    public BigModulus power(BigInteger exponent) throws ArithmeticException {
	return new BigModulus(value.modPow(exponent, modulus), modulus);
    } 

    /**
     * Returns modular multiplicative inverse of this, mod m.  Throws an
     * ArithmeticException if m <= 0 or this has no multiplicative inverse
     * mod m (i.e., gcd(this, m) != 1).
     */
    public Arithmetic inverse() throws ArithmeticException {
	return new BigModulus(value.modInverse(modulus), modulus);
    } 

    // Arithmetic implementation

    public Arithmetic zero() {return new BigModulus(BigInteger.valueOf(0), modulus);}
    public Arithmetic one() {return new BigModulus(BigInteger.valueOf(1), modulus);}

    // TODO:  allow Scalars as b, as well meaning [a]+b = [a+b] ?
    public Quotient add(Quotient b) {
	return add((BigModulus) b);
    } 
    public Quotient subtract(Quotient b) {
	return subtract((BigModulus) b);
    } 
    public Quotient multiply(Quotient b) {
	return multiply((BigModulus) b);
    } 
    public Quotient divide(Quotient b) {
	return divide((BigModulus) b);
    } 
    public Quotient power(Quotient b) {
	return power((BigModulus) b);
    } 

    public Arithmetic add(Arithmetic b) {
	return add((BigModulus) b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	return subtract((BigModulus) b);
    } 
    public Arithmetic minus() {
	return new BigModulus(value.negate(), modulus);
    } 
    public Arithmetic multiply(Arithmetic b) {
	return multiply((BigModulus) b);
    } 
    public Arithmetic divide(Arithmetic b) {
	return divide((BigModulus) b);
    } 
    public Arithmetic power(Arithmetic b) {
	return power((BigModulus) b);
    } 


    /**
     * Converts this number to an int.  Standard narrowing primitive conversion
     * as per The Java Language Specification.
     */
    public int intValue() {
	return value.intValue();
    } 

    /**
     * Converts this number to a long.  Standard narrowing primitive conversion
     * as per The Java Language Specification.
     */
    public long longValue() {
	return value.longValue();
    } 

    /**
     * Converts this number to a float.  Similar to the double-to-float
     * narrowing primitive conversion defined in The Java Language
     * Specification: if the number has too great a magnitude to represent
     * as a float, it will be converted to infinity or negative infinity,
     * as appropriate.
     */
    public float floatValue() {
	return value.floatValue();
    } 

    /**
     * Converts the number to a double.  Similar to the double-to-float
     * narrowing primitive conversion defined in The Java Language
     * Specification: if the number has too great a magnitude to represent
     * as a double, it will be converted to infinity or negative infinity,
     * as appropriate.
     */
    public double doubleValue() {
	return value.doubleValue();
    } 

    /**
     * Returns the string representation of this number in the given radix.
     * If the radix is outside the range from Character.MIN_RADIX(2) to
     * Character.MAX_RADIX(36) inclusive, it will default to 10 (as is the
     * case for Integer.toString).  The digit-to-character mapping provided
     * by Character.forDigit is used, and a minus sign is prepended if
     * appropriate.  (This representation is compatible with the (String, int)
     * constructor.)
     */
    public String toString(int radix) {
	return value.toString(radix);
    } 

    /**
     * Returns the string representation of this number, radix 10.  The
     * digit-to-character mapping provided by Character.forDigit is used,
     * and a minus sign is prepended if appropriate.  (This representation
     * is compatible with the (String) constructor, and allows for string
     * concatenation with Java's + operator.)
     */
    public String toString() {
	return value.toString();
    } 

    /**
     * Returns the number of bits in the minimal two's-complement
     * representation of this number, *excluding* a sign bit, i.e.,
     * (ceil(log2(this < 0 ? -this : this + 1))).  (For positive
     * numbers, this is equivalent to the number of bits in the
     * ordinary binary representation.)
     */
    public int bitLength() {
	return value.bitLength();
    } 

    /**
     * Returns the two's-complement representation of this number.  The array
     * is big-endian (i.e., the most significant byte is in the [0] position).
     * The array contains the minimum number of bytes required to represent
     * the number (ceil((this.bitLength() + 1)/8)).  (This representation is
     * compatible with the (byte[]) constructor.)
     */
    public byte[] toByteArray() {
	return value.toByteArray();
    } 
}
