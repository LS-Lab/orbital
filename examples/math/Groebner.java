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
    public static void main(String arg[]) {
	test_simple();
	System.out.println();
	test_more();
	System.out.println();
	quotientCalculation();
    }
    private static void test_simple() {
	// some polynomials in <b>Q</b>[X,Y]
	Multinomial/*<Rational>*/ g =
	    Values.multinomial(new int[][] {
		{0, 1},
		{1, 0}
	    });
	Set/*_<Multinomial<Rational>>_*/ G = new HashSet();
	G.add(g);

	Multinomial/*<Rational>*/ f = g.multiply(g);

	printArithmetic(g, f, false);

	System.out.println("reducing (" + f + ") with respect to " + G);
	System.out.println("reduce (" + f + ") with respect to " + G + ", = "
			   + AlgebraicAlgorithms.reduce(f, G, AlgebraicAlgorithms.LEXICOGRAPHIC));
	System.out.println("Groebner basis of " + G + ", = "
			   + AlgebraicAlgorithms.groebnerBasis(G, AlgebraicAlgorithms.LEXICOGRAPHIC));

	G.add(f);
	System.out.println("Groebner basis of " + G + ", = "
			   + AlgebraicAlgorithms.groebnerBasis(G, AlgebraicAlgorithms.LEXICOGRAPHIC));
    }

    private static void test_more() {
	// some polynomials in <b>Q</b>[X,Y]
	Multinomial/*<Rational>*/ Garray[] = {
	    Values.multinomial(new int[][] {
		{0, 0, 4},
		{3, 0, 2}
	    }),
	    Values.multinomial(new int[][] {
		{-2, -2, 1}
	    })
	};
	Set/*_<Multinomial<Rational>>_*/ G = new HashSet(Arrays.asList(Garray));
	
	printArithmetic(Garray[0], Garray[1], false);

	Multinomial/*<Rational>*/ f = Values.multinomial(new int[][] {
	    {0, 0, 2, 0},
	    {0, 0, 0, 0},
	    {0, 0, 0, 0},
	    {0, 0, 0, 1}
	});
	System.out.println("reducing (" + f + ") with respect to " + G);
	System.out.println("reduce (" + f + ") with respect to " + G + ", = "
			   + AlgebraicAlgorithms.reduce(f, G, AlgebraicAlgorithms.LEXICOGRAPHIC));
	System.out.println("Groebner basis of " + G + ", = "
			   + AlgebraicAlgorithms.groebnerBasis(G, AlgebraicAlgorithms.LEXICOGRAPHIC));
    }

    private static final Comparator order = AlgebraicAlgorithms.LEXICOGRAPHIC;
    private static void quotientCalculation() {
	System.out.println("calculate with quotients of polynomials");
	// create elements in <b>R</b>[X]/(Y^2-X^3-X^2)
	final Collection m = Arrays.asList(new Multinomial[] {
	    // alternative form of construction: explicit concatenation
	    // of monomials. This is more to type, but also more
	    // simple to construct
	    Values.MONOMIAL(new int[] {0,2}).subtract(Values.MONOMIAL(new int[] {3,0}))
	    .subtract(Values.MONOMIAL(new int[] {2,0}))
	    });
	// the Groebner basis of m
	final Set gm = AlgebraicAlgorithms.groebnerBasis(new HashSet(m), order);
	Quotient/*<Multinomial<Real>>*/ f =
	    Values.quotient(Values.multinomial(new double[][] {
		{2,1},
		{3,0}
	    }), gm, order);
	Quotient/*<Multinomial<Real>>*/ g =
	    Values.quotient(Values.multinomial(new double[][] {
		{-1,1},
		{1,1}
	    }), gm, order);

	// perform calculations in both fields
	System.out.println("perform calculations in a quotient ring modulo " + m);
	printArithmetic(f, g, false);

	f = Values.quotient(Values.multinomial(new double[][] {
		{2,-1},
		{3,0},
		{-1,0}
	    }), gm, order);
	printArithmetic(f, g, false);
    }
}// Groebner
