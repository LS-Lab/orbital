/**
 * @(#)ModernLogic.java 0.7 1999/01/16 Andre Platzer
 * 
 * Copyright (c) 1999-2002 Andre Platzer. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.logic.sign.type.*;
import orbital.logic.sign.ParseException;
import orbital.logic.sign.concrete.Notation;

import orbital.logic.functor.Functor;
import java.util.Iterator;
import java.util.NoSuchElementException;

import orbital.logic.trs.Variable;

import orbital.logic.functor.*;

import java.io.StringReader;

import orbital.math.MathUtilities;
import orbital.math.Arithmetic;
import orbital.math.Values;
import java.util.Arrays;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.*;
import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A support class for implementing modern logic.
 * <p>
 * Deriving a concrete logic from this class may save some implementation effort.
 * This class uses {@link ModernFormula} and may pass its instance <code class="keyword">this</code>
 * as an argument to the constructor methods like {@link ModernFormula#createSymbol(Logic,Symbol)}.
 * </p>
 * @version 0.8, 1999/01/16
 * @author  Andr&eacute; Platzer
 * @see ModernFormula
 */
abstract class ModernLogic implements Logic {
    private static final Logger logger = Logger.getLogger(ModernLogic.class.getName());
    /**
     * Whether runtime type checks are enabled.
     */
    private static /*final*/ boolean TYPE_CHECK = true;
    /**
     * Enable or disable runtime type checks.
     */
    /*private*/ static void setEnableTypeChecks(boolean enable) {
	System.out.println((enable ? "enable" : "disable") + " type checks");
	TYPE_CHECK = enable;
    }
    /**
     * A complex error offset that is not representable by a locator for ParseException.
     */
    static final int COMPLEX_ERROR_OFFSET = -1;

