/**
 * @(#)Backtracking.java 0.9 2000/06/22 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.List;
import orbital.math.functional.Function;

import orbital.math.functional.Operations;
import orbital.math.functional.Functions;
import orbital.math.functional.Functionals;
import java.util.ArrayList;

/**
 * Framework (template class) for Backtracking Algorithms.
 * 
 * <p>
 * Note that Backtracking is also a common mechanism for resolving nondeterministic choices.</p>
 * 
 * @version 0.9, 2000/06/23
 * @author Uwe A&szlig;mann
 * @author Andr&eacute; Platzer
 * @see BacktrackingProblem
 * @see DepthFirstSearch
 */
public class Backtracking/**<int numberOfVars>**/ implements AlgorithmicTemplate {
    //TODO: unify with DepthFirstSearch?
    public Object solve(AlgorithmicProblem p) {
	return solve((BacktrackingProblem) p);
    } 

    /**
     * @invariant currentChoices is an non­empty fixed-size list of partial solution values
     */
    private List	        currentChoices;
    private BacktrackingProblem solution;

    /**
     * solves by backtracking.
     * @return an array of the solution choices.
     */
    public List solve(BacktrackingProblem problem_solving) {
	solution = problem_solving;
	currentChoices = new ArrayList(solution.getNumberOfVars());
	if (solveByBacktracking(0))
	    return currentChoices;
	else
	    return null;
    } 

    /**
     * O(n<sup>n</sup>) in the worst case.
     */
    public Function complexity() {
	return Functionals.bind(Operations.power);
    } 

    public Function spaceComplexity() {
	//TODO: assure
	return Functions.id;
    }

    /**
     * @pre i is level upto which current choices are valid
     * @post RES is indicator whether extension worked
     */
    private final boolean solveByBacktracking(int depth) {

	// provide space for this depth
	currentChoices.add(depth, null);
	for (int j = 0; j < solution.getNumberOfVariants(depth); j++) {
	    currentChoices.set(depth, solution.chooseNext(depth));

	    if (currentChoices.get(depth) == null)
		return false;		// there are no more variants, backtrack

	    if (solution.isConsistent(currentChoices, depth)) {

		// consistent, recurse on
		if (depth + 1 >= solution.getNumberOfVars())
		    return true;	// we have it!

		if (!solveByBacktracking(depth + 1))
		    continue;		// extension did not work, try again
	    } 
	} 

	// if we arrive here, this partial solution cannot be extended further since we have no more variants to choose
	return false;
    } 

}
