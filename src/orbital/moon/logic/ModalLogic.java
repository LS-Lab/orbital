/*
 * @(#)ModalLogic.java 1.1 2002-11-23 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;
import orbital.logic.imp.ParseException;
import orbital.logic.functor.Functor;
import orbital.logic.functor.Functor.Composite;
import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;
import orbital.logic.trs.*;
import orbital.moon.logic.bridge.SubstitutionImpl.MatcherImpl;
import orbital.moon.logic.bridge.SubstitutionImpl.UnifyingMatcher;

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

import orbital.math.MathUtilities;
import orbital.io.IOUtilities;
import orbital.util.InnerCheckedException;
import java.beans.IntrospectionException;

import orbital.logic.functor.Notation;
import orbital.logic.functor.Notation.NotationSpecification;

import orbital.util.Utility;

import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A ModalLogic class that represents modal logic.
 * 
 * @version 1.1, 2002-11-23
 * @author  Andr&eacute; Platzer
 */
public class ModalLogic extends ClassicalLogic {
    /**
     * tool-main
     * @todo parse arguments in order to obtain OperatorSet used
     */
    public static void main(String arg[]) throws Exception {
	if (orbital.signe.isHelpRequest(arg)) {
	    System.out.println(usage);
	    System.out.println("Core operators:\n\t" + new ModalLogic().coreSignature());
	    return;
	} 
	ModalLogic logic = new ModalLogic();
	Reader rd = null;
	try {
	    rd = new InputStreamReader(System.in);
	    proveAll(rd, logic, true);
	}
	finally {
	    if (rd != null)
		rd.close();
	}
    } 
    public static final String usage = "interpret modal logic";

    private static final Logger logger = Logger.getLogger(ModalLogic.class.getName());

    private static final Type   WORLD = //@xxx Types.objectType(new Object() {}.getClass(), "world");
	Types.INDIVIDUAL;
    private static final Symbol CURRENT_WORLD = new SymbolBase("s", WORLD, null, true);
    private final Formula CURRENT_WORLD_form;
    private static final Symbol ACCESSIBLE = new SymbolBase("R", Types.predicate(Types.product(new Type[] {WORLD,WORLD})), null, false);
    private final Formula ACCESSIBLE_form;

    //@todo remove this bugfix that replaces "xfy" by "yfy" associativity only for *.jj parsers to work without inefficient right-associative lookahead.
    private static final String xfy = "yfy";

    private /*static*/ final Interpretation _coreInterpretation;
    private /*static*/ final Signature _coreSignature;

    private final ClassicalLogic classical;
    public ModalLogic() {
	this.classical = new ClassicalLogic(ClassicalLogic.RESOLUTION_INFERENCE);
	_coreInterpretation =
	    LogicSupport.arrayToInterpretation(new Object[][] {
		{LogicFunctions.necessary,          // "[]"
		 new NotationSpecification(900, "fy", Notation.PREFIX)},
		{LogicFunctions.possible,           // "<>"
		 new NotationSpecification(900, "fy", Notation.PREFIX)}
	    }, true, false, true).union(new ClassicalLogic().coreInterpretation());
	_coreSignature = _coreInterpretation.getSignature();
	this.CURRENT_WORLD_form = (Formula) createAtomic(CURRENT_WORLD);
	this.ACCESSIBLE_form = (Formula) createAtomic(ACCESSIBLE);
    }

    /**
     * facade for convenience.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade</a>
     */
    public boolean infer(String expression, String exprDerived) throws ParseException {
	Formula B[] = (Formula[]) Arrays.asList(createAllExpressions(expression)).toArray(new Formula[0]);
	Formula D = (Formula) createExpression(exprDerived);
	return inference().infer(B, D);
    } 

    public boolean satisfy(Interpretation I, Formula F) {
        // assure core interpretation unless overwritten
        I = new QuickUnitedInterpretation(_coreInterpretation, I);
	//@internal "modal formulas only have a semantic value with respect to a possibly infinite set of possible worlds along with an accessibility relation. They are available for inference, but they cannot be interpreted with finite means."
	return classical.satisfy(I, F);
    } 

