/**
 * @(#)AbstractMatrixTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import junit.framework.*;
import java.util.Random;

import java.awt.Dimension;

/**
 * A sample test case, testing .
 * @version 1.1, 2002-09-14
 */
public class AbstractMatrixTest extends check.TestCase {
    private static final int TEST_REPETITION = 20;
    private Values vf;
    private Real tolerance;
    private Matrix/*<Scalar>*/ M1;
    private Matrix/*<Complex>*/ M2;
    private Matrix M3;
    private Vector/*<Rational>*/ v1, v2;
    private Vector v3;

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(AbstractMatrixTest.class);
    }
    protected void setUp() {
	vf = Values.getDefaultInstance();
	random = new Random();
	tolerance = vf.valueOf(1e-6);
	//@xxx class Debug produces an error with gjc error: type parameter orbital.math.Arithmetic[] is not within its bound orbital.math.Arithmetic
	// this is because he confuses vf.valueOf(R[]) with vf.valueOf(R[][]) although R is bound to be orbital.math.Arithmetic
	M1 = vf.valueOf(new Scalar[][] {
	    {vf.valueOf(2), vf.rational(3, 4), vf.rational(-1, 2)},
	    {vf.rational(3, 4), vf.rational(1, 2), vf.valueOf(-1)},
	    {vf.rational(-1, 2), vf.valueOf(0), vf.rational(1)}
	});
	v1 = vf.valueOf(new Rational[] {
	    vf.valueOf(1), vf.rational(-1, 3), vf.rational(1, 2)
	});
	M2 = vf.valueOf(new Complex[][] {
	    {vf.complex(1, 2), vf.complex(2, -1)},
	    {vf.complex(1, -2), vf.complex(-1, -1)}
	});
	v2 = vf.valueOf(new Scalar[] {
	    vf.valueOf(1), vf.complex(1, 2)
	});
	M3 = vf.valueOf(new Arithmetic[][] {
	    {vf.valueOf(2), vf.rational(3, 4)},
	    {vf.rational(-1, 2), vf.valueOf(0)}
	});
	v3 = vf.valueOf(new Arithmetic[] {
	    vf.valueOf(1), vf.rational(-1, 3)
	});
    }

    public void testMixedTypeMatrix() {
	Matrix M = M1;
	Vector v = v1;
	System.out.println(M + "*" + v + "=" + M.multiply(v));
	System.out.println("|M|=" + M.det() + "\t||M||=" + M.norm() + "\tM^-1=\n" + M.inverse());
    }
    public void testComplexMatrix() {
	Matrix M = M2;
	Vector v = v2;
	System.out.println(M + "*" + v + "=" + M.multiply(v));
	System.out.println("|M|=" + M.det() + "\t||M||=" + M.norm() + "\tM^-1=\n" + M.inverse());
    }
    public void testHyperMatrix() {
	/*Matrix M3 = vf.valueOf(new Arithmetic[][] {
	  {vf.valueOf(2), vf.valueOf(3)},
	  {vf.valueOf(-3), vf.valueOf(2)}
	  });
	  Matrix M4 = vf.valueOf(new Arithmetic[][] {
	  {vf.rational(1, 3), vf.valueOf(2)},
	  {vf.rational(-3, 2), vf.rational(-2, 4)}
	  });
	  Matrix hypermatrix = vf.valueOf(new Arithmetic[][] {
	  {M.subMatrix(0,1, 0,1), M2},
	  {M3, M4}
	  });
	  System.out.println(hypermatrix + "^-1 =");
	  System.out.println("Hypermatrices cannot be inverted, yet");
	  System.out.println(hypermatrix.inverse());*/
    } 

    // (partial) assertion condition checks
    public void testAssertConditions() throws Exception {
	Matrix M = M3;
	Vector v = v3;
	for (int i = 0; i < 2; i++) {
	    System.out.println("reference behaviour part " + i);
	    Scalar s = vf.valueOf(-2);
	    Matrix B = (Matrix) M.clone();
	    Vector b = (Vector) v.clone();
	    assertTrue( M != B && M.equals(B) , "clone");
	    assertTrue( v != b && v.equals(b) , "clone");

	    assertTrue( M.toArray() != M.toArray() , "cloned toArray");
	    assertTrue( v.toArray() != v.toArray() , "cloned toArray");
    			
	    System.out.println("reference behaviour: arithmetic operations");
	    System.out.println(M + "*" + v + "=" + M.multiply(v));
	    assertTrue( M.equals(B) , "immutable multiply");
	    System.out.println(M + "*" + s + "=" + M.multiply(s));
	    assertTrue( M.equals(B) , "immutable multiply");
	    System.out.println(M + "*" + M + "=" + M.multiply(M));
	    assertTrue( M.equals(B) , "immutable multiply");

	    System.out.println("reference behaviour: structure changes");
	    assertTrue( M.removeRow(1).equals(M) , "return this");
	    System.out.println("removed row\n" + M);
	    assertTrue( !M.equals(B) , "structure change mutates");
	    Matrix B2 = (Matrix) M.clone();
	    assertTrue( M.removeColumn(1).equals(M) , "return this");
	    System.out.println("removed column\n" + M);
	    assertTrue( !M.equals(B2) , "structure change mutates");
	    System.out.println(M + "*" + M + "=" + M.multiply(M));
	    assertTrue( !M.equals(B2) , "structure change mutates");
	    assertTrue( M.insertColumns(M).equals(M) , "return this");
	    System.out.println("appended columns\n" + M);
	    assertTrue( !M.equals(B2) , "structure change mutates");
	    assertTrue( M.insertColumns(M).equals(M) , "return this");
	    System.out.println("appended columns\n" + M);
	    assertTrue( !M.equals(B2) , "structure change mutates");

	    assertTrue( v.remove(0).equals(v) , "return this");
	    System.out.println("removed " + v);
	    assertTrue( !v.equals(b) , "structure change mutates");
	    assertTrue( v.insert(vf.valueOf(7)).equals(v) , "return this");
	    System.out.println("appended " + v);
	    // 		assertTrue( !v.equals(b) , "structure change mutates";
	    // 		assertTrue( v.insert(v).equals(v) , "return this";
	    // 		System.out.println("appended " + v);
	    // 		assertTrue( !v.equals(b) , "structure change mutates";
    			
	    M = (Matrix) B.clone();
	    v = (Vector) b.clone();
	    assertTrue( M != B && M.equals(B) , "clone");
	    assertTrue( v != b && v.equals(b) , "clone");
    			
	    System.out.println("reference behaviour: sub-view");
	    v = M.getColumn(1);
	    System.out.println(M + ", column " + v);
	    v.set(1, vf.valueOf(42));
	    assertTrue( !v.equals(b) && !M.equals(B) , "sub-view modifications write through");
	    System.out.println(M + ", column " + v);

	    B2 = (Matrix) M.clone();
	    v = M.getRow(0);
	    System.out.println(M + ", row " + v);
	    v.set(1, vf.valueOf(-42));
	    assertTrue( !v.equals(b) && !M.equals(B2) , "sub-view modifications write through");
	    System.out.println(M + ", row " + v);
    			
	    M = (Matrix) B.clone();
	    B2 = (Matrix) M.clone();
	    System.out.println("appending columns\n" + M + "\nto\n" + M + " ...");
	    boolean bok1 = M.insertColumns(M).equals(M);
	    assertTrue( bok1 , "return this");
	    System.out.println("... is\n" + M);
	    assertTrue( !M.equals(B2) , "structure change mutates");

	    B2 = (Matrix) M.clone();
	    Matrix N = M.subMatrix(0,1, 1,3);
	    assertTrue( !N.equals(M) , "sub-view different");
	    System.out.println("Matrix\n" + M + ", sub-view\n" + N);
	    N.set(1, 1, vf.NEGATIVE_INFINITY);
	    System.out.println("Matrix\n" + M + ", sub-view\n" + N);
	    assertTrue( !M.equals(B2) , "sub-view modifications write through");

	    B2 = (Matrix) M.clone();
	    v = M.getRow(0);
	    System.out.println(M + ", row " + v);
	    v.set(1, vf.valueOf(-444));
	    assertTrue( !v.equals(b) && !M.equals(B2) , "sub-view modifications write through");
	    System.out.println(M + ", row " + v);

	    M = new RMatrix(((AbstractMatrix)B).toDoubleArray());
	    v = new RVector(((AbstractVector)b).toDoubleArray());
	}
    }

    /**
     * Tests det(), trace() for invariance under conjugation by
     * T^-1.A.T
     */
    public void testConjugationInvariance() {
	final double MIN = -40;
	final double MAX = 40;
	for (int i = 0; i < TEST_REPETITION; i++) {
	    int dim = integerArgument(1, 6).intValue();
	    //@todo how to construct other invertible matrices (Basisergänzungssatz?)
	    Matrix T = vf.DIAGONAL(vectorArgument(dim, MIN, MAX));
	    assert T.isInvertible() : "T constructed to be invertible";
	    Matrix A = matrixArgument(dim,dim, MIN, MAX);

	    Matrix conjA = (Matrix) T.inverse().multiply(A).multiply(T);
	    assertTrue(A.trace().equals(conjA.trace(), tolerance) , "trace is invariant to conjugation");
	    assertTrue(A.det().equals(conjA.det(), tolerance) , "det is invariant to conjugation");
	}
    }

    //@internal almost identical to @see test/orbital.math.functional.FunctionTest, @todo reuse
    // create (random) argument values

    private Random random;
    private Integer integerArgument(int min, int max) {
	return vf.valueOf(min + random.nextInt(max-min));
    }
    private Real realArgument(double min, double max) {
	return vf.valueOf(((max-min) * random.nextDouble() + min));
    }
    private Matrix matrixArgument(int height, int width, double min, double max) {
	Dimension dim = new Dimension(height, width);
	Matrix x = vf.newInstance(dim);
	for (int i = 0; i < dim.height; i++)
	    for (int j = 0; j < dim.width; j++)
		x.set(i,j, realArgument(min,max));
	return x;
    }
    private Vector vectorArgument(int dim, double min, double max) {
	Vector x = vf.newInstance(dim);
	for (int i = 0; i < dim; i++)
	    x.set(i, realArgument(min, max));
	return x;
    }
}
