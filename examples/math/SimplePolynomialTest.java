import orbital.math.*;
import orbital.math.functional.*;

/**
 * PolynomialTest.java
 *
 *
 * Created: Tue Apr 30 17:40:27 2002
 *
 * @author Andr&eacute; Platzer
 * @version
 */

public class SimplePolynomialTest {
    public static void main(String arg[]) {
        // get us a value factory for creating arithmetic objects
        final Values vf = Values.getDefaultInstance();
        Function p = Functions.polynom(5);
        final Symbol x = vf.symbol("X");

        p = vf.asPolynomial(vf.valueOf(new int[] {1,2,3,4}));
        System.out.println("polynomial p=" + p);
        System.out.println("derive\t(" + p + ")' =\t" + p.derive());
        System.out.println("derive\t(" + p.apply(x) + ")' =\t" + p.derive().apply(x));
        System.out.println("integrate\t(" + p + ") =\t" + p.integrate());
        System.out.println("integrate\t(" + p.apply(x) + ") =\t" + p.integrate().apply(x));

        p = Functions.polynom(vf.valueOf(new int[] {1,2,3,4}));
        System.out.println("derive\t(" + p + ")' =\t" + p.derive());
        System.out.println("derive\t(" + p.apply(x) + ")' =\t" + p.derive().apply(x));
        System.out.println("integrate\t(" + p + ") =\t" + p.integrate());
        System.out.println("integrate\t(" + p.apply(x) + ") =\t" + p.integrate().apply(x));

        p = Functions.polynom(5);
        System.out.println("polynomial p=" + p);
        System.out.println("derive\t(" + p + ")' =\t" + p.derive());
        System.out.println("derive\t(" + p.apply(x) + ")' =\t" + p.derive().apply(x));
        System.out.println("integrate\t(" + p + ") =\t" + p.integrate());
        System.out.println("integrate\t(" + p.apply(x) + ") =\t" + p.integrate().apply(x));

    }
    
}// PolynomialTest
