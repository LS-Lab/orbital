/**
 * @(#)Matrix.java 1.0 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.ListIterator;

import java.util.NoSuchElementException;

/**
 * Represents a matrix of any dimension n&times;m.
 * <p>
 * R<sup>n&times;m</sup> = { <span class="matrix">M</span>=<big>(</big>m<sub>i,j</sub><big>)</big> &brvbar; m<sub>i,j</sub>&isin;R} &cong; R<sup>n</sup> &otimes;<sub>R</sub> R<sup>m</sup> where
 * <table>
 *   <tr><td rowspan="4"><span class="matrix">M</span> = <big>(</big>m<sub>i,j</sub><big>)</big> = </td>
 *       <td rowspan="4" style="font-size: 600%; font-weight: 200">[</td> <td>m<sub>0,0 </sub>,</td> <td>m<sub>0,1 </sub>,</td> <td>m<sub>0,2 </sub>,</td> <td>&#8230;,</td> <td>m<sub>0,m-1 </sub></td> <td rowspan="4" style="font-size: 600%; font-weight: 200">]</td></tr>
 *   <tr> <td>m<sub>1,0 </sub>,</td> <td>m<sub>1,1 </sub>,</td> <td>m<sub>1,2 </sub>,</td> <td>&#8230;,</td> <td>m<sub>1,m-1 </sub></td> </tr>
 *   <tr> <td colspan="4">&#8942;</td> <td>&#8942;</td> </tr>
 *   <tr> <td>m<sub>n-1,0</sub>,</td> <td>m<sub>n-1,1</sub>,</td> <td>m<sub>n-1,2</sub>,</td> <td>&#8230;,</td> <td>m<sub>n-1,m-1</sub></td> </tr>
 * </table>
 * If the dimension is height&times;width then the matrix has
 * height rows and width columns.
 * </p>
 * <p>
 * The components m<sub>i,j</sub>&isin;R are any arithmetic objects.
 * If R is a true ring with 1,
 * the square matrices (R<sup>n&times;n</sup>,+,&middot;,&#8729;) form a
 * unital, associative R-<a href="doc-files/AlgebraicStructures.html#laws_2_1">algebra</a>
 * with the laws of composition + and &#8729; and the law of action &middot;.
 * But it is non-commutative and has zero divisors for n&ge;2.
 * The general matrices (R<sup>n&times;m</sup>,+,&middot;) form an
 * R-<a href="doc-files/AlgebraicStructures.html#laws_1_1">module</a>,
 * at least,
 * with the law of composition + and the law of action &middot;.
 * If R is a field, the matrices even form an R-vector space.
 * </p>
 * <p>
 * If you intend to use <em>mutable</em> arithmetic elements, note the discussion of
 * mutations per reference vs. explicit cloning in {@link Tensor#set(int[],Arithmetic)}
 * which generally holds for all operations that set component values.
 * </p>
 * <p>
 * Also note that some few methods will change its instance and explicitly <code><span class="keyword">return</span> <span class="keyword">this</span></code>
 * to allow chaining of structural changes,
 * whilst arithmetic methods will leave a matrix unchanged but return a modified version.
 * Refer to the documentation of the individual methods for details.
 * </p>
 * 
 * @invariants super &and; rank()==2
 * @structure extends Tensor
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Values#tensor(Arithmetic[][])
 * @see Values#valueOf(Arithmetic[][])
 * @see Values#valueOf(double[][])
 * @todo turn into a template Matrix<R implements Arithmetic>
 */
public interface Matrix/*<R implements Arithmetic>*/ extends Tensor/*<R>*/ {
    // get/set-methods
	
    /**
     * Returns the dimension of the matrix.
     */
    Dimension dimension();

    /**
     * Returns the component value at a position (i|j).
     * <p>
     * Of course, this method only has a meaning for free modules like vector spaces.
     * </p>
     * @param i the row of the value to get.
     * @param j the column of the value to get.
     * @return m<sub>i,j</sub>.
     */
    Arithmetic/*>R<*/ get(int i, int j);

    /**
     * Sets a component value at a position (i|j).
     * <p>
     * Of course, this method only has a meaning for free modules like vector spaces.
     * </p>
     * @param i the row of the value to set.
     * @param j the column of the value to set.
     * @param mij the value to set for the element m<sub>i,j</sub> at position (i|j)
     * @throws UnsupportedOperationException if this matrix is constant and does not allow this operation.
     * @see #modCount
     */
    void set(int i, int j, Arithmetic/*>R<*/ mij) throws UnsupportedOperationException;

    // iterator-views
	
    /**
     * Returns an iterator over the column vectors.
     */
    ListIterator/*_<R>_*/ getColumns();

    /**
     * Returns an iterator over the row vectors.
     */
    ListIterator/*_<R>_*/ getRows();

