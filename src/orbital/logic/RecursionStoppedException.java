/*
 * @(#)RecursionStoppedException.java 0.9 1998/12/20 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * An Exception thrown if a recursive algorithm is explicitly stopped
 * during recursion.
 * 
 * @version 0.9, 20/12/98
 * @author  Andr&eacute; Platzer
 */
public class RecursionStoppedException extends RuntimeException {
    private static final long serialVersionUID = -4005462153245344729L;
    public RecursionStoppedException() {}

    public RecursionStoppedException(String spec) {
	super(spec);
    }
}
