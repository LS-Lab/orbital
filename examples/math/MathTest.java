import orbital.math.*;

/**
 * Base class for testing some arithmetics.
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 */
public abstract class MathTest {
    public static final void printArithmetic(Arithmetic x, Arithmetic y, boolean withDivisions) {
        Rational a = Values.getDefaultInstance().valueOf(4);
        Arithmetic zero = x.zero();
        Arithmetic one = x.one();
        System.out.println("(" + zero + ") + (" + x + ") = " + zero.add(x));
        assert zero.add(x).equals(x) : "0+x=x \t(" + zero + ") + (" + x + ") = " + zero.add(x) + " = " + x;
        assert x.add(zero).equals(x) : "x+0=x \t(" + x + ") + (" + zero + ") = " + x;

        assert zero.add(zero).equals(zero) : "0+0=0 \t(" + zero + ")+(" + zero + ") = " + zero;

        System.out.println("(" + x + ") + (" + y + ") = " + x.add(y));

        System.out.println("-(" + x + ") = " + x.minus());
        assert x.minus().add(x).equals(zero) : "(-x)+x=0 \t-(" + x + ") + (" + x + ") = " + zero;
        assert x.add(x.minus()).equals(zero) : "x+(-x)=0 \t(" + x + ") + (-(" + x + ")) = " + zero;

        assert zero.minus().equals(zero) : "-0=0 \t-(" + zero + ") = " + zero;
        assert zero.subtract(zero).equals(zero) : "0-0=0 \t" + zero + "-(" + zero + ") = " + zero;

        System.out.println("(" + x + ") - (" + y + ") = " + x.subtract(y));
        assert x.subtract(x).equals(zero) : "x-x=0 \t(" + x + ") - (" + x + ") = " + zero;

        System.out.println(a + "*(" + x + ") = " + x.scale(a));
        System.out.println("(" + one + ") * (" + x + ") = " + one.multiply(x));
        assert one.multiply(x).equals(x) : "1*x=x \t" + one + " * (" + x + ") = " + x;
        assert x.multiply(one).equals(x) : "x*1=x \t(" + x + ") * " + one + " = " + x;

        assert zero.multiply(zero).equals(zero) : "0*0=0 \t(" + zero + ") * (" + zero + ") = " + zero;
        assert zero.multiply(x).equals(zero) : "0*x=0 \t(" + zero + ") * (" + x + ") = " + zero;
        assert x.multiply(zero).equals(zero) : "x*0=0 \t(" + x + ") * (" + zero + ") = " + zero;
        assert zero.multiply(one).equals(zero) : "0*1=0 \t(" + zero + ") * (" + one + ") = " + zero;
        assert one.multiply(zero).equals(zero) : "1*0=0 \t(" + one + ") * (" + zero + ") = " + zero;
        
        System.out.println("(" + x + ") * (" + y + ") = " + x.multiply(y));

        //@todo commutative +?

        if (withDivisions) {
            System.out.println("(" + x + ")^-1 = " + x.inverse());
            assert x.inverse().multiply(x).equals(one) : "x^-1 * x=1 \t(" + x.inverse() + ") * (" + x + ") = " + one;
            assert x.multiply(x.inverse()).equals(one) : "x*x^-1=1 \t(" + x + ") * (" + x.inverse() + ") = " + one;

            System.out.println("(" + one + ") / (" + x + ") = " + one.divide(x));
            assert one.divide(x).equals(x.inverse()) : "1/x=x^-1 \t" + one + " / (" + x + ") = " + x.inverse();
            assert one.inverse().equals(one) : "1^-1=1 \t(" + one + ")^-1 = " + one;
            assert one.divide(one).equals(one) : "1/1=1 \t" + one + " / (" + one + ") = " + one;

            System.out.println("(" + x + ") / (" + one + ") = " + x.divide(one));
            assert x.divide(one).equals(x) : "x/1=x \t(" + x + ") / 1 = " + x;

            assert zero.divide(one).equals(zero) : "0/1=0 \t(" + zero + ") / (" + one + ") = " + zero;
            
            System.out.println("(" + x + ") / (" + y + ") = " + x.divide(y));

            //@todo sometimes commutative *?
        }
    }
}