    /**
     * Returns an iterator over all components (row-wise).
     * <p>
     * If you cannot avoid it, call transpose().iterator() to get an iterator over all components, column by column.</p>
     * @return an iterator that iterates over {m<sub>0,0</sub>,&#8230;,m<sub>0,m-1</sub>, m<sub>1,0</sub>,&#8230;,m<sub>1,m-1</sub>,&#8230;, m<sub>n-1,0</sub>,&#8230;,m<sub>n-1,m-1</sub>}.
     */
    ListIterator/*_<R>_*/ iterator();

    // sub-views
	
    /**
     * Returns the column vector view of a column.
     * <p>
     * The returned vector is a structurally unmodifiable <a href="Tensor.html#view">view</a>.</p>
     * <p>
     * For M<sub>i,j</sub>, bindSecond(c).</p>
     * @return a vector view of the specified column <span class="matrix">M</span><sub>(0:n-1,c:c)</sub> in this matrix.
     * @see orbital.logic.functor.Functionals#bindSecond(orbital.logic.functor.BinaryFunction, Object)
     */
    Vector/*<R>*/ getColumn(int c);

    /**
     * Sets the column vector at a column.
     * @preconditions col.dimension() == dimension().height
     * @see #modCount
     */
    void setColumn(int c, Vector/*<R>*/ col) throws UnsupportedOperationException;

    /**
     * Returns the row vector view of a row.
     * <p>
     * The returned vector is a structurally unmodifiable <a href="Tensor.html#view">view</a>.</p>
     * <p>
     * For M<sub>i,j</sub> bindFirst(r).</p>
     * @return a vector view of the specified row <span class="matrix">M</span><sub>(r:r,0:m-1)</sub> in this matrix.
     * @todo document and specify whether the return-value is a clone or a reference to the row vector, etc.
     * @see orbital.logic.functor.Functionals#bindFirst(orbital.logic.functor.BinaryFunction, Object)
     */
    Vector/*<R>*/ getRow(int r);

    /**
     * Sets the row vector at a row.
     * @preconditions row.dimension() == dimension().width
     * @see #modCount
     */
    void setRow(int r, Vector/*<R>*/ row) throws UnsupportedOperationException;

    /**
     * Get a sub-matrix view ranging (i1:i2,j1:j2) inclusive.
     * That is Moeler notation.
     * <p>
     * The returned matrix is a structurally unmodifiable <a href="Tensor.html#view">view</a>.</p>
     * @preconditions i1&le;i2 && j1&le;j2 && valid(i1, j1) && valid(i2, j2)
     * @param i1 the top-most row index of the sub matrix view to get.
     * @param i2 the bottom-most row index of the sub matrix view to get.
     * @param j1 the left-most column index of the sub matrix view to get.
     * @param j2 the right-most column index of the sub matrix view to get.
     * @return a matrix view of the specified part of this matrix.
     * <table>
     *   <tr><td rowspan="3"><span class="matrix">M</span><sub>(i1:i2,j1:j2)</sub> = <big>(</big>m<sub>i,j</sub><big>)</big><sub>i&isin;{i1,&#8230;,i2},j&isin;{j1,&#8230;,j2}</sub> = </td>
     *       <td rowspan="3" style="font-size: 600%; font-weight: 200">[</td> <td>m<sub>i1,j1</sub>,</td> <td>&#8230;,</td> <td>m<sub>i1,j2</sub></td> <td rowspan="3" style="font-size: 600%; font-weight: 200">]</td></tr>
     *   <tr> <td colspan="2">&#8942;</td> <td>&#8942;</td> </tr>
     *   <tr> <td>m<sub>i2,j1</sub>,</td> <td>&#8230;,</td> <td>m<sub>i2,j2</sub></td> </tr>
     * </table>
     */
    Matrix/*<R>*/ subMatrix(int i1, int i2, int j1, int j2);

    // various get/set properties
	
    /**
     * Returns the main-diagonal-vector of this square matrix.
     * The vector that consists of m<sub>i,i</sub>.
     * @preconditions isSquare()
     * @todo should we turn this into a view as well?
     */
    Vector/*<R>*/ getDiagonal();

    /**
     * Checks whether this matrix is a square matrix of size n&times;n.
     * @preconditions true
     * @postconditions RES == (dimension().width == dimension().height)
     */
    boolean isSquare();

    /**
     * Checks whether this square matrix is symmetric.
     * Symmetric matrices are those that satisfy <span class="matrix">M</span><sup>T</sup> = <span class="matrix">M</span>
     * alias m<sub>i,j</sub>=m<sub>j,i</sub> &forall;i,j&isin;<b>N</b>.
     * <p>
     * In <b>R</b><sup>n&times;m</sup>, symmetric is the same as self-adjoint.
     * </p>
     * @preconditions isSquare()
     * @postconditions RES.equals(transpose().equals(this))
     * @throws ArithmeticException if this is not a square matrix since only square matrices can be symmetric.
     */
    boolean isSymmetric() throws ArithmeticException;

