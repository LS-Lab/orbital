/**
 * @(#)BranchAndBound.java 1.0 2000/09/22 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Collection;

import orbital.math.functional.Functions;
import orbital.math.functional.Operations;
import orbital.math.Values;

/**
 * Branch-and-bound (B&B).
 * <p>
 * B&B is complete, optimal, and
 * has a time complexity of O(b<sup>d</sup>) and a space complexity of O(b*d).</p>
 *
 * @version 1.0, 2000/09/22
 * @author  Andr&eacute; Platzer
 * @internal Branch-and-bound is a technique from Operations Research.
 * @see "Lawler, E.L. and Wood, D.E. Branch-and-bound methods: A survey. Operations Research. 14(4):699-719. 1966."
 * @todo we could just as well formulate Branch-and-bound as a Decorator of GeneralSearchProblem (may be more useful for chaining and combining search algorithm policies).
 */
public class BranchAndBound extends DepthFirstBoundingSearch implements HeuristicAlgorithm {
    private static final long serialVersionUID = -1698181871423830937L;
    //TODO: is Operations Research Branch and Bound more general than this or equivalent? And what is Branch and Cut?
    /**
     * The applied heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @serial
     */
    private Function heuristic;
    /**
     * A sufficiently high upper bound for a solution beyond which search will not continue.
     * @serial
     */
    private double maxBound;
    
    /**
     * Create a new instance of Branch and Bound search.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @param maximumUpperBound is a sufficiently high upper bound for a solution.
     *  If there is no solution below this bound, the search will fail.
     * @see #getEvaluation()
     */
    public BranchAndBound(Function heuristic, double maximumUpperBound) {
    	setHeuristic(heuristic);
    	setContinuedWhenFound(true);
    	setMaxBound(maximumUpperBound);
    }
    
    /**
     * Get the maximum upper bound for a solution.
     */
    public double getMaxBound() {
    	return maxBound;
    }

    /**
     * Set the maximum upper bound for a solution.
     * @param bound is a sufficiently high upper bound for a solution.
     *  If there is no solution below this bound, the search will fail.
     */
    public void setMaxBound(double maximumUpperBound) {
    	this.maxBound = maximumUpperBound;
    }
	
    public Function getHeuristic() {
	return heuristic;
    }

    public void setHeuristic(Function heuristic) {
    	if (heuristic == null)
	    throw new NullPointerException("null is not a heuristic");
	this.heuristic = heuristic;
    }

    /**
     * f(n) = g(n) + h(n).
     */
    public Function getEvaluation() {
	return evaluation;
    }
    private transient Function evaluation = createEvaluation();
    private final Function createEvaluation() {
	return new Function() {
    		public Object apply(Object a) {
		    GeneralSearchProblem.Option o = (GeneralSearchProblem.Option)a;
		    return Operations.plus.apply(Values.valueOf(o.getCost()), heuristic.apply(o/*.getState()*/));
    		}
	    };
    }
    /**
     * Sustain transient variable initialization when deserializing.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	evaluation = createEvaluation();
    }
 

    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function complexity() {
	return (orbital.math.functional.Function) Operations.power.apply(Values.symbol("b"),Functions.id);
    }
    public boolean isOptimal() {
    	return true;
    }
        
    /**
     * Updates bound to solution cost.
     * Since better solutions can only be found below this bound.
     */
    protected GeneralSearchProblem.Option processSolution(GeneralSearchProblem.Option node) {
	setBound(node.getCost());
	return node;
    }
	
    protected GeneralSearchProblem.Option solveImpl(GeneralSearchProblem problem) {
	setBound(getMaxBound());
	return super.solveImpl(problem);
    }
}
