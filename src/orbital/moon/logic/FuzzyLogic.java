/*
 * @(#)FuzzyLogic.java 0.9 1997/05/01 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.imp.Logic;
import orbital.logic.imp.Inference;
import orbital.logic.functor.Functor;
import orbital.logic.functor.Functor.Composite;
import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;

import orbital.logic.imp.Signature;
import orbital.logic.imp.Symbol;
import orbital.logic.imp.Formula;
import orbital.logic.imp.Interpretation;
import orbital.logic.imp.Expression;

import orbital.logic.imp.SignatureBase;
import orbital.logic.imp.InterpretationBase;
import orbital.logic.imp.SymbolBase;
import orbital.logic.imp.LogicBasis;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.StringReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.TreeMap;

import orbital.math.MathUtilities;
import orbital.io.IOUtilities;
import orbital.util.InnerCheckedException;
import java.beans.IntrospectionException;

import orbital.logic.functor.Notation;
import orbital.logic.functor.Notation.NotationSpecification;

import orbital.util.Utility;

/**
 * A FuzzyLogic class that represents logic values in quantitative fuzzy logic.
 * <p>
 * Fuzzy logic is a numeric approach in which truth-values represent a degree of truth
 * specified as a real number in the range [0,1].
 * (Unlike probabilities in the range [0,1] which specify a degree of belief.)</p>
 * <p>
 * <table>
 *   <tr>
 *     <td colspan="3">&#8911; is a <dfn>fuzzy AND operator</dfn>, iff</td>
 *   </tr>
 *   <tr>
 *     <td>(c)</td>
 *     <td>a &#8911; b = b &#8911; a</td>
 *     <td>&quot;commutative&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(1)</td>
 *     <td>0 &#8911; 0 = 0<br>
 *       1 &#8911; 0 = 0<br>
 *       0 &#8911; 1 = 0<br>
 *       1 &#8911; 1 = 1</td>
 *     <td>&quot;&and; boundary conditions&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(mon)</td>
 *     <td>a<sub>1</sub>&le;a<sub>2</sub> and b<sub>1</sub>&le;b<sub>2</sub> implies a<sub>1</sub> &#8911; b<sub>1</sub> &le; a<sub>2</sub> &#8911; b<sub>2</sub></td>
 *     <td>&quot;monotonic&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(a)</td>
 *     <td>a &#8911; (b &#8911; c) = (a &#8911; b) &#8911; c</td>
 *     <td>&quot;associative&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(idem)</td>
 *     <td>a &#8911; a = a</td>
 *     <td>&quot;idempotent&quot; (optional)</td>
 *   </tr>
 * </table>
 * The only function that fulfills all axioms is the classical a&#8911;b = max(a,b) [Klir, Folger 1988].
 * </p>
 * <p>
 * <table>
 *   <tr>
 *     <td colspan="3">&#8910; is a <dfn>fuzzy OR operator</dfn>, iff</td>
 *   </tr>
 *   <tr>
 *     <td>(c)</td>
 *     <td>a &#8910; b = b &#8910; a</td>
 *     <td>&quot;commutative&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(1)</td>
 *     <td>0 &#8910; 0 = 0<br>
 *       1 &#8910; 0 = 1<br>
 *       0 &#8910; 1 = 1<br>
 *       1 &#8910; 1 = 1</td>
 *     <td>&quot;&or; boundary conditions&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(mon)</td>
 *     <td>a<sub>1</sub>&le;a<sub>2</sub> and b<sub>1</sub>&le;b<sub>2</sub> implies a<sub>1</sub> &#8910; b<sub>1</sub> &le; a<sub>2</sub> &#8910; b<sub>2</sub></td>
 *     <td>&quot;monotonic&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(a)</td>
 *     <td>a &#8910; (b &#8910; c) = (a &#8910; b) &#8910; c</td>
 *     <td>&quot;associative&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(idem)</td>
 *     <td>a &#8910; a = a</td>
 *     <td>&quot;idempotent&quot; (optional)</td>
 *   </tr>
 * </table>
 * The only function that fulfills all axioms is the classical a&#8910;b = min(a,b) [Klir, Folger 1988].
 * <table>
 *   <tr>
 *     <td colspan="3">~ is a <dfn>fuzzy NOT operator</dfn>, iff</td>
 *   </tr>
 *   <tr>
 *     <td>(1)</td>
 *     <td>~  1 = 0<br>
 *       ~  0 =1</td>
 *     <td>&quot;&not; boundary conditions&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(mon)</td>
 *     <td>anb implies ~  b &le; ~  a</td>
 *     <td>&quot;monotonic&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(inv)</td>
 *     <td>~ ~ a = a</td>
 *     <td>&quot;involutorical&quot; (optional)</td>
 *   </tr>
 * </table>
 * </p>
 * <p>
 * <dl class="def">
 * Let X be a universal set.
 *   <dt>fuzzy set</dt>
 *   <dd>A = {(x,&mu;<sub>A</sub>(x)) &brvbar; x&isin;X} is defined with a membership function &mu;<sub>A</sub>:X&rarr;[0,1].
 *    A fuzzy set A is sometimes described formally as "A=&mu;<sub>A</sub>(a<sub>1</sub>)/a<sub>1</sub> + &mu;<sub>A</sub>(a<sub>2</sub>)/a<sub>2</sub> + ... + &mu;<sub>A</sub>(a<sub>n</sub>)/a<sub>n</sub>"
 *    where + and / are syntactic symbols and do not denote arithmetic operations.
 *    Then fuzzy sets and fuzzy logic are related by
 *      <blockquote>
 *      &mu;<sub>A&cup;B</sub> = &mu;<sub>A</sub> &#8910; &mu;<sub>B</sub><br />
 *      &mu;<sub>A&cap;B</sub> = &mu;<sub>A</sub> &#8911; &mu;<sub>B</sub><br />
 *      &mu;<sub>A<sup>&#8705;</sup></sub> = ~ &mu;<sub>A</sub>
 *      </blockquote>
 *    </dd>
 *   <dt>cardinality</dt>
 *   <dd>|A| = &sum;<sub>x&isin;X</sub> &mu;<sub>A</sub>(x)</dd>
 *   <dt>entropy</dt>
 *   <dd class="Formula">E(A) = |A&cap;A<sup>&#8705;</sup>| / |A&cup;A<sup>&#8705;</sup>|</dd>
 * </dl>
 * </p>
 * 
 * @version 0.7, 1999/01/11
 * @author  Andr&eacute; Platzer
 * @see "Klir, G. and Folger, T. (1988), Fuzzy Sets, Uncertainty and Information, Prentice-Hall, Englewood Cliffs."
 */
