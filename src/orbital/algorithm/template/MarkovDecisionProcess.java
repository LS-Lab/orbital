/**
 * @(#)MarkovDecisionProcess.java 1.0 2000/10/11 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;
import orbital.algorithm.template.MarkovDecisionProblem.Transition;

import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;
import orbital.logic.functor.MutableFunction;
import java.io.Serializable;
import orbital.math.*;

import orbital.logic.functor.Functionals;

import java.util.Iterator;
import java.util.Collection;

import orbital.util.Pair;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Markov decision process (MDP). For closed-loop planning.
 * <p>
 * <dl class="def">
 * Let &gamma;&isin;[0,1] be a discount factor and &pi;:S&rarr;A(s) a policy.
 *   <dt>expected cost sum</dt>
 *   <dd>One evaluation function (or cost function or utility function or value function) f<sub>&pi;</sub>
 *   corresponding to the policy &pi; is
 *   <center>
 *       <span class="Formula">f<sub>&pi;</sub>:S&rarr;<b>R</b>; f<sub>&pi;</sub>(s) = <b>E</b>[&sum;<span class="doubleIndex"><sup>&infin;</sup><sub>t=0</sub></span> &gamma;<sup>t</sup>c(s<sub>t</sub>,a<sub>t</sub>) | s<sub>0</sub>=s,&pi;]</span>
 *   </center>
 *   i.e. the expected value of the discounted immediate cost sum starting from state s under policy &pi;.
 *   </dd>
 *   <dt>Q-values</dt>
 *   <dd>The action-value cost of an action a&isin;A(s) in state s&isin;S as evaluated by f:S&rarr;<b>R</b> is
 *   <center>
 *       <span class="Formula">Q<sub>f</sub>:S&times;A(s)&rarr;<b>R</b>; Q<sub>f</sub>(s,a) = c(s,a) + &gamma;*<b>E</b>[f(s<sub>t+1</sub>)|s<sub>t</sub>=s,a<sub>t</sub>=a]<br />
 *        = c(s,a) + &gamma;*&sum;<sub>s'&isin;S</sub> <b>P</b>(s'|s,a)*f(s')</span>
 *   </center>
 *   If f=f<sub>&pi;</sub>, Q<sub>f<sub>&pi;</sub></sub>(s,a) is the expected cost of performing
 *   action a&isin;A(s) in state s&isin;S
 *   and thereafter following the policy &pi;.
 *   </dd>
 *   <dt>greedy policy</dt>
 *   <dd>A policy &pi; is greedy with respect to an action-value function Q:S&times;A(s)&rarr;<b>R</b> if
 *   <center>
 *       <span class="Formula">&forall;s&isin;S Q(s,&pi;(s)) = min<sub>a&isin;A(s)</sub> Q(s,a)</span>
 *   </center>
 *   Given an action-value function Q:S&times;A(s)&rarr;<b>R</b>,
 *   one greedy policy &pi;<sub>Q</sub> with respect to Q is given by
 *   <center>
 *       <span class="Formula">&pi;<sub>Q</sub>(s) = argmin<sub>a&isin;A(s)</sub> Q(s,a)</span>
 *   </center>
 *   Similarly, &pi;<sub>f</sub> := &pi;<sub>Q<sub>f</sub></sub> is one
 *   greedy policy with respect to an evaluation function f:S&rarr;<b>R</b>.
 *   </dd>
 * </dl>
 * A solution &pi;<sup>*</sup> is optimal if it has minimum <em>expected</em> cost
 * (alias maximum expected utility),
 * i.e. f<sub>&pi;<sup>*</sup></sub> = f<sup>*</sup> &and; &pi;<sub>f<sup>*</sup></sub> = &pi;<sup>*</sup>.
 * It is
 * <blockquote>
 *     f<sup>*</sup>:S&rarr;<b>R</b> is the optimal evaluation function &hArr;
 *     <span class="Formula">f<sup>*</sup>(s) = min<sub>a&isin;A(s)</sub> Q<sub>f<sup>*</sup></sub>(s,a)</span>
 * </blockquote>
 * Which is one form of the Bellman optimality equation.
 * </p>
 * <p>
 * By the way, a minor generalization is sometimes used that would change a policy to be a
 * stochastic function &pi;:S&times;A&rarr;[0,1]
 * specifying the probability &pi;(s,a) with that it chooses the action a&isin;A(s) in state s&isin;S.
 * But this does not usually improve the quality of solution policies.</p>
 * <p>
 * To apply MDP planning, perform something like:
 * <pre>
 * <span class="Orbital">MarkovDecisionProcess</span> planner <span class="operator">=</span> ...;
 * <span class="comment">// obtain a plan</span>
 * <span class="Orbital">Function</span> plan <span class="operator">=</span> planner.solve(problem);
 * <span class="comment">// follow the plan</span>
 * <span class="keyword">for</span> (<span class="Class">Object</span> state <span class="operator">=</span> <var>initial</var>; <span class="operator">!</span>problem.isSolution(state); state <span class="operator">=</span> <var>observe</var>()) {
 *     <var>perform</var>(plan.apply(state));
 * }
 * </pre></p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see MarkovDecisionProblem
 * @see <a href="http://www.ldc.usb.ve/~hector">Hector Geffner. Modelling and Problem Solving</a>
 * @see "A. Barto, S. Bradtke, and S. Singh. Learning to act using real-time dynamic programming. <i>Artificial Intelligence</i>, 72:81-138, 1995."
 * @todo @see "H. Geffner and B. Bonet. Solving Large POMDPs using Real Time Dynamic Programming."
 * @TODO extend more general base class or interface Planning
 */
