/**
 * @(#)GeneticAlgorithm.java 1.0 1998/08/01 Andre Platzer
 * 
 * Copyright (c) 1995-1998 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import orbital.algorithm.template.ProbabilisticAlgorithm;
import orbital.algorithm.template.AlgorithmicProblem;
import java.io.Serializable;
import orbital.logic.functor.Function;
import java.util.List;

import java.util.Random;
import java.util.Iterator;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import orbital.math.MathUtilities;
import orbital.util.InnerCheckedException;
import orbital.util.SuspiciousError;

import orbital.math.functional.Operations;
import orbital.math.Values;

import java.util.logging.Logger;
import java.util.logging.Level;

// package-level protected circumscription
import orbital.moon.evolutionary.SelectionStatistics;

/**
 * A base class for genetic algorithms.
 * Genetic algorithms can be used as an
 * evolutionary search algorithm exploring or exploiting an almost arbitrary search space.
 * <p>
 * A genetic algorithm provides the following operators:<ul>
 *   <li><strong>evolve</strong> that will simulate the evolutionary search. (Decoupled into sub class)</li>
 *   <li><strong>select</strong> one of the Genomes. (Decoupled into selection function.)</li>
 * </ul></p>
 * <p>
 * Prior to using a genetic algorithm to solve a problem you need to decide:<ol>
 *   <li>Which genetic representation to use for the members in state space?</li>
 *   <li>What fitness-weighting to use as an objective function to maximize?</li>
 *   <li>Perhaps define customized reproduction operators for the {@link Gene} representation,
 *    in order to support convergence with domain knowledge.</li>
 *   <li>Decide which selection function to apply.</li>
 * </ol></p>
 * <p>
 * After implementing the {@link GeneticAlgorithmProblem} interface use the evolutionary genetic algorithm
 * like:
 * <pre>
 * ga = <span class="keyword">new</span> <span class="Orbital">IncrementalGeneticAlgorithm</span>(<span class="Number">2</span>,<span class="Number">2</span>, maximumRecombination, maximumMutation);
 * ga.setSelector(<span class="Orbital">Selectors</span>.rouletteWheel());
 * <span class="Class">Object</span> solution <span class="operator">=</span> ga.solve(<var>geneticAlgorithmProblem</var>);
 * </pre>
 * Or, if you need additional control of the single steps, use:
 * <pre>
 * ga = <span class="keyword">new</span> <span class="Orbital">IncrementalGeneticAlgorithm</span>(<span class="Number">2</span>,<span class="Number">2</span>, maximumRecombination, maximumMutation);
 * ga.setSelector(<span class="Orbital">Selectors</span>.rouletteWheel());
 * ga.setWeighting(<var>fitnessWeighting</var>);
 * ga.setPopulation(initialPopulation);
 * <span class="comment">// while stop condition is not true</span>
 * <span class="keyword">while</span> (<span class="operator">!</span><var>isSolution</var>()) {
 *     ga.evolve();
 *     <span class="Class">System</span>.out.println(ga.getPopulation());
 * }
 * </pre>
 * With a problem-specific implementation of the stop condition <code>isSolution()</code>
 * which could simply consider the number-of-generations, goodness-of-best-solution or convergence-of-population.
 * 
 * @version 1.0, 2000/03/28
 * @author  Andr&eacute; Platzer
 * @invariant sub classes must support no-arg constructor (for cloning)
 * @structure delegate population:Population
 * @structure delegate selection:Function
 * @see GeneticAlgorithmProblem
 * @see orbital.algorithm.template.HillClimbing
 * @see java.util.Random
 * @see "Goldberg, D. E. Genetic Algorithms in Search, Optimization and Machine Learning. .1989."
 * @see "Friedberg, R.M. A learning machine: Part I. IBM Journal, 2:2-13, 1958."
 * @see "Holland, J. H. Adaption in Natural and Artificial Systems. University of Michigan Press. 1975."
 * @todo improve algorithmic template implementation removing setXYZ things and constructor arguments?
 * @todo introduce sub classes ParallelGeneticAlgorithm (resembling ConcurrenceGeneticAlgorithm with parallel evaluation, but without Pair weighting)
 *  DemeGeneticAlgorithm, ParallelDemeGeneticAlgorithm (with parallel population processed in parallel)
 * @todo introduce getConvergence() and getPopulationConvergence()
 */
