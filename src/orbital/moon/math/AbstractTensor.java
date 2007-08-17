/**
 * @(#)AbstractTensor.java 1.0 2002-08-07 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;

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
import orbital.logic.functor.Predicate;
import orbital.logic.functor.Predicates;

import java.util.HashSet;

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
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
abstract class AbstractTensor/*<R extends Arithmetic>*/
    extends AbstractProductArithmetic/*<R,int[],Tensor<R>>*/ implements Tensor/*<R>*/, Serializable {
    private static final long serialVersionUID = 7889937971348824822L;

    // object-methods
        
    /**
     * Checks two tensors for equality.
     */
    public boolean equals(Object o) {
        return (o instanceof Tensor) && super.equals(o);
    }
    
    public int hashCode() {
        return super.hashCode();
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
     * @postconditions RES != RES
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected abstract Tensor/*<R>*/ newInstance(int[] dim);

    protected final Arithmetic/*>Tensor<R><*/ newInstance(Object productIndexSet) {
        return newInstance((int[]) productIndexSet);
    }

    // product

    protected Object productIndexSet(Arithmetic/*>Tensor<R><*/ productObject) {
        return ((Tensor)productObject).dimensions();
    }

    protected ListIterator/*<R>*/ iterator(Arithmetic/*>Tensor<R><*/ productObject) {
        return ((Tensor)productObject).iterator();
    }
    
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

    public ListIterator iterator() {
        return new ListIterator() {
                private Combinatorical cursor = Combinatorical.getPermutations(dimensions());
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
                        AbstractTensor.this.set(lastRet, (Arithmetic)o);
                        expectedModCount = modCount;
                    } catch(IndexOutOfBoundsException e) {
                        throw new ConcurrentModificationException();
                    }
                }

                // UnsupportedOperationException, categorically

                public int nextIndex() {
                    throw new UnsupportedOperationException("a tensor does not have a one-dimensional index");
                }
                public int previousIndex() {
                    throw new UnsupportedOperationException("a tensor does not have a one-dimensional index");
                }

                public void add(Object o) {
                    throw new UnsupportedOperationException("adding a single element from a tensor is impossible");
                } 
                public void remove() {
                    throw new UnsupportedOperationException("removing a single element from a tensor is impossible");
                } 

                private final void checkForComodification() {
                    if (modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
    } 

    // sub-views
        
    /**
     * Grants access to a tensor with automatic index transformation.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @structure delegates m:AbstractTensor (minimal part)
     */
    static abstract class TransformedAccessTensor/*<R extends Arithmetic>*/ extends AbstractTensor/*<R>*/ {
        private static final long serialVersionUID = -3609507213928180122L;
        /**
         * the Tensor to which we grant (transformed) access.
         * @serial
         */
        private final AbstractTensor/*<R>*/ m;
    
        /**
         * The modCount value that the iterator believes the backing
         * object should have. If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        private transient int expectedModCount = 0;

        protected TransformedAccessTensor(AbstractTensor/*<R>*/ m) {
            this.m = m;
            this.expectedModCount = m.modCount;
        }

        protected final AbstractTensor/*<R>*/ getDelegatee() {
            return m;
        }


        /**
         * Returns the transformed index.  Used by {@link
         * #get(int[])}, and {@link #set(int[],Arithmetic)} for index
         * transformation.  However, neither {@link #dimensions()},
         * nor {@link #rank()} could guess any changes in dimension
         * that the index transformation causes.
         */
        protected abstract int[] transformIndex(int[] index);
    
        protected Tensor/*<R>*/ newInstance(int[] dimensions) {
            checkForComodification();
            return m.newInstance(dimensions);
        }

        public int rank() {
            return m.rank();
        }
    
        public int[] dimensions() {
            checkForComodification();
            return m.dimensions();
        } 
    
        public Arithmetic/*>R<*/ get(int[] i) {
            validate(i);
            checkForComodification();
            return m.get(transformIndex(i));
        } 
        public void set(int[] i, Arithmetic/*>R<*/ mi) {
            validate(i);
            checkForComodification();
            m.set(transformIndex(i), mi);
        } 
    
        public Object clone() {
            checkForComodification();
            return new ArithmeticTensor/*<R>*/(super.toArray__Tensor());
        } 

        protected final void checkForComodification() {
            if (m.modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    public Tensor/*<R>*/ subTensor(int[] i1, int[] i2) {
        return new SubTensor(this, i1, i2);
    } 
    private static class SubTensor/*<R extends Arithmetic>*/ extends TransformedAccessTensor/*<R>*/ {
        private static final long serialVersionUID = -8431476748988993108L;
        /**
         * contains the offsets in m where this sub-view tensor starts.
         * @serial
         */
        private final int[]    offset;
        /**
         * contains the dimension in m that this sub-view tensor ranges over.
         * @serial
         */
        private final int[]    dim;

        public SubTensor(AbstractTensor/*<R>*/ m, int[] i1, int[] i2) {
            super(m);
            Utility.pre(i1.length == m.rank() && i2.length == m.rank(), "indices must be of correct rank.");
            Utility.pre(Setops.all(Values.getDefaultInstance().valueOf(i1).iterator(), Values.getDefaultInstance().valueOf(i2).iterator(), Predicates.lessEqual), "Ending indices cannot be less than starting indices.");
            m.validate(i1);
            m.validate(i2);
            this.offset = i1;
            this.dim = new int[i1.length];
            for (int k = 0; k < dim.length; k++)
                dim[k] =  i2[k] - i1[k] + 1;
        }
    
        public final int[] dimensions() {
            checkForComodification();
            return (int[]) dim.clone();
        } 

        protected int[] transformIndex(int[] i) {
            int[] itranslated = new int[i.length];
            for (int k = 0; k < i.length; k++)
                itranslated[k] = offset[k] + i[k];
            return itranslated;
        }
    }

    public Tensor/*<R>*/ subTensor(int level, int index) {
        return new PartTensor/*<R>*/(this, level, index);
    } 
    private static class PartTensor/*<R extends Arithmetic>*/ extends TransformedAccessTensor/*<R>*/ {
        private static final long serialVersionUID = 4545087879048756777L;
        /**
         * the level l of indices to fix for this view.
         * @serial
         */
        private final int        level;
        
        /**
         * the the index c<sub>l</sub> of the tensor part view at the level-th index.
         * @serial
         */
        private final int        index;

        /**
         * creates a new part tensor of a tensor.
         */
        public PartTensor(AbstractTensor/*<R>*/ m, int level, int index) {
            super(m);
            Utility.pre(0 <= level && level < m.rank(), "level is within the rank");
            Utility.pre(0 <= index && index < m.dimensions()[level], "index is within the dimensions");
            this.level = level;
            this.index = index;
        }
    
        public int rank() {
            return getDelegatee().rank() - 1;
        }
    
        public final int[] dimensions() {
            checkForComodification();
            //@internal optimizable cache
            int[] dim = getDelegatee().dimensions();
            int[] dimTranslated = new int[dim.length - 1];
            System.arraycopy(dim, 0, dimTranslated, 0, level);
            System.arraycopy(dim, level + 1, dimTranslated, level, dim.length - (level + 1));
            return dimTranslated;
        } 

        protected final int[] transformIndex(int[] i) {
            int[] itranslated = new int[i.length + 1];
            System.arraycopy(i, 0, itranslated, 0, level);
            itranslated[level] = index;
            System.arraycopy(i, level, itranslated, level + 1, i.length - level);
            return itranslated;
        }
    }

    public Tensor/*<R>*/ subTensorTransposed(int[] permutation) {
        return new TransposedTensor(this, permutation);
    } 
    private static class TransposedTensor/*<R extends Arithmetic>*/ extends TransformedAccessTensor/*<R>*/ {
        private static final long serialVersionUID = 590361721474800306L;
        /**
         * contains the index permutation for this tensor.
         * @serial
         */
        private final int[]    permutation;

        public TransposedTensor(final AbstractTensor/*<R>*/ m, int[] permutation) {
            super(m);
            Utility.pre(permutation.length == m.rank(), "indices must be of correct rank.");
            Utility.pre(Setops.all(Values.getDefaultInstance().valueOf(permutation).iterator(), new Predicate() {
                    public boolean apply(Object o) {
                        return (o instanceof Integer) && MathUtilities.isin(((Integer)o).intValue(), 0, m.rank() - 1);
                    }
                }), "The mapping table of a permutation in S_n contains the integers {0,...,n-1}.");
            //@see Setops.hasDuplicates
            Utility.pre(new HashSet(Setops.asList(Values.getDefaultInstance().valueOf(permutation).iterator())).size() == m.rank(), "A permutation is bijective, so its mapping table should not contain duplicates.");
            this.permutation = permutation;
        }
    
        public final int[] dimensions() {
            checkForComodification();
            return transformIndex(getDelegatee().dimensions());
        } 

        protected int[] transformIndex(int[] i) {
            int[] itranslated = new int[i.length];
            for (int k = 0; k < i.length; k++)
                itranslated[k] = i[permutation[k]];
            return itranslated;
        }
    }
    public void setSubTensor(int level, int index, Tensor/*<R>*/ part) {
        Tensor embed = subTensor(level, index);
        Utility.pre(part.rank() == rank()-1, "part has compatible rank");
        Utility.pre(Utility.equalsAll(part.dimensions(), embed.dimensions()), "part has compatible dimensions");
        ListIterator dst;
        Setops.copy(dst = embed.iterator(), part.iterator());
        assert !dst.hasNext() : "equal dimensions have iterators of equal length";
    } 
    public void setSubTensor(int[] i1, int[] i2, Tensor/*<R>*/ sub) {
        Tensor embed = subTensor(i1, i2);
        Utility.pre(sub.rank() == rank(), "sub tensor has compatible rank");
        Utility.pre(Utility.equalsAll(sub.dimensions(), embed.dimensions()), "sub tensor has compatible dimensions");
        ListIterator dst;
        Setops.copy(dst = embed.iterator(), sub.iterator());
        assert !dst.hasNext() : "equal dimensions have iterators of equal length";
    } 


    public Real norm() {
        //@todo verify that this really is a norm
        return (Real/*__*/) Functions.sqrt.apply(Operations.sum.apply(Functionals.map(Functions.square, Functionals.map(Functions.norm, iterator()))));
    } 

    // arithmetic-operations
        
    //@todo that's not quite true for strange R
    public Arithmetic zero() {return Values.getDefaultInstance().ZERO(dimensions());}
    public Arithmetic one() {
        throw new UnsupportedOperationException();
    }
    
    public Tensor/*<R>*/ add(Tensor/*<R>*/ B) {
        return (Tensor)super.add((Arithmetic)B);
    }

    public Tensor/*<R>*/ subtract(Tensor/*<R>*/ B) {
        return (Tensor)super.subtract((Arithmetic)B);
    } 

    public Arithmetic scale(Arithmetic s) {
        return (Tensor)super.scale(s);
    } 

    public Arithmetic divide(Arithmetic b) {
        if (b instanceof Scalar)
	    return scale(b.inverse());
	else
	    return super.divide(b);
    } 

    public Tensor/*<R>*/ multiply(Tensor/*<R>*/ b) {
        //@todo beautify with subTensor(...), or would that lack performance
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
        return (Tensor)super.scale(b);
    }

    // Arithmetic implementation

    public Arithmetic add(Arithmetic b) {
        return add((Tensor) b);
    } 
    public Arithmetic subtract(Arithmetic b) {
        return subtract((Tensor) b);
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
     * @preconditions 0 <= i < dimension().height && 0 <= j < dimension().width
     * @postconditions true
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
     * @note once we have covariant return-types, rename to toArray().
     */
    public Object/*>R<*/[] toArray__Tensor() {
        final int[] dim = dimensions();
        final Object[] r = (Object[]) Array.newInstance(Arithmetic/*>R<*/.class, dim);
        for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
            int[] i = index.next();
            Utility.setPart(r, i, (Arithmetic/*>R<*/) get(i));
        }
        return r;
    } 
}
