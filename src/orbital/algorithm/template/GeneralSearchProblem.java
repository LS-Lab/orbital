/**
 * @(#)GeneralSearchProblem.java 1.0 2000/09/17 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Iterator;
import java.io.Serializable;

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
 *   <li>a transition function t:S&times;A(s)&rarr;S; (s,a)&#8614;t(s,a) mapping current states and chosen action to the next state.</li>
 *   <li>action costs c:S&times;A(s)&rarr;<b>R</b>;(s,a)&#8614;c(s,a)&gt;0
 *     for taking the action a&isin;A(s) in the state s&isin;S. 
 *     Also note that there is no strict requirement for c(s,a)&gt;0. Instead there are some
 *     criterions that ensure convergence even for c(s,a)&isin;[0,&infin;).
 *   </li>
 *   <li>an initial state s<sub>0</sub>&isin;S.</li>
 *   <li>a set G&sube;S of goal states. In fact, the explicit goal states are usually hidden in a mere "blackbox" predicate goal query G&sube;&weierp;(S).</li>
 * </ul>
 * The applicable actions A(s) then form the search space as a graph G=&lang;S, E&rang; with E={&lang;s,t(s,a)&rang; &brvbar; s&isin;S, a&isin;A(s)}.
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
 * To be precise, most search algorithms even require <dfn>locally finite graphs</dfn> G
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
 * @version 1.0, 2000/09/17
 * @author  Andr&eacute; Platzer
 * @see GeneralSearch
 * @see BacktrackingProblem
 * @see MarkovDecisionProblem
 * @todo introduce dynamic backtracking on search graphs
 * @todo we could subclass TransitionModel if we would do without expand(Option) and rather use the pair of methods, actions and transitions with the latter returning a single option, only.
 *  Then, of course, we would somehow move getCost to Option. But GSP.getCost returns immediate cost, whereas GSP.Option.getCost returns accumulated cost.
 *  For use in local optimizers, actions() does not need to check for applicability, if transitions() returns the old state if an action later proofs not applicable, without any harm. However, really following the state space would require applicability checks right ahead.
 */
public interface GeneralSearchProblem/*<S,A>*/ extends AlgorithmicProblem {
    /**
     * Get the initial state of the problem.
     * <p>
     * Note that a single initial state is no restriction since one can always introduce
     * 0-cost transitions from a single artificial initial state to a set of real initial states
     * without affecting the search problem.</p>
     * @return s<sub>0</sub> &isin; S.
     */
    Object/*>S<*/ getInitialState();
    
