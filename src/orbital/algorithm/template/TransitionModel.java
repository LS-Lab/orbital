/**
 * @(#)TransitionModel.java 1.0 2002/05/30 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Iterator;

/**
 * Represents a transition model.
 * The central part underlying several other formalizations of systems
 * consists of a transition model.
 * <p>
 * A <dfn>transition model</dfn> is a mathematical model for (formal) systems with states
 * and action-driven state changes.
 * It forms a rewrite system, perhaps with additional probability information.
 * A transition model is characterized by
 * <ul>
 *  <li>a state space S.</li>
 *  <li>a set of actions A.</li>
 *  <li>sets of actions A(s)&sube;A applicable in each state s&isin;S. Usually A(s) = {a&isin;A &brvbar; &exist;s&#697;&isin;S&#8726;{&perp;} <b>P</b>(s&#697;|s,a)&gt;0}.</li>
 *  <li>
 *    the action-dependent (stochastic) <dfn>transition relation</dfn>
 *    <div style="text-align:center">&tau;:A&rarr;<big>(</big>S&times;S&rarr;[0,1]<big>)</big></div>
 *    on S specified as either
 *    <ul class="or">
 *      <li>
 *        deterministic transition function t:S&times;A(s)&rarr;S,
 *        with t(s,a) being the next state reached (for sure) when performing action a&isin;A(s) in state s&isin;S.
 *        <div>&tau;(a)(s,s&#697;) := 1 iff s&#697;=t(s,a)</div>
 *      </li>
 *      <li>
 *        non-deterministic transition function t:S&times;A(s)&rarr;&weierp;(S), 
 *        such that t(s,a) is the set of next states that could be reached when performing action a&isin;A(s) in state s&isin;S.
 *        <div>&tau;(a)(s,s&#697;) := 1 &#8260; |t(s,a)| iff s&#697;&isin;t(s,a)</div>
 *      </li>
 *      <li>
 *        non-deterministic transition relation T&sube;S&times;A(s)&times;S,
 *        such that &lang;s,a,s&#697;&rang;&isin;T iff s&#697; is a next state that could be reached when performing action a&isin;A(s) in state s&isin;S.
 *        <div>&tau;(a)(s,s&#697;) := 1 &#8260; |{s&#697;&isin;S &brvbar; T(s,a,s&#697;)}| iff T(s,a,s&#697;)</div>
 *      </li>
 *      <li>
 *        stochastic transition probabilities P<sub>a</sub>:S&rarr;[0,1]; s&#697;&#8614;P<sub>a</sub>(s&#697;|s) := <b>P</b>(s&#697;|s,a) := <b>P</b>(s<sub>t+1</sub>=s&#697;|s<sub>t</sub>=s,a<sub>t</sub>=a)
 *        = P<sub>a</sub>(s&#697;|s) = P<sub>a</sub>(s&#697; &cap; s) / P<sub>a</sub>(s)
 *        denoting the probability of reaching state s&#697;&isin;S on taking the action a&isin;A(s) in state s&isin;S.
 *        The function P is written this way in order to remind that it has a specific probability distribution.
 *        Another possibility would be to use a combined function
 *        <div>t:S&times;A(s)&rarr;&weierp;(S&times;[0,1]); (s,a)&#8614;<big>{</big>&lang;s&#697;,P<sub>a</sub>(s&#697;|s)&rang; &brvbar; s&#697;&isin;S<big>}</big></div>
 *        which is more closely related to implementation issues (and thus used here),
 *        but usually considered inconvenient for the pure purpose of notation.
 *        Stochastic transitions provide the most general case of these types of transitions.
 *        <div>&tau;(a)(s,s&#697;) := <b>P</b>(s&#697;|s,a)</div>
 *      </li>
 *    </ul>
 *    As notation for a transition from s&isin;S to s&#697;&isin;S under the
 *    action a&isin;A(s) with transition probability p&isin;[0,1] we sometimes use
 *    <div style="text-align: center"> <img src="doc-files/transition_notation.png" /> <!-- s &rarr;<sup>p</<sup><sub>a</sub> s&#697;--> </div>
 *    here.
 *  </li>
 * </ul>
 * <p>
 * &tau;(a&sdot;b) = &tau;(a)&#8728;&tau;(b) = <big>(</big>(s,s&#697;) &#8614; <b>P</b>(&#8897;<sub>z&isin;S</sub>(s<sub>t+2</sub>=s&#697;&and;s<sub>t+1</sub>=z) | a<sub>t+1</sub>=b,s<sub>t</sub>=s,a<sub>t</sub>=a)<big>)</big>
 * = <big>(</big>(s,s&#697;) &#8614; &sum;<sub>z&isin;S</sub> &tau;(a)(s,z)*&tau;(b)(z,s&#697;)<big>)</big>
 * The last equation is true if the events are independent, f.ex. for a transition model
 * satisfying the <a href="MarkovDecisionProblem.html#MarkovProperty">Markov property</a>
 * for states.
 * In the same manner, &tau;(a)<sup>n</sup> = &tau;(a<sup>n</sup>) is the
 * (stochastic) transition relation for n transitions of fixed action a&isin;A.
 * &tau;(a<sup>*</sup>) = &tau;(a)<sup>&infin;</sup> is the transitive closure with a fixed action.
 * </p>
 * <p>
 * A non-deterministic transition model is a Semi-Thue system with CH3 acception rules
 * (more precise: reductions).</p>
 * <p>
 * Note that you can as well use this interface in its raw version (i.e. without instantiating
 * template parameters) for mere non-deterministic transitions without stochastic information
 * by ignoring the type-restriction of {@link TransitionModel.ProbabilisticTransition} to {@link #transition(Object,Object,Object)}.
 * </p>
 *
 * @version 1.0, 2002/05/30
 * @author  Andr&eacute; Platzer
 * @see TransitionPath
 * @see orbital.logic.functor.Function
 * @see orbital.logic.functor.BinaryPredicate
 * @xxx didn't we model the case of a non-deterministic transition function with type O, and the special case of O=Option being the combined function of stochastic transtition probabilities?
 * @todo improve and generalize TransitionModel (which might also be applicable in the implementation of SimulatedAnnealing)
 */
