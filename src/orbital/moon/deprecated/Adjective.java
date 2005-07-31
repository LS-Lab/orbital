/*
 * @(#)Adjective.java 0.9 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * An Adjective interface is a callback for a generic Adjective.
 * Implementations check a certain property of an Object to see
 * whether it matches this Attribute or Adjective.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @deprecated Use more general orbital.logic.functor.Predicate instead.
 * @see orbital.logic.functor.Predicate
 */
public
interface Adjective {

	/**
	 * is called to check whether the Adjective fits for an Object.
	 * 
	 * @return returns Adjective checked for Object.
	 */
	boolean fits(Object obj);
}
