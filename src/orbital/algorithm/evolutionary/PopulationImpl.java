/**
 * @(#)PopulationImpl.java 1.1 2002-11-15 Andre Platzer
 * 
 * Copyright (c) 1995-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import java.io.Serializable;
import java.util.List;

import orbital.logic.functor.Function;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import orbital.math.Metric;
import orbital.math.MathUtilities;
import orbital.math.Real;
import orbital.math.Values;
import orbital.util.SuspiciousError;
import java.util.Collections;

import orbital.util.InnerCheckedException;

import java.util.logging.Logger;
import java.util.logging.Level;

// package-level protected circumscription
import orbital.moon.evolutionary.SelectionStatistics;

/**
 * This class implements a population of genomes and provides methods for genetic algorithms.
 *
 * @version 1.1, 2002-11-15
 * @author  Andr&eacute; Platzer
 */
public class PopulationImpl extends Population {
    private static final Logger logger = Logger.getLogger(PopulationImpl.class.getName());

    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = 5580906634238304628L;

    /**
     * The number of abstract parents required to produce children.
     * @serial
     */
    private int			  parentCount = Integer.MIN_VALUE;

    /**
     * The number of children produced with one reproduction involving parentCount parents.
     * @serial
     */
    private int			  childrenCount = Integer.MIN_VALUE;

    /**
     * Maximum probability rating of recombining parental genomes per production.
     * @serial
     */
    private double		  maximumRecombination = Double.NaN;

    /**
     * Maximum probability rating of mutation level for reproduction.
     * @serial
     */
    private double		  maximumMutation = Double.NaN;

    /**
     * Construct a new population with no properties set.
     * The properties must be set, lateron, like the other constructors already do initially.
     */
    public PopulationImpl() {
    }

    /**
     * Construct a new population.
     * @param parentCount The number of abstract parents required to produce children.
     * @param childrenCount The number of children produced with one reproduction involving parentCount parents.
     * @param maximumRecombination Maximum recombination rating.
     *  Maximum probability rating of recombining parental genomes per production.
     * @param maximumMutation Maximum mutation rating.
     *  Maximum probability rating of mutation level for reproduction.
     */
    public PopulationImpl(int parentCount, int childrenCount, double maximumRecombination, double maximumMutation) {
	this.parentCount = parentCount;
	this.childrenCount = childrenCount;
	setMaximumRecombination(maximumRecombination);
	setMaximumMutation(maximumMutation);
    }
    /**
     * Construct a new population.
     * Default number of childrens and parents used for reproduction is 2.
     */
    public PopulationImpl(double maximumRecombination, double maximumMutation) {
	this(2, 2, maximumRecombination, maximumMutation);
    }
    PopulationImpl(int capacity) {
	super(capacity);
    }
	

    /**
     * Returns a deep copy of this population.
     */
    public Object clone() {
	PopulationImpl c = (PopulationImpl) super.clone();
	c.setParentCount(getParentCount());
	c.setChildrenCount(getChildrenCount());
	c.setMaximumRecombination(getMaximumRecombination());
	c.setMaximumMutation(getMaximumMutation());
	return c;
    } 


    /*@todo add to equals?
	    if (childrenCount != b.childrenCount || parentCount != b.parentCount || maximumRecombination != b.maximumRecombination || maximumMutation != b.maximumMutation)
		return false;
    */

    public String toString() {
	return super.toString() + "\n[parentCount=" + parentCount + ",childrenCount=" + childrenCount +", maximumRecombination=" + maximumRecombination + ",maximumMutation=" + maximumMutation + "]";
    }	

    // get/set methods
    
    /**
     * Get the number of abstract parents required to produce children.
     */
    public int getParentCount() {
	return parentCount;
    } 
    /**
     * Set the number of abstract parents required to produce children.
     * @preconditions n > 0
     */
    public void setParentCount(int n) {
	if (!(n > 0))
	    throw new IllegalArgumentException("parentCount positive");
	parentCount = n;
    } 
    /**
     * Get the number of children produced with one reproduction involving parentCount parents.
     */
    public int getChildrenCount() {
	return childrenCount;
    } 
    /**
     * Set the number of children produced with one reproduction involving parentCount parents.
     * @preconditions n >= 0
     */
    public void setChildrenCount(int n) {
	if (!(n >= 0))
	    throw new IllegalArgumentException("childrenCount non-negative");
	childrenCount = n;
    } 

