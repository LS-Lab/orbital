import orbital.math.*;
import orbital.math.Integer;
import orbital.logic.functor.Function;

/**
 * Runnable application emulating complex numbers by
 * quotients of polynomials.
 * Will compare results of calculation for complex numbers
 * and quotients of polynomials.
 *
 * @version 0.8, 2002/04/28
 * @author  Andr&eacute; Platzer
 */
public class ComplexEmulation {
    private static final int modulus = 17;
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) {
	emulationCalculation();
    }
    private static void emulationCalculation() {
	// create elements in C alias R[X]/(X^2+1)
	final Polynomial/*<Real>*/ m =
	    Values.asPolynomial(Values.valueOf(new double[] {1,0,1}));
	Quotient/*<Polynomial<Real>>*/ a =
	    Values.quotient(Values.polynomial(new double[] {2,4}), m);
	Complex ac = complexForm(a);
	Quotient/*<Polynomial<Real>>*/ b =
	    Values.quotient(Values.polynomial(new double[] {-3,1}), m);
	Complex bc = complexForm(b);
	// i in C corresponds to X in R[X]/(X^2+1)
	Quotient/*<Polynomial<Real>>*/ i = Values.quotient(
						       Values.asPolynomial(Values.valueOf(new double[] {0,1})),
						       m);
	// perform calculations in both fields
	System.out.println("(" + a + ") + (" + b + ") = " + a.add(b));
	compare(a.add(b), ac.add(bc));
	System.out.println("(" + a + ") - (" + b + ") = " + a.subtract(b));
	compare(a.subtract(b), ac.subtract(bc));
	System.out.println("(" + a + ") * (" + b + ") = " + a.multiply(b));
	compare(a.multiply(b), ac.multiply(bc));
	System.out.println("(" + a + ") * (" + b + ") + (" + a + ")*i= " + a.multiply(b).add(a.multiply(i)));
	compare(a.multiply(b).add(a.multiply(i)),
		ac.multiply(bc).add(ac.multiply(Values.i)));
    }

    /**
     * Compares a complex value and a polynomial in R[X]/(X^2+1).
     */
    private static void compare(Quotient pclass, Complex c) {
	Polynomial p = (Polynomial) pclass.representative();
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
	Polynomial p = (Polynomial) pclass.representative();
	if (p.degreeValue() > 2)
	    throw new AssertionError("R[X]/(X^2+1) does not contain polynomials of (reduced) degree " + p.degree());
	return Values.complex((Real)p.get(0), (Real)p.get(1));
    }
}