public class FuzzyLogic extends ModernLogic implements Logic {
    /**
     * tool-main
     */
    public static void main(String arg[]) throws Exception {
	if (arg.length > 0 && "-?".equals(arg[0])) {
	    System.out.println(usage);
	    System.out.println("Core operators:\n\t" + new FuzzyLogic().coreSignature());
	    return;
	} 
	FuzzyLogic logic = new FuzzyLogic();
	System.out.println("Enter sequence 'A|~C' to verify. Simply leave blank to type 'false' or {} or null.");
	System.out.print("Type base expression (A): ");
	System.out.flush();
	String expr = IOUtilities.readLine(System.in);
	System.out.print("Type sequence expression (C): ");
	System.out.flush();
	String  expr2 = IOUtilities.readLine(System.in);
	System.out.println(logic.satisfy(new InterpretationBase(SignatureBase.EMPTY, Collections.EMPTY_MAP), logic.createFormula(expr2)));
	boolean sat = logic.infer(expr, expr2);
	System.out.println(expr + (sat ? " satisfies " : " does not satisfy ") + expr2);
    } 
    public static final String usage = "interpret fuzzy logic";

    public FuzzyLogic() {}

    /**
     * facade for convenience.
     * @see <a href="{@docRoot}/DesignPatterns/Facade.html">Facade</a>
     */
    public boolean infer(String expression, String exprDerived) throws java.text.ParseException {
	Signature sigma = scanSignature(expression).union(scanSignature(exprDerived));
	Formula B[] = {
	    createFormula(expression)
	};
	Formula D = createFormula(exprDerived);
	System.err.println(B[0] + " is interpreted to " + B[0].apply(null));
	System.err.println(D + " is interpreted to " + D.apply(null));
	return inference().infer(B, D);
    } 

    public boolean satisfy(Interpretation I, Formula F) {
	if (F == null)
	    throw new NullPointerException("null is not a formula");
	assert F instanceof FuzzyLogicFormula : "F is a formula in this logic";
        // assure core interpretation unless overwritten
        I = new QuickUnitedInterpretation(_coreInterpretation, I);
	return MathUtilities.equals(((Double) F.apply(I)).doubleValue(), 1, 0.001);
    } 

    public Inference inference() {
	throw new InternalError("no calculus implemented");
    } 

