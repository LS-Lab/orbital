/**
 * @(#)Combinatorical.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;

import java.io.Serializable;

import java.util.Arrays;
import java.util.NoSuchElementException;
import orbital.math.MathUtilities;
import orbital.util.SuspiciousError;

import orbital.math.functional.Operations;
import orbital.math.Values;
import orbital.math.Integer;

/**
 * Class for combinatorical operations.
 * 
 * @version 0.9, 2000/11/29
 * @author  Andr&eacute; Platzer
 * @internal is an interface
 */
public abstract class Combinatorical /*implements Iterator<int[]> like*/ implements Serializable {
    private static final long serialVersionUID = 8581671329920186455L;
    private static class Debug {
	private Debug() {}
	public static void main(String arg[]) throws Exception {
	    Combinatorical c = getCombinations(3, 5, false);
	    System.out.println("all " + c.count() + " " + c);
	    int count = 0;
	    while (c.hasNext()) {
		System.out.println(orbital.math.MathUtilities.format(c.next()));
		count++;
	    } 
	    System.out.println("generated " + count + " which is " +(count == c.count() ? "correct" : "NOT correct"));
	} 
    }	// Debug

    
	/**
	 * Returns the number of combinatorical tuples that araise from this sequence.
	 */
    public abstract int count();

    /**
     * Whether this sequence has a next combinatorical tuple.
     */
    public abstract boolean hasNext();

    /**
     * Get the next combinatorical tuple.
     * @return the next combinatorical tuple.
     *  <b>Note:</b> the array returned should <em>not</em> be modified.
     * @throws NoSuchElementException if not hasNext().
     */
    public abstract int[] next();

    
    /**
     * Whether this sequence has a previous combinatorical tuple.
     */
    public abstract boolean hasPrevious();

    /**
     * Get the previous combinatorical tuple.
     * @return the previous combinatorical tuple.
     *  <b>Note:</b> the array returned should <em>not</em> be modified.
     * @throws NoSuchElementException if not hasPrevious().
     */
    public abstract int[] previous();

    // facade factory
	
    /**
     * Get a combinatorical instance.
     * @param r the size of the tuples.
     *  The number of elements to choose out of n.
     * @param combinations whether only combinations are allowed, or every permutation.
     * @param n the number of elements choosable. n = |M|.
     * @param repetition whether elements in a tuple are allowed to repeat.
     * @see #getPermutations(int, int, boolean)
     * @see #getCombinations(int, int, boolean)
     */
    public static Combinatorical getInstance(int r, boolean combinations, int n, boolean repetition) {
	return combinations ? getCombinations(r, n, repetition) : getPermutations(r, n, repetition);
    }

    /**
     * Get all r-permutations of n elements.
     * @param r the size of the tuples to permute.
     *  The number of elements to choose out of n per tuple.
     * @param n the number of elements choosable. n = |M|.
     * @param repetition whether elements in a tuple are allowed to repeat.
     *  The permutations with repetition contain n<sup>r</sup>,
     *  those without repetition contain n<b>P</b>r tuples.
     */
    public static Combinatorical getPermutations(int r, int n, boolean repetition) {
	return repetition ? (Combinatorical) new RepetitivePermutation(r, n) : (Combinatorical) new NonrepetitivePermutation(r, n);
    }
    public static Combinatorical getPermutations(int n, boolean repetition) {
	return getPermutations(n, n, repetition);
    }
    /**
     * Get all (generalized) permutations elements.
     * @param n the numbers of elements choosable. r := n.length is the size of the tuples
     *  and n[i] is the number of elements choosable for the element at index i of the tuple.
     */
    public static Combinatorical getPermutations(int[] n) {
	return new GeneralizedPermutation(n);
    }

    /**
     * Get all r-combinations of n elements.
     * Combinations are those permutations ignoring order and(!) whose elements are always sorted.
     * So its like the difference between a set and a list.
     * @param r the size of the tuples to combinate.
     *  The number of elements to choose out of n per tuple.
     * @param n the number of elements choosable. n = |M|.
     * @param repetition whether elements in a tuple are allowed to repeat.
     *  The combinations with repetition contain <big>(</big><sup>n + <span class="doubleIndex">r</sup><sub>r</sub></span> <sup>-1</sup><big>)</big>,
     *  those without repetition contain n<b>C</b>r = <big>(</big><span class="doubleIndex"><sup>n</sup><sub>r</sub></span><big>)</big> tuples.
     */
    public static Combinatorical getCombinations(int r, int n, boolean repetition) {
	return repetition ? (Combinatorical) new RepetitiveCombination(r, n) : new NonrepetitiveCombination(r, n);
    }
}

