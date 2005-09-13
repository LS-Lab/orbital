/**
 * @(#)DiverseMathTest.java 1.2 2003-07-21 Andre Platzer
 * 
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

import junit.framework.*;

/**
 * A test case, integrating some assertion-aware programs from
 * examples/math/.  This case simply integrates the assertions of the
 * examples, even though they do not cover all testing issues.
 * @version $Id$
 */
public class DiverseMathTest extends check.TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(DiverseMathTest.class);
    }

    public void testComplexEmulation() {
        ComplexEmulation.main(new String[0]);
    }

    public void testModuloArithmeticTest() {
        ModuloArithmeticTest.main(new String[0]);
    }

    public void testPolynomialTest() {
        PolynomialTest.main(new String[0]);
    }

    public void testGroebner() {
        Groebner.main(new String[0]);
    }

    public void testRationalEmulation() {
        RationalEmulation.main(new String[0]);
    }

}
