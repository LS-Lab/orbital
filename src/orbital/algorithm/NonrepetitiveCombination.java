/**
 * @(#)NonrepetitiveCombination.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;


import java.util.NoSuchElementException;
import orbital.math.MathUtilities;

/**
 * Com[n,r](true)
 * @version 0.9, 2000/11/29
 */
class NonrepetitiveCombination extends Combinatorical {
    private static final long serialVersionUID = -5852223116797053870L;
    private int     r;
    private int     n;
    private int[]   combination;
    /**
     * @preconditions r <= n
     */
    public NonrepetitiveCombination(int r, int n) {
    	if (!(r <= n))
	    throw new IllegalArgumentException("only r <= n combinations without repetition exist, cannot pick more than present");
    	this.r = r;
    	this.n = n;
    	this.combination = new int[r];
	for (int i = 0; i < combination.length; i++)
	    combination[i] = i;
	// prestep for next to return the first tuple
	combination[combination.length - 1]--;
    }

    public boolean hasNext() {
	// search for the (for example rightmost) element that is below its maximum
	for (int i = combination.length - 1; i >= 0; i--) {
	    if (combination[i] < i + n - combination.length)
		return true;
	}
	return false;
	//@todo could optimise with something like return combination[0] < n - combination.length;
    } 

    public int[] next() {
	// search the rightmost element that is below its maximum
	for (int i = combination.length - 1; i >= 0; i--) {
	    if (combination[i] < i + n - combination.length) {
		combination[i]++;
		for (int j = i + 1; j < combination.length; j++)
		    combination[j] = combination[j - 1] + 1;
		return combination;
	    }
	} 
	throw new NoSuchElementException();
    } 

    public boolean hasPrevious() {
	throw new UnsupportedOperationException("not yet implemented");
    }

    public int[] previous() {
	throw new UnsupportedOperationException("not yet implemented");
    }

    public int count() {
	return (int) MathUtilities.nCr(n, r);
    } 

    public String toString() {
	return getClass().getName() + "[of " + r + " elements out of " + n + "]";
    }
}
