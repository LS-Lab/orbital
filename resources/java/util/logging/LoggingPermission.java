/*
 * @(#)LoggingPermission.java	1.5 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java.util.logging;

import java.security.*;

/**
 * The permission which the SecurityManager will check when code
 * that is running with a SecurityManager calls one of the logging
 * control methods (such as Logger.setLevel).
 * <p>
 * Currently there is only one named LoggingPermission.  This is "control"
 * and it grants the ability to control the logging configuration, for
 * example by adding or removing Handlers, by adding or removing Filters,
 * or by changing logging levels.
 * <p>
 * Programmers do not normally create LoggingPermission objects directly.
 * Instead they are created by the security policy code based on reading
 * the security policy file.
 *
 *
 * @version 1.5, 12/03/01
 * @since 1.4
 * @see java.security.BasicPermission
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.SecurityManager
 *
 */

public final class LoggingPermission extends java.security.BasicPermission {

    /**
     * Creates a new LoggingPermission object. This constructor exists for
     * use by the Policy object to instantiate new Permission objects.
     *
     * @param name Permission name.  Must be "control".
     * @param actions Must be either null or the empty string.
     * @throws IllegalArgumentException if arguments are invalid
     */
    public LoggingPermission(String name, String actions) throws IllegalArgumentException {
        super(name);
	if (!name.equals("control")) {
	    throw new IllegalArgumentException("name: " + name);
	}
	if (actions != null && actions.length() > 0) {
	    throw new IllegalArgumentException("actions: " + actions);
	}
    }
}
