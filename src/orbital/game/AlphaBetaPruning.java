/**
 * @(#)AlphaBetaPruning.java 0.9 2001/07/01 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.logic.functor.Function;

import java.util.Iterator;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * &alpha;-&beta;-pruning adversary search.
 * <p>
 * &alpha;-&beta;-pruning performs exactly the same computation as a normal minimax search, but
 * prunes the search tree at nodes that permit moves even worse than the best one found so far.</p>
 *
 * @internal so to say, the pruning is the bound part of
 * BranchAndBound, &alpha; is the maximizer's bound and &beta; the
 * minimizer's bound. (But those bounds interact!) In other words,
 * &alpha;-&beta;-pruning is just Branch&amp;Bound in the adversary
 * search case.
 * @see orbital.algorithm.template.BranchAndBound
 * @version 0.8, 2001/07/01
 * @author  Andr&eacute; Platzer
 * @internal min-max trees with compact values in [0,1] are and-or trees with fuzzy logic operators.
 * @todo generalize? implement a general interface?
 * @todo could optimize calculation to spend some memory for reuse of the last move's search tree
 * @todo flexibilize computation such that not only move sequences Black,White,Black,White,... but also Black,Black,White,Black,White,White according to field.getTurn()
 */
public class AlphaBetaPruning extends AdversarySearch {
    private static final Logger logger = Logger.getLogger(AlphaBetaPruning.class.getName());
    /**
     * the maximum number of (half-) turns to look into the future
     * during search.  0 would mean no move, but does not make any
     * sense, since not a single option would then be inspected.
     * @serial
     */
    private int maxDepth;
    /**
     * the utility function with which to evaluate the
     * utility of a state beyond cut-off.
     * @serial
     */
    private Function/*<Object, Number>*/ utility;
	
    /**
     * The current depth during the current search.
     * @internal at least if not concurrent thread sychronizes us off while we are still running,
     *  we do not need to store this value.
     */
    private transient int currentDepth;

    /**
     * Create a new instance of &alph;-&beta;-pruning adversary search.
     * @param maxDepth the maximum number of (half-) turns to look
     * into the future during search.  0 would mean no move, but does
     * not make any sense, since not a single option would then be
     * inspected.
     * @param utility the utility function with which to evaluate the
     * utility of a state beyond cut-off.
     */
    public AlphaBetaPruning(int maxDepth, Function/*<Object, Number>*/ utility) {
        this.setMaxDepth(maxDepth);
        this.setUtility(utility);
        this.currentDepth = 0;
    }
    
    /**
     * Get the maximum number of (half-) turns to look into the future
     * during search.  0 would mean no move, but does not make any
     * sense, since not a single option would then be inspected.
     */
    public int getMaxDepth() {
    	return maxDepth;
    }

    private void setMaxDepth(int argMaxDepth) {
	if (argMaxDepth < 0)
	    throw new IllegalArgumentException("illegal maxDepth " + argMaxDepth + " < 0");
    	this.maxDepth = argMaxDepth;
    }

    /**
     * Get the utility function with which to evaluate the
     * utility of a state beyond cut-off.
     */
    public Function/*<Object, Number>*/ getUtility() {
	return utility;
    }
    private void setUtility(Function/*<Object, Number>*/ argUtility) {
	if (argUtility == null)
	    throw new NullPointerException("not a utility function " + argUtility);
	this.utility = argUtility;
    }

    /**
     * Whether a node with value v is preferred over one with w.
     * <p>
     * Called to check which action to choose best.
     * This implementation checks whether the v &gt; w.
     * Overwrite to get additional behaviour.
     * </p>
     * @return Whether a node with value v is preferred over one with w
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     */
    protected boolean isPreferred(double v, double w) {
	return v > w;
    }

