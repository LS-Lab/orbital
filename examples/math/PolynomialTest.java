import orbital.math.*;

import orbital.SP;

class PolynomialTest {
    private PolynomialTest() {}
    public static void main(String arg[]) {
	{
	    Polynomial/*<Real>*/ f = Values.polynomial(new double[] {2,1,4,2});
	    Polynomial/*<Real>*/ g = Values.polynomial(new double[] {3,5,8,-1});
	    System.out.println("(" + f + ") * (" + g + ") = " + f.multiply(g));
	}
	Polynomial/*<Rational>*/ f = Values.polynomial(new Rational[] {Values.ZERO, Values.ZERO, Values.ONE});
	Polynomial/*<Rational>*/ g = Values.polynomial(new Rational[] {Values.ZERO, Values.ONE});
	System.out.println("(" + f + ") + (" + g + ") = " + f.add(g));
	System.out.println("(" + f + ") - (" + g + ") = " + f.subtract(g));
	System.out.println("(" + f + ") * (" + g + ") = " + f.multiply(g));
	System.out.println("(" + f + ") div (" + g + ") = " + f.quotient(g));
	System.out.println("(" + f + ") mod (" + g + ") = " + f.modulo(g));
	Rational a = Values.rational(4);
	System.out.println("(" + f + ")(" + a + ") = " + f.apply(a) + " = " + f.modulo(Values.polynomial(new Rational[] {(Rational) a.minus(), Values.ONE})));
	Euclidean v[] = MathUtilities.gcd(new Polynomial/*<Rational>*/[] {f, g});
	System.out.println("gcd(" + f + "," + g + ") = " + v[v.length - 1] + " = (" + v[0] + ")*(" + f + ") + (" + v[1] + ")*(" + g + ")");

	f = Values.polynomial(new Rational[] {Values.ONE, Values.ONE, Values.ONE, Values.ONE, Values.ONE, Values.ONE});
	g = Values.polynomial(new Rational[] {Values.ONE, Values.valueOf(-1), Values.ZERO, Values.valueOf(-1), Values.ONE});
	System.out.println("(" + f + ") + (" + g + ") = " + f.add(g));
	System.out.println("(" + f + ") - (" + g + ") = " + f.subtract(g));
	System.out.println("(" + f + ") * (" + g + ") = " + f.multiply(g));
	System.out.println("(" + f + ") div (" + g + ") = " + f.quotient(g));
	System.out.println("(" + f + ") mod (" + g + ") = " + f.modulo(g));
	Euclidean rem = f.modulo(Values.polynomial(new Rational[] {(Rational) a.minus(), Values.ONE}));
	System.out.println("(" + f + ")(" + a + ") = " + f.apply(a) + " = " + rem);
	SP.assert(f.apply(a).toString().equals(rem.toString()), "weak form of constant polynomial / rational equality");
	a = Values.rational(2,7);
	rem = f.modulo(Values.polynomial(new Rational[] {(Rational) a.minus(), Values.ONE}));
	System.out.println("(" + f + ")(" + a + ") = " + f.apply(a) + " = " + rem);
	SP.assert(f.apply(a).toString().equals(rem.toString()), "weak form of constant polynomial / rational equality");
	v = MathUtilities.gcd(new Polynomial/*<Rational>*/[] {f, g});
	System.out.println("gcd(" + f + "," + g + ") = " + v[v.length - 1] + " = (" + v[0] + ")*(" + f + ") + (" + v[1] + ")*(" + g + ")");
    }

}
