/**
 * @(#)AlphaBetaPruning.java 0.9 2001/07/01 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryPredicate;

import orbital.logic.functor.Predicates;
import java.util.Iterator;
import orbital.robotic.Position;
import orbital.robotic.Move;

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
 * @attribute time complexity O((b<sup>d+1</sup>-1)/(b-1)) in depth d with branching factor b.
 * @attribute query complexity {@link #getUtility()} O(b<sup>d</sup>) in depth d with branching factor b.
 * @see orbital.algorithm.template.BranchAndBound
 * @version 0.8, 2001/07/01
 * @author  Andr&eacute; Platzer
 * @internal min-max trees with compact values in [0,1] are and-or trees with fuzzy logic operators.
 * @todo generalize? implement a general interface?
 * @todo could optimize calculation to spend some memory for reuse of the last move's search tree
 * @todo in addition to the worst-case minimax values computed for
 * decision, we could additionally compute estimates of the probable
 * values. These can be estimated from the average minimax values
 * found in a node's succcessors so far. However, the distribution is
 * not easy, since the "Stichprobe" stems from a kind of bounded
 * drawing which is quitted (pruned) once too good?? or too bad
 * options occurred.  The advantage is that when _max knows several
 * options which have equally bad minimax values, then it could prefer
 * the one with higher (estimated) probable utility value. So in a
 * sense, when our opponent is not perfectly rational (according to
 * our utility), then we can still hope for the better cases.
 * Actually, there's also a trade-off when to prefer probably better
 * ones to worst-case better ones.  But all this must be optional
 * behaviour (so implemented in a subclass).
 */
public class AlphaBetaPruning extends AdversarySearch {
    static final Logger logger = Logger.getLogger(AlphaBetaPruning.class.getName());
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
     * The preference relation for options.
     * @serial
     */
    private BinaryPredicate/*<Option, Option>*/ preference;

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
     * @param preference the {@link #getPreference() preference relation} to use.
     */
    public AlphaBetaPruning(int maxDepth, Function/*<Object, Number>*/ utility, BinaryPredicate/*<Option,Option>*/ preference) {
        this.setMaxDepth(maxDepth);
        this.setUtility(utility);
	this.setPreference(preference);
        this.currentDepth = 0;
    }
    /**
     * Create a new instance of &alph;-&beta;-pruning adversary search
     * with default preference.
     * <p>
     * Use a preference relation that checks prefers v to w whenever v &gt; w.</p>
     */
     public AlphaBetaPruning(int maxDepth, Function/*<Object, Number>*/ utility) {
	this(maxDepth, utility, Predicates.greater);
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
     * Get the preference relation deciding which options to prefer.
     * <p>
     * Called to check which action to choose best.
     * Note that this method is (currently) only relevant at the first layer of states in the search tree.
     * Also note that this method's return value affects the utility value of the initial state.
     * </p>
     * @return a preference relation on {@link AdversarySearch.Option}s.
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     */
    protected BinaryPredicate/*<Option,Option>*/ getPreference() {
	return preference;
    }
    private void setPreference(BinaryPredicate/*<Option,Option>*/ newPreferenceRelation) {
	this.preference = newPreferenceRelation;
    }

    /**
     * The current depth during the current search.
     */
    protected final int getCurrentDepth() {
	return currentDepth;
    }

    /**
     * Search for the best option to take.
     * @param state in which state to choose an action.
     * @return the best move option (according to h).
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
	    final BinaryPredicate preference = getPreference();
	    final orbital.math.ValueFactory valueFactory = orbital.math.Values.getDefault();
	    // the best move found so far, always has utility alpha
	    Option bestOption = new NoOption();
	    bestOption.setUtility(alpha);
	    if (cutOff(state))
		throw new AssertionError("should never cut off the very first node prior to attempting any moves. currentDepth=" + currentDepth + ", maxDepth=" + maxDepth);
	    else
		for (Iterator s = successors(state); s.hasNext(); ) {
		    Option p = (Option) s.next();
		    double v = minimax(p.getState(), alpha, beta);
		    p.setUtility(v);
		    if (preference.apply(p, bestOption)) {
			logger.log(Level.FINEST, "evaluate utility: {1} for {0} preferred to {2} of {3}", new Object[] {format(v), p, format(alpha), bestOption});
			//@internal may also decrease alpha below  "alpha = Math.max(alpha, v);", but if that's our clients preference...
			alpha = v;
			bestOption = p;
		    } else {
			assert bestOption != null : "always prefer choices to null";
			logger.log(Level.FINEST, "evaluate utility: {1} for {0} =< {2} for {3}", new Object[] {format(v), p, format(alpha), bestOption});
		    }
		    if (alpha >= beta)
			break;
            	}
	    if (bestOption instanceof NoOption) {
		assert !successors(state).hasNext() || alpha != Double.NEGATIVE_INFINITY || beta != Double.POSITIVE_INFINITY : "at least with usual arguments there must have been no successors in order to produce no best option";
		return null;
	    } else {
		assert bestOption.getUtility() == alpha : "the best move found so far, has utility alpha";
		return bestOption;
	    }
        }
        finally {
	    currentDepth--;
        }
    }

    /**
     * Initial no option place holder.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2003-01-20
     */
    private static class NoOption extends Option {
	public NoOption() {
	    super((Field) null, (Position)null, (Figure)null, (Move)null);
	}

	public String toString() {
	    return getClass().getName();
	}
    }

    /**
     * Just a hook for nice formatting in logger outputs.
     */
    static final Number format(double d) {
	return new Double(d);
	/*return Double.isNaN(d) ? "<NaN>" : d == Double.POSITIVE_INFINITY ? "+<inf>"
	  : d == Double.NEGATIVE_INFINITY ? "-<inf>" : (d + "");*/
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
    private double minimax(final Field state, double alpha, double beta) {
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
		return ((Number) getUtility().apply(state)).doubleValue();
	    else
		for (Iterator s = successors(state); s.hasNext(); ) {
		    final Field nextState = ((Option) s.next()).getState();
		    alpha = Math.max(alpha, minimax(nextState, alpha, beta));
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
		return ((Number) getUtility().apply(state)).doubleValue();
	    else
            	for (Iterator s = successors(state); s.hasNext(); ) {
		    final Field nextState = ((Option) s.next()).getState();
		    beta = Math.min(beta, minimax(nextState, alpha, beta));
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
	return getCurrentDepth() % 2 == 0;
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
