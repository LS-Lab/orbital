/**
 * @(#)SimulatedAnnealing.java 0.9 2001/05/11 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Iterator;
import java.util.Collection;
import java.util.Random;

import java.util.List;

import orbital.logic.functor.Function;

import orbital.math.Values;

import java.util.Collections;
import orbital.util.Setops;
import orbital.util.Utility;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Simulated Annealing (SA) search.
 * A probabilistic and heuristic search algorithm
 * and local optimizer.
 * <p>
 * No special implementation data structure since simulated annealing <em>discards old</em> nodes.
 * Because of this "amnesy", simulated annealing is a <em>suboptimal</em> search strategy
 * and simulated annealing is not complete (but see below).
 * However, due to its Monte Carlo nature it has a certain probability of
 * avoiding local optima and actually reaching a global optimum.
 * In practical applications, it usually scores much better than random-restart hill-climbing.</p>
 * <p>
 * Simulated annealing problems can omit checking for solutions and simply wait until
 * the temperature drops to zero.</p>
 * <p>
 * Another possibility of using simulated annealing is to develop to a good balance of f at
 * a high temperature, first, and then decrease the temperature. Also performing
 * some optimization at temperature 0 still (equalling ordinary hill-climbing then),
 * ensures that the solution is a at least at a local optimum.
 * </p>
 * <p>
 * The Boltzman distribution specifies the probability of being at enery level E:=f(s) given
 * the current temperature T
 * <center><b>P</b>(E) = 1/z * <b>e</b><sup>-E/T</sup></center>
 * Where z is a normalization constant ensuring that <b>P</b> sums up to 1.
 * </p>
 * <dl id="Properties">
 *  <dt>theorem of convergence</dt>
 *  <dd>
 *    If T(n) &ge; (&Oslash;+1)&Delta; / &#13266;(n+2) and T(n)&rarr;0 (n&rarr;&infin;)
 *    <br />Then <b>P</b>("at a global optimum on the n-th move") &rarr; 1 (n&rarr;&infin;)
 *    <div>
 *      Where
 *      <ul>
 *        <li>T:<b>N</b>&rarr;<b>R</b> is the "temperature" scheduling function.</li>
 *        <li>&Oslash; := maximum distance of two solutions (number of moves in between).</li>
 *        <li>&Delta; := max {|f(s)-f(s&#697;)| &brvbar; s,s&#697;&isin;S &and; <b>P</b>(s&#697;|s)&gt;0}.
 *          Note that like in {@link MarkovDecisionProblem MDPs}
 *          <b>P</b>(s&#697;|s) is the probability of reaching s&#697; from s with one move.
 *        </li>
 *    </div>
 *  </dd>
 *  <dt>(behaviour at fixed temperature T)</dt>
 *  <dd>
 *    If &exist;lim<sub>n&rarr;&infin;</sub> <b>P</b>(s<sub>n</sub>=s)
 *    and |&#8899;<sub>a&isin;A(s)</sub>t(s,a)|&le;b is bounded,
 *    and the selection of next states occurs uniformly (each with probability 1/b)
 *    and <b>P</b>(s&rarr;s&#697;) / <b>P</b>(s&#697;&rarr;s) = <b>e</b><sup>-f(s&#697;)</sup> / <b>e</b><sup>-f(s)</sup>
 *    <br />Then <b>P</b>(s<sub>n</sub>=s) &rarr; <b>e</b><sup>-f(s)</sup> / &sum;<sub>s&#697;&isin;S</sub> (<b>e</b><sup>-f(s&#697;)</sup>) (n&rarr;&infin;)
 *    which does <em>only</em> depend on f(s).
 *    <div>
 *      Where
 *      <ul>
 *        <li><b>P</b>(s<sub>n</sub>=s) is the probability of being in state s at time n.</li>
 *        <li>&#8899;<sub>a&isin;A(s)</sub>t(s,a) is the set of states reachable from s (with any one action).</li>
 *        <li><b>P</b>(s&rarr;s&#697;) is the probability that the search algorithm accepts a move from s&isin;S to s&#697;&isin;S.</li>
 *    </div>
 *  </dd>
 * </dl>
 * <p>
 * lim<sub>n&rarr;&infin;</sub> <b>P</b>(s<sub>n</sub>=s) converges with any
 * initial state s<sub>0</sub> if the Markov system underlying the state transition
 * is ergodic (i.e. from any s&isin;S to any t&isin;S the is a path from s to t
 * with non-zero transition probability). It also converges if the Markov system
 * is homogenous (i.e. transition probabilities independent from time) and aperiodic.
 * Also, for example, the condition with the acceptance probability is satisfied
 * by simulated annealing, and thus metropolis search.
 * </p>
 *
 * @version 0.9, 2001/05/11
 * @author  Andr&eacute; Platzer
 * @see HillClimbing
 * @todo could move some parts into a super class
 */
