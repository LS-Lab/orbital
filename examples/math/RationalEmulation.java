import orbital.math.*;
import orbital.math.Integer;

/**
 * Runnable application FractionTest.
 * Also "emulates" rational numbers by
 * fractions of integers.
 * Will compare results of calculation for rational numbers
 * and fractions of integers.
 *
 * @version 0.8, 2002/06/18
 * @author  Andr&eacute; Platzer
 */
public class FractionTest {
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) {
	simpleCalculation();
	emulationCalculation();
    }
    private static void simpleCalculation() {
	// create elements in Q(X) = Quot(Q[X]) = Quot(Z[X])
	Fraction/*<Polynomial<Rational>,Polynomial<Rational>>*/ f =
	    Values.fraction(Values.polynomial(new Rational[] {Values.ONE, Values.valueOf(2)}),
			    Values.polynomial(new Rational[] {Values.ONE, Values.ONE, Values.ONE, Values.ONE, Values.ONE, Values.ONE}));
	Fraction/*<Polynomial<Rational>,Polynomial<Rational>>*/ g =
	    Values.fraction(Values.polynomial(new Rational[] {Values.ZERO, Values.ONE, Values.valueOf(2)}),
			    Values.polynomial(new Rational[] {Values.ONE, Values.valueOf(2), Values.ONE, Values.valueOf(-4)}));

	// perform calculations in Q(X)
	System.out.println("-(" + f + ") = " + f.minus());
	System.out.println("(" + f + ") + (" + g + ") = " + f.add(g));
	System.out.println("(" + f + ") - (" + g + ") = " + f.subtract(g));
	System.out.println("(" + f + ") * (" + g + ") = " + f.multiply(g));
	System.out.println("(" + f + ")^-1 = " + f.inverse());
	System.out.println("(" + f + ") / (" + g + ") = " + f.divide(g));
    }

    private static void emulationCalculation() {
	// create elements in Q alias Quot(Z)
	Fraction/*<Integer,Integer>*/ a =
	    Values.fraction(Values.valueOf(3), Values.valueOf(7));
	Rational ac = rationalForm(a);
	Fraction/*<Integer,Integer>*/ b =
	    Values.fraction(Values.valueOf(5), Values.valueOf(8));
	Rational bc = rationalForm(b);

	// perform calculations in both fields
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
	return Values.rational((Integer)fclass.numerator(), (Integer)fclass.denominator());
    }
}
