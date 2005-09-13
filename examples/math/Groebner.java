import orbital.math.*;
import java.util.*;

/**
 * Some operations with multivariate polynomials,
 * including construction of a Gr&ouml;bner basis.
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 */
public class Groebner extends MathTest {
    private static final Comparator order = AlgebraicAlgorithms.DEGREE_REVERSE_LEXICOGRAPHIC;
    // get us a value factory for creating arithmetic objects
    private static final Values vf = Values.getDefaultInstance();
    public static void main(String arg[]) {
        test_simple();
        System.out.println();
        test_more();
        System.out.println();
        quotientCalculation();
        System.out.println();
        quadraticAlgebra();
    }
    private static void test_simple() {
        // some polynomials in <b>Q</b>[X,Y]
        Polynomial/*<Rational>*/ g =
            vf.polynomial(new int[][] {
                {0, 1},
                {1, 0}
            });
        Set/*_<Polynomial<Rational>>_*/ G = new HashSet();
        G.add(g);

        Polynomial/*<Rational>*/ f = g.multiply(g);

        printArithmetic(g, f, false);

        System.out.println("reducing (" + f + ") with respect to " + G);
        System.out.println("reduce (" + f + ") with respect to " + G + ", = "
                           + AlgebraicAlgorithms.reduce(f, G, order));
        System.out.println("Groebner basis of " + G + ", = "
                           + AlgebraicAlgorithms.groebnerBasis(G, order));

        G.add(f);
        System.out.println("Groebner basis of " + G + ", = "
                           + AlgebraicAlgorithms.groebnerBasis(G, order));
    }

    private static void test_more() {
        // some polynomials in <b>Q</b>[X,Y]
        Polynomial/*<Rational>*/ Garray[] = {
            vf.polynomial(new int[][] {
                {0, 0, 4},
                {3, 0, 2}
            }),
            vf.polynomial(new int[][] {
                {-2, -2, 1}
            })
        };
        Set/*_<Polynomial<Rational>>_*/ G = new HashSet(Arrays.asList(Garray));
        
        printArithmetic(Garray[0], Garray[1], false);

        Polynomial/*<Rational>*/ f = vf.polynomial(new int[][] {
            {0, 0, 2, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 1}
        });
        System.out.println("reducing (" + f + ") with respect to " + G);
        System.out.println("reduce (" + f + ") with respect to " + G + ", = "
                           + AlgebraicAlgorithms.reduce(f, G, order));
        System.out.println("Groebner basis of " + G + ", = "
                           + AlgebraicAlgorithms.groebnerBasis(G, order));
    }

    private static void quotientCalculation() {
        System.out.println("calculate with quotients of polynomials");
        // create elements in <b>R</b>[X]/(Y^2-X^3-X^2)
        final Collection m = Arrays.asList(new Polynomial[] {
            // alternative form of construction: explicit concatenation
            // of monomials. This is more to type, but also more
            // simple to construct
            vf.MONOMIAL(new int[] {0,2}).subtract(vf.MONOMIAL(new int[] {3,0}))
            .subtract(vf.MONOMIAL(new int[] {2,0}))
            });
        // the Groebner basis of m
        final Set gm = AlgebraicAlgorithms.groebnerBasis(new HashSet(m), order);
        Quotient/*<Polynomial<Real>>*/ f =
            vf.quotient(vf.polynomial(new double[][] {
                {2,1},
                {3,0}
            }), gm, order);
        Quotient/*<Polynomial<Real>>*/ g =
            vf.quotient(vf.polynomial(new double[][] {
                {-1,1},
                {1,1}
            }), gm, order);

        // perform calculations in both fields
        System.out.println("perform calculations in a quotient ring modulo " + m);
        printArithmetic(f, g, false);

        f = vf.quotient(vf.polynomial(new double[][] {
                {2,-1},
                {3,0},
                {-1,0}
            }), gm, order);
        printArithmetic(f, g, false);
    }

    private static void quadraticAlgebra() {
        final Arithmetic alpha = vf.valueOf(4);
        final Arithmetic beta = vf.valueOf(2);
        System.out.println("calculate in a quadratic algebra of type (" + alpha + "," + beta + ")");
        final Collection m = Arrays.asList(new Polynomial[] {
            vf.polynomial(new int[][] {
                {0},
                {-1},
                {1}
            }),
            vf.polynomial(new int[][] {
                {0,-1},
                {0,1}
            }),
            vf.polynomial(new Arithmetic[][] {
                {vf.ZERO,beta,vf.valueOf(-1)},
                {alpha,vf.ZERO,vf.ZERO}
            })
        });
        // the Groebner basis of m
        final Set gm = AlgebraicAlgorithms.groebnerBasis(new HashSet(m), order);

        Quotient/*<Polynomial<Real>>*/ f =
            vf.quotient(vf.polynomial(new double[][] {
                {2,1},
                {3,0}
            }), gm, order);
        Quotient/*<Polynomial<Real>>*/ g =
            vf.quotient(vf.polynomial(new double[][] {
                {-1,1},
                {1,1}
            }), gm, order);

        // perform calculations in both fields
        System.out.println("perform calculations in a quotient ring modulo " + m);
        printArithmetic(f, g, false);

        f = vf.quotient(vf.polynomial(new double[][] {
                {2,-1},
                {3,0},
                {-1,0}
            }), gm, order);
        printArithmetic(f, g, false);
    }
}// Groebner
