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
 * @version $Id$
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
     */
    public SimpleGeneticAlgorithm() {
    }

    public double getPopulationGrowth() {
	PopulationImpl population = (PopulationImpl) getPopulation();
	return (double) population.getChildrenCount() / population.getParentCount();
    } 

    // central virtual methods

    public void evolve() {
	PopulationImpl population = (PopulationImpl) getPopulation();
	PopulationImpl.DataCopy copy = null;
	assert (copy = new PopulationImpl.DataCopy(population)) != null;

	Population newPopulation = population.newInstance((int) Math.ceil(population.size() * getPopulationGrowth()));
	newPopulation.setGeneticAlgorithm(this);
	for (int j = 0; j < population.size(); j += population.getParentCount()) {
	    Genome children[] = population.reproduce();
    
	    // merge children into new population
	    for (int i = 0; i < children.length; i++)
		newPopulation.add(children[i]);

	    assert copy.validateReferentialIntegrity(population);
	}
	newPopulation.setGeneration(population.getGeneration() + 1);
	//@xxx setPopulation does not work with the BreederControl
	//@todo caution: Population#evaluate(boolean) is called without neccessity
	setPopulation(newPopulation);
    } 
}
