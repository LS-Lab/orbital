/**
 * @(#)ClassicalLogic.java 0.7 1999/01/16 Andre Platzer
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
import orbital.logic.imp.ParseException;

import orbital.logic.functor.Functor;
import orbital.logic.functor.Functor.Composite;
import orbital.logic.functor.*;
import orbital.logic.functor.Predicates;
import orbital.logic.trs.*;
import orbital.moon.logic.bridge.SubstitutionImpl.UnifyingMatcher;

import java.util.Set;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import java.util.Arrays;
import java.util.Collections;
import orbital.util.Setops;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.NoSuchElementException;

import orbital.algorithm.Combinatorical;
import orbital.io.IOUtilities;
import java.io.IOException;
import java.io.StringReader;
import java.io.InputStreamReader;

import orbital.util.Utility;
import orbital.math.MathUtilities;
import orbital.util.InnerCheckedException;
import orbital.util.IncomparableException;
import java.beans.IntrospectionException;
import java.io.*;

import orbital.logic.functor.Notation;
import orbital.logic.functor.Notation.NotationSpecification;
import orbital.logic.functor.Functor.Specification;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ListIterator;
import orbital.algorithm.Combinatorical;

/**
 * Implementation of modern but classical predicate logic (first-order logic).
 * <p>
 * <dl class="def">
 *   <dt>classical logic</dt> <dd>is any logic that accepts <span xml:lang="la">tertium non datur</span>
 *    (alias the Principle of excluded middle, alias the Principle of bivalence).
 *    In a classical logic <em>all</em> logical statements have exactly one truth-value of either
 *    <code>true</code> (&#8868;), or <code>false</code> (&perp;).
 *    It is a two-valued logic.</dd>
 *   <dt>non-classical logic</dt> <dd>does not assume <span xml:lang="la">tertium non datur</span>.
 *    Especially, &not;&not;&phi; usually is not equivalent to &phi;.
 *    <div>What, for example is the truth-value of the following informal statements?
 *      <blockquote>"nowhere in the decimal representation of &pi; does the digit 7
 *      occur 77 times
 *      (with the occurrences immediately following each other)"<br />
 *      "Ancient Greeks worshipped Zeus" (cf. free logic)
 *      </blockquote>
 *    </div>
 *    Most non-classical logics are multi-valued logics.</dd>
 *   <dt>traditional logic</dt> <dd>is the logic prior to Frege</dd>
 *   <dt>modern logic</dt> <dd>is a logic in the spirit of Frege.
 *    It provides <span class="dt">multiple genericity</span>, which means that multiple quantifiers can concern
 *    different individuals. This is possible by using variable symbols.</dd>
 * </dl>
 * </p>
 * <p>
 * For the classical logic, the logical deduction relation is called
 * logic sequence (&#8872;) or semantic sequence. It is a logic inference (correct deduction).
 * Then the inference relation is written as &#8866;,
 * the inference operation is called consequence-operation <code><i>C&#8205;n</i></code>
 * and the implication is called material classical implication and written as &rArr;.</p>
 * <p>
 * The classical logic is truth-functional and it is:
 * <center>I(&not;A) = true if and only if I(A)=false</center>
 * </p>
 * <p>
 * For the ClassicalLogic the inference operation is called the consequence operation <code>Cn</code> over &#8872;.</p>
 * <p>
 * Kurt G&ouml;del's <span xml:lang="de">Vollst&auml;ndigkeitssatz</span> (1930) proves that
 * there is a sound and complete calculus for first-order logic &#8872; that is <a href="../../algorithm/doc-files/computability.html#semi-decidable">semi-decidable</a>.
 * Alonzo Church (1936) and Alan Turing (1936) simultaneously showed that &#8872; is <a href="../../algorithm/doc-files/computability.html#undecidable">undecidable</a>.
 * (Since the tautological formulas are <a href="../../algorithm/doc-files/computability.html#undecidable">undecidable</a>,
 *  and therefore satisfiable formulas are not even <a href=""../../algorithm/doc-files/computability.html#semi-decidable">semi-decidable</a>.)
 * <span class="@todo is Schöning sure?">As a corollary, consistency of formulas is also just semi-decidable.</span>
 * The first constructive proof for a sound and complete calculus for &#8872; was due to Robinson (1965).
 * </p>
 * <p>
 * However, Kurt G&ouml;del's <span xml:lang="de">Unvollst&auml;ndigkeitssatz</span> (1931)
 * proves that in first-order logic, the arithmetic theory Theory(<b>N</b>,+,*) is not axiomatizable
 * and <span class="todo">thus undecidable</span>.
 * This <span class="todo">shows</span> that every sound calculus for an extended first-order logic including
 * arithmetic (<b>N</b>,+,*) and mathematical induction (for <b>N</b>) is <em><a href="../../logic/imp/Interpretation.html#complete">incomplete</a></em>
 * (whatever axioms and inference rules it might have).</p>
 * <p>
 * Higher-order logic inference rules must be unsound or incomplete anyway.
 * In any case, at least the part of first-order predicate logic without quantifiers,
 * which is called propositional logic, has a simple sound and complete calculus that makes
 * it decidable.
 * </p>
 * @version 0.8, 1999/01/16
 * @version 0.7, 1999/01/16
 * @author  Andr&eacute; Platzer
 * @see "G&ouml;del, Kurt (1930). &Uuml;ber die Vollst&auml;digkeit des Logikkalk&uuml;s. PhD Thesis, University of Vienna."
 * @see "G&ouml;del, Kurt (1931). &Uuml;ber formal unentscheidbare S&auml;tze der Principia mathematica und verwandter Systeme I. Monatshefte f&uuml;r Mathematik und Physik, 38:173-198."
 * @see "Church, Alonzo (1936). A note on the Entscheidungsproblem. Journal of Symbolic Logic, 1:40-41 and 101-102."
 * @see "Turing, Alan M. (1936). On computable numbers, with an application to the Entscheidungsproblem. Proceedings of the London Mathematical Society, 2nd series, 42:230-265. Correction published in Vol. 43, pages 544-546."
 * @todo refactorize common ideas into a super class
 * @todo introduce &#407;ukasiewicz logic
 * @todo Especially provide forall as a functional (higher-order function) of lambda-operator then (@see note to orbital.logic.functor.Substitition)
 */
public class ClassicalLogic extends ModernLogic {
    private static class Debug {
	private Debug() {}
	public static void main(String arg[]) throws Exception {
	    ClassicalLogic.main(new String[] {"-normalForm", "all", "none", "properties"});
	} 
    }	 // Debug

    /**
     * Maximum number of InferenceMechanism objects (for typesafe enum).
     */
    private static final int MAX_INFERENCE_MECHANISMS = 10;

    private static final Logger logger = Logger.getLogger(ClassicalLogic.class.getName());

