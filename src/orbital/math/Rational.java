/**
 * @(#)Rational.java 1.0 2000/08/03 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Predicate;

/**
 * Representation of a rational number <span class="Formula">a&#8260;s &isin; <b>Q</b></span>.
 * <p>
 * <b>Q</b> := Quot(<b>Z</b>) := (<b>Z</b>&#8726;{0})<sup>-1</sup><b>Z</b> = {a&#8260;s &brvbar; a,s&isin;<b>Z</b> &and; s&ne;0}
 * is the field of {@link Fraction fractions} of the ring <b>Z</b>.
 * </p>
 * <p>
 * A rational number a&#8260;s &isin; <b>Q</b>
 * with numerator a and denominator s is usually written as
 * <pre>
 *  a
 *  <span style="text-decoration: overline">s</span>
 * </pre>
 * </p>
 * <p>
 * Rational numbers are a subfield of <b>R</b>, and the smallest subfield
 * of all fields of characteristic 0.
 * </p>
 * 
 * @version 1.0, 2000/08/03
 * @author  Andr&eacute; Platzer
 * @stereotype data-type
 * @structure synonym for Fraction<Integer>
 * @see Values#rational(Integer, Integer)
 * @see Values#rational(int, int)
 * @see Values#rational(Integer)
 * @see Values#rational(int)
 * @see Fraction
 * @see "N. Bourbaki, Algebra I.9.4: The field of rational numbers."
 * @see "N. Bourbaki, Algebra I.2.4: Monoid of fractions of a commutative monoid."
 * @todo extend Fraction<Integer,Integer> but what's up with the different return-types of numerator()?
 */
public interface Rational extends Real {
    /**
     * Returns the numerator component.
     * @return a of this rational number a&#8260;s.
     */
    Integer numerator(); 

    /**
     * Returns the denominator component.
     * @return s of this rational number a&#8260;s.
     */
    Integer denominator();

    /**
     * Get the "canonical" representative (cancelled out and normalized)
     * of the equivalence class of rationals equal to this.
     * @return a cancelled and normalized rational.
     * @postconditions RES.equals(this) && MathUtilities.gcd(RES.numerator(), RES.denominator()) == 1 && RES.denominator() > 0.
     * @note S' = <b>Z</b>&#8726;{0} is the set of allowed denominators.
     */
    Rational representative();


    /**
     * returns the absolute of a rational.
     * @todo think about this!
     */
    //public abstract Rational abs();

    // Arithmetic implementation synonyms
    /**
     * adds two rationals returning a third as a result
     */
    Rational add(Rational b);
    /**
     * subtracts two rationals returning a third as a result
     */
    Rational subtract(Rational b);
    /**
     * multiplies two rationals returning a third as a result
     */
    Rational multiply(Rational b);
    /**
     * divides two rationals returning a third as a result
     */
    Rational divide(Rational b);
    /**
     * Return a<sup>b</sup>.
     * @postconditions (b != 0 &rarr; RES instanceof Rational)
     */
    Rational power(Integer b);


    /**
     * Checks whether the given number is in the set of rationals.
     * return whether v is rational or an integer.
     */
    public static final Predicate/*<Object>*/ isa = new Predicate/*<Object>*/() {
	    public boolean apply(Object v) {
		return v instanceof Rational;
	    }
	};
    /**
     * Checks whether the given number is in the set of rationals and not a subset.
     * return whether v&isin;<b>Q</b> is a rational and not an integer.
     */
    public static final Predicate/*<Object>*/ hasType = new Predicate/*<Object>*/() {
	    public boolean apply(Object v) {
		return isa.apply(v) && !Integer.isa.apply(v);
	    }
	};
}
