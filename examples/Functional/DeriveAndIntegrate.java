import orbital.math.Scalar;
import orbital.math.functional.*;
import orbital.math.Values;

public class DeriveAndIntegrate {
    public static void main(String arg[]) throws Exception {
	test_unary();
	test_operations();

	// test_binary();
    } 
    private static void test_unary() throws Exception {
	System.out.println("Derivatives elemental unary functions");
	Function f = Functions.constant(Values.getDefaultInstance().valueOf(7));
	System.out.println(f + " ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functions.id;
	System.out.println(f + " ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functions.pow(Values.getDefaultInstance().valueOf(5));
	System.out.println(f + " ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functions.reciprocal;
	System.out.println(f + " ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functions.exp;
	System.out.println(f + " ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functions.log;
	System.out.println(f + " ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functions.exp(Values.getDefaultInstance().valueOf(10));
	System.out.println(f + " ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
    } 

    private static void test_operations() throws Exception {
	System.out.println("Derivatives elemental operation functions");
	Function a = Functions.symbolic("f"), b = Functions.symbolic("g");
	Function f = Functionals.compose(Operations.plus, a, b);
	System.out.println("(" + f + ") ' = " + f.derive());
	f = Functionals.compose(Operations.minus, a);
	System.out.println("(" + f + ") ' = " + f.derive());
	f = Functionals.compose(Operations.subtract, a, b);
	System.out.println("(" + f + ") ' = " + f.derive());
	f = Functionals.compose(Operations.times, a, b);
	System.out.println("(" + f + ") ' = " + f.derive());
	f = Functionals.compose(Operations.inverse, a);
	System.out.println("(" + f + ") ' = " + f.derive());
	f = Functionals.compose(Operations.divide, a, b);
	System.out.println("(" + f + ") ' = " + f.derive());
	f = Functionals.compose(Operations.power, a, b);
	System.out.println("(" + f + ") ' = " + f.derive());

	f = Functionals.compose(Operations.plus, a, b);
	System.out.println("(" + f + ") ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functionals.compose(Operations.minus, a);
	System.out.println("(" + f + ") ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functionals.compose(Operations.subtract, a, b);
	System.out.println("(" + f + ") ' = " + f.derive() + "\n  integral " + f + " = " + f.integrate());
	f = Functionals.compose(Operations.times, a, b);
	System.out.println("(" + f + ") ' = " + f.derive() + "\n  integral " + f + " = " /* +f.integrate() */);
	try {
	    System.out.println(f.integrate());
	} catch (Throwable trial) {}
	f = Functionals.compose(Operations.inverse, a);
	System.out.println("(" + f + ") ' = " + f.derive() + "\n  integral " + f + " = " /* +f.integrate() */);
	try {
	    System.out.println(f.integrate());
	} catch (Throwable trial) {}
	f = Functionals.compose(Operations.divide, a, b);
	System.out.println("(" + f + ") ' = " + f.derive() + "\n  integral " + f + " = " /* +f.integrate() */);
	try {
	    System.out.println(f.integrate());
	} catch (Throwable trial) {}
	f = Functionals.compose(Operations.power, a, b);
	System.out.println("(" + f + ") ' = " + f.derive() + "\n  integral " + f + " = " /* +f.integrate() */);
	try {
	    System.out.println(f.integrate());
	} catch (Throwable trial) {}
    } 
}
