import orbital.math.*;

/**
 * Some operations with multivariate polynomials,
 * including construction of a Groebner basis.
 *
 * @author Andr&eacute; Platzer
 * @version 0.8, 2002-08-21
 */
public class Groebner {
    public static void main(String arg[]) {
	test_simple();
	System.out.println();
	test_more();
    }
    private static void test_simple() {
	// some polynomials in <b>Q</b>[X,Y]
	Multinomial/*<Rational>*/ g =
	    Values.multinomial(new int[][] {
		{0, 1},
		{1, 0}
	    });
	Multinomial/*<Rational>*/ G[] = {
	    g
	};

	Multinomial/*<Rational>*/ f = g.multiply(g);

	printArithmetic(g, f);

	System.out.println("reducing (" + f + ") with respect to " + MathUtilities.format(G));
	System.out.println("reduce (" + f + ") with respect to " + MathUtilities.format(G) + ", = "
			   + AlgebraicAlgorithms.reduce(f, G, AlgebraicAlgorithms.LEXICOGRAPHIC));
	System.out.println("Groebner basis of " + MathUtilities.format(G) + ", = "
			   + MathUtilities.format(AlgebraicAlgorithms.groebnerBasis(G, AlgebraicAlgorithms.LEXICOGRAPHIC)));

    }

    private static void test_more() {
	// some polynomials in <b>Q</b>[X,Y]
	Multinomial/*<Rational>*/ G[] = {
	    Values.multinomial(new int[][] {
		{0, 0, 4},
		{3, 0, 2}
	    }),
	    Values.multinomial(new int[][] {
		{-2, -2, 1}
	    })
	};
	printArithmetic(G[0], G[1]);

	Multinomial/*<Rational>*/ f = Values.multinomial(new int[][] {
	    {0, 0, 2, 0},
	    {0, 0, 0, 0},
	    {0, 0, 0, 0},
	    {0, 0, 0, 1}
	});
	System.out.println("reducing (" + f + ") with respect to " + MathUtilities.format(G));
	System.out.println("reduce (" + f + ") with respect to " + MathUtilities.format(G) + ", = "
			   + AlgebraicAlgorithms.reduce(f, G, AlgebraicAlgorithms.LEXICOGRAPHIC));
	System.out.println("Groebner basis of " + MathUtilities.format(G) + ", = "
			   + MathUtilities.format(AlgebraicAlgorithms.groebnerBasis(G, AlgebraicAlgorithms.LEXICOGRAPHIC)));
    }

    private static void printArithmetic(Multinomial f, Multinomial g) {
	Rational a = Values.valueOf(4);
	System.out.println("(" + f.zero() + ") + (" + f + ") = " + f.zero().add(f));
	assert f.zero().add(f).equals(f) : "0+x=x";
	assert f.add(f.zero()).equals(f) : "x+0=x";
	System.out.println("(" + f + ") + (" + g + ") = " + f.add(g));
	System.out.println("-(" + f + ") = " + f.minus());
	System.out.println("(" + f + ") - (" + g + ") = " + f.subtract(g));
	System.out.println(a + "*(" + f + ") = " + f.scale(a));
	System.out.println("(" + f.one() + ") * (" + f + ") = " + f.one().multiply(f));
	assert f.one().multiply(f).equals(f) : "1*x=x";
	assert f.multiply(f.one()).equals(f) : "x*1=x";
	System.out.println("(" + f + ") * (" + g + ") = " + f.multiply(g));
    }
}// Groebner