    /**
     * Prove all conjectures read from a reader.
     * The conjectures have the following forms
     * <pre>
     * &lt;premise&gt; (, &lt;premise&gt;)<sup>*</sup> |= &lt;conclusion&gt;    # &lt;comment&gt; &lt;EOL&gt;
     * &lt;formula&gt; == &lt;formula&gt;    # &lt;comment&gt; &lt;EOL&gt;
     * ...
     * </pre>
     * @param rd the source for the conjectures to prove.
     * @param logic the logic to use.
     * @param all_true If <code>true</code> this method will return whether all conjectures in rd
     *  could be proven.
     *  If <code>false</code> this method will return whether some conjectures in rd
     *  could be proven.
     * @return a value depending upon all_true.
     * @see LogicParser#readTRS(Reader,ExpressionSyntax,Function)
     * @todo provide a hook method for subclasses (they can thus provide normalForm and closure)
     */
    protected static final boolean proveAll(Reader rd, ModernLogic logic, boolean all_true) throws ParseException, IOException {
	return proveAll(rd, logic, all_true, false, false, false);
    }
    static final boolean proveAll(Reader rd, ModernLogic logic, boolean all_true, boolean normalForm, boolean closure, boolean verbose) throws ParseException, IOException {
	DateFormat df = new SimpleDateFormat("H:mm:ss:S");
	df.setTimeZone(TimeZone.getTimeZone("Greenwich/Meantime"));
	final long start = System.currentTimeMillis();
	// sums proof duration excluding I/O
	long    duration = 0;

	boolean some = false;
	boolean all = true;

	boolean eof = false;
	do {
	    String formula = "";
	    String comment = null;					// not in comment mode

	    boolean wasWhitespace = false;
	    while (!eof) {
		int ch = rd.read();
		if (ch == -1) {
		    eof = true;
		    break;
		} else if (ch == '\r')
		    continue;
		else if (ch == '\n')
		    break;
		else if ((ch == ' ' || ch == '\t')) {
		    //@todo why should we want to skip multiple whitespaces, except for trailing comments?
		    if (comment != null || !wasWhitespace) {
			wasWhitespace = true;
			// add to comment or formula, depending upon whether in comment mode or not
			if (comment == null)
			    formula += (char) ch;
			else
			    comment += (char) ch;
		    } else
			// skip multiple nonbreaking white-spaces unless in comment mode
			continue;
		} else if (ch == '#')
		    // enter comment mode
		    comment = "";
		else {
		    wasWhitespace = false;
		    // add to comment or formula, depending upon whether in comment mode or not
		    if (comment == null)
			formula += (char) ch;
		    else
			comment += (char) ch;
		}
	    }
			
	    if ("".equals(formula)) {
		if (comment != null)
		    System.out.println('#' + comment);
		// skip proving comment-only lines since it's pointless
		continue;
	    }

	    final long proofStart = System.currentTimeMillis();

	    // split formula into knowledge and formula
	    String knowledge = "";
	    // some term substitutions (currently substitutes only once)
	    final int eq = formula.indexOf("==");
	    int e = eq;
	    if (e >= 0)
		formula = "(" + formula.substring(0, e) + ")" + " <=> " + "(" + formula.substring(e + 2) + ")";
	    e = Math.max(formula.indexOf("|="), formula.indexOf("|-"));
	    if (e >= 0) {
		knowledge = formula.substring(0, e);
		formula = formula.substring(e + 2);
	    }
	    if (eq >= 0)
		//@xxx better use a Parser for this, f.ex. by turning a==b into a|=b and b|=a otherwise we would do garbage for "p->q,r->s==x" or even "p(x,y) |= p(a,b)"
		formula = formula.replace(',', '&');


	    // infer
	    final boolean sat = logic.infer(knowledge, formula, verbose);
	    System.out.println(knowledge + (sat ? "\t|= " : "\tNOT|= ") + formula);

	    // verify equivalence of its NF
	    if (normalForm) {
		Formula f = (Formula) logic.createExpression(formula);
		String formName[] = {
		    "disjunctive",
		    "conjunctive"
		};
		Formula form[] = {
		    ClassicalLogic.Utilities.disjunctiveForm(f, true),
		    ClassicalLogic.Utilities.conjunctiveForm(f, true)
		};
		for (int i = 0; i < form.length; i++) {
		    if (verbose)
			System.out.println(formName[i] + " normal form: " + form[i]);
		    if (!logic.inference().infer(new Formula[] {f}, form[i]))
			throw new InternalError("wrong " + formName[i] + "NF " + form[i] + " =| for " + formula);
		    if (!logic.inference().infer(new Formula[] {form[i]}, f))
			throw new InternalError("wrong " + formName[i] + "NF " + form[i] + " |= for " + formula);
		}
	    }

	    // 
	    if (closure) {
		Formula f = (Formula) logic.createExpression(formula);
		String formName[] = {
		    "universal",
		    "existential",
		    "constant"
		};
		Formula form[] = {
		    ClassicalLogic.Utilities.universalClosure(f),
		    ClassicalLogic.Utilities.existentialClosure(f),
		    ClassicalLogic.Utilities.constantClosure(f)
		};
		System.out.println("FV(" + f + ") = " + f.getFreeVariables());
		System.out.println("BV(" + f + ") = " + f.getBoundVariables());
		System.out.println(" V(" + f + ") = " + f.getVariables());
		System.out.println("Sigma(" + f + ") = " + f.getSignature());
		for (int i = 0; i < form.length; i++) {
		    System.out.println(formName[i] + " closure: " + form[i]);
		}
	    }

	    duration += System.currentTimeMillis() - proofStart;

	    // keep records
	    some |= sat;
	    all &= sat;
	} while (!eof);

	final Date eta = new Date(System.currentTimeMillis() - start);
	logger.log(Level.INFO, "timing is: Proof duration {0}\ntiming is: total time including I/O {1}",
		   new Object[] {df.format(new Date(duration)), df.format(eta)});
	return all_true ? all : some;
    } 

    public String toString() {
	return getClass().getName() + '[' + ']';
    }

    /**
     * Checks whether the logics are compatible.
     * An undefined logic of <code>null</code> is compatible with any logic.
     */
    boolean compatible(Logic l) {
	return l == null
	    || getClass() == l.getClass()
	    || getClass().isAssignableFrom(l.getClass())
	    || l.getClass().isAssignableFrom(getClass());
    }

