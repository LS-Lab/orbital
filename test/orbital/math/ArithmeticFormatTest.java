/**
 * @(#)ArithmeticFormatTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import junit.framework.*;

import java.text.ParseException;

/**
 * A sample test case, testing.
 * @version 0.9, 1998-03-04
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
		    Arithmetic value = format.parse(test[i][j]);
		    System.out.println(test[i][j] + "\t== " + value + ",");
		    assertTrue( format.parse(value.toString()).equals(value) , "re-parse of representation of an object should equal that object " + test[i][j]);
		}
		catch (ParseException ex) {
		    fail(ex.getMessage());
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
	    Arithmetic s = format.parse(n);
	    System.out.println("Parsed\n"+s);
	    System.out.println("Understood?");
	    assert format.parse(s.toString()).equals(s) : "re-parse of representation of an object should equal that object " + n;
	}
	catch (ParseException ex) {
	    fail(ex.getMessage());
	}
    }
}
