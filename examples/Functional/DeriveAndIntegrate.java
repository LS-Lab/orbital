import orbital.math.functional.*;
import orbital.math.*;

public class DeriveAndIntegrate {
    public static void main(String arg[]) throws Exception {
	test_unary();
	test_operations();

	// test_binary();
    } 
    private static void test_unary() throws Exception {
	System.out.println("Derivatives elemental unary functions");
	print(Functions.constant(Values.getDefaultInstance().valueOf(7)));
	print(Functions.id);
	print(Functions.pow(Values.getDefaultInstance().valueOf(5)));
	print(Functions.reciprocal);
	print(Functions.exp);
	print(Functions.log);
	print(Functions.exp(Values.getDefaultInstance().valueOf(10)));
    }
    private static void print(Function f) {
	Arithmetic x = Values.getDefaultInstance().symbol("x");
	System.out.print("(" + f.apply(x) + ") ' = ");
	System.out.print(f.derive().apply(x) + "\n  integral " + f + " = ");
	Function intf = null;
	try {
	    intf = f.integrate();;
	}
	catch (Throwable notintegrable) {}
	System.out.print(intf != null ? intf.apply(x) : "<null>");
	System.out.println();
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

	print(Functionals.compose(Operations.plus, a, b));
	print(Functionals.compose(Operations.minus, a));
	print(Functionals.compose(Operations.subtract, a, b));
	print(Functionals.compose(Operations.times, a, b));
	print(Functionals.compose(Operations.inverse, a));
	print(Functionals.compose(Operations.divide, a, b));
	print(Functionals.compose(Operations.power, a, b));
    } 
}
