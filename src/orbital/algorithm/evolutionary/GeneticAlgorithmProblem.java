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
 * @version 1.0, 2000/08/07
 * @author Andr&eacute; Platzer
 * @see GeneticAlgorithm
 * @see orbital.algorithm.template.GeneralSearchProblem
 * @todo can we use a general (derived) interface GeneticAlgorithmSearchProblem implements GeneralSearchProblem
 *  so that we can simply treat a search problem with a genetic algorithm
 */
public
interface GeneticAlgorithmProblem extends AlgorithmicProblem {

	/**
	 * Get the evaluation objective function.
	 * Specifies the algorithm for weighting of a Genome's fitness.
	 * <p>
	 * A genetic algorithm will maximize this objective function.</p>
	 */
	Function/*<Object, Number>*/ getWeighting();

	/**
	 * Generate (<strong>create</strong>) the initial population of genomes.
	 * @return the initial population created with an initial set of problem-specific Genomes.
	 * @see Population#create(Genome, int)
	 */
	// roughly corresponds to a set of (Genome-encoded) states that is part of the full state space
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
