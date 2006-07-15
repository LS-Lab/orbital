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
 * Represents a tensor t&isin;R<sup>n<sub>0</sub>&times;n<sub>1</sub>&times;&#8230;&times;n<sub>r-1</sub></sup> of dimensions n<sub>0</sub>&times;n<sub>1</sub>&times;&#8230;&times;n<sub>r-1</sub> and rank r.
 * <p>
 * If you intend to use <em>mutable</em> arithmetic elements, note the discussion of
 * mutations per reference vs. explicit cloning in {@link #set(int[],Arithmetic)}
 * which generally holds for all operations that set component values.</p>
 * <p>
 * Also note that some few methods will change its instance and explicitly <code><span class="keyword">return</span> <span class="keyword">this</span></code>
 * to allow chaining of structural changes,
 * whilst arithmetic methods will leave a tensor unchanged but return a modified version.
 * Refer to the documentation of the individual methods for details.</p>
 * 
 * @invariants succeedes(#clone()) &and; (overwrites(#clone()) &or; this implements Cloneable)
 * @structure extends Arithmetic
 * @structure extends Iteratable
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Values#tensor(Arithmetic[])
 * @see Values#tensor(Arithmetic[][])
 * @see Values#tensor(Arithmetic[][][])
 * @see Values#tensor(Object)
 */
public interface Tensor/*<R extends Arithmetic>*/ extends Arithmetic {
    // object-methods

    /**
     * @postconditions RES&ne;RES &and; RES&ne;this &and; RES.equals(this)
     */
    Object clone();

    // get/set-methods

    /**
     * Get the rank of the tensor.
     * The rank is the number of dimensions needed as indices for the components of this tensor.
     * It is the grade for the tensor algebra T(M) over the underlying module M.
     * @postconditions RES&ge;0
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
     * @postconditions RES.length == rank()
     */
    int[] dimensions();

    /**
     * Get the component specified by index.
     * <p>
     * Of course, this method only has a meaning for tensors of free modules like vector spaces.
     * </p>
     * @param i the index i of the component value to get.
     * @preconditions valid(i) := (i.length == rank() &and; &forall;k 0&le;i[k]&le;dimensions()[k]-1)
     * @return t<sub>i[0].i[1],&#8230;i[i.length-1]</sub> &isin; R.
     * @internal see orbital.util.Utility#get(Object[],int[])
     */
    Arithmetic/*>R<*/ get(int[] i);

    /**
     * Sets the component specified by index.
     * <p>
     * Of course, this method only has a meaning for tensors of free modules like vector spaces.
     * </p>
     * <p>
     * Depending upon the implementation, the value <var>vi</var>
     * might be kept per reference, if possible. However, whether it
     * is kept per reference is a matter of performance and subject to
     * free interpretation by the implementation class. So if
     * <var>vi</var> is <em>mutable</em>, and a tensor implementation
     * decides to keep it per reference, any modifications of
     * <var>vi</var> would also appear in that tensor, rendering it
     * useless. Since every implementation is allowed to choose the
     * tensor's internal data representation freely, you should not
     * rely on such behaviour of mutable arithmetic objects to provoke
     * inner state changes. Instead you should carefully avoid setting
     * a value by reference without cloning, if you intend to change
     * its state, afterwards.  Explicitly cloning <var>vi</var> prior
     * to setting it as an element of this tensor is always safe
     * (provided that you do not keep additional references to
     * it). If, however, <var>vi</var> is <em>immutable</em> (which
     * should be the usual case), no such sources of mistakes exist.
     * </p>
     * @param i the index i of the component value to set.
     * @param vi the value to set for the component v<sub>i</sub> at position i.
     * @preconditions valid(i)
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
     * or {@link ListIterator#add(Object)} and {@link ListIterator#remove()},
     * and won't usually do so. The reason is that one-dimensional indices are meaningless
     * for tensors of rank r&gt;1 and that adding a single component to a tensor is not allowed
     * as it would destroy its rectangular form.
     * Nevertheless, vectors (rank 1) may support those operations.
     * </p>
     * @return an iterator that iterates over v<sub>0,&#8230;,0</sub>,&#8230;,v<sub>1,&#8230;,0</sub>,&#8230;&#8230;,v<sub>n<sub>1</sub>-1,&#8230;,n<sub>r</sub>-1</sub>.
     * @internal it seems better not to introduce a new interface in between Iterator and ListIterator,
     *  since we rely on ListIterators for Matrix.getColumns() etc anyway.
     */
    ListIterator/*<R>*/ iterator();
    
    // sub-views

    /**
     * Get a sub-tensor view ranging (i1:i2) inclusive.
     * <p>
     * The resulting tensor is a (sub-)<dfn id="view">view</dfn> of this tensor.
     * Therefore, the returned tensor is backed by this tensor, so changes in the returned tensor are reflected in this tensor, and vice-versa.
     * </p>
     * <p>
     * The semantics of the tensor returned by this method becomes undefined if the backing tensor
     * (i.e., this object) is <em>structurally modified</em> in any way other than via the returned tensor.
     * (Structural modifications are those that change the dimensions, or otherwise perturb it in
     * such a fashion that iterations in progress may yield incorrect results.)
     * </p>
     * <p>
     * The resulting tensor is <dfn id="structurallyUnmodifiable">structurally unmodifiable</dfn>
     * in order to prevent it from inducing undefined behaviour on its backing tensor.
     * Query operations on the returned tensor "read through" to this object,
     * and attempts to structurally modify the returned vector, whether direct or via its iterator,
     * result in an {@link java.util.UnsupportedOperationException}.
     * However, setting single components will "write through" to the this object.
     * </p>
     * @preconditions i1.length==rank() &and; i2.length==rank() &and; &forall;k i1[k]&le;i2[k] &and; valid(i1) &and; valid(i2)
     * @return a tensor view of the specified part of this tensor.
     * <table>
     *   <tr><td>t<sub>(i1:i2)</sub> = <big>(</big>m<sub>l</sub><big>)</big><sub>l&isin;<big>{</big>l&isin;<b>N</b><sup>r</sup> &brvbar; &forall;k i1[k]&le;l[k]&le;i2[k]<big>}</big></sub></td> </tr>
     * </table>
     */
    Tensor/*<R>*/ subTensor(int[] i1, int[] i2);

    /**
     * Get a view on a part of the tensor of a lesser rank.
     * <p>
     * The returned tensor is a structurally unmodifiable <a href="Tensor.html#view">view</a>.</p>
     * @param level the level l of indices to fix for this view.
     * @param index the index c<sub>l</sub> of the tensor part view at the level-th index.
     * @return a tensor view of the specified part t<sub>i<sub>0</sub>&times;i<sub>1</sub>&times;&#8230;c<sub>l</sub>&#8230;&times;i<sub>r-1</sub></sub> in this tensor.
     * @postconditions RES.rank() = rank() - 1 &and; ....
     * @see #setSubTensor(int,int,Tensor)
     * @see orbital.logic.functor.Functionals#bindFirst(orbital.logic.functor.BinaryFunction, Object)
     * @see orbital.logic.functor.Functionals#bindSecond(orbital.logic.functor.BinaryFunction, Object)
     */
    Tensor/*<R>*/ subTensor(int level, int index);
    /**
     * Sets a part of lesser rank in this tensor.
     * @preconditions part.rank()==rank()-1 &and; Utilities.equalsAll(part.dimensions(), subTensor(level,index).dimensions())
     * @see #subTensor(int,int)
     * @see #modCount
     * @todo remove since this is a simple derived operation? Or even introduce setSubTensor(int[],int[],Tensor), but Matrix does not have this, either.
     */
    void setSubTensor(int level, int index, Tensor/*<R>*/ part);

    /**
     * Returns a view on this tensor transposed.
     * <p>
     * The returned tensor is a <a href="Tensor.html#view">view</a>.</p>
     * @param permutation the mapping table of a permutation &sigma;&isin;S<sub>rank()</sub>
     *  mapping i&#8614;permutation[i] for permuting the indices.
     * @return a tensor view of the transposed tensor t<sub>i<sub>&sigma;(0)</sub>&times;i<sub>&sigma;(1)</sub>&times;&#8230;&times;i<sub>&sigma;(r-1)</sub></sub>
     *  with permuted indices.
     * @preconditions permutation&isin;S<sub>rank()</sub>
     * @postconditions RES.rank() = rank() &and; RES.get(i) = get(permutation(i))
     * @xxx rename, what's a good convention for names of views?
     */
    Tensor/*<R>*/ subTensorTransposed(int[] permutation);
    
    // norm
        
    /**
     * Returns the norm || ||<sub>p</sub> of this tensor.
     * <p>This method implements p-norms, where<br>
     * <span class="Formula">||x||<sub>p</sub> = (|x<sub>1</sub>|<sup>p</sup> + ... + |x<sub>n</sub>|<sup>p</sup>)<sup>1/p</sup></span>.<br>
     * <span class="Formula">||x||<sub>&infin;</sub> = max {|x<sub>1</sub>|,...,|x<sub>n</sub>|}</span>.</p>
     * @preconditions p>=1
     */
    //@todo ? public Real norm(double p);

    // arithmetic-operations

    /**
     * Adds two tensors returning a tensor.
     * @preconditions Arrays.equals(dimensions(), b.dimension())
     *  otherwise there can only be a purely symbolic result in tensor algebra.
     * @postconditions Arrays.equals(RES.dimensions(), dimensions())
     *          && RES.get(i) == get(i) + b.get(i)
     * @attribute associative
     * @attribute neutral (0)
     * @attribute inverse (-v)
     * @attribute commutative
     */
    Tensor/*<R>*/ add(Tensor/*<R>*/ b);

    /**
     * Subtracts two tensors returning a tensor.
     * @preconditions Arrays.equals(dimensions(), b.dimension())
     *  otherwise there can only be a purely symbolic result in tensor algebra.
     * @postconditions Arrays.equals(RES.dimensions(), dimensions())
     *          && RES.get(i) == get(i) - b.get(i)
     * @attribute associative
     */
    Tensor/*<R>*/ subtract(Tensor/*<R>*/ b);

    /**
     * Multiplies a scalar with a tensor returning a tensor.
     * @preconditions true
     * @postconditions Arrays.equals(RES.dimensions(), dimensions())
     *          && RES.get(i) == s&sdot;get(i)
     * @attribute associative
     * @attribute left-neutral (1)
     * @return s&middot;this
     * @note once we have covariant return-types.
     */
    //Tensor/*<R>*/ scale(Scalar s);

    /**
     * Inner product of a tensor with a tensor returning a tensor.
     * <table>
     *   <tr>
     *     <td class="nameOfMap" rowspan="2">·</td>
     *     <td class="leftOfMap">R<sup>n<sub>0</sub>&times;&#8230;&times;n<sub>r-1</sub>&times;h</sup>&times;R<sup>h&times;m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup></td>
     *     <td class="arrowOfMap">&rarr;</td>
     *     <td class="rightOfMap">R<sup>n<sub>0</sub>&times;&#8230;&times;n<sub>r-1</sub>&times;h</sup>&times;R<sup>h&times;m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup></td>
     *   </tr>
     *   <tr>
     *     <td class="leftOfMap"><big>(</big>(a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,i<sub>r</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,i<sub>r</sub></sub> , (b<sub>j<sub>-1</sub>,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>j<sub>-1</sub>,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub><big>)</big></td>
     *     <td class="arrowOfMap">&#8614;</td>
     *     <td class="rightOfMap">(&sum;<span class="doubleIndex"><sub>&nu;=0</sub><sup>h-1</sup></span> a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,&nu;</sub></sub>&sdot;b<sub>&nu;,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub></td>
     *   </tr>
     * </table>
     * @preconditions dimensions()[rank()-1] == b.dimensions()[0]
     * @return the inner product a·b.
     * @postconditions RES.dimensions() = {dimensions()[0],&#8230;dimensions()[rank()-2]}&cup;{b.dimensions()[1],&#8230;b.dimensions()[b.rank()-1]}
     * @note inner product is only one (partial) multiplication on graded tensor algebra
     */
    Tensor/*<R>*/ multiply(Tensor/*<R>*/ b);

    /**
     * Tensor product of a tensor with a tensor returning a tensor.
     * <table>
     *   <tr>
     *     <td class="nameOfMap" rowspan="2">&otimes;</td>
     *     <td class="leftOfMap">R<sup>n<sub>0</sub>&times;&#8230;&times;n<sub>r-1</sub></sup>&times;R<sup>m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup></td>
     *     <td class="arrowOfMap">&rarr;</td>
     *     <td class="rightOfMap">R<sup>n<sub>0</sub>&times;&#8230;&times;n<sub>r-1</sub>&times;m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup></td>
     *   </tr>
     *   <tr>
     *     <td class="leftOfMap"><big>(</big>(a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub> , (b<sub>j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub><big>)</big><br />=(a,b)</td>
     *     <td class="arrowOfMap">&#8614;</td>
     *     <td class="rightOfMap">(a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub></sub>&sdot;b<sub>j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub>,j<sub>0</sub>,&#8230;,j<sub>s-1</sub></sub>
     *     =&#770;
     *     (a<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub></sub>&middot;b)<sub>i<sub>0</sub>,&#8230;,i<sub>r-1</sub></sub>
     *     </td>
     *   </tr>
     * </table>
     * At least formally, the last form of the calculation resembles the scalar multiplication b&middot;a
     * if a was a vector of a left-R<sup>m<sub>0</sub>&times;&#8230;&times;m<sub>s-1</sub></sup>-modules.
     * @todo so why don't we unify tensor and scale? Because there may as well be distinct definitions?
     * @return the tensor product (or outer product) a&otimes;b.
     * @postconditions RES.dimensions() = dimensions()&cup;b.dimensions()
     * @note tensor product is only one multiplication on graded tensor algebra T(M).
     *  Although it is always defined, a more "natural" multiplication on tensors of rank 2, for
     *  example, is the {@link #multiply(Tensor) inner product} which therefore plays the role of
     *  the default (though partial) ring multiplication.
     */
    Tensor/*<R>*/ tensor(Tensor/*<R>*/ b);

    // operations on tensors
        
    /**
     * Returns this tensor transposed.
     */
    //@todo ? Tensor/*<R>*/ transpose();


    // Structural modifications

//     /**
//      * Insert a value into this tensor at the specified index.
//      * @preconditions 0<=index && index<=dimension()
//      * @return this.
//      * @postconditions RES == this
//      *       && RES.dimension() == OLD(dimension()) + 1
//      */
//     Tensor/*<R>*/ insert(int index, Arithmetic/*>R<*/ v);
 
//     /**
//      * Insert all components of a tensor into this tensor at the specified index.
//      * @preconditions 0<=index && index<=dimension()
//      * @return this.
//      * @postconditions RES == this
//      *       && RES.dimension() == OLD(dimension()) + v.dimension()
//      */
//     Tensor/*<R>*/ insertAll(int index, Tensor/*<R>*/ v);

//     /**
//      * Append a value to this tensor.
//      * @return this.
//      * @postconditions RES == this
//      *       && RES.dimension() == OLD(dimension()) + 1
//      */
//     Tensor/*<R>*/ insert(Arithmetic/*>R<*/ v);

//     /**
//      * Append all components of a tensor to this tensor.
//      * @return this.
//      * @postconditions RES == this
//      *       && RES.dimension() == OLD(dimension()) + v.dimension()
//      */
//     Tensor/*<R>*/ insertAll(Tensor/*<R>*/ v);

//     /**
//      * Remove the component at an index from this tensor.
//      * @return this.
//      * @postconditions RES == this
//      *       && RES.dimension() == OLD(dimension()) - 1
//      */
//     Tensor/*<R>*/ remove(int index);
}
