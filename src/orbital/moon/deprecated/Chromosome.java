/**
 * @(#)Chromosome.java 0.9 1998/08/01 Andre Platzer
 * 
 * Copyright (c) 1995-1998 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

//TODO: use Chromosome[] as members of a population to express a list of Genes, or
//TODO: implement genes (accompanied by their interpretation)
//TODO: implement other mutations (like single-point, like slight interpreteted value mutation: +-1 .. +-4 not +-2^32)

import java.io.Serializable;
import orbital.math.Metric;
import java.util.Comparator;

import orbital.algorithm.UniqueShuffle;
import orbital.math.MathUtilities;
import orbital.util.Utility;

/**
 * This class is the common base class for chromosome data in a population.
 * For bio informatics, chromosomes and genome are synonyms.
 * <p>
 * A chromosome provides the following operators and functions:<ul>
 * <li><strong>mutate</strong> that defines how its mutation is done, depending on a probability.</li>
 * <li><strong>crossover</strong> for reproduction to generate children chromosomes.</li>
 * <li>an evaluation function.</li>
 * <li>and a difference measure.</li>
 * </ul>
 * </p>
 * <p>This implementation uses a generic set of boolean-data as chromosome data.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @deprecated since orbital1.0 Use Genome and Gene.BitSet instead.
 */
public class Chromosome implements Serializable {

    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long	   serialVersionUID = -8787664851609784302L;

    /**
     * Compare two chromosomes according to their fitness descending.
     */
    public static final Comparator comparator = new FitnessComparator();
    /**
     * @see Genome#comparator
     */
    private static class FitnessComparator implements Comparator, Serializable {
	public int compare(Object o1, Object o2) {
	    Chromosome a = (Chromosome) o1;
	    Chromosome b = (Chromosome) o2;
	    double	   aw = a.getFitness();
	    double	   bw = b.getFitness();
	    if (aw == bw)
		return 0;
	    else if (aw < bw)
		return +1;
	    else
		return -1;
	} 
    }
    
    /**
     * The set of Chromosome data represented as booleans.
     * binary string implementation.
     * <em>Evolves: could move to sub classes</em>.
     * @serial
     */
    protected boolean[] data;

    /**
     * The fitness this Chromosome is about to have according to the Algorithm
     * in evaluate().
     * Will be <code>Double.NaN</code> if it has not yet been calculated or some
     * chromosome data has changed.
     * @see #evaluate(boolean)
     * @serial
     */
    protected double	fitness = Double.NaN;

    /**
     * Create a Chromosome of a certain length.
     * @param length the number of boolean data flags in this chromosome.
     */
    public Chromosome(int length) {
	this.fitness = Double.NaN;
	this.data = new boolean[length];
	for (int i = 0; i < length; i++)
	    data[i] = false;
    }
    private Chromosome(boolean[] data, double fitness) {
	this.fitness = fitness;
	this.data = data;
    }

    // get/set methods

    /**
     * Get the length of the boolean data.
     */
    public final int length() {
	return data.length;
    } 

    /**
     * Get the fitness calculated the least recently.
     */
    public double getFitness() {
	return fitness;
    } 

    /**
     * Set the fitness calculated.
     */
    public void setFitness(double fitness) {
	this.fitness = fitness;
    } 
    public void setFitness(Number fitness) {
	setFitness(fitness.doubleValue());
    } 

    /**
     * Get the boolean value at index.
     * @return the boolean data at the bit with index.
     */
    public boolean get(int index) {
	if (index < 0 || index > length())
	    throw new ArrayIndexOutOfBoundsException(index + " should be in [0;" + length() + "[");
	return data[index];
    } 

    /**
     * Set the boolean value at index.
     * <p>
     * Consider setting fitness to <code>Double.NaN</code> due to the change to remind evaluator.</p>
     * @param value  the boolean value to be set at index.
     */
    public void set(int index, boolean value) {
	if (index < 0 || index > length())
	    throw new ArrayIndexOutOfBoundsException(index + " should be in [0;" + length() + "[");
	data[index] = value;
    } 

    /**
     * Get the chromosome data. Use with care!
     */
    public boolean[] getData() {
	return data;
    } 

    /**
     * Set the chromosome data. Use with care!
     * Will clear fitness.
     */
    public void setData(boolean[] n) {
	data = n;
	setFitness(Double.NaN);	   // fitness changed
    } 

    /**
     * Returns a copy of this chromosome.
     */
    public Object clone() {
	return new Chromosome(data, fitness);
    } 

    /**
     * Checks for equality.
     */
    public boolean equals(Object o) {
	if (o instanceof Chromosome) {
	    Chromosome B = (Chromosome) o;
	    if (length() != B.length())
		return false;
	    for (int i = 0; i < length(); i++)
		if (data[i] != B.data[i])
		    return false;
	    return true;
	} 
	return false;
    } 

    // central virtual methods
    // transformation methods

