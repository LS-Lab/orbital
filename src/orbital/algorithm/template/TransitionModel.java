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
 * and state changes.
 * It forms a rewrite system, perhaps with additional probability information.
 * A transition model is characterized by
 * <ul>
 *  <li>a state space S.</li>
 *  <li>a set of actions A.</li>
 *  <li>sets of actions A(s)&sube;A applicable in each state s&isin;S. Usually A(s) = {s&#697;&isin;S &brvbar; <b>P</b>(s&#697;|s,a)&gt;0}.</li>
 *  <li>
 *    the action-dependent (stochastic) <dfn>transition relation</dfn>
 *    <div style="text-align:center">&tau;:A&rarr;<big>(</big>S&times;S&rarr;[0,1]<big>)</big></div>
 *    on S specified as either
 *    <ul type="circle">
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
 *    As a notation for a transition from s&isin;S to s&#697;&isin;S under the
 *    action a&isin;A(s) with a transition probability p&isin;[0,1] we sometimes
 *    use
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
 * Note that you can as well use this interface in its raw version (i.e. without instantiating
 * template parameters) for mere non-deterministic transitions without stochastic information
 * by ignoring the type-restriction of {@link TransitionModel.Option} to {@link #transitions(Object,Object)}.
 * </p>
 *
 * @version 1.0, 2002/05/30
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.BinaryPredicate
 * @see TransitionModelPath
 * @xxx didn't we model the case of a non-deterministic transition function with type O, and the special case of O=Option being the combined function of stochastic transtition probabilities?
 * @todo improve and generalize TransitionModel (which might also be applicable in the implementation of SimulatedAnnealing)
 */
public interface TransitionModel/*<A,S, O extends Option>*/ {
    /**
     * Get the applicable actions of a state.
     * @param state the state s&isin;S whose applicable actions to determine.
     * @pre s&isin;S
     * @return A(s)&sube;A, a list of alternative actions applicable in the state s&isin;S.
     *  The order of the list is decisive because for actions with equal costs
     *  the first will be preferred.
     * @post RES=A(s)
     * @see GeneralSearchProblem#expand(GeneralSearchProblem.Option)
     * @see GreedyProblem#nextCandidates(List)
     */
    Iterator/*<A>*/ actions(Object/*>S<*/ state);
	
    /**
     * Get all transitions possible at a state under a given action.
     * <p>
     * For efficiency reasons it is recommended that this method does only return
     * those states s&#697;&isin;S that can be reached (i.e. where <b>P</b>(s&#697;|s,a) &gt; 0).
     * Although this is not strictly required if it would be too expensive to determine.
     * </p>
     * <p>
     * Note that the resulting iterator will never be empty since the transition probabilities
     * sum up 1, even though the next state may not differ from the previous state.
     * </p>
     * @param state the state s&isin;S.
     * @param action the action a&isin;A(s) that must be applicable in state s&isin;S.
     * @pre s&isin;S &and; a&isin;A(s)
     * @return a list of {@link TransitionModel.Option options}
     *  &lang;s&#697;,<b>P</b>(s&#697;|s,a)&rang; = &lang;s&#697;,&tau;(a)(s,s&#697;)&rang;
     *  with states s&#697;&isin;S that could be reached
     *  when performing the action a in the state s;
     *  and probability <b>P</b>(s&#697;|s,a)&gt;0 of reaching state s&#697;
     *  when performing the action a in the state s.
     * @post RES={&lang;s&#697;,&tau;(a)(s,s&#697;)&rang;&isin;S&times;[0,1] &brvbar; s&#697;&isin;S &and; &tau;(a)(s,s&#697;)&gt;0} &and; &sum;<sub>s&#697;&isin;S</sub> &tau;(a)(s,s&#697;) = 1 &and; RES.hasNext()
     */
    Iterator/*<O>*/ transitions(Object/*>S<*/ state, Object/*>A<*/ action);
	

    /**
     * Represents an option during a transition model.
     * <p>
     * An option is at least a tuple &lang;s&#697;,p&rang;&isin;S&times;[0,1]
     * of a state, and the probability of reaching it (in the corresponding context).</p>
     * @stereotype &laquo;Structure&raquo;
     * @todo should we bookkeep the state and action that took us to s`&#697;?
     * @version 1.0, 2002/05/30
     * @author  Andr&eacute; Platzer
     */
    public static interface Option extends Comparable {
	/**
	 * Checks for equality.
	 * Implementations will at least check for equal states, but ignore
	 * the transition probabilities leading to the states.
	 * However, depending upon concrete application, additional conditions may be checked
	 * for equality.
	 */
	boolean equals(Object o);
		
	int hashCode();
		
	/**
	 * Compares options according to their probabilities.
	 */
	int compareTo(Object o);

	/**
	 * Get the (target) state.
	 * @return the state s&#697;&isin;S that we would reach by taking this option.
	 */
	Object/*>S<*/ getState();
		
	/**
	 * Get the transition probability.
	 * @return the transition probability p&isin;[0,1] of reaching this option.
	 */
	double getProbability();
    }
}
