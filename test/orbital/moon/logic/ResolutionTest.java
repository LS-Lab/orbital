/**
 * @(#)ResolutionTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import junit.framework.*;

/**
 * A sample test case, testing Resolution.
 * @version 1.1, 2002-09-14
 */
public class ResolutionTest extends ClassicalLogicTest {

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(ResolutionTest.class);
    }
    protected void setUp() {
    }

    protected void test(String name) {
	try {
	    ClassicalLogic.main(new String[] {"-resolution", name});
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
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
    public void testFol() {
	test("fol");
    }
}
