/**
 * @(#)BacktrackingProblem.java 0.9 2000/06/24 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.List;

/**
 * Hook class for Backtracking Algorithms
 * @version 0.9, 2000/06/24
 * @author Uwe A&szlig;mann
 * @author Andr&eacute; Platzer
 * @see Backtracking
 * @see GeneralSearchProblem
 */
public
interface BacktrackingProblem/**<int numberOfVars, int numberOfVariants>**/ extends AlgorithmicProblem {

	/**
	 * private: numberOfVars x numberOfVariants Matrix of solution
	 * values for solutions
	 */

	/**
	 * Get the number of solution variables to find with Backtracking.
	 * @pre true
	 */
	int getNumberOfVars();

	/**
	 * Query on the number of solution variants for a single variable.
	 * @return the number of variants for choosing the solution variable at the specified depth.
	 * @pre depth >= 0
	 */
	int getNumberOfVariants(int depth);

	/**
	 * Checks whether the current choices are still consistent.
	 * @param choices a fixed-length list ({@link #getNumberOfVars()}) of partial solution values.
	 * @param depth is the depth level upto which current choices should be proved valid.
	 * @return whether the choices at the depth still represent a valid partial solution.
	 * @pre depth >= 0 is the depth level upto which current choices should be proved valid
	 * @post RES indicates whether valid partial solution
	 */
	boolean isConsistent(List choices, int depth);

	/**
	 * Get the next choice variant at a depth.
	 * @param depth is the depth level which should yield a new solution variant.
	 * @return a new solution value for the variable at depth.
	 * @pre depth >= 0
	 * @post RES is new solution value
	 */
	Object chooseNext(int depth);
}