public abstract class GeneticAlgorithm implements ProbabilisticAlgorithm, Serializable {
    private static final Logger logger = Logger.getLogger(GeneticAlgorithm.class.getName());
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = -5214141290856811039L;

    
    /**
     * This field is <em>only</em> used to call {@link #getRandom()}.
     * @todo could we transform it into a static singleton random source, instead?
     * @todo static singleton is a rather ugly solution, is there a better one?
     */
    static GeneticAlgorithm geneticAlgorithm = null;

    /**
     * The number of abstract parents virtually required to produce children.
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
     * The selection scheme to apply while evolving.
     * @serial
     */
    private Function		  selection = null;

    /**
     * The Population for this GeneticAlgorithm.
     * @serial
     */
    private Population	  population = null;

    /**
     * Specifies the algorithm for weighting of a Genome's fitness.
     * It must be set before use and is concrete problem-specific.
     * @serial
     */
    private Function/*<Object, Number>*/ fitnessWeighting = null;

    /**
     * Construct a new GeneticAlgorithm.
     * @param parentCount The number of abstract parents virtually required to produce children.
     * @param childrenCount The number of children produced with one reproduction involving parentCount parents.
     * @param maximumRecombination Maximum recombination rating.
     *  Maximum probability rating of recombining parental genomes per production.
     * @param maximumMutation Maximum mutation rating.
     *  Maximum probability rating of mutation level for reproduction.
     */
    protected GeneticAlgorithm(int parentCount, int childrenCount, double maximumRecombination, double maximumMutation) {
	this();
	if (!MathUtilities.isProbability(maximumRecombination))
	    throw new IllegalArgumentException("invalid recombination probability " + maximumRecombination);
	if (!MathUtilities.isProbability(maximumMutation))
	    throw new IllegalArgumentException("invalid mutation probability " + maximumMutation);
	this.parentCount = parentCount;
	this.childrenCount = childrenCount;
	this.maximumRecombination = maximumRecombination;
	this.maximumMutation = maximumMutation;
    }

    /**
     * for deserialization only
     */
    protected GeneticAlgorithm() {
	if (geneticAlgorithm != null)
	    // ignore singleton since we only need its getRandom() source
	    ;//throw new InnerCheckedException(new InstantiationException("current implementation is a singleton"));
	geneticAlgorithm = this;
    }

    /**
     * Sustain singleton when deserializing since that seems to shirk constructor calls.
     * @see #GeneticAlgorithm()
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
	if (geneticAlgorithm != null)
	    // ignore singleton since we only need its getRandom() source
	    ;//throw new InnerCheckedException(new InstantiationException("current implementation is a singleton"));
	geneticAlgorithm = this;

	// prepare to read the alternate persistent fields
	java.io.ObjectInputStream.GetField fields = null;
	try { 
	    fields = in.readFields();
	} catch (ClassNotFoundException ex) {
	    throw new java.io.IOException();
	}

	parentCount = fields.get("parentCount", Integer.MIN_VALUE);
	childrenCount = fields.get("childrenCount", Integer.MIN_VALUE);
	maximumRecombination = fields.get("maximumRecombination", Double.NaN);
	maximumMutation = fields.get("maximumMutation", Double.NaN);
	selection = (Function) fields.get("selection", null);
	population = (Population) fields.get("population", null);
	fitnessWeighting = (Function) fields.get("fitnessWeighting", null);
	random = (Random) fields.get("random", null);
	    
	// read the alternate persistent fields
	// also read maximumCrossover, if no maximumRecombination was available
	if (fields.defaulted("maximumRecombination")) {
	    maximumRecombination = fields.get("maximumCrossover", Double.NaN);
	}
    }

    /**
     * Returns a deep copy of this exact type of genetic algorithm.
     */
    public Object clone() {
	GeneticAlgorithm c;
	try {
	    c = (GeneticAlgorithm) getClass().newInstance();
    	}
    	catch (InstantiationException e) {throw new InnerCheckedException("invariant: sub classes of " + GeneticAlgorithm.class + " must support no-arg constructor for cloning.", e);}
    	catch (IllegalAccessException e) {throw new InnerCheckedException("invariant: sub classes of " + GeneticAlgorithm.class + " must support no-arg constructor for cloning.", e);}
	c.setParentCount(getParentCount());
	c.setChildrenCount(getChildrenCount());
	c.setMaximumRecombination(getMaximumRecombination());
	c.setMaximumMutation(getMaximumMutation());
	c.setPopulation((Population) population.clone());
	//@todo clone is protected!! for c.setSelection((Function) selection.clone());
	return c;
    } 

