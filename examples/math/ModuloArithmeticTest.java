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
	// create elements in Z/nZ
	final int n = 17;
	Quotient/*<Integer>*/ i = Values.quotient(8, n);
	Quotient/*<Integer>*/ j = Values.quotient(11, n);

	// perform calculations in Z/mZ
   	System.out.println("Calculating modulo n=" + n + "\tfast normalization to (-n,n)");
	printArithmetic(i, j, false);
	Arithmetic c = i.add(j.multiply(i));
	System.out.println(c);
    }

    private static void moreSophisticatedCalculation() {
    	System.out.println("Calculating modulo m=" + modulus + "\tnormalization to [0,m)");
    	final Function/*<Integer,Integer>*/ m = new Function/*<Integer,Integer>*/() {
    		public Object/*>Integer<*/ apply(Object/*>Integer<*/ i) {
		    return Values.valueOf(((((Integer)i).intValue() % modulus) + modulus) % modulus);
    		}
	    };
        Quotient/*<Integer>*/ i = Values.quotient(Values.valueOf(3), m);
        Quotient/*<Integer>*/ j = Values.quotient(Values.valueOf(-1), m);
	// perform calculations in Z / modulus*Z
	printArithmetic(i, j, false);
	//System.out.println("(" + i + ") / (" + j + ") = " + i.divide(j));
    }
}
