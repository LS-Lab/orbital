/**
 * @(#)Real.java 1.0 1999/08/16 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Predicate;

/**
 * Representation of a real number <span class="Formula">a&isin;<b>R</b></span>.
 * <p>
 * Of course, the machine prevents us from handling real numbers at all.
 * This class provides rational decimal numbers up to machine precision, instead.
 * In fact, the class {@link Real} is limited to decimals and does not even feel that any
 * irrational numbers could possibly exist as well.
 * But for we are patient humans, we wisely nod and satisfy ourselfes with decimal numbers.</p>
 * 
 * @invariant (this is Comparable &and; <span class="consistent">&#9671;</span>abnormal(&not;Comparable)) &or; <span class="provable">&#9633;</span>abnormal(Comparable)
 * @version 1.0, 1999/08/16
 * @author  Andr&eacute; Platzer
 * @see Values#valueOf(double)
 * @see Values#valueOf(float)
 * @see Values#valueOf(java.math.BigDecimal)
 * @todo introduce Real.Big storage implementation class, Integer.Big.
 *  Then we could introduce inner classes .Auto as well, who will will always use the adequate storage which increases (or perhaps even :-( decreases) automatically.
 * @todo document them methods
 */
public interface Real extends Complex, Comparable {
    /**
     * Compares this object with the specified object for order.
     * @pre this and o have comparable types
     * @return a negative integer, zero, or a positive integer as this object is
     *  less than, equal to, or greater than the specified object.
     * @post &forall;e1,e2&isin;class (e1.compareTo((Object)e2)==0) &hArr; (e1.equals((Object)e2), i.e. compareTo is consistent with equals.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this Object.
     * @see Comparable#compareTo(Object)
     */
    int compareTo(Object o);

    /**
     * Returns the value of the specified real as a <code>float</code>. This may involve rounding.
     * Similar to the double-to-float <i>narrowing primitive conversion</i> as defined in
     * <i>The Java Language Specification</i>:
     * if this real has too great a magnitude to represent as a <code>float</code>,
     * it will be converted to <tt>Float.NEGATIVE_INFINITY</tt> or <tt>Float.POSITIVE_INFINITY</tt> as appropriate.
     * @return the numeric value represented by this object after conversion to type <code>float</code>.
     */
    float floatValue();

    /**
     * Returns the value of the specified real as a <code>double</code>. This may involve rounding.
     * Similar to the double-to-float <i>narrowing primitive conversion</i> as defined in
     * <i>The Java Language Specification</i>:
     * if this real has too great a magnitude to represent as a <code>double</code>,
     * it will be converted to <tt>Double.NEGATIVE_INFINITY</tt> or <tt>Double.POSITIVE_INFINITY</tt> as appropriate.
     * @return the numeric value represented by this object after conversion to type <code>double</code>.
     */
    double doubleValue();

    Real add(Real b);
    Real subtract(Real b);
    Real multiply(Real b);
    Real divide(Real b);
    Real power(Real b);

    /**
     * Checks whether the given number is in the set of reals.
     * return whether v is real (or rational or an integer).
     */
    public static final Predicate/*<Object>*/ isa = new Predicate/*<Object>*/() {
	    public boolean apply(Object v) {
		return v instanceof Real;
	    }
	};

    /**
     * Checks whether the given number is in the set of reals and not a subset.
     * <p>
     * To be precise, for all numbers with machine precision can only be rational.
     * Nevertheless, we model the difference between (machine precision) reals
     * and explicit fractional numbers as {@link Rational}s with numerator and denominator.</p>
     * return whether v&isin;<b>R</b>\<b>Q</b> is real, but not rational and thus
     *  irrational (for machine dimensions).
     */
    public static final Predicate/*<Object>*/ hasType = new Predicate/*<Object>*/() {
	    public boolean apply(Object v) {
		return isa.apply(v) && !Rational.isa.apply(v);
	    }
	};
}
