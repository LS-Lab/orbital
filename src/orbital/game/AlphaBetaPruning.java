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
 * <p>
 * <i><b>Note:</b> This class is subject to change since a substitute for {@link orbital.game.MoveWeighting.Argument}
 * may be required some day.</i></p>
 *
 * @version 0.8, 2001/07/01
 * @author  Andr&eacute; Platzer
 * @internal min-max trees with compact values in [0,1] are and-or trees with fuzzy logic operators.
 * @todo generalize? implement a general interface?
 * @todo could optimize calculation to spend some memory for reuse of the last move's search tree
 */
public class AlphaBetaPruning extends AdversarySearch {
    private static final Logger logger = Logger.getLogger(AlphaBetaPruning.class.getName());
    private int maxDepth;
    private int currentDepth;
    private Function/*<Object, Number>*/ utility;
	
    /**
     * @param utility the utility function with which to evaluate the utility of a state after cut-off.
     */
    public AlphaBetaPruning(int maxDepth, Function/*<Object, Number>*/ utility) {
        this.maxDepth = maxDepth;
        this.currentDepth = 0;
        this.utility = utility;
    }
    
    
    public int getMaxDepth() {
    	return maxDepth;
    }
    /**
     * Whether a node with value v is preferred over one with w.
     * <p>
     * Called to check which action to choose best.
     * This implementation checks whether the v &gt; w.
     * Overwrite to get additional behaviour.
     * </p>
     * @return Whether a node with value v is preferred over one with w
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
    	assert currentDepth == 0 : "search starts at currentDepth 0, and should as well come back to 0";
    	return max_(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    /**
     * a modification of max(Field,double,double) that returns the best move,
     * instead of its &alpha;-value.
     * @param alpha the value of the best choice we have found so far at any choice point along the path for the maximizer.
     * @param beta the value of the best (i.e., lowest-value) choice we have found so far at any choice point along the path for the minimizer.
     * @return the best option (according to its &alpha;-value).
     */
    private Option max_(final Field state, double alpha, final double beta) {
    	currentDepth++;
    	try {
	    Option bestOption = null;							// the best move found so far, has value alpha
	    if (cutOff(state))
		throw new AssertionError("should never cut off the very first node prior to attempting any moves. currentDepth=" + currentDepth + ", maxDepth=" + maxDepth);
	    else
		for (Iterator s = successors(state); s.hasNext(); ) {
		    Option p = (Option) s.next();
		    double v = min(p.getState(), alpha, beta);
		    if (isPreferred(v, alpha)) {
			logger.log(Level.FINEST, "evaluate utility", v + " for " + p + " PREFERRED");
			bestOption = p;
		    } else
			logger.log(Level.FINEST, "evaluate utility", v + " for " + p + " =< " + alpha);
		    alpha = Math.max(alpha, v);
		    if (alpha >= beta)
			break;
            	}
	    bestOption.setUtility(alpha);
	    return bestOption;
        }
        finally {
	    currentDepth--;
        }
    }

    /**
     * maximizer decision.
     * @param alpha the value of the best choice we have found so far at any choice point along the path for the maximizer.
     * @param beta the value of the best (i.e., lowest-value) choice we have found so far at any choice point along the path for the minimizer.
     * @return the minimax value of state.
     */
    private double max(final Field state, double alpha, final double beta) {
    	currentDepth++;
    	try {
	    if (cutOff(state))
		return ((Number) utility.apply(state)).doubleValue();
	    else for (Iterator s = successors(state); s.hasNext(); ) {
		alpha = Math.max(alpha, min(((Option) s.next()).getState(), alpha, beta));
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
     * minimizer decision.
     * @param alpha the value of the best choice we have found so far at any choice point along the path for the maximizer.
     * @param beta the value of the best (i.e., lowest-value) choice we have found so far at any choice point along the path for the minimizer.
     * @return the minimax value of state.
     */
    private double min(final Field state, final double alpha, double beta) {
    	currentDepth++;
    	try {
	    if (cutOff(state))
		return ((Number) utility.apply(state)).doubleValue();
	    else
            	for (Iterator s = successors(state); s.hasNext(); ) {
		    beta = Math.min(beta, max(((Option) s.next()).getState(), alpha, beta));
		    if (beta <= alpha)
			return alpha;
            	}
	    return beta;
        }
        finally {
	    currentDepth--;
        }
    }

    /**
     * Whether to cut off search at the given state and evaluate with the utility function, instead of expanding.
     * <p>
     * This implementation will cut off search after maxDepth.
     * Overwrite to get sophisticated behaviour.
     * </p>
     */
    protected boolean cutOff(Field state) {
	return currentDepth > maxDepth;
    }
}
