/**
 * @(#)GeneralBoundingSearch.java 1.0 2000/09/20 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Collection;
import java.util.Iterator;

import orbital.math.Values;
import orbital.math.Real;

/**
 * Abstract general bounding search scheme.
 * <p>
 * A general search that only executes up to a specified bound (which might as well vary during search).
 * </p>
 *
 * @structure refines GeneralSearch
 * @structure extends GeneralSearch
 * @version 1.0, 2000/09/20
 * @author  Andr&eacute; Platzer
 */
public abstract class GeneralBoundingSearch extends GeneralSearch implements EvaluativeAlgorithm {
    private static final long serialVersionUID = -1094428950720102400L;
    /**
     * Whether the search is continued after a solution is found.
     * @serial
     */
    private boolean continuedWhenFound = true;
    
    /**
     * The current bound beyond which search will not continue.
     * @serial
     * @todo optimize internal representation to Scalar.
     */
    private double bound;
    
    /**
     * Get the current bound.
     * The search will only execute up to the specified bound
     * (which might as well vary during search).
     * @return the current bound beyond which search will not continue.
     * @see #isOutOfBounds(Object)
     */
    protected double getBound() {
	return bound;
    }

    /**
     * Set the current bound.
     */
    protected void setBound(double bound) {
	this.bound = bound;
	//TODO: make "bound" a bound property?
    }

    /**
     * Whether the search is continued after a solution is found.
     * @return whether to continue searching for even better solutions when
     *  a solution has already been found.
     */
    protected boolean isContinuedWhenFound() {
	return continuedWhenFound;
    }

    /**
     * Set whether the search should continue after a solution is found.
     * @param continueWhenFound whether to continue searching for even better solutions
     *  when a solution is already found.
     *  Subclasses should set it to <code>false</code> if they are assuming that
     *  the first solution found is already the best one,
     *  which cannot be guaranteed for all algorithms.
     *  Set to <code>true</code> if the search should continue (perhaps after bounding) to find
     *  even better solutions.
     */
    protected void setContinuedWhenFound(boolean continuedWhenFound) {
	this.continuedWhenFound = continuedWhenFound;
    }

    /**
     * Process a solution.
     * <p>
     * The default implementation simply returns the solution node description.
     * Overwrite to get more sophisticated behaviour.</p.>
     * @param node the node describing the solution.
     * @return the solution after processing it.
     * @pre getProblem().isSolution(node)
     */
    protected Object/*>S<*/ processSolution(Object/*>S<*/ node) {
	return node;
    }

    /**
     * Whether a node is out of bounds.
     * <p>
     * Called to check whether to prune a node.
     * This implementation checks whether f(n) &gt; bound.
     * Overwrite to get additional behaviour.
     * </p>
     * @param node the node to check.
     * @return whether the node is out of current bounds.
     * @see #getBound()
     * @see <a href="{@docRoot}/DesignPatterns/TemplateMethod.html">Template Method</a>
     * @todo would we profit from transforming bound into a Real?
     */
    protected boolean isOutOfBounds(Object/*>S<*/ node) {
	return Values.valueOf(getBound()).compareTo(getEvaluation().apply(node)) < 0;
    }
	
    //	protected Option search(Collection nodes) {
    //		/* contains current best (minimum) solution */
    //		Option bestSolution = null;
    //
    //		while (!nodes.isEmpty()) {
    //    		Option node = select(nodes);
    //
    //            if (isOutOfBounds(node))
    //            	continue;									// prune node
    //    		
    //    		if (problem.isSolution(node)) {
    //    			Option solution = processSolution(node);
    //    			if (bestSolution == null || solution.compareTo(bestSolution) < 0)
    //    				bestSolution = solution;
    //    			// continue to find even better solutions, or is it enough?
    //    			if (!isContinuedWhenFound())
    //    				break;
    //    		}
    //    		Collection children = orbital.util.Setops.asList(problem.expand(node));
    //    		nodes = add(children, nodes);
    //    	}
    //
    //    	// report best solution or fail
    //    	return bestSolution;
    //	}

    /**
     * {@inheritDoc}	
     * @see <a href="{@docRoot}/DesignPatterns/TemplateMethod.html">Template Method</a>
     * @internal Implemented as an iterative unrolling of a right-linear tail-recursion.
     */
    protected Object/*>S<*/ search(Iterator nodes) {
	final Function/*<S,Real>*/ g = getProblem().getAccumulatedCostFunction();
	/* contains current best (minimum) solution */
	Object/*>S<*/ bestSolution = null;
	/* contains the accumulated cost of bestSolution, thus the current best accumulated cost */
	Real bestAccumulatedCost = Values.NaN;

	while (nodes.hasNext()) {
	    Object/*>S<*/ node = nodes.next();
            
            if (isOutOfBounds(node)) {
            	nodes.remove();                             // prune node
            	continue;
            }
    		
	    if (getProblem().isSolution(node)) {
		Object/*>S<*/ solution = processSolution(node);
		Real accumulatedCost = (Real/*__*/) g.apply(solution);
		// @link orbital.util.Setops#argmin
		if (bestSolution == null || accumulatedCost.compareTo(bestAccumulatedCost) < 0) {
		    bestSolution = solution;
		    bestAccumulatedCost = accumulatedCost;
		}
		// continue to find even better solutions, or is it enough?
		if (!isContinuedWhenFound())
		    break;
	    }
    	}

    	// report best solution or fail
    	return bestSolution;
    }
}
