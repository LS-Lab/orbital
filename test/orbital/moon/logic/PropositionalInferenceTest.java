/**
 * @(#)PropositionalInferenceTest.java 1.1 2002-11-29 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import junit.framework.*;

/**
 * A sample test case, testing PropositionalInference.
 * @version $Id$
 * @attribute run-time 5min assuming java -da
 * @attribute run-time 6h 10min assuming java -ea
 */
public class PropositionalInferenceTest extends ClassicalLogicTest {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(PropositionalInferenceTest.class);
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
