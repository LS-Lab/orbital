/**
 * @(#)NumericalAlgorithmsTest.java 1.1 2007-09-04 Andre Platzer
 * 
 * Copyright (c) 2007 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import junit.framework.*;

import orbital.math.functional.*;
import java.math.BigInteger;

/**
 * testing numerical algorithms.
 * @version $Id$
 */
public class NumericalAlgorithmsTest extends check.TestCase {
    private static final int TEST_REPETITION = 10;
    private Values vf;
    private ArithmeticTestPatternGenerator random;
    private Real tolerance;
    protected void setUp() {
        vf = Values.getDefaultInstance();
        tolerance = vf.valueOf(MathUtilities.getDefaultTolerance());
        random = new ArithmeticTestPatternGenerator(-1000,1000);
    }
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(NumericalAlgorithmsTest.class);
    }

    public void testdSolve() {
        Real tau;
        Real eta;
        Real min, max;
        int steps = 50;
        BinaryFunction f;
        Function y;

        f = Functions.binaryConstant(vf.valueOf(5));
        tau = vf.ZERO();
        eta = vf.ZERO();
        min = tau;
        max = vf.valueOf(4);
        checkndSolve(f, tau, eta,
                     min, max,
                     steps);
        
        f = Functions.projectSecond;
        tau = vf.ZERO();
        eta =vf.ONE();
        min = tau;
        max = vf.valueOf(5);
        checkndSolve(f, tau, eta,
                     min, max,
                     steps);

        f = Functionals.onSecond((Function)Operations.plus.apply(Functions.tan, Functions.one));
        tau = vf.ONE();
        eta = vf.ONE();
        min = vf.ONE();
        max = vf.valueOf(1.1);
        steps = 4;
        checkndSolve(f, tau, eta,
                     min, max,
                     steps);
        
    }

    protected void checkndSolve(orbital.math.functional.BinaryFunction/*<Real,Vector<Real>>*/ f, Real tau, Vector/*<Real>*/ eta,
                                Real min, Real max,
                                int steps) {
        System.out.println("solving numerical differential equations");
        System.out.println("Solving ODE x'(t) == " + f + "\n  with initial value " + eta + " at " + tau + " in range [" + min + "," + max + "]");
        Function y = NumericalAlgorithms.dSolve(f, tau, eta,
                                                min, max,
                                                steps, 4);
        System.out.println("  solution\t" + y);
        System.out.println("  solution at " + tau + " is " + y.apply(tau));
        // randomized equality test
        for (int j = 0; j < TEST_REPETITION; j++) {
            Real r = vf.valueOf(random.randomDouble(min.doubleValue(), max.doubleValue()));
            System.out.println("\ty(" + r + ") = " + y.apply(r));
        }
    }

    protected void checkndSolve(orbital.math.functional.BinaryFunction/*<Real,Real>*/ f, Real tau, Real eta,
                                Real min, Real max,
                                int steps) {
        System.out.println("solving numerical differential equations");
        try {
            System.out.println("Solving ODE x'(t) == " + f.apply(vf.symbol("t"),vf.symbol("x")) + "\n  with initial value " + eta + " at " + tau + " in range [" + min + "," + max + "]");
        } catch(Exception ignore) {
            System.out.println("Solving ODE x'(t) == " + f + "\n  with initial value " + eta + " at " + tau + " in range [" + min + "," + max + "]");
        }
        Function y = NumericalAlgorithms.dSolve(f, tau, eta,
                                                min, max,
                                                steps, 4);
        System.out.println("  solution\t" + y);
        System.out.println("  solution at " + tau + " is " + y.apply(tau));
        // randomized equality test
        for (int j = 0; j < TEST_REPETITION; j++) {
            Real r = vf.valueOf(random.randomDouble(min.doubleValue(), max.doubleValue()));
            System.out.println("\ty(" + r + ") = " + y.apply(r));
        }
    }
}
