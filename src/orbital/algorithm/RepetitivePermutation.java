/**
 * @(#)RepetitiveCombination.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;


import java.util.Arrays;
import java.util.NoSuchElementException;
import orbital.math.MathUtilities;
import orbital.util.SuspiciousError;

/**
 * Per[n,r](true)
 * @version 0.9, 2000/11/29
 * @author  Andr&eacute; Platzer
 * @todo outroduce and use GeneralizedPermutation({n,....,n}) instead.
 */
class RepetitivePermutation extends Combinatorical {
    private static final long serialVersionUID = 787550243481366022L;
    private int     r;
    private int     n;
    private int[]   permutation;
    public RepetitivePermutation(int r, int n) {
    	this.r = r;
    	this.n = n;
    	this.permutation = new int[r];
	Arrays.fill(permutation, 0);
	// prestep for next to return the first tuple
	if (permutation.length > 0)
	    permutation[permutation.length - 1]--;
    }

    public boolean hasNext() {
	for (int i = permutation.length - 1; i >= 0; i--)
	    if (permutation[i] + 1 < n)
		return true;
	return false;
    } 

    public int[] next() {
	if (!hasNext())
	    throw new NoSuchElementException();
	for (int i = permutation.length - 1; i >= 0; i--) {
	    if (++permutation[i] >= n)
		permutation[i] = 0;
	    else
		return permutation;
	} 
	if (permutation.length == 0)
	    return permutation;
	else
	    throw new SuspiciousError();
    } 

    public boolean hasPrevious() {
	if (true)
	    throw new UnsupportedOperationException("not yet implemented");
	for (int i = permutation.length - 1; i >= 0; i--)
	    if (permutation[i] - 1 >= 0)
		return true;
	    else if (permutation[i] < 0)
		// we are below 0 (at the beginning)
		return false;
	return false;
    } 

    public int[] previous() {
	if (true)
	    throw new UnsupportedOperationException("not yet implemented");
	if (!hasPrevious())
	    throw new NoSuchElementException();
	for (int i = permutation.length - 1; i >= 0; i--) {
	    if (--permutation[i] < 0)
		permutation[i] = n - 1;
	    else
		return permutation;
	} 
	if (permutation.length == 0)
	    return permutation;
	else
	    throw new SuspiciousError();
    } 

    public int count() {
	return (int) Math.pow(n, r);
    } 

    /*
     * private static final int EMPTY = -1;    // value not element [0|n-1] used to indicate a currently variably Combinating value.
     * private void Combinate(int perm[],int e) {
     * for(int i=0;i<possibilities;i++) {
     * int n_perm[] = new int[perm.length];
     * System.arraycopy(perm,0, n_perm,0, perm.length);
     * 
     * n_perm[ e-1 ] = i;    // e-1-th will be set to all values 0..n
     * if (e-1>0)
     * Combinate(n_perm, e-1);         //RS: recursion step
     * else
     * ;//inform.apply(n_perm);           //RA: recursion abort
     * }
     * }
     */
	 
    public String toString() {
	return getClass().getName() + " of " + r + " elements out of " + n;
    }
}