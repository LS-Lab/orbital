/**
 * @(#)AbstractTensor.java 1.0 2002-08-07 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.io.Serializable;
import java.util.Iterator;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import java.lang.reflect.Array;
import java.util.Arrays;
import orbital.util.Setops;
import orbital.util.InnerCheckedException;
import orbital.util.Utility;
import orbital.algorithm.Combinatorical;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;
import orbital.math.functional.Functions;

/**
 * @internal All tensor methods iterate row-wise.
 *  This means that the row (i) will be iterated over in the outer loop,
 *  and the columm (j) in the inner loop.
 *  Therefore, matrices A[i][j] build up of a two dimensional array, should have
 *  the row (i) as the first index in the array and the column (j) as the second
 *  to ensure all methods iterate linearly in memory and without stride.
 *  Due to C/Java memory storage conventions (storing columns consequtively),
 *  this has great effect on performance.
 *  Fortran memory storage conventions are exactly the other way around.
 * @version 1.0, 2002-08-07
 * @author  Andr&eacute; Platzer
 * @todo could AbstractMatrix or AbstractVector profit from extending us?
 */
abstract class AbstractTensor/*<R implements Arithmetic>*/ extends AbstractArithmetic implements Tensor/*<R>*/, Serializable {
    private static final long serialVersionUID = 7889937971348824822L;

    // object-methods
	
    /**
     * Checks two tensors for equality.
     */
    public boolean equals(Object o) {
	if (o instanceof Tensor) {
	    Tensor/*<R>*/ B = (Tensor) o;
	    if (!Arrays.equals(dimensions(), B.dimensions()))
		return false;
	    return orbital.util.Setops.all(iterator(), B.iterator(), orbital.logic.functor.Predicates.equal);
	} 
	return false;
    } 

    public boolean equals(Object o, Real tolerance) {
	return Metric.INDUCED.distance(this, (Tensor)o).compareTo(tolerance) < 0;
    }

    public int hashCode() {
	//TODO: can we use Utility.hashCodeAll(Object) as well?
	int hash = 0;
	//@todo functional?
	for (Iterator i = iterator(); i.hasNext(); ) {
	    Object e = i.next();
	    hash ^= e == null ? 0 : e.hashCode();
	} 
	return hash;
    } 

