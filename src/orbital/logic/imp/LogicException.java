/**
 * @(#)LogicException.java 1.0 1999/03/04 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

/**
 * Thrown whenever an exception related to logic or a calculus occurs.
 * 
 * @version 1.0, 1999/03/04
 * @author  Andr&eacute; Platzer
 */
public
class LogicException extends RuntimeException {
	public LogicException(String message) {
		super(message);
	}
	public LogicException() {}
}
