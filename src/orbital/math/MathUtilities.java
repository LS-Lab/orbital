/**
 * @(#)MathUtilities.java 0.9 1998/08/21 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Functor;
import java.math.BigInteger;

import orbital.math.functional.MathFunctor;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

import orbital.algorithm.Combinatorical;
import java.util.Iterator;
import orbital.util.Utility;

import orbital.io.IOUtilities;
import orbital.util.SuspiciousError;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class contains basic mathematical utilities.
 * <p><a id="SystemProperties">Properties</a>:
 * <ul>
 *   <li>Math precision settings:
 *     <ul>
 *       <li><tt>orbital.math.MathUtilities.defaultPrecisionDigits</tt> - the default number of precision digits (for display etc.).</li>
 *       <li><tt>orbital.math.MathUtilities.defaultTolerance</tt> - the default tolerance for two numbers to be treated equal. (experimental)</li>
 *     </ul>
 *   </li>
 * </ul>
 * </p>
 * 
 * @stereotype &laquo;Utilities&raquo;
 * @version 0.9, 1999/03/15
 * @author  Andr&eacute; Platzer
 * @see java.lang.Math
 * @see orbital.util.Utility
 * @see Evaluations
 * @see Stat
 * @see NumericalAlgorithms
 */
public final class MathUtilities {
    private static class Debug {
	public static void main(String arg[]) throws Exception {
	    System.out.println("gcds/lcms:");
	    for (int i = 0; i < 20; i++) {
		int a = 1 + (int) (Math.random() * 100);
		int b = 1 + (int) (Math.random() * 100);
		System.out.println(a + " " + b + ": gcd:" + gcd(a, b) + ", lcm:" + lcm(a, b));
	    } 
	    System.out.println(gcd(-8, -4));
	    System.out.println(gcd(-8, 4));
	    System.out.println(gcd(8, -4));
	    System.out.println("romans:");
	    for (short d = 1; d < 30; d++)
		System.out.println(d + "=^=" + toRoman(d));
	    double v;
	    v = 1112.345678999999;
	    System.out.println(v + ":" + format(v));
	    System.out.println(v + ":" + format(v, 6));
	    System.out.println(v + ":" + format(v, -2));
	    System.out.println(v + ":" + format(123.000, 6));
	    System.out.println("primes:");
	    for (BigInteger p = BigInteger.valueOf(1); p.compareTo(BigInteger.valueOf(140)) < 0; p = p.add(BigInteger.valueOf(1)))
		System.out.print((isPrime(p) ? 'ø' : ' ') + "" + p + "\t");
	    System.out.println();
	    System.out.println(format(new byte[] {12,4,77,1,-1,0,-0,-5,8,127,-127,-128,80,-80,2,-2,10,-10}));
	} 
    }	 // Debug


    private static final Logger logger = Logger.getLogger(MathUtilities.class.getName());

    /**
     * prevent instantiation - final static class
     */
    private MathUtilities() {}

    /**
     * The default number of precision digits (for display etc.).
     * @see #setDefaultPrecisionDigits(int)
     */
    private static int	 DefaultPrecisionDigits = 8;
    /**
     * The default tolerance for two numbers to be treated equal.
     * (experimental)
     * @see #setDefaultTolerance(double)
     * @invariant DefaultTolerance &ge; 0
     */
    private static double DefaultTolerance = .00000000001;

    /**
     * set default tolerance and default precision digits by property
     */
    static {
	String property = MathUtilities.class.getName() + ".defaultPrecisionDigits";
	try {
	    String desc = System.getProperty(property, DefaultPrecisionDigits + "");
	    try {
		DefaultPrecisionDigits = java.lang.Integer.parseInt(desc);
	    } catch (NumberFormatException nonumber) {
		logger.log(Level.SEVERE, "invalid property setting {0}={1}" , new Object[] {property, desc});
	    } 
	    property = MathUtilities.class.getName() + ".defaultTolerance";
	    desc = System.getProperty(property, DefaultTolerance + "");
	    try {
		DefaultTolerance = java.lang.Double.parseDouble(desc);
	    } catch (NumberFormatException nonumber) {
		logger.log(Level.SEVERE, "invalid property setting {0}={1}" , new Object[] {property, desc});
	    } 
	}
	catch (SecurityException nevertheless) {
	    // especially catch SecurityExceptions if we were not allowed to read properties
	}
	catch (Exception nevertheless) {
	    logger.log(Level.WARNING, "use default property setting for {0} due to {1}" , new Object[] {property, nevertheless});
	} 
	logger.log(Level.CONFIG, "property setting {0}={1}", new Object[] {property, new Double(DefaultTolerance)});
    } 

