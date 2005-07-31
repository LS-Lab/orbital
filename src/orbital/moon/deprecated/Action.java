/*
 * @(#)Action.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * Action (Notify) callback interface is a description of something taking notice
 * of an action.
 * 
 * @deprecated Use more general orbital.logic.functor.Function instead.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.Function
 */
public
interface Action {

	/**
	 * called back when action is notified.
	 */
	public Object action(Object arg);
}
