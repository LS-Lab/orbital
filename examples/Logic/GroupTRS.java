import orbital.logic.functor.*;
import orbital.moon.logic.MathExpressionSyntax;
import orbital.moon.logic.LogicParser;
import orbital.logic.trs.*;
import orbital.logic.sign.Expression;
import orbital.logic.sign.ParseException;
import orbital.io.IOUtilities;
import java.util.*;
import java.io.*;

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
	new GroupTRS().run();
    }
    /**
     * the syntax of mathematical expressions used for parsing.
     */
    private MathExpressionSyntax syntax;
    public GroupTRS() {
	this.syntax = new MathExpressionSyntax();
    }
    public void run() throws Exception {
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
		
	// load a nice substitution to apply, from a file
	Substitution sigma = LogicParser.readTRS(
	    new InputStreamReader(getClass().getResourceAsStream("group.trs")),
	    syntax,
	    unwrapArithmeticObjects
	    );

	// apply the substitution
	System.out.println("applying " + sigma);
	System.out.println("is");
	System.out.println("sigma(A) = " + sigma.apply(A));
	System.out.println("sigma^2(A) = " + sigma.apply(sigma.apply(A)));
	System.out.println("sigma^3(A) = " + sigma.apply(sigma.apply(sigma.apply(A))));
	Object A_ = Functionals.fixedPoint(sigma, A);
	System.out.println("TRS:\t\t\tA->" + A_);
	Object B_ = Functionals.fixedPoint(sigma, B);
	System.out.println("TRS:\t\t\tB->" + B_);
	System.out.println(A.equals(B) ? "A=B" : "A!=B");
    } 
	
    private final Object parse(String expression) throws ParseException {
	return syntax.createMathExpression(expression);
    }

    /**
     * Transformation that unwraps the represented arithmetic object
     * of a parsed expression.
     */
    private final Function unwrapArithmeticObjects = new Function() {
	    public Object apply(Object x) {
		return syntax.getValueOf((Expression)x);
	    }
	};
}
