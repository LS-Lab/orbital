/**
 * @(#)ValuesTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import junit.framework.*;
import java.util.*;

/**
 * A sample test case, testing Values.
 * @version 1.1, 2002-09-14
 */
public class ValuesTest extends check.TestCase {
    private static final int TEST_REPETITION = 10;
    private Values vf;
    private Arithmetic a[];
    private Arithmetic b[];
    private Random random;

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
	random = new Random();
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

    private int integerArgument(int min, int max) {
	return min + (min == max ? 0 : random.nextInt(max-min));
    }
    public void testTrivialTensors_valueOf_ArithmeticArray() {
	System.out.println("ValueFactor.valueOf(Arithmetic[])");
	for (int i = 0; i < TEST_REPETITION; i++) {
	    Integer v = vf.valueOf(integerArgument(-10,10));
	    test(vf.valueOf(new Arithmetic[] {v}), v);
	}
    }
    public void testTrivialTensors_valueOf_intArray() {
	System.out.println("ValueFactor.valueOf(int[])");
	for (int i = 0; i < TEST_REPETITION; i++) {
	    Integer v = vf.valueOf(integerArgument(-10,10));
	    test(vf.valueOf(new int[] {v.intValue()}), v);
	}
    }
    public void testTrivialTensors_tensor_ArithmeticArray() {
	System.out.println("ValueFactor.tensor(Arithmetic[])");
	for (int i = 0; i < TEST_REPETITION; i++) {
	    Integer v = vf.valueOf(integerArgument(-10,10));
	    test(vf.tensor(new Arithmetic[] {v}), v);
	}
    }
    public void testTrivialTensors_tensor_intArray() {
	System.out.println("ValueFactor.tensor(int[])");
	for (int i = 0; i < TEST_REPETITION; i++) {
	    Integer v = vf.valueOf(integerArgument(-10,10));
	    test(vf.tensor(new int[] {v.intValue()}), v);
	}
    }

    /**
     * Test tensor to have expected value as its only component.
     */
    private void test(Tensor t, Arithmetic expected) {
	System.out.println(t.getClass() + " " + t + " corresponds to " + expected);
	assertEquals(t.rank(), 1);
	assertEquals(t.dimensions().length, 1);
	assertEquals(t.dimensions()[0], 1);
	assertEquals(t.get(new int[] {0}), expected);
	Iterator i = t.iterator();
	assertTrue(i.hasNext() , "tensor with 1 element has elements in its iterator");
	assertEquals(i.next(), expected);
	assertTrue(!i.hasNext() , "tensor with 1 element has no more than 1elements in its iterator");

	assertTrue(t instanceof Vector, "rank 1 tensor collapses to vector");
	Vector v = (Vector) t;
	assertEquals(v.dimension(), 1);
	assertEquals(v.get(0), expected);
	i = v.iterator();
	assertTrue(i.hasNext() , "tensor with 1 element has elements in its iterator");
	assertEquals(i.next(), expected);
	assertTrue(!i.hasNext() , "tensor with 1 element has no more than 1elements in its iterator");
    }
}

