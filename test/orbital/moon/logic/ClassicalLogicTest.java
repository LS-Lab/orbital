/**
 * @(#)ClassicalLogicTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import junit.framework.*;

/**
 * A sample test case, testing ClassicalLogic.
 * @version 1.1, 2002-09-14
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

    public void testSemanticInferenceEquivalences() {
	try {
	    ClassicalLogic.main(new String[] {"-normalForm", "all"});
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	}
    }
    public void testSemanticInferenceGarbage() {
	try {
	    ClassicalLogic.main(new String[] {"-normalForm", "none"});
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	}
    }
    public void testSemanticInferenceProperties() {
	try {
	    ClassicalLogic.main(new String[] {"-normalForm", "properties"});
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	}
    }

    public void testResolutionEquivalences() {
	try {
	    ClassicalLogic.main(new String[] {"-resolution", "all"});
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	}
    }
    public void testResolutionGarbage() {
	try {
	    ClassicalLogic.main(new String[] {"-resolution", "none"});
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	}
    }
    public void testResolutionProperties() {
	try {
	    ClassicalLogic.main(new String[] {"-resolution", "properties"});
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	}
    }
    public void testResolutionFol() {
	try {
	    ClassicalLogic.main(new String[] {"-resolution", "fol"});
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	}
    }
}