public abstract class MarkovDecisionProcess /*extends Planning*/ implements AlgorithmicTemplate/*<MarkovDecisionProblem,Function>*/, Serializable {
    private static final long serialVersionUID = 2351017747303613618L;
    private static final Logger logger = Logger.getLogger(MarkovDecisionProcess.class.getName());
    private static final Values valueFactory = Values.getDefaultInstance();
    public Object solve(AlgorithmicProblem p) {
        return solve((MarkovDecisionProblem) p);
    }
    
    /**
     * The MDP problem to solve.
     * @serial
     */
    private MarkovDecisionProblem problem;
        
    /**
     * Get the current problem.
     * @return the problem specified in the last call to solve.
     */
    protected final MarkovDecisionProblem getProblem() {
        return problem;
    }
    /**
     * Set the current problem.
     * @param newproblem the problem specified in the last call to solve.
     */
    private final void setProblem(MarkovDecisionProblem newproblem) {
        this.problem = newproblem;
    }
    
    /**
     * Solves an MDP problem.
     * @return the solution policy function S&rarr;A found,
     *  or <code>null</code> if no solution could be found.
     */
    public Function/*<S,A>*/ solve(MarkovDecisionProblem p) {
        setProblem(p);
        return plan();
    }
    
    // central virtual methods
    
    /**
     * Run the planning.
     * @return the solution policy function S&rarr;A found,
     *  or <code>null</code> if no solution could be found.
     */
    protected abstract Function/*<S,A>*/ plan();

    /**
     * Abstract base class for Markov decision processes solved per dynamic programming.
     *
     * @invariants getDiscount()&isin;[0,1]
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see orbital.algorithm.template.DynamicProgramming
     * @see "A. Barto, S. Bradtke, and S. Singh. Learning to act using real-time dynamic programming. <i>Artificial Intelligence</i>, 72:81-138, 1995."
     * @see "Bellman, R. E. (1957). Dynamic Programming. Princeton University Press, Princeton, New Jersey."
     * @todo possible to unify with orbital.algorithm.template.DynamicProgramming?
     */
    public static abstract class DynamicProgramming extends MarkovDecisionProcess implements HeuristicAlgorithm {
        private static final long serialVersionUID = 6262421425846708636L;
        /**
         * the current discount factor &gamma;.
         * @serial
         */
        private Real     discount;
        /**
         * the heuristic function h, used for unknown states.
         * @serial
         */
        private Function heuristic;

