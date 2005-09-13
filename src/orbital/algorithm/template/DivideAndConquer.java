/**
 * @(#)DivideAndConquer.java 1.0 2000/06/22 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.math.functional.Function;

import orbital.math.functional.Operations;
import orbital.math.functional.Functions;

/**
 * Framework (template class) for Divide and Conquer Algorithms.
 * <p>top-down technique</p>
 * @version $Id$
 * @author Uwe A&szlig;mann
 * @author  Andr&eacute; Platzer
 * @attribute stateless
 * @see DivideAndConquerProblem
 */
public class DivideAndConquer implements AlgorithmicTemplate {
    public Object solve(AlgorithmicProblem p) {
	return solve((DivideAndConquerProblem) p);
    } 

    /**
     * solves by divide and conquer.
     * @postconditions solution is a merge of partial solutions.
     * @return the solution object as merged from the partial problems.
     */
    public Object solve(DivideAndConquerProblem p) {
	return solveByDivideAndConquer(p);
    } 

    /**
     * &asymp;O(n*&#13266; n).
     * With O(&#13266; n) for divide and conquer
     * and O(n) for each merge.
     * <p>
     * More precisely, if you have a recurrence T(<var>n</var>) that describes the running time of
     * a divide and conquer algorithm that
     * divides a problem of size <var>n</var> into <var>p</var> subproblems of size <var>n</var>/<var>d</var>, each, 
     * and the cost of dividing and merging a problem of size <var>n</var> is f(<var>n</var>),
     * you can apply the master theorem to find an precise asymptotic bound for the running time
     * T(<var>n</var>).
     * </p>
     * <p id="MasterTheorem">
     * <h3 class="compact">Master Theorem</h3>
     * Let <span class="Formula">T(<var>n</var>) = <var>p</var>*T([<var>n</var>/<var>d</var>]) + f(<var>n</var>)</span>
     * with <var>p</var>&ge;1,<var>d</var>&gt;1,f:<b>N</b>&rarr;<b>R</b>.
     * Then
     * <table style="width: 100%;">
     *   <tr>
     *     <td rowspan="3">T(n) &#8712;</td>
     *     <td rowspan="3" style="font-size: 600%;">{</span></td>
     *     <td>&Theta;(n<sup>&#13266;<sub>d</sub>p</sup>)</td>
     *     <td>&#8656; &#8707;&epsilon;&gt;0 f(n)&#8712;O(n<sup>&#13266;<sub>d</sub>p-&epsilon;<sup>)</sup></sup></td>
     *   </tr>
     *   <tr>
     *     <td>&Theta;(n<sup>&#13266;<sub>d</sub>p</sup>&#8901;&#13266;n)</td>
     *     <td>&#8656; f(n)&#8712;&Theta;(n<sup>&#13266;<sub>d</sub>p<sup>)</sup></sup></td>
     *   </tr>
     *   <tr>
     *     <td>&Theta;(f(n))</td>
     *     <td>&#8656; &#8707;&epsilon;&gt;0 f(n)&#8712;&#937;(n<sup>&#13266;<sub>d</sub>p+&epsilon;</sup>)
     *       <br />
     *       &#8743; &#8707;c&lt;1 p&#8901;f([n/d]) &#8804; c&#8901;f(n) p.t. n&#8712;<b>N</b></td>
     *   </tr>
     * </table>	 
     * Note that we can either choose [<var>n</var>/<var>d</var>] to be the gaussian floor &lfloor;<var>n</var>/<var>d</var>&rfloor;,
     * or the gaussian ceiling &lceil;<var>n</var>/<var>d</var>&rceil;,
     * with both choices achieving the same asymptotic behaviour.
     * </p>
     */
    public Function complexity() {
	return (Function) Operations.times.apply(Functions.id, Functions.log);
    } 

    public Function spaceComplexity() {
	throw new UnsupportedOperationException("not yet implemented");
    } 

    /**
     * @invariants parts is an non­empty array of partial solutions
     * @postconditions solution is a merge of partial solutions.
     */
    private final Object solveByDivideAndConquer(DivideAndConquerProblem p) {
	if (p.smallEnough()) {	  // base case
	    return p.basicSolve();
	} else {				  // recursion
	    // divide problem
	    DivideAndConquerProblem[] parts = p.divide();
	    Object[]				  partialSolutions = new Object[parts.length];

	    // conquer partial problems independently
	    for (int i = 0; i < parts.length; i++) {
		partialSolutions[i] = solveByDivideAndConquer(parts[i]);
	    } 

	    // merge to solution
	    return p.merge(partialSolutions);
	} 
    } 
}
