/*
 * @(#)Condition.java 0.9 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * A Condition interface is a callback for a generic Condition.
 * 
 * @version 0.9, 07/06/97
 * @author  Andr&eacute; Platzer
 * @deprecated Use more general orbital.logic.functor.VoidPredicate instead.
 * @see orbital.logic.functor.VoidPredicate
 */
public
interface Condition {

	/**
	 * is called to check whether the Condition is true or false.
	 * 
	 * @return returns state of Condition.
	 */
	boolean isTrue();
}