    /**
     * Checks whether this square matrix is regular.
     * Invertible matrices are also called regular,
     * which are those with invertible determinant.
     * @return <code>true</code> if this matrix is invertible and <code>false</code> if it is singular (linear rank&lt;n).
     * @preconditions isSquare()
     * @postconditions RES &hArr; det()&isin;R<sup>&times;</sup>
     * @throws ArithmeticException if this is not a square matrix since only square matrices can be regular.
     */
    boolean isInvertible() throws ArithmeticException;

    /**
     * @deprecated Since Orbital1.1 use {@link #isInvertible()} instead.
     */
    boolean isRegular() throws ArithmeticException;

    /**
     * Checks how definite this square matrix is.
     * <p>
     * f.ex. a symmetric matrix is positive definite iff every main minor is &gt; 0.
     * i.e. &forall;i&isin;{0,&#8230;,n-1} det <span class="matrix">A</span>(0:i,0:i) &gt; 0.
     * This means that every square submatrix that includes the element a<sub>0,0</sub>
     * has a strictly positive determinant.
     * </p>
     * <p>
     * C.f. Sylvester-normal form of quadratic forms.
     * </p>
     * @preconditions isSquare() &and; <span class="@todo">isSymmetric()?</span>
     * @return
     *  <ul>
     *    <li>d&gt;0 if this matrix is positive definite, i.e. &forall;<span class="vector">x</span>&isin;V&#8726;{0} &lang;<span class="vector">x</span>,<span class="matrix">A</span>&#8729;<span class="vector">x</span>&rang; &gt; 0.</li>
     *    <li>d&ge;0 if this matrix is positive semi-definite, i.e. &forall;<span class="vector">x</span>&isin;V &lang;<span class="vector">x</span>,<span class="matrix">A</span>&#8729;<span class="vector">x</span>&rang; &ge; 0.</li>
     *    <li>d=0 if this matrix is indefinite, i.e. &exist;<span class="vector">x</span>&isin;V &lang;<span class="vector">x</span>,<span class="matrix">A</span>&#8729;<span class="vector">x</span>&rang; &gt;0 &and; &exist;<span class="vector">y</span>&isin;V &lang;<span class="vector">y</span>,<span class="matrix">A</span>&#8729;<span class="vector">y</span>&rang; &lt;0.</li>
     *    <li>d&le;0 if this matrix is negative semi-definite, i.e. &forall;<span class="vector">x</span>&isin;V &lang;<span class="vector">x</span>,<span class="matrix">A</span>&#8729;<span class="vector">x</span>&rang; &le; 0.</li>
     *    <li>d&lt;0 if this matrix is negative definite, i.e. &forall;<span class="vector">x</span>&isin;V&#8726;{0} &lang;<span class="vector">x</span>,<span class="matrix">A</span>&#8729;<span class="vector">x</span>&rang; &lt; 0.</li>
     *  </ul>
     * @throws ArithmeticException if this is not a square matrix since only square matrices can be symmetric.
     * @todo does <span class="matrix">A</span> need to be symmetric? And improve docu
     */
    int isDefinite() throws ArithmeticException;
	
    /**
     * (linear) rank of this matrix.
     * i.e. the maximum number of column vectors (or row vectors) that are linear independent.
     * @return (linear) rank <span class="matrix">M</span> := dim<sub>K</sub>(im(<span class="matrix">M</span>)).
     * @see Tensor#rank()
     */
    int linearRank();


    /**
     * Returns the norm || ||<sub>p</sub> of this matrix.
     * <p>
     * ||.||:<b>C</b><sup>n&times;m</sup>&rarr;[0,&infin;) is a <dfn>matrix norm</dfn> or consistent if it is a (vector) norm and
     * <ul>
     *   <li>&forall;<span class="matrix">x</span>&isin;<b>C</b><sup>n&times;m</sup>,<span class="matrix">y</span>&isin;<b>C</b><sup>m&times;l</sup> ||<span class="matrix">x</span>&sdot;<span class="matrix">y</span>|| &le; ||<span class="matrix">x</span>||&sdot;||<span class="matrix">y</span>||	(sub multiplicative)
     * </ul>
     * ||.|| is <dfn>compatible</dfn> or conform with the vector norms ||.||<sub><b>C</b><sup>n</sup></sub>, ||.||<sub><b>C</b><sup>m</sup></sub> if
     * <ul>
     *   <li>&forall;<span class="matrix">A</span>&isin;<b>C</b><sup>n&times;m</sup>, <span class="vector">x</span>&isin;<b>C</b><sup>m</sup> ||<span class="matrix">A</span>&#8729;<span class="vector">x</span>||<sub><b>C</b><sup>n</sup></sub> &le; ||<span class="matrix">A</span>||&sdot;||<span class="vector">x</span>||<sub><b>C</b><sup>m</sup></sub>
     * </ul>
     * ||.|| is induced by the vector norms ||.||<sub><b>C</b><sup>n</sup></sub>, ||.||<sub><b>C</b><sup>m</sup></sub> if
     * <ul>
     *   <li>&forall;<span class="matrix">A</span>&isin;<b>C</b><sup>n&times;m</sup>, <span class="vector">x</span>&isin;<b>C</b><sup>m</sup> ||<span class="matrix">A</span>|| = max {||<span class="matrix">A</span>&#8729;<span class="vector">x</span>||<sub><b>C</b><sup>n</sup></sub> / ||<span class="vector">x</span>||<sub><b>C</b><sup>m</sup></sub> &brvbar; <span class="vector">x</span>&ne;0} = max {||<span class="matrix">A</span>&#8729;<span class="vector">x</span>||<sub><b>C</b><sup>n</sup></sub> &brvbar; ||<span class="vector">x</span>||<sub><b>C</b><sup>m</sup></sub>=1}
     * </ul>
     * which is a measure for how much <span class="matrix">A</span> stretches vectors.
     * </p>
     * <p>This method should at least implement those induced p-norms
     * <ul>
     *   <li>||<span class="matrix">A</span>||<sub>1</sub> = max {&sum;<span class="doubleIndex"><sub>i=1</sub><sup>n</sup></span>|a<sub>ij</sub>| &brvbar; 1&le;j&le;m} is the norm of column sums.</li>
     *   <li>||<span class="matrix">A</span>||<sub>&infin;</sub> = max {&sum;<span class="doubleIndex"><sub>j=1</sub><sup>m</sup></span>|a<sub>ij</sub>| &brvbar; 1&le;i&le;n} is the norm of row sums.</li>
     *   <li>||<span class="matrix">A</span>||<sub>2</sub> = sqrt max Eigenvalues(<span class="matrix">A</span><sup>*</sup>&#8729;<span class="matrix">A</span>) is the spectral norm. (optional operation)</li>
     * </ul>
     * </p>
     * @todo document again, the norms implemented by AbstractMatrix.
     * @preconditions p>=1
     */
    Real norm(double p);

