/**
 * @(#)Polynomial.java 1.1 2002/08/21 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Polynomial p&isin;R[S] := R<sup>(S)</sup>.
 * <p>
 * R<sup>(S)</sup> := <big>&#8853;</big><sub>s&#8712;S</sub> R
 * is the R-<a href="doc-files/AlgebraicStructures.html#algebraOfMagma">algebra of the magma</a> S over R.
 * </p>
 * <p>
 * With S=<b>N</b><sup>n</sup> these are the multivariate polynomials in n variables.
 * Of course, the multivariate polynomials are polynomials over polynomial rings:
 * R[X<sub>0</sub>,...,X<sub>n-1</sub>] = R[X<sub>0</sub>][X<sub>1</sub>]...[X<sub>n-1</sub>].
 * The importance of polynomial rings comes from the fact that they are the
 * free commutative and associative R-algebras
 * Libasc<sub>R</sub>(I) := R<sup>(N<sup>(I)</sup>)</sup> = R[(X<sub>i</sub>)<sub>i&isin;I</sub>].
 * Although, of course, this is usually restricted to a finite number of variables for the purpose of computation.
 * <!-- @todo could we calculate only infinitely generated algebras as well? -->
 * </p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Values#polynomial(Object)
 * @see Values#asPolynomial(Tensor)
 * @see "N. Bourbaki, Algebra III.2.6: Algebra of a magma, a monoid, a group."
 * @see "N. Bourbaki, Algebra III.2.7: Free algebras."
 * @todo implements Function<T,T> instead with T any "compatible" type (see Algebra I) and evaluation of Horner-Scheme
 */
public interface Polynomial/*<R implements Arithmetic, S implements Arithmetic>*/
    extends Arithmetic, orbital.math.functional.Function/*<orbital.logic.functor.Function<S,E>,E>*/ {
    // Get/Set properties
    /**
     * Describes the index magma S of our polynomial ring R[S].
     * <p>
     * The index set specifies what form indices of coefficients have.
     * Since there usually is no computer representation of the full
     * index set, this method will only return a description object
     * that can be compared to other index sets via {@link
     * Object#equals(Object)}.
     * <p>
     * The precise structure of this object is not defined but, for
     * example, for the polynomial ring
     * R[<b>N</b><sup>n</sup>]=R[X<sub>0</sub>,...,X<sub>n-1</sub>] in
     * n variables, it may simply be the integer n.
     * </p>
     * @return an (memento) description of the index set S which is even a magma.
     * @postconditions RES.supports(#equals(Object)) || RES.getClass().isArray()
     */
    Object indexSet();

    /**
     * Returns an iterator over the (relevant) indices.
     * <p>
     * The order of this iterator is not generally defined, but should be deterministical.
     * Particularly, the iterator may - but need not - be restricted to occurring indices
     * with coefficients &ne;0.
     * </p>
     * @return an iterator over a finite set of indices in S
     *  at least containing all indices of coefficients &ne;0.
     * @postconditions &forall;i&isin;S&#8726;RES get(i)=0
     */
    Iterator/*_<S>_*/ indices();

    /**
     * Get the i-th coefficient.
     * The i-th coefficient &alpha;<sub>i</sub> is the coefficient of &iota;(i) for i&isin;S.
     * For example, if S=<b>N</b><sup>n</sup> that is the coefficient of the monomial
     * X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>,
     * which is <code>0</code> if |i|&gt;deg(this).
     * @preconditions i&isin;{@link #indexSet() S}
     * @return &alpha;<sub>i</sub>.
     */
    Arithmetic/*>R<*/ get(Arithmetic/*>S<*/ i);

    // graduation
    
    /**
     * Get the degree of this polynomial.
     * <p>
     * For example, if S=<b>N</b><sup>n</sup> then this method returns the total degree
     * deg(this) := max {|i|:=&sum;<sub>j=0,...,n-1</sub> i<sub>j</sub> &brvbar; i&isin;<b>N</b><sup>n</sup> &and; a<sub>i</sub>&ne;0}.
     * </p>
     * @throws UnsupportedOperationException if R[S] is not a graded ring with a very meaningful graduation.
     *  By providing this option, implementations are not forced to use trivial graduations if no
     *  meaningful graduation exists.
     * @note though graduations may have a more general set of degrees than <b>Z</b>, we restrict
     * ourselves to that case, which is by far the most usual one.
     */
    Integer degree();
    /**
     * Get the (int value of the) degree of this polynomial.
     * @see #degreeValue()
     */
    int degreeValue();
	
    // iterator views

    /**
     * Returns an iterator over all coefficients (up to degree).
     * @xxx remove/adapt, whatever. What are "all" coefficients up to "degree" in general?
     */
    ListIterator/*_<R>_*/ iterator();

    // function
    
    /**
     * Evaluate this polynomial at <var>a</var>.
     * Using the <span xml:lang="de">"Einsetzungshomomorphismus"</span> from the universal mapping property.
     * @param a the index embedding <code>a</code>:S&rarr;(E,&sdot;), encoded as a {@link orbital.logic.functor.Function Function&lt;S,E&gt;},
     *  that determines to which element <code>a</code>(s) to map the index s&isin;S.<br />
     *  With S=<b>N</b><sup>n</sup> <code>a</code> can also be encoded as a {@link Vector Vector&lt;E&gt;}
     *  <code>a</code>&isin;E<sup>n</sup>&cong;E<sup><b>N</b><sup>n</sup></sup>.
     * @return f(a) = f((X<sub>k</sub>)<sub>k</sub>)|<sub>(X<sub>k</sub>)<sub>k</sub>=a</sub>.
     * @xxx adapt document
     */
    Object/*>E<*/ apply(Object/*>orbital.logic.functor.Function<S,E><*/ a);

    // Arithmetic
    
    Polynomial/*<R,S>*/ add(Polynomial/*<R,S>*/ b);
    Polynomial/*<R,S>*/ subtract(Polynomial/*<R,S>*/ b);
    /**
     * Multiplies two polynomials.
     * @return
     * <div>
     *   (&sum;<sub>s&isin;S</sub> &alpha;<sub>s</sub>&middot;&iota;(s))&sdot;(&sum;<sub>s&isin;S</sub>
     *   &beta;<sub>s</sub>&middot;&iota;(s)) = &sum;<sub>s&isin;S</sub> (&sum;<sub>t&sdot;u=s</sub>
     *   &alpha;<sub>t</sub>&#8202;&beta;<sub>u</sub>)&middot;&iota;(s)
     *   = &sum;<sub>s&isin;S</sub> &alpha;<sub>s</sub>&middot;(&sum;<sub>t&isin;S</sub>
     *   &beta;<sub>t</sub>&middot;&iota;(t))&sdot;&iota;(s)
     *   = &sum;<sub>s,t&isin;S</sub> (&alpha;<sub>s</sub>&#8202;&beta;<sub>t</sub>)&middot;(&iota;(s)&sdot;&iota;(t))
     * </div>
     */
    Polynomial/*<R,S>*/ multiply(Polynomial/*<R,S>*/ b);
}
