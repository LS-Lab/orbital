/**
 * @(#)OutOfCheeseError.java 0.9 1999/09/10 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * This class is a OutOfCheeseError thrown like an UnknownError whenever really dull garbage happens
 * which cannot be categorized further.<p>
 * This particular error should raise if the whole logical construction is rolling and tumbling.
 * 
 * @version 0.9, 1999/09/10
 * @author  Andr&eacute; Platzer
 * @see GeneralComplexionException
 */
public
class OutOfCheeseError extends UnknownError {
	public OutOfCheeseError(String message) {
		super(message);
	}

	public OutOfCheeseError() {
		super("Don't mention it. ++?????++ Out of Cheese Error. Redo From Start.");
	}
}
