/**
 * @(#)AbstractVector.java 1.0 1999/03/08 Andre Platzer
 * 
 * Copyright (c) 1999-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import java.util.Iterator;
import java.util.ListIterator;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;
import orbital.math.functional.Functions;

import java.awt.Dimension;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import orbital.util.Utility;

//@todo ensure that multiply((Vector) b) does in fact call multiply(Vector<R>) instead of leading to an infinite recursion. Here and in Matrix stuff
abstract class AbstractVector/*<R implements Arithmetic>*/ extends AbstractTensor implements Vector/*<R>*/ {
    private static final long serialVersionUID = 372991453454528414L;

    // factory-method
	
    /**
     * instantiate a new vector with dimension dim of the same type like this.
     * <p>
     * This method is a replacement for a constructor in the implementation of Vector.
     * </p>
     * @param dim the dimension desired for the vector.
     * @return a vector of the same type as this, dimension as specified
     * The elements need not be initialized since they will soon be by the calling method.
     * @post RES != RES
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected abstract Vector/*<R>*/ newInstance(int dim);
    
    protected final Tensor/*<R>*/ newInstance(int[] dim) {
	return dim.length == 1 ? newInstance(dim[0]) : Values.getDefaultInstance().newInstance(dim);
    }

    // get/set-methods
	
    /**
     * returns the value at component specified by index if that can be converted to a double.
     * Convenience method.
     * @throws UnsupportedOperationException if the value can not be represented as a double.
     */
    public double getDoubleValue(int i) {
	try {
	    return ((Number) get(i)).doubleValue();
	} catch (ClassCastException x) {
	    throw new UnsupportedOperationException("no real number");
	} 
    } 

    /**
     * Set all components of this vector.
     * @param v the components the matrix should have from now on.
     *  The dimensions must not necessarily match the current ones.
     * @post dimension() == v.length && get(i) == v[i]
     * @see #toArray()
     * @see #modCount
     */
    protected abstract void set(Arithmetic/*>R<*/[] v);
    
    // sub-views

    public Vector/*<R>*/ subVector(int i1, int i2) {
	return new SubVector/*<R>*/(this, i1, i2);
    } 
    private static class SubVector/*<R implements Arithmetic>*/ extends AbstractVector/*<R>*/ {
	private static final long serialVersionUID = 1246257328047975917L;
    	/**
    	 * the Vector whose sub-vector we are.
    	 * @serial
    	 * @internal note we could just as well avoid this field and transform this class into a nonstatic inner class, don't we?
    	 */
    	private final AbstractVector/*<R>*/ v;
    	/**
    	 * contains the offset in v where this sub-view vector starts.
    	 * @serial
    	 */
    	private final int    offset;
    	/**
    	 * contains the size in v which is the dimension of this sub-view vector.
    	 * @serial
    	 */
    	private final int    size;
    
    	/**
    	 * The modCount value that the iterator believes the backing
    	 * object should have. If this expectation is violated, the iterator
    	 * has detected concurrent modification.
    	 */
    	private transient int expectedModCount = 0;

    	public SubVector(AbstractVector/*<R>*/ v, int i1, int i2) {
	    if (!(i1 <= i2))
		throw new IllegalArgumentException("Ending index " + i2 + " cannot be less than starting index " + i1 + ".");
	    v.validate(i1);
	    v.validate(i2);
	    this.v = v;
	    this.offset = i1;
	    this.size = i2 - i1 + 1;
	    this.expectedModCount = v.modCount;
    	}
    
    	protected Vector/*<R>*/ newInstance(int dim) {
	    checkForComodification();
	    return newInstance(dim);
    	} 
    
    	public final int dimension() {
	    checkForComodification();
	    return size;
    	} 
    
    	public Arithmetic/*>R<*/ get(int i) {
	    validate(i);
	    checkForComodification();
	    return v.get(i + offset);
    	} 
    	public void set(int i, Arithmetic/*>R<*/ vi) {
	    validate(i);
	    checkForComodification();
	    v.set(i + offset, vi);
    	} 
    
    	protected void set(Arithmetic/*>R<*/[] v) {
	    if (dimension() != v.length)
		throw new UnsupportedOperationException("sub-vector cannot be altered. clone first");
	    checkForComodification();
	    throw new UnsupportedOperationException("Altering the underlying buffer of a sub-vector is not yet supported");
            /*
	      expectedModCount = m.modCount;
	      modCount++;
            */
    	} 
    
