/**
 * @(#)PackageUtilities.java 1.1 2002-08-30 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.functor;


/**
 *
 * @version 1.1, 2002-08-30
 * @author  Andr&eacute; Platzer
 */
final class PackageUtilities {
    static final Object toTruth(boolean b) {
	return b ? Boolean.TRUE : Boolean.FALSE;
    } 
    
    // truth-value of a value
    static final boolean getTruth(Object v) {
	return ((Boolean) v).booleanValue();
    } 
}