    /**
     * {@inheritDoc}
     * Usually returns the Frobenius norm of this matrix.
     * <p>
     * ||<span class="matrix">A</span>|| = &radic;<span class="text-decoration: overline">(&sum;<span class="doubleIndex"><sub>i=1</sub><sup>n</sup></span>&sum;<span class="doubleIndex"><sub>j=1</sub><sup>m</sup></span> |a<sub>ij</sub>|<sup>2</sup>) the Frobenius norm.
     * </p>
     * <p>
     * Note that the Frobenius norm is not a p-norm.
     * It is a norm got by {@link Values#asVector(Matrix) identifying matrices with vectors}.</p>
     */
    Real norm();

    /**
     * Returns the trace of the matrix representation.<p>
     * The trace is invariant to conjugation (similar matrices):
     * Tr (<span class="matrix">T</span><sup>-1</sup>&#8729;<span class="matrix">A</span>&#8729;<span class="matrix">T</span>) = Tr <span class="matrix">A</span>.
     * @return sum of the main-diagonal-vectors components.
     * @preconditions isSquare()
     * @throws ArithmeticException if this is not a square matrix, since only square matrix have a trace.
     */
    Arithmetic/*>R<*/ trace() throws ArithmeticException;

    /**
     * Returns the determinant of the matrix representation. The determinant is useful to determine if a
     * matrix is {@link #isInvertible() invertible}.
     * The determinant is the universal alternating map
     * R<sup>n&times;n</sup>&cong;(R<sup>n</sup>)<sup>n</sup>&rarr;&Lambda;<sup>n</sup>(R<sup>n</sup>)&cong;R
     * of the exterior product &Lambda;<sup>n</sup>(R<sup>n</sup>).
     * It is denoted as |<span class="matrix">M</span>| := det <span class="matrix">M</span>.
     * <p>
     * <table>
     * <tr><td colspan="3">det:R<sup>n&times;n</sup>&rarr;R exists and is uniquely
     *     defined by</td>
     * </tr>
     *   <tr>
     *     <td>(ml)</td>
     *     <td><table>
     *       <tr>
     *         <td rowspan="5">det</td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td align="center">a<sub>0</sub></td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *         <td rowspan="5">=</td>
     *         <td rowspan="5">&alpha;&middot;det</td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td align="center">a<sub>0</sub></td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *         <td rowspan="5">+</td>
     *         <td rowspan="5">&beta;&middot;det</td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td align="center">a<sub>0</sub></td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *       </tr>
     *       <tr>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td align="center" style="white-space: nowrap">&alpha;a<sub>i</sub> + &beta;b<sub>i</sub></td>
     *         <td align="center">a<sub>i</sub></td>
     *         <td align="center">b<sub>i</sub></td>
     *       </tr>
     *       <tr>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td align="center">a<sub>n-1</sub></td>
     *         <td align="center">a<sub>n-1</sub></td>
     *         <td align="center">a<sub>n-1</sub></td>
     *       </tr>
     *     </table></td>
     *     <td>&quot;multi-linear&quot; (linear in each row)</td>
     *   </tr>
     *   <tr>
     *     <td>( )</td>
     *     <td>rank <span class="matrix">M</span> &lt; n &hArr; det(<span class="matrix">M</span>)=0</td>
     *     <td>&quot;&asymp;alternating&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>(1)</td>
     *     <td>det(I) = 1</td>
     *     <td>&nbsp;</td>
     *   </tr>
     *   <tr>
     *     <td colspan="3"><b>&rArr; Properties</b></td>
     *   </tr>
     *   <tr>
     *     <td>&nbsp;( )</td>
     *     <td><table>
     *       <tr>
     *         <td rowspan="7">det</td>
     *         <td rowSpan="7" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td align="center">a<sub>0</sub></td>
     *         <td rowSpan="7" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *         <td rowspan="7">=</td>
     *         <td rowspan="7">-det</td>
     *         <td rowSpan="7" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td align="center">a<sub>0</sub></td>
     *         <td rowSpan="7" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *       </tr>
     *       <tr>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td align="center">a<sub>i</sub> </td>
     *         <td align="center">a<sub>j</sub></td>
     *       </tr>
     *       <tr>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td align="center">a<sub>j</sub></td>
     *         <td align="center">a<sub>i</sub></td>
     *       </tr>
     *       <tr>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td align="center">a<sub>n-1</sub></td>
     *         <td align="center">a<sub>n-1</sub></td>
     *       </tr>
     *     </table></td>
     *     <td>&quot;skew symmetric&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>( )</td>
     *     <td>det(<span class="matrix">M</span><sup>T</sup>) = det(<span class="matrix">M</span>)</td>
     *     <td>&quot;invariant to transposition&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>( )</td>
     *     <td>det(<span class="matrix">M</span>&#8729;<span class="matrix">N</span>) = det(<span class="matrix">M</span>)&sdot;det(<span class="matrix">N</span>)</td>
     *     <td>&nbsp;</td>
     *   </tr>
     *   <tr>
     *     <td>( )</td>
     *     <td><span class="matrix">M</span>&isin;(R<sup>n&times;n</sup>)<sup>&times;</sup> &hArr; det(<span class="matrix">M</span>)&isin;R<sup>&times;</sup><br />
     *     &rArr; det(<span class="matrix">M</span><sup>-1</sup>) = det(<span class="matrix">M</span>)<sup>-1</sup>
     *     </td>
     *     <td>&nbsp;</td>
     *   </tr>
     *   <tr>
     *     <td>( )</td>
     *     <td><table>
     *       <tr>
     *         <td rowspan="5">det</td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td align="center">a<sub>0</sub></td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *         <td rowspan="5">=</td>
     *         <td rowspan="5">det</td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td align="center">a<sub>0</sub></td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *       </tr>
     *       <tr>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td align="center">a<sub>i</sub>  + &sum;<sub>k&ne;i</sub> &lambda;<sub>k</sub>a<sub>k</sub></td>
     *         <td align="center">a<sub>j</sub></td>
     *       </tr>
     *       <tr>
     *         <td align="center">&#8942;</td>
     *         <td align="center">&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td align="center">a<sub>n-1</sub></td>
     *         <td align="center">a<sub>n-1</sub></td>
     *       </tr>
     *     </table></td>
     *     <td>&quot;invariant to EOP&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>( )</td>
     *     <td><table>
     *       <tr>
     *         <td rowSpan="5">det</td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td>a<sub>0</sub></td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *         <td rowSpan="5"> = &sum;<sub>&sigma;&isin;S<sub>n</sub></sub> sign &sigma; &sdot; a<sub>1&sigma;(1)</sub> &sdot;
     *           &#8230;&sdot; a<sub>n&sigma;(n)</sub></td>
     *       </tr>
     *       <tr>
     *         <td>&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td>a<sub>i</sub></td>
     *       </tr>
     *       <tr>
     *         <td>&#8942;</td>
     *       </tr>
     *       <tr>
     *         <td>a<sub>n</sub></td>
     *       </tr>
     *     </table></td>
     *     <td>&quot;determinant formula of Leibniz&quot;</td>
     *   </tr>
     * </table>
     * </p>
     * <p>
     * The determinant is invariant to conjugation (similar matrices):
     * det (<span class="matrix">T</span><sup>-1</sup>&#8729;<span class="matrix">A</span>&#8729;<span class="matrix">T</span>) = det <span class="matrix">A</span>.
     * A matrix is invertible if and only if its determinant is invertible.
     * However, if the determinant is approximately zero then inverse transform operations might not carry enough numerical precision to produce meaningful results.</p>
     * <p>
     * (det <span class="matrix">A</span>)' = &sum;<span class="doubleIndex"><sub>i=0</sub><sup>n-1</sup></span> det <big>(</big>a<sub>0</sub>&#8230;a<sub>i</sub>'&#8230;a<sub>n-1</sub><big>)</big>
     * where a<sub>i</sub> = (a<sub>0,i</sub>,&#8230;,a<sub>n-1,i</sub>)<sup>t</sup> is the i-th column of <span class="matrix">A</span>.
     * </p>
     * @return det <span class="matrix">A</span> = |<span class="matrix">A</span>|
     * @preconditions isSquare()
     * @postconditions det() multilinear
     *  	&& (rank() &lt; dimension().width &hArr; det() = 0)
     *  	&& IDENTITY(n).det() = 1
     * @todo document determinant properties and uniqueness
     * @throws ArithmeticException if this is not a square matrix, since determinant is only defined for square matrices.
     * @todo should R be a commutative ring with one?
     */
    Arithmetic/*>R<*/ det() throws ArithmeticException;

