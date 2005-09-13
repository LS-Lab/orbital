/** $Id$
 * @(#)$RCSfile$ 1.1 2003-06-28 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * Thrown if an object expected to be {@link
 * Utility#isIteratable(Object) generalized iteratable} is not.
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @version-revision $Revision$, $Date$
 */
public class NotIteratableException extends ClassCastException {
    public NotIteratableException() {}
    
    public NotIteratableException(String s) {
        super(s);
    }

    public NotIteratableException(String s, Class found, Object expected) {
        super(s + " found: " + found + " expected: " + expected);
    }
    public NotIteratableException(Class found, Object expected) {
        this("", found, expected);
    }
}// NotIteratableException
