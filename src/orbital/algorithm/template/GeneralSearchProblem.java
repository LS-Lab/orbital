/**
 * @(#)GeneralSearchProblem.java 1.0 2000/09/17 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.MutableFunction;
import java.util.Iterator;
import java.io.Serializable;

import orbital.math.Scalar;
import orbital.math.Real;
import orbital.math.Values;
import orbital.util.Utility;
import orbital.math.MathUtilities;

/**
 * Hook class for GeneralSearch algorithm. Objects implementing this interface represent
 * a state model.
 * <p>
 * A <dfn>state model</dfn> is a mathematical model for making sense of some classes of problems.
 * Apart from action costs, it is essentially a deterministic (finite) automaton to control.
 * A state model is characterized by
 * <ul>
 *   <li>a finite and discrete state space S.</li>
 *   <li>a finite set of actions A.</li>
 *   <li>sets of actions A(s)&sube;A applicable in each state s&isin;S.</li>
 *   <li>a transition function t:S&times;A(s)&rarr;S; (s,a)&#8614;t(s,a) mapping current states and chosen action to the next state.
 *     The deterministic transition function is wrapped in a {@link TransitionModel transition model} with a transition relation.
 *   </li>
 *   <li>action costs c:S&times;A(s)&rarr;<b>R</b>;(s,a)&#8614;c(s,a)&gt;0
 *     for taking the action a&isin;A(s) in the state s&isin;S. 
 *     Also note that there is no strict requirement for c(s,a)&gt;0. Instead there are some
 *     criterions that ensure convergence even for c(s,a)&isin;[0,&infin;).
 *   </li>
 *   <li>an initial state s<sub>0</sub>&isin;S.</li>
 *   <li>a set G&sube;S of goal states. In fact, the explicit goal states are usually hidden in a mere "blackbox" predicate goal query G&sube;&weierp;(S).</li>
 * </ul>
 * The applicable actions A(s) then span the search space as a graph G=&lang;S,{&lang;s,t(s,a)&rang; &brvbar; s&isin;S, a&isin;A(s)}&rang;.
 * Its solution is a sequence of applicable actions that leads from an initial state to a goal state.
 * A solution is optimal if it has minimum cost.</p>
 * <p>
 * Derived values
 * <ul>
 *   <li>K(s,t) := min {&sum;<span class="doubleIndex"><sup>n</sup><sub>i=0</sub></span>c(s<sub>i</sub>,a<sub>i</sub>) &brvbar; n&isin;<b>N</b>, s<sub>0</sub>=s, s<sub>i+1</sub>:=t(s<sub>i</sub>,a<sub>i</sub>),s<sub>n</sub>=t,a<sub>i</sub>&isin;A(s<sub>i</sub>)}
 *   is the minimum accumulated cost for going from s&isin;S to t&isin;S.</li>
 *   <li>h<sup>*</sup>(s) := min K(s,G) is the effective cost function for the best path from s.</li>
 *   <li>g<sup>*</sup>(s) := K(s<sub>0</sub>,s) is the minimum cost needed to reach s.</li>
 *   <li>f<sup>*</sup>(s) := g<sup>*</sup>(s) + h<sup>*</sup>(s) is the minimum cost for paths going through s.</li>
 * </ul>
 * </p>
 * <p>
 * To be precise, most search algorithms even require <dfn id="locallyFinite">locally finite graphs</dfn> G
 * (i.e. with finite branching factors) that have costs that "keep away from zero", i.e.
 * <center>&exist;&epsilon;&gt; 0 &forall;s&isin;S&forall;a&isin;A(s) c(s,a) &gt; &epsilon;</center>
 * to achieve completeness.
 * </p>
 * <p>
 * Solving state models can produce an open-loop plan for control.
 * </p>
 * <p>
 * For defining a state model, several representation models may be of use,
 * even including {@link orbital.algorithm.evolutionary.Gene genetic data models}.
 * </p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see GeneralSearch
 * @see BacktrackingProblem
 * @see MarkovDecisionProblem
 * @todo introduce dynamic backtracking on search graphs
 * @todo we could subclass TransitionModel if we would do without expand(Option) and rather use the pair of methods, actions and transitions with the latter returning a single option, only.
 *  Then, of course, we would somehow move getCost to Option. But GSP.getCost returns immediate cost, whereas GSP.Option.getCost returns accumulated cost.
 *  For use in local optimizers, actions() does not need to check for applicability, if states() returns the old state if an action later proofs not applicable, without any harm. However, really following the state space would require applicability checks right ahead.
 */
public interface GeneralSearchProblem/*<A,S,M extends Transition>*/ extends MarkovDecisionProblem/*<A,S,M>*/ {
    /**
     * Get the initial state of the problem.
     * <p>
     * Note that a single initial state is no restriction since one can always introduce
     * 0-cost transitions from a single artificial initial state to a set of true initial states
     * without affecting the search problem.
     * </p>
     * <p>
     * Make sure that this method consistently returns the initial state even for repeated
     * invocations, since some iterative search algorithms may rely on this feature.
     * </p>
     * @return s<sub>0</sub> &isin; S.
     * @postconditions getAccumulatedCostFunction().apply(RES) = 0 &and; (RES==OLD(RES) or problem changed)
     */
    Object/*>S<*/ getInitialState();

