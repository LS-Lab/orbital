/**
 * @(#)Selectors.java 1.0 2000/03/29 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import orbital.logic.functor.Function;
import java.io.Serializable;

import java.util.Random;
import orbital.util.Utility;

/**
 * Selection schemes for evolutionary genetic algorithms.
 * Selectors <i>select</i> a parent from the genomes.
 * 
 * @stereotype &laquo;Module&raquo;
 * @version 1.0, 2000/11/01
 * @author  Andr&eacute; Platzer
 * @see orbital.util.Setops
 * @see orbital.util.Callback
 * @see GeneticAlgorithm#setSelection(Function)
 */
public class Selectors {
    /**
     * module class - prevent instantiation.
     */
    private Selectors() {}
	
    /**
     * Better genomes will more likely be selected.
     * Weighted roulette wheel selector.
     * @pre requires either strictly negative or strictly positive fitness values.
     * @see #likelyBetter()
     */
    public static final Function/*<Chromosome, Population>*/ rouletteWheel() {
	return new RouletteWheelSelector();
    }
    private static class RouletteWheelSelector extends SelectionImpl {
	private static final long serialVersionUID = -8558724347382003603L;
	/**
	 * partial sums.
	 * partialSum[k] = &sum;<span class="doubleIndex"><sub>i=0</sub><sup>k</sup></span> f<sub>i</sub>
	 * with the fitness weights f<sub>0</sub>,...,f<sub>n-1</sub>.
	 */
	private transient double partialSum[];
        /**
         * weighted roulette wheel.  Likliehood of selection is proportionate to the fitness.
	 * @pre p is sorted
	 */
	public Object apply(Object p) {
            Population population = (Population) p;
            //@todo would not need calling every time, but how to achieve that we get notified when population changed?
            update(population);
            return selectImpl(population);
        }
        /**
         * weighted roulette wheel.  Likliehood of selection is proportionate to the fitness.
         * Binary search method (using cached partial sums).
	 * @see "Goldberg, D. E. Genetic Algorithms in Search, Optimization and Machine Learning. 1989."
	 * @pre p is sorted && partialSum updated
	 */
        private Object selectImpl(Population population) {
            double cutoff = population.getGeneticAlgorithm().getRandom().nextDouble();
            int lower = 0, upper = population.size() - 1;
            while (lower <= upper){
                int i = lower + (upper - lower) / 2;
                if (partialSum[i] > cutoff)
		    upper = i - 1;
                else
		    lower = i + 1;
            }
            if (lower >= population.size())
            	lower = population.size() - 1;
            return population.get(lower);
        }
        private void update(Population population) {
	    partialSum = new double[population.size()];
            double min = population.get(0, false).getFitness();
            double max = population.get(0, true).getFitness();
            if (max == min)
                for(int i = 0; i < partialSum.length; i++)
		    // equal likelihoods
		    partialSum[i] = (double) (i+1) / (double) partialSum.length;
            else if ((max > 0 && min >= 0) || (max <= 0 && min < 0)) {
            	partialSum[0] = population.get(0).getFitness();
            	for (int i=1; i < partialSum.length; i++)
		    partialSum[i] = population.get(i).getFitness() + partialSum[i-1];
            	for (int i = 0; i < partialSum.length; i++)
		    partialSum[i] /= partialSum[partialSum.length - 1];
            }
            else
            	throw new IllegalArgumentException("rouletteWheel selection requires either strictly negative or strictly positive fitness values");
        }
	public String toString() {return "roulette wheel selector";}
    }

    /**
     * Better genomes will more likely be selected.
     * Unconstrained but less accurate version of roulette wheel.
     * Uses a Las Vegas loop.
     * @see #rouletteWheel()
     * @todo which implementation is this?
     */
    public static final Function/*<Chromosome, Population>*/ likelyBetter() {
	return new LikelyBetterSelector();
    }
    private static class LikelyBetterSelector extends SelectionImpl {
	private static final long serialVersionUID = -5845373429801808253L;
	public Object apply(Object p) {
            Population population = (Population) p;
            Random	   random = population.getGeneticAlgorithm().getRandom();
	    double	   selection;
	    do {
		selection = random.nextDouble();
	    } while (Utility.flip(random, selection));
	    return population.get((int) (selection * population.size()));
	} 
	public String toString() {return "likely better selector";}
    } 

    /**
     * Pick the better one of two genomes selected with roulette wheel.
     * Tournament selector returns slightly better genomes than roulette wheel.
     */
    public static final Function tournament() {
	return new TournamentSelector();
    }
    private static class TournamentSelector extends SelectionImpl {
	private static final long serialVersionUID = -3254563371130485851L;
	/**
	 * The roulette wheel selector used twice per tournament selection.
	 * @serial
	 */
	private final RouletteWheelSelector roulette = new RouletteWheelSelector();
	public Object apply(Object p) {
	    Population population = (Population) p;
	    roulette.update(population);
	    Genome a = (Genome) roulette.selectImpl(population);
	    Genome b = (Genome) roulette.selectImpl(population);
	    return b.getFitness() > a.getFitness() ? b : a;
	} 
	public String toString() {return "tournament selector";}
    } 

    /**
     * One of the best genomes will be selected, randomly.
     * Rank selector.
     */
    public static final Function rank() {
	return new RankSelector();
    }
    private static class RankSelector extends SelectionImpl {
	private static final long serialVersionUID = 330621762438324420L;
	/**
	 * true for best selection, false for worst selection.
	 * @serial
	 */
	private boolean			  whom = true;
	public Object apply(Object p) {
            Population population = (Population) p;
	    int	   count = 1;
	    double most = population.get(0, whom).getFitness();
	    for (int i = 1; i < population.size() && population.get(i, whom).getFitness() == most; i++)
		count++;
	    return population.get(population.getGeneticAlgorithm().getRandom().nextInt(count), whom);
	} 
	public String toString() {return "rank selector";}
    } 

    /**
     * All genomes will be selected with uniform probability 1/n.
     * Uniform selector.
     */
    public static final Function uniform() {
	return new UniformSelector();
    }
    private static class UniformSelector extends SelectionImpl {
	private static final long serialVersionUID = -4385650629055265254L;
	public Object apply(Object p) {
            Population population = (Population) p;
	    return population.get(population.getGeneticAlgorithm().getRandom().nextInt(population.size()));
	} 
	public String toString() {return "uniform selector";}
    } 

    /**
     * Stochastic remainder selector
     */

    /**
     * Deterministic sampling selector
     */
}


/**
 * Selection implementation for selections schemes that do not require preprocessing.
 */
abstract class SelectionImpl implements Function/*<Chromosome, Population>*/, Serializable {
    private static final long serialVersionUID = -3217305129591017547L;
    /**
     * Generic instantiation clone
     */
    public Object clone() throws CloneNotSupportedException {
	try {
	    return (Function) getClass().newInstance();
	} catch (InstantiationException x) {
	    throw new CloneNotSupportedException("instantiate: " + getClass().getName());
	} catch (IllegalAccessException x) {
	    throw new CloneNotSupportedException("illegally accessed: " + getClass().getName());
	} 
    } 

    public boolean equals(Object o) {
	return o != null && getClass() == o.getClass();
    }
	
    public int hashCode() {
	return getClass().hashCode();
    }
}
