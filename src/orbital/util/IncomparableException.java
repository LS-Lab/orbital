/**
 * @(#)IncomparableException.java 1.1 2002-09-11 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * Thrown to indicate that two particular elements of a partial order are incomparable.
 *
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version 1.1, 2002-09-11
 * @see Comparable
 * @see java.util.Comparator
 */
public class IncomparableException extends IllegalArgumentException {
    public IncomparableException() {
	
    }
    
    public IncomparableException(String mesg) {
	super(mesg);
    }
}// IncomparableException
