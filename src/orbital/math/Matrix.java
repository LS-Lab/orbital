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
 * R<sup>n&times;m</sup> = { M=<big>(</big>m<sub>i,j</sub><big>)</big> &brvbar; m<sub>i,j</sub>&isin;R} &cong; R<sup>n</sup> &otimes;<sub>R</sub> R<sup>m</sup> where
 * <table>
 *   <tr><td rowspan="4">M = <big>(</big>m<sub>i,j</sub><big>)</big> = </td>
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
 * the square matrices (R<sup>n&times;n</sup>,+,&lowast;,&middot;) form a
 * unital, associative R-<a href="doc-files/AlgebraicStructures.html#laws_2_1">algebra</a>
 * with the laws of composition + and &middot; and the law of action &lowast;.
 * But it is non-commutative and has zero divisors for n&ge;2.
 * The general matrices (R<sup>n&times;m</sup>,+,&lowast;) form an
 * R-<a href="doc-files/AlgebraicStructures.html#laws_1_1">module</a>,
 * at least,
 * with the law of composition + and the law of action &lowast;.
 * If R is a field, the matrices even form an R-vector space.
 * </p>
 * <p>
 * If you intend to use <em>mutable</em> arithmetic elements, note the discussion of
 * mutations per reference vs. explicit cloning in {@link #set(int,int,Arithmetic)}
 * which generally holds for all operations setting elements.
 * </p>
 * <p>
 * Also note that some few methods will change its instance and explicitly <code><span class="keyword">return</span> <span class="keyword">this</span></code>
 * to allow chaining of structural changes,
 * whilst arithmetic methods will leave a matrix unchanged but return a modified version.
 * Refer to the documentation of the individual methods for details.
 * </p>
 * 
 * @invariant succeedes(#clone()) &and; (overwrites(#clone()) &or; this implements Cloneable) &and; rank() == 2
 * @structure extends Tensor
 * @version 1.0, 2000/08/08
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
     * Returns the value at a position (i|j).
     * <p>
     * Of course, this method only has a meaning for free modules like vector spaces.
     * </p>
     * @param i the row of the value to get.
     * @param j the column of the value to get.
     * @return m<sub>i,j</sub>.
     */
    Arithmetic/*>R<*/ get(int i, int j);

    /**
     * Sets a value at a position (i|j).
     * <p>
     * Of course, this method only has a meaning for free modules like vector spaces.
     * </p>
     * @param i the row of the value to set.
     * @param j the column of the value to set.
     * @param m the value to set for the element m<sub>i,j</sub> at position (i|j)
     *  Depending upon the implementation, the value m might be kept per reference, if possible.
     *  So if m is <em>mutable</em>, any changes to it could occur at the matrix, rendering it useless.
     *  Since whether m is kept per reference is a matter of performance and subject to
     *  free interpretation by the implementation class, you should not rely on such behaviour
     *  to provoke inner state changes. Instead you should carefully avoid setting a value
     *  by reference without cloning, if you intend to change its state, afterwards.
     *  Explicitly cloning m prior to setting it as an element of this matrix is always safe,
     *  as well as not keeping additional references to it.
     *  If m is <em>immutable</em> (which should be the usual case), no such sources of
     *  mistakes exist.
     * @throws UnsupportedOperationException if this matrix is constant and does not allow this operation.
     * @see #modCount
     */
    void set(int i, int j, Arithmetic/*>R<*/ m) throws UnsupportedOperationException;

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
     * Returns an iterator over all elements, row-wise.
     * <p>
     * If you cannot avoid it, call transpose().iterator() to get an iterator over all elements, column by column.</p>
     * @return an iterator that iterates over {m<sub>0,0</sub>,&#8230;,m<sub>0,m-1</sub>, m<sub>1,0</sub>,&#8230;,m<sub>1,m-1</sub>,&#8230;, m<sub>n-1,0</sub>,&#8230;,m<sub>n-1,m-1</sub>}.
     */
    Iterator/*_<R>_*/ iterator();

    // sub-views
	
    /**
     * Returns the column vector view of a column.
     * The returned vector is backed by this matrix, so changes in the returned vector are reflected in this matrix, and vice-versa.
     * <p>
     * For M<sub>i,j</sub>, bindSecond(c).</p>
     * @return a vector view of the specified column M<sub>(0:n-1,c:c)</sub> in this matrix.
     * @see orbital.logic.functor.Functionals#bindSecond(orbital.logic.functor.BinaryFunction, Object)
     */
    Vector/*<R>*/ getColumn(int c);

    /**
     * Sets the column vector at a column.
     * @pre col.dimension() == dimension().height
     * @see #modCount
     */
    void setColumn(int c, Vector/*<R>*/ col) throws UnsupportedOperationException;

    /**
     * Returns the row vector view of a row.
     * The returned vector is backed by this matrix, so changes in the returned vector are reflected in this matrix, and vice-versa.
     * <p>
     * For M<sub>i,j</sub> bindFirst(r).</p>
     * @return a vector view of the specified row M<sub>(r:r,0:m-1)</sub> in this matrix.
     * @todo document and specify whether the return-value is a clone or a reference to the row vector, etc.
     * @see orbital.logic.functor.Functionals#bindFirst(orbital.logic.functor.BinaryFunction, Object)
     */
    Vector/*<R>*/ getRow(int r);

    /**
     * Sets the row vector at a row.
     * @pre row.dimension() == dimension().width
     * @see #modCount
     */
    void setRow(int r, Vector/*<R>*/ row) throws UnsupportedOperationException;

    /**
     * Get a sub-matrix view ranging (i1:i2,j1:j2) inclusive.
     * This is Moeler notation.
     * The returned matrix is backed by this matrix, so changes in the returned matrix are reflected in this matrix, and vice-versa.
     * <p>
     * The semantics of the matrix returned by this method become undefined if the backing matrix
     * (i.e., this object) is <em>structurally modified</em> in any way other than via the returned matrix.
     * (Structural modifications are those that change the dimension, or otherwise perturb it in
     * such a fashion that iterations in progress may yield incorrect results.)
     * </p>
     * @pre i1&le;i2 && j1&le;j2 && valid(i1, j1) && valid(i2, j2)
     * @param i1 the top-most row index of the sub matrix view to get.
     * @param i2 the bottom-most row index of the sub matrix view to get.
     * @param j1 the left-most column index of the sub matrix view to get.
     * @param j2 the right-most column index of the sub matrix view to get.
     * @return a matrix view of the specified part of this matrix.
     * <table>
     *   <tr><td rowspan="3">M<sub>(i1:i2,j1:j2)</sub> = <big>(</big>m<sub>i,j</sub><big>)</big><sub>i&isin;{i1,&#8230;,i2},j&isin;{j1,&#8230;,j2}</sub> = </td>
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
     * @pre isSquare()
     * @todo should we turn this into a view as well?
     */
    Vector/*<R>*/ getDiagonal();

    /**
     * Checks whether this matrix is a square matrix of size n&times;n.
     * @pre true
     * @post RES == (dimension().width == dimension().height)
     */
    boolean isSquare();

    /**
     * Checks whether this square matrix is symmetric.
     * Symmetric matrices are those that satisfy M<sup>T</sup> = M
     * alias m<sub>i,j</sub>=m<sub>j,i</sub> &forall;i,j&isin;<b>N</b>.
     * <p>
     * In <b>R</b><sup>n&times;m</sup>, symmetric is the same as self-adjoint.
     * </p>
     * @pre isSquare()
     * @post RES.equals(transpose().equals(this))
     * @throws ArithmeticException if this is not a square matrix since only square matrices can be symmetric.
     */
    boolean isSymmetric() throws ArithmeticException;

    /**
     * Checks whether this squary matrix is regular.
     * Regular matrices are those that are invertible,
     * which are those with invertible determinant.
     * @return <code>true</code> if this matrix is regular (thereby invertible) and <code>false</code> if it is singular (linear Rank&lt;n).
     * @pre isSquare()
     * @post RES &hArr; det()&isin;R<sup>&times;</sup>
     * @throws ArithmeticException if this is not a square matrix since only square matrices can be regular.
     */
    boolean isRegular() throws ArithmeticException;

    /**
     * Checks how definite this square matrix is.
     * <p>
     * f.ex. a symmetric matrix is positive definite iff every main minor is &gt; 0.
     * i.e. &forall;i&isin;{0,&#8230;,n-1} det A(0:i,0:i) &gt; 0.
     * This means that every square submatrix that includes the element a<sub>0,0</sub>
     * has a strictly positive determinant.
     * </p>
     * <p>
     * C.f. Sylvester-normal form of quadratic forms.
     * </p>
     * @pre isSquare() &and; <span class="@todo">isSymmetric()?</span>
     * @return
     *  <ul>
     *    <li>d&gt;0 if this matrix is positive definite, i.e. &forall;x&isin;V&#8726;{0} &lang;x,A&lowast;x&rang; &gt; 0.</li>
     *    <li>d&ge;0 if this matrix is positive semi-definite, i.e. &forall;x&isin;V &lang;x,A&lowast;x&rang; &ge; 0.</li>
     *    <li>d=0 if this matrix is indefinite, i.e. &exist;x&isin;V &lang;x,A&lowast;x&rang; &gt;0 &and; &exist;y&isin;V &lang;y,A&lowast;y&rang; &lt;0.</li>
     *    <li>d&le;0 if this matrix is negative semi-definite, i.e. &forall;x&isin;V &lang;x,A&lowast;x&rang; &le; 0.</li>
     *    <li>d&lt;0 if this matrix is negative definite, i.e. &forall;x&isin;V&#8726;{0} &lang;x,A&lowast;x&rang; &lt; 0.</li>
     *  </ul>
     * @throws ArithmeticException if this is not a square matrix since only square matrices can be symmetric.
     * @todo does A need to be symmetric? And improve docu
     */
    int isDefinite() throws ArithmeticException;
	
    /**
     * Rank of this matrix.
     * i.e. the maximum number of column vectors (or row vectors) that are linear independent.
     * @return (linear) rank M := dim<sub>K</sub>(im(M)).
     * @see Tensor#rank()
     */
    int linearRank();


    /**
     * Returns the norm || ||<sub>p</sub> of this matrix.
     * <p>
     * ||.||:<b>C</b><sup>n&times;m</sup>&rarr;[0,&infin;) is a <dfn>matrix norm</dfn> or consistent if it is a (vector) norm and
     * <ul>
     *   <li>&forall;x&isin;<b>C</b><sup>n&times;m</sup>,y&isin;<b>C</b><sup>m&times;l</sup> ||x&sdot;y|| &le; ||x||&sdot;||y||	(sub multiplicative)
     * </ul>
     * ||.|| is <dfn>compatible</dfn> or conform with the vector norms ||.||<sub>C<sup>n</sup></sub>, ||.||<sub>C<sup>m</sup></sub> if
     * <ul>
     *   <li>&forall;A&isin;<b>C</b><sup>n&times;m</sup>, x&isin;<b>C</b><sup>m</sup> ||Ax||<sub>C<sup>n</sup></sub> &le; ||A||&sdot;||x||<sub>C<sup>m</sup></sub>
     * </ul>
     * ||.|| is induced by the vector norms ||.||<sub>C<sup>n</sup></sub>, ||.||<sub>C<sup>m</sup></sub> if
     * <ul>
     *   <li>&forall;A&isin;<b>C</b><sup>n&times;m</sup>, x&isin;<b>C</b><sup>m</sup> ||A|| = max {||Ax||<sub>C<sup>n</sup></sub> / ||x||<sub>C<sup>m</sup></sub> &brvbar; x&ne;0} = max {||Ax||<sub>C<sup>n</sup></sub> &brvbar; ||x||<sub>C<sup>m</sup></sub>=1}
     * </ul>
     * which is a measure for how much A stretches vectors.
     * </p>
     * <p>This method should implement induced p-norms, where
     * <ul>
     *   <li>||A||<sub>1</sub> = max {&sum;<span class="doubleIndex"><sub>i=1</sub><sup>n</sup></span>|a<sub>ij</sub>| &brvbar; 1&le;j&le;m} is the norm of column sums.</li>
     *   <li>||A||<sub>&infin;</sub> = max {&sum;<span class="doubleIndex"><sub>j=1</sub><sup>m</sup></span>|a<sub>ij</sub>| &brvbar; 1&le;i&le;n} is the norm of row sums.</li>
     *   <li>||A||<sub>2</sub> = sqrt max Eigenvalues(A<sup>*</sup>&middot;A) is the spectral norm. (optional operation)</li>
     * </ul>
     * </p>
     * @todo document again, the norms implemented by AbstractMatrix.
     * @pre p>=1
     */
    Real norm(double p);

    /**
     * Returns the trace of the matrix representation.<p>
     * The trace is invariant for similar matrices: <code>Tr (T<sup>-1</sup>&middot;A&middot;T) = Tr A</code>.
     * @return sum of the main-diagonal-vectors components.
     * @pre isSquare()
     * @throws ArithmeticException if this is not a square matrix, since only square matrix have a trace.
     */
    Arithmetic/*>R<*/ trace() throws ArithmeticException;

    /**
     * Returns the determinant of the matrix representation. The determinant is useful to determine if the
     * matrix can be inverted. The determinant is denoted as det M = |M|.
     * It is the universal alternating mapping R<sup>n&times;</sup>&cong;(R<sup>n</sup>)<sup>n</sup>&rarr;&Lambda;<sup>n</sup>(R<sup>n</sup>)&cong;R
     * of the exterior product &Lambda;<sup>n</sup>(R<sup>n</sup>).
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
     *         <td rowspan="5">&alpha;&lowast;det</td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">[</td>
     *         <td align="center">a<sub>0</sub></td>
     *         <td rowSpan="5" style="FONT-SIZE: 600%; FONT-WEIGHT: 200">]</td>
     *         <td rowspan="5">+</td>
     *         <td rowspan="5">&beta;&lowast;det</td>
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
     *     <td>rank M &lt; n &hArr; det(M)=0</td>
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
     *     <td>det(M<sup>T</sup>) = det(M)</td>
     *     <td>&quot;invariant to transposition&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>( )</td>
     *     <td>det(M&middot;N) = det(M)&sdot;det(N)</td>
     *     <td>&nbsp;</td>
     *   </tr>
     *   <tr>
     *     <td>( )</td>
     *     <td>M&isin;GL(n,R) &hArr; det(M)&isin;R<sup>&times;</sup><br />
     *     &rArr;det(M<sup>-1</sup>) = det(M)<sup>-1</sup>
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
     * The determinant is invariant for similar matrices: <code>det (T<sup>-1</sup>&middot;A&middot;T) = det A</code>.
     * If the determinant is non-zero, then this matrix is regular and invertible.
     * However, if the determinant is approximately zero then inverse transform operations might not carry enough precision to produce meaningful results.</p>
     * <p>
     * (det A)' = &sum;<span class="doubleIndex"><sub>i=0</sub><sup>n-1</sup></span> det <big>(</big>a<sub>0</sub>&#8230;a<sub>i</sub>'&#8230;a<sub>n-1</sub><big>)</big>
     * where a<sub>i</sub> = (a<sub>0,i</sub>,&#8230;,a<sub>n-1,i</sub>)<sup>t</sup> is the i-th column of A.
     * </p>
     * @return det A = |A|
     * @pre isSquare()
     * @post det() multilinear
     *  	&& (rank() &lt; dimension().width &hArr; det() == 0)
     *  	&& IDENTITY(n).det() == 1
     * @todo document determinant properties and uniqueness
     * @throws ArithmeticException if this is not a square matrix, since determinant is only defined for square matrices.
     * @todo should R be a commutative ring with one?
     */
    Arithmetic/*>R<*/ det() throws ArithmeticException;

    // arithmetic-operations
	
    /**
     * Adds two matrices returning a matrix.
     * Associative, neutral element O, inverse -A, commutative.
     * @pre dimension().equals(B.dimension())
     * @post RES.dimension().equals(dimension())
     *  	&& RES.get(i, j) == get(i,j) + B.get(i,j)
     * @attribute associative
     * @attribute neutral (0)
     * @attribute inverse (-A)
     * @attribute commutative
     */
    Matrix/*<R>*/ add(Matrix/*<R>*/ B);

    /**
     * Subtracts two matrices returning a matrix.
     * @pre dimension().equals(B.dimension())
     * @post RES.dimension().equals(dimension())
     *  	&& RES.get(i, j) == get(i,j) - B.get(i,j)
     * @attribute associative
     */
    Matrix/*<R>*/ subtract(Matrix/*<R>*/ B);
    /**
     * Multiplies two matrices returning a matrix.
     * If A&isin;R<sup>n&times;m</sup> and
     * B&isin;R<sup>m&times;l</sup> the resulting matrix
     * A&middot;B&isin;R<sup>n&times;l</sup>.
     * This is the ring multiplication.
     * @return the n&times;l matrix A&middot;B.
     * @pre dimension().width == B.dimension().height
     * @post RES.dimension().height == dimension().height && RES.dimension().width == B.dimension().width
     *  	&& RES.get(i, j) == getRow(i) &sdot; B.getColumn(j)
     * @attribute associative
     * @attribute neutral (I)
     */
    Matrix/*<R>*/ multiply(Matrix/*<R>*/ B);

    /**
     * Multiplies a matrix with a scalar returning a matrix.
     * This is the scalar multiplication.
     * @pre true
     * @post RES.dimension().equals(dimension())
     *  	&& RES.get(i, j) == s &sdot; get(i,j)
     * @return s&lowast;A
     * @todo multiply(Arithmetic) would need to determine type of s deep, in case it is not a scalar, but a matrix, or anything in R. Except if we separated ring and scalar (and perhaps tensor) multiplication by name.
     */
    Matrix/*<R>*/ multiply(Scalar s);
    Matrix/*<R>*/ scale(Scalar s);

    /**
     * Multiplies a matrix with a vector returning a vector.
     * If A&isin;R<sup>n&times;m</sup> and
     * v&isin;R<sup>m</sup> is a column vector of dimension m,
     * the resulting column vector A&middot;v&isin;R<sup>n</sup> has dimension n.
     * @todo is this a tensor product, or a faltung?
     * @return the n-dimensional column vector A&middot;v.
     * @pre dimension().width == B.dimension()
     * @post RES.dimension() == dimension().height
     *  	&& RES.get(i) == getRow(i) &sdot; B
     */
    Vector/*<R>*/ multiply(Vector/*<R>*/ B);

    // operations on matrices

    /**
     * Returns this matrix transposed.<br>
     * .<sup>T</sup>:R<sup>n&times;m</sup>&rarr;R<sup>m&times;n</sup>; M&#8614;M<sup>T</sup>:=(t<sub>i,j</sub>) with t<sub>i,j</sub> = m<sub>j,i</sub>.
     * <p>
     * <ul id="Properties">
     *   <li>(A+B)<sup>T</sup> = A<sup>T</sup> + B<sup>T</sup></li>
     *   <li>(&lambda;&middot;A)<sup>T</sup> = &lambda;&middot;A<sup>T</sup></li>
     *   <li>(A&middot;C)<sup>T</sup> = C<sup>T</sup>&middot;A<sup>T</sup></li>
     *   <li><big>(</big>A<sup>T</sup><big>)<sup>T</sup></big> = A</li>
     * </ul>
     * &forall;A,B&isin;R<sup>n&times;m</sup>,C&isin;R<sup>m&times;l</sup> &lambda;&isin;R
     * </p>
     * @return the m&times;n matrix M<sup>T</sup>=(t<sub>i,j</sub>) with elements t<sub>i,j</sub> = m<sub>j,i</sub>.
     * @post RES.get(j,i) == get(i,j) && RES.dimension().width == dimension().height && RES.dimension().height == dimension().width
     * @see #conjugate()
     */
    Matrix/*<R>*/ transpose();

    /**
     * Returns this matrix conjugate transposed.<br>
     * .<sup>&#8889;</sup>:R<sup>n&times;m</sup>&rarr;R<sup>m&times;n</sup>; M&#8614;M<sup>&#8889;</sup>:=<span style="text-decoration: overline">M</span><sup>T</sup> = (t<sub>i,j</sub>) with t<sub>i,j</sub> = <span style="text-decoration: overline">m</span><sub>j,i</sub>.
     * <p>
     * <ul id="Properties">
     *   <li>(A+B)<sup>&#8889;</sup> = A<sup>&#8889;</sup> + B<sup>&#8889;</sup></li>
     *   <li>(&lambda;&middot;A)<sup>&#8889;</sup> = <span style="text-decoration: overline">&lambda;</span>&middot;A<sup>&#8889;</sup> = &lambda;<sup>*</sup>&middot;A<sup>&#8889;</sup></li>
     *   <li>(A&middot;C)<sup>&#8889;</sup> = C<sup>&#8889;</sup>&middot;A<sup>&#8889;</sup></li>
     *   <li><big>(</big>A<sup>&#8889;</sup><big>)<sup>&#8889;</sup></big> = A</li>
     *   <li>.<sup>&#8889;</sup> = .<sup>T</sup> on <b>R</b><sup>n&times;m</sup></li>
     * </ul>
     * &forall;A,B&isin;R<sup>n&times;m</sup>,C&isin;R<sup>m&times;l</sup>, &lambda;&isin;R.
     * </p>
     * <p>
     * Relative to a finite orthonormal basis .<sup>&#8889;</sup> is the adjoint operator.
     * </p>
     * @return the m&times;n matrix M<sup>&#8889;</sup>=<span style="text-decoration: overline">M</span><sup>T</sup>.
     * @post RES.get(j,i) == conjugate(get(i,j)) RES.dimension().width == dimension().height && RES.dimension().height == dimension().width
     * @see Complex#conjugate()
     * @see #transpose()
     */
    Matrix/*<R>*/ conjugate();
    //@TODO: introduce public Matrix re() either here, or as a more general function
    //@TODO: introduce public Matrix im()
	

    // diverse decomposition algorithms

    /**
     * Returns the pseudo inverse matrix A<sup>+</sup>.
     * <p>
     * The pseudo inverse of A&isin;<b>R</b><sup>n&times;m</sup> is the matrix A<sup>+</sup>&isin;<b>R</b><sup>m&times;n</sup>
     * that satisfies
     * <ul>
     *   <li>&forall;x&isin;<b>R</b><sup>n</sup> A<sup>+</sup>&middot;x&isin;NullSpace(A)<sup>&#9524;</sup></li>
     *   <li>&forall;x&isin;<b>R</b><sup>n</sup> ||x - A&middot;A<sup>+</sup>&middot;x|| minimal</li>
     * </ul>
     * It is uniquely characterized by the Penrose axioms
     * <ol>
     *   <li>(A<sup>+</sup>&middot;A)<sup>T</sup> = A<sup>+</sup>&middot;A</li>
     *   <li>(A&middot;A<sup>+</sup>)<sup>T</sup> = A&middot;A<sup>+</sup></li>
     *   <li>A<sup>+</sup>&middot;A&middot;A<sup>+</sup> = A<sup>+</sup></li>
     *   <li>A&middot;A<sup>+</sup>&middot;A = A</li>
     * </ol>
     * </p>
     * <p>
     * Let A = U<sup>&#8889;</sup>&sdot;D&sdot;V be the singular-value decomposition of A into a
     * diagonal matrix D of singular values and row orthonormal matrices U,V.
     * Then
     * <center>A<sup>+</sup> = V<sup>&#8889;</sup>&sdot;D<sup>-1</sup>&sdot;U</center>
     * <center>(&hArr; A&sdot;A<sup>+</sup> - I is minimal)</center>
     * </p>
     * @return the pseudo inverse A<sup>+</sup>&isin;<b>R</b><sup>m&times;n</sup> of this matrix A&isin;<b>R</b><sup>n&times;m</sup>.
     * @pre true
     * @post RES.dimension().equals(transpose().dimension())
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
     * @pre dimension().height == cols.dimension().height
     * @post RES == this
     *  	&& RES.dimension().height == OLD(dimension().height)
     *  	&& RES.dimension().width == OLD(dimension().width) + cols.dimension().width
     */
    Matrix/*<R>*/ insertColumns(int index, Matrix/*<R>*/ cols);

    /**
     * Insert rows into this matrix.
     * @param rows a x&times;m matrix containing x rows to be added at the end.
     * @pre dimension().width == rows.dimension().width
     * @return this.
     * @post RES == this
     *  	&& RES.dimension().width == OLD(dimension().width)
     *  	&& RES.dimension().height == OLD(dimension().height) + rows.dimension().height
     */
    Matrix/*<R>*/ insertRows(int index, Matrix/*<R>*/ rows);

    /**
     * Append columns to this matrix.
     * @param cols a n&times;x matrix containing x columns to be added at the end.
     * @return this.
     * @pre dimension().height == cols.dimension().height
     * @post RES == this
     *  	&& RES.dimension().height == OLD(dimension().height)
     *  	&& RES.dimension().width == OLD(dimension().width) + cols.dimension().width
     */
    Matrix/*<R>*/ insertColumns(Matrix/*<R>*/ cols);

    /**
     * Append rows to this matrix.
     * @param rows a x&times;m matrix containing x rows to be added at the end.
     * @pre dimension().width == rows.dimension().width
     * @return this.
     * @post RES == this
     *  	&& RES.dimension().width == OLD(dimension().width)
     *  	&& RES.dimension().height == OLD(dimension().height) + rows.dimension().height
     */
    Matrix/*<R>*/ insertRows(Matrix/*<R>*/ rows);

    /**
     * Remove a column from this matrix.
     * @return this.
     * @pre c&isin;[0, dimension().width)
     * @post RES == this
     *  	&& RES.dimension().height == OLD(dimension().height)
     *  	&& RES.dimension().width == OLD(dimension().width) - 1
     */
    Matrix/*<R>*/ removeColumn(int c);

    /**
     * Remove a row from this matrix.
     * @return this.
     * @pre r&isin;[0, dimension().height)
     * @post RES == this
     *  	&& RES.dimension().width == OLD(dimension().width)
     *  	&& RES.dimension().height == OLD(dimension().height) - 1
     */
    Matrix/*<R>*/ removeRow(int r);

    /**
     * Returns an array containing all the elements in this matrix.
     * The first index in this array specifies the row, the second is for column.
     * @post RES[i][j] == get(i, j) && RES != RES
     * @see #set(Arithmetic[][])
     * @see Object#clone()
     */
    public Arithmetic/*>R<*/[][] toArray();
}
