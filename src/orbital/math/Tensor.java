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
     * The rank is the number of dimensions needed as indices for its components.
     * @see Matrix#linearRank()
     */
    int rank();
    
    /**
     * Returns the dimensions of the tensor.
     * @todo document
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
     * @return an iterator that iterates over v<sub>0,&#8230;,0</sub>,&#8230;,v<sub>1,&#8230;,0</sub>,&#8230;,v<sub>n<sub>1</sub>-1,&#8230;,n<sub>r</sub>-1</sub>.
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
     *  otherwise there can only be a purely symbolic result
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
     *  otherwise there can only be a purely symbolic result
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
    //Tensor/*<R>*/ multiply(Scalar s);

    /**
     * Multiplies a tensor with a matrix returning a tensor.
     * If column-tensor <code>v</code> is sized <code>n</code> and transposed and
     * the matrix <code>A</code> is sized <code>n&times;m</code>,
     * the resulting column-tensor <code>v&middot;A</code> is sized <code>m</code>.
     * @pre dimension() == B.dimension().height
     */
    //@note tensor product is one multiplication on graded tensor algebra
    //@todo Tensor/*<R>*/ multiply(Matrix/*<R>*/ B);

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
