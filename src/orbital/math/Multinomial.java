/**
 * @(#)Multinomial.java 1.1 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.math.functional.Function;

import java.util.ListIterator;

/**
 * (Multivariate) polynomial p&isin;R<span>[X<sub>0</sub>,...,X<sub>n-1</sub>]</span>.
 * <p>
 * Of course, the multivariate polynomials are the {@link Polynomial polynomials} over polynomial rings:
 * R[X<sub>0</sub>,...,X<sub>n-1</sub>] = R[X<sub>0</sub>][X<sub>1</sub>]...[X<sub>n-1</sub>].
 * The importance of polynomial rings comes from the fact that they are the
 * free commutative and associative R-algebras
 * Libasc<sub>R</sub>(I) := R<sup>(N<sup>(I)</sup>)</sup> = R[(X<sub>i</sub>)<sub>i?I</sub>].
 * Although, of course, this is restricted to a finite number of variables for computation.
 * <!-- @todo could we calculate only infinitely generated algebras as well? -->
 * </p>
 *
 * @version 1.1, 2001/12/09
 * @author  Andr&eacute; Platzer
 * @see Polynomial
 * @see Values#multinomial(Object)
 * @todo let Polynomial extend Multinomial just like Vector extending Tensor?
 * @todo implements Function<T,T> instead with T any "compatible" type (see Algebra I) and evaluation of Horner-Scheme
 */
public interface Multinomial/*<R implements Arithmetic>*/ extends Arithmetic, Function/*<Vector<R>,R>*/ {
    // Get/Set properties
    /**
     * Get the number of variables ("rank") of our polynomial ring.
     * @return n for a polynomial p&isin;R[X<sub>0</sub>,...,X<sub>n-1</sub>].
     * @todo rename
     */
    int numberOfVariables();

    /**
     * Get the (total) degree of this polynomial.
     * @return deg(this) = max {|i|:=&sum;<sub>j=0,...,n-1</sub> i<sub>j</sub> &brvbar; i&isin;<b>N</b><sup>n</sup> &and; a<sub>i</sub>&ne;0}
     */
    Integer degree();
    /**
     * Get the (int value of) the (total) degree of this polynomial.
     * @see #degreeValue()
     */
    int degreeValue();
    /**
     * Get the the (partial) degree of this polynomial with respect to the single variables.
     * @see #degree()
     * @todo rename?
     */
    int[] dimensions();
	
    /**
     * Get the i-th coefficient.
     * The i-th coefficient is the coefficient of the monomial
     * X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>.
     * @return a<sub>i</sub> if |i|&le;deg(this), or <code>0</code> if |i|&gt;deg(this).
     */
    Arithmetic/*>R<*/ get(int[] i);

    // iterator views

    /**
     * Returns an iterator over all coefficients (up to degree).
     */
    ListIterator iterator();

    // function
    
    /**
     * Evaluate this polynomial at <var>a</var>.
     * Using the <span xml:lang="de">"Einsetzungshomomorphismus"</span>.
     * @param a the argument a&isin;R<sup>n</sup> as a {@link Vector Vector<R>} of dimension n.
     * @return f(a) = f((X<sub>k</sub>)<sub>k</sub>)|<sub>(X<sub>k</sub>)<sub>k</sub>=a</sub> = f(X<sub>0</sub>,...,X<sub>n-1</sub>)|<sub>X<sub>0</sub>=a<sub>0</sub>,X<sub>1</sub>=a<sub>1</sub>,...,X<sub>n-1</sub>=a<sub>n-1</sub></sub>.
     * @todo we could just as well generalize the argument and return type of R
     */
    Object/*>R<*/ apply(Object/*>R<*/ a);

    // Arithmetic
    
    Multinomial/*<R>*/ add(Multinomial/*<R>*/ b);
    Multinomial/*<R>*/ subtract(Multinomial/*<R>*/ b);
    Multinomial/*<R>*/ multiply(Multinomial/*<R>*/ b);
}
