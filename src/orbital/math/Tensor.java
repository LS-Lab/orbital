/**
 * @(#)Tensor.java 1.1 2002/06/09 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.util.Iterator;
import java.util.ListIterator;

import java.awt.Dimension;
import java.util.NoSuchElementException;

/**
 * Represents a tensor of any dimensions n<sub>1</sub>&times;n<sub>2</sub>&times;&#8230;&times;n<sub>r</sub>.
 * <p>
 * If you intend to use <em>mutable</em> arithmetic elements, note the discussion of
 * mutations per reference vs. explicit cloning in {@link Matrix#set(int,int,Arithmetic)}
 * which generally holds for all operations setting elements.</p>
 * <p>
 * Also note that some few methods will change its instance and explicitly <code><span class="keyword">return</span> <span class="keyword">this</span></code>
 * to allow chaining of structural changes,
 * whilst arithmetic methods will leave a tensor unchanged but return a modified version.
 * Refer to the documentation of the individual methods for details.</p>
 * 
 * @invariant succeedes(#clone()) &and; (overwrites(#clone()) &or; this implements Cloneable)
 * @structure extends Arithmetic
 * @structure extends Iteratable
 * @version 1.1, 2002/06/09
 * @author  Andr&eacute; Platzer
 * @see Values#tensor(Arithmetic[])
 * @see Values#tensor(Arithmetic[][])
 * @see Values#tensor(Arithmetic[][][])
 * @see Values#tensor(Object[])
 */
public interface Tensor/*<R implements Arithmetic>*/ extends Arithmetic {
    // object-methods

    /**
     * @post RES != RES && RES != this && RES.equals(this)
     */
    Object clone();

    // get/set-methods

    /**
     * Get the rank of the tensor.
     * The rank is the number of dimensions needed as indices for the components of this tensor.
     * @post RES&ge;0
     * @see Matrix#linearRank()
     */
    int rank();
    
    /**
     * Returns the dimensions of the tensor.
     * <p>
     * If n<sub>1</sub>&times;n<sub>2</sub>&times;&#8230;&times;n<sub>r</sub>
     * are the dimensions of this tensor of rank r, then all (valid)
     * indices i&isin;<b>N</b><sup>r</sup> to this tensor satisfy
     * &forall;k i<sub>k</sub>&isin;{0,&#8230;,n<sub>k</sub>-1}.
     * </p>
     * @return an array d containing the dimensions of this tensor.
     * @post RES.length == rank()
     */
    int[] dimensions();

    /**
     * Get the value at the component specified by index.
     * <p>
     * Of course, this method only has a meaning for tensors of free modules like vector spaces.
     * </p>
     * @pre valid(i) := (i.length == rank() &and; &forall;k 0&le;i[k]&le;dimensions()[k]-1)
     * @internal see orbital.util.Utility#get(Object[],int[])
     */
    Arithmetic/*>R<*/ get(int[] i);

    /**
     * Sets a value at the component specified by index.
     * <p>
     * Of course, this method only has a meaning for tensors of free modules like vector spaces.
     * </p>
     * @pre valid(i)
     * @throws UnsupportedOperationException if this tensor is constant and does not allow modifications.
     */
    void set(int[] i, Arithmetic/*>R<*/ vi) throws UnsupportedOperationException;

    // iterator-views

    /**
     * Returns an iterator over all elements.
     * @return an iterator that iterates over v<sub>0,&#8230;,0</sub>,&#8230;,v<sub>1,&#8230;,0</sub>,&#8230;&#8230;,v<sub>n<sub>1</sub>-1,&#8230;,n<sub>r</sub>-1</sub>.
     */
    Iterator/*_<R>_*/ iterator();
    
    // sub-views

    //@todo how about things like Tensor getRow(int[]) etc.

