/**
 * @(#)LambdaTest.java 1.1 2002-11-24 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import junit.framework.*;
import java.io.*;

/**
 * A sample test case, testing ClassicalLogic.
 * @version $Id$
 */
public class LambdaTest extends check.TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(LambdaTest.class);
    }
    protected void setUp() {
    }

    protected void test(String name, boolean allTrue) {
        try {
            ClassicalLogic logic = new ClassicalLogic();
            ClassicalLogic.proveAll(new InputStreamReader(this.getClass().getResourceAsStream(name),
                                                          ClassicalLogic.DEFAULT_CHARSET),
                                    logic, allTrue);
        }
        catch (Throwable ex) {
            ex.printStackTrace();
            fail(ex.getMessage() + " in file " + name);
        }
    }
    public void testEquivalences() {
        test("lambda-equivalence.txt", true);
    }
    public void testGarbage() {
        test("lambda-garbage.txt", false);
    }
}


