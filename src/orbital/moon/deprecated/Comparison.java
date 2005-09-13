/*
 * @(#)Comparison.java 0.9 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * A Comparison interface is a callback for a generic Comparison between two
 * Objects.
 * 
 * <p>Comparing:<ul>
 * <li>not(Comparison)
 * </ul>
 * 
 * @deprecated Use java.util.Comparator since JDK1.2
 * @see java.util.Comparator
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public
interface Comparison {

	/**
	 * a&lt;b: Will compare whether a is less than b under a certain Comparison
	 * criteria.
	 * @return whether a is less than b
	 */
	boolean less(Object a, Object b);


	/**
	 * Comparing Implementations.
	 * @deprecated Use orbital.util.ReverseComparator since JDK1.2.
	 */
	static class Comparing {
		public static final Comparison not(final Comparison comparison) {
			return new Comparison() {
				public boolean less(Object a, Object b) {
					return !comparison.less(a, b);
				} 
			};
		} 

	}
}
