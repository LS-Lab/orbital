/**
 * @(#)DynamicProgramming.java 1.0 2000/07/31 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.math.functional.Function;

import orbital.math.functional.Functions;
import orbital.math.Values;
import orbital.util.Utility;

/**
 * Framework (template class) for Dynamic Programming Algorithms.
 * <p>
 * Requires that the problem exhibits <dfn>optimal substructure</dfn>, i.e.
 * optimal solutions to subproblems somehow generalize to optimal solutions
 * of the whole problem. Then each optimal solutions carries within it
 * independent optimal solutions to its subproblems. Further requires that the
 * <dfn>subproblems overlap</dfn>, i.e. a simple recursive formulation would
 * revisit the same subproblem over and over again.
 * </p>
 * <p>
 * bottom-up technique:
 * <ul>
 *   <li> A table of all sub-instance results is constructed. </li>
 *   <li> The entries corresponding to the smallest sub-instances are initiated at the
 *   start of the algorithm. </li>
 *   <li> The remaining entries are filled in following a precise order (that corresponds
 *   to increasing sub-instance size) using only those entries that have already
 *   been computed. </li>
 *   <li> Each entry is calculated exactly once. </li>
 *   <li> The final value computed is the solution to the initial problem instance. </li>
 *   <li> Implementation is by iteration (never by recursion, even though the analysis of
 *   a problem may naturally suggest a recursive solution). </li>
 * </ul>
 * top-down technique:
 * <ul>
 *   <li>Use the natural but inefficient recursive algorithm to solve the problem,
 *     and memoize results.
 *   </li>
 * </ul>
 * </p>
 * 
 * @version 1.0, 2000/07/31
 * @author  Andr&eacute; Platzer
 * @see DynamicProgrammingProblem
 * @see Greedy
 * @see HeuristicAlgorithm.PatternDatabaseHeuristic
 * @see "R. E. Bellman. Dynamic Programming. Princeton Universit Press, Princeton, NJ, 1957."
 * @todo could introduce an implicit form of DP that uses recursion to a base case with value reuse. However, explicit DP has a dramatic advantage in stack space consumption
 */
public class DynamicProgramming implements AlgorithmicTemplate {
    /**
     * a (possibly multidimensional) array containing the partial solutions already solved, or null.
     */
    private Object[] partialSolutions;

    public Object solve(AlgorithmicProblem p) {
	return solve((DynamicProgrammingProblem) p);
    } 

    /**
     * solves by dynamic programming.
     * @invariants partialSolutions is a (possibly multidimensional) array of partial solutions.
     * @postconditions solution is a merge of partial solutions.
     * @return the solution object as merged from the partial problems.
     */
    public Object solve(DynamicProgrammingProblem p) {
	partialSolutions = p.getInitialPartialSolutions();
	while (!p.isSolution(partialSolutions)) {

	    // the next part we divided the problem into
	    int[]  part = p.nextPart();

	    // solve part
	    Object psol = p.solve(part, partialSolutions);

	    // memorize this partial solution
	    setSolutionPart(part, partialSolutions, psol);
	} 

	// merge all partial solutions into the complete solution
	return p.merge(partialSolutions);
    } 

    /**
     * O(n<sup>2</sup>)
     */
    public Function complexity() {
	return Functions.pow(Values.getDefaultInstance().valueOf(2));
    } 

    public Function spaceComplexity() {
	//TODO: assure
	return complexity();
    } 

    // Convenience utilities methods @todo move to Utilities
	
    /**
     * Get the element in the (possibly multi-dimensional) array partialSolutions specified by the part specification.
     * @preconditions partSpecification.length is not lower than the number of dimensions for partialSolutions.
     * @return partialSolutions[partSpecification[0]][partSpecification[1]]...[partSpecification[partSpecification.length-1]] .
     * @see Utility#getPart(Object[],int[])
     */
    public static Object getSolutionPart(int[] partSpecification, Object[] partialSolutions) {
	return Utility.getPart(partialSolutions, partSpecification);
    } 

    public static void setSolutionPart(int[] partSpecification, Object[] partialSolutions, Object value) {
	Utility.setPart(partialSolutions, partSpecification, value);
    } 
}