    /**
     * Check whether the given state is a goal state (a valid solution to the problem).
     * <p>
     * Optional variation: Search algorithms generally seek out for one single solution. In order to find all
     * solutions to a problem, simply let this method store solutions and return false instead,
     * until enough solutions have occurred.
     * However, expanding solution nodes should result in an empty list to ensure termination, then.</p>
     * @param n the option n=&lang;s,a,c&rang; whose state s&isin;S to check for being a goal state.
     * @pre s&isin;S
     * @return G(n), resp. whether s&isin;G.
     */
    boolean isSolution(Option/*<S,A>*/ n);

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
     * @pre s&isin;S &and; a&isin;A(s)
     * @post &lang;s',a',c'&rang; &isin; RES &hArr; &exist;a'&isin;A(s) (s' = t(s,a') &and; c' = c+c(s,a'))
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
    Iterator/*<Option<S,A>>*/ expand(Option/*<S,A>*/ n);
	
    /**
     * Get the immediate cost of an option to take.
     * <p>
     * Note that this method usually will only get called
     * if the {@link GeneralSearchProblem.Option#GeneralSearchProblem.Option(Object,Object,GeneralSearchProblem.Option,GeneralSearchProblem) convenience constructor}
     * is in use.
     * Otherwise you will only have to ensure that the cost accumulates in the options returned by {@link #expand(GeneralSearchProblem.Option)},
     * thus avoiding the need for this method alltogether.
     * Since the cost function is conceptually important, we have decided to keep this method,
     * allowing the convenience constructor mentioned above to work.
     * </p>
     * @param n the option n=&lang;s,a,c&rang; that specifies which action a&isin;A(s) is taken and in which state s&isin;S.
     * @return c(s,a) the cost of taking the action a&isin;A(s) in state s&isin;S.
     * @pre s&isin;S &and; a&isin;A(s)
     * @post c(s,a)>0 &or; c(s,a)&isin;[0,&infin;)
     */
    double getCost(Option/*<S,A>*/ n);


    /**
     * Represents an option node during a search problem.
     * <p>
     * An option node is a triple &lang;s,a,c&rang;&isin;S&times;A&times;<b>R</b>
     * of a state, the action, and the accumulated cost to reach it.</p>
     * @stereotype &laquo;Structure&raquo;
     * @invariant getAction()&isin;A(getState())
     */
    public static class Option implements Comparable, Serializable {
	/**
	 * the state s&isin;S of this option node.
	 * @serial
	 */
	private Object/*>S<*/ state;
	/**
	 * the applicable action a&isin;A performed to reach this state.
	 * @serial
	 */
	private Object/*>A<*/ action;
	/**
	 * the accumulated cost c=g(s) up to this state.
	 * @serial
	 */
	private double cost;
	/**
	 * Create a new option &lang;s,a,c&rang;.
	 * @param state the state s&isin;S.
	 * @param action the applicable action a&isin;A to reach the state.
	 * @param cost the accumulated cost c to reach the state.
	 */
	public Option(Object/*>S<*/ state, Object/*>A<*/ action, double cost) {
	    this.state = state;
	    this.action = action;
	    this.cost = cost;
	}
	public Option(Object/*>S<*/ state, Object/*>A<*/ action) {
	    this(state, action, 0);
	}
	public Option(Object/*>S<*/ state, double cost) {
	    this(state, null, cost);
	}
	public Option(Object/*>S<*/ state) {
	    this(state, null);
	}

	/**
	 * Convenient constructor for ease of use.
	 * <p>
	 * Use this constructor to avoid spreading cost calculation but use one single method.
	 * It will automatically accumulate the cost from the parent state according to the
	 * general search problem provided.</p>
	 * @see GeneralSearchProblem#getCost(GeneralSearchProblem.Option)
	 * @see #GeneralSearchProblem.Option(Object, Object, double)
	 */
	public Option(Object/*>S<*/ state, Object/*>A<*/ action, Option/*<S,T>*/ parent, GeneralSearchProblem p) {
	    this(state, action, 0);
	    setCost(parent.getCost() + p.getCost(this));
	}
		
	/**
	 * Get the state.
	 * @return the state s&isin;S of this option node.
	 */
	public Object/*>S<*/ getState() {
	    return state;
	}
		
	protected void setState(Object/*>S<*/ state) {
	    this.state = state;
	}
		
	/**
	 * Get the action.
	 * @return the action a performed to reach this state.
	 */
	public Object/*>A<*/ getAction() {
	    return action;
	}
		
	protected void setAction(Object/*>A<*/ action) {
	    this.action = action;
	}
		
	/**
	 * Get the accumulated cost.
	 * @return the accumulated cost g(n) up to this node.
	 */
	public double getCost() {
	    return cost;
	}
		
	public void setCost(double cost) {
	    this.cost = cost;
	}
		
	public boolean equals(Object o) {
	    if (!(o instanceof Option))
		return false;
	    Option b = (Option) o;
	    return MathUtilities.equals(cost, b.cost, MathUtilities.getDefaultTolerance())
		&& Utility.equals(getState(), b.getState())
		&& Utility.equals(getAction(), b.getAction());
	}
		
	public int hashCode() {
	    return Utility.hashCode(getState())
		^ Utility.hashCode(getAction());
	}
		
	/**
	 * Compares options according to their cost.
	 */
	public int compareTo(Object o) {
	    //@see Double#compare(double,double)
	    return new Double(getCost()).compareTo(new Double(((Option)o).getCost()));
	}
		
	public String toString() {
	    return getClass().getName() + "[" + state + "," + action + ",(" + cost + ")]";
	}
    }
}
