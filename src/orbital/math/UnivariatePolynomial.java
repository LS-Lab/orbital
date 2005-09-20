/**
 * @(#)UnivariatePolynomial.java 1.1 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.math.functional.Function;

import java.util.ListIterator;

/**
 * (Univariate) polynomial p&isin;R[X].
 * <p>
 * Let R be a commutative ring with 1.
 * The polynomial ring over R in one variable X is
 * <center>R[X] := {&sum;<sub>i&isin;<b>N</b></sub> a<sub>i</sub>X<sup>i</sup> = (a<sub>i</sub>)<sub>i&isin;<b>N</b></sub> &brvbar; a<sub>i</sub>=0 p.t. i&isin;<b>N</b> &and; &forall;i&isin;<b>N</b> a<sub>i</sub>&isin;R}</center>
 * with the convolution as multiplication.
 * It is an associative, graded R-algebra, and as commutative or unital as R.
 * R[X] inherits the properties of being an integrity domain, factorial (a unique factorization domain), Noetherian from R.
 * Additionally, if R is an integrity domain, then R[X]<sup>&times;</sup> = R<sup>&times;</sup>.
 * </p>
 * <p>
 * The polynomial ring over a field in <em>one</em> variable even is Euclidean.
 * </p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Polynomial
 * @see Values#polynomial(Arithmetic[])
 * @see Values#polynomial(Object)
 * @see Values#asPolynomial(Vector)
 * @see NumericalAlgorithms#polynomialInterpolation(Matrix)
 * @todo implements Function<T,T> instead with T any "compatible" type (see Algebra I) and evaluation of Horner-Scheme
 * @todo rename is there a better name for this class?
 */
public interface UnivariatePolynomial/*<R extends Arithmetic>*/ extends Euclidean, Polynomial/*<R,Integer>*/, Function/*_<R,R>_*/ {
    // Get/Set properties
    /**
     * Get the degree of this polynomial.
     * <p>
     * This is the Euclidean degree function &delta;
     * and also the graduation function for polynomials.
     * 0 is an element of undefined or all or none degrees.
     * So for 0 we should return <code>null</code>
     * (or {@link java.lang.Integer#MIN_VALUE}, but this is not recommended).
     * </p>
     * @return deg(this) = max {i&isin;<b>N</b> &brvbar; a<sub>i</sub>&ne;0}
     */
    Integer degree();
        
    /**
     * Get the coefficient of X<sup>i</sup>.
     * Convenience method for {@link Polynomial#get(Arithmetic)}.
     * @return a<sub>i</sub> if i&le;deg(this), or <code>0</code> if i&gt;deg(this).
     */
    Arithmetic/*>R<*/ get(int i);

    // iterator views

    /**
     * Returns an iterator over all coefficients (up to degree).
     * <!-- @xxx how about ListIterator#add(Object) instead
     * The resulting iterator will always allow {@link ListIterator#next()} without throwing
     * a {@link java.util.NoSuchElementException} in order to allow setting a polynomial's
     * coefficiens even beyond the current degree.
     * However {@link ListIterator#hasNext()} will nevertheless return <code>false</code> after passing
     * the leading coefficient determining the degree.
     * -->
     * @postconditions always (RES.succeedes(#next()))
     */
    ListIterator iterator();
        
    // function

    /**
     * Evaluate this polynomial at <var>a</var>.
     * Using the <a href="#Einsetzungshomomorphismus" xml:lang="de">"Einsetzungshomomorphismus"</a>.
     * @return f(a) = f(X)|<sub>X=a</sub> = (f(X) mod (X-a))
     * @todo we could just as well generalize the argument and return type of R
     */
    Object/*>R<*/ apply(Object/*>R<*/ a);

    // Arithmetic
    
    UnivariatePolynomial/*<R>*/ add(UnivariatePolynomial/*<R>*/ b);
    UnivariatePolynomial/*<R>*/ subtract(UnivariatePolynomial/*<R>*/ b);
    UnivariatePolynomial/*<R>*/ multiply(UnivariatePolynomial/*<R>*/ b);

    UnivariatePolynomial/*<R>*/ quotient(UnivariatePolynomial/*<R>*/ g);
    UnivariatePolynomial/*<R>*/ modulo(UnivariatePolynomial/*<R>*/ g);

    /**
     * Returns an array containing all the coefficients of this polynomial.
     * @return a new array containing all our coefficients.
     * @postconditions RES[i]==get(i) &and; RES.length==degree()+1 &and; RES!=RES
     * @see Object#clone()
     */
    public Arithmetic/*>R<*/[] getCoefficients();
}
