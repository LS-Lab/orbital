/**
 * @(#)ContinuousComparator.java 0.9 2000/03/04 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.deprecated;

/**
 * A continuous comparison function or distance measure for metrices.
 * This class is a simple but important extension of {@link java.util.Comparator}
 * that is applicable in numerical context. Its absolute value is a general metric measure.
 * <p>
 * A similarity measure can be derived from a continuous comparison function easily:
 * if <code>f</code> is a continuous comparison function with maximum deviation (half range of values)
 * of <code>d</code>, then <code>1-Math.abs(f(x,y)/d)</code> is the similarity of x and y.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see java.util.Comparator
 * @TODO deprecated Use orbital.math.Metric instead
 */
public
interface ContinuousComparator {

        /**
         * Compares its two arguments for order.
         * Returns a number whose absolute value says how much both arguments equal.
         * A negative number expresses that the first argument is smaller than the second,
         * zero that they are totally equal and a positive number that the first argument is larger.
         * The absolute value gives a measure for how much they differ (for metrics).
         * <p>
         * The implementor must ensure that compare is commutative/symmetric:
         * <code>compare(x, y) == compare(y, x) for all x and y</code>.
         * (This implies that <code>compare(x, y)</code> must throw an exception if and only if <code>compare(y, x)</code> throws an exception.)
         * <p>
         * It is generally required that <code>(compare(x, y)==0.0) == (x.equals(y))</code>.
         * <small>If this is not the case any comparison function that violates this condition should clearly indicate this fact.
         * The recommended language is "Note: this comparison imposes distances that are inconsistent with equals."</small>
         * <p>
         * Additionally, it should fulfill the triangular relation <code>compare(a,b) =&lt; compare(a,c) + compare(c,b)</code>.
         */
        double compare(Object o1, Object o2);
}
