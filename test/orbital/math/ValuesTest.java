/**
 * @(#)ValuesTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import junit.framework.*;

/**
 * A sample test case, testing Values.
 * @version 1.1, 2002-09-14
 */
public class ValuesTest extends check.TestCase {
    private Values vf;
    private Arithmetic a[];
    private Arithmetic b[];

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(ValuesTest.class);
    }
    protected void setUp() {
	vf = Values.getDefaultInstance();
	a = new Arithmetic[] {
	    vf.valueOf(5), vf.rational(1, 4), vf.valueOf(1.23456789), vf.complex(-1, 2)
	};
	b = new Arithmetic[] {
	    vf.valueOf(7), vf.rational(3, 4), vf.valueOf(3.1415926), vf.complex(3, 2)
	};
    }

    public void testInteroperableTypeAdd() {
	for (int k = 0; k < a.length; k++) {
	    System.out.println(a[k].getClass() + " arithmetic combined with various types");
	    for (int i = 0; i < b.length; i++)
		System.out.println(a[k] + "+" + b[i] + " = " + a[k].add(b[i]) + "\tof " + a[k].add(b[i]).getClass());
	    Object x1, x2;
	    assertTrue( (x1 = a[k].add(b[0])).equals(x2 = a[k].add((Integer) b[0])) , "compile-time sub-type result equals run-time sub-type result");
	    assertTrue( x1.getClass() == x2.getClass(), "compile-time sub-type result equals run-time sub-type");
	    assertTrue( (x1 = a[k].add(b[1])).equals(x2 = a[k].add((Rational) b[1])) , "compile-time sub-type result equals run-time sub-type result");
	    assertTrue( x1.getClass() == x2.getClass(), "compile-time sub-type result equals run-time sub-type");
	    assertTrue( (x1 = a[k].add(b[2])).equals(x2 = a[k].add((Real) b[2])) , "compile-time sub-type result equals run-time sub-type result");
	    assertTrue( x1.getClass() == x2.getClass(), "compile-time sub-type result equals run-time sub-type");
	    assertTrue( (x1 = a[k].add(b[3])).equals(x2 = a[k].add((Complex) b[3])) , "compile-time sub-type result equals run-time sub-type result");
	    assertTrue( x1.getClass() == x2.getClass(), "compile-time sub-type result equals run-time sub-type");
	}
    }
}