    /**
     * Get a sub-tensor view ranging (i:j) inclusive.
     * The returned tensor is backed by this tensor, so changes in the returned tensor are reflected in this tensor, and vice-versa.
     * <p>
     * The semantics of the tensor returned by this method become undefined if the backing tensor
     * (i.e., this object) is <em>structurally modified</em> in any way other than via the returned tensor.
     * (Structural modifications are those that change the dimensions, or otherwise perturb it in
     * such a fashion that iterations in progress may yield incorrect results.)</p>
     * @pre i&le;j && valid(i) && valid(j)
     * @return a matrix view of the specified part of this matrix.
     */
    Tensor/*<R>*/ subTensor(int[] i, int[] j);

    // norm
	
    /**
     * Returns the norm || ||<sub>p</sub> of this tensor.
     * <p>This method implements p-norms, where<br>
     * <span class="Formula">||x||<sub>p</sub> = (|x<sub>1</sub>|<sup>p</sup> + ... + |x<sub>n</sub>|<sup>p</sup>)<sup>1/p</sup></span>.<br>
     * <span class="Formula">||x||<sub>&infin;</sub> = max {|x<sub>1</sub>|,...,|x<sub>n</sub>|}</span>.</p>
     * @pre p>=1
     */
    //@todo ? public Real norm(double p);

    // arithmetic-operations

    /**
     * Adds two tensors returning a tensor.
     * @pre Arrays.equals(dimensions(), b.dimension())
     *  otherwise there can only be a purely symbolic result in tensor algebra.
     * @post Arrays.equals(RES.dimensions(), dimensions())
     *  	&& RES.get(i) == get(i) + b.get(i)
     * @attribute associative
     * @attribute neutral (0)
     * @attribute inverse (-v)
     * @attribute commutative
     */
    Tensor/*<R>*/ add(Tensor/*<R>*/ b);

    /**
     * Subtracts two tensors returning a tensor.
     * @pre Arrays.equals(dimensions(), b.dimension())
     *  otherwise there can only be a purely symbolic result in tensor algebra.
     * @post Arrays.equals(RES.dimensions(), dimensions())
     *  	&& RES.get(i) == get(i) - b.get(i)
     * @attribute associative
     */
    Tensor/*<R>*/ subtract(Tensor/*<R>*/ b);

    /**
     * Multiplies a scalar with a tensor returning a tensor.
     * @pre true
     * @post Arrays.equals(RES.dimensions(), dimensions())
     *  	&& RES.get(i) == s&sdot;get(i)
     * @attribute associative
     * @attribute neutral
     * @return s&lowast;this
     * @note once we have covariant return-types.
     */
    //Tensor/*<R>*/ scale(Scalar s);

    /**
     * Inner product of a tensor with a tensor returning a tensor.
     * <p>
     * ·:R<sup>n<sub>1</sub>&times;&#8230;&times;n<sub>r</sub>&times;h</sup>&times;R<sup>h&times;m<sub>1</sub>&times;&#8230;&times;m<sub>s</sub></sup>&rarr;R<sup>n<sub>1</sub>&times;&#8230;&times;n<sub>r</sub>&times;m<sub>1</sub>&times;&#8230;&times;m<sub>s</sub></sup>;
     * <big>(</big>(a<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub>,i<sub>r+1</sub></sub>)<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub>,i<sub>r+1</sub></sub> , (b<sub>j<sub>0</sub>,j<sub>1</sub>,&#8230;,j<sub>s</sub></sub>)<sub>j<sub>0</sub>,j<sub>1</sub>,&#8230;,j<sub>s</sub></sub><big>)</big> &#8614;
     * (&sum;<span class="doubleIndex"><sub>&nu;=0</sub><sup>h-1</sup></span> a<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub>,&nu;</sub></sub>&sdot;b<sub>&nu;,j<sub>1</sub>,&#8230;,j<sub>s</sub></sub>)<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub>,j<sub>1</sub>,&#8230;,j<sub>s</sub></sub>
     * </p>
     * @pre dimensions()[rank()-1] == b.dimensions()[0]
     * @return the inner product a·b.
     * @post RES.dimensions() = {dimensions()[0],&#8230;dimensions()[rank()-2]}&cup;{b.dimensions()[1],&#8230;b.dimensions()[b.rank()-1]}
     * @note inner product is only one (partial) multiplication on graded tensor algebra
     */
    //@todo introduce
    //Tensor/*<R>*/ multiply(Tensor/*<R>*/ b);

