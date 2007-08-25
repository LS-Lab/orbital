/**
 * @(#)ArithmeticFormatTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import junit.framework.*;

import java.text.ParseException;
//import java.text.NumberFormatException;

/**
 * Tests formatting and parsing of arithmetic quantities.
 * @version $Id$
 */
public class ArithmeticFormatTest extends check.TestCase {
    private Values vf;
    private ArithmeticFormat format;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(ArithmeticFormatTest.class);
    }
    protected void setUp() {
        vf = Values.getDefaultInstance();
        ArithmeticFormat.getDefaultInstance().getNumberFormat().setMaximumFractionDigits(8);
        format = ArithmeticFormat.getDefaultInstance();
    }
    
    public void testFormatComplex() {
        Complex values[] = {vf.complex(1,0), vf.complex(-1,0), vf.complex(0,1), vf.complex(0,-1), vf.complex(1,1), vf.complex(1,-1), vf.complex(-1,1), vf.complex(-1,-1),
                            vf.complex(2,0), vf.complex(-2,0), vf.complex(0,2), vf.complex(0,-2), vf.complex(2,2), vf.complex(2,-2), vf.complex(-2,2), vf.complex(-2,-2)};
        String expected[] = {"1", "-1", "i", "-i", "1+i", "1-i", "-1+i", "-1-i",
                             "2", "-2", "i*2", "-i*2", "2+i*2", "2-i*2", "-2+i*2", "-2-i*2"};
        for (int i = 0; i < values.length; i++) {
            System.out.print(values[i]);
            assertEquals("format " + values[i] + " as " + expected[i], values[i].toString(), expected[i]);
        }
    }
    public void testParseReparse() {
        String test[][] = new String[][] {
            {"1","-1","i","-i"},
            {"1+i","i+1","1-i","-i+1","-1+i","i-1","-1-i","-i-1"},
            {"2+3*i","2+i*3","3*i+2","i*3+2","2+3i","2+i*3","3i+2","i*3+2"},
            {"2-3*i","2-i*3","-3*i+2","-i*3+2","2-3i","2-i*3","-3i+2","-i*3+2"},
            {"-2+3*i","-2+i*3","3*i-2","i*3-2","-2+3i","-2+i*3","3i-2","i*3-2"},
            {"-2-3*i","-2-i*3","-3*i-2","-i*3-2","-2-3i","-2-i*3","-3i-2","-i*3-2"},
            {"2+-3*i","2+i*-3","-3*i+2","i*-3+2","2+-3i","2+i*-3","-3i+2","i*-3+2"}};
        for (int i=0;i<test.length;i++) {
            for (int j=0;j<test[i].length;j++)
                try {
                    Arithmetic value = vf.parse(test[i][j]);
                    System.out.println(test[i][j] + "\t== " + value + ",");
                    assertTrue( vf.parse(value.toString()).equals(value) , "re-parse of representation of an object should equal that object " + test[i][j]);
                }
                catch (NumberFormatException ex) {
                    fail(ex.getMessage() + " in " + test[i][j]);
                }
            System.out.println();
        }
    }
    public void testMatrixParse() {
        Matrix M = vf.valueOf(new Arithmetic[][] {
            {vf.valueOf(2), vf.complex(3, 4), vf.rational(-1, 2)},
            {vf.valueOf(3.12), vf.rational(1, 2), vf.valueOf(-1)},
            {vf.rational(-1, 2), vf.valueOf(0), vf.rational(1)}
        });
        Vector v = vf.valueOf(new Arithmetic[] {
            vf.valueOf(1), vf.rational(-1, 3), vf.rational(1, 2)
        });
        System.out.println(M + "*" + v + "=" + M.multiply(v));
        System.out.println("Type arithmetic examination object to parse (finish with Esc or Ctrl-Z or '#')");
        String n = "[1, 4, -1]\n[2/3, 3/4\t8]\n[3i+2\t1/4,15];[8,  -0,  -1]";
        try {
            System.out.println("From '"+n+"'");
            Arithmetic s = vf.parse(n);
            System.out.println("Parsed\n"+s);
            System.out.println("Understood?");
            assertTrue( vf.parse(s.toString()).equals(s) , "re-parse of representation of an object should equal that object " + n);
        }
        catch (NumberFormatException ex) {
            fail(ex.getMessage() + " in '" + n + "'");
        }
    }

    public void testParsable() {
	assertTrue( vf.ZERO().equals(parsable("0", true)), "parsed 0");
	assertTrue( vf.ZERO().equals(parsable("-0", true)), "parsed -0");
	//assertTrue( vf.ZERO().equals(parsable("+0", true)), "parsed 0");
	assertTrue( vf.ONE().equals(parsable("1", true)), "parsed unit");
	assertTrue( vf.valueOf(5).equals(parsable("5", true)), "parsed integer");
	assertTrue( vf.valueOf(5.2).equals(parsable("5.2", true)), "parsed real");
	assertTrue( vf.rational(2,3).equals(parsable("2/3", true)), "parsed rational");
	assertTrue( vf.I().equals(parsable("i", true)), "parsed complex unit");
	assertTrue( vf.complex(2,3).equals(parsable("2+3i", true)), "parsed complex");
	assertTrue( vf.complex(2,3).equals(parsable("2+3*i", true)), "parsed complex");
	assertTrue( vf.complex(2,3).equals(parsable("2+3i", true)), "parsed complex");
	assertTrue( vf.complex(2,-3).equals(parsable("2-3*i", true)), "parsed complex");
	assertTrue( vf.complex(2,-3).equals(parsable("2-3i", true)), "parsed complex");
	assertTrue( vf.complex(2,-3).equals(parsable("2+i*-3", true)), "parsed complex");
	assertTrue( vf.valueOf(new Arithmetic[] {
	    vf.ONE(), vf.ZERO(), vf.complex(2,3), vf.rational(3,4),vf.ONE().minus()
	    }).equals(parsable("(1,0,2+3*i,3/4,-1)", true)), "parsed vector");
	assertTrue( vf.valueOf(new Arithmetic[][] {
	    {vf.ONE(), vf.ZERO()},
	    {vf.complex(2,3), vf.rational(3,4)},
	    {vf.ONE().minus(), vf.valueOf(2)}
	    }).equals(parsable("[1,0]\n[2+3*i,3/4]\n[-1, 2]", true)), "parsed matrix");
	// we cannot yet parse polynomials
	/*assertTrue( vf.polynomial(new Arithmetic[] {
	    vf.valueOf(-4), vf.valueOf(2), vf.valueOf(3)
	    }).equals(parsable("-4*X^0+2*X^1+3*X^2", true)), "parsed polynomial");*/
	// we cannot yet parse rational complex
	//assertTrue( vf.complex(vf.rational(2,3),vf.rational(3,4)).equals(parsable("2/3+3/4*i", true)), "parsed rational complex");
    }

    public void testNonValues() {
        String test[] = new String[] {
	    "ik", "3ij", "i+i", "3i+2i", "ij", "i+", "3+5", "i-",
	    "3-i-",
	    "(2, 3/5, 2+3i", "[-4,7]\n[2/3,3/4+2/3i,5",
	};
        for (int i=0;i<test.length;i++) {
	    parsable(test[i], false);
        }
    }

    

    protected Arithmetic parsable(String expr, boolean expectparsable) {
        final String desc = expr + " " + (expectparsable ? "is" : "is not") + " parsable";
        try {
            System.out.println("is this a parsable arithmetic value:\t" + expr);
            Arithmetic p = vf.parse(expr);
            assertTrue(true == expectparsable , desc + " result=" + p + "@" + p.getClass());
            return p;
        }
        catch (NumberFormatException e) {
            System.out.println(e);
            assertTrue(false == expectparsable , desc + " " + e);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e);
            assertTrue(false == expectparsable , desc + " " + e);
        }
	return null;
    }
}
