/**
 * @(#)ThresholdAccepting.java 1.0 2002/06/02 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Iterator;

import orbital.logic.functor.Function;

import orbital.math.Values;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Threshold Accepting (TA) search.
 * A probabilistic and heuristic search algorithm
 * and local optimizer.
 * <p>
 * The behaviour in practical applications approximates that of simulated annealing,
 * but this algorithm is a little faster.
 * </p>
 * <p>
 * At temperature 0 this algorithm equals ordinary hill-climbing.
 * </p>
 *
 * @version 1.0, 2002/06/02
 * @author  Andr&eacute; Platzer
 * @see SimulatedAnnealing
 * @see HillClimbing
 */
public class ThresholdAccepting extends ScheduledLocalOptimizerSearch {
    private static final long serialVersionUID = -1339322840710154421L;
    private static final Logger logger = Logger.getLogger(ThresholdAccepting.class.getName());
    /**
     * Create a new instance of threshold accepting search.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     * @param schedule a mapping <b>N</b>&rarr;<b>R</b>
     *  from time to "temperature" controlling the cooling, and thus
     *  the probability of downward steps.
     *  Algorithm stops if the temperature drops to <span class="Number">0</span>
     *  (or isSolution is <span class="keyword">true</span>,
     *  or it fails due to a lack of alternative expansion nodes).
     * @pre lim<sub>t&rarr;&infin;</sub>schedule(t) = 0 &and; schedule decreases monotonically
     */
    public ThresholdAccepting(Function/*<GeneralSearchProblem.Option, Arithmetic>*/ heuristic, Function/*<Integer, Real>*/ schedule) {
    	super(heuristic, schedule, FIRST_LOCAL_SELECTION);
    }


    /**
     * f(n) = h(n).
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
	return orbital.math.functional.Functions.constant(Values.getDefaultInstance().symbol("b"));
    }
    public boolean isOptimal() {
    	return false;
    }

    public boolean isCorrect() {
	return false;
    }
	
    protected Iterator createTraversal(final GeneralSearchProblem problem) {
	return new OptionIterator(getLocalSelection().createLocalRestriction(problem, this), this);
    }

    /**
     * An iterator over a state space in (probabilistic) greedy order for threshold accepting.
     * @version 1.0, 2001/08/01
     * @author  Andr&eacute; Platzer
     */
    private static class OptionIterator extends LocalOptimizerSearch.OptionIterator {
	private static final long serialVersionUID = -3674513421043835094L;
	public OptionIterator(GeneralSearchProblem problem, ScheduledLocalOptimizerSearch algorithm) {
	    super(problem, algorithm);
	    this.currentValue = ((Number) algorithm.getEvaluation().apply(getState())).doubleValue();
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
	 * but only move to worse nodes, if they worsen by at most T.</p>
	 */
	public boolean accept(Object/*>S<*/ state, Object/*>S<*/ sp) {
	    final ScheduledLocalOptimizerSearch algorithm = (ScheduledLocalOptimizerSearch) getAlgorithm();
	    // current temperature scheduled for successive cooling
	    this.T = ((Number) algorithm.getSchedule().apply(Values.getDefaultInstance().valueOf(t))).doubleValue();
	    this.t++;

	    final double value = ((Number) algorithm.getEvaluation().apply(sp)).doubleValue();
	    final double deltaEnergy = value - currentValue;

	    // usually solution isSolution test is omitted, anyway, but we'll still call
	    // if (getProblem().isSolution(sp))
	    //     return true;

	    // always move to better nodes,
	    // but move to worse nodes only if they are not too much worse
	    if (deltaEnergy <= T) {
		if (logger.isLoggable(Level.FINER))
		    logger.log(Level.FINER, "threshold accepting update (" + currentValue +") to (" + value + ") delta=" + deltaEnergy);
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
}
