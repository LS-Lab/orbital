/**
 * @(#)Fraction.java 1.1 2002/06/18 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

/**
 * Representation of a fraction <span class="Formula">a&#8260;s &isin; S<sup>-1</sup>M = M<sub>S</sub></span>.
 * <p>
 * S<sup>-1</sup>M := M<sub>S</sub> := M&times;S/~ = {a&#8260;s &brvbar; a&isin;M &and; s&isin;S}
 * with congruence relation ~ defined by
 * <div>a&#8260;s ~ b&#8260;t :&hArr; &exist;s&#697;&isin;S s&#697;&sdot;(a&sdot;t-b&sdot;s)=0
 * (&hArr; a&sdot;t-b&sdot;s = 0 if M is an integrity domain)</div>
 * is the R<sub>S</sub>-module (or ring or monoid) of <dfn>fractions</dfn> of the R-module (or commutative ring with 1 or monoid) M
 * with denominators in S&le;(R,&sdot;).
 * Where S&le;(R,&sdot;) is a submonoid of the multiplicative group of R. <!-- @todo clearify for modules and rings -->
 * S<sup>-1</sup>M is also called <dfn>localisation of M to S</dfn>.
 * </p>
 * <p>
 * Especially, for an integrity domain R, Quot(R) := (R&#8726;{0})<sup>-1</sup>R is the field
 * of fractions of R, and for R-modules M, M<sub>R&#8726;{0}</sub> is a Quot(R)-vector space.
 * </p>
 * <p>
 * There is the canonical embedding homomorphism a&#8614;a&#8260;1 which is injective if and
 * only if S does not contain zero divisors (especially 0&notin;S).
 * </p>
 * <p>
 * A fraction a&#8260;s &isin; S<sup>-1</sup>M
 * with numerator a and denominator s is usually written as
 * <pre>
 *  a
 *  <span style="text-decoration: overline">s</span>
 * </pre>
 * </p>
 * 
 * @version 1.1, 2002/06/18
 * @author  Andr&eacute; Platzer
 * @see Values#fraction(Arithmetic,Arithmetic)
 * @see "N. Bourbaki, Algebra I.2.4: Monoid of fractions of a commutative monoid."
 * @todo introduce interface Fraction<M,S> for modules (or R-algebras or monoids) of fractions with denominators in S.
 * @todo introduce (single-sided!) mixed operations Fraction<M,S>&times;M&rarr;Fraction<M,S> with the canonical injection
 */
public interface Fraction/*<M extends Arithmetic,S extends M>*/ extends Arithmetic {
    /**
     * Returns the numerator component.
     * @return p of this fraction p&#8260;q.
     */
    Arithmetic/*>M<*/ numerator(); 

    /**
     * Returns the denominator component.
     * @return q of this fraction p&#8260;q.
     */
    Arithmetic/*>S<*/ denominator();


    // Arithmetic implementation synonyms
    /**
     * Adds two fractions returning a third as a result.
     * @return a&#8260;s + b&#8260;t := (a&sdot;t+b&sdot;s)&#8260;(s&sdot;t).
     */
    Fraction/*<M,S>*/ add(Fraction/*<M,S>*/ bt);
    /**
     * Subtracts two fractions returning a third as a result.
     * @return a&#8260;s - b&#8260;t := (a&sdot;t-b&sdot;s)&#8260;(s&sdot;t).
     */
    Fraction/*<M,S>*/ subtract(Fraction/*<M,S>*/ bt);
    /**
     * Multiplies two fractions returning a third as a result.
     * @return a&#8260;s &sdot; b&#8260;t := (a&sdot;b)&#8260;(s&sdot;t).
     */
    Fraction/*<M,S>*/ multiply(Fraction/*<M,S>*/ bt);
    /**
     * Divides two fractions returning a third as a result.
     * @pre b&isin;S &or; b invertible anyway
     * @return a&#8260;s &#8725; b&#8260;t := (a&sdot;t)&#8260;(s&sdot;b).
     * @throws ArithmeticException if b&notin;S and b is not invertible.
     */
    Fraction/*<M,S>*/ divide(Fraction/*<M,S>*/ bt);
    /**
     * {@inheritDoc}
     * @post RES instanceof Fraction<M,S>
     * @todo when covariant return-types change return-type to Fraction<M,S>.
     */
    Arithmetic scale(Arithmetic alpha);
}
