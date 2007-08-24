/**
 * @(#)FunctionTest.java 1.1 2002-03-24 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import junit.framework.*;
import junit.extensions.*;

import orbital.math.*;
import orbital.math.Vector;
import orbital.math.Integer;
import orbital.math.functional.*;
import com.wolfram.jlink.*;
import java.lang.reflect.*;
import java.util.*;
import java.awt.Dimension;
import orbital.util.*;

/**
 * Automatic test-driver checking (some parts of) orbital.math.functional.* against Mathematica.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo test Rational as well
 */
public class FunctionTest extends check.TestCase {
    private static final int  TEST_REPETITION = 20/**************20*/;
    private static final Values vf;
    static {
	// make format precision compatible with Mathematica for appropriate numerical comparisons
        MathUtilities.setDefaultPrecisionDigits(17);
	// set arbitrary precision as default (for adequate comparison with Mathematica)
	/*System.setProperty("orbital.math.Values.implementation",
			   orbital.moon.math.BigValuesImpl.class.getName());*/
	Map params = new HashMap();
	params.put("orbital.math.Scalar.precision", "big");
	Values.setDefault(Values.getInstance(params));
	vf = Values.getDefaultInstance();
    }

    private static final Real tolerance = vf.valueOf(1e-5);
    private static final int DSOLVE_PRECISION_DIGITS = 3;
    private static final int MAX_DSOLVE_DIM = 3;
    private static final int MAX_SYMBOLIC_DSOLVE_DIM = 7;
    private static final ArithmeticFormat mf = ArithmeticFormat.MATH_EXPORT_FORMAT;
        
    // the default matrix and vector (ddim.width) dimension
    private static final Dimension ddim = new Dimension(2,2);

    // test type bit mask constants
    public static final int TYPE_NONE = 0;

    public static final int TYPE_INTEGER  = 1;
    public static final int TYPE_RATIONAL = 2;
    public static final int TYPE_REAL     = 4;
    public static final int TYPE_COMPLEX  = 8;
    public static final int TYPE_SCALAR   = TYPE_INTEGER | TYPE_RATIONAL | TYPE_REAL | TYPE_COMPLEX;

    public static final int TYPE_VECTOR = 32;
    public static final int TYPE_MATRIX = 64;
    public static final int TYPE_TENSOR = 128 | TYPE_MATRIX | TYPE_VECTOR;

    public static final int TYPE_ALL = TYPE_SCALAR | TYPE_TENSOR;
    public static int       TYPE_DEFAULT = TYPE_SCALAR;
    public static final int TYPE_NUMERIC = TYPE_ALL;

    public static final int TYPE_SYMBOL = 1<<16;

    public static void main(String[] argv) {
	//try {new FunctionTest().testdSolve_fully_symbolic();}catch(Exception e) {e.printStackTrace();}
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        //@internal could perhaps use RepeatedTest for testCalculations
        TestSuite suite = new TestSuite(FunctionTest.class);
        return suite;
    }

    /*
    private final ArithmeticTestPatternGenerator random
    = new ArithmeticTestPatternGenerator(-1000, 1000, vf);*/

    private KernelLink ml;
    private final Random random = new Random();
    protected void setUp() {
    }
    
    protected void createMathLink() {
        /*
          (Windows)
          -linkmode launch -linkname 'c:/math40/mathkernel.exe'
        
          (Unix)
          -linkmode launch -linkname 'math -mathlink'
        */

        try {
            ml = MathLinkFactory.createKernelLink("-linkmode launch -linkname '"
                                                  + System.getProperty("com.wolfram.jlink.kernel")
                                                  + "'");
            ml.setComplexClass(ComplexAdapter.class);

            // Get rid of the initial InputNamePacket the kernel will send
            // when it is launched.
            ml.discardAnswer();

            // define our imaginary unit
	    //@xxx could have side effects for testdSolve
            ml.evaluate("i = I;");
            ml.discardAnswer();
        } catch (MathLinkException e) {
            throw new Error("Fatal error opening link: " + e.getMessage());
        }
    }
    protected void closeMathLink() {
	if (ml != null) {
	    ml.close();
	    ml = null;
	}
    }
    
    protected void tearDown() throws MathLinkException {
	closeMathLink();
    }


