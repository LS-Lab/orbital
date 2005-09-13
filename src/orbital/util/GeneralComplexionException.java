/**
 * @(#)GeneralComplexionException.java 0.9 1999/12/18 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * This Error is thrown like an UnknownError whenever really big nonsense happens
 * which cannot be categorized any further.
 * This particular error should raise if the whole thing is getting too complex.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see OutOfCheeseError
 */
public class GeneralComplexionException extends UnknownError {
    public GeneralComplexionException(String message) {
        super(message);
    }

    public GeneralComplexionException() {
        super("General Complexion Exception");
    }
}
