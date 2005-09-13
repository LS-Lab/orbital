/**
 * @(#)Vector.java 1.0 1999/03/08 Andre Platzer
 * 
 * Copyright (c) 1999-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.util.Iterator;
import java.util.ListIterator;

import java.awt.Dimension;
import java.util.NoSuchElementException;

/**
 * Represents a mathematical vector of any dimension n.
 * <p>
 * <table>
 * <tr><td rowspan="4"><span class="vector">v</span>&isin;V=R<sup>n</sup>, <span class="vector">v</span> = (v<sub>0</sub>,v<sub>1</sub>,v<sub>2</sub>,&#8230;,v<sub>n-1</sub>)<sup>T</sup> = </td>
 *     <td rowspan="4" style="font-size: 600%; font-weight: 200">(</td> <td>v<sub>0</sub></td> <td rowspan="4" style="font-size: 600%; font-weight: 200">)</td></tr>
 * <tr> <td>v<sub>1</sub></td> </tr>
 * <tr> <td>&#8942;</td> </tr>
 * <tr> <td>v<sub>n-1</sub></td> </tr>
 * </table>
 * </p>
 * <p>
 * The components v<sub>i</sub>&isin;R are any arithmetic objects forming a ring.
 * (R<sup>n</sup>,+,&middot;) form a (free) R-module
 * with the law of composition + and the law of action &middot;.
 * If R is a field the vectors even form a vector space over R, which justifies the name vector.
 * If R is an integrity domain, then R<sup>n</sup>&cong;R<sup>m</sup> &hArr; n=m.
 * </p>
 * <hr />
 * A <a href="doc-files/AlgebraicStructures.html#vectorSpace"><dfn>vector space</dfn></a> over a field R is a set V with an algebraic structure of
 * <table style="border: none; padding: 3">
 *   <tr>
 *     <td rowspan="2">(1)</td>
 *     <td>(V,+) is an Abelian group with the law of composition</td>
 *   </tr>
 *   <tr>
 *     <td>+:V×V&rarr;V; (<span class="vector">v</span>,<span class="vector">w</span>)&#8614;<span class="vector">v</span>+<span class="vector">w</span></td>
 *   </tr>
 *   <tr>
 *     <td>(2)</td>
 *     <td>&middot;:R×V&rarr;V; (&lambda;,<span class="vector">v</span>)&#8614;&lambda;&middot;<span class="vector">v</span>
 *       is a law of action (the scalar multiplication &middot; or sometimes ·)</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;(&quot;a&quot;)</td>
 *     <td>&lambda;&middot;(&mu;&middot;<span class="vector">v</span>) = (&lambda;&sdot;&mu;)&middot;<span class="vector">v</span></td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;(&quot;d&quot;)</td>
 *     <td>(&lambda;+&mu;)&middot;<span class="vector">v</span> = &lambda;&middot;<span class="vector">v</span> + &mu;&middot;<span class="vector">v</span></td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;(&quot;d&quot;)</td>
 *     <td>&lambda;&middot;(<span class="vector">v</span>+<span class="vector">w</span>) = &lambda;&middot;<span class="vector">v</span> + &lambda;&middot;<span class="vector">w</span></td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;(&quot;n&quot;)</td>
 *     <td>1&middot;<span class="vector">v</span> = <span class="vector">v</span></td>
 *     <td></td>
 *   </tr>
 * </table>
 * &forall;&lambda;,&mu;&isin;R, <span class="vector">v</span>,<span class="vector">w</span>&isin;V.
 * <p>
 * If you intend to use <em>mutable</em> arithmetic elements, note the discussion of
 * mutations per reference vs. explicit cloning in {@link Tensor#set(int[],Arithmetic)}
 * which generally holds for all operations that set component values.</p>
 * <p>
 * Also note that some few methods will change its instance and explicitly <code><span class="keyword">return</span> <span class="keyword">this</span></code>
 * to allow chaining of structural changes,
 * whilst arithmetic methods will leave a vector unchanged but return a modified version.
 * Refer to the documentation of the individual methods for details.</p>
 * 
 * @invariants super &and; rank()==1
 * @structure extends Tensor
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Values#tensor(Arithmetic[])
 * @see Values#valueOf(Arithmetic[])
 * @see Values#valueOf(double[])
 * @todo turn into a template Vector<R implements Arithmetic>
 */