    public void testCalculations() {
	createMathLink();
	TYPE_DEFAULT = TYPE_INTEGER | /*TYPE_RATIONAL|*/ TYPE_REAL | TYPE_COMPLEX;
	final int scalarTypes = TYPE_DEFAULT;
        try {
            ml.evaluate("2+2");
            ml.waitForAnswer();
            int result = ml.getInteger();
            System.out.println("2 + 2 = " + result);

            ml.evaluate("2+2*(I+1)");
            ml.waitForAnswer();
            Complex result2 = ((ComplexAdapter) ml.getComplex()).getValue();
            System.out.println("2 + 2*(I+1) = " + result2 + "\t" + result2.getClass());
                        
            final double MIN = -1000;
            final double MAX = +1000;
            final double EPS = Double.longBitsToDouble(Double.doubleToLongBits(1.0)+1)-1.0;
            final double SMIN = -10;
            final double SMAX = +10;
            final double PI = Math.PI;
            System.err.println("epsilon = " + EPS + " = " + Long.toString(Double.doubleToLongBits(EPS), 16));
            System.err.println("1 + epsilon = " + (1 + EPS));
            System.err.println("-1 + epsilon = " + (-1 + EPS));
            System.err.println("1 - epsilon = " + (1 - EPS));
            System.err.println("(1 + epsilon/2) + epsilon/2= " + ((1 + EPS/2) + EPS/2));
                        
            //delta, logistic, reciprocal, sign
            //@todo id, zero with tensor once Functions.zero has been adapted
            testFunction("(#1)&",       Functions.id, MIN, MAX, TYPE_ALL, TYPE_ALL);
            testFunction("(1)&",        Functions.one, MIN, MAX, TYPE_ALL, TYPE_ALL);
            testFunction("(0)&",        Functions.zero, MIN, MAX, TYPE_ALL, TYPE_ALL);
            testFunction("Plus",        Operations.plus, MIN, MAX, TYPE_ALL, scalarTypes);
            testFunction("Plus",        Operations.plus, MIN, MAX, TYPE_TENSOR, scalarTypes);
            testFunction("Plus",        Operations.plus, MIN, MAX, TYPE_TENSOR, TYPE_REAL);
            testFunction("Subtract",    Operations.subtract, MIN, MAX, TYPE_ALL, scalarTypes);
            testFunction("Subtract",    Operations.subtract, MIN, MAX, TYPE_TENSOR, TYPE_REAL);
            testFunction("Times",       Operations.times, MIN, MAX, scalarTypes, scalarTypes);
            //@todo Operations.times with TYPE_TENSOR
            testFunction("Dot", Operations.times, MIN, MAX, TYPE_MATRIX, scalarTypes);
            testFunction("Dot", Operations.times, MIN, MAX, TYPE_MATRIX, TYPE_REAL);
            //testFunction("Divide",    Operations.divide, MIN, MAX, TYPE_REAL | TYPE_COMPLEX);
            //testFunction("Power",     Operations.power, MIN, MAX);
            testFunction("Minus",       Operations.minus, MIN, MAX, TYPE_ALL, scalarTypes);
            //testFunction("Inverse",   Operations.inverse, MIN, MAX, TYPE_MATRIX);
            try {
                testFunction("Exp",     Functions.exp, -10, 10, scalarTypes, TYPE_NONE);
                testFunction("Log",     Functions.log, EPS, MAX, scalarTypes, TYPE_NONE);
            }
            catch (AssertionError ignore) {
                ignore.printStackTrace();
            }
                        
            testFunction("Sqrt",        Functions.sqrt, 0, MAX, scalarTypes, TYPE_NONE);
            testFunction("Abs",         Functions.norm, MIN, MAX, TYPE_ALL, TYPE_ALL);
            testFunction("(#1^2)&",     Functions.square, MIN, MAX, scalarTypes | TYPE_MATRIX, scalarTypes);
            //testFunction("DiracDelta",Functions.diracDelta, MIN, MAX);

            testFunction("Sin",         Functions.sin, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Cos",         Functions.cos, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Tan",         Functions.tan, SMIN, SMAX, scalarTypes, TYPE_NONE); //...
            testFunction("Cot",         Functions.cot, -PI+EPS, -EPS, scalarTypes, TYPE_NONE); //...
            testFunction("Cot",         Functions.cot, EPS, PI-EPS, scalarTypes, TYPE_NONE);
            testFunction("Csc",         Functions.csc, EPS, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Csc",         Functions.csc, SMIN, EPS, scalarTypes, TYPE_NONE);
            testFunction("Sec",         Functions.sec, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Sinh",        Functions.sinh, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Cosh",        Functions.cosh, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Tanh",        Functions.tanh, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Coth",        Functions.coth, EPS, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Coth",        Functions.coth, SMIN, EPS, scalarTypes, TYPE_NONE);
            testFunction("Csch",        Functions.csch, SMIN, EPS, scalarTypes, TYPE_NONE);
            testFunction("Csch",        Functions.csch, EPS, SMAX, scalarTypes, TYPE_NONE);
            testFunction("Sech",        Functions.sech, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("ArcCos",      Functions.arccos, -1, 1, scalarTypes, TYPE_NONE);
            testFunction("ArcSin",      Functions.arcsin, -1, 1, scalarTypes, TYPE_NONE);
            //testFunction("ArcCot",    Functions.arccot, SMIN, SMAX, scalarTypes, TYPE_NONE);  // differs by PI for negative values. we return positive values
            testFunction("ArcTan",      Functions.arctan, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("ArcCosh",     Functions.arcosh, 1, SMAX, scalarTypes, TYPE_NONE);
            testFunction("ArcSinh",     Functions.arsinh, SMIN, SMAX, scalarTypes, TYPE_NONE);
            testFunction("ArcTanh",     Functions.artanh, -1+EPS, 1-EPS, TYPE_REAL | TYPE_COMPLEX, TYPE_NONE);
            System.out.println();
            System.out.println("PASSED");
        } catch (MathLinkException ex) {
            System.out.println();
            throw (RuntimeException) new RuntimeException("MathLinkException occurred: " + ex).initCause(ex);
        }
	finally {
	    closeMathLink();
	}
    }
        
    /**
     * @param testType the types of arguments to test.
     * @param componentType the types of components of the arguments to test (if testType includes any tensors).
     * @todo add tensor types
     */
    private void testFunction(String mFunction, Function jFunction, double min, double max, int testType, int componentType) throws MathLinkException {
        try {
            // Integer value test
            for (int i = 0; (testType & TYPE_INTEGER) != 0 && i < TEST_REPETITION; i++) {
                compareResults(mFunction, jFunction,
                               vf.valueOf(integerArgument((int)Math.ceil(min),(int)Math.floor(max))));
            }
    
            // Real value test
            for (int i = 0; (testType & TYPE_REAL) != 0 && i < TEST_REPETITION; i++)
                try {
                    compareResults(mFunction, jFunction,
                                   vf.valueOf(realArgument(min,max)));
                }
                catch (MathLinkException e) {
                    if (!"machine number overflow".equals(e.getMessage()))
                        throw e;
                }
                                
            // Complex value test
            for (int i = 0; (testType & TYPE_COMPLEX) != 0 && i < TEST_REPETITION; i++)
                try {
                    compareResults(mFunction, jFunction,
                                   vf.complex(realArgument(min,max), realArgument(min,max)));
                }
                catch (MathLinkException e) {
                    if (!"machine number overflow".equals(e.getMessage()))
                        throw e;
                }
        }
        catch (UnsupportedOperationException ignore) {}
    }

    /**
     * Compares the results of a function application
     * of Java and of Mathematica.
     */
    private void compareResults(String mFunction, Function jFunction, Scalar x) throws MathLinkException {
        final String  mFunctionCall = mFunction + "[" + x + "]";
        final String  jFunctionCall = jFunction + "[" + x + "]";
        ml.evaluate("N[" + mFunctionCall + "]");
        ml.waitForAnswer();
        System.out.print(mFunctionCall + " = ");
        Complex mresult;
//      if (ml.getType() == MathLink.MLTKSYM && "ComplexInfinity".equals(ml.getSymbol()))
//          mresult = Values.INFINITY;
//      else
            mresult = ((ComplexAdapter) ml.getComplex()).getValue();
        System.out.println(mresult);
        final Complex jresult = (Complex) jFunction.apply(x);
        System.out.println(jFunctionCall + " = " + jresult);
        boolean isSuccessful = jresult.equals(mresult, tolerance);
        assertTrue(isSuccessful , mFunctionCall + " = " + mresult + " != " + jFunctionCall + "@" + x.getClass() + " = " + jresult + "@" + jresult.getClass() + "\tdelta=" + jresult.subtract(mresult));
    }

    /**
     * @param testType the types of arguments to test.
     * @param componentType the types of components of the arguments to test (if testType includes any tensors).
     * @internal the possible range of arguments is a major problem.
     */
    private void testFunction(String mFunction, BinaryFunction jFunction, double min, double max, int testType, int componentType) throws MathLinkException {
        try {
            // Integer value test
            for (int i = 0; (testType & TYPE_INTEGER) != 0 && i < TEST_REPETITION; i++)
                try {
                    compareResults(mFunction, jFunction,
                                   vf.valueOf(integerArgument((int)Math.ceil(min),(int)Math.floor(max))),
                                   vf.valueOf(integerArgument((int)Math.ceil(min),(int)Math.floor(max))));
                }
                catch (MathLinkException e) {
                    if (!"machine number overflow".equals(e.getMessage()))
                        throw e;
                }
    
            // Real value test
            for (int i = 0; (testType & TYPE_REAL) != 0 && i < TEST_REPETITION; i++)
                try {
                    compareResults(mFunction, jFunction,
                                   vf.valueOf(realArgument(min,max)),
                                   vf.valueOf(realArgument(min,max)));
                }
                catch (MathLinkException e) {
                    if (!"machine number overflow".equals(e.getMessage()))
                        throw e;
                }
    
            // Complex value test
            for (int i = 0; (testType & TYPE_COMPLEX) != 0 && i < TEST_REPETITION; i++)
                try {
                    compareResults(mFunction, jFunction,
                                   vf.complex(realArgument(min,max),realArgument(min,max)),
                                   vf.complex(realArgument(min,max),realArgument(min,max)));
                }
                catch (MathLinkException e) {
                    if (!"machine number overflow".equals(e.getMessage()))
                        throw e;
                }

            // Vector value test
            for (int i = 0; (testType & TYPE_VECTOR) != 0 && i < TEST_REPETITION; i++)
                try {
                    final Vector jx = vectorArgument(min,max, componentType, ddim.width);
                    final Vector jy = vectorArgument(min,max, componentType, ddim.width);
                    final String  mFunctionCall = mFunction + "[" + listForm(jx) + "," + listForm(jy) + "]";
                    final String  jFunctionCall = jFunction + "[" + jx + "," + jy + "]";
                    ml.evaluate("N[" + mFunctionCall + "]");
                    ml.waitForAnswer();
                    System.out.print(mFunctionCall + " = ");
                    final Vector mresult = vf.valueOf(ComplexAdapter.unconvert((ComplexAdapter[])ml.getComplexArray1()));
                    System.out.println(mresult);
                    final Vector jresult = (Vector) jFunction.apply(jx, jy);
                    System.out.println(jFunctionCall + " = " + jresult);
                    assertTrue(jresult.equals(mresult, tolerance) , mFunctionCall + " = " + mresult + " != " + jFunctionCall + " = " + jresult + "\n\tdelta=" + jresult.subtract(mresult));
                }
                catch (MathLinkException e) {
                    if (!"machine number overflow".equals(e.getMessage()))
                        throw e;
                }

            // Matrix value test
            for (int i = 0; (testType & TYPE_MATRIX) != 0 && i < TEST_REPETITION; i++)
                try {
                    final Matrix jx = matrixArgument(min,max, componentType, ddim);
                    final Matrix jy = matrixArgument(min,max, componentType, ddim);
                    final String  mFunctionCall = mFunction + "[" + listForm(jx) + "," + listForm(jy) + "]";
                    final String  jFunctionCall = jFunction + "[" + jx + "," + jy + "]";
                    ml.evaluate("N[" + mFunctionCall + "]");
                    ml.waitForAnswer();
                    System.out.print(mFunctionCall + " = ");
                    final Matrix mresult = vf.valueOf(ComplexAdapter.unconvert((ComplexAdapter[][])ml.getComplexArray2()));
                    System.out.println(mresult);
                    final Matrix jresult = (Matrix) jFunction.apply(jx, jy);
                    System.out.println(jFunctionCall + " = " + jresult);
                    assertTrue(jresult.equals(mresult, tolerance) , mFunctionCall + " =\n" + mresult + "\n!=\n" + jFunctionCall + " =\n" + jresult + "\ndelta=\n" + jresult.subtract(mresult));
                }
                catch (MathLinkException e) {
                    if (!"machine number overflow".equals(e.getMessage()))
                        throw e;
                }
        }
        catch (UnsupportedOperationException ignore) {}
    }

    /**
     * Compares the results of a binary function application
     * of Java and of Mathematica.
     */
    private void compareResults(String mFunction, BinaryFunction jFunction, Scalar x, Scalar y) throws MathLinkException {
        final String  mFunctionCall = mFunction + "[" + x + "," + y + "]";
        final String  jFunctionCall = jFunction + "[" + x + "," + y + "]";
        try {
            ml.evaluate("N[" + mFunctionCall + "]");
            ml.waitForAnswer();
            System.out.print(mFunctionCall + " = ");
            final Complex mresult = ((ComplexAdapter) ml.getComplex()).getValue();
            System.out.println(mresult);
            final Complex jresult = (Complex) jFunction.apply(x, y);
            System.out.println(jFunctionCall + " = " + jresult);
            boolean isSuccessful = jresult.equals(mresult, tolerance);
            assertTrue(isSuccessful , mFunctionCall + " = " + mresult + " != " + jFunctionCall + " = " + jresult + "\tdelta=" + jresult.subtract(mresult));
        }
        catch (MathLinkException e) {
            if (!"machine number overflow".equals(e.getMessage()))
                throw e;
        }
    }


    // testing more sophisticated symbolic algorithms

    // testing differential equation solving

    /**
     * Successively solve all fully symbolic (constant) differential equations.
     */
    public void testdSolve_fully_symbolic()
	throws MathLinkException {
	createMathLink();
	try {
	    for (int n = 1; n < MAX_SYMBOLIC_DSOLVE_DIM; n++) {
		System.out.println("Fully symbolically solve differential equation of dimension " + n);
		final Dimension dim = new Dimension(n,n);
		final Real tau = vf.ZERO();
		Matrix A = vf.newInstance(dim);
		for (int i = 0; i < dim.height; i++)
		    for (int j = 0; j < dim.width; j++)
			A.set(i,j, vf.symbol("a" + i + j));
		// make strict upper diagonal matrix for nilpotence
		for (int i = 0; i < A.dimension().height; i++) {
		    for (int j = 0; j <= i && j < A.dimension().width; j++) {
			A.set(i, j, vf.ZERO());
		    }
		}
		Vector b = vf.newInstance(dim.height);
		for (int i = 0; i < b.dimension(); i++)
		    b.set(i, vf.symbol("b" + i));
		Vector eta = vf.newInstance(dim.height);
		for (int i = 0; i < eta.dimension(); i++)
		    eta.set(i, vf.symbol("x0" + i));
		checkdSolve(A,b,tau,eta);
	    }
	}
	catch (MathLinkException e) {
	    if (!"machine number overflow".equals(e.getMessage()))
		throw e;
	}
	finally {
	    closeMathLink();
	}
    }

    public void testdSolve_homogeneous_numeric_int() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
	casetestdSolve(-1000, +1000, TYPE_INTEGER /*| TYPE_REAL*/, true);
    }
    public void testdSolve_inhomogeneous_numeric_int() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
	casetestdSolve(-1000, +1000, TYPE_DEFAULT, false);
    }
    public void testdSolve_homogeneous_symbolic_int() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
	casetestdSolve(-1000, +1000, TYPE_SYMBOL, true);
    }
    public void testdSolve_inhomogeneous_symbolic_int() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
	casetestdSolve(-1000, +1000, TYPE_SYMBOL, false);
    }

