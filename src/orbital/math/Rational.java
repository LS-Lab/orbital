/**
 * @(#)Rational.java 1.0 2000/08/03 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Predicate;

/**
 * Representation of a rational number <span class="Formula">p&#8260;q &isin; <b>Q</b></span>.
 * <p>
 * <b>Q</b> := Quot(<b>Z</b>) := (<b>Z</b>&#8726;{0})<sup>-1</sup><b>Z</b> = {p&#8260;q &brvbar; p,q&isin;<b>Z</b> &and; q&ne;0}
 * is the field of fractions of the ring <b>Z</b>.
 * p is the numerator and q the denominator component.
 * </p>
 * <p>
 * A rational number p&#8260;q &isin; <b>Q</b> is usually written as
 * <pre>
 *  p
 *  <span style="text-decoration: overline">q</span>
 * </pre>
 * </p>
 * <p>
 * Rational numbers are a subfield of <b>R</b>, and the smallest subfield
 * of all fields of characteristic 0.
 * </p>
 * <p>
 * <a id="theory"></a>
 * For rational numbers we have a congruence relation &cong; defined by
 * <div>p&#8260;q &cong; a&#8260;b :&hArr; &exist;t&isin;<b>Z</b>\{0} t*(p*p-a*q)=0 &hArr; p*b =a*q</div>
 * And to be precise, with a rational number 3&#8260;4 we identify the whole equivalence class
 * [3&#8260;4] = {p&#8260;q &brvbar; p&#8260;q &cong; 3&#8260;4}
 * of which 3&#8260;4 is the canonical representative
 * since its numerator and denominator are cancelled and its denominator is strictly positive.
 * So after all, rational numbers are the quotient Quot(<b>Z</b>) := <b>Z</b>&times(<b>Z</b>\{0}) / &cong;.
 * </p>
 * 
 * @version 1.0, 2000/08/03
 * @author  Andr&eacute; Platzer
 * @see Values#rational(Integer, Integer)
 * @see Values#rational(int, int)
 * @see Values#rational(Integer)
 * @see Values#rational(int)
 * @see "N. Bourbaki, Algebra I.9.4: The field of rational numbers."
 * @see "N. Bourbaki, Algebra I.2.4: Monoid of fractions of a commutative monoid."
 */
public interface Rational extends Real {
    /**
     * Returns the numerator component.
     * @return p of this rational number p&#8260;q.
     * @todo should we generalize the type of numerators and denominators from int to, perhaps, Arithmetic?
     */
    Integer numerator(); 

    /**
     * Returns the denominator component.
     * @return q of this rational number p&#8260;q.
     */
    Integer denominator();

    /**
     * Get the "canonical" representative (cancelled and normalized) of the equivalence class of rationals equal to this.
     * @return a cancelled and normalized rational.
     * @post RES.equals(this) && MathUtilities.gcd(RES.numerator(), RES.denominator()) == 1 && RES.denominator() > 0.
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
     * adds two Rationals returning a third as a result
     */
    Rational add(Rational b);
    /**
     * subtracts two Rationals returning a third as a result
     */
    Rational subtract(Rational b);
    /**
     * multiplies two Rationals returning a third as a result
     */
    Rational multiply(Rational b);
    /**
     * divides two Rationals returning a third as a result
     */
    Rational divide(Rational b);
    //@todo reintroduce once covariant return-types are allowed for Integer. public abstract Rational power(Integer b);
    /*Rational power_(Integer b) {
      return (Rational) Operations.power.apply(this, b);
      }*/


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
