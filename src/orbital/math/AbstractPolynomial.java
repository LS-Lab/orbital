/**
 * @(#)AbstractPolynomial.java 1.0 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.math.functional.Function;
import java.io.Serializable;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import orbital.logic.functor.Functionals;
import orbital.logic.functor.BinaryFunction;

import orbital.util.Setops;
import orbital.logic.functor.Predicates;

import java.util.ListIterator;

abstract class AbstractPolynomial/*<R implements Arithmetic>*/ extends AbstractArithmetic implements Polynomial/*<R>*/, Serializable {
    private static final long serialVersionUID = -5253561352164949692L;
    /**
     * Which implementation of the multiplication to use.
     */
    private static final boolean IMPLEMENTATION_KARATSUBA = false;
	
    /**
     * The 0&isin;R of the underlying ring of coefficients.
     * @invariant subclasses must set this value to get(0).zero()
     */
    transient Arithmetic/*>R<*/  R_ZERO;
    public AbstractPolynomial() {
    }
  
    public boolean equals(Object o) {
    	if (o instanceof Polynomial) {
	    Polynomial/*<R>*/ p = (Polynomial) o;
	    if (degreeValue() != p.degreeValue())
		return false;
	    return Setops.all(iterator(), p.iterator(), Predicates.equal);
    	}
    	return false;
    }
    public int hashCode() {
    	throw new UnsupportedOperationException();
    }

    public Integer degree() {
	return Values.valueOf(degreeValue());
    }

    /**
     * Sets a value for the coefficient specified by index.
     * @pre i&isin;<b>N</b>
     * @throws UnsupportedOperationException if this polynomial is constant and does not allow modifications.
     */
    protected abstract void set(int i, Arithmetic vi);
	
    // iterator-views

