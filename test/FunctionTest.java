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
 * Automatical test-driver checking (some parts of) orbital.math.functional.* against Mathematica.
 *
 * @version 1.0, 2002/03/24
 * @author  Andr&eacute; Platzer
 * @todo test Rational as well
 */
public class FunctionTest {
    private static final int  TEST_REPETITION = 20;
    private static final Real tolerance = Values.valueOf(1e-10);
	
    // test type bit mask constants
    public static final int	  TYPE_INTEGER = 1;
    public static final int	  TYPE_REAL = 2;
    public static final int	  TYPE_COMPLEX = 4;
    public static final int	  TYPE_SCALAR = TYPE_INTEGER | TYPE_REAL | TYPE_COMPLEX;
    public static final int	  TYPE_VECTOR = 32;
    public static final int	  TYPE_MATRIX = 64;
    public static final int	  TYPE_MV = TYPE_MATRIX | TYPE_VECTOR;
    public static final int	  TYPE_ALL = TYPE_SCALAR | TYPE_MV;
    public static final int	  TYPE_DEFAULT = TYPE_SCALAR;

    public static void main(String[] argv) {
	new FunctionTest().run();
    }

    private KernelLink ml;
    private Random random = new Random();
    public FunctionTest() {
        /*
	  (Windows)
	  java SampleProgram -linkmode launch -linkname 'c:/math40/mathkernel.exe'
        
	  (Unix)
	  java SampleProgram -linkmode launch -linkname 'math -mathlink'
        */

	try {
	    ml = MathLinkFactory.createKernelLink("-linkmode launch -linkname 'd:/progra~1/math/MathKernel.exe'");
	    ml.setComplexClass(ComplexAdapter.class);

	    // Get rid of the initial InputNamePacket the kernel will send
	    // when it is launched.
	    ml.discardAnswer();

	    // define our imaginary unit
	    ml.evaluate("i = I;");
	    ml.discardAnswer();
	} catch (MathLinkException e) {
	    throw new Error("Fatal error opening link: " + e.getMessage());
	}
    }
    