public interface Vector/*<R implements Arithmetic>*/ extends Tensor/*<R>*/ {
    // get/set-methods
	
    /**
     * Returns the dimension of the vector.
     * The dimension is the number <code>n</code> of elements contained.
     */
    int dimension();

    /**
     * Returns the value at the component specified by index.
     * <p>
     * Of course, this method only has a meaning for free modules like vector spaces.
     * </p>
     */
    Arithmetic/*>R<*/ get(int i);

    /**
     * Sets a value at the component specified by index.
     * <p>
     * Of course, this method only has a meaning for free modules like vector spaces.
     * </p>
     * @throws UnsupportedOperationException if this vector is constant and does not allow modifications.
     */
    void set(int i, Arithmetic/*>R<*/ vi) throws UnsupportedOperationException;

    // iterator-views

    /**
     * Returns an iterator over all components.
     * @return an iterator that iterates over v<sub>0</sub>,&#8230;,v<sub>n-1</sub>.
     * @postconditions RES.supports(#nextIndex()} &and; RES.supports(#previousIndex()}
     *  &and; RES.supports(#add(Object)} &and; RES.supports(#remove()}
     */
    ListIterator/*_<R>_*/ iterator();
    
    // sub-views

    /**
     * Get a sub-vector view ranging (i1:i2) inclusive.
     * <p>
     * The returned vector is a structurally unmodifiable <a href="Tensor.html#view">view</a>.</p>
     * @preconditions i1<=i2 && valid(i1) && valid(i2)
     * @return a matrix view of the specified part of this matrix.
     */
    Vector/*<R>*/ subVector(int i1, int i2);

    // norm
	
    /**
     * Returns the norm || ||<sub>p</sub> of this vector.
     * <p>This method implements p-norms, where<br>
     * <span class="Formula">||<span class="vector">x</span>||<sub>p</sub> = (|x<sub>1</sub>|<sup>p</sup> + &#8230; + |x<sub>n</sub>|<sup>p</sup>)<sup>1/p</sup></span>.<br>
     * <span class="Formula">||<span class="vector">x</span>||<sub>&infin;</sub> = max {|x<sub>1</sub>|,&#8230;,|x<sub>n</sub>|}</span>.</p>
     * @preconditions p>=1
     */
    public Real norm(double p);

    // arithmetic-operations

    /**
     * Adds two vectors returning a vector.
     * @preconditions dimension() == b.dimension()
     * @postconditions RES.dimension() == dimension()
     *  	&& RES.get(i) == get(i) + b.get(i)
     * @attribute associative
     * @attribute neutral (0)
     * @attribute inverse (-v)
     * @attribute commutative
     */
    Vector/*<R>*/ add(Vector/*<R>*/ b);

    /**
     * Subtracts two vectors returning a vector.
     * @preconditions dimension() == b.dimension()
     * @postconditions RES.dimension() == dimension()
     *  	&& RES.get(i) == get(i) - b.get(i)
     * @attribute associative
     */
    Vector/*<R>*/ subtract(Vector/*<R>*/ b);