        /**
         * @param heuristic the heuristic function to use.
         * @param gamma The discount factor &gamma; describes, how much immediate results are
         *  preferred over future results.
         * @see #setHeuristic(Function)
         * @see #setDiscount(Real)
         */
        public DynamicProgramming(Function heuristic, Real gamma) {
            this.heuristic = heuristic;
            this.discount = gamma;
        }
        /**
         * @deprecated convenience constructor, prefer to use {@link Values#valueOf(double)}..
         */
        public DynamicProgramming(Function heuristic, double gamma) {
            this(heuristic, Values.getDefaultInstance().valueOf(gamma));
        }
        
        public DynamicProgramming(Function heuristic) {
            this(heuristic, 1);
        }

        /**
         * Set the discount factor &gamma;.
         * @param gamma The discount factor &gamma; describes, how much immediate results are
         *  preferred over future results.
         *  The higher the factor, the more balanced preference, the lower the factor, the more
         *  preference is taken for immediate results.
         *  For &gamma;=0, immediate costs are considered, only.
         *  For &gamma;=1, the undiscounted case, additional assumptions are required to produce
         *  a well-defined decision problem and ensure convergence.
         * @preconditions gamma&isin;[0,1]
         * @todo move to super class?
         */
        public void setDiscount(Real gamma) {
            if (!MathUtilities.isin(gamma, Values.ZERO, Values.ONE))
                throw new IllegalArgumentException("assert that discount " + gamma + " isin [0,1]");
            this.discount = gamma;
        }
        /**
         * Get the discount factor &gamma;.
         * @postconditions RES&isin;[0,1]
         */
        public Real getDiscount() {
            return discount;
        }
        
        public Function getHeuristic() {
            return heuristic;
        }
    
        /**
         * Set the heuristic function to use.
         * <p>
         * Note that the new heuristic function will only apply to unknown future states
         * for bootstrapping.
         * States that have already been estimated with the old heuristic function will not be updated.
         * Nevertheless its always safe to set the heuristic function immediately
         * before a call to {@link #plan()}.</p>
         */
        public void setHeuristic(Function heuristic) {
            this.heuristic = heuristic;
        }
    
        /**
         * Create a mapping.
         * <p>
         * Overwrite to implement another lookup table than hash maps. f.ex. neural networks etc.
         * However, beware of implicit function approximization and generalization techniques for
         * U, that might disturb the convergence of RTDP.</p>
         * @return an arbitrary table-like implementation ready to keep values for arguments.
         * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
         */
        protected MutableFunction createMap() {
            return new MutableFunction.TableFunction(getEvaluation());
        }

        /**
         * f(s) = h(s).
         */
        public Function getEvaluation() {
            return getHeuristic();
        }
    
        /**
         * Calculate the maximum expected utility (MEU) action.
         * @param Q the action-value cost function Q:S&times;A(s)&rarr;<b>R</b>
         *  evaluating the expected utility of actions in states.
         * @param state the state s&isin;S in which to take an action.
         * @return the Pair (a, Q)&isin;A(s)&times;<b>R</b> with maximum expected utility,
         *  which means minimum expected cost sum in this case.
         * @postconditions RES = (a,Q) &and; a = argmin<sub>a'&isin;A(s)</sub> Q(s,a')
         *  &and; Q = min<sub>a'&isin;A(s)</sub> Q(s,a').
         */
        protected orbital.util.Pair/*<A, Number>*/ maximumExpectedUtility(BinaryFunction/*<S,A,Real>*/ Q, Object/*>S<*/ state) {
            // search for minimal expected cost applicable action
            return PackageUtilities.min(getProblem().actions(state), Functionals.bindFirst(Q, state));
        }

