/**
 * @(#)AbstractMultinomial.java 1.0 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.math.functional.Function;
import java.io.Serializable;

import java.util.NoSuchElementException;
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

import java.util.ListIterator;

abstract class AbstractMultinomial/*<R implements Arithmetic>*/ extends AbstractProductArithmetic implements Multinomial/*<R>*/, Serializable {
    //private static final long serialVersionUID = 0;
	
    /**
     * The index (0,...,0) of the constant term.
     * @invariant subclasses must set this value to {0,...,0}&isin;<b>N</b><sup>numberOfVariables()</sup>
     */
    transient int[] CONSTANT_TERM;
    public AbstractMultinomial() {
    }
  
    public boolean equals(Object o) {
	// would need dimensions() to reduce to non-zero part
    	//return (o instanceof Multinomial) && super.equals(o);
	if (o instanceof Multinomial) {
	    AbstractMultinomial/*>T<*/ b = (AbstractMultinomial) o;
	    final int[] d = Functionals.map(Operations.max, dimensions(), b.dimensions());
	    return Setops.all(iterator(d), b.iterator(d), Predicates.equal);
	} 
	return false;
    } 

    public int hashCode() {
	//@xxx throw new UnsupportedOperationException("would require dimensions() to reduce to non-zero part");
	// the following is ok (though, perhaps, not ver surjective), since 0 has hashCode 0 anyway
	int hash = 0;
	//@todo functional?
	for (java.util.Iterator i = iterator(this); i.hasNext(); ) {
	    Object e = i.next();
	    hash += e == null ? 0 : e.hashCode();
	} 
	return hash;
    }

    public Integer degree() {
	return Values.valueOf(degreeValue());
    }

    /**
     * Get a tensor view of the coefficients.
     * @xxx somehow get rid of this trick
     */
    abstract Tensor tensorViewOfCoefficients();

    /**
     * Sets a value for the coefficient specified by index.
     * @pre i&isin;<b>N</b><sup>n</sup>
     * @throws UnsupportedOperationException if this polynomial is constant and does not allow modifications.
     */
    protected abstract void set(int[] i, Arithmetic vi);
	
    protected final Object productIndexSet(Arithmetic/*>T<*/ productObject) {
	return dimensions();
    }

    protected ListIterator/*_<R>_*/ iterator(Arithmetic/*>T<*/ productObject) {
	return ((Multinomial)productObject).iterator();
    }
    
    // factory-methods
    
    /**
     * instantiate a new polynomial with storage for a polynomial of degree.
     * @param dim the dimension desired for the vector.
     * @return a vector of the same type as this, dimension as specified
     * The elements need not be initialized since they will soon be by the calling method.
     * @post RES != RES
     * @see <a href="{@docRoot}/DesignPatterns/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected abstract Multinomial/*<R>*/ newInstance(int[] dimensions);
	
