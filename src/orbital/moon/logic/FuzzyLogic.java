/*
 * @(#)FuzzyLogic.java 0.9 1997/05/01 Andre Platzer
 * 
 * Copyright (c) 1997-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.logic.sign.type.*;
import orbital.logic.sign.ParseException;
import orbital.logic.functor.Functor;
import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.StringReader;
import java.io.Serializable;
import java.io.ObjectStreamException;

import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.TreeMap;

import orbital.math.Values;
import orbital.math.MathUtilities;
import orbital.io.IOUtilities;
import orbital.util.InnerCheckedException;
import java.beans.IntrospectionException;

import orbital.logic.sign.concrete.Notation;
import orbital.logic.sign.concrete.Notation.NotationSpecification;

import orbital.util.Utility;

/**
 * Implementation of quantitative fuzzy logics.
 * <p>
 * Fuzzy logic is a numeric approach in which truth-values represent a degree of truth
 * specified as a real number in the range [0,1].
 * (Unlike probabilities in the range [0,1] which specify a degree of belief.)</p>
 * <p>
 * <table>
 *   <tr>
 *     <td colspan="3">&#8911;:[0,1]<sup>2</sup>&rarr;[0,1] is a <dfn>fuzzy AND operator</dfn>, or triangular norm, iff</td>
 *   </tr>
 *   <tr>
 *     <td>(n)</td>
 *     <td>a &#8911; 1 = a</td>
 *     <td>&quot;neutral&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(c)</td>
 *     <td>a &#8911; b = b &#8911; a</td>
 *     <td>&quot;commutative&quot;</td>
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
 *   <tr>
 *     <td>(C)</td>
 *     <td>&#8911; is continuous</td>
 *     <td>&quot;continuous&quot; (optional)</td>
 *   </tr>
 *   <tr>
 *     <td>()</td>
 *     <td>a &#8911; 0 = 0</td>
 *     <td>(&lArr; (n),(c),(mon))</td>
 *   </tr>
 *   <tr>
 *     <td>()</td>
 *     <td>0 &#8911; 0 = 0<br>
 *       1 &#8911; 0 = 0<br>
 *       0 &#8911; 1 = 0<br>
 *       1 &#8911; 1 = 1</td>
 *     <td>&quot;&and; boundary conditions&quot; (&lArr; (n),(c),(mon))</td>
 *   </tr>
 * </table>
 * The only function that fulfills all axioms including the optional ones is a&#8911;b = max(a,b) [Klir, Folger 1988].
 * </p>
 * <p>
 * <table>
 *   <tr>
 *     <td colspan="3">&#8910;:[0,1]<sup>2</sup>&rarr;[0,1] is a <dfn>fuzzy OR operator</dfn> or co-t-norm, iff</td>
 *   </tr>
 *   <tr>
 *     <td>(n)</td>
 *     <td>a &#8910; 0 = a</td>
 *     <td>&quot;neutral&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(c)</td>
 *     <td>a &#8910; b = b &#8910; a</td>
 *     <td>&quot;commutative&quot;</td>
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
 *   <tr>
 *     <td>(C)</td>
 *     <td>&#8910; is continuous</td>
 *     <td>&quot;continuous&quot; (optional)</td>
 *   </tr>
 *   <tr>
 *     <td>()</td>
 *     <td>a &#8911; 1 = 1</td>
 *     <td>(&lArr; (n),(c),(mon))</td>
 *   </tr>
 *   <tr>
 *     <td>()</td>
 *     <td>0 &#8910; 0 = 0<br>
 *       1 &#8910; 0 = 1<br>
 *       0 &#8910; 1 = 1<br>
 *       1 &#8910; 1 = 1</td>
 *     <td>&quot;&or; boundary conditions&quot; (&lArr; (n),(c),(mon))</td>
 *   </tr>
 * </table>
 * The only function that fulfills all axioms including the optional ones is a&#8910;b = min(a,b) [Klir, Folger 1988].
 * <table>
 *   <tr>
 *     <td colspan="3">~ is a <dfn>fuzzy NOT operator</dfn>, iff</td>
 *   </tr>
 *   <tr>
 *     <td>(1)</td>
 *     <td>~1 = 0<br>
 *       ~0 =1</td>
 *     <td>&quot;&not; boundary conditions&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(mon)</td>
 *     <td>a&lt;b implies ~a &ge; ~b</td>
 *     <td>&quot;monotonic&quot;</td>
 *   </tr>
 *   <tr>
 *     <td>(inv)</td>
 *     <td>~ ~ a = a</td>
 *     <td>&quot;involutive&quot; (optional)</td>
 *   </tr>
 *   <tr>
 *     <td>(C)</td>
 *     <td>~ is continuous</td>
 *     <td>&quot;continuous&quot; (optional)</td>
 *   </tr>
 * </table>
 * </p>
 * <p>
 * Derived operators are the fuzzy implication and bisubjunction.
 * <div>a&rarr;b := sup{c &brvbar; a&#8911;c&le;b}</div>
 * <div>a&harr;b := (a&rarr;b)&#8911;(b&rarr;a)</div>
 * </p>
 * <p>
 * G&ouml;del and drastic operator sets bound all operator sets. For any operators (&#8911;,&#8910;)
 * it is
 * <div style="text-align: center">max{a,b}&le;a&#8910;b&le;{@link #DRASTIC u<sup>*</sup>}(a,b) &le; {@link #DRASTIC i<sup>*</sup>}(a,b)&le;a&#8911;b&le;min{a,b}</div>
 * </p>
 * <p>
 * <dl class="def">
 * Let X be a universal set.
 *   <dt>fuzzy set</dt>
 *   <dd>A = {(x,&mu;<sub>A</sub>(x)) &brvbar; x&isin;X} is defined with a membership function &mu;<sub>A</sub>:X&rarr;[0,1].
 *    A finite fuzzy set A of domain {a<sub>1</sub>,&#8230;,a<sub>n</sub>} is sometimes denoted formally as
 *    <div>"A=&mu;<sub>A</sub>(a<sub>1</sub>)/a<sub>1</sub> + &mu;<sub>A</sub>(a<sub>2</sub>)/a<sub>2</sub> + ... + &mu;<sub>A</sub>(a<sub>n</sub>)/a<sub>n</sub>"</div>
 *    where + and / are purely syntactic symbols and do not denote arithmetic operations.
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
 * <p>
 * Also note that for fuzzy sets, idem potence and distributive on the one hand,
 * and <span xml:lang="la">tertium non datur</span> on the other hand, are mutually exclusive
 * properties for all operators.
 * </p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see "Klir, G. and Folger, T. (1988), Fuzzy Sets, Uncertainty and Information, Prentice-Hall, Englewood Cliffs."
 * @see "Gottwald, Siegfried. A Treatise On Many-Valued Logics, volume 9 of Studies in Logic and Computation. Research Studies Press, Baldock, Hertfordshire, England, 2001."
 * @see "H&aacute;jek, Petr. Metamathematics of Fuzzy Logic, volume 4 of Trends in Logic - Studia Logica Library. Kluwer Academic Publishers, 1998."
 */
