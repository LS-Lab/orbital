/**
 * @(#)MathUtilitiesTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import junit.framework.*;

import java.math.BigInteger;

/**
 * A sample test case, testing .
 * @version 1.1, 2002-09-14
 */
public class MathUtilitiesTest extends check.TestCase {
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(MathUtilitiesTest.class);
    }

    public void testGcdLcm() {
	for (int i = 0; i < 20; i++) {
	    int a = 1 + (int) (Math.random() * 100);
	    int b = 1 + (int) (Math.random() * 100);
	    System.out.println(a + " " + b + ": gcd:" + AlgebraicAlgorithms.gcd(a, b) + ", lcm:" + AlgebraicAlgorithms.lcm(a, b));
	} 
	assertEquals(AlgebraicAlgorithms.gcd(-8, -4), 4);
	assertEquals(AlgebraicAlgorithms.gcd(-8, 4), 4);
	assertEquals(AlgebraicAlgorithms.gcd(8, -4), 4);
    }
    private static String expectedRomans[] = {
	null,
	"I",
	"II",
	"III",
	"IV",
	"V",
	"VI",
	"VII",
	"VIII",
	"IX",
	"X",
	"XI",
	"XI",
	"XIII",
	"XIV",
	"XV",
	"XVI",
	"XVII",
	"XVIII",
	"XIX",
	"XX",
	"XXI",
	"XXII",
	"XXIII",
	"XXIII",
	"XXV",
	"XXVI",
	"XXVII",
	"XXVIII",
	"XXIX"
    };
    public void testRomans() {
	for (short d = 1; d < 30; d++)
	    assertEquals(MathUtilities.toRoman(d), expectedRomans[d]);
    }
    public void testFormat() {
	double v;
	v = 1112.345678999999;
	System.out.println(v + ":" + MathUtilities.format(v));
	assertEquals(MathUtilities.format(v, 6), "1112.345679");
	assertEquals(MathUtilities.format(v, -2), "1112"); //@todo sure that we wouldn't expect 1100?
	assertEquals(MathUtilities.format(123.000, 6), "123");
	assertEquals(MathUtilities.format(new byte[] {12,4,77,1,-1,0,-0,-5,8,127,-127,-128,80,-80,2,-2,10,-10}),
		     "012:004:077:001:255:000:000:251:008:127:129:128:080:176:002:254:010:246");
    }

    private static final boolean expectedPrime[] = {
	true,
	false, true, true, false, true, false, true, false, false, false, true, 
	false, true, false, false, false, true, false, true, false, false, false, 
	true, false, false, false, false, false, true, false, true, false, false, 
	false, false, false, true, false, false, false, true, false, true, false, 
	false, false, true, false, false, false, false, false, true, false, false, 
	false, false, false, true, false, true, false, false, false, false, false, 
	true, false, false, false, true, false, true, false, false, false, false, 
	false, true, false, false, false, true, false, false, false, false, false, 
	true, false, false, false, false, false, false, false, true, false, false, 
	false, true, false, true, false, false, false, true, false, true, false, 
	false, false, true, false, false, false, false, false, false, false, false, 
	false, false, false, false, false, true, false, false, false, true, false, 
	false, false, false, false, true, false, true, false
    };
    public void testPrimes() {
	for (BigInteger p = BigInteger.valueOf(0); p.compareTo(BigInteger.valueOf(140)) < 0; p = p.add(BigInteger.valueOf(1)))
	    assertTrue(p + (expectedPrime[p.intValue()] ? " is " : " is not ") + "prime", MathUtilities.isPrime(p) == expectedPrime[p.intValue()]);
    } 
}