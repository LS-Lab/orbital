/**
 * @(#)Scalar.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Predicate;

/**
 * Abstraction of all scalar arithmetic number objects.
 * <p>
 * This class is the base class for all Arithmetic objects of scalar number type.
 * Scalars usually implement {@link Comparable} as well.
 * Most scalar implementation also tend to extend {@link java.lang.Number} but are not required
 * to do so, since that would break the freedom of inheriting from another base class.
 * </p>
 * 
 * @invariant usually this is Comparable || <span class="provable">&#9633;</span>abnormal(Comparable)
 * @stereotype &laquo;data-type&raquo;
 * @version 1.0, 2000/08/08
 * @author  Andr&eacute; Platzer
 * @todo perhaps drop Comparable here?
 */
public interface Scalar extends Arithmetic/*, Comparable*/ {
    /**
     * Checks whether the given arithmetic object is a number.
     * return whether v is complex, real, rational or an integer.
     */
    public static final Predicate isa = new Predicate() {
	    public boolean apply(Object v) {
    		return v instanceof Scalar;
	    }
	};
}
