/**
 * @(#)FuzzyLogicTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;
import junit.framework.*;
import java.util.*;

/**
 * A sample test case, testing FuzzyLogic.
 * @version 1.1, 2002-09-14
 */
public class FuzzyLogicTest extends check.TestCase {
    private FuzzyLogic logic;
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(FuzzyLogicTest.class);
    }
    protected void setUp() {
    }

    protected void test(String formula, boolean satisfied) {
	assertSat(formula, satisfied);
	//@todo test fuzzy logic axioms (neutral, commutative,...) for formula
    }
    protected void assertSat(String formula, boolean satisfied) {
	try {
	    Formula f = (Formula) logic.createExpression(formula);
	    Interpretation I = new InterpretationBase(f.getSignature(), Collections.EMPTY_MAP);
	    assertTrue(logic.satisfy(I, f) == satisfied , formula + (satisfied ? " should be " : " should not be ") + "satisfied\n\t(in " + logic + " interpreted to " + f.apply(I) +")");
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	}
    }
    public void testGoedel() {
	logic = new FuzzyLogic();
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("~0.005|0.995", false);
	test("0.9995|1", true);
    }
    public void testProduct() {
	logic = new FuzzyLogic(FuzzyLogic.PRODUCT);
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("~0.005|0.995", true);
    }
    public void testBounded() {
	logic = new FuzzyLogic(FuzzyLogic.BOUNDED);
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("0.5|0.5", true);
	test("~0.005|0.995", true);
	test("~0.006|0.995", true);
    }
    public void testDrastic() {
	logic = new FuzzyLogic(FuzzyLogic.DRASTIC);
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("~(0.995&0.995)", true);
	test("~0.995|0.001", true);
    }
    public void testHamacher() {
	logic = new FuzzyLogic(FuzzyLogic.HAMACHER(1));
	test("0.5&~0.2", false);

	logic = new FuzzyLogic(FuzzyLogic.HAMACHER(2));
	test("0.5&~0.2", false);

	logic = new FuzzyLogic(FuzzyLogic.HAMACHER(0.1));
	test("0.5&~0.2", false);

	logic = new FuzzyLogic(FuzzyLogic.HAMACHER(0));
	test("0.5&~0.2", false);
    }
    public void testYager() {
	logic = new FuzzyLogic(FuzzyLogic.YAGER(1));
	test("0.5&~0.2", false);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(2));
	test("0.5&~0.2", false);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(0.1));
	test("0.5&~0.2", false);

	// approaching Goedel
	logic = new FuzzyLogic(FuzzyLogic.YAGER(1e4));
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("~0.005|0.995", false);
	test("0.9995|1", true);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(Double.POSITIVE_INFINITY));
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("~0.005|0.995", false);
	test("0.9995|1", true);
    }
}