    /**
     * Search for the best option to take.
     * @param state in which state to choose an action.
     * @return the best move option (according to h).
     * @preconditions this implementation assumes a two player game
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     */
    public Option solve(Field state) {
    	assert currentDepth == 0 : "search starts at currentDepth 0";
	try {
	    return max_(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}
	finally {
	    assert currentDepth == 0 : "search ends at currentDepth 0";
	}
    }
    
    /**
     * A modification of {@link #max(Field,double,double)} that returns the best move,
     * instead of its &alpha;-value.
     * @param alpha the value of the best (i.e. highest-utility) choice we have found so far,
     *  at any choice point along the path to state for the maximizer.
     * @param beta the value of the best (i.e., lowest-value) choice we have found so far,
     *  at any choice point along the path to state for the minimizer.
     * @return the best option (according to its &alpha;-value).
     */
    private Option max_(final Field state, double alpha, final double beta) {
	assert isOurLeaguesTurn(state) : "otherwise ours would not have an opportunity to move, anyway";
    	currentDepth++;
    	try {
	    Option bestOption = null;							// the best move found so far, has value alpha
	    if (cutOff(state))
		throw new AssertionError("should never cut off the very first node prior to attempting any moves. currentDepth=" + currentDepth + ", maxDepth=" + maxDepth);
	    else
		for (Iterator s = successors(state); s.hasNext(); ) {
		    Option p = (Option) s.next();
		    double v = minmax(p.getState(), alpha, beta);
		    if (isPreferred(v, alpha)) {
			logger.log(Level.FINEST, "evaluate utility", v + " for " + p + " PREFERRED");
			bestOption = p;
		    } else
			logger.log(Level.FINEST, "evaluate utility", v + " for " + p + " =< " + alpha);
		    alpha = Math.max(alpha, v);
		    if (alpha >= beta)
			break;
            	}
	    if (bestOption != null)
		bestOption.setUtility(alpha);
	    else {
		assert alpha != Double.NEGATIVE_INFINITY || beta != Double.POSITIVE_INFINITY || !successors(state).hasNext() : "at least with usual arguments there must have been no successors in order to produce no best option";
	    }
	    return bestOption;
        }
        finally {
	    currentDepth--;
        }
    }

    /**
     * Maximizer or minimizer decision.
     * Whether maximizer or minimizer decides is up to {@link #isOurLeague(Field)}.
     * @param alpha the value of the best (i.e. highest-utility) choice we have found so far,
     *  at any choice point along the path to state for the maximizer.
     * @param beta the value of the best (i.e., lowest-value) choice we have found so far,
     *  at any choice point along the path to state for the minimizer.
     * @return the minimax value of state.
     */
    private double minmax(final Field state, double alpha, double beta) {
	return isOurLeaguesTurn(state)
	    ? max(state, alpha, beta)
	    : min(state, alpha, beta);
    }
    
    /**
     * Maximizer decision.
     * @param alpha the value of the best (i.e. highest-utility) choice we have found so far,
     *  at any choice point along the path to state for the maximizer.
     * @param beta the value of the best (i.e., lowest-value) choice we have found so far,
     *  at any choice point along the path to state for the minimizer.
     * @return the minimax value of state.
     */
    private double max(final Field state, double alpha, final double beta) {
    	currentDepth++;
    	try {
	    if (cutOff(state))
		return ((Number) utility.apply(state)).doubleValue();
	    else
		for (Iterator s = successors(state); s.hasNext(); ) {
		    final Field nextState = ((Option) s.next()).getState();
		    alpha = Math.max(alpha, minmax(nextState, alpha, beta));
		    if (alpha >= beta)
			return beta;
		}
	    return alpha;
        }
        finally {
	    currentDepth--;
        }
    }

    /**
     * Minimizer decision.
     * @param alpha the value of the best (i.e. highest-utility) choice we have found so far,
     *  at any choice point along the path to state for the maximizer.
     * @param beta the value of the best (i.e., lowest-value) choice we have found so far,
     *  at any choice point along the path to state for the minimizer.
     * @return the minimax value of state.
     */
    private double min(final Field state, final double alpha, double beta) {
    	currentDepth++;
    	try {
	    if (cutOff(state))
		return ((Number) utility.apply(state)).doubleValue();
	    else
            	for (Iterator s = successors(state); s.hasNext(); ) {
		    final Field nextState = ((Option) s.next()).getState();
		    beta = Math.min(beta, minmax(nextState, alpha, beta));
		    if (beta <= alpha)
			return alpha;
            	}
	    return beta;
        }
        finally {
	    currentDepth--;
        }
    }

    protected boolean isOurLeaguesTurn(Field state) {
	return currentDepth % 2 == 0;
    }

    /**
     * Whether to cut off search at the given state and evaluate with the utility function, instead of expanding.
     * <p>
     * This implementation will cut off search after maxDepth.
     * Overwrite to get sophisticated behaviour.
     * </p>
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     */
    protected boolean cutOff(Field state) {
	return currentDepth > maxDepth;
    }
}