    /**
     * Get the maximum probability rating of recombining parental genomes per production.
     */
    public double getMaximumRecombination() {
	return maximumRecombination;
    } 
    /**
     * Set the maximum probability rating of recombining parental genomes per production.
     * @preconditions recombination&isin;[0,1] is a probability
     */
    public void setMaximumRecombination(double recombination) {
	if (!MathUtilities.isProbability(recombination))
	    throw new IllegalArgumentException("invalid probability " + recombination);
	maximumRecombination = recombination;
    } 
    /**
     * Get the maximum probability rating of mutation level for reproduction.
     */
    public double getMaximumMutation() {
	return maximumMutation;
    } 
    /**
     * Set the maximum probability rating of mutation level for reproduction.
     * @preconditions mutation&isin;[0,1] is a probability
     */
    public void setMaximumMutation(double mutation) {
	if (!MathUtilities.isProbability(mutation))
	    throw new IllegalArgumentException("invalid probability " + mutation);
	maximumMutation = mutation;
    } 

    //

    /**
     * Performs one reproduction returning the resulting children.
     * Selects parents and will recombine and mutate to produce children genomes.
     * @return the children produced.
     * @see #getSelection()
     * @see Genome#recombine(Gene[],int,double)
     * @see #getMaximumRecombination()
     * @see Genome#mutate(double)
     * @see #getMaximumMutation()
     * @preconditions getSelection() != null
     * @postconditions this.equals(OLD)
     * @todo already move to data part Population? It would currently know GeneticAlgorithm for getMaximumRecombination() anyway. However, we could also decide to move this information to Population as well, which would include almost all of the properties. @xxx decide soon
     */
    /*protected*/ Genome[] reproduce() {
	final Function selection = getGeneticAlgorithm().getSelection();
	if (selection == null)
	    throw new IllegalStateException("no selection object has been set");
	else if (getParentCount() < 0)
	    throw new IllegalStateException("no parentCount has been set");
	else if (getChildrenCount() < 0)
	    throw new IllegalStateException("no childrenCount has been set");
	else if (java.lang.Double.isNaN(getMaximumMutation()))
	    throw new IllegalStateException("no maximumMutation has been set");
	else if (java.lang.Double.isNaN(getMaximumRecombination()))
	    throw new IllegalStateException("no maximumRecombination has been set");
	DataCopy copy = null;
	assert (copy = new DataCopy(this)) != null;
		
	final Genome parents[] = new Genome[getParentCount()];

	// select parents
	for (int i = 0; i < parents.length; i++)
	    parents[i] = (Genome) selection.apply(this);
	SelectionStatistics.selectionStatistics.setSelected(this, parents);

	// overall parental similarity
	final double similarity = 1 - Population.overallDistance(parents);
	logger.log(Level.FINEST, "evolve", "OVERALL parental distance " + (1 - similarity));
	logger.log(Level.FINEST, "evolve", "OVERALL population distance " + getOverallDistance());

	// recombine children
	//@todo consider whether similarity * maximumRecombination would really be better?
	Genome children[] = (Genome[]) parents[0].recombine(parents, getChildrenCount(), getMaximumRecombination());
	assert children.length == getChildrenCount() : "childrenCount(" + getChildrenCount() + ") children expected";

	assert copy.validateReferentialIntegrity(this);

	// mutate inherited genome data
	for (int i = 0; i < children.length; i++)
	    children[i] = (Genome) children[i].mutate(similarity * getMaximumMutation());

	assert copy.validateReferentialIntegrity(this);
		
	return children;
    }

    /**
     * assert that references did not change since the given clone of the population and now.
     * <pre>
     * DataCopy copy = null;
     * assert (copy = new DataCopy(population)) != null;
     * ...
     * assert copy.validateReferentialIntegrity(population);
     * </pre>
     * @internal complex assertion validation with data copy
     */
    static class DataCopy {
	private final Population copy;
	private final Genome[] referenceCopy;
	DataCopy(Population population) {
	    this.copy = (Population) population.clone();
	    this.referenceCopy = (Genome[]) population.getMembers().toArray(new Genome[population.size()]);
	    validateReferentialIntegrity(population);
	}
	final boolean validateReferentialIntegrity(Population population) throws AssertionError {
	    assert population.equals(copy) : "population not yet changed " + population + "\n" + copy;
	    Genome[] referenceCopy2 = (Genome[]) population.getMembers().toArray(new Genome[population.size()]);
	    assert referenceCopy.length == referenceCopy2.length;
	    for (int i = 0; i < referenceCopy.length; i++) {
		assert referenceCopy[i] == referenceCopy2[i] : "assert that reference " + i + " has not yet changed";
	    }
	    return true;
	}
    }

    // Utilities
	
    public static Population create(Genome prototype, int size) {
	return create(new PopulationImpl(size), prototype, size);
    }
}
