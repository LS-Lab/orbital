/** $Id$
 * @(#)$RCSfile$ 1.1 2003-01-25 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.type;

/**
 * Thrown whenever an exception due to invalid typing occurs.
 *
 *
 * @author <a href="mailto:NOSPAM@functologic.com">Andr&eacute; Platzer</a>
 * @version 1.1, 2003-01-25
 * @version-revision $Revision$, $Date$
 */
public class TypeException extends RuntimeException {
    public TypeException() {
	
    }
    
    public TypeException(String message) {
	super(message);
    }

    public TypeException(String message, Type required, Type found) {
	super(message);
    }
    
    /**
     * Get the type that would have been required.
     */
    //@todo introduce public Type getRequiredType();

    /**
     * Get the type that was found.
     */
    //@todo introduce public Type get....Type();
}// TypeException
