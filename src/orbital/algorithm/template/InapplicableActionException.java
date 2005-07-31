/**
 * @(#)InapplicableActionException.java 1.0 2002/06/18 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

/**
 * Thrown to indicate that an action was not applicable in the state.
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 */
public class InapplicableActionException extends IllegalArgumentException {
    private static final long serialVersionUID = 4275365251635332886L;
    
    public InapplicableActionException() {}
    
    public InapplicableActionException(String s) {
	super(s);
    }
}// InapplicableActionException
