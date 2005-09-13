/**
 * @(#)TestCase.java 1.1 2002-09-14 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package check;

/**
 * TestCase.java
 *
 *
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version $Id$
 */
public abstract class TestCase extends junit.framework.TestCase {
    public TestCase() {
        
    }
    public TestCase(String name) {
        super(name);
    }

    public static void assertTrue(boolean condition, String message) {
        junit.framework.TestCase.assertTrue(message, condition);
    }
}// TestCase