    /**
     * Tensor product of a tensor with a tensor returning a tensor.
     * <p>
     * &otimes;:R<sup>n<sub>1</sub>&times;&#8230;&times;n<sub>r</sub></sup>&times;R<sup>m<sub>1</sub>&times;&#8230;&times;m<sub>s</sub></sup>&rarr;R<sup>n<sub>1</sub>&times;&#8230;&times;n<sub>r</sub>&times;m<sub>1</sub>&times;&#8230;&times;m<sub>s</sub></sup>;
     * <big>(</big>(a<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub></sub>)<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub></sub> , (b<sub>j<sub>1</sub>,&#8230;,j<sub>s</sub></sub>)<sub>j<sub>1</sub>,&#8230;,j<sub>s</sub></sub><big>)</big> &#8614;
     * (a<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub></sub></sub>&sdot;b<sub>j<sub>1</sub>,&#8230;,j<sub>s</sub></sub>)<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub>,j<sub>1</sub>,&#8230;,j<sub>s</sub></sub>
     * =&#770;
     * (a<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub></sub></sub>&lowast;(b<sub>j<sub>1</sub>,&#8230;,j<sub>s</sub></sub>)<sub>j<sub>1</sub>,&#8230;,j<sub>s</sub></sub>)<sub>i<sub>1</sub>,&#8230;,i<sub>r</sub></sub>
     * </p>
     * @return the tensor product (or outer product) a&otimes;b.
     * @post RES.dimensions() = dimensions()&cup;b.dimensions()
     * @note tensor product is only one multiplication on graded tensor algebra
     */
    //@todo introduce Tensor/*<R>*/ tensor(Tensor/*<R>*/ b); which seems better than multiply(Tensor/*<R>*/ b);
    //Tensor/*<R>*/ tensor(Tensor/*<R>*/ b);

    // operations on tensors
	
    /**
     * Returns this tensor transposed.
     * Also distinguished via tensor.multiply(matrix) or matrix.multiply(tensor) instead.
     * @see Matrix#transpose()
     */
    //@todo ? Matrix/*<R>*/ transpose();


    // Structural modifications

//     /**
//      * Insert a value into this tensor at the specified index.
//      * @pre 0<=index && index<=dimension()
//      * @return this.
//      * @post RES == this
//      *  	&& RES.dimension() == OLD(dimension()) + 1
//      */
//     Tensor/*<R>*/ insert(int index, Arithmetic/*>R<*/ v);
 
//     /**
//      * Insert all components of a tensor into this tensor at the specified index.
//      * @pre 0<=index && index<=dimension()
//      * @return this.
//      * @post RES == this
//      *  	&& RES.dimension() == OLD(dimension()) + v.dimension()
//      */
//     Tensor/*<R>*/ insertAll(int index, Tensor/*<R>*/ v);

//     /**
//      * Append a value to this tensor.
//      * @return this.
//      * @post RES == this
//      *  	&& RES.dimension() == OLD(dimension()) + 1
//      */
//     Tensor/*<R>*/ insert(Arithmetic/*>R<*/ v);

//     /**
//      * Append all components of a tensor to this tensor.
//      * @return this.
//      * @post RES == this
//      *  	&& RES.dimension() == OLD(dimension()) + v.dimension()
//      */
//     Tensor/*<R>*/ insertAll(Tensor/*<R>*/ v);

//     /**
//      * Remove the component at an index from this tensor.
//      * @return this.
//      * @post RES == this
//      *  	&& RES.dimension() == OLD(dimension()) - 1
//      */
//     Tensor/*<R>*/ remove(int index);
}
