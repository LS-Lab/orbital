/**
 * @(#)Complex.java 1.0 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Predicate;

/**
 * Representation of a complex number <span class="Formula">a + <b>i</b>*b&isin;<b>C</b></span>.
 * <p>
 * Complex numbers are a field that is a superset of <b>R</b>&sub;<b>C</b>.
 * They are isomorph to a special polynomial ring,
 * <span class="Formula"><b>C</b> &cong; <b>R</b>[<b>i</b>] / (<b>i</b><sup>2</sup> = -1) = <b>R</b>[X] / (X<sup>2</sup>+1).</span>
 * </p>
 * <p>
 * <span style="float: left; font-size: 200%">&#9761;</span>
 * Since complex numbers are a field, they are also an {@link Euclidean Euclidean ring}.
 * However, because fields are (in a way) &quot;degenerate&quot;
 * Euclidean rings, they do not extend the {@link Euclidean} interface.
 * The Euclidean quotient and remainder operations would not perform
 * very interesting calculations, and the Euclidean quotient would
 * fully coincide with ordinary division in a field.
 * </p>
 * <p>
 * <span style="float: left; font-size: 200%">&#9761;</span>
 * Complex numbers are not like real numbers. They are not ordered.
 * </p>
 * 
 * @invariant (&not;super &and; &not;(this is Comparable)) &or; <span class="provable">&#9633;</span>abnormal(&not; Comparable)
 * @version 1.0, 1996/02/03
 * @author  Andr&eacute; Platzer
 * @see Values#complex(Real, Real)
 * @see Values#complex(double, double)
 * @see Values#cartesian(Real, Real)
 * @see Values#cartesian(double, double)
 * @see Values#polar(Real, Real)
 * @see Values#polar(double, double)
 */
public interface Complex extends Scalar {
    /**
     * Compares two complex numbers for equality.
     * <p>
     * Two complex numbers z, z' &isin; <b>C</b> are equal iff
     * re z = re z' &and; im z = im z'.
     * This is a component-wise equality.
     * </p>
     * @pre true
     * @post RES &hArr; Complex.isa(o) &and; re() = o.re() &and; im() = o.im()
     */
    boolean equals(Object o);

    /**
     * Returns a hash code value for the object.
     * @pre true
     * @post RES == re().hashCode() ^ im().hashCode()
     */
    int hashCode();
	
    /**
     * Get the real component.
     * @return &real;(z) = re z = re (a + <b>i</b>b) = a = (z+<span class="conjugate">z</span>) / 2
     * @see #im()
     */
    Real re();

    /**
     * Get the imaginar component.
     * @return &image;(z) = im z = im (a + <b>i</b>b) = b = (z-<span class="conjugate">z</span>) / (2<b>i</b>)
     * @see #re()
     */
    Real im();

    /**
     * Returns the absolute |z|.
     * <p>
     * It is
     * |z|<sup>2</sup> = z*<span class="conjugate">z</span> = re(z)<sup>2</sup>+im(z)<sup>2</sup>.
     * and |x*y| = |x|*|y|, |z<sup>-1</sup>| = |z|<sup>-1</sup>.</p>
     * <p>
     * Also called length, magnitude or modulus.</p>
     * @return the absolute |z| = &radic;<span style="text-decoration: overline">re(z)<sup>2</sup>+im(z)<sup>2</sup></span>.
     * @todo couldn't we change return type to Arithmetic or even Real here?
     * @see #arg()
     */
    Real norm();

    /**
     * Returns the principal angle (argument) component of a polar complex.
     * But adding 2k&pi; to the principle angle will be an angle as well.
     * @return the angle &phi; of r*<b>e</b><sup><b>i</b>&phi;</sup>.
     *  The angle &ang; &phi; in radians is measured counter-clockwise from the real axis.
     *  Value will be in range [-&pi;,&pi;] and is not yet in the correct sector!
     * @see #norm()
     */
    Real arg();

    /**
     * Returns the complex conjugated <span class="conjugate">z</span> = z<sup>*</sup> = z'.
     * <p>
     * Conjugation is an involutorical field-automorphism that is identical on <b>R</b>.</p>
     * @return the complex number a - <b>i</b>*b.
     */
    Complex conjugate();

    /**
     * Whether this complex number is infinite.
     * @return whether the real or imaginary part is infinite.
     * @see java.lang.Double#isInfinite(double)
     */
    boolean isInfinite();

    /**
     * Whether this complex number is NaN.
     * @return whether the real or imaginary part is NaN.
     * @see java.lang.Double#isNaN(double)
     */
    boolean isNaN();
    
    // arithmetic operations
	
    /**
     * adds two Complexes returning a third as a result
     */
    Complex add(Complex b);

    /**
     * subtracts two Complexes returning a third as a result
     */
    Complex subtract(Complex b);

    /**
     * multiplies two Complexes returning a third as a result
     */
    Complex multiply(Complex b);

    /**
     * divides two complex numbers.
     */
    Complex divide(Complex b);

    /**
     * power of complex numbers.
     */
    Complex power(Complex x);


    /**
     * Checks whether the given number is in the set of complex numbers.
     * return whether v is complex, real, rational or an integer.
     */
    public static final Predicate/*<Object>*/ isa = new Predicate/*<Object>*/() {
	    public boolean apply(Object v) {
		return v instanceof Complex;
	    }
	};

    /**
     * Checks whether the given number is in the set of complexes and not a subset.
     * return whether v&isin;<b>C</b>\<b>R</b> is complex, but not real.
     */
    public static final Predicate/*<Object>*/ hasType = new Predicate/*<Object>*/() {
	    public boolean apply(Object v) {
		return isa.apply(v) && !Real.isa.apply(v);
	    }
	};
}