    // arithmetic-operations
	
    /**
     * Adds two matrices returning a matrix.
     * @preconditions dimension().equals(B.dimension())
     * @postconditions RES.dimension().equals(dimension())
     *  	&& RES.get(i, j) == get(i,j) + B.get(i,j)
     * @attribute associative
     * @attribute neutral (<span class="matrix">O</span>)
     * @attribute inverse (-A)
     * @attribute commutative
     */
    Matrix/*<R>*/ add(Matrix/*<R>*/ B);

    /**
     * Subtracts two matrices returning a matrix.
     * @preconditions dimension().equals(B.dimension())
     * @postconditions RES.dimension().equals(dimension())
     *  	&& RES.get(i, j) == get(i,j) - B.get(i,j)
     * @attribute associative
     */
    Matrix/*<R>*/ subtract(Matrix/*<R>*/ B);
    /**
     * Multiplies two matrices returning a matrix.
     * If <span class="matrix">A</span>&isin;R<sup>n&times;m</sup> and
     * <span class="matrix">B</span>&isin;R<sup>m&times;l</sup> the resulting matrix
     * <span class="matrix">A</span>&#8729;<span class="matrix">B</span>&isin;R<sup>n&times;l</sup>.
     * This is the ring multiplication of matrices,
     * and an inner product.
     * @return the n&times;l matrix <span class="matrix">A</span>&#8729;<span class="matrix">B</span>.
     * @preconditions dimension().width == B.dimension().height
     * @postconditions RES.dimension().height == dimension().height && RES.dimension().width == B.dimension().width
     *  	&& RES.get(i, j) == getRow(i) &sdot; B.getColumn(j)
     * @attribute associative
     * @attribute neutral (I)
     */
    Matrix/*<R>*/ multiply(Matrix/*<R>*/ B);

