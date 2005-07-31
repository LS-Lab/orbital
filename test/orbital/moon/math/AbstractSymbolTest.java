/**
 * @(#)AbstractSymbolTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import junit.framework.*;

import orbital.moon.logic.MathExpressionSyntax;
import orbital.logic.sign.ParseException;

/**
 * A sample test case, testing Values.
 * @version $Id$
 */
public class AbstractSymbolTest extends check.TestCase {
    private Values vf;
    private MathExpressionSyntax syntax;

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    protected void setUp() {
	vf = Values.getDefaultInstance();
	// for parsing
	syntax = new MathExpressionSyntax();
    }
    public static Test suite() {
	return new TestSuite(AbstractSymbolTest.class);
    }

    public void testMatrixWithSymbols() {
	Matrix M = vf.valueOf(new Arithmetic[][] {
	    {vf.symbol("a"), vf.symbol("b")},
	    {vf.symbol("c"), vf.symbol("d")}
	});
	Vector v = vf.valueOf(new Arithmetic[] {
	    vf.valueOf(1), vf.valueOf(2)
	});
	
	assertTrue( ValuesImpl.symbolic.apply(M));
	assertTrue(!ValuesImpl.symbolic.apply(v));
	assertTrue(!ValuesImpl.symbolic.apply(vf.IDENTITY(7,7)));
        assertTrue( ValuesImpl.symbolic.apply(M.multiply(v)));

	try {
	    System.out.println(M + "*" + v + "=" + M.multiply(v));
	    assertEquals(M.multiply(v), vf.valueOf(new Arithmetic[] {
		syntax.createMathExpression("a+b*2"),
		syntax.createMathExpression("c+d*2")
	    }));

	    System.out.println(M + "^-1 =\n" + M.inverse());

	    M = vf.valueOf(new Arithmetic[][] {
		{vf.valueOf(2), vf.symbol("a")},
		{vf.symbol("d"), vf.valueOf(4)}
	    });
	    v = vf.valueOf(new Arithmetic[] {
		vf.valueOf(1), vf.valueOf(2)
	    });
	    System.out.println(M + "*" + v + "=" + M.multiply(v));
	    assertEquals(M.multiply(v), vf.valueOf(new Arithmetic[] {
		syntax.createMathExpression("2+a*2"),
		syntax.createMathExpression("d+8")
	    }));
	} catch (ParseException ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	} catch (IllegalArgumentException ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    } 

}
