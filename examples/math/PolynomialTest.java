import orbital.math.*;

class PolynomialTest extends MathTest {
    private PolynomialTest() {}
    public static void main(String arg[]) {
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
	{
	    Polynomial/*<Real>*/ f = vf.polynomial(new double[] {2,1,4,2});
	    Polynomial/*<Real>*/ g = vf.polynomial(new double[] {3,5,8,-1});
	    System.out.println("(" + f + ") * (" + g + ") = " + f.multiply(g));
	}
	UnivariatePolynomial/*<Rational>*/ f = vf.polynomial(new Rational[] {vf.ZERO, vf.ZERO, vf.ONE});
	UnivariatePolynomial/*<Rational>*/ g = vf.polynomial(new Rational[] {vf.ZERO, vf.ONE});
	Rational a = vf.rational(4);
	printArithmetic(f,g,false);
	System.out.println("(" + f + ") div (" + g + ") = " + f.quotient(g));
	System.out.println("(" + f + ") mod (" + g + ") = " + f.modulo(g));
	System.out.println("(" + f + ")(" + a + ") = " + f.apply(a) + " = " + f.modulo(vf.polynomial(new Rational[] {(Rational) a.minus(), vf.ONE})));
	Euclidean v[] = AlgebraicAlgorithms.gcd(new Euclidean[] {f, g});
	System.out.println("gcd(" + f + "," + g + ") = " + v[v.length - 1] + " = (" + v[0] + ")*(" + f + ") + (" + v[1] + ")*(" + g + ")");

	f = vf.polynomial(new Rational[] {vf.ONE, vf.ONE, vf.ONE, vf.ONE, vf.ONE, vf.ONE});
	g = vf.polynomial(new Rational[] {vf.ONE, vf.valueOf(-1), vf.ZERO, vf.valueOf(-1), vf.ONE});
	printArithmetic(f,g,false);
	System.out.println("(" + f + ") div (" + g + ") = " + f.quotient(g));
	System.out.println("(" + f + ") mod (" + g + ") = " + f.modulo(g));
	Euclidean rem = f.modulo(vf.polynomial(new Rational[] {(Rational) a.minus(), vf.ONE}));
	System.out.println("(" + f + ")(" + a + ") = " + f.apply(a) + " = " + rem);
	assert f.apply(a).toString().equals(rem.toString()) : "weak form of constant polynomial / rational equality";
	a = vf.rational(2,7);
	rem = f.modulo(vf.polynomial(new Rational[] {(Rational) a.minus(), vf.ONE}));
	System.out.println("(" + f + ")(" + a + ") = " + f.apply(a) + " = " + rem);
	assert f.apply(a).toString().equals(rem.toString()) : "weak form of constant polynomial / rational equality";
	v = AlgebraicAlgorithms.gcd(new Euclidean[] {f, g});
	System.out.println("gcd(" + f + "," + g + ") = " + v[v.length - 1] + " = (" + v[0] + ")*(" + f + ") + (" + v[1] + ")*(" + g + ")");
    }

}