    // heavy implementation

    public Expression createAtomic(Symbol symbol) {
	Expression RES = createAtomicImpl(symbol);
	assert RES != null : "@postconditions RES != null";	     
	assert !TYPE_CHECK || RES.getType().equals(symbol.getType()) && (((RES instanceof Variable) && ((Variable)RES).isVariable()) == symbol.isVariable()) : "@postconditions " + RES.getType() + "=" + symbol.getType() + " & (" + ((RES instanceof Variable) && ((Variable)RES).isVariable()) + "<->" + symbol.isVariable() + ") for " + symbol + " = " + RES;
	return RES;
    }

    private Expression createAtomicImpl(Symbol symbol) {
	if (symbol == null)
	    throw new NullPointerException("illegal symbol: " + symbol);
	final String signifier = symbol.getSignifier();
	assert signifier != null;

	// check if it's already predefined in the coreSignature()
	if (coreSignature().contains(symbol)) {
	    // fixed interpretation of core signature
	    final Object ref = coreInterpretation().get(symbol);
	    return createFixedSymbol(symbol, ref, true);
	}
	// ordinary (new) symbols
	assert !("true".equals(signifier) || "false".equals(signifier)) : "true and false are in core signature and no ordinary symbols";

	// test for syntactically legal <INTEGER_LITERAL> | <FLOATING_POINT_LITERAL>
	//@todo could also move to an infinite coreInterpretation()
	if (symbol.getType().subtypeOf(Types.getDefault().objectType(Arithmetic.class))
	    || symbol.getType().subtypeOf(Types.getDefault().objectType(Number.class)))
	    try {
		return createFixedSymbol(symbol, Values.getDefaultInstance().valueOf(signifier), false);
	    }
	    catch (NumberFormatException trial) {}

	if (symbol.getType().equals(Types.getDefault().objectType(String.class))) {
	    try {
		// test for syntactically legal <STRING_LITERAL>
		isSTRING_LITERAL(symbol);
		return createSymbol(symbol);
	    }
	    catch (IllegalArgumentException trial) {}
	}
	// test for syntactically legal <IDENTIFIER>
	isIDENTIFIER(symbol);
	return createSymbol(symbol);
    } 

    
    public Expression.Composite compose(Expression compositor, Expression arguments[]) throws ParseException, TypeException {
	if (compositor == null)
	    throw new NullPointerException("illegal arguments: compositor " + compositor + " composed with " + MathUtilities.format(arguments));
        if (TYPE_CHECK && !Types.isApplicableTo(compositor.getType(), arguments))
	    throw new TypeException("compositor " + Types.toTypedString(compositor) + " not applicable to the " + arguments.length + " arguments " + MathUtilities.format(arguments) + ':' + Types.typeOf(arguments), compositor.getType().domain(), Types.typeOf(arguments));

	Expression.Composite RES = composeImpl(compositor, arguments);
	assert RES != null : "@postconditions RES != null";	     
	assert !TYPE_CHECK || RES.getType().equals(compositor.getType().on(Types.typeOf(arguments))) : "@postconditions " + RES.getType() + " = " + compositor.getType() + "(on)" + Types.typeOf(arguments) + " = " + compositor.getType().on(Types.typeOf(arguments)) + " (right type compose)\n\tfor " + RES + " = compose(" + compositor + " , " + MathUtilities.format(arguments) + ")";
	return RES;
    }
    Expression.Composite composeImpl(Expression compositor, Expression arguments[]) throws ParseException {
	if (!(compositor instanceof ModernFormula.FixedAtomicSymbol))
	    return composeDelayed((Formula) compositor,
				  arguments,
				  compositor instanceof ModernFormula.AtomicSymbol
				  ? ((ModernFormula.AtomicSymbol)compositor).getSymbol().getNotation().getNotation()
				  : Notation.DEFAULT);
	else {
	    // optimized composition for fixed interpretation compositors
	    ModernFormula.FixedAtomicSymbol opfix = (ModernFormula.FixedAtomicSymbol) compositor;
	    Functor ref = (Functor) opfix.getReferent();
	    assert ref.toString().equals(opfix.getSymbol().getSignifier()) : "interprets with a functor of the same string representation (functor " + ref + " for symbol " + opfix.getSymbol() + ")";
	    try {
		// core-symbols
		// fixed interpretation of core signature
		Symbol s2 = null;
		assert (s2 = coreSignature().get(ref.toString(), arguments)) != null : "composition functors occur in the signature";
		assert opfix.getSymbol().equals(s2) : "enforce any potential unambiguities of operators";
		return composeFixed(opfix.getSymbol(), (Functor)ref, arguments);
	    }
	    catch (IllegalArgumentException ex) {throw new ParseException(ex.getMessage(), COMPLEX_ERROR_OFFSET);}
	}
    }


