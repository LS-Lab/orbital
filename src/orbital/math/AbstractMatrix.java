/**
 * @(#)AbstractMatrix.java 1.0 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.ListIterator;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import orbital.util.Utility;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;
import orbital.math.functional.Functions;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @internal All matrix methods iterate row-wise.
 *  This means that the row (i) will be iterated over in the outer loop,
 *  and the columm (j) in the inner loop.
 *  Therefore, matrices A[i][j] build up of a two dimensional array, should have
 *  the row (i) as the first index in the array and the column (j) as the second
 *  to ensure all methods iterate linearly in memory and without stride.
 *  Due to C/Java memory storage conventions (storing columns consequtively),
 *  this has great effect on performance.
 *  Fortran memory storage conventions are exactly the other way around.
 * @version 1.0, 2000/08/08
 * @author  Andr&eacute; Platzer
 */
abstract class AbstractMatrix/*<R implements Arithmetic>*/ extends AbstractTensor implements Matrix/*<R>*/ {
    private static final Logger logger = Logger.getLogger(Matrix.class.getName());
    private static final long serialVersionUID = 1360625645424730123L;

    // factory-methods
    
    /**
     * Instantiates a new matrix with dimension dim of the same type like this.
     * <p>This method is a replacement for a constructor in the implementation of Matrix.</p>
     * @param dim the dimension desired for the matrix.
     * @return a matrix of the same type as this, dimension as specified.
     * The elements need not be initialized since they will soon be by the calling method.
     * @post RES != RES
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected abstract Matrix/*<R>*/ newInstance(Dimension dim);

    /**
     * Instantiates a new matrix with dimension dim of the same type like this.
     * <p>This method is a replacement for a constructor in the implementation of Matrix.</p>
     * @post RES != RES
     * @see #newInstance(Dimension)
     */
    protected final Matrix/*<R>*/ newInstance(int height, int width) {
	return newInstance(new Dimension(width, height));
    } 
    protected final Tensor/*<R>*/ newInstance(int[] dim) {
	return dim.length == 2 ? newInstance(dim[0], dim[1]) : Values.getDefaultInstance().newInstance(dim);
    }

    // get/set-methods
	
    /**
     * Returns the value at a position (i|j) if that can be converted to a double.
     * Convenience method.
     * @throws UnsupportedOperationException if the value can not be represented as a double.
     */
    public double getDoubleValue(int i, int j) {
	try {
	    return ((Number) get(i, j)).doubleValue();
	} catch (ClassCastException x) {
	    throw new UnsupportedOperationException("no real number");
	} 
    } 

    // iterator-views
	
    /**
     * The number of times this object has been structurally modified.
     * Structural modifications are those that change the number of elements in
     * the object or otherwise modify its internal structure.
     * This field is used to make iterators of the object fail-fast.
     * <p>
     * To use this feature, increase modCount whenever an implementation method changes
     * this matrix.</p>
     * @see java.util.ConcurrentModificationException
     */
    protected transient int modCount = 0;