public class FuzzyLogic extends ModernLogic implements Logic {
    /**
     * Maximum number of OperatorSet objects (for typesafe enum).
     */
    private static final int MAX_OPERATORS = 20;

    private static final TypeSystem typeSystem = Types.getDefault();
    /**
     * tool-main
     * @todo parse arguments in order to obtain OperatorSet used
     */
    public static void main(String arg[]) throws Exception {
	if (orbital.signe.isHelpRequest(arg)) {
	    System.out.println(usage);
	    System.out.println("Core operators:\n\t" + new FuzzyLogic().coreSignature());
	    return;
	} 
	FuzzyLogic logic = new FuzzyLogic();
	System.out.println("Enter sequence 'A|~C' to verify. Simply leave blank to type 'false' or {} or null.");
	System.out.print("Type base expression (A): ");
	System.out.flush();
	String expr = "{" + IOUtilities.readLine(System.in) + "}";
	System.out.print("Type sequence expression (C): ");
	System.out.flush();
	String  expr2 = IOUtilities.readLine(System.in);
	System.out.println(logic.satisfy(InterpretationBase.EMPTY(SignatureBase.EMPTY), logic.createFormula(expr2)));
	boolean sat = logic.infer(expr, expr2);
	System.out.println(expr + (sat ? " satisfies " : " does not satisfy ") + expr2);
    } 
    public static final String usage = "interpret fuzzy logic";