    	public Object clone() {
	    checkForComodification();
	    return new ArithmeticVector/*<R>*/(super.toArray());
    	} 

    	private final void checkForComodification() {
    	    if (v.modCount != expectedModCount)
		throw new ConcurrentModificationException();
    	}
    }
	
    // iterator-views

    /**
     * The number of times this Vector has been structurally modified.
     * Structural modifications are those that change the number of elements in
     * the Vector or otherwise modify its internal structure.
     * This field is used to make iterators of the Vector fail-fast.
     * <p>
     * To use this feature, increase modCount whenever an implementation method changes
     * this vector.</p>
     * @see java.util.ConcurrentModificationException
     */
    protected transient int modCount = 0;

    public ListIterator iterator() {
	return new ListIterator() {
		private int cursor = 0;
		private int lastRet = -1;
        	/**
        	 * The modCount value that the iterator believes that the backing
        	 * object should have. If this expectation is violated, the iterator
        	 * has detected concurrent modification.
        	 */
        	private transient int expectedModCount = modCount;

		public boolean hasNext() {
		    return cursor < dimension();
		} 
        	public boolean hasPrevious() {
        	    return cursor != 0;
        	}

		public Object next() {
		    try {
            		Object next = get(cursor);
			checkForComodification();
            		lastRet = cursor++;
            		return next;
		    }
		    catch(IndexOutOfBoundsException e) {
			checkForComodification();
    	    		throw new NoSuchElementException();
        	    }
		} 
        	public Object previous() {
        	    try {
            		Object previous = get(--cursor);
            		checkForComodification();
            		lastRet = cursor;
            		return previous;
        	    } catch(IndexOutOfBoundsException e) {
            		checkForComodification();
            		throw new NoSuchElementException();
        	    }
        	}
        
        	public int nextIndex() {
        	    return cursor;
        	}
        
        	public int previousIndex() {
        	    return cursor-1;
        	}

		public void remove() {
        	    if (lastRet == -1)
			throw new IllegalStateException();
		    checkForComodification();
        
        	    try {
            		AbstractVector.this.remove(lastRet);
            		if (lastRet < cursor)
            		    cursor--;
            		lastRet = -1;
            		expectedModCount = modCount;
        	    }
        	    catch(IndexOutOfBoundsException e) {
			throw new ConcurrentModificationException();
        	    }
		} 
        	public void set(Object o) {
        	    if (!(o instanceof Arithmetic))
        	    	throw new IllegalArgumentException();
        	    if (lastRet == -1)
            		throw new IllegalStateException();
		    checkForComodification();
        
        	    try {
            		AbstractVector.this.set(lastRet, (Arithmetic/*>R<*/)o);
            		expectedModCount = modCount;
        	    } catch(IndexOutOfBoundsException e) {
			throw new ConcurrentModificationException();
        	    }
        	}
        
        	public void add(Object o) {
        	    if (!(o instanceof Arithmetic))
        	    	throw new IllegalArgumentException();
		    checkForComodification();
        
        	    try {
            		insert(cursor++, (Arithmetic/*>R<*/)o);
            		lastRet = -1;
            		expectedModCount = modCount;
        	    } catch(IndexOutOfBoundsException e) {
			throw new ConcurrentModificationException();
        	    }
        	}
        	
        	private final void checkForComodification() {
        	    if (modCount != expectedModCount)
			throw new ConcurrentModificationException();
        	}
	    };
    } 
    
    // norm
	
    /**
     * Returns a norm ||.|| of this Vector.
     * <p>
     * Usually returns the Euclidean norm ||.||<sub>2</sub> of this Vector.
     * This equals the length of the Vector in its vector space <b>R</b><sup>n</sup>.
     * The length is determined with Pythagoras triangles.</p>
     * <p>Also see "Jensen".</p>
     */
    public Real norm() {
	// return multiply(this);
	return norm(DefaultNorm);
    } 

    /**
     * default euclidian norm for geometrical distance in euclidian vector spaces
     */
    private static double DefaultNorm = 2;

