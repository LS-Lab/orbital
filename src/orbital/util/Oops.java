/**
 * @(#)Oops.java 0.9 2000/08/30 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * Oops is another funny error that should not be thrown.
 * Except perhaps for very funny error conditions.
 * 
 * @version 0.9, 2000/08/30
 * @author  Andr&eacute; Platzer
 * @see OutOfCheeseError
 * @see GeneralComplexionException
 */
public class Oops extends UnknownError {
    public Oops(String message) {
	super(message);
    }

    public Oops() {
	super("Oops!");
    }
}