    public void testdSolve_homogeneous_numeric() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER | TYPE_RATIONAL;
	casetestdSolve(-1000, +1000, TYPE_DEFAULT, true);
    }
    public void testdSolve_inhomogeneous_numeric() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER | TYPE_RATIONAL;
	casetestdSolve(-1000, +1000, TYPE_DEFAULT, false);
    }
    public void testdSolve_homogeneous_symbolic() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER | TYPE_RATIONAL;
	casetestdSolve(-1000, +1000, TYPE_SYMBOL, true);
    }
    public void testdSolve_inhomogeneous_symbolic() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER | TYPE_RATIONAL;
	casetestdSolve(-1000, +1000, TYPE_SYMBOL, false);
    }

    /*
    public void testdSolve_homogeneous_numeric_uni() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
	casetestdSolve(-1, +1, TYPE_DEFAULT, true);
    }
    public void testdSolve_inhomogeneous_numeric_uni() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
	casetestdSolve(-1, +1, TYPE_DEFAULT, false);
    }
    public void testdSolve_homogeneous_symbolic_uni() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
	casetestdSolve(-1, +1, TYPE_SYMBOL, true);
    }
    public void testdSolve_inhomogeneous_symbolic_uni() throws MathLinkException {
	FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
	casetestdSolve(-1, +1, TYPE_SYMBOL, false);
    }
    */
    
    protected void casetestdSolve(double MIN, double MAX, int componentType, boolean homogeneous)
	throws MathLinkException {
	createMathLink();
	try {
	    for (int rep = 0; rep < TEST_REPETITION; rep++) {
		final int n = integerArgument(1,MAX_DSOLVE_DIM);
		final Dimension dim = new Dimension(n,n);
		final Real tau = vf.ZERO();
		Matrix A = matrixArgument(MIN,MAX, componentType&TYPE_NUMERIC, dim);
		// make strict upper diagonal matrix for nilpotence
		for (int i = 0; i < A.dimension().height; i++) {
		    for (int j = 0; j <= i && j < A.dimension().width; j++) {
			A.set(i, j, vf.ZERO());
		    }
		}
		Vector b = homogeneous
		    ? vf.ZERO(n)
		    : vectorArgument(MIN,MAX, componentType, dim.height);
		Vector eta = vectorArgument(MIN,MAX, componentType, dim.height);
		checkdSolve(A,b,tau,eta);
	    }
	}
	catch (MathLinkException e) {
	    if (!"machine number overflow".equals(e.getMessage()))
		throw e;
	}
	finally {
	    closeMathLink();
	}
    }

    protected void checkdSolve(Matrix/*<R>*/ A, Vector/*<R>*/ b, Real tau, Vector/*<R>*/ eta)
	throws MathLinkException {
	try {
	    final Symbol t = vf.symbol("t");
	    System.out.println("Solving ODE x'(t) ==\n" + A + "*x(t) + " + b + "\n  with initial value " + eta + " at " + tau);
	    Function f = AlgebraicAlgorithms.dSolve(A, b, tau, eta);
	    System.out.println("  solution\t" + f);
	    System.out.println("  solution at " + t + " is " + f.apply(t));
	    
	    UnivariatePolynomial fc[] = AlgebraicAlgorithms.componentPolynomials((UnivariatePolynomial)f);
	    assertEquals("vectorial polynomial of component polynomials is the original",
			 f, AlgebraicAlgorithms.vectorialPolynomial(fc));

	    // compare results to Mathematica's DSolve
	    /*
	     * Generates essentially the following template
	     * Module[{A = {{4, -6}, {1, -1}},
	     *   eta = {2, 5},
	     *   X},
	     *  X[t_] = {x1[t], x2[t]};
	     *  X[t] /. DSolve[Join[MapThread[#1 == #2 &, {X'[t], A.X[t] + b}],
	     *      MapThread[#1 == #2 &, {X[0], eta}]
	     *      ],
	     *     X[t], t] [[1]]
	     *  ]	      
	     */
	    final String oursol = mf.format(f.apply(t));
	    // construct {x0[t], x1[t], ... x(n-1)[t]}
	    String dimensionspace = "{";
	    for (int i = 0; i < A.dimension().height; i++) {
		dimensionspace += "x" + i + "[" + t + "]";
		if (i + 1 < A.dimension().height)
		    dimensionspace += ",";
	    }

	    // as a reference get a solution of the same ODE from Mathematica
	    dimensionspace += "}";
	    String ode = "Module[{X}, X[" + t + "_] = " + dimensionspace + ";\n"
		+ "X[" + t + "] /.\n"
		+ "Simplify[\n"
		+ "DSolve[Join[\n"
		+ " MapThread[#1==#2&, {X'[" + t + "], (" + mf.format(A) + ").X[" + t + "] + " + mf.format(b) + "}],\n"
		+ " MapThread[#1==#2&, {X[" + tau + "], (" + mf.format(eta) + ")}]],\n"
		+ " X[" + t + "], " + t + "][[1]]\n"
		+ "]]";
	    ml.newPacket();
	    final String refsol = ml.evaluateToOutputForm("" + ode + "", 80);
	    ml.newPacket();
	    System.out.println("Our solution:\n" + oursol);
	    System.out.println("Ref.solution:\n" + refsol);

	    // verify in Mathematica that oursol really solves ODE
	    ml.newPacket();
	    ml.evaluate("{True}==Union[FullSimplify[N["
			+ " MapThread[#1==#2&,"
			+ "  {D[" + oursol +"," + t + "], (" + mf.format(A) + ").(" + oursol + ") + " + mf.format(b) + "}\n"
			+ "]\n"
			+ "]]]"
			);
	    ml.waitForAnswer();
	    final String solvesODE = ml.getExpr().toString();
	    ml.newPacket();
	    if (!solvesODE.equals("True")) {
		System.out.println("{True}==Union[FullSimplify["
				   + " MapThread[#1==#2&,"
				   + "  {D[" + oursol +"," + t + "], (" + mf.format(A) + ").(" + oursol + ") + " + mf.format(b) + "}\n"
				   + "]\n"
				   + "]]");
		System.out.println("Result: " + solvesODE);
		System.out.println("dSolve solution validation FAILED");
	    }
	    assertTrue(solvesODE.equals("True") , " dSolve solves ODE\n our solution:\n" + oursol + " \n ref.solution:\n" + refsol + "\n solves " + "x'==\n" + mf.format(A) + ".x + " + mf.format(b) + "\nwith initial value " + mf.format(eta) + "\nresulting in " + solvesODE);


	    // compare our solution and reference solution in Mathematica
	    ml.newPacket();
	    ml.evaluate("Simplify[(\n" + ode + " == \n" + oursol + ")]");
	    ml.waitForAnswer();
	    final String comparison = ml.getExpr().toString();
	    ml.newPacket();
	    if (!comparison.equals("True")) {
		System.out.println("Simplify[(\n" + ode + " == \n" + oursol + ")]");
		System.out.println("Result: " + comparison);
		System.out.println("dSolve comparison validation FAILED");
	    }
	    //assertTrue(comparison.equals("True") , " dSolve equivalence:\n " + oursol + "\n x'==\n" + mf.format(A) + ".x + " + mf.format(b) + "\nwith initial value " + mf.format(ta) + "\nreference solution:\n" + refsol + "\nresulting in " + comparison);
	}
	catch (MathLinkException e) {
	    if (!"machine number overflow".equals(e.getMessage()))
		throw e;
	}
    }


    // particular examples
    public void testdSolveExamples() throws MathLinkException {
	createMathLink();	
	try {
	    System.out.println("solving differential equations");
	    final Real tau = vf.ZERO();
	    Matrix A = vf.valueOf(new double[][] {
		{0}
	    });
	    Vector b = vf.valueOf(new Arithmetic[]{vf.valueOf(2)});
	    Vector eta = vf.valueOf(new Symbol[]{vf.symbol("x0")});
	    checkdSolve(A, b, tau, eta);

	    A = vf.valueOf(new double[][] {
		{0,1},
		{0,0}
	    });
	    b = vf.valueOf(new double[]{0,0});
	    eta = vf.valueOf(new double[]{0,0});
	    checkdSolve(A, b, tau, eta);


	    eta = vf.valueOf(new double[]{1,2});
	    checkdSolve(A, b, tau, eta);
	
	    eta = vf.valueOf(new Symbol[]{vf.symbol("z0"),vf.symbol("v0")});
	    checkdSolve(A, b, tau, eta);

	    A = vf.valueOf(new double[][] {
		{0,1,0},
		{0,0,1},
		{0,0,0},
	    });
	    b = vf.valueOf(new double[]{0,0,0});
	    eta = vf.valueOf(new Symbol[]{vf.symbol("z0"),vf.symbol("v0"),vf.symbol("a")});
	    System.out.println("train dynamics with constant acceleration a as x3 and initial values of position, velocity and acceleration " + eta);
	    checkdSolve(A, b, tau, eta);


	    A = vf.valueOf(new double[][] {
		{0,1,2},
		{0,0,1},
		{0,0,0},
	    });
	    b = vf.valueOf(new double[]{0,0,0});
	    eta = vf.valueOf(new double[]{1,2,3});
	    checkdSolve(A, b, tau, eta);


	    A = vf.valueOf(new double[][] {
		{0,1},
		{0,0}
	    });
	    b = vf.valueOf(new Arithmetic[]{vf.ZERO,vf.symbol("a")});
	    eta = vf.valueOf(new Symbol[]{vf.symbol("z0"),vf.symbol("v0")});
	    System.out.println("train dynamics with constant acceleration a as inhomogeneous part and initial value " + eta);
	    checkdSolve(A, b, tau, eta);

	    A = vf.valueOf(new double[][] {
		{0,1,0,0},
		{0,0,1,0},
		{0,0,0,1},
		{0,0,0,0},
	    });
	    b = vf.valueOf(new Arithmetic[]{vf.ZERO,vf.ZERO,vf.ZERO,vf.symbol("b")});
	    eta = vf.valueOf(new Symbol[]{vf.symbol("a1"),vf.symbol("a2"),vf.symbol("a3"),vf.symbol("a4")});
	    checkdSolve(A, b, tau, eta);
	}
	catch (MathLinkException e) {
	    if (!"machine number overflow".equals(e.getMessage()))
		throw e;
	}
	finally {
	    closeMathLink();
	}
    }
    

    // create (random) argument values
    
    private int integerArgument(int min, int max) {
        return min + random.nextInt(max-min + 1);
    }
    private double realArgument(double min, double max) {
        return ((max-min) * random.nextDouble() + min);
    }
    private int symbolId = 1;
    private Arithmetic randomArgument(double min, double max, int testType) {
	if ((testType & (TYPE_INTEGER|TYPE_COMPLEX|TYPE_SYMBOL)) == 0)
	    // default type if no type that we could possible support/deliver
	    testType = TYPE_DEFAULT;
        if ((testType & TYPE_INTEGER) != 0 && Utility.flip(random, 0.4))
            return vf.valueOf(integerArgument((int)min, (int)max));
        else if ((testType & TYPE_COMPLEX) != 0 && Utility.flip(random, 0.4))
            return vf.complex(realArgument(min, max), realArgument(min, max));
	else if ((testType & TYPE_RATIONAL) != 0 && Utility.flip(random, 0.4))
	    return vf.rational(integerArgument((int)min, (int)max), integerArgument(1, (int)max));
        else if ((testType & TYPE_SYMBOL) != 0 && Utility.flip(random, 0.4))
            return vf.symbol("a" + (symbolId++));
        else { // random generator always said no, then choose first applying type
	    if ((testType & TYPE_INTEGER) != 0)
		return vf.valueOf(integerArgument((int)min, (int)max));
	    else if ((testType & TYPE_COMPLEX) != 0)
		return vf.complex(realArgument(min, max), realArgument(min, max));
	    //      else if ((testType & TYPE_RATIONAL) != 0)
	    //          return vf.rational(integerArgument((int)min, (int)max), integerArgument(1, (int)max));
	    else if ((testType & TYPE_SYMBOL) != 0)
		return vf.symbol("a" + (symbolId++));
	    else
		throw new IllegalArgumentException("no type provided");
        }
    }
    private Matrix matrixArgument(double min, double max, int testType, Dimension dim) {
        Matrix x = vf.newInstance(dim);
        if (testType == TYPE_REAL && Utility.flip(random, 0.5))
            // randomly switch to RMatrix
            x = vf.valueOf(new double[dim.height][dim.width]);
        for (int i = 0; i < dim.height; i++)
            for (int j = 0; j < dim.width; j++)
                x.set(i,j, randomArgument(min, max, testType));
        return x;
    }
    private Vector vectorArgument(double min, double max, int testType, int dim) {
        Vector x = vf.newInstance(dim);
        if (testType == TYPE_REAL && Utility.flip(random, 0.5))
            // randomly switch to RVector
            x = vf.valueOf(new double[dim]);
        for (int i = 0; i < dim; i++)
            x.set(i, randomArgument(min, max, testType));
        return x;
    }

    // Helpers

    /**
     * Adapter class between orbital.math.* and J/Link.
     * @structure delegate value:Complex
     */
    public static class ComplexAdapter {
        private Complex value;
        public ComplexAdapter(double re, double im) {
            value = vf.complex(re,im);
        }
                
        public static final Complex[] unconvert(ComplexAdapter[] v) {
            Complex[] r = new Complex[v.length];
            for (int i = 0; i < r.length; i++)
                r[i] = v[i].getValue();
            return r;
        }
        public static final Complex[][] unconvert(ComplexAdapter[][] v) {
            Complex[][] r = new Complex[v.length][v[0].length];
            for (int i = 0; i < r.length; i++)
                for (int j = 0; j < r[i].length; j++)
                    r[i][j] = v[i][j].getValue();
            return r;
        }
                
        /**
         * Whether we are equal to another ComplexAdapter or Complex.
         */
        public boolean equals(Object o) {
            return o instanceof ComplexAdapter && value.equals(((ComplexAdapter)o).value)
                || Complex.isa.apply(o) && value.equals(o);
        }
                
        public double re() {
            return value.re().doubleValue();
        }

        public double im() {
            return value.im().doubleValue();
        }
            
        public Complex getValue() {
            return value;
        }
            
        public String toString() {
            return value.toString();
        }
    }
        
    private String listForm(Matrix m) {
        String           nl = " ";//System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (int i = 0; i < m.dimension().height; i++) {
            sb.append((i == 0 ? "" : "," + nl) + '{');
            for (int j = 0; j < m.dimension().width; j++)
                sb.append((j == 0 ? "" : ",\t") + m.get(i, j));
            sb.append('}');
        } 
        sb.append('}');
        return sb.toString();
    }

    private String listForm(Vector m) {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (int i = 0; i < m.dimension(); i++)
            sb.append((i == 0 ? "" : ", ") + m.get(i));
        sb.append('}');
        return sb.toString();
    }
}