    // delegation helper methods
    
    // base case atomic symbols

    /**
     * Construct (a formula view of) an atomic symbol.
     * @param symbol the symbol for which to create a formula representation
     * @see orbital.logic.sign.ExpressionBuilder#createAtomic(Symbol)
     */
    public Formula createSymbol(Symbol symbol) {
	return ModernFormula.createSymbol(this, symbol);
    }
    /**
     * Construct (a formula view of) an atomic symbol with a fixed interpretation.
     * @param symbol the symbol for which to create a formula representation
     * @param referent the fixed interpretation of this symbol
     * @param core whether symbol is in the core such that it does not belong to the proper signature.
     * @see orbital.logic.sign.ExpressionBuilder#createAtomic(Symbol)
     */
    public Formula createFixedSymbol(Symbol symbol, Object referent, boolean core) {
	return ModernFormula.createFixedSymbol(this, symbol, referent, core);
    }

    // composition
    
    /**
     * Delayed composition of a symbol with some arguments.
     * Usually for user-defined predicates etc. or predicates subject to interpretation.
     * @param f the compositing formula.
     * @param arguments the arguments to the composition by f.
     * @param notation the notation for the composition (usually determined by the composing symbol).
     */
    public Formula.Composite composeDelayed(Formula f, Expression arguments[], Notation notation) {
	return ModernFormula.composeDelayed(this, f, arguments, notation);
    }

    /**
     * Instant composition of functors with a fixed core interperation
     * Usually for predicates etc. subject to fixed core interpretation.
     * @param f the compositing formula.
     * @param arguments the arguments to the composition by f.
     * @param fsymbol the symbol with with the fixed interpretation f.
     */
    public Formula.Composite composeFixed(Symbol fsymbol, Functor f, Expression arguments[]) {
	return ModernFormula.composeFixed(this, fsymbol, f, arguments);
    }

    // parsing

    /**
     * {@inheritDoc}.
     * Parses single formulas or sequences of formulas delimited by comma and enclosed in curly brackets.
     * Sequences of expressions are represented by a <em>special</em> compound expression encapsulating the array of
     * expressions as its {@link Expression.Composite#getComponent() component}.
     * @todo enhance documentation (and implementation) of how to distinguish this special ExpressionSequence
     * from other compound expressions.
     */
    public Expression createExpression(String expression) throws ParseException {
	if (expression == null)
	    throw new NullPointerException("null is not an expression");
	try {
	    LogicParser parser = new LogicParser(new StringReader(expression));
	    parser.setSyntax(this);
	    return parser.parseFormulas();
	} catch (orbital.moon.logic.ParseException ex) {
	    throw new ParseException(ex.getMessage() + "\nIn expression: " + expression,
				     ex.currentToken == null ? COMPLEX_ERROR_OFFSET : ex.currentToken.next.beginLine,
				     ex.currentToken == null ? COMPLEX_ERROR_OFFSET : ex.currentToken.next.beginColumn,
				     ex);
	} catch (TypeException ex) {
	    //@internal we could also elongate "\nIn expression: " + expression, to the exception message.
	    throw ex;
	} catch (IllegalArgumentException ex) {
	    //@internal we could also elongate "\nIn expression: " + expression, to the exception message.
	    throw ex;
	} 
    }
    
