/**
 * @(#)MathUtilities.java 1.1 2002-08-21 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Function;
import java.util.Comparator;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Collections;
import java.util.List;

import java.util.TreeSet;
import java.util.LinkedList;
import java.util.Arrays;

import orbital.math.functional.Functionals;
import orbital.logic.functor.Predicate;
import orbital.logic.functor.Predicates;
import orbital.logic.functor.BinaryFunction;
import orbital.algorithm.Combinatorical;
import orbital.util.Pair;
import orbital.util.Setops;
import orbital.util.Utility;
import orbital.util.ReverseComparator;
import orbital.math.functional.Operations;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Algebraic algorithms.
 *
 *
 * @author Andr&eacute; Platzer
 * @version 1.1, 2002-08-21
 * @see MathUtilities
 * @see orbital.util.Utility
 */
public class AlgebraicAlgorithms {
    private static final Logger logger = Logger.getLogger(AlgebraicAlgorithms.class.getName());
    /**
     * prevent instantiation - final static class
     */
    private AlgebraicAlgorithms() {}

    // admissible total orders on monoid of monomials

    /**
     * Lexicographical order on monoid of monomials.
     * This is an admissible total order.
     * The monomials are expected to be encoded as their exponents in the form of
     * <code>int[]</code>s.
     * <div>
     *   X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &le; X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>j<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>
     *   :&hArr; X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>=X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>j<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup> &or; i<sub>k</sub>&lt;j<sub>k</sub> for k := min{k &brvbar; i<sub>k</sub>&ne;j<sub>k</sub>}
     * </div>
     * Especially X<sub>0</sub> &gt; X<sub>1</sub> &gt; ... &gt; X<sub>n-1</sub>.
     */
    public static final Comparator LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object m1, Object m2) {
		final int[] nu = (int[]) m1;
		final int[] mu = (int[]) m2;
		if (nu.length != mu.length)
		    throw new IllegalArgumentException("incompatible monomial exponents from polynomial rings with a different number of variables");
		for (int k = 0; k < nu.length; k++) {
		    int c = nu[k] - mu[k];
		    if (c != 0)
			return c;
		}
		return 0;
	    }
	};

    /**
     * Reverse lexicographical order on monoid of monomials.
     * This is an admissible total order.
     * The monomials are expected to be encoded as their exponents in the form of
     * <code>int[]</code>s.
     * <div>
     *   X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &le; X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>j<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>
     *   :&hArr; X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>=X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>j<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup> &or; i<sub>k</sub>&lt;j<sub>k</sub> for k := max{k &brvbar; i<sub>k</sub>&ne;j<sub>k</sub>}
     * </div>
     * Especially X<sub>0</sub> &lt; X<sub>1</sub> &lt; ... &lt; X<sub>n-1</sub>.
     */
    public static final Comparator REVERSE_LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object m1, Object m2) {
		final int[] nu = (int[]) m1;
		final int[] mu = (int[]) m2;
		if (nu.length != mu.length)
		    throw new IllegalArgumentException("incompatible monomial exponents from polynomial rings with a different number of variables");
		for (int k = nu.length - 1; k >= 0; k--) {
		    int c = nu[k] - mu[k];
		    if (c != 0)
			return c;
		}
		return 0;
	    }
	};
    
    /**
     * Degree lexicographical order on monoid of monomials.
     * Thus compares for degree in favor of lexicographical comparison.
     * This is an admissible total order.
     * The monomials are expected to be encoded as their exponents in the form of
     * <code>int[]</code>s.
     * <div>
     *   X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &le; X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>j<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>
     *   :&hArr; deg(X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>)&lt;deg(X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>j<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>) &or; <big>(</big>deg(X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>)=deg(X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>j<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>) &and; X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>i<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> {@link #LEXICOGRAPHIC &lt;<sub>lexico</sub>} X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;X<sub>1</sub><sup>j<sub>1</sub></sup>&sdot;...&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup><big>)</big>
     * </div>
     */
    public static final Comparator DEGREE_LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object m1, Object m2) {
		final int[] nu = (int[]) m1;
		final int[] mu = (int[]) m2;
		if (nu.length != mu.length)
		    throw new IllegalArgumentException("incompatible monomial exponents from polynomial rings with a different number of variables");
		int c = ((Integer)Operations.sum.apply(Values.valueOf(nu))).intValue()
		    - ((Integer)Operations.sum.apply(Values.valueOf(mu))).intValue();
		if (c != 0)
		    return c;
		else
		    return LEXICOGRAPHIC.compare(m1, m2);
	    }
	};

    
    /**
     * The (partial) order on polynomials induced by an admissible total order on monomials.
     * @param monomialOrder is the underlying admissible total order on the monoid of monomials to use.
     */
    public static final Comparator INDUCED(final Comparator monomialOrder) {
	return new InducedPolynomialComparator(monomialOrder);
    }
    private static final class InducedPolynomialComparator implements Comparator {
	private final Comparator monomialOrder;
	public InducedPolynomialComparator(Comparator monomialOrder) {
	    this.monomialOrder = monomialOrder;
	}

	public boolean equals(Object o) {
	    return (o instanceof InducedPolynomialComparator)
		&& monomialOrder.equals(((InducedPolynomialComparator)o).monomialOrder);
	}
	public int compare(Object p1, Object p2) {
	    final SortedSet amon = new TreeSet(new ReverseComparator(monomialOrder));
	    amon.addAll(occurringMonomials((Multinomial)p1));
	    final SortedSet bmon = new TreeSet(new ReverseComparator(monomialOrder));
	    bmon.addAll(occurringMonomials((Multinomial)p2));
	    try {
		// like Setops.find(Functionals.map("asFunction"(monomialOrder), amon.iterator(), bmon.iterator()), Functionals.bindSecond(Predicates.equal, Values.ZERO));
		// but with lazy evaluation of Functionals.map such that it stops at the first even if the iterators have inequal lengths
		for (Iterator i = amon.iterator(), j = bmon.iterator(); i.hasNext() || j.hasNext(); ) {
		    //@todo which alternative?
		    if (!i.hasNext() || !j.hasNext())
			if (((Multinomial)p1).degreeValue() < 0)
			    return -1;
			else if (((Multinomial)p2).degreeValue() < 0)
			    return 1;
		    else
			throw new IndexOutOfBoundsException();
// 		    //@todo verify: in case of different number of occurring monomials, the one that has more is greater
// 		    if (!i.hasNext())
// 			return -1;
// 		    else if (!j.hasNext())
// 			return 1;
		    int cmp = monomialOrder.compare(i.next(), j.next());
		    if (cmp != 0)
			return cmp;
		}
		return 0;
	    } catch (IndexOutOfBoundsException differentLengthOfMonomials) {
		Function Xpower = new Function() {
			public Object apply(Object i) {
			    return AbstractMultinomial.MONOMIAL(Values.ONE, (int[])i);
			}
			public String toString() {
			    return "X0^.*...Xn^.";
			}
		    };
		throw (IllegalArgumentException)
		    new IllegalArgumentException("incomparable arguments " + p1 + " and " + p2
						 + "\nwith (sorted) monomials " + Functionals.map(Xpower, new LinkedList(amon)) + " and " + Functionals.map(Xpower, new LinkedList(bmon))).initCause(differentLengthOfMonomials);
	    }
	}
    }

    // an isomorphism useful in conjunction with tensor products
    
    /**
     * Of a rank r tensor with rank s tensor components, make a rank r+s tensor.
     * This applies recursively until (non-tensor) components of rank 0 have been reached.
     * Requires that all components are of uniform rank and dimensions.
     * @todo publicize?
     */
    static final Tensor flatten(Tensor t) {
	int[] sdim = null;
	for (Iterator i = t.iterator(); i.hasNext(); ) {
	    Object ti = i.next();
	    if (ti instanceof Tensor) {
		int[] d = ((Tensor)ti).dimensions();
		if (sdim == null)
		    sdim = d;
		else
		    Utility.pre(Utility.equalsAll(d, sdim), "components have uniform rank and dimensions");
	    } else {
		if (sdim == null)
		    sdim = new int[0];
		else
		    Utility.pre(sdim.length == 0, "components have uniform ran and dimensions");
	    }
	}
	if (sdim.length == 0)
	    return t;
	else {
	    final int[] tdim = t.dimensions();
	    final int[] dim = new int[tdim.length + sdim.length];
	    System.arraycopy(tdim, 0, dim, 0, tdim.length);
	    System.arraycopy(sdim, 0, dim, tdim.length, sdim.length);
	    Tensor r = Values.newInstance(dim);
	    for (Combinatorical index = Combinatorical.getPermutations(r.dimensions()); index.hasNext(); ) {
		int[] i = index.next();
		int[] ai = new int[tdim.length];
		System.arraycopy(i, 0, ai, 0, tdim.length);
		int[] bi = new int[sdim.length];
		System.arraycopy(i, tdim.length, bi, 0, i.length - tdim.length);
		Tensor tai = (Tensor) t.get(ai);
		r.set(i, tai.get(bi));
	    }
	    return flatten(r);
	}
    }

    // divisibility (gcd, lcm) and CRT

    /**
     * Returns greatest common divisor (GCD) of two elements of a ring.
     * The GCD is the greatest (with respect to dividability) element in the ring which divides both, a and b.
     * <p>
     * In an Euclidean ring R it is true that
     * <ul>
     *   <li>&forall;a&isin;R\{0} gcd(a, 0) = a</li>
     *   <li>&forall;a&isin;R,b&isin;R\{0} gcd(a, b) = gcd(b, a mod b)</li>
     * </ul>
     * @pre &not;(a==0 &and; b==0)
     * @return gcd(a,b) := inf(a,b) with divides as a partial order (N,|).
     * @note the father of all algorithms is Euclid.
     * @note the mother of all data structures are Euclidean rings.
     * @note There are even principal rings which are not Euclidean but where one can define the equivalent of the Euclidean algorithm.
     *  The algorithm for rational numbers was given in Book VII of Euclid's Elements, and the algorithm for reals appeared in Book X,
     *  and is the earliest example of an integer relation algorithm (Ferguson et al. 1999, also see Ferguson-Forcade algorithm in Ferguson et al. 1999). 
     * @see "Ferguson, H. R. P.; Bailey, D. H.; and Arno, S. "Analysis of PSLQ, An Integer Relation Finding Algorithm." Math. Comput. 68, 351-369, 1999."
     * @todo optimize
     * @has time complexity gcd&isin;O(log(max{||a||, ||b||}))
     * @todo we could multiply the resulting gcd, r, s by a constant!=0 (which is a unit if R is a field) to obtain a normalized gcd.
     */
    public static Euclidean gcd(Euclidean a, Euclidean b) {
	Euclidean list[] = {a, b};
	return gcd(list)[list.length];		//sic(!)
    }
    public static Euclidean lcm(Euclidean a, Euclidean b) {
	return (Euclidean) a.multiply(b).divide(gcd(a,b));
    }
    /**
     * n-ary and extended gcd.
     * <p>
     * This implementation uses the extended euclidian algorithm, 
     * Euclid-Lagrange-Berlekamp Algorithm (ELBA).
     * </p>
     * @param elements an array {a<sub>0</sub>,...,a<sub>n-1</sub>} &sube; R whose gcd to determine.
     * @pre &not;&forall;i elements[i]==0
     * @return an array {s<sub>0</sub>,...,s<sub>n-1</sub>, d} &sube; R where
     *  d = gcd({a<sub>0</sub>,...,a<sub>n-1</sub>}) = &sum;<sub>i=0,...,n-1</sub> s<sub>i</sub>*a<sub>i</sub>.
     * @internal note see extended euclidian algorithm ELBA (Euclid-Lagrange-Berlekamp Algorithm) for a decomposition into d=gcd(a,b),r,s in R such that d = r*a + s*b
     * @todo use documentation from gcd(Euclidean, Euclidean)
     * @see #gcd(Euclidean,Euclidean)
     */
    public static Euclidean[] gcd(final Euclidean elements[]) {
	switch (elements.length) {
	case 0:
	    throw new IllegalArgumentException("positive array-size expected. gcd not defined for zero elements");
	case 1:
	    return new Euclidean[] {(Euclidean) elements[1].one(), elements[0]};
	case 2:
	    // see below
	    break;
	default:
	    //@todo fold a with gcd, and somehow combine the overall gcd
	    throw new UnsupportedOperationException("gcd of more than two elements not yet implemented");
	}
	final Euclidean a = elements[0], b = elements[1];
	final Euclidean ZERO = (Euclidean) a.zero();
	final Euclidean ONE = (Euclidean) a.one();
	if (a.norm().equals(Values.ZERO) && b.norm().equals(Values.ZERO))
	    throw new ArithmeticException("gcd(0, 0) is undefined");
	if (b.norm().equals(Values.ZERO))
	    return new Euclidean[] {ONE, a};				//@todo verify that this is correct, especially for a=0 and in conjunction with rationals occuring in ranks < size
	Euclidean a0 = a, a1 = b;
	Euclidean r0 = ONE, r1 = ZERO;
	Euclidean s0 = ZERO, s1 = ONE;
	while (!a1.norm().equals(Values.ZERO)) {
	    // @invariant gcd(OLD(a), OLD(b)) == gcd(a0, a1)
	    //   &and; a0 == r0*a + s0*b
	    //   &and; a1 == r1*a + s1*b
			
	    // calculate the quotient a0 div a1
	    Euclidean q = a0.quotient(a1);
	    Euclidean t;
			
	    // transform (a0, a1)
	    t = (Euclidean) a0.subtract(q.multiply(a1));
	    // t == a0.modulo(a1)
	    assert a0.modulo(a1).equals(t) : "a mod b == a - (a div b)*b, i.e. " + a0.modulo(a1) + " == " + a0 + " - " + q + "*" + a1 + " == " + a0 + " - " + q.multiply(a1) + " == " + t;
	    // (a0, a1) := (a1, a0 mod a1);
	    a0 = a1;
	    a1 = t;
			
	    // transform (r0, r1)
	    t = (Euclidean) r0.subtract(q.multiply(r1));
	    r0 = r1;
	    r1 = t;

	    // transform (s0, s1)
	    t = (Euclidean) s0.subtract(q.multiply(s1));
	    s0 = s1;
	    s1 = t;
	} 
	assert !a0.norm().equals(Values.ZERO) : "gcd != 0 && @todo";
	assert a0.equals(r0.multiply(a).add(s0.multiply(b))) : "a0 == r0*a + s0*b";
	return new Euclidean[] {
	    r0, s0,
	    a0
	};
    } 

    /**
     * Returns greatest common divisor (GCD) of two integers.
     * The GCD is the greatest integer which divides both numbers.
     * <p>
     * In an Euclidean ring R it is true that
     * <ul>
     *   <li>&forall;a&isin;R gcd(a, 0) = a</li>
     *   <li>&forall;a&isin;R,b&isin;R\{0} gcd(a, b) = gcd(b, a mod b)</li>
     * </ul>
     * <p>
     * This implementation uses the non-extended Euclidian algorithm.
     * </p>
     * @pre &not;(a==0 &and; b==0)
     * @return gcd(a,b) := inf(a,b) with | (divides) as a partial order on <b>N</b>.
     * @internal note see extended Euclidian algorithm ELBA (Euclid-Lagrange-Berlekamp Algorithm) for a decomposition into g=ggT(a,b),r,s in R such that g = r*a + s*b
     * @todo optimize
     * @has time complexity gcd&isin;O(&#13266;(max{||a||, ||b||}))
     */
    public static int gcd(int a, int b) {
	if ((a == 0 && b == 0))
	    throw new ArithmeticException("gcd(0, 0) is undefined");
	if (b == 0)
	    return a;				//@todo verify that this is correct, especially for a=0 and in conjunction with rationals occuring in ranks < size
	boolean flipsign = false;
	if (a < 0) {
	    a *= -1;
	    flipsign = !flipsign;
	} 
	if (b < 0) {
	    b *= -1;
	    flipsign = !flipsign;
	} 
	assert a >= 0 && b > 0 : "a>=0 && b>0 for gcd, now";
	while (a != 0) {
	    // @invariant gcd(OLD(a), OLD(b)) == gcd(a, b)
	    //@todo we really should perform (a, b) := (b, a mod b); instead?
	    if (b > a) {
		int t = a;
		a = b;
		b = t;
	    } 
	    a %= b;
	} 
	assert b != 0 : "gcd != 0 && @todo";
	return flipsign ? -b : b;
    } 

    /**
     * Returns least common multiple (LCM) of two integers.
     * The LCM is the smallest integer which is a multiple of both numbers.
     * <p>
     * This implementation uses <code>a*b/gcd(a,b)</code>.</p>
     * @return lcm(a,b) := sup(a,b) with | "divides" as a partial order on <b>N</b>.
     */
    public static int lcm(int a, int b) {
	return a * b / gcd(a, b);
    } 

    /**
     * Simulatenously solve independent congruences.
     * <center>x &equiv; x<sub>i</sub> (mod m<sub>i</sub>) for i=1,...,n</center>
     * The Chinese Remainder Theorem guarantees a unique solution x
     * (modulo m<sub>1</sub>*...*m<sub>n</sub>), if the m<sub>i</sub> are coprime,
     * i.e. (m<sub>i</sub>)+(m<sub>j</sub>)=(1).
     * <table>
     *   <caption>The isomorphisms involved form the Chinese remainder algorithm</caption>
     *   <tr>
     *     <td class="leftOfMap">R<big>/</big>&#8898;<sub>&#957;=1,...,n</sub> I<sub>&#957;</sub></td>
     *     <td class="arrowOfMap">&rarr;&#771;</td>
     *     <td class="rightOfMap">R/I<sub>1</sub> &times; &#8230; &times; R/I<sub>n</sub></td>
     *   </tr>
     *   <tr>
     *     <td class="leftOfMap">x</td>
     *     <td class="arrowOfMap">&#8614;</td>
     *     <td class="rightOfMap"><big>(</big>x (mod m<sub>1</sub>),...,x (mod m<sub>n</sub>)<big>)</big></td>
     *   </tr>
     *   <tr>
     *     <td class="leftOfMap">&sum;<sub>i=1,...,n</sub> x<sub>i</sub><big>(</big>(&prod;<sub>j&ne;i</sub> m<sub>j</sub>)<sup>-1</sup> (mod m<sub>i</sub>)<big>)</big>&prod;<sub>j&ne;i</sub> m<sub>j</sub></td>
     *     <td class="arrowOfMap">&#8612;</td>
     *     <td class="rightOfMap">(x<sub>1</sub>,...,x<sub>n</sub>)</td>
     *   </tr>
     * </table>
     * <p>
     * The incremental algorithm is
     * <pre>
     * x := x<sub>1</sub>
     * M := 1
     * <span class="keyword">for</span> i := 2 <span class="keyword">to</span> n <span class="keyword">do</span>
     *     M := M*m<sub>i-1</sub>
     *     x := x + <big>(</big>(x<sub>i</sub> - x)*(M<sup>-1</sup> mod m<sub>i</sub>) mod m<sub>i</sub><big>)</big> * M
     * <span class="keyword">end for</span>
     * <span class="keyword">return</span> x
     * </pre>
     * </p>
     * @param x the array of congruence values x<sub>1</sub>,...,x<sub>n</sub>.
     * @param m the array of corresponding moduli m<sub>1</sub>,...,m<sub>n</sub>.
     * @pre &forall;i&ne;j (x[i])+(x[j])=(1), i.e. gcd(x[i],x[j])=1.
     * @return the unique solution x (modulo m<sub>1</sub>*...*m<sub>n</sub>).
     * @internal implements Chinese remainder algorithm which is a direct consequence of the
     *  Chinese Remainder Theorem.
     */
    public static final Arithmetic chineseRemainder(Arithmetic x[], Arithmetic m[]) {
	if (x.length != m.length)
	    throw new IllegalArgumentException("must give the same number of congruence values and modulos");
	Arithmetic xStar = x[0];
	Arithmetic M = xStar.one();
	for (int i = 1; i < m.length; i++) {
	    M = M.multiply(m[i-1]);
	    final Arithmetic c = Values.quotient((Euclidean) M, (Euclidean) m[i]).inverse()/*.representative()*/;		// the inverse modulo m[i] of m
	    final Arithmetic s = Values.quotient((Euclidean) (x[i].subtract(xStar)).multiply(c), (Euclidean) m[i]).representative();
	    xStar = xStar.add(s.multiply(M));
	}
	return xStar;
    }

    // Groebner

    /**
     * Reduce f with respect to g.
     * @param monomialOrder the order of monomials, which is decisive for the time complexity.
     * @return h if h is a reduced reduction of f with respect to g.
     * @internal elementaryReduction = "wenn ein Monom verschwindet und der Rest kleiner wird"
     * @internal reduction = transitiveClosure(elementaryReduction)
     */
    public static final Multinomial/*<R>*/ reduce(Multinomial/*<R>*/ f, Multinomial/*<R>*/[] g, final Comparator monomialOrder) {
	logger.log(Level.FINER, "reducing ({0} with respect to {1} ...", new Object[] {f, MathUtilities.format(g)});
	// enrich g with exponents of leading monomials
	final Pair/*<Multinomial<R>,int[]>*/[] basis = new Pair[g.length];
	for (int i = 0; i < basis.length; i++) {
	    final Multinomial gi = g[i];
	    final int[] leadingMonomial = leadingMonomial(gi, monomialOrder);
	    basis[i] = new Pair(gi, leadingMonomial);
	}
	//@internal would we profit from using orbital.logic.trs, even though polynomials do not currently have an introspectable internal structure per Functor.Composite?
	Function elementaryReduce = new Function/*<Multinomial,Multinomial>*/() {
		public Object apply(Object o) {
		    // only cast since Multinomial does not yet have dimensions()
		    final AbstractMultinomial f = (AbstractMultinomial)o;
		    //@internal we would prefer reverse direction, also starting with leading coefficient
		    final SortedSet occurring = new TreeSet(new ReverseComparator(monomialOrder));
		    occurring.addAll(occurringMonomials(f));
		    for (Iterator index = occurring.iterator(); index.hasNext(); ) {
			final int[] nu = (int[]) index.next();
			final Arithmetic/*>R<*/ cnu = f.get(nu);
			assert !cnu.norm().equals(Values.ZERO) : "@post of occurringMonomials(...)";
			reductionPolynomials:
			for (int j = 0; j < basis.length; j++) {
			    final Multinomial gj = (Multinomial)basis[j].A;
			    final int[] lgj = (int[])basis[j].B;
			    // always divisible in fields: coefficient cnu by lc(gj)=gj.get(lgj)
			    final Arithmetic/*>R<*/ cdiv = cnu.divide(gj.get(lgj));
			    // test divisibility of monomial X^nu by l(gj)
			    final int[] xdiv = new int[lgj.length];
			    for (int k = 0; k < xdiv.length; k++) {
				xdiv[k] = nu[k] -  lgj[k];
				if (xdiv[k] < 0)
				    continue reductionPolynomials;
			    }
			    // divisible
			    final Multinomial q = AbstractMultinomial.MONOMIAL(cdiv, xdiv);
			    final Multinomial reduction = f.subtract(q.multiply(gj));
			    assert reduction.get(nu).norm().equals(Values.ZERO) : AbstractMultinomial.MONOMIAL(Values.ONE, nu) + " does not occur in " + reduction + " anymore";
			    assert INDUCED(monomialOrder).compare(reduction, f) < 0 : reduction + "<" + f;
			    if (!reduction.get(nu).norm().equals(Values.ZERO))
				throw new AssertionError(AbstractMultinomial.MONOMIAL(Values.ONE, nu) + " does not occur in " + reduction + " anymore");
			    if (!(INDUCED(monomialOrder).compare(reduction, f) < 0))
				throw new AssertionError(reduction + "<" + f);
			    logger.log(Level.FINEST, "elementary reduction {0} - {1} * ({2}) == {3}", new Object[] {f, q, gj, reduction});
			    return reduction;
		}
		    }
		    return f;
		}
	    };
	return (Multinomial) Functionals.fixedPoint(elementaryReduce, f);
    }

    /**
     * Get a collection of those (exponents of) monomials that occur in f
     * (i.e. with coefficient &ne;0).
     */
    private static Collection/*_<int[]>_*/ occurringMonomials(final Multinomial f) {
	return Setops.select(null,
			     Setops.asList(Combinatorical.asIterator(Combinatorical.getPermutations(f.dimensions()))),
			     new Predicate() {
				 public boolean apply(Object i) {
				     return !f.get((int[])i).norm().equals(Values.ZERO);
				 }
			     });
    }
    /**
     * Get (the exponent of) the leading monomial l(f) of f.
     */
    private static int[] leadingMonomial(Multinomial f, Comparator monomialOrder) {
	return (int[]) Collections.max(occurringMonomials(f), monomialOrder);
    }

    /**
     * Get the reduced Groebner basis of g.
     * <p>
     * <dl>
     * A Groebner basis G &sube; I &#8884; K[X<sub>1</sub>,...,X<sub>n</sub>] is
     *   <dt>minimal</dt>
     *   <dd>&forall;g,h&isin;G l(g) &#8740; l(h)</dd>
     *   <dt>reduced</dt>
     *   <dd>&forall;g&isin;G g reduced with respect to G\{g}
     *     <div>&rArr; G minimal</div>
     *   </dd>
     * </dl>
     * Two minimal Groebner bases have the same number of elements and the same leading monomials.
     * The reduced Groebner basis exists and is unique.
     * </p>
     * @param monomialOrder the order of monomials, which is decisive for the time complexity.
     * @note The Buchberger algorithm used to construct a Groebner basis is equivalent
     *  to {@link #gcd(Euclidean[])} in case of one variable,
     *  and to {@link LUDecomposition} in case of linear polynomials.
     * @internal whenever an elementary reduction is possible, use the reduced polynomial instead of the original polynomial.
     * @xxx do we really get the unique reduced Groebner basis? The elements seem to be reduced, though they are perhaps not unique. However, different monomial orders may have been the cause.
     * @internal GroebnerBasis = "if the term rewrite system for reduce is confluent"
     */
    public static final Multinomial/*<R>*/[] groebnerBasis(Multinomial/*<R>*/[] g, final Comparator monomialOrder) {
	ergaenzeBasis:
	while (true) {
	    for (int i = 0; i < g.length; i++) {
		for (int j = i + 1; j < g.length; j++) {
		    // construct Sgigj = S(g[i], g[j])
		    final int[] lgi = leadingMonomial(g[i], monomialOrder);
		    final int[] lgj = leadingMonomial(g[j], monomialOrder);
		    // construct X^nu and X^mu such that l(X^nu*g[i])==l(X^mu*g[j]) (also @see #lcm(Euclidean,Euclidean))
		    final int[] d = Functionals.map(Operations.max, lgi, lgj);
		    final int[] nu = Functionals.map(Operations.subtract, d, lgi);
		    final int[] mu = Functionals.map(Operations.subtract, d, lgj);
		    assert Setops.all(Values.valueOf(nu).iterator(), Values.valueOf(mu).iterator(), new orbital.logic.functor.BinaryPredicate() { public boolean apply(Object nui, Object mui) {return nui.equals(Values.ZERO) || mui.equals(Values.ZERO);} }) : "coprime " + AbstractMultinomial.MONOMIAL(Values.ONE, nu) + " and " + AbstractMultinomial.MONOMIAL(Values.ONE, mu);
		    // Xpowernugi = 1/lc(g[i]) * X<sup>nu</sup>*g[i]
		    final Multinomial Xpowernugi = AbstractMultinomial.MONOMIAL(g[i].get(lgi).inverse(), nu).multiply(g[i]);
		    // Xpowernugi = 1/lc(g[j]) * X<sup>mu</sup>*g[j]
		    final Multinomial Xpowermugj = AbstractMultinomial.MONOMIAL(g[j].get(lgj).inverse(), mu).multiply(g[j]);
		    assert Utility.equalsAll(leadingMonomial(Xpowernugi, monomialOrder), leadingMonomial(Xpowermugj, monomialOrder)) : "construction should generate equal leading monomials (" + leadingMonomial(Xpowernugi, monomialOrder) + " of " + Xpowernugi + " and " + leadingMonomial(Xpowermugj, monomialOrder) + " of " + Xpowermugj + ") which vanish by subtraction";
		    final Multinomial Sgigj = Xpowernugi.subtract(Xpowermugj);
		    assert Sgigj.get(d).norm().equals(Values.ZERO) : "construction should generate equal leading monomials which vanish by subtraction";
		    logger.log(Level.FINER, "S(gi,gj) = {0} * ({1})  -  {2} * ({3})", new Object[] {AbstractMultinomial.MONOMIAL(g[i].get(lgi).inverse(), nu), g[i], AbstractMultinomial.MONOMIAL(g[j].get(lgj).inverse(), mu), g[j]});
		    final Multinomial r = reduce(Sgigj, g, monomialOrder);
		    // tolerant equality to 0 (roughly 0)
		    if (r.degreeValue() >= 0
			&& ((AbstractMultinomial)r).tensorViewOfCoefficients().norm().equals(Values.ZERO, Values.valueOf(MathUtilities.getDefaultTolerance()))) {
			logger.log(Level.FINE, "add reduction {0}", r);
			List bprime = new LinkedList(Arrays.asList(g));
			bprime.add(r);
			g = (Multinomial[]) bprime.toArray(new Multinomial[0]);
			continue ergaenzeBasis;
		    }
		}
	    }
	    break ergaenzeBasis;
	}
	return reduceGroebnerBasis(g, monomialOrder);
    }

    private static final Multinomial/*<R>*/[] reduceGroebnerBasis(Multinomial/*<R>*/[] g, final Comparator monomialOrder) {
	List basis = new LinkedList(Arrays.asList(g));
	logger.log(Level.FINE, "reducing Groebner basis {0}", basis);
	replaceWithReductions:
	while (true) {
	    for (int i = 0; i < basis.size(); i++) {
		Multinomial gi = (Multinomial) basis.get(i);
		List others = new LinkedList(basis);
		others.remove(i);
		Multinomial r = reduce(gi, (Multinomial[])others.toArray(new Multinomial[0]), monomialOrder);
		if (!r.equals(gi)) {
		    // g[i] not reduced with respect to the others, so replace by its reduction
		    basis.remove(i);
		    if (r.degreeValue() < 0)
			// skip adding 0
			logger.log(Level.FINER, "skip {0}", r);
		    else
			basis.add(r);
		    //@internal or go on with this for loop until nothing changes anymore
		    continue replaceWithReductions;
		}
	    }
	    break replaceWithReductions;
	}
	return (Multinomial[]) basis.toArray(new Multinomial[0]);
    }
}// AlgebraicAlgorithms
