/**
 * @(#)Population.java 0.9 1998/08/01 Andre Platzer
 * 
 * Copyright (c) 1995-1998 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import java.io.Serializable;
import java.util.List;

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

/**
 * This class represents a population of genomes as a data structure.
 * Some of these Genomes will reproduce with {@link Genome#recombine(Gene[],int,double) Genome.recombine}
 * to generate the members of the next generation.
 * <p>
 * A population provides the following operators and functions:
 * <ul>
 *   <li><strong>create</strong> an initial set of genomes.</li>
 * </ul>
 * </p>
 *
 * @structure extend List<Genome> sorted on descending {@link Genome#getFitness()}
 * @structure aggregate members:List<Genome>
 * @invariant sub classes must support nullary constructor (for cloning)
 * @version 1.0, 2000/03/28
 * @author  Andr&eacute; Platzer
 */
public abstract class Population implements Serializable /*//TODO: extends DelegateList<Genome> sometime, but this is sorted! */ {
    private static final Logger logger = Logger.getLogger(Population.class.getName());

    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = 3858632627875948854L;

    /**
     * The genetic algorithm evolving this population.
     * @serial
     */
    private GeneticAlgorithm  geneticAlgorithm;

    /**
     * The current generation count.
     * @serial
     */
    private int		      generation;

    /**
     * The Genomes that are members of this population.
     * This sorted list is kept sorted according to the {@link Genome#getFitness() fitness}.
     * @serial
     */
    private List	      members;

    /**
     * Create an empty population.
     */
    protected Population() {
	init();
	this.members = new ArrayList();
    }

    /**
     * Create an empty population with the given capacity.
     */
    Population(int capacity) {
	init();
	this.members = new ArrayList(capacity);
    }
	
    private void init() {
	this.generation = 0;
	this.geneticAlgorithm = null;
    }

    /**
     * Create a new instance of the exact same type ensuring a minimum capacity.
     */
    Population newInstance(int capacity) {
	try {
	    Population l = (Population) getClass().newInstance();
	    //@XXX: members instantiated twice for Population type, now, could optimize
	    l.members = new ArrayList(capacity);
	    return l;
    	}
    	catch (InstantiationException e) {throw new InnerCheckedException("invariant: sub classes of " + Population.class + " must support nullary constructor for cloning.", e);}
    	catch (IllegalAccessException e) {throw new InnerCheckedException("invariant: sub classes of " + Population.class + " must support nullary constructor for cloning.", e);}
    }

    /**
     * Returns a deep copy of this population.
     */
    public Object clone() {
	Population n = newInstance(size());
	n.generation = generation;
	//@internal direct copy to members without merge is stable and will not reorder equal fitnesses
	for (Iterator i = iterator(); i.hasNext(); )
	    n.members.add(((Genome) i.next()).clone());
	return n;
    } 

    /**
     * Checks for equality.
     */
    public boolean equals(Object o) {
	if (o instanceof Population) {
	    Population b = (Population) o;
	    return getGeneration() == b.getGeneration() && members.equals(b.members);
	} 
	return false;
    } 
	
    public int hashCode() {
	return getGeneration() ^ members.hashCode();
    }


    // get/set methods
    
    GeneticAlgorithm getGeneticAlgorithm() {
    	return geneticAlgorithm;
    }
    void setGeneticAlgorithm(GeneticAlgorithm geneticAlgorithm) {
    	this.geneticAlgorithm = geneticAlgorithm;
    }
	
    /**
     * Get the current generation count.
     */
    public int getGeneration() {
	return generation;
    } 
    protected void setGeneration(int newGeneration) {
	this.generation = newGeneration;
    } 

    /**
     * Gets the i-th member-Genome.
     */
    public Genome get(int i) {
	try {
	    return (Genome) members.get(i);
	} catch (ClassCastException oops) {
	    throw new SuspiciousError("member no Genome: " + oops);
	} 
    } 

    /**
     * Get the i-th member in the order specified.
     * @param best whether to get the i-th best, or the i-th worst.
     */
    public Genome get(int i, boolean best) {
	return best ? get(i) : get(size() - 1 - i);
    } 

