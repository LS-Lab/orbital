import orbital.math.*;
import orbital.logic.functor.*;

/**
 * PolynomialEvaluation.java
 *
 *
 * @author Andr&eacute; Platzer
 * @version 1.0, 2002-08-15
 */

public class PolynomialEvaluation{
    
    public static void main(String[] args){
	Polynomial/*<Real>*/ f = Values.polynomial(new int[] {2,1,4,2});
	Arithmetic x = Values.valueOf(2);

	System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));

    	x = Values.valueOf(3.14);
	System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));

    	x = Values.rational(2,3);
	System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));

    	x = Values.complex(-3,2);
	System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));

    	x = Values.valueOf(new double[][] {
	    {2,1},
	    {-1,3}
	    });
	//@internal deactivated (requires c+a*b instead of c+b*a)
	//System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));
    }

    /**
     * implementation like Polynomial#apply(Object)
     */
    public static Object apply(Polynomial f, final Arithmetic a) {
	// horner schema is (|0, &lambda;c,b. c+b*a|) for foldRight like banana
	return Functionals.banana(Values.ZERO, new BinaryFunction() {
		public Object apply(Object c, Object b) {
		    return ((Arithmetic)c).add(((Arithmetic)b).multiply(a));
		}
	    }, f.iterator());
    }
    
} // PolynomialEvaluation
