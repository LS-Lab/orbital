import orbital.SP;
import orbital.AssertionError;
import orbital.math.*;

/**
 * Demonstrate assertion specifications.
 */
public class SpecDemo {
	public static void main(String arg[]) {
		try {
			demonstrateFeatures();
			useSpecifications();
		} catch (Exception x) {
			System.err.println(x);
		} 
		System.exit(0);	   // neccessary since askTester will display a dialog and thus, threads are created by JVM
	} 

	private static void demonstrateFeatures() {
		System.out.println("demonstrating all types of assertions...");
		SP.assert(true, "assertion that passes");
		try {
			SP.assert(false, "assertion that fails");
		} catch (AssertionError x) {
			System.out.println(x);
		} 
		try {
			SP.ask("Do you wish that this assertion passes?", "affirmation asking the tester");
		} catch (AssertionError x) {
			System.out.println(x);
		} 
		SP.skipIf(true, "skipping all assertions");
		SP.assert(false, "assertion that would fail if it had not been skipped");
		SP.skipIf(false, "end of skipping assertions");
	} 

	private static void useSpecifications() {
		System.out.println("use specifications for complex number arithmetic...");
		Complex a = Values.complex(2, 5);
		SP.assert(a != null, "instantiation should work");
		Complex b = Values.complex(3, -1);
		SP.assert(b != null, "instantiation should work");
		Complex c = a.multiply(b);
		SP.assert(c != null, "multiplication returns a valid object");
		SP.assert(c.re().doubleValue() == 11 && c.im().doubleValue() == 13, "multiplication (" + a + ") * (" + b + ") returned " + c);
		System.out.println("multiplication (" + a + ") * (" + b + ") = " + c);
		Complex d = c.conjugate();
		SP.assert(d.re().equals(c.re()) && d.im().equals(c.im().minus()), "conjugate logic");
		System.out.println("conjugate is " + d);
		Real e = d.norm();
		System.out.println("norm: " + e);

		// produce rounding error
		double f = e.doubleValue();
		for (int i = 0; i < 10; i++)
			f += 0.1;
		f -= 1;
		System.out.println("the next assertion will fail");
		SP.assert(f * f == 290, "transformation does not change norm, except for rounding errors");
	} 
}