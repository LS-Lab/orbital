/**
 * @(#)ModalLogicTest.java 1.1 2002-11-24 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import junit.framework.*;
import java.io.*;

/**
 * A sample test case, testing ModalLogic.
 * @version 1.1, 2002-11-24
 */
public class ModalLogicTest extends check.TestCase {

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(ModalLogicTest.class);
    }
    protected void setUp() {
    }

    protected void test(String name, boolean allTrue) {
	try {
	    ModalLogic logic = new ModalLogic();
	    ModalLogic.proveAll(new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/" + name)), logic, allTrue);
	}
	catch (Throwable ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage() + " in file " + name);
	}
    }
    public void testEquivalences() {
	test("modal-equivalence.txt", true);
    }
    public void testGarbage() {
	test("modal-garbage.txt", false);
    }
}
