/**
 * @(#)Integer.java 1.0 2000/08/03 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Predicate;

/**
 * Representation of an integer number <span class="Formula">k&isin;<b>Z</b></span>.
 * <p>
 * Integer numbers are a sub group of <b>Q</b> for '+', it is embedded in <b>Q</b> for the field operation '*'.</p>
 * 
 * @version 1.0, 2000/08/03
 * @author  Andr&eacute; Platzer
 * @see Values#valueOf(int)
 * @see Values#valueOf(long)
 * @see Values#valueOf(short)
 * @see Values#valueOf(byte)
 * @see Values#valueOf(java.math.BigInteger)
 */
public interface Integer extends Rational, Euclidean {
    /**
     * Returns the value of the specified integer as a <code>byte</code>. This may involve rounding or truncation.
     * Similar to the standard <i>narrowing primitive conversion</i> as defined in
     * <i>The Java Language Specification</i>:
     * if this integer is too big to fit in an int, only the low-order bits are returned.
     * @return the numeric value represented by this object after conversion to type <code>byte</code>.
     */
    byte byteValue();

    /**
     * Returns the value of the specified integer as a <code>short</code>. This may involve rounding or truncation.
     * Similar to the standard <i>narrowing primitive conversion</i> as defined in
     * <i>The Java Language Specification</i>:
     * if this integer is too big to fit in an int, only the low-order bits are returned.
     * @return the numeric value represented by this object after conversion to type <code>short</code>.
     */
    short shortValue();

    /**
     * Returns the value of the specified integer as a <code>int</code>. This may involve rounding or truncation.
     * Similar to the standard <i>narrowing primitive conversion</i> as defined in
     * <i>The Java Language Specification</i>:
     * if this integer is too big to fit in an int, only the low-order bits are returned.
     * @return the numeric value represented by this object after conversion to type <code>int</code>.
     */
    int intValue();

    /**
     * Returns the value of the specified integer as a <code>long</code>. This may involve rounding or truncation.
     * Similar to the standard <i>narrowing primitive conversion</i> as defined in
     * <i>The Java Language Specification</i>:
     * if this integer is too big to fit in an int, only the low-order bits are returned.
     * @return the numeric value represented by this object after conversion to type <code>long</code>.
     */
    long longValue();

    // Arithmetic implementation synonyms
    Integer add(Integer b);
    Integer subtract(Integer b);
    Integer multiply(Integer b);
    Integer power(Integer b);


    /**
     * Checks whether the given number is in the set of integers.
     * return whether v is an integer.
     */
    public static final Predicate/*<Object>*/ isa = new Predicate/*<Object>*/() {
	    public boolean apply(Object v) {
		return v instanceof Integer;
	    }
	};

    /**
     * Checks whether the given number is in the set of integers and not a subset.
     * return whether v&isin;<b>Z</b> is an integer.
     */
    public static final Predicate/*<Object>*/ hasType = new Predicate/*<Object>*/() {
	    public boolean apply(Object v) {
		return isa.apply(v);
	    }
	};
}