        /**
         * Get the action-value cost function of an action and state.
         * @param U The evaluation function U:S&rarr;<b>R</b> mapping states to expected cost sum.
         * @return Q<sub>U</sub>:S&times;A(s)&rarr;<b>R</b>; (s,a) &#8614; Q<sub>U</sub>(s,a) = c(s,a) + &gamma;*&sum;<sub>s'&isin;S</sub> <b>P</b>(s'|s,a)*U(s').
         *  Q<sub>U</sub>(s,a) is the action-value cost of the action a&isin;A(s) in state s&isin;S as evaluated by U.
         * @see "C. J. C. H. Watkins. Learning from Delayed Rewards. PhD thesis, Cambridge University, Cambridge, England, 1989."
         */
        protected BinaryFunction/*<S,A,Real>*/ getActionValue(final Function/*<S,Real>*/ U) {
            return new BinaryFunction/*<S,A,Real>*/() {
                    public Object apply(Object state, Object action) {
                        // cost = Q<sub>U</sub>(s,a)
                        Scalar cost = valueFactory.ZERO;
                        Scalar originalCost = valueFactory.NaN;
                        if (logger.isLoggable(Level.FINEST)) logger.log(Level.FINEST, "DPMDP.Q", "\tc(" + action + "," + state + ") ...");
                        final MarkovDecisionProblem problem = getProblem();
                        final Iterator r = problem.states(action, state);
                        assert r.hasNext() : "@postconditions";
                        while (r.hasNext()) {
                            final Object sp = r.next();
                            final Transition t = (Transition) problem.transition(action, state, sp);
                            if (logger.isLoggable(Level.FINEST)) logger.log(Level.FINEST, "DPMDP.Q", "\t    + " + t.getProbability() + " * " + U.apply(sp) + " for " + sp);
                            cost = (Scalar) cost.add(t.getProbability().multiply((Arithmetic)U.apply(sp)));
                            final Real c = t.getCost();
                            assert !originalCost.equals(c, Values.getDefault().valueOf(MathUtilities.getDefaultTolerance())) : "@postconditions(getCost()): cost of transitions with the same action from the same state should be equal. Difference " + ((Real)originalCost.subtract(c)).doubleValue() + "=" + originalCost.subtract(c) + "=" + originalCost + "-" + c;
                            originalCost = c;
                        }
                        if (logger.isLoggable(Level.FINER)) logger.log(Level.FINER, "DPMDP.Q", "\tc(" + action + "," + state + ")\t= " + originalCost + " + " + getDiscount() + " * " +  cost);
                        return originalCost.add(getDiscount().multiply(cost));
                    }
                };
        }
    
        /**
         * Get a greedy policy with respect to an action-value cost function Q.
         * @param Q an action-value cost function Q:S&times;A(s)&rarr;<b>R</b>.
         * @return &pi;<sub>Q</sub> = &lambda;s. argmin<sub>a&isin;A(s)</sub> Q(s,a).
         * @see Greedy
         * @see #getActionValue(Function)
         * @interal see #maximumExpectedUtility(Object)
         */
        protected Function/*<S,A>*/ getGreedyPolicy(final BinaryFunction/*<S,A,Real>*/ Q) {
            return new Function/*<S,A>*/() {
                    public Object apply(Object state) {
                        if (logger.isLoggable(Level.FINER)) {
                            logger.log(Level.FINER, "DPMDP.policy", "CHOOSING " + state);
                            logger.log(Level.FINER, "DPMDP.policy", "CHOSE " + state + " do " + maximumExpectedUtility(Q, state));
                        }
                        // return the action chosen to take
                        return maximumExpectedUtility(Q, state).A;
                    }
                };
        }
    }
}
