/**
 * @(#)AlgebraicAlgorithms java 1.1 2002-08-21 Andre Platzer
 * 
 * Copyright (c) 2002-2007 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Function;
import java.util.Comparator;
import java.io.Serializable;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Collections;
import java.util.List;

import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ArrayList;

import orbital.math.functional.Functionals;
import orbital.logic.functor.Functions;
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
 * Algebraic algorithms and computer algebra.
 *
 * @stereotype Utilities
 * @stereotype Module
 * @author Andr&eacute; Platzer
 * @version $Id$
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
     * @note Reduced Gr&ouml;bner bases w.r.t. lexicographic orders have triangular shape.
     * @see #LEXICOGRAPHIC(int[])
     */
    public static final Comparator/*<Vector<Integer>>*/ LEXICOGRAPHIC = new Comparator/*<Vector<Integer>>*/() {
            public int compare(Object/*>Vector<Integer><*/ m1, Object/*>Vector<Integer><*/ m2) {
                final Vector/*<Integer>*/ nu = getExponentVector(m1);
                final Vector/*<Integer>*/ mu = getExponentVector(m2);
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
     * @see #LEXICOGRAPHIC(int[])
     */
    public static final Comparator/*<Vector<Integer>>*/ REVERSE_LEXICOGRAPHIC = new Comparator/*<Vector<Integer>>*/() {
            public int compare(Object/*>Vector<Integer><*/ m1, Object/*>Vector<Integer><*/ m2) {
                final Vector/*<Integer>*/ nu = getExponentVector(m1);
                final Vector/*<Integer>*/ mu = getExponentVector(m2);
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
     * (Generalised) lexicographical order on monoid of monomials.
     * This is an admissible total order.
     * The monomials are expected to be encoded as their exponents in the form of
     * <code>int[]</code>s.
     * This 
     * <div>
     *   X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &le; X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>
     *   :&hArr; X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>=X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup> <br />
     *   &or; i<sub>&pi;(k)</sub>&lt;j<sub>&pi;(k)</sub> for k := min{k &brvbar; i<sub>&pi;(k)</sub>&ne;j<sub>&pi;(k)</sub>}
     * </div>
     * Elimination orders favouring to eliminate quantified variables over
     * non-quantifieds can be specified by a permutation.
     * @todo provide elimination order favouring to eliminate quantified vars over non-quantifieds see Bockmayr&Weispfenning in Handbook of Automated Reasoning.
     * @param permutation the permutation &pi;&isin;S<sub>n</sub> specifying the order of relevance of the variables as
     *  X<sub>&pi;(0)</sub> &gt; X<sub>&pi;(1)</sub> &gt; &#8230; &gt; X<sub>&pi;(n-1)</sub>
     * @preconditions &pi; is a permutation
     * @note Reduced Gr&ouml;bner bases w.r.t. lexicographic orders have triangular shape modulo permutation.
     * @see #LEXICOGRAPHIC
     * @see #REVERSE_LEXICOGRAPHIC
     */
    public static final Comparator/*<Vector<Integer>>*/ LEXICOGRAPHIC(final int permutation[]) {
        checkPermutation(permutation);
        return new Comparator/*<Vector<Integer>>*/() {
            public int compare(Object/*>Vector<Integer><*/ m1, Object/*>Vector<Integer><*/ m2) {
                final Vector/*<Integer>*/ nu = getExponentVector(m1);
                final Vector/*<Integer>*/ mu = getExponentVector(m2);
                if (nu.dimension() != mu.dimension())
                    throw new IllegalArgumentException("incompatible monomial exponents from polynomial rings with a different number of variables");
                for (int k = 0; k < nu.dimension(); k++) {
                    int index = permutation[k];
                    int c = ((Integer)nu.get(index)).subtract((Integer)mu.get(index)).intValue();
                    if (c != 0)
                        return c;
                }
                return 0;
            }

            public String toString() {
                return AlgebraicAlgorithms.class.getName() + ".LEXICOGRAPHIC[" + MathUtilities.format(permutation) +"]";
            }
        };
    }

    /**
     * Checks whether the given int[] is a permutation.
     */
    private static final void checkPermutation(int permutation[]) throws IllegalArgumentException {
        // bucket sort
        boolean found[] = new boolean[permutation.length];
        for (int i = 0; i < found.length; i++) {
            found[i] = false;
        }
        for (int i = 0; i < permutation.length; i++) {
            int v = permutation[i];
            if (v < 0 || v >= found.length)
                throw new IllegalArgumentException(MathUtilities.format(permutation) + " is no permutation due to illegal entry " + v);
            if (found[i])
                throw new IllegalArgumentException(MathUtilities.format(permutation) + " is no permutation due to duplicate entry " + v);
            found[i] = true;
        }
    }

    /**
     * Generalised degree-lexicographical order on monoid of monomials.
     * Thus compares for degree in favor of the specified order.
     * In case of (reverse) lexicographic order as basis, this is an admissible total order.
     * The monomials are expected to be encoded as their exponents in the form of
     * <code>int[]</code>s.
     * <div>
     *   X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &le; X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>
     *   :&hArr; deg(X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>)&lt;deg(X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>) <br />
     *   &or; <big>(</big>deg(X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup>)=deg(X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup>) &and; X<sub>0</sub><sup>i<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>i<sub>n-1</sub></sup> &lt;<sub><var>base</var></sub> X<sub>0</sub><sup>j<sub>0</sub></sup>&sdot;&#8230;&sdot;X<sub>n-1</sub><sup>j<sub>n-1</sub></sup><big>)</big>
     * </div>
     * @param monomialBaseOrder the order <var>base</var> to use when degree order is equal.
     */
    public static final Comparator/*<Vector<Integer>>*/ DEGREE(final Comparator/*<Vector<Integer>>*/ monomialBaseOrder) {
        return new Comparator/*<Vector<Integer>>*/() {
            public int compare(Object/*>Vector<Integer><*/ m1, Object/*>Vector<Integer><*/ m2) {
                final Vector/*<Integer>*/ nu = getExponentVector(m1);
                final Vector/*<Integer>*/ mu = getExponentVector(m2);
                if (nu.dimension() != mu.dimension())
                    throw new IllegalArgumentException("incompatible monomial exponents from polynomial rings with a different number of variables");
                int c = ((Integer)Operations.sum.apply(nu)).intValue()
                    - ((Integer)Operations.sum.apply(mu)).intValue();
                if (c != 0)
                    return c;
                else
                    return monomialBaseOrder.compare(m1, m2);
            }

            public String toString() {
                return AlgebraicAlgorithms.class.getName() + ".DEGREE(" + monomialBaseOrder + ")";
            }
        };
    }
    
    /**
     * Degree lexicographical order on monoid of monomials.
     * @see #DEGREE(Comparator)
     * @see #LEXICOGRAPHIC
     */
    public static final Comparator/*<Vector<Integer>>*/ DEGREE_LEXICOGRAPHIC = DEGREE(LEXICOGRAPHIC);

    /**
     * Degree reverse-lexicographical order on monoid of monomials.
     * @see #DEGREE(Comparator)
     * @see #REVERSE_LEXICOGRAPHIC
     */
    public static final Comparator/*<Vector<Integer>>*/ DEGREE_REVERSE_LEXICOGRAPHIC = DEGREE(REVERSE_LEXICOGRAPHIC);

    
    /**
     * The partial lexicographial order on polynomials induced by an admissible total order on monomials.
     * p&gt;q iff the largest monomial which occurs in p or q but not both is in p.
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
     * @see #DEGREE(Comparator)
     * @see #LEXICOGRAPHIC(int[])
     */
    public static final /*<R extends Arithmetic, S extends Arithmetic>*/
        Comparator/*<Polynomial<R,S>>*/ INDUCED(final Comparator/*<S>*/ monomialOrder) {
        return new InducedPolynomialComparator(monomialOrder);
    }
    private static final class InducedPolynomialComparator/*<R extends Arithmetic, S extends Arithmetic>*/
        implements Comparator/*<Polynomial<R,S>>*/ {
        private final Comparator/*<S>*/ monomialOrder;
        // pretty-printer adding X^.. notation to exponents
        private static final Function/*<S,Polynomial<R,S>>*/ Xpower =
            new Function/*<S,Polynomial<R,S>>*/() {
                public Object/*>Polynomial<R,S><*/ apply(Object/*>S<*/ i) {
                    return (Polynomial/*<R,S>*/) ((Arithmetic)i).valueFactory().MONOMIAL((Vector/*>S<*/)i);
                }
                public String toString() {
                    return "X0^.*...Xn^.";
                }
            };
        public InducedPolynomialComparator(Comparator/*<S>*/ monomialOrder) {
            this.monomialOrder = monomialOrder;
        }

        public boolean equals(Object o) {
            return (o instanceof InducedPolynomialComparator)
                && monomialOrder.equals(((InducedPolynomialComparator)o).monomialOrder);
        }
        public int hashCode() {
            return monomialOrder.hashCode();
        }
        public int compare(Object/*>Polynomial<R,S><*/ p1, Object/*>Polynomial<R,S><*/ p2) {
                // the reverse ordered monomials occurring in p1
            final SortedSet/*<S>*/ amon = new TreeSet(new ReverseComparator(monomialOrder));
            amon.addAll(occurringMonomials((Polynomial/*<R,S>*/)p1));
                // the reverse ordered monomials occurring in p2
            final SortedSet/*<S>*/ bmon = new TreeSet(new ReverseComparator(monomialOrder));
            bmon.addAll(occurringMonomials((Polynomial/*<R,S>*/)p2));
            try {
                // like Setops.find(Functionals.map("asFunction"(monomialOrder), amon.iterator(), bmon.iterator()), Functionals.bindSecond(Predicates.equal, Values.ZERO));
                // but with lazy evaluation of Functionals.map such that it stops at the first even if the iterators have inequal lengths
                for (Iterator/*<S>*/ i = amon.iterator(), j = bmon.iterator(); i.hasNext() || j.hasNext(); ) {
                    if (!i.hasNext() || !j.hasNext()) {
                        // exactly one of the polynomials has no more occurring monomials
                        // the larger polynomial is the one that still has monomials
                        if (!i.hasNext()) {
                                assert j.hasNext();
                                return -1;
                        } else if (!j.hasNext()) {
                                assert i.hasNext();
                                return 1;
                        } else {
                                throw new AssertionError("cannot happen due to tertium non datur");
                        }
                    }
                    //              //@todo verify: in case of different number of occurring monomials, the one that has more is greater
                    //              if (!i.hasNext())
                    //                  return -1;
                    //              else if (!j.hasNext())
                    //                  return 1;
                    int cmp = monomialOrder.compare(i.next(), j.next());
                    if (cmp != 0)
                        return cmp;
                }
                return 0;
            } catch (IndexOutOfBoundsException differentLengthOfMonomials) {
                throw (IllegalArgumentException)
                    new IllegalArgumentException("incomparable arguments " + p1 + " and " + p2
                                                 + "\nwith (sorted) monomials occurrences " + Functionals.map(Xpower, new LinkedList(amon)) + " and " + Functionals.map(Xpower, new LinkedList(bmon))).initCause(differentLengthOfMonomials);
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
            Tensor r = t.valueFactory().newInstance(dim);
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
    public static final BinaryFunction/*<Euclidean,Euclidean,Euclidean>*/ gcd = new BinaryFunction/*<Euclidean,Euclidean,Euclidean>*/() {
            public Object/*>Euclidean<*/ apply(Object/*>Euclidean<*/ a, Object/*>Euclidean<*/ b) {
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
     * @see "Ferguson, H. R. P.; Bailey, D. H.; and Arno, S. Analysis of PSLQ, An Integer Relation Finding Algorithm. Math. Comput. 68, 351-369, 1999."
     * @todo optimize
     * @has time complexity gcd&isin;O(log(max{||a||, ||b||}))
     * @todo we could multiply the resulting gcd, r, s by a constant!=0 (which is a unit if R is a field) to obtain a normalized gcd.
     */
    public static Euclidean gcd(Euclidean a, Euclidean b) {
        Euclidean list[] = {a, b};
        return gcd(list)[list.length];          //sic(!)
    }
    /**
     * Returns least common multiple (lcm) of two elements of an (Euclidean) ring.
     * @see #lcm(Euclidean,Euclidean)
     */
    public static final BinaryFunction/*<Euclidean,Euclidean,Euclidean>*/ lcm = new BinaryFunction/*<Euclidean,Euclidean,Euclidean>*/() {
            public Object/*>Euclidean<*/ apply(Object/*>Euclidean<*/ a, Object/*>Euclidean<*/ b) {
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
            throw new UnsupportedOperationException("gcd of more than two elements not yet implemented " + MathUtilities.format(elements));
        }
        final Euclidean a = elements[0], b = elements[1];
        final Euclidean ZERO = (Euclidean) a.zero();
        final Euclidean ONE = (Euclidean) a.one();
        if (a.isZero() && b.isZero())
            throw new ArithmeticException("gcd(0, 0) is undefined");
        if (b.isZero())
            return new Euclidean[] {ONE, a};                            //@todo verify that this is correct, especially for a=0 and in conjunction with rationals occuring in ranks < size
        Euclidean a0 = a, a1 = b;
        Euclidean r0 = ONE, r1 = ZERO;
        Euclidean s0 = ZERO, s1 = ONE;
        while (!a1.isZero()) {
            // @invariants gcd(OLD(a), OLD(b)) == gcd(a0, a1)
            //   &and; a0 == r0*a + s0*b
            //   &and; a1 == r1*a + s1*b
                        
            // calculate the quotient a0 div a1
            Euclidean q = a0.quotient(a1);
            Euclidean t;
                        
            // transform (a0, a1)
            t = (Euclidean) a0.subtract(q.multiply(a1));
            // t == a0.modulo(a1)
            //@todo reenable test once it is working again:
            assert a0.modulo(a1).equals(t) : "a mod b == a - (a div b)*b, i.e. " + a0 + " mod " + a1 + " == " + a0.modulo(a1) + " == " + a0 + " - " + q + "*" + a1 + " == " + a0 + " - " + q.multiply(a1) + " == " + t;
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
        assert !a0.isZero() : "gcd != 0 && @todo";
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
            return a;                           //@todo verify that this is correct, especially for a=0 and in conjunction with rationals occuring in ranks < size
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
    public static final Quotient/*<? extends Arithmetic>*/ chineseRemainder(Arithmetic x[], Arithmetic m[]) {
        if (x.length != m.length)
            throw new IllegalArgumentException("must give the same number of congruence values and modulos");
        final ValueFactory vf = x[0].valueFactory();
        Euclidean xStar = (Euclidean) x[0];
        Euclidean M = (Euclidean) xStar.one();
        for (int i = 1; i < m.length; i++) {
            M = (Euclidean) M.multiply(m[i-1]);
            final Arithmetic c = vf.quotient(M, (Euclidean) m[i]).inverse()/*.representative()*/;               // the inverse modulo m[i] of m
            final Arithmetic s = vf.quotient((Euclidean) (x[i].subtract(xStar)).multiply(c), (Euclidean) m[i]).representative();
            xStar = (Euclidean) xStar.add(s.multiply(M));
        }
        M = (Euclidean) M.multiply(m[m.length-1]);
        assert M.equals(Operations.product.apply(vf.valueOf(m))) : "total modulus " + M + " = " + Operations.product.apply(vf.valueOf(m));
        return vf.quotient(xStar, M);
    }

    //
    // Gr&ouml;bner basis
    //

    /**
     * Reduce f with respect to g.
     * @param g the collection of multinomials for reducing f. 
     * @param monomialOrder the <a href="#monomialOrder">order of monomials</a>, which is decisive for the time complexity.
     * @return h if h is a reduced reduction of f with respect to g.
     * @see #reduce(Collection, Comparator)
     */
    public static final /*<R extends Arithmetic, S extends Arithmetic>*/ Polynomial/*<R,S>*/ reduce(Polynomial/*<R,S>*/ f, Collection/*<Polynomial<R,S>>*/ g, final Comparator/*<S>*/ monomialOrder) {
        return (Polynomial) reduce(g, monomialOrder).apply(f);
    }
    /**
     * Reduce<sub>g</sub>:K[X<sub>0</sub>,...,X<sub>n-1</sub>]&rarr;K[X<sub>0</sub>,...,X<sub>n-1</sub>]; f &#8614; "f reduced with respect to g".
     * Performs a division by multiple polynomials.
     * <p>
     * Iteratedly performs the Buchberger-reduction
     * <center>f &rarr;<sub>g</sub> h := f - &lambda;<sub>&nu;</sub>/l<sub>c</sub>(g) * X<sup>&nu;</sup>/l(g) * g</center>
     * where l(g) is the leading monomial in g with leading coefficient l<sub>c</sub>(g), and
     * f has the multivariate form &sum;<sub>&nu;</sub> &lambda;<sub>&nu;</sub>*X<sup>&nu;</sup>.
     * For such a reduction, X<sup>&nu;</sup> no longer occurs in h and h<f or h=0.
     * </p>
     * @param g the collection of multinomials for reducing polynomials.
     * @param monomialOrder the <a href="#monomialOrder">order of monomials</a>, which is decisive for the time complexity.
     * @return a function that reduces polynomials with respect to g.
     * @see Values#quotient(Polynomial,Set,Comparator)
     * @internal elementaryReduction = "wenn ein Monom verschwindet (mit Hilfe des Leitmonomos eines der g) und der Rest kleiner wird"
     * @internal reduction = transitiveClosure(elementaryReduction)
     */
    public static final /*<R extends Arithmetic,S extends Arithmetic>*/ Function/*<Polynomial<R,S>,Polynomial<R,S>>*/ reduce(Collection/*<Polynomial<R,S>>*/ g, Comparator/*<S>*/ monomialOrder) {
        return new ReductionFunctionFast/*<R,S>*/(g, monomialOrder);
    }
    private static final class ReductionFunction/*<R extends Arithmetic,S extends Arithmetic>*/
        implements Function/*<Polynomial<R,S>,Polynomial<R,S>>*/, Serializable {
        private static final long serialVersionUID = -51945340881045435L;
        private final Collection/*<Polynomial<R,S>>*/ g;
        private final Comparator/*<S>*/ monomialOrder;
        private final Comparator/*<S>*/ inducedOrder;
    	/**
    	 * Caches whether any g polynomial is numerical
    	 */
    	private final boolean symbolicg;
        private final Function/*<Polynomial<R,S>,Polynomial<R,S>>*/ elementaryReduce;
        public ReductionFunction(final Collection/*<Polynomial<R,S>>*/ g, Comparator/*<S>*/ newmonomialOrder) {
            if (!Setops.all(g, Functionals.bindSecond(Utility.instanceOf, Polynomial.class))) {
                throw new IllegalArgumentException("prerequisite failed: " + "collection<" + Polynomial.class.getName() + "> expected, found violation " + Setops.find(g, Functionals.not(Functionals.bindSecond(Utility.instanceOf, Polynomial.class))) + " in "+ g);
            }
    		//@internal using Collections.unmodifiableCollection would spoil equals
            this.g = Collections.unmodifiableSet(new LinkedHashSet(g));
    		this.symbolicg = !Setops.some(g, Arithmetic.numerical);
            this.monomialOrder = newmonomialOrder;
            this.inducedOrder = INDUCED(monomialOrder);
            if (g.isEmpty()) {
                this.elementaryReduce = Functions.id;
                return;
            }
            // enrich g with exponents of leading monomials for caching
            // cache (polynomial, leading monomial)
            final Pair/*<Polynomial<R,S>,S>*/[] basis = new Pair[g.size()];
            {
                Iterator/*<Polynomial<R,S>>*/ it = g.iterator();
                for (int i = 0; i < basis.length; i++) {
                    final Polynomial/*<R,S>*/ gi = (Polynomial) it.next();
                    final Arithmetic leadingMonomial = leadingMonomial(gi, monomialOrder);
                    basis[i] = new Pair(gi, leadingMonomial);
                }
            }
            //@internal would we profit from using orbital.logic.trs, even though polynomials do not currently have an introspectable internal structure per Functor.Composite?
            this.elementaryReduce = new Function/*<Polynomial<R,S>,Polynomial<R,S>>*/() {
                    public Object/*>Polynomial<R,S><*/ apply(Object/*>Polynomial<R,S><*/ o) {
                        final Polynomial/*<R,S>*/ f = (Polynomial)o;
                        final ValueFactory vf = f.valueFactory();
                        //@internal we would prefer reverse direction (because we want to start eliminating large not small monomials), also starting with leading coefficient
                        final SortedSet/*<S>*/ occurring = new TreeSet(new ReverseComparator(monomialOrder));
                        occurring.addAll(occurringMonomials(f));
                        for (Iterator/*<S>*/ index = occurring.iterator(); index.hasNext(); ) {
                            final Arithmetic/*>S<*/ nu = (Arithmetic/*>S<*/) index.next();
                            final Arithmetic/*>R<*/ cnu = f.get(nu);
                            assert !cnu.isZero() : "@postconditions of occurringMonomials(...)";
                            reductionPolynomials:
                            for (int j = 0; j < basis.length; j++) {
                                final Polynomial/*<R,S>*/ gj = (Polynomial/*>Polynomial<R,S><*/)basis[j].A;
                                final Arithmetic/*>S<*/ lgj = (Arithmetic/*>S<*/)basis[j].B;

                                // test divisibility
                                Arithmetic/*>R<*/ cdiv;
                                final Arithmetic/*>S<*/ xdiv;
                                try {
                                    // test divisibility of monomial X^nu by l(gj)
                                    xdiv = divideMonomial(nu, lgj);
                                    if (xdiv == null)
                                        continue reductionPolynomials;
                                    // test divisibility of coefficient cnu by lc(gj)=gj.get(lgj)
                                    cdiv = (Arithmetic/*>R<*/)cnu.divide(gj.get(lgj));
                                    if (cdiv instanceof Scalar) {
                                        // simplify domain and cancel rationals leading to less complex coefficients.
                                        cdiv = vf.narrow((Scalar)cdiv);
                                    }
                                }
                                catch (ArithmeticException indivisible) {
                                    continue reductionPolynomials;
                                }
                                // divisible, then q := cdiv*X<sup>xdiv</sup>
                                final Polynomial/*<R,S>*/ q = vf.MONOMIAL(cdiv, xdiv);
                                Polynomial/*<R,S>*/ reduction = f.subtract(q.multiply(gj));
                                if (symbolicg) {
                                	assert reduction.get(nu).isZero() : vf.MONOMIAL(cnu.one(), nu) + " does not occur in " + reduction + " anymore";
                                } else if (!reduction.get(nu).isZero()) {
                                        Arithmetic rnu = reduction.get(nu);
                                    if (MathUtilities.equals(rnu.norm(),
                                                             rnu.zero(),
                                                             MathUtilities.getDefaultTolerance()
                                                             )) {
                                        //@internal trick correct numerical instabilites
                                        reduction = reduction.subtract(vf.MONOMIAL(reduction.get(nu), nu));
                                    }
                                    if (!reduction.get(nu).isZero()) {
                                        throw new AssertionError(vf.MONOMIAL(cnu.one(), nu) + " does not occur in " + reduction + " anymore, even after numerical precision correction");
                                    }
                                }
                                assert inducedOrder.compare(reduction, f) < 0 : reduction + "<" + f;
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
        public Object/*>Polynomial<R,S><*/ apply(Object/*>Polynomial<R,S><*/ f) {
            logger.log(Level.FINEST, "reducing {0} with respect to {1} ...", new Object[] {f, g});
            return Functionals.fixedPoint(elementaryReduce, f);
        }
        
        public String toString() {
                return "ReductionFunction[" + g + "; " + monomialOrder + "]";
        }
    }

    private static final class ReductionFunctionFast/*<R extends Arithmetic,S extends Arithmetic>*/
    implements Function/*<Polynomial<R,S>,Polynomial<R,S>>*/, Serializable {
    	//private static final long serialVersionUID;
    	private final Collection/*<Polynomial<R,S>>*/ g;
    	private final Comparator/*<S>*/ monomialOrder;
    	private final Comparator/*<S>*/ inducedOrder;
    	/**
    	 * Leading monomial cache
    	 */
    	private final Pair/*<Polynomial<R,S>,S>*/[] basis;
    	/**
    	 * Caches whether any g polynomial is numerical
    	 */
    	private final boolean symbolicg;
    	public ReductionFunctionFast(final Collection/*<Polynomial<R,S>>*/ g, Comparator/*<S>*/ newmonomialOrder) {
    		if (!Setops.all(g, Functionals.bindSecond(Utility.instanceOf, Polynomial.class))) {
    			throw new IllegalArgumentException("prerequisite failed: " + "collection<" + Polynomial.class.getName() + "> expected, found violation " + Setops.find(g, Functionals.not(Functionals.bindSecond(Utility.instanceOf, Polynomial.class))) + " in "+ g);
    		}
    		//@internal using Collections.unmodifiableCollection would spoil equals
    		this.g = Collections.unmodifiableSet(new LinkedHashSet(g));
    		this.symbolicg = !Setops.some(g, Arithmetic.numerical);
    		this.monomialOrder = newmonomialOrder;
    		this.inducedOrder = INDUCED(monomialOrder);
    		// enrich g with exponents of leading monomials for caching
    		// cache (polynomial, leading monomial)
    		this.basis = new Pair[g.size()];
    		{
    			Iterator/*<Polynomial<R,S>>*/ it = g.iterator();
    			for (int i = 0; i < basis.length; i++) {
    				final Polynomial/*<R,S>*/ gi = (Polynomial) it.next();
    				final Arithmetic leadingMonomial = leadingMonomial(gi, monomialOrder);
    				basis[i] = new Pair(gi, leadingMonomial);
    			}
    		}
    	}
    	public boolean equals(Object o) {
    		return (o instanceof ReductionFunctionFast)
    		&& Utility.equals(g, ((ReductionFunctionFast) o).g)
    		&& Utility.equals(monomialOrder, ((ReductionFunctionFast) o).monomialOrder);
    	}
    	public int hashCode() {
    		return Utility.hashCode(g) ^ Utility.hashCode(monomialOrder);
    	}
    	public Object/*>Polynomial<R,S><*/ apply(final Object/*>Polynomial<R,S><*/ o) {
    		logger.log(Level.FINEST, "reducing {0} with respect to {1} ...", new Object[] {o, g});
    		if (g.isEmpty()) {
    			return o;
    		}
    		Polynomial f = (Polynomial)o;
			final ValueFactory vf = f.valueFactory();
			//@internal we would prefer reverse direction (because we want to start eliminating large not small monomials), also starting with leading coefficient
			final Queue/*<S>*/ monomials = new PriorityQueue(20, new ReverseComparator(monomialOrder));
			monomials.addAll(occurringMonomials(f));
			// caching all monomial exponents occurred so far that cannot be reduced with respect to g.
			final Set/*<S>*/ noreduction = new LinkedHashSet();
			leadingReductions:
			while (!monomials.isEmpty()) {
				final Arithmetic/*>S<*/ nu = (Arithmetic/*>S<*/) monomials.poll();
				final Arithmetic/*>R<*/ cnu = f.get(nu);
				assert !cnu.isZero() : "@postconditions of occurringMonomials(...)";
				reductionPolynomials:
					for (int j = 0; j < basis.length; j++) {
						final Polynomial/*<R,S>*/ gj = (Polynomial/*>Polynomial<R,S><*/)basis[j].A;
						final Arithmetic/*>S<*/ lgj = (Arithmetic/*>S<*/)basis[j].B;

						// test divisibility
						Arithmetic/*>R<*/ cdiv;
						final Arithmetic/*>S<*/ xdiv;
						try {
							// test divisibility of monomial X^nu by l(gj)
							xdiv = divideMonomial(nu, lgj);
							if (xdiv == null)
								continue reductionPolynomials;
							// test divisibility of coefficient cnu by lc(gj)=gj.get(lgj)
							cdiv = (Arithmetic/*>R<*/)cnu.divide(gj.get(lgj));
							if (cdiv instanceof Scalar) {
								// simplify domain and cancel rationals leading to less complex coefficients.
								cdiv = vf.narrow((Scalar)cdiv);
							}
						}
						catch (ArithmeticException indivisible) {
							continue reductionPolynomials;
						}
						// divisible, then q := cdiv*X<sup>xdiv</sup>
						final Polynomial/*<R,S>*/ q = vf.MONOMIAL(cdiv, xdiv);
						Polynomial/*<R,S>*/ reduction = f.subtract(q.multiply(gj));
						if (symbolicg) {
							assert reduction.get(nu).isZero() : vf.MONOMIAL(cnu.one(), nu) + " does not occur in " + reduction + " anymore";
						} else if (!reduction.get(nu).isZero()) {
							Arithmetic rnu = reduction.get(nu);
							if (MathUtilities.equals(rnu.norm(),
									rnu.zero(),
									MathUtilities.getDefaultTolerance()
							)) {
								//@internal trick correct numerical instabilites
								reduction = reduction.subtract(vf.MONOMIAL(reduction.get(nu), nu));
							}
							if (!reduction.get(nu).isZero()) {
								throw new AssertionError(vf.MONOMIAL(cnu.one(), nu) + " does not occur in " + reduction + " anymore, even after numerical precision correction");
							}
						}
						assert inducedOrder.compare(reduction, f) < 0 : reduction + "<" + f;
						logger.log(Level.FINEST, "elementary reduction {0} - {1} * ({2}) == {3}", new Object[] {f, q, gj, reduction});
						f =  reduction;
						monomials.clear();
						Collection newmons = occurringMonomials(f);
						newmons.removeAll(noreduction);
						monomials.addAll(newmons);
						continue leadingReductions;
					}
				// g could not reduce this exponent at all, cache this information and do not try again (assuming fields where division is always possible)
				noreduction.add(nu);
			}
            assert f.equals(new ReductionFunction(g, monomialOrder).apply(o)) : "optimized result " + f + " equals canonical result " + new ReductionFunction(g, monomialOrder).apply(o);
    		return f;
    	}

    	public String toString() {
    		return "ReductionFunctionFast[" + g + "; " + monomialOrder + "]";
    	}
    }
    
    /**
     * Get the reduced Gr&ouml;bner basis of g.
     * <p>
     * <dl class="def">
     *   <dt>Gr&ouml;bner basis</dt>
     *   <dd>
     *     A finite generating system G is a Gr&ouml;bner basis of I&#8884;K[X<sub>1</sub>,&#8230;,X<sub>n</sub>]
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
     *       <li>&forall;f&ne;g&isin;G 0 is a reduction of the
     *         S(f,g) := 1&#8725;l<sub>c</sub>(f)*X<sup class="vector">&nu;</sup>&sdot;f - 1&#8725;l<sub>c</sub>(g)*X<sup class="vector">&mu;</sup>&sdot;g <br />
     *         where X<sup class="vector">&nu;</sup>,X<sup class="vector">&mu;</sup> are coprime and such that
     *         l(X<sup class="vector">&nu;</sup>&sdot;f)=l(X<sup class="vector">&mu;</sup>&sdot;g)
     *       </li>
     *     </ol>
     *   </dd>
     * </dl>
     * <dl class="def">
     * A Gr&ouml;bner basis G of I &#8884; K[X<sub>1</sub>,&#8230;,X<sub>n</sub>] is
     *   <dt>minimal</dt>
     *   <dd>&forall;g&ne;h&isin;G l(g) &#8740; l(h)</dd>
     *   <dt>reduced</dt>
     *   <dd>&forall;g&isin;G g reduced with respect to G\{g}
     *     <div>&rArr; G minimal</div>
     *     <div>G unique (up to multiplication by constants)</div>
     *   </dd>
     * </dl>
     * </p>
     * <p>
     * Two minimal Gr&ouml;bner bases have the same number of elements and the same leading monomials.
     * There is a unique reduced Gr&ouml;bner basis. Gr&ouml;bner basis were developed
     * as a canonical simplifier as a means for computation in factor polynomial rings. 
     * </p>
     * <h3>Applications</h3>
     * Let V(I)={x&isin;R<sup>n</sup> : &forall;f&isin;I f(x)=0}
     * be the <dfn>vanishing ideal</dfn> of ideal I
     * and let G be the reduced Gr&ouml;bner basis of the ideal I
     * with respect to some monomial ordering.
     * <ul>
     *   <li>With respect to the <emph>lexicographic term ordering</emph> <var>X<sub>n</sub>&gt;...&gt;X<sub>1</sub></var>,
     *     G has the <emph>elimination property</emph>, i.e. 
     *     <center>(G)&cap;K[X<sub>1</sub>,&#8230;,X<sub>i</sub>] = (G&cap;K[X<sub>1</sub>,&#8230;,X<sub>n</sub>])</center>
     *     This relationship of "triangularisation" (in case V(I) is 0-dimensional) is important for successively solving systems of polynomial equations by backsubstitution.
     *   </li>
     *   <li>Implicitisation, i.e., converting parametric equations to implicit form.
     *     For the parametrized surface x<sub>1</sub>=f<sub>1</sub>(t<sub>1</sub>,...,t<sub>m</sub>),...,x<sub>n</sub>=f<sub>n</sub>(t<sub>1</sub>,...,t<sub>m</sub>),
     *     a Gr&ouml;bner basis (with respect to a monomial order having parameters greater than variables)
     *     of the
     *     <center>(x<sub>1</sub>-f<sub>1</sub>(t<sub>1</sub>,...,t<sub>m</sub>),...,x<sub>n</sub>-f<sub>n</sub>(t<sub>1</sub>,...,t<sub>m</sub>))</center>
     *     yields a basis of which the subset of polynomials without the parameters t<sub>i</sub>
     *     describes the smallest affine variety containing the original surface.
     *   </li>
     *   <li>Deciding equality of ideals by equality of their
     *     unique reduced Gr&ouml;bner bases with respect to the same monomial ordering.</li>
     *   <li>Intersecting ideals I=(G) and J=(H)
     *     as the restriction to polynomials without fresh variable t
     *     of the Gr&ouml;bner basis of
     *     <center>{t*G,(1-t)*H}</center>
     *     with respect to a t&gt;x<sub>1</sub>,...,x<sub>n</sub> monomial order.
     *   </li>
     *   <li>V(I)=&empty; iff G={1}, i.e., there are no solutions (when K is algebraically closed)</li>
     *   <li>V(I) is 0-dimensional iff for each X<sub>i</sub>, G contains an f with a pure leading monomial l(f)=X<sub>i</sub><sup>k</sup>, which means "finitely many solutions" (when K is algebraically closed)</li> 
     * </ul>
     * @param g the collection of multinomials that is a generating system of the ideal (g)
     *  for which to construct a Gr&ouml;bner basis.
     * @param monomialOrder the <a href="#monomialOrder">order of monomials</a>, which is decisive for the time complexity.
     * @note The Buchberger algorithm used to construct a Gr&ouml;bner basis is equivalent
     *  to {@link #gcd(Euclidean[])} in case of one variable,
     *  and to {@link LUDecomposition} in case of linear polynomials.
     * @internal whenever an elementary reduction is possible, use the reduced polynomial instead of the original polynomial.
     * @internal Gr&ouml;bnerBasis = "if the term rewrite system for reduce is confluent"
     * @internal generalisations to non-field rings (or non-commutative) are possible, see the much more expensive Ritt-reduction.
     * @internal parallel implementation of Gr&ouml;bner bases possible but communication is required at each S-polynomial discovery. Idea: late check with newly discovered S-polynomials after some time (by versioning of discoveries)?
     * @todo scale to get rid of denominators, and of non-primitive polynomials (divide by gcd of coefficients)
     * @see "Buchberger, Bruno. <i>Ein Algorithmus zum Auffinden der Basiselemente des Restklassenrings nach einem nulldimensionalen Polynomideal</i>. PhD thesis, Universit&auml;t Innsbruck, 1965."
     * @see "Buchberger, Bruno. Gr&ouml;bner bases: An algorithmic method in polynomial ideal theory. In Bose, N.K., editor, <i>Recent Trends in Multidimensional Systems Theory</i>. Reidel Publ.Co., 1985."
     * @see "Knuth, Donald E. and Bendix, P.B. Simple word problems in universal algebras. In Leech, J., editor, <i>Computational Problems in Abstract Algebras</i>. p263-297. Pergamon Press, Oxford, 1970."
     */
    public static final /*<R extends Arithmetic, S extends Arithmetic>*/
        Set/*<Polynomial<R,S>>*/ groebnerBasis(Set/*<Polynomial<R,S>>*/ g, final Comparator/*<S>*/ monomialOrder) {
    	if (g.isEmpty()) {
    		return Collections.EMPTY_SET;
    	} else {
    		if (g.contains(((Polynomial)g.iterator().next()).zero())) {
    			g = new LinkedHashSet(g);
    			g.remove(((Polynomial)g.iterator().next()).zero());
    			if (g.isEmpty()) {
    				return Collections.EMPTY_SET;
    			}
    		}
        }
        logger.log(Level.FINE, "Computing Groebner Basis of {0} with respect to {1}", new Object[] {g, monomialOrder});
        Set/*<Polynomial<R,S>>*/ rgb = groebnerBasisImpl(g, monomialOrder);
        logger.log(Level.FINE, "Groebner Basis is {2} of {0} with respect to {1}", new Object[] {g, monomialOrder, rgb});
        Set temp, nrgb = null;
//        assert containsAll(nrgb = groebnerBasisImpl(g, monomialOrder), g, monomialOrder) : "the original generating system " + g + " is in the ideal spanned by its (non-reduced) Groebner basis " + nrgb;
//        assert containsAllSPolynomials(nrgb, monomialOrder) : "the (non-reduced) Groebner Basis " + nrgb + " reduces all its S-polynomials to 0";
//        assert equalSpan(rgb, nrgb, monomialOrder) : "reduced Groebner basis " + rgb + " and (non-reduced) Groebner basis " + nrgb + " of " + g + " have equal span";
        assert containsAll(rgb, g, monomialOrder) : "the original generating system " + g + " is in the ideal spanned by its Groebner basis " + rgb;
        assert containsAllSPolynomials(rgb, monomialOrder) : "the (reduced) Groebner Basis " + rgb + " reduces all its S-polynomials to 0";
        assert (temp = new LinkedHashSet(autoReduce(new LinkedList(rgb), monomialOrder))).equals(rgb) : "the Groebner Basis implementation alreay yields auto-reduced Groebner Bases " + rgb + " with auto-reduction " + temp;
        //assert (temp = reducedGroebnerBasis(rgb, monomialOrder)).equals(rgb) : "reduced Groebner basis " + temp + " of a reduced Groebner basis " + rgb + " equals the former Groebner basis (up to constant factors)";
        //assert (temp = groebnerBasisImpl(rgb, monomialOrder)).equals(rgb) : "(non-reduced) Groebner basis " + temp + " of a (reduced) Groebner basis " + rgb + " equals the former Groebner basis (up to constant factors)";
        return rgb;
    }

    private static final /*<R extends Arithmetic, S extends Arithmetic>*/
    Set/*<Polynomial<R,S>>*/ groebnerBasisImpl(Collection/*<Polynomial<R,S>>*/ gg, final Comparator/*<S>*/ monomialOrder) {
        return groebnerBasisImpl_Opt(gg, monomialOrder);
    }
    /**
     * Get the non-reduced Gr&ouml;bner basis of g (Implementation).
     * Using syzygy S-polynomials:
     * <div>S(f,g) = lcm(l<sub>t</sub>(f),l<sub>t</sub>(g))/l<sub>t</sub>(f) f - lcm(l<sub>t</sub>(f),l<sub>t</sub>(g))/l<sub>t</sub>(g) g</div>
     * which will let the leading term cancel by construction.
     * Here, l<sub>t</sub>(f) := l<sub>c</sub>(f) l(f) is the <dfn>leading term</dfn>. 
     * @internal Beware: we internally use slightly rescaled S-polynomials. 
     */
    private static final /*<R extends Arithmetic, S extends Arithmetic>*/
        Set/*<Polynomial<R,S>>*/ groebnerBasisImpl_Opt(final Collection/*<Polynomial<R,S>>*/ gg, final Comparator/*<S>*/ monomialOrder) {
        // partial Groebner Basis
        // @invariant: all S-polynomials within g have already been considered
        final List/*<Polynomial<R,S>>*/ g = new ArrayList();
        // working list, start with small polynomials to improve efficiency
        final PriorityQueue/*<Polynomial<R,S>>*/ working = new PriorityQueue(gg.size()+1, INDUCED(monomialOrder));
        // auto-reduce original polynomials so that no inter-reductions are possible any more
        for (Iterator i = autoReduce(gg, monomialOrder).iterator(); i.hasNext(); ) {
                Polynomial p = (Polynomial)i.next();
                assert validate(p, gg) : "auto-reduced polynomial " + p + " in ideal of " + gg;
                working.add(p);
        }
        // the first element is in the candidate groebner basis, otherwise nothing happens later, all others are in working list
        g.add(working.poll());
        while (!working.isEmpty()) {
                // get (smallest) polynomial from working, moving it to g
            // pre-reduce gi with respect to current G
            final Polynomial/*<R,S>*/ gi = reduce((Polynomial)working.poll(), g, monomialOrder);
            final ValueFactory vf = gi.valueFactory();
                assert validate(gi, gg) : "reduced polynomial " + gi + " from working set in ideal of " + gg;
            if (gi.isZero()) {
                continue;
            }
            final Arithmetic lgi = leadingMonomial(gi, monomialOrder);
            // forward subsumption to avoid divisible leading monomials in g
            // add gi to g, moving all polynomials with leading monomials that are multiples of gi' leading monomial to the working list again
            for (Iterator j = g.iterator(); j.hasNext(); ) {
                final Polynomial/*<R,S>*/ gj = (Polynomial)j.next();
                final Arithmetic lgj = leadingMonomial(gj, monomialOrder);
                assert !lgi.equals(lgj) : "leading exponents different as " + gi + " has been reduced with " + g;
                if (divideMonomial(lgj, lgi) != null) {
                        Collection/*<Polynomial<R,S>>*/ others = new LinkedList(g);
                        others.remove(gj);
                        others.add(gi);
                        j.remove();
                    // this is a bottleneck, especially if it turns out that r=0
                    final Polynomial r = reduce(gj, others, monomialOrder);
                        assert validate(r, gg) : "intra-reduced polynomial " + r + " from G in ideal of " + gg;
                    if (isZeroPolynomial.apply(r)) {
                        logger.log(Level.FINER, "skip partial auto-reduction {0} of {1} from adding {2}", new Object[] {r, gj, gi});
                    } else {
                        logger.log(Level.FINE, "partial auto-reduction {0} of {1} from adding {2}", new Object[] {r, gj, gi});
                        working.add(r);
                    }
                }
            }
            List/*<Polynomial<R,S>>*/ gnew = new LinkedList(g);
            gnew.add(gi);
            // critical syzygy pair formation
            // form all S-polynomials of gi with g
            for (Iterator/*<Polynomial<R,S>>*/ j = g.iterator(); j.hasNext(); ) {
                    final Polynomial/*<R,S>*/ gj = (Polynomial)j.next();

                    // constructing S-polynomials of g[i] and g[j]
                    // construct Sgigj = S(g[i], g[j])
                    final Polynomial Sgigj = sPolynomial(gi, gj, monomialOrder, true);
                    if (Sgigj == null) {
                        // optimizations know that S(g[i],g[j]) will reduce to 0, hence skip
                        logger.log(Level.FINER, "skip optimization reduction from {2} and {3}", new Object[] {gi, gj});
                                                //assert isZeroPolynomial.apply(reduce(sPolynomial(gi, gj, monomialOrder, false), Setops.union(g,Collections.singleton(gj)), monomialOrder)) : "optimization of S-polynomial construction forecasts correctly, i.e., if it will reduce to 0: S(" + gi + ", " + gj + ") = " + sPolynomial(gi, gj, monomialOrder, false) + " gives " + reduce(sPolynomial(gi, gj, monomialOrder, false), g, monomialOrder) + "\nwith respect to " + g;
                    } else {
                        assert validate(Sgigj, gg) : "Syzygy " + Sgigj + " from G in ideal of " + gg;
                        // this is the major bottleneck, especially if it turns out that r=0
                        final Polynomial r = reduce(Sgigj, gnew, monomialOrder);
                        assert validate(r, gg) : "reduced Syzygy " + r + " from G in ideal of " + gg;
                        logger.log(Level.FINER, "S({0},{1}) = {2} reduced to {3}", new Object[] {gi, gj, Sgigj, r});
                        if (isZeroPolynomial.apply(r)) {
                            logger.log(Level.FINER, "skip reduction {0} of {1} from {2} and {3}", new Object[] {r, Sgigj, gi, gj});
                        } else {
                            logger.log(Level.FINE, "add work size {4} reduction {0} of {1} from {2} and {3}", new Object[] {r, Sgigj, gi, gj, new java.lang.Integer(working.size())});
                            working.add(r);
                        }
                    }
            }
            g.add(gi);
            logger.log(Level.FINE, "add reduction giving size {1} with {0}", new Object[] {gi, new java.lang.Integer(g.size())});
        }
        Set nrgb = null;
        assert (nrgb = new LinkedHashSet(g)) != null;
        Set rgb = new LinkedHashSet(autoReduce(new ArrayList(g), monomialOrder));
        assert containsAll(nrgb, g, monomialOrder) : "the original generating system " + g + " is in the ideal spanned by its (non-reduced) Groebner basis " + nrgb;
        assert containsAllSPolynomials(nrgb, monomialOrder) : "the (non-reduced) Groebner Basis " + nrgb + " reduces all its S-polynomials to 0";
        assert equalSpan(rgb, nrgb, monomialOrder) : "reduced Groebner basis " + rgb + " and (non-reduced) Groebner basis " + nrgb + " of " + g + " have equal span";
        return rgb;
    }
    
    private static final /*<R extends Arithmetic, S extends Arithmetic>*/
    Set/*<Polynomial<R,S>>*/ groebnerBasisImpl_Direct(Collection/*<Polynomial<R,S>>*/ gg, final Comparator/*<S>*/ monomialOrder) {
        final List/*<Polynomial<R,S>>*/ g = new ArrayList(gg);
        ValueFactory vf;
        ergaenzeGroebnerBasis:
                while (true) {
                        for (int i = 0; i < g.size(); i++) {
                                final Polynomial/*<R,S>*/ gi = (Polynomial)g.get(i);
                                vf = gi.valueFactory();
                                for (int j = i + 1; j < g.size(); j++) {
                                        final Polynomial/*<R,S>*/ gj = (Polynomial)g.get(j);

                                        // constructing S-polynomials of g[i] and g[j]
                                        // construct Sgigj = S(g[i], g[j])
                                        final Polynomial Sgigj = sPolynomial(gi, gj, monomialOrder, true);
                                        if (Sgigj == null) {
                                                // optimizations know that S(g[i],g[j]) will reduce to 0, hence skip
                                                logger.log(Level.FINER, "skip optimization reduction from {2} and {3}", new Object[] {gi, gj});
                                                //assert isZeroPolynomial.apply(reduce(sPolynomial(gi, gj, monomialOrder, false), g, monomialOrder)) : "optimization of S-polynomial construction forecasts correctly, i.e., if it will reduce to 0: S(" + gi + ", " + gj + ") = " + sPolynomial(gi, gj, monomialOrder, false) + " gives " + reduce(sPolynomial(gi, gj, monomialOrder, false), g, monomialOrder) + "\nwith respect to " + g;
                                        } else {
                                                // this is the major bottleneck, especially if it turns out that r=0
                                                final Polynomial r = reduce(Sgigj, g, monomialOrder);
                                                logger.log(Level.FINER, "S({0},{1}) = {2} reduced to {3}", new Object[] {gi, gj, Sgigj, r});
                                                if (isZeroPolynomial.apply(r)) {
                                                        logger.log(Level.FINER, "skip reduction {0} of {1} from {2} and {3}", new Object[] {r, Sgigj, gi, gj});
                                                } else {
                                                        logger.log(Level.FINE, "add reduction {0} of {1} from {2} and {3}", new Object[] {r, Sgigj, gi, gj});
                                                        g.add(r);
                                                        continue ergaenzeGroebnerBasis;
                                                }
                                        }
                                }
                        }
                        break ergaenzeGroebnerBasis;
                }
        Set nrgb = null;
        assert (nrgb = new LinkedHashSet(g)) != null;
        Set rgb = new LinkedHashSet(autoReduce(new ArrayList(g), monomialOrder));
        assert containsAll(nrgb, g, monomialOrder) : "the original generating system " + g + " is in the ideal spanned by its (non-reduced) Groebner basis " + nrgb;
        assert containsAllSPolynomials(nrgb, monomialOrder) : "the (non-reduced) Groebner Basis " + nrgb + " reduces all its S-polynomials to 0";
        assert equalSpan(rgb, nrgb, monomialOrder) : "reduced Groebner basis " + rgb + " and (non-reduced) Groebner basis " + nrgb + " of " + g + " have equal span";
        return rgb;
    }

    /**
     * Construct the syzygy S-polynomial S(f,g) of f and g.
     * <div>S(f,g) = lcm(l<sub>t</sub>(f),l<sub>t</sub>(g))/l<sub>t</sub>(f) f - lcm(l<sub>t</sub>(f),l<sub>t</sub>(g))/l<sub>t</sub>(g) g</div>
     * which will let the leading term cancel by construction.
     * Here, l<sub>t</sub>(f) := l<sub>c</sub>(f) l(f) is the <dfn>leading term</dfn>. 
     * @internal Beware: we internally use slightly rescaled S-polynomials.
     * @return S(f,g),
     *  or <code>null</code> if the S-polynomial is known to reduce to 0 (if <code>optimize==true</code>).
     * @param optimize whether to optimize S-polynomial construction and return <code>null</code> objects
     *  instead of S-polynomials.
     */
    private static final /*<R extends Arithmetic, S extends Arithmetic>*/
        Polynomial/*<R,S>*/ sPolynomial(final Polynomial/*<R,S>*/ f, final Polynomial/*<R,S>*/ g, final Comparator/*<S>*/ monomialOrder, boolean optimize) {
        final ValueFactory vf = f.valueFactory();
        // Gr&ouml;bner rank of g[i], i.e., leading monomial l(g[i])
        final Vector/*>S<*/ lf = getExponentVector(leadingMonomial(f, monomialOrder));
        final Vector/*>S<*/ lg = getExponentVector(leadingMonomial(g, monomialOrder));
        // construct X^nu and X^mu coprime such that l(X^nu*g[i])==l(X^mu*g[j]) (also @see #lcm(Euclidean,Euclidean))
        // let d=lcm(l(f),l(g)), or more precisely the exponent of this monomial
        if (optimize && lf.equals(lg)) {
                // identical leading exponents can be reduced
                return null;
        }
        final Vector/*>S<*/ d = Functionals.map(Operations.max, lf, lg);
        if (optimize && d.equals(Functionals.map(Operations.plus, lf, lg))) {
            // Optimization: if l(f) and l(g) are coprime, i.e., lcm(l(f),l(g))=l(f)*l(g), then S(f,g) reduces to 0 so no reduction needed
            return null;
        }
        // let nu=lf/d, or more precisely the exponent of this monomial
        final Vector/*>S<*/ nu = d.subtract(lf);
        // let mu=lg/d, or more precisely the exponent of this monomial
        final Vector/*>S<*/ mu = d.subtract(lg);
        assert Setops.all(nu.iterator(), mu.iterator(), new orbital.logic.functor.BinaryPredicate() { public boolean apply(Object nui, Object mui) {return ((Arithmetic)nui).isZero() || ((Arithmetic)mui).isZero();} }) : "coprime " + vf.MONOMIAL( nu) + " and " + vf.MONOMIAL(mu);
        // Xpowernuf = 1/lc(g[i]) * X<sup>nu</sup>*g[i]
        final Polynomial Xpowernuf = vf.MONOMIAL(f.get(lf).inverse(), nu).multiply(f);
        // Xpowermug = 1/lc(g[j]) * X<sup>mu</sup>*g[j]
        final Polynomial Xpowermug = vf.MONOMIAL(g.get(lg).inverse(), mu).multiply(g);
        assert leadingMonomial(Xpowernuf, monomialOrder).equals(leadingMonomial(Xpowermug, monomialOrder)) : "construction should generate equal leading monomials (" + leadingMonomial(Xpowernuf, monomialOrder) + " of " + Xpowernuf + " and " + leadingMonomial(Xpowermug, monomialOrder) + " of " + Xpowermug + ") which cancel by subtraction";
        final Polynomial syzygy = Xpowernuf.subtract(Xpowermug);
        assert syzygy.get(d).isZero() : "construction should generate equal leading monomials which cancel by subtraction";
        logger.log(Level.FINER, "S({0},{1}) = {2} * ({3})  -  {4} * ({5}) = {6}", new Object[] {f, g, vf.MONOMIAL(f.get(lf).inverse(), nu), f, vf.MONOMIAL(g.get(lg).inverse(), mu), g, syzygy});
        return syzygy;
    }

    /**
     * Auto-Reduce the set of poynomials g, so that each polynomial is reduced with respect to all the others.
     * @todo optimize
     */
    private static final /*<R extends Arithmetic, S extends Arithmetic>*/
        List/*<Polynomial<R,S>>*/ autoReduce(Collection/*<Polynomial<R,S>>*/ g, final Comparator/*<S>*/ monomialOrder) {
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
                    // g[i]  reduced with respect to the others, so replace by its reduction
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
     * @see Polynomial#indices()
     */
    public static /*<R extends Arithmetic, S extends Arithmetic>*/
        Collection/*<S>*/ occurringMonomials(final Polynomial/*<R,S>*/ f) {
        return Setops.select(null,
                             Setops.asList(f.indices()),
                             new Predicate() {
                                 public boolean apply(Object i) {
                                     return !f.get((Arithmetic)i).isZero();
                                 }
                             });
    }
    /**
     * Get (the exponent of) the leading monomial l(f) of f.
     */
    private static /*<R extends Arithmetic, S extends Arithmetic>*/
        Arithmetic/*>S<*/ leadingMonomial(Polynomial/*<R,S>*/ f, Comparator/*<S>*/ monomialOrder) {
        if (f.isZero()) {
                throw new IllegalArgumentException("zero polynomial has no leading monomial");
        }
        return (Arithmetic/*>S<*/) Collections.max(occurringMonomials(f), monomialOrder);
    }

    /**
     * tolerant equality to 0 (or roughly 0) for polynomials with numerical quantities.
     */
    private static final Predicate isZeroPolynomial = new Predicate() {
            public boolean apply(Object p) {
                Polynomial r = (Polynomial) p;
                if (Arithmetic.numerical.apply(r)) {
                    final ValueFactory vf = r.valueFactory();
                    Tensor rt = vf.asTensor(r);
                    return r.degreeValue() < 0
                        || rt.equals(vf.ZERO(rt.dimensions()), vf.valueOf(MathUtilities.getDefaultTolerance()));
                } else {
                        return r.isZero();
                }
            }
        };

    /**
     * Whether the ideal spanned by the given Gr&ouml;bner basis contains all elements of f.
     */
    private static final /*<R extends Arithmetic, S extends Arithmetic>*/
        boolean containsAll(Set/*<Polynomial<R,S>>*/ groebnerBasis, Collection/*<Polynomial<R,S>>*/ f, Comparator/*<S>*/ monomialOrder) {
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
     * Whether the two Gr&ouml;bner bases span the same ideal.
     */
    private static final /*<R extends Arithmetic, S extends Arithmetic>*/
        boolean equalSpan(Set/*<Polynomial<R,S>>*/ groebnerBasis1, Set/*<Polynomial<R,S>>*/ groebnerBasis2, Comparator/*<S>*/ monomialOrder) {
        return containsAll(groebnerBasis1, groebnerBasis2, monomialOrder)
            && containsAll(groebnerBasis2, groebnerBasis1, monomialOrder);
    }

    /**
     * Checks that the groebner basis g reduces all its S-polynomials to 0
     */
    private static final boolean containsAllSPolynomials(Set gb,
                        Comparator monomialOrder) {
        final List/*<Polynomial<R,S>>*/ g = new ArrayList(gb);
        ValueFactory vf;
        for (int i = 0; i < g.size(); i++) {
                final Polynomial/*<R,S>*/ gi = (Polynomial)g.get(i);
                vf = gi.valueFactory();
                for (int j = i + 1; j < g.size(); j++) {
                        final Polynomial/*<R,S>*/ gj = (Polynomial)g.get(j);

                        // constructing S-polynomials of g[i] and g[j]
                        // construct Sgigj = S(g[i], g[j])
                        final Polynomial Sgigj = sPolynomial(gi, gj, monomialOrder, false);
                        if (Sgigj == null) {
                                // optimizations know that S(g[i],g[j]) will reduce to 0, hence skip
                                //logger.log(Level.FINE, "skip optimization reduction from {2} and {3}", new Object[] {gi, gj});
                                //assert isZeroPolynomial.apply(reduce(sPolynomial(gi, gj, monomialOrder, false), g, monomialOrder)) : "optimization of S-polynomial construction forecasts correctly, i.e., if it will reduce to 0: S(" + gi + ", " + gj + ") = " + sPolynomial(gi, gj, monomialOrder, false) + " gives " + reduce(sPolynomial(gi, gj, monomialOrder, false), g, monomialOrder) + "\nwith respect to " + g;
                        } else {
                                // this is the major bottleneck, especially if it turns out that r=0
                                final Polynomial r = reduce(Sgigj, g, monomialOrder);
                                //logger.log(Level.FINER, "S({0},{1}) = {2} reduced to {3}", new Object[] {gi, gj, Sgigj, r});
                                if (isZeroPolynomial.apply(r)) {
                                        //logger.log(Level.FINE, "skip reduction {0} of {1} from {2} and {3}", new Object[] {r, Sgigj, gi, gj});
                                } else {
                                        assert false: "Groebner Basis " + gb + " reduces all its S-polynomials to 0, including S(" + gi + "," + gj + ")=" + Sgigj + " giving " + r;
                                return false;
                                }
                        }
                }
        }
        return true;
        }


    //
    // Differential Equations
    //

    /**
     * Symbolically solves ordinary differential equation system.
     * Solves (in)homogeneous ODE with constant coefficients.
     * @param A the complex matrix of coefficients.
     * @param tau the initial time &tau; of the initial values &eta;.
     * @param eta the complex vector &eta; of initial values.
     * @param b the inhomogeneous vector.
     * @return The solution x of the initial value problem
     *   <center>
     *     x'(t)=A*x(t) + b(t)<br />
     *     x(&tau;)=&eta;
     *   </center>
     *  which is
     *   <center>
     *     x(t)=e<sup>A(t-&tau;)</sup>&eta; + &int;<sub>&tau;</sub><sup>t</sup> e<sup>A(t-s)</sup>b(s) ds
     *   </center>
     *  where
     *   <center>
     *     e<sup>At</sup> = &sum;<sub>n=0,...</sub> 1/n! A<sup>n</sup>t<sup>n</sup>
     *   </center>
     * @see "Walter, W. Ordinary Differential Equations Springer, 1998"
     */
    public static final /*<R extends Complex>*/
        orbital.math.functional.Function/*<Real,Vector<R>>*/ dSolve(Matrix/*<R>*/ A, Vector/*<R>*/ b, Real tau, Vector/*<R>*/ eta) {
        if (!A.isSquare())
            throw new IllegalArgumentException("square coefficient matrix expected " + A);
        if (A.dimension().width != eta.dimension())
            throw new IllegalArgumentException("initial value expected of compatible dimension. Dimension " + A.dimension() + " of " + A + " does not fit dimension " + eta.dimension() + " of " + eta);
        if (A.dimension().height != b.dimension())
            throw new IllegalArgumentException("constant vector expected of compatible dimension. Dimension " + A.dimension() + " of " + A + " does not fit dimension " + b.dimension() + " of " + b);
        final ValueFactory vf = eta.valueFactory();
        // contains the successive powers 1/n!*A^n*eta
        List/*<Vector<R>>*/ epowers = new LinkedList/*<Vector<R>>*/();
        // contains the successive powers 1/n!*A^n*b if needed at all (otherwise null if b=0)
        List/*<Vector<R>>*/ bpowers =
            b.isZero() ? null : new LinkedList/*<Vector<R>>*/();
        // index into powers
        int n = 0;
        // add A^0*eta=eta
        epowers.add(eta);
        // add A^0*b=b
        if (bpowers != null) bpowers.add(b);
        n++;
        // contains the successive powers A^n
        Matrix/*<R>*/ p = A;
        // contains the successive factorials
        int f = 1;
        while (!p.isZero()) {
            if (n > A.dimension().width)
                throw new UnsupportedOperationException("solving differential equations only implemented for nilpotent systems. Yet " + A + "^n = " + p);
            // successive factorial
            f *= n;
            assert f == MathUtilities.factorial(n) : "on-the-fly factorial " + f + " == " + n + "! = " + MathUtilities.factorial(n);
            assert p.equals(A.power(vf.valueOf(n))) : "on-the-fly power " + p + " == " + A + "^" + n + " = " + A.power(vf.valueOf(n));
            // append 1/n!*A^n*eta
            epowers.add(p.multiply(eta).multiply(vf.rational(1,f)));
            //epowers.add(p.multiply(eta).divide(vf.valueOf(f)));
            // append 1/n!*A^n*b
            if (bpowers != null) bpowers.add(p.multiply(b).multiply(vf.rational(1,f)));
            // next round
            p = p.multiply(A);
            n++;
        }

        // the (vectorial-coefficient) univariate polynomial e^(At)eta in t
        final UnivariatePolynomial/*<Vector<R>>*/ eAeta =
            vf.polynomial((Vector[])epowers.toArray(new Vector/*<R>*/[0]));
        if (bpowers != null) {
            //@xxx the following snipet does not yet work completely?
            // polynomial components of e^A*b
            UnivariatePolynomial/*<R>*/[] bpolyc =
                componentPolynomials(vf.polynomial(
                    (Vector[])bpowers.toArray(new Vector/*<R>*/[0])));
            // component-wise integration
            for (int i = 0; i < bpolyc.length; i++) {
                bpolyc[i] = (UnivariatePolynomial)bpolyc[i].integrate();
            }
            UnivariatePolynomial/*<Vector<R>>*/ bpoly = vectorialPolynomial(bpolyc);
            //System.out.println("\thom  : " + eAeta);
            //System.out.println("\tinhom: " + bpoly);
            if (!tau.isZero())
                throw new UnsupportedOperationException("inhomogeneous solutions not yet implemented for tau!=0");
            else
                return eAeta.add(bpoly);
        }
        // shift eAeta by -tau unless that's zero
        return tau.isZero()
            ? eAeta
            : Functionals.compose(eAeta, Functionals.bindSecond(Operations.subtract, tau));
    }

    // converters

    /**
     * Converts a univariate polynomial with R-vectorial coefficients
     * into an array of polynomials obtained by coordinate projections.
     * @parameter p a vectorial polynomial a<sub>0</sub>+a<sub>1</sub>X+...+a<sub>n</sub>X<sup>n</sup>
     *  with vectorial coefficients a<sub>i</sub>
     * @return an array of the respective scalar polynomials
     *  P<sub>i</sub> = a<sub>0,i</sub>+a<sub>1,i</sub>X+...+a<sub>n,i</sub>X<sup>n</sup>
     * @see #vectorialPolynomial(UnivariatePolynomial[])
     */
    public static final /*<R extends Arithmetic>*/
        UnivariatePolynomial/*<R>*/[] componentPolynomials(UnivariatePolynomial/*<Vector<R>>*/ p) {
        final ValueFactory vf = p.valueFactory();
        Vector/*<R>*/ a0 = (Vector) p.get(0);
        // a growing list view for the resulting component polynomials
        List/*<R>*/ piv[] = new List/*<R>*/[a0.dimension()];
        for (int i = 0; i < piv.length; i++) {
            piv[i] = new LinkedList/*<R>*/();
        }
        // transfer
        for (ListIterator i = p.iterator(); i.hasNext(); ) {
            Vector/*<R>*/ ak = (Vector/*<R>*/)i.next();
            assert ak.dimension() == piv.length : "coefficient vectors are assumed to have uniform dimensions";
            for (int j = 0; j < ak.dimension(); j++) {
                piv[j].add(ak.get(j));
            }
        }
        UnivariatePolynomial/*<R>*/ pi[] = new UnivariatePolynomial/*<R>*/[piv.length];
        for (int i = 0; i < pi.length; i++) {
            pi[i] = vf.polynomial((Arithmetic/*>R<*/[])piv[i].toArray(new Arithmetic/*>R<*/[0]));
        }
        return pi;
    }

    /**
     * Converts an array of coordinate polynomials
     * to a polynomial with R-vectorial coefficients.
     * @parameter pi an array of scalar coordinate polynomials
     *  P<sub>i</sub> = a<sub>0,i</sub>+a<sub>1,i</sub>X+...+a<sub>n,i</sub>X<sup>n</sup>
     * @return a vectorial polynomial a<sub>0</sub>+a<sub>1</sub>X+...+a<sub>n</sub>X<sup>n</sup>
     *  with vectorial coefficients a<sub>i</sub>=(a<sub>i,1</sub>,...,a<sub>i,k</sub>)
     * @see #componentPolynomials(UnivariatePolynomial)
     */
    public static final /*<R extends Arithmetic>*/
        UnivariatePolynomial/*<Vector<R>>*/ vectorialPolynomial(UnivariatePolynomial/*<R>*/ pi[]) {
        final ValueFactory vf = pi.length > 0 ? pi[0].valueFactory() : Values.getDefaultInstance();
        // the maximum degree of the polynomials
        /*final Integer degvalue = (Integer)Operations.sup.apply(Functionals.map(new Function() {
                public Object apply(Object pii) {
                    return ((UnivariatePolynomial)pii).degree();
                }
                }, Arrays.asList(pi)));*/
        Integer degvalue = vf.valueOf(-1);
        for (int i = 0; i < pi.length; i++) {
            if (pi[i].degree().compareTo(degvalue) > 0)
                degvalue = pi[i].degree();
        }
        if (degvalue.intValue() < 0) {
            // all polynomials are zero
            return vf.polynomial(new Vector/*<R>*/[] {
                vf.ZERO(pi.length)
            });
        }
        final int deg = degvalue.intValue();
        Vector/*<R>*/ pv[] = new Vector/*<R>*/[deg+1];
        for (int k = 0; k <= deg; k++) {
            Arithmetic/*>R<*/ ai[] = new Arithmetic/*>R<*/[pi.length];
            for (int i = 0; i < ai.length; i++) {
                ai[i] = pi[i].get(k);
            }
            pv[k] = vf.valueOf(ai);
        }
        return vf.polynomial(pv);
    }

    /**
     * returns the result of dividing monomial X^a by monomial X^b, or null if this division is impossible.
     * @param a
     * @param b
     * @return X^a/X^b represented as a-b or null if not divisible
     */
    private static final Arithmetic divideMonomial(Arithmetic a, Arithmetic b) {
    	Vector va = getExponentVector(a);
    	Vector vb = getExponentVector(b);
    	assert va.dimension() == vb.dimension() : "compatible ranks of polynomial rings";
    	int dim = va.dimension();
    	ListIterator i = va.iterator(), j = vb.iterator();
    	while (i.hasNext()) {
    		assert i.hasNext() && j.hasNext() : "compatible ranks of polynomial rings";
    		Integer ai = (Integer)i.next();
    		Integer bi = (Integer)j.next();
    		if (ai.compareTo(bi) < 0) {
    			assert Setops.some((getExponentVector(a.subtract(b))).iterator(), Functionals.bindSecond(Predicates.less, a.valueFactory().ZERO())) : "optimized implementation fits to canonical test";
    			return null;
    		}
        }
		assert !i.hasNext() && !j.hasNext() : "compatible ranks of polynomial rings";
        // test divisibility of monomial X^a by X^b
        Arithmetic div = a.subtract(b);
        //@internal the following is a trick for S=<b>N</b><sup>n</sup> represented as <b>Z</b><sup>n</sup> (get rid when introducing Natural extends Integer)
		assert !Setops.some((getExponentVector(div)).iterator(), Functionals.bindSecond(Predicates.less, a.valueFactory().ZERO())) : "optimized implementation fits to canonical test";
//        if (Setops.some((getExponentVector(div)).iterator(), Functionals.bindSecond(Predicates.less, a.valueFactory().ZERO())))
//                return null;
//        else {
  //            Functionals.map(Functionals.asFunction(Operations.greaterEqual), getExponentVector(a), getExponentVector(b));
                return div;
//        }
    }

    /**
     * Get a vectorial representation of an index for an exponent of a monomial, if possible
     * @param m
     * @return
     */
    private static Vector/*<Integer>*/ getExponentVector(Object m) {
    	if (m instanceof Vector) {
    		return (Vector)m;
    	} else if (m instanceof Integer) {
    		// univariate case
    		return ((Arithmetic)m).valueFactory().valueOf(new Integer[] {(Integer)m});
    	} else {
    		throw new ClassCastException("Cannot convert exponent representation into Vector<Integer> from " + m);
    	}
    }

    /**
     * Checks whether f is in the ideal generated by G (approximate check)
     * @return
     */
    private static final boolean validate(Polynomial f, Collection/*<Polynomial<R,S>>*/ g) {
        return true;
    }
 //  validate against Mathematica
 //   private static final boolean validate(Polynomial f, Collection/*<Polynomial<R,S>>*/ g) {
        /*
        final int vars = ((Polynomial)g.iterator().next()).rank();
        String varlist = "{";
        final String multinomialVariables[] = {"X", "Y", "Z"};
        for (int v = 0; v < vars; v++) {
                varlist += (v>0 ? "," : "") +  (vars<=3 ? multinomialVariables[v] : "X" + v);
        }
        varlist += "}";
        String query = "FullSimplify[PolynomialReduce[" + f+ ",GroebnerBasis[" + listForm(g) + "," + varlist + "], " + varlist + "][[2]] == 0]";
        String res = ml.evaluateToInputForm(query, 100);
        System.out.println(res + " for " + query);
        //assert "True".equals(res) : "Groebner Basis element is in ideal " + query );
        return "True".equals(res);
    }
    private static com.wolfram.jlink.KernelLink ml;
    static {createMathLink();}
    
    protected static void createMathLink() {
        try {
            ml = com.wolfram.jlink.MathLinkFactory.createKernelLink("-linkmode launch -linkname '"
                                                  + System.getProperty("com.wolfram.jlink.kernel")
                                                  + "'");

            // Get rid of the initial InputNamePacket the kernel will send
            // when it is launched.
            ml.discardAnswer();

            // define our imaginary unit
            //@xxx could have side effects for testdSolve
            ml.evaluate("i = I;");
            ml.discardAnswer();
        } catch (com.wolfram.jlink.MathLinkException e) {
            throw new Error("Fatal error opening link: " + e.getMessage());
        }
    }
    protected static void closeMathLink() {
        if (ml != null) {
            ml.close();
            ml = null;
        }
    }
    private static String listForm(Collection m) {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (Iterator i = m.iterator(); i.hasNext(); )
            sb.append(i.next() + (i.hasNext() ? "," : ""));
        sb.append('}');
        return sb.toString();
    }
    */

}// AlgebraicAlgorithms
