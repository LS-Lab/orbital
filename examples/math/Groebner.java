import orbital.math.*;
import java.util.*;

/**
 * Some operations with multivariate polynomials,
 * including construction of a Groebner basis.
 *
 * @author Andr&eacute; Platzer
 * @version 0.8, 2002-08-21
 */
public class Groebner extends MathTest {
    private static final Comparator order = AlgebraicAlgorithms.LEXICOGRAPHIC;
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
	    Values.polynomial(new int[][] {
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
	    Values.polynomial(new int[][] {
		{0, 0, 4},
		{3, 0, 2}
	    }),
	    Values.polynomial(new int[][] {
		{-2, -2, 1}
	    })
	};
	Set/*_<Polynomial<Rational>>_*/ G = new HashSet(Arrays.asList(Garray));
	
	printArithmetic(Garray[0], Garray[1], false);

	Polynomial/*<Rational>*/ f = Values.polynomial(new int[][] {
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
	    Values.MONOMIAL(new int[] {0,2}).subtract(Values.MONOMIAL(new int[] {3,0}))
	    .subtract(Values.MONOMIAL(new int[] {2,0}))
	    });
	// the Groebner basis of m
	final Set gm = AlgebraicAlgorithms.groebnerBasis(new HashSet(m), order);
	Quotient/*<Polynomial<Real>>*/ f =
	    Values.quotient(Values.polynomial(new double[][] {
		{2,1},
		{3,0}
	    }), gm, order);
	Quotient/*<Polynomial<Real>>*/ g =
	    Values.quotient(Values.polynomial(new double[][] {
		{-1,1},
		{1,1}
	    }), gm, order);

	// perform calculations in both fields
	System.out.println("perform calculations in a quotient ring modulo " + m);
	printArithmetic(f, g, false);

	f = Values.quotient(Values.polynomial(new double[][] {
		{2,-1},
		{3,0},
		{-1,0}
	    }), gm, order);
	printArithmetic(f, g, false);
    }

    private static void quadraticAlgebra() {
	final Arithmetic alpha = Values.valueOf(4);
	final Arithmetic beta = Values.valueOf(2);
	System.out.println("calculate in a quadratic algebra of type (" + alpha + "," + beta + ")");
	final Collection m = Arrays.asList(new Polynomial[] {
	    Values.polynomial(new int[][] {
		{0},
		{-1},
		{1}
	    }),
	    Values.polynomial(new int[][] {
		{0,-1},
		{0,1}
	    }),
	    Values.polynomial(new Arithmetic[][] {
		{Values.ZERO,beta,Values.valueOf(-1)},
		{alpha,Values.ZERO,Values.ZERO}
	    })
	});
	// the Groebner basis of m
	final Set gm = AlgebraicAlgorithms.groebnerBasis(new HashSet(m), order);

	Quotient/*<Polynomial<Real>>*/ f =
	    Values.quotient(Values.polynomial(new double[][] {
		{2,1},
		{3,0}
	    }), gm, order);
	Quotient/*<Polynomial<Real>>*/ g =
	    Values.quotient(Values.polynomial(new double[][] {
		{-1,1},
		{1,1}
	    }), gm, order);

	// perform calculations in both fields
	System.out.println("perform calculations in a quotient ring modulo " + m);
	printArithmetic(f, g, false);

	f = Values.quotient(Values.polynomial(new double[][] {
		{2,-1},
		{3,0},
		{-1,0}
	    }), gm, order);
	printArithmetic(f, g, false);
    }
}// Groebner