    /**
     * Multiplies a matrix with a scalar returning a matrix.
     * This is the scalar multiplication.
     * @preconditions true
     * @postconditions RES.dimension().equals(dimension())
     *  	&& RES.get(i, j) == s &sdot; get(i,j)
     * @return s&middot;<span class="matrix">A</span>
     * @todo multiply(Arithmetic) would need to determine type of s deep, in case it is not a scalar, but a matrix, or anything in R. Except if we separated ring and scalar (and perhaps tensor) multiplication by name.
     */
    Matrix/*<R>*/ scale(Scalar s);
    /**
     * Multiplies a matrix with a scalar returning a matrix.
     * @see #scale(Scalar)
     * @todo outroduce
     */
    Matrix/*<R>*/ multiply(Scalar s);

    /**
     * Multiplies a matrix with a vector returning a vector.
     * If <span class="matrix">A</span>&isin;R<sup>n&times;m</sup> and
     * v&isin;R<sup>m</sup> is a column vector of dimension m,
     * the resulting column vector <span class="matrix">A</span>&#8729;v&isin;R<sup>n</sup> has dimension n.
     * This is an inner product.
     * @todo is this a tensor product, or a faltung?
     * @return the n-dimensional column vector <span class="matrix">A</span>&#8729;v.
     * @preconditions dimension().width == B.dimension()
     * @postconditions RES.dimension() == dimension().height
     *  	&& RES.get(i) == getRow(i) &sdot; B
     */
    Vector/*<R>*/ multiply(Vector/*<R>*/ B);

    // operations on matrices

    /**
     * Returns this matrix transposed.
     * <br />
     * <sup>t</sup>&middot;:R<sup>n&times;m</sup>&rarr;R<sup>m&times;n</sup>; <span class="matrix">M</span>&#8614;<sup>t</sup><span class="matrix">M</span>:=<span class="matrix">M</span><sup>T</sup>:=(t<sub>i,j</sub>) with t<sub>i,j</sub> = m<sub>j,i</sub>.
     * <p>
     * <ul id="Properties">
     *   <li><sup>t</sup>(<span class="matrix">A</span>+<span class="matrix">B</span>) = <sup>t</sup><span class="matrix">A</span> + <sup>t</sup><span class="matrix">B</span></li>
     *   <li><sup>t</sup>(&lambda;&middot;<span class="matrix">A</span>) = &lambda;&middot;<sup>t</sup><span class="matrix">A</span></li>
     *   <li><sup>t</sup>(<span class="matrix">A</span>&#8729;<span class="matrix">C</span>) = <sup>t</sup><span class="matrix">C</span>&#8729;<sup>t</sup><span class="matrix">A</span></li>
     *   <li><big><sup>t</sup>(</big><sup>t</sup><span class="matrix">A</span><big>)</big> = <span class="matrix">A</span></li>
     * </ul>
     * &forall;<span class="matrix">A</span>,<span class="matrix">B</span>&isin;R<sup>n&times;m</sup>,<span class="matrix">C</span>&isin;R<sup>m&times;l</sup> &forall;&lambda;&isin;R
     * </p>
     * @return the m&times;n matrix <sup>t</sup><span class="matrix">M</span>=(t<sub>i,j</sub>) with elements t<sub>i,j</sub> = m<sub>j,i</sub>.
     * @postconditions RES.get(j,i) == get(i,j) && RES.dimension().width == dimension().height && RES.dimension().height == dimension().width
     * @see #conjugate()
     */
    Matrix/*<R>*/ transpose();

