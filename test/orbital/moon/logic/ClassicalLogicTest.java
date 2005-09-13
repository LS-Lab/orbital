/**
 * @(#)ClassicalLogicTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import junit.framework.*;

/**
 * A sample test case, testing ClassicalLogic.
 * @version $Id$
 */
public class ClassicalLogicTest extends check.TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(ClassicalLogicTest.class);
    }
    protected void setUp() {
    }

    protected void test(String name) {
        try {
            ClassicalLogic.main(new String[] {name});
        }
        catch (Throwable ex) {
            ex.printStackTrace();
            fail(ex.getMessage() + " in file " + name);
        }
    }
    public void testEquivalences() {
        test("all");
    }
    public void testGarbage() {
        test("none");
    }
    public void testProperties() {
        test("properties");
    }
}
