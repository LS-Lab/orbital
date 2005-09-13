/**
 * @(#)RepetitiveCombination.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;


import java.util.Arrays;
import java.util.NoSuchElementException;
import orbital.math.MathUtilities;

/**
 * Com[n,r](true)
 * @version $Id$
 */
class RepetitiveCombination extends Combinatorical {
    private static final long serialVersionUID = -7887257459549546388L;
    private int     r;
    private int     n;
    private int[]   combination;
    public RepetitiveCombination(int r, int n) {
    	this.r = r;
    	this.n = n;
    	this.combination = new int[r];
	Arrays.fill(combination, 0);
	// prestep for next to return the first tuple
	combination[combination.length - 1]--;
    }

    public boolean hasNext() {
	// search for the (for example rightmost) element that is below its maximum
	for (int i = combination.length - 1; i >= 0; i--) {
	    if (combination[i] + 1 < n)
		return true;
	}
	return false;
    } 

    public int[] next() {
	// search for the rightmost element that is below its maximum
	for (int i = combination.length - 1; i >= 0; i--) {
	    if (combination[i] + 1 < n) {
		combination[i]++;
		for (int j = i + 1; j < combination.length; j++)
		    combination[j] = combination[j - 1];
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
	return (int) MathUtilities.nCr(n + r - 1, r);
    } 

    public String toString() {
	return getClass().getName() + "[of " + r + " elements out of " + n + "]";
    }
}
