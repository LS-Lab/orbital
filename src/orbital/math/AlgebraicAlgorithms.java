/**
 * @(#)MathUtilities.java 1.1 2002-08-21 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Function;
import java.util.Comparator;
import java.io.Serializable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Collections;
import java.util.List;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
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
 * @stereotype Utilities
 * @stereotype Module
 * @author Andr&eacute; Platzer
 * @version 1.1, 2002-08-21
 * @see MathUtilities
 * @see orbital.util.Utility
 */
public final class AlgebraicAlgorithms {
    private static final Logger logger = Logger.getLogger(AlgebraicAlgorithms.class.getName());
    /**
     * prevent instantiation - module class
     */
    private AlgebraicAlgorithms() {}

    // admissible total orders on monoid of monomials

    /**
     * Lexicographical order on monoid of monomials.
     * This is an admissible total order.
     * The monomials are expected to be encoded as their exponents in the form of
     * <code>int[]</code>s.
     * <div>
     *   X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &le; X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>
     *   :&hArr; X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>=X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup> <br />
     *   &or; i<sub>k</sub>&lt;j<sub>k</sub> for k := min{k &brvbar; i<sub>k</sub>&ne;j<sub>k</sub>}
     * </div>
     * Especially X<sub>0</sub><sup>2</sup> &gt; X<sub>0</sub> &gt; X<sub>1</sub><sup>2</sup> &gt; X<sub>1</sub> &gt; &#8230; &gt; X<sub>n-1</sub> &gt; 1.
     */
    public static final Comparator LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object m1, Object m2) {
		final Vector/*<Integer>*/ nu = (Vector/*<Integer>*/) m1;
		final Vector/*<Integer>*/ mu = (Vector/*<Integer>*/) m2;
		if (nu.dimension() != mu.dimension())
		    throw new IllegalArgumentException("incompatible monomial exponents from polynomial rings with a different number of variables");
		for (Iterator k = nu.iterator(), j = mu.iterator(); k.hasNext() || j.hasNext(); ) {
		    assert k.hasNext() && j.hasNext() : "equal dimensions have equally structured iterators";
		    int c = ((Integer)k.next()).subtract((Integer)j.next()).intValue();
		    if (c != 0)
			return c;
		}
		return 0;
	    }

	    public String toString() {
		return AlgebraicAlgorithms.class.getName() + ".LEXICOGRAPHIC";
	    }
	};

    /**
     * Reverse lexicographical order on monoid of monomials.
     * This is an admissible total order.
     * The monomials are expected to be encoded as their exponents in the form of
     * <code>int[]</code>s.
     * <div>
     *   X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &le; X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>
     *   :&hArr; X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>=X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup> <br />
     *   &or; i<sub>k</sub>&lt;j<sub>k</sub> for k := max{k &brvbar; i<sub>k</sub>&ne;j<sub>k</sub>}
     * </div>
     * Especially 1 &lt; X<sub>0</sub> &lt; X<sub>0</sub><sup>2</sup> &lt; X<sub>1</sub> &lt; X<sub>1</sub><sup>2</sup> &lt; &#8230; &lt; X<sub>n-1</sub>.
     */
    public static final Comparator REVERSE_LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object m1, Object m2) {
		final Vector/*<Integer>*/ nu = (Vector/*<Integer>*/) m1;
		final Vector/*<Integer>*/ mu = (Vector/*<Integer>*/) m2;
		if (nu.dimension() != mu.dimension())
		    throw new IllegalArgumentException("incompatible monomial exponents from polynomial rings with a different number of variables");
		for (int k = nu.dimension() - 1; k >= 0; k--) {
		    int c = ((Integer)nu.get(k)).subtract((Integer)mu.get(k)).intValue();
		    if (c != 0)
			return c;
		}
		return 0;
	    }

	    public String toString() {
		return AlgebraicAlgorithms.class.getName() + ".REVERSE_LEXICOGRAPHIC";
	    }
	};
    
    /**
     * Degree lexicographical order on monoid of monomials.
     * Thus compares for degree in favor of lexicographical comparison.
     * This is an admissible total order.
     * The monomials are expected to be encoded as their exponents in the form of
     * <code>int[]</code>s.
     * <div>
     *   X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &le; X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>
     *   :&hArr; deg(X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>)&lt;deg(X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>) <br />
     *   &or; <big>(</big>deg(X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>)=deg(X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>) &and; X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> {@link #LEXICOGRAPHIC &lt;<sub>lexico</sub>} X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup><big>)</big>
     * </div>
     * Especially X<sub>0</sub><sup>2</sup> &gt; X<sub>1</sub><sup>2</sup> &gt; X<sub>0</sub> &gt; X<sub>1</sub> &gt; &#8230; &gt; X<sub>n-1</sub> &gt; 1.
     */
    public static final Comparator DEGREE_LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object m1, Object m2) {
		final Vector/*<Integer>*/ nu = (Vector/*<Integer>*/) m1;
		final Vector/*<Integer>*/ mu = (Vector/*<Integer>*/) m2;
		if (nu.dimension() != mu.dimension())
		    throw new IllegalArgumentException("incompatible monomial exponents from polynomial rings with a different number of variables");
		int c = ((Integer)Operations.sum.apply(nu)).intValue()
		    - ((Integer)Operations.sum.apply(mu)).intValue();
		if (c != 0)
		    return c;
		else
		    return LEXICOGRAPHIC.compare(m1, m2);
	    }

	    public String toString() {
		return AlgebraicAlgorithms.class.getName() + ".DEGREE_LEXICOGRAPHIC";
	    }
	};

    
    /**
     * The (partial) order on polynomials induced by an admissible total order on monomials.
     * <dl class="def" id="monomialOrder">
     * Let &le; &sube; M<sub>n</sub> &times; M<sub>n</sub> be a total order on the monoid of monomials
     * M<sub>n</sub> := {X<sup class="vector">&nu;</sup> := X<sub>1</sub><sup>&nu;<sub>1</sub></sup>&sdot;&#8230;&sdot;X<sub>n</sub><sup>&nu;<sub>n</sub></sup> &brvbar; <span class="vector">&nu;</span>=(&nu;<sub>1</sub>,&#8230;,&nu;<sub>n</sub>) &isin; <b>N</b><sup>n</sup>}.
     *   <dt id="admissible">admissible</dt>
     *   <dd>&le; is admissible, if
     *     <ol class="and">
     *       <li>&forall;X<sup class="vector">&nu;</sup>,X<sup class="vector">&mu;</sup>&isin;M<sub>n</sub> X<sup class="vector">&nu;</sup> &le; X<sup class="vector">&mu;</sup> &rArr; &forall;X<sup class="vector">&lambda;</sup>&isin;M<sub>n</sub> X<sup class="vector">&nu;</sup>&sdot;X<sup class="vector">&lambda;</sup> &le; X<sup class="vector">&mu;</sup>&sdot;X<sup class="vector">&lambda;</sup></li>
     *       <li>&#8739; &sube; &le;<br />
     *         (&hArr; 1 = min M<sub>n</sub>)</li>
     *     </ol>
     *     &rArr; M<sub>n</sub> is well-ordered with &le;.
     *   </dd>
     * </dl>
     * @param monomialOrder is the underlying admissible total order on the monoid of monomials to use.
     *  Such monomial orders include {@link #LEXICOGRAPHIC}, {@link #REVERSE_LEXICOGRAPHIC}, {@link #DEGREE_LEXICOGRAPHIC}.
     * @see #LEXICOGRAPHIC
     * @see #REVERSE_LEXICOGRAPHIC
     * @see #DEGREE_LEXICOGRAPHIC
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
	public int hashCode() {
	    return monomialOrder.hashCode();
	}
	public int compare(Object p1, Object p2) {
	    final SortedSet amon = new TreeSet(new ReverseComparator(monomialOrder));
	    amon.addAll(occurringMonomials((Polynomial)p1));
	    final SortedSet bmon = new TreeSet(new ReverseComparator(monomialOrder));
	    bmon.addAll(occurringMonomials((Polynomial)p2));
	    try {
		// like Setops.find(Functionals.map("asFunction"(monomialOrder), amon.iterator(), bmon.iterator()), Functionals.bindSecond(Predicates.equal, Values.ZERO));
		// but with lazy evaluation of Functionals.map such that it stops at the first even if the iterators have inequal lengths
		for (Iterator i = amon.iterator(), j = bmon.iterator(); i.hasNext() || j.hasNext(); ) {
		    //@todo which alternative?
		    if (!i.hasNext() || !j.hasNext())
			if (((Polynomial)p1).degreeValue() < 0)
			    return -1;
			else if (((Polynomial)p2).degreeValue() < 0)
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
			    return Values.getDefaultInstance().MONOMIAL((int[])i);
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
	    Tensor r = Values.getDefaultInstance().newInstance(dim);
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
     * Returns greatest common divisor (gcd) of two elements of an (Euclidean) ring.
     * @see #gcd(Euclidean,Euclidean)
     */
    public static final BinaryFunction gcd = new BinaryFunction/*<Euclidean,Euclidean,Euclidean>*/() {
	    public Object apply(Object a, Object b) {
		return gcd((Euclidean)a, (Euclidean)b);
	    }
	};
    /**
     * Returns greatest common divisor (gcd) of two elements of an (Euclidean) ring.
     * The gcd is the greatest element (with respect to divisibility) in the ring which divides both, a and b.
     * <p>
     * In an Euclidean ring R it is true that
     * <ul>
     *   <li>&forall;a&isin;R\{0} gcd(a, 0) = a</li>
     *   <li>&forall;a&isin;R,b&isin;R\{0} gcd(a, b) = gcd(b, a mod b)</li>
     *   <li>Also see {@link Euclidean}</li>
     * </ul>
     * </p>
     * @preconditions &not;(a==0 &and; b==0)
     * @return gcd(a,b) := inf(a,b) with divides (&#8739;) as a partial order on R.
     * @note the father of all algorithms is Euclid.
     * @note the mother of all data structures are Euclidean rings.
     * @note There are even principal ideal rings which are not Euclidean but where one can define the equivalent of the Euclidean algorithm.
     *  The algorithm for rational numbers was given in Book VII of Euclid's Elements, and the algorithm for reals appeared in Book X,
     *  and is the earliest example of an integer relation algorithm (Ferguson et al. 1999, also see Ferguson-Forcade algorithm in Ferguson et al. 1999).
     * @note gcd and lcm also exist in factorial rings (unique factorization domains), although they
     *  do not possess the same properties and their computation is far more expensive then.
     * @see "Ferguson, H. R. P.; Bailey, D. H.; and Arno, S. "Analysis of PSLQ, An Integer Relation Finding Algorithm." Math. Comput. 68, 351-369, 1999."
     * @todo optimize
     * @has time complexity gcd&isin;O(log(max{||a||, ||b||}))
     * @todo we could multiply the resulting gcd, r, s by a constant!=0 (which is a unit if R is a field) to obtain a normalized gcd.
     */
    public static Euclidean gcd(Euclidean a, Euclidean b) {
	Euclidean list[] = {a, b};
	return gcd(list)[list.length];		//sic(!)
    }
    /**
     * Returns least common multiple (lcm) of two elements of an (Euclidean) ring.
     * @see #lcm(Euclidean,Euclidean)
     */
    public static final BinaryFunction lcm = new BinaryFunction/*<Euclidean,Euclidean,Euclidean>*/() {
	    public Object apply(Object a, Object b) {
		return lcm((Euclidean)a, (Euclidean)b);
	    }
	};
    /**
     * Returns least common multiple (lcm) of two ring elements.
     * The lcm is the smallest ring element (with respect to divisibility) which is a multiple of both.
     * <p>
     * This implementation uses <code>a*b/gcd(a,b)</code>.</p>
     * @return lcm(a,b) := sup(a,b) with &#8739; "divides" as a partial order on R.
     */
    public static Euclidean lcm(Euclidean a, Euclidean b) {
	return (Euclidean) a.multiply(b).divide(gcd(a,b));
    }
    /**
     * n-ary and extended gcd.
     * <p>
     * This implementation uses the extended euclidian algorithm, 
     * Euclid-Lagrange-Berlekamp Algorithm (ELBA).
     * </p>
     * @param elements an array {a<sub>0</sub>,&#8230;,a<sub>n-1</sub>} &sube; R whose gcd to determine.
     * @preconditions &not;&forall;i elements[i]=0
     * @return an array {s<sub>0</sub>,&#8230;,s<sub>n-1</sub>, d} &sube; R with
     *  cofactors s<sub>i</sub> and greatest common divisor d,
     *  where
     *  d = gcd({a<sub>0</sub>,&#8230;,a<sub>n-1</sub>}) = &sum;<sub>i=0,&#8230;,n-1</sub> s<sub>i</sub>*a<sub>i</sub>.
     * @internal gcd({a<sub>0</sub>,&#8230;,a<sub>n-1</sub>}) = gcd(gcd({a<sub>0</sub>,&#8230;,a<sub>n-2</sub>}),a<sub>n-2</sub>)
     * @internal ELBA (Euclid-Lagrange-Berlekamp Algorithm) for a decomposition into d=gcd(a,b),r,s in R such that d = r*a + s*b
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
	    //@todo fold a with gcd, and somehow combine the overall gcd, and the s[i]
	    Euclidean d = (Euclidean) Functionals.foldLeft(gcd, elements[0], elements);
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
	    // @invariants gcd(OLD(a), OLD(b)) == gcd(a0, a1)
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
     * Returns greatest common divisor (gcd) of two integers.
     * @preconditions &not;(a==0 &and; b==0)
     * @todo optimize, or simply call {@link #gcd(Euclidean,Euclidean)}
     */
    static int gcd(int a, int b) {
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
	    // @invariants gcd(OLD(a), OLD(b)) == gcd(a, b)
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
     * Returns least common multiple (lcm) of two integers.
     * @see #lcm(Euclidean,Euclidean)
     */
    static int lcm(int a, int b) {
	return a * b / gcd(a, b);
    } 

    /**
     * Simulatenously solve independent congruences.
     * <center>x &equiv; x<sub>i</sub> (mod m<sub>i</sub>) for i=1,&#8230;,n</center>
     * The Chinese Remainder Theorem guarantees a unique solution x
     * (modulo m<sub>1</sub>*&#8230;*m<sub>n</sub>), if the m<sub>i</sub> are coprime,
     * i.e. (m<sub>i</sub>)+(m<sub>j</sub>)=(1).
     * <table>
     *   <caption>The isomorphisms involved form the Chinese remainder algorithm</caption>
     *   <tr>
     *     <td class="leftOfMap">R<big>/</big>&#8898;<sub>&#957;=1,&#8230;,n</sub> I<sub>&#957;</sub></td>
     *     <td class="arrowOfMap">&rarr;&#771;</td>
     *     <td class="rightOfMap">R/I<sub>1</sub> &times; &#8230; &times; R/I<sub>n</sub></td>
     *   </tr>
     *   <tr>
     *     <td class="leftOfMap">x</td>
     *     <td class="arrowOfMap">&#8614;</td>
     *     <td class="rightOfMap"><big>(</big>x (mod m<sub>1</sub>),&#8230;,x (mod m<sub>n</sub>)<big>)</big></td>
     *   </tr>
     *   <tr>
     *     <td class="leftOfMap">&sum;<sub>i=1,&#8230;,n</sub> x<sub>i</sub><big>(</big>(&prod;<sub>j&ne;i</sub> m<sub>j</sub>)<sup>-1</sup> (mod m<sub>i</sub>)<big>)</big>&prod;<sub>j&ne;i</sub> m<sub>j</sub></td>
     *     <td class="arrowOfMap">&#8612;</td>
     *     <td class="rightOfMap">(x<sub>1</sub>,&#8230;,x<sub>n</sub>)</td>
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
     * @param x the array of congruence values x<sub>1</sub>,&#8230;,x<sub>n</sub>.
     * @param m the array of corresponding moduli m<sub>1</sub>,&#8230;,m<sub>n</sub>.
     * @preconditions &forall;i&ne;j (m[i])+(m[j])=(1), i.e. gcd(m[i],m[j])=1
     *  &and; m[i] use compatible representatives.
     * @return the unique solution x (modulo m<sub>1</sub>*&#8230;*m<sub>n</sub>).
     * @note Newton interpolation and especially Lagrange interpolation are just special cases of
     * incremental Chinese Remainder Theorem.
     * @internal implements Chinese remainder algorithm which is a direct consequence of the
     *  Chinese Remainder Theorem.
     * @todo change Arithmetic to Euclidean since the implementation won't work otherwise anyway?
     */
    public static final Quotient/*<Arithmetic>*/ chineseRemainder(Arithmetic x[], Arithmetic m[]) {
	if (x.length != m.length)
	    throw new IllegalArgumentException("must give the same number of congruence values and modulos");
	final Values vf = Values.getDefaultInstance();
	Euclidean xStar = (Euclidean) x[0];
	Euclidean M = (Euclidean) xStar.one();
	for (int i = 1; i < m.length; i++) {
	    M = (Euclidean) M.multiply(m[i-1]);
	    final Arithmetic c = vf.quotient(M, (Euclidean) m[i]).inverse()/*.representative()*/;		// the inverse modulo m[i] of m
	    final Arithmetic s = vf.quotient((Euclidean) (x[i].subtract(xStar)).multiply(c), (Euclidean) m[i]).representative();
	    xStar = (Euclidean) xStar.add(s.multiply(M));
	}
	M = (Euclidean) M.multiply(m[m.length-1]);
	assert M.equals(Operations.product.apply(vf.valueOf(m))) : "total modulus " + M + " = " + Operations.product.apply(vf.valueOf(m));
	return vf.quotient(xStar, M);
    }

    // Groebner basis

    /**
     * Reduce f with respect to g.
     * @param g the collection of multinomials for reducing f. 
     * @param monomialOrder the <a href="#monomialOrder">order of monomials</a>, which is decisive for the time complexity.
     * @return h if h is a reduced reduction of f with respect to g.
     * @see #reduce(Collection, Comparator)
     */
    public static final Polynomial/*<R>*/ reduce(Polynomial/*<R>*/ f, Collection/*_Polynomial<R,S>_*/ g, final Comparator/*_<S>_*/ monomialOrder) {
	return (Polynomial) reduce(g, monomialOrder).apply(f);
    }
    /**
     * Reduce<sub>g</sub>:K[X<sub>0</sub>,...,X<sub>n-1</sub>]&rarr;K[X<sub>0</sub>,...,X<sub>n-1</sub>]; f &#8614; "f reduced with respect to g".
     * @param g the collection of multinomials for reducing polynomials.
     * @param monomialOrder the <a href="#monomialOrder">order of monomials</a>, which is decisive for the time complexity.
     * @return a function that reduces polynomials with respect to g.
     * @see Values#quotient(Polynomial,Set,Comparator)
     * @internal elementaryReduction = "wenn ein Monom verschwindet (mit Hilfe des Leitmonomos eines der g) und der Rest kleiner wird"
     * @internal reduction = transitiveClosure(elementaryReduction)
     */
    public static final Function/*<Polynomial<R,S>,Polynomial<R,S>>*/ reduce(Collection/*_Polynomial<R,S>_*/ g, Comparator/*_<S>_*/ monomialOrder) {
	return new ReductionFunction(g, monomialOrder);
    }
    private static final class ReductionFunction implements Function/*<Polynomial<R,S>,Polynomial<R,S>>*/, Serializable {
	private static final long serialVersionUID = -51945340881045435L;
	private final Collection/*_<Polynomial<R,S>>_*/ g;
	private final Comparator monomialOrder;
	private final Function elementaryReduce;
	public ReductionFunction(Collection/*_Polynomial<R,S>_*/ g, Comparator newmonomialOrder) {
	    Utility.pre(Setops.all(g, Functionals.bindSecond(Utility.instanceOf, Polynomial.class)), "collection<" + Polynomial.class.getName() + "> expected");
	    this.g = g;
	    this.monomialOrder = newmonomialOrder;
	    // enrich g with exponents of leading monomials
	    final Pair/*<Polynomial<R,S>,S>*/[] basis = new Pair[g.size()];
	    {
		Iterator it = g.iterator();
		for (int i = 0; i < basis.length; i++) {
		    final Polynomial gi = (Polynomial) it.next();
		    final Arithmetic leadingMonomial = leadingMonomial(gi, monomialOrder);
		    basis[i] = new Pair(gi, leadingMonomial);
		}
	    }
	    //@internal would we profit from using orbital.logic.trs, even though polynomials do not currently have an introspectable internal structure per Functor.Composite?
	    this.elementaryReduce = new Function/*<Polynomial,Polynomial>*/() {
		    public Object apply(Object o) {
			final Polynomial f = (Polynomial)o;
			final Values vf = Values.getDefaultInstance();
			//@internal we would prefer reverse direction, also starting with leading coefficient
			final SortedSet occurring = new TreeSet(new ReverseComparator(monomialOrder));
			occurring.addAll(occurringMonomials(f));
			for (Iterator index = occurring.iterator(); index.hasNext(); ) {
			    final Arithmetic/*>S<*/ nu = (Arithmetic) index.next();
			    final Arithmetic/*>R<*/ cnu = f.get(nu);
			    assert !cnu.norm().equals(Values.ZERO) : "@postconditions of occurringMonomials(...)";
			    reductionPolynomials:
			    for (int j = 0; j < basis.length; j++) {
				final Polynomial gj = (Polynomial)basis[j].A;
				final Arithmetic/*>S<*/ lgj = (Arithmetic)basis[j].B;

				// test divisibility
				final Arithmetic/*>R<*/ cdiv;
				final Arithmetic xdiv;
				try {
				    // test divisibility of coefficient cnu by lc(gj)=gj.get(lgj)
				    cdiv = cnu.divide(gj.get(lgj));
				    // test divisibility of monomial X^nu by l(gj)
				    xdiv = nu.subtract(lgj);
				    //@internal the following is a trick for S=<b>N</b><sup>n</sup> represented as <b>Z</b><sup>n</sup> (get rid when introducing Natural extends Integer)
				    if (Setops.some(((Vector)xdiv).iterator(), Functionals.bindSecond(Predicates.less, Values.ZERO)))
					continue reductionPolynomials;
				}
				catch (ArithmeticException indivisible) {
				    continue reductionPolynomials;
				}
				// divisible, then q := cdiv*X<sup>xdiv</sup>
				final Polynomial q = vf.MONOMIAL(cdiv, xdiv);
				final Polynomial reduction = f.subtract(q.multiply(gj));
				assert reduction.get(nu).norm().equals(Values.ZERO) : vf.MONOMIAL(Values.ONE, nu) + " does not occur in " + reduction + " anymore";
				assert INDUCED(monomialOrder).compare(reduction, f) < 0 : reduction + "<" + f;
				if (!reduction.get(nu).norm().equals(Values.ZERO))
				    throw new AssertionError(vf.MONOMIAL(Values.ONE, nu) + " does not occur in " + reduction + " anymore");
				if (!(INDUCED(monomialOrder).compare(reduction, f) < 0))
				    throw new AssertionError(reduction + "<" + f);
				logger.log(Level.FINEST, "elementary reduction {0} - {1} * ({2}) == {3}", new Object[] {f, q, gj, reduction});
				return reduction;
			    }
			}
			return f;
		    }
		};
	}
	public boolean equals(Object o) {
	    return (o instanceof ReductionFunction)
		&& Utility.equals(g, ((ReductionFunction) o).g)
		&& Utility.equals(monomialOrder, ((ReductionFunction) o).monomialOrder);
	}
	public int hashCode() {
	    return Utility.hashCode(g) ^ Utility.hashCode(monomialOrder);
	}
	public Object apply(Object f) {
	    logger.log(Level.FINEST, "reducing ({0} with respect to {1} ...", new Object[] {f, g});
	    return Functionals.fixedPoint(elementaryReduce, f);
	}
    }

    /**
     * Get the reduced Groebner basis of g.
     * <p>
     * <dl class="def">
     *   <dt>Groebner basis</dt>
     *   <dd>
     *     A finite generating system G is a Groebner basis of I&#8884;K[X<sub>1</sub>,&#8230;,X<sub>n</sub>]
     *     if one of the following equivalent conditions is satisfied.
     *     <ol class="equiv">
     *       <li>L(I)=L(G)
     *           where L(A) := {l(a)X<sup class="vector">&nu;</sup> &brvbar; a&isin;A,X<sup class="vector">&nu;</sup>&isin;M<sub>n</sub>} "&#8884;" M<sub>n</sub> for A&sube;M<sub>n</sub>
     *       </li>
     *       <li>&forall;f&isin;I\{0} &exist;g&isin;G l(g) &#8739; l(f)</li>
     *       <li>&forall;f&isin;I f is reduced to 0 with respect to G</li>
     *       <li><big>(</big>f&isin;I &hArr; &exist;q<sub>g</sub>&isin;K[X<sub>1</sub>,&#8230;,X<sub>n</sub>] f = &sum;<sub>g&isin;G</sub> q<sub>g</sub>g &and; l(f) = max{l(q<sub>g</sub>g) &brvbar; g&isin;G}<big>)</big></li>
     *       <li>each f&isin;K[X<sub>1</sub>,&#8230;,X<sub>n</sub>] has a unique (reduced) reduction with respect to G</li>
     *       <li>the G-reduced polynomials form a system of representatives of K[X<sub>1</sub>,&#8230;,X<sub>n</sub>]/I</li>
     *       <li>&forall;f&ne;g&isin;G 0 is a reduction of
     *         S(f,g) := 1&#8725;l<sub>c</sub>(f)*X<sup class="vector">&nu;</sup>&sdot;f - 1&#8725;l<sub>c</sub>(g)*X<sup class="vector">&mu;</sup>&sdot;g <br />
     *         where X<sup class="vector">&nu;</sup>,X<sup class="vector">&mu;</sup> are coprime and such that
     *         l(X<sup class="vector">&nu;</sup>&sdot;f)=l(X<sup class="vector">&mu;</sup>&sdot;g)
     *       </li>
     *     </ol>
     *   </dd>
     * </dl>
     * <dl class="def">
     * A Groebner basis G of I &#8884; K[X<sub>1</sub>,&#8230;,X<sub>n</sub>] is
     *   <dt>minimal</dt>
     *   <dd>&forall;g&ne;h&isin;G l(g) &#8740; l(h)</dd>
     *   <dt>reduced</dt>
     *   <dd>&forall;g&isin;G g reduced with respect to G\{g}
     *     <div>&rArr; G minimal</div>
     *   </dd>
     * </dl>
     * Two minimal Groebner bases have the same number of elements and the same leading monomials.
     * There is a unique reduced Groebner basis.
     * </p>
     * @param g the collection of multinomials that is a generating system of the ideal (g)
     *  for which to construct a Groebner basis.
     * @param monomialOrder the <a href="#monomialOrder">order of monomials</a>, which is decisive for the time complexity.
     * @note The Buchberger algorithm used to construct a Groebner basis is equivalent
     *  to {@link #gcd(Euclidean[])} in case of one variable,
     *  and to {@link LUDecomposition} in case of linear polynomials.
     * @internal whenever an elementary reduction is possible, use the reduced polynomial instead of the original polynomial.
     * @internal GroebnerBasis = "if the term rewrite system for reduce is confluent"
     * @todo scale to get rid of denominators, and of non-primitive polynomials (divide by gcd of coefficients)
     */
    public static final Set/*_<Polynomial<R,S>>_*/ groebnerBasis(Set/*_<Polynomial<R,S>>_*/ g, final Comparator/*_<S>_*/ monomialOrder) {
	Set/*_<Polynomial<R,S>>_*/ rgb = reducedGroebnerBasis(g, monomialOrder);
	Set temp, nrgb = null;
	assert (temp = reducedGroebnerBasis(rgb, monomialOrder)).equals(rgb) : "reduced Groebner basis " + temp + " of a reduced Groebner basis equals the former Groebner basis";
	assert (temp = groebnerBasisImpl(rgb, monomialOrder)).equals(rgb) : "(non-reduced) Groebner basis " + temp + " of a (reduced) Groebner basis " + rgb + " equals the former Groebner basis";
	assert containsAll(nrgb = groebnerBasisImpl(g, monomialOrder), g, monomialOrder) : "the original generating system " + g + " is in the ideal spanned by its (non-reduced) Groebner basis " + nrgb;
	assert equalSpan(rgb, nrgb, monomialOrder) : "reduced Groebner basis " + rgb + " and (non-reduced) Groebner basis " + nrgb + " of " + g + " have equal span";
	assert containsAll(rgb, g, monomialOrder) : "the original generating system " + g + " is in the ideal spanned by its (reduced) Groebner basis " + rgb;
	return rgb;
    }
    /**
     * Get the reduced Groebner basis of g (Implementation).
     */
    private static final Set/*_<Polynomial<R,S>>_*/ reducedGroebnerBasis(Collection/*_<Polynomial<R,S>>_*/ g, final Comparator monomialOrder) {
	return new HashSet(reduceGroebnerBasis(new ArrayList(groebnerBasisImpl(g, monomialOrder)), monomialOrder));
    }
    /**
     * Get the non-reduced Groebner basis of g (Implementation).
     */
    private static final Set/*_<Polynomial<R,S>>_*/ groebnerBasisImpl(Collection/*_<Polynomial<R,S>>_*/ gg, final Comparator monomialOrder) {
	final List/*_<Polynomial<R,S>>_*/ g = new ArrayList(gg);
	final Values vf = Values.getDefaultInstance();
	ergaenzeGroebnerBasis:
	while (true) {
	    for (int i = 0; i < g.size(); i++) {
		for (int j = i + 1; j < g.size(); j++) {
		    final Polynomial/*<R>*/ gi = (Polynomial)g.get(i);
		    final Polynomial/*<R>*/ gj = (Polynomial)g.get(j);
		    // construct Sgigj = S(g[i], g[j])
		    final Vector/*>S<*/ lgi = (Vector) leadingMonomial(gi, monomialOrder);
		    final Vector/*>S<*/ lgj = (Vector) leadingMonomial(gj, monomialOrder);
		    // construct X^nu and X^mu coprime such that l(X^nu*g[i])==l(X^mu*g[j]) (also @see #lcm(Euclidean,Euclidean))
		    final Vector/*>S<*/ d = Functionals.map(Operations.max, lgi, lgj);
		    final Vector/*>S<*/ nu = d.subtract(lgi);
		    final Vector/*>S<*/ mu = d.subtract(lgj);
		    assert Setops.all(nu.iterator(), mu.iterator(), new orbital.logic.functor.BinaryPredicate() { public boolean apply(Object nui, Object mui) {return nui.equals(Values.ZERO) || mui.equals(Values.ZERO);} }) : "coprime " + vf.MONOMIAL( nu) + " and " + vf.MONOMIAL(mu);
		    // Xpowernugi = 1/lc(g[i]) * X<sup>nu</sup>*g[i]
		    final Polynomial Xpowernugi = vf.MONOMIAL(gi.get(lgi).inverse(), nu).multiply(gi);
		    // Xpowernugi = 1/lc(g[j]) * X<sup>mu</sup>*g[j]
		    final Polynomial Xpowermugj = vf.MONOMIAL(gj.get(lgj).inverse(), mu).multiply(gj);
		    assert leadingMonomial(Xpowernugi, monomialOrder).equals(leadingMonomial(Xpowermugj, monomialOrder)) : "construction should generate equal leading monomials (" + leadingMonomial(Xpowernugi, monomialOrder) + " of " + Xpowernugi + " and " + leadingMonomial(Xpowermugj, monomialOrder) + " of " + Xpowermugj + ") which vanish by subtraction";
		    final Polynomial Sgigj = Xpowernugi.subtract(Xpowermugj);
		    assert Sgigj.get(d).norm().equals(Values.ZERO) : "construction should generate equal leading monomials which vanish by subtraction";
		    final Polynomial r = reduce(Sgigj, g, monomialOrder);
		    logger.log(Level.FINER, "S({0},{1}) = {2} * ({3})  -  {4} * ({5}) = {6} reduced to {7}", new Object[] {gi, gj, vf.MONOMIAL(gi.get(lgi).inverse(), nu), gi, vf.MONOMIAL(gj.get(lgj).inverse(), mu), gj, Sgigj, r});
		    if (isZeroPolynomial.apply(r))
			logger.log(Level.FINE, "skip reduction {0} of {1} from {2} and {3}", new Object[] {r, Sgigj, gi, gj});
		    else {
			logger.log(Level.FINE, "add reduction {0} of {1} from {2} and {3}", new Object[] {r, Sgigj, gi, gj});
			g.add(r);
			continue ergaenzeGroebnerBasis;
		    }
		}
	    }
	    break ergaenzeGroebnerBasis;
	}
	return new HashSet(g);
    }

    /**
     * Reduce the Groebner basis g.
     */
    private static final List/*_<Polynomial<R,S>>_*/ reduceGroebnerBasis(Collection/*_<Polynomial<R,S>>_*/ g, final Comparator monomialOrder) {
	final List basis = new ArrayList(g);
	logger.log(Level.FINE, "reducing Groebner basis {0}", basis);
	replaceWithReductions:
	while (true) {
	    for (int i = 0; i < basis.size(); i++) {
		final Polynomial gi = (Polynomial) basis.get(i);
		final List others = new LinkedList(basis);
		others.remove(i);
		final Polynomial r = reduce(gi, others, monomialOrder);
		if (!r.equals(gi)) {
		    // g[i] not reduced with respect to the others, so replace by its reduction
		    basis.remove(i);
		    if (isZeroPolynomial.apply(r)) 
			// skip adding 0
			logger.log(Level.FINER, "remove {0} and skip adding reduction {1}", new Object[] {gi, r});
		    else {
			basis.add(r);
			logger.log(Level.FINER, "replace {0} by reduction {1}", new Object[] {gi, r});
		    }
		    //@internal or go on with this for loop until nothing changes anymore
		    continue replaceWithReductions;
		}
	    }
	    break replaceWithReductions;
	}
	return basis;
    }

    // Implementation helpers
    
    /**
     * Get a collection of those (exponents of) monomials that occur in f
     * (i.e. with coefficient &ne;0).
     */
    private static Collection/*_<S>_*/ occurringMonomials(final Polynomial/*<S>*/ f) {
	return Setops.select(null,
			     Setops.asList(f.indices()),
			     new Predicate() {
				 public boolean apply(Object i) {
				     return !f.get((Arithmetic)i).norm().equals(Values.ZERO);
				 }
			     });
    }
    /**
     * Get (the exponent of) the leading monomial l(f) of f.
     */
    private static Arithmetic/*>S<*/ leadingMonomial(Polynomial/*<S>*/ f, Comparator monomialOrder) {
	return (Arithmetic/*>S<*/) Collections.max(occurringMonomials(f), monomialOrder);
    }

    /**
     * tolerant equality to 0 (or roughly 0) for polynomials.
     */
    private static final Predicate isZeroPolynomial = new Predicate() {
	    public boolean apply(Object p) {
		Polynomial r = (Polynomial) p;
		final Values vf = Values.getDefaultInstance();
		return r.degreeValue() < 0
		    || vf.asTensor(r).norm().equals(Values.ZERO, vf.valueOf(MathUtilities.getDefaultTolerance()));
	    }
	};

    /**
     * Whether the ideal spanned by the given Groebner basis contains all elements of f.
     */
    private static final boolean containsAll(Set/*_<Polynomial<R,S>>_*/ groebnerBasis, Collection/*_<Polynomial<R,S>>_*/ f, Comparator monomialOrder) {
	// reduces its arguments with respect to groebnerBasis
	final Function reductor = Functionals.bindFirst(Functionals.apply, reduce(groebnerBasis, monomialOrder));
	//final Arithmetic zero = ((Polynomial)groebnerBasis.iterator().next()).zero();
	// whether argument is in the ideal spanned by groebnerBasis
	//final Predicate isZero = Functionals.bindSecond(Predicates.equal, zero);
	final Predicate isZero = isZeroPolynomial;
	final Predicate inIdeal = Functionals.compose(isZero, reductor);
	if (logger.isLoggable(Level.FINEST))
	    logger.log(Level.FINEST, "\t{0}\nisin\t{1}\nall\t{2}", new Object[] {
		Functionals.map(reductor ,new LinkedList(f)),
		Functionals.map(Functionals.asFunction(inIdeal),new LinkedList(f)),
		new Boolean(Setops.all(f, inIdeal))
		});
	return Setops.all(f, inIdeal);
    }
    
    /**
     * Whether the two Groebner bases span the same ideal.
     */
    private static final boolean equalSpan(Set/*_<Polynomial<R,S>>_*/ groebnerBasis1, Set/*_<Polynomial<R,S>>_*/ groebnerBasis2, Comparator monomialOrder) {
	return containsAll(groebnerBasis1, groebnerBasis2, monomialOrder)
	    && containsAll(groebnerBasis2, groebnerBasis1, monomialOrder);
    }
}// AlgebraicAlgorithms