    /**
     * The number of times this Polynomial has been structurally modified.
     * Structural modifications are those that change the number of elements in
     * the Polynomial or otherwise modify its internal structure.
     * This field is used to make iterators of the Polynomial fail-fast.
     * <p>
     * To use this feature, increase modCount whenever an implementation method changes
     * this polynomial.</p>
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
		    return cursor <= degreeValue();
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
		    throw new UnsupportedOperationException();
		} 
        	public void set(Object o) {
        	    if (!(o instanceof Arithmetic))
        	    	throw new IllegalArgumentException();
        	    if (lastRet == -1)
            		throw new IllegalStateException();
		    checkForComodification();
        
        	    try {
            		AbstractPolynomial.this.set(lastRet, (Arithmetic/*>R<*/)o);
            		expectedModCount = modCount;
        	    } catch(IndexOutOfBoundsException e) {
			throw new ConcurrentModificationException();
        	    }
        	}
        
        	public void add(Object o) {
		    throw new UnsupportedOperationException();
        	}
        	
        	private final void checkForComodification() {
        	    if (modCount != expectedModCount)
			throw new ConcurrentModificationException();
        	}
	    };
    } 
	
    // @todo copy horner scheme to examples
    public Object/*>R<*/ apply(Object/*>R<*/ a) {
	final Arithmetic/*>R<*/ acast = (Arithmetic/*>R<*/)a;
	if (acast instanceof Symbol) {
	    // use ordinary evaluation scheme to improve readability
	    Arithmetic r = get(0);
	    for (int i = 1; i <= degreeValue(); i++) {
		Arithmetic ci = get(i);
		r = r.add(ci.multiply(acast.power(Values.valueOf(i))));
	    }
	    return r;
	}
	// horner schema is (|0, &lambda;c,b. c+b*a|) for foldRight like banana
	return Functionals.banana(R_ZERO, new BinaryFunction/*<R,R,R>*/() {
		public Object/*>R<*/ apply(Object/*>R<*/ c, Object/*>R<*/ b) {
		    return (Object/*>R<*/) ((Arithmetic/*>R<*/)c).add(((Arithmetic/*>R<*/)b).multiply(acast));
		    //return ((Arithmetic/*>R<*/)c).add(((Arithmetic/*>R<*/)b).multiply((Arithmetic/*>R<*/)a));
		}
	    }, iterator());
    }	

    /**
     * <i>d</i>f/<i>d</i>x = (<var>a</var><sub>0</sub> + <var>a</var><sub>1</sub>X + <var>a</var><sub>2</sub>X<sup>2</sup> + ... + <var>a</var><sub>n</sub>X<sup>n</sup>)' = <var>a</var><sub>1</sub> + 2<var>a</var><sub>2</sub>X + 3<var>a</var><sub>3</sub>X<sup>2</sup> + ... + n<var>a</var><sub>n</sub>X<sup>n-1</sup>.
     */
    public Function derive() {
	Arithmetic[] ai = new Arithmetic[degreeValue()];
	if (ai.length == 0)
	    return this;
	for (int i = 1; i <= degreeValue(); i++)
	    ai[i - 1] = get(i).multiply(Values.valueOf(i));
	return Values.polynomial(ai);
    } 

    /**
     * &int; (<var>a</var><sub>0</sub> + <var>a</var><sub>1</sub>X + <var>a</var><sub>2</sub>X<sup>2</sup> + ... + <var>a</var><sub>n</sub>X<sup>n</sup>) <i>d</i>x = <var>a</var><sub>0</sub>X + <var>a</var><sub>1</sub>X<sup>2</sup>/2 + ... + <var>a</var><sub>n</sub>X<sup>n+1</sup>/(n+1).
     */
    public Function integrate() {
	if (degreeValue() < 0)
	    return this;
	Arithmetic[] ai = new Arithmetic[(degreeValue() + 1) + 1];
	ai[0] = R_ZERO;
	for (int i = 0; i <= degreeValue(); i++)
	    ai[i + 1] = get(i).divide(Values.valueOf(i + 1));
	return Values.polynomial(ai);
    }

    public Arithmetic zero() {
	return Values.polynomial(new Arithmetic/*>R<*/[] {get(0).zero()});
    }

    public Arithmetic one() {
	return Values.polynomial(new Arithmetic/*>R<*/[] {get(0).one()});
    }

    public Real norm() {
    	return degreeValue() < 0 ? Values.ZERO : Values.POSITIVE_INFINITY;
    }

    //@todo we should also support adding other functions (like in AbstractFunctor)?
	
    public Arithmetic add(Arithmetic b) {
	return addImpl((Polynomial)b);
    }
    public Euclidean add(Euclidean b) {
	return addImpl((Polynomial)b);
    }
    public Polynomial/*<R>*/ add(Polynomial/*<R>*/ b) {
	return addImpl(b);
    }
    //@note this ugly trick is necessary because in #add(Euclidean) we somehow cannot cast and call add((Polynomial/*<R>*/) b);
    private Polynomial/*<R>*/ addImpl(Polynomial/*<R>*/ b) {
	// optimized component-wise addition
	if (degreeValue() < 0)
	    return b;
	if (b.degreeValue() < 0)
	    return this;
	Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[Math.max(degreeValue(), b.degreeValue()) + 1];
	final int mindeg = Math.min(degreeValue(), b.degreeValue());
	for (int i = 0; i <= mindeg; i++)
	    r[i] = (Arithmetic/*>R<*/) get(i).add(b.get(i));
	assert !(degreeValue() > mindeg && b.degreeValue() > mindeg) : "deg(" + this + ")=" + degreeValue() + ", deg(" + b + ")=" + b.degreeValue() + " mindeg=" + mindeg + " cannot be greater than both degrees";
	// optimized plus saving some empty additions with 0
	if (degreeValue() > mindeg)
	    for (int i = mindeg + 1; i <= degreeValue(); i++)
		r[i] = get(i);
	else if (b.degreeValue() > mindeg)
	    for (int i = mindeg + 1; i <= b.degreeValue(); i++)
		r[i] = b.get(i);
	return representative(r);
    }
	
    public Arithmetic minus() {
	Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[degreeValue()];
	if (r.length == 0)
	    return this;
	for (int i = 0; i < r.length; i++)
	    r[i] = (Arithmetic/*>R<*/) get(i).minus();
	return Values.polynomial(r);
    }

    public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
	return subtractImpl((Polynomial)b);
    } 
    public Euclidean subtract(Euclidean b) throws ArithmeticException {
	return subtractImpl((Polynomial)b);
    } 
    public Polynomial/*<R>*/ subtract(Polynomial/*<R>*/ b) {
	return subtractImpl(b);
    }
    private Polynomial/*<R>*/ subtractImpl(Polynomial/*<R>*/ b) {
	return (Polynomial) add(b.minus());
    }

    public Arithmetic scale(Arithmetic/*<R>*/ alpha) {
	if (degreeValue() < 0)
	    return this;
	Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[degreeValue() + 1];
	for (int i = 0; i < r.length; i++) {
	    r[i] = (Arithmetic/*>R<*/) get(i).scale(alpha);
	}
	return representative(r);
    }

    public Arithmetic multiply(Arithmetic b) {
	return multiply((Polynomial)b);
    }
    public Euclidean multiply(Euclidean b) {
	return multiplyImpl((Polynomial)b);
    }
    public Polynomial/*<R>*/ multiply(Polynomial/*<R>*/ b) {
	return multiplyImpl(b);
    }
    /**
     * possible implementations of polynomial multiplication include
     * <ul>
     *   <li>na&iuml;ve convolution in O(n<sup>2</sup>)</li>
     *   <li>Divide and Conquer with Karatsuba's trick in O(n<sup>&#13266;<sub>2</sub>3</sup>).
     *     The trick is to use the equation
     *     <center>(aY+b) * (cY+d) = (ac)Y<sup>2</sup> + ((a+b)(c+d)-(ac)-(bd))Y + (bd)</center>
     *     with Y=X<sup>&lceil;max{deg(f),deg(g)}/2&rceil;</sup>
     *     recursively saving one multiplication out of four in each recursion.
     *   </li>
     *   <li>By using the Fast Fourier-Transform (FFT) in O(n*&#13266;n):
     *     evaluate by FFT, multiply single nodes, interpolate by FFT<sup>-1</sup>.
     *     Given a primitive (<var>n</var>*<var>m</var>)-th root of unity <var>&omega;</var>&isin;<b>F</b>
     *     the FFT is the decomposition of the n-dimensional discrete fourier-transform
     *     into a product of sparse matrices.
     *     <center>DFT<sub>n*m</sub>(&omega;) = &Pi;&sdot;(I<sub>m</sub>&otimes;DFT<sub>n</sub>(&omega;<sup>m</sup>))&sdot;T(&omega;)&sdot;(DFT<sub>m</sub>(&omega;<sup>n</sup>)&otimes;I<sub>n</sub>)</center>
     *     with
     *     <center>T(&omega;) = &#8720;<sub>k=0,...,m-1</sub>&Delta;<sub>k</sub> = diag(&Delta;<sup>0</sup>,...,&Delta;<sup>m-1</sup>)
     *     is a block-diagonal matrix, and the diagonal matrix
     *     &Delta;=diag(1,&omega;,...,&omega;<sup>n-1</sup>)</center>
     *     The discrete fourier-transform is only a special form of the vandermond matrix for
     *     polynomial evaluation
     *     <center>DFT<sub>n</sub>(&omega;) = (&omega;<sup>&nu;&mu;</sup>)<sub>&nu;,&mu;&isin;{0,...,n-1}</sub></center>
     *     and its inverse for polynomial interpolation
     *     <center>DFT<sub>n</sub>(&omega;)<sup>-1</sup> = 1/n*DFT<sub>n</sub>(&omega;<sup>-1</sup>)</center>
     *   </li>
     * </ul>
     */
    private Polynomial/*<R>*/ multiplyImpl(Polynomial/*<R>*/ b) {
	if (IMPLEMENTATION_KARATSUBA)
	    return multiplyImplKaratsuba(b);
	//@todo could we speed this up by FFT or horner schema, or Karatsuba?
	if (degreeValue() < 0)
	    return this;
	else if (b.degreeValue() < 0)
	    return b;
	Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[degreeValue() + b.degreeValue() + 1];
	for (int i = 0; i < r.length; i++) {
	    //r[i] = &sum;<sub>k=0,...,i</sub> a[k]*b[i-k]
	    r[i] = (Arithmetic/*>R<*/) get(0).multiply(b.get(i));
	    for (int k = 1; k <= i; k++)
		r[i] = (Arithmetic/*>R<*/) r[i].add(get(k).multiply(b.get(i-k)));
	}
	return representative(r);
    }
    /**
     * @todo debug Addendum.main multiplication of X^2*X^1 == 0 is completely wrong
     */
    private Polynomial/*<R>*/ multiplyImplKaratsuba(Polynomial/*<R>*/ poly2) {
	//@todo could we speed this up by FFT or horner schema, or Karatsuba?
	if (degreeValue() < 0)
	    return this;
	else if (poly2.degreeValue() < 0)
	    return poly2;
	int n = Math.max(degreeValue(), poly2.degreeValue());
	if (n == 0)					// base case
	    return representative(new Arithmetic/*>R<*/[] {(Arithmetic/*>R<*/) get(0).multiply(poly2.get(0))});
	else {						// recursion
	    int d = (n+1) >> 1;
	    assert d == (int) Math.ceil(n/2.0) : "bit optimization works " + d + "==" + (n/2.0);
	    n = d << 1;
			
	    final Polynomial/*<R>*/ alpha[] = split(this, d);
	    final Polynomial/*<R>*/ beta[] = split(poly2, d);
	    final Polynomial/*<R>*/ ac = alpha[1].multiply(beta[1]);
	    final Polynomial/*<R>*/ bd = alpha[0].multiply(beta[0]);
	    final Polynomial/*<R>*/ t = (alpha[1].add(alpha[0])).multiply(beta[1].add(beta[0]));
	    // return ac*X^n + (t-ac-bd)*X^d + bd
	    // optimized because addition should affect distinct coefficients
	    Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[n + Math.max(ac.degreeValue(),0) + 1];
	    assert bd.degreeValue() < d : "bd.degreeValue() < d required for shift optimized add of distinct coefficients";
	    for (int i = 0; i < d; i++)
		r[i] = bd.get(i);
	    assert t.degreeValue() + d < n : "t.degreeValue() + d < n required for shift optimized add of distinct coefficients " + t.degreeValue() + "+" + d + "<" + n + " with " + alpha[0] + "," + alpha[1] + " and " + beta[0] + "," + beta[1] + " and t="+t;
	    assert ac.degreeValue() + d < n : "ac.degreeValue() + d < n required for shift optimized add of distinct coefficients";
	    assert bd.degreeValue() + d < n : "bd.degreeValue() + d < n required for shift optimized add of distinct coefficients";
	    for (int i = d; i < n; i++) {
		final int j = i - d;
		r[i] = (Arithmetic/*>R<*/) t.get(j).subtract(ac.get(j)).subtract(bd.get(j));
	    }
	    for (int i = n; i < r.length; i++)
		r[i] = ac.get(i - n);
	    return representative(r);
	}
    }
    /**
     * Split a polynomial at the index s in two pieces.
     * @post p == RES[1]*X<sup>s</sup> + RES[0] &and; RES[0].degreeValue()&lt;division
     */
    private /*static*/ final Polynomial/*<R>*/[] split(Polynomial/*<R>*/ p, int s) {
	Polynomial/*<R>*/ split[] = new AbstractPolynomial/*<R>*/[2];
	Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[s];
	for (int i = 0; i < r.length; i++)
	    r[i] = p.get(i);
	split[0] = representative(r);
	r = new Arithmetic/*>R<*/[Math.max(p.degreeValue() + 1 - s, 0)];
	for (int i = 0; i < r.length; i++)
	    r[i] = p.get(s + i);
	split[1] = representative(r);
	return split;
    }

    public Arithmetic divide(Arithmetic b) throws UnsupportedOperationException {
	throw new UnsupportedOperationException("dividing Euclideans is not generally defined");
    } 

    public Arithmetic inverse() throws UnsupportedOperationException {
	throw new UnsupportedOperationException("inverse of Euclideans is not generally defined");
    } 

    public Euclidean quotient(Euclidean g) {
	return polynomialDivision(this, (Polynomial) g, true);
    }
    public Polynomial/*<R>*/ quotient(Polynomial/*<R>*/ g) {
	return polynomialDivision(this, g, true);
    }
    public Euclidean modulo(Euclidean g) {
	return polynomialDivision(this, (Polynomial) g, false);
    }
    public Polynomial/*<R>*/ modulo(Polynomial/*<R>*/ g) {
	return polynomialDivision(this, g, false);
    }
    /**
     * @param returnQuotient if <code>true</code> will return quotient, if <code>false</code> will return remainder modulo g instead.
     * @todo optimize, f.ex. use shifting instead of explicit multiplication, and avoid subtract that calls representative
     */
    private Polynomial/*<R>*/ polynomialDivision(final Polynomial/*<R>*/ f, final Polynomial/*<R>*/ g, boolean returnQuotient) {
	// the highest coefficient of g
	final Arithmetic/*>R<*/ bm = g.get(g.degreeValue());
	Arithmetic/*>R<*/ quotient[] = new Arithmetic/*>R<*/[f.degreeValue() - g.degreeValue() + 1];
		
	Polynomial/*<R>*/ f0 = f;
	for (int k = quotient.length - 1; k >= 0; k--) {
	    final Arithmetic/*>R<*/ ai = f0.get(f0.degreeValue());
	    final Arithmetic/*>R<*/ ck = (Arithmetic/*>R<*/) ai.divide(bm);
	    quotient[k] = ck;
	    f0 = f0.subtract(BASE(ck, k).multiply(g));
	    if (f0.norm().equals(Values.ZERO)) {
		for (int i = k - 1; i >= 0; i--)
		    quotient[i] = R_ZERO;
		break;
	    }
	}
	return returnQuotient
	    ? representative(quotient)
	    : f0;
    }

    private Polynomial/*<R>*/ BASE(Arithmetic/*>R<*/ s, int k) {
	Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[k + 1];
	for (int i = 0; i < k; i++)
	    r[i] = R_ZERO;
	r[k] = s;
	return Values.polynomial(r);
    }

    /**
     *
     */
    private Polynomial/*<R>*/ representative(Arithmetic/*>R<*/ a[]) {
	int deg;
	for (deg = a.length - 1; deg >= 0; deg--)
	    if (!a[deg].norm().equals(Values.ZERO))
		break;
	if (deg < 0)
	    return Values.polynomial(new Arithmetic/*>R<*/[0]);
	//assert(deg == max {i&isin;<b>N</b> : a<sub>i</sub> &ne; 0}
	assert 0 <= deg && deg < a.length : "degree " + deg + " is in [0,n]";
	if (deg == a.length - 1)
	    // fast shortcut avoiding copy of references in a to r
	    return Values.polynomial(a);
	Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[deg + 1];                                                                                    
	for (int i = 0; i < r.length; i++)
	    r[i] = a[i];
	return Values.polynomial(r);
    }
	
    public Arithmetic/*>R<*/[] getCoefficients() {
	if (degreeValue() < 0)
	    return new Arithmetic/*>R<*/[0];
	Arithmetic/*>R<*/[] a = new Arithmetic/*>R<*/[degreeValue() + 1];
	for (int i = 0; i <= degreeValue(); i++)
	    a[i] = get(i);
	return a;
    } 

    public String toString() {
	assert !get(degreeValue()).norm().equals(Values.ZERO) : "definition of degree implies that the degree-th coefficient is != 0";
	return ArithmeticFormat.getDefaultInstance().format(this);
    }
}
