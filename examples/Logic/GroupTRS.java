import orbital.logic.functor.*;
import orbital.moon.logic.MathExpressionSyntax;
import orbital.logic.trs.*;
import orbital.logic.imp.ParseException;
import orbital.io.IOUtilities;
import java.util.*;

/**
 * Group Term Rewrite System (TRS).
 * It will parse two mathematical expressions for groups, and check whether
 * they have equal results.
 * Good expressions to type are
 *		
 *
 * @fixme x*(y*x)^-1 produces strange errors because _X8 does not match y^-1.
 */
public class GroupTRS {
    public static void main(String arg[]) throws Exception {
	System.out.print("Type first expression (A): ");
	System.out.flush();
	String expr = IOUtilities.readLine(System.in);
	Object A = parse(expr);
	System.out.println("Original expression:\t" + expr);
	System.out.println("Parsed function:\t" + A);
	System.out.print("Type second expression (B): ");
	System.out.flush();
	expr = IOUtilities.readLine(System.in);
	Object B = parse(expr);
	System.out.println("Original expression:\t" + expr);
	System.out.println("Parsed function:\t" + B);
		
	// apply a nice substitution
	Substitution sigma = Substitutions.getInstance(Arrays.asList(new Object[] {
	    // axiom left identity
	    Substitutions.createSingleSidedMatcher(parse("1*_X"), parse("_X")),
	    // axiom left inverse
	    Substitutions.createSingleSidedMatcher(parse("_X^-1*_X"), parse("1")),
	    // axiom associative
	    Substitutions.createSingleSidedMatcher(parse("(_X1*_X2)*_X3"), parse("_X1*(_X2*_X3)")),
	    //
	    Substitutions.createSingleSidedMatcher(parse("_X1^-1*(_X1*_X2)"), parse("_X2")),
	    // identity is idem potent
	    Substitutions.createSingleSidedMatcher(parse("1^-1"), parse("1")),
	    // right identity
	    Substitutions.createSingleSidedMatcher(parse("_X*1"), parse("_X")),
	    // inversion is involutive
	    Substitutions.createSingleSidedMatcher(parse("(_X^-1)^-1"), parse("_X")),
	    // right inverse
	    Substitutions.createSingleSidedMatcher(parse("_X*_X^-1"), parse("1")),
	    //
	    Substitutions.createSingleSidedMatcher(parse("_X1*((_X1^-1)*_X8)"), parse("_X8")),
	    // inverse of a product
	    Substitutions.createSingleSidedMatcher(parse("(_X1*_X2)^-1"), parse("_X2^-1*_X1^-1")),
	}));
		
	System.out.println("applying " + sigma);
	System.out.println("sigma(A) = " + sigma.apply(A));
	System.out.println("sigma^2(A) = " + sigma.apply(sigma.apply(A)));
	System.out.println("sigma^3(A) = " + sigma.apply(sigma.apply(sigma.apply(A))));
	Object A_ = Functionals.fixedPoint(sigma, A);
	Object B_ = Functionals.fixedPoint(sigma, B);
	System.out.println("TRS:\t\t\tA->" + A_);
	System.out.println("TRS:\t\t\tB->" + B_);
	System.out.println(A.equals(B) ? "A=B" : "A!=B");
    } 
	
    private static final Object parse(String expression) throws ParseException {
	return new MathExpressionSyntax().createMathExpression(expression);
    }
}