    /**
     * Get the accumulated cost function.
     * <p>
     * This function encapsulates read write access to the accumulated
     * cost values. Search algorithms can accumulate cost for states by
     * setting g(s) to the accumulate cost value, and later query that
     * accumulate cost value again, by applying g.
     * </p>
     * </p>
     * The most simple way of providing such an accumulated cost function
     * g, is to enrich states with a (private) field for accumulated
     * cost that is accessible via g. So you can simply use
     * S&times;<b>R</b> as states instead of S for storing accumulated
     * cost values.
     * </p>
     * <p>
     * Since search algorithms may invoke this method several times, it should
     * not perform too slow. So consider returning a single pre-initialized
     * instance of the accumulate cost function.
     * </p>
     * <p>
     * Note that accumulated cost functions usually do not need to be cloned.
     * </p>
     * @return the accumulated cost function g:S&rarr;<b>R</b>, mapping states s to their
     *  accumulated cost g(s).
     *  That function must map S to accumulated cost values g(s) represented as {@link Real}s.
     * @postconditions RES == OLD(RES)
     * @attribute secret storage of accumulated cost values of states
     * @internal alternative would be to return a ModifiableFunction and let SearchAlgorithms use it to accumulate cost according to g(newState) := g(lastState)+transition(a,lastState,newState).getCost(); Could also be used to associate other search information with states.
     * @internal alternative would be to restrict S to have a method S.getCost() by requiring S to implement a specific interface. That's neither flexible nor beautiful.
     */
    MutableFunction/*<S,Real>*/ getAccumulatedCostFunction();

    /**
     * Expands a state.
     * <p>
     * The expanded children of n span the search graph.</p>
     * <p>
     * Optional variation: To avoid simultaneous expansion of all resulting states, simply let this method return
     * the action or state descriptions, only, and expand them later on.
     * Be aware, however, that this approach will only save storage space and time for algorithms
     * that consider the A(s) sequentially, choosing solely the first suitable state.
     * Other algorithms that perform a ranking of possible expansions, the states will need to
     * be constructed anyway.</p>
     * <p>
     * By the way, searching often does not explicitly refer to the actions taken, instead they
     * usually form the relevant part of a solution.</p>
     * <p>
     * <b>Note</b>: the return-type of is Iterator in order to increase space efficiency for
     * problems with a good expand-on-demand behaviour. Additionally, this enables implementing
     * classes to use do/undo to expand states.
     * Implementations can either
     * <ul class="or">
     *   <li>conservatively provide an iterator over a list that has been explicitly constructed.</li>
     *   <li>explicitly provide a problem-specific iterator that constructs the successor states
     *   on demand.</li>
     *   <li>or use the {@link orbital.util.StreamMethod} connector to provide an implicit yet
     *   constructive iterator in a very simple way.</li>
     * </ul>
     * Also note that if an implementation of expand() wants to optimize memory performance
     * for the cost of limiting it to search algorithms based on depth-first search,
     * then it can apply the do/undo technique.
     * Alternatively, if applicable actions can be determined quickly but constructing the
     * resulting states is expensive, the (simpler) technique of lazy state construction can be
     * applied. In order to achieve this, {@link GeneralSearchProblem.Option#getState() getState()}
     * must be overwritten to perform lazy construction of resulting states. However, this
     * technique is not that powerful as do/undo, and is less useful if the calculation of costs
     * depends on the specific resulting states anyway.
     * </p>
     * @param n the option n=&lang;s,a,c&rang; that specifies the state s&isin;S chosen to expand.
     * @preconditions s&isin;S &and; a&isin;A(s)
     * @postconditions &lang;s',a',c'&rang; &isin; RES &hArr; &exist;a'&isin;A(s) (s' = t(s,a') &and; c' = c+c(s,a'))
     * @return {&lang;t(s,a'),a',c+c(s,a')&rang; &isin; S&times;A(s)&times;<b>R</b> &brvbar; a'&isin;A(s)}.
     *  An iterator over the {@link GeneralSearchProblem.Option options} of states that can be reached from s by applicable actions.
     *  The options returned should have set the action a' that lead there, and the real
     *  accumulated cost c+c(s,a') needed to get there.
     * @see GreedyProblem#nextCandidates(List)
     * @todo changed return-type to Iterator<Option>, instead. Allows expand-on-demand and do/undo (and perhaps even explicit backtracking) instead of exand to all successor states simultaneously.
     *  Perhaps, BreadthFirstSearch would profit from a SequenceIterator over a QueuedIterator which contains all expanded Iterators, then.
     *  Perhaps, DepthFirstSearch   would profit from a SequenceIterator over a QueuedIterator (but as a LIFO-Stack!) which contains all expanded Iterators, then.
     *  However, BestFirstSearch has some overhead problems caused by the conversion from Iterator to Collection, again, for sorting.
     *  see orbital.util.StreamMethod as a utility for implementation.
     */
    //Iterator/*<Option<S,A>>*/ expand(Option/*<S,A>*/ n);

