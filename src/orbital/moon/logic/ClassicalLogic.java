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
import java.beans.IntrospectionException;
import java.io.*;

import orbital.logic.functor.Notation;
import orbital.logic.functor.Notation.NotationSpecification;
import orbital.logic.functor.Functor.Specification;

import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.logging.Logger;
import java.util.logging.Level;
import orbital.logic.imp.Signature;
import orbital.logic.imp.Expression;

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
 * <center>I(&not;A) = true if and only if I(A)=false</center>.
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
public class ClassicalLogic extends ModernLogic implements Logic {
    private static class Debug {
	private Debug() {}
	public static void main(String arg[]) throws Exception {
	    ClassicalLogic.main(new String[] {"-normalForm", "all", "none", "properties"});
	} 
    }	 // Debug

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
	    return;
	} 
	try {
	    boolean normalForm = false;
	    boolean verbose = false;
	    String charset = null;

	    ClassicalLogic logic = new ClassicalLogic();
	    boolean hasBeenProving = false;

	    //@todo we should print an error if there already was a file in arg, but the last options are not followed by a file again and are completely vain
	    for (int option = 0; option < arg.length; option++) {
		if ("-normalForm".equals(arg[option]))
		    normalForm = true;
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
		    Signature sigma = logic.scanSignature(expression);
		    Interpretation[] Int = logic.createAllInterpretations(sigma);
		    for (int i = 0; i < Int.length; i++)
			System.out.println(Int[i] + ":\t" + logic.satisfy(Int[i], B));
		    hasBeenProving = true;
		} else {
		    String file = arg[option];
		    Reader rd = null;
		    System.out.println("proving " + file + " ...");
		    try {
			if ("all".equalsIgnoreCase(file)) {
			    rd = new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/semantic-equivalence.txt"), DEFAULT_CHARSET);
			    if (!proveAll(rd, logic, true, normalForm, verbose))
				throw new LogicException("instantiated " + logic + " which does not support all conjectures of semantic equivalences. Either the logic is non-classical, or the resource file is corrupt");
			} else if ("none".equalsIgnoreCase(file)) {
			    rd = new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/semantic-garbage.txt"), DEFAULT_CHARSET);
			    if (proveAll(rd, logic, false, normalForm, verbose))
				throw new LogicException("instantiated " + logic + " which does support a contradictory conjecture of semantic garbage. Either the logic is non-classical, or the resource file is corrupt");
			} else if ("properties".equalsIgnoreCase(file)) {
			    rd = new InputStreamReader(logic.getClass().getResourceAsStream("/orbital/resources/semantic-properties.txt"), DEFAULT_CHARSET);
			    if (!proveAll(rd, logic, true, normalForm, verbose))
				throw new LogicException("instantiated " + logic + " which does not support all conjectures of semantic properties. Either the logic is non-classical, or the resource file is corrupt");
			} else {
			    rd = charset == null
				? new FileReader(file)
				: new InputStreamReader(new FileInputStream(file), charset);
			    if (!proveAll(rd, logic, true, normalForm, verbose))
				System.err.println("could not prove all conjectures");
			    else
				System.err.println("all conjectures were proven successfully");
			}
		    }
		    catch (FileNotFoundException x) {
			System.err.println(x);
			System.err.println(orbital.signe.getHelpAboutHelp());
			return;
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
		    proveAll(rd, logic, true, normalForm, verbose);
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
    public static final String usage = "usage: [options] [all|none|properties|<filename>|table]\n\tall\tprove important semantic-equivalence expressions\n\tnone\ttry to prove some semantic-garbage expressions\n\tproperties\tprove some properties of classical logic inference relation\n\t<filename>\ttry to prove all expressions in the given file\n\ttable\tprint a function table of the expression instead\n\t-\tUse no arguments at all to be asked for expressions\n\t\tto prove.\noptions:\n\t-normalForm\tcheck the conjunctive and disjunctive forms in between\n\t-resolution\tuse resolution instead of semantic inference\n\t-verbose\tbe more verbose (f.ex. print normal forms if -normalForm)\n\t-charset=<encoding>\tthe character set or encoding to use for reading files\n\nTo check whether A and B are equivalent, enter '|= A<->B'";

    /**
     * Prove all conjectures read from a reader.
     * @param rd the source for the conjectures to prove.
     * @param logic the logic to use.
     * @param all_true If <code>true</code> this method will return whether all conjectures in rd
     *  could be proven.
     *  If <code>false</code> this method will return whether some conjectures in rd
     *  could be proven.
     * @return a value depending upon all_true.
     */
    public static boolean proveAll(Reader rd, ClassicalLogic logic, boolean all_true) throws java.text.ParseException, IOException {
	return proveAll(rd, logic, all_true, false, false);
    }
    private static boolean proveAll(Reader rd, ClassicalLogic logic, boolean all_true, boolean normalForm, boolean verbose) throws java.text.ParseException, IOException {
	DateFormat df = new SimpleDateFormat("H:mm:ss:S");
	df.setTimeZone(TimeZone.getTimeZone("Greenwich/Meantime"));
	Date	   loadeta;
	long	   start = System.currentTimeMillis();

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
	    final boolean sat = logic.infer(knowledge, formula);
	    System.out.println(knowledge + (sat ? "\t|= " : "\tNOT|= ") + formula);

	    // verify equivalence of its NF
	    if (normalForm) {
		String normalFormName[] = {
		    "disjunctive",
		    "conjunctive"
		};
		Formula nf[] = {
		    Utilities.disjunctiveForm(logic.createFormula(formula), true),
		    Utilities.conjunctiveForm(logic.createFormula(formula), true)
		};
		for (int i = 0; i < nf.length; i++) {
		    if (verbose)
			System.out.println(normalFormName[i] + " normal form: " + nf[i]);
		    if (!logic.inference().infer(new Formula[] {logic.createFormula(formula)}, nf[i]))
			throw new InternalError("wrong NF " + nf[i] + " =| for " + formula);
		    if (!logic.inference().infer(new Formula[] {nf[i]}, logic.createFormula(formula)))
			throw new InternalError("wrong NF " + nf[i] + " |= for " + formula);
		}
	    }

	    // keep records
	    some |= sat;
	    all &= sat;
	} while (!eof);

	Date   eta = new Date(System.currentTimeMillis() - start);
	logger.log(Level.INFO, "timing is Proof duration {0}", df.format(eta));
	return all_true ? all : some;
    } 


    
    // classical logic
    
    // enum of inference mechanisms
    /**
     * Semantic inference with tables.
     */
    public static final int SEMANTIC_INFERENCE = 0;
    /**
     * Resolution inference.
     * Inference mechanism driven by full resolution.
     */
    public static final int RESOLUTION_INFERENCE = 1;
    /**
     * The inference mechanism applied for the {@link #inference() inference relation}.
     * @todo change to typesafe enum
     * @serial
     * @see #inference()
     */
    private int inferenceMechanism = SEMANTIC_INFERENCE;
    
    public ClassicalLogic() {}
    public ClassicalLogic(int inferenceMechanism) {
	setInferenceMechanism(inferenceMechanism);
    }

    /**
     * Set the inference mechanism applied for the {@link #inference() inference relation}.
     * @see #inference()
     * @see #SEMANTIC_INFERENCE
     * @see #RESOLUTION_INFERENCE
     */
    public void setInferenceMechanism(int mechanism) {
    	if (mechanism < SEMANTIC_INFERENCE || RESOLUTION_INFERENCE < mechanism)
	    throw new IllegalArgumentException("no such inference mechanism: " + mechanism);
    	this.inferenceMechanism = mechanism;
    }
    protected int getInferenceMechanism() {
    	return inferenceMechanism;
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
		if (DNFeliminate == null) DNFeliminate = Substitutions.getInstance(Arrays.asList(new Object[] {
		    // eliminate implications
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1->_X2"), logic.createExpression("~_X1|_X2")),
		    // eliminate equivalences
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1<->_X2"), logic.createExpression("(_X1&_X2) | (~_X1&~_X2)")),
		    // eliminate antivalences
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1^_X2"), logic.createExpression("(_X1&~_X2) | (~_X1&_X2)")),
		}));
		f = (Formula) Functionals.fixedPoint(DNFeliminate, f);
		// simplification part (necessary and does not disturb local confluency?)
		if (simplifying && DNFSimplification == null) DNFSimplification = Arrays.asList(new Object[] {
		    // seems necessary, does not disturb local confluency? evaluate constants
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~false"), logic.createAtomicLiteral("true")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~true"), logic.createAtomicLiteral("false")),
    				
		    // necessary and does not disturb local confluency? absorbtion
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1&(_X1|_X2)"), logic.createAtomicLiteralVariable("_X1")),
		    // necessary and does not disturb local confluency? idempotent
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X&_X"), logic.createAtomicLiteralVariable("_X")),
		    //@xxx for DNF either this rule (s.b.) or substitution has an error (infinite recursion)
		    //Substitutions.createSingleSidedMatcher(logic.createExpression("_X|_X"), logic.createAtomicLiteralVariable("_X")),
		    // necessary and does not disturb local confluency? neutral element
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X&true"), logic.createAtomicLiteralVariable("_X")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X|false"), logic.createAtomicLiteralVariable("_X")),
		    // necessary and does not disturb local confluency? duplicate to dual to neutral element (until conditional commutative is supplied)
		    Substitutions.createSingleSidedMatcher(logic.createExpression("true&_X"), logic.createAtomicLiteralVariable("_X")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("false|_X"), logic.createAtomicLiteralVariable("_X")),
		    // necessary and does not disturb local confluency? dual to neutral element
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X&false"), logic.createAtomicLiteral("false")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X|true"), logic.createAtomicLiteral("true")),
		    // necessary and does not disturb local confluency? duplicate to dual to neutral element (until conditional commutative is supplied)
		    Substitutions.createSingleSidedMatcher(logic.createExpression("false&_X"), logic.createAtomicLiteral("false")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("true|_X"), logic.createAtomicLiteral("true")),
		    // necessary and does not disturb local confluency? complement
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X&~_X"), logic.createAtomicLiteral("false")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X|~_X"), logic.createAtomicLiteral("true")),
		    // necessary and does not disturb local confluency? conditional commutative (according to lexical order)
		    new LexicalConditionalUnifyingMatcher(logic.createExpression("_X2&_X1"), logic.createExpression("_X1&_X2"), logic.createAtomicLiteralVariable("_X1"), logic.createAtomicLiteralVariable("_X2")),
		    // necessary and does not disturb local confluency? conditional associative (according to lexical order)
		    //@todo
		});
		// transform to DNF part
		if (DNFtrs == null) DNFtrs = Arrays.asList(new Object[] {
		    // involution duplex negatio est affirmatio
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~~_X"), logic.createAtomicLiteralVariable("_X")),
		    // deMorgan
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~(_X1|_X2)"), logic.createExpression("~_X1&~_X2")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~(_X1&_X2)"), logic.createExpression("~_X1|~_X2")),
		    // distribute | over &
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1&(_X2|_X3)"), logic.createExpression("(_X1&_X2)|(_X1&_X3)")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("(_X2|_X3)&_X1"), logic.createExpression("(_X2&_X1)|(_X3&_X1)"))
		});
		//@todo simplifying conditional rules: commutative with lexical sort, etc.
		return (Formula) Functionals.fixedPoint(Substitutions.getInstance(simplifying ? new ArrayList(Setops.union(DNFSimplification, DNFtrs)) : DNFtrs), f);
	    } catch (java.text.ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}
	// lazy initialized cache for TRS rules
	private static Substitution DNFeliminate;
	private static List DNFtrs;
	private static List DNFSimplification;

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
		if (CNFeliminate == null) CNFeliminate = Substitutions.getInstance(Arrays.asList(new Object[] {
		    // eliminate implications
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1->_X2"), logic.createExpression("~_X1|_X2")),
		    // eliminate equivalences
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1<->_X2"), logic.createExpression("(_X1|~_X2) & (~_X1|_X2)")),
		    // eliminate antivalences
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1^_X2"), logic.createExpression("(_X1|_X2) & (~_X1|~_X2)")),
		}));
		f = (Formula) Functionals.fixedPoint(CNFeliminate, f);
		// simplification part (necessary and does not disturb local confluency?)
		if (simplifying && CNFSimplification == null) CNFSimplification = Arrays.asList(new Object[] {
		    // seems necessary, does not disturb local confluency? evaluate constants
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~false"), logic.createAtomicLiteral("true")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~true"), logic.createAtomicLiteral("false")),
		    // necessary and does not disturb local confluency? absorbtion
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1&(_X1|_X2)"), logic.createAtomicLiteralVariable("_X1")),
		    // necessary and does not disturb local confluency? idempotent
		    //@xxx for CNF either this rule (s.b.) or substitution has an error. infinite recursion for (a&b)<->(b&a)
		    //Substitutions.createSingleSidedMatcher(logic.createExpression("_X&_X"), logic.createAtomicLiteralVariable("_X")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X|_X"), logic.createAtomicLiteralVariable("_X")),
		    // necessary and does not disturb local confluency? neutral element
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X&true"), logic.createAtomicLiteralVariable("_X")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X|false"), logic.createAtomicLiteralVariable("_X")),
		    // necessary and does not disturb local confluency? duplicate to dual to neutral element (until conditional commutative is supplied)
		    Substitutions.createSingleSidedMatcher(logic.createExpression("true&_X"), logic.createAtomicLiteralVariable("_X")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("false|_X"), logic.createAtomicLiteralVariable("_X")),
		    // necessary and does not disturb local confluency? dual to neutral element
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X&false"), logic.createAtomicLiteral("false")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X|true"), logic.createAtomicLiteral("true")),
		    // necessary and does not disturb local confluency? duplicate to dual to neutral element (until conditional commutative is supplied)
		    Substitutions.createSingleSidedMatcher(logic.createExpression("false&_X"), logic.createAtomicLiteral("false")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("true|_X"), logic.createAtomicLiteral("true")),
		    // necessary and does not disturb local confluency? complement
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X&~_X"), logic.createAtomicLiteral("false")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X|~_X"), logic.createAtomicLiteral("true")),
		    // necessary and does not disturb local confluency? conditional commutative (according to lexical order)
		    new LexicalConditionalUnifyingMatcher(logic.createExpression("_X2&_X1"), logic.createExpression("_X1&_X2"), logic.createAtomicLiteralVariable("_X1"), logic.createAtomicLiteralVariable("_X2")),

		    // necessary and does not disturb local confluency? right associative
		    //@xxx for CNF infinite recursion for (a&b)<->(b&a) and a<->b<->c. this is because conditional commutative and right-associative oscillate, then
		    //Substitutions.createSingleSidedMatcher(logic.createExpression("(_X1&_X2)&_X3"), logic.createExpression("_X1&(_X2&_X3)")),
		});
		// transform to CNF part
		if (CNFtrs == null) CNFtrs = Arrays.asList(new Object[] {
		    // involution duplex negatio est affirmatio
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~~_X"), logic.createAtomicLiteralVariable("_X")),
		    // deMorgan
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~(_X1|_X2)"), logic.createExpression("~_X1&~_X2")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~(_X1&_X2)"), logic.createExpression("~_X1|~_X2")),
		    // distribute & over |
		    Substitutions.createSingleSidedMatcher(logic.createExpression("_X1|(_X2&_X3)"), logic.createExpression("(_X1|_X2)&(_X1|_X3)")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("(_X2&_X3)|_X1"), logic.createExpression("(_X2|_X1)&(_X3|_X1)"))
		});
		return (Formula) Functionals.fixedPoint(Substitutions.getInstance(simplifying ? new ArrayList(Setops.union(CNFSimplification, CNFtrs)) : CNFtrs), f);
	    } catch (java.text.ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}
	// lazy initialized cache for TRS rules
	private static Substitution CNFeliminate;
	private static List CNFtrs;
	private static List CNFSimplification;
	
	/**
	 * Transforms into implicative normal form (INF)
	 * @todo introduce Formula implicativeForm(Formula f) as TRS
	 */

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
		    new SkolemizingUnifyingMatcher(logic.createExpression("?_X1 _A"), logic.createExpression("_A"), logic.createAtomicLiteralVariable("_X1")),
		}));
		return (Formula) Functionals.fixedPoint(SkolemTransform, F);
	    } catch (java.text.ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}
	// lazy initialized cache for TRS rules
	private static Substitution SkolemTransform;

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
		if (CNFeliminate == null) conjunctiveForm(new ClassicalLogic().createFormula("true"));
		F = (Formula) Functionals.fixedPoint(CNFeliminate, F);
		// negation normal form transform TRS
		if (NegationNFTransform == null) NegationNFTransform = Substitutions.getInstance(Arrays.asList(new Object[] {
		    //@xxx note that the  _Xi should be metavariables for formulas
		    // involution duplex negatio est affirmatio
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~~_X"), logic.createAtomicLiteralVariable("_X")),
		    // deMorgan
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~(_X1|_X2)"), logic.createExpression("~_X1&~_X2")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~(_X1&_X2)"), logic.createExpression("~_X1|~_X2")),
		    // negated implication
		    // s.a. Substitutions.createSingleSidedMatcher(logic.createExpression("~(_X1->_X2)"), logic.createExpression("_X1&~_X2")),
		    // negated equivalence
		    // s.a. Substitutions.createSingleSidedMatcher(logic.createExpression("~(_X1<->_X2)"), logic.createExpression("(~_X1)<->_X2")),
		    // negated all-quantifier
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~(°_V _X)"), logic.createExpression("?_V ~_X")),
		    // negated exists-quantifier
		    Substitutions.createSingleSidedMatcher(logic.createExpression("~(?_V _X)"), logic.createExpression("°_V ~_X")),
		}));
		return (Formula) Functionals.fixedPoint(NegationNFTransform, F);
	    } catch (java.text.ParseException ex) {
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
		// skolem transform TRS
		if (QuantifierDropTransform == null) QuantifierDropTransform = Substitutions.getInstance(Arrays.asList(new Object[] {
		    //@xxx note that A should be a metavariable for a formula
		    Substitutions.createSingleSidedMatcher(logic.createExpression("°_X1 _A"), logic.createExpression("_A")),
		    Substitutions.createSingleSidedMatcher(logic.createExpression("?_X1 _A"), logic.createExpression("_A")),
		}));
		return (Formula) Functionals.fixedPoint(QuantifierDropTransform, F);
	    } catch (java.text.ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}

	// lazy initialized cache for TRS rules
	private static Substitution QuantifierDropTransform;

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

	    public Object replace(Object t) {
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
		final Symbol skolemFunctionSymbol = new DistinctSymbol("s", skolemType, null, false);

		// build expression form
		try {
		    Expression[] freeVariableExpressions = new Expression[freeVariables.size()];
		    Iterator it = freeVariables.iterator();
		    for (int i = 0; i < freeVariables.size(); i++)
			freeVariableExpressions[i] = logic.createAtomic((Symbol)it.next());
		    assert !it.hasNext();
		    // expression form of s(FV(t))
		    Expression applied_s = logic.compose(skolemFunctionSymbol,
							 freeVariableExpressions);

		    // really substitute "[x->s(FV(t))]"
		    return Substitutions.getInstance(Arrays.asList(new Object[] {
			Substitutions.createExactMatcher(x, applied_s)
		    })).apply(r);
		} catch (java.text.ParseException ex) {
		    throw (InternalError) new InternalError("Unexpected syntax in internal term construction").initCause(ex);
		}
	    }
	}

    }


    // convenience methods
    /**
     * speed up for internal parsing in TRS
     */
    private final Expression createAtomicIndividualVariable(String signifier) {
	return createAtomic(new SymbolBase(signifier, SymbolBase.UNIVERSAL_ATOM, null, true));
    }
    /**
     * speed up for internal parsing in TRS
     */
    private final Expression createAtomicLiteralVariable(String signifier) {
	return createAtomic(new SymbolBase(signifier, SymbolBase.BOOLEAN_ATOM, null, true));
    }
    /**
     * speed up for internal parsing in TRS
     */
    private final Expression createAtomicLiteral(String signifier) {
	return createAtomic(new SymbolBase(signifier, SymbolBase.BOOLEAN_ATOM, null, false));
    }

    /**
     * facade for convenience.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Method</a>
     */
    public boolean infer(String expression, String exprDerived) throws java.text.ParseException {
	if (expression == null)
	    throw new NullPointerException("null is not an expression");
	Formula B[] = (Formula[]) Arrays.asList(createAllExpressions(expression)).toArray(new Formula[0]);
	Formula D = (Formula) createExpression(exprDerived);
	return inference().infer(B, D);
    } 
	

    /**
     * character element of core signature.
     */
    private static final String operators = "~! |&^-><=(,)°?";

    //@todo remove this bugfix that replaces "xfy" by "yfy" associativity only for *.jj parsers to work without inefficient right-associative lookahead.
    private static final String xfy = "yfy";

    private static final Interpretation _coreInterpretation =
	LogicSupport.arrayToInterpretation(new Object[][] {
	    /**
	     * Contains ordered map (in precedence order) of initial functors
	     * and their notation specifications.
	     * Stored internally as an array of length-2 arrays.
	     * @invariant sorted, i.e. precedenceOf[i] < precedenceOf[i+1]
	     * @todo if Resolution would not need them, we could also directly embed LogicFunctions, here
	     */
	    {LogicFunctions.apply,       // "@"
	     new NotationSpecification(500, "xfx", Notation.INFIX)},
	    //@fixme debug why the thing ~(a->a) is displayed as ~a->a etc.  Use BESTFIX!
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
	     new NotationSpecification(1100, "fxx", Notation.PREFIX)}
	}, false);
    private static final Signature _coreSignature = _coreInterpretation.getSignature();
    /**
     * reserved keywords: core signature symbols.
     * Contains supposed variable symbols due to a misinterpretation, instead of the real
     * symbols. Especially specifies the wrong type.
     * @todo adapt? to new situation of createAtomic(<u>Symbol</u>)
     */
    private static Set keywords = new HashSet(Arrays.asList(new Object[] {LogicParser.defaultSymbolFor("true"), LogicParser.defaultSymbolFor("false"), LogicParser.defaultSymbolFor("xor"), LogicParser.defaultSymbolFor("all"), LogicParser.defaultSymbolFor("some"), LogicParser.defaultSymbolFor("lambda")}));

    // Helper utilities.

    static class LogicFunctions {
        private LogicFunctions() {}
    
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
	//@todo could we turn forall into type (&sigma;&rarr;t)&rarr;t alias Function<Function<S,boolean>,boolean> and use &forall;(&lambda;x.t)
    	public static final BinaryFunction forall = new BinaryFunction() {
		private final Type logicalTypeDeclaration = null;
    		public Object apply(Object x, Object a) {
		    throw new LogicException("quantified formulas only have a semantic value with respect to a possibly infinite domain. They are available for inference, but cannot be interpreted.");
    		}
		public String toString() { return "°"; }
	    };

    	public static final BinaryFunction exists = new BinaryFunction() {
    		public Object apply(Object x, Object a) {
		    throw new LogicException("quantified formulas only have a semantic value with respect to a possibly infinite domain. They are available for inference, but cannot be interpreted.");
    		}
		public String toString() { return "?"; }
	    };

	//@xxx trick for functions that never get called
    	public static final BinaryFunction lambda = new BinaryFunction() {
		/*private static*/ final Specification specification = new Specification(new Class[] {
		    Object.class, Object.class
		}, Function.class);
    		public Object apply(Object x, Object t) {
		    throw new AssertionError("this method never gets called since lambda cannot be interpreted truthh-functionally, but already receives a structural modification in compose(...)");
    		}
		public String toString() { return "\\"; }
	    };
    	public static final BinaryFunction apply = new BinaryFunction() {
		//@internal (almost) identical to @see orbital.logic.functor.Functionals#apply but additionally asserting that no one every really calls apply.
		private /*static*/ final Specification specification = new Specification(new Class[] {
		    Function/*_<A,B>_*/.class, Object/*_>A<_*/.class
		}, Object/*_>B<_*/.class);
    		public Object apply(Object f, Object g) {
		    throw new AssertionError("this method never gets called since compose cannot be interpreted truth-functionally, but already receives a structural modification in compose(...)");
    		}
		public String toString() { return "@"; }
	    };
    }

    //@xxx get rid of these shared static variables
    private static final Symbol APPLY;
    static final Symbol LAMBDA;
    static {
	//@internal we need some valid non-null arguments.
	Expression OBJ = Utilities.logic.createAtomic(new SymbolBase("OBJ", SymbolBase.UNIVERSAL_ATOM));
	APPLY = _coreSignature.get("@", new Expression[] {OBJ,OBJ});
	assert APPLY != null : "apply operator found";
	LAMBDA  = _coreSignature.get("\\", new Expression[] {OBJ,OBJ});
	assert LAMBDA != null : "lambda operator found";
    }
    public Expression compose(Expression op, Expression arguments[]) throws java.text.ParseException {
	if (op == null)
	    throw new NullPointerException("illegal arguments: operator " + op + " composed with " + MathUtilities.format(arguments));
        if (!Types.isApplicableTo(op.getType(), arguments))
	    throw new java.text.ParseException("operator " + op + " not applicable to the " + arguments.length + " arguments " + MathUtilities.format(arguments), ClassicalLogic.COMPLEX_ERROR_OFFSET);

	// handle special cases of term construction, first
	if ((op instanceof ModernFormula.FixedAtomicSymbol)
	    && LAMBDA.equals(((ModernFormula.FixedAtomicSymbol)op).getSymbol())) {
	    //@todo if we stick to compose(Expression,Expression[]) then perhaps we could provide &lambda;-abstractions by introducing a core symbol LAMBDA that has as fixed interpretation a binary function that ... But of &lambda;(x.t), x will never get interpreted, so it is a bit different than composeFixed(lambda,{x,t}) would suggest. &lambda;-abstraction are not truth-functional!
	    assert arguments.length == 2;
	    assert arguments[0] instanceof ModernFormula.AtomicSymbol : "Symbols when converted to formulas become AtomicSymbols";
	    Symbol x = (Symbol) ((Formula)arguments[0]).getSignature().iterator().next();
	    assert x.isVariable() : "we only form lambda abstractions with respect to variables";
	    return createLambdaProp(x, (Formula) arguments[1]);
	} else
	    return super.compose(op, arguments);
    }
    /**
     * @deprecated Use {@link #compose(Expression,Expression[])} instead, converting op via {@link ExpressionBuilder#createAtomic(Symbol)}.
     */
    public Expression compose(Symbol op, Expression arguments[]) throws java.text.ParseException {
	if (op == null)
	    throw new NullPointerException("illegal arguments: operator " + op + " composed with " + MathUtilities.format(arguments));
        if (!Types.isApplicableTo(op.getType(), arguments))
	    throw new java.text.ParseException("operator " + op + " not applicable to the " + arguments.length + " arguments " + MathUtilities.format(arguments), ClassicalLogic.COMPLEX_ERROR_OFFSET);

	// handle special cases of term construction, first
	if (APPLY.equals(op)) {
	    //@deprecated since compose(Expression,Expression[]) already can do this, directly.
	    // do we still need such a language operator for something, or can it be removed (no one ever calls) and use the meta-operator instead
	    Expression rest[] = new Expression[arguments.length - 1];
	    System.arraycopy(arguments, 1, rest, 0, rest.length);
	    return compose(arguments[0], rest);
	} else if (LAMBDA.equals(op)) {
	    //@todo if we stick to compose(Expression,Expression[]) then perhaps we could provide &lambda;-abstractions by introducing a core symbol LAMBDA that has as fixed interpretation a binary function that ... But of &lambda;(x.t), x will never get interpreted, so it is a bit different than composeFixed(lambda,{x,t}) would suggest.
	    assert arguments.length == 2;
	    assert arguments[0] instanceof ModernFormula.AtomicSymbol : "Symbols when converted to formulas become AtomicSymbols";
	    Symbol x = (Symbol) ((Formula)arguments[0]).getSignature().iterator().next();
	    assert x.isVariable() : "we only form lambda abstractions with respect to variables";
	    return createLambdaProp(x, (Formula) arguments[1]);
	} else
	    return super.compose(op, arguments);
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
     */
    private static class LambdaAbstractionFormula extends ModernFormula implements Function.Composite {
	private final Symbol x;
	private final Formula term;
	public LambdaAbstractionFormula(Logic logic, Symbol x, Formula term) {
	    super(logic);
	    this.x = x;
	    this.term = term;
	}

	public Functor getCompositor() {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	public Object getComponent() {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	public void setCompositor(Functor f) {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}
	public void setComponent(Object g) {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
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
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}

	public Set getBoundVariables() {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}

	public Set getVariables() {
	    throw new UnsupportedOperationException("not yet implemented for " + getClass());
	}

	public Object apply(Object i) {
	    final Interpretation I = (Interpretation)i;
	    // return I(&lambda;x.t)
	    return new Function() {
		    public Object apply(Object a) {
			// interpret term in the modification I<x/a> of I.
			Map modif = new HashMap();
			modif.put(x, a);
			Interpretation modification =
			    new InterpretationBase(new SignatureBase(Collections.singleton(x)), modif);
			Interpretation modifiedI =
			    new QuickUnitedInterpretation(modification, I);
			System.err.println(modifiedI + "\nper modification <" + x + "/" + a + "> in " + term);
			return term.apply(modifiedI);
		    }
		};
	}
	public String toString() {
	    //@todo use notation like all others do
	    return "\\" + x + "." + term;
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
	return getInferenceMechanism() == SEMANTIC_INFERENCE
	    ? _semanticInference
	    : _resolution;
    } 
    private final Inference _resolution = new Resolution();
    private final Inference _semanticInference = new Inference() {
	    public boolean infer(Formula[] B, Formula D) {
    		Signature sigma = D.getSignature();
    		for (int i = 0; i < B.length; i++)
		    sigma = sigma.union(B[i].getSignature());
		// semantic test whether all interpretations that satisfy all formulas in B, also satisfy D
		Interpretation[] Int = createAllInterpretations(sigma);
		loop:		for (int i = 0; i < Int.length; i++) {
		    Interpretation I = Int[i];
		    // I |= B is defined as &forall;F&isin;B: I |= F
		    for (int b = 0; b < B.length; b++)
			if (!B[b].apply(I).equals(Boolean.TRUE)) //if (!satisfy(I, B[b]))
			    continue loop;
		    if (!D.apply(I).equals(Boolean.TRUE)) //if (!satisfy(I, D))
			return false;
		}
		return true;
	    } 
	    public boolean isSound() {
		return true;
	    } 
	    public boolean isComplete() {
		return true;
	    } 
	};
    
    public Signature coreSignature() {
	return _coreSignature;
    } 
    public Interpretation coreInterpretation() {
	return _coreInterpretation;
    }
    public Signature scanSignature(String expression) {
	return new SignatureBase(scanSignatureImpl(expression));
    } 
    //@todo (half-done?) Use LogicParser to find out the Signature, also adapt it such that he finds out new symbols like in ~p(X)
    private static Set scanSignatureImpl(String expression) {
	if (expression == null)
	    throw new NullPointerException("null is not an expression");
	Collection		names = new LinkedList();
	StringTokenizer st = new StringTokenizer(expression, operators, false);
	while (st.hasMoreElements()) {
	    names.add(LogicParser.defaultSymbolFor((String) st.nextElement()));
	}
	names = new TreeSet(names);
	// the signature really should not include core signature symbols
	names.removeAll(keywords);
	return (Set) names;
    } 



    /**
     * Get all possible &Sigma;-Interpretations associating
     * the symbols in &Sigma; with elements of the world.
     * Interpretations are conceptually irrelevant for syntactic calculi of inference relations
     * but may optionally be used to implement a naive calculus.
     * @pre sigma is only a signature of propositional logic
     * @return all &Sigma;-Interpretations that are valid in this Logic (i.e. that can be formed with Signature &Sigma;).
     * @xxx somehow in a formula like (\x. x>2)(7) the numbers 2, and 7 are also subject to interpretation by true or false.
     */
    private Interpretation[] createAllInterpretations(Signature sigma) {
	if (sigma == null)
	    throw new NullPointerException("invalid signature: " + sigma);
	Signature sigmaComb = new SignatureBase(sigma);
	for (Iterator it = sigmaComb.iterator(); it.hasNext(); ) {
	    final Symbol s = (Symbol)it.next();
	    final Type type = s.getType();
	    if (type.equals(Types.TRUTH))
		// ordinary propositional logic
		;
	    else if (!s.isVariable() && type.subtypeOf(Types.type(orbital.math.Scalar.class)))
	    	// forget about interpreting _fixed_ constants @xxx generalize concept
	    	it.remove();
	    else
		throw new IllegalArgumentException("a signature of propositional logic should not contain " + s + " of type " + type);
	}

	Combinatorical   comb = Combinatorical.getPermutations(sigmaComb.size(), 2, true);
	Interpretation[] all = new Interpretation[comb.count()];
	for (int i = 0; i < all.length; i++) {
	    Interpretation I = new InterpretationBase(sigmaComb, new HashMap());
	    Iterator	   it = sigmaComb.iterator();
	    int[] c = comb.next();
	    for (int s = 0; it.hasNext(); s++)
		I.put(it.next(), c[s] == 0 ? Boolean.FALSE : Boolean.TRUE);
	    all[i] = I;
	} 
	return all;
    } 



    /**
     * Convenience method.
     * @deprecated Use <code>(Formula) createExpression(expression)</code> instead.
     * @todo remove
     */
    public Formula createFormula(String expression) throws java.text.ParseException {
	return (Formula) createExpression(expression);
    } 
   
    /**
     * @deprecated empty formulas are not defined
     */
    static Formula EMPTY = new ModernFormula(new ClassicalLogic()) {
	    public Type getType() {
		return Types.TRUTH;
	    }
	    public Signature getSignature() {
		return SignatureBase.EMPTY;
	    }
	    public Object apply(Object I) {return Boolean.TRUE;}
	    public String toString() {return "";}
	    public Formula not() {
		// optimized display, otherwise EMPTY.not().toString() = "~"
		throw new UnsupportedOperationException("deprecated. Use 'true' or 'false' instead of empty formulas");
	    } 
	};
}
