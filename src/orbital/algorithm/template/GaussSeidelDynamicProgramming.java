/**
 * @(#)GaussSeidelDynamicProgramming.java 1.0 2001/06/10 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import orbital.math.Real;

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
 * Gau&szlig;-Seidel dynamic programming.
 * <p>
 * Gau&szlig;-Seidel dynamic programming is a variant of synchronous dynamic programming performing
 * value iteration in a sequential "sweep" of all the states after each state backup.
 * Due to this fact, it can even be seen half-way as asynchronous dynamic programming.
 * For the same reason, Gau&szlig;-Seidel dynamic programming usually converges faster than
 * sychronous dynamic programming.</p>
 * <p>
 * Gau&szlig;-Seidel dynamic programming permanently uses a dynamic programming variant of value iteration
 * for the sequence of utility functions U<sub>k</sub>.
 * <center class="Formula">
 *   U<sub>k+1</sub>(s) := min<sub>a&isin;A(s)</sub> Q<sub>U</sub>(s,a)
 * </center>
 * where
 * <center class="Formula">
 *   U(t) := U<sub>k+1</sub>(t) &lArr; t&lt;s
 * </center>
 * <center class="Formula">
 *  U(t) := U<sub>k</sub>(t) &lArr; t&ge;s
 * </center>
 * </p>
 *
 * @invariants getDiscount()&isin;[0,1]
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see DynamicProgramming
 * @see "A. Barto, S. Bradtke, and S. Singh. Learning to act using real-time dynamic programming. <i>Artificial Intelligence</i>, 72:81-138, 1995."
 */
public class GaussSeidelDynamicProgramming/*<A,S,M extends MarkovDecisionProblem.Transition>*/
    extends MarkovDecisionProcess.DynamicProgramming/*<A,S,M>*/
    implements HeuristicAlgorithm/*<MarkovDecisionProblem<A,S,M>,S>*/ {
    private static final long serialVersionUID = -5923519196510123671L;
    private static final Logger logger = Logger.getLogger(GaussSeidelDynamicProgramming.class.getName());
    /**
     * the tolerance value below which the evaluation function is considered
     *  to have converged.
     * @serial
     */
    private double tolerance;
    /**
     * the full set S of <em>all</em> states of the problem.
     * @serial
     */
    private Collection/*<S>*/ states;
    /**
     * @param states the full set S of <em>all</em> states of the problem.
     * @param tolerance the tolerance value below which the evaluation function is considered
     *  to have converged.
     */
    public GaussSeidelDynamicProgramming(Function/*<S,Real>*/ heuristic, Collection/*<S>*/ states, double tolerance) {
        super(heuristic);
        this.states = states;
        this.tolerance = tolerance;
    }
    
    protected Function/*<S,A>*/ plan() {
        /**
         * estimates U of optimal value function h<sup>*</sup>:S&rarr;<b>R</b>.
         * If h is admissible U will converge (monotonically) up to h<sup>*</sup>.
         * Updated via DP on current states, instead of value iteration on each state until convergence.
         */
        final MutableFunction/*<S,Real>*/ U = createMap();
        final BinaryFunction/*<S,A,Real>*/ Q = getActionValue(U);
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
            for (Iterator/*<S>*/ i = states.iterator(); i.hasNext(); ) {
                Object/*>S<*/ state = i.next();
                double old = ((Number) U.apply(state)).doubleValue();
                
                // search minimal expected cost applicable action
                Pair/*<A, Real>*/ p = maximumExpectedUtility(Q, state);

                // update U(s) (alias backup)
                U.set(state, p.getB());
                logger.log(Level.FINER, "GSDP", "  U(" + state + ")\t:= " + p.getB());
                        
                delta = Math.max(delta, Math.abs(old - ((Number) p.getB()).doubleValue()));
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