public interface TransitionModel/*<A,S, O extends ProbabilisticTransition>*/ {
    /**
     * Checks two transition models for equality (optional).
     * <p>
     * Since checking two transition models for equivalence is rather difficult,
     * most implementations may safely skip this method.
     * </p>
     * <p>
     * Two non-deterministic transition models with transition relations
     * &sigma:A&rarr;&weierp;(S&times;S) and &tau;:A&rarr;&weierp;(T&times;T)
     * are equivalent, if the have a bisimulation.
     * &rho;&sube;S&times;T is a <dfn>bisimulation</dfn> of &sigma; and &tau; :&hArr;
     * <div>&forall;s&isin;S&forall;t&isin;T <big>(</big>s&rho;t &rarr; &forall;a&isin;A (&forall;s&#697;&isin;S &sigma;(a)(s,s&#697;)&rarr;&exist;t&#697;&isin;T &tau;(a)(t,t&#697;)&and;s&#697;&rho;t&#697;)<br />
     *     &forall;t&#697;&isin;T &tau;(a)(t,t&#697;)&rarr;&exist;s&#697;&isin;S &sigma;(a)(s,s&#697;)&and;s&#697;&rho;t&#697;)) <big>)</big>
     * </div>
     * </p>
     * <ul>
     *   <li>S=T &rArr; bisimulation is the same as compatible?</li>
     *   <li>&rho; functional &rArr; (bi?)simulation is the same as homomorphism?</li>
     *   <li>&weierp;(S&times;T) has a maximum bisimulation which equals max fix<sub>&rho;</sub> BISIM(&rho;)
     *     where BISIM(&rho;) = &lambda;s&lambda;t. &forall;a&isin;A (&forall;s&#697;&isin;S (&sigma;(a)(s,s&#697;)&rarr;&exist;t&#697;&isin;T &tau;(a)(t,t&#697;)&and;s&#697;&rho;t&#697;)<br />
     *     &forall;t&#697;&isin;T &tau;(a)(t,t&#697;)&rarr;&exist;s&#697;&isin;S &sigma;(a)(s,s&#697;)&and;s&#697;&rho;t&#697;).
     *   </li>
     * </ul>
     */
    //boolean equals(Object x);
    /**
     * Ha, ha.
     */
    //int hashCode();

    /**
     * Get the applicable actions at a state.
     * <p>
     * Intuitively, applicable actions are those that result in a valid
     * transition. So for a state, the applicable actions are the only
     * actions relevant for leaving that state with any transition
     * (including transitions that lead back to the state the
     * transition just started in).
     * </p>
     * <p>
     * For several reasons (including performance) it is widely recommended that
     * <div style="text-align: center">A(s) = {a&isin;A &brvbar; &exist;s&#697;&isin;S&#8726;{&perp;} <b>P</b>(s&#697;|s,a)&gt;0}
     * = {a&isin;A &brvbar; &tau;(a)(s,&perp;)&ne;1}
     * = A&#8726;&tau;<sup>-1</sup>({s}&times;{&perp;})</div>
     * In fact, this is not a <em>strict</em> requirement, if the computation would be far too
     * expensive. However, the TransitionModel implementation would then have to deal with
     * cases where an action was chosen that has later been found out to be inapplicable,
     * contrary to the initial guess of {@link #actions(Object)}.
     * Since this may result in rather messy implementations, relieving this requirement
     * should generally be limited to very specific and well documented cases.
     * </p>
     * @param state the state s&isin;S whose applicable actions to determine.
     * @pre s&isin;S
     * @return A(s)&sube;A, a list of alternative actions applicable in the state s&isin;S.
     *  The order of the list can be decisive because for actions with equal costs
     *  the first will be preferred.
     * @post RES=A(s)&sube;A
     * @see GeneralSearchProblem#expand(GeneralSearchProblem.Option)
     * @see GreedyProblem#nextCandidates(List)
     */
    Iterator/*<A>*/ actions(Object/*>S<*/ state);
	
