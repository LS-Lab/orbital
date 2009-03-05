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
import java.math.BigInteger;
import java.util.*;
import java.awt.Dimension;
import orbital.util.*;
import orbital.logic.functor.Predicate;
import orbital.logic.sign.concrete.Notation;

/**
 * Automatic test-driver checking (some parts of) orbital.math.functional.* randomly against Mathematica for cross-validation.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo test Rational as well
 */
public class FunctionTest extends check.TestCase {
        private static final int TEST_REPETITION = 100;
        private static final int TEST_GROEBNER_REPETITION = 2;
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
                try {FunctionTest t=new FunctionTest();t.testGroebner();t.testGroebnerSparse();}catch(Exception e) {e.printStackTrace();}
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


        final double MIN = -1000;
        final double MAX = +1000;
        final double EPS = Double.longBitsToDouble(Double.doubleToLongBits(1.0)+1)-1.0;
        final double SMIN = -10;
        final double SMAX = +10;
        final double PI = Math.PI;
        final int scalarTypes = TYPE_INTEGER | TYPE_RATIONAL| TYPE_REAL | TYPE_COMPLEX;
        public void test_calc_id() {
                //delta, logistic, reciprocal, sign
                //@todo id, zero with tensor once Functions.zero has been adapted
                testCalculations("(#1)&",       Functions.id, MIN, MAX, TYPE_ALL, TYPE_ALL);
        }
        public void test_calc_1() {
                testCalculations("(1)&",        Functions.one, MIN, MAX, TYPE_ALL, TYPE_ALL);
        }
        public void test_calc_0() {
                testCalculations("(0)&",        Functions.zero, MIN, MAX, TYPE_ALL, TYPE_ALL);
        }
        public void test_calc_plus() {
                testCalculations("Plus",        Operations.plus, MIN, MAX, TYPE_ALL, scalarTypes);
        }
        public void test_calc_plus_tensor() {
                testCalculations("Plus",        Operations.plus, MIN, MAX, TYPE_TENSOR, scalarTypes);
        }
        public void test_calc_plus_tensor_real() {
                testCalculations("Plus",        Operations.plus, MIN, MAX, TYPE_TENSOR, TYPE_REAL);
        }
        public void test_calc_subtract() {
                testCalculations("Subtract",    Operations.subtract, MIN, MAX, TYPE_ALL, scalarTypes);
        }
        public void test_calc_subtract_tensor_real() {
                testCalculations("Subtract",    Operations.subtract, MIN, MAX, TYPE_TENSOR, TYPE_REAL);
        }
        public void test_calc_times() {
                testCalculations("Times",       Operations.times, MIN, MAX, scalarTypes, scalarTypes);
        }
        //@todo Operations.times with TYPE_TENSOR
        public void test_calc_dot() {
                testCalculations("Dot", Operations.times, MIN, MAX, TYPE_MATRIX, scalarTypes);
        }
        //testCalculations("Dot", Operations.times, MIN, MAX, TYPE_MATRIX, TYPE_REAL);
        public void test_calc_inverse() {
                testCalculations("(1/#1)&",   Operations.inverse, MIN, MAX, TYPE_SCALAR, scalarTypes);
        }
        public void test_calc_divide() {
                testCalculations("Divide",    Operations.divide, MIN, MAX, TYPE_REAL | TYPE_COMPLEX, scalarTypes);
        }
        public void test_calc_power_specific839() {
            assertTrue(vf.valueOf(new BigInteger("-839")).power(vf.valueOf(new BigInteger("13"))).equals(vf.valueOf(new BigInteger("-102071733558600433817286813640088718119"))), "(-839)^13=-102071733558600433817286813640088718119");
            assertTrue(vf.valueOf(new BigInteger("-839")).power(vf.valueOf(new BigInteger("-13"))).equals(vf.rational(vf.MINUS_ONE(),vf.valueOf(new BigInteger("102071733558600433817286813640088718119")))), "(-839)^(-13)=-1/102071733558600433817286813640088718119");
            assertTrue(vf.valueOf(-839).power(vf.valueOf(13)).equals(vf.valueOf(new BigInteger("-102071733558600433817286813640088718119"))), "(-839)^13=-102071733558600433817286813640088718119");
            assertTrue(vf.valueOf(-839).power(vf.valueOf(-13)).equals(vf.rational(vf.MINUS_ONE(),vf.valueOf(new BigInteger("102071733558600433817286813640088718119")))), "(-839)^(-13)=-1/102071733558600433817286813640088718119");
        }
        public void test_calc_power_specific186() {
                Integer e = vf.valueOf(new BigInteger("3893872581689833443233563169034491633143557775488961130562372300487316430586485635201439851951310873773751194100538601716225231388802910711695783763195151770694021633072417175360245375791101683230742092534774458719701135724461853776787932709686314576708776189070464854918967355206907677756885640575094795874204723453458290138193639842618551855481764146936155362036812799889940893611892788555396131125466437920820822016"));
            assertTrue(vf.valueOf(new BigInteger("186")).power(vf.valueOf(new BigInteger("184"))).equals(e), "(186)^184=....");
            assertTrue(vf.valueOf(new BigInteger("186")).power(vf.valueOf(new BigInteger("-184"))).equals(vf.rational(vf.ONE(),e)), "186^-184=-1/...");
            assertTrue(vf.valueOf(186).power(vf.valueOf(184)).equals(e), "186^184=...");
            assertTrue(vf.valueOf(186).power(vf.valueOf(-184)).equals(vf.rational(vf.ONE(),e)), "186^-184=1/....");
        }
        public void test_calc_power_specific235() {
            assertTrue(vf.valueOf(new BigInteger("235")).power(vf.valueOf(new BigInteger("114"))).equals(vf.valueOf(new BigInteger("2003255293326170173089299143840932011646545098342714282893392324739078765198952834874440764945327478470048084145538180664013906365890861937246340933665952330664018713980720474439622253242153397644743292466731942248689689499703916499007139151444789604283869266510009765625"))), "235^114=2003255293326170173089299143840932011646545098342714282893392324739078765198952834874440764945327478470048084145538180664013906365890861937246340933665952330664018713980720474439622253242153397644743292466731942248689689499703916499007139151444789604283869266510009765625");            
            assertTrue(vf.valueOf(235).power(vf.valueOf(114)).equals(vf.valueOf(new BigInteger("2003255293326170173089299143840932011646545098342714282893392324739078765198952834874440764945327478470048084145538180664013906365890861937246340933665952330664018713980720474439622253242153397644743292466731942248689689499703916499007139151444789604283869266510009765625"))), "235^114=2003255293326170173089299143840932011646545098342714282893392324739078765198952834874440764945327478470048084145538180664013906365890861937246340933665952330664018713980720474439622253242153397644743292466731942248689689499703916499007139151444789604283869266510009765625");            
        }
        public void test_calc_power_specific494() {
            assertTrue(vf.valueOf(new BigInteger("-494")).power(vf.valueOf(new BigInteger("309"))).equals(vf.valueOf(new BigInteger(
                        "-229946719102371565447710703498387918803239577351487409836368736245893"+
                        "3912782459831068926129255046599887644457751487170699209127627018974235"+
                        "2755619115457855817251420067022659821877200105306165791514759536899524"+
                        "0784713412223450407111225840108234369476505141366231056171190196455032"+
                        "7649889486756485664931590029150605845571237066193707636553441267796690"+
                        "7021298900336779142470304686046980547369020896251941216879477802116577"+
                        "9911222080857138209775584171672025310133644484825147453081147975523603"+
                        "9001488546095287705809770581541463209112952661726355989822301691100540"+
                        "1287755031935739510759626435009387197879295850636155077305537595577603"+
                        "5305716215524117911315448988748440333627002295965399111032266590908898"+
                        "0882253092866994002290059812264315478823211782660425206362802385333072"+
            "4738726546735676493068858806702154688091576231469641895514210304"))), "(-494)^309=-....");
            assertTrue(vf.valueOf(new BigInteger("-494")).power(vf.valueOf(new BigInteger("-309"))).equals(vf.rational(vf.MINUS_ONE(),vf.valueOf(new BigInteger(
                        "229946719102371565447710703498387918803239577351487409836368736245893"+
                        "3912782459831068926129255046599887644457751487170699209127627018974235"+
                        "2755619115457855817251420067022659821877200105306165791514759536899524"+
                        "0784713412223450407111225840108234369476505141366231056171190196455032"+
                        "7649889486756485664931590029150605845571237066193707636553441267796690"+
                        "7021298900336779142470304686046980547369020896251941216879477802116577"+
                        "9911222080857138209775584171672025310133644484825147453081147975523603"+
                        "9001488546095287705809770581541463209112952661726355989822301691100540"+
                        "1287755031935739510759626435009387197879295850636155077305537595577603"+
                        "5305716215524117911315448988748440333627002295965399111032266590908898"+
                        "0882253092866994002290059812264315478823211782660425206362802385333072"+
            "4738726546735676493068858806702154688091576231469641895514210304")))), "(-494)^(-309)=-1/...");
        }
        public void test_calc_power_small() {
                testCalculations("Power",     Operations.power, -10, 10, new int[] {TYPE_ALL, TYPE_INTEGER}, scalarTypes);
        }
        public void test_calc_power() {
                testCalculations("Power",     Operations.power, MIN, MAX, new int[] {TYPE_ALL, TYPE_INTEGER}, scalarTypes);
        }
        //testCalculations("Power",     Operations.power, MIN, MAX);
        public void test_calc_minus() {
                testCalculations("Minus",       Operations.minus, MIN, MAX, TYPE_ALL, scalarTypes);
        }
        //testCalculations("Inverse",   Operations.inverse, MIN, MAX, TYPE_MATRIX);
        public void test_calc_exp() {
                testCalculations("Exp",     Functions.exp, -10, 10, scalarTypes, TYPE_NONE);
        }
        public void test_calc_log() {
                testCalculations("Log",     Functions.log, EPS, MAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_sqrt() {
                testCalculations("Sqrt",        Functions.sqrt, 0, MAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_abs() {
                testCalculations("Abs",         Functions.norm, MIN, MAX, TYPE_ALL, TYPE_ALL);
        }
        public void test_calc_pow2() {
                testCalculations("(#1^2)&",     Functions.square, MIN, MAX, scalarTypes | TYPE_MATRIX, scalarTypes);
        }
        //testCalculations("DiracDelta",Functions.diracDelta, MIN, MAX);
        public void test_calc_sin() {
                testCalculations("Sin",         Functions.sin, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_cos() {
                testCalculations("Cos",         Functions.cos, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_tan() {
                testCalculations("Tan",         Functions.tan, SMIN, SMAX, scalarTypes, TYPE_NONE); //...
        }
        public void test_calc_cot() {
                testCalculations("Cot",         Functions.cot, -PI+EPS, -EPS, scalarTypes, TYPE_NONE); //...
        }
        public void test_calc_cot2() {
                testCalculations("Cot",         Functions.cot, EPS, PI-EPS, scalarTypes, TYPE_NONE);
        }
        public void test_calc_csc() {
                testCalculations("Csc",         Functions.csc, EPS, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_csc2() {
                testCalculations("Csc",         Functions.csc, SMIN, EPS, scalarTypes, TYPE_NONE);
        }
        public void test_calc_sec() {
                testCalculations("Sec",         Functions.sec, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_sinh() {
                testCalculations("Sinh",        Functions.sinh, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_cosh() {
                testCalculations("Cosh",        Functions.cosh, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_tanh() {
                testCalculations("Tanh",        Functions.tanh, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_coth() {
                testCalculations("Coth",        Functions.coth, EPS, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_coth2() {
                testCalculations("Coth",        Functions.coth, SMIN, EPS, scalarTypes, TYPE_NONE);
        }
        public void test_calc_csch() {
                testCalculations("Csch",        Functions.csch, SMIN, EPS, scalarTypes, TYPE_NONE);
        }
        public void test_calc_csch2() {
                testCalculations("Csch",        Functions.csch, EPS, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_sech() {
                testCalculations("Sech",        Functions.sech, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_arccos() {
                testCalculations("ArcCos",      Functions.arccos, -1, 1, scalarTypes, TYPE_NONE);
        }
        public void test_calc_arcsin() {
                testCalculations("ArcSin",      Functions.arcsin, -1, 1, scalarTypes, TYPE_NONE);
        }
        public void test_calc_arctan() {
                //testCalculations("ArcCot",    Functions.arccot, SMIN, SMAX, scalarTypes, TYPE_NONE);  // differs by PI for negative values. we return positive values
                testCalculations("ArcTan",      Functions.arctan, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_arccosh() {
                testCalculations("ArcCosh",     Functions.arcosh, 1, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_arcsinh() {
                testCalculations("ArcSinh",     Functions.arsinh, SMIN, SMAX, scalarTypes, TYPE_NONE);
        }
        public void test_calc_arctanh() {
                testCalculations("ArcTanh",     Functions.artanh, -1+EPS, 1-EPS, TYPE_REAL | TYPE_COMPLEX, TYPE_NONE);
        }

        private void testCalculations(String mFunction, BinaryFunction jFunction, double min, double max, int testType, int componentType) {
                testCalculations(mFunction, jFunction, min, max, new int[] {testType,testType}, componentType);
        }

        private void testCalculations(String mFunction, BinaryFunction jFunction, double min, double max, int[] testType, int componentType) {
                createMathLink();
                TYPE_DEFAULT = scalarTypes;
                try {
                        ml.evaluate("2+2");
                        ml.waitForAnswer();
                        int result = ml.getInteger();
                        System.out.println("2 + 2 = " + result);

                        ml.evaluate("2+2*(I+1)");
                        ml.waitForAnswer();
                        Complex result2 = getResult(vf, ml);
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
                        testFunction(vf, mFunction,jFunction, min, max, testType, componentType);
                        System.out.println();
                        System.out.println("PASSED");
                } catch (MathLinkException ex) {
                        System.out.println();
                        throw (RuntimeException) new RuntimeException("MathLinkException occurred: " + ex).initCause(ex);
                } catch (ExprFormatException ex) {
                        System.out.println();
                        throw (RuntimeException) new RuntimeException("MathLinkException occurred: " + ex).initCause(ex);
                }
                finally {
                        closeMathLink();
                }
        }

        private void testCalculations(String mFunction, Function jFunction, double min, double max, int testType, int componentType) {
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
                        Complex result2 = getResult(vf, ml);
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
                        testFunction(vf, mFunction,jFunction, min, max, testType, componentType);
                        System.out.println();
                        System.out.println("PASSED");
                } catch (MathLinkException ex) {
                        System.out.println();
                        throw (RuntimeException) new RuntimeException("MathLinkException occurred: " + ex).initCause(ex);
                } catch (ExprFormatException ex) {
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
        private void testFunction(ValueFactory vf, String mFunction, Function jFunction, double min, double max, int testType, int componentType) throws MathLinkException {
                try {
                        // Integer value test
                        for (int i = 0; (testType & TYPE_INTEGER) != 0 && i < TEST_REPETITION; i++) {
                                compareResults(mFunction, jFunction,
                                                vf.valueOf(integerArgument(vf, (int)Math.ceil(min),(int)Math.floor(max))));
                        }

                        // Real value test
                        for (int i = 0; (testType & TYPE_REAL) != 0 && i < TEST_REPETITION; i++)
                                try {
                                        compareResults(mFunction, jFunction,
                                                        vf.valueOf(realArgument(vf, min,max)));
                                }
                        catch (MathLinkException e) {
                                if (!"machine number overflow".equals(e.getMessage()))
                                        throw e;
                        }

                        // Complex value test
                        for (int i = 0; (testType & TYPE_COMPLEX) != 0 && i < TEST_REPETITION; i++)
                                try {
                                        compareResults(mFunction, jFunction,
                                                        vf.complex(realArgument(vf, min,max), realArgument(vf, min,max)));
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
                try {
                        ml.evaluate("N[" + mFunctionCall + "]");
                        ml.waitForAnswer();
                        System.out.print(mFunctionCall + " = ");
                        Complex mresult = null;
                        try {
                                Expr la = ml.peekExpr();
                                if (la.numberQ()) {
                                                mresult = getResult(vf, ml);
                                } else {
                                        // don't know what to do with non-number, assume true
                                        System.out.println("FunctionTest doesn't understand " + la);
                                        ml.newPacket();
                                        return;
                                }
                        } catch(MathLinkException ignore) {
                                // assume success
                                System.out.println("FunctionTest doesn't understand " + mresult);
                                ml.newPacket();
                                return;
                        } catch (ExprFormatException ex) {
                                // assume success
                                System.out.println("FunctionTest doesn't understand " + mresult);
                                ml.newPacket();
                                return;
                        }
                        System.out.println(mresult);
                        final Complex jresult = (Complex) jFunction.apply(x);
                        System.out.println(jFunctionCall + " = " + jresult);
                        checkArithmetic(jresult);
                        boolean isSuccessful = jresult.equals(mresult, tolerance);
                        assertTrue(isSuccessful , mFunctionCall + " = " + mresult + " != " + jFunctionCall + "@" + x.getClass() + " = " + jresult + "@" + jresult.getClass() + "\tdelta=" + jresult.subtract(mresult));
                } catch (MathLinkException ex) {
                        throw new MathLinkException(ex, "Comparing " + mFunction + " and " + jFunction + " on " + x + "  could not evaluate " + ex);
                }
        }

        /**
         * @param testType the respective types of arguments to test.
         * @param componentType the types of components of the arguments to test (if testType includes any tensors).
         * @internal the possible range of arguments is a major problem.
         */
        private void testFunction(ValueFactory vf, String mFunction, BinaryFunction jFunction, double min, double max, int[] testType, int componentType) throws MathLinkException {
                try {
                        // Integer value test
                        for (int i = 0; (testType[0] & TYPE_INTEGER) != 0 && (testType[1] & TYPE_INTEGER) != 0 && i < TEST_REPETITION; i++)
                                try {
                                        compareResults(mFunction, jFunction,
                                                        vf.valueOf(integerArgument(vf, (int)Math.ceil(min),(int)Math.floor(max))),
                                                        vf.valueOf(integerArgument(vf, (int)Math.ceil(min),(int)Math.floor(max))));
                                }
                        catch (MathLinkException e) {
                                if (!"machine number overflow".equals(e.getMessage()))
                                        throw e;
                        }

                        // Real value test
                        for (int i = 0; (testType[0] & TYPE_REAL) != 0 && (testType[1] & TYPE_REAL) != 0 && i < TEST_REPETITION; i++)
                                try {
                                        compareResults(mFunction, jFunction,
                                                        vf.valueOf(realArgument(vf, min,max)),
                                                        vf.valueOf(realArgument(vf, min,max)));
                                }
                        catch (MathLinkException e) {
                                if (!"machine number overflow".equals(e.getMessage()))
                                        throw e;
                        }

                        // Complex value test
                        for (int i = 0; (testType[0] & TYPE_COMPLEX) != 0 && (testType[1] & TYPE_COMPLEX) != 0 && i < TEST_REPETITION; i++)
                                try {
                                        compareResults(mFunction, jFunction,
                                                        vf.complex(realArgument(vf, min,max),realArgument(vf, min,max)),
                                                        vf.complex(realArgument(vf, min,max),realArgument(vf, min,max)));
                                }
                        catch (MathLinkException e) {
                                if (!"machine number overflow".equals(e.getMessage()))
                                        throw e;
                        }

                        // Vector value test
                        for (int i = 0; (testType[0] & TYPE_VECTOR) != 0 && (testType[1] & TYPE_VECTOR) != 0 &&i < TEST_REPETITION; i++)
                                try {
                                        final Vector jx = vectorArgument(vf, min,max, componentType, ddim.width);
                                        final Vector jy = vectorArgument(vf, min,max, componentType, ddim.width);
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
                        for (int i = 0; (testType[0] & TYPE_MATRIX) != 0 && (testType[1] & TYPE_MATRIX) != 0 && i < TEST_REPETITION; i++)
                                try {
                                        final Matrix jx = matrixArgument(vf, min,max, componentType, ddim);
                                        final Matrix jy = matrixArgument(vf, min,max, componentType, ddim);
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

                        // mixed value test
                        for (int i = 0; (testType[0] & TYPE_SCALAR) != 0 && (testType[1] & TYPE_SCALAR) != 0 && i < TEST_REPETITION; i++)
                                try {
                                        compareResults(mFunction, jFunction,
                                                        (Scalar)randomArgument(vf, min, max, testType[0]& TYPE_SCALAR),
                                                        (Scalar)randomArgument(vf, min, max, testType[1] & TYPE_SCALAR));
                                }
                        catch (MathLinkException e) {
                                if (!"machine number overflow".equals(e.getMessage()))
                                        throw e;
                        }
                }
                catch (UnsupportedOperationException ignore) {}
        }

        private void testFunction(ValueFactory vf, String mFunction, BinaryFunction jFunction, double min, double max, int testType, int componentType) throws MathLinkException {
                testFunction(vf, mFunction, jFunction, min, max, new int[] {testType, testType}, componentType);
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
                        //@xxx built-in loss of precision
                        final Complex mresult = getResult(vf, ml);
                        System.out.println(mresult);
                        final Complex jresult = (Complex) jFunction.apply(x, y);
                        System.out.println(jFunctionCall + " = " + jresult);
                        checkArithmetic(jresult);
                        boolean isSuccessful = jresult.equals(mresult, tolerance);
                        assertTrue(isSuccessful , mFunctionCall + " = " + mresult + " != " + jFunctionCall + " = " + jresult + "\tdelta=" + jresult.subtract(mresult));
                }
                catch (MathLinkException e) {
                        if (!"machine number overflow".equals(e.getMessage()))
                                throw new MathLinkException(e, "Comparing " + mFunction + " and " + jFunction + " on " + x + " and " + y + "  could not evaluate " + e);
                }
                catch (ExprFormatException e) {
                        throw new MathLinkException(e, "Comparing " + mFunction + " and " + jFunction + " on " + x + " and " + y + "  could not evaluate " + e);
                }
        }


        // testing more sophisticated symbolic algorithms

        // groebner basis tests

        public void testGroebner() throws MathLinkException {
                // make format precision compatible with Mathematica for appropriate numerical comparisons
                MathUtilities.setDefaultPrecisionDigits(17);
                // set arbitrary precision as default (for adequate comparison with Mathematica)
                /*System.setProperty("orbital.math.Values.implementation",
                           orbital.moon.math.BigValuesImpl.class.getName());*/
                Map params = new HashMap();
                params.put("orbital.math.Scalar.precision", "big");
                Values.setDefault(Values.getInstance(params));
                ValueFactory vf = Values.getDefaultInstance();
                testGroebner(vf);
        }
        public void testGroebnerSparse() throws MathLinkException {
                // make format precision compatible with Mathematica for appropriate numerical comparisons
                MathUtilities.setDefaultPrecisionDigits(17);
                // set arbitrary precision as default (for adequate comparison with Mathematica)
                /*System.setProperty("orbital.math.Values.implementation",
                           orbital.moon.math.BigValuesImpl.class.getName());*/
                Map params = new HashMap();
                params.put("orbital.math.Scalar.precision", "big");
                params.put("orbital.math.Polynomial.sparse", Boolean.TRUE);
                Values.setDefault(Values.getInstance(params));
                ValueFactory vf = Values.getDefaultInstance();
                testGroebner(vf);
        }
        /**
         * Randomly test groebner bases
         */
        protected void testGroebner(ValueFactory vf) throws MathLinkException {
                createMathLink();       
                final double MIN = -10;
                final double MAX = +10;
                try {
                        Comparator monomialOrder;
                        String mmonorder;
                        monomialOrder = AlgebraicAlgorithms.LEXICOGRAPHIC;
                        mmonorder = "Lexicographic";
                        for (int i = 0; i < TEST_GROEBNER_REPETITION; i++) {
                                checkGroebner(vf, MIN,MAX,TYPE_INTEGER, monomialOrder, mmonorder);
                        }
                        for (int i = 0; i < TEST_GROEBNER_REPETITION; i++) {
                                checkGroebner(vf, MIN,MAX,TYPE_RATIONAL, monomialOrder, mmonorder);
                        }
                        for (int i = 0; i < TEST_GROEBNER_REPETITION; i++) {
                                checkGroebner(vf, MIN,MAX,TYPE_INTEGER|TYPE_RATIONAL, monomialOrder, mmonorder);
                        }
                        monomialOrder = AlgebraicAlgorithms.DEGREE_LEXICOGRAPHIC;
                        mmonorder = "DegreeLexicographic";
                        for (int i = 0; i < TEST_GROEBNER_REPETITION; i++) {
                                checkGroebner(vf, MIN,MAX,TYPE_INTEGER, monomialOrder, mmonorder);
                        }
                        for (int i = 0; i < TEST_GROEBNER_REPETITION; i++) {
                                checkGroebner(vf, MIN,MAX,TYPE_RATIONAL, monomialOrder, mmonorder);
                        }
                        for (int i = 0; i < TEST_GROEBNER_REPETITION; i++) {
                                checkGroebner(vf, MIN,MAX,TYPE_INTEGER|TYPE_RATIONAL, monomialOrder, mmonorder);
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
        private void checkGroebner(ValueFactory vf, double MIN, double MAX, int testType, Comparator monomialOrder, String mmonorder) throws MathLinkException {
                Set g = new LinkedHashSet();
                final int DEG = 3;
                final int VARS = 5;
                final int NUM = 4;
                final int vars = integerArgument(vf, 1, VARS);
                int num = integerArgument(vf, 1, NUM);
                for (int p = 0; p < num; p++) {
                        Polynomial gi = polyArgument(vf, MIN, MAX, testType, vars, DEG);
                        if (gi.toString().length() > 10) {
                                // use string output as complexity bound and randomly remove complexity
                                for (ListIterator i = gi.iterator(); i.hasNext(); ) {
                                        Arithmetic x = (Arithmetic)i.next();
                                        if (Utility.flip(random, 0.75))
                                                i.set(x.zero());
                                }
                        }
                        g.add(gi);
                        if (g.toString().length() > 40) {
                                // use string output as complexity bound
                                break;
                        }
                }
                checkGroebner(vf, g, MIN,MAX,testType, monomialOrder, mmonorder, DEG);
        }

        private void checkGroebner(ValueFactory vf, Set g, double MIN, double MAX, int testType, Comparator monomialOrder, String mmonorder, int DEG) throws MathLinkException {
                final int MEMBER_CHECK = 10;
                final int vars = ((Polynomial)g.iterator().next()).rank();
                String varlist = "{";
                final String multinomialVariables[] = {"X", "Y", "Z"};
                for (int v = 0; v < vars; v++) {
                        varlist += (v>0 ? "," : "") +  (vars<=3 ? multinomialVariables[v] : "X" + v);
                }
                varlist += "}";
                System.out.println("Computing Groebner Basis of " + g);
                //@todo timeout this computation if it takes too long
                Collection GB = AlgebraicAlgorithms.groebnerBasis(g, monomialOrder);
                System.out.println("Groebner Basis " + GB);
                Setops.all(GB, checkAlgebraic);
                // check if GB is in ideal
                for (Iterator j = GB.iterator(); j.hasNext(); ) {
                        String query = "FullSimplify[PolynomialReduce[" + j.next() + ",GroebnerBasis[" + listForm(g) + "," + varlist + ", MonomialOrder->" + mmonorder + "], " + varlist + ", MonomialOrder->" + mmonorder + "][[2]] == 0]";
                        String res = ml.evaluateToInputForm(query, 100);
                        assertTrue("True".equals(res), "Groebner Basis element is in ideal " + query );
                }
                // check identical residues on several examples
                for (int k = 0; k < MEMBER_CHECK; k++) {
                        Polynomial f = polyArgument(vf, MIN, MAX, testType, vars, DEG);
                        System.out.println("Check member " + f);
                        Polynomial r = AlgebraicAlgorithms.reduce(f, GB, monomialOrder);
                        checkArithmetic(r);
                        String query = "FullSimplify[PolynomialReduce[" + f + ",GroebnerBasis[" + listForm(g) + "," + varlist + ", MonomialOrder->" + mmonorder + "], " + varlist + ", MonomialOrder->" + mmonorder + "][[2]] == " + r + "]";
                        String res = ml.evaluateToInputForm(query, 80);
                        assertTrue("True".equals(res), "Same residue for each Groebner Basis " + query );
                }
        }

        public void disabled_testGroebnerSpecific() throws MathLinkException {
                createMathLink();       
                final double MIN = -10;
                final double MAX = +10;
                final ValueFactory vf = Values.getDefault();
                try {
                        final Polynomial mo = vf.MONOMIAL(new int[] {0,0,0,0,0,0,0});
                        //              -1+X0*X6
                        //              -1+X0*X4^2
                        //              -1+X0*X5^2
                        //              X4^2*X6+X3*X6+X1*X5^2+X1*X4^2-X1*X3
                        //              X2^2-2*X1*X6+X1^2
                        //              X6^2-X5^4+X4^4+2*X3*X5^2-X3^2
                        //              -1+X4^4-X3^2            
                        Set g = new LinkedHashSet(Arrays.asList(new Polynomial[] {
                                        vf.MONOMIAL(new int[] {1,0,0,0,0,0,1}).subtract(mo),
                                        vf.MONOMIAL(new int[] {1,0,0,0,2,0,0}).subtract(mo),
                                        vf.MONOMIAL(new int[] {1,0,0,0,0,2,0}).subtract(mo),
                                        vf.MONOMIAL(new int[] {0,0,0,0,2,0,1}).add(vf.MONOMIAL(new int[] {0,0,0,1,0,0,1})).add(vf.MONOMIAL(new int[] {0,1,0,0,0,2,0})).add(vf.MONOMIAL(new int[] {0,1,0,0,2,0,0})).subtract(vf.MONOMIAL(new int[] {0,1,0,1,0,0,0})),
                                        vf.MONOMIAL(vf.ONE(), new int[] {0,0,2,0,0,0,0}).add(vf.MONOMIAL(vf.valueOf(-2),new int[] {0,1,0,0,0,0,1})).add(vf.MONOMIAL(new int[] {0,2,0,0,0,0,0})),
                                        vf.MONOMIAL(vf.ONE(), new int[] {0,0,0,0,0,0,2}).subtract(vf.MONOMIAL(new int[] {0,0,0,0,0,4,0})).add(vf.MONOMIAL(new int[] {0,0,0,0,4,0,0})).add(vf.MONOMIAL(vf.valueOf(2), new int[] {0,0,0,1,0,2,0})).subtract(vf.MONOMIAL(new int[] {0,0,0,2,0,0,0})),
                                        vf.MONOMIAL(vf.ONE(), new int[] {0,0,0,0,4,0,0}).subtract(vf.MONOMIAL(new int[] {0,0,0,2,0,0,0})).subtract(mo),
                        }));
                        Comparator monomialOrder;
                        String mmonorder;
                        monomialOrder = AlgebraicAlgorithms.DEGREE_LEXICOGRAPHIC;
                        mmonorder = "DegreeLexicographic";
                        assertTrue(!AlgebraicAlgorithms.reduce(mo, g, monomialOrder).isZero(), "1 is not reducible");
                        checkGroebner(vf, g, MIN, MAX, TYPE_INTEGER|TYPE_RATIONAL, monomialOrder, mmonorder, 6);
                        monomialOrder = AlgebraicAlgorithms.LEXICOGRAPHIC;
                        mmonorder = "Lexicographic";
                        assertTrue(!AlgebraicAlgorithms.reduce(mo, g, monomialOrder).isZero(), "1 is not reducible");
                        checkGroebner(vf, g, MIN, MAX, TYPE_INTEGER|TYPE_RATIONAL, monomialOrder, mmonorder, 6);
                }
                catch (MathLinkException e) {
                        if (!"machine number overflow".equals(e.getMessage()))
                                throw e;
                }
                finally {
                        closeMathLink();
                }
        }

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
                casetestdSolve(vf, -1000, +1000, TYPE_INTEGER /*| TYPE_REAL*/, true);
        }
        public void testdSolve_inhomogeneous_numeric_int() throws MathLinkException {
                FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
                casetestdSolve(vf, -1000, +1000, TYPE_DEFAULT, false);
        }
        public void testdSolve_homogeneous_symbolic_int() throws MathLinkException {
                FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
                casetestdSolve(vf, -1000, +1000, TYPE_SYMBOL, true);
        }
        public void testdSolve_inhomogeneous_symbolic_int() throws MathLinkException {
                FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
                casetestdSolve(vf, -1000, +1000, TYPE_SYMBOL, false);
        }

        public void testdSolve_homogeneous_numeric() throws MathLinkException {
                FunctionTest.TYPE_DEFAULT = TYPE_INTEGER | TYPE_RATIONAL;
                casetestdSolve(vf, -1000, +1000, TYPE_DEFAULT, true);
        }
        public void testdSolve_inhomogeneous_numeric() throws MathLinkException {
                FunctionTest.TYPE_DEFAULT = TYPE_INTEGER | TYPE_RATIONAL;
                casetestdSolve(vf, -1000, +1000, TYPE_DEFAULT, false);
        }
        public void testdSolve_homogeneous_symbolic() throws MathLinkException {
                FunctionTest.TYPE_DEFAULT = TYPE_INTEGER | TYPE_RATIONAL;
                casetestdSolve(vf, -1000, +1000, TYPE_SYMBOL, true);
        }
        public void testdSolve_inhomogeneous_symbolic() throws MathLinkException {
                FunctionTest.TYPE_DEFAULT = TYPE_INTEGER | TYPE_RATIONAL;
                casetestdSolve(vf, -1000, +1000, TYPE_SYMBOL, false);
        }

        /*
    public void testdSolve_homogeneous_numeric_uni() throws MathLinkException {
        FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
        casetestdSolve(vf, -1, +1, TYPE_DEFAULT, true);
    }
    public void testdSolve_inhomogeneous_numeric_uni() throws MathLinkException {
        FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
        casetestdSolve(vf, -1, +1, TYPE_DEFAULT, false);
    }
    public void testdSolve_homogeneous_symbolic_uni() throws MathLinkException {
        FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
        casetestdSolve(vf, -1, +1, TYPE_SYMBOL, true);
    }
    public void testdSolve_inhomogeneous_symbolic_uni() throws MathLinkException {
        FunctionTest.TYPE_DEFAULT = TYPE_INTEGER;
        casetestdSolve(vf, -1, +1, TYPE_SYMBOL, false);
    }
         */

        protected void casetestdSolve(ValueFactory vf, double MIN, double MAX, int componentType, boolean homogeneous)
        throws MathLinkException {
                createMathLink();
                try {
                        for (int rep = 0; rep < TEST_REPETITION; rep++) {
                                final int n = integerArgument(vf, 1,MAX_DSOLVE_DIM);
                                final Dimension dim = new Dimension(n,n);
                                final Real tau = vf.ZERO();
                                Matrix A = matrixArgument(vf, MIN,MAX, componentType&TYPE_NUMERIC, dim);
                                // make strict upper diagonal matrix for nilpotence
                                for (int i = 0; i < A.dimension().height; i++) {
                                        for (int j = 0; j <= i && j < A.dimension().width; j++) {
                                                A.set(i, j, vf.ZERO());
                                        }
                                }
                                Vector b = homogeneous
                                ? vf.ZERO(n)
                                                : vectorArgument(vf, MIN,MAX, componentType, dim.height);
                                Vector eta = vectorArgument(vf, MIN,MAX, componentType, dim.height);
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
			final String refsol = ml.evaluateToInputForm("" + ode, 80); //ml.evaluateToOutputForm("" + ode + "", 80);
			ml.newPacket();
			System.out.println("Our solution:\n" + oursol);
			System.out.println("Ref.solution:\n" + refsol);


			// compare our solution and reference solution in Mathematica
			ml.newPacket();
			ml.evaluate("FullSimplify[(\n" + refsol + " == \n" + oursol + ")]");
			ml.waitForAnswer();
			final String comparison = ml.getExpr().toString();
			ml.newPacket();
			if (!comparison.equals("True")) {
				System.out.println("FullSimplify[(\n" + refsol + " == \n" + oursol + ")]");
				System.out.println("Result: " + comparison);
				System.out.println("dSolve comparison validation FAILED");
			}
			final boolean comparesODE = comparison.equals("True");
			if (comparesODE) {
				assertTrue(comparesODE , " dSolve equivalence:\n " + oursol + "\n x'==\n" + mf.format(A) + ".x + " + mf.format(b) + "\nwith initial value " + mf.format(eta) + "\nreference solution:\n" + refsol + "\nresulting in " + comparison);
				return;
			}

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

				// second chance with full bracket format
				final Notation oldNotation = Notation.getDefault();
				Notation.setDefault(Notation.FULLFIX);
				final String oursol2 = mf.format(f.apply(t));
				// verify in Mathematica that oursol really solves ODE
				ml.newPacket();
				ml.evaluate("{True}==Union[FullSimplify[N["
						+ " MapThread[#1==#2&,"
						+ "  {D[" + oursol2 +"," + t + "], (" + mf.format(A) + ").(" + oursol + ") + " + mf.format(b) + "}\n"
						+ "]\n"
						+ "]]]"
				);
				ml.waitForAnswer();
				final String solvesODE2 = ml.getExpr().toString();
				ml.newPacket();
				if (!solvesODE2.equals("True")) {
					System.out.println("{True}==Union[FullSimplify["
							+ " MapThread[#1==#2&,"
							+ "  {D[" + oursol2 +"," + t + "], (" + mf.format(A) + ").(" + oursol + ") + " + mf.format(b) + "}\n"
							+ "]\n"
							+ "]]");
					System.out.println("Result: " + solvesODE2);
					System.out.println("dSolve solution second validation FAILED");
				}
				ml.evaluate("FullSimplify["
						+ "  {D[" + oursol2 +"," + t + "], (" + mf.format(A) + ").(" + oursol + ") + " + mf.format(b) + "}\n"
						+ "]\n"
						+ "]]"
				);
				ml.waitForAnswer();
				final String solvesODE3 = ml.getExpr().toString();
				ml.newPacket();
				Notation.setDefault(oldNotation);
				assertTrue(solvesODE3.equals("True") || solvesODE2.equals("True") , " dSolve solves ODE on second validation\n our solution:\n" + oursol + "\nour second solution:\n" + oursol2 + " \n ref.solution:\n" + refsol + "\n solves " + "x'==\n" + mf.format(A) + ".x + " + mf.format(b) + "\nwith initial value " + mf.format(eta) + "\nresulting in " + solvesODE2);
			} else {
				assertTrue(solvesODE.equals("True") , " dSolve solves ODE\n our solution:\n" + oursol + " \n ref.solution:\n" + refsol + "\n solves " + "x'==\n" + mf.format(A) + ".x + " + mf.format(b) + "\nwith initial value " + mf.format(eta) + "\nresulting in " + solvesODE);
			}
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
			b = vf.valueOf(new Arithmetic[]{vf.ZERO(),vf.symbol("a")});
			eta = vf.valueOf(new Symbol[]{vf.symbol("z0"),vf.symbol("v0")});
			System.out.println("train dynamics with constant acceleration a as inhomogeneous part and initial value " + eta);
			checkdSolve(A, b, tau, eta);

			A = vf.valueOf(new double[][] {
					{0,1,0,0},
					{0,0,1,0},
					{0,0,0,1},
					{0,0,0,0},
			});
			b = vf.valueOf(new Arithmetic[]{vf.ZERO(),vf.ZERO(),vf.ZERO(),vf.symbol("b")});
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


	public void testndSolve() throws MathLinkException, ExprFormatException {
		createMathLink();    
		assertTrue(false, "test disabled temporarily");
		try {
			Real tau;
			Real eta;
			Real min, max;
			int steps = 20;
			BinaryFunction f;
			Function y;

			f = Functions.binaryConstant(vf.valueOf(5));
			tau = vf.ZERO();
			eta = vf.ZERO();
			min = tau;
			max = vf.valueOf(8);
			checkndSolve(vf, f, tau, eta,
					min, max,
					steps);

			// solution of x'=x,x(0)=1 is e^x
                        f = Functions.projectSecond;
                        tau = vf.ZERO();
                        eta =vf.ONE();
                        min = tau;
                        max = vf.valueOf(3);
                        steps = 20;
                        checkndSolve(vf, f, tau, eta,
                                        min, max.divide(vf.valueOf(2)),
                                        steps);

                        f = Functionals.onSecond((Function)Operations.plus.apply(Functions.tan, Functions.one));
                        tau = vf.ONE();
                        eta = vf.ONE();
                        min = vf.ONE();
                        max = vf.valueOf(1.1);
                        checkndSolve(vf, f, tau, eta,
                                        min, max,
                                        steps);
                }
                catch (MathLinkException e) {
                        if (!"machine number overflow".equals(e.getMessage()))
                                throw e;
                }
                catch (ExprFormatException e) {
                        if (!"machine number overflow".equals(e.getMessage()))
                                throw e;
                }
                finally {
                        closeMathLink();
                }
        }
        protected void checkndSolve(orbital.math.functional.BinaryFunction/*<Real,Vector<Real>>*/ f, Real tau, Vector/*<Real>*/ eta,
                        Real min, Real max,
                        int steps) throws MathLinkException {
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
                        Real r = vf.valueOf(realArgument(vf, min.doubleValue(), max.doubleValue()));
                        System.out.println("\ty(" + r + ") = " + y.apply(r));
                }
        }
        protected void checkndSolve(ValueFactory vf, orbital.math.functional.BinaryFunction/*<Real,Vector<Real>>*/ f, Real tau, Real eta,
                        Real min, Real max,
                        int steps) throws MathLinkException, ExprFormatException {
                final Real tolerance = vf.valueOf(0.01);
                final Symbol y = vf.symbol("y");
                final Symbol t = vf.symbol("t");
                System.out.println("solving numerical differential equations");
                String ode = null;
                try {
                        System.out.println("Solving ODE x'(t) == " + f.apply(t,vf.symbol("x")) + "\n  with initial value " + eta + " at " + tau + " in range [" + min + "," + max + "]");
                        ode = y + "'[" + t + "] == " + f.apply(t,vf.symbol(y+"["+t+"]"));
                } catch(Exception ignore) {
                        System.out.println("Solving ODE x'(t) == " + f + "\n  with initial value " + eta + " at " + tau + " in range [" + min + "," + max + "]");
                }
                Function sol = NumericalAlgorithms.dSolve(f, tau, eta,
                                min, max,
                                steps, 4);
                System.out.println("  solution\t" + sol);
                System.out.println("  solution at " + tau + " is " + sol.apply(tau));
                assertTrue(eta.equals(sol.apply(tau), tolerance), "initial value " + eta + "==" + sol.apply(tau) + " respected at " + tau);
                // randomized equality test
                for (int j = 0; j < TEST_REPETITION; j++) {
                        Real r = vf.valueOf(realArgument(vf, min.doubleValue(), max.doubleValue()));
                        Arithmetic jresult = (Arithmetic)sol.apply(r);
                        System.out.println("\ty(" + r + ") = " + jresult);
                        if (ode != null) {
                                String node = ""
                                        + "NDSolve[{" + ode + ",\n"
                                        + " " + y + "[" + tau + "] == " + mf.format(eta) + "},\n"
                                        + " " + y + ",{" + t + "," + min + "," + max + "}\n"
                                        + "]";
                                ml.newPacket();
                                ml.evaluate(y + "[" + r + "]" + "/. " + node + "[[1]]");
                                ml.waitForAnswer();
                                final Complex mresult = getResult(vf, ml);
                                ml.newPacket();
                                // accept tolerance percent of mresult deviation
                                boolean isSuccessful = jresult.equals(mresult, tolerance.multiply(mresult.norm()));
                                if (!isSuccessful)
                                        System.out.println("FAILED " + "NDSolve = " + mresult + " != " + "ndSolve(x'(t)=" + f + ")" + " = " + jresult + "@" + jresult.getClass() + "\tdelta=" + jresult.subtract(mresult));
                                assertTrue(isSuccessful , "NDSolve = " + mresult + " != " + "ndSolve(x'(t)=" + f + ")" + " = " + jresult + "@" + jresult.getClass() + " at " + r + "\tdelta=" + jresult.subtract(mresult));
                        }
                }
        }

        // create (random) argument values

        private int integerArgument(ValueFactory vf, int min, int max) {
                return min + random.nextInt(max-min + 1);
        }
        private double realArgument(ValueFactory vf, double min, double max) {
                return ((max-min) * random.nextDouble() + min);
        }
        private int symbolId = 1;
        /**
         * Create random argument of the specified types
         * @param min
         * @param max
         * @param testType
         * @return
         */
        private Arithmetic randomArgument(ValueFactory vf, double min, double max, int testType) {
                Arithmetic x = randomArgumentImpl(vf, min, max, testType);
                checkArithmetic(x);
                return x;
        }
        private Arithmetic randomArgumentImpl(ValueFactory vf, double min, double max, int testType) {
                if ((testType & (TYPE_INTEGER|TYPE_RATIONAL|TYPE_REAL|TYPE_COMPLEX|TYPE_SYMBOL)) == 0)
                        // default type if no type that we could possible support/deliver
                        testType = TYPE_DEFAULT;
                if ((testType & TYPE_INTEGER) != 0 && Utility.flip(random, 0.3))
                        return vf.valueOf(integerArgument(vf, (int)min, (int)max));
                else if ((testType & TYPE_RATIONAL) != 0 && Utility.flip(random, 0.4))
                        return vf.rational(integerArgument(vf, (int)min, (int)max), integerArgument(vf, 1, (int)max));
                else if ((testType & TYPE_REAL) != 0 && Utility.flip(random, 0.4))
                        return vf.valueOf(realArgument(vf, min, max));
                else if ((testType & TYPE_COMPLEX) != 0 && Utility.flip(random, 0.4))
                        return vf.complex(realArgument(vf, min, max), realArgument(vf, min, max));
                else if ((testType & TYPE_SYMBOL) != 0 && Utility.flip(random, 0.4))
                        return vf.symbol("a" + (symbolId++));
                else { // random generator always said no, then choose first applying type
                        if ((testType & TYPE_INTEGER) != 0)
                                return vf.valueOf(integerArgument(vf, (int)min, (int)max));
                        else if ((testType & TYPE_RATIONAL) != 0)
                                return vf.rational(integerArgument(vf, (int)min, (int)max), integerArgument(vf, 1, (int)max));
                        else if ((testType & TYPE_REAL) != 0)
                                return vf.valueOf(realArgument(vf, min, max));
                        else if ((testType & TYPE_COMPLEX) != 0)
                                return vf.complex(realArgument(vf, min, max), realArgument(vf, min, max));
                        //      else if ((testType & TYPE_RATIONAL) != 0)
                        //          return vf.rational(integerArgument((int)min, (int)max), integerArgument(1, (int)max));
                        else if ((testType & TYPE_SYMBOL) != 0)
                                return vf.symbol("a" + (symbolId++));
                        else
                                throw new IllegalArgumentException("no type provided");
                }
        }
        private Matrix matrixArgument(ValueFactory vf, double min, double max, int testType, Dimension dim) {
                Matrix x = vf.newInstance(dim);
                if (testType == TYPE_REAL && Utility.flip(random, 0.5))
                        // randomly switch to RMatrix
                        x = vf.valueOf(new double[dim.height][dim.width]);
                for (int i = 0; i < dim.height; i++)
                        for (int j = 0; j < dim.width; j++)
                                x.set(i,j, randomArgument(vf, min, max, testType));
                checkArithmetic(x);
                return x;
        }
        private Vector vectorArgument(ValueFactory vf, double min, double max, int testType, int dim) {
                Vector x = vf.newInstance(dim);
                if (testType == TYPE_REAL && Utility.flip(random, 0.5))
                        // randomly switch to RVector
                        x = vf.valueOf(new double[dim]);
                for (int i = 0; i < dim; i++)
                        x.set(i, randomArgument(vf, min, max, testType));
                return x;
        }

        private Polynomial polyArgument(ValueFactory vf, double min, double max, int testType, int deg[]) {
                Tensor x = vf.ZERO(deg);
                for (ListIterator i = x.iterator(); i.hasNext(); ) {
                        i.next();
                        i.set(randomArgument(vf, min, max, testType));
                }
                Polynomial p = vf.asPolynomial(x);
                AlgebraicAlgorithmsTest.checkPolynomial(vf, p);
                return p;
        }

        /**
         * Checks several algebraic properties and relations of an arithmetic object.
         */
        protected boolean checkArithmetic(Arithmetic x) {
                if (x instanceof Polynomial)
                        return AlgebraicAlgorithmsTest.checkPolynomial(vf, (Polynomial)x);
                        return ArithmeticTest.checkArithmetic(vf, x, false);
                }
        protected final Predicate checkAlgebraic = new Predicate() {

                        public boolean apply(Object arg) {
                                return checkArithmetic((Arithmetic)arg);
                        }
                
        };

        private Polynomial polyArgument(ValueFactory vf, double min, double max, int testType, int varrank, int maxpartialdeg) {
                int deg[] = new int[varrank];
                for (int di = 0; di < deg.length; di++) {
                        deg[di] = integerArgument(vf, 1, maxpartialdeg);
                }
                return polyArgument(vf, min, max, testType, deg);
        }

        // Helpers

        private Complex getResult(ValueFactory vf, MathLink ml) throws MathLinkException, ExprFormatException {
                Expr e = ml.getExpr();
                if (e.integerQ()) { //(ml.getType() == MathLink.MLTKINT) {
                        return vf.valueOf(e.asBigInteger());
                } else if (e.realQ()) { //(ml.getType() == MathLink.MLTKINT) {
                        return vf.valueOf(e.asBigDecimal());
                } else if (e.complexQ()) {
                        // because of a flaw in the MathLink design, we can lose to double precision here
                        return vf.complex(e.re(), e.im());
                } else if ("Indeterminate".equals(e.toString())) {
                    return vf.NaN();
                } else if ("Infinity".equals(e.toString())) {
                    return vf.POSITIVE_INFINITY();
                } else if ("ComplexInfinity".equals(e.toString())) {
                    return vf.INFINITY();
                } else if ("DirectedInfinity".equals(e.head().toString())) {
                    return vf.INFINITY();
            } else {

                        throw new IllegalStateException("Cannot understand as number: " + e);
                //return ((ComplexAdapter) ml.getComplex()).getValue();
            }
        }


        /**
         * Adapter class between orbital.math.* and J/Link.
         * J/Link has a built in loss of precision that we are facing here: ONLY DOUBLE PRECISION SUPPORTED!
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
        private String listForm(Collection m) {
                StringBuffer sb = new StringBuffer();
                sb.append('{');
                for (Iterator i = m.iterator(); i.hasNext(); )
                        sb.append(i.next() + (i.hasNext() ? "," : ""));
                sb.append('}');
                return sb.toString();
        }
}
