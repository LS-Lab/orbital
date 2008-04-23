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
 * @version $Id$
 */
class NonrepetitivePermutation extends Combinatorical {
    private static final long serialVersionUID = 5519106291934622529L;
    private int   r;
    private int   n;
    private int[] permutation;
    /**
     * struggle to get the first permutation returned as well, without next(int[]) saying finish.
     */
    private boolean first;
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
        assert isSorted(permutation) : "initialized to very first sorted permutation " + MathUtilities.format(permutation);
        if (r < n)
            throw new UnsupportedOperationException("r < n not yet implemented");
        this.first = true;
    }

    private boolean isVeryFirst() {
        if (this.first) {
            assert isSorted(permutation) : "very first sorted permutation " + MathUtilities.format(permutation);
        }
        return this.first;
    }
    
    /**
     * Returns the number of permutations that araise from this permutation-sequence.
     */
    public int count() {
        return MathUtilities.nPr(n, r);
    } 

    public boolean hasNext() {
        return isVeryFirst() || permute((int[]) permutation.clone());
    }

    public int[] next() {
        if (isVeryFirst()) {
            this.first = false;
            return permutation;
        }
        int[] old = (int[]) permutation.clone();
        if (permute(permutation)) {
            return permutation;
        } else {
            // same as very first permutation _again_ hence finished
            // restore initial permutation, i.e. unpermute
            System.arraycopy(old,0, permutation,0, old.length);
        }
        if (r == n)
            throw new NoSuchElementException("no more elements for r=n in " + this);
        assert r < n : "r < n case because r <= n and r == n is solved";
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
     * n the number of possible values for each element of the permutation. n = |M|.
     * @return whether the permutation set now is a new one rather than the very first one again.
     */
    public static boolean permute(int permutation[]) {
        int first = 0;
        int last = permutation.length;
        if (first == last) {
            assert isSorted(permutation) : "very first sorted permutation " + MathUtilities.format(permutation);
            return false;
        }
        int i = first;
        ++i;
        if (i == last) {
            assert isSorted(permutation) : "very first sorted permutation " + MathUtilities.format(permutation);
            return false;
        }
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
                assert !isSorted(permutation) : "not very first sorted permutation " + MathUtilities.format(permutation);
                return true;
            } 
            if (i == first) {
                reverse(permutation, first, last);
                assert isSorted(permutation) : "very first sorted permutation " + MathUtilities.format(permutation);
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
     * Whether the given permutation is sorted 0,1,2,3,...,n-1
     */
    private static boolean isSorted(int permutation[]) {
        for (int i = 0; i < permutation.length; i++) {
            if (permutation[i] != i)
                return false;
        }
        return true;
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
