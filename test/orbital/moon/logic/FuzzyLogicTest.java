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

	// test some fuzzy logic axioms (neutral, commutative,...) for formula

	// some fuzzy and operator axioms
	formula = "(" + formula + ")";
	// neutral
	assertEquiv(formula + "&1", formula);
	//
	assertEquiv(formula + "&0", "0");
	// boundary conditions
	assertEquiv("0&0", "0");
	assertEquiv("1&0", "0");
	assertEquiv("0&1", "0");
	assertEquiv("1&1", "1");

	// some fuzzy or operator axioms
	// neutral
	assertEquiv(formula + "|0", formula);
	//
	assertEquiv(formula + "|1", "1");
	// boundary conditions
	assertEquiv("0|0", "0");
	assertEquiv("1|0", "1");
	assertEquiv("0|1", "1");
	assertEquiv("1|1", "1");

	// some fuzzy not operator axioms
	// boundary conditions
	assertEquiv("~1", "0");
	assertEquiv("~0", "1");
    }
    protected void assertEquiv(String formula1, String formula2) {
	try {
	    Formula f = (Formula) logic.createExpression(formula1);
	    Formula f2 = (Formula) logic.createExpression(formula2);
	    Interpretation I = new InterpretationBase(f.getSignature().union(f2.getSignature()), Collections.EMPTY_MAP);
	    assertTrue(f.apply(I).equals(f2.apply(I)) , formula1 + " == " + formula2 + " interpreted to " + f.apply(I) + " == " + f2.apply(I) + " of " + f.apply(I).getClass() + " resp. " + f2.apply(I).getClass());
	}
	catch (Throwable ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage() + " in " + formula1 + " == " + formula2) /*.initCause(ex)*/;
	}
    }
    protected void assertSat(String formula, boolean satisfied) {
	String satDesc = (satisfied ? " should be " : " should not be ") + "satisfied";
	try {
	    Formula f = (Formula) logic.createExpression(formula);
	    Interpretation I = new InterpretationBase(f.getSignature(), Collections.EMPTY_MAP);
	    assertTrue(logic.satisfy(I, f) == satisfied , formula + satDesc + "\n\t(in " + logic + " interpreted to " + f.apply(I) +")");
	}
	catch (Throwable ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage() + " in " + formula + satDesc) /*.initCause(ex)*/;
	}
    }
    public void testGoedel() {
	logic = new FuzzyLogic();
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("0.5|0.5", false);
	test("~0.005|0.995", false);
	test("0.9995|1", true);
    }
    public void testProduct() {
	logic = new FuzzyLogic(FuzzyLogic.PRODUCT);
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("0.5|0.5", false);
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
	test("0.5|0.5", true);
	test("0.1|0.1", true);
	test("~(0.995&0.995)", true);
	test("~0.995|0.001", true);
    }
    public void testHamacher() {
	logic = new FuzzyLogic(FuzzyLogic.HAMACHER(1));
	test("0.5&~0.2", false);
	test("0.5|0.5", false);

	logic = new FuzzyLogic(FuzzyLogic.HAMACHER(2));
	test("0.5&~0.2", false);
	test("0.5|0.5", false);

	logic = new FuzzyLogic(FuzzyLogic.HAMACHER(0.1));
	test("0.5&~0.2", false);
	test("0.5|0.5", false);

	logic = new FuzzyLogic(FuzzyLogic.HAMACHER(0));
	test("0.5&~0.2", false);
	test("0.5|0.5", false);
    }
    public void testYager() {
	logic = new FuzzyLogic(FuzzyLogic.YAGER(1));
	test("0.5&~0.2", false);
	test("0.5|0.5", true);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(2));
	test("0.5&~0.2", false);
	test("0.5|0.5", false);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(3));
	test("0.5&~0.2", false);
	test("0.5|0.5", false);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(0.5));
	test("0.5&~0.2", false);
	test("0.5|0.5", true);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(0.1));
	test("0.5&~0.2", false);
	test("0.5|0.5", true);
	test("~0.005|0.995", true);

	// approaching Goedel
	logic = new FuzzyLogic(FuzzyLogic.YAGER(1e1));
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("0.5|0.5", false);
	test("~0.005|0.995", true);
	test("0.9995|1", true);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(1e2));
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("0.5|0.5", false);
	test("~0.005|0.995", true);
	test("0.9995|1", true);

	logic = new FuzzyLogic(FuzzyLogic.YAGER(1e3));
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("0.5|0.5", false);
	test("~0.005|0.995", false);
	test("0.9995|1", true);

	//	logic = new FuzzyLogic(FuzzyLogic.YAGER(1e4));
	test("0.5&~0.2", false);
	test("0.5&~(0.2|0.1)|0.4", false);
	test("0.5|0.5", false);
	test("~0.005|0.995", false);
	test("0.9995|1", true);

	//@xxx note that indeterminate 0^0 occurs, here. But Java does not worry about it.
	logic = new FuzzyLogic(FuzzyLogic.YAGER(Double.POSITIVE_INFINITY));
	test("0.5&~0.2", false);
	//test("0.5&~(0.2|0.1)|0.4", false);
	//test("0.5|0.5", false);
	test("~0.005|0.995", true);
	test("0.9995|1", true);
    }
}
