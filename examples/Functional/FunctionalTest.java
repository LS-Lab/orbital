import orbital.math.Arithmetic;

import orbital.math.functional.*;
import orbital.math.*;

public class FunctionalTest {
    public static void main(String arg[]) throws Exception {
	orbital.Adjoint.print("Debug");
	System.out.println("3+6=" + Operations.plus.apply(Values.valueOf(3), Values.valueOf(6)));
	System.out.println("3.14+6/15=" + Operations.plus.apply(Values.valueOf(3.14), Operations.divide.apply(Values.valueOf(6), Values.valueOf(15))));
	Function h = (Function) Operations.plus.apply(Functions.square, Functions.linear(Values.valueOf(2)));
	System.out.println("f+g=" + h);
	System.out.println("(f+g)(4)=" + h.apply(Values.valueOf(4)));
	h = (Function) Operations.plus.apply(Operations.inverse, Operations.minus);
	System.out.println("(1/x)+(-x)=" + h + " (functional)");
	System.out.println("((1/x)+(-x)) (7) = " + h.apply(Values.valueOf(7)));
	System.out.println("((1/x)+(-x)) (8/14) = " + h.apply(Values.rational(8, 14)) + "");

	// ((-)*id) (x) = (-x)*x
	h = (Function) Operations.times.apply(Operations.minus, Functions.id);
	System.out.println("Apply function (-)*id (x) = " + h.apply(Values.symbol("x")) + " to multiple arguments of various types");
	System.out.println("(-)*id = " + h + " (functional)");
	System.out.println("(-)*id (5) = " + h.apply(Values.valueOf(5)) + " (applied to scalar)");
	System.out.println("(-)*id (4/7) = " + h.apply(Values.rational(4, 7)) + " (applied to rational scalar)");
	System.out.println("(-)*id (2+3i) = " + h.apply(Values.complex(2, 3)) + " (applied to complex)");
	Vector v = Values.valueOf(new double[] {
	    3, -2
	});
	System.out.println("(-)*id (" + v + ") = " + h.apply(v) + " (applied to vector with scalar dot product)");
	Matrix M = Values.valueOf(new double[][] {
	    {1, 2},
	    {4, -1}
	});
	System.out.println("(-)*id (\n" + M + "\n) =\n" + h.apply(M) + " (applied to matrix)");
	System.out.println("(-)*id (a) = " + h.apply(Values.symbol("a")) + " (applied to symbol)");
    } 
}
