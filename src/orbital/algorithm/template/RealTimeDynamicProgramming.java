/**
 * @(#)RealTimeDynamicProgramming.java 1.0 2000/10/11 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;

import orbital.logic.functor.BinaryFunction;

import orbital.moon.logic.functor.MutableFunction;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import orbital.util.Pair;
import java.util.HashMap;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Real-Time Dynamic Programming (RTDP).
 * <p>
 * Real-Time Dynamic Programming is a variant of asynchronous Dynamic Programming performed
 * concurrently with the control process.
 * It uses concurrent value iteration.</p>
 * <p>
 * If the heuristic function h is admissible h &le; h<sup>*</sup>, then the greedy policy v will
 * eventually become optimal after several cycles of repeated trials.
 * If h is good, very large problems can be solved.</p>
 * <p>
 * RTDP permanently uses a real-time dynamic programming variant of value iteration
 * for the utility function U (alias state-value function V).
 * <center>
 *   <span class="Formula">U(s) := min<sub>a&isin;A(s)</sub> Q<sub>U</sub>(s,a) = min<sub>a&isin;A(s)</sub> <big>(</big>c(s,a) + &gamma;*&sum;<sub>t&isin;S</sub> P<sub>a</sub>(t|s) * U(t)<big>)</big></span>
 * </center>
 * depending upon a discount factor &gamma;&isin;[0,1].
 * The formula is a dynamic programming update derived from the condition of the Bellman Optimality Equation.
 * The key fact is that a necessary and sufficient condition for a policy &pi;<sup>*</sup> to be optimal is that
 * the expected costs U<sup>*</sup>(s) that result from starting in state s&isin;S and acting according to &pi;<sup>*</sup>
 * must satisfy a form of the Bellman Optimality Equation:
 * <center>
 *   <span class="Formula">U(s) = min<sub>a&isin;A(s)</sub> Q<sub>U</sub>(s,a) = min<sub>a&isin;A(s)</sub> <big>(</big>c(s,a) + &gamma;*&sum;<sub>t&isin;S</sub> P<sub>a</sub>(t|s) * U(t)<big>)</big></span>
 * </center>
 * </p>
 * <p>
 * RTDP can as well be considered a reinforcement learning technique with the costs being
 * a negative reward R(t(s,a)) = -c(s,a) made dependent on both, the state s&isin;S and action a&isin;A(s),
 * and the task being to minimize costs U(s) instead of maximize utilities U(s).
 * However, there is a multitude of possibilities of defining the costs in terms of the reward.</p>
 * <p>
 * RTDP is the stochastic generalization of Learning Real Time Search (LRTA<sup>*</sup>). For deterministic actions and discounting &gamma;=1
 * RTDP collapses to LRTA<sup>*</sup>.</p>
 *
 * @invariant getDiscount()&isin;[0,1]
 * @version 1.0, 2000/10/11
 * @author  Andr&eacute; Platzer
 * @see Greedy
 * @see DynamicProgramming
 * @todo @see "H. Geffner and B. Bonet. Solving Large POMDPs using Real Time Dynamic Programming."
 * @see "A. Barto, S. Bradtke, and S. Singh. Learning to act using real-time dynamic programming. <i>Artificial Intelligence</i>, 72:81-138, 1995."
 * @see "Bellman, R. E. (1957). Dynamic Programming. Princeton University Press, Princeton, New Jersey."
 * @todo RTDP should be allowed to back up more states than just the current state after each single step.
 * @todo note that RTDP updating might confuse results a little for circular search graphs with repetitive states. Then costs might grow too high.
 * @todo ties break randomly to ensure better convergence
 * @todo introduce sub class Adaptive Real-Time Dynamic Programming that handles unknown models as well.
 * @todo could we plot the resulting evaluation function with a Plot3D in some examples to visualize what's happening?
 */
public class RealTimeDynamicProgramming extends MarkovDecisionProcess.DynamicProgramming implements HeuristicAlgorithm {
    private static final Logger logger = Logger.getLogger(RealTimeDynamicProgramming.class.getName());
    public RealTimeDynamicProgramming(Function heuristic) {
	super(heuristic);
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
    	return new Function() {
    		public Object apply(Object state) {
		    Pair/*<Object, Number>*/ p = maximumExpectedUtility(Q, state);

		    // update U(s) (alias backup)
		    U.set(state, p.B);
		    logger.log(Level.FINER, "RTDP", "  U(" + state + ")\t:= " + p.B);

		    // return the action chosen to take
		    return p.A;
		}
	    };
    }

    public orbital.math.functional.Function complexity() {
	return orbital.math.functional.Functions.constant(orbital.math.Values.POSITIVE_INFINITY);
    }

    public orbital.math.functional.Function spaceComplexity() {
	throw new UnsupportedOperationException("not yet implemented");
    }
}