public class SimulatedAnnealing extends LocalOptimizerSearch implements HeuristicAlgorithm, ProbabilisticAlgorithm {
    private static final long serialVersionUID = -1780030298934767181L;
    private static final Logger logger = Logger.getLogger(SimulatedAnnealing.class.getName());
    /**
     * The heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     * @serial
     */
    private Function/*<GeneralSearchProblem.Option, Arithmetic>*/ heuristic;
    /**
     * A mapping <b>N</b>&rarr;<b>R</b> from time to "temperature"
     * controlling the cooling, and thus the probability of downward steps.
     * @serial
     */
    private Function/*<Integer, Double>*/ schedule;
    /**
     * Create a new instance of simulated annealing search.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     * @param a mapping <b>N</b>&rarr;<b>R</b>
     *  from time to "temperature" controlling the cooling, and thus
     *  the probability of downward steps.
     *  Algorithm stops if the temperature drops to <span class="Number">0</span>
     *  (or isSolution is <span class="keyword">true</span>,
     *  or it fails due to a lack of alternative expansion nodes).
     */
    public SimulatedAnnealing(Function heuristic, Function schedule) {
    	this.heuristic = heuristic;
    	this.schedule = schedule;
    }

    public Function getHeuristic() {
	return heuristic;
    }

    public void setHeuristic(Function heuristic) {
	this.heuristic = heuristic;
    }

    /**
     * Get the scheduling function.
     * @return a mapping <b>N</b>&rarr;<b>R</b>
     *  from time to "temperature" controlling the cooling, and thus
     *  the probability of downward steps.
     *  Algorithm stops if the temperature drops to <span class="Number">0</span>
     *  (or isSolution is <span class="keyword">true</span>,
     *  or it fails due to a lack of alternative expansion nodes).
     */
    public Function getSchedule() {
	return schedule;
    }

    public void setSchedule(Function schedule) {
	this.schedule = schedule;
    }

    /**
     * f(n) = h(n).
     * @todo sure??
     */
    public Function getEvaluation() {
    	return getHeuristic();
    }

    /**
     * O(&infin;).
     */
    public orbital.math.functional.Function complexity() {
	return orbital.math.functional.Functions.constant(Values.POSITIVE_INFINITY);
    }
    /**
     * O(b) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function spaceComplexity() {
	return orbital.math.functional.Functions.constant(Values.symbol("b"));
    }
    public boolean isOptimal() {
    	return false;
    }

    public boolean isCorrect() {
	return false;
    }
	
    protected Iterator createTraversal(final GeneralSearchProblem problem) {
	return new OptionIterator(problem, this);
    }

    /**
     * An iterator over a state space in (probabilistic) greedy order for simulated annealing.
     * @version 1.0, 2001/08/01
     * @author  Andr&eacute; Platzer
     */
    private class OptionIterator extends LocalOptimizerSearch.OptionIterator {
	private static final long serialVersionUID = -5170488902830279816L;
	public OptionIterator(GeneralSearchProblem problem, ProbabilisticAlgorithm probabilisticAlgorithm) {
	    super(problem, probabilisticAlgorithm);
	    this.currentValue = ((Number) getEvaluation().apply(getState())).doubleValue();
	    this.t = 0;
	    // initialize to any value !=0 for hasNext() to return true. The real value will be calculated in  in accept(), anyway
	    this.T = Double.POSITIVE_INFINITY;
	}

	private double currentValue;
	private int t;
	// current temperature scheduled for successive cooling
	private double T;

