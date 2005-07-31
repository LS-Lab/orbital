/**
 * @(#)IncomparableException.java 1.1 2002-09-11 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import orbital.math.MathUtilities;

/**
 * Thrown to indicate that two particular elements of a partial order are incomparable.
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @see Comparable
 * @see java.util.Comparator
 */
public class IncomparableException extends IllegalArgumentException {
    private final Object incomparableObjects[];
    public IncomparableException() {
	this.incomparableObjects = null;
    }

    public IncomparableException(Object incomparableObjects[]) {
	this.incomparableObjects = (Object[])incomparableObjects.clone();
    }

    public IncomparableException(String mesg) {
	super(mesg);
	this.incomparableObjects = null;
    }

    public IncomparableException(String mesg, Object incomparableObjects[]) {
	super(mesg + ": " + MathUtilities.format(incomparableObjects));
	this.incomparableObjects = (Object[])incomparableObjects.clone();
    }

    public IncomparableException(String mesg, Object incomparableObject1, Object incomparableObject2) {
	this(mesg, new Object[] {incomparableObject1, incomparableObject2});
    }
    
    /**
     * Get the objects which are incomparable, but have been attempted
     * to compare.
     */
    public Object[] getIncomparableObjects() {
	return incomparableObjects != null
	    ? (Object[])incomparableObjects.clone()
	    : null;
    }
}// IncomparableException
