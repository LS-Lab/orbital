/**
 * @(#)IterationLimitException.java 0.9 2000/06/17 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * Thrown when the limit for the maximum number of iterations is overrun.
 * 
 * @version 0.9, 2000/06/17
 * @author  Andr&eacute; Platzer
 */
public
class IterationLimitException extends LimitException {

	/**
	 * The limit for the maximum number of iterations for mathematical operations
	 * such as fixed point iterations.
	 */
	public static int MaxIterations = 4096;

	public IterationLimitException() {}

	public IterationLimitException(String spec) {
		super(spec);
	}
}
