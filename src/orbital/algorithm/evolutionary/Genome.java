/**
 * @(#)Genome.java 1.0 1998/08/01 Andre Platzer
 * 
 * Copyright (c) 1995-1998 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import java.util.Comparator;

import java.io.Serializable;
import orbital.logic.functor.Function;

/**
 * The Genome data in a population.
 * For bio informatics, genomes and chromosomes are treated as synonyms.
 * <p>
 * A Genome provides the following operators and functions:
 * <ul>
 *   <li><strong>mutate</strong> that defines how its mutation is done, depending on a probability.</li>
 *   <li><strong>recombine</strong> for recombining genetic information of the parents to generate genomes of the children (reproduction).</li>
 *   <li>an evaluation function.</li>
 *   <li>and a difference measure.</li>
 * </ul>
 * </p>
 * <p>
 * Genomes provide fitness caching and clearing.</p>
 * 
 * @version 1.0, 2000/03/28
 * @author  Andr&eacute; Platzer
 * @todo move to package orbital.algorithm.representation or anything
 */
public class Genome extends Gene.List {

    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long	   serialVersionUID = -6225184727917632864L;

    /**
     * Compare two Genomes according to their fitness, descending.
     */
    public static final Comparator/*<Genome, Genome>*/ comparator = new FitnessComparator();
    private static class FitnessComparator implements Comparator/*<Genome, Genome>*/, Serializable {
	private static final long serialVersionUID = -7579598676068557251L;
	public int compare(Object/*>Genome<*/ o1, Object/*>Genome<*/ o2) {
	    Genome a = (Genome) o1;
	    Genome b = (Genome) o2;
	    double	   aw = a.getFitness();
	    double	   bw = b.getFitness();
	    //@xxx won't work for NaN
	    if (aw == bw)
		return 0;
	    else if (aw < bw)
		return +1;
	    else
		return -1;
	} 
    }
    
    /**
     * The population that contains this genome.
     * @serial
     * @see #evaluate(Population, boolean)
     */
    //Population			population = null;
	
    /**
     * The fitness this Genome is about to have according to the Algorithm
     * in evaluate().
     * Will be <code>Double.NaN</code> if it has not yet been calculated or some
     * Genome data has changed.
     * @see #evaluate(Population, boolean)
     * @serial
     */
    private double	fitness = Double.NaN;

    public Genome() {}
    /**
     * Construct a genome with a single gene.
     * @param content the initial member gene.
     * @see #add(Object)
     */
    public Genome(Gene content) {
	add(content);
    }

    public Object clone() {
	Genome r = (Genome) super.clone();
	r.setFitness(getFitness());
	// already done in no-arg constructor, but to make it clear
	r.setPopulation(null);
	return r;
    } 

    public boolean equals(Object o) {
	return new Double(getFitness()).equals(new Double(((Genome) o).getFitness())) && super.equals(o);
    }
   	
    public int hashCode() {
	return new Double(getFitness()).hashCode() ^ super.hashCode();
    }

    // get/set methods

    /**
     * Get the fitness calculated the least recently.
     * @see #evaluate(Population, boolean)
     * @see #setFitness(double)
     */
    public double getFitness() {
	return fitness;
    } 
    /**
     * Whether genome has a fitness != Double.NaN.
     * @return !Double.isNaN(getFitness()).
     * @see #getFitness()
     */
    public boolean hasFitness() {
	return !Double.isNaN(getFitness());
    } 

    /**
     * Set the fitness calculated.
     */
    public void setFitness(double fitness) {
	this.fitness = fitness;
    } 
    public void setFitness(java.lang.Number fitness) {
	setFitness(fitness.doubleValue());
    } 
    /**
     * Clears the fitness.
     * Called to say that the fitness has changed and must be re-evaluated.
     * Sets the fitness to {@link Double#NaN}.
     * @see #setFitness(double)
     */
    public void clearFitness() {
	setFitness(Double.NaN);
    } 
	
    void setPopulation(Population population) {}

    /**
     * Use with care! Will not clear fitness if the return-value is modified.
     * This constraint does apply to all parts of the gene returned, as well,
     * if they are modified, the fitness <em>must</em> be cleared and re-evaluated.
     */
    public Object/*>Gene<*/ get(int i) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPermission(new RuntimePermission("accessDeclaredMembers"));
	} 
	return super.get(i);
    } 

    /**
     * Set one gene.
     * Will clear fitness.
     */
    public Object/*>Gene<*/ set(int i, Object/*>Gene<*/ g) {
	clearFitness();
	return super.set(i, g);
    } 
    /**
     * Add one gene.
     * Will clear fitness.
     */
    public boolean add(Object/*>Gene<*/ o) {
	clearFitness();
	return super.add(o);
    } 
    /**
     * Insert one gene.
     * Will clear fitness.
     */
    public void add(int index, Object/*>Gene<*/ o) {
	clearFitness();
	super.add(index, o);
    } 


    /**
     * Use with care!! Will not clear fitness if the return-value is modified.
     * @see #get(int)
     */
    public Object/*java.util.List*/ get() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPermission(new RuntimePermission("accessDeclaredMembers"));
	} 
	return super.get();
    } 
    /**
     * Set the Genome data.
     * Will clear fitness.
     */
    public void set(Object/*java.util.List*/ list) {
	clearFitness();
	super.set(list);
    } 

    // central virtual methods
    // transformation methods

    public Gene/*Genome*/ mutate(double probability) {
	Genome n = (Genome) super.mutate(probability);
	n.clearFitness();
	return n;
    } 

    public Gene/*Genome*/[] recombine(final Gene[] parents, int childrenCount, double recombinationProbability) {
	Genome[] children = (Genome[]) super.elementwiseRecombine(parents, childrenCount, recombinationProbability);
	// fitness changed
	for (int c = 0; c < children.length; c++)
	    children[c].clearFitness();
	return children;
    } 

    /**
     * Get an inverted version of this Genome.
     * @return inverted Genome where all data booleans are negated.
     */
    public Gene/*Genome*/ inverse() {
	Genome r = (Genome) super.inverse();
	r.clearFitness();
	return r;
    } 

    // fitness evaluation and caching implementation

    /**
     * Evaluates the fitness of the Genomes, if necessary.
     * <p>
     * Default implementation will consider {@link GeneticAlgorithm#fitnessWeighting}.</p>
     * @param redo force whole evaluation again, even for cached fitness values.
     *  Should usually be <code>false</code> for efficiency reasons.
     * @param population the population containing this genome.
     *  (Redundant information but required for better performance).
     * @see #getFitness()
     */
    public void evaluate(Population population, boolean redo) {
	// neither NaN nor forced to redo
	if (!(redo || !hasFitness()))
	    // since nothing changed
	    return;
	if (population == null)
	    throw new IllegalArgumentException("Population null, cannot evaluate genome");
	final GeneticAlgorithm algorithm = population.getGeneticAlgorithm();
	if (algorithm == null)
	    throw new IllegalStateException("Population is not part of a GeneticAlgorithm, cannot evaluate genome");
	final Function weighting = algorithm.getWeighting();
	if (weighting == null)
	    throw new IllegalStateException("use GeneticAlgorithm.setWeighting to set a Weighting-Instance, first, cannot evaluate genome");
	setFitness((java.lang.Number) weighting.apply(this));
    } 
}
