/**
 * @(#)FactoryConfigurationError.java 1.1 2002-12-06 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.type;

/**
 * Thrown when a problem with configuration of the factories exists.
 * This error will typically be thrown when the class of a {@link TypeSystem} specified
 * in the <a href="Types.html#SystemProperties">system properties</a>
 * cannot be found or instantiated. 
 *
 * @version $Id$
 * @author <a href="">Andr&eacute; Platzer</a>
 */
public class FactoryConfigurationError extends Error {
    public FactoryConfigurationError(String message, Throwable cause) {
        super(message + " due to " + cause);
        ////initCause(cause);   //@internal @version 1.4
    }
    
}// FactoryConfigurationError