    public ListIterator getColumns() {
	return new ListIterator() {
		private int cursor = 0;
		private int lastRet = -1;
        	/**
        	 * The modCount value that the iterator believes the backing
        	 * object should have. If this expectation is violated, the iterator
        	 * has detected concurrent modification.
        	 */
        	private transient int expectedModCount = modCount;

		public boolean hasNext() {
		    return cursor < dimension().width;
		} 
        	public boolean hasPrevious() {
        	    return cursor != 0;
        	}

		public Object next() {
		    try {
            		Object next = getColumn(cursor);
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
            		Object previous = getColumn(--cursor);
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
            		removeColumn(lastRet);
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
        	    if (!(o instanceof Vector))
        	    	throw new IllegalArgumentException();
        	    if (lastRet == -1)
            		throw new IllegalStateException();
		    checkForComodification();
        
        	    try {
            		setColumn(lastRet, (Vector)o);
            		expectedModCount = modCount;
        	    } catch(IndexOutOfBoundsException e) {
			throw new ConcurrentModificationException();
        	    }
        	}
        
        	public void add(Object o) {
        	    if (!(o instanceof Matrix))
        	    	throw new IllegalArgumentException();
		    checkForComodification();
        
        	    try {
            		insertColumns(cursor++, (Matrix)o);
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

    public ListIterator getRows() {
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
		    return cursor < dimension().height;
		} 
        	public boolean hasPrevious() {
        	    return cursor > 0;
        	}

		public Object next() {
		    try {
            		Object next = getRow(cursor);
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
            		Object previous = getRow(--cursor);
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
            		removeRow(lastRet);
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
        	    if (!(o instanceof Vector))
        	    	throw new IllegalArgumentException();
        	    if (lastRet == -1)
            		throw new IllegalStateException();
		    checkForComodification();
        
        	    try {
            		setRow(lastRet, (Vector)o);
            		expectedModCount = modCount;
        	    } catch(IndexOutOfBoundsException e) {
			throw new ConcurrentModificationException();
        	    }
        	}
        
        	public void add(Object o) {
        	    if (!(o instanceof Matrix))
        	    	throw new IllegalArgumentException();
		    checkForComodification();
        
        	    try {
            		insertRows(cursor++, (Matrix)o);
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

    public ListIterator iterator() {
	return new ListIterator() {
		private int i = 0;
		private int j = 0;
		private int lastRetI = -1;
		private int lastRetJ = -1;
        	/**
        	 * The modCount value that the iterator believes that the backing
        	 * object should have. If this expectation is violated, the iterator
        	 * has detected concurrent modification.
        	 */
        	private transient int expectedModCount = modCount;

		public boolean hasNext() {
		    return i < dimension().height && j < dimension().width;
		} 
		public Object next() {
		    if (!hasNext())
			throw new NoSuchElementException();
		    Object v = get(i, j);
		    checkForComodification();
		    lastRetI = i;
		    lastRetJ = j;
		    if (++j >= dimension().width) {
			j = 0;
			i++;
		    } 
		    return v;
		} 
		public boolean hasPrevious() {
		    return i > 0 || j > 0;
		} 
		public Object previous() {
		    if (!hasPrevious())
			throw new NoSuchElementException();
		    if (--j < 0) {
			j = dimension().width - 1;
			i--;
		    } 
		    Object v = get(i, j);
		    checkForComodification();
		    lastRetI = i;
		    lastRetJ = j;
		    return v;
		} 

        	public void set(Object o) {
        	    if (!(o instanceof Arithmetic))
        	    	throw new IllegalArgumentException();
		    assert (lastRetI == -1) == (lastRetJ == -1) : "initialization (or remove, or add) resets both variables";
        	    if (lastRetI == -1 && lastRetJ == -1)
            		throw new IllegalStateException();
		    checkForComodification();
        
        	    try {
            		AbstractMatrix.this.set(lastRetI, lastRetJ, (Arithmetic)o);
            		expectedModCount = modCount;
        	    } catch(IndexOutOfBoundsException e) {
			throw new ConcurrentModificationException();
        	    }
        	}

		// UnsupportedOperationException, categorically

		public int nextIndex() {
		    throw new UnsupportedOperationException("a matrix does not have a one-dimensional index");
		}
		public int previousIndex() {
		    throw new UnsupportedOperationException("a matrix does not have a one-dimensional index");
		}

		public void add(Object o) {
		    throw new UnsupportedOperationException("adding a single element to a matrix is impossible");
		} 
		public void remove() {
		    throw new UnsupportedOperationException("removing a single element from a matrix is impossible");
		} 

        	private final void checkForComodification() {
        	    if (modCount != expectedModCount)
			throw new ConcurrentModificationException();
        	}
	    };
    } 

    // sub-views
	
    public Vector/*<R>*/ getColumn(int c) {
	return new ColumnVector/*<R>*/(this, c);
    } 
    private static class ColumnVector/*<R implements Arithmetic>*/ extends AbstractVector/*<R>*/ {
	private static final long serialVersionUID = -5595085518698922020L;
    	/**
    	 * contains the matrix whose column vector we are.
    	 * @serial
    	 */
    	private final AbstractMatrix/*<R>*/ m;
    
    	/**
    	 * the column index of this column vector in the matrix m.
    	 * @serial
    	 */
    	private final int	 c;

    	/**
    	 * creates a new column vector of a matrix.
    	 */
    	public ColumnVector(AbstractMatrix/*<R>*/ m, int column) {
	    //@todo expectedModCount
	    m.validate(0, column);
	    this.m = m;
	    this.c = column;
    	}
    
    	protected Vector/*<R>*/ newInstance(int dim) {
	    return (Vector) m.newInstance(new int[] {dim});
    	} 
    
    	public final int dimension() {
	    return m.dimension().height;
    	} 
    
    	public Arithmetic/*>R<*/ get(int i) {
	    return m.get(i, c);
    	} 
    	public void set(int i, Arithmetic/*>R<*/ vi) {
	    m.set(i, c, vi);
    	} 
    	protected void set(Arithmetic/*>R<*/[] v) {
	    if (v.length != dimension())
		throw new UnsupportedOperationException("column vector cannot be altered. clone first");
	    throw new UnsupportedOperationException("Altering the underlying buffer of a column vector is not yet supported");
    	}
    
    	public Object clone() {
	    return new ArithmeticVector/*<R>*/(super.toArray());
    	} 
    }

    public void setColumn(int c, Vector/*<R>*/ col) throws UnsupportedOperationException {
	validate(col.dimension() - 1, c);
	Utility.pre(col.dimension() == dimension().height, "column vector and Matrix need compatible height (" + col.dimension() + "!=" + dimension().height + ")");
	//@todo can this be made functional?
	for (int i = 0; i < col.dimension(); i++)
	    set(i, c, col.get(i));
    } 

    public Vector/*<R>*/ getRow(int r) {
	return new RowVector/*<R>*/(this, r);
    } 
    private static class RowVector/*<R implements Arithmetic>*/ extends AbstractVector/*<R>*/ {
	private static final long serialVersionUID = -4915663894227454973L;
    	/**
    	 * contains the matrix whose row vector we are.
    	 * @serial
    	 */
    	private final AbstractMatrix/*<R>*/ m;
    
    	/**
    	 * the row index of this row vector in the matrix m.
    	 * @serial
    	 */
    	private final int	 r;

    	/**
    	 * creates a new row vector of a matrix.
    	 */
    	public RowVector(AbstractMatrix/*<R>*/ m, int row) {
	    m.validate(row, 0);
	    this.m = m;
	    this.r = row;
    	}
    
    	protected Vector/*<R>*/ newInstance(int dim) {
	    return (Vector) m.newInstance(new int[] {dim});
    	} 
    
    	public final int dimension() {
	    return m.dimension().width;
    	} 
    
    	public Arithmetic/*>R<*/ get(int j) {
	    return m.get(r, j);
    	} 
    	public void set(int j, Arithmetic/*>R<*/ vj) {
	    m.set(r, j, vj);
    	} 
    	protected void set(Arithmetic/*>R<*/[] v) {
	    if (v.length != dimension())
		throw new UnsupportedOperationException("row vector cannot be altered. clone first");
	    throw new UnsupportedOperationException("Altering the underlying buffer of a row vector is not yet supported");
    	}
    
    	public Object clone() {
	    return new ArithmeticVector/*<R>*/(super.toArray());
    	} 
    }

    public void setRow(int r, Vector/*<R>*/ row) throws UnsupportedOperationException {
	validate(r, row.dimension() - 1);
	Utility.pre(row.dimension() == dimension().width, "row vector and Matrix need compatible width (" + row.dimension() + "!=" + dimension().width + ")");
	for (int j = 0; j < row.dimension(); j++)
	    set(r, j, row.get(j));
    } 

    /**
     * Set all components of this matrix.
     * @param v the components the matrix should have from now on.
     *  The dimensions must not necessarily match the current ones.
     *  The first index in this array specifies the row, the second is for the column.
     * @pre v is rectangular, i.e. v[i].length == v[i-1].length
     * @post dimension().height == v.length && dimension().width == v[0].length && RES[i][j] == get(i, j)
     * @see #toArray()
     * @see #modCount
     * @see #set(int,int,Arithmetic)
     */
    protected abstract void set(Arithmetic/*>R<*/[][] v);

    public Matrix/*<R>*/ subMatrix(int i1, int i2, int j1, int j2) {
	return new SubMatrix/*<R>*/(this, i1, i2, j1, j2);
	// alternative implementation per shallow copy
	/*Utility.pre(i1 <= i2 && j1 <= j2, "Ending row cannot be less than starting row. Ending column cannot be less than starting column");
	  validate(i1, j1);
	  validate(i2, j2);
	  Matrix S = newInstance(i2 - i1 + 1, j2 - j1 + 1);
	  for (int i = i1; i <= i2; i++)
	  for (int j = j1; j <= j2; j++)
	  S.set(i - i1, j - j1, get(i, j));
	  return S;*/
    } 
    private static class SubMatrix/*<R implements Arithmetic>*/ extends AbstractMatrix/*<R>*/ {
	private static final long serialVersionUID = -3683869698064404738L;
    	/**
    	 * the Matrix whose sub-matrix we are.
    	 * @serial
    	 */
    	private final AbstractMatrix/*<R>*/ m;
    	/**
    	 * contains the column offset in m where this sub-view matrix starts.
    	 * @serial
    	 */
    	private final int    columnOffset;
    	/**
    	 * contains the column size in m which is the height of this sub-view matrix.
    	 * @serial
    	 */
    	private final int    height;
    	/**
    	 * contains the row offset in m where this sub-view matrix starts.
    	 * @serial
    	 */
    	private final int    rowOffset;
    	/**
    	 * contains the row size in m which is the width of this sub-view matrix.
    	 * @serial
    	 */
    	private final int    width;
    
    	/**
    	 * The modCount value that the iterator believes the backing
    	 * object should have. If this expectation is violated, the iterator
    	 * has detected concurrent modification.
    	 */
    	private transient int expectedModCount = 0;

    	public SubMatrix(AbstractMatrix/*<R>*/ m, int i1, int i2, int j1, int j2) {
	    Utility.pre(i1 <= i2 && j1 <= j2, "Ending row cannot be less than starting row. Ending column cannot be less than starting column");
	    m.validate(i1, j1);
	    m.validate(i2, j2);
	    this.m = m;
	    this.rowOffset = i1;
	    this.columnOffset = j1;
	    this.height = i2 - i1 + 1;
	    this.width = j2 - j1 + 1;
	    this.expectedModCount = m.modCount;
    	}
    
    	protected Matrix/*<R>*/ newInstance(Dimension dim) {
	    checkForComodification();
	    return m.newInstance(dim);
    	} 
    
    	public final Dimension dimension() {
	    checkForComodification();
	    return new Dimension(width, height);
    	} 
    
    	public Arithmetic/*>R<*/ get(int i, int j) {
	    validate(i, j);
	    checkForComodification();
	    return m.get(i + rowOffset, j + columnOffset);
    	} 
    	public void set(int i, int j, Arithmetic/*>R<*/ mij) {
	    validate(i, j);
	    checkForComodification();
	    m.set(i + rowOffset, j + columnOffset, mij);
    	} 
    
    	protected void set(Arithmetic/*>R<*/[][] v) {
	    if (dimension().height != v.length || dimension().width != v[0].length)
		throw new UnsupportedOperationException("sub-matrix cannot be altered. clone first");
	    checkForComodification();
	    throw new UnsupportedOperationException("Altering the underlying buffer of a sub-matrix is not yet supported");
            /*
	      expectedModCount = m.modCount;
	      modCount++;
            */
    	} 
    
    	public Object clone() {
	    checkForComodification();
	    return new ArithmeticMatrix/*<R>*/(super.toArray());
    	} 

    	private final void checkForComodification() {
    	    if (m.modCount != expectedModCount)
		throw new ConcurrentModificationException();
    	}
    }

    public Vector/*<R>*/ getDiagonal() {
	//@todo could also be seen as a view, in principle
	if (!isSquare())
	    throw new ArithmeticException("Only square matrices have a diagonal vector");
	Vector/*<R>*/ diagon = (Vector) newInstance(new int[] {dimension().height});
	for (int i = 0; i < diagon.dimension(); i++)
	    diagon.set(i, get(i, i));
	return diagon;
    } 

    public boolean isSquare() {
	return dimension().width == dimension().height;
    } 

    public boolean isSymmetric() throws ArithmeticException {
	if (!isSquare())
	    throw new ArithmeticException("Only square matrices can be symmetric");

	// optimized. it's enough to run through half the matrix excluding the diagonal
	for (int i = 0; i < dimension().height; i++)
	    for (int j = i + 1; j < dimension().width; j++)
		if (!get(i, j).equals(get(j, i)))
		    return false;
	return true;
    } 

    public int isDefinite() throws ArithmeticException {
	if (!isSquare())
	    throw new ArithmeticException("Only square matrices can be positive, negative or indefinite");

	//@todo optimize sometime
	// initialize with sign of the optimized determinante of (0:0,0:0)
	int sign = -MathUtilities.sign(Values.ZERO.compareTo(get(0, 0)));
	if (sign == 0)
	    throw new UnsupportedOperationException("only the test for positive definite and negative definite has been implemented yet");
	for (int i = 1; i < dimension().height; i++) {
	    int s = -MathUtilities.sign(Values.ZERO.compareTo(subMatrix(0,i, 0,i).det()));
	    if (s != sign)
		throw new UnsupportedOperationException("only the test for positive definite and negative definite has been implemented yet");
	}
	return sign;
    } 

    public boolean isInvertible() throws ArithmeticException {
	return !det().norm().equals(Values.ZERO);					// (!) applicable for Double.NaN as well
    } 
    public boolean isRegular() throws ArithmeticException {
	return isInvertible();
    }

    public int linearRank() {
	if (!isSquare())
	    throw new UnsupportedOperationException("linear rank not yet implemented for non-square matrices. Append 0s");
	return LUDecomposition.decompose(this).linearRank();
    }

    void swapColumns(int a, int b) {
	if (a == b)
	    return;
	Vector/*<R>*/ v = getColumn(a);
	setColumn(a, getColumn(b));
	setColumn(b, v);
    } 
    void swapRows(int a, int b) {
	if (a == b)
	    return;
	Vector/*<R>*/ v = getRow(a);
	setRow(a, getRow(b));
	setRow(b, v);
    } 


    /**
     * Returns the Frobenius norm of this Matrix.
     * <p>
     * <code>||A|| = &radic;<span class="text-decoration: overline">(&sum;<span class="doubleIndex"><sub>i=1</sub><sup>n</sup></span>&sum;<span class="doubleIndex"><sub>j=1</sub><sup>m</sup></span> |a<sub>ij</sub>|<sup>2</sup>)</code> the Frobenius norm.
     * </p>
     * <p>
     * Note that the Frobenius norm is not a p-norm.
     * It is a norm got by identifying matrices with vectors.</p>
     * @xxx after verifying use {@link AbstractTensor#norm()}.
     */
    public Real norm() {
	return (Real/*__*/) Functions.sqrt.apply(Operations.sum.apply(Functionals.map(Functions.square, Functionals.map(Functions.norm, iterator()))));
    } 

    public Real norm(double p) {
	Utility.pre(p >= 1, "p-norm defined for p>=1");
	final orbital.logic.functor.Function sumOfNorms = new orbital.logic.functor.Function() {
		//@internal this only is a composition with listable, but we require non-recursive map
		public Object apply(Object v) {
		    return Operations.sum.apply(orbital.math.functional.Functionals.map(Functions.norm, (Vector)v));
		}
	    };
	if (p == Double.POSITIVE_INFINITY) {
	    // norm of maximum row sum
	    return (Real) Operations.sup.apply(Functionals.map(sumOfNorms, getRows()));
	} else if (p == 1) {
	    // norm of maximum column sum
	    return (Real) Operations.sup.apply(Functionals.map(sumOfNorms, getColumns()));
	} else if (p == 2) {
	    // Spectral norm
	    // = Sqrt(maximum eigenvalue (A^* . A))
	    throw new UnsupportedOperationException("Spectral norm not yet implemented");
	} 
	throw new UnsupportedOperationException("only 1, 2 and infinity norms are provided");
    } 

    public Arithmetic/*>R<*/ trace() throws ArithmeticException {
	if (!isSquare())
	    throw new ArithmeticException("trace only defined for square matrices");
	return (Arithmetic/*>R<*/) Operations.sum.apply(getDiagonal());
    } 

    public Arithmetic/*>R<*/ det() throws ArithmeticException {
	if (!isSquare())
	    throw new ArithmeticException("determinant only defined for square matrices");
	// recursion base case
	if (dimension().width == 1)
	    return get(0, 0);
	if (dimension().width == 2)
	    return (Arithmetic/*>R<*/) get(0, 0).multiply(get(1, 1)).subtract(get(1, 0).multiply(get(0, 1)));

	Integer MINUS_ONE = (Integer) Values.ONE.minus();
	Arithmetic/*>R<*/  det = (Arithmetic/*>R<*/) Values.ZERO;
	// development of 0-th row
	Matrix/*<R>*/ innerMatrix = ((Matrix) clone()).removeRow(0);
	for (int j = 0; j < dimension().width; j++) {
	    // recursion
	    det = (Arithmetic/*>R<*/) det.add(((j & 1) == 0 ? Values.ONE : MINUS_ONE).multiply(get(0, j)).multiply(((Matrix) innerMatrix.clone()).removeColumn(j).det()));
	} 
	return det;
    } 

    // arithmetic-operations
	
    //@todo that's not quite true for strange R
    public Arithmetic zero() {return Values.getDefaultInstance().ZERO(dimension());}
    public Arithmetic one() {
    	if (!isSquare())
	    throw new UnsupportedOperationException("only square matrices have an identity matrix");
    	return Values.getDefaultInstance().IDENTITY(dimension());
    }
    
     public Matrix/*<R>*/ add(Matrix/*<R>*/ B) {
	 return (Matrix) super.add((Tensor) B);
     }

    public Matrix/*<R>*/ subtract(Matrix/*<R>*/ B) {
	 return (Matrix) super.subtract((Tensor) B);
    } 

    public Matrix/*<R>*/ multiply(Matrix/*<R>*/ B) {
	//@internal more beautiful than Tensor.multiply(Tensor) so still keep a while
	//@todo optimize like in RMatrix
	Utility.pre(dimension().width == B.dimension().height, "Matrix A.B only defined for dimension n x m multiplied with m x l");
	Matrix/*<R>*/ ret = newInstance(dimension().height, B.dimension().width);
	for (int i = 0; i < ret.dimension().height; i++)
	    for (int j = 0; j < ret.dimension().width; j++)
		ret.set(i, j, getRow(i).multiply(B.getColumn(j)));
	return ret;
    } 

    public Matrix/*<R>*/ multiply(Scalar s) {
	//@todo outroduce
	return (Matrix) scale(s);
    } 
    public Matrix/*<R>*/ scale(Scalar s) {
	return (Matrix) scale((Arithmetic)s);
    } 

    public Vector/*<R>*/ multiply(Vector/*<R>*/ B) {
	Utility.pre(dimension().width == B.dimension(), "row vector A.v only defined for Matrix multiplied with row vector of dimension width. " + dimension().width + "!=" + B.dimension());
	Vector/*<R>*/ ret = (Vector) newInstance(new int[] {dimension().height});	// column vector
	for (int i = 0; i < ret.dimension(); i++)
	    ret.set(i, getRow(i).multiply(B));
	return ret;
    } 

    // Arithmetic implementation

    public Arithmetic add(Arithmetic b) {
	return add((Matrix) b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	return subtract((Matrix) b);
    } 
    public Arithmetic multiply(Arithmetic b) {
	if (b instanceof Scalar)
	    return scale((Scalar) b);
	else if (b instanceof Vector)
	    return multiply((Vector) b);
	else if (b instanceof Matrix)
	    return multiply((Matrix) b);
	else if (b instanceof Tensor)
	    /* we explicitly refer to super here because somehow JDK1.4 complains about ambiguity */
	    return super.multiply((Tensor) b);
	throw new IllegalArgumentException("wrong type " + b.getClass());
    } 

    public Matrix/*<R>*/ transpose() {
	Matrix/*<R>*/ T = newInstance(dimension().width, dimension().height);
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		T.set(j, i, get(i, j));
	return T;
    } 

    public Matrix/*<R>*/ conjugate() {
	Matrix/*<R>*/ T = newInstance(dimension().width, dimension().height);
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++) {
		//@todo what to do for hyper matrices
		T.set(j, i, ((Complex) get(i,j)).conjugate());
	    }
	return T;
    } 
	

    // diverse decomposition algorithms

    /**
     * Returns the inverse matrix of this square matrix if exists.
     * <p>
     * Implemented as complete gaussian elemination algorithm.
     * </p>
     * @return if this matrix is regular return its inverse.
     * @throws ArithmeticException if this matrix is singular and cannot be inverted.
     * @pre isInvertible()
     * @post multiply(RES).equals(IDENTITIY(dimension()) && RES.multiply(this).equals(IDENTITY(dimension()) && RES.dimension().equals(dimension())
     * @note it has been proven that matrix inversion can be performed exactly as fast as matrix multiplication.
     * @todo optimize (also consider Willi Schönhauer)
     * @todo join both matrices and transform together up to half width
     * @todo unite with LUDecomposition?
     */
    public Arithmetic inverse() throws ArithmeticException {
	assert isSquare() : "only square matrices can be inverted " + this;
	Matrix/*<R>*/ A = (Matrix) clone();						
	Matrix/*<R>*/ AI = Values.getDefaultInstance().IDENTITY(dimension().width, dimension().width);
	// now transform
	// A        ---> Identity
	// Identity ---> A^-1

	// transform the two matrices equally
	for (int i = 0; i < dimension().width; i++) {

	    // transform a[i|i]-->1 (diagonal 1s)
	    Arithmetic ap = A.get(i, i);
	    if (ap.norm().equals(Values.ZERO))
		throw new UnsupportedOperationException("pivot: diagonal 0s are not expected, must use pivot. found " + ap);
	    Arithmetic apinv = ap.inverse();

	    // divide row by a[i|i]
	    //FIXME: for hypermatrix, mixed Scalar&times;Matrix operations +, - must exist
	    // for +, - , multiplication with IdentityMatrix might prove ok, but don't change * and /
	    // println(A.getRow(i)+"*"+apinv);
	    // apinv must be treated as if we'd multiply with a scalar here, for hypermatrix
	    // perhaps tensor products would help?

	    // row to be transformed
	    Vector urow = (Vector) A.getRow(i).scale(apinv);
	    A.setRow(i, urow);
	    Vector urowI = (Vector) AI.getRow(i).scale(apinv);
	    AI.setRow(i, urowI);
	    for (int j = 0; j < dimension().height; j++) {
		if (j == i)
		    continue;
		// transform a[i|j]-->0 (j!=i)
		// subtract from all other rows a multiple of the row urow
		Arithmetic f = A.get(j, i);
		if (f.norm().equals(Values.ZERO))
		    continue;
		if (logger.isLoggable(Level.FINEST))
		    logger.log(Level.FINEST, "Matrix.inverse() \t{0} - {1}\n\t\t{2} - {3}", new Object[] {A.getRow(j), urow.scale(f), AI.getRow(j), urowI.scale(f)});
		A.setRow(j, (Vector) A.getRow(j).subtract(urow.scale(f)));
		AI.setRow(j, (Vector) AI.getRow(j).subtract(urowI.scale(f)));
	    } 
	} 

	if (!MathUtilities.equalsCa(A, Values.getDefaultInstance().IDENTITY(dimension().width, dimension().width))) {
	    logger.log(Level.FINEST, "found a supposed inverse:\n{0} but failed to transform to identity matrix:\n{1} ({2})", new Object[] {AI, A, A.getClass()});
	    assert !isInvertible() : "a matrix is singular <=> determinant=0 <=> it cannot be inverted (apart from numerical uncertainty)";
	    throw new ArithmeticException("NoninvertibleMatrixException: singular matrix");
	} 
	return AI;
    } 

    public Matrix/*<R>*/ pseudoInverse() {
	//@TODO: implement with QR-decompositon and so on (for more general matrices of lesser rank)

	// assume Rank A = min {n=dimension().height, m=dimension().width}
	// pseudoInverse is unambiguous, then
	if (dimension().width <= dimension().height) {
	    // M = A^T.A
	    Matrix/*<R>*/ M = transpose().multiply(this);
	    if (!M.isInvertible())
		throw new UnsupportedOperationException("not yet implemented for matrices with lesser rank (ambiguous)");

	    // (A^T.A)^-1.A^T for Rank A = m <= n
	    return ((Matrix) M.inverse()).multiply(transpose());
	} else {	// dimension().height < dimension().width
			// M = A.A^T
	    Matrix/*<R>*/ M = this.multiply(transpose());
	    if (!M.isInvertible())
		throw new UnsupportedOperationException("not yet implemented for matrices with lesser rank (ambiguous)");

	    // A^T.(A.A^T)^-1 for Rank A = n < m
	    return (Matrix) transpose().multiply((Matrix) M.inverse());
	} 
    } 



    // Structural manipulations

    public Matrix/*<R>*/ insertColumns(int index, Matrix/*<R>*/ cols) {
	if (index != dimension().width)
	    validate(0, index);
	Utility.pre(dimension().height == cols.dimension().height, "Matrix must have same height (number of rows)");
	Arithmetic/*>R<*/[][] A = new Arithmetic/*>R<*/[dimension().height][dimension().width + cols.dimension().width];
	for (int i = 0; i < dimension().height; i++) {
	    for (int j = 0; j < index; j++)
		A[i][j] = get(i, j);
	    for (int j = 0; j < cols.dimension().width; j++)
		A[i][index + j] = cols.get(i, j);
	    for (int j = index; j < dimension().width; j++)
		A[i][cols.dimension().width + j] = get(i, j);
	}
	set(A);
	return this;
    }

    public Matrix/*<R>*/ insertRows(int index, Matrix/*<R>*/ rows) {
	if (index != dimension().height)
	    validate(index, 0);
	Utility.pre(dimension().width == rows.dimension().width, "Matrix must have same width (number of columns)");
	Arithmetic/*>R<*/[][] A = new Arithmetic/*>R<*/[dimension().height + rows.dimension().height][dimension().width];
	for (int i = 0; i < index; i++)
	    for (int j = 0; j < dimension().width; j++)
		A[i][j] = get(i, j);
	for (int i = 0; i < rows.dimension().height; i++)
	    for (int j = 0; j < rows.dimension().width; j++)
		A[index + i][j] = rows.get(i, j);
	for (int i = index; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		A[rows.dimension().height + i][j] = get(i, j);
	set(A);
	return this;
    } 

    public Matrix/*<R>*/ insertColumns(Matrix/*<R>*/ cols) {
	return insertColumns(dimension().width, cols);
    } 

    public Matrix/*<R>*/ insertRows(Matrix/*<R>*/ rows) {
	return insertRows(dimension().height, rows);
    } 

    public Matrix/*<R>*/ removeColumn(int c) {
	validate(0, c);
	Arithmetic/*>R<*/[][] A = new Arithmetic/*>R<*/[dimension().height][dimension().width - 1];
	for (int i = 0; i < dimension().height; i++) {
	    for (int j = 0; j < c; j++)
		A[i][j] = get(i, j);
	    for (int j = c + 1; j < dimension().width; j++)
		A[i][j - 1] = get(i, j);
	} 
	set(A);
	return this;
    } 

    public Matrix/*<R>*/ removeRow(int r) {
	validate(r, 0);
	Arithmetic/*>R<*/[][] A = new Arithmetic/*>R<*/[dimension().height - 1][dimension().width];
	for (int i = 0; i < r; i++)
	    for (int j = 0; j < dimension().width; j++)
		A[i][j] = get(i, j);
	for (int i = r + 1; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		A[i - 1][j] = get(i, j);
	set(A);
	return this;
    } 

    // tensor version
    
    public final int rank() {
	return 2;
    }

    public final int[] dimensions() {
	Dimension dim = dimension();
	return new int[] {dim.height, dim.width};
    }

    public final Arithmetic/*>R<*/ get(int[] i) {
	valid(i);
	return get(i[0], i[1]);
    }

    public final void set(int[] i, Arithmetic/*>R<*/ vi) {
	valid(i);
	set(i[0], i[1], vi);
    }

    public final Tensor subTensor(int[] i, int[] j) {
	valid(i);
	valid(j);
	return subMatrix(i[0], j[0], i[1], j[1]);
    }

    public final Tensor add(Tensor b) {
	return add((Matrix)b);
    }
    public final Tensor subtract(Tensor b) {
	return subtract((Matrix)b);
    }

    /**
     * Validate (i|j) indices within dimension.
     * @pre 0 <= i < dimension().height && 0 <= j < dimension().width
     * @post true
     * @throws ArrayIndexOutOfBoundsException if the index (i|j) is out of bounds for columns or rows.
     * @todo turn into an aspect, only.
     */
    final void validate(int i, int j) {
	if (i < 0)
	    throw new ArrayIndexOutOfBoundsException("Row index (" + i + ") is negative");
	if (j < 0)
	    throw new ArrayIndexOutOfBoundsException("Column index (" + j + ") is negative");
	if (i >= dimension().height)
	    throw new ArrayIndexOutOfBoundsException("Row index (" + i + ") out of number of rows (" + dimension().height + ")");
	if (j >= dimension().width)
	    throw new ArrayIndexOutOfBoundsException("Column index (" + j + ") out of number of columns (" + dimension().width + ")");
    }
    final void valid(int[] i) {
	if (i.length != rank())
	    throw new ArrayIndexOutOfBoundsException("illegal number of indices (" + i.length + " indices) for tensor of rank " + rank());
	validate(i[0], i[1]);
    } 

    public Arithmetic/*>R<*/[][] toArray() {
	Arithmetic/*>R<*/[][] a = new Arithmetic/*>R<*/ [dimension().height][dimension().width];
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		a[i][j] = get(i, j);
	return a;
    } 
    double[][] toDoubleArray() {
	double[][] a = new double[dimension().height][dimension().width];
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		a[i][j] = getDoubleValue(i, j);
	return a;
    } 

    public String toString() {
	return ArithmeticFormat.getDefaultInstance().format(this);
    } 
}
