/**
 * @(#)Quotient.java 1.1 2002/01/12 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Function;

/**
 * Quotient represents an (algebraic) equivalence class a&#772;=&atilde;=[a]&isin;M/~.
 * <p>
 * Let &pi;:M&#8608;M/~;a&#8614;a&#772; be the canonical projection to the equivalence classes.
 * When <strong>choosing</strong> one <em>left</em>-inverse &pi;<sup>-1</sup>, we have a
 * canonical representative a=&pi;<sup>-1</sup>(a&#772;) of a&#772;.
 * However be aware that usually &pi;<sup>-1</sup> &#8728; &pi; &ne; id
 * which means that the canonical representative of the equivalence class a&#772;&isin;M/~
 * of a&isin;M usually is not a itself.
 * </p>
 * <p>
 * An implementation will reduce the values a&isin;M of the algebraic structure M
 * modulo ~ to get the canonical representative of the equivalence class a&#772;.
 * Although an implementation is encouraged to reduce modulo ~ after each operation,
 * this is not strictly required by some applications, as long as equality on
 * the representatives is implemented to fit to the congruence of the equivalence classes.
 * Of course, values of intermediate states need not be reduced at all.
 * Nevertheless, an implementation that has a more lazy reduction policy should document
 * this very carefully because it may affect precision considerations.
 * </p>
 * <h3>Examples of Usage</h3>
 * <p>
 * For example in order to perform algebraic operations in the ring
 * <b>Z</b>/n<b>Z</b> = <b>Z</b>/(n) &cong; {0,1,...,n-1}
 * use a construct like the following:
 * <pre>
 * <span class="comment">// create elements in <b>Z</b>/n<b>Z</b></span>
 * <span class="keyword">final</span> <span class="primitiveType">int</span> n = <span class="Number">17</span>;
 * <span class="Orbital">Quotient</span>&lt;<span class="Orbital">Integer</span>&gt; a = <span class="Orbital">Values</span>.quotient(<span class="Number">8</span>, n);
 * <span class="Orbital">Quotient</span>&lt;<span class="Orbital">Integer</span>&gt; b = <span class="Orbital">Values</span>.quotient(<span class="Number">11</span>, n);
 * <span class="comment">// perform calculations in <b>Z</b>/n<b>Z</b></span>
 * <span class="Orbital">Arithmetic</span> c = a.add(b.multiply(a));
 * </pre>
 * If you choose a prime n=p&isin;<b>N</b> then the above construct gives
 * the finite field <b>F</b><sub>p</sub> of p elements.
 * <small>
 * Note that you can simply leave out parametric type specifiers like
 * <tt>&lt;<span class="Orbital">Integer</span>&gt;</tt> if you do not
 * intend to use Java Generics (see <a href="{@docRoot}/templates.html#avoid">ignoring templates</a>).
 * </small>
 * </p>
 * <p>
 * In fact, you could also perform algebraic operations in the quotient ring
 * <b>Q</b>[X]/(X<sup>2</sup>+X+1)
 * <pre>
 * <span class="comment">// create elements in <b>Q</b>[X]/(X<sup>2</sup>+X+1)</span>
 * <span class="keyword">final</span> <span class="Orbital">Polynomial</span>&lt;<span class="Orbital">Rational</span>&gt; m =
 *     <span class="Orbital">Values</span>.asPolynomial(<span class="Orbital">Values</span>.valueOf(<span class="operator">new</span> <span class="primitiveType">int</span>[] {<span class="Number">1</span>,<span class="Number">1</span>,<span class="Number">1</span>}));
 * <span class="Orbital">Quotient</span>&lt;<span class="Orbital">Polynomial</span>&lt;<span class="Orbital">Rational</span>&gt;&gt; a = <span class="Orbital">Values</span>.quotient(..., m);
 * <span class="Orbital">Quotient</span>&lt;<span class="Orbital">Polynomial</span>&lt;<span class="Orbital">Rational</span>&gt;&gt; b = <span class="Orbital">Values</span>.quotient(..., m);
 * <span class="comment">// perform calculations in <b>Q</b>[X]/(X<sup>2</sup>+X+1)</span>
 * <span class="Orbital">Arithmetic</span> c = a.add(b.multiply(a));
 * </pre>
 * Or get the complex field <b>C</b> as the quotient ring
 * <b>R</b>[X]/(X<sup>2</sup>+1)
 * <pre>
 * <span class="comment">// create elements in <b>C</b> alias <b>R</b>[X]/(X<sup>2</sup>+1)</span>
 * <span class="keyword">final</span> <span class="Orbital">Polynomial</span>&lt;<span class="Orbital">Real</span>&gt; m =
 *     <span class="Orbital">Values</span>.asPolynomial(<span class="Orbital">Values</span>.valueOf(<span class="operator">new</span> <span class="primitiveType">double</span>[] {<span class="Number">1</span>,<span class="Number">0</span>,<span class="Number">1</span>}));
 * <span class="Orbital">Quotient</span>&lt;<span class="Orbital">Polynomial</span>&lt;<span class="Orbital">Real</span>&gt;&gt; a = <span class="Orbital">Values</span>.quotient(..., m);
 * <span class="Orbital">Quotient</span>&lt;<span class="Orbital">Polynomial</span>&lt;<span class="Orbital">Real</span>&gt;&gt; b = <span class="Orbital">Values</span>.quotient(..., m);
 * <span class="comment">// <b>i</b>&isin;<b>C</b> corresponds to X&isin;<b>R</b>[X]/(X^2+1)</span>
 * <span class="Orbital">Quotient</span>&lt;<span class="Orbital">Polynomial</span>&lt;<span class="Orbital">Real</span>&gt;&gt; i = <span class="Orbital">Values</span>.quotient(
 *     <span class="Orbital">Values</span>.asPolynomial(<span class="Orbital">Values</span>.valueOf(<span class="operator">new</span> <span class="primitiveType">double</span>[] {<span class="Number">0</span>,<span class="Number">1</span>})),
 *     m);
 * <span class="comment">// perform calculations in <b>C</b> alias <b>R</b>[X]/(X<sup>2</sup>+1)</span>
 * <span class="Orbital">Arithmetic</span> c = a.add(b.multiply(a)).multiply(i);
 * </pre>
 * </p>
 *
 * @version 1.1, 2002/01/12
 * @author  Andr&eacute; Platzer
 * @see <a href="doc-files/AlgebraicStructures.html#quotient">Quotient Structures</a>
 * @see Values#quotient(Arithmetic,Function)
 * @see Values#quotient(Euclidean,Euclidean)
 * @internal this is an interface "Modulus/Quotient" or thing that extends Arithmetic and provides things like Z/nZ, F_p, and perhaps Q[X]/(X^2+X+1).
 * @todo couldn't we perhaps dynamically extend M at runtime if M is an interface?
 */