    /**
     * Returns this matrix conjugate transposed.<br>
     * .<sup>&#8889;</sup>:R<sup>n&times;m</sup>&rarr;R<sup>m&times;n</sup>; <span class="matrix">M</span>&#8614;<span class="matrix">M</span><sup>&#8889;</sup>:=<sup>t</sup><span class="Matrix" style="text-decoration: overline">M</span> = (t<sub>i,j</sub>) with t<sub>i,j</sub> = <span style="text-decoration: overline">m</span><sub>j,i</sub>.
     * <p>
     * <ul id="Properties">
     *   <li>(<span class="matrix">A</span>+<span class="matrix">B</span>)<sup>&#8889;</sup> = <span class="matrix">A</span><sup>&#8889;</sup> + <span class="matrix">B</span><sup>&#8889;</sup></li>
     *   <li>(&lambda;&middot;<span class="matrix">A</span>)<sup>&#8889;</sup> = <span style="text-decoration: overline">&lambda;</span>&middot;<span class="matrix">A</span><sup>&#8889;</sup> = &lambda;<sup>*</sup>&#8729;<span class="matrix">A</span><sup>&#8889;</sup></li>
     *   <li>(<span class="matrix">A</span>&#8729;<span class="matrix">C</span>)<sup>&#8889;</sup> = <span class="matrix">C</span><sup>&#8889;</sup>&#8729;<span class="matrix">A</span><sup>&#8889;</sup></li>
     *   <li><big>(</big><span class="matrix">A</span><sup>&#8889;</sup><big>)<sup>&#8889;</sup></big> = <span class="matrix">A</span></li>
     *   <li>.<sup>&#8889;</sup> = .<sup>T</sup> on <b>R</b><sup>n&times;m</sup></li>
     * </ul>
     * &forall;<span class="matrix">A</span>,<span class="matrix">B</span>&isin;R<sup>n&times;m</sup>,<span class="matrix">C</span>&isin;R<sup>m&times;l</sup> &forall;&lambda;&isin;R.
     * </p>
     * <p>
     * Relative to a finite orthonormal basis .<sup>&#8889;</sup> is the adjoint operator.
     * </p>
     * @return the m&times;n matrix <span class="matrix">M</span><sup>&#8889;</sup>=<sup>t</sup><span class="Matrix" style="text-decoration: overline">M</span>.
     * @postconditions RES.get(j,i) == get(i,j).conjugate() RES.dimension().width == dimension().height && RES.dimension().height == dimension().width
     * @see Complex#conjugate()
     * @see #transpose()
     */
    Matrix/*<R>*/ conjugate();
    //@TODO: introduce public Matrix re() either here, or as a more general function
    //@TODO: introduce public Matrix im()
	

    // diverse decomposition algorithms

