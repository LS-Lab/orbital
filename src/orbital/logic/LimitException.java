/**
 * @(#)LimitException.java 0.9 2000/06/17 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * Thrown when a limit is exceeded.
 * 
 * @version 0.9, 2000/06/17
 * @author  Andr&eacute; Platzer
 */
public
class LimitException extends RuntimeException {
	public LimitException() {}

	public LimitException(String spec) {
		super(spec);
	}
}