    public Object clone() {
	try {
	    return super.clone();
	}
	catch (CloneNotSupportedException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + Tensor.class + " must either overwrite clone() or implement " + Cloneable.class, nonconform);}
    }

    // factory-methods
    
    /**
     * Instantiates a new tensor with dimensions dim of the same type like this.
     * <p>This method is a replacement for a constructor in the implementation of Tensor.</p>
     * @param dim the dimensions desired for the tensor.
     * @return a tensor of the same type as this, dimensions as specified.
     * The elements need not be initialized since they will soon be by the calling method.
     * @post RES != RES
     * @see <a href="{@docRoot}/DesignPatterns/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected abstract Tensor/*<R>*/ newInstance(int[] dim);

    // iterator-views
	
    /**
     * The number of times this object has been structurally modified.
     * Structural modifications are those that change the number of elements in
     * the object or otherwise modify its internal structure.
     * This field is used to make iterators of the object fail-fast.
     * <p>
     * To use this feature, increase modCount whenever an implementation method changes
     * this tensor.</p>
     * @see java.util.ConcurrentModificationException
     */
    protected transient int modCount = 0;

    public Iterator iterator() {
	return new Iterator() {
		//@todo expectedModCount
		private Combinatorical i = Combinatorical.getPermutations(dimensions());
		public boolean hasNext() {
		    return i.hasNext();
		} 
		public Object next() {
		    return get(i.next());
		} 
		public void remove() {
		    throw new UnsupportedOperationException("removing a single element from a tensor is impossible");
		} 
	    };
    } 

    // sub-views
	
    public Tensor/*<R>*/ subTensor(int[] i, int[] j) {
	validate(i);
	validate(j);
	throw new UnsupportedOperationException("not yet implemented");
    } 
    public Real norm() {
	//@todo verify that this really is a norm
	return (Real/*__*/) Functions.sqrt.apply(Operations.sum.apply(Functionals.map(Functions.square, Functionals.map(Functions.norm, iterator()))));
    } 

    // arithmetic-operations
	
    public Arithmetic zero() {return Values.ZERO(dimensions());}
    public Arithmetic one() {
	throw new UnsupportedOperationException();
    }
    
    public Tensor/*<R>*/ add(Tensor/*<R>*/ B) {
	Utility.pre(Arrays.equals(dimensions(),B.dimensions()), "Tensor A+B only defined for equal dimensions");
	Tensor/*<R>*/ ret = newInstance(dimensions());

	//TODO: cache Dimension dim = dimension(); in all these methods
	// component-wise
	for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
	    int[] i = index.next();
	    ret.set(i, (Arithmetic/*>R<*/) get(i).add(B.get(i)));
	}
	return ret;
    } 

    public Tensor/*<R>*/ subtract(Tensor/*<R>*/ B) {
	Utility.pre(Arrays.equals(dimensions(), B.dimensions()), "Tensor A-B only defined for equal dimensions");
	Tensor/*<R>*/ ret = newInstance(dimensions());

	// component-wise
	for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
	    int[] i = index.next();
	    ret.set(i, (Arithmetic/*>R<*/) get(i).subtract(B.get(i)));
	}
	return ret;
    } 

    public Arithmetic scale(Arithmetic s) {
	Tensor ret = newInstance(dimensions());

	// component-wise
	for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
	    int[] i = index.next();
	    ret.set(i, s.multiply(get(i)));
	}
	return ret;
    } 

    public Tensor/*<R>*/ multiply(Tensor/*<R>*/ b) {
	final int[] dim = new int[rank() + b.rank() - 2];
	final int[] d = dimensions();
	final int[] e = b.dimensions();
	// the index to convolute
	final int conv = d.length - 1;
	// the (common) length of the convolution
	final int len = d[conv];
	if (d[conv] != e[0])
	    throw new IllegalArgumentException("inner product a.b only defined for dimension n1 x ... x nr x n multiplied with n x m1 x ... x mr, but not for " + MathUtilities.format(dimensions()) + " with " + MathUtilities.format(b.dimensions()));
	System.arraycopy(d, 0, dim, 0, conv);
	System.arraycopy(e, 1, dim, conv, e.length - 1);
	Tensor ret = newInstance(dim);

	//@internal optimizable by far (cache optimization and everything) and beautifiable as well
	for (Combinatorical index = Combinatorical.getPermutations(dim); index.hasNext(); ) {
	    final int[] ij = index.next();
	    Arithmetic s = Values.ZERO;  //@xxx what's our 0?
	    for (int nu = 0; nu < len; nu++) {
		final int[] i = new int[d.length];
		System.arraycopy(ij, 0, i, 0, conv);
		i[conv] = nu;
		
		final int[] j = new int[e.length];
		j[0] = nu;
		System.arraycopy(ij, conv, j, 1, e.length - 1);

		s = s.add(get(i).multiply(b.get(j)));
	    }
	    ret.set(ij, s);
	}
	return ret;
    } 

    public Tensor/*<R>*/ tensor(Tensor/*<R>*/ b) {
	Tensor ret = newInstance(dimensions());

	for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
	    int[] i = index.next();
	    ret.set(i, b.multiply(get(i)));
	}
	return ret;
    }

    // Arithmetic implementation

    public Arithmetic add(Arithmetic b) {
	return add((Tensor) b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	return subtract((Tensor) b);
    } 
    public Arithmetic minus() {
	Tensor/*<R>*/ ret = newInstance(dimensions());
	// component-wise
	for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
	    int[] i = index.next();
	    ret.set(i, (Arithmetic/*>R<*/) get(i).minus());
	}
	return ret;
    } 
    public Arithmetic multiply(Arithmetic b) {
	if (b instanceof Scalar)
	    return scale((Scalar) b);
	else if (b instanceof Tensor)
	    return multiply((Tensor) b);
	throw new IllegalArgumentException("wrong type " + b.getClass());
    } 

    public Arithmetic inverse() throws ArithmeticException {
	throw new UnsupportedOperationException();
    } 

    /**
     * Validate i indices within dimension.
     * @pre 0 <= i < dimension().height && 0 <= j < dimension().width
     * @post true
     * @throws ArrayIndexOutOfBoundsException if the index (i|j) is out of bounds for columns or rows.
     * @todo turn into an aspect, only.
     */
    final void validate(int[] i) {
	if (i.length != rank())
	    throw new ArrayIndexOutOfBoundsException("illegal number of indices (" + i.length + " indices) for tensor of rank " + rank());
	int[] dim = dimensions();
	for (int k = 0; k < i.length; k++) {
	    if (i[k] < 0)
		throw new ArrayIndexOutOfBoundsException(k + "-th index (" + i[k] + ") is negative");
	    if (i[k] >= dim[k])
		throw new ArrayIndexOutOfBoundsException(k + "-th index (" + i[k] + ") out of dimension (" + dim[k] + ")");
	}
    } 
    public String toString() {
	return ArithmeticFormat.getDefaultInstance().format(this);
    }
    /**
     * Returns an array containing all the elements in this tensor.
     * The first index in this array specifies the row, the second is for column.
     */
    public Object/*>R<*/[] toArray() {
 	final int[] dim = dimensions();
 	final Object[] r = (Object[]) Array.newInstance(Arithmetic/*>R<*/.class, dim);
	for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
	    int[] i = index.next();
	    Utility.setPart(r, i, (Arithmetic/*>R<*/) get(i));
	}
	return r;
    } 
}
