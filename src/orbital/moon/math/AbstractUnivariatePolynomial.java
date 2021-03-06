/**
 * @(#)AbstractUnivariatePolynomial.java 1.0 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import orbital.math.functional.Function;
import java.io.Serializable;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import orbital.logic.functor.Functionals;
import orbital.logic.functor.BinaryFunction;

import orbital.util.KeyValuePair;
import orbital.util.Setops;
import orbital.util.Utility;
import orbital.logic.functor.Predicates;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo would we profit from extending AbstractPolynomial instead?
 */
abstract class AbstractUnivariatePolynomial/*<R extends Arithmetic>*/
    extends AbstractProductArithmetic/*<R,Integer,UnivariatePolynomial<R>>*/
    implements UnivariatePolynomial/*<R>*/, Serializable {
    private static final long serialVersionUID = -5253561352164949692L;
    /**
     * Which implementation of the multiplication to use.
     */
    private static final boolean IMPLEMENTATION_KARATSUBA = false;
        
    /**
     * The 0&isin;R of the underlying ring of coefficients.
     * @invariants subclasses must set this value to get(0).zero()
     */
    transient Arithmetic/*>R<*/  COEFFICIENT_ZERO;
    public AbstractUnivariatePolynomial(ValueFactory valueFactory) {
        super(valueFactory);
        // can be overwritten in subclasses
        this.COEFFICIENT_ZERO = valueFactory.ZERO();
    }
  
    public boolean equals(Object o) {
        // identical degrees imply compatible iterator() so that we do not need iterator(int dim) functions
        return o instanceof UnivariatePolynomial && degree().equals(((UnivariatePolynomial)o).degree()) && super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean isZero() {
        assert (degreeValue() < 0) == equals(zero()) : "polynomial is zero iff its degree is negative: " + this;
        return degreeValue() < 0;
    }

    public int rank() {
        return 1;
    }

    public int[] degrees() {
        return new int[] {degreeValue()};
    }
    
    /**
     * Sets a value for the coefficient specified by index.
     * Convenience method for {@link #set(Arithmetic,Arithmetic)}.
     * @preconditions i&isin;<b>N</b>
     * @throws UnsupportedOperationException if this polynomial is constant and does not allow modifications.
     */
    protected abstract void set(int i, Arithmetic vi);

    
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
    protected abstract UnivariatePolynomial/*<R>*/ newInstance(int degree);
        
    protected final Polynomial/*<R,Integer>*/ newInstance(int[] dim) {
        if (dim.length == 1)
            return newInstance(dim[0]);
        else throw new InternalError("multinomials not supported, use ValueFactory.polynomial instead");
    }

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
                        AbstractUnivariatePolynomial.this.set(lastRet, (Arithmetic/*>R<*/)o);
                        expectedModCount = modCount;
                    } catch(IndexOutOfBoundsException e) {
                        throw new ConcurrentModificationException();
                    }
                }
        
                public void add(Object o) {
                    throw new UnsupportedOperationException("not implemented (would shift exponents)");
                }
                
                private final void checkForComodification() {
                    if (modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
    } 
        
    public Iterator monomials() {
        return new Iterator() {
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

                public Object next() {
                    try {
                        Object next = get(cursor);
                        checkForComodification();
                        lastRet = cursor++;
                        return new KeyValuePair(valueFactory().valueOf(lastRet), next);
                    }
                    catch(IndexOutOfBoundsException e) {
                        checkForComodification();
                        throw new NoSuchElementException();
                    }
                } 

                public void remove() {
                    throw new UnsupportedOperationException();
                } 
               
                private final void checkForComodification() {
                    if (modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
    } 

    
    public Object/*>R<*/ apply(Object/*>R<*/ a) {
        final Arithmetic/*>R<*/ acast = (Arithmetic/*>R<*/)a;
        if (acast instanceof Symbol) {
            // use ordinary evaluation scheme to improve readability
            Arithmetic r = get(0);
            final ValueFactory vf = acast.valueFactory();
            for (int i = 1; i <= degreeValue(); i++) {
                Arithmetic ci = get(i);
                r = r.add(ci.multiply(acast.power(vf.valueOf(i))));
            }
            return r;
        }
        // horner schema is (|0, &lambda;c,b. c+b*a|) for foldRight like banana
        return Functionals.banana(COEFFICIENT_ZERO, new BinaryFunction/*<R,R,R>*/() {
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
        final ValueFactory vf = valueFactory();
        for (int i = 1; i <= degreeValue(); i++)
            ai[i - 1] = get(i).multiply(vf.valueOf(i));
        return vf.polynomial(ai);
    } 

    /**
     * &int; (<var>a</var><sub>0</sub> + <var>a</var><sub>1</sub>X + <var>a</var><sub>2</sub>X<sup>2</sup> + ... + <var>a</var><sub>n</sub>X<sup>n</sup>) <i>d</i>x = <var>a</var><sub>0</sub>X + <var>a</var><sub>1</sub>X<sup>2</sup>/2 + ... + <var>a</var><sub>n</sub>X<sup>n+1</sup>/(n+1).
     */
    public Function integrate() {
        if (degreeValue() < 0)
            return this;
        Arithmetic[] ai = new Arithmetic[(degreeValue() + 1) + 1];
        final ValueFactory vf = valueFactory();
        ai[0] = COEFFICIENT_ZERO;
        for (int i = 0; i <= degreeValue(); i++)
            ai[i + 1] = get(i).divide(vf.valueOf(i + 1));
        return vf.polynomial(ai);
    }

    public Arithmetic zero() {
    	assert COEFFICIENT_ZERO != null : "COEFFICIENT_ZERO set";
        return valueFactory().polynomial(new Arithmetic/*>R<*/[] {/*get(0)*/COEFFICIENT_ZERO.zero()});
    }

    public Arithmetic one() {
    	assert COEFFICIENT_ZERO != null : "COEFFICIENT_ZERO set";
        return valueFactory().polynomial(new Arithmetic/*>R<*/[] {/*get(0)*/COEFFICIENT_ZERO.one()});
    }

    public Real norm() {
        return degreeValue() < 0 ? valueFactory().ZERO() : valueFactory().POSITIVE_INFINITY();
    }

    //@todo we should also support adding other functions (like in AbstractFunctor)?
        
    public Arithmetic add(Arithmetic b) {
        return addImpl((UnivariatePolynomial)b);
    }
    public Euclidean add(Euclidean b) {
        return addImpl((UnivariatePolynomial)b);
    }
    public UnivariatePolynomial/*<R>*/ add(UnivariatePolynomial/*<R>*/ b) {
        return addImpl(b);
    }
    //@note this ugly trick is necessary because in #add(Euclidean) we somehow cannot cast and call add((UnivariatePolynomial/*<R>*/) b);
    //@internal using return (UnivariatePolynomial)super.add((Arithmetic)b); does not work since the two iterators may have different hasNext(), though next() would work
    private UnivariatePolynomial/*<R>*/ addImpl(UnivariatePolynomial/*<R>*/ b) {
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
        //@internal optimized plus saving some empty additions with 0
        if (degreeValue() > mindeg)
            for (int i = mindeg + 1; i <= degreeValue(); i++)
                r[i] = get(i);
        else if (b.degreeValue() > mindeg)
            for (int i = mindeg + 1; i <= b.degreeValue(); i++)
                r[i] = b.get(i);
        return representative(r);
    }
        
    public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
        return subtractImpl((UnivariatePolynomial)b);
    } 
    public Euclidean subtract(Euclidean b) throws ArithmeticException {
        return subtractImpl((UnivariatePolynomial)b);
    } 
    public UnivariatePolynomial/*<R>*/ subtract(UnivariatePolynomial/*<R>*/ b) {
        return subtractImpl(b);
    }
    private UnivariatePolynomial/*<R>*/ subtractImpl(UnivariatePolynomial/*<R>*/ b) {
        return (UnivariatePolynomial) add(b.minus());
    }

    public Arithmetic multiply(Arithmetic b) {
        return multiply((UnivariatePolynomial)b);
    }
    public Euclidean multiply(Euclidean b) {
        return multiplyImpl((UnivariatePolynomial)b);
    }
    public UnivariatePolynomial/*<R>*/ multiply(UnivariatePolynomial/*<R>*/ b) {
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
    private UnivariatePolynomial/*<R>*/ multiplyImpl(UnivariatePolynomial/*<R>*/ b) {
        //@todo could we speed this up by FFT or horner schema?
        if (IMPLEMENTATION_KARATSUBA)
            return multiplyImplKaratsuba(b);
        else
            return multiplyImplConvolution(b);
    }
    private UnivariatePolynomial/*<R>*/ multiplyImplConvolution(UnivariatePolynomial/*<R>*/ b) {
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
    private UnivariatePolynomial/*<R>*/ multiplyImplKaratsuba(UnivariatePolynomial/*<R>*/ poly2) {
        if (degreeValue() < 0)
            return this;
        else if (poly2.degreeValue() < 0)
            return poly2;
        int n = Math.max(degreeValue(), poly2.degreeValue());
        if (n == 0)                                     // base case
            return representative(new Arithmetic/*>R<*/[] {(Arithmetic/*>R<*/) get(0).multiply(poly2.get(0))});
        else {                                          // recursion
            int d = (n+1) >> 1;
            assert d == (int) Math.ceil(n/2.0) : "bit optimization works " + d + "==" + (n/2.0);
            n = d << 1;
                        
            final UnivariatePolynomial/*<R>*/ alpha[] = split(this, d);
            final UnivariatePolynomial/*<R>*/ beta[] = split(poly2, d);
            final UnivariatePolynomial/*<R>*/ ac = alpha[1].multiply(beta[1]);
            final UnivariatePolynomial/*<R>*/ bd = alpha[0].multiply(beta[0]);
            final UnivariatePolynomial/*<R>*/ t = (alpha[1].add(alpha[0])).multiply(beta[1].add(beta[0]));
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
            assert representative(r).equals(multiplyImplConvolution(poly2)) : "result of polynomial multiplication per Karatsuba and convolution equal";
            return representative(r);
        }
    }
    /**
     * Split a polynomial at the index s in two pieces.
     * @postconditions p == RES[1]*X<sup>s</sup> + RES[0] &and; RES[0].degreeValue()&lt;division
     */
    private /*static*/ final UnivariatePolynomial/*<R>*/[] split(UnivariatePolynomial/*<R>*/ p, int s) {
        UnivariatePolynomial/*<R>*/ split[] = new AbstractUnivariatePolynomial/*<R>*/[2];
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
        return quotient((UnivariatePolynomial)g);
    }
    public UnivariatePolynomial/*<R>*/ quotient(UnivariatePolynomial/*<R>*/ g) {
        return polynomialDivision(this, g, true);
    }
    public Euclidean modulo(Euclidean g) {
        return modulo((UnivariatePolynomial)g);
    }
    public UnivariatePolynomial/*<R>*/ modulo(UnivariatePolynomial/*<R>*/ g) {
        return polynomialDivision(this, g, false);
    }
    /**
     * @param returnQuotient if <code>true</code> will return quotient, if <code>false</code> will return remainder modulo g instead.
     * @todo optimize, f.ex. use shifting instead of explicit multiplication, and avoid subtract that calls representative
     */
    private UnivariatePolynomial/*<R>*/ polynomialDivision(final UnivariatePolynomial/*<R>*/ f, final UnivariatePolynomial/*<R>*/ g, boolean returnQuotient) {
        if (f.degreeValue() < g.degreeValue())
            return f;
        else if (g.degreeValue() < 0)
            throw new ArithmeticException("/ by " + g);
        // the highest coefficient of g
        final Arithmetic/*>R<*/ bm = g.get(g.degreeValue());
        Arithmetic/*>R<*/ quotient[] = new Arithmetic/*>R<*/[f.degreeValue() - g.degreeValue() + 1];
                
        UnivariatePolynomial/*<R>*/ f0 = f;
        for (int k = quotient.length - 1; k >= 0; k--) {
            final Arithmetic/*>R<*/ ai = f0.get(f0.degreeValue());
            final Arithmetic/*>R<*/ ck = (Arithmetic/*>R<*/) ai.divide(bm);
            quotient[k] = ck;
            f0 = f0.subtract(BASE(ck, k).multiply(g));
            if (f0.isZero()) {
                for (int i = k - 1; i >= 0; i--)
                    quotient[i] = COEFFICIENT_ZERO;
                break;
            }
        }
        UnivariatePolynomial/*<R>*/ q = representative(quotient);
        assert f.equals(q.multiply(g).add(f0)) : "Euclidean degree equation " + f + " = (" + q + ")*(" + g + ") + " + f0;
        assert f0.degreeValue() < 0 || f0.degreeValue() < g.degreeValue() : "Euclidean degree condition " + f0.degree() + " < " + g.degree() + " or " + f0 + "=0";
        return returnQuotient
            ? q
            : f0;
    }

    private UnivariatePolynomial/*<R>*/ BASE(Arithmetic/*>R<*/ s, int k) {
        Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[k + 1];
        for (int i = 0; i < k; i++)
            r[i] = COEFFICIENT_ZERO;
        r[k] = s;
        return valueFactory().polynomial(r);
    }

    /**
     *
     */
    private UnivariatePolynomial/*<R>*/ representative(Arithmetic/*>R<*/ a[]) {
        int deg;
        for (deg = a.length - 1; deg >= 0; deg--)
            if (!a[deg].isZero())
                break;
        if (deg < 0)
            //@todo perhaps prefer {R_ZERO}?
            return valueFactory().polynomial(new Arithmetic/*>R<*/[0]);
        //assert(deg == max {i&isin;<b>N</b> : a<sub>i</sub> &ne; 0}
        assert 0 <= deg && deg < a.length : "degree " + deg + " is in [0,n]";
        if (deg == a.length - 1)
            // fast shortcut avoiding copy of references in a to r
            return valueFactory().polynomial(a);
        Arithmetic/*>R<*/ r[] = new Arithmetic/*>R<*/[deg + 1];                                                                                    
        for (int i = 0; i < r.length; i++)
            r[i] = a[i];
        return valueFactory().polynomial(r);
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
        assert degreeValue() < 0 || !get(degreeValue()).isZero() : "definition of degree implies that the degree-th (" + degree() + "-th) coefficient (" + get(degreeValue()) + ") is != 0";
        return ArithmeticFormat.getDefaultInstance().format(this);
    }


    // identical to @see AbstractPolynomial
    public Integer degree() {
        return valueFactory().valueOf(degreeValue());
    }
    protected final Object productIndexSet(Arithmetic/*>UnivariatePolynomial<R><*/ productObject) {
        return dimensions();
    }
    protected ListIterator/*<R>*/ iterator(Arithmetic/*>UnivariatePolynomial<R><*/ productObject) {
        return ((AbstractUnivariatePolynomial)productObject).iterator();
    }
    protected final Arithmetic/*>UnivariatePolynomial<R><*/ newInstance(Object/*>Integer<*/ productIndexSet) {
        //@xxx wrong type case?
        return newInstance((int[])productIndexSet);
    }

    // polynomial version
    
    abstract Tensor tensorViewOfCoefficients();

    public final Object indexSet() {
        return valueFactory().ONE();
    }
    public Iterator indices() {
        if (degreeValue() < 0)
            return java.util.Collections.EMPTY_LIST.listIterator();
        //@xxx use Functionals.Anamorphism or something
        java.util.List l = new java.util.ArrayList(degreeValue() + 1);
        for (int i = 0; i <= degreeValue(); i++)
            l.add(valueFactory().valueOf(i));
        return java.util.Collections.unmodifiableList(l).listIterator();
    }

    public final int[] dimensions() {
        return new int[] {degreeValue()};
    }

    public final Arithmetic/*>R<*/ get(Arithmetic i) {
        return get(toIndex(i));
    }
    public final Arithmetic/*>R<*/ get(int[] i) {
        valid(i);
        return get(i[0]);
    }

    public final void set(int[] i, Arithmetic/*>R<*/ vi) {
        valid(i);
        set(i[0], vi);
    }
    public final void set(Arithmetic i, Arithmetic/*>R<*/ vi) {
        set(toIndex(i), vi);
    }
    
    private static int toIndex(Arithmetic i) {
    	if (i instanceof Integer) {
    		return ((Integer)i).intValue();
    	} else if (i instanceof Vector) {
    		Vector v = (Vector)i;
    		if (v.dimension() == 1 && v.get(0) instanceof Integer) {
    			return ((Integer)v.get(0)).intValue();
    		}
    	}
    	throw new ClassCastException("Cannot convert to index " + i + "@" + i.getClass());
    }

    final void valid(int[] i) {
        if (i.length != 1)
            throw new ArrayIndexOutOfBoundsException("illegal number of indices (" + i.length + " indices) for univariate polynomial");
    } 

    public Polynomial/*<R,Integer>*/ add(Polynomial/*<R,Integer>*/ b) {
        return add((UnivariatePolynomial)b);
    }
    public Polynomial/*<R,Integer>*/ subtract(Polynomial/*<R,Integer>*/ b) {
        return subtract((UnivariatePolynomial)b);
    }
    public Polynomial/*<R,Integer>*/ multiply(Polynomial/*<R,Integer>*/ b) {
        return multiply((UnivariatePolynomial)b);
    }
}
