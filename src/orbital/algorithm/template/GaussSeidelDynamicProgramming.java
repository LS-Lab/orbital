/**
 * @(#)GaussSeidelDynamicProgramming.java 1.0 2001/06/10 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;

import orbital.logic.functor.BinaryFunction;

import orbital.logic.functor.MutableFunction;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import java.util.HashMap;
import orbital.util.Pair;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Gau&szlig;-Seidel Dynamic Programming.
 * <p>
 * Gau&szlig;-Seidel Dynamic Programming is a variant of synchronous Dynamic Programming performing
 * value iteration in a sequential "sweep" of all the states after each state backup.
 * Due to this fact, it can even be seen half-way as asynchronous Dynamic Programming.
 * For the same reason, Gau&szlig;-Seidel Dynamic Programming usually converges faster than
 * sychronous Dynamic Programming.</p>
 * <p>
 * Gau&szlig;-Seidel Dynamic Programming permanently uses a dynamic programming variant of value iteration
 * for the sequence of utility functions U<sub>k</sub>.
 * <center>
 *   <span class="Formula">U<sub>k+1</sub>(s) := min<sub>a&isin;A(s)</sub> Q<sub>U</sub>(s,a)</span>
 * </center>
 * where
 * <center>
 *   <span class="Formula">U(t) := U<sub>k+1</sub>(t) &lArr; t&lt;s, U(t) := U<sub>k</sub>(t) &lArr; t&ge;s</span>
 * </center>
 * </p>
 *
 * @invariant getDiscount()&isin;[0,1]
 * @version 1.0, 2001/06/10
 * @author  Andr&eacute; Platzer
 * @see DynamicProgramming
 * @see "A. Barto, S. Bradtke, and S. Singh. Learning to act using real-time dynamic programming. <i>Artificial Intelligence</i>, 72:81-138, 1995."
 */
public class GaussSeidelDynamicProgramming extends MarkovDecisionProcess.DynamicProgramming implements HeuristicAlgorithm {
    private static final Logger logger = Logger.getLogger(GaussSeidelDynamicProgramming.class.getName());
    private double tolerance;
    private Collection states;
    /**
     * @param states the full set S of <em>all</em> states of the problem.
     * @param tolerance the tolerance value below which the evaluation function is considered
     *  to have converged.
     */
    public GaussSeidelDynamicProgramming(Function heuristic, Collection states, double tolerance) {
	super(heuristic);
	this.states = states;
	this.tolerance = tolerance;
    }
    
    protected Function plan() {
	/**
	 * estimates U of optimal value function h<sup>*</sup>:S&rarr;<b>R</b>.
	 * If h is admissible U will converge (monotonically) up to h<sup>*</sup>.
	 * Updated via DP on current states, instead of value iteration on each state until convergence.
	 */
	final MutableFunction U = createMap();
	final BinaryFunction Q = getActionValue(U);
	// explicitly initialize U(s) = h(s)
	/*for (Iterator i = problem.getStates().iterator(); i.hasNext(); ) {
	  Object state = i.next();
	  putCost(v, state, getEvaluation().apply(state));
	  }*/
	// maximum change during iteration sweep
	double delta;
	// value iteration
	do {
	    delta = 0;
	    for (Iterator i = states.iterator(); i.hasNext(); ) {
		Object state = i.next();
                double old = ((Number) U.apply(state)).doubleValue();
                
		// search minimal expected cost applicable action
		Pair/*<Object, Number>*/ p = maximumExpectedUtility(Q, state);

		// update U(s) (alias backup)
		U.set(state, p.B);
		logger.log(Level.FINER, "GSDP", "  U(" + state + ")\t:= " + p.B);
    			
		delta = Math.max(delta, Math.abs(old - ((Number) p.B).doubleValue()));
	    }
    	} while (!(delta < tolerance));

    	// return &pi;<sub>f</sub> = &lambda;s: arg min<sub>a&isin;A(s)</sub> Q<sub>f</sub>(s,a)
    	return getGreedyPolicy(Q);
    }

    public orbital.math.functional.Function complexity() {
	return orbital.math.functional.Functions.constant(orbital.math.Values.POSITIVE_INFINITY);
    }

    public orbital.math.functional.Function spaceComplexity() {
	throw new UnsupportedOperationException("not yet implemented");
    }
}
