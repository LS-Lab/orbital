/**
 * @(#)SimilarityMeasure.java 0.9 2000/03/04 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;


/**
 * A similarity measuring function.
 * 
 * @version 0.9, 2000/03/04
 * @author  Andr&eacute; Platzer
 * @see java.util.Comparator
 * @deprecated Use more general calculation of orbital.math.Metric, instead.
 */
public
interface SimilarityMeasure {

	/**
	 * Compares its two arguments for similarity.
	 * Returns a number ranging from 0 to 1 saying how much the arguments resemble.
	 * <p>
	 * The implementor must ensure that similar is commutative:
	 * <code>similar(x, y) == similar(y, x) for all x and y</code>.
	 * (This implies that <code>similar(x, y)</code> must throw an exception if and only if <code>similar(y, x)</code> throws an exception.)
	 * <p>
	 * It is generally the case, but not strictly required that <code>(similar(x, y)==1.0) == (x.equals(y))</code>.
	 * Generally speaking, any similarity measure that violates this condition should clearly indicate this fact.
	 * The recommended language is "Note: this similarity measure imposes similarities that are inconsistent with equals."
	 */
	double similar(Object o1, Object o2);
}