    public Real norm(double p) {
	if (!(p >= 1))
	    throw new IllegalArgumentException("p-norm defined for p>=1");
	if (p == Double.POSITIVE_INFINITY)
	    return (Real/*__*/) Operations.sup.apply(Functionals.map(Functions.norm, this));
	return (Real/*__*/) Operations.power.apply(Operations.sum.apply(Functionals.map(Functions.pow(p), Functionals.map(Functions.norm, iterator()))), Values.getDefaultInstance().valueOf(1 / p));
    } 

    // arithmetic-operations

    public Arithmetic zero() {return Values.getDefaultInstance().ZERO(dimension());}
    public Arithmetic one() {throw new UnsupportedOperationException("vector spaces do not have a 1");}
    
    public Vector/*<R>*/ add(Vector/*<R>*/ b) {
	return (Vector) super.add((Tensor)b);
    }

    public Vector/*<R>*/ subtract(Vector/*<R>*/ b) {
	return (Vector) super.subtract((Tensor)b);
    } 

    /**
     * This implementation returns the standard scalar product<br />
     * (x,y) &#8614; &lang;x,y&rang; = x·y = x<sup>T</sup>&sdot;y = <big>&sum;</big><span class="doubleIndex"><sub>i=0</sub><sup>n-1</sup></span> x<sub>i</sub>*y<sub>i</sub>.
     */
    public Arithmetic/*>R<*/ multiply(Vector/*<R>*/ b) {
	Utility.pre(dimension() == b.dimension(), "vectors for dot-product must have equal dimension");
	// need Functionals.map(Function,Tensor) and Functionals.foldRight(Function,Object,Tensor)
	return (Arithmetic/*>R<*/) Functionals.foldRight(Operations.plus, Values.ZERO, Functionals.map(Operations.times, iterator(), b.iterator()));
    } 

    public Vector/*<R>*/ multiply(Scalar s) {
	//@todo outroduce
	return (Vector) scale(s);
    }
    public Vector/*<R>*/ scale(Scalar s) {
	return (Vector) scale((Arithmetic)s);
    }

    public Vector/*<R>*/ multiply(Matrix/*<R>*/ B) {
	Utility.pre(dimension() == B.dimension().height, "column-Vector v.A only defined for column-Vector multiplied with Matrix of same height. " + dimension() + "!=" + B.dimension().height);
	Vector/*<R>*/ ret = newInstance(B.dimension().width);	  // row-vector
	for (int i = 0; i < ret.dimension(); i++)
	    ret.set(i, multiply(B.getColumn(i)));
	return ret;
    } 

