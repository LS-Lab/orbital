/**
 * @(#)MathExpressionTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.math.*;
import orbital.math.functional.*;
import orbital.logic.sign.ParseException;
import junit.framework.*;


/**
 * A sample test case, testing MathExpression.
 * @version 1.1, 2002-09-14
 */
public class MathExpressionTest extends check.TestCase {
    private Values vf;
    private Real tolerance;

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(MathExpressionTest.class);
    }
    protected void setUp() {
	vf = Values.getDefaultInstance();
	tolerance = vf.valueOf(0.01);
    }

    protected Arithmetic test(String expr) {
	try {
	    System.out.println("Original expression:\t" + expr);
	    System.out.print("Parsed function:\t"); System.out.flush();
	    MathExpressionSyntax syntax = new MathExpressionSyntax();
	    Arithmetic p = new MathExpressionSyntax().createMathExpression(expr);
	    System.out.println(p);
	    System.out.print("Evaluates to:\t"); System.out.flush();
	    System.out.println((p instanceof Function ? ((Function) p).apply(null) : p));	//@XXX: erm why null?
	    return p;
	}
	catch (Throwable ex) {
	    System.out.println();
	    ex.printStackTrace();
	    fail(ex.getMessage() + " in expression: " + expr);
	    return null;
	}
    }
    protected void parsable(String expr, boolean expectparsable) {
	final String desc = expr + " " + (expectparsable ? "is" : "is not") + " parsable";
	try {
	    System.out.println("is this a parsable expression:\t" + expr);
	    MathExpressionSyntax syntax = new MathExpressionSyntax();
	    Arithmetic p = new MathExpressionSyntax().createMathExpression(expr);
	    assertTrue(true == expectparsable , desc);
	    return;
	}
	catch (ParseException fallthrough) {
	    System.out.println(fallthrough);
	}
	catch (IllegalArgumentException fallthrough) {
	    System.out.println(fallthrough);
	}
	assertTrue(false == expectparsable , desc);
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
	compare("(4+1.2)*(3-(1-3)^2^2)^3/(5/(8*1/4))-sin(cos(2/5)*10)",
		null,
		vf.valueOf(-4569.97)
		);
	parsable("1-+--3", true);
    }
    public void DISABLEDtestVariables() {
	compare("1+3*2*(5-x/8)+sin(2*x-4)*3",
		null,
		null
		);
	compare("5*1+3*2*(5-x/8)+sin(3+1-2*x-4)*3",
		null,
		test("5+6*(5-x/8)+sin(4-2*x-4)*3")
		);
    }
    public void testNonExpressions() {
	parsable("1+", false);
	parsable("1+3*/5", false);
	parsable("1+(2*8-4", false);
	parsable("1+3*(1-2))", false);
	parsable("+5*-+3/unknown(x)", false);
	parsable("unknown(4)", false);
	parsable("/5", false);
	parsable("(7>5)+1", false);
	parsable("(7>5)<2", false);
	parsable(".2", false);
    }

    private void compare(String expr, Object parsed, Object evaluated) {
	Arithmetic x = test(expr);
	assertTrue( x != null , expr + " parsable");
	assertTrue( parsed == null || x.equals(parsed) , x + " = " + parsed);
	Object result = x instanceof Function ? ((Function)x).apply(null) : null;
	if (result instanceof Arithmetic)
	    assertTrue( evaluated == null || result != null && ((Arithmetic)result).equals(evaluated, tolerance) , x + " evaluates to " + (x instanceof Function ? result : "<non function>")  + " but should evaluate to " + evaluated);
	else
	    assertTrue( evaluated == null || result != null && result.equals(evaluated) , x + " evaluates to " + (x instanceof Function ? result : "<non function>")  + " but should evaluate to " + evaluated);
    }
}
