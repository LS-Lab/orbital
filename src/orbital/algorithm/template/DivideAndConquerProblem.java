/**
 * @(#)DivideAndConquerProblem.java 1.0 2000/06/24 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

/**
 * Hook class for problems solved by DivideAndConquer.
 * @version 1.0, 2000/06/24
 * @author Uwe A&szlig;mann
 * @author  Andr&eacute; Platzer
 * @see DivideAndConquer
 */
public interface DivideAndConquerProblem extends AlgorithmicProblem {

    /**
     * Whether this problem is small enough to be solved as the base case
     * using {@link #basicSolve()}.
     * @see #basicSolve
     */
    boolean smallEnough();

    /**
     * Solve the base case.
     * @pre smallEnough()
     * @return the solution for this base case.
     * @see #smallEnough
     */
    Object basicSolve();

    /**
     * Divide this problem into several problem parts which can be solved independently.
     * Solving will then continue with these parts in ascending order, before
     * they will be merged.
     * @pre &not;smallEnough()
     * @return an array of (smaller) sub problems.
     */
    DivideAndConquerProblem[] divide();

    /**
     * Merge several partial solutions to a complete solution.
     * <p>
     * For single-sided divide and conquer, simply returns this as the answer.</p>
     * @param partialSolutions partial solutions
     * @pre &not;smallEnough()
     * @return the complete solution consisting of the partial solutions.
     */
    Object merge(Object[] partialSolutions);
}
