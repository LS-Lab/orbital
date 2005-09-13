import orbital.math.*;
import orbital.math.Integer;
import orbital.logic.functor.Function;

/**
 * Runnable application emulating complex numbers by
 * quotients of polynomials.
 * Will compare results of calculation for complex numbers
 * and quotients of polynomials.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class ComplexEmulation extends MathTest {
    private static final int modulus = 17;
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) {
        emulationCalculation();
    }
    private static void emulationCalculation() {
        // get us a value factory for creating arithmetic objects
        final Values vf = Values.getDefaultInstance();
        System.out.println("emulate complex numbers with quotients of polynomials");
        // create elements in C alias R[X]/(X^2+1)
        final UnivariatePolynomial/*<Real>*/ m =
            (UnivariatePolynomial) vf.polynomial(new double[] {1,0,1});
        Quotient/*<Polynomial<Real>>*/ a =
            vf.quotient(vf.polynomial(new double[] {2,4}), m);
        Complex ac = complexForm(a);
        Quotient/*<Polynomial<Real>>*/ b =
            vf.quotient(vf.polynomial(new double[] {-3,1}), m);
        Complex bc = complexForm(b);
        // i in C corresponds to X in R[X]/(X^2+1)
        Quotient/*<Polynomial<Real>>*/ i =
            vf.quotient(vf.polynomial(new double[] {0,1}), m);
        // perform calculations in both fields
        System.out.println("perform calculations in both fields and compare results");
        System.out.println("(" + a + ") + (" + b + ") = " + a.add(b));
        compare(a.add(b), ac.add(bc));
        System.out.println("(" + a + ") - (" + b + ") = " + a.subtract(b));
        compare(a.subtract(b), ac.subtract(bc));
        System.out.println("(" + a + ") * (" + b + ") = " + a.multiply(b));
        compare(a.multiply(b), ac.multiply(bc));
        System.out.println("(" + a + ") * (" + b + ") + (" + a + ")*i= " + a.multiply(b).add(a.multiply(i)));
        compare(a.multiply(b).add(a.multiply(i)),
                ac.multiply(bc).add(ac.multiply(vf.i)));

        printArithmetic(a, b, false);
    }

    /**
     * Compares a complex value and a polynomial in R[X]/(X^2+1).
     */
    private static void compare(Quotient pclass, Complex c) {
        UnivariatePolynomial p = (UnivariatePolynomial) pclass.representative();
        if (p.degreeValue() > 2)
            throw new AssertionError("R[X]/(X^2+1) does not contain polynomials of (reduced) degree " + p.degree());
        if (c.re().equals(p.get(0)) && c.im().equals(p.get(1)))
            return;
        else
            throw new AssertionError(c + " != " + p);
    }
    /**
     * Convert a polynomial in R[X]/(X^2+1) to complex form.
     */
    private static Complex complexForm(Quotient/*<Polynomial>*/ pclass) {
        UnivariatePolynomial p = (UnivariatePolynomial) pclass.representative();
        if (p.degreeValue() > 2)
            throw new AssertionError("R[X]/(X^2+1) does not contain polynomials of (reduced) degree " + p.degree());
        return Values.getDefaultInstance().complex((Real)p.get(0), (Real)p.get(1));
    }
}
