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
 * <tr><td rowspan="4">v&isin;V=R<sup>n</sup>, v = (v<sub>0</sub>,v<sub>1</sub>,v<sub>2</sub>,&#8230;,v<sub>n-1</sub>)<sup>T</sup> = </td>
 *     <td rowspan="4" style="font-size: 600%; font-weight: 200">(</td> <td>v<sub>0</sub></td> <td rowspan="4" style="font-size: 600%; font-weight: 200">)</td></tr>
 * <tr> <td>v<sub>1</sub></td> </tr>
 * <tr> <td>&#8942;</td> </tr>
 * <tr> <td>v<sub>n-1</sub></td> </tr>
 * </table>
 * </p>
 * <p>
 * The components v<sub>i</sub>&isin;R are any arithmetic objects forming a ring.
 * (R<sup>n</sup>,+,&lowast;) form a (free) R-module
 * with the law of composition + and the law of action &lowast;.
 * If R is a field the vectors even form a vector space over R, which justifies the name vector.
 * </p>
 * <hr />
 * A <a href="doc-files/AlgebraicStructures.html#vectorSpace"><dfn>vector space</dfn></a> over a field R is a set V with an algebraic structure of
 * <table style="border: none; padding: 3">
 *   <tr>
 *     <td>(1)</td>
 *     <td>(V,+) is a commutative group with the law of composition</td>
 *     <td>+:V×V&rarr;V; (v,w)&#8614;v+w</td>
 *   </tr>
 *   <tr>
 *     <td>(2)</td>
 *     <td>&lowast; is a law of action (the scalar multiplication &lowast; or sometimes ·)</td>
 *     <td>&lowast;:R×V&rarr;V; (&lambda;,v)&#8614;&lambda;&lowast;v</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;(&quot;a&quot;)</td>
 *     <td>&lambda;&lowast;(&mu;&lowast;v) = (&lambda;&sdot;&mu;)&lowast;v</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;(&quot;d&quot;)</td>
 *     <td>(&lambda;+&mu;)&lowast;v = &lambda;&lowast;v + &mu;&lowast;v</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;(&quot;d&quot;)</td>
 *     <td>&lambda;&lowast;(v+w) = &lambda;&lowast;v + &lambda;&lowast;w</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;(&quot;n&quot;)</td>
 *     <td>1&lowast;v = v</td>
 *     <td></td>
 *   </tr>
 * </table>
 * &forall;&lambda;,&mu;&isin;R, v,w&isin;V.
 * <p>
 * If you intend to use <em>mutable</em> arithmetic elements, note the discussion of
 * mutations per reference vs. explicit cloning in {@link Matrix#set(int,int,Arithmetic)}
 * which generally holds for all operations setting elements.</p>
 * <p>
 * Also note that some few methods will change its instance and explicitly <code><span class="keyword">return</span> <span class="keyword">this</span></code>
 * to allow chaining of structural changes,
 * whilst arithmetic methods will leave a vector unchanged but return a modified version.
 * Refer to the documentation of the individual methods for details.</p>
 * 
 * @invariant succeedes(#clone()) &and; (overwrites(#clone()) &or; this implements Cloneable) &and; rank()==1
 * @structure extends Tensor
 * @version 1.0, 2000/08/08
 * @author  Andr&eacute; Platzer
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
     * returns the value at component specified by index.
     */
    Arithmetic/*>R<*/ get(int i);

    /**
     * sets a value at component specified by index.
     * @throws UnsupportedOperationException if this vector is constant and does not allow modifications.
     */
    void set(int i, Arithmetic/*>R<*/ vi) throws UnsupportedOperationException;

    // iterator-views

    /**
     * Returns an iterator over all elements.
     * @return an iterator that iterates over v<sub>0</sub>,&#8230;,v<sub>n-1</sub>.
     * @post RES instanceof {@link ListIterator}
     * @todo covariant return-types.
     */
    Iterator/*_<R>_*/ iterator();
    
    // sub-views

    /**
     * Get a sub-vector view ranging (i1:i2) inclusive.
     * The returned vector is backed by this vector, so changes in the returned vector are reflected in this vector, and vice-versa.
     * <p>
     * The semantics of the vector returned by this method become undefined if the backing vector
     * (i.e., this object) is <em>structurally modified</em> in any way other than via the returned vector.
     * (Structural modifications are those that change the dimension, or otherwise perturb it in
     * such a fashion that iterations in progress may yield incorrect results.)</p>
     * @pre i1<=i2 && valid(i1) && valid(i2)
     * @return a matrix view of the specified part of this matrix.
     */
    Vector/*<R>*/ subVector(int i1, int i2);

    // norm
	
    /**
     * Returns the norm || ||<sub>p</sub> of this vector.
     * <p>This method implements p-norms, where<br>
     * <span class="Formula">||x||<sub>p</sub> = (|x<sub>1</sub>|<sup>p</sup> + &#8230; + |x<sub>n</sub>|<sup>p</sup>)<sup>1/p</sup></span>.<br>
     * <span class="Formula">||x||<sub>&infin;</sub> = max {|x<sub>1</sub>|,&#8230;,|x<sub>n</sub>|}</span>.</p>
     * @pre p>=1
     */
    public Real norm(double p);

    // arithmetic-operations

    /**
     * Adds two vectors returning a vector.
     * @pre dimension() == b.dimension()
     * @post RES.dimension() == dimension()
     *  	&& RES.get(i) == get(i) + b.get(i)
     * @attribute associative
     * @attribute neutral (0)
     * @attribute inverse (-v)
     * @attribute commutative
     */
    Vector/*<R>*/ add(Vector/*<R>*/ b);

    /**
     * Subtracts two vectors returning a vector.
     * @pre dimension() == b.dimension()
     * @post RES.dimension() == dimension()
     *  	&& RES.get(i) == get(i) - b.get(i)
     * @attribute associative
     */
    Vector/*<R>*/ subtract(Vector/*<R>*/ b);

    /**
     * Scalar-dot-product &lang;&middot;,&middot;&rang;:V&times;V&rarr;F of two vectors.
     * <p>&lang;&middot;,&middot;&rang;:V&times;V&rarr;<b>R</b> is a real scalar product and V a euclidian vector space, if &forall;x,y&isin;V:
     * <table>
     *   <tr>
     *     <td>(bl)</td>
     *     <td>&lang;.,y&rang; and &lang;x,.&rang; are linear</td>
     *     <td>&quot;bilinear&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>(s)</td>
     *     <td>&lang;x,y&rang; = &lang;y,x&rang;</td>
     *     <td>&quot;symmetric&quot; (this method is commutative)</td>
     *   </tr>
     *   <tr>
     *     <td>(pdef)</td>
     *     <td>&lang;x,x&rang;&ge;0 and &lang;x,x&rang;=0 &hArr; x=0</td>
     *     <td>&quot;positive definite&quot;</td>
     *   </tr>
     * </table></p>
     * <p>&lang;&middot;,&middot;&rang;:V&times;V&rarr;<b>C</b> is a complex scalar product and V a unitarian vector space, if &forall;x,y&isin;V:
     * <table>
     *   <tr>
     *     <td>(ll)</td>
     *     <td>&lang;&middot;,y&rang; is linear</td>
     *     <td>&quot;left-linear&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>(h)</td>
     *     <td>&lang;x,y&rang; = <span style="text-decoration: overline">&lang;y,x&rang;</span></td>
     *     <td>&quot;hermite&quot;</td>
     *   </tr>
     *   <tr>
     *     <td>(pdef)</td>
     *     <td>&lang;x,x&rang;&ge;0 and &lang;x,x&rang;=0 &hArr; x=0</td>
     *     <td>&quot;positive definite&quot;</td>
     *   </tr>
     * </table></p>
     * <p>
     * A scalar product &lang;&middot;,&middot;&rang; induces a norm ||.||:V&rarr;[0,&infin;); x &#8614; ||x|| := &radic;<span style="text-decoration: overline">&lang;x,x&rang;</span>
     * </p>
     * <p>
     * The standard scalar-product which will often be implemented, is<br />
     * (x,y) &#8614; &lang;x,y&rang; = = x<sup>T</sup>·y = <big>&sum;</big><span class="doubleIndex"><sub>i=0</sub><sup>n-1</sup></span> x<sub>i</sub>&sdot;y<sub>i</sub>.
     * It belongs to the euclidian 2-norm and is the inner product of vectors.
     * </p>
     * @pre dimension() == b.dimension()
     * @post RES.dimension() == dimension()
     *  	&& RES == &lang;this, b&rang;
     * @see Normed
     */
    Arithmetic/*>R<*/ multiply(Vector/*<R>*/ b);

    /**
     * Multiplies a vector with a scalar returning a vector.
     * @pre true
     * @post RES.dimension().equals(dimension())
     *  	&& RES.get(i) == s&sdot;get(i)
     * @attribute associative
     * @attribute neutral
     * @return s&lowast;v
     */
    Vector/*<R>*/ multiply(Scalar s);

    /**
     * Multiplies a vector with a matrix returning a vector.
     * If column-vector <code>v</code> is sized <code>n</code> and transposed and
     * the matrix <code>A</code> is sized <code>n&times;m</code>,
     * the resulting column-vector <code>v&middot;A</code> is sized <code>m</code>.
     * @pre dimension() == B.dimension().height
     */
    Vector/*<R>*/ multiply(Matrix/*<R>*/ B);

    /**
     * Vector-cross-product of two vectors.<br>
     * &times;:<b><b>R</b></b><sup>3</sup>&times;<b><b>R</b></b><sup>3</sup>&rarr;<b><b>R</b></b><sup>3</sup>; (x,y) &#8614; x&times;y = (x<sub>1</sub>y<sub>2</sub>-x<sub>2</sub>y<sub>1</sub>, x<sub>2</sub>y<sub>0</sub>-x<sub>0</sub>y<sub>2</sub>, x<sub>0</sub>y<sub>1</sub>-x<sub>1</sub>y<sub>0</sub>)
     * <p>
     * cross is antisymmetric: b&times;a = -(a&times;b)</p>
     * @pre dimension() == 3 && dimension() == b.dimension()
     * @post RES.multiply(this) == 0 && RES.multiply(b) == 0.
     * @return the cross-product vector which will be orthogonal on this and b.
     *  x&times;y &perp; x &and; x&times;y &perp; y.
     * @pre (dimension() == 3 || dimension() == 2) && dimension() == b.dimension()
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
     * @pre 0<=index && index<=dimension()
     * @return this.
     * @post RES == this
     *  	&& RES.dimension() == OLD(dimension()) + 1
     */
    Vector/*<R>*/ insert(int index, Arithmetic/*>R<*/ v);
 
    /**
     * Insert all components of a vector into this vector at the specified index.
     * @pre 0<=index && index<=dimension()
     * @return this.
     * @post RES == this
     *  	&& RES.dimension() == OLD(dimension()) + v.dimension()
     */
    Vector/*<R>*/ insertAll(int index, Vector/*<R>*/ v);

    /**
     * Append a value to this vector.
     * @return this.
     * @post RES == this
     *  	&& RES.dimension() == OLD(dimension()) + 1
     */
    Vector/*<R>*/ insert(Arithmetic/*>R<*/ v);

    /**
     * Append all components of a vector to this vector.
     * @return this.
     * @post RES == this
     *  	&& RES.dimension() == OLD(dimension()) + v.dimension()
     */
    Vector/*<R>*/ insertAll(Vector/*<R>*/ v);

    /**
     * Remove the component at an index from this vector.
     * @return this.
     * @post RES == this
     *  	&& RES.dimension() == OLD(dimension()) - 1
     */
    Vector/*<R>*/ remove(int index);

    /**
     * Returns an array containing all the elements in this vector.
     * @post RES[i] == get(i) && RES != RES
     * @see #set(Arithmetic[])
     * @see Object#clone()
     */
    public Arithmetic/*>R<*/[] toArray();
}
