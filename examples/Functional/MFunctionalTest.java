import orbital.math.Scalar;
import orbital.math.Values;
import orbital.math.functional.*;
import orbital.math.functional.Functions;

//@fixme
public class MFunctionalTest {
    public static void main(String arg[]) throws Exception {
	System.out.println();
	Function f = Functionals.compose(Operations.times, Functions.id, Functionals.compose(Operations.plus, Functions.id, Functions.id));
	System.out.println("f = " + f);
	f = (Function) Functions.id.multiply(Functions.id.add(Functions.id));
	System.out.println("f = " + f);
	System.out.println("f(3) = " + f.apply(Values.valueOf(3)));
	System.out.println("df/dx = f' = " + f.derive());
	System.out.println("df/dx(3) = " + f.derive().apply(Values.valueOf(3)));
	BinaryFunction g = Functionals.compose(Operations.times, Functionals.onFirst(Functions.id), Operations.plus);
	System.out.println("g = " + g);
	System.out.println("g(x,y) = " + g.apply(Values.symbol("x"), Values.symbol("y")));
	System.out.println("g(7,8) = " + g.apply(Values.valueOf(7), Values.valueOf(8)));
	System.out.println("dg/d(x,y) = " + g.derive());
	System.out.println("dg/d(x,y)(7,8) = " + g.derive().apply(Values.valueOf(7), Values.valueOf(8)));
	System.out.println("dg/d(x,y) = " + g.derive());
	System.out.println("dg/d(x,y)(7,8) = " + g.derive().apply(Values.valueOf(7), Values.valueOf(8)));

    } 
}
