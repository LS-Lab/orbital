/**
 * @(#)AbstractMultivariatePolynomial.java 1.1 2002/08/21 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import orbital.math.functional.Function;
import java.util.ListIterator;
import java.util.Iterator;

import java.util.ConcurrentModificationException;

import orbital.math.functional.Functionals;
import orbital.logic.functor.BinaryFunction;

import orbital.math.functional.Operations;

import orbital.util.Setops;
import orbital.logic.functor.Predicates;
import orbital.util.Utility;
import java.util.Arrays;
import java.lang.reflect.Array;

import orbital.algorithm.Combinatorical;

/**
 * This implementation specially assumes S=<b>N</b><sup>n</sup>,
 * i.e. multivariate polynomials in the proper sense.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo introduce an instance the encodes monomials more efficiently
 * as bit-encoded in a long integer: perhaps 10 bit for each variable,
 * resulting in up to X^1024, and up to 64/10~=6 variables.  The
 * coefficients can then be stored in a HashSet. Perhaps, also sparse
 * tensors could be stored like that.
 */
public
abstract class AbstractMultivariatePolynomial/*<R extends Arithmetic>*/
    extends AbstractPolynomial/*<R,Vector<Integer>>*/ {
    private static final long serialVersionUID = -2237060189065872837L;
    
    /**
     * The index (0,...,0) of the constant term.
     * @invariants subclasses must set this value to {0,...,0}&isin;<b>N</b><sup>indexSet()</sup>
     */
    transient int[] CONSTANT_TERM;
  
    public boolean equals(Object o) {
        // would need dimensions() to reduce to non-zero part
        //return (o instanceof Polynomial) && super.equals(o);
        if (o instanceof Polynomial) {
            // optimized version of super.equals
            AbstractMultivariatePolynomial/*>T<*/ b = (AbstractMultivariatePolynomial) o;
            final int[] d = Functionals.map(Operations.max, dimensions(), b.dimensions());
            return Setops.all(iterator(d), b.iterator(d), Predicates.equal);
        } 
        return false;
    }
    public int hashCode() {
        return super.hashCode();
    }

    protected Object/*>Vector<Integer><*/ productIndexSet(Arithmetic/*>Polynomial<R,Vector<Integer>><*/ productObject) {
        //@xxx note that this ruins compatibility with instances of superclass, only
        return Values.getDefault().valueOf(dimensions());
    }

    //@xxx we do not ultimately need these following methods, but only have them for performance for S=<b>N</b><sup>n</sup>
    
    /**
     * Get the the dimensions of the representation of this polynomial with respect to the single variables.
     * @internal note the off by one difference of degrees() and dimensions(),
     * because degrees() means maximum and dimensions() means count.
     * Also dimensions() can be larger because representation need not be minimal (there can be additional zeros).
     * @see #degrees()
     */
    public/*protected*/ abstract int[] dimensions();

    /**
     * Get a tensor view of the coefficients.
     * @xxx somehow get rid of this trick
     */
    abstract Tensor tensorViewOfCoefficients();

    protected abstract Arithmetic get(int[] i);
    /**
     * Sets a value for the coefficient specified by index.
     * @preconditions i&isin;<b>N</b><sup>n</sup>
     * @throws UnsupportedOperationException if this polynomial is constant and does not allow modifications.
     */
    protected abstract void set(int[] i, Arithmetic vi);

    
    public int[] degrees() {
        final int degrees[] = new int[rank()];
        Arrays.fill(degrees, -1);
        Combinatorical cursor = Combinatorical.getPermutations(dimensions());
        //@todo can be optimized considerably by jumping forward within the permutations when nonzero index is already known.
        while (cursor.hasNext()) {
            int[] index = cursor.next();
            assert degrees.length == index.length;
            // non-zero coefficient?
            if (!get(index).isZero()) {
                // degrees = max(degrees, index)
                for (int i = 0; i < degrees.length; i++) {
                    if (index[i] > degrees[i]) {
                        degrees[i] = index[i];
                    }
                }
            }
        }
        return degrees;
    }
    
    // factory-methods
    
    /**
     * instantiate a new polynomial with storage for a polynomial of degree.
     * @param dim the dimension desired for the vector.
     * @return a vector of the same type as this, dimension as specified
     * The elements need not be initialized since they will soon be by the calling method.
     * @postconditions RES != RES
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected Polynomial/*<R,Vector<Integer>>*/ newInstance(Vector/*<Integer>*/ dimensions) {
        int[] pis = new int[dimensions.dimension()];
        for (int i = 0; i < pis.length; i++) {
            pis[i] = ((Integer)dimensions.get(i)).intValue();
        }
        return newInstance(pis);
    }
    /**
     * instantiate a new polynomial with storage for a polynomial of degree.
     * @param dim the dimension desired for the vector.
     * @return a vector of the same type as this, dimension as specified
     * The elements need not be initialized since they will soon be by the calling method.
     * @postconditions RES != RES
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see #newInstance(Vector)
     */
    protected abstract Polynomial/*<R,Vector<Integer>>*/ newInstance(int[] dimensions);
        
    protected final Arithmetic/*>Polynomial<R,Vector<Integer>><*/ newInstance(Object/*>Vector<Integer><*/ productIndexSet) {
        if (productIndexSet instanceof int[]) {
            return newInstance((int[])productIndexSet);
        } else {
            return newInstance((Vector)productIndexSet);
        }
            
    }

    // iterator-views
    
    public final ListIterator iterator() {
        return iterator(dimensions());
    }

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

    public Iterator indices() {
        return new ListIterator() {
                //@structure delegates to cursor wrapping int[] into Vector<Integer>
                private final Combinatorical cursor = Combinatorical.getPermutations(dimensions());
                /**
                 * The modCount value that the iterator believes that the backing
                 * object should have. If this expectation is violated, the iterator
                 * has detected concurrent modification.
                 */
                private transient int expectedModCount = modCount;
                public boolean hasNext() {
                    return cursor.hasNext();
                } 
                public Object next() {
                    try {
                        Object v = Values.getDefaultInstance().tensor(cursor.next());
                        checkForComodification();
                        return v;
                    }
                    catch(IndexOutOfBoundsException e) {
                        checkForComodification();
                        throw (AssertionError) new AssertionError("cursor should already have thrown a NoSuchElementException").initCause(e);
                    }
                } 
                public boolean hasPrevious() {
                    return cursor.hasPrevious();
                } 
                public Object previous() {
                    try {
                        Object v = Values.getDefaultInstance().tensor(cursor.previous());
                        checkForComodification();
                        return v;
                    }
                    catch(IndexOutOfBoundsException e) {
                        checkForComodification();
                        throw (AssertionError) new AssertionError("cursor should already have thrown a NoSuchElementException").initCause(e);
                    }
                } 

                // UnsupportedOperationException, categorically

                public void set(Object o) {
                    throw new UnsupportedOperationException("setting elements in an index set of a polynomial is undefined");
                }

                public int nextIndex() {
                    throw new UnsupportedOperationException("a polynomial does not have a one-dimensional index");
                }
                public int previousIndex() {
                    throw new UnsupportedOperationException("a polynomial does not have a one-dimensional index");
                }

                public void add(Object o) {
                    throw new UnsupportedOperationException("adding elements to an index set of a polynomial is undefined");
                } 
                public void remove() {
                    throw new UnsupportedOperationException("removing elements from an index set of a polynomial is undefined");
                } 

                private final void checkForComodification() {
                    if (modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
    } 

    /**
     * Provides an iterator over the coefficients of the specified dimensions.
     * @preconditions &forall;k dim[k]&ge;dimensions()[k]
     * @todo wouldn't Polynomial need to have this?
     * @internal almost identical to @see AbstractTensor.iterator()
     */
    ListIterator iterator(final int[] dim) {
        for (int k = 0; k < dim.length; k++)
            Utility.pre(dim[k]>=dimensions()[k], "forall k dim[k]>=dimensions()[k]");
        return new ListIterator() {
                private final Combinatorical cursor = Combinatorical.getPermutations(dim);
                //@internal we could just as well store lastRet as Vector<Integer> but int[] saves a lot of wrapping/unwrapping
                private int[] lastRet = null;
                /**
                 * The modCount value that the iterator believes that the backing
                 * object should have. If this expectation is violated, the iterator
                 * has detected concurrent modification.
                 */
                private transient int expectedModCount = modCount;
                public boolean hasNext() {
                    return cursor.hasNext();
                } 
                public Object next() {
                    try {
                        Object v = get(lastRet = (int[])cursor.next().clone());
                        checkForComodification();
                        return v;
                    }
                    catch(IndexOutOfBoundsException e) {
                        checkForComodification();
                        throw (AssertionError) new AssertionError("cursor should already have thrown a NoSuchElementException").initCause(e);
                    }
                } 
                public boolean hasPrevious() {
                    return cursor.hasPrevious();
                } 
                public Object previous() {
                    try {
                        Object v = get(lastRet = (int[])cursor.previous().clone());
                        checkForComodification();
                        return v;
                    }
                    catch(IndexOutOfBoundsException e) {
                        checkForComodification();
                        throw (AssertionError) new AssertionError("cursor should already have thrown a NoSuchElementException").initCause(e);
                    }
                } 

                public void set(Object o) {
                    if (!(o instanceof Arithmetic))
                        throw new IllegalArgumentException();
                    if (lastRet == null)
                        throw new IllegalStateException();
                    checkForComodification();
        
                    try {
                        AbstractMultivariatePolynomial.this.set(lastRet, (Arithmetic)o);
                        expectedModCount = modCount;
                    } catch(IndexOutOfBoundsException e) {
                        throw new ConcurrentModificationException();
                    }
                }

                // UnsupportedOperationException, categorically

                public int nextIndex() {
                    throw new UnsupportedOperationException("a polynomial does not have a one-dimensional index");
                }
                public int previousIndex() {
                    throw new UnsupportedOperationException("a polynomial does not have a one-dimensional index");
                }

                public void add(Object o) {
                    throw new UnsupportedOperationException("adding a single element from a polynomial is impossible");
                } 
                public void remove() {
                    throw new UnsupportedOperationException("removing a single element from a polynomial is impossible");
                } 

                private final void checkForComodification() {
                    if (modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
    } 
        
    public Arithmetic zero() {
        int[] dim = new int[((Integer)indexSet()).intValue()];
        Arrays.fill(dim, 1);
        AbstractMultivariatePolynomial r = (AbstractMultivariatePolynomial)newInstance(dim);
        r.set(CONSTANT_TERM, get(CONSTANT_TERM).zero());
        return r;
    }

    public Arithmetic one() {
        int[] dim = new int[((Integer)indexSet()).intValue()];
        Arrays.fill(dim, 1);
        AbstractMultivariatePolynomial r = (AbstractMultivariatePolynomial)newInstance(dim);
        r.set(CONSTANT_TERM, get(CONSTANT_TERM).one());
        return r;
    }

    //@todo we should also support adding other functions (like in AbstractFunctor)?

    protected Arithmetic operatorImpl(BinaryFunction op, Arithmetic bb) {
        // only cast since Polynomial does not (yet?) have iterator(int[])
        AbstractMultivariatePolynomial b = (AbstractMultivariatePolynomial)bb;
        if (!indexSet().equals(b.indexSet()))
            throw new IllegalArgumentException("a+b only defined for equal indexSet()");
        final int[] d = Functionals.map(Operations.max, dimensions(), b.dimensions());
        AbstractMultivariatePolynomial/*>T<*/ ret = (AbstractMultivariatePolynomial)newInstance(d);

        // component-wise
        ListIterator dst;
        Setops.copy(dst = ret.iterator(d), Functionals.map(op, iterator(d), b.iterator(d)));
        assert !dst.hasNext() : "equal dimensions for iterator view implies equal structure of iterators";
        return ret;
    }
    
    //@todo optimizable by far, but already optimized super.multiply
    public Polynomial/*<R,Vector<Integer>>*/ multiply(Polynomial/*<R,Vector<Integer>>*/ bb) {
        // only cast since Polynomial does not know an equivalent of dimensions()
        AbstractMultivariatePolynomial b = (AbstractMultivariatePolynomial)bb;
        if (isZero())
            return this;
        else if (b.isZero())
            return b;
        if (rank() != b.rank() || dimensions().length != b.dimensions().length) {
        	throw new IllegalArgumentException("Cannot multiply polynomials of different polynomial rings with " + dimensions() + " and " + b.dimensions() + " variables");
        }
        final int[] d = Functionals.map(Operations.plus, degrees(), b.degrees());
        for (int i = 0; i < d.length; i++) {
        	// dimensions[i]=degrees[i]+1
        	d[i]++;
        }
        Polynomial/*<R>*/ ret = newInstance(d);
        ((AbstractMultivariatePolynomial)ret).setZero();
        // ret = &sum;<sub>i&isin;dimensions()</sub> a<sub>i</sub>X<sup>i</sup> * b
        // perform (slow) multiplications on monomial base
        for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
            int[] i = index.next();
            // si = a<sub>i</sub>X<sup>i</sup> * b
            AbstractMultivariatePolynomial/*<R>*/ si = (AbstractMultivariatePolynomial)newInstance(Functionals.map(Operations.plus, i, b.dimensions()));
            si.setZero();
            final int[] sidim = si.dimensions();
            final int[] sidim_1 = new int[sidim.length];
            for (int k = 0; k < sidim_1.length; k++)
                sidim_1[k] = sidim[k] - 1;
            ((AbstractTensor)si.tensorViewOfCoefficients()).setSubTensor(i, sidim_1,
                         ((AbstractMultivariatePolynomial)b.scale(get(i))).tensorViewOfCoefficients());
            ret = ret.add(si);
        }
        return ret;
    }

}
