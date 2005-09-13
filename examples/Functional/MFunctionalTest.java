import orbital.math.*;
import orbital.math.functional.*;

//@fixme
public class MFunctionalTest {
    public static void main(String arg[]) throws Exception {
        // get us a value factory for creating arithmetic objects
        final Values vf = Values.getDefaultInstance();
        System.out.println();
        Function f =
            Functionals.compose(Operations.times, Functions.id, Functionals.compose(Operations.plus, Functions.id, Functions.id));
        System.out.println("f = " + f);
        f = (Function) Functions.id.multiply(Functions.id.add(Functions.id));
        System.out.println("f = " + f);
        System.out.println("f(3) = " + f.apply(vf.valueOf(3)));
        System.out.println("df/dx = f' = " + f.derive());
        System.out.println("df/dx(3) = " + f.derive().apply(vf.valueOf(3)));
        BinaryFunction g =
            Functionals.compose(Operations.times, Functionals.onFirst(Functions.id), Operations.plus);
        System.out.println("g = " + g);
        System.out.println("g(x,y) = " + g.apply(vf.symbol("x"), vf.symbol("y")));
        System.out.println("g(7,8) = " + g.apply(vf.valueOf(7), vf.valueOf(8)));
        System.out.println("dg/d(x,y) = " + g.derive());
        System.out.println("dg/d(x,y) = " + g.derive().getClass());
        System.out.println("dg/d(x,y) (x,y) = " + g.derive().apply(vf.symbol("x"), vf.symbol("y")));
        System.out.println("dg/d(x,y) (7,8) = " + g.derive().apply(vf.valueOf(7), vf.valueOf(8)));
        System.out.println("dg/d(x,y) = " + g.derive());
        System.out.println("dg/d(x,y) (7,8) = " + g.derive().apply(vf.valueOf(7), vf.valueOf(8)));

    } 
}
