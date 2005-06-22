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
 * @author Andr&eacute; Platzer
 * @version 1.1, 2003-01-25
 * @version-revision $Revision$, $Date$
 */
public class TypeException extends RuntimeException {
    /**
     * The type that would have been required.
     * @serial
     */
    private final Type required;

    /**
     * The type that was actually found at the type occurrence.
     * @serial
     */
    private final Type occurred;

    public TypeException() {
	this.required = null;
	this.occurred = null;
    }
    
    public TypeException(String message) {
	this(message, null, null);
    }

    public TypeException(String message, Type required, Type occurred) {
	super(message);
	this.required = required;
	this.occurred = occurred;
    }
    
    /**
     * Get the type that would have been required.
     */
    public Type getRequired() {
	return required;
    }

    /**
     * Get the type that was actually found.
     */
    public Type getOccurred() {
	return occurred;
    }
}// TypeException
