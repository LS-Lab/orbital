/**
 * @(#)CombinatoricalTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;

import junit.framework.*;

import java.util.Random;

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
	return min + random.nextInt(max-min);
    }
    public void testNonrepetitiveCombinationCount() {
	int n = integerArgument(1, 5);
	int r = integerArgument(1, n);
	test(Combinatorical.getCombinations(r, n, false));
    }
    public void testRepetitiveCombinationCount() {
	test(Combinatorical.getCombinations(integerArgument(1, 5), integerArgument(1, 5), true));
    }
    public void testNonrepetitivePermutationCount() {
	int n = integerArgument(1, 5);
	int r = integerArgument(1, n);
	r = n; //@internal since not yet implemented otherwise
	test(Combinatorical.getPermutations(r, n, false));
    }
    public void testRepetitivePermutationCount() {
	test(Combinatorical.getPermutations(integerArgument(1, 5), integerArgument(1, 5), true));
    }
    private void test(Combinatorical c) {
	System.out.println("all " + c.count() + " " + c);
	int count = 0;
	while (c.hasNext()) {
	    System.out.println(orbital.math.MathUtilities.format(c.next()));
	    count++;
	} 
	System.out.println("generated " + count + " which is " +(count == c.count() ? "correct" : "NOT correct"));
	assertTrue(count == c.count() , c + " Combinatorical.count()=" + c.count() + " matches Combinatorical.hasNext()=" + count);
    } 
}