    public Signature scanSignature(String expression) throws ParseException {
	return ((Formula)createExpression(expression)).getSignature();
    }


    // convenience helpers

    /**
     * Inference (facade for convenience).
     * @param w the comma separated list of premise expressions to parse.
     * @return whether w <span class="inference">|~</span><sub>K</sub> d.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Method</a>
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see #createAllExpressions(String)
     * @see #createExpression(String)
     * @see orbital.logic.imp.Inference#infer(Formula[],Formula)
     */
    public boolean infer(String w, String d) throws ParseException {
	return infer(w, d, false);
    } 
    private boolean infer(String w, String d, boolean verbose) throws ParseException {
	Expression B_parsed[] = createAllExpressions(w);
	Formula B[] = B_parsed instanceof Formula[]
	    ? (Formula[]) B_parsed
	    : (Formula[]) Arrays.asList(B_parsed).toArray(new Formula[0]);
	Formula D = (Formula) createExpression(d);
	logger.log(Level.FINE, "Formula {0} has type {1} with sigma={2}", new Object[] {D, D.getType(), D.getSignature()});
	if (verbose)
	    System.out.println(MathUtilities.format(B) + "\t|=\t" + D + " ??");
	return inference().infer(B, D);
    } 
	
    /**
     * Create a sequence of (compound) expressions by parsing a list of expressions.
     * This method is like {@link #createExpression(String)}, but restricted to lists of expressions.
     * <p>
     * For example, in the context of conjectures when given
     * <pre>
     * {A&B, A&~C}
     * </pre>
     * an implementation could parse it as two formulas <code>A&B</code> and <code>A&~C</code>.
     * </p>
     * @param expressions the comma separated list of expressions to parse.
     * @throws UnsupportedOperationException if no syntax notation for sequences of formulas
     *  has been defined.
     * @postconditions RES instanceof Formula[] covariant return-type
     * @todo deprecated
     * @note This method is superfluous since its sole function is to unwrap the result of
     *  {@link #createExpression(String)} for sequences of expressions. So you are advised to
     *  stick to the interface method {@link #createExpression(String)} to minimize implementation
     *  dependencies of your code.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see #createExpression(String)
     */
    public Expression[] createAllExpressions(String expressions) throws ParseException {
	Expression expr = createExpression(expressions);
	assert expr instanceof ExpressionSequence : "parsed sequence of expressions";
	return (Formula[]) Arrays.asList((Expression[]) ((ExpressionSequence)expr).getComponent())
	    .toArray(new Formula[0]);
    }

    /**
     * Test for syntactically legal <IDENTIFIER>
     * @throws IllegalArgumentException if signifier is not an IDENTIFIER.
     * @todo can't we use new LogicParserTokenManager(signifier).getNextToken()?
     */
    private void isIDENTIFIER(Symbol symbol) {
	String signifier = symbol.getSignifier();
	for (int i = 0; i < signifier.length(); i++) {
	    char ch = signifier.charAt(i);
	    if ((i > 0 && !(ch == '_' || Character.isLetterOrDigit(ch)))
		|| (i == 0 && !(ch == '_' || Character.isLetter(ch))))
		throw new IllegalArgumentException("illegal character `" + ch + "' in symbol '" + symbol + "'");
	}
    }

    /**
     * Test for syntactically legal <STRING_LITERAL>
     * @throws IllegalArgumentException if signifier is not a STRING_LITERAL.
     * @todo can't we use new LogicParserTokenManager(signifier).getNextToken()?
     */
    private void isSTRING_LITERAL(Symbol symbol) {
	String signifier = symbol.getSignifier();
	if (signifier.length() < 2 || signifier.charAt(0) != '\"' || signifier.charAt(signifier.length() - 1) != '\"')
	    throw new IllegalArgumentException("illegal character in string '" + symbol + "'");
	for (int i = 1; i < signifier.length() - 1; i++) {
	    char ch = signifier.charAt(i);
	    if (ch == '\"' && signifier.charAt(i-1) != '\\')
		throw new IllegalArgumentException("illegal character `" + ch + "' in string '" + symbol + "'");
	}
    }
}
