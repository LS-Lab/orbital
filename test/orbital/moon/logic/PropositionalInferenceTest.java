/**
 * @(#)PropositionalInferenceTest.java 1.1 2002-11-29 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import junit.framework.*;

/**
 * A sample test case, testing PropositionalInference.
 * @version 1.1, 2002-11-29
 */
public class PropositionalInferenceTest extends ClassicalLogicTest {

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(PropositionalInferenceTest.class);
    }
    protected void setUp() {
    }

    protected void test(String name) {
	try {
	    ClassicalLogic.main(new String[] {"-inference=PROPOSITIONAL_INFERENCE", name});
	}
	catch (Throwable ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage() + " in file " + name);
	}
    }
}