    public boolean equals(Object o) {
	if (o != null && getClass() == o.getClass()) {
	    GeneticAlgorithm b = (GeneticAlgorithm) o;
	    if (childrenCount != b.childrenCount || parentCount != b.parentCount || maximumRecombination != b.maximumRecombination || maximumMutation != b.maximumMutation)
		return false;
	    // b's selection is allowed to have b assigned, so compare classes, only
	    return population.equals(b.population) && selection.getClass() == b.selection.getClass();
	} 
	return false;
    } 
	
    public int hashCode() {
	throw new InternalError("not yet implemented");
    }

    // get/set methods
	
    /**
     * Get the number of abstract parents virtually required to produce children.
     */
    public int getParentCount() {
	return parentCount;
    } 
    /**
     * Set the number of abstract parents virtually required to produce children.
     * @pre n > 0
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
     * @pre n >= 0
     */
    public void setChildrenCount(int n) {
	if (!(n >= 0))
	    throw new IllegalArgumentException("childrenCount non-negative");
	childrenCount = n;
    } 
    /**
     * Get the population growth factor.
     * @return the factor by which the population size increases with each generation (or decreases if &lt; 1).
     */
    public abstract double getPopulationGrowth();
	
    /**
     * Get the maximum probability rating of recombining parental genomes per production.
     */
    public double getMaximumRecombination() {
	return maximumRecombination;
    } 
    /**
     * Set the maximum probability rating of recombining parental genomes per production.
     * @pre recombination&isin;[0,1] is a probability
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
     * @pre mutation&isin;[0,1] is a probability
     */
    public void setMaximumMutation(double mutation) {
	if (!MathUtilities.isProbability(mutation))
	    throw new IllegalArgumentException("invalid probability " + mutation);
	maximumMutation = mutation;
    } 
    /**
     * Get the selection scheme to apply while evolving.
     */
    public Function getSelection() {
	return selection;
    } 
    /**
     * Set the selection scheme to apply while evolving.
     */
    public void setSelection(Function selector) {
	this.selection = selector;
    } 

    /**
     * Get the Population for this GeneticAlgorithm.
     * @todo finalize for performance?
     */
    public Population getPopulation() {
	return population;
    } 
    /**
     * Set the Population for this GeneticAlgorithm.
     * @internal @see Population#setGeneticAlgorithm(GeneticAlgorithm)
     */
    public void setPopulation(Population pop) {
	if (this.population != null)
	    this.population.setGeneticAlgorithm(null);
	this.population = pop;
	this.population.setGeneticAlgorithm(this);
	this.population.evaluate(false);
    } 
    /**
     * Get the weighting function.
     * @return the weighting function that specifies the algorithm for weighting of a Genome's fitness.
     *  It is concrete problem-specific.
     */
    public Function/*<Object, Number>*/ getWeighting() {
	return fitnessWeighting;
    } 
    /**
     * Set the weighting function.
     * @param fitnessWeighting the weighting function that specifies the algorithm for weighting of a Genome's fitness.
     *  It must be set before use and is concrete problem-specific.
     */
    public void setWeighting(Function/*<Object, Number>*/ fitnessWeighting) {
	this.fitnessWeighting = fitnessWeighting;
    } 

    /**
     * The central Random-generator for the genetic algorithm.
     * @serial the random source is serialized to let the seed persist.
     */
    private Random random = new Random();

    public Random getRandom() {
	return random;
    }

    public void setRandom(Random randomGenerator) {
	random = randomGenerator;
    }

    public boolean isCorrect() {
	return true;
    }

    public orbital.math.functional.Function complexity() {
	//@TODO return infinity instead?
	return orbital.math.functional.Functions.nondet;
    } 

    public orbital.math.functional.Function spaceComplexity() {
	return (orbital.math.functional.Function) Operations.power.apply(Values.valueOf(getPopulationGrowth()), orbital.math.functional.Functions.id);
    }



