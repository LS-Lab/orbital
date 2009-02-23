/**
 * @(#)ArithmeticTest.java 1.1 2003-08-06 Andre Platzer
 * 
 * Copyright (c) 2002-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.math;
import orbital.logic.functor.BinaryPredicate;
import orbital.logic.functor.Predicate;
import orbital.math.functional.*;

import junit.framework.*;
import junit.extensions.*;
import java.util.*;
import java.math.*;

/**
 * Base class for testing some arithmetics.
 * @version $Id$
 * @see  <a href="examples/math/MathTest.java">MathTest example</a>
 */
public class ArithmeticTest extends check.TestCase {
    private static final int TEST_REPETITIONS = 1000;
    private Real tolerance;
    private Values vf;
    private Arithmetic a[];
    private Arithmetic b[];
    private ArithmeticTestPatternGenerator random;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(ArithmeticTest.class);
    }
    protected void setUp() {
        vf = Values.getDefaultInstance();
        tolerance = vf.valueOf(MathUtilities.getDefaultTolerance());
        random = new ArithmeticTestPatternGenerator(-1000,1000);
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
    protected final void testArithmetic(Arithmetic x, Arithmetic y, boolean withDivisions) {
    	checkArithmetic(Values.getDefault(), x, y, withDivisions);
    }

    /**
     * Check contract properties of arithmetic objects, i.e., usual laws with one arithmetic object.
     * @param withDivisions whether or not to test laws involving division as well.
     */
    public static final boolean checkArithmetic(ValueFactory vf, Arithmetic x, boolean withDivisions) {
    	if (x instanceof Symbol)
    		return true;
        Rational a = vf.valueOf(4);
        Arithmetic zero = x.zero();
        Arithmetic one = x.one();
        System.out.println("(" + zero + ") + (" + x + ") = " + zero.add(x));
        assertTrue(zero.add(x).equals(x) , "0+x=x:\t(" + zero + ") + (" + x + ") = " + zero.add(x) + " = " + x);
        assertTrue(x.add(zero).equals(x) , "x+0=x:\t(" + x + ") + (" + zero + ") = " + x.add(zero) + " = " + x);

        assertTrue(zero.add(zero).equals(zero) , "0+0=0:\t(" + zero + ")+(" + zero + ") = " + zero.add(zero) + " = " + zero);

        System.out.println("-(" + x + ") = " + x.minus());
        assertTrue(x.minus().add(x).equals(zero) , "(-x)+x=0:\t-(" + x + ") + (" + x + ") = " + x.minus().add(x) + " = " + zero);
        assertTrue(x.add(x.minus()).equals(zero) , "x+(-x)=0:\t(" + x + ") + (-(" + x + ")) = " + x.add(x.minus()) + " = " + zero);

        assertTrue(zero.minus().equals(zero) , "-0=0:\t-(" + zero + ") = " + zero.minus() + " = " + zero);
        assertTrue(zero.subtract(zero).equals(zero) , "0-0=0:\t" + zero + "-(" + zero + ") = " + zero.subtract(zero) + " = " + zero);

        assertTrue(x.subtract(x).equals(zero) , "x-x=0:\t(" + x + ") - (" + x + ") = " + x.subtract(x) + " = " + zero);

        System.out.println(a + "*(" + x + ") = " + x.scale(a));
        System.out.println("(" + one + ") * (" + x + ") = " + one.multiply(x));
        assertTrue(one.multiply(x).equals(x) , "1*x=x:\t" + one + " * (" + x + ") = " + one.multiply(x) + " = " + x);
        assertTrue(x.multiply(one).equals(x) , "x*1=x:\t(" + x + ") * " + one + " = " + x.multiply(one) + " = " + x);

        assertTrue(zero.multiply(zero).equals(zero) , "0*0=0:\t(" + zero + ") * (" + zero + ") = " + zero.multiply(zero) + " = " + zero.multiply(zero) + " = " + zero);
        assertTrue(zero.multiply(x).isZero() , "0*x=0:\t(" + zero + ") * (" + x + ") = " + zero.multiply(x) + " is zero");
        assertTrue(zero.multiply(x).equals(zero) , "0*x=0:\t(" + zero + ") * (" + x + ") = " + zero.multiply(x) + " = " + zero);
        assertTrue(x.multiply(zero).equals(zero) , "x*0=0:\t(" + x + ") * (" + zero + ") = " + x.multiply(zero) + " = " + zero);
        assertTrue(zero.multiply(one).equals(zero) , "0*1=0:\t(" + zero + ") * (" + one + ") = " + zero.multiply(one) + " = " + zero);
        assertTrue(one.multiply(zero).equals(zero) , "1*0=0:\t(" + one + ") * (" + zero + ") = " + one.multiply(zero) + " = " + zero);
        
    	assertTrue(x.zero().isZero(), "the zero " + x.zero() + " of " + x + " is zero");
    	assertTrue(x.one().isOne(), "the one " + x.one() + " of " + x + " is one");
		assertTrue(x.add(x.minus()).isZero(), "(" + x + ") + -(" + x + ") = " + x.add(x.minus()) + " is zero");
		assertTrue(x.add(x.minus()).equals(x.zero()), "(" + x + ") + -(" + x + ") = " + x.add(x.minus()) + " = " + x.zero());
		assertTrue(x.subtract(x).isZero(), "(" + x + ") - (" + x + ") = " + x.subtract(x) + " is zero");
		assertTrue(x.subtract(x).equals(x.zero()), "(" + x + ") - (" + x + ") = " + x.subtract(x) + " = " + x.zero());
		assertTrue(x.add(x.zero()).equals(x), "(" + x + ") + (" + x.zero() + ") = " + x.add(x.zero()) + " = " + x);
		assertTrue(x.subtract(x.zero()).equals(x), "(" + x + ") - (" + x.zero() + ") = " + x.subtract(x.zero()) + " = " + x);
		assertTrue(x.zero().subtract(x).equals(x.minus()), "(" + x.zero() + ") - (" + x + ") = " + x.zero().subtract(x) + " = " + x.minus());
		assertTrue(x.zero().add(x).equals(x), "(" + x.zero() + ") + (" + x + ") = " + x.zero().add(x) + " = " + x);
		assertTrue(x.multiply(x.one()).equals(x), "(" + x + ") * (" + x.one() + ") = " + x.multiply(x.one()) + " = " + x);
		assertTrue(x.multiply(x.one().minus()).equals(x.minus()), "(" + x + ") * (" + x.one().minus() + ") = " + x.multiply(x.one().minus()) + " = " + x.minus());
		assertTrue(x.multiply(x.zero()).isZero(), "(" + x + ") * (" + x.zero() + ") = " + x.multiply(x.zero()) + " is zero");
		assertTrue(x.multiply(x.zero()).equals(x.zero()), "(" + x + ") * (" + x.zero() + ") = " + x.multiply(x.zero()) + " = " + x.zero());
		assertTrue(x.one().multiply(x).equals(x), "(" + x.one() + ") * (" + x + ") = " + x.one().multiply(x) + " = " + x);
		assertTrue(x.one().minus().multiply(x).equals(x.minus()), "(" + x.one().minus() + ") * (" + x + ") = " + x.one().minus().multiply(x) + " = " + x.minus());
		assertTrue(x.one().multiply(x).subtract(x).isZero(), "(" + x.one() + ") * (" + x + ") - " + x + " = " + x.one().multiply(x).subtract(x) + " is zero");
		assertTrue(x.one().multiply(x).subtract(x).equals(x.zero()), "(" + x.one() + ") * (" + x + ") - " + x + " = " + x.one().multiply(x).subtract(x) + " = " + x.zero());
        //@todo commutative +?

        if (withDivisions) {
            System.out.println("(" + x + ")^-1 = " + x.inverse());
            assertTrue(x.inverse().multiply(x).equals(one) , "x^-1 * x=1:\t(" + x.inverse() + ") * (" + x + ") = " + one);
            assertTrue(x.multiply(x.inverse()).equals(one) , "x*x^-1=1:\t(" + x + ") * (" + x.inverse() + ") = " + one);

            System.out.println("(" + one + ") / (" + x + ") = " + one.divide(x));
            assertTrue(one.divide(x).equals(x.inverse()) , "1/x=x^-1:\t" + one + " / (" + x + ") = " + x.inverse());
            assertTrue(one.inverse().equals(one) , "1^-1=1:\t(" + one + ")^-1 = " + one);
            assertTrue(one.divide(one).equals(one) , "1/1=1:\t" + one + " / (" + one + ") = " + one);

            System.out.println("(" + x + ") / (" + one + ") = " + x.divide(one));
            assertTrue(x.divide(one).equals(x) , "x/1=x:\t(" + x + ") / 1 = " + x);

            assertTrue(zero.divide(one).equals(zero) , "0/1=0:\t(" + zero + ") / (" + one + ") = " + zero);
            
            //@todo sometimes commutative *?
        }
        return true;
    }
    /**
     * Test usual laws with two arithmetic objects.
     * @param withDivisions whether or not to test laws involving division as well.
     */
    public static final boolean checkArithmetic(ValueFactory vf, Arithmetic x, Arithmetic y, boolean withDivisions) {
    	checkArithmetic(vf, x, withDivisions);
    	checkArithmetic(vf, y, withDivisions);
    	System.out.println("(" + x + ") + (" + y + ") = " + x.add(y));

    	System.out.println("(" + x + ") - (" + y + ") = " + x.subtract(y));
    	System.out.println("(" + x + ") * (" + y + ") = " + x.multiply(y));
    	System.out.println("(" + x + ") / (" + y + ") = " + x.divide(y));
        return true;
    }


    protected final Predicate checkArithmetic(final ValueFactory vf, final boolean withDivisions) {
    	return new Predicate() {
    		public boolean apply(Object arg) {
    			return checkArithmetic(vf, (Arithmetic)arg, withDivisions);
    		}

    	};
    }
    protected final BinaryPredicate checkArithmetics(final ValueFactory vf, final boolean withDivisions) {
    	return new BinaryPredicate() {
    		public boolean apply(Object arg, Object arg2) {
    			return checkArithmetic(vf, (Arithmetic)arg, (Arithmetic)arg2, withDivisions);
    		}

    	};
    }

    

    // test various doubleValue() etc things.
    public void testValueOf() {
        for (int i = 0; i < TEST_REPETITIONS; i++) {
            {
                int x = random.randomInt();
                Integer args[] = {
                    vf.valueOf(x),
                    vf.valueOf((long)x),
                    vf.valueOf(BigInteger.valueOf(x))
                };
                for (int k = 0; k < args.length; k++) {
                    Number xs = (Number) args[k];
                    assertTrue(x == xs.intValue(),
                               "intValue() " + x + " == " + xs.intValue() + " of " + xs + "@" + xs.getClass());
                    assertTrue((long)x == xs.longValue(),
                               "longValue() " + x + " == " + xs.longValue() + " of " + xs + "@" + xs.getClass());
                    assertTrue((short)x == xs.shortValue(),
                               "shortValue() " + x + " == " + xs.shortValue() + " of " + xs + "@" + xs.getClass());
                    assertTrue((byte)x == xs.byteValue(),
                               "byteValue() " + x + " == " + xs.byteValue() + " of " + xs + "@" + xs.getClass());
                    assertTrue((double)x == xs.doubleValue(),
                               "doubleValue() " + x + " == " + xs.doubleValue() + " of " + xs + "@" + xs.getClass());
                    assertTrue((float)x == xs.floatValue(),
                               "floatValue() " + x + " == " + xs.floatValue() + " of " + xs + "@" + xs.getClass());
                }
            }
            {
                double x = random.randomDouble();
                Real args[] = {
                    vf.valueOf(x),
                    vf.valueOf(BigDecimal.valueOf(x))
                };
                for (int k = 0; k < args.length; k++) {
                    Number xs = (Number) args[k];
                    assertTrue((double)x == xs.doubleValue(),
                               "doubleValue() " + x + " == " + xs.doubleValue() + " of " + xs + "@" + xs.getClass());
                    assertTrue((float)x == xs.floatValue(),
                               "floatValue() " + x + " == " + xs.floatValue() + " of " + xs + "@" + xs.getClass());
                    try {
                        assertTrue((int)x == xs.intValue(),
                                   "intValue() " + x + " == " + xs.intValue() + " of " + xs + "@" + xs.getClass());
                        assertTrue((long)x == xs.longValue(),
                                   "longValue() " + x + " == " + xs.longValue() + " of " + xs + "@" + xs.getClass());
                        assertTrue((short)x == xs.shortValue(),
                                   "shortValue() " + x + " == " + xs.shortValue() + " of " + xs + "@" + xs.getClass());
                        assertTrue((byte)x == xs.byteValue(),
                                   "byteValue() " + x + " == " + xs.byteValue() + " of " + xs + "@" + xs.getClass());
                    }
                    catch (ArithmeticException ex) {
                        if (!ex.getMessage().contains("Rounding"))
                            throw ex;
                    }
                }
            }
            {
                int p = random.randomInt();
                int q = random.randomInt(1,1000);
                double x = (double) p / (double) q;
                Rational args[] = {
                    vf.rational(p,q),
                    vf.rational(vf.valueOf(p), vf.valueOf(q)),
                    vf.rational(vf.valueOf(BigInteger.valueOf(p)), vf.valueOf(BigInteger.valueOf(q)))
                };
                for (int k = 0; k < args.length; k++) {
                    Number xs = (Number) args[k];
                    assertTrue((double)x == xs.doubleValue(),
                               "doubleValue() " + x + " == " + xs.doubleValue() + " of " + xs + "@" + xs.getClass());
                    assertTrue((float)x == xs.floatValue(),
                               "floatValue() " + x + " == " + xs.floatValue() + " of " + xs + "@" + xs.getClass());
                    try {
                        assertTrue((int)x == xs.intValue(),
                                   "intValue() " + x + " == " + xs.intValue() + " of " + xs + "@" + xs.getClass());
                        assertTrue((long)x == xs.longValue(),
                                   "longValue() " + x + " == " + xs.longValue() + " of " + xs + "@" + xs.getClass());
                        assertTrue((short)x == xs.shortValue(),
                                   "shortValue() " + x + " == " + xs.shortValue() + " of " + xs + "@" + xs.getClass());
                        assertTrue((byte)x == xs.byteValue(),
                                   "byteValue() " + x + " == " + xs.byteValue() + " of " + xs + "@" + xs.getClass());
                    }
                    catch (ArithmeticException ex) {
                        if (!ex.getMessage().contains("Rounding"))
                            throw ex;
                    }
                }
            }
        }
    }

    // check a special bugfix
    public void testSpecial() {
        Real tolerance = vf.valueOf(0.00000001);
        Real args[] = {
            vf.valueOf(-1),
            vf.valueOf((long)-1),
            vf.valueOf(BigInteger.valueOf(-1)),
            vf.valueOf(-1.0),
            vf.valueOf(BigDecimal.valueOf(-1.0))
        };
        Real expected = vf.valueOf(-0.6420926159343308);
        for (int i = 0; i < args.length; i++) {
            System.out.println("cot(" + args[i] + ") == " + Functions.cot.apply(args[i]) + " == " + expected);
            assertTrue(((Arithmetic)Functions.cot.apply(args[i])).equals(expected, tolerance), "cot(" + args[i] + ") == " + Functions.cot.apply(args[i]) + " == " + expected);
        }
    }
}

