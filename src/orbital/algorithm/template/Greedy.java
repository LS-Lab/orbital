/**
 * @(#)Greedy.java 1.0 2000/06/24 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.List;
import orbital.math.functional.Function;

import java.util.LinkedList;

import orbital.math.functional.Functions;
import orbital.math.functional.Operations;

/**
 * Framework (template class) for Greedy Algorithms.
 * The greedy algorithm does only perform local decisions.
 * 
 * @version 1.0, 2000/06/22
 * @author  Andr&eacute; Platzer
 * @see GreedyProblem
 * @see DynamicProgramming
 * @see HillClimbing
 * @note an optimization could keep the candidates in a heap if only our GreedyProblem would promise not to remove any candidates (and tells us new candidates only instead of those that we already knew). However which heap to choose might depend on the problem, binomial, fibonacci, ...
 */
public class Greedy implements AlgorithmicTemplate {
    public Object solve(AlgorithmicProblem p) {
	return solve((GreedyProblem) p);
    } 

    /**
     * solves by greedy.
     * <p id="canonicalGreedy">
     * The canonical greedy algorithm for a filtered system of sets (<span class="set">C</span>,<span class="family">U</span>) is
     * <pre>
     * {e<sub>1</sub>,&#8230;,e<sub>n</sub>} <span class="assignment">:=</span> <span class="set">C</span> sorted such that w(e<sub>1</sub>)&ge;&#8230;&ge;w(e<sub>n</sub>)
     * <span class="set">S</span> <span class="assignment">:=</span> &empty;
     * <span class="keyword">for</span> <var>i</var> <span class="operator">=</span> <span class="Number">1</span> <span class="keyword">to</span> n <span class="keyword">do</span>
     *     <span class="keyword">if</span> <span class="set">S</span><span class="operator">&cup;</span>{e<sub><var>i</var></sub>}&isin;<span class="family">U</span> <span class="keyword">then</span>
     *         <span class="comment">// optionally check that w(e<sub><var>i</var></sub>)&ge;0 if w has negative values</span>
     *         <span class="set">S</span> <span class="assingment">:=</span> <span class="set">S</span> <span class="operator">&cup;</span> {e<sub><var>i</var></sub>}
     *     <span class="keyword">end if</span>
     * <span class="keyword">end for</span>
     * <span class="keyword">return</span> <span class="set">S</span>
     * </pre>
     * Observe that the greedy algorithm does only perform local decisions.
     * </p>
     * <p>
     * Somewhat generalized, with a little more explicit structure,
     * and adapted to the concrete methods in the hook class,
     * the greedy algorithm in <a href="http://www-robotics.eecs.lehigh.edu/~bacon/setl-doc.html">SETL</a> looks like this:
     * <pre>
     * Set greedy(Set <span class="set">C</span>) {
     *     Set <span class="set">S</span> <span class="assignment">:=</span> &empty;;
     *     <span class="keyword">while</span> <span class="set">S</span> <span class="keyword">is</span> partialSolution <span class="keyword">and</span> <span class="set">C</span> &ne; &empty; <span class="keyword">do</span>
     *         <span class="comment">// weight is quality criterium</span>
     *         <i>retract <var>x</var> from <span class="set">C</span> such that w(<var>x</var>) is maximal (with regard to <span class="set">S</span>)</i>;
     *         <span class="comment">// nextPartialSolution computes new partial solution</span>
     *         <span class="set">S</span> <span class="assignment">:=</span> nextPartialSolution(<span class="set">S</span>,<var>x</var>);
     *         <span class="comment">// generalized case with changing candidates</span>
     *         <span class="set">C</span> <span class="assignment">:=</span> nextCandidates(<span class="set">C</span>);
     *     <span class="keyword">end while</span>;
     *     <span class="keyword">if</span> S <span class="keyword">is</span> solution <span class="keyword">then</span>
     *         <span class="keyword">return</span> S;
     *     <span class="keyword">else</span>
     *         <span class="keyword">return</span> &empty;;
     * }
     * </pre>
     * </p>
     * @return the list of the candidates chosen for the solution.
     * @internal optimizable we could remember the index of the current best candidate during search for removing it later on
     */
    public List solve(GreedyProblem p) {
	List C = p.getInitialCandidates();
	List S = new LinkedList();
	while (p.isPartialSolution(S) && !C.isEmpty()) {

	    // weighting is quality criterium
	    // retract x from C such that w(x) is maximal;
	    final Object x = PackageUtilities.max(C.iterator(), p.getWeightingFor(S)).A;
	    C.remove(x);

	    // nextPartialSolution computes new partial solution that includes x if feasible
	    S = p.nextPartialSolution(S, x);
	    // generalized case with changing candidates
	    C = p.nextCandidates(C);
	} 

	if (p.isSolution(S))
	    return S;
	else
	    return null;
    } 

    /**
     * O(n*log n + n*f(n)) for n=|C| candidates.
     * Provided that the running time of the independency check in {@link GreedyProblem#isPartialSolution(List)}
     * is f(n).
     * @internal note f(n) is considered to be in O(log n).
     */
    public Function complexity() {
	return (Function) Operations.times.apply(Functions.id, Functions.log);
    } 

    public Function spaceComplexity() {
	//TODO: assure
	return complexity();
    } 
}