    /**
     * {@inheritDoc}
     * <p>
     * Searching often does not explicitly refer to the actions taken, but they
     * usually form the relevant part of a solution.</p>
     * <p>
     * <b>Note</b>: the return-type is Iterator in order to increase space efficiency for
     * problems with a good expand-on-demand behaviour. Additionally, this enables implementations
     * to use do/undo for expanding states.
     * Implementations can either
     * <ul class="or">
     *   <li>conservatively provide an iterator over a list that has been explicitly constructed.</li>
     *   <li>explicitly implement and provide a problem-specific iterator that constructs the
     *     actions leading to successor states on demand.
     *   </li>
     *   <li>or use the {@link orbital.util.StreamMethod} connector to provide an implicit yet
     *     constructive iterator in a very simple way.
     *   </li>
     * </ul>
     * Also note that if an implementation of {@link #states(Object,Object)} wants to optimize
     * memory performance for the cost of limiting it to search algorithms based on depth-first search,
     * then it can apply the do/undo technique.
     * Alternatively, if applicable actions can be determined quickly but constructing the
     * resulting states is expensive, the (usual) approach of lazy state construction
     * can be used. In order to achieve this, let {@link #actions(Object)} return actions,
     * without constructing any states. Then {@link #states(Object,Object)} performs lazy construction
     * of resulting states on every call. However, this technique is not that powerful as do/undo,
     * and it is less useful if the calculation of costs depends on the specific resulting states anyway.
     * Nevertheless, it is much more simple to implement.
     * </p>
     */
    Iterator/*<A>*/ actions(Object/*>S<*/ state);

    /**
     * {@inheritDoc}
     * Deterministic case (will only return one single transition per action).
     * @postconditions super &and; &not;(RES.hasNext() after RES.next())
     */
    Iterator/*<S>*/ states(Object/*>A<*/ action, Object/*>S<*/ state);

    /**
     * {@inheritDoc}
     * Deterministic case.
     * Will only return &ne;0 for the unique s&#697; = t(s,a).
     * So the only true information obtained is the {@link GeneralSearchProblem.Transition#getCost() immediate action cost}
     * of the transition,
     * plus any (optional) problem-specific additional information.
     * @postconditions RES.getProbability()&isin;{0,1} &and; RES instanceof {@link GeneralSearchProblem.Transition}
     * @see orbital.math.functional.Functions#diracDelta
     * @internal covariant return-types or generics would allow returning M=Transition.
     */
    TransitionModel.Transition/*>M<*/ transition(Object/*>A<*/ action, Object/*>S<*/ state, Object/*>S<*/ statep);

    /**
     * Represents an option node during a search problem.
     * <p>
     * An option node is a tuple &lang;a,c&rang;&isin;A&times;<b>R</b>
     * of an action performed to reach a state s&#697; from a state s, and the immediate action cost.</p>
     * @stereotype Structure
     * @invariants getAction()&isin;A(s)
     */
    public static class Transition implements MarkovDecisionProblem.Transition, Serializable {
        private static final long serialVersionUID = 257664629450534598L;
        /**
         * the applicable action a&isin;A performed to reach this state.
         * @serial
         * @todo do we always need this?
         */
        private Object/*>A<*/ action;
        /**
         * the immediate action cost c=c(s,a) of the action performed to reach the state s&#697;.
         * @serial
         */
        private Real cost;
        /**
         * Create a new option &lang;a,c&rang;.
         * @param action the applicable action a&isin;A to reach the state s&#697;.
         * @param cost the immediate action cost c(s,a) of the action performed to reach the state s&#697;.
         */
        public Transition(Object/*>A<*/ action, Real cost) {
            this.action = action;
            this.cost = cost;
        }
        /**
         * @deprecated convenience constructor, prefer to use {@link Values#valueOf(double)}..
         */
        public Transition(Object/*>A<*/ action, double cost) {
            this(action, Values.getDefaultInstance().valueOf(cost));
        }

        /**
         * Get the action.
         * @return the action a performed to reach the state s&#697;.
         */
        public Object/*>A<*/ getAction() {
            return action;
        }
                
        protected void setAction(Object/*>A<*/ action) {
            this.action = action;
        }

        public Real getCost() {
            return cost;
        }

        /**
         * 1 since deterministic transition.
         */
        public final Scalar getProbability() {
            return Values.ONE;
        }
                
        /**
         * Compares options according to their cost.
         */
        public int compareTo(Object o) {
            throw new UnsupportedOperationException("functionality removed since it depends on evaluation function comparator");
        }
                
        public String toString() {
            return getClass().getName() + "[" + getAction() + ",(" + getCost() + ")]";
        }
    }
}