    // Arithmetic implementation
    // TODO: think about several implementations, whether further types could be multiplied,...
    public Arithmetic add(Arithmetic b) {
	return add((Vector) b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	return subtract((Vector) b);
    } 
    public Arithmetic multiply(Arithmetic b) {
	if (b instanceof Scalar)
	    return scale((Scalar) b);
	else if (b instanceof Matrix)
	    return multiply((Matrix) b);
	else if (b instanceof Vector)
	    return multiply((Vector) b);
	else if (b instanceof Tensor)
	    /* we explicitly refer to super here because somehow JDK1.4 complains about ambiguity */
	    return super.multiply((Tensor) b);
	else if (b instanceof Symbol || b instanceof orbital.math.functional.MathFunctor)
	    return scale(b);
	throw new IllegalArgumentException("wrong type: " + b.getClass());
    } 
    public Arithmetic inverse() {
	throw new UnsupportedOperationException("vector space is no field");
    } 
    public Arithmetic divide(Arithmetic b) {
	throw new UnsupportedOperationException("vector space is no field");
    } 
    public Arithmetic power(Arithmetic b) {
	throw new UnsupportedOperationException("vector space is no field");
    } 

    public Vector/*<R>*/ cross(Vector/*<R>*/ b) {
	Utility.pre((dimension() == 3 || dimension() == 2) && dimension() == b.dimension(), "domain of cross-product is 3D");
	Vector/*<R>*/ r = newInstance(3);
	if (dimension() == 3) {
	    r.set(0, (Arithmetic/*>R<*/) get(1).multiply(b.get(2)).subtract(get(2).multiply(b.get(1))));
	    r.set(1, (Arithmetic/*>R<*/) get(2).multiply(b.get(0)).subtract(get(0).multiply(b.get(2))));
	} else {
	    r.set(0, (Arithmetic/*>R<*/) Values.ZERO);
	    r.set(1, (Arithmetic/*>R<*/) Values.ZERO);
	}
	r.set(2, (Arithmetic/*>R<*/) get(0).multiply(b.get(1)).subtract(get(1).multiply(b.get(0))));
	return r;
    } 

    public Matrix/*<R>*/ transpose() {
	Matrix/*<R>*/ r = (Matrix) newInstance(new int[] {1, dimension()});
	r.setRow(0, this);
	return r;
    } 


    // Structural modifications

    public Vector/*<R>*/ insert(int index, Arithmetic/*>R<*/ v) {
	if (index != dimension())
	    validate(index);
	Arithmetic/*>R<*/[] a = new Arithmetic/*>R<*/[dimension() + 1];
	for (int i = 0; i < index; i++)
	    a[i] = get(i);
	a[index] = v;
	for (int i = index; i < dimension(); i++)
	    a[i + 1] = get(i);
	// updates modCount
	set(a);
	return this;
    } 
 
    public Vector/*<R>*/ insertAll(int index, Vector/*<R>*/ v) {
	if (index != dimension())
	    validate(index);
	Arithmetic/*>R<*/[] a = new Arithmetic/*>R<*/[dimension() + v.dimension()];
	for (int i = 0; i < index; i++)
	    a[i] = get(i);
	for (int i = 0; i < v.dimension(); i++)
	    a[index + i] = v.get(i);
	for (int i = index; i < dimension(); i++)
	    a[v.dimension() + i] = get(i);
	// updates modCount
	set(a);
	return this;
    }

    public Vector/*<R>*/ insert(Arithmetic/*>R<*/ v) {
	return insert(dimension(), v);
    } 

    public Vector/*<R>*/ insertAll(Vector/*<R>*/ v) {
	return insertAll(dimension(), v);
    } 

    public Vector/*<R>*/ remove(int index) {
	validate(index);
	Arithmetic/*>R<*/[] a = new Arithmetic/*>R<*/[dimension() - 1];
	for (int i = 0; i < index; i++)
	    a[i] = get(i);
	for (int i = index; i < a.length; i++)
	    a[i] = get(i + 1);
	// updates modCount
	set(a);
	return this;
    } 

    // tensor version
    
    public final int rank() {
	return 1;
    }

    public final int[] dimensions() {
	return new int[] {dimension()};
    }

    public final Arithmetic/*>R<*/ get(int[] i) {
	valid(i);
	return get(i[0]);
    }

    public final void set(int[] i, Arithmetic/*>R<*/ vi) {
	valid(i);
	set(i[0], vi);
    }

    public final Tensor subTensor(int[] i, int[] j) {
	valid(i);
	valid(j);
	return subVector(i[0], j[0]);
    }

    public final Tensor add(Tensor b) {
	return add((Vector)b);
    }
    public final Tensor subtract(Tensor b) {
	return subtract((Vector)b);
    }

    /**
     * Validate index within dimension.
     * @pre 0 <= i < dimension()
     * @post true
     * @throws ArrayIndexOutOfBoundsException if the index i is out of bounds.
     * @todo turn into an aspect, only.
     */
    final void validate(int i) {
	if (i < 0)
	    throw new ArrayIndexOutOfBoundsException("index (" + i + ") is negative");
	if (i >= dimension())
	    throw new ArrayIndexOutOfBoundsException("index (" + i + ") out of dimension (" + dimension() + ")");
    } 

    final void valid(int[] i) {
	if (i.length != rank())
	    throw new ArrayIndexOutOfBoundsException("illegal number of indices (" + i.length + " indices) for tensor of rank " + rank());
	validate(i[0]);
    } 
    
    public Arithmetic/*>R<*/[] toArray() {
	Arithmetic/*>R<*/[] a = new Arithmetic/*>R<*/[dimension()];
	for (int i = 0; i < dimension(); i++)
	    a[i] = get(i);
	return a;
    } 
    double[] toDoubleArray() {
	double[] a = new double[dimension()];
	for (int i = 0; i < dimension(); i++)
	    a[i] = getDoubleValue(i);
	return a;
    } 

    public String toString() {
	/*StringBuffer sb = new StringBuffer();
	  for (int i = 0; i < dimension(); i++)
	  sb.append((i == 0 ? "" : ",") + get(i));
	  return "(" + sb.toString() + ")";*/
	return ArithmeticFormat.getDefaultInstance().format(this);
    } 
}
