/**
 * @(#)IncompleteCalculusException.java 1.0 1999/03/04 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

/**
 * A LogicException thrown when an incomplete calculus is used.
 * Especially thrown to indicate that an operation has been requested that could not
 * be performed due to the calculus used being incomplete.
 * 
 * @version 1.0, 1999/03/04
 * @author  Andr&eacute; Platzer
 */
public class IncompleteCalculusException extends LogicException {
    private static final long serialVersionUID = 548457840486427483L;

    public IncompleteCalculusException(String message) {
	super(message);
    }
    public IncompleteCalculusException() {}
}
