/**
 * @(#)ArithmeticMatrixTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import junit.framework.*;

/**
 * A sample test case, testing Values.
 * @version 1.1, 2002-09-14
 */
public class ArithmeticMatrixTest extends check.TestCase {
    private Values vf;
    private Real tolerance;
    private Matrix M, N;
    private Vector v;
    private Vector u;

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    protected void setUp() {
	vf = Values.getDefaultInstance();
	tolerance = vf.valueOf(1e-6);
	M = new ArithmeticMatrix(vf.valueOf(new double[][] {
	    {2, 1, 0, -2},
	    {1, 2, 4, 1},
	    {-2, 1, 2, -2},
	    {-3, 0, 1, -4}
	}).toArray());
	N = new ArithmeticMatrix(new Arithmetic[][] {
	    {vf.valueOf(-1), vf.valueOf(0), vf.valueOf(0), vf.valueOf(1)},
	    {vf.valueOf(4), vf.valueOf(2.1), vf.valueOf(-1), vf.valueOf(3)},
	    {vf.complex(-2, 1), vf.valueOf(-5), vf.valueOf(0), vf.rational(2,3)},
	    {vf.rational(1,3), vf.rational(-2,6), vf.valueOf(4), vf.rational(1,4)}
	});
	//@xxx class Debug produces an error with gjc error: type parameter double[] is not within its bound orbital.math.Arithmetic
	v = new ArithmeticVector(vf.valueOf(new double[] {
	    1, 2, 1, 2
	}).toArray());
	u = new ArithmeticVector(vf.valueOf(new double[] {
	    2, 1, 0, -3
	}).toArray());
    }
    public static Test suite() {
	return new TestSuite(ArithmeticMatrixTest.class);
    }

    public void testNormDetTrEtc() {
	System.out.println("\nM := " + M + "\n");
	System.out.println(M + "*" + v + "=" + M.multiply(v));
	System.out.println(u + "*" + v + "=" + u.multiply(v));
	System.out.println(v + "*" + 2 + "=" + v.multiply(vf.valueOf(2)));
	System.out.println("norm ||M||\t=" + M.norm());
	assertTrue(M.norm().equals(vf.valueOf(8.3666), tolerance));
	System.out.println("column sum norm\t=" + M.norm(1));
	assertTrue(M.norm(1).equals(vf.valueOf(9)));
	System.out.println("row sum norm\t=" + M.norm(Double.POSITIVE_INFINITY));
	assertTrue(M.norm(Double.POSITIVE_INFINITY).equals(vf.valueOf(8)));
	System.out.println("Rank M\t=" + M.linearRank());
	assertTrue(M.linearRank() == 4, "maximum rank");
	System.out.println("det M\t= |M|=" + M.det());
	assertTrue(M.det().equals(vf.valueOf(30)));
	System.out.println("Tr M\t=" + M.trace());
	assertTrue(M.trace().equals(vf.valueOf(2)));
	System.out.println("M^-1\t=" + M.inverse());

	System.out.println("\nN := " + N+ "\n");
	System.out.println("norm ||N||\t=" + N.norm());
	assertTrue(N.norm().equals(vf.valueOf(8.89602), tolerance));
	System.out.println("column sum norm\t=" + N.norm(1));
	assertTrue(N.norm(1).equals(vf.valueOf(7.5694), vf.valueOf(1e-4)));
	System.out.println("row sum norm\t=" + N.norm(Double.POSITIVE_INFINITY));
	assertTrue(N.norm(Double.POSITIVE_INFINITY).equals(vf.valueOf(10.1), tolerance));
	System.out.println("Rank N\t=" + N.linearRank());
	System.out.println("det N\t= |N|=" + N.det());
	assertTrue(N.det().equals(vf.complex(132.161, 8.06667), tolerance));
	System.out.println("Tr N\t=" + N.trace());
	assertTrue(N.trace().equals(vf.valueOf(1.35)));
	System.out.println("N^-1\t=" + N.inverse());
	System.out.print(M + "\n*\n" + N);
	System.out.println("=" + M.multiply(N));

	N = (Matrix) vf.valueOf("[2, 4]\n[-1, 0]\n[-1/2, 1/5]\n[2i+3, -1]");
	System.out.println("\nN := " + N+ "\n");
	System.out.println("norm ||N||\t=" + N.norm());
	assertTrue(N.norm().equals(vf.valueOf(5.94054), vf.valueOf(1e-5)));
	System.out.println("column sum norm\t=" + N.norm(1));
	assertTrue(N.norm(1).equals(vf.valueOf(7.10555), vf.valueOf(1e-5)));
	System.out.println("row sum norm\t=" + N.norm(Double.POSITIVE_INFINITY));
	assertTrue(N.norm(Double.POSITIVE_INFINITY).equals(vf.valueOf(6), tolerance));
	System.out.print(M + "\n*\n" + N);
	System.out.println("=" + M.multiply(N));
    }

}