    private static final Type TRUTH = typeSystem.objectType(orbital.math.Real.class, "truth");

    private static final Values valueFactory = Values.getDefaultInstance();
    /**
     * list of static elements of signature.
     */
    private static final String operators = "~! \t\r\n|&^-><=(),";

    /**
     * Remembers the fuzzy logic operators used in coreInterpretation().
     */
    private final OperatorSet fuzzyLogicOperators;
    //@todo remove this bugfix that replaces "xfy" by "yfy" associativity only for *.jj parsers to work without inefficient right-associative lookahead.
    private static final String xfy = "yfy";

    private final Interpretation _coreInterpretation;
    private final Signature _coreSignature;
    public FuzzyLogic() {
	this(GOEDEL);
    }
    private static final String typAssoc = "f";  //@xxx should be "fx"?
    /**
     * Create a new fuzzy logic implementation with a specific operator set.
     * @param fuzzyLogicOperators the set of fuzzy logic operators to use.
     * @see #GOEDEL
     * @see #BOUNDED
     * @see #PRODUCT
     * @see #DRASTIC
     * @see #HAMACHER(double)
     * @see #YAGER(double)
     */
    public FuzzyLogic(OperatorSet fuzzyLogicOperators) {
	this.fuzzyLogicOperators = fuzzyLogicOperators;
	final OperatorSet op = fuzzyLogicOperators;
	this._coreInterpretation =
	LogicSupport.arrayToInterpretation(new Object[][] {
	    {typeSystem.UNIVERSAL(),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},
	    {TRUTH,//@fixme replace true/false by 1.0,0.0
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},
	    {typeSystem.objectType(orbital.math.Integer.class, "integer"),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},
	    //@internal type-alias for truth is necessary, since LogicParser will treat 0.5:real.
	    {typeSystem.objectType(orbital.math.Real.class, "real"),
	     new NotationSpecification(500, typAssoc, Notation.POSTFIX)},

	    {LogicFunctions.forall,       // "°"
	     new NotationSpecification(900, "fxx", Notation.PREFIX)},
	    {LogicFunctions.exists,       // "?"
	     new NotationSpecification(900, "fxx", Notation.PREFIX)},

	    {op.not(),          // "~"
	     new NotationSpecification(900, "fy", Notation.PREFIX)},
	    {op.and(),          // "&"
	     new NotationSpecification(910, xfy, Notation.INFIX)},
	    {LogicFunctions.xor,          // "^"
	     new NotationSpecification(914, xfy, Notation.INFIX)},
	    {op.or(),           // "|"
	     new NotationSpecification(916, xfy, Notation.INFIX)},
	    {op.impl(),         // "->"
	     new NotationSpecification(920, "xfx", Notation.INFIX)},
	    {LogicFunctions.reverseImpl, // "<-"
	     new NotationSpecification(920, "xfx", Notation.INFIX)},
	    {LogicFunctions.equiv,        // "<->"
	     new NotationSpecification(920, xfy, Notation.INFIX)}
	}, true, false, true);
	this._coreSignature = _coreInterpretation.getSignature();
    }

    public String toString() {
	return getClass().getName() + '[' + fuzzyLogicOperators + ']';
    }

    /**
     * facade for convenience.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade</a>
     */
    public boolean infer(String expression, String exprDerived) throws ParseException {
	Formula B[] = (Formula[]) Arrays.asList(createAllExpressions(expression)).toArray(new Formula[0]);
	Formula D = (Formula) createExpression(exprDerived);
	System.err.println(B.length > 0 ? B[0] + " is interpreted to " + B[0].apply(null) : "");
	System.err.println(D + " is interpreted to " + D.apply(null));
	return inference().infer(B, D);
    } 