    /**
     * Scalar-dot-product &lang;&middot;,&middot;&rang;:V&times;V&rarr;F of two vectors.
     * <p>&lang;&middot;,&middot;&rang;:V&times;V&rarr;<b>R</b> is a real scalar product and V a euclidian vector space, if &forall;<span class="vector">x</span>,<span class="vector">y</span>&isin;V:
     * <table>
     *   <tr>
     *     <td>(bl)</td>
     *     <td>&lang;.,<span class="vector">y</span>&rang; and &lang;<span class="vector">x</span>,.&rang; are linear</td>
     *     <td>&quot;bilinear&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>(s)</td>
     *     <td>&lang;<span class="vector">x</span>,<span class="vector">y</span>&rang; = &lang;<span class="vector">y</span>,<span class="vector">x</span>&rang;</td>
     *     <td>&quot;symmetric&quot; (this method is commutative)</td>
     *   </tr>
     *   <tr>
     *     <td>(pdef)</td>
     *     <td>&lang;<span class="vector">x</span>,<span class="vector">x</span>&rang;&ge;0 and &lang;<span class="vector">x</span>,<span class="vector">x</span>&rang;=0 &hArr; <span class="vector">x</span>=0</td>
     *     <td>&quot;positive definite&quot;</td>
     *   </tr>
     * </table></p>
     * <p>&lang;&middot;,&middot;&rang;:V&times;V&rarr;<b>C</b> is a complex scalar product and V a unitarian vector space, if &forall;<span class="vector">x</span>,<span class="vector">y</span>&isin;V:
     * <table>
     *   <tr>
     *     <td>(ll)</td>
     *     <td>&lang;&middot;,<span class="vector">y</span>&rang; is linear</td>
     *     <td>&quot;left-linear&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>(h)</td>
     *     <td>&lang;<span class="vector">x</span>,<span class="vector">y</span>&rang; = <span style="text-decoration: overline">&lang;<span class="vector">y</span>,<span class="vector">x</span>&rang;</span></td>
     *     <td>&quot;hermite&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>(pdef)</td>
     *     <td>&lang;<span class="vector">x</span>,<span class="vector">x</span>&rang;&ge;0 and &lang;<span class="vector">x</span>,<span class="vector">x</span>&rang;=0 &hArr; <span class="vector">x</span>=0</td>
     *     <td>&quot;positive definite&quot;</td>
     *   </tr>
     * </table></p>
     * <p>
     * A scalar product &lang;&middot;,&middot;&rang; induces a norm ||.||:V&rarr;[0,&infin;); <span class="vector">x</span> &#8614; ||<span class="vector">x</span>|| := &radic;<span style="text-decoration: overline">&lang;<span class="vector">x</span>,<span class="vector">x</span>&rang;</span>
     * </p>
     * <p>
     * The standard scalar-product which will often be implemented, is<br />
     * (<span class="vector">x</span>,<span class="vector">y</span>) &#8614; &lang;<span class="vector">x</span>,<span class="vector">y</span>&rang; = <span class="vector">x</span><sup>T</sup>·<span class="vector">y</span> = <big>&sum;</big><span class="doubleIndex"><sub>i=0</sub><sup>n-1</sup></span> x<sub>i</sub>&sdot;y<sub>i</sub>.
     * It belongs to the euclidian 2-norm and is the inner product of vectors.
     * </p>
     * @preconditions dimension() == b.dimension()
     * @postconditions RES.dimension() == dimension()
     *  	&& RES == &lang;this, b&rang;
     * @see Normed
     */
    Arithmetic/*>R<*/ multiply(Vector/*<R>*/ b);

    /**
     * Multiplies a vector with a scalar returning a vector.
     * @preconditions true
     * @postconditions RES.dimension().equals(dimension())
     *  	&& RES.get(i) == s&sdot;get(i)
     * @attribute associative
     * @attribute neutral
     * @return s&middot;<span class="vector">v</span>
     */
    Vector/*<R>*/ scale(Scalar s);
    /**
     * Multiplies a vector with a scalar returning a vector.
     * @see #scale(Scalar)
     * @todo outroduce
     */
    Vector/*<R>*/ multiply(Scalar s);

