/**
 * @(#)IncrementalGeneticAlgorithm.java 1.0 1998/08/01 Andre Platzer
 * 
 * Copyright (c) 1995-1998 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import java.util.List;

import java.util.Iterator;
import java.util.ArrayList;

import orbital.util.Utility;

/**
 * An incremental genetic algorithm with overlapping populations and only
 * one reproduction per generation.
 * 
 * @version 1.0, 2000/03/28
 * @author  Andr&eacute; Platzer
 * @structure delegate population:Population
 * @structure delegate selection:Function
 */
public class IncrementalGeneticAlgorithm extends GeneticAlgorithm {
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = -8865758770147605049L;

    /**
     * Construct a new GeneticAlgorithm.
     */
    public IncrementalGeneticAlgorithm() {
    }

    public double getPopulationGrowth() {
	PopulationImpl population = (PopulationImpl) getPopulation();
	if (population == null)
	    return 1;
	return (double) (population.size() - population.getParentCount() + population.getChildrenCount()) / population.size();
    } 

    // central virtual methods

    public void evolve() {
	Genome children[] = ((PopulationImpl) getPopulation()).reproduce();

	// merge children into population
	replaceGenomes(children);
	getPopulation().setGeneration(getPopulation().getGeneration() + 1);
    } 

    /**
     * Replaces a list of Genomes by removing one and merging the new ones.
     * <p>
     * Will remove the required number of Genomes first and then insert
     * which makes sure that no Genome is removed immediately after insertion.</p>
     * @see #selectRemove()
     * @see Population#add(Object)
     * @pre n.length <= population.size()
     * @post n.length Genomes in population selected by selectRemove() are removed, and 
     *  those in n are added instead
     */
    protected void replaceGenomes(Genome[] n) {
	Population population = getPopulation();
	if (!(n.length <= population.size()))
	    throw new IllegalArgumentException("cannot replace more genomes than we have members");
	// for assertion
	int oldSize = -1;
	assert (oldSize = population.size()) >= 0;
	DataCopy copy = null;
	assert (copy = new DataCopy(population)) != null;
	// end for assertion

	for (int i = 0; i < n.length; i++) {
	    int crem;
	    population.remove(crem = selectRemove());
	    //@xxx sure that these assertions are right? the indices will change after each remove operation!
	    assert copy.addRemovedIndex(crem);
	}
	for (int i = 0; i < n.length; i++)
	    population.add(n[i]);

	assert copy.validateReferentialIntegrity(n);
    	assert population.size() == oldSize : "replace does not change size (too weak)";
    } 

    /**
     * @internal complex assertion validation with data copy
     */
    class DataCopy {
	private final Population copy;
	private final Genome[] referenceCopy;
	private final List		 removedIndices = new ArrayList();
	DataCopy(Population population) {
	    this.copy = (Population) population.clone();
	    this.referenceCopy = (Genome[]) population.getMembers().toArray(new Genome[population.size()]);
	}
	final boolean addRemovedIndex(int i) {
	    removedIndices.add(new Integer(i));
	    return true;
	}
	final boolean validateReferentialIntegrity(Genome[] n) throws AssertionError {
	    Genome[] referenceCopy2 = (Genome[]) getPopulation().getMembers().toArray(new Genome[getPopulation().size()]);
	    assert referenceCopy.length == referenceCopy2.length;
	    for (int i = 0, ni = 0; i < referenceCopy.length; i++)
		if (removedIndices.contains(new Integer(i))) {			// has been removed?
		    assert Utility.containsIdenticalTo(referenceCopy2, n[ni]) : "assert that we replaced reference " + i + " by " + n[ni++] + " not yet changed";
		} else                                        // except when fitness is so low that it has been removed. So assertion disabled
		    ;//assert Utility.containsIdenticalTo(referenceCopy2, referenceCopy[i]) || referenceCopy[i].getFitness() <= population.get(0, false).getFitness() : "assert we did not replace reference " + i + " aka " + referenceCopy[i] + " which is non-worst in\n" + population;
	    return true;
	}
    }

    // selection implementation methods

    /**
     * Selects which Genome to remove.
     * <p>
     * Genomes weighted less will more likely be removed.
     * Overwrite to implement a different selection behaviour.
     */
    private int selectRemove() {
	Population population = getPopulation();
	// Rank worst
	int	   count = 1;
	double worst = population.get(0, false).getFitness();
	for (int i = 1; i < population.size() && population.get(i, false).getFitness() == worst; i++)
	    count++;
	return population.size() - 1 - getRandom().nextInt(count);
    } 
}