    /**
     * Charset of internal files for tool-main.
     * @internal note reader.read() does not terminate for UTF-16. Seems JDK BugID
     * @internal however our UTF-8 does not start with three strange bytes, as for Notepad.exe.
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    // tool methods

    /**
     * tool-main
     */
    public static void main(String arg[]) throws Exception {
	if (orbital.signe.isHelpRequest(arg)) {
	    System.out.println(usage);
	    System.out.println("Core logical junctors and operators:\n\t" + new ClassicalLogic().coreSignature());
	    if ("-verbose".equalsIgnoreCase(arg[0])) {
		System.out.println(" = {");
		for (Iterator i = new ClassicalLogic().coreSignature().iterator(); i.hasNext(); ) {
		    Symbol s = (Symbol)i.next();
		    System.out.println("\t" + s + "\t: " + s.getType());
		}
		System.out.println(" }");
	    }
	    return;
	} 
	try {
	    // possible command-line options
	    boolean normalForm = false;
	    boolean verbose = false;
	    boolean closure = false;
	    String charset = null;

	    ClassicalLogic logic = new ClassicalLogic();
	    boolean hasBeenProving = false;

	    //@todo we should print an error if there already was a file in arg, but the last options are not followed by a file again and are completely vain
	    for (int option = 0; option < arg.length; option++) {
		if ("-normalForm".equals(arg[option]))
		    normalForm = true;
		else if ("-closure".equals(arg[option]))
		    closure = true;
		else if ("-verbose".equals(arg[option]))
		    verbose = true;
		else if ("-resolution".equals(arg[option]))
		    logic.setInferenceMechanism(RESOLUTION_INFERENCE);
		else if (arg[option].startsWith("-charset=")) {
		    charset = arg[option].substring("-charset=".length());
		    System.out.println("using charset " + charset);
		} else if ("table".equalsIgnoreCase(arg[option])) {
		    System.out.print("Type expression: ");
		    System.out.flush();
		    String expression = IOUtilities.readLine(System.in);
		    Formula B = (Formula) logic.createExpression(expression);
		    Signature sigma = B.getSignature();
		    for (Iterator Int = logic.createAllInterpretations(sigma, sigma);
			 Int.hasNext();
			 ) {
			Interpretation I = (Interpretation) Int.next();
			System.out.println(I + ":\t" + logic.satisfy(I, B));
		    }
		    hasBeenProving = true;
		} else {
		    String file = arg[option];
		    Reader rd = null;
		    System.out.println("proving " + file + " ...");
		    try {
			if ("all".equalsIgnoreCase(file)
			    || "none".equalsIgnoreCase(file)
			    || "properties".equalsIgnoreCase(file)
			    || "fol".equalsIgnoreCase(file)
			    ) {
			    //@internal our resolution does not prove things resulting from contradictious facts (after simplification) so avoid those cases by providing a special file for resolution.
			    final String mech = logic.getInferenceMechanism() == RESOLUTION_INFERENCE
				? "resolution"
				: "semantic";
			    String resName;
			    boolean expected;
			    if ("all".equalsIgnoreCase(file)) {
				resName = mech + "-equivalence.txt";
				expected = true;
			    } else if ("none".equalsIgnoreCase(file)) {
				resName = mech + "-garbage.txt";
				expected = false;
			    } else if ("properties".equalsIgnoreCase(file)) {
				resName = "semantic-properties.txt";
				expected = true;
			    } else if ("fol".equalsIgnoreCase(file)) {
				resName = "resolution-fol.txt";
				expected = true;
			    } else
				throw new InternalError("none of the cases of which one occurs is true");
			    rd = new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/" + resName), DEFAULT_CHARSET);
			    if (expected != proveAll(rd, logic, expected, normalForm, closure, verbose))
				throw new LogicException("instantiated " + logic + " which does " + (expected ? "not support all conjectures" : "a contradictory conjecture") + " of " + resName + ". Either the logic is non-classical, or the resource file is corrupt.");
			} else {
			    rd = charset == null
				? new FileReader(file)
				: new InputStreamReader(new FileInputStream(file), charset);
			    if (!proveAll(rd, logic, true, normalForm, closure, verbose))
				System.err.println("could not prove all conjectures");
			    else
				System.err.println("all conjectures were proven successfully");
			}
		    }
		    catch (FileNotFoundException x) {
			System.err.println(orbital.signe.getHelpAboutHelp());
			throw x;
		    }
		    finally {
			if (rd != null)
			    rd.close();
			hasBeenProving = true;
		    }
		}
	    }
    
	    if (!hasBeenProving) {
		// we did not have something to prove yet, so go ask the user what to do
		//@todo we could just as well append "-verbose", "con" to the arguments instead
		System.out.println("Enter logic sequences 'A |= C' or equivalences 'A == C' to prove.");
		System.out.println("Simply leave blank to denote the empty set {}.");
		System.out.println("Type EOF (Ctrl-Z or C-d) to quit proving further formulas.");
		verbose = true;
		Reader rd = null;
		try {
		    rd = new InputStreamReader(System.in);
		    proveAll(rd, logic, true, normalForm, closure, verbose);
		}
		finally {
		    if (rd != null)
			rd.close();
		}
	    }
    	}
    	catch (Exception ex) {
	    logger.log(Level.SEVERE, "exception occured", ex);
	    throw ex;
    	}
    	catch (Error ex) {
	    logger.log(Level.SEVERE, "exception occured", ex);
	    throw ex;
    	}
    }
    /**
     * @todo move content to the ResourceBundle.
     */
    public static final String usage = "usage: [options] [all|none|properties|fol|<filename>|table]\n\tall\tprove important semantic-equivalence expressions\n\tnone\ttry to prove some semantic-garbage expressions\n\tproperties\tprove some properties of classical logic inference relation\n\tfol\tprove important equivalences of first-order logic\n\n\t<filename>\ttry to prove all expressions in the given file\n\ttable\tprint a function table of the expression instead\n\t-\tUse no arguments at all to be asked for expressions\n\t\tto prove.\noptions:\n\t-normalForm\tcheck the conjunctive and disjunctive forms in between\n\t-closure\tprint the all/existence closures in between\n\t-resolution\tuse resolution instead of semantic inference\n\t-verbose\tbe more verbose (f.ex. print normal forms if -normalForm)\n\t-charset=<encoding>\tthe character set or encoding to use for reading files\n\nTo check whether A and B are equivalent, enter '|= A<->B'";


    
    // classical logic
    
    /**
     * Specifies the nference mechanism applied for the {@link ClassicalLogic#inference() inference relation}.
     * @version 1.1, 2002-09-14
     * @author  Andr&eacute; Platzer
     * @see <a href="{@docRoot}/Patterns/Design/enum.html">typesafe enum pattern</a>
     * @internal typesafe enumeration pattern class to specify fuzzy logic operators
     * @invariant a.equals(b) &hArr; a==b
     * @todo improve name
     */
    public static abstract class InferenceMechanism implements Serializable, Comparable {
	private static final long serialVersionUID = -3446080535669332735L;
	/**
	 * the name to display for this enum value
	 * @serial
	 */
	private final String name;

	/**
	 * Ordinal of next enum value to be created
	 */
	private static int nextOrdinal = 0;

	/**
	 * Assign an ordinal to this enum value
	 * @serial
	 */
	private final int ordinal = nextOrdinal++;

	/**
	 * Table of all canonical references to enum value classes.
	 */
	private static InferenceMechanism[] values = new InferenceMechanism[MAX_INFERENCE_MECHANISMS];

	InferenceMechanism(String name) {
	    this.name = name;
	    values[nextOrdinal - 1] = this;
	}

	/**
	 * Order imposed by ordinals according to the order of creation.
	 * @post consistent with equals
	 */
	public int compareTo(Object o) {
	    return ordinal - ((InferenceMechanism) o).ordinal;
	} 

	/**
	 * Maintains the guarantee that all equal objects of the enumerated type are also identical.
	 * @post a.equals(b) &hArr; if a==b.
	 */
	public final boolean equals(Object that) {
	    return super.equals(that);
	} 
	public final int hashCode() {
	    return super.hashCode();
	} 

	public String toString() {
	    return this.name;
	} 

	/**
	 * Maintains the guarantee that there is only a single object representing each enum constant.
	 * @serialData canonicalized deserialization
	 */
	private Object readResolve() throws ObjectStreamException {
	    // canonicalize
	    return values[ordinal];
	} 

	/**
	 * Defines the inference mechanism used.
	 * @post RES==OLD(RES)
	 */
	abstract Inference inference();

    }

    /**
     * The inference mechanism applied for the {@link #inference() inference relation}.
     * @serial
     * @see #inference()
     */
    private InferenceMechanism inferenceMechanism;
    
    public ClassicalLogic() {
	this(SEMANTIC_INFERENCE);
    }
    public ClassicalLogic(InferenceMechanism inferenceMechanism) {
	setInferenceMechanism(inferenceMechanism);
    }

    public String toString() {
	return getClass().getName() + '[' + getInferenceMechanism() + ']';
    }

    /**
     * Set the inference mechanism applied for the {@link #inference() inference relation}.
     * @see #inference()
     * @see #SEMANTIC_INFERENCE
     * @see #RESOLUTION_INFERENCE
     */
    public void setInferenceMechanism(InferenceMechanism mechanism) {
    	this.inferenceMechanism = mechanism;
    }
    protected InferenceMechanism getInferenceMechanism() {
    	return inferenceMechanism;
    }

    //@todo remove this bugfix that replaces "xfy" by "yfy" associativity only for *.jj parsers to work without inefficient right-associative lookahead.
    private static final String xfy = "yfy";

