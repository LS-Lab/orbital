/**
 * @(#)Tensor.java 1.1 2002/06/09 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.util.ListIterator;

import java.awt.Dimension;
import java.util.NoSuchElementException;

/**
 * Represents a tensor of any dimensions n<sub>0</sub>&times;n<sub>1</sub>&times;&#8230;&times;n<sub>r-1</sub>.
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
     * If n<sub>0</sub>&times;n<sub>1</sub>&times;&#8230;&times;n<sub>r-1</sub>
     * are the dimensions of this tensor of rank r, then all (valid)
     * indices i&isin;<b>N</b><sup>r</sup> to this tensor satisfy
     * &forall;k i<sub>k</sub>&isin;{0,&#8230;,n<sub>k</sub>-1}.
     * </p>
     * @return an array d containing the dimensions of this tensor.
     * @post RES.length == rank()
     */
    int[] dimensions();

    /**
     * Get the component specified by index.
     * <p>
     * Of course, this method only has a meaning for tensors of free modules like vector spaces.
     * </p>
     * @pre valid(i) := (i.length == rank() &and; &forall;k 0&le;i[k]&le;dimensions()[k]-1)
     * @return t<sub>i[0].i[1],&#8230;i[i.length-1]</sub> &isin; R.
     * @internal see orbital.util.Utility#get(Object[],int[])
     */
    Arithmetic/*>R<*/ get(int[] i);

    /**
     * Sets the component specified by index.
     * <p>
     * Of course, this method only has a meaning for tensors of free modules like vector spaces.
     * </p>
     * @pre valid(i)
     * @throws UnsupportedOperationException if this tensor is constant and does not allow modifications.
     * @see #modCount
     */
    void set(int[] i, Arithmetic/*>R<*/ vi) throws UnsupportedOperationException;

    // iterator-views

    /**
     * Returns an iterator over all components.
     * <p>
     * <b>Note:</b> the list iterator returned is not required to support
     * {@link ListIterator#nextIndex()} and {@link ListIterator#previousIndex()},
     * or {@link ListIterator#add()} and {@link ListIterator#remove()},
     * and won't usually do so. The reason is that one-dimensional indices are meaningless
     * for tensors of rank r&gt;1 and that adding a single component to a tensor is not allowed
     * as it would destroy its rectangular form.
     * Nevertheless, vectors (rank 1) may support those operations.
     * </p>
     * @return an iterator that iterates over v<sub>0,&#8230;,0</sub>,&#8230;,v<sub>1,&#8230;,0</sub>,&#8230;&#8230;,v<sub>n<sub>1</sub>-1,&#8230;,n<sub>r</sub>-1</sub>.
     * @internal it seems better not to introduce a new interface in between Iterator and ListIterator,
     *  since we rely on ListIterators for Matrix.getColumns() etc anyway.
     */
    ListIterator/*_<R>_*/ iterator();
    
    // sub-views

    /**
     * Get a sub-tensor view ranging (i1:i2) inclusive.
     * The returned tensor is backed by this tensor, so changes in the returned tensor are reflected in this tensor, and vice-versa.
     * <p>
     * The semantics of the tensor returned by this method become undefined if the backing tensor
     * (i.e., this object) is <em>structurally modified</em> in any way other than via the returned tensor.
     * (Structural modifications are those that change the dimensions, or otherwise perturb it in
     * such a fashion that iterations in progress may yield incorrect results.)</p>
     * @pre i1.length==rank() &and; i2.length==rank() &and; &forall;k i1[k]&le;i2[k] &and; valid(i1) &and; valid(i2)
     * @return a tensor view of the specified part of this tensor.
     * <table>
     *   <tr><td>t<sub>(i1:i2)</sub> = <big>(</big>m<sub>l</sub><big>)</big><sub>l&isin;<big>{</big>l&isin;<b>N</b><sup>r</sup> &brvbar; &forall;k i1[k]&le;l[k]&le;i2[k]<big>}</big></sub></td> </tr>
     * </table>
     */
    Tensor/*<R>*/ subTensor(int[] i1, int[] i2);

    /**
     * Returns a view on a part of the tensor of a lesser rank.
     * The returned tensor is backed by this tensor, so changes in the returned tensor are reflected in this tensor, and vice-versa.
     * @param level the level l of indices to fix for this view.
     * @param index the index c<sub>l</sub> of the tensor part view at the level-th index.
     * @return a tensor view of the specified part t<sub>i<sub>0</sub>&times;i<sub>1</sub>&times;&#8230;c<sub>l</sub>&#8230;&times;i<sub>r-1</sub></sub> in this tensor.
     * @post RES.rank() = rank() - 1 &and; ....
     * @see #setSubTensor(int,int,Tensor)
     * @see orbital.logic.functor.Functionals#bindFirst(orbital.logic.functor.BinaryFunction, Object)
     * @see orbital.logic.functor.Functionals#bindSecond(orbital.logic.functor.BinaryFunction, Object)
     * @xxx rename, what's a "sub"-tensor of lesser rank called
     */
    Tensor/*<R>*/ subTensor(int level, int index);
    /**
     * Sets a part of lesser rank in this tensor.
     * @pre part.rank()==rank()-1 &and; Utilities.equalsAll(part.dimensions(), subTensor(level,index).dimensions())
     * @see #subTensor(int,int)
     * @see #modCount
     */
    void setSubTensor(int level, int index, Tensor/*<R>*/ part);

    /**
     * Returns a view on this tensor transposed.
     * The returned tensor is backed by this tensor, so changes in the returned tensor are reflected in this tensor, and vice-versa.
     * @param permutation the mapping table of a permutation &sigma;&isin;S<sub>rank()</sub>
     *  mapping i&#8614;permutation[i] for permuting the indices.
     * @return a tensor view of the transposed tensor t<sub>i<sub>&sigma;(0)</sub>&times;i<sub>&sigma;(1)</sub>&times;&#8230;&times;i<sub>&sigma;(r-1)</sub></sub>
     *  with permuted indices.
     * @pre permutation&isin;S<sub>rank()</sub>
     * @post RES.rank() = rank() &and; RES.get(i) = get(permutation(i))
     * @xxx rename, what's a good convention for names of views?
     */
    Tensor/*<R>*/ subTensorTransposed(int[] permutation);
    
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
     * <table>
     *   <tr> <td>·:R<sup>n<sub>0</sub>&times;&#8230;&times;n<sub>r-1</sub>&times;h</sup>&times;R<sup>h&times;m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup>&rarr;R<sup>n<sub>0</sub>&times;&#8230;&times;n<sub>r-1</sub>&times;m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup>;</td> </tr>
     *   <tr> <td><big>(</big>(a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,i<sub>r</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,i<sub>r</sub></sub> , (b<sub>j<sub>-1</sub>,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>j<sub>-1</sub>,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub><big>)</big> &#8614;
     *     (&sum;<span class="doubleIndex"><sub>&nu;=0</sub><sup>h-1</sup></span> a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,&nu;</sub></sub>&sdot;b<sub>&nu;,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>
     *   </td>
     *   </tr>
     * </table>
     * @pre dimensions()[rank()-1] == b.dimensions()[0]
     * @return the inner product a·b.
     * @post RES.dimensions() = {dimensions()[0],&#8230;dimensions()[rank()-2]}&cup;{b.dimensions()[1],&#8230;b.dimensions()[b.rank()-1]}
     * @note inner product is only one (partial) multiplication on graded tensor algebra
     */
    Tensor/*<R>*/ multiply(Tensor/*<R>*/ b);

    /**
     * Tensor product of a tensor with a tensor returning a tensor.
     * <table>
     *   <tr> <td>&otimes;:R<sup>n<sub>0</sub>&times;&#8230;&times;n<sub>r-1</sub></sup>&times;R<sup>m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup>&rarr;R<sup>n<sub>0</sub>&times;&#8230;&times;n<sub>r-1</sub>&times;m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup>;</td> </tr>
     *   <tr> <td><big>(</big>(a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub> , (b<sub>j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub><big>)</big> &#8614;
     *     (a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub></sub>&sdot;b<sub>j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>
     *     =&#770;
     *     (a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub></sub>&lowast;(b<sub>j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub>
     *   </td>
     *   </tr>
     * </table>
     * @return the tensor product (or outer product) a&otimes;b.
     * @post RES.dimensions() = dimensions()&cup;b.dimensions()
     * @note tensor product is only one multiplication on graded tensor algebra
     */
    Tensor/*<R>*/ tensor(Tensor/*<R>*/ b);

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
