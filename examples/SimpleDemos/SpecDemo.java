import orbital.SP;
import orbital.math.*;

/**
 * Demonstrate assertion specifications.
 * @deprecated sine JDK1.4 assertions have been included into the Java Language Specification.
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
	SP.asserts(true, "assertion that passes");
	try {
	    SP.asserts(false, "assertion that fails");
	} catch (AssertionError x) {
	    System.out.println(x);
	} 
	try {
	    SP.ask("Do you wish that this assertion passes?", "affirmation asking the tester");
	} catch (AssertionError x) {
	    System.out.println(x);
	} 
	SP.skipIf(true, "skipping all assertions");
	SP.asserts(false, "assertion that would fail if it had not been skipped");
	SP.skipIf(false, "end of skipping assertions");
    } 

    private static void useSpecifications() {
	System.out.println("use specifications for complex number arithmetic...");
	Complex a = Values.getDefaultInstance().complex(2, 5);
	SP.asserts(a != null, "instantiation should work");
	Complex b = Values.getDefaultInstance().complex(3, -1);
	SP.asserts(b != null, "instantiation should work");
	Complex c = a.multiply(b);
	SP.asserts(c != null, "multiplication returns a valid object");
	SP.asserts(c.re().doubleValue() == 11 && c.im().doubleValue() == 13, "multiplication (" + a + ") * (" + b + ") returned " + c);
	System.out.println("multiplication (" + a + ") * (" + b + ") = " + c);
	Complex d = c.conjugate();
	SP.asserts(d.re().equals(c.re()) && d.im().equals(c.im().minus()), "conjugate logic");
	System.out.println("conjugate is " + d);
	Real e = d.norm();
	System.out.println("norm: " + e);

	// produce rounding error
	double f = e.doubleValue();
	for (int i = 0; i < 10; i++)
	    f += 0.1;
	f -= 1;
	System.out.println("the next assertion will fail");
	SP.asserts(f * f == 290, "transformation does not change norm, except for rounding errors");
    } 
}