    public void run() {
    	MathUtilities.setDefaultPrecisionDigits(17);
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
	    final double EPS = Double.MIN_VALUE;
	    final double SMIN = -10;
	    final double SMAX = +10;
			
	    //delta, logistic, reciprocal, sign
	    testFunction("(#1)&",		Functions.id, MIN, MAX);
	    testFunction("(1)&",		Functions.one, MIN, MAX);
	    testFunction("(0)&",		Functions.zero, MIN, MAX);
	    testFunction("Plus",	Operations.plus, MIN, MAX, TYPE_ALL, TYPE_SCALAR);
	    testFunction("Plus",	Operations.plus, MIN, MAX, TYPE_MV, TYPE_REAL);
	    testFunction("Subtract",	Operations.subtract, MIN, MAX, TYPE_ALL, TYPE_SCALAR);
	    testFunction("Subtract",	Operations.subtract, MIN, MAX, TYPE_MV, TYPE_REAL);
	    testFunction("Times",	Operations.times, MIN, MAX, TYPE_SCALAR, TYPE_SCALAR);
	    testFunction("Dot",	Operations.times, MIN, MAX, TYPE_MATRIX, TYPE_SCALAR);
	    testFunction("Dot",	Operations.times, MIN, MAX, TYPE_MATRIX, TYPE_REAL);
	    //testFunction("Divide",	Operations.divide, MIN, MAX, TYPE_REAL | TYPE_COMPLEX);
	    //testFunction("Power",	Operations.power, MIN, MAX);
	    testFunction("Minus",	Operations.minus, MIN, MAX);
	    //testFunction("Inverse",	Operations.inverse, MIN, MAX, TYPE_MATRIX);
	    try {
		testFunction("Exp",		Functions.exp, -30, 30);
		testFunction("Log",		Functions.log, EPS, MAX);
	    }
	    catch (AssertionError ignore) {
		ignore.printStackTrace();
	    }
			
	    testFunction("Sqrt",		Functions.sqrt, 0, MAX);
	    testFunction("Abs",		Functions.norm, MIN, MAX);
	    testFunction("(#1^2)&",		Functions.square, MIN, MAX);
	    //testFunction("DiracDelta",	Functions.diracDelta, MIN, MAX);

	    testFunction("Sin",		Functions.sin, SMIN, SMAX);
	    testFunction("Cos",		Functions.cos, SMIN, SMAX);
	    testFunction("Tan",		Functions.tan, SMIN, SMAX); //...
	    testFunction("Cot",		Functions.cot, SMIN, SMAX); //...
	    testFunction("Csc",		Functions.csc, EPS, SMAX);
	    testFunction("Csc",		Functions.csc, SMIN, EPS);
	    testFunction("Sec",		Functions.sec, SMIN, SMAX);
	    testFunction("Sinh",	Functions.sinh, SMIN, SMAX);
	    testFunction("Cosh",	Functions.cosh, SMIN, SMAX);
	    testFunction("Tanh",	Functions.tanh, SMIN, SMAX);
	    testFunction("Coth",	Functions.coth, EPS, SMAX);
	    testFunction("Coth",	Functions.coth, SMIN, EPS);
	    testFunction("Csch",	Functions.csch, SMIN, EPS);
	    testFunction("Csch",	Functions.csch, EPS, SMAX);
	    testFunction("Sech",	Functions.sech, SMIN, SMAX);
	    testFunction("ArcCos",	Functions.arccos, -1, 1);
	    testFunction("ArcSin",	Functions.arcsin, -1, 1);
	    //testFunction("ArcCot",	Functions.arccot, SMIN, SMAX);	// differs by PI for negative values. we return positive values
	    testFunction("ArcTan",	Functions.arctan, SMIN, SMAX);
	    testFunction("ArcCosh",	Functions.arcosh, 1, SMAX);
	    testFunction("ArcSinh",	Functions.arsinh, SMIN, SMAX);
	    testFunction("ArcTanh",	Functions.artanh, -1+EPS, 1-EPS);
	    System.out.println();
	    System.out.println("PASSED");
	} catch (MathLinkException e) {
	    System.out.println();
	    throw (RuntimeException) new RuntimeException("MathLinkException occurred: " + e.getMessage()).initCause(e);
	} finally {
	    ml.close();
	}
    }
	
    /**
     * @internal the possible range of arguments is a major problem.
     */
    private void testFunction(String mFunction, Function jFunction, double min, double max) throws MathLinkException {
	try {
	    // Integer value test
	    /*for (int i = 0; i < TEST_REPETITION; i++) {
	      final int	  x = integerArgument(min,max);
	      final Integer jx = Values.valueOf(x);
	      compareResults(mFunction, jFunction, jx);
	      final String  mFunctionCall = mFunction + "[" + x + "]";
	      final String  jFunctionCall = mFunction + "[" + x + "]";
	      ml.evaluate(mFunctionCall);
	      ml.waitForAnswer();
	      System.out.print(mFunctionCall + " = ");
	      final double mresult = ml.getDouble();
	      System.out.println(mresult);
	      final Real jresult = (Real) jFunction.apply(jx);
	      System.out.println(jFunctionCall + " = " + jresult);
	      if (!jresult.equals(Values.valueOf(mresult), tolerance))
	      compareResults(mFunction, jFunction, jx, false);
	      else if (Math.abs(jresult.doubleValue() - mresult) >= tolerance.doubleValue())
	      compareResults(mFunction, jFunction, jx, false);
	      else
	      compareResults(mFunction, jFunction, jx, true);
	      }*/
    
	    // Real value test
	    for (int i = 0; i < TEST_REPETITION; i++)
		try {
		    final double x = realArgument(min,max);
		    final Real	 jx = Values.valueOf(x);
		    final String mFunctionCall = mFunction + "[" + x + "]";
		    final String jFunctionCall = jFunction + "[" + jx + "]";
		    ml.evaluate(mFunctionCall);
		    ml.waitForAnswer();
		    System.out.print(mFunctionCall + " = ");
		    final double mresult = ml.getDouble();
		    System.out.println(mresult);
		    final Real jresult = (Real) jFunction.apply(jx);
		    System.out.println(jFunctionCall + " = " + jresult);
		    if (!jresult.equals(Values.valueOf(mresult), tolerance))
			compareResults(mFunction, jFunction, jx, false);
		    else if (Math.abs(jresult.doubleValue() - mresult) >= tolerance.doubleValue())
			compareResults(mFunction, jFunction, jx, false);
		    else
			compareResults(mFunction, jFunction, jx, true);
		}
		catch (MathLinkException e) {
		    if (!"machine number overflow".equals(e.getMessage()))
			throw e;
		}
    				
	    // Complex value test
	    for (int i = 0; i < TEST_REPETITION; i++)
		try {
		    final double  x = realArgument(min,max);
		    final double  y = realArgument(min,max);
		    final Complex jx = Values.complex(x, y);
		    final String  mFunctionCall = mFunction + "[" + x + " + I*" + y + "]";
		    final String  jFunctionCall = jFunction + "[" + jx + "]";
		    ml.evaluate(mFunctionCall);
		    ml.waitForAnswer();
		    System.out.print(mFunctionCall + " = ");
		    final Complex mresult = ((ComplexAdapter) ml.getComplex()).getValue();
		    System.out.println(mresult);
		    final Complex jresult = (Complex) jFunction.apply(jx);
		    System.out.println(jFunctionCall + " = " + jresult);
		    if (!jresult.equals(mresult, tolerance))
			compareResults(mFunction, jFunction, jx, false);
		    else
			compareResults(mFunction, jFunction, jx, true);
		}
		catch (MathLinkException e) {
		    if (!"machine number overflow".equals(e.getMessage()))
			throw e;
		}
    	}
    	catch (UnsupportedOperationException ignore) {}
    }
    private void compareResults(String mFunction, Function jFunction, Scalar x, boolean wasSuccessful) throws MathLinkException {
	final String  mFunctionCall = mFunction + "[" + x + "]";
	final String  jFunctionCall = jFunction + "[" + x + "]";
	ml.evaluate(mFunctionCall);
	ml.waitForAnswer();
	System.out.print(mFunctionCall + " = ");
	final Complex mresult = ((ComplexAdapter) ml.getComplex()).getValue();
	System.out.println(mresult);
	final Complex jresult = (Complex) jFunction.apply(x);
	System.out.println(jFunctionCall + " = " + jresult);
	boolean isSuccessful = jresult.equals(mresult, tolerance);
	if (isSuccessful != wasSuccessful)
	    throw new InternalError("this should not happen");
	if (!isSuccessful)
	    throw new AssertionError(mFunctionCall + " = " + mresult + " != " + jFunctionCall + " = " + jresult + "\tdelta=" + jresult.subtract(mresult));
    }

    /**
     * @internal the possible range of arguments is a major problem.
     */
    private void testFunction(String mFunction, BinaryFunction jFunction, double min, double max, int testType, int componentType) throws MathLinkException {
	try {
	    // Integer value test
	    for (int i = 0; (testType & TYPE_INTEGER) != 0 && i < TEST_REPETITION; i++)
		try {
		    final int	  x = integerArgument((int)min,(int)max);
		    final int	  y = integerArgument((int)min,(int)max);
		    final Integer jx = Values.valueOf((long)x);
		    final Integer jy = Values.valueOf((long)y);
		    final String  mFunctionCall = mFunction + "[" + x + "," + y + "]";
		    final String  jFunctionCall = jFunction + "[" + jx + "," + jy + "]";
		    ml.evaluate(mFunctionCall);
		    ml.waitForAnswer();
		    System.out.print(mFunctionCall + " = ");
		    final double mresult = ml.getDouble();
		    System.out.println(mresult);
		    final Real jresult = (Real) jFunction.apply(jx, jy);
		    System.out.println(jFunctionCall + " = " + jresult);
		    if (!jresult.equals(Values.valueOf(mresult), tolerance))
			compareResults(mFunction, jFunction, jx, jy, false);
		    else if (Math.abs(jresult.doubleValue() - mresult) >= tolerance.doubleValue())
			compareResults(mFunction, jFunction, jx, jy, false);
		    else
			compareResults(mFunction, jFunction, jx, jy, true);
		}
		catch (MathLinkException e) {
		    if (!"machine number overflow".equals(e.getMessage()))
			throw e;
		}
    
	    // Real value test
	    for (int i = 0; (testType & TYPE_REAL) != 0 && i < TEST_REPETITION; i++)
		try {
		    final double x = realArgument(min,max);
		    final double y = realArgument(min,max);
		    final Real	 jx = Values.valueOf(x);
		    final Real	 jy = Values.valueOf(y);
		    final String  mFunctionCall = mFunction + "[" + x + "," + y + "]";
		    final String  jFunctionCall = jFunction + "[" + jx + "," + jy + "]";
		    ml.evaluate(mFunctionCall);
		    ml.waitForAnswer();
		    System.out.print(mFunctionCall + " = ");
		    final double mresult = ml.getDouble();
		    System.out.println(mresult);
		    final Real jresult = (Real) jFunction.apply(jx, jy);
		    System.out.println(jFunctionCall + " = " + jresult);
		    if (!jresult.equals(Values.valueOf(mresult), tolerance))
			compareResults(mFunction, jFunction, jx, jy, false);
		    else if (Math.abs(jresult.doubleValue() - mresult) >= tolerance.doubleValue())
			compareResults(mFunction, jFunction, jx, jy, false);
		    else
			compareResults(mFunction, jFunction, jx, jy, true);
		}
		catch (MathLinkException e) {
		    if (!"machine number overflow".equals(e.getMessage()))
			throw e;
		}
    
	    // Complex value test
	    for (int i = 0; (testType & TYPE_COMPLEX) != 0 && i < TEST_REPETITION; i++)
		try {
		    final double  x1 = realArgument(min,max);
		    final double  x2 = realArgument(min,max);
		    final double  y1 = realArgument(min,max);
		    final double  y2 = realArgument(min,max);
		    final Complex jx = Values.complex(x1, x2);
		    final Complex jy = Values.complex(y1, y2);
		    final String  mFunctionCall = mFunction + "[" + x1 + " + I*" + x2 + "," + y1 + " + I*" + y2 + "]";
		    final String  jFunctionCall = jFunction + "[" + jx + "," + jy + "]";
		    ml.evaluate(mFunctionCall);
		    ml.waitForAnswer();
		    System.out.print(mFunctionCall + " = ");
		    final Complex mresult = ((ComplexAdapter) ml.getComplex()).getValue();
		    System.out.println(mresult);
		    final Complex jresult = (Complex) jFunction.apply(jx, jy);
		    System.out.println(jFunctionCall + " = " + jresult);
		    compareResults(mFunction, jFunction, jx, jy, jresult.equals(mresult, tolerance));
		}
		catch (MathLinkException e) {
		    if (!"machine number overflow".equals(e.getMessage()))
			throw e;
		}

	    // Vector value test
	    for (int i = 0; (testType & TYPE_VECTOR) != 0 && i < TEST_REPETITION; i++)
		try {
		    final Vector jx = vectorArgument(min,max, componentType);
		    final Vector jy = vectorArgument(min,max, componentType);
		    final String  mFunctionCall = mFunction + "[" + listForm(jx) + "," + listForm(jy) + "]";
		    final String  jFunctionCall = jFunction + "[" + jx + "," + jy + "]";
		    ml.evaluate(mFunctionCall);
		    ml.waitForAnswer();
		    System.out.print(mFunctionCall + " = ");
		    final Vector mresult = Values.valueOf(ComplexAdapter.unconvert((ComplexAdapter[])ml.getComplexArray1()));
		    System.out.println(mresult);
		    final Vector jresult = (Vector) jFunction.apply(jx, jy);
		    System.out.println(jFunctionCall + " = " + jresult);
		    if (!jresult.equals(mresult, tolerance))
			throw new AssertionError(mFunctionCall + " = " + mresult + " != " + jFunctionCall + " = " + jresult + "\n\tdelta=" + jresult.subtract(mresult));
		}
		catch (MathLinkException e) {
		    if (!"machine number overflow".equals(e.getMessage()))
			throw e;
		}

	    // Matrix value test
	    for (int i = 0; (testType & TYPE_MATRIX) != 0 && i < TEST_REPETITION; i++)
		try {
		    final Matrix jx = matrixArgument(min,max, componentType);
		    final Matrix jy = matrixArgument(min,max, componentType);
		    final String  mFunctionCall = mFunction + "[" + listForm(jx) + "," + listForm(jy) + "]";
		    final String  jFunctionCall = jFunction + "[" + jx + "," + jy + "]";
		    ml.evaluate(mFunctionCall);
		    ml.waitForAnswer();
		    System.out.print(mFunctionCall + " = ");
		    final Matrix mresult = Values.valueOf(ComplexAdapter.unconvert((ComplexAdapter[][])ml.getComplexArray2()));
		    System.out.println(mresult);
		    final Matrix jresult = (Matrix) jFunction.apply(jx, jy);
		    System.out.println(jFunctionCall + " = " + jresult);
		    if (!jresult.equals(mresult, tolerance))
			throw new AssertionError(mFunctionCall + " =\n" + mresult + "\n!=\n" + jFunctionCall + " =\n" + jresult + "\ndelta=\n" + jresult.subtract(mresult));
		}
		catch (MathLinkException e) {
		    if (!"machine number overflow".equals(e.getMessage()))
			throw e;
		}
    	}
    	catch (UnsupportedOperationException ignore) {}
    }
    private void compareResults(String mFunction, BinaryFunction jFunction, Scalar x, Scalar y, boolean wasSuccessful) throws MathLinkException {
	final String  mFunctionCall = mFunction + "[" + x + "," + y + "]";
	final String  jFunctionCall = jFunction + "[" + x + "," + y + "]";
	try {
	    ml.evaluate(mFunctionCall);
	    ml.waitForAnswer();
	    System.out.print(mFunctionCall + " = ");
	    final Complex mresult = ((ComplexAdapter) ml.getComplex()).getValue();
	    System.out.println(mresult);
	    final Complex jresult = (Complex) jFunction.apply(x, y);
	    System.out.println(jFunctionCall + " = " + jresult);
	    boolean isSuccessful = jresult.equals(mresult, tolerance);
	    if (isSuccessful != wasSuccessful)
		throw new InternalError("this should not happen");
	    if (!isSuccessful)
		throw new AssertionError(mFunctionCall + " = " + mresult + " != " + jFunctionCall + " = " + jresult + "\tdelta=" + jresult.subtract(mresult));
	}
	catch (MathLinkException e) {
	    if (!"machine number overflow".equals(e.getMessage()))
		throw e;
	}
    }
	
    private int integerArgument(int min, int max) {
	return (int)((max-min) * random.nextDouble()) + min;
    }
    private double realArgument(double min, double max) {
	return ((max-min) * random.nextDouble() + min);
    }
    private Scalar randomArgument(double min, double max, int testType) {
	if ((testType & TYPE_INTEGER) != 0 && Utility.flip(random, 0.4))
	    return Values.valueOf(integerArgument((int)min, (int)max));
	else if ((testType & TYPE_COMPLEX) != 0 && Utility.flip(random, 0.4))
	    return Values.complex(realArgument(min, max), realArgument(min, max));
	else
	    return Values.valueOf(realArgument(min, max));
    }
    private Matrix matrixArgument(double min, double max, int testType) {
	Dimension dim = new Dimension(2, 2);
	Matrix x = Values.getInstance(dim);
	if (testType == TYPE_REAL && Utility.flip(random, 0.5))
	    // possibly switch to RMatrix
	    x = Values.valueOf(new double[dim.height][dim.width]);
	for (int i = 0; i < dim.height; i++)
	    for (int j = 0; j < dim.width; j++)
		x.set(i,j, randomArgument(min, max, testType));
	return x;
    }
    private Vector vectorArgument(double min, double max, int testType) {
	int dim = 2;
	Vector x = Values.getInstance(dim);
	if (testType == TYPE_REAL && Utility.flip(random, 0.5))
	    // possibly switch to RMatrix
	    x = Values.valueOf(new double[dim]);
	for (int i = 0; i < dim; i++)
	    x.set(i, randomArgument(min, max, testType));
	return x;
    }
	
    /**
     * Adapter class between orbital.math.* and J/Link.
     * @structure delegate value:Complex
     */
    public static class ComplexAdapter {
	private Complex value;
	public ComplexAdapter(double re, double im) {
	    value = Values.complex(re,im);
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
	String		 nl = " ";//System.getProperty("line.separator");
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
