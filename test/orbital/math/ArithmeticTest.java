/**
 * @(#)ArithmeticTest.java 1.1 2003-08-06 Andre Platzer
 * 
 * Copyright (c) 2002-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.math;
import orbital.math.functional.*;

import junit.framework.*;
import junit.extensions.*;
import java.util.*;

/**
 * Base class for testing some arithmetics.
 * @version $Id$
 * @see  <a href="examples/math/MathTest.java">MathTest example</a>
 */
public class ArithmeticTest extends check.TestCase {
    private Real tolerance;
    private Values vf;
    private Arithmetic a[];
    private Arithmetic b[];
    private Random random;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(ArithmeticTest.class);
    }
    protected void setUp() {
        vf = Values.getDefaultInstance();
        tolerance = vf.valueOf(MathUtilities.getDefaultTolerance());
        random = new Random();
    }

    //testArithmetic(x,y, xplusy,minusy,xsuby,xtimesy, inversey,xdividey, xpowery)
    public void testInteger() {
        testArithmetic(vf.valueOf(4),vf.valueOf(7), vf.valueOf(11),vf.valueOf(-7),vf.valueOf(-3),vf.valueOf(28));
        testArithmetic(vf.valueOf(6),vf.valueOf(-1), vf.valueOf(5),vf.valueOf(1),vf.valueOf(7),vf.valueOf(-6), vf.valueOf(-1),vf.valueOf(-6), vf.rational(1,6));
        testArithmetic(vf.valueOf(6),vf.valueOf(2), vf.valueOf(8),vf.valueOf(-2),vf.valueOf(4),vf.valueOf(12), vf.rational(1,2),vf.valueOf(3), vf.valueOf(36));
        testArithmetic(vf.valueOf(5),vf.valueOf(7), vf.valueOf(12),vf.valueOf(-7),vf.valueOf(-2),vf.valueOf(35), vf.rational(1,7),vf.rational(5,7), vf.valueOf(78125));
    }
    public void testRational() {
        testArithmetic(vf.rational(4,3),vf.rational(2,5), vf.rational(26,15),vf.rational(-4,10),vf.rational(14,15),vf.rational(8,15), vf.rational(5,2),vf.rational(10,3));
    }
    public void testReal() {
        testArithmetic(vf.valueOf(1.2),vf.valueOf(2.5), vf.valueOf(3.7),vf.valueOf(-2.5),vf.valueOf(-1.3),vf.valueOf(3), vf.valueOf(0.4),vf.valueOf(0.48), vf.valueOf(1.5774409656148784067560729744663));
    }
    public void testComplex() {
        testArithmetic(vf.complex(1,1),vf.complex(-2,2), vf.complex(-1,3),vf.complex(2,-2),vf.complex(3,-1),vf.valueOf(-4), vf.complex(-0.25,-0.25),vf.complex(0,-0.5), vf.complex(0.0664134997123102611246411821123, -0.0799545284640340262599558877723));
    }

    // mixed types
    
    public void testMixedIntegerRational() {
        testArithmetic(vf.valueOf(3),vf.rational(2,3), vf.rational(11,3),vf.rational(-2,3),vf.rational(7,3),vf.valueOf(2), vf.rational(3,2), vf.rational(9,2), vf.valueOf(2.08008382305190411453005682436));
    }
    public void testMixedIntegerReal() {
        testArithmetic(vf.valueOf(3),vf.valueOf(2.12), vf.valueOf(5.12),vf.valueOf(-2.12),vf.valueOf(0.88),vf.valueOf(6.36), vf.valueOf(0.47169811320754716981132075471698),vf.valueOf(1.4150943396226415094339622641509), vf.valueOf(10.268264804532778384558296943364));
    }
    public void testMixedIntegerComplex() {
        testArithmetic(vf.valueOf(2),vf.complex(1,-1), vf.complex(3,-1),vf.complex(-1,+1),vf.complex(1,+1),vf.complex(2,-2), vf.complex(0.5,0.5),vf.complex(1,1), vf.complex(1.53847780272794425315665998732, -1.27792255262726960230006582293));
    }
    
    public void testMixedRationalInteger() {
        testArithmetic(vf.rational(2,3),vf.valueOf(3), vf.rational(11,3),vf.rational(-3),vf.rational(-7,3),vf.valueOf(2), vf.rational(1,3), vf.rational(2,9), vf.valueOf(0.2962962962962962962962962962963));
    }
    public void testMixedRationalReal() {
        testArithmetic(vf.rational(3,2),vf.valueOf(2.5), vf.valueOf(4),vf.valueOf(-2.5),vf.valueOf(-1),vf.valueOf(3.75), vf.valueOf(0.4),vf.valueOf(0.6), vf.valueOf(2.7556759606310753604719445840441));
        testArithmetic(vf.rational(2,3),vf.valueOf(2.4), vf.valueOf(3.066666666666),vf.valueOf(-2.4),vf.valueOf(-1.73333333333333),vf.valueOf(1.6), vf.valueOf(0.4166666666666),vf.valueOf(0.2777777777777), vf.valueOf(0.37790355574097506159008825190904));
    }
    public void testMixedRationalComplex() {
        testArithmetic(vf.rational(3,2),vf.complex(1,-1), vf.complex(2.5,-1),vf.complex(-1,+1),vf.complex(0.5,+1),vf.complex(1.5,-1.5), vf.complex(0.5,0.5),vf.complex(0.75,0.75), vf.complex(1.37837855465924956195540515710, -0.591669299571541203435232763353));
    }

    public void testMixedRealInteger() {
        testArithmetic(vf.valueOf(2.12),vf.valueOf(3), vf.valueOf(5.12),vf.valueOf(-3),vf.valueOf(-0.88),vf.valueOf(6.36), vf.rational(1,3),vf.valueOf(0.70666666666666666666666666666667), vf.valueOf(9.528128));
    }
    public void testMixedRealRational() {
        testArithmetic(vf.valueOf(2.5),vf.rational(3,2), vf.valueOf(4),vf.rational(-3,2),vf.valueOf(1),vf.valueOf(3.75), vf.rational(2,3),vf.valueOf(1.6666666666666666666666666666667), vf.valueOf(3.9528470752104741649986169305409));
    }
    public void testMixedRealComplex() {
        testArithmetic(vf.valueOf(1.5),vf.complex(1,-1), vf.complex(2.5,-1),vf.complex(-1,+1),vf.complex(0.5,+1),vf.complex(1.5,-1.5), vf.complex(0.5,0.5),vf.complex(0.75,0.75), vf.complex(1.37837855465924956195540515710, -0.591669299571541203435232763353));
    }
    
    public void testMixedComplexInteger() {
        testArithmetic(vf.complex(1,-1),vf.valueOf(2), vf.complex(3,-1),vf.valueOf(-2),vf.complex(-1,-1),vf.complex(2,-2), vf.rational(1,2),vf.complex(0.5,-0.5), vf.complex(0, -2));
    }
    public void testMixedComplexRational() {
        testArithmetic(vf.complex(1,-1),vf.rational(3,2), vf.complex(2.5,-1),vf.rational(-3,2),vf.complex(-0.5,-1),vf.complex(1.5,-1.5), vf.rational(2,3),vf.complex(0.66666666666666666666666666666667,-0.66666666666666666666666666666667), vf.complex(0.643594252905582624735443437418, -1.55377397403003730734415895306));
    }
    public void testMixedComplexReal() {
        testArithmetic(vf.complex(1,-1),vf.valueOf(1.5), vf.complex(2.5,-1),vf.valueOf(-1.5),vf.complex(-0.5,-1),vf.complex(1.5,-1.5), vf.valueOf(0.66666666666666666666666666666667),vf.complex(0.66666666666666666666666666666667,-0.66666666666666666666666666666667), vf.complex(0.643594252905582624735443437418, -1.55377397403003730734415895306 ));
    }

    // helpers

    private int integerArgument(int min, int max) {
        return min + (min == max ? 0 : random.nextInt(max-min));
    }

    /**
     * Test expected results.
     */
    protected final void testArithmetic(Arithmetic x, Arithmetic y,
                                        Arithmetic xplusy,
                                        Arithmetic minusy,
                                        Arithmetic xsuby,
                                        Arithmetic xtimesy) {
        testArithmetic(x, y, false);
        assertOp(x, y, Operations.plus, xplusy);
        assertOp(y, Operations.minus, minusy);
        assertOp(x, y, Operations.subtract, xsuby);
        assertOp(x, y, Operations.times, xtimesy);
    }
    /**
     * Test expected results.
     */
    protected final void testArithmetic(Arithmetic x, Arithmetic y,
                                        Arithmetic xplusy,
                                        Arithmetic minusy,
                                        Arithmetic xsuby,
                                        Arithmetic xtimesy,
                                        Arithmetic inversey,
                                        Arithmetic xdividey
                                        ) {
        testArithmetic(x, y, true);
        testArithmetic(x, y, xplusy, minusy, xsuby, xtimesy);
        assertOp(y, Operations.inverse, inversey);
        assertOp(x, y, Operations.divide, xdividey);
    }
    /**
     * Test expected results.
     */
    protected final void testArithmetic(Arithmetic x, Arithmetic y,
                                        Arithmetic xplusy,
                                        Arithmetic minusy,
                                        Arithmetic xsuby,
                                        Arithmetic xtimesy,
                                        Arithmetic inversey,
                                        Arithmetic xdividey,
                                        Arithmetic xpowery
                                        ) {
        testArithmetic(x, y, xplusy, minusy, xsuby, xtimesy, inversey, xdividey);
        assertOp(x, y, Operations.power, xpowery);
    }
    private final void assertOp(Arithmetic x, Arithmetic y, BinaryFunction op, Arithmetic expected) {
        Arithmetic found = (Arithmetic)op.apply(x,y);
        assertTrue("("+ x + ") " + op + " (" + y + ") = " + found + " = " + expected, found.equals(expected, tolerance));
        if (op == Operations.plus) {
            found = x.add(y);
        } else if (op == Operations.plus) {
            found = x.add(y);
        } else if (op == Operations.subtract) {
            found = x.subtract(y);
        } else if (op == Operations.times) {
            found = x.multiply(y);
        } else if (op == Operations.divide) {
            found = x.divide(y);
        } else if (op == Operations.power) {
            found = x.power(y);
        } else if (op == Operations.divide) {
            found = x.divide(y);
        } else {
            // skip secondary test below
            return;
        }
        assertTrue("("+ x + ") " + op + " (" + y + ") = " + found + " = " + expected, found.equals(expected, tolerance));
    }
    private final void assertOp(Arithmetic x, Function op, Arithmetic expected) {
        Arithmetic found = (Arithmetic)op.apply(x);
        assertTrue(op + "("+ x + ") " + " = " + found + " = " + expected, found.equals(expected, tolerance));
        if (op == Operations.inverse) {
            found = x.inverse();
        } else if (op == Operations.minus) {
            found = x.minus();
        } else {
            // skip secondary test below
            return;
        }
        assertTrue(op + "("+ x + ") " + " = " + found + " = " + expected, found.equals(expected, tolerance));
    }
    /**
     * Test usual laws with two arithmetic objects.
     * @param withDivisions whether or not to test laws involving division as well.
     */
    protected final void testArithmetic(Arithmetic x, Arithmetic y, boolean withDivisions) {
        Rational a = Values.getDefaultInstance().valueOf(4);
        Arithmetic zero = x.zero();
        Arithmetic one = x.one();
        System.out.println("(" + zero + ") + (" + x + ") = " + zero.add(x));
        assertTrue(zero.add(x).equals(x) , "0+x=x \t(" + zero + ") + (" + x + ") = " + zero.add(x) + " = " + x);
        assertTrue(x.add(zero).equals(x) , "x+0=x \t(" + x + ") + (" + zero + ") = " + x);

        assertTrue(zero.add(zero).equals(zero) , "0+0=0 \t(" + zero + ")+(" + zero + ") = " + zero);

        System.out.println("(" + x + ") + (" + y + ") = " + x.add(y));

        System.out.println("-(" + x + ") = " + x.minus());
        assertTrue(x.minus().add(x).equals(zero) , "(-x)+x=0 \t-(" + x + ") + (" + x + ") = " + zero);
        assertTrue(x.add(x.minus()).equals(zero) , "x+(-x)=0 \t(" + x + ") + (-(" + x + ")) = " + zero);

        assertTrue(zero.minus().equals(zero) , "-0=0 \t-(" + zero + ") = " + zero);
        assertTrue(zero.subtract(zero).equals(zero) , "0-0=0 \t" + zero + "-(" + zero + ") = " + zero);

        System.out.println("(" + x + ") - (" + y + ") = " + x.subtract(y));
        assertTrue(x.subtract(x).equals(zero) , "x-x=0 \t(" + x + ") - (" + x + ") = " + zero);

        System.out.println(a + "*(" + x + ") = " + x.scale(a));
        System.out.println("(" + one + ") * (" + x + ") = " + one.multiply(x));
        assertTrue(one.multiply(x).equals(x) , "1*x=x \t" + one + " * (" + x + ") = " + x);
        assertTrue(x.multiply(one).equals(x) , "x*1=x \t(" + x + ") * " + one + " = " + x);

        assertTrue(zero.multiply(zero).equals(zero) , "0*0=0 \t(" + zero + ") * (" + zero + ") = " + zero);
        assertTrue(zero.multiply(x).equals(zero) , "0*x=0 \t(" + zero + ") * (" + x + ") = " + zero);
        assertTrue(x.multiply(zero).equals(zero) , "x*0=0 \t(" + x + ") * (" + zero + ") = " + zero);
        assertTrue(zero.multiply(one).equals(zero) , "0*1=0 \t(" + zero + ") * (" + one + ") = " + zero);
        assertTrue(one.multiply(zero).equals(zero) , "1*0=0 \t(" + one + ") * (" + zero + ") = " + zero);
        
        System.out.println("(" + x + ") * (" + y + ") = " + x.multiply(y));

        //@todo commutative +?

        if (withDivisions) {
            System.out.println("(" + x + ")^-1 = " + x.inverse());
            assertTrue(x.inverse().multiply(x).equals(one) , "x^-1 * x=1 \t(" + x.inverse() + ") * (" + x + ") = " + one);
            assertTrue(x.multiply(x.inverse()).equals(one) , "x*x^-1=1 \t(" + x + ") * (" + x.inverse() + ") = " + one);

            System.out.println("(" + one + ") / (" + x + ") = " + one.divide(x));
            assertTrue(one.divide(x).equals(x.inverse()) , "1/x=x^-1 \t" + one + " / (" + x + ") = " + x.inverse());
            assertTrue(one.inverse().equals(one) , "1^-1=1 \t(" + one + ")^-1 = " + one);
            assertTrue(one.divide(one).equals(one) , "1/1=1 \t" + one + " / (" + one + ") = " + one);

            System.out.println("(" + x + ") / (" + one + ") = " + x.divide(one));
            assertTrue(x.divide(one).equals(x) , "x/1=x \t(" + x + ") / 1 = " + x);

            assertTrue(zero.divide(one).equals(zero) , "0/1=0 \t(" + zero + ") / (" + one + ") = " + zero);
            
            System.out.println("(" + x + ") / (" + y + ") = " + x.divide(y));

            //@todo sometimes commutative *?
        }
    }
}

