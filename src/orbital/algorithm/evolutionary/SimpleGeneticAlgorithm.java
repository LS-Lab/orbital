/**
 * @(#)SimpleGeneticAlgorithm.java 1.0 2001/03/30 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import java.util.List;

import java.util.Iterator;
import java.util.ArrayList;

/**
 * A simple genetic algorithm with non-overlapping populations.
 * At each generation, it creates an entirely new population per reproduction.
 * 
 * @version 1.0, 2001/03/30
 * @author  Andr&eacute; Platzer
 * @structure delegate population:Population
 * @structure delegate selection:Function
 */
public class SimpleGeneticAlgorithm extends GeneticAlgorithm {
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = 7501114603877978617L;

    /**
     * Construct a new GeneticAlgorithm.
     * @param parentCount The number of abstract parents virtually required to produce children.
     * @param childrenCount The number of children produced with one reproduction involving parentCount parents.
     * @param maximumCrossover Maximum crossover rating.
     *  Maximum probability rating of mutation level per production.
     * @param maximumMutation Maximum mutation rating.
     *  Maximum probability rating of crossover level for reproducation.
     */
    public SimpleGeneticAlgorithm(int parentCount, int childrenCount, double maximumCrossover, double maximumMutation) {
	super(parentCount, childrenCount, maximumCrossover, maximumMutation);
    }

    // for serialization only
    protected SimpleGeneticAlgorithm() {}

    public double getPopulationGrowth() {
	return (double) getChildrenCount() / getParentCount();
    } 

    // central virtual methods

    /**
     * <strong>evolves</strong> to the next generation for this population.
     * Parents are selected and will crossover and mutate to produce children Genomes
     * who will replace some Genomes in this population.
     * This operation is sometimes called breeding.
     * @see GeneticAlgorithm#reproduce()
     */
    public void evolve() {
	if (getSelection() == null)
	    throw new IllegalStateException("no selection object has been set");
	Population population = getPopulation();
	DataCopy copy = null;
	assert (copy = new DataCopy(population)) != null;

	Population newPopulation = population.newInstance((int) Math.ceil(population.size() * getPopulationGrowth()));
	newPopulation.setGeneticAlgorithm(this);
	for (int j = 0; j < population.size(); j += getParentCount()) {
	    Genome children[] = reproduce();
    
	    // merge children into new population
	    for (int i = 0; i < children.length; i++)
		newPopulation.add(children[i]);

	    assert copy.validateReferentialIntegrity();
	}
	newPopulation.setGeneration(population.getGeneration() + 1);
	//@todo caution: Population#evaluate(boolean) is called without neccessity
	setPopulation(newPopulation);
    } 
}
