/**
 * @(#)Polynomial.java 1.1 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.math.functional.Function;

import java.util.ListIterator;

/**
 * Univariate polynomial p&isin;R[X].
 * <p>
 * Let R be a commutative ring with 1.
 * The polynomial ring over R in one variable X is
 * <center>R[X] := {&sum;<sub>i&isin;<b>N</b></sub> a<sub>i</sub>X<sup>i</sup> = (a<sub>i</sub>)<sub>i&isin;<b>N</b></sub> &brvbar; a<sub>i</sub>=0 p.t. i&isin;<b>N</b> &and; &forall;i&isin;<b>N</b> a<sub>i</sub>&isin;R}</center>
 * It is a commutative, unital, and associative, graded R-algebra.
 * R[X] inherits the properties of being an integrity domain, factorial (a unique factorization domain), Noetherian from R.
 * Additionally, if R is an integrity domain, then R[X]<sup>&times;</sup> = R<sup>&times;</sup>.
 * </p>
 * <p>
 * The polynomial ring over a field in <em>one</em> variable even is Euclidean.
 * </p>
 *
 * @version 1.1, 2001/12/09
 * @author  Andr&eacute; Platzer
 * @see Values#polynomial(Arithmetic[])
 * @see Values#polynomial(double[])
 * @see Values#asPolynomial(Vector)
 * @see NumericalAlgorithms#polynomialInterpolation(Matrix)
 * @todo implements Function<T,T> instead with T any "compatible" type (see Algebra I) and evaluation of Horner-Scheme
 * @todo generalize to multivariat polynomials (which are no longer Euclidean). Then we would need get(int[]), R[][]...[] getCoefficients(), Iterator iterator(), and implements Function<Vector<R>,R>.
 */
public interface Polynomial/*<R implements Arithmetic>*/ extends Euclidean, Function/*<R,R>*/ {
    // Get/Set properties
    /**
     * Get the degree of this polynomial.
     * <p>
     * This degree is the Euclidean degree function &delta; for polynomials.
     * </p>
     * @return deg(this) = max {i&isin;<b>N</b> &brvbar; a<sub>i</sub>&ne;0}
     */
    Integer degree();
    /**
     * Get the (int value of) the degree of this polynomial.
     * @see #degreeValue()
     */
    int degreeValue();
	
    /**
     * Get the i-th coefficient.
     * @return a<sub>i</sub> if i&le;deg(this), or <code>0</code> if i&gt;deg(this).
     */
    Arithmetic/*>R<*/ get(int i);

    // iterator views

    /**
     * Returns an iterator over all coefficients (up to degree).
     */
    ListIterator iterator();
	
    /**
     * Evaluate this polynomial at <var>a</var>.
     * Using the <span xml:lang="de">"Einsetzungshomomorphismus"</span>.
     * @return f(a) = f(X)|<sub>X=a</sub> = (f(X) mod (X-a))
     * @todo we could just as well generalize the argument and return type of R
     */
    Object/*>R<*/ apply(Object/*>R<*/ a);

    // Arithmetic
    
    Polynomial/*<R>*/ add(Polynomial/*<R>*/ b);
    Polynomial/*<R>*/ subtract(Polynomial/*<R>*/ b);
    Polynomial/*<R>*/ multiply(Polynomial/*<R>*/ b);

    Polynomial/*<R>*/ quotient(Polynomial/*<R>*/ g);
    Polynomial/*<R>*/ modulo(Polynomial/*<R>*/ g);

    /**
     * Returns an array containing all the coefficients of this polynomial.
     * @return a new array containing all our coefficients.
     * @post RES[i]==get(i) &and; RES.length==degree()+1 &and; RES!=RES
     * @see Object#clone()
     */
    public Arithmetic/*>R<*/[] getCoefficients();
}