    /**
     * Get all states reachable with any transitions from the state under a given action.
     * <p>
     * Intuitively, those are the only relevant states which can be reached
     * by any transitions (from the given state under the given action) at all.
     * </p>
     * <p>
     * For performance reasons it is recommended that this method does only return
     * those states s&#697;&isin;S that can truely be reached
     * (i.e. where <b>P</b>(s&#697;|s,a) &gt; 0).
     * Although this is not strictly required if it would be too expensive to determine.
     * </p>
     * <p>
     * Note that the resulting iterator will never be empty since the
     * transition probabilities sum up 1 (or integrate to 1 in the
     * case of a continuous transition probability distribution),
     * even though the next state may not differ from the previous
     * state.
     * </p>
     * @param action the action a&isin;A(s) that must be applicable in state s&isin;S.
     * @param state the state s&isin;S.
     * @pre s&isin;S &and; a&isin;A(s)
     * @return a list of states s&#697;&isin;S that could be reached
     *  when performing the action a in the state s.
     * @post RES &supe; {s}&#8728;&tau;(a) = {s&#697;&isin;S &brvbar; &tau;(a)(s,s&#697;)&gt;0} &and; RES.hasNext()
     * @todo rename but not to nextStates, reachableStates
     */
    Iterator/*<S>*/ transitions(Object/*>A<*/ action, Object/*>S<*/ state);
	
    // central operation
    
    /**
     * Get (information about) the transition from a state to another state under a given action.
     * <p>
     * This central method specifies the central action-dependent (stochastic) transition relation
     * <div style="text-align:center">&tau;:A&rarr;<big>(</big>S&times;S&rarr;[0,1]<big>)</big></div>
     * on S. With transitions specified by &tau;(a)(s,s&#697;)
     * <div style="text-align: center"> <img src="doc-files/transition_notation.png" /> </div>
     * </p>
     * @param action the action a&isin;A(s) that must be applicable in state s&isin;S.
     * @param state the state s&isin;S.
     * @param statep the state s&#697;&isin;S.
     * @pre s,s&#697;&isin;S &and; a&isin;A(s)
     * @return &tau;(a)(s,s&#697;) which is the probability <b>P</b>(s&#697;|s,a)
     *  of reaching state s&#697;&isin;S when performing action a&isin;A(s) in the state s&isin;S.
     *  Usually represented as a {@link TransitionModel.ProbabilisticTransition transition}
     *  which may contain additional information.
     * @post RES=&tau;(a)(s,s&#697;)&isin;[0,1] (more precisely RES.getProbability()=&tau;(a)(s,s&#697;)) &and; &sum;<sub>s&#697;&isin;S</sub> &tau;(a)(s,s&#697;) = 1
     * @internal alternative we could also extend Function<A,BinaryFunction<S,S>> if the corresponding apply(a) method did not have to create new BinaryFunctions for every call or loose the property of being stateless when caching.
     */
    ProbabilisticTransition/*>O<*/ transition(Object/*>A<*/ action, Object/*>S<*/ state, Object/*>S<*/ statep);

    /**
     * Represents (information about) a transition option during a transition model.
     * <p>
     * An option is at least a 1-tuple &lang;p&rang;&isin;[0,1]
     * of the probability of reaching a state s&#697; (in the corresponding context).
     * However, it may contain additional information about the transition.
     * </p>
     * @stereotype &laquo;Structure&raquo;
     * @todo should we bookkeep the state and action that took us to s´ as well as s´?
     * @version 1.0, 2002/05/30
     * @author  Andr&eacute; Platzer
     */
    public static interface ProbabilisticTransition extends Comparable {
	/**
	 * Checks for equality.
	 * <!-- Implementations will at least check for equal states, but ignore
	 * the transition probabilities leading to the states.
	 * However, depending upon concrete application, additional conditions may be checked
	 * for equality. -->
	 */
	boolean equals(Object o);
		
	int hashCode();
		
	/**
	 * Compares transition options according to their probabilities.
	 */
	int compareTo(Object o);

	/**
	 * Get the transition probability.
	 * @return the transition probability p&isin;[0,1] of taking this transition.
	 */
	double getProbability();
    }
}
