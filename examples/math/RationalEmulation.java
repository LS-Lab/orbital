import orbital.math.*;
import orbital.math.Integer;

/**
 * Runnable application testing fractions and also emulating rational numbers.
 * Also "emulates" rational numbers by
 * fractions of integers.
 * Will compare results of calculation for rational numbers
 * and fractions of integers.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class RationalEmulation extends MathTest {
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) {
	emulationCalculation();
	simpleCalculation();
    }
    private static void simpleCalculation() {
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
	System.out.println("perform some calculations in Q[X] = Quot(Q[X]) = Quot(Z[X])");
	// create elements in Q(X) = Quot(Q[X]) = Quot(Z[X])
	Fraction/*<Polynomial<Rational>,Polynomial<Rational>>*/ f =
	    vf.fraction(vf.polynomial(new Rational[] {vf.ONE, vf.valueOf(2)}),
			vf.polynomial(new Rational[] {vf.ONE, vf.ONE, vf.ONE, vf.ONE, vf.ONE, vf.ONE}));
	Fraction/*<Polynomial<Rational>,Polynomial<Rational>>*/ g =
	    vf.fraction(vf.polynomial(new Rational[] {vf.ZERO, vf.ONE, vf.valueOf(2)}),
			vf.polynomial(new Rational[] {vf.ONE, vf.valueOf(2), vf.ONE, vf.valueOf(-4)}));

	// perform calculations in Q(X)
	printArithmetic(f,g,true);
    }

    /**
     * Rational emulation.
     */
    private static void emulationCalculation() {
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
	System.out.println("emulate rational numbers with fractions of integers");
	// create elements in Q alias Quot(Z)
	Fraction/*<Integer,Integer>*/ a =
	    vf.fraction(vf.valueOf(3), vf.valueOf(7));
	Rational ac = rationalForm(a);
	Fraction/*<Integer,Integer>*/ b =
	    vf.fraction(vf.valueOf(5), vf.valueOf(8));
	Rational bc = rationalForm(b);

	// perform calculations in both fields
	System.out.println("perform calculations in both fields and compare results");
	System.out.println("(" + a + ") + (" + b + ") = " + a.add(b));
	compare(a.add(b), ac.add(bc));
	System.out.println("(" + a + ") - (" + b + ") = " + a.subtract(b));
	compare(a.subtract(b), ac.subtract(bc));
	System.out.println("(" + a + ") * (" + b + ") = " + a.multiply(b));
	compare(a.multiply(b), ac.multiply(bc));
	System.out.println("(" + a + ") / (" + b + ") = " + a.divide(b));
	System.out.println("(" + a + ") * (" + b + ") + (" + a + ")= " + a.multiply(b).add(a));
	compare(a.multiply(b).add(a),
		ac.multiply(bc).add(ac));

	printArithmetic(a, b, true);
    }

    /**
     * Compares a rational value and a fraction of integers.
     */
    private static void compare(Fraction fclass, Rational c) {
	if (c.equals(rationalForm(fclass)))
	    return;
	else
	    throw new AssertionError(c + " != " + fclass);
    }
    /**
     * Convert a fraction of integers to rational form.
     */
    private static Rational rationalForm(Fraction/*<Integer>*/ fclass) {
	return Values.getDefaultInstance().rational((Integer)fclass.numerator(), (Integer)fclass.denominator());
    }
}
