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
import orbital.logic.sign.*;
import orbital.logic.sign.Expression.Composite;
import orbital.logic.sign.ParseException;
import orbital.logic.sign.type.*;

import orbital.logic.functor.Functor;
import orbital.logic.functor.*;
import orbital.logic.functor.Predicates;
import orbital.logic.trs.*;
import orbital.moon.logic.bridge.SubstitutionImpl.UnifyingMatcher;
import orbital.math.functional.Operations;

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

import orbital.logic.sign.concrete.Notation;
import orbital.logic.sign.concrete.Notation.NotationSpecification;
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
 * @fixme we prove contradictious things with SEMANTIC_INFERENCE:
 *  |= (2+3<5)
 *  NOT|= (2+3<5) | a
 *  |= (2+3<7) | a
 */
public class ClassicalLogic extends ModernLogic {
    private static final boolean PI_SYNTACTICAL_SUBSTITUTION = true;
    /**
     * Maximum number of InferenceMechanism objects (for typesafe enum).
     */
    private static final int MAX_INFERENCE_MECHANISMS = 10;

    private static final Logger logger = Logger.getLogger(ClassicalLogic.class.getName());

    private static final TypeSystem typeSystem = Types.getDefault();

    /**
     * Charset of internal files for tool-main.
     * @internal note reader.read() does not terminate for UTF-16. Seems JDK BugID
     * @internal however our UTF-8 does not start with three strange bytes, as for Notepad.exe.
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * The prefix to the resources.
     */
    private static final String resources = "/orbital/moon/logic/";
    
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
		    System.out.println("\t" + Types.toTypedString(s));
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
		else if (arg[option].startsWith("-charset=")) {
		    charset = arg[option].substring("-charset=".length());
		    System.out.println("using charset " + charset);
		} else if (arg[option].startsWith("-inference=")) {
		    String mechanismDescription = arg[option].substring("-inference=".length()).toUpperCase();
		    try {
			InferenceMechanism mechanism = (InferenceMechanism) logic.getClass().getField(mechanismDescription).get(null);
			logic.setInferenceMechanism(mechanism);
			System.out.println("Using " + mechanism);
		    }
		    catch (NoSuchFieldException ex) {
			System.err.println("illegal inference mechanism " + mechanismDescription);
			throw ex;
		    }
		    catch (SecurityException ex) {
			System.err.println("could not access inference mechanism " + mechanismDescription);
			throw ex;
		    }
		    catch (IllegalAccessException ex) {
			System.err.println("inaccessible inference mechanism " + mechanismDescription);
			throw ex;
		    }
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
				throw new AssertionError("none of the cases of which one occurs is true");
			    rd = new InputStreamReader(logic.getClass().getResourceAsStream(resources + resName), DEFAULT_CHARSET);
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
    public static final String usage = "usage: [options] [all|none|properties|fol|<filename>|table]\n\tall\tprove important semantic-equivalence expressions\n\tnone\ttry to prove some semantic-garbage expressions\n\tproperties\tprove some properties of classical logic inference relation\n\tfol\tprove important equivalences of first-order logic\n\n\t<filename>\ttry to prove all expressions in the given file\n\ttable\tprint a function table of the expression instead\n\t-\tUse no arguments at all to be asked for expressions\n\t\tto prove.\noptions:\n\t-normalForm\tcheck the conjunctive and disjunctive forms in between\n\t-closure\tprint the universal/existential closures in between\n\t-inference=<inference_mechanism>\tuse ClassicalLogic.<inference_mechanism> instead of semantic inference\n\t-verbose\tbe more verbose (f.ex. print normal forms if -normalForm)\n\t-charset=<encoding>\tthe character set or encoding to use for reading files\n\nTo check whether A and B are equivalent, enter '|= A<->B'";


    
    // classical logic
    
    /**
     * Specifies the nference mechanism applied for the {@link ClassicalLogic#inference() inference relation}.
     * @version 1.1, 2002-09-14
     * @author  Andr&eacute; Platzer
     * @see <a href="{@docRoot}/Patterns/Design/enum.html">typesafe enum pattern</a>
     * @internal typesafe enumeration pattern class to specify fuzzy logic operators
     * @invariants a.equals(b) &hArr; a==b
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
	 * @postconditions consistent with equals
	 */
	public int compareTo(Object o) {
	    return ordinal - ((InferenceMechanism) o).ordinal;
	} 

	/**
	 * Maintains the guarantee that all equal objects of the enumerated type are also identical.
	 * @postconditions a.equals(b) &hArr; if a==b.
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
	Object readResolve() throws ObjectStreamException {
	    // canonicalize
	    return values[ordinal];
	} 

	/**
	 * Defines the inference mechanism used.
	 * @postconditions RES==OLD(RES)
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
    /**
     * Create a classical logic with the specified inference mechanism.
     * @param inferenceMechanism the inference mechanism applied for the {@link #inference() inference relation}.
     * @see #setInferenceMechanism(InferenceMechanism)
     */
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

    /**
     * Specifies special &lambda; notation <code>"\x.t"</code>.
     */
    private static final Notation NOTATION_LAMBDA = new Notation("lambda") {
	    //private static final long serialVersionUID = 0
	    public String format(Object compositor, Object arg) {
		StringBuffer sb = new StringBuffer();
		if (compositor != null) {
		    if (compositor instanceof Composite)
			// descend into composite compositors. will receive brackets, automatically, since !hasCompactBrackets("")
			sb.append(Notation.DEFAULT.format("", compositor));
		    else
			sb.append(compositor + "");
		}
		if (arg == null)
		    throw new NullPointerException();
		if (arg != null) {
		    Collection a = Utility.asCollection(arg);
		    if (a.size() != 2)
			throw new IllegalArgumentException("argument of size 2 expected");
		    Iterator i = a.iterator();
		    sb.append(i.next());
		    sb.append('.');
		    sb.append(i.next());
		    assert !i.hasNext() : "size 2";
		} 
		return sb.toString();
	    }
	    /**
	     * Maintains the guarantee that there is only a single object representing each enum constant.
	     * @serialData canonicalized deserialization
	     */
	    private Object readResolve() throws java.io.ObjectStreamException {
		// canonicalize
		return NOTATION_LAMBDA;
	    } 
	};

    //@todo remove this bugfix that replaces "xfy" by "yfy" associativity only for *.jj parsers to work without inefficient right-associative lookahead.
    private static final String xfy = "yfy";
    private static final String typAssoc = "f";  //@xxx should be "fx"?

    //@todo unmodifiable view
    private static final Interpretation _coreInterpretation =
	LogicSupport.arrayToInterpretation(new Object[][] {
	    /**
	     * Contains (usually ordered) map (in precedence order) of initial functors
	     * and their notation specifications.
	     * Stored internally as an array of length-2 arrays.
	     * @invariants sorted, i.e. precedenceOf[i] < precedenceOf[i+1]
	     */
	    {typeSystem.UNIVERSAL(),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},
	    {typeSystem.objectType(java.lang.Boolean.class, "truth"),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},
	    {typeSystem.objectType(java.lang.Object.class, "individual"),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},
	    {typeSystem.objectType(orbital.math.Integer.class, "integer"),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},
	    {typeSystem.objectType(orbital.math.Real.class, "real"),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},
	    {typeSystem.objectType(String.class, "string"),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},

	    {typeSystem.list(),
	     new NotationSpecification(500, "fx", Notation.PREFIX)},
	    {typeSystem.set(),
	     new NotationSpecification(500, "fx", Notation.PREFIX)},
	    //@fixme debug why we print (s->truth)->truth as s->truth->truth
	    {typeSystem.map(),
	     new NotationSpecification(500, "xfx", Notation.INFIX)},
	    {typeSystem.product(),
	     new NotationSpecification(500, "fx", Notation.PREFIX)},

	    {Operations.plus, null},
	    {Operations.minus, null},
	    {Operations.subtract, null},
	    {Operations.times, null},
	    {Operations.inverse, null},
	    {Operations.divide, null},
	    //	    {Operations.power, null},  //@xxx confuses xor

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

	    {LogicFunctions.forall,       // "°"
	     new NotationSpecification(900, "fy", Notation.PREFIX)},
	    {LogicFunctions.exists,       // "?"
	     new NotationSpecification(900, "fy", Notation.PREFIX)},
	    //@internal &lambda; is the only non-functional operator, both with (single-shot) lazy evaluation and with eager evaluation.
	    {LogicFunctions.lambda,       // "\\"
	      new NotationSpecification(900, "fxy", NOTATION_LAMBDA)},
	    {LogicFunctions.pi,           // "\\\\"
	     new NotationSpecification(900, "fxx", Notation.PREFIX)},

	    {LogicFunctions.not,          // "~"
	     new NotationSpecification(900, "fx", Notation.PREFIX)},
	    {LogicFunctions.and,          // "&"
	     new NotationSpecification(910, xfy, Notation.INFIX)},
	    {LogicFunctions.xor,          // "^"
	     new NotationSpecification(914, xfy, Notation.INFIX)},
	    {LogicFunctions.or,           // "|"
	     new NotationSpecification(916, xfy, Notation.INFIX)},
	    //@fixme (a->b)->c gets formatted as a -> b -> c, just as a->(b->c)
	    {LogicFunctions.impl,         // "->"
	     new NotationSpecification(920, "xfx", Notation.INFIX)},
	    {LogicFunctions.reverseImpl, // "<-"
	     new NotationSpecification(920, "xfx", Notation.INFIX)},
	    {LogicFunctions.equiv,        // "<->"
	     new NotationSpecification(920, xfy, Notation.INFIX)}
	}, false, true, true);
    private static final Signature _coreSignature = _coreInterpretation.getSignature();

    /**
     * Determine the type to pass as parameter to a &Pi;-abstraction, for resulting in a type
     * applicable to the given argument type.
     * Thereby we try to unify the &Pi;-abstracted type term with the actual argument type,
     * successively descending into the domains if necessary.
     * @param argumentType the à priori required application type is
     * the type of the argument passed to the expression of the given
     * &Pi;-abstraction type.
     * @return the application type actually passed as parameter to
     * the &Pi;-abstraction, for accepting the given argument type.
     * @internal we need to unify: For example, if &forall; : (\\s:*.(s&rarr;o)&rarr;o) and if f:i&rarr;o then &forall;(f) : o.
     * @internal try to unify successively with all left prefixes: i.e. first with (s->t)->a then with s->t then with s.
     */
    /*private*/ Type calculateParameterTypeForPiAbstraction(PiAbstractionType piabst, Type argumentType) {
	Formula abstractedType = (Formula) ((Object[])piabst.getComponent())[1];
	while (abstractedType instanceof PiAbstractionExpression) {
	    abstractedType = (Formula) ((Object[])
					((PiAbstractionType)LogicParser.myasType(abstractedType, coreSignature())).getComponent())[1];
	}
	{
	    //@internal already start with domain, since application still needs a result type, since f:s->s not applicable to c:s->s, but only to c:s. 
	    //@todo could be simplified and reunited with identical case in loop below
	    if (abstractedType instanceof Expression.Composite
		  && ((Expression.Composite)abstractedType).getCompositor() == typeSystem.map())
		// move up one domain.
		//@internal Corresponds to abstractedType = abstractedType.domain();
		abstractedType = ((Formula[]) ((Expression.Composite)abstractedType).getComponent())[0];
	    else
		throw new IllegalArgumentException("could not unify argument type " + argumentType + " for Pi-abstraction type " + piabst);
	}
	while (true) {
	    // the required application type
	    final Formula reqApType = (Formula) new TypeToFormula().apply(argumentType);
	    System.err.println("unify " + reqApType + "\n  and " + abstractedType);
	    final Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {
		reqApType,
		abstractedType
	    }));
	    System.err.println("  is " + mu);
	    if (mu != null) {
		// the application type actually passed as parameter to the &Pi;-abstraction.
		final Type parameterApType = LogicParser.myasType((Expression)mu.apply(createAtomic(piabst.getVariable())), coreSignature());
		logger.log(Level.FINEST, "compositor of type {0} applied to the arguments of type{1} (= {2}). Result has 'instantiated' type {3} by parameter {4}.", new Object[] {piabst, argumentType, reqApType, piabst.apply(parameterApType), parameterApType});
		return parameterApType;
	    } if (abstractedType instanceof Expression.Composite
		  && ((Expression.Composite)abstractedType).getCompositor() == typeSystem.map())
		// move up one domain.
		//@internal Corresponds to abstractedType = abstractedType.domain();
		abstractedType = ((Formula[]) ((Expression.Composite)abstractedType).getComponent())[0];
	    else
		throw new IllegalArgumentException("could not unify argument type " + argumentType + " for Pi-abstraction type " + piabst);
	}
    }	

    /**
     * Converts a type to a formula representing a type.
     * @version 1.1, 2003-02-05
     * @author Andr&eacute; Platzer
     * @see LogicParser#asType(Expression)
     */
    class TypeToFormula implements Function {
	public Object apply(Object o) {
	    if (o instanceof Type.Composite) {
		try {
		    Type.Composite c = (Type.Composite)o;
		    return compose(createAtomic(symbolFor(c.getCompositor(),
							  Types.declaredTypeOf((Functor)c.getCompositor()))),
				   (Expression[])
				   ((Collection)
				    Functionals.map(this, Utility.asCollection(c.getComponent())))
				   //@internal first of all Functionals.map has ArrayStoreException. Second, getComponent() does not need to be an array, but can also be a Type.
				   .toArray(new Expression[0])
				   );
		}
		catch (ParseException ex) {
		    throw new InnerCheckedException(ex);
		}
		catch (IntrospectionException ex) {
		    throw new InnerCheckedException(ex);
		}
	    } else
		return createAtomic(symbolFor(o, typeSystem.TYPE()));
	}
	private final Symbol symbolFor(Object o, Type type) {
	    Symbol s = coreSignature().get(o.toString(),type);
	    if (s == null)
		throw new IllegalArgumentException("no such symbol " + o + " : " + type);
	    else
		return s;
	}
    }

    final Type parseTypeExpression(String expression) {
	try {
	    LogicParser parser = new LogicParser(new StringReader(expression));
	    parser.setSyntax(this);
	    return parser.parseType();
	} catch (orbital.moon.logic.ParseException ex) {
	    throw (InternalError) new InternalError("Unexpected syntax in internal term construction")
		.initCause(new ParseException(ex.getMessage() + "\nIn expression: " + expression, ex.currentToken == null ? COMPLEX_ERROR_OFFSET : ex.currentToken.next.beginLine, ex.currentToken == null ? COMPLEX_ERROR_OFFSET : ex.currentToken.next.beginColumn, ex) );
	}
    }
    
    /**
     * Parses single term.
     * @see #createExpression(String)
     */
    public Expression createTerm(String expression) throws ParseException {
	if (expression == null)
	    throw new NullPointerException("null is not an expression");
	try {
	    LogicParser parser = new LogicParser(new StringReader(expression));
	    parser.setSyntax(this);
	    return parser.parseTerm();
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

    // Helper utilities.

    static class LogicFunctions {
        LogicFunctions() {}
    
	private static final Type UNARY_LOGICAL_JUNCTOR = typeSystem.predicate(Types.TRUTH);
	private static final Type BINARY_LOGICAL_JUNCTOR = typeSystem.predicate(typeSystem.product(new Type[] {Types.TRUTH, Types.TRUTH}));

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
    	public static final BinaryFunction reverseImpl = new BinaryFunction() {
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
	    //private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.product(new Type[] {Types.INDIVIDUAL, Types.TRUTH}), Types.TRUTH);
	    private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.map(Types.INDIVIDUAL, Types.TRUTH), Types.TRUTH);
	    public Object apply(Object f) {
		throw new LogicException("quantified formulas only have a semantic value with respect to a possibly infinite domain. They are available for inference, but they cannot be interpreted with finite means.");
	    }
	    public String toString() { return "°"; }
	};

    	public static final Function exists = new ExistsPlaceholder();
	private static final class ExistsPlaceholder implements Function {
	    //@todo also templatize with t somehow? should be (t->TRUTH)->truth
	    private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.map(Types.INDIVIDUAL, Types.TRUTH), Types.TRUTH);
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
	    //@todo also templatize with t somehow? //@xxx type should be &Pi;s&Pi;t. (s*t->(s->t))
	    //private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.product(new Type[] {typeSystem.UNIVERSAL(), typeSystem.UNIVERSAL()}), typeSystem.map(typeSystem.UNIVERSAL(), typeSystem.UNIVERSAL()));
	    //private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.product(new Type[] {Types.INDIVIDUAL, Types.INDIVIDUAL}), typeSystem.map(Types.INDIVIDUAL, Types.INDIVIDUAL));
	    private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.product(new Type[] {Types.INDIVIDUAL, Types.TRUTH}), typeSystem.map(Types.INDIVIDUAL, Types.TRUTH));
	    public Object apply(Object x, Object t) {
		throw new AssertionError("this method never gets called since lambda cannot be interpreted truth-functionally, but already receives a structural modification in compose(...)");
	    }
	    public String toString() { return "\\"; }
	};
    	public static final BinaryFunction pi = new PiPlaceholder();
	private static final class PiPlaceholder implements BinaryFunction {
	    private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.product(new Type[] {typeSystem.TYPE(), typeSystem.TYPE()}), typeSystem.map(typeSystem.TYPE(), typeSystem.TYPE()));
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
	Expression BOTTOM = Utilities.logic.createAtomic(new SymbolBase("BOTTOM", typeSystem.ABSURD()));
	LAMBDA = _coreSignature.get("\\", new Expression[] {BOTTOM,BOTTOM});
	assert LAMBDA != null : "lambda operator found";
	PI = _coreSignature.get("\\\\", new Expression[] {BOTTOM,BOTTOM}); //@todo
    }
    /**
     * @internal replaces
     *  [(f:&Pi;x.t)(a:&tau;) &rarr; <big>(</big>&lt;to (&Pi;x.t)(&mu;(x))&gt;(f)<big>)</big>(a:&tau;)]
     *  where &mu; = mgU(&tau;,t)
     */
    public Expression.Composite compose(Expression compositor, Expression arguments[]) throws ParseException {
	// handle special cases of term construction, first and before type checking occurs since the type &Pi;-abstractions need more flexibility
	if (compositor.getType() instanceof PiAbstractionType) {
	    final PiAbstractionType piabst = (PiAbstractionType) compositor.getType();
	    // the application type actually passed as parameter to the &Pi;-abstraction.
	    final Type parameterApType = calculateParameterTypeForPiAbstraction(piabst, Types.typeOf(arguments));
	    logger.log(Level.FINEST, "compositor {0} : {1} applied to the {2} arguments {3} : {4}. Result has 'instantiated' type {5} by parameter {6}.", new Object[] {compositor, compositor.getType(), new java.lang.Integer(arguments.length), MathUtilities.format(arguments), Types.typeOf(arguments), piabst.apply(parameterApType), parameterApType});
	    //@todo could we exchange compositor by a formula that only differs in type, and thus avoid conversion formula?
	    Expression typeConv = null;
	    try {
		return super.compose(
				     typeConv = super.compose(new PiApplicationExpression(piabst,
									   piabst.apply(parameterApType)
									   ),
					       new Expression[] {compositor}
					       ),
				 arguments
				 );
	    }
	    finally {
		assert typeConv == null || typeConv.getType().equals(piabst.apply(parameterApType)) : "type conversion " + Types.toTypedString(typeConv) + " leads to expected type " + piabst.apply(parameterApType);
	    }
	}
	return super.compose(compositor, arguments);
    }
    /**
     * @internal special handling of compositors &lambda; and &Pi;.
     */
    Expression.Composite composeImpl(Expression compositor, Expression arguments[]) throws ParseException {
	// handle special cases of term construction, first
	if ((compositor instanceof ModernFormula.FixedAtomicSymbol)
	    && LAMBDA.equals(((ModernFormula.FixedAtomicSymbol)compositor).getSymbol())) {
	    //@todo we provide &lambda;-abstractions by introducing a core symbol LAMBDA that has as fixed interpretation a binary function that ... But of &lambda;(x.t), x will never get interpreted, so it is a bit different than composeFixed(lambda,{x,t}) would suggest. &lambda;-abstraction are not truth-functional!
	    assert arguments.length == 2;
	    assert arguments[0] instanceof ModernFormula.AtomicSymbol : "Symbols when converted to formulas become AtomicSymbols";
	    Symbol x = (Symbol) ((Formula)arguments[0]).getSignature().iterator().next();
	    assert x.isVariable() : "we only form lambda abstractions with respect to variables";
	    return createLambdaProp(x, (Formula) arguments[1]);
	} else if ((compositor instanceof ModernFormula.FixedAtomicSymbol)
	    && PI.equals(((ModernFormula.FixedAtomicSymbol)compositor).getSymbol())) {
	    assert arguments.length == 2;
	    assert arguments[0] instanceof ModernFormula.AtomicSymbol : "Symbols when converted to formulas become AtomicSymbols";
	    Symbol x = (Symbol) ((Formula)arguments[0]).getSignature().iterator().next();
	    assert x.isVariable() : "we only form lambda abstractions with respect to variables";
	    return new PiAbstractionExpression(x, (Formula) arguments[1]);
	} else
	    return super.composeImpl(compositor, arguments);
    }

    // &lambda;

    /**
     * Term constructor &lambda;&#8728; on propositions.
     * The &lambda;-operator cannot be interpreted truth-functionally but needs structural
     * information about t prior to evaluating that.
     * @see orbital.logic.trs.Substitutions#lambda
     */
    private final Formula.Composite createLambdaProp(Symbol x, Formula t) {
	return new LambdaAbstractionFormula(this, x, (Formula)t);
    }

    /**
     * Formulas of the form &lambda;x.t, for functions constructed per &lambda;-abstraction.
     * <p>
     * This class ensures the non-truth-functional interpretation
     * <div>I(&lambda;x.t) := (a &#8614; I&lt;x/a&gt;(t)) = (a &#8614; I(t[x&#8614;a])</div>
     * We use the first form of semantic modification, instead of the
     * second variant of syntactic substitution (which would require
     * that a is syntactically well-formed, c.f. Tarski semantics).
     * </p>
     * @author Andr&eacute; Platzer
     * @version 2002/07/15
     * @see orbital.logic.trs.Substitutions#lambda
     * @note &lambda; has no functional interpretation, regardless of whether eager or lazy evaluation is used.
     * @internal note that regarding &forall;x a as &forall(&lambda;x.a) may consume a little more time since one additional composition must be considered for unifications. However, the more systematic concept and simplified (since localized) handling of bindings is worth it. Also quantifiers are functional, then, and only &lambda; is not.
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

	public orbital.logic.Composite construct(Object f, Object g) {
	    LambdaAbstractionFormula c = new LambdaAbstractionFormula();
	    c.setCompositor(f);
	    c.setComponent(g);
	    return c;
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
	    return Notation.DEFAULT;
	}
	public void setNotation(Notation notation) {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	    
	// implementation of orbital.logic.imp.Expression interface
	public Type getType() {
	    return typeSystem.map(x.getType(), term.getType());
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
	    return getNotation().format(getCompositor(), getComponent());
	}
    }

    // &Pi;

    /**
     * (&Pi;x.term):*&rarr;* type constructor expression.
     * An expression that represents a &Pi;-abstraction and has a canonical interpretation.
     * <p>
     * (&Pi;x.term) is a prototype which only becomes a type by instantiation, i.e.
     * feeding its formal parameter x with a type as an argument.
     * Conceptual realisation of generic types or parametric types.
     * The massive problem arises from: what is the type of the constructor &Pi;?
     * Do we need meta-language, and then meta-meta-language, or not?
     * (&Pi;x.term) in turn is required to type &lambda;.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-11-10
     * @fixme This class captures interpretations of s:* in some cases, like nested Pis.
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
	    if (!orbital.moon.logic.sign.type.StandardTypeSystem.kind.apply(term.getType()))
		//@xxx expected type is not complete
		throw new TypeException("would not expect type " + term.getType() + " for type expressions, but a kind:[]", term.getType().typeSystem().TYPE(), term.getType());
	}

	public orbital.logic.Composite construct(Object f, Object g) {
	    PiAbstractionExpression c = new PiAbstractionExpression();
	    c.setCompositor(f);
	    c.setComponent(g);
	    return c;
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
	    return Notation.DEFAULT;
	}
	public void setNotation(Notation notation) {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	    
	// implementation of orbital.logic.imp.Expression interface
	public Type getType() {
	    //@xxx wrong type? And equals *&rarr;*?
	    return typeSystem.map(x.getType(), term.getType());
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

	/**
	 * @return I(&Pi;x.term)
	 * @see LambdaAbstractionFormula#apply(Object)
	 */
	public Object apply(Object i) {
	    final Interpretation I = (Interpretation)i;
	    // return I(&Pi;x.term)
	    logger.log(Level.FINEST, "{0}\n in {1} leads to {2}", new Object[] {I, term, new PiAbstractionType(x, term, I)});
	    return new PiAbstractionType(x, term, I);
	}

	public String toString() {
	    return getNotation().format(getCompositor(), getComponent());
	}
    }

    /**
     * (&Pi;x.term):*&rarr;* type.
     * @see PiAbstractionExpression
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-11-10
     * @xxx in fact this is not truely a type :* but a constructor? :*->*
     */
    static class PiAbstractionType implements Type.Composite {
	static final Specification callTypeDeclaration = new Specification(new Class[] {Type.class}, Type.class);
	
	private Symbol x;
	private Formula term;
	private Interpretation I;
	private PiAbstractionType() {}
	public PiAbstractionType(Symbol x, Formula term, Interpretation I) {
	    this.x = x;
	    this.term = term;
	    this.I = I;
	    if (!orbital.moon.logic.sign.type.StandardTypeSystem.kind.apply(term.getType()))
		//@xxx expected type is not complete
		throw new TypeException("would not expect type " + term.getType() + " for type expressions, but a kind:[]", term.getType().typeSystem().TYPE(), term.getType());
	}

	public TypeSystem typeSystem() {
	    return term.getType().typeSystem();
	}

	Symbol getVariable() {
	    return x;
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
		throw (UnsupportedOperationException) new UnsupportedOperationException("invariant: sub classes of " + Functor.Composite.class + " must either support nullary constructor for modification cloning or overwrite construct(Object,Object)").initCause(ass);
	    }
	    catch (IllegalAccessException ass) {
		throw (UnsupportedOperationException) new UnsupportedOperationException("invariant: sub classes of " + Functor.Composite.class + " must either support nullary constructor for modification cloning or overwrite construct(Object,Object)").initCause(ass);
	    }
	}
	public Notation getNotation() {
	    return Notation.DEFAULT;
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
	    Type.Composite b = (Type.Composite) o;
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
	    else if (tau == typeSystem().UNIVERSAL())
		return -1;
	    else if (tau == typeSystem().ABSURD())
		return 1;
	    else
		throw new IncomparableException(this + " compared to " + tau);
	}
	public boolean subtypeOf(Type tau) {
	    //@xxx
	    if (equals(tau))
		return true;
	    else if (tau == typeSystem().UNIVERSAL())
		return true;
	    else if (tau == typeSystem().ABSURD())
		return false;
	    else
		//@xxx throw new UnsupportedOperationException(this + " =< " + tau);
		return false;
	}

	public Type on(Type a) {
	    // @return (&Pi;x:*. s->t)(a:*) = t mu
	    // with mu=mgU(s,a)
	    //@xxx is this correct? And what about the implementation?
	    return apply(a);
	}
	/**
	 * @return (&Pi;x:*.t)(a:*) = t[x&rarr;a]
	 * @see LambdaAbstractionFormula#apply(Object)
	 */
	public Type apply(Type a) {
	    if (PI_SYNTACTICAL_SUBSTITUTION) {
		final Expression ta = (Expression) Substitutions.getInstance(Collections.singletonList(
									   Substitutions.createExactMatcher(new ClassicalLogic().createAtomic(x) , new ClassicalLogic().new TypeToFormula().apply(a))
									   ))
		    .apply(term);
		logger.log(Level.FINER, "({0})[{1} -> {2}]\n leads to {3}", new Object[] {term, x, a, ta});
		return LogicParser.myasType(ta, I.getSignature());
	    } else {
		//@todo shouldn't we prefer substitution instead of semantical modification? Would also solve the following
		//@fixme problem of nested &Pi; which invalidate unification on second level
		// interpret term in the modification I<x/a> of I.
		Interpretation modification =
		    new InterpretationBase(new SignatureBase(Collections.singleton(x)),
					   Collections.singletonMap(x, a));
		Interpretation modifiedI =
		    new QuickUnitedInterpretation(modification, I);
		logger.log(Level.FINER, "{0}\nper modification <{1}/{2}> in {3} leads to {4}", new Object[] {modifiedI, x, a, term, term.apply(modifiedI)});
		return (Type)/*LogicParser.asType((Expression)*/( term.apply(modifiedI));
	    }
	}

	public String toString() {
	    return /*I +*/ "[" + getNotation().format(getCompositor(), getComponent()) + "]";
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
     * @internal note that this is (almost) like ModernFormula.FixedAtomicSymbol(logic, new SymbolBase("<to " + applied + ">",typeSystem.map(abstraction, applied),null,false), Functions.id, false). Apart from getSignature and equals/hashCode.
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
	    return typeSystem.map(abstraction, applied);
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
     * <p>
     * This inference mechanism is usually slow, but has the advantage of involving no calculus
     * but directly following the semantics of formulas. Inspite of its bad average performance,
     * it may be superior to other propositional inference mechanisms in pathological cases or
     * cases with a very small number of different propositional atoms and large formulas.
     * </p>
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
     * @attribute computability semi-decidable
     */
    public static final InferenceMechanism RESOLUTION_INFERENCE = new InferenceMechanism("RESOLUTION") {
	    private final Inference _resolution = new orbital.moon.logic.resolution.Resolution();
	    Inference inference() {
		return _resolution;
	    }
	};
    /**
     * Propositional inference.
     * Inference mechanism specialized for fast propositional inference.
     * Currently uses Davis-Putnam-Loveland algorithm.
     * @attribute time complexity CoNP-complete
     */
    public static final InferenceMechanism PROPOSITIONAL_INFERENCE = new InferenceMechanism("PROPOSITIONAL") {
	    private final Inference _propositional = new PropositionalInference();
	    Inference inference() {
		return _propositional;
	    }
	};

    /**
     * Get all possible &Sigma;-Interpretations associating
     * the symbols in &Sigma; with elements of the world.
     * Interpretations are conceptually irrelevant for syntactic calculi of inference relations
     * but may optionally be used to implement a naive calculus.
     * @preconditions propositionalSigma&sube;sigma &and; propositionalSigma is only a signature of propositional logic
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
	    //@see propositionalOnly(Signature)
	    final Symbol s = (Symbol)it.next();
	    final Type type = s.getType();
	    if (type.equals(Types.TRUTH))
		// ordinary propositional logic
		;
	    else if (!s.isVariable() && type.subtypeOf(typeSystem.objectType(orbital.math.Scalar.class)))
	    	// forget about interpreting _fixed_ constants @xxx generalize concept
	    	it.remove();
	    else {
		TypeSystem typeSystem = type.typeSystem();
		throw new TypeException("a signature of propositional logic should not contain " + s + " of type " + type, typeSystem.sup(new Type[] {Types.TRUTH, typeSystem.objectType(orbital.math.Scalar.class)}), type);
	    }
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
     * @stereotype Utilities
     * @stereotype Module
     * @version 1.0, 1999/01/16
     * @author  Andr&eacute; Platzer
     * @see orbital.util.Utility
     * @todo introduce ringForm(Formula) transforming to ring normal from (RNF) over {&and;,xor}
     *  which is for arity n
     *  <b>F</b><sub>2</sub>[X<sub>1</sub>,...,X<sub>n</sub>]/(X<sub>1</sub><sup>2</sup>-X<sub>1</sub>,...,X<sub>n</sub><sup>2</sup>-X<sub>n</sub>)
     * @todo introduce Bryant normal form (alias Shannon/OBDD)
     */
    public static final class Utilities {
	private static final ClassicalLogic logic = new ClassicalLogic();
	/**
	 * prevent instantiation - module class.
	 */
	private Utilities() {}
    
	/**
	 * Checks that a signature only contains propositional logic.
	 * @throws IllegalArgumentException if sigma contains non-propositional first-order logic
	 * @todo improve interface (return instead of throw)
	 */
	static final void propositionalOnly(Signature sigma) {
	    for (Iterator it = sigma.iterator(); it.hasNext(); ) {
		final Symbol s = (Symbol)it.next();
		final Type type = s.getType();
		if (type.equals(Types.TRUTH))
		    // ordinary propositional logic
		    ;
		else if (!s.isVariable() && type.subtypeOf(typeSystem.objectType(orbital.math.Scalar.class)))
		    // forget about interpreting _fixed_ constants @xxx generalize concept
		    ;
		else {
		    TypeSystem typeSystem = type.typeSystem();
		    throw new TypeException("a signature of propositional logic should not contain " + s + " of type " + type, typeSystem.sup(new Type[] {Types.TRUTH, typeSystem.objectType(orbital.math.Scalar.class)}), type);
		}
	    }
	}

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
	 * @preconditions true
	 * @postconditions RES &equiv; f
	 * @attribute time complexity NP-complete
	 */
	public static Formula disjunctiveForm(Formula f) {
	    return disjunctiveForm(f, false);
	}
	public static Formula disjunctiveForm(Formula f, boolean simplifying) {
	    try {
		// eliminate derived junctors not in the basis (&forall;,&and;,&or;&not;)
		if (DNFeliminate == null)
		    DNFeliminate = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream(resources + "trs/DNF/eliminate.trs")), logic);
		f = (Formula) Functionals.fixedPoint(DNFeliminate, f);
		// simplification part (necessary and does not disturb local confluency?)
		if (simplifying && DNFSimplification == null)
		    DNFSimplification =
			Setops.union(
				     readTRS(new InputStreamReader(logic.getClass().getResourceAsStream(resources + "trs/DNF/simplify.trs")), logic).getReplacements(),
				     Arrays.asList(new Object[] {
					 // necessary and does not disturb local confluency? conditional commutative (according to lexical order)
					 new LexicalConditionalUnifyingMatcher(logic.createExpression("_X2&_X1"), logic.createExpression("_X1&_X2"), logic.createAtomicLiteralVariable("_X1"), logic.createAtomicLiteralVariable("_X2")),
					 // necessary and does not disturb local confluency? conditional associative (according to lexical order)
					 //@todo
				     }));
		// transform to DNF part
		if (DNFtrs == null)
		    DNFtrs = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream(resources + "trs/DNF/transformToDNF.trs")), logic);
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
	 * @preconditions true
	 * @postconditions RES &equiv; f
	 * @todo ~(a|a) == ~a&~a instead of == ~a somehow because of pattern matching
	 */
	public static Formula conjunctiveForm(Formula f) {
	    return conjunctiveForm(f, false);
	}
	public static Formula conjunctiveForm(Formula f, boolean simplifying) {
	    try {
		// eliminate derived junctors not in the basis (&forall;,&and;,&or;&not;)
		if (CNFeliminate == null)
		    CNFeliminate = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream(resources + "trs/CNF/eliminate.trs")), logic);
		f = (Formula) Functionals.fixedPoint(CNFeliminate, f);
		// simplification part (necessary and does not disturb local confluency?)
		if (simplifying && CNFSimplification == null)
		    CNFSimplification =
			Setops.union(
				     readTRS(new InputStreamReader(logic.getClass().getResourceAsStream(resources + "trs/CNF/simplify.trs")), logic).getReplacements(), 
				     Arrays.asList(new Object[] {
					 // necessary and does not disturb local confluency? conditional commutative (according to lexical order)
					 new LexicalConditionalUnifyingMatcher(logic.createExpression("_X2&_X1"), logic.createExpression("_X1&_X2"), logic.createAtomicLiteralVariable("_X1"), logic.createAtomicLiteralVariable("_X2")),

					 // necessary and does not disturb local confluency? right associative
					 //@xxx for CNF infinite recursion for (a&b)<->(b&a) and a<->b<->c. this is because conditional commutative and right-associative oscillate, then
					 //Substitutions.createSingleSidedMatcher(logic.createExpression("(_X1&_X2)&_X3"), logic.createExpression("_X1&(_X2&_X3)")),
				     }));
		// transform to CNF part
		if (CNFtrs == null)
		    CNFtrs = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream(resources + "trs/CNF/transformToCNF.trs")), logic);
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
		    NegationNFTransform = readTRS(new InputStreamReader(logic.getClass().getResourceAsStream(resources + "trs/negationNF.trs")), logic);
		return (Formula) Functionals.fixedPoint(NegationNFTransform, F);
	    } catch (ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}

	// lazy initialized cache for TRS rules
	private static Substitution NegationNFTransform;


	// clause and clause set handling
	
	/**
	 * The contradictory clause &empty; &equiv; &#9633; &equiv; &perp;.
	 * <p>
	 * The contradictory set of clauses is {&empty;}={&#9633;}
	 * while the tautological set of clauses is {}.
	 * </p>
	 */
	public static final Set/*_<Formula>_*/ CONTRADICTION = Collections.EMPTY_SET;

	private static final Formula FORMULA_FALSE = (Formula) logic.createAtomic(new SymbolBase("false", Types.TRUTH));
	private static final Formula FORMULA_TRUE = (Formula) logic.createAtomic(new SymbolBase("true", Types.TRUTH));
	
	/**
	 * Transforms into clausal form.
	 * <p>
	 * Defined per structural induction.
	 * </p>
	 * @param simplifying Whether or not to use simplified CNF for calculating clausal forms.
	 * @todo assert
	 * @todo move to orbital.moon.logic.resolution.?
	 */
	public static final Set/*_<Set<Formula>>_*/ clausalForm(Formula f, boolean simplifying) {
	    try {
		return clausalFormClauses(Utilities.conjunctiveForm(f, simplifying));
	    }
	    catch (IllegalArgumentException ex) {
		throw (AssertionError) new AssertionError(ex.getMessage() + " in " + Utilities.conjunctiveForm(f, simplifying) + " of " + f).initCause(ex);
	    }
	}
	/**
	 * Convert a formula (that is in CNF) to a set of clauses.
	 * @preconditions term=conjunctiveForm(term)
	 */
	private static final Set/*_<Set<Formula>>_*/ clausalFormClauses(Formula term) {
	    //@todo assert assume right-associative nesting of &
	    if (term instanceof Composite) {
		Composite f = (Composite) term;
		Object    op = f.getCompositor();
		//@todo could also query ClassicalLogic.LogicFunctions.and etc. from logic.coreInterpretation once
		if (op == ClassicalLogic.LogicFunctions.and) {
		    Formula[] components = (Formula[]) f.getComponent();
		    assert components.length == 2 : "binary " + op + "/" + components.length + " expected";
		    return Setops.union(clausalFormClauses(components[0]), clausalFormClauses(components[1]));
		} else if (op == ClassicalLogic.LogicFunctions.or) {
		    Formula[] components = (Formula[]) f.getComponent();
		    assert components.length == 2 : "binary " + op + "/" + components.length + " expected";
		    Set C = Setops.union(clausalFormClause(components[0]), clausalFormClause(components[1]));
		    return C.contains(FORMULA_TRUE) ? Collections.EMPTY_SET : singleton(C);
		} else if (op == ClassicalLogic.LogicFunctions.not) {
		    Object c = f.getComponent();
		    // evaluate constants
		    if (FORMULA_FALSE.equals(c))
			return clausalFormClauses(FORMULA_TRUE);
		    else if (FORMULA_TRUE.equals(c))
			return clausalFormClauses(FORMULA_FALSE);
		    return singleton(singleton(term));
		} else if (!(op instanceof ModernFormula.AtomicSymbol))
		    throw new IllegalArgumentException("conjunctive normal form should not contain " + op + " of " + op.getClass());
	    }
	    // atomic parts
	    return term.equals(FORMULA_FALSE)
		? singleton(CONTRADICTION)
		: term.equals(FORMULA_TRUE)
		? Collections.EMPTY_SET
		: singleton(singleton(term));
	}
	/**
	 * Convert a disjunction of literals to a single clause.
	 * @return the clause, note that the clause can be further collapsed if it contains true.
	 */
	private static final Set/*_<Formula>_*/ clausalFormClause(Formula term) {
	    if (term instanceof Composite) {
		Composite f = (Composite) term;
		Object    op = f.getCompositor();
		if (op == ClassicalLogic.LogicFunctions.or) {
		    Formula[] components = (Formula[]) f.getComponent();
		    assert components.length == 2 : "binary " + op + "/" + components.length + " expected";
		    return Setops.union(clausalFormClause(components[0]), clausalFormClause(components[1]));
		} else if (op == ClassicalLogic.LogicFunctions.not) {
		    Object c = f.getComponent();
		    // evaluate constants
		    if (FORMULA_FALSE.equals(c))
			return clausalFormClause(FORMULA_TRUE);
		    else if (FORMULA_TRUE.equals(c))
			return clausalFormClause(FORMULA_FALSE);
		    return singleton(term);
		} else if (op == ClassicalLogic.LogicFunctions.and)
		    throw new IllegalArgumentException("(right-associative) conjunctive normal form should not contain " + op + ". Make sure the formula is right-associative for &");
		else if (!(op instanceof ModernFormula.AtomicSymbol))
		    throw new IllegalArgumentException("conjunctive normal form should not contain " + op);
	    }
	    // atomic parts
	    return term.equals(FORMULA_FALSE)
		? CONTRADICTION
		: singleton(term);
	}

	private static final Set singleton(Object o) {
	    Set r = new HashSet();
	    r.add(o);
	    return r;
	}

 	/**
 	 * Get the free variables of a formula represented as a clause.
 	 * @return freeVariables(clause)
 	 * @internal note that for clauses FV(C)=V(C) &and; BV(C)=&empty;
	 * @deprecated Since Orbital 1.2 use {@link orbital.moon.logic.resolution.Clause#getFreeVariables()} instead.
 	 */
 	static final Signature clausalFreeVariables(Set/*_<Formula>_*/ clause) {
 	    // return banana (|&empty;,&cup;|) (map ((&lambda;x)x.freeVariables()), clause)
 	    Set freeVariables = new HashSet();
 	    for (Iterator i = clause.iterator(); i.hasNext(); )
 		freeVariables.addAll(((Formula)i.next()).getVariables());
 	    return new SignatureBase(freeVariables);
 	}

	// first-order

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
	 *   <li>sk(&exist;&lambda;x A) = sk((&lambda;x A)(f(x<sub>1</sub>,...,x<sub>n</sub>))) where FV(&exist;&lambda;x A) = {x<sub>1</sub>,...,x<sub>n</sub>}</li>
	 * </ul>
	 * Skolemization is
	 * <center class="Formula">&forall;x&exist;y &phi; &cong; &exist;F&forall;x &phi;[y&#8614;F(x)]</center>
	 * usually with the satisfiability-equivalent transformation "constantify" to
	 * <center>&forall;x &phi;[y&#8614;f(x)]</center>
	 * </p>
	 * This method will call {@link #negationForm(Formula)}.
	 */
	public static final Formula skolemForm(Formula F) {
	    // transform to negation normal form
	    F = negationForm(F);
	    try {
		// skolem transform TRS
		if (SkolemTransform == null) SkolemTransform = Substitutions.getInstance(Collections.singletonList(
		    //@xxx note that A should be a metavariable for a formula
		    new SkolemizingUnifyingMatcher(logic.createExpression("?_X1 _A"),
						   logic.createExpression("_A"),
						   //@internal note that _X1 should have the same type as in ?_X1 _A above
						   new SymbolBase("_X1", Types.INDIVIDUAL, null, true)
						   )
		    ));
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
		    skolemType = typeSystem.map(typeSystem.product(arguments), Types.INDIVIDUAL);
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
	 * @postconditions RES.getFreeVariables()=&empty; &and; RES=F.getFreeVariables()->iterate(x;G=F;G=G.forall(x))
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
	 * @postconditions RES.getFreeVariables()=&empty; &and; RES=F.getFreeVariables()->iterate(x;G=F;G=G.exist(x))
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
	 * @postconditions RES.getFreeVariables()=&empty; &and; RES=...
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
	

	// negation aware of duplex negatio est affirmatio
	
	/**
	 * Get the negation of F without introducing duplex negatios.
	 * @return G if F=&not;G, and &not;F otherwise.
	 * @preconditions F==ClassicalLogic.conjunctiveForm(F)
	 * @postconditions RES==ClassicalLogic.conjunctiveForm(F.not())
	 * @todo to ClassicalLogic.Utilities?
	 */
	public static final Formula negation(Formula F) {
	    // used duplex negatio est affirmatio (optimizable)
	    if ((F instanceof Composite)) {
		Composite f = (Composite) F;
		Object    g = f.getCompositor();
		if (g == ClassicalLogic.LogicFunctions.not)
		    // use duplex negatio est affirmatio to avoid double negations
		    return (Formula) f.getComponent();
	    }
	    // two special cases of negation that can be evaluated
	    if (FORMULA_FALSE.equals(F))
		return FORMULA_TRUE;
	    else if (FORMULA_TRUE.equals(F))
		return FORMULA_FALSE;
	    return F.not();
	}

	// Helper
	
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
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     */
    private final Expression createAtomicIndividualVariable(String signifier) {
	return createAtomic(new SymbolBase(signifier, Types.INDIVIDUAL, null, true));
    }
    /**
     * speed up for internal parsing in TRS
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     */
    private final Expression createAtomicLiteralVariable(String signifier) {
	return createAtomic(new SymbolBase(signifier, Types.TRUTH, null, true));
    }
    /**
     * speed up for internal parsing in TRS
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
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
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     * @todo remove
     */
    public Formula createFormula(String expression) throws ParseException {
	return (Formula) createExpression(expression);
    } 
}