    //@todo unmodifiable view
    private static final Interpretation _coreInterpretation =
	LogicSupport.arrayToInterpretation(new Object[][] {
	    /**
	     * Contains ordered map (in precedence order) of initial functors
	     * and their notation specifications.
	     * Stored internally as an array of length-2 arrays.
	     * @invariant sorted, i.e. precedenceOf[i] < precedenceOf[i+1]
	     */
	    //@fixme debug why the thing ~(a->a) is displayed as ~a->a etc.  Use BESTFIX! (@see Notation#hasCompactBrackets)
	    {Types.UNIVERSAL,
	     new NotationSpecification(500, "xf", Notation.POSTFIX)},
	    {Types.objectType(java.lang.Boolean.class, "truth"),
	     new NotationSpecification(500, "xf", Notation.POSTFIX)},
	    {Types.objectType(java.lang.Object.class, "individual"),
	     new NotationSpecification(500, "xf", Notation.POSTFIX)},
	    {Types.objectType(orbital.math.Integer.class, "integer"),
	     new NotationSpecification(500, "xf", Notation.POSTFIX)},
	    {Types.objectType(orbital.math.Real.class, "real"),
	     new NotationSpecification(500, "xf", Notation.POSTFIX)},
	    {Types.objectType(String.class, "string"),
	     new NotationSpecification(500, "xf", Notation.POSTFIX)},


	    {Types.list,
	     new NotationSpecification(500, "fx", Notation.PREFIX)},
	    {Types.set,
	     new NotationSpecification(500, "fx", Notation.PREFIX)},
	    {Types.map,
	     new NotationSpecification(500, "xfx", Notation.INFIX)},

	    {orbital.math.functional.Operations.plus, null},

	    {Predicates.equal,            // "="
	     new NotationSpecification(700, "xfx", Notation.INFIX)},
	    //@xxx debug !=
	    {Predicates.unequal,          // "!="
	     new NotationSpecification(700, "xfx", Notation.INFIX)},
	    {Predicates.greater,          // ">"
	     new NotationSpecification(700, "xfx", Notation.INFIX)},
	    {Predicates.less,             // "<"
	     new NotationSpecification(700, "xfx", Notation.INFIX)},
	    {Predicates.greaterEqual,     // ">="
	     new NotationSpecification(700, "xfx", Notation.INFIX)},
	    {Predicates.lessEqual,        // "=<"
	     new NotationSpecification(700, "xfx", Notation.INFIX)},
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
	     new NotationSpecification(950, "fxx", Notation.PREFIX)},
	    {LogicFunctions.lambda,       // "\\"
	     new NotationSpecification(1100, "fxx", Notation.PREFIX)},
	    {LogicFunctions.pi,           // "\\\\"
	     new NotationSpecification(1100, "fxx", Notation.PREFIX)}
	}, false, true, true);
    private static final Signature _coreSignature = _coreInterpretation.getSignature();

    // Helper utilities.

    static class LogicFunctions {
        LogicFunctions() {}
    
	private static final Type UNARY_LOGICAL_JUNCTOR = Types.predicate(Types.TRUTH);
	private static final Type BINARY_LOGICAL_JUNCTOR = Types.predicate(Types.product(new Type[] {Types.TRUTH, Types.TRUTH}));

    	// interpretation for a truth-value
    	private static final Object getInt(boolean b) {
	    return b ? Boolean.TRUE : Boolean.FALSE;
    	} 
    
    	// truth-value of a value
    	private static final boolean getTruth(Object v) {
	    return ((Boolean) v).booleanValue();
    	} 

	// (still) identical to @see orbital.moon.logic.functor.Operations.not...
	//@todo move implementation to a superclass orbital.logic.functor.Operations of orbital.math.functional.Operations?
	// moved, but if we generalize those implementations, we will _here_ only accept _elementary_ operations on _boolean_ truth-values, not on formulas or something.

	// Basic logical operations (elemental junctors).
    	public static final Function not = new Function() {
		private final Type logicalTypeDeclaration = UNARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a) {
		    return getInt(!getTruth(a));
		}
		public String toString() { return "~"; }
	    }; 

    	public static final BinaryFunction/*<Boolean,Boolean, Boolean>*/ and = new BinaryFunction() {
		private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a, Object b) {
		    return getInt(getTruth(a) && getTruth(b));
    		}
		public String toString() { return "&"; }
	    };
    
    	public static final BinaryFunction or = new BinaryFunction() {
		private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a, Object b) {
		    return getInt(getTruth(a) || getTruth(b));
    		}
		public String toString() { return "|"; }
	    };

	// Derived logical operations.

	//@todo The following functions for derived logical operations could be generalized (see LogicBasis)
    	public static final BinaryFunction xor = new BinaryFunction() {
		private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a, Object b) {
		    return getInt(getTruth(a) ^ getTruth(b));
    		}
		public String toString() { return "^"; }
	    };

    	public static final BinaryFunction impl = new BinaryFunction() {
		private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a, Object b) {
		    return getInt(!getTruth(a) || getTruth(b));
    		}
		public String toString() { return "->"; }
	    };

    	//@todo rename
    	public static final BinaryFunction leftwardImpl = new BinaryFunction() {
		private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a, Object b) {
		    return getInt(getTruth(a) || !getTruth(b));
    		}
		public String toString() { return "<-"; }
	    };

    	public static final BinaryFunction equiv = new BinaryFunction() {
		private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a, Object b) {
		    return getInt(getTruth(a) == getTruth(b));
    		}
		public String toString() { return "<->"; }
	    };

	// Basic logical operations (elemental quantifiers).

	//@todo we could implement a semantic apply() if only Interpretations would tell us a collection of entities in the universe
	//@todo somehow turn forall into type (&sigma;&rarr;t)&rarr;t alias Function<Function<S,boolean>,boolean> and use &forall;(&lambda;x.t)
    	public static final Function forall = new ForallPlaceholder();
	private static final class ForallPlaceholder implements Function {
	    //@todo also templatize with t somehow? should be (t->TRUTH)->truth
	    //private final Type logicalTypeDeclaration = Types.map(Types.product(new Type[] {Types.INDIVIDUAL, Types.TRUTH}), Types.TRUTH);
	    private final Type logicalTypeDeclaration = Types.map(Types.map(Types.INDIVIDUAL, Types.TRUTH), Types.TRUTH);
	    public Object apply(Object f) {
		throw new LogicException("quantified formulas only have a semantic value with respect to a possibly infinite domain. They are available for inference, but they cannot be interpreted with finite means.");
	    }
	    public String toString() { return "°"; }
	};

    	public static final Function exists = new ExistsPlaceholder();
	private static final class ExistsPlaceholder implements Function {
	    //@todo also templatize with t somehow? should be (t->TRUTH)->truth
	    private final Type logicalTypeDeclaration = Types.map(Types.map(Types.INDIVIDUAL, Types.TRUTH), Types.TRUTH);
	    public Object apply(Object f) {
		throw new LogicException("quantified formulas only have a semantic value with respect to a possibly infinite domain. They are available for inference, but they cannot be interpreted with finite means.");
	    }
	    public String toString() { return "?"; }
	};

	//@xxx trick for functions that never get called
    	public static final BinaryFunction lambda = new LambdaPlaceholder();
	private static final class LambdaPlaceholder implements BinaryFunction {
	    /*private static*/ final Specification callTypeDeclaration = new Specification(new Class[] {
		Object.class, Object.class
	    }, Function.class);
	    //@todo also templatize with t somehow? //@xxx type should be s*t->(s->t)
	    //private final Type logicalTypeDeclaration = Types.map(Types.product(new Type[] {Types.UNIVERSAL, Types.UNIVERSAL}), Types.map(Types.UNIVERSAL, Types.UNIVERSAL));
	    //private final Type logicalTypeDeclaration = Types.map(Types.product(new Type[] {Types.INDIVIDUAL, Types.INDIVIDUAL}), Types.map(Types.INDIVIDUAL, Types.INDIVIDUAL));
	    private final Type logicalTypeDeclaration = Types.map(Types.product(new Type[] {Types.INDIVIDUAL, Types.TRUTH}), Types.map(Types.INDIVIDUAL, Types.TRUTH));
	    public Object apply(Object x, Object t) {
		throw new AssertionError("this method never gets called since lambda cannot be interpreted truthh-functionally, but already receives a structural modification in compose(...)");
	    }
	    public String toString() { return "\\"; }
	};
    	public static final BinaryFunction pi = new PiPlaceholder();
	private static final class PiPlaceholder implements BinaryFunction {
	    private final Type logicalTypeDeclaration = Types.map(Types.product(new Type[] {Types.TYPE, Types.TYPE}), Types.map(Types.TYPE, Types.TYPE));
	    public Object apply(Object x, Object t) {
		throw new AssertionError("this method never gets called since pi is an abstract type construction");
	    }
	    public String toString() { return "\\\\"; }
	};
    }

    //@xxx get rid of these shared static variables
    // perhaps, LAMBDA is that important, that we should even publicize it to orbital.logic.imp.*? Or let them query it from the coreSignature() by "\\"?
    static final Symbol LAMBDA;
    static final Symbol PI;
    static {
	//@internal we need some valid non-null arguments. We use one that can be converted to any type lambda might need
	Expression BOTTOM = Utilities.logic.createAtomic(new SymbolBase("BOTTOM", Types.ABSURD));
	LAMBDA = _coreSignature.get("\\", new Expression[] {BOTTOM,BOTTOM});
	assert LAMBDA != null : "lambda operator found";
	PI = _coreSignature.get("\\\\", new Expression[] {BOTTOM,BOTTOM}); //@todo
    }
    public Expression compose(Expression op, Expression arguments[]) throws ParseException {
	// handle special cases of term construction, first and before type checking occurs since the type &Pi;-abstractions need more flexibility
	if (op.getType() instanceof PiAbstractionType) {
	    PiAbstractionType piabst = (PiAbstractionType) op.getType();
	    //@xxx to be honest, we somehow need to unify? For example, if &forall; : (\\s:*.(s&rarr;o)&rarr;o) and if f:i&rarr;o then &forall;(f) : o.
	    Type apType = Types.typeOf(arguments);
	    logger.log(Level.FINEST, "compositor {0} : {1} applied to the {2} arguments {3} : {4}. Result has type {5}", new Object[] {op, op.getType(), new java.lang.Integer(arguments.length), MathUtilities.format(arguments), Types.typeOf(arguments), piabst.apply(Types.typeOf(arguments))});
	    //@todo could we exchange op by a formula that only differs in type, and thus avoid conversion formula?
	    return super.compose(super.compose(new PiApplicationExpression(piabst,
									   piabst.apply(apType)
									   ),
					       new Expression[] {op}
					       ),
				 arguments);
	}
	return super.compose(op, arguments);
    }
    Expression composeImpl(Expression op, Expression arguments[]) throws ParseException {
	// handle special cases of term construction, first
	if ((op instanceof ModernFormula.FixedAtomicSymbol)
	    && LAMBDA.equals(((ModernFormula.FixedAtomicSymbol)op).getSymbol())) {
	    //@todo we provide &lambda;-abstractions by introducing a core symbol LAMBDA that has as fixed interpretation a binary function that ... But of &lambda;(x.t), x will never get interpreted, so it is a bit different than composeFixed(lambda,{x,t}) would suggest. &lambda;-abstraction are not truth-functional!
	    assert arguments.length == 2;
	    assert arguments[0] instanceof ModernFormula.AtomicSymbol : "Symbols when converted to formulas become AtomicSymbols";
	    Symbol x = (Symbol) ((Formula)arguments[0]).getSignature().iterator().next();
	    assert x.isVariable() : "we only form lambda abstractions with respect to variables";
	    return createLambdaProp(x, (Formula) arguments[1]);
	} else if ((op instanceof ModernFormula.FixedAtomicSymbol)
	    && PI.equals(((ModernFormula.FixedAtomicSymbol)op).getSymbol())) {
	    assert arguments.length == 2;
	    assert arguments[0] instanceof ModernFormula.AtomicSymbol : "Symbols when converted to formulas become AtomicSymbols";
	    Symbol x = (Symbol) ((Formula)arguments[0]).getSignature().iterator().next();
	    assert x.isVariable() : "we only form lambda abstractions with respect to variables";
	    return new PiAbstractionExpression(x, (Formula) arguments[1]);
	} else
	    return super.composeImpl(op, arguments);
    }
    
    /**
     * Term constructor &lambda;&#8728; on propositions.
     * The &lambda;-operator cannot be interpreted truth-functionally but needs structural
     * information about t prior to evaluating that.
     * @see orbital.logic.trs.Substitutions#lambda
     */
    private final Formula createLambdaProp(Symbol x, Formula t) {
	return new LambdaAbstractionFormula(this, x, (Formula)t);
    }

    /**
     * Formulas of the form &lambda;x.t, for functions constructed per &lambda;-abstraction.
     * <p>
     * This class ensures the non-truth-functional interpretation
     * I(&lambda;x.t) := (a&#8614;I&lt;x/a&gt;(t)) = (a&#8614;I(t[x&#8614;a]).
     * It usually uses the first form of semantic modification, instead of the
     * second variant of syntactic substitution (which would require that a is syntactically well-formed).
     * </p>
     * @author Andr&eacute; Platzer
     * @version 2002/07/15
     * @see orbital.logic.trs.Substitutions#lambda
     * @internal note that regarding &forall;x a as &forall(&lambda;x.a) may consume a little more time since one additional composition must be considered for unifications. However, the more systematic concept and simplified (since localized) handling of bindings is worth it.
     */
    private static class LambdaAbstractionFormula extends ModernFormula.AbstractCompositeFormula {
	private Symbol x;
	private Formula term;
	private LambdaAbstractionFormula() {
	    super(null);
	}
	public LambdaAbstractionFormula(Logic logic, Symbol x, Formula term) {
	    super(logic);
	    this.x = x;
	    this.term = term;
	}

	// identical to @see orbital.logic.functor.Functionals.BinaryCompositeFunction
	public Object getCompositor() {
	    //@internal this trick will allow LambdaAbstractionFormulas to unify. null does not unify anything.
	    return LogicFunctions.lambda;
	} 
	public Object getComponent() {
	    return new /*@xxx which type? Formula*/Object[] {
		x, term
	    };
	} 

	public void setCompositor(Object compositor) {
	    if (compositor != getCompositor())
		throw new IllegalArgumentException("special compositor of " + getClass() + " expected");
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    Object[] a = (/*@xxx which type? Formula*/Object[]) g;
	    if (a.length != 2)
		throw new IllegalArgumentException(Object.class + "[2] expected");
	    setUnderlyingLogicLikeIn((Formula)a[1]);
	    this.x = (Symbol)a[0];
	    this.term = (Formula)a[1];
	}
	public Notation getNotation() {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	public void setNotation(Notation notation) {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	    
	// implementation of orbital.logic.imp.Expression interface
	public Type getType() {
	    return Types.map(x.getType(), term.getType());
	}
        public Signature getSignature() {
	    Signature sigma = new SignatureBase(term.getSignature());
	    sigma.add((Symbol)x);
	    return sigma;
        }

	public Set getFreeVariables() {
	    return Setops.difference(term.getFreeVariables(), Collections.singleton(x));
	}

	public Set getBoundVariables() {
	    return Setops.union(term.getBoundVariables(), Collections.singleton(x));
	}

	public Set getVariables() {
	    return Setops.union(term.getVariables(), Collections.singleton(x));
	}

	public Object apply(Object i) {
	    final Interpretation I = (Interpretation)i;
	    // return I(&lambda;x.t)
	    return new Function() {
		    public Object apply(Object a) {
			// interpret term in the modification I<x/a> of I.
			Interpretation modification =
			    new InterpretationBase(new SignatureBase(Collections.singleton(x)),
						   Collections.singletonMap(x, a));
			Interpretation modifiedI =
			    new QuickUnitedInterpretation(modification, I);
			logger.log(Level.FINER, "{0}\nper modification <{1}/{2}> in {3}", new Object[] {modifiedI, x, a, term});
			return term.apply(modifiedI);
		    }
		};
	}
	public String toString() {
	    //@todo use notation like all others do. Should we also pretty-print &forall;(&lambda;x.p) as &forall;x p or something?
	    return "(\\" + x + "." + term + ")";
	}
    }

    /**
     * (&Pi;x.term):*&rarr;*
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-11-10
     * @xxx in fact this is not truely a type :* but a constructor? :*->*
     */
    private static class PiAbstractionExpression extends ModernFormula.AbstractCompositeFormula {
	private Symbol x;
	private Formula term;
	private PiAbstractionExpression() {
	    super(null);
	}
	public PiAbstractionExpression(Symbol x, Formula term) {
	    super(null);//@xxx
	    this.x = x;
	    this.term = term;
	    if (term.getType() != Types.TYPE)
		throw new IllegalArgumentException("would not expect type " + term.getType() + " for type expressions @xxx except for other kinds like *->*");
	}

	// identical to @see orbital.logic.functor.Functionals.BinaryCompositeFunction
	public Object getCompositor() {
	    //@internal this trick will allow formulas to unify. null does not unify anything.
	    return LogicFunctions.pi;
	} 
	public Object getComponent() {
	    return new Object[] {
		x, term
	    };
	} 

	public void setCompositor(Object compositor) {
	    if (compositor != getCompositor())
		throw new IllegalArgumentException("special compositor of " + getClass() + " expected");
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    Object[] a = (Object[]) g;
	    if (a.length != 2)
		throw new IllegalArgumentException(Object.class + "[2] expected");
	    this.x = (Symbol)a[0];
	    this.term = (Formula)a[1];
	}
	public Notation getNotation() {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	public void setNotation(Notation notation) {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	    
	// implementation of orbital.logic.imp.Expression interface
	public Type getType() {
	    //@xxx wrong type? And equals *&rarr;*?
	    return Types.map(x.getType(), term.getType());
	}
        public Signature getSignature() {
	    Signature sigma = new SignatureBase(term.getSignature());
	    sigma.add((Symbol)x);
	    return sigma;
        }
	public Set getFreeVariables() {
	    return Setops.difference(term.getFreeVariables(), Collections.singleton(x));
	}

	public Set getBoundVariables() {
	    return Setops.union(term.getBoundVariables(), Collections.singleton(x));
	}

	public Set getVariables() {
	    return Setops.union(term.getVariables(), Collections.singleton(x));
	}

	// return I(&lambda;x.t)
	//@see LambdaAbstractionFormula#apply(Object)
	public Object apply(Object i) {
	    final Interpretation I = (Interpretation)i;
	    // return I(&lambda;x.t)
	    return new PiAbstractionType(x, term, I);
	}

	public String toString() {
	    //@todo use notation like all others do.
	    return "(\\\\" + x + "." + term + ")";
	}
    }

    /**
     * (&Pi;x.term):*&rarr;*
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-11-10
     * @xxx in fact this is not truely a type :* but a constructor? :*->*
     */
    static class PiAbstractionType implements Type, Functor.Composite/**@xxx should be Types.Composite*/ {
	static final Specification callTypeDeclaration = new Specification(new Class[] {Type.class}, Type.class);
	
	private Symbol x;
	private Formula term;
	private Interpretation I;
	private PiAbstractionType() {
	}
	public PiAbstractionType(Symbol x, Formula term, Interpretation I) {
	    this.x = x;
	    this.term = term;
	    this.I = I;
	    if (term.getType() != Types.TYPE)
		throw new IllegalArgumentException("would not expect type " + term.getType() + " for type expressions @xxx except for other kinds like *->*");
	}

	// identical to @see orbital.logic.functor.Functionals.BinaryCompositeFunction
	public Object getCompositor() {
	    //@internal this trick will allow &Pi;-types to unify. null does not unify anything.
	    return LogicFunctions.pi;
	} 
	public Object getComponent() {
	    return new Object[] {
		x, term
	    };
	} 

	public void setCompositor(Object compositor) {
	    if (compositor != getCompositor())
		throw new IllegalArgumentException("special compositor of " + getClass() + " expected");
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    Object[] a = (Object[]) g;
	    if (a.length != 2)
		throw new IllegalArgumentException(Object.class + "[2] expected");
	    this.x = (Symbol)a[0];
	    this.term = (Formula)a[1];
	}
	public orbital.logic.Composite construct(Object f, Object g) {
	    try {
		orbital.logic.Composite c = (orbital.logic.Composite) getClass().newInstance();
		c.setCompositor(f);
		c.setComponent(g);
		return c;
	    }
	    catch (InstantiationException ass) {
		throw (UnsupportedOperationException) new UnsupportedOperationException("invariant: sub classes of " + Functor.Composite.class + " must either support nullary constructor for modification cloning or overwrite newInstance(Functor.Composite,Object)").initCause(ass);
	    }
	    catch (IllegalAccessException ass) {
		throw (UnsupportedOperationException) new UnsupportedOperationException("invariant: sub classes of " + Functor.Composite.class + " must either support nullary constructor for modification cloning or overwrite newInstance(Functor.Composite,Object)").initCause(ass);
	    }
	}
	public Notation getNotation() {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	public void setNotation(Notation notation) {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	    
	// identical to @see orbital.logic.functor.Function.Composite.Abstract
	/**
	 * Checks for equality.
	 * Two CompositeFunctors are equal iff their classes,
	 * their compositors and their components are equal.
	 */
	public boolean equals(Object o) {
	    if (o == null || getClass() != o.getClass())
		return false;
	    // note that it does not matter to which .Composite we cast since we have already checked for class equality
	    Functor.Composite b = (Functor.Composite) o;
	    return Utility.equals(getCompositor(), b.getCompositor())
		&& Utility.equalsAll(getComponent(), b.getComponent());
	}

	public int hashCode() {
	    return Utility.hashCode(getCompositor()) ^ Utility.hashCodeAll(getComponent());
	}
	

	public boolean apply(Object o) {
	    throw new UnsupportedOperationException();
	}

	public Type domain() {
	    //@xxx sure?
	    return x.getType();
	}
	public Type codomain() {
	    return term.getType();
	}
	public int compareTo(Object tau) {
	    //@xxx
	    if (equals(tau))
		return 0;
	    else if (tau == Types.UNIVERSAL)
		return -1;
	    else if (tau == Types.ABSURD)
		return 1;
	    else
		throw new IncomparableException(this + " compared to " + tau);
	}
	public boolean subtypeOf(Type tau) {
	    //@xxx
	    if (equals(tau))
		return true;
	    else if (tau == Types.UNIVERSAL)
		return true;
	    else if (tau == Types.ABSURD)
		return false;
	    else
		//@xxx throw new UnsupportedOperationException(this + " =< " + tau);
		return false;
	}

	// return (&Pi;x.t)(a) = t[x&rarr;a]
	//@see LambdaAbstractionFormula#apply(Object)
	public Type apply(Type a) {
	    // interpret term in the modification I<x/a> of I.
	    Interpretation modification =
		new InterpretationBase(new SignatureBase(Collections.singleton(x)),
				       Collections.singletonMap(x, a));
	    Interpretation modifiedI =
		new QuickUnitedInterpretation(modification, I);
	    logger.log(Level.FINER, "{0}\nper modification <{1}/{2}> in {3}", new Object[] {modifiedI, x, a, term});
	    return (Type)/*LogicParser.asType((Expression)*/( term.apply(modifiedI));
	}

	public String toString() {
	    //@todo use notation like all others do.
	    return "(\\\\" + x + "." + term + ")";
	}
    }

    /**
     * &Pi;-application is in fact only a type conversion expression.
     * For a &Pi;-abstraction &Pi;x:*.t, when given a type &alpha;:<span class="type">*</span>
     * this conversion has the type
     * (&Pi;x:*.t) &rarr; (&Pi;x:*.t)(&alpha;) = t[x&#8614;&alpha;]
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-11-19
     * @internal currently this is only type conversion and has nothing to do with the particular task of &Pi;-application.
     * @internal note that this is (almost) like ModernFormula.FixedAtomicSymbol(logic, new SymbolBase("<to " + applied + ">",Types.map(abstraction, applied),null,false), Functions.id, false). Apart from getSignature and equals/hashCode.
     */
    private static class PiApplicationExpression extends ModernFormula {
	private PiAbstractionType abstraction;
	/**
	 * applied = (&Pi;x:*.t)(&alpha;) = t[x&#8614;&alpha;]
	 */
	private Type applied;
	private PiApplicationExpression() {
	    super(null);
	}
	public PiApplicationExpression(PiAbstractionType abstraction, Type applied) {
	    super(null);//@xxx
	    this.abstraction = abstraction;
	    this.applied = applied;
	}

	// identical to @see orbital.logic.functor.Function.Composite.Abstract
	/**
	 * Checks for equality.
	 * Two CompositeFunctors are equal iff their classes,
	 * their compositors and their components are equal.
	 */
	public boolean equals(Object o) {
	    if (o == null || getClass() != o.getClass())
		return false;
	    PiApplicationExpression b = (PiApplicationExpression)o;
	    return Utility.equals(applied, b.applied)
		&& Utility.equals(abstraction, b.abstraction);
	}

	public int hashCode() {
	    return Utility.hashCode(applied) ^ Utility.hashCode(abstraction);
	}
	
	// implementation of orbital.logic.imp.Expression interface
	public Type getType() {
	    return Types.map(abstraction, applied);
	}
        public Signature getSignature() {
	    return SignatureBase.EMPTY;
        }
	public Set getFreeVariables() {
	    return Collections.EMPTY_SET;
	}

	public Set getBoundVariables() {
	    return Collections.EMPTY_SET;
	}

	public Set getVariables() {
	    return Collections.EMPTY_SET;
	}

	public Object apply(Object i) {
	    final Interpretation I = (Interpretation)i;
	    return Functions.id;
	}

	public String toString() {
	    if (Logger.global.isLoggable(Level.FINEST))
		return "<to " + applied + ">";
	    else
		return "";
	}
    }

    // Logic implementation

    public boolean satisfy(Interpretation I, Formula F) {
	// avoid accepting null formulas, here
	if (F == null)
	    throw new NullPointerException("null is not a formula");
	assert F instanceof ModernFormula && getClass().isInstance(((ModernFormula)F).getUnderlyingLogic()) : "F is a formula in this logic";
        // assure core interpretation @xxx may be unnecessary due to fixed interpretation of core signature
        I = new QuickUnitedInterpretation(ClassicalLogic._coreInterpretation, I);
	return ((Boolean) F.apply(I)).booleanValue();
    } 

    public Inference inference() {
	return getInferenceMechanism().inference();
    } 
    
    public Signature coreSignature() {
	return _coreSignature;
    } 
    public Interpretation coreInterpretation() {
	return _coreInterpretation;
    }



    // enum of inference mechanisms @internal this must be below initialization of coreSignature since Resolution needs it.
    /**
     * Semantic inference with truth-tables.
     */
    public static final InferenceMechanism SEMANTIC_INFERENCE = new InferenceMechanism("SEMANTIC_INFERENCE") {
	    /**
	     * @attribute stateless
	     */
	    private final Inference _semanticInference = new Inference() {
		    public boolean infer(Formula[] B, Formula D) {
			Signature sigma = D.getSignature();
			// sigma limited to the (free) part that really affects semantic inference
			Signature sigmaInt = relevantSignatureOf(D);
			for (int i = 0; i < B.length; i++) {
			    sigma = sigma.union(B[i].getSignature());
			    sigmaInt = sigmaInt.union(relevantSignatureOf(B[i]));
			}
			// semantic test whether all interpretations that satisfy all formulas in B, also satisfy D
			loop:
			for (Iterator Int = createAllInterpretations(sigmaInt, sigma);
			     Int.hasNext();
			     ) {
			    Interpretation I = (Interpretation) Int.next();
			    // I |= B is defined as &forall;F&isin;B: I |= F
			    for (int b = 0; b < B.length; b++)
				if (!B[b].apply(I).equals(Boolean.TRUE)) //if (!satisfy(I, B[b]))
				    continue loop;
			    if (!D.apply(I).equals(Boolean.TRUE)) //if (!satisfy(I, D))
				return false;
			}
			return true;
		    }
		    /**
		     * Limits a signature by removing bound (-only) variables that do not affect
		     * semantic inference, anyway.
		     */
		    private Signature relevantSignatureOf(Formula F) {
			Collection boundOnly = new HashSet(F.getBoundVariables());
			boundOnly.removeAll(F.getFreeVariables());
			Signature sig = new SignatureBase(F.getSignature());
			sig.removeAll(boundOnly);
			return sig;
		    }
		    public boolean isSound() {
			return true;
		    } 
		    public boolean isComplete() {
			return true;
		    } 
		};
	    Inference inference() {
		return _semanticInference;
	    }
	};
    /**
     * Resolution inference.
     * Inference mechanism driven by full first-order resolution.
     */
    public static final InferenceMechanism RESOLUTION_INFERENCE = new InferenceMechanism("RESOLUTION") {
	    private final Inference _resolution = new Resolution();
	    Inference inference() {
		return _resolution;
	    }
	};

    /**
     * Get all possible &Sigma;-Interpretations associating
     * the symbols in &Sigma; with elements of the world.
     * Interpretations are conceptually irrelevant for syntactic calculi of inference relations
     * but may optionally be used to implement a naive calculus.
     * @pre propositionalSigma&sube;sigma &and; propositionalSigma is only a signature of propositional logic
     * @param sigma the full declared signature of the interpretations to create.
     * @param propositionalSigma the part of the signature for which to create all interpretations.
     * @return all &Sigma;-Interpretations in this Logic (i.e. that can be formed with Signature &Sigma;).
     * @xxx somehow in a formula like (\x. x>2)(7) the numbers 2, and 7 are also subject to interpretation by true or false.
     */
    private static Iterator/*_<Interpretation>_*/ createAllInterpretations(Signature propositionalSigma, final Signature sigma) {
	assert sigma != null && propositionalSigma != null : "signatures are !=null: " + propositionalSigma + ", " + sigma;
	assert sigma.containsAll(propositionalSigma) : propositionalSigma + " subset of " + sigma;
	// determine the non-fixed propositional part of propositionalSigma
	final Signature sigmaComb = new SignatureBase(propositionalSigma);
	for (Iterator it = sigmaComb.iterator(); it.hasNext(); ) {
	    final Symbol s = (Symbol)it.next();
	    final Type type = s.getType();
	    if (type.equals(Types.TRUTH))
		// ordinary propositional logic
		;
	    else if (!s.isVariable() && type.subtypeOf(Types.objectType(orbital.math.Scalar.class)))
	    	// forget about interpreting _fixed_ constants @xxx generalize concept
	    	it.remove();
	    else
		throw new IllegalArgumentException("a signature of propositional logic should not contain " + s + " of type " + type);
	}

	// interpret sigmaComb in all possible ways
	return new Iterator() {
		final Combinatorical comb = Combinatorical.getPermutations(sigmaComb.size(), 2, true);
		public Object next()
		{
		    // although I is a sigma-interpretation, it only interprets sigmaComb
		    Interpretation I = new InterpretationBase(sigma, new HashMap());
		    Iterator	   it = sigmaComb.iterator();
		    int[] c = comb.next();
		    for (int s = 0; it.hasNext(); s++)
			I.put(it.next(), c[s] == 0 ? Boolean.FALSE : Boolean.TRUE);
		    return I;
		}

		public boolean hasNext()
		{
		    return comb.hasNext();
		}

		public void remove()
		{
		    throw new UnsupportedOperationException();
		}
	    };
    } 




    /**
     * Formula transformation utilities.
     * @stereotype &laquo;Utilities&raquo;
     * @stereotype &laquo;Module&raquo;
     * @version 1.0, 1999/01/16
     * @author  Andr&eacute; Platzer
     * @see orbital.util.Utility
     */
    public static final class Utilities {
	private static final ClassicalLogic logic = new ClassicalLogic();
	/**
	 * prevent instantiation - module class.
	 */
	private Utilities() {}
    
	/**
	 * Transforms into disjunctive normal form (DNF).
	 * <p>
	 * This TRS terminates but is not confluent.
	 * </p>
	 * <p>
	 * Note that the transformation into DNF is NP-complete, since the problem
	 * SAT<sub>DNF</sub> of satisfiability in DNF is linear in the length of the formula,
	 * whereas general satisfiability SAT = SAT<sub>CNF</sub> is NP-complete.
	 * Since every formula has an equivalent in DNF the transformation itself must be NP-complete.
	 * </p>
	 * @see "Rolf Socher-Ambrosius. Boolean algebra admits no convergent term rewriting system, Springer Lecture Notes in Computer Science 488, RTA '91."
	 * @internal see mathematische Berechnungstheorie vermittelt, daß es nicht immer möglich ist, mit einer endlichen Folge von Transformationen je zwei beliebig gewählte Ausdrücke in ihre Normalform zu überführen.
	 * @todo Sollten DNF/KNF von "innen nach außen" erstellt werden?
	 * @pre true
	 * @post RES &equiv; f
	 */
	public static Formula disjunctiveForm(Formula f) {
	    return disjunctiveForm(f, false);
	}
	public static Formula disjunctiveForm(Formula f, boolean simplifying) {
	    try {
		// eliminate derived junctors not in the basis (&forall;,&and;,&or;&not;)
		if (DNFeliminate == null)
		    DNFeliminate = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/trs/DNF/eliminate.trs")), logic);
		f = (Formula) Functionals.fixedPoint(DNFeliminate, f);
		// simplification part (necessary and does not disturb local confluency?)
		if (simplifying && DNFSimplification == null)
		    DNFSimplification =
			Setops.union(
				     readTRS(new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/trs/DNF/simplify.trs")), logic).getReplacements(),
				     Arrays.asList(new Object[] {
					 // necessary and does not disturb local confluency? conditional commutative (according to lexical order)
					 new LexicalConditionalUnifyingMatcher(logic.createExpression("_X2&_X1"), logic.createExpression("_X1&_X2"), logic.createAtomicLiteralVariable("_X1"), logic.createAtomicLiteralVariable("_X2")),
					 // necessary and does not disturb local confluency? conditional associative (according to lexical order)
					 //@todo
				     }));
		// transform to DNF part
		if (DNFtrs == null)
		    DNFtrs = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/trs/DNF/transformToDNF.trs")), logic);
		//@todo simplifying conditional rules: commutative with lexical sort, etc.
		return (Formula) Functionals.fixedPoint(simplifying ? Substitutions.getInstance(new ArrayList(Setops.union(DNFSimplification, DNFtrs.getReplacements()))) : DNFtrs, f);
	    } catch (ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}
	// lazy initialized cache for TRS rules
	private static Substitution DNFeliminate;
	private static Substitution DNFtrs;
	private static Collection DNFSimplification;

	/**
	 * Transforms into conjunctive normal form (CNF).
	 * <p>
	 * This TRS terminates but is not confluent.</p>
	 * @todo verify
	 * @pre true
	 * @post RES &equiv; f
	 * @todo ~(a|a) == ~a&~a instead of == ~a somehow because of pattern matching
	 */
	public static Formula conjunctiveForm(Formula f) {
	    return conjunctiveForm(f, false);
	}
	public static Formula conjunctiveForm(Formula f, boolean simplifying) {
	    try {
		// eliminate derived junctors not in the basis (&forall;,&and;,&or;&not;)
		if (CNFeliminate == null)
		    CNFeliminate = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/trs/CNF/eliminate.trs")), logic);
		f = (Formula) Functionals.fixedPoint(CNFeliminate, f);
		// simplification part (necessary and does not disturb local confluency?)
		if (simplifying && CNFSimplification == null)
		    CNFSimplification =
			Setops.union(
				     readTRS(new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/trs/CNF/simplify.trs")), logic).getReplacements(), 
				     Arrays.asList(new Object[] {
					 // necessary and does not disturb local confluency? conditional commutative (according to lexical order)
					 new LexicalConditionalUnifyingMatcher(logic.createExpression("_X2&_X1"), logic.createExpression("_X1&_X2"), logic.createAtomicLiteralVariable("_X1"), logic.createAtomicLiteralVariable("_X2")),

					 // necessary and does not disturb local confluency? right associative
					 //@xxx for CNF infinite recursion for (a&b)<->(b&a) and a<->b<->c. this is because conditional commutative and right-associative oscillate, then
					 //Substitutions.createSingleSidedMatcher(logic.createExpression("(_X1&_X2)&_X3"), logic.createExpression("_X1&(_X2&_X3)")),
				     }));
		// transform to CNF part
		if (CNFtrs == null)
		    CNFtrs = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/trs/CNF/transformToCNF.trs")), logic);
		return (Formula) Functionals.fixedPoint(simplifying ? Substitutions.getInstance(new ArrayList(Setops.union(CNFSimplification, CNFtrs.getReplacements()))) : CNFtrs, f);
	    } catch (ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}
	// lazy initialized cache for TRS rules
	private static Substitution CNFeliminate;
	private static Substitution CNFtrs;
	private static Collection CNFSimplification;
	
	/**
	 * Transforms into implicative normal form (INF)
	 * @todo introduce Formula implicativeForm(Formula f) as TRS
	 */

	/**
	 * Get the negation normal form of a formula.
	 * <p>
	 * A formula is in a negation normal form if the only negations
	 * are due to literals, i.e. negations may only occur directly
	 * in front of an atom.
	 * </p>
	 * <p>
	 * In order to prevent ill-defined negation normal forms, we will first
	 * get rid of derived junctors like &rarr;,&harr; etc.
	 * </p>
	 */
	public static final Formula negationForm(Formula F) {
	    try {
		// eliminate derived junctors not in the basis (&forall;,&exist;,&and;,&or;&not;)
		if (CNFeliminate == null) conjunctiveForm(logic.createFormula("true"));
		F = (Formula) Functionals.fixedPoint(CNFeliminate, F);
		// negation normal form transform TRS
		if (NegationNFTransform == null)
		    NegationNFTransform = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/trs/negationNF.trs")), logic);
		return (Formula) Functionals.fixedPoint(NegationNFTransform, F);
	    } catch (ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}

	// lazy initialized cache for TRS rules
	private static Substitution NegationNFTransform;

	/**
	 * Drop any quantifiers.
	 * Will simply remove every quantifier from F.
	 */
	public static final Formula dropQuantifiers(Formula F) {
	    try {
		// quantifier drop transform TRS
		if (QuantifierDropTransform == null) QuantifierDropTransform = Substitutions.getInstance(Arrays.asList(new Object[] {
		    //@xxx note that A should be a metavariable for a formula
		    Substitutions.createSingleSidedMatcher(logic.createExpression("°_X1 _A"), logic.createExpression("_A")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("?_X1 _A"), logic.createExpression("_A")),
		}));
		return (Formula) Functionals.fixedPoint(QuantifierDropTransform, F);
	    } catch (ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}

	// lazy initialized cache for TRS rules
	private static Substitution QuantifierDropTransform;

	/**
	 * Get the Skolem normal form of a formula.
	 * <p>
	 * <em>After</em> transforming F into negation normal form,
	 * a Skolem normal form can be constructed per
	 * <ul>
	 *   <li>sk(A) = A if A is a literal</li>
	 *   <li>sk(A&and;B) = sk(A)&and;sk(B)</li>
	 *   <li>sk(A&or;B) = sk(A)&or;sk(B)</li>
	 *   <li>sk(&forall;x A) = &forall;x sk(A)</li>
	 *   <li>sk(&exist;x A) = sk(A[x&rarr;f(x<sub>1</sub>,...,x<sub>n</sub>)]) where FV(&exist;x A) = {x<sub>1</sub>,...,x<sub>n</sub>}</li>
	 *   <!-- @todo how "meta" is the following, or would it simply work? -->
	 *   <li>sk(&exist;&lambda;x A) = sk((&lambda;x A)(f(x<sub>1</sub>,...,x<sub>n</sub>))) where FV(&exist;x A) = {x<sub>1</sub>,...,x<sub>n</sub>}</li>
	 * </ul>
	 * </p>
	 * This method will call {@link #negationForm(Formula)}.
	 */
	public static final Formula skolemForm(Formula F) {
	    // transform to negation normal form
	    F = negationForm(F);
	    try {
		// skolem transform TRS
		if (SkolemTransform == null) SkolemTransform = Substitutions.getInstance(Arrays.asList(new Object[] {
		    //@xxx note that A should be a metavariable for a formula
		    new SkolemizingUnifyingMatcher(logic.createExpression("?_X1 _A"),
						   logic.createExpression("_A"),
						   //@internal note that _X1 should have the same type as in ?_X1 _A above
						   new SymbolBase("_X1", Types.INDIVIDUAL, null, true)
						   )
		}));
		return (Formula) Functionals.fixedPoint(SkolemTransform, F);
	    } catch (ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}
	// lazy initialized cache for TRS rules
	private static Substitution SkolemTransform;

	/**
	 * Unifying matcher that skolemizes (existentially quantified) variables
	 * into functions of the free variables.
	 * <p>
	 * Will match with unification, and replace (with unifier applied),
	 * but afterwards skolemize the given variable away.
	 * </p>
	 *
	 * @version 0.9, 2001/07/14
	 * @author  Andr&eacute; Platzer
	 * @todo could also skolemize second-order quantified predicates
	 */
	private static class SkolemizingUnifyingMatcher extends UnifyingMatcher {
	    private static final long serialVersionUID = 5595771241916973901L;
	    private Object skolemizedVariable;
	    /**
	     * Unifying matcher that skolemizes.
	     */
	    public SkolemizingUnifyingMatcher(Object pattern, Object substitute, Object skolemizedVariable) {
		super(pattern, substitute);
		this.skolemizedVariable = skolemizedVariable;
	    }

	    public Object replace(final Object t) {
		final Object r = super.replace(t);
		final Object x = getUnifier().apply(skolemizedVariable);
		// now substitute "[x->s(FV(t))]"
		final Set freeVariables = ((Formula)t).getFreeVariables();
		final Type skolemType;
		{
		    Type arguments[] = new Type[freeVariables.size()];
		    Arrays.fill(arguments, Types.INDIVIDUAL);
		    skolemType = Types.map(Types.product(arguments), Types.INDIVIDUAL);
		}
		final Symbol skolemFunctionSymbol = new UniqueSymbol("s", skolemType, null, false);

		// build expression form
		try {
		    Expression[] freeVariableExpressions = new Expression[freeVariables.size()];
		    Iterator it = freeVariables.iterator();
		    for (int i = 0; i < freeVariables.size(); i++)
			freeVariableExpressions[i] = logic.createAtomic((Symbol)it.next());
		    assert !it.hasNext();
		    // expression form of s(FV(t))
		    Expression applied_s = logic.compose(logic.createAtomic(skolemFunctionSymbol),
							 freeVariableExpressions);

		    // really substitute "[x->s(FV(t))]"
		    assert x instanceof Symbol : "assuming that ?_X1 _A has a Symbol as left component (not the corresponding atomic formula)";
		    Substitution skolemizer = Substitutions.getInstance(Arrays.asList(new Object[] {
			Substitutions.createExactMatcher(logic.createAtomic((Symbol)x), applied_s)
		    }));

		    if (logger.isLoggable(Level.FINEST))
			logger.log(Level.FINEST, "skolemForm( {0} ) = {1} by {2} due to skolem variable={3} and FV={4}. Initially matched expression to skolemize by {5}", new Object[] {t, skolemizer.apply(r), skolemizer, x, freeVariables, getUnifier()});
		    return skolemizer.apply(r);
		} catch (ParseException ex) {
		    throw (InternalError) new InternalError("Unexpected syntax in internal term construction").initCause(ex);
		}
	    }
	}

	// closure operators for free variables
	
	/**
	 * Get the &forall;-closure of a formula.
	 * @param F the formula having free variables FV(F)=:{<var>x<sub>1</sub></var>,...,<var>x<sub>n</sub></var>}.
	 * @return the universal closure
	 *  Cl<sub>&forall;</sub>F = &forall;<var>x<sub>1</sub></var>...&forall;<var>x<sub>n</sub></var> F
	 * @post RES.getFreeVariables()=&empty; &and; RES=F.getFreeVariables()->iterate(x;G=F;G=G.forall(x))
	 * @see #existentialClosure(Formula)
	 * @see #constantClosure(Formula)
	 */
	public static final Formula universalClosure(Formula F) {
	    //@todo rewrite pure functional foldLeft
	    for (Iterator i = F.getFreeVariables().iterator();
		 i.hasNext();
		 )
		F = F.forall((Symbol)i.next());
	    return F;
	}

	/**
	 * Get the &exist;-closure of a formula.
	 * @param F the formula having free variables FV(F)=:{<var>x<sub>1</sub></var>,...,<var>x<sub>n</sub></var>}.
	 * @return the existential closure
	 *  Cl<sub>&exist;</sub>F = &exist;<var>x<sub>1</sub></var>...&exist;<var>x<sub>n</sub></var> F
	 * @post RES.getFreeVariables()=&empty; &and; RES=F.getFreeVariables()->iterate(x;G=F;G=G.exist(x))
	 * @see #universalClosure(Formula)
	 * @see #constantClosure(Formula)
	 */
	public static final Formula existentialClosure(Formula F) {
	    //@todo rewrite pure functional foldLeft
	    for (Iterator i = F.getFreeVariables().iterator();
		 i.hasNext();
		 )
		F = F.exists((Symbol)i.next());
	    return F;
	}

	/**
	 * Get the constant-closure of a formula.
	 * <p>
	 * This method replaces all free variables by constants of the same signifier, type and notation.
	 * Especially, in combination with other formulas of the same free variables, the closure constants
	 * will be the same, unlike any skolem constants introduced in the &exists;-closures of those
	 * formulas.
	 * </p>
	 * @param F the formula having free variables FV(F)=:{<var>x<sub>1</sub></var>,...,<var>x<sub>n</sub></var>}.
	 * @return the constant closure
	 *  Cl<sub>const</sub>F = F[<var>x<sub>1</sub></var>&rarr;x<sub>1</sub>,...,<var>x<sub>n</sub></var>&rarr;x<sub>n</sub>]
	 * @post RES.getFreeVariables()=&empty; &and; RES=...
	 * @see #existentialClosure(Formula)
	 * @see #universalClosure(Formula)
	 */
	public static final Formula constantClosure(Formula F) {
	    return (Formula) Substitutions.getInstance(Functionals.map(new Function/*<Symbol,Matcher>*/() {
		    public Object apply(Object o) {
			Symbol x = (Symbol)o;
			assert x.isVariable() : "FV are free _variables_";
			Symbol xconst = new SymbolBase(x.getSignifier(),
						       x.getType(),
						       x.getNotation(),
						       false);
			return Substitutions.createExactMatcher(logic.createAtomic(x),
								logic.createAtomic(xconst));
		    }
		}, F.getFreeVariables())).apply(F);
	}
	
	
	/**
	 * Reads a term-rewrite system from a stream.
	 * The syntax is
	 * <pre>
	 * &lt;matchingSide&gt; |- &lt;replacementSide&gt;    # &lt;comment&gt; &lt;EOL&gt;
	 * ...
	 * </pre>
	 * @todo move somewhere and also let Simplification.java use it.
	 */
	private static final Substitution readTRS(Reader reader, ExpressionSyntax syntax) {
	    try {
		return LogicParser.readTRS(reader, syntax);
	    } catch (ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    } catch (IOException ex) {
		throw (RuntimeException) new RuntimeException("error reading " + reader).initCause(ex);
	    }
	}
    }


    // convenience methods
    /**
     * speed up for internal parsing in TRS
     */
    private final Expression createAtomicIndividualVariable(String signifier) {
	return createAtomic(new SymbolBase(signifier, Types.INDIVIDUAL, null, true));
    }
    /**
     * speed up for internal parsing in TRS
     */
    private final Expression createAtomicLiteralVariable(String signifier) {
	return createAtomic(new SymbolBase(signifier, Types.TRUTH, null, true));
    }
    /**
     * speed up for internal parsing in TRS
     */
    private final Expression createAtomicLiteral(String signifier) {
	return createAtomic(new SymbolBase(signifier, Types.TRUTH, null, false));
    }

    public Expression[] createAllExpressions(String expressions) throws ParseException {
	if (expressions == null)
	    throw new NullPointerException("null is not an expression");
	final String s = expressions.trim();
	if (s.length() == 0)
	    return new Formula[0];
	else if (!(s.charAt(0) == '{' && s.charAt(s.length()-1) == '}'))
	    //@internal adapt from older syntax
	    expressions = "{" + expressions + "}";
	return super.createAllExpressions(expressions);
    }

    /**
     * Convenience method.
     * @deprecated Use <code>(Formula) createExpression(expression)</code> instead.
     * @todo remove
     */
    public Formula createFormula(String expression) throws ParseException {
	return (Formula) createExpression(expression);
    } 
}