    /**
     * Returns the pseudo inverse matrix <span class="matrix">A</span><sup>+</sup>.
     * <p>
     * The pseudo inverse of <span class="matrix">A</span>&isin;<b>R</b><sup>n&times;m</sup> is the matrix <span class="matrix">A</span><sup>+</sup>&isin;<b>R</b><sup>m&times;n</sup>
     * that satisfies
     * <ul>
     *   <li>&forall;<span class="vector">x</span>&isin;<b>R</b><sup>n</sup> <span class="matrix">A</span><sup>+</sup>&#8729;<span class="vector">x</span>&isin;NullSpace(<span class="matrix">A</span>)<sup>&#9524;</sup></li>
     *   <li>&forall;<span class="vector">x</span>&isin;<b>R</b><sup>n</sup> ||<span class="vector">x</span> - <span class="matrix">A</span>&#8729;<span class="matrix">A</span><sup>+</sup>&#8729;<span class="vector">x</span>|| minimal</li>
     * </ul>
     * It is uniquely characterized by the Penrose axioms
     * <ol>
     *   <li>(<span class="matrix">A</span><sup>+</sup>&#8729;<span class="matrix">A</span>)<sup>T</sup> = <span class="matrix">A</span><sup>+</sup>&#8729;<span class="matrix">A</span></li>
     *   <li>(<span class="matrix">A</span>&#8729;<span class="matrix">A</span><sup>+</sup>)<sup>T</sup> = <span class="matrix">A</span>&#8729;<span class="matrix">A</span><sup>+</sup></li>
     *   <li><span class="matrix">A</span><sup>+</sup>&#8729;<span class="matrix">A</span>&#8729;<span class="matrix">A</span><sup>+</sup> = <span class="matrix">A</span><sup>+</sup></li>
     *   <li><span class="matrix">A</span>&#8729;<span class="matrix">A</span><sup>+</sup>&#8729;<span class="matrix">A</span> = <span class="matrix">A</span></li>
     * </ol>
     * </p>
     * <p>
     * Let <span class="matrix">A</span> = <span class="matrix>U</span><sup>&#8889;</sup>&sdot;<span class="matrix>D</span>&sdot;<span class="matrix>V</span>
     * be the singular-value decomposition of <span class="matrix">A</span> into a
     * diagonal matrix <span class="matrix>D</span> of singular values,
     * and row-orthonormal matrices <span class="matrix>U</span>,<span class="matrix>V</span>.
     * Then
     * <center><span class="matrix">A</span><sup>+</sup> = <span class="matrix>V</span><sup>&#8889;</sup>&sdot;<span class="matrix>D</span><sup>-1</sup>&sdot;<span class="matrix>U</span></center>
     * <center>(&hArr; <span class="matrix">A</span>&sdot;<span class="matrix">A</span><sup>+</sup> - I is minimal)</center>
     * </p>
     * @return the pseudo inverse <span class="matrix">A</span><sup>+</sup>&isin;<b>R</b><sup>m&times;n</sup> of this matrix <span class="matrix">A</span>&isin;<b>R</b><sup>n&times;m</sup>.
     * @preconditions true
     * @postconditions RES.dimension().equals(transpose().dimension())
     *  	&& RES.multiply(this).transpose().equals(RES.multiply(this))
     *  	&& this.multiply(RES).transpose().equals(this.multiply(RES))
     *  	&& RES.multiply(this).multiply(RES).equals(RES)
     *  	&& this.multiply(RES).multiply(this).equals(this)
     */
    Matrix/*<R>*/ pseudoInverse();


    // Structural manipulations

    /**
     * Insert columns into this matrix.
     * @param cols a n&times;l matrix containing l columns to be added at the end.
     * @return this.
     * @preconditions dimension().height == cols.dimension().height
     * @postconditions RES == this
     *  	&& RES.dimension().height == OLD(dimension().height)
     *  	&& RES.dimension().width == OLD(dimension().width) + cols.dimension().width
     */
    Matrix/*<R>*/ insertColumns(int index, Matrix/*<R>*/ cols);

    /**
     * Insert rows into this matrix.
     * @param rows a x&times;m matrix containing x rows to be added at the end.
     * @preconditions dimension().width == rows.dimension().width
     * @return this.
     * @postconditions RES == this
     *  	&& RES.dimension().width == OLD(dimension().width)
     *  	&& RES.dimension().height == OLD(dimension().height) + rows.dimension().height
     */
    Matrix/*<R>*/ insertRows(int index, Matrix/*<R>*/ rows);

    /**
     * Append columns to this matrix.
     * @param cols a n&times;x matrix containing x columns to be added at the end.
     * @return this.
     * @preconditions dimension().height == cols.dimension().height
     * @postconditions RES == this
     *  	&& RES.dimension().height == OLD(dimension().height)
     *  	&& RES.dimension().width == OLD(dimension().width) + cols.dimension().width
     */
    Matrix/*<R>*/ insertColumns(Matrix/*<R>*/ cols);

    /**
     * Append rows to this matrix.
     * @param rows a x&times;m matrix containing x rows to be added at the end.
     * @preconditions dimension().width == rows.dimension().width
     * @return this.
     * @postconditions RES == this
     *  	&& RES.dimension().width == OLD(dimension().width)
     *  	&& RES.dimension().height == OLD(dimension().height) + rows.dimension().height
     */
    Matrix/*<R>*/ insertRows(Matrix/*<R>*/ rows);

    /**
     * Remove a column from this matrix.
     * @return this.
     * @preconditions c&isin;[0, dimension().width)
     * @postconditions RES == this
     *  	&& RES.dimension().height == OLD(dimension().height)
     *  	&& RES.dimension().width == OLD(dimension().width) - 1
     */
    Matrix/*<R>*/ removeColumn(int c);

    /**
     * Remove a row from this matrix.
     * @return this.
     * @preconditions r&isin;[0, dimension().height)
     * @postconditions RES == this
     *  	&& RES.dimension().width == OLD(dimension().width)
     *  	&& RES.dimension().height == OLD(dimension().height) - 1
     */
    Matrix/*<R>*/ removeRow(int r);

    /**
     * Returns an array containing all the elements in this matrix.
     * The first index in this array specifies the row, the second is for column.
     * @postconditions RES[i][j] == get(i, j) && RES != RES
     * @see #set(Arithmetic[][])
     * @see Object#clone()
     */
    public Arithmetic/*>R<*/[][] toArray();
}
