import orbital.math.*;
import orbital.math.Integer;
import orbital.logic.functor.Function;

/**
 * Runnable application ModuloArithmeticTest.
 *
 * @version 0.8, 2002/02/09
 * @author  Andr&eacute; Platzer
 */
public class ModuloArithmeticTest extends MathTest {
    private static final int modulus = 17;
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) {
	simpleCalculation();
	moreSophisticatedCalculation();
    }
    private static void simpleCalculation() {
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
	// create elements in Z/nZ
	final int n = 17;
	Quotient/*<Integer>*/ i = vf.quotient(8, n);
	Quotient/*<Integer>*/ j = vf.quotient(11, n);

	// perform calculations in Z/mZ
   	System.out.println("Calculating modulo n=" + n + "\tfast normalization to (-n,n)");
	printArithmetic(i, j, false);
	Arithmetic c = i.add(j.multiply(i));
	System.out.println(c);
    }

    private static void moreSophisticatedCalculation() {
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
    	System.out.println("Calculating modulo m=" + modulus + "\tnormalization to [0,m)");
    	final Function/*<Integer,Integer>*/ m = new Function/*<Integer,Integer>*/() {
    		public Object/*>Integer<*/ apply(Object/*>Integer<*/ i) {
		    return vf.valueOf(((((Integer)i).intValue() % modulus) + modulus) % modulus);
    		}
	    };
        Quotient/*<Integer>*/ i = vf.quotient(vf.valueOf(3), m);
        Quotient/*<Integer>*/ j = vf.quotient(vf.valueOf(-1), m);
	// perform calculations in Z / modulus*Z
	printArithmetic(i, j, false);
	//System.out.println("(" + i + ") / (" + j + ") = " + i.divide(j));
    }
}
