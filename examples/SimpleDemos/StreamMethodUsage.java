import orbital.util.StreamMethod;
import java.util.Iterator;

import orbital.math.*;

/**
 * Demonstrates how to use stream methods as a means for simplified protocols.
 * Stream methods provide stream-valued method return-values, so they are especially
 * useful for methods that want to return a multitude of values (which you might have
 * encoded as arrays or collections).
 * Additionally, you can easily adapt the synchronicity.
 * 
 * @version 0.9, 2001/09/20
 * @author  Andr&eacute; Platzer
 */
public class StreamMethodUsage {
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) throws Exception {
        new StreamMethodUsage().run();
    }
    
    /**
     * Runnable-init entry point.
     */
    public StreamMethodUsage() {
    }
    
    /**
     * Runnable-start entry point.
     */
    public void run() {
    	computeSomeSquareRoots();
    	computeSomeHigherRoots();
    }

    private void computeSomeSquareRoots() {
	System.out.println("square roots ...");
    	// compute all square roots of 16
    	Iterator roots = SquareRoot.sqrt(16);
    	while (roots.hasNext())
	    System.out.println("sqrt(16) = " + roots.next());

    	// compute all square roots of 123.4321
    	roots = SquareRoot.sqrt(123.4321);
    	while (roots.hasNext())
	    System.out.println("sqrt(123.4321) = " + roots.next());
    }

    private void computeSomeHigherRoots() {
	System.out.println("\nhigher roots ...");

    	// print all 2nd roots of 16
    	printAllRoots(2, Values.valueOf(16.0));
    	// print all 3rd roots of 16
    	printAllRoots(3, Values.valueOf(16.0));
    	// print all 3rd roots of 2+i
    	printAllRoots(3, Values.complex(2, 1));
    	// print all 4th roots of 2+i
    	printAllRoots(4, Values.complex(2, 1));
    	// print all 5th roots of 2+i
    	printAllRoots(5, Values.complex(2, 1));
    	// print all 6th roots of 2+i
    	printAllRoots(6, Values.complex(2, 1));
    	// print all 3rd roots of 2-2i
    	printAllRoots(3, Values.complex(2, -2));
    }
    
    private void printAllRoots(int n, Complex value) {
    	// compute all n-th roots of value and print them
    	Iterator roots = SquareRoot.root(n, value);
    	while (roots.hasNext())
	    System.out.println(n + "throot(" + value + ") = " + roots.next());
    }
}

class SquareRoot {
    /**
     * Whether we want to compute the roots upon request only (true),
     * or whether we already start computing the next roots and store them
     * until they are needed (false).
     */
    private static final boolean SYNCHRONOUS_ROOTS = true;
    /**
     * Get all square roots ±sqrt(v) of a value.
     * @param v the value v whose square roots to determine.
     * @return ±sqrt(v) alias {+sqrt(v), - sqrt(v)}.
     */
    public static final Iterator sqrt(final double v) {
	return new StreamMethod(SYNCHRONOUS_ROOTS) {
		public void runStream() {
		    double root1 = +Math.sqrt(v);
		    resumedReturn(new Double(root1));
		    double root2 = -Math.sqrt(v);
		    resumedReturn(new Double(root2));
		}
	    }.apply();
    }

    /**
     * Get all n-th roots of a (complex) value.
     * @param v the value v whose n-th roots to determine.
     * @return <em>all</em> n-th roots.
     */
    public static final Iterator root(final int n, final Complex v) {
	return new StreamMethod(SYNCHRONOUS_ROOTS) {
		public void runStream() {
		    // the absolute r = |v| of v
		    double r = v.norm().doubleValue();
		    // the argument phi of v (also known as principal angle)
		    double phi = v.arg().doubleValue();
				
		    double pi = Math.PI;

		    for (int k = 0; k < n; k++) {
			Complex root = Values.polar(Math.pow(r, 1.0/n), (phi + 2*k*pi) / n);
			resumedReturn(root);
		    }
		}
	    }.apply();
    }
}