    /**
     * Multiplies a vector with a matrix returning a vector.
     * If row-vector <code><span class="vector">v</span></code> is sized <code>n</code> and
     * the matrix <code class="matrix">A</code> is sized <code>n&times;m</code>,
     * the resulting row-vector <code><span class="vector">v</span>&#8729;<span class="matrix">A</span></code> is sized <code>m</code>.
     * This is an inner product.
     * @preconditions dimension() == <span class="matrix">B</span>.dimension().height
     */
    Vector/*<R>*/ multiply(Matrix/*<R>*/ B);

    /**
     * Vector-cross-product of two vectors. <br />
     * &times;:<b><b>R</b></b><sup>3</sup>&times;<b><b>R</b></b><sup>3</sup>&rarr;<b><b>R</b></b><sup>3</sup>; (<span class="vector">x</span>,<span class="vector">y</span>) &#8614; <span class="vector">x</span>&times;<span class="vector">y</span> = (x<sub>1</sub>y<sub>2</sub>-x<sub>2</sub>y<sub>1</sub>, x<sub>2</sub>y<sub>0</sub>-x<sub>0</sub>y<sub>2</sub>, x<sub>0</sub>y<sub>1</sub>-x<sub>1</sub>y<sub>0</sub>)
     * <p>
     * cross is antisymmetric: <span class="vector">y</span>&times;<span class="vector">x</span> = -(<span class="vector">x</span>&times;<span class="vector">y</span>)</p>
     * @preconditions dimension() == 3 && dimension() == b.dimension()
     * @postconditions RES.multiply(this) == 0 && RES.multiply(b) == 0.
     * @return the cross-product vector which will be orthogonal on this and b.
     *  <span class="vector">x</span>&times;<span class="vector">y</span> &perp; <span class="vector">x</span> &and; <span class="vector">x</span>&times;<span class="vector">y</span> &perp; <span class="vector">y</span>.
     * @preconditions (dimension() == 3 || dimension() == 2) && dimension() == b.dimension()
     * @attribute antisymmetric
     */
    Vector/*<R>*/ cross(Vector/*<R>*/ b);

    // operations on vectors
	
    /**
     * Returns this vector transposed.
     * Also distinguished via vector.multiply(matrix) or matrix.multiply(vector) instead.
     * @see Matrix#transpose()
     */
    Matrix/*<R>*/ transpose();


    // Structural modifications

    /**
     * Insert a value into this vector at the specified index.
     * @preconditions 0<=index && index<=dimension()
     * @return this.
     * @postconditions RES == this
     *  	&& RES.dimension() == OLD(dimension()) + 1
     */
    Vector/*<R>*/ insert(int index, Arithmetic/*>R<*/ v);
 
    /**
     * Insert all components of a vector into this vector at the specified index.
     * @preconditions 0<=index && index<=dimension()
     * @return this.
     * @postconditions RES == this
     *  	&& RES.dimension() == OLD(dimension()) + <span class="vector">v</span>.dimension()
     */
    Vector/*<R>*/ insertAll(int index, Vector/*<R>*/ v);

    /**
     * Append a value to this vector.
     * @return this.
     * @postconditions RES == this
     *  	&& RES.dimension() == OLD(dimension()) + 1
     */
    Vector/*<R>*/ insert(Arithmetic/*>R<*/ v);

    /**
     * Append all components of a vector to this vector.
     * @return this.
     * @postconditions RES == this
     *  	&& RES.dimension() == OLD(dimension()) + <span class="vector">v</span>.dimension()
     */
    Vector/*<R>*/ insertAll(Vector/*<R>*/ v);

    /**
     * Remove the component at an index from this vector.
     * @return this.
     * @postconditions RES == this
     *  	&& RES.dimension() == OLD(dimension()) - 1
     */
    Vector/*<R>*/ remove(int index);

    /**
     * Returns an array containing all the elements in this vector.
     * @postconditions RES[i] == get(i) && RES != RES
     * @see #set(Arithmetic[])
     * @see Object#clone()
     */
    public Arithmetic/*>R<*/[] toArray();
}
