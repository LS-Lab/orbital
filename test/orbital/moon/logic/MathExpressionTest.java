/**
 * @(#)MathExpressionTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.math.*;
import orbital.math.functional.*;
import junit.framework.*;


/**
 * A sample test case, testing MathExpression.
 * @version 1.1, 2002-09-14
 */
public class MathExpressionTest extends check.TestCase {
    private Values vf;

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(MathExpressionTest.class);
    }
    protected void setUp() {
	vf = Values.getDefaultInstance();
    }

    protected Arithmetic test(String expr) {
	try {
	    System.out.println("Original expression:\t" + expr);
	    MathExpressionSyntax syntax = new MathExpressionSyntax();
	    Arithmetic p = new MathExpressionSyntax().createMathExpression(expr);
	    System.out.println("Parsed function:\t" + p);
	    System.out.println("Evaluates to:\t" + (p instanceof Function ? ((Function) p).apply(null) : p));	//@XXX: erm why null?
	    return p;
	}
	catch (Throwable ex) {
	    fail(ex.getMessage());
	    return null;
	}
    }

    public void testSimple() {
	compare("2+4",
		Operations.plus.apply(Functions.constant(vf.valueOf(2)),vf.valueOf(4)),
		Operations.plus.apply(vf.valueOf(2),vf.valueOf(4))
		);
	compare("2+4",
		Operations.plus.apply(Functions.constant(vf.valueOf(2)),vf.valueOf(4)),
		vf.valueOf(6)
		);
	compare("2+3*7",
		Operations.plus.apply(vf.valueOf(2), Operations.times.apply(Functions.constant(vf.valueOf(3)),vf.valueOf(7))),
		vf.valueOf(23)
		);
	compare("2+3*7/8",
		null,
		vf.rational(37,8)
		);
	compare("8+3*sin(3.1415926/(1+1))",
		null,
		vf.valueOf(11)
		);
    }

    private void compare(String expr, Object parsed, Object evaluated) {
	Arithmetic x = test(expr);
	assertTrue( x != null , expr + " parsable");
	assertTrue( parsed == null || x.equals(parsed) , x + " = " + parsed);
	assertTrue( evaluated == null || (x instanceof Function && ((Function)x).apply(null).equals(evaluated)) , x + " evaluates to " + evaluated);
    }
}
