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

/**
 * Generalized Per[n] with n&isin;<b>N</b><sup>r</sup>.
 * @version 0.9, 2002-08-18
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
    }

    public boolean hasNext() {
	for (int k = index.length - 1; k >= 0; k--)
	    if (index[k] >= dimensions[k])
		return false;
	return true;
    } 

    public int[] next() {
	if (!hasNext())
	    throw new NoSuchElementException();
	int[] r = (int[]) index.clone();
	for (int k = index.length - 1; k >= 0; k--) {
	    if (++index[k] == dimensions[k])
		index[k] = 0;
	    else if (index[k] > dimensions[k]) {
		assert false : NoSuchElementException.class + " should already have occurred";
	    } else
		return r;
	}
	if (index.length == 0)
	    return r;
	else
	    // mark finish
	    index[0] = dimensions[0] + 1;
	return r;
    } 

    public boolean hasPrevious() {
	for (int k = index.length - 1; k >= 0; k--)
	    if (index[k] - 1 >= 0)
		return true;
	return false;
    } 

    public int[] previous() {
	if (!hasPrevious())
	    throw new NoSuchElementException();
	for (int k = index.length - 1; k >= 0; k--) {
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
	return ((Integer) Operations.product.apply(Values.getDefaultInstance().valueOf(dimensions))).intValue();
    } 

    public String toString() {
	return getClass().getName() + " of " + MathUtilities.format(dimensions) + " elements";
    }
}