	/**
	 * {@inheritDoc}.
	 * <p>
	 * This implementation will always move to better nodes,
	 * but only move to worse nodes with a probability of <b>e</b><sup>-&Delta;/T</sup>,
	 * depending upon the decrease &Delta;:=f(s&#697;)-f(s).</p>
	 */
	public boolean accept(GeneralSearchProblem.Option state, GeneralSearchProblem.Option sp) {
	    // current temperature scheduled for successive cooling
	    this.T = ((Number) getSchedule().apply(new Integer(t))).doubleValue();
	    this.t++;

	    final double value = ((Number) getEvaluation().apply(sp)).doubleValue();
	    final double deltaEnergy = value - currentValue;

	    // usually solution isSolution test is omitted, anyway, but we'll still call
	    // if (getProblem().isSolution(sp))
	    //     return true;

	    //@note negated use of deltaEnergy values everywhere since the evaluation evaluates cost and not utility (unlike Russel & Norvig who seem to consider maximizing energy instead of minimizing)
	    // always move to better nodes,
	    // but move to worse nodes, only with a certain probability
	    if (deltaEnergy <= 0
		|| Utility.flip(getRandom(), Math.exp(-deltaEnergy / T))) {
		if (logger.isLoggable(Level.FINER))
		    logger.log(Level.FINER, "simulated annealing update (" + currentValue +") to (" + value + ") delta=" + deltaEnergy + (deltaEnergy > 0 ? "" : " with probability " + Math.exp(-deltaEnergy / T)));
		// either an improvement, or decreasing by chance
		currentValue = value;
		return true;
	    } else
		return false;
	}

	public boolean hasNext() {
	    return T != 0;
	}
    };

    
    
//     protected GeneralSearchProblem.Option solveImpl(GeneralSearchProblem problem) {
// 	//@xxx Implement Iterator version. But the problem is how we can expand current again, all the time, and ignore node if it is too bad (but see below)
// 	Collection/*<Option>*/ nodes = createCollection();
// 	nodes.add(new GeneralSearchProblem.Option(problem.getInitialState()));
// 	return search(nodes);
//     }

//     protected Collection/*<Option>*/ createCollection() {
// 	return new java.util.LinkedList/*<Option>*/();
//     }

//     /**
//      * Select a node, randomly.
//      */
//     protected GeneralSearchProblem.Option select(Collection nodes) {
//     	List _nodes = (List) nodes;
//     	// in principle unnecessary since add will discard old list, anyway
//     	return (GeneralSearchProblem.Option) _nodes.remove(getRandom().nextInt(_nodes.size()));
//     }

//     /**
//      * discard old list, returning new.
//      */
//     protected Collection add(Collection newNodes, Collection oldNodes) {
//     	return newNodes;
//     }

//     //@todo transform since this is a kind of following (or executing) a probabilistic TransitionModel
//     protected GeneralSearchProblem.Option search(Collection nodes) {
// 	// current node @xxx should be initial state!
// 	GeneralSearchProblem.Option current = null;
// 	double currentValue = Double.POSITIVE_INFINITY;
// 	for (int t = 0; !nodes.isEmpty(); t++) {
// 	    // current temperature scheduled for successive cooling
// 	    final double T = ((Number) getSchedule().apply(new Integer(t))).doubleValue();
// 	    if (T == 0)
// 		return current;
// 	    final GeneralSearchProblem.Option node = select(nodes);

// 	    if (current == null) {	// @xxx current should already be initial state, ensure once!
// 		current = node;
// 		// currentValue will be set below, since currentValue == Double.POSITIVE_INFINITY
// 	    }

// 	    // usually solution isSolution test is omitted, anyway, but we'll still call
// 	    if (getProblem().isSolution(node))
// 		return node;
    		
// 	    final double value = ((Number) getEvaluation().apply(node)).doubleValue();
// 	    final double deltaEnergy = value - currentValue;
// 	    //@note negated use of deltaEnergy values everywhere since the evaluation evaluates cost and not utility (unlike Russel & Norvig who seem to consider maximizing energy instead of minimizing)
// 	    // always move to better nodes,
// 	    // but move to worse nodes, only with a certain probability
// 	    if (deltaEnergy <= 0
// 		|| Utility.flip(getRandom(), Math.exp(-deltaEnergy / T))) {
// 		if (logger.isLoggable(Level.FINER))
// 		    logger.log(Level.FINER, "simulated annealing update (" + currentValue +") to (" + value + ") delta=" + deltaEnergy + (deltaEnergy > 0 ? "" : " with probability " + Math.exp(-deltaEnergy / T)));
// 		// either an improvement, or decreasing by chance
// 		current = node;
// 		currentValue = value;
// 	    }

// 	    Collection children = orbital.util.Setops.asList(getProblem().expand(current));
// 	    nodes = add(children, nodes);
//     	}

//     	// current choice instead of failing
//     	return current;
//     }
}
