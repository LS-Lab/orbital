/**
 * @(#)CombinatoricalTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;

import junit.framework.*;

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
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(CombinatoricalTest.class);
    }
    protected void setUp() {
	random = new Random();
    }
    private int integerArgument(int min, int max) {
	return min + (min == max ? 0 : random.nextInt(max-min));
    }
    public void testNonrepetitiveCombinationCount() {
	boolean repetition = false;
	int n = integerArgument(1, 5);
	int r = integerArgument(1, n);
	test(Combinatorical.getCombinations(r, n, repetition), !repetition);
    }
    public void testRepetitiveCombinationCount() {
	boolean repetition = true;
	test(Combinatorical.getCombinations(integerArgument(1, 5), integerArgument(1, 5), repetition), !repetition);
    }
    public void testNonrepetitivePermutationCount() {
	int n = integerArgument(1, 5);
	int r = integerArgument(1, n);
	r = n; //@internal since not yet implemented otherwise
	test(Combinatorical.getPermutations(r, n, false), true);
    }
    public void testRepetitivePermutationCount() {
	boolean repetition = true;
	test(Combinatorical.getPermutations(integerArgument(1, 5), integerArgument(1, 5), repetition), !repetition);
    }
    /**
     * Returns a list of integer lists containing all combinatorical possibilities.
     */
    private List/*_<List<orbital.math.Integer>>_*/ test(final Combinatorical c, boolean permutation) {
	System.out.println("all " + c.count() + " " + c);
	List possibilities = new ArrayList(c.count());
	int count = 0;
	while (c.hasNext()) {
	    int[] v = c.next();
	    System.out.println(orbital.math.MathUtilities.format(v));
	    possibilities.add(new Functionals.Catamorphism(new ArrayList(v.length), new BinaryFunction() {
		    public Object apply(Object vi, Object list) {
			((List)list).add(vi);
			return list;
		    }
		}).apply(v));
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
	return possibilities;
    }

    /**
     * Checks that all elements of l are different.
     */
    private boolean allDifferent(List l) {
	return new HashSet(l).size() == l.size();
    }
}
