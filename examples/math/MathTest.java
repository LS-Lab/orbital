import orbital.math.*;

/**
 * Base class for testing some arithmetics.
 *
 * @author Andr&eacute; Platzer
 * @version 1.0, 2002-08-23
 */
public abstract class MathTest {
    public static final void printArithmetic(Arithmetic x, Arithmetic y, boolean withDivisions) {
	Rational a = Values.valueOf(4);
	System.out.println("(" + x.zero() + ") + (" + x + ") = " + x.zero().add(x));
	assert x.zero().add(x).equals(x) : "0+x=x\t(" + x.zero() + ") + (" + x + ") = " + x.zero().add(x) + " = " + x;
	assert x.add(x.zero()).equals(x) : "x+0=x\t(" + x + ") + (" + x.zero() + ") = " + x;
	assert x.minus().add(x).equals(x.zero()) : "(-x)+x=0\t-(" + x + ") + (" + x + ") = " + x.zero();
	assert x.add(x.minus()).equals(x.zero()) : "x+(-x)=0\t(" + x + ") + (-(" + x + ")) = " + x.zero();
	assert x.subtract(x).equals(x.zero()) : "x-x=0\t(" + x + ") - (" + x + ") = " + x.zero();
	System.out.println("(" + x + ") + (" + y + ") = " + x.add(y));
	System.out.println("-(" + x + ") = " + x.minus());
	System.out.println("(" + x + ") - (" + y + ") = " + x.subtract(y));
	System.out.println(a + "*(" + x + ") = " + x.scale(a));
	System.out.println("(" + x.one() + ") * (" + x + ") = " + x.one().multiply(x));
	assert x.one().multiply(x).equals(x) : "1*x=x\t" + x.one() + " * (" + x + ") = " + x;
	assert x.multiply(x.one()).equals(x) : "x*1=x\t(" + x + ") * " + x.one() + " = " + x;
	System.out.println("(" + x + ") * (" + y + ") = " + x.multiply(y));
	if (withDivisions) {
	    System.out.println("(" + x + ")^-1 = " + x.inverse());
	    assert x.inverse().multiply(x).equals(x.one()) : "x^-1 * x=1\t(" + x.inverse() + ") * (" + x + ") = " + x.one();
	    assert x.multiply(x.inverse()).equals(x.one()) : "x*x^-1=1\t(" + x + ") * (" + x.inverse() + ") = " + x.one();
	    System.out.println("(" + x.one() + ") / (" + x + ") = " + x.one().divide(x));
	    assert x.one().divide(x).equals(x.inverse()) : "1/x=x^-1\t" + x.one() + " / (" + x + ") = " + x.inverse();
	    System.out.println("(" + x + ") / (" + x.one() + ") = " + x.divide(x.one()));
	    assert x.divide(x.one()).equals(x) : "x/1=x\t(" + x + ") / 1 = " + x;
	    System.out.println("(" + x + ") / (" + y + ") = " + x.divide(y));
	}
    }
}

