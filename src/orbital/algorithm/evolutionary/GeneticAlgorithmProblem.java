/**
 * @(#)GeneticAlgorithmProblem.java 1.0 2000/08/07 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import orbital.algorithm.template.AlgorithmicProblem;
import orbital.logic.functor.Function;

/**
 * Hook class for problems solved by GeneticAlgorithm.
 * <p>
 * Prior to using a genetic algorithm to solve a problem you need to decide:
 * <ol>
 *   <li>Which {@link Gene genetic <strong>representation</strong>} to use for the members in state space.</li>
 *   <li>What {@link #getWeighting() fitness-<strong>evaluation</strong>} to use as an objective function to maximize
 *    <em>and</em> guide search.</li>
 *   <li>Perhaps, define customized <code><a href="Gene.html#reproduce"><strong>reproduction</strong></a></code>
 *     operators for the gene representation, in order to support convergence with domain knowledge.</li>
 *   <li>Decide which {@link Selectors <strong>selection</strong>} function to apply.</li>
 * </ol>
 * These decisions characterizing a problem are encapsulated by an implementation of
 * <code>GeneticAlgorithmProblem</code>.
 * </p>
 * @version 1.0, 2000/08/07
 * @author Andr&eacute; Platzer
 * @see GeneticAlgorithm
 * @see orbital.algorithm.template.GeneralSearchProblem
 * @todo can we use a general (derived) interface GeneticAlgorithmSearchProblem implements GeneralSearchProblem
 *  so that we can simply treat a search problem with a genetic algorithm
 */
public interface GeneticAlgorithmProblem extends AlgorithmicProblem {

    /**
     * Get the evaluation objective function.
     * Specifies the algorithm for evaluating of a Genome's fitness.
     * <p>
     * A genetic algorithm will maximize this objective function.</p>
     * @see orbital.algorithm.template.EvaluativeAlgorithm#getEvaluation()
     * @todo rename, perhaps to getEvaluation()
     */
    Function/*<Object, Number>*/ getWeighting();

    /**
     * Generate (<strong>create</strong>) the initial population of genomes.
     * @return the initial population created with an initial set of problem-specific genomes
     *  defining the genetic representation for the members in the state space.
     * @see Population#create(Genome, int)
     * @internal roughly corresponds to a set of (Genome-encoded) states that is part of the full state space
     */
    Population getPopulation();

    /**
     * Check whether the given population of choices is a valid solution to the problem.
     * @pre partialSolutions resulted in repeated calls of solve after a single call to getInitialPartialSolutions.
     * @post RES indicates whether we found a solution to the problem
     * @return whether we found a solution to the problem.
     *  Often implemented with a convergence criterion.
     */
    boolean isSolution(Population pop);


    /**
     * Get the selection scheme to apply for evolving.
     * @see Selectors
     */
    // roughly corresponds to an evaluation function that is already minimizing choices
    //TODO: remove and do not use in GeneticAlgorithm.solve
    //Function getSelection();
    // TODO: perhaps: setSelection(Selection) in this _class_ as well?

    /**
     * Get GeneticAlgorithm constructor data as well?
     */
    // TODO: s.a. is no good idea, or what?
}