    // problem solving methods
    public Object solve(AlgorithmicProblem problem) {
	return solve((GeneticAlgorithmProblem) problem);
    }
    /**
     * Solve a genetic algorithm problem.
     * Assuming that the {@link #setSelection(Function) selector} has already been set.
     * @pre getSelection() != null
     * @return the population with the solution that was accepted by {@link GeneticAlgorithmProblem#isSolution(Population) isSolution}.
     * @see #setWeighting(Function)
     * @see GeneticAlgorithmProblem#getWeighting()
     * @see #setPopulation(Population)
     * @see GeneticAlgorithmProblem#getPopulation()
     * @see #evolve()
     */
    public Population solve(GeneticAlgorithmProblem problem) {
	if (getSelection() == null)
	    throw new IllegalStateException("no selection function is set");
	this.setWeighting(problem.getWeighting());
	this.setPopulation(problem.getPopulation());
	logger.log(Level.FINER, "created", this + System.getProperty("line.separator") + getPopulation());
	// while stop condition is not true
	while (!problem.isSolution(getPopulation())) {
	    evolve();
	    logger.log(Level.FINER, "evolve", getPopulation());
	} 
	return population;
    } 


    // central virtual methods

    /**
     * <strong>evolves</strong> to the next generation for this population.
     * Parents are selected and will recombine and mutate to produce children genomes
     * who will replace some genomes in this population.
     * This operation is sometimes called breeding.
     * @see #selection
     * @see Genome#recombine(Gene[],int,double)
     * @see #maximumRecombination
     * @see #maximumMutation
     */
    public abstract void evolve();

	
    // helper methods

    /**
     * Helper method that performs one reproduction.
     * Parents are selected and will recombine and mutate to produce child genomes
     * who will be returned.
     * @return the children produced.
     * @see #evolve()
     * @see #selection
     * @see Genome#recombine(Gene[],int,double)
     * @see #maximumRecombination
     * @see #maximumMutation
     * @pre selection != null
     * @post this.equals(OLD)
     */
    /*protected*/ Genome[] reproduce() {
	if (selection == null)
	    throw new IllegalStateException("no selection object has been set");
	DataCopy copy = null;
	assert (copy = new DataCopy(population)) != null;
		
	Genome parents[] = new Genome[parentCount];

	// select parents
	for (int i = 0; i < parents.length; i++)
	    parents[i] = (Genome) selection.apply(population);
	SelectionStatistics.selectionStatistics.setSelected(population, parents);

	// overall parental similarity
	double similarity = 1 - Population.overallDistance(parents);
	logger.log(Level.FINEST, "evolve", "OVERALL parental distance " + (1 - similarity));
	logger.log(Level.FINEST, "evolve", "OVERALL population distance " + population.getOverallDistance());
	// recombine children
	//@todo consider whether similarity * maximumRecombination would really be better?
	Genome children[] = (Genome[]) parents[0].recombine(parents, childrenCount, maximumRecombination);
	assert children.length == childrenCount : "childrenCount(" + childrenCount + ") children expected";

	assert copy.validateReferentialIntegrity();

	// mutate inherited Genome data
	for (int i = 0; i < children.length; i++)
	    children[i] = (Genome) children[i].mutate(similarity * maximumMutation);

	assert copy.validateReferentialIntegrity();
		
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
    class DataCopy {
	private final Population copy;
	private final Genome[] referenceCopy;
	DataCopy(Population population) {
	    this.copy = (Population) population.clone();
	    this.referenceCopy = (Genome[]) population.getMembers().toArray(new Genome[population.size()]);
	    validateReferentialIntegrity();
	}
	final boolean validateReferentialIntegrity() throws AssertionError {
	    assert population.equals(copy) : "population not yet changed " + population + "\n" + copy;
	    Genome[] referenceCopy2 = (Genome[]) population.getMembers().toArray(new Genome[population.size()]);
	    assert referenceCopy.length == referenceCopy2.length;
	    for (int i = 0; i < referenceCopy.length; i++) {
		assert referenceCopy[i] == referenceCopy2[i] : "assert that reference " + i + " has not yet changed";
	    }
	    return true;
	}
    }

    public String toString() {
	return getClass().getName() + "[parentCount=" + parentCount + ",childrenCount=" + childrenCount +", maximumRecombination=" + maximumRecombination + ",maximumMutation=" + maximumMutation + "]";
    } 
}