    // for statistics
	
    /**
     * Calculate an overall difference of all genomes in the population with several difference comparisons.
     */
    public double getOverallDistance() {
	return overallDistance((Genome[]) members.toArray(new Genome[0]));
    }

    /**
     * Returns an array with the fitness values of all genomes.
     * For purpose of statistics.
     */
    public double[] getFitnessArray() {
	double[] r = new double[size()];
	Iterator it = iterator();
	for (int i = 0; i < r.length; i++)
	    r[i] = ((Genome) it.next()).getFitness();
	return r;
    } 

    // list methods

    /**
     * Get the size of population i.e. the number of member genomes in it.
     */
    public int size() {
	return members.size();
    } 

    /**
     * Append a genome without replacing which will increase the size.
     * @see #merge(Genome)
     */
    public boolean add(Object o) {
	Genome g = (Genome) o;
	merge(g);
	g.setPopulation(this);
	return true;
    } 
    public boolean remove(Object o) {
	Genome g = (Genome) o;
	if (!members.remove(g))
	    return false;
	else {
	    g.setPopulation(null);
	    return true;
	}
    } 
    public Object remove(int index) {
	Genome g = (Genome) members.remove(index);
	g.setPopulation(null);
	return g;
    } 
    public Iterator iterator() {
	//@xxx the iterators remove method will not call this.remove(...) since we are no DelegateList
	// thus setPopulation(null) will not get called. But setPopulation currently does nothing
	// the same goes for the listIterator methods, and is also true for add(...).
	return members.iterator();
    } 
    public ListIterator listIterator() {
	return members.listIterator();
    } 
    public ListIterator listIterator(int index) {
	return members.listIterator(index);
    } 
    public List getMembers() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPermission(new RuntimePermission("accessDeclaredMembers"));
	} 
	return getMyMembers();
    } 
    List getMyMembers() {
	return members;
    }
    void setMyMembers(List newMembers) {
	this.members = newMembers;
    } 
    /**
     * Just for indexed BeanInfo until beans support template typed lists.
     */
    /*public Genome[] getMembersArray() {
      SecurityManager security = System.getSecurityManager();
      if (security != null) {
      security.checkPermission(new RuntimePermission("accessDeclaredMembers"));
      } 
      return (Genome[]) members.toArray(new Genome[0]);
      } */

    /**
     * Merges a genome into this population according to its fitness.
     * Will evaluate fitness if necessary, i.e, if it is <code>Double.NaN</code>.
     * @pre all current members already have a fitness that is not Double.NaN
     * @see #evaluate(boolean)
     * @see orbital.util.Setops#merge(List, List, Comparator)
     */
    private void merge(Genome n) {
	double fitness = n.getFitness();
	if (Double.isNaN(fitness)) {
	    n.evaluate(this, false);		 		//XXX: this.evaluate(false), instead?
	    fitness = n.getFitness();
	} 
	int i;
	if (Double.isNaN(fitness)) {
	    logger.log(Level.WARNING, "merging", "no fitness after evaluation");
	    i = 0;                  		//XXX: is this always a good idea, or only for creation?
	}
	else
	    for (i = 0; i < size(); i++)
		if (!(((Genome) members.get(i)).getFitness() > fitness))
		    break;
	members.add(i, n);
    } 

    // fitness evaluation and caching implementation

    /**
     * Evaluates the fitness of the Genomes, if necessary.
     * <p>
     * Default implementation will call evaluate for each member Genome.</p>
     * @param redo force whole evaluation again, even for cached fitness values.
     *  Should usually be <code>false</code> for efficiency reasons.
     */
    public void evaluate(boolean redo) {
	for (Iterator i = iterator(); i.hasNext(); ) {
	    Genome c = (Genome) i.next();
	    if (redo || Double.isNaN(c.getFitness()))
		c.evaluate(this, redo);
	}
	Collections.sort(members, Genome.comparator);
    } 

    /**
     * Update geneticAlgorithm when loading old version without geneticAlgorithm field.
     * @todo remove when all serialized data is updated
     */
    /*private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      in.defaultReadObject();
      if (geneticAlgorithm == null)
      geneticAlgorithm = GeneticAlgorithm.geneticAlgorithm;
      if (geneticAlgorithm == null)
      throw new java.io.InvalidObjectException("no genetic algorithm coule be deserialized in an old format version");
      }*/

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	StringWriter  buf = new StringWriter();
	PrintWriter   wr = new PrintWriter(buf);
	DecimalFormat form = (DecimalFormat) NumberFormat.getInstance();
	form.applyPattern("00 ");
	wr.println("Generation " + getGeneration() + ":");
	wr.println("No Fit\tGenome");
	double sumFitness = 0.0;
	for (int i = 0; i < size(); i++) {
	    Genome c = get(i);
	    sumFitness += c.getFitness();
	    wr.println(form.format(i) + MathUtilities.format(c.getFitness(), 2) + "\t" + c);
	} 
	wr.print("Best=" + get(0, true).getFitness() + ", Avg=" + sumFitness / size() + ", Worst=" + get(0, false).getFitness());
	wr.close();
	return buf.toString();
    } 
	
    // Utilities
	
    /**
     * Generate (<strong>create</strong>) a new random population of genomes.
     * <p>
     * Note that the prototype genome should not contain genes that are immune to mutation
     * since that is an essential part of creating an initial random population without
     * problem specific means.</p>
     * <p>
     * Also note that a mixed random and problem specific initial population might get
     * better results. So consider merging them.</p>
     * @param population the population to fill with Genomes.
     * @param size the initial size of the new population. i.e. the initial number of Genomes.
     * @param prototype the genome prototype to clone and mutate to create the population.
     *  The prototype must have the right structure to serve as a problem specific solution,
     *  but does not necessarily need to have meaningful values.
     * @return the population filled with random genomes according to the prototype.
     *  <b>Note:</b> this population will not yet have been {@link #evaluate(boolean) evaluated}.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Method</a>
     * @see Genome#mutate(double)
     * @see Genome#inverse()
     */
    public static Population create(Population population, Genome prototype, int size) {
	if (prototype == null)
	    throw new IllegalArgumentException("illegal prototype " + prototype);
	for (int i = 1; i <= size / 2; i++) {
	    Genome t;
	    // increasing mutation
	    //@TODO: think about not increasing mutation from [0,5] to [0,1] by multiplying with 2
	    population.members.add(t = (Genome) prototype.mutate(2 * 1.0 * (double) i / (double) size));
	    // complementary Genome
	    population.members.add((Genome) t.inverse());
	} 
	// odd population size
	if (population.size() < size)
	    population.members.add(prototype.mutate(1.0));
	return population;
    }

    /**
     * Generate (<strong>create</strong>) a new random population of genomes.
     * <p>
     * Note that the prototype genome should not contain genes that are immune to mutation
     * since that is an essential part of creating an initial random population without
     * problem specific means.</p>
     * <p>
     * Also note that a mixed random and problem specific initial population might get
     * better results. So consider merging them.</p>
     * @param size the initial size of the new population. i.e. the initial number of Genomes.
     * @param prototype the genome prototype to clone and mutate to create the population
     *  The prototype must have the right structure to serve as a problem specific solution,
     *  but does not necessarily need to have meaningful values.
     * @return a new random population with genomes according to the prototype.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Method</a>
     * @see Genome#mutate(double)
     * @see Genome#inverse()
     */
    public static Population create(Genome prototype, int size) {
	return create(new PopulationImpl(size), prototype, size);
    }

    /**
     * Calculate an overall difference of multiple genomes with several difference comparisons.
     * @todo rewrite pure functionally
     */
    static double overallDistance(Genome c[]) {
	Metric measure = c[0].distanceMeasure();
	Real difference = measure.distance(c[0], c[c.length - 1]);
	for (int i = 0; i < c.length - 1; i++)
	    difference = difference.add(measure.distance(c[i], c[i + 1]).norm());
	return difference.divide(Values.getDefaultInstance().valueOf(c.length)).doubleValue();
    } 
}