    public boolean satisfy(Interpretation I, Formula F) {
	if (F == null)
	    throw new NullPointerException("null is not a formula");
	assert F instanceof ModernFormula && getClass().isInstance(((ModernFormula)F).getUnderlyingLogic()) : "F is a formula in this logic";
        // assure core interpretation unless overwritten
        I = new QuickUnitedInterpretation(_coreInterpretation, I);
	return MathUtilities.equals(((Number) F.apply(I)).doubleValue(), 1.0, 0.001);
    } 

    public Inference inference() {
	throw new InternalError("no calculus implemented. Only use explicit interpretation");
    } 

    public Signature coreSignature() {
	return _coreSignature;
    } 
    public Interpretation coreInterpretation() {
	return _coreInterpretation;
    }


    
    private Formula createFormula(String expression) throws ParseException {
	return (Formula) createExpression(expression);
    }

    // Helpers
    
    /**
     * interpretation for a truth-value
     */
    static final Object getInt(double v) {
	return (Number) valueFactory.valueOf(v);
    } 
    
    /**
     * truth-value of a Formulas Interpretation
     */
    static final double getTruth(Object f) {
	return ((Number) f).doubleValue();
    } 
    
    
    /**
     * Specifies the type of fuzzy logic to use.
     * Instances will define the set of fuzzy logic operators applied.
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see <a href="{@docRoot}/Patterns/Design/enum.html">typesafe enum pattern</a>
     * @internal typesafe enumeration pattern class to specify fuzzy logic operators
     * @invariants a.equals(b) &hArr; a==b
     * @todo improve name
     */
    public static abstract class OperatorSet implements Serializable, Comparable {
	private static final long serialVersionUID = -3938437045097544303L;
	/**
	 * the name to display for this enum value
	 * @serial
	 */
	private final String	  name;

	/**
	 * Ordinal of next enum value to be created
	 */
	private static int	  nextOrdinal = 0;

	/**
	 * Table of all canonical references to enum value classes.
	 */
	private static OperatorSet[] values = new OperatorSet[MAX_OPERATORS];

	/**
	 * Assign an ordinal to this enum value
	 * @serial
	 */
	private final int	  ordinal = nextOrdinal++;

	protected OperatorSet(String name) {
	    this.name = name;
	    values[nextOrdinal - 1] = this;
	}

	/**
	 * Order imposed by ordinals according to the order of creation.
	 * @postconditions consistent with equals
	 */
	public int compareTo(Object o) {
	    return ordinal - ((OperatorSet) o).ordinal;
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
	private Object readResolve() throws ObjectStreamException {
	    // canonicalize
	    return values[ordinal];
	} 

	/**
	 * Defines the NOT operator to use in the fuzzy logic.
	 * @postconditions RES==OLD(RES)
	 */
	public abstract Function not();

	/**
	 * Defines the fuzzy AND operator to use in the fuzzy logic.
	 * @postconditions RES==OLD(RES)
	 */
    	public abstract BinaryFunction and();
    
	/**
	 * Defines the fuzzy OR operator to use in the fuzzy logic.
	 * @postconditions RES==OLD(RES)
	 */
    	public abstract BinaryFunction or();

	/**
	 * Defines the implication operator to use in the fuzzy logic.
	 * a&rarr;b := sup{c &brvbar; a&#8911;c&le;b}.
	 * @postconditions RES==OLD(RES)
	 */
    	public abstract BinaryFunction impl();
    }

    // enumeration of fuzzy logic operators