    public Inference inference() {
	return _reductionInference;
    }
    /**
     * reduces formulas to classical FOL and the use classical.inference()
     */
    private final Inference _reductionInference = new Inference() {
	    public boolean infer(Formula[] B, Formula D) {
	    final Inference classicalInference = classical.inference();
		Formula[] Bred = new Formula[B.length];
		for (int i = 0; i < B.length; i++) {
		    Bred[i] = Utilities.modalReduce(B[i]);
		    if (i > 0 )
			System.err.print(", ");
		    System.err.println(Bred[i]);
		}
		Formula Dred = Utilities.modalReduce(D);
		System.err.println(" |-<red> " + Dred + " ??");
		return classicalInference.infer(Bred, Dred);
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

    
    // Helpers
    
    static class LogicFunctions extends ClassicalLogic.LogicFunctions {
        private LogicFunctions() {}
    
	private static final Type UNARY_LOGICAL_JUNCTOR = Types.map(Types.TRUTH, Types.TRUTH);
	private static final Type BINARY_LOGICAL_JUNCTOR = Types.map(Types.product(new Type[] {Types.TRUTH, Types.TRUTH}), Types.TRUTH);

    	public static final Function necessary = new Function() {
		private final Type logicalTypeDeclaration = UNARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a) {
		    throw new LogicException("modal formulas only have a semantic value with respect to a possibly infinite set of possible worlds along with an accessibility relation. They are available for inference, but they cannot be interpreted with finite means.");
        	}
		public String toString() { return "[]"; }
	    }; 
    	public static final Function possible = new Function() {
		private final Type logicalTypeDeclaration = UNARY_LOGICAL_JUNCTOR;
    		public Object apply(Object a) {
		    throw new LogicException("modal formulas only have a semantic value with respect to a possibly infinite set of possible worlds along with an accessibility relation. They are available for inference, but they cannot be interpreted with finite means.");
        	}
		public String toString() { return "<>"; }
	    }; 
    }


    /**
     * Formula transformation utilities.
     * @stereotype &laquo;Utilities&raquo;
     * @stereotype &laquo;Module&raquo;
     * @version 1.1, 2002-11-24
     * @author  Andr&eacute; Platzer
     * @see orbital.util.Utility
     */
    private static final class Utilities {
	private static final ModalLogic logic = new ModalLogic();
	/**
	 * prevent instantiation - module class.
	 */
	private Utilities() {}

	/**
	 * Reduces a formula of quantified modal logic to classical first-order logic.
	 * <ul>
	 *   <li>red(p(t1,....,tn)) = p(t1,...,tn,s)&w(s) if p is a predicate.</li>
	 *   <li>red(A&and;B) = red(A)&and;red(B)</li>
	 *   <li>red(A&or;B) = red(A)&or;red(B)</li>
	 *   <li>red(&not;A) = &not;red(A)</li>
	 *   <li>red(&forall;x A) = &forall;x (&not;w(x)&rarr;red(A))</li>
	 *   <li>red(&exist;x A) = &exist;x (&not;w(x)&and;red(A))</li>
	 *   <li>red(&#9633;x A) = &forall;t:world (R(s,t)&rarr;(red(A)[s&#8614;t])) where t is new in A.</li>
	 *   <li>red(&#9671;x A) = &exist;t:world (R(s,t)&and;(red(A)[s&#8614;t]) where t is new in A.</li>
	 * </ul>
	 * @xxx better could also transform
	 *   <li>red(p(t1,....,tn)) = p(s)(t1,...,tn)&w(s) if p is a predicate.</li>
	 *  which would be better by far, but Resolution can't handle it.
	 *  the reduced p would then have the type WORLD&rarr;p.getType()
	 */
	private static final Formula modalReduce(Formula F) {
	    try {
		if (modalReduceTransform == null)
		    modalReduceTransform = Substitutions.getInstance(Arrays.asList(new Object[] {
			//@xxx note that A should be a metavariable for a formula
			new NecessaryUnifyingMatcher(logic.createExpression("[](_A)"), logic.createExpression("_A")),
			new PossibleUnifyingMatcher(logic.createExpression("<>(_A)"), logic.createExpression("_A")),
			new ContextualizeUnifyingMatcher(),
		    }));
		return (Formula) modalReduceTransform.apply(F);
	    } catch (ParseException ex) {
		throw (InternalError) new InternalError("Unexpected syntax in internal term").initCause(ex);
	    }
	}

	// lazy initialized cache for TRS rules
	private static Substitution modalReduceTransform;

	/**
	 * Unifying matcher that adds context arguments to predicates.
	 * <p>
	 * p(t1,....,tn) &#8614; p(t1,...,tn,s)
	 * </p>
	 *
	 * @version 1.1, 2002-11-23
	 * @author  Andr&eacute; Platzer
	 */
	private static class ContextualizeUnifyingMatcher extends MatcherImpl {
	    //private static final long serialVersionUID = 0;
	    private static final Set coreReferents = new HashSet(logic.coreInterpretation().values());
	    public ContextualizeUnifyingMatcher() {
		super("<predicate application>", "<contextualized>");
	    }

	    public boolean matches(Object term) {
		if (term instanceof Formula) {
		    if (term instanceof Functor.Composite) {
			Functor.Composite f = (Functor.Composite) term;
			Functor           op = (Functor) f.getCompositor();
			if (coreReferents.contains(op))
			    // do not change elements of the coreSignature()
			    return false;
			if (op instanceof Formula) {
			    // op has type =< ABSURD&rarr;TRUTH
			    if (((Formula)op).getType().codomain().subtypeOf(Types.TRUTH))
				if (op instanceof ModernFormula.AtomicSymbol)
				    // only match predicates
				    return true;
			}
			return false;
		    } else
			// term has type =< ABSURD&rarr;TRUTH
			if (((Formula)term).getType().codomain().subtypeOf(Types.TRUTH))
			    if (term instanceof ModernFormula.AtomicSymbol)
				// only match predicates
				return true;
		}
		return false;
	    }
	    public Object replace(Object term) {
		Expression[] arg;
		Symbol       p;
		if (term instanceof Functor.Composite) {
		    Functor.Composite f = (Functor.Composite) term;
		    Functor           op = (Functor) f.getCompositor();
		    arg = asExpressionArray(f.getComponent());
		    p = ((ModernFormula.AtomicSymbol)op).getSymbol();
		} else {
		    arg = new Expression[0];
		    p = ((ModernFormula.AtomicSymbol)term).getSymbol();
		}

		try {
		    Expression[]      argWithContext = new Expression[arg.length + 1];
		    System.arraycopy(arg, 0, argWithContext, 0, arg.length);
		    argWithContext[argWithContext.length - 1] = logic.CURRENT_WORLD_form;
		    Type tau = p.getType().domain();
		    NotationSpecification notat = p.getNotation();
		    Symbol pmod =
			new SymbolBase(p.getSignifier(),
				       //@fixme type extension does not truely work for tau = product type
				       Types.map(Types.product(new Type[] {tau, WORLD}), p.getType().codomain()),
				       new NotationSpecification(notat.getPrecedence(),
								 notat.getAssociativity() + "x",
								 notat.getNotation()),
				       p.isVariable());
		    return logic.compose(logic.createAtomic(pmod), argWithContext);
		} catch (ParseException ex) {
		    throw (InternalError) new InternalError("Unexpected syntax in internal term construction").initCause(ex);
		}
	    }

	    private static final Expression[] asExpressionArray(Object o) {
		if (o instanceof Expression[])
		    return (Expression[])o;
		else if (o instanceof Expression)
		    return new Expression[] {(Expression)o};
		else if (o instanceof Object[] && ((Object[])o).length == 0)
		    return new Expression[] {};
		else
		    throw new IllegalArgumentException(o + " of " + o.getClass());
	    }
	}

	/**
	 * Unifying matcher that makes a variable unique.
	 * <ul>
	 *   <li>red(&#9633;x A) = &forall;t:world (R(s,t)&rarr;(red(A)[s&#8614;t])) where t is new in A.</li>
	 * </ul>
	 * @version 1.1, 2002-11-23
	 * @author  Andr&eacute; Platzer
	 */
	private static class NecessaryUnifyingMatcher extends UnifyingMatcher {
	    //private static final long serialVersionUID = 0;
	    public NecessaryUnifyingMatcher(Object pattern, Object substitute) {
		super(pattern, substitute);
	    }

	    public Object replace(Object term) {
		Formula A_original = (Formula) super.replace(term);
		Formula A_red = modalReduce(A_original);
		// make a new variable that does not occur in A
		final Symbol t = new UniqueSymbol("t", WORLD, null, true);
		// formula version of t
		final Formula t_form = (Formula) logic.createAtomic(t);
		// A := A_original[s&#8614;t]
		Formula A = (Formula) Substitutions.getInstance(Collections.singletonList(
										  Substitutions.createExactMatcher(logic.CURRENT_WORLD_form, t_form)
										  )
							).apply(A_red);
		logger.log(Level.FINE, "reduce matched {0} with mgu={1} corresponds to red({2})={3} which world-transformed to {4}", new Object[] {term, getUnifier(), A_original, A_red, A});

		try {
		    // return &forall;t:world (R(s,t)->A)
		    Formula R_st = (Formula) logic.compose(logic.ACCESSIBLE_form, new Expression[] {
			logic.CURRENT_WORLD_form,
			t_form
		    });
		    return R_st.impl(A).forall(t);
		} catch (ParseException ex) {
		    throw (InternalError) new InternalError("Unexpected syntax in internal term construction").initCause(ex);
		}
	    }
	}

	/**
	 * Unifying matcher that makes a variable unique.
	 * <ul>
	 *   <li>red(&#9671;x A) = &exist;t:world (R(s,t)&and;(red(A)[s&#8614;t]) where t is new in A.</li>
	 * </ul>
	 * @version 1.1, 2002-11-23
	 * @author  Andr&eacute; Platzer
	 */
	private static class PossibleUnifyingMatcher extends UnifyingMatcher {
	    //private static final long serialVersionUID = 0;
	    public PossibleUnifyingMatcher(Object pattern, Object substitute) {
		super(pattern, substitute);
	    }

	    public Object replace(Object term) {
		Formula A_original = (Formula) super.replace(term);
		Formula A_red = modalReduce(A_original);
		// make a new variable that does not occur in A
		final Symbol t = new UniqueSymbol("t", WORLD, null, true);
		// formula version of t
		final Formula t_form = (Formula) logic.createAtomic(t);
		// A := A_original[s&#8614;t]
		Formula A = (Formula) Substitutions.getInstance(Collections.singletonList(
										  Substitutions.createExactMatcher(logic.CURRENT_WORLD_form, t_form)
										  )
							).apply(A_red);
		logger.log(Level.FINE, "reduce matched {0} with mgu={1} corresponds to red({2})={3} which world-transformed to {4}", new Object[] {term, getUnifier(), A_original, A_red, A});

		try {
		    // return &exist;t:world (R(s,t)&A)
		    Formula R_st = (Formula) logic.compose(logic.ACCESSIBLE_form, new Expression[] {
			logic.CURRENT_WORLD_form,
			t_form
		    });
		    return R_st.and(A).exists(t);
		} catch (ParseException ex) {
		    throw (InternalError) new InternalError("Unexpected syntax in internal term construction").initCause(ex);
		}
	    }
	}
    }
}
