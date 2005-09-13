/**
 * @(#)RecursionLimitException.java 0.9 2000/06/17 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * Thrown when the limit for the maximum number of recursions is overrun.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class RecursionLimitException extends LimitException {
    private static final long serialVersionUID = 634853460730351591L;
    /**
     * The limit for the maximum number of recursions for mathematical operations
     * such as fixed point recursions.
     */
    public static int MaxRecursions = 4096;

    public RecursionLimitException() {}

    public RecursionLimitException(String spec) {
        super(spec);
    }
}