    /**
     * Set the default number of precision digits (for display etc.).
     */
    public static void setDefaultPrecisionDigits(int defaultPrecisionDigits) {
	MathUtilities.DefaultPrecisionDigits = defaultPrecisionDigits;
    }
    /**
     * Get the default number of precision digits (for display etc.).
     */
    public static int getDefaultPrecisionDigits() {
	return MathUtilities.DefaultPrecisionDigits;
    }
    /**
     * Set the default tolerance for two numbers to be treated equal.
     * (experimental)
     * @pre defaultTolerance &ge; 0
     */
    public static void setDefaultTolerance(double defaultTolerance) {
	MathUtilities.DefaultTolerance = defaultTolerance;
    }
    /**
     * Get the default tolerance for two numbers to be treated equal.
     * (experimental)
     * @post RES &ge; 0
     */
    public static double getDefaultTolerance() {
	return MathUtilities.DefaultTolerance;
    }

    /**
     * Whether a value is in a specified range.
     * @return value &isin; [lower, higher].
     */
    public static final boolean isin(int value, int lower, int higher) {
	return lower <= value && value <= higher;
    } 
    /**
     * Whether a value is in a specified range.
     * @return value &isin; [lower, higher].
     */
    public static final boolean isin(double value, double lower, double higher) {
	return lower <= value && value <= higher;
    } 

    /**
     * This function is true only when the value is an even number.
     * @return (value&1)==0.
     */
    public static final boolean even(int value) {
	return (value & 1) == 0;
    } 

    /**
     * This function is true only when the value is an odd number.
     * @return (value&1)!=0.
     */
    public static final boolean odd(int value) {
	return (value & 1) != 0;
    } 

    /**
     * The signum of a number.
     * @return 1 if value &gt; 0, -1 if value &lt; 0 and 0 if value = 0.
     */
    public static final int sign(double value) {
	return value > 0 ? 1 : value < 0 ? -1 : value == 0 ? 0 : undefined(value + " does not have a sign");
    } 
    public static final int sign(int value) {
	return value > 0 ? 1 : value < 0 ? -1 : value == 0 ? 0 : undefined(value + " does not have a sign");
    } 
    private static final int undefined(String msg) {
	throw new ArithmeticException(msg);
    }

    /**
     * Check whether the given value is a probability value.
     * Probabilities range from 0 to 1.
     * @return whether value is in range [0,1].
     */
    public static boolean isProbability(double value) {
	return 0 <= value && value <= 1;
    } 

