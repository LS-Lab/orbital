/**
 * @(#)CombinatoricalTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;

import junit.framework.*;
import junit.extensions.*;

import java.util.Random;
import orbital.logic.functor.*;
import orbital.util.*;
import orbital.math.*;
import java.util.*;

/**
 * A sample test case, testing .
 * @version 1.1, 2002-09-14
 */
public class CombinatoricalTest extends check.TestCase {
    private Random random;
    private ValueFactory vf;
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new RepeatedTest(new TestSuite(CombinatoricalTest.class), 5);
    }
    protected void setUp() {
	random = new Random();
	vf = Values.getDefaultInstance();
    }
    private int integerArgument(int min, int max) {
	return min + (min == max ? 0 : random.nextInt(max-min+1));
    }
    public void testNonrepetitiveCombinationCount() {
	boolean repetition = false;
	int n = integerArgument(1, 5);
	int r = integerArgument(1, n);
	test(Combinatorical.getCombinations(r, n, repetition), !repetition, r, false);
    }
    public void testRepetitiveCombinationCount() {
	boolean repetition = true;
	int r = integerArgument(1, 5);
	test(Combinatorical.getCombinations(r, integerArgument(1, 5), repetition), !repetition, r, false);
    }
    public void testNonrepetitivePermutationCount() {
	int n = integerArgument(1, 5);
	int r = integerArgument(1, n);
	r = n; //@internal since not yet implemented otherwise
	test(Combinatorical.getPermutations(r, n, false), true, r, false);
    }
    public void testRepetitivePermutationCount() {
	boolean repetition = true;
	int r = integerArgument(1, 5);
	test(Combinatorical.getPermutations(r, integerArgument(1, 5), repetition), !repetition, r, false);
    }
    public void testGeneralizedPermutation() {
	boolean repetition = true;
	int r = integerArgument(1, 5);
	int n[] = new int[r];
	for (int i = 0; i < r; i++) {
	    n[i] = integerArgument(1, 5);
	}
	test(Combinatorical.getPermutations(n), !repetition, r, true);
    }
    /**
     * Returns a list of integer lists containing all combinatorical possibilities.
     * @param testReverse whether or not to test whether previous works as well.
     */
    private List/*_<List<orbital.math.Integer>>_*/ test(final Combinatorical c, boolean permutation, int r, boolean testReverse) {
	System.out.println("all " + c.count() + " " + c);
	List possibilities = new ArrayList(c.count());
	int count = 0;
	assertTrue(!testReverse || !c.hasPrevious(), "positioned at first implies that there is no previous");
	while (c.hasNext()) {
	    int[] v = c.next();
	    System.out.println(orbital.math.MathUtilities.format(v));
	    assertEquals(v.length, r);
	    List l = new ArrayList(v.length);
	    for (int i = 0; i < v.length; i++) {
		l.add(vf.valueOf(v[i]));
	    }
	    //@todo one is the reverse of the other
	    /*assertEquals(l,
			 new Functionals.Catamorphism(new ArrayList(v.length), new BinaryFunction() {
				 public Object apply(Object vi, Object list) {
				     ((List)list).add(vi);
				     return list;
				 }
			     }).apply(v)
			     );*/
	    possibilities.add(l);
	    count++;
	} 
	System.out.println("generated " + count + " which is " +(count == c.count() ? "correct" : "NOT correct"));
	System.out.println("generated " + possibilities);
	assertTrue(count == c.count() , c + " Combinatorical.count()=" + c.count() + " matches Combinatorical.hasNext()=" + count);
	assertTrue(allDifferent(possibilities) , c + " no combinatorical possibility occurs twice in " + possibilities);
	if (permutation)
	    assertTrue(Setops.all(possibilities, new Predicate() {
		    public boolean apply(Object v) {
			assertTrue(allDifferent((List/*_<Integer>_*/)v) , c + " for permutations all elements of the permutation are different " + MathUtilities.format(v));
			return true;
		    }
		}) , c + " for permutations, all elements of the permutation are different");
	assertTrue(!c.hasNext(), "wandered through completely, implies that we cannot go further");
	if (testReverse) {
	    final ListIterator reverse = possibilities.listIterator(possibilities.size());
	    while (c.hasPrevious()) {
		assertTrue(reverse.hasPrevious(), "forward and backward traversals have same length");
		int[] v = c.previous();
		List l = (List)reverse.previous();
		// System.out.println("Forward " + l + " equals backward " + MathUtilities.format(v));
		assertEquals(v.length, l.size());
		for (int i = 0; i < v.length; i++) {
		    assertEquals(v[i], ((orbital.math.Integer)l.get(i)).intValue());
		}
	    }
	    assertTrue(!reverse.hasPrevious(), "forward and backward traversals have same length");
	}
	
	return possibilities;
    }

    /**
     * Checks that all elements of l are different.
     */
    private boolean allDifferent(List l) {
	return new HashSet(l).size() == l.size();
    }
}
