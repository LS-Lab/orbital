/**
 * @(#)ClauseTest.java 1.1 2004-09-16 Andre Platzer
 * 
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import junit.framework.*;
import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.moon.logic.ClassicalLogic;

/**
 * A sample test case, testing Clauses and ClausalSets.
 * @version $Id$
 */
public class ClauseTest extends check.TestCase {
    private Logic logic = null;
    private ClausalFactory factory = null;

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(ClauseTest.class);
    }
    protected void setUp() {
	logic = new ClassicalLogic();
	factory = new DefaultClausalFactory();
    }

    public void testClausalConversionEquivalence() {
	testFormula("a&b|~c&a");
	testFormula("a&(a&b&~a)|c&a");
	testFormula("a&(a&b|~a)|c&a");
	testFormula("a&(a&b|~d)|c&a");
	testFormula("a&(a|b|~d)|c&a|e&a");
    }

    protected void testFormula(String formula) {
	try {
	    Formula f = (Formula) logic.createExpression(formula);
	    ClausalSet s = factory.asClausalSet(f);
	    Formula g = s.toFormula();
	    assertTrue(logic.inference().infer(new Formula[] {f}, g),
		       "formula->clausal->formula conversion remains equivalent for " + formula + "\n  (=>) " + f + "\t|=\t" + g + "\nthe latter is in CNF " + s);
	    assertTrue(logic.inference().infer(new Formula[] {g}, f),
		       "formula->clausal->formula conversion remains equivalent for " + formula + "\n  (<=) " + g + "\t|=\t" + f + "\nthe former is in CNF " + s);
	}
	catch (ParseException ex) {
	    fail("failed parsing " + formula + " due to " + ex);
	}
    }
}
