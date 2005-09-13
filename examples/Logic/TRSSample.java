import orbital.logic.functor.*;
import orbital.moon.logic.MathExpressionSyntax;
import orbital.logic.trs.*;
import orbital.io.IOUtilities;
import orbital.math.functional.Functionals;
import orbital.math.functional.Functions;
import orbital.math.*;
import java.util.*;

/**
 * Term Rewrite System (TRS) example.
 * It will parse a mathematical expression, evaluate it, perform a substitution,
 * try to unify it with another expression.
 * Good expressions to type are<pre>
 *      17* 3+x/7+2*sin[2 * 7 + 8]*x + 2 * x/(8 +x)
 *		17* 3+y/7+2*sin[2 * 7 + 8] + 2 * z/(8 +z)
 *		21 * 5 / 7 + (-1)*cos[2 * 7 + 8] + (2 * 5) * z / (-7)
 * </pre>21 * 5 / 7 + (-1)*cos[2 * 7 + 8] + (2 * 5) * z / (-7)
 * @see orbital.logic.trs.Substitutions
 */
public class TRSSample {
    public static void main(String arg[]) throws Exception {
	System.out.print("Type expression: ");
	System.out.flush();
	String expr = IOUtilities.readLine(System.in);
	Object p = new MathExpressionSyntax().createMathExpression(expr);
	System.out.println("Original expression:\t" + expr);
	System.out.println("Parsed function:\t" + p);
	try {
	    System.out.println("Evaluates to:\t\t" + (p instanceof Function ? ((Function) p).apply(null) : p));	//XXX: erm why null?
	} catch (Exception ignore) {}
		
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
	// apply a nice substitution
	Substitution sigma = Substitutions.getInstance(Arrays.asList(new Object[] {
	    Substitutions.createExactMatcher(Functions.constant(vf.symbol("x")), Functions.id),
	    Substitutions.createExactMatcher(Functions.sin, Functions.cos),
	    Substitutions.createExactMatcher(Functions.constant(vf.valueOf(7)), Functions.square),
	    Substitutions.createExactMatcher(Functions.constant(vf.symbol("z")), Functions.constant(vf.symbol("tau")))
	}));
	// or apply a substitution that performs unification as well
	/*sigma = Substitutions.getInstance(Arrays.asList(new Object[] {
	  Substitutions.createSingleSidedMatcher(orbital.math.functional.Operations.times.apply(vf.symbol("x"), vf.valueOf(8)), Functions.constant(vf.symbol("x")))
	  }));*/
		
	//logger.log(Level.DEBUG, "term construction is " + p.getClass() + "@ " + functionTree(p));

	System.out.println("applying\t" + sigma);
	Object t = sigma.apply(p);
	System.out.println("Leads to the replacement:\t\t" + t);
	System.out.println("which");
	try {
	    Arithmetic v = vf.valueOf(3);
	    System.out.println("at " + v + " Evaluates to:\t" + (t instanceof Function ? ((Function) t).apply(v) : t));	//XXX: erm why null?
	    v = vf.valueOf(5);
	    System.out.println("at " + v + " Evaluates to:\t" + (t instanceof Function ? ((Function) t).apply(v) : t));	//XXX: erm why null?
	} catch (Exception ignore) {}
		

	System.out.println("TRS:\t\t\t" + Functionals.fixedPoint(sigma, p));

	// try to unify the parsed expression with b
	Function b = Functionals.compose(Functions.sin, Functions.constant(vf.symbol("x")));
	Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {p, b}));
	if (mu == null)
	    System.out.println("not unifiable with " + b);
	else {
	    System.out.println("unifiable with " + b + " per " + mu);
	    Object mup = mu.apply(p);
	    Object mub = mu.apply(b);
	    System.out.println(mup + " ===\r\n" + mub + " which are " + (mup.equals(mub) ? "equal" : "UNEQUAL"));
	}
    } 


    // for full form printing
	
    /**
     * Creates a function tree view of a functor by decomposing it into its components.
     * @see Functor.Composite
     */
    /*private static orbital.util.graph.Node functionTree(Object f) {
      if (!(f instanceof Functor.Composite))
      return new orbital.util.graph.ListTree.TreeNode(f, f.getClass().getName());
      Functor.Composite c = (Functor.Composite) f;
      Functor			  compositor = c.getCompositor();
      Collection		  components = orbital.util.Utility.asCollection(c.getComponent());
      orbital.util.graph.Node	n = new orbital.util.graph.ListTree.TreeNode(compositor, compositor.getClass().getName());
      // n.addAll(Functionals.map(functionTree, components));
      for (Iterator i = components.iterator(); i.hasNext(); )
      n.add(functionTree(i.next()));
      return n;
      } */

}