    /**
     * static elements of signature
     */
    protected static String   operators = "~! |&^-><=(),";

    //@todo remove this bugfix that replaces "xfy" by "yfy" associativity only for *.jj parsers to work without inefficient right-associative lookahead.
    private static final String xfy = "yfy";

    private static final Interpretation _coreInterpretation =
	LogicSupport.arrayToInterpretation(new Object[][] {
	    {LogicFunctions.not,          // "~"
	     new NotationSpecification(900, "fy", Notation.PREFIX)},
	    {LogicFunctions.and,          // "&"
	     new NotationSpecification(910, xfy, Notation.INFIX)},
	    {LogicFunctions.xor,          // "^"
	     new NotationSpecification(914, xfy, Notation.INFIX)},
	    {LogicFunctions.or,           // "|"
	     new NotationSpecification(916, xfy, Notation.INFIX)},
	    {LogicFunctions.impl,         // "->"
	     new NotationSpecification(920, xfy, Notation.INFIX)},
	    {LogicFunctions.leftwardImpl, // "<-"
	     new NotationSpecification(920, xfy, Notation.INFIX)},
	    {LogicFunctions.equiv,        // "<->"
	     new NotationSpecification(920, xfy, Notation.INFIX)},
	    {LogicFunctions.forall,       // "°"
	     new NotationSpecification(950, "fxx", Notation.PREFIX)},
	    {LogicFunctions.exists,       // "?"
	     new NotationSpecification(950, "fxx", Notation.PREFIX)}
	}, true);

    private static final Signature _coreSignature = _coreInterpretation.getSignature();

    public Signature coreSignature() {
	return _coreSignature;
    } 
    public Interpretation coreInterpretation() {
	return _coreInterpretation;
    }

    public Signature scanSignature(String expression) {
	return new SignatureBase(scanSignatureImpl(expression));
    } 
    static Set scanSignatureImpl(String expression) {
	if (expression == null)
	    return SignatureBase.EMPTY;
	Collection		names = new LinkedList();
	StringTokenizer st = new StringTokenizer(expression, operators, false);
	while (st.hasMoreElements())
	    //XXX: undo pair unless comparable
	    names.add(LogicParser.defaultSymbolFor((String) st.nextElement()));
	names = new TreeSet(names);
	// the signature really should not include core signature symbols
	names.remove(LogicParser.defaultSymbolFor("true"));
	names.remove(LogicParser.defaultSymbolFor("false"));
	return (Set) names;
    } 

    
    private Formula createFormula(String expression) throws java.text.ParseException {
	return (Formula) createExpression(expression);
    }

    static class LogicFunctions {
        private LogicFunctions() {}
    
    	// interpretation for a truth-value
    	protected static final Object getInt(double v) {
	    return new Double(v);
    	} 
    
    	// truth-value of a Formulas Interpretation
    	protected static final double getTruth(Object f) {
	    return ((Double) f).doubleValue();
    	} 
    
	// Basic logical operations (elemental junctors).
    	public static final Function not = new Function() {
    		public Object apply(Object a) {
		    return getInt(1 - getTruth(a));
        	}
		public String toString() { return "~"; }
	    }; 

    	public static final BinaryFunction and = new BinaryFunction() {
    		public Object apply(Object a, Object b) {
		    // classical fuzzy defintion
		    return getInt(Math.min(getTruth(a), getTruth(b)));
		    // return max(0, a+b-1) limited difference
		    // return ?new FuzzyLogicFormula(a.value * b.value);
    		}
		public String toString() { return "&"; }
	    };
    
    	public static final BinaryFunction or = new BinaryFunction() {
    		public Object apply(Object a, Object b) {
		    return getInt(Math.max(getTruth(a), getTruth(b)));
		    // Yager-Union function min(1, (a^p + b^p)^(1/p)) for p>=1.
		    // return min(1, a+b) limited sum
    		}
		public String toString() { return "|"; }
	    };

	// Derived logical operations.

	//XXX: null will prevent calling .toString() and thus hinder Interpretation.get(...)
	//TODO: The following functions for derived logical operations could be generalized perhaps (see LogicBasis)
    	public static final BinaryFunction xor = null;

    	//@todo how about =< as an implementation of the implication in fuzzy logic?
    	public static final BinaryFunction impl = null;

    	public static final BinaryFunction leftwardImpl = null;

    	public static final BinaryFunction equiv = null;

	// Basic logical operations (elemental quantifiers).

    	public static final BinaryFunction forall = null;

    	public static final BinaryFunction exists = null;
    }
}