// Implementations

/**
 * Per[n,r](true)
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
	for (int i = permutation.length - 1; i >= 0; i--)
	    if (permutation[i] - 1 >= 0)
		return true;
	    else if (permutation[i] < 0)
		// we are below 0 (at the beginning)
		return false;
	return false;
    } 

    public int[] previous() {
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
/**
 * Generalized Per[n] with n&isin;<b>N</b><sup>r</sup>.
 * @see Iterator
 * @internal (apart from more flexible bounds, dimensions instead of n, almost) identical to @see RepetitivePermutation
 */
class GeneralizedPermutation extends Combinatorical {
    private static final long serialVersionUID = 6710387882794688842L;
    private int[] dimensions;
    private int[] index;
    /**
     * Create a new multi index iterator starting at (0,...,0).
     */
    public GeneralizedPermutation(int[] dimensions) {
	this.dimensions = dimensions;
	this.index = new int[dimensions.length];
	Arrays.fill(index, 0);
	// prestep for next to return the first tuple
	if (index.length > 0)
	    index[index.length - 1]--;
	//index[0]--;
    }

    public boolean hasNext() {
	for (int k = index.length - 1; k >= 0; k--)
	    //for (int k = 0; k < index.length; k++)
	    if (index[k] + 1 < dimensions[k])
		return true;
	return false;
    } 

    public int[] next() {
	if (!hasNext())
	    throw new NoSuchElementException();
	for (int k = index.length - 1; k >= 0; k--) {
	    //for (int k = 0; k < index.length; k++) {
	    if (++index[k] >= dimensions[k])
		index[k] = 0;
	    else
		return index;
	} 
	if (index.length == 0)
	    return index;
	else
	    throw new SuspiciousError();
    } 

    public boolean hasPrevious() {
	for (int k = index.length - 1; k >= 0; k--)
	    //for (int k = 0; k < index.length; k++)
	    if (index[k] - 1 >= 0)
		return true;
	return false;
    } 

    public int[] previous() {
	if (!hasPrevious())
	    throw new NoSuchElementException();
	for (int k = index.length - 1; k >= 0; k--) {
	    //for (int k = 0; k < index.length; k++) {
	    if (--index[k] < 0)
		index[k] = dimensions[k] - 1;
	    else
		return index;
	} 
	if (index.length == 0)
	    return index;
	else
	    throw new SuspiciousError();
    } 

    public int count() {
	return ((Integer) Operations.product.apply(Values.valueOf(dimensions))).intValue();
    } 

    public String toString() {
	return getClass().getName() + " of " + MathUtilities.format(dimensions) + " elements";
    }
}

/**
 * Per[n,r](false)
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
     * @pre r <= n
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
	return getClass().getName() + " of " + r + " elements out of " + n;
    }
}

/**
 * Com[n,r](true)
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
	return combination[0] + 1 < n;
    } 

    public int[] next() {
	// search the rightmost element that is below its maximum
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
	return getClass().getName() + " of " + r + " elements out of " + n;
    }
}

/**
 * Com[n,r](true)
 */
class NonrepetitiveCombination extends Combinatorical {
    private static final long serialVersionUID = -5852223116797053870L;
    private int     r;
    private int     n;
    private int[]   combination;
    /**
     * @pre r <= n
     */
    public NonrepetitiveCombination(int r, int n) {
    	if (!(r <= n))
	    throw new IllegalArgumentException("only r <= n combinations without repetition exist");
    	this.r = r;
    	this.n = n;
    	this.combination = new int[r];
	for (int i = 0; i < combination.length; i++)
	    combination[i] = i;
	// prestep for next to return the first tuple
	combination[combination.length - 1]--;
    }

    public boolean hasNext() {
	return combination[0] < n - combination.length;
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
	return getClass().getName() + " of " + r + " elements out of " + n;
    }
}
