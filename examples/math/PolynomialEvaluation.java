import orbital.math.*;
import orbital.logic.functor.*;

/**
 * PolynomialEvaluation.java
 *
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 */

public class PolynomialEvaluation{
    
    public static void main(String[] args){
        // get us a value factory for creating arithmetic objects
        final Values vf = Values.getDefaultInstance();
        Polynomial/*<Real>*/ f = vf.polynomial(new int[] {2,1,4,2});
        Arithmetic x = vf.valueOf(2);

        System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));

        x = vf.valueOf(3.14);
        System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));

        x = vf.rational(2,3);
        System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));

        x = vf.complex(-3,2);
        System.out.println(f + " evaluated at " + x + " is\t" + apply(f, x));

        x = vf.valueOf(new double[][] {
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
        return Functionals.banana(a.zero(), new BinaryFunction() {
                public Object apply(Object c, Object b) {
                    return ((Arithmetic)c).add(((Arithmetic)b).multiply(a));
                }
            }, f.iterator());
    }
    
} // PolynomialEvaluation
