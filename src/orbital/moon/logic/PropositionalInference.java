/**
 * @(#)PropositionalInference.java 1.1 2002-11-29 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;
import orbital.moon.logic.ClassicalLogic.Utilities;
import orbital.logic.imp.*;

import java.util.*;
import orbital.util.Setops;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Specialized propositional logic inference.
 * Implemented by Davis-Putnam-Loveland algorithm.
 *
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version 1.1, 2002-11-29
 */
public class PropositionalInference implements Inference {
    /**
     * Whether or not to use simplified clausal forms.
     */
    private static final boolean SIMPLIFYING = false;
    private static final Logger logger = Logger.getLogger(PropositionalInference.class.getPackage().getName());

    public PropositionalInference() {
	
    }
    public boolean infer(Formula[] B, Formula D) {
        // convert B to clausalForm
        Set/*_<Set<Formula>>_*/ S = new LinkedHashSet();
        for (Iterator i = Arrays.asList(B).iterator(); i.hasNext(); ) {
	    S.addAll(Utilities.clausalForm((Formula) i.next(), SIMPLIFYING));
	}

	//@todo could we remove tautologies?

        logger.log(Level.FINE, "W = {0}", S);
        if (logger.isLoggable(Level.FINEST))
	    for (int i = 0; i < B.length; i++)
		logger.log(Level.FINEST, "W thus contains transformation of original formula {0}", Utilities.conjunctiveForm(B[i], SIMPLIFYING));

        // negate query since we are a negative test calculus
        Formula query = D.not();

        // convert (negated) query to clausalForm S
	Set queryClauses = Utilities.clausalForm(query, SIMPLIFYING);

	//@todo could we remove tautologies from query?
	logger.log(Level.FINE, "negated goal = {0} = {1}", new Object[] {query, queryClauses});
	//@internal if we don't copy the set, then formatting would need copies of the (mutable) sets.
	return refute(Setops.union(S, queryClauses));
    }
    public boolean isSound() {
	return true;
    } 
    public boolean isComplete() {
	//@internal for propositional logic
	return true;
    }

    /**
     * @param S the set of clauses to refute. may be changed by this method.
     * @return <code>false</code> if S is satisfiable,
     *  <code>true</code> if S is unsatisfiable (due to refutation).
     */
    private boolean refute(Set/*_<Set<Formula>>_*/ S) {
        logger.log(Level.FINE, "S = {0}", new LinkedHashSet(S));
	if (S.isEmpty()) {
	    logger.log(Level.FINE, "satisfiable S = {0}", new LinkedHashSet(S));
	    return false;
	}
	// search for any unit clause
	for (Iterator i = S.iterator(); i.hasNext(); ) {
	    Set/*_<Formula>_*/ C = (Set)i.next();
	    assert C.size() > 0 : "contradictions will have been detected earlier";
	    if (C.size() != 1)
		continue;
	    Formula literalC = (Formula) C.iterator().next();
	    S = reduce(literalC, S);
	    if (S.contains(ClassicalLogic.Utilities.CONTRADICTION)) {
		logger.log(Level.FINE, "unsatisfiable S = {0}", new LinkedHashSet(S));
		return true;
	    }
	    //@internal restarting loop would suffice @todo optimize by i = S.iterator();
	    return refute(S);
	}

	// no unit clause, so choose an atom P
	//@internal we better choose a literal literalP and rely on duplex negation est affirmatio
	Formula literalP = (Formula) ((Set)S.iterator().next()).iterator().next();
        logger.log(Level.FINER, "choose unit clause {0}", literalP);
	return refute(Setops.union(S, Collections.singleton(Collections.singleton(literalP))))
	    && refute(Setops.union(S, Collections.singleton(Collections.singleton(Resolution.negation(literalP)))));
    }

    /**
     * @param S will be changed
     */
    private Set/*_<Set<Formula>>_*/ reduce(final Formula C, Set/*_<Set<Formula>>_*/ S) {
        logger.log(Level.FINER, "reduce({0}, {1})", new Object[] {C, new LinkedHashSet(S)});
	final Formula notC = Resolution.negation(C);
	for (Iterator i = S.iterator(); i.hasNext(); ) {
	    Set/*_<Formula>_*/ clause = (Set)i.next();
	    if (clause.contains(C)) {
		logger.log(Level.FINEST, "remove clause {0}", new LinkedHashSet(clause));
		i.remove();
	    } else if (clause.remove(notC)) {
		//@internal modifying clause also has effect in S since S contains clause (by reference)
		logger.log(Level.FINEST, "remove literal {0} retaining clause {1}", new Object[] {notC, new LinkedHashSet(clause)});
	    } else {
		logger.log(Level.FINEST, "leave clause {0}", new LinkedHashSet(clause));
	    }
	}
        logger.log(Level.FINER, "reduce({0}, ...) = {1}", new Object[] {C, new LinkedHashSet(S)});
	return S;
    }

    //@todo remove
    /*private Formula atomInLiteral(Formula literal) {
	// used duplex negatio est affirmatio (optimizable)
	if ((literal instanceof Composite)) {
	    Composite f = (Composite) literal;
	    Object    g = f.getCompositor();
	    if (g == ClassicalLogic.LogicFunctions.not)
		// use duplex negatio est affirmatio to avoid double negations
		return (Formula) f.getComponent();
	}
	return literal;
	}*/
}// PropositionalInference
