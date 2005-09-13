/**
 * @(#)GreedyProblem.java 1.0 2000/06/24 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.List;

/**
 * Hook class for Greedy Algorithms.
 * <dl>
 * Let <span class="family">U</span>&sube;&weierp;(<span class="set">C</span>) be a family of subsets of a finite set <span class="set">C</span>.
 * Further let w:<span class="set">C</span>&rarr;<b>R</b> be the weighting function (usually w&ge;0).
 *   <dt>filtered system</dt>
 *   <dd>
 *     (<span class="set">C</span>,<span class="family">U</span>) is called a "filtered" system of sets, if
 *     <ul>
 *       <li>&empty;&isin;<span class="family">U</span></li>
 *       <li>&forall;A&sube;B&forall;B&isin;<span class="family">U</span> A&isin;<span class="family">U</span></li>
 *     </ul>
 *     <small>Note: filtered systems here are just the contrary to filters in the topological sense.</small>
 *   </dd>
 *   <dt>matroid</dt>
 *   <dd>
 *     A filtered system of sets (<span class="set">C</span>,<span class="family">U</span>) is called a matroid, if
 *     it satisfies the exchange property
 *     <ul>
 *       <li>&forall;<span class="set">A</span>,<span class="set">B</span>&isin;<span class="family">U</span> (|<span class="set">A</span>|&lt;|<span class="set">B</span>| &rarr; &exist;x&isin;<span class="set">B</span>&#8726;<span class="set">A</span> <span class="set">A</span>&cup;{x}&isin;<span class="family">U</span>)</li>
 *     </ul>
 *   </dd>
 *   <dt>weight of a set</dt>
 *   <dd>
 *     The total weight of the set <span class="set">M</span>&isin;<span class="family">U</span> is
 *     <center>w(<span class="set">M</span>) := &sum;<sub>m&isin;<span class="set">M</span></sub> w(m)</center>
 *   </dd>
 * </dl>
 * The optimization problem belonging to a filtered system (<span class="set">C</span>,<span class="family">U</span>) and
 * a weighting function w:<span class="set">C</span>&rarr;<b>R</b>
 * is to find a maximal (according to &sube;) set <span class="set">M<sup>*</sup></span>&isin;<span class="family">U</span>
 * with optimal weight
 * <center>w(<span class="set">M<sup>*</sup></span>) = max<sub><span class="set">M</span>&isin;<span class="family">U</span></sub> w(<span class="set">M</span>)</center>
 * <p>
 * <h5 class="compact">Proposition</h5>
 * Let (<span class="set">C</span>,<span class="family">U</span>) be a filtered system of sets.
 * The <a href="Greedy.html#canonicalGreedy">Greedy-Algorithm</a> finds an optimal solution for
 * each weighting function w:<span class="set">C</span>&rarr;<b>R</b>,
 * if and only if (<span class="set">C</span>,<span class="family">U</span>) is a matroid.
 * <h5 class="compact">Note</h5>
 * Even if (<span class="set">C</span>,<span class="family">U</span>) is not a matroid,
 * the greedy algorithm may still find optimal solutions for some,
 * but not for all weighting functions w:<span class="set">C</span>&rarr;<b>R</b>.
 * If in fact, a greedy algorithm does not even yield an optimal solution, it may nevertheless
 * be a good heuristic.
 * </p>
 * <p>
 * <small>
 * However note that some very simple greedy algorithms may be more intuitive if formulated
 * in a single implementation method rather than encapsulated in an instance of GreedyProblem.
 * </small>
 * </p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Greedy
 * @todo What's the connection of filtered system of sets with topological filters?
 * @internal Schrittweises Ausschöpfen alias Raffke Algorithmen alias Greedy Algorithmen.
 */
public interface GreedyProblem extends AlgorithmicProblem {

    /**
     * get the initial set of candidates.
     * @preconditions true
     * @postconditions RES is the initial alternative candidates for the solution.
     * @return the initial set of candidates, usually <span class="set">C</span>.
     * @see #nextCandidates(List)
     */
    List getInitialCandidates();

    /**
     * Extends the choices with a new_choice if that is feasible, otherwise nothing is changed.
     * @param choices the valid partial solution <span class="set">M</span>.
     * @param new_choice the new choice <var>x</var> with maximum local weight.
     * @preconditions choices is a valid partial solution, new_choice has maximum local weight.
     * @postconditions RES new solution value that includes new_choice if feasible.
     *  Usually RES=choices&cup;{new_choice}.
     * @return usually <span class="set">M</span>&cup;{x}, the choices extended by the new_choice
     * @see #isPartialSolution(List)
     */
    List nextPartialSolution(List choices, Object new_choice);

    /**
     * Test whether the given list of choices still is a valid (partial) solution.
     * @param choices a list <span class="set">M</span> of partial solution values.
     * @postconditions RES indicates whether valid partial solution
     * @return whether <span class="set">M</span>&isin;<span class="family">U</span>, i.e.
     *  whether <span class="set">M</span> is independent and thus an admissible partial solution.
     * @see #nextPartialSolution(List,Object)
     * @see #isSolution(List)
     */
    boolean isPartialSolution(List choices);

    /**
     * Get the next set of candidates.
     * <p>
     * If the list of candidates does not change this method can simply return candidates.</p>
     * @param the remaining set of candidates <span class="set">C</span> not yet considered.
     * @preconditions candidates are the current alternative candidates for the solution.
     * @postconditions: RES is the next alternative candidates for the solution.
     * @return the next alternative candidates for the solution.
     *  For strict matroids simply <span class="set">C</span>.
     * @see #getInitialCandidates()
     */
    List nextCandidates(List candidates);

    /**
     * Check whether the given list of choices is a valid solution to the problem.
     * @preconditions no more alternative candidatess or isPartialSolution is no longer true.
     * @postconditions RES indicates whether we found a solution to the problem
     * @see #isPartialSolution(List)
     */
    boolean isSolution(List choices);


    /**
     * Get an objective function.
     * <p>
     * This objective function will be maximized which means that
     * objects with a higher objective value are strictly preferred.
     * </p>
     * <p>
     * If the weighting function never changes for this problem, consider using a singleton
     * instead of creating a new one on each call. This will increase efficiency.
     * </p>
     * @param choices the current situation of choices.
     * @preconditions choices is a valid partial solution.
     * @postconditions RES the objective weighting function for the current situation of choices
     *  which will only be referenced until the next call of this function.
     *  Usually w&ge;0.
     * @return the objective weighting function w:<span class="set">C</span>&rarr;<b>R</b> on the candidates.
     * @note if this problem is a matroid, greedy will find a soltuion for any weighting function w.
     *  So we could set the weighting function in Greedy, directly, then.
     */
    Function/*<Object, Number>*/ getWeightingFor(List choices);

}