    protected final Arithmetic/*>T<*/ newInstance(Object productIndexSet) {
	return newInstance((int[])productIndexSet);
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

    /**
     * Provides an iterator over the coefficients of the specified dimensions.
     * @pre &forall;k dim[k]&ge;dimensions()[k]
     * @todo wouldn't Multinomial need to have this?
     * @internal almost identical to @see AbstractTensor.iterator()
     */
    ListIterator iterator(final int[] dim) {
	for (int k = 0; k < dim.length; k++)
	    Utility.pre(dim[k]>=dimensions()[k], "forall k dim[k]>=dimensions()[k]");
	return new ListIterator() {
		private Combinatorical cursor = Combinatorical.getPermutations(dim);
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
            		AbstractMultinomial.this.set(lastRet, (Arithmetic)o);
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
	
    /**
     * Evaluate this multinomial at <var>a</var>.
     * Using the "Einsetzungshomomorphismus".
     * @return f(a) = f(X)|<sub>X=a</sub> = (f(X) mod (X-a))
     * @todo copy horner scheme to examples
     * @todo we could just as well generalize the argument and return type of R
     */
    public Object/*>R<*/ apply(Object/*>R<*/ a) {
	throw new UnsupportedOperationException("not yet implemented");
    }	

    public Function derive() {
	throw new UnsupportedOperationException("not yet implemented");
    } 

    public Function integrate() {
	throw new ArithmeticException();
    }

    public Arithmetic zero() {
	int[] dim = new int[numberOfVariables()];
	Arrays.fill(dim, 1);
 	Object r = Array.newInstance(Arithmetic/*>R<*/.class, dim);
	Utility.setPart(r, CONSTANT_TERM, get(CONSTANT_TERM).zero());
	return Values.multinomial(r);
    }

    public Arithmetic one() {
	int[] dim = new int[numberOfVariables()];
	Arrays.fill(dim, 1);
 	Object r = Array.newInstance(Arithmetic/*>R<*/.class, dim);
	Utility.setPart(r, CONSTANT_TERM, get(CONSTANT_TERM).one());
	return Values.multinomial(r);
    }

    public Real norm() {
    	return degreeValue() < 0 ? Values.ZERO : Values.POSITIVE_INFINITY;
    }

    //@todo we should also support adding other functions (like in AbstractFunctor)?
	
    public Arithmetic add(Arithmetic b) {
	return add((Multinomial)b);
    }
    public Multinomial/*<R>*/ add(Multinomial/*<R>*/ bb) {
	// only cast since Multinomial does not yet have dimensions()
	AbstractMultinomial b = (AbstractMultinomial)bb;
	if (numberOfVariables() != b.numberOfVariables())
	    throw new IllegalArgumentException("a+b only defined for equal numberOfVariables()");
	final int[] d = Functionals.map(Operations.max, dimensions(), b.dimensions());
	AbstractMultinomial/*>T<*/ ret = (AbstractMultinomial)newInstance(d);

	// component-wise
	ListIterator dst;
	Setops.copy(dst = ret.iterator(d), Functionals.map(Operations.plus, iterator(d), b.iterator(d)));
	assert !dst.hasNext() : "equal dimensions for iterator view implies equal structure of iterators";
	return ret;
    }
	
    public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
	return subtract((Multinomial)b);
    } 
    public Multinomial/*<R>*/ subtract(Multinomial/*<R>*/ b) {
	return (Multinomial) add(b.minus());
    }

    public Arithmetic multiply(Arithmetic b) {
	return multiply((Multinomial)b);
    }

    //@todo optimizable by far
    public Multinomial/*<R>*/ multiply(Multinomial/*<R>*/ bb) {
	// only cast since Multinomial does not yet have dimensions()
	AbstractMultinomial b = (AbstractMultinomial)bb;
	if (degreeValue() < 0)
	    return this;
	else if (b.degreeValue() < 0)
	    return b;
	final int[] d = Functionals.map(Operations.plus, dimensions(), b.dimensions());
	Multinomial/*<R>*/ ret = newInstance(d);
	setAllZero(ret);
	// ret = &sum;<sub>i&isin;dimensions()</sub> a<sub>i</sub>X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> * b
	// perform (slow) multiplications on monomial base
	for (Combinatorical index = Combinatorical.getPermutations(dimensions()); index.hasNext(); ) {
	    int[] i = index.next();
	    // si = a<sub>i</sub>X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> * b
	    AbstractMultinomial/*<R>*/ si = (AbstractMultinomial)newInstance(Functionals.map(Operations.plus, i, b.dimensions()));
	    setAllZero(si);
	    //System.err.println("\t+ " + b.scale(get(i)) + " X^" + MathUtilities.format(i));
	    final int[] sidim = si.dimensions();
	    final int[] sidim_1 = new int[sidim.length];
	    for (int k = 0; k < sidim_1.length; k++)
		sidim_1[k] = sidim[k] - 1;
	    setSubTensor(si.tensorViewOfCoefficients(), i, sidim_1,
			 ((AbstractMultinomial)b.scale(get(i))).tensorViewOfCoefficients());
	    //System.err.println("\t(+ " + b.scale(get(i)));
	    ret = ret.add(si);
	    //System.err.println("\t(= " + ret);
	}
	//System.err.println("\t== " + ret);
	return ret;
    }

    //@internal identical to @see AbstractTensor.setSubTensor(int[],int[],Tensor). Once that exists, get rid of this
    private void setSubTensor(Tensor t, int[] i1, int[] i2, Tensor/*<R>*/ sub) {
	Tensor embed = t.subTensor(i1, i2);
	Utility.pre(sub.rank() == t.rank(), "sub tensor has compatible rank");
	Utility.pre(Utility.equalsAll(sub.dimensions(), embed.dimensions()), "sub tensor has compatible dimensions");
	ListIterator dst;
	Setops.copy(dst = embed.iterator(), sub.iterator());
	assert !dst.hasNext() : "equal dimensions have iterators of equal length";
    } 

    /**
     * Sets all coefficients of p to 0.
     */
    private void setAllZero(Multinomial p) {
	for (ListIterator i = p.iterator(); i.hasNext(); ) {
	    i.next();
	    i.set(get(CONSTANT_TERM).zero());
	}
    }

    public Arithmetic divide(Arithmetic b) throws UnsupportedOperationException {
	throw new UnsupportedOperationException("dividing polynomials is not generally defined");
    } 

    public Arithmetic inverse() throws UnsupportedOperationException {
	throw new UnsupportedOperationException("inverse of polynomials is not generally defined");
    } 

    public String toString() {
	return format(this, new StringBuffer(), new java.text.FieldPosition(0)).toString();
	//return ArithmeticFormat.getDefaultInstance().format(this);
    }


    /**
     * The monomial coefficient*X<sub>0</sub><sup>exponents[0]</sup>...X<sub>n-1</sub><sup>exponents[n-1]</sup>.
     * @todo move to Values?
     * @internal horribly complicate implementation
     */
    public static final Multinomial/*<R>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, int[] exponents) {
	int[] dim = new int[exponents.length];
	for (int k = 0; k < dim.length; k++)
	    dim[k] = exponents[k]+1;
	AbstractMultinomial m = new ArithmeticMultinomial(dim);
	m.set(m.CONSTANT_TERM, coefficient.zero());
	m.setAllZero(m);
	m.set(exponents, coefficient);
	return m;
    }

    // <polynomialPrefix> cn <polynomialTimesOperator> <polynomialVariable> <polynomialPowerOperator> n (<polynomialPlusOperator>|<polynomialPlusAlternative>) ... c2 <polynomialTimesOperator> <polynomialVariable> <polynomialPowerOperator> 2 (<polynomialPlusOperator>|<polynomialPlusAlternative>)<polynomialSuffix> c1 <polynomialTimesOperator> <polynomialVariable> (<polynomialPlusOperator>|<polynomialPlusAlternative>)<polynomialSuffix> c0 <polynomialSuffix> 
    private String polynomialPrefix					= "";
    private String polynomialTimesOperator = "*";
    private String polynomialVariable = "X";
    private String polynomialPowerOperator = "^";
    private String polynomialPlusOperator = "+";
    private String polynomialPlusAlternative = "-";
    private String polynomialSuffix					= "";
    /**
     * Specialization of format.
     * @todo move to ArithmeticFormat
     * @todo provide a parser
     */
    public StringBuffer format(Multinomial p, StringBuffer result, java.text.FieldPosition fieldPosition) {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);
	final int initialIndex = result.length();
		
	// @todo improve format
	result.append(polynomialPrefix);
	for (Combinatorical index = Combinatorical.getPermutations(p.dimensions()); index.hasNext(); ) {
	    final int[] i = index.next();
	    final Arithmetic ci = p.get(i);
	    final boolean constantTerm = Setops.all(Values.valueOf(i).iterator(), Functionals.bindSecond(Predicates.equal, Values.ZERO));
	    // only print nonzero elements (but print the 0-th coefficient if it is the only one)
	    if (!ci.norm().equals(Values.ZERO)
		|| (constantTerm && p.degreeValue() <= 0)) {
		int startIndex = result.length();
		// whether the coefficient ci has been skipped
		boolean skipped;
		if (ci.equals(ci.one()) && !constantTerm)
		    // skip 1 (except for constant term)
		    skipped = true;
		else if (ci.equals(ci.one().minus()) && !constantTerm) {
		    // shorten -1 to - (except for constant term)
		    result.append(polynomialPlusAlternative);
		    skipped = true;
		} else {
//		    format(ci, result, fieldPosition);
		    result.append(ci);
		    skipped = false;
		}
		// separator for all but the first coefficient,
		// provided that there is not already an alternative separator
 		if (startIndex > initialIndex &&
 		    !(result.length() > startIndex && result.substring(startIndex).startsWith(polynomialPlusAlternative)))
		    result.insert(startIndex, polynomialPlusOperator);
		
		for (int k = 0; k < p.numberOfVariables(); k++)
		    if (i[k] != 0) {
			if (skipped)
			    // only skip times operator once
			    skipped = false;
			else
			    result.append(polynomialTimesOperator);
			result.append(polynomialVariable + k + (i[k] > 1 ? polynomialPowerOperator + i[k] : ""));
		    }
	    }
	}
	result.append(polynomialSuffix);

	return result;
    }
}
