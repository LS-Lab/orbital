/**
 * @(#)DynamicProgrammingOptimizingProblem.java 0.9 2000/08/01 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Collection;
import orbital.logic.functor.Function;

import orbital.robotic.strategy.Evaluation;
import orbital.robotic.strategy.Selection.Selecting;

import orbital.util.Utility;

/**
 * Base hook class for problems solved by DynamicProgramming for optimization.
 * 
 * @version 0.9, 2000/08/01
 * @author  Andr&eacute; Platzer
 */
public abstract class DynamicProgrammingOptimizingProblem implements DynamicProgrammingProblem {

    /**
     * memorize weights analogue to partialSolutions
     */
    private Object[] partialWeights;

    /**
     * Initialize weights as the specified (possibly multidimensional) array of number-objects.
     * This should be called in the implementation of {@link DynamicProgrammingProblem#getInitialPartialSolutions()}.
     */
    public void init(Object[] weights) {
	this.partialWeights = weights;
    } 

    /**
     * Get the partial weight corresponding to the part specification.
     */
    public double getPartialWeight(int[] part) {
	Number w = ((Number) DynamicProgramming.getSolutionPart(part, partialWeights));
	return w == null ? Double.NaN : w.doubleValue();
    } 

    /**
     * Get the partial weights memorized analogue to partialSolutions.
     */
    public Object[] getPartialWeights() {
	return partialWeights;
    } 

    /**
     * Get the options available to optimize for the given part.
     */
    public abstract Collection getOptionsFor(int[] part);

    /**
     * Get an objective function.
     * <p>
     * If the weighting never changes for this problem, consider using a single weighting instance
     * instead of creating a new one on each call. This will increase efficiency.</p>
     * @param choices the current situation of choices.
     * @pre choices is a valid partial solution.
     * @post RES the objective weighting function for the current situation of choices
     * which will only be used upto the next call of this function.
     * @return the objective weighting function.
     * It will only be used upto the next call of this function.
     */
    //TODO: change return-type to Function/*<int[], Double>*/
    public abstract Function/*<Object, Number>*/ getWeightingFor(int[] part);

    public Object solve(int[] part, Object[] partialSolutions) {
	Collection options = getOptionsFor(part);
	Evaluation eval = new Evaluation(Selecting.max(), getWeightingFor(part));
	eval.addAll(options);
	Object optimum = eval.evaluate();

	// memorize weights as well to be dynamic
	Utility.setPart(partialWeights, part, eval.getSelection().getWeight());
	return optimum;
    } 
}
