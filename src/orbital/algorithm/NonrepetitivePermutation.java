/**
 * @(#)NonrepetitivePermutation.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;


import java.util.NoSuchElementException;
import orbital.math.MathUtilities;

/**
 * Per[n,r](false)
 * @version 0.9, 2000/11/29
 */
class NonrepetitivePermutation extends Combinatorical {
    private static final long serialVersionUID = 5519106291934622529L;
    private int   r;
    private int   n;
    private int[] permutation;
    /**
     * struggle to get the first permutation returned as well, without next(int[]) saying finish.
     */
    private boolean first = true;
    /**
     * @preconditions r <= n
     */
    public NonrepetitivePermutation(int r, int n) {
    	if (!(r <= n))
	    throw new IllegalArgumentException("only r <= n permutations without repetition exist");
    	this.r = r;
    	this.n = n;
    	this.permutation = new int[r];
	for (int i = 0; i < permutation.length; i++)
	    permutation[i] = i;
	if (r < n)
	    throw new UnsupportedOperationException("r < n not yet implemented");
    }
    
    /**
     * Returns the number of permutations that araise from this permutation-sequence.
     */
    public int count() {
	return MathUtilities.nPr(n, r);
    } 

    public boolean hasNext() {
	int[] copy = (int[]) permutation.clone();
	return first || permute(copy);
    }

    public int[] next() {
	if (first) {
	    first = false;
	    return permutation;
	}
	else if (permute(permutation))
	    return permutation;
	if (r == n)
	    throw new NoSuchElementException();
	assert r < n : "r < n case because r <= n abd r == n is solved";
	throw new UnsupportedOperationException("r < n not yet implemented");
    }

    public boolean hasPrevious() {
	throw new UnsupportedOperationException("not yet implemented");
    }

    public int[] previous() {
	throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Given a current permutation, calculates the next permutation.
     * @param permutation the current permutation array of length k.
     * @param n the number of possible values for each element of the permutation. n = |M|.
     * @return whether the permutation set is a new one or again the first one.
     */
    public static boolean permute(int permutation[]) {
	int first = 0;
	int last = permutation.length;
	if (first == last)
	    return false;
	int i = first;
	++i;
	if (i == last)
	    return false;
	i = last;
	--i;

	while (true) {
	    int ii = i--;
	    if (permutation[i] < permutation[ii]) {
		int j = last;
		while (!(permutation[i] < permutation[--j]));
		// iter_swap(i, j);
		int T = permutation[i];
		permutation[i] = permutation[j];
		permutation[j] = T;
		// reverse(ii, last);
		reverse(permutation, ii, last);
		return true;
	    } 
	    if (i == first) {
		reverse(permutation, first, last);
		return false;
	    } 
	} 
    } 
    private static void reverse(int permutation[], int first, int last) {
	while (first < last) {
	    // iter_swap(first++, --last);
	    int T = permutation[first];
	    permutation[first++] = permutation[--last];
	    permutation[last] = T;
	} 
    } 

    /**
     * will produce all permutating values.
     * @deprecated
     */
    /*
     * public void rePermutate() {
     * int n = permutationOrder.length;
     * int perm[] = new int[n];
     * for(int i=0;i<n;i++)
     * perm[i] = EMPTY;    // all elements shell permutate
     * permutate(perm,n);
     * }
     * 
     * private static final int EMPTY = -1;    // value not element [0|n-1] used to indicate a currently variably permutating value.
     * private final void permutate(int perm[],int n) {
     * for(int i=0;i<n;i++) {
     * int n_perm[] = new int[perm.length];
     * System.arraycopy(perm,0, n_perm,0, perm.length);
     * 
     * n_perm[ getNthEmpty(i,n_perm) ] = n-1;    // i-th EMPTY will be set to current value n-1
     * //if (n-1<=0) {for(int q=0;q<n_perm.length;q++) System.err.print(n_perm[q]+", "); System.err.println("\b\t"+(n-1)+" set at "+getNthEmpty(i,perm)+"("+i+".)");}
     * if (n-1>0)
     * permutate(n_perm, n-1);         //RS: recursion step
     * //RA: recursion abort
     * }
     * }
     */

    /**
     * returns the index of the n-th EMPTY value in v[].
     */

    /*
     * private int getNthEmpty(int n,int v[]) {
     * int i = 0;  // index in v
     * for(int iEmpty=0;iEmpty<=n;iEmpty++,i++) {    // count EMPTY values inclusive
     * while(v[i] != EMPTY)
     * i++;
     * if (iEmpty==n)
     * return i;
     * }
     * return -1;
     * }
     */

    public String toString() {
	return getClass().getName() + "[of " + r + " elements out of " + n + "]";
    }
}