public interface Quotient/*<M implements Arithmetic>*/ extends Arithmetic {
    // Get/Set Properties
	
    /**
     * Get the quotient operator &pi;<sup>-1</sup>&#8728;&pi;:M&rarr;M
     * modulo whom we reduce the values to their canonical representative.
     * <p>
     * This modulo operator maps an element a&isin;M to the canonical representative
     * of its equivalence class modulo ~.
     * </p>
     * <p>
     * Note that this quotient operator should usually provide {@link Object#equals(Object)}
     * to support checking for equal types of equivalence classes.
     * </p>
     * @return the quotient operator &pi;<sup>-1</sup>&#8728;&pi;:M&rarr;M of this quotient M/~.
     * @post ... RES == OLD(RES) &and; RES is functional
     * @todo rename
     */
    Function/*<M,M>*/ getQuotientOperator();

    /**
     * Get the "canonical" representative of this equivalence class.
     * @return a "canonical" element &pi;<sup>-1</sup>(a&#772;)&isin;R such that &pi;(a) = a&#772; = this.
     * @post RES == getQuotientOperator().apply(this) &and; getQuotientOperator().apply(RES).equals(RES)
     *  &and; new Modulus(RES, getQuotientOperator()).equals(this)
     *  &and; &forall;a,b&isin;M/~ (a.equals(b) &rArr; a.representative().equals(b.representative()))
     * @internal Von a&#772M kennt man nicht a persönlich, sondern man hätte auch sein Äquivalent erwischen können.
     */
    Arithmetic/*>M<*/ representative();

    // extends Arithmetic<Quotient>

    /**
     * Returns (modular) multiplicative inverse of this (mod ~).
     * @throws ArithmeticException if this quotient has no
     * multiplicative inverse modulo ~.
     * At least in quotients of {@link Euclidean euclidean rings} R
     * an element a&isin;R/(m) does not have a multiplicative inverse,
     * iff gcd(a, m)&ne;1.
     */
    Arithmetic inverse() throws ArithmeticException;

    Quotient/*<M>*/ add(Quotient/*<M>*/ b);
    Quotient/*<M>*/ subtract(Quotient/*<M>*/ b);
    Quotient/*<M>*/ multiply(Quotient/*<M>*/ b);
    Quotient/*<M>*/ divide(Quotient/*<M>*/ b) throws ArithmeticException;
    Quotient/*<M>*/ power(Quotient/*<M>*/ b);
}