    /**
     * Check whether the given value is an integer, only.
     * Integers have no fractional part.
     */
    public static boolean isInteger(double value) {
	return fract(value) == 0.0;
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
     * <p>
     * This implementation uses the extended euclidian algorithm, 
     * ELBA: Euclid-Lagrange-Berlekamp Algorithm.
     * </p>
     * @pre &not;(a==0 &and; b==0)
     * @return gcd(a,b) := inf(a,b) with divides as a partial order (N,|).
     * @note the father of all algorithms: Euclid.
     * @note the mother of all data structures: ADT Euclidean ring
     * @note There are even principal rings which are not Euclidean but where one can define the equivalent of the Euclidean algorithm.
     *  The algorithm for rational numbers was given in Book VII of Euclid's Elements, and the algorithm for reals appeared in Book X,
     *  and is the earliest example of an integer relation algorithm (Ferguson et al. 1999, also see Ferguson-Forcade algorithm in Ferguson et al. 1999). 
     * @see "Ferguson, H. R. P.; Bailey, D. H.; and Arno, S. "Analysis of PSLQ, An Integer Relation Finding Algorithm." Math. Comput. 68, 351-369, 1999."
     * @internal note see extended euclidian algorithm ELBA (Euclid-Lagrange-Berlekamp Algorithm) for a decomposition into d=gcd(a,b),r,s in R such that d = r*a + s*b
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
     * @param elements an array {a<sub>0</sub>,...,a<sub>n-1</sub>} &sube; R whose gcd to determine.
     * @pre &not;&forall;i elements[i]==0
     * @return an array {s<sub>0</sub>,...,s<sub>n-1</sub>, d} &sube; R where
     *  d = gcd({a<sub>0</sub>,...,a<sub>n-1</sub>}) = &sum;<sub>i=0,...,n-1</sub> s<sub>i</sub>*a<sub>i</sub>.
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
     * @note the father of all algorithms: Euclid.
     * @note the mother of all data structures: Euclidean rings
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
     * @return lcm(a,b) := sup(a,b) with divides as a partial order (N,|).
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
     * <p>
     *    Remark: the isomorphisms involved are useful for computing with the Chinese remainder algorithm. They are
     *    <ul>
     *      <li>x &#8614; <big>(</big>x (mod m<sub>1</sub>),...,x (mod m<sub>n</sub>)<big>)</big></li>
     *      <li>&sum;<sub>i=1,...,n</sub> x<sub>i</sub><big>(</big>(&prod;<sub>j&ne;i</sub> m<sub>j</sub>)<sup>-1</sup> (mod m<sub>i</sub>)<big>)</big>&prod;<sub>j&ne;i</sub> m<sub>j</sub> &#8612; (x<sub>1</sub>,...,x<sub>n</sub>)</li>
     *    </ul>
     * </p>
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

    // primes
    
    /**
     * Generate a probable prime number. The BigInteger returned is prime with
     * a certain probability depending on the value certainty.
     * 
     * @param strength - bitlength for keys.
     * @param certainty - the probability for being prime exceeds 1 - 1/2<sup>certainty</sup>.
     * @param randSource - the Random-Source necessary to generate primes, should be an instance of SecureRandom.
     * @param strongPrime - true to produce cryptographically strong primes where (p-1)/2 is prime again.
     */
    public static BigInteger generatePrime(int strength, int certainty, Random randSource, boolean strongPrime) {
	BigInteger p;
	p = new BigInteger(strength, certainty, randSource);
	if (strongPrime) {
	    // ensure that (p-1)/2 is not prime, as well
	    while (!p.subtract(BigInteger.valueOf(1)).shiftRight(1).isProbablePrime(certainty))
		p = new BigInteger(strength, certainty, randSource);
	    assert isPrime(p) : "strong condition: prime";

	    //@todo check whether p-1 has a big prime factor
	    //@todo check whether p+1 has a big prime factor
	} 
	return p;
    } 

    /**
     * Really check whether a BigInteger is prime.
     * A number is prime, if its absolute is not evenly divisible by any number
     * other than 1 (and itself of course).
     * Runtime of this brute force implementation is long.
     * <p>
     * If you have all factors d that divide p evenly and are d&le;&radic;(p), then
     * you can get the complements p/d.
     * To test whether p is prime, you check all d that divide p evenly
     * with d&le;&radic;(p)&le;t&le;p. If you cannot calculate squarer roots (as with BigNumbers)
     * you must find a clever t. Since with p having bitlength n, then
     * p&le;2<sup>n+1</sup>, therefore &radic;(p)&le;2<sup>(n+1)/2</sup>=:t.
     * So all d's that you need to check for divisibility are those with
     * bitlength&le;(n+1)/2.
     * <p>
     * The d's starting at 3 will be increased by 4, 2, 2, 2, and again
     * 4, 2, 2, 2 because those numbers divisible by 2 and 5 are already
     * checked for earlier.
     * @see BigInteger#isProbablePrime(int)
     */
    public static boolean isPrime(BigInteger val) {
	BigInteger p = val.abs();
	if (p.equals(ONE))
	    return false;
	else if (p.equals(TWO) || p.equals(FIVE))
	    return true;
	else if (!p.testBit(0))	  // /2 ?
	    return false;
	else if (p.remainder(FIVE).equals(ZERO))	   // /5 ?
	    return false;

	/* BigInteger sqrt = p.pow((double)1/2); */

	// test up to sqrt
	int s = 0;
	for (BigInteger i = THREE; i.bitLength() <= (p.bitLength() + 1) / 2 /* i.compareTo(sqrt)<=0 */; ) {
	    if (p.remainder(i).equals(ZERO))	// /i ?
		return false;

	    i = i.add(s == 0 ? FOUR : TWO);		// ###1 ###3 ###7 ###9
	    if (++s >= 4)
		s = 0;
	} 

	return true;
    } 

    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FOUR = BigInteger.valueOf(4);
    private static final BigInteger FIVE = BigInteger.valueOf(5);


    // combinatorical functions

    /**
     * n! factorial.
     * @return the factorial n<b>!</b> = <span class="barbedwireBracket">{|</span>1, (1+x)*y<span class="barbedwireBracket">|}</span> n = &Gamma;(n+1).
     * @internal rewrite pure functional?
     */
    public static long factorial(int n) {
	long r = 1;
	while (n > 0)
	    r *= n--;
	return r;
    } 

    /**
     * binomial coefficient <big>(</big><span class="doubleIndex"><sup>n</sup><sub>r</sub></span><big>)</big> = nCr number of combinations without repetition.
     * @return <big>(</big><span class="doubleIndex"><sup>n</sup><sub>r</sub></span><big>)</big> := n! / (r! * (n-r)!).
     * @see orbital.algorithm.Combinatorical#getCombinations(int, int, boolean) getCombinations(int, int, false)
     */
    public static int nCr(int n, int r) {
	return (int) (factorial(n) / (factorial(r) * factorial(n - r)));
    } 

    /**
     * nPr number of permutations without repetition.
     * @return n<sup style="text-decoration: underline">r</sup> := n! / (n-r)!
     * @see orbital.algorithm.Combinatorical#getPermutations(int, int, boolean) getPermutations(int, int, false)
     */
    public static int nPr(int n, int r) {
	return (int) (factorial(n) / factorial(n - r));
    } 

    /**
     * Multinomial which is a generalized binomial.
     * @see #nCr(int, int)
     */
    public static int multinomial(int[] n) {
	int  sum = 0;
	long prod = 1;
	for (int i = 0; i < n.length; i++) {
	    sum += n[i];
	    prod *= factorial(n[i]);
	} 
	return (int) (factorial(sum) / prod);
    } 


    // rounding functions

    /**
     * Returns the mathematically rounded part of a
     * <code>double</code> value.
     */
    public static int round(double a, int rounding_style) {
	throw new UnsupportedOperationException("not yet implemented");
    } 

    /**
     * Returns the fractional part of a
     * <code>double</code> value that is in range of [0,1[ and
     * is equal for all fractional digits.
     * @return <code>a - &lfloor;a&rfloor;</code>.
     * @see java.lang.Math#floor(double)
     */
    public static double fract(double a) {
	return a - Math.floor(a);
    } 

    /**
     * Returns the Gaussian integer part of a
     * <code>double</code> value. Its the largest integer
     * that is smaller than or equal.
     * @return <code>&lceil;a&rceil; := sup {z in <b>Z</b> | z<=a} == (int) Math.floor(a)</code>.
     * @see java.lang.Math#floor(double)
     */
    public static int gaussian(double a) {
	return (int) Math.floor(a);
    } 

    /**
     * Returns the smallest (closest to negative infinity)
     * <code>double</code> value that is not less than the argument and
     * is equal for at least a precision value.
     * @return &lceil;a/precision&rceil;*precision = <code>Math.ceil(a/precision)*precision</code>.
     * @see #floory(double, double)
     */
    public static double ceily(double a, double precision) {
	return Math.ceil(a / precision) * precision;
    } 
    public static double ceily(double a) {
	return ceily(a, precisionFor(a));
    } 

    /**
     * Returns the largest (closest to positive infinity)
     * <code>double</code> value that is not greater than the argument and
     * is equal for at least a precision value.
     * <code>MathUtilities.floory(a, .01)</code> will result in <code>a</code> rounded down
     * to a number formated as <code>.##</code>,
     * while <code>MathUtilities.floory(a, 100)</code> will result in <code>a</code> rounded down
     * to a number formated as <code><i>several #s preceding</i>######<b>00</b>.</code>.
     * @return &lfloor;a/precision&rfloor;*precision = <code>Math.floor(a/precision)*precision</code>.
     * @see #ceily(double, double)
     */
    public static double floory(double a, double precision) {
	return Math.floor(a / precision) * precision;
    } 
    public static double floory(double a) {
	return floory(a, precisionFor(a));
    } 

    /**
     * Returns the closest <code>double</code> to the argument that is equal for at least a precision value.
     * 
     * @param   a   a <code>double</code> value.
     * @param   precision   a <code>double</code> value setting the precision.
     * The smaller the precision, the more precise, the number returned,
     * the larger the less precise and rounded earlier.
     * Precisions above 1 will even round the non-fractional part.
     * @return (<span class="keyword">double</span>) <span class="Class">Math</span>.round(a <span class="operator">/</span> precision) <span class="operator">*</span> precision.
     */
    public static double roundy(double a, double precision) {
	return (double) Math.round(a / precision) * precision;
    } 
    public static double roundy(double a) {
	return roundy(a, precisionFor(a));
    } 

    /**
     * Get the precision for a given a specified tolerance relative to the magnitude of a.
     * <p>
     * Roughly gives the precision for tolerance percent of a, but adjusted to decimal digits.
     * </p>
     * @return tolerance * 10<sup>&lceil;&#13266;<sub>10</sub> a&rceil;</sup>.
     */
    public static double precisionFor(double a, double tolerance) {
	return tolerance * Math.pow(10, Math.ceil(Math.log(a) / Math.log(10)));
    } 
    /**
     * Get the precision for a default tolerance relative to the magnitude of a.
     */
    public static double precisionFor(double a) {
	return precisionFor(a, DefaultTolerance);
    } 


    // tolerant equality

    /**
     * Checks whether two numbers are roughly equal.
     * @param tolerance specifies how much a and b may differ to be treated as equal.
     * @return a &asymp; b. More precisely <code>|a-b| &lt; tolerance</code>.
     * @see Complex#equals(Object,Real)
     */
    public static boolean equals(double a, double b, double tolerance) {
	return Math.abs(a - b) < tolerance;
    } 
    public static boolean equals(Arithmetic a, Arithmetic b, double tolerance) {
	return Metric.INDUCED.distance(a, b).doubleValue() < tolerance;
    } 

    /**
     * Checks whether two numbers are roughly equal.
     * @see #equals(double,double,double)
     * @see #DefaultTolerance
     * @return a &asymp; b.
     */
    static boolean equalsCa(double a, double b) {
	return equals(a, b, DefaultTolerance);
    } 
    static boolean equalsCa(Arithmetic a, Arithmetic b) {
	return equals(a, b, DefaultTolerance);
    }


    // arithmetic widening equalizer
	
    /**
     * Get the transformation function for minimum widening equalized Arithmetic objects.
     * This transformation is a logical function that transforms an array of arithmetic objects
     * into an array of minimum widening equalized arithmetic objects whose values are equal to the original ones.
     * <dl class="def">
     *   <dt>arithmetic objects are minimum widening equalized</dt>
     *   <dd>if either <ul>
     *   <li>they have the same type, and this type is the minimum type (the most restrictive one).
     *   So whenever possible an integer will be preferred over a rational,
     *   a rational over a real and that over a complex.
     *   That is they are instances of the common superclass.
     *   </li>
     *   <li>or they have minimum compatible types, such as a matrix and a vector.</li>
     *   </ul>
     * </dd>
     * </dl>
     * <p>
     * This transformation function is often used to implement sly arithmetic operations with
     * full dynamic dispatch by {@link orbital.math.functional.Operations}.
     * </p>
     * @return a logical transformation function that takes an array of objects (usually Arithmetic objects)
     * and returns an array of the same length (usually 2).
     * The elements returned have the same value as the elements in the argument array.
     * And all will have the same minimum (that is most restrictive) type.
     * This means that an integer will be returned instead of a real whenever possible,
     * a real instead of a complex and so on.
     * But it will always be true that both elements returned have exactly the same
     * or a very compatible type.
     * @pre 0<=args.length && args.length<=2 (currently)
     * @post RES.length==args.length
     *   && (RES[0].getClass() "compatible to" RES[1].getClass() || RES[0].getClass() == RES[1].getClass())
     * @see Scalar#minimumEqualized(Number, Number)
     * @see orbital.math.functional.Operations
     * @see #setEqualizer(orbital.logic.functor.Function)
     */
    public static final orbital.logic.functor.Function/*<Object[],Object[]>*/ getEqualizer() {
	return equalizer;
    } 

    /**
     * Set the transformation function for minimum widening equalized Arithmetic objects.
     * <p>
     * The transformation function set here must fulfill the same criteria the default one
     * does as described in the getEqualizer() method. To simply hook an additional
     * transformation, implement your transformation function on top of the one got from
     * getEqualizer().</p>
     * @see #getEqualizer()
     */
    public static final void setEqualizer(orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer) throws SecurityException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPermission(new RuntimePermission("setStatic.equalizer"));
	} 
	MathUtilities.equalizer = equalizer;
    } 

    private static orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer = new orbital.logic.functor.Function/*<Object[],Object[]>*/() {
	    public Object/*>Object[]<*/ apply(Object/*>Object[]<*/ o) {
		if (o instanceof Arithmetic[]) {
		    Arithmetic operands[] = (Arithmetic[]) o;
		    if (operands.length <= 1)
			return operands;
		    return minimumEqualized(operands);
		} 
		return o;
	    } 
	};
    private static final Arithmetic[] minimumEqualized(Arithmetic[] a) {
	assert a.length == 2 : "currently for binary operations, only";
	//@todo!
	if (a[0] == null || a[1] == null)
	    throw new NullPointerException("null is no true arithmetic object");
	if (/*(a[0] == null || a[1] == null)
	      ||*/ a[0].getClass() == a[1].getClass())
	    return a;
	else if (a[0] instanceof Number && a[1] instanceof Number)
	    return Values.minimumEqualized((Number) a[0], (Number) a[1]);
	else if ((a[0] instanceof Matrix || a[1] instanceof Matrix)
		 || (a[0] instanceof Vector || a[1] instanceof Vector))
	    return a;
	else if ((a[0] instanceof MathFunctor || a[0] instanceof Symbol) || (a[1] instanceof MathFunctor || a[1] instanceof Symbol))
	    if (a[0] instanceof MathFunctor || a[0] instanceof Symbol)
		return a;
	    else
		return new Arithmetic[] {
		    makeSymbolAware(a[0]), a[1]
		};	//XXX: how exactly?
	throw new AssertionError("the types of the arguments could not be equalized: " + (a == null ? "null" : a[0].getClass() + "") + " and " + (a[1] == null ? "null" : a[1].getClass() + ""));
    } 

    /**
     * @todo beautify and check whether it is necessary to convert numbers to those symbolic arithmetic function trucs!
     * @todo xxx see Functionals.genericCompose(*Function, ...) calls to constant(...)
     */
    private static final Arithmetic makeSymbolAware(Arithmetic x) {
	assert !(x instanceof MathFunctor || x instanceof Symbol) : "math functors and symbols are already aware of symbols";
	//TODO: think about
	return orbital.math.functional.Functions.constant(x);
    } 
    
    // diverse
    
    /**
     * Returns &int;<span class="doubleIndex"><sub>a</sub><sup>b</sup></span> f <i>d</i>x.
     * @see NumericalAlgorithms#integrate(orbital.math.functional.Function, Arithmetic, Arithmetic)
     */
    public static Arithmetic integrate(orbital.math.functional.Function f, Arithmetic a, Arithmetic b) {
    	orbital.math.functional.Function F = f.integrate();
    	return ((Arithmetic) F.apply(b)).subtract((Arithmetic) F.apply(a));
    }
    
    /**
     * Generate the taylor series expansion of f about x<sub>0</sub> to order n.
     * @todo introduce
     */
    //public static Function taylorSeries(Function f, Arithmetic x0, int n)
	
    /**
     * Of a rank r tensor with rank s tensor components, make a rank r+s tensor.
     * This applies recursively until (non-tensor) components of rank 0 have been reached.
     * Requires that all components are of uniform rank and dimensions.
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
	    Tensor r = Values.getInstance(dim);
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

    // nice number formatting

    /**
     * Formats a numeric double-value as a String representation with
     * a specified number of precision digits.
     */
    public static String format(double v, int precisionDigits) {
	StringBuffer sb = new StringBuffer();
	for (int i = precisionDigits; i < 0; i++)
	    sb.append('0');
	sb.append('.');
	for (int i = 0; i < precisionDigits; i++)
	    sb.append('#');
	DecimalFormat form = new DecimalFormat(sb.toString(), new DecimalFormatSymbols(Locale.UK));
	StringBuffer  s = new StringBuffer(form.format(v));
	while (s.charAt(s.length() - 1) == '0')
	    s.deleteCharAt(s.length() - 1);
	if (s.charAt(s.length() - 1) == '.')
	    s.deleteCharAt(s.length() - 1);
	if (s.length() == 0 || "-".equals(s.toString()))
	    return "0";
	return s.toString();
    } 
    private static String format(Number v, int precisionDigits) {
	if (v == null)
	    return "null";
	return format(v.doubleValue(), precisionDigits);
    } 

    /**
     * formats a numeric double-value.
     * @see #DefaultTolerance
     */
    public static String format(double v) {
	return format(v, DefaultPrecisionDigits);
    } 

    public static String format(byte[] v) {
	if (v.length == 0)
	    return null;
	StringBuffer sb = new StringBuffer();
	NumberFormat nf = NumberFormat.getInstance();
	nf.setMinimumIntegerDigits(3);
	for (int i = 0; i < v.length; i++)
	    sb.append((i > 0 ? ":" : "") + nf.format(/*@internal see IOUtilities.byteToUnsigned*/(v[i])&0xFF));
	return sb.toString();
    } 

    /**
     * Formats an object as a String representation.
     * <p>
     * Does special handling for numbers, arithmetic objects, functors, for arrays, for two-dimensional arrays, etc.</p>
     * @see ArithmeticFormat
     */
    public static String format(Object o) {
	if (o == null)
	    return "" + null;
	else if (o instanceof Arithmetic)
	    return ArithmeticFormat.getDefaultInstance().format(o);
	else if (o instanceof Number)
	    if ((o instanceof java.lang.Integer) || (o instanceof java.lang.Byte) || (o instanceof java.lang.Short) || (o instanceof java.lang.Long))
		return ((Number) o).longValue() + "";
	    else
		return format(((Number) o).doubleValue());
	else if (o.getClass().isArray() && o.getClass().getComponentType().isArray()) {
	    //@todo simplify to Values.tensor(o).toString();
	    // 2-dimensional array type
	    // (or more dimensional array type)
	    String		 nl = System.getProperty("line.separator");
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < Array.getLength(o); i++) {
		sb.append((i == 0 ? "" : nl) + '[');
		Object e = Array.get(o, i);
		for (int j = 0; j < Array.getLength(e); j++)
		    sb.append((j == 0 ? "" : ",\t") + format(Array.get(e, j)));
		sb.append(']');
	    } 
	    return sb.toString();
	} else if (o.getClass().isArray()) {
	    // 1-dimensional array type
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < Array.getLength(o); i++) {
		sb.append(format(Array.get(o, i)));
		if (i < Array.getLength(o) - 1)
		    sb.append(",\t");
	    } 
	    return sb.toString();
	} else
	    return "" + o;
    } 


    /**
     * Converts arabic numbers to roman numbers.
     * @param arabic  the short value (in arabic chiffres) to be converted into roman chiffres.
     * It must be an integer value between 1 (I) and 3999 (MMMCMXCIX).
     * @flag not-tested
     * @todo test
     * @todo &#8576;=1000, &#8577;=5000, &#8578;=10000.
     */
    public static String toRoman(short arabic) {
	if (arabic <= 0 || arabic >= 4000)
	    throw new IllegalArgumentException("number out of bounds");
	StringBuffer sp = new StringBuffer();
	int			 r = romans.length - 1;
	for (short dec = 10000; dec > 1; dec /= 10) {
	    logger.log(Level.FINEST, "" + dec, "" + fract((double) arabic / dec) + "->" + 10 * fract(roundy((double) arabic / dec, .01)));
	    short ch = (short) (10 * fract(roundy((double) arabic / dec, .01)));
	    switch (ch) {
	    case 0:
		break;
	    case 1:
	    case 2:
	    case 3:
		for (int j = 0; j < ch; j++)
		    sp.append(romans[r]);
		break;
	    case 4:
		sp.append(romans[r]);
		/* fallthrough */
	    case 5:
		sp.append(romans[r + 1]);
		break;
	    case 6:
	    case 7:
	    case 8:
		sp.append(romans[r + 1]);
		for (int j = 0; j < ch - 5; j++)
		    sp.append(romans[r]);
		break;
	    case 9:
		sp.append(romans[r]);
		sp.append(romans[r + 2]);
		break;
	    default:
		throw new SuspiciousError("chiffre (" + ch + ") is neither 0 nor 1-9");
	    }
	    r -= 2;
	} 
	return sp.toString();
    } 
    private static char romans[] = {
	'I', 'V', 'X', 'L', 'C', 'D', 'M'
    };

    /**
     * Excerpt a double array from a vector, if possible.
     */
    static final double[] toDoubleArray(Vector v) {
	return ((AbstractVector)v).toDoubleArray();
    }
    /**
     * Excerpt a double array from a matrix, if possible.
     */
    static final double[][] toDoubleArray(Matrix m) {
	return ((AbstractMatrix)m).toDoubleArray();
    }
}
