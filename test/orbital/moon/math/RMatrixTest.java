/**
 * @(#)RMatrixTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import junit.framework.*;

/**
 * A sample test case, testing Values.
 * @version $Id$
 */
public class RMatrixTest extends check.TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    protected void setUp() {
    }
    public static Test suite() {
        return new TestSuite(RMatrixTest.class);
    }

    public void testRMatrixAndFallbackDefault() throws Exception {
        testRMatrixAndFallback(Values.getDefaultInstance());
    }
    public void testRMatrixAndFallbackBig() throws Exception {
        testRMatrixAndFallback(new BigValuesImpl());
    }
    public void testRMatrixAndFallbackFast() throws Exception {
        testRMatrixAndFallback(new FastValuesImpl());
    }
    public void testRMatrixAndFallbackVI() throws Exception {
        testRMatrixAndFallback(new ValuesImpl());
    }

    public void testRMatrixAndFallback(ValueFactory valueFactory) {
        final ValueFactory vf = valueFactory;
        Matrix M = new RMatrix(new double[][] {
            {2, 1, 0, -2},
            {1, 2, 4, 1},
            {-2, 1, 2, -2},
            {-3, 0, 1, -4}
        }, vf);
        Vector v = new RVector(new double[] {
            1, 2, 1, 2
        }, vf);
        Vector u = new RVector(new double[] {
            2, 1, 0, -3
        }, vf);
        System.out.println(M + "*" + v + "=" + M.multiply(v));
        assertEquals(M.multiply(v), vf.valueOf(new double[] {
            0, 11, -2, -10
        }));
        System.out.println(u + "*" + v + "=" + u.multiply(v));
        assertEquals(u.multiply(v),vf.valueOf(-2));
        System.out.println(v + "*" + 2 + "=" + v.multiply(vf.valueOf(2)));
        assertEquals(u.multiply(vf.valueOf(2)),vf.valueOf(new double[] {
            4, 2, 0, -6
        }));
        System.out.println("M^-1=" + M.inverse());
        System.out.println("fall-back to arithmetic matrix");
        v = vf.valueOf(new Arithmetic[] {
            vf.valueOf(1), vf.complex(3,-4), vf.rational(-1, 3), vf.rational(1, 2)
        });
        System.out.println(M + "*" + v + "=" + M.multiply(v));
        assertTrue(!((Complex)M.multiply(v).get(0)).im().equals(vf.ZERO()));
    } 

}