    /**
     * Get a <strong>mutated</strong> version of this Chromosome.
     * Each bit of chromosome data will be flipped with a specified probability.
     * @param probability the probability with that each bit of the chromosome mutates.
     */
    public Chromosome mutate(double probability) {
	Chromosome n = (Chromosome) clone();
	if (!MathUtilities.isProbability(probability))
	    throw new IllegalArgumentException("invalid probability");
	for (int i = 0; i < n.length(); i++)
	    if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), probability))
		n.data[i] = !n.data[i];
	setFitness(Double.NaN);	   // fitness changed
	return n;
    } 


    /**
     * Will genetically <strong>crossover</strong> Chromosome data from parents to their children
     * via reproduction.
     * <pre>
     * a       a  = direct ancestors to be used
     * n * --- ;    n  = number of children to be produced
     * p       p  = probability for each part of parent's chromosome to be inherited
     * a/p = elongation of chromosome length
     * n/a = growth of population size
     * if n/a &lt; 1 the population is contracting.
     * if n/a = 1 the population size is fixed.
     * if n/a &gt; 1 the population is growing.
     * </pre>
     * Usually it is p=n/a.
     * @param parents the chromosomes to be used as parents for the children.
     * <code>a</code> is the number of parents (direct ancestors).
     * @param children the chromosome objects that are set to be the children produced.
     * <code>n</code> is the number of children to be produced.
     * @param crossoverProbability the probability with that the inherited chromosome data is crossed over.
     * This does not necessarily imply an exchange of data, chromosomes might as well
     * realign or repair at random. But it makes a data crossover possible.
     */
    public void crossover(Chromosome[] parents, Chromosome[] children, double crossoverProbability) {
	if (!MathUtilities.isProbability(crossoverProbability))
	    throw new IllegalArgumentException("invalid probability");
	int			  a = parents.length;	  // a
	int			  n = children.length;	  // n
	double		  p = a;				  // p

	// uniformly distribute chromosome data of all parents over the children
	UniqueShuffle par = new UniqueShuffle(a);
	for (int i = 0; i < parents[0].length(); i++) {
	    if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), crossoverProbability))
		par.reShuffle(GeneticAlgorithm.geneticAlgorithm.getRandom());
	    else
		par.unShuffle();
	    for (int c = 0; c < n; c++)
		children[c].data[i] = parents[par.next()].data[i];
	} 

	// remember fitness change
	for (int c = 0; c < n; c++)
	    children[c].setFitness(Double.NaN);
    } 

    /**
     * Get an inverted version of this Chromosome.
     * @return inverted chromosome where all data booleans are negated.
     */
    public Chromosome inverse() {
	Chromosome r = new Chromosome(length());
	for (int i = 0; i < length(); i++)
	    r.data[i] = !data[i];
	r.setFitness(Double.NaN);	 // fitness changed
	return r;
    } 

    /**
     * Get the distance measure for this class.
     * @return a distance measure whose deviation is 1
     *  such that it can easily be used as a measure for similarity.
     *  Additionally, only positive numbers are returned.
     */
    protected Metric distanceMeasure() {
	return metric;
    } 
    private static final Metric metric = new Metric() {
	    public double distance(Object o1, Object o2) {
		Chromosome a = (Chromosome) o1;
		Chromosome b = (Chromosome) o2;
		if (a.length() != b.length())
		    return 0;
		int differences = 0;
		for (int i = 0; i < a.length(); i++)
		    if (a.data[i] != b.data[i])
			differences++;
		return differences / a.length();
	    } 
	};

    // fitness evaluation and caching implementation

    /**
     * Evaluates the fitness of the chromosomes, if necessary.
     * <p>
     * Default implementation will consider {@link GeneticAlgorithm#fitnessWeighting}.</p>
     * @param redo force whole evaluation again, even for cached fitness values.
     * Should usually be <code>false</code> for efficiency reasons.
     */
    public void evaluate(boolean redo) {
	if (fitness == fitness && !redo)	   // !isNaN and no force to redo
	    return;						   // since nothing changed
	if (GeneticAlgorithm.geneticAlgorithm.fitnessWeighting == null)
	    throw new IllegalStateException("GeneticAlgorithm.fitnessWeighting is not set to a Weighting-Instance");
	setFitness((Number) GeneticAlgorithm.geneticAlgorithm.fitnessWeighting.apply(this));
    } 


    /**
     * Returns a string representation of this object.
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < length(); i++)
	    sb.append(data[i] ? "1" : "0");
	return sb.toString();
    } 

    /**
     * Returns a new Chromosome object initialized to the value of the specified String.
     * The argument is interpreted as representing a sequence of boolean values coded as the characters <code>1</code> and <code>0</code>.
     * 
     * @param s the string to be parsed.
     * @return a newly constructed Chromosome initialized to the value represented by the string argument.
     * @throws NumberFormatException - if the string does not contain a parsable value.
     */
    public static Chromosome valueOf(String s) throws NumberFormatException {
	Chromosome c = new Chromosome(s.length());
	for (int i = 0; i < s.length(); i++)
	    switch (s.charAt(i)) {
	    case '1':
		c.data[i] = true;
		break;
	    case '0':
		c.data[i] = false;
		break;
	    default:
		throw new NumberFormatException("Chromosome data contains illegal character '" + s.charAt(i) + "' at index " + i);
	    }
	return c;
    } 
}
