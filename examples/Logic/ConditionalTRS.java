import orbital.logic.functor.Functionals;
import orbital.logic.functor.Predicate;
import orbital.logic.sign.Expression;
import orbital.logic.sign.ParseException;
import orbital.logic.trs.Substitution;
import orbital.logic.trs.Substitutions;
import orbital.moon.logic.ClassicalLogic;
import orbital.util.Utility;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Conditional Term Rewrite System (TRS).
 * It will parse two expressions and transform them according to
 * a rewrite system with
 * _X1&_X2 |- _X2  /;  when _X1 can be simplified to true
 */
public class ConditionalTRS {

    /**
     * the syntax of expressions used for parsing.
     */
    private ClassicalLogic syntax;

    public static void main(String args[]) throws Exception {
        (new ConditionalTRS()).run();
    }

    public ConditionalTRS() {
        syntax = new ClassicalLogic();
    }

    public void run() throws Exception {
        System.out.print("Type expression (A): ");
        System.out.flush();
        String s = Utility.readLine(System.in);
        Expression expression = parse(s);
        System.out.println("Original expression:\t" + s);
        System.out.println("Parsed expression:\t" + expression);

        // rewrite rules
        Substitution rewrite = Substitutions.getInstance(Arrays.asList(new Object[] {
            Substitutions.createSingleSidedMatcher(syntax.createExpression("_X1&_X2"), 
						   syntax.createExpression("_X2"), 
						   new Simplifyable(syntax.createExpression("_X1"))), 
 	    Substitutions.createSingleSidedMatcher(syntax.createExpression("_X1|false"), 
 						   syntax.createExpression("_X1")), 
 	    Substitutions.createSingleSidedMatcher(syntax.createExpression("false|_X1"), 
 						   syntax.createExpression("_X1"))
	}));

        System.out.println("applying " + rewrite);
        System.out.println("is");
        System.out.println("rewrite(A) = " + rewrite.apply(expression));
        System.out.println("rewrite^2(A) = " + rewrite.apply(rewrite.apply(expression)));
        System.out.println("rewrite^3(A) = " + rewrite.apply(rewrite.apply(rewrite.apply(expression))));

        Object obj = Functionals.fixedPoint(rewrite, expression);
        System.out.println("TRS:\t\t\tA->" + obj);
    }

    private final Expression parse(String s) throws ParseException {
        return syntax.createExpression(s);
    }

    
    /**
     * A predicate condition checking whether the counterpart of
     * "check" can be simplified to true.
     */
    private class Simplifyable implements Predicate {

        private Expression check;

        public Simplifyable(Expression expression) {
            check = expression;
        }

        public boolean apply(Object obj) {
	    try {
		// the partial single sided matcher
		Substitution mu = (Substitution)obj;
		// the counterpart of "check" in the actual occurrence of the pattern
		Expression occurrence = (Expression)mu.apply(check);
		return occurrence.equals(syntax.createExpression("true")) 
		    || occurrence.equals(syntax.createExpression("~a|a")) 
		    || occurrence.equals(syntax.createExpression("~b|b"));
	    } catch (ParseException parseexception) {
		throw (AssertionError)(new AssertionError("internal resource damage, syntax expected correct")).initCause(parseexception);
	    }
        }

        public String toString() {
            return ("when " + check + " simplifies to true");
        }

    }
}
