/**
 * @(#)SteadyStateGeneticAlgorithm.java 1.0 2001/03/30 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import java.util.List;

import java.util.Iterator;
import java.util.ArrayList;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A steady state genetic algorithm with overlapping populations.
 * Reproduces a given percentage of the population and replaces (if better).
 * <p>
 * Note: to make sure this algorithm is compatible with {@link ParallelEvaluationPopulation}
 * each new genome that arises is evaluated prior to updating the populaiton.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @structure delegate population:Population
 * @structure delegate selection:Function
 */
public class SteadyStateGeneticAlgorithm extends GeneticAlgorithm {
    private static final Logger logger = Logger.getLogger(SteadyStateGeneticAlgorithm.class.getName());
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = -7588440717264226789L;
    
    /**
     * The number of replacements to do at each generation (&le; population.size()).
     * We will perform as many reproductions such that at least <code>replacement</code> replacements
     * have occured.
     * @serial
     */
    private int numberOfReplacements = 0;
        
    /**
     * Construct a new GeneticAlgorithm.
     * @param replacements the number of replacements to do at each generation (&le; population.size()).
     *  We will perform as many reproductions such that at least <code>replacement</code> replacements
     *  have occured.
     */
    public SteadyStateGeneticAlgorithm(int replacements) {
        this.numberOfReplacements = replacements;
    }

    // for serialization only
    protected SteadyStateGeneticAlgorithm() {}

    /**
     * Get the number of replacements to do at each generation (&le; population.size()).
     * We will perform as many reproductions such that at least <code>replacement</code> replacements
     * have occured.
     */
    public int getNumberOfReplacements() {
        return numberOfReplacements;
    }
    public void setNumberOfReplacements(int replacements) {
        this.numberOfReplacements = replacements;
    }
        
    public double getPopulationGrowth() {
        return 1;
    } 

    // central virtual methods

    public void evolve() {
        PopulationImpl population = (PopulationImpl) getPopulation();
        for (int j = 0; j < numberOfReplacements; j += population.getParentCount()) {
            Genome children[] = population.reproduce();

            // merge children into new population
            for (int i = 0; i < children.length; i++) {
                children[i].evaluate(population, false);
                population.add(children[i]);
                logger.log(Level.FINER, "evolve child", "with " + children[i].getFitness());
                // remove worst (rank selector)
                population.remove(population.size() - 1);
            }
        }
        population.setGeneration(population.getGeneration() + 1);
    } 
}