    /**
     * G&ouml;del and Zadeh operators in fuzzy logic (default).
     * <div>a &#8911; b = min{a,b}</div>
     * <div>a &#8910; b = max{a,b}</div>
     * <div>a &rarr; b = b if a&gt;b, resp. =1 if a&le;b</div>
     * G&ouml;del operators are the "outer" bound of fuzzy logic operators, i.e.
     * min is the greatest fuzzy AND operator,
     * and max the smallest fuzzy OR operator.
     * <p>
     * <h5><!-- @todo quote --> principle of minimum specificity</h5>
     * <blockquote>
     *   
     * </blockquote>
     * &rArr; in the absence of further knowledge, choose G&ouml;del operators.
     */
    public static OperatorSet GOEDEL = new OperatorSet("Gödel") {
	    private static final long serialVersionUID = 2408339318090056142L;
	    public Function not() {
		return LogicFunctions.not;
	    }

	    public BinaryFunction and() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object a, Object b) {
			    return getInt(Math.min(getTruth(a), getTruth(b)));
			}
			public String toString() { return "&"; }
		    };
	    }
    
	    public BinaryFunction or() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object a, Object b) {
			    return getInt(Math.max(getTruth(a), getTruth(b)));
			}
			public String toString() { return "|"; }
		    };
	    }

	    public BinaryFunction impl() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object wa, Object wb) {
			    final double a = getTruth(wa);
			    final double b = getTruth(wb);
			    return getInt(b < a ? b : 1);
			}
			public String toString() { return "->"; }
		    };
	    }
	};

    /**
     * Product operators in fuzzy logic.
     * <div>a &#8911; b = a&sdot;b</div>
     * <div>a &#8910; b = a+b - a&sdot;b</div>
     * <div>a &rarr; b = min{b/a,1}, resp. =1 for a=0</div>
     */
    public static OperatorSet PRODUCT = new OperatorSet("Product") {
	    private static final long serialVersionUID = 1914120346137890612L;
	    private static final double tolerance = 0.000001;
	    public Function not() {
		return LogicFunctions.not;
	    }

	    public BinaryFunction and() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object a, Object b) {
			    return getInt(getTruth(a) * getTruth(b));
			}
			public String toString() { return "&"; }
		    };
	    }
    
	    public BinaryFunction or() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object wa, Object wb) {
			    final double a = getTruth(wa);
			    final double b = getTruth(wb);
			    return getInt(a + b - a * b);
			}
			public String toString() { return "|"; }
		    };
	    }

	    public BinaryFunction impl() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object wa, Object wb) {
			    final double a = getTruth(wa);
			    final double b = getTruth(wb);
			    return getInt(MathUtilities.equals(a, 0, tolerance) ? 1 : Math.min(b/a,1));
			}
			public String toString() { return "->"; }
		    };
	    }
	};

    /**
     * Bounded or &#407;ukasiewicz operators in fuzzy logic.
     * Which come from the implication &rArr; in the &#407;ukasiewicz-logic, by
     * a&rArr;b &equiv; &not;(a&#8911;&not;b), as well as a&#8910;b &equiv; &not;a&rArr;b.
     * <div>a &#8911; b = max{0,a+b-1}</div>
     * <div>a &#8910; b = min{1,a+b}</div>
     * <div>a &rarr; b = a &rArr; b = min{1,b-a+1}</div>
     */
    public static OperatorSet BOUNDED = new OperatorSet("Bounded") {
	    private static final long serialVersionUID = 2512028904916107754L;
	    public Function not() {
		return LogicFunctions.not;
	    }

	    public BinaryFunction and() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object a, Object b) {
			    return getInt(Math.max(0, getTruth(a) + getTruth(b) - 1));
			}
			public String toString() { return "&"; }
		    };
	    }
    
	    public BinaryFunction or() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object a, Object b) {
			    return getInt(Math.min(1, getTruth(a) + getTruth(b)));
			}
			public String toString() { return "|"; }
		    };
	    }

	    public BinaryFunction impl() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object a, Object b) {
			    return getInt(Math.min(1, getTruth(b) - getTruth(a) + 1));
			}
			public String toString() { return "->"; }
		    };
	    }
	};

    /**
     * Hamacher operators in fuzzy logic.
     * <div class="Formula">a &#8911; b = a&sdot;b / <big>(</big>&gamma;+(1-&gamma;)(a+b-a&sdot;b)<big>)</big></div>
     * <div class="Formula">a &#8910; b = <big>(</big>a+b-(2-&gamma;)a&sdot;b<big>)</big> / <big>(</big>1-(1-&gamma;)a&sdot;b<big>)</big></div>
     * @param gamma the parameter &gamma;.
     * @preconditions gamma&ge;0
     */
    public static OperatorSet HAMACHER(final double gamma) {
	if (!(gamma >= 0))
	    throw new IllegalArgumentException("illegal value for gamma: " + gamma + " < 0");
	return gamma == 0
	    ? (OperatorSet)
	    new OperatorSet("Hamacher(0)") {
		// special case handling polarities for gamma=0
		private static final double tolerance = 0.000001;
		public Function not() {
		    return LogicFunctions.not;
		}

		public BinaryFunction and() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object wa, Object wb) {
				final double a = getTruth(wa);
				final double b = getTruth(wb);
				final double ab = a*b;
				return getInt(MathUtilities.equals(a, 0, tolerance) && MathUtilities.equals(b, 0, tolerance)
					      ? 0
					      : ab / (a+b-ab));
			    }
			    public String toString() { return "&"; }
			};
		}
    
		public BinaryFunction or() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object wa, Object wb) {
				final double a = getTruth(wa);
				final double b = getTruth(wb);
				final double ab = a*b;
				return getInt(MathUtilities.equals(a, 1, tolerance) && MathUtilities.equals(b, 1, tolerance)
					      ? 1
					      : (a+b-2*ab) / (1 - ab));
			    }
			    public String toString() { return "|"; }
			};
		}

		public BinaryFunction impl() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object a, Object b) {
				throw new UnsupportedOperationException(this + " not yet implemented for HAMACHER");
			    }
			    public String toString() { return "->"; }
			};
		}
	    }
	    : (OperatorSet)
	    new OperatorSet("Hamacher(" + gamma + ")") {
		private static final long serialVersionUID = -8210989001070817280L;
		public Function not() {
		    return LogicFunctions.not;
		}

		public BinaryFunction and() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object wa, Object wb) {
				final double a = getTruth(wa);
				final double b = getTruth(wb);
				final double ab = a*b;
				return getInt(ab / (gamma + (1-gamma)*(a+b-ab)));
			    }
			    public String toString() { return "&"; }
			};
		}
    
		public BinaryFunction or() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object wa, Object wb) {
				final double a = getTruth(wa);
				final double b = getTruth(wb);
				final double ab = a*b;
				return getInt((a+b-(2-gamma)*ab) / (1 - (1-gamma)*ab));
			    }
			    public String toString() { return "|"; }
			};
		}

		public BinaryFunction impl() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object a, Object b) {
				throw new UnsupportedOperationException(this + " not yet implemented for HAMACHER");
			    }
			    public String toString() { return "->"; }
			};
		}
	    };
    }

    /**
     * Yager operators in fuzzy logic.
     * <div class="Formula">a &#8911; b = 1 - min<big>{</big>1,<big>(</big>(1-a)<sup>p</sup>+(1-b)<sup>p</sup>)<big>)</big><sup>1/p</sup><big>}</big></div>
     * <div class="Formula">a &#8910; b = min<big>{</big>1,<big>(</big>a<sup>p</sup>+b<sup>p</sup>)<big>)</big><sup>1/p</sup><big>}</big></div>
     * For p&rarr;&infin; these operators approximate those of {@link #GOEDEL}.
     * @preconditions p&gt;0
     */
    public static OperatorSet YAGER(final double p) {
	if (!(p > 0))
	    throw new IllegalArgumentException("illegal parameter: " + p + " =< 0");
	final double inverse_p = 1/p;
	return new OperatorSet("Yager(" + p + ")") {
		private static final long serialVersionUID = 5886310887805210830L;
		public Function not() {
		    //@internal there also is a Yager complement (1-a<sup>p</sup>)<sup>1/p</sup>, but the ususal complement satisfies the duality. (There are even more fuzzy NOT operators: drastic, continuous fuzzy complement, Sugeno, Yager, and the natural complement)
		    return LogicFunctions.not;
		}

		public BinaryFunction and() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object wa, Object wb) {
				final double a = getTruth(wa);
				final double b = getTruth(wb);
				return getInt(1 - Math.min(1,Math.pow(Math.pow(1-a,p)+Math.pow(1-b,p),inverse_p)));
			    }
			    public String toString() { return "&"; }
			};
		}
    
		public BinaryFunction or() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object wa, Object wb) {
				final double a = getTruth(wa);
				final double b = getTruth(wb);
				return getInt(Math.min(1,Math.pow(Math.pow(a,p)+Math.pow(b,p),inverse_p)));
			    }
			    public String toString() { return "|"; }
			};
		}

		public BinaryFunction impl() {
		    return new BinaryFunction() {
			    private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			    public Object apply(Object a, Object b) {
				throw new UnsupportedOperationException(this + " not yet implemented for YAGER");
			    }
			    public String toString() { return "->"; }
			};
		}
	    };
    }

    /**
     * Drastic operators in fuzzy logic.
     * <div>a &#8911; b = i<sup>*</sup>(a,b) := min{a,b} if max{a,b}=1, else 0</div>
     * <div>a &#8910; b = u<sup>*</sup>(a,b) := max{a,b} if min{a,b}=0, else 1</div>
     * <div>a &rarr; b = b if b&lt;a=1, else 1</div>
     * Drastic operators are the "inner" bound of fuzzy logic operators, i.e.
     * i<sup>*</sup> is the smallest fuzzy AND operator,
     * and u<sup>*</sup> the greatest fuzzy OR operator.
     * @attribute discontinuous
     */
    public static OperatorSet DRASTIC = new OperatorSet("Drastic") {
	    private static final long serialVersionUID = -2065043465614357255L;
	    public Function not() {
		return LogicFunctions.not;
	    }

	    public BinaryFunction and() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object wa, Object wb) {
			    final double a = getTruth(wa);
			    final double b = getTruth(wb);
			    return getInt(a == 1.0 || b == 1.0 ? Math.min(a, b) : 0);
			}
			public String toString() { return "&"; }
		    };
	    }
    
	    public BinaryFunction or() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object wa, Object wb) {
			    final double a = getTruth(wa);
			    final double b = getTruth(wb);
			    return getInt(a == 0.0 || b == 0.0 ? Math.max(a, b) : 1);
			}
			public String toString() { return "|"; }
		    };
	    }

	    public BinaryFunction impl() {
		return new BinaryFunction() {
			private final Type logicalTypeDeclaration = LogicFunctions.BINARY_LOGICAL_JUNCTOR;
			public Object apply(Object wa, Object wb) {
			    final double a = getTruth(wa);
			    final double b = getTruth(wb);
			    return getInt(b < a && a == 1.0 ? b : 1);
			}
			public String toString() { return "->"; }
		    };
	    }
	};


    static class LogicFunctions {
        private LogicFunctions() {}
    
	private static final Type UNARY_LOGICAL_JUNCTOR = typeSystem.map(TRUTH, TRUTH);
	private static final Type BINARY_LOGICAL_JUNCTOR = typeSystem.map(typeSystem.product(new Type[] {TRUTH, TRUTH}), TRUTH);

	// Basic logical operations (elemental junctors).
    	public static final Function not = new Function() {
		private final Type logicalTypeDeclaration = UNARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a) {
		    return getInt(1 - getTruth(a));
        	}
		public String toString() { return "~"; }
	    }; 

	// Derived logical operations.

	//TODO: The following functions for derived logical operations could be generalized perhaps (see LogicBasis)
    	public static final BinaryFunction xor = null;

    	//@todo how about =< as an implementation of the implication in fuzzy logic?
    	public static final BinaryFunction impl = null;

    	public static final BinaryFunction reverseImpl = null;

    	public static final BinaryFunction equiv = null;

	// Basic logical operations (elemental quantifiers).

    	public static final BinaryFunction forall = null;

    	public static final BinaryFunction exists = null;
    }
}
