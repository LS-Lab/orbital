import orbital.math.*;
import orbital.math.functional.*;

public class FunctionalDemo {
    public static void main(String arg[]) throws Exception {
        Function g = Functions.square;
        Function h = (Function) Operations.times.apply(Functions.id, Functions.id);
        print(g);
        print(h);
        print(g.derive());
        print(h.derive());
        print(h.derive().derive());
    } 

    private static void print(Function f) {
        // get us a value factory for creating arithmetic objects
        final Values vf = Values.getDefaultInstance();
        final Arithmetic X = vf.symbol("x");
        System.out.println(f.apply(X));
        System.out.println("has some values");
        for (int i = 0; i < 4; i++) {
            Scalar x = vf.valueOf(i);
            System.out.println("(" + x + "|" + f.apply(x) + ")\t");
        }
    }
}
