/**
 * @(#)GeneralizedPermutaion.java 0.9 2002-08-18 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;


import java.util.Arrays;
import java.util.NoSuchElementException;
import orbital.math.MathUtilities;
import orbital.util.SuspiciousError;

import orbital.math.functional.Operations;
import orbital.math.Values;
import orbital.math.Integer;
import orbital.util.Utility;

/**
 * Generalized Per[n] with n&isin;<b>N</b><sup>r</sup>.
 * @version 0.9, 2002-08-18
 * @internal (apart from more flexible bounds, dimensions instead of n, almost) identical to @see RepetitivePermutation
 * @todo why do we start changing index.length - 1 instead of 0?
 */
class GeneralizedPermutation extends Combinatorical {
    private static final long serialVersionUID = 6710387882794688842L;
    private final int[] dimensions;
    private int[] index;
    /**
     * Create a new multi index iterator starting at (0,...,0).
     */
    public GeneralizedPermutation(final int[] dimensions) {
	this.dimensions = dimensions;
	this.index = zero();
    }

    public boolean hasNext() {
	for (int k = index.length - 1; k >= 0; k--) {
	    if (index[k] >= dimensions[k]) {
		return false;
	    }
	}
	return true;
    } 

    public int[] next() {
	if (!hasNext())
	    throw new NoSuchElementException();
	int[] r = (int[]) index.clone();
	for (int k = index.length - 1; k >= 0; k--) {
	    index[k]++;
	    if (index[k] == dimensions[k]) {
		index[k] = 0;
	    } else if (index[k] > dimensions[k]) {
		assert false : NoSuchElementException.class + " should already have occurred";
	    } else if (index[k] < dimensions[k]) {
		return r;
	    } else {
		assert false;
	    }
	}
	// went through all possibilities, finish of iterator
	if (index.length == 0) {
	    return r;
	} else {
	    assert Utility.equalsAll(index, zero()) : "rotated back to 0";
	    // mark finish
	    for (int i = 0; i < index.length; i++) {
		index[i] = dimensions[i] - 1;
	    }
	    index[index.length - 1]++;
	}
	return r;
    }

    public boolean hasPrevious() {
	for (int k = index.length - 1; k >= 0; k--) {
	    if (index[k] - 1 >= 0) {
		return true;
	    }
	}
	return false;
    } 

    public int[] previous() {
	if (!hasPrevious())
	    throw new NoSuchElementException();
	for (int k = index.length - 1; k >= 0; k--) {
	    if (--index[k] < 0) {
		index[k] = dimensions[k] - 1;
	    } else {
		return index;
	    }
	} 
	if (index.length == 0) {
	    return index;
	} else {
	    throw new SuspiciousError();
	}
    } 

    public int count() {
	return ((Integer) Operations.product.apply(Values.getDefaultInstance().valueOf(dimensions))).intValue();
    } 

    public String toString() {
	return getClass().getName() + "[of " + MathUtilities.format(dimensions) + " elements]";
    }

    /**
     * 0 = (0,...,0)
     */
    private int[] zero() {
	int[] zero = new int[dimensions.length];
	Arrays.fill(zero, 0);
	return zero;
    }
}
