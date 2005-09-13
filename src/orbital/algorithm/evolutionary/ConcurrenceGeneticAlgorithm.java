/**
 * @(#)ConcurrenceGeneticAlgorithm.java 0.9 2000/03/09 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import orbital.util.Pair;

import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import orbital.math.Evaluations;
import orbital.logic.functor.Function;
import orbital.util.Utility;

/**
 * This class is a genetic algorithm that weights its members in comparison to the others.
 * <p>
 * Evaluation will be called with {@link orbital.util.Pair}s to weight.
 * Either it returns a Pair of Numbers then, or it is called with both
 * elements again to get their associated weight.</p>
 * <p><em>Experimental</em></p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @invariants getPopulation() instanceof ComparingPopulation
 */
public class ConcurrenceGeneticAlgorithm extends IncrementalGeneticAlgorithm {
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = 6996937771442542793L;

    /**
     * Implemented as round robin comparison.
     * Round robin comparison: every genome is compared to every other genome.
     * O(n/2*(n-1))
     * @internal @see below
     */       
    public static final int ROUND_ROBIN = 1;
    public static final int HIERARCHY = 2;
    /**
     * Implemented as flat hierarchy comparison.
     * Any pairs compete, and then any pairs of winners compete, etc.
     * The loosers of one level all get the same scores.
     * O(n-1)
     */
    public static final int FLAT_HIERARCHY = 3;
    
    /**
     * The type of concurrence comparison used.
     * @serial
     * @see #FLAT_HIERARCHY
     * @see #HIERARCHY
     * @see #ROUND_ROBIN
     */
    private int concurrenceType;
    
    public ConcurrenceGeneticAlgorithm() {
    }
        
    public int getConcurrenceType() {
        return concurrenceType;
    }
    /**
     * Set the type of concurrence comparison used.
     * @see #FLAT_HIERARCHY
     * @see #HIERARCHY
     * @see #ROUND_ROBIN
     */
    public void setConcurrenceType(int type) {
        if (type < ROUND_ROBIN || FLAT_HIERARCHY < type)
            throw new IllegalArgumentException("illegal type " + type);
        concurrenceType = type;
        if (getPopulation() != null)
            setPopulation(getPopulation());
    }

    /*//TODO: public ConcurrenceGeneticAlgorithm(int initialLength, int size, double maxCrossover, double maxMutation) {
      super(new ComparingPopulation(initialLength, size), 2, 2, maxCrossover, maxMutation);
      }*/

    public void setPopulation(Population pop) {
        //TODO: instantly use ComparingPopulation instead of Population to avoid early calls to evaluate
        super.setPopulation(convertPopulation(pop));
        validateInvariant();
        //XXX: only necessary when recently created @see ComparingPopulation#create
        //already performed in super class getPopulation().evaluate(false);
        //validateInvariant();
    } 

    /**
     * Convert a population to a comparing population according to getConcurrenceType()
     * if necessary.
     */
    private Population convertPopulation(Population pop) {
        switch (getConcurrenceType()) {
        case FLAT_HIERARCHY:
            return pop instanceof FlatHierarchyComparingPopulation ? pop : new FlatHierarchyComparingPopulation(pop);
        case HIERARCHY:
            throw new UnsupportedOperationException("hierarchy type not yet implemented");
        case ROUND_ROBIN:
            return pop instanceof RoundRobinComparingPopulation ? pop : new RoundRobinComparingPopulation(pop);
        default:
            throw new IllegalStateException("illegal concurrence type set");
        }
    }
        
    /**
     * Returns the number of concurrence comparisons required for current population.
     * @return the number of concurrence comparisons required evolving
     *  each generation of the current population, depending upon the concurrence type.
     * @see #getConcurrenceType()
     */
    public int getConcurrenceComparisons() {
        if (getPopulation() == null)
            return Integer.MIN_VALUE;
        int n = getPopulation().size();
        switch (getConcurrenceType()) {
        case FLAT_HIERARCHY:
            return n - 1;
        case HIERARCHY:
            return (int) Math.ceil(n * (Math.log(n) / Math.log(2)) / 2);
        case ROUND_ROBIN:
            return (int) Math.ceil(n * (n - 1) / 2.);
        default:
            throw new IllegalStateException("illegal concurrence type set");
        }
    }

    public boolean isCorrect() {
        return false;
    }

    public void evolve() {
        validateInvariant();
        // insert anywhere
        super.evolve();
        // sort again
        getPopulation().evaluate(true);
        validateInvariant();
    } 
    
    public String toString() {
        String type;
        switch (getConcurrenceType()) {
        case FLAT_HIERARCHY:
            type = "FLAT_HIERARCHY";
            break;
        case HIERARCHY:
            type = "HIERARCHY";
            break;
        case ROUND_ROBIN:
            type = "ROUND_ROBIN";
            break;
        default:
            throw new IllegalStateException("illegal concurrence type set");
        }
        return super.toString() + type;
    }
    private final void validateInvariant() {
        assert getPopulation() instanceof ComparingPopulation : "invariant";
    }
}

/**
 * This class is a Population that weights its members in comparison to the others.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see ParallelEvaluationPopulation
 */
abstract class ComparingPopulation extends ParallelEvaluationPopulation {
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = -1212333692101864921L;

    /**
     * Whether we use parallel or sequential evaluation.
     */
    public static final boolean PARALLEL_MODE = "true".equals(System.getProperty(ConcurrenceGeneticAlgorithm.class.getName() + ".parallel", "true"));

    protected ComparingPopulation() {}

    /**
     * Convert a population into a comparing population using its members list reference.
     */
    protected ComparingPopulation(Population original) {
        setGeneration(original.getGeneration());
        setMyMembers(original.getMyMembers());
    }

    /**
     * adds at an arbitrary position since calculation is done later.
     * @see #evaluate(boolean)
     * @see ParallelEvaluationPopulation#add(Object)
     */
    public boolean add(Object o) {
        Genome g = (Genome) o;
        g.setPopulation(this);
        return getMyMembers().add(g);
    } 

    /**
     * Weight the list of chromosomes.
     * <p>
     * will call {@link GeneticAlgorithm#getEvaluation()}.{@link Function#apply(Object)}</code>
     * with a {@link Pair Pair&lt;Genome,Genome&gt;} to determine weight
     * and then with each chromosome to get weight.
     * @return the list of weights belonging to the corresponding chromosomes (in the same order).
     */
    protected double[] weight(Genome[] cs) {
        final Function evaluation = getGeneticAlgorithm().getEvaluation();
        Object   o = evaluation.apply(new Pair(cs[0], cs[1]));
        if (o instanceof Pair) {
            Pair p = (Pair) o;
            return new double[] {((Number) p.A).doubleValue(), ((Number) p.B).doubleValue()};
        } else {
            Number   w = (Number) o;
            double[] r = new double[2];
            for (int i = 0; i < r.length; i++)
                r[i] = ((Number) evaluation.apply(cs[i])).doubleValue();
            assert new Double(r[0]).equals(w) : "binary comparison evaluation returns the same for Pair(a,b) and a";
            return r;
        }
    } 

    /**
     * Check whether all members have a fitness value != Double.NaN.
     */
    protected boolean allUpToDate() {
        for (Iterator i = iterator(); i.hasNext(); )
            if (Double.isNaN(((Genome) i.next()).getFitness()))
                return false;
        return true;
    }
                        
    // comparing implementation

    /**
     * Implement comparison.
     */
    //@todo public abstract void evaluate(boolean redo);

    /**
     * comparing implementation helper thread.
     * (Concurrently) compares the given genomes and synchronizedly adds to genome fitnesses.
     */
    class Evaluator implements Runnable {
        Genome[]  genome;
        private boolean redo;
        /**
         * The weights calculated once finished with ComparingPopulation#weight(Genome[]).
         */
        double[] weights;
        public Evaluator(Genome[] genome, boolean redo) {
            this.genome = genome;
            this.redo = redo;
        }
                
        public void run() {
            this.weights = weight(genome);
            assert weights.length == 2 : "binary comparison";
            for (int i = 0; i < weights.length; i++)
                synchronized(genome[i]) {
                    genome[i].setFitness(genome[i].getFitness() + weights[i]);
                }
        }
    };
}

/**
 * Implemented as flat hierarchy comparison.
 * Any pairs compete, and then any pairs of winners compete, etc.
 * The loosers of one level all get the same scores.
 * O(n-1)
 */
class FlatHierarchyComparingPopulation extends ComparingPopulation {
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = -5374461378540201110L;

    public FlatHierarchyComparingPopulation() {}
    public FlatHierarchyComparingPopulation(Population original) {
        super(original);
    }

    // comparing implementation

    public void evaluate(boolean redo) {
        if (!redo && allUpToDate())
            return;
        else
            redo = true;
        // initialize to 0
        for (Iterator i = iterator(); i.hasNext(); ) {
            ((Genome) i.next()).setFitness(0);
        } 

        // compare one-by-one, drop the loser
        List comparing = new ArrayList(getMyMembers());
        while (comparing.size() >= 2) {
            UniqueShuffle p = new UniqueShuffle(comparing.size());
            p.reShuffle();

            // the out list of the genomes to be removed
            List out = new LinkedList();
            for (int g = 0; g < comparing.size() / 2; g++) {
                // compare <em>any</em> two
                Genome[] genome = {
                    (Genome) comparing.get(p.next()), (Genome) comparing.get(p.next())
                };
                if (PARALLEL_MODE) {
                    Runnable runnable = new Evaluator(genome, redo, out);
                    synchronized(this) {
                        new Thread(getGenerationEvaluators(), runnable).start();
                    }
                } else {
                    double[] weights = weight(genome);
                    assert weights.length == 2 : "binary comparison";
                    for (int i = 0; i < weights.length; i++)
                        genome[i].setFitness(genome[i].getFitness() + weights[i]);
                    if (weights[0] < weights[1])
                        out.add(genome[0]);
                    else if (weights[1] < weights[0])
                        out.add(genome[1]);
                    else {
                        assert weights[0] == weights[1] : "equal weights since no NaN will occur";
                        // the winner in a draw game is randomly choosen
                        out.add(Utility.flip(getGeneticAlgorithm().getRandom(), 0.5) ? genome[0] : genome[1]);
                    }
                }
            } 

            if (PARALLEL_MODE)
                waitForEvaluators();
            comparing.removeAll(out);
        } 

        Collections.sort(getMyMembers(), Genome.comparator);
    } 

        
    /**
     * comparing implementation helper thread that updates the out list, automatically.
     */
    class Evaluator extends ComparingPopulation.Evaluator {
        /** the out list of the genomes to be removed */
        private List out;
        public Evaluator(Genome[] genome, boolean redo, List out) {
            super(genome, redo);
            this.out = out;
        }
                
        public void run() {
            super.run();
            synchronized(out) {
                if (weights[0] < weights[1])
                    out.add(genome[0]);
                else if (weights[1] < weights[0])
                    out.add(genome[1]);
                else {
                    assert weights[0] == weights[1] : "equal weights since no NaN will occur";
                    // the winner in a draw game is randomly choosen
                    out.add(Utility.flip(getGeneticAlgorithm().getRandom(), 0.5) ? genome[0] : genome[1]);
                }
            }
        }
    };
}

/**
 * Implemented as hierarchy comparison.
 * je zwei werden verglichen, je zwei Gewinner und je zwei Verlierer werden
 * wieder miteinander verglichen.
 * O(n/2 * &#13266;<sub>2</sub>(n))
 * @todo introduce
 */
 
/**
 * Implemented as round robin comparison.
 * Round robin comparison: every genome is compared to every other genome.
 * O(n/2*(n-1))
 * Insert: compare new one with everyone, and (if population size changes) all old genomes update their fitness
 * (aber wie ohne, dass einige ad infinitum abhauen?)
 * O(n-1)
 * @todo thread each evaluation
 * @todo could introduce add(Object) to do Einfügen in O(n) instead of evaluate(true)?
 */
class RoundRobinComparingPopulation extends ComparingPopulation {
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = -3527901944099709659L;
        
    public RoundRobinComparingPopulation() {}
    public RoundRobinComparingPopulation(Population original) {
        super(original);
    }
    
    // comparing implementation

    public /*synchronized*/ void evaluate(boolean redo) {
        if (!redo && allUpToDate())
            return;
        else
            redo = true;
        // initialize to 0
        for (Iterator i = iterator(); i.hasNext(); ) {
            ((Genome) i.next()).setFitness(0);
        } 

        // compare each with all subsequent ones
        for (ListIterator j = listIterator(); j.hasNext(); ) {
            Genome g1 = (Genome) j.next();
            for (ListIterator k = listIterator(j.nextIndex()); k.hasNext(); ) {
                // compare any distinct two
                Genome[] genome = {
                    g1, (Genome) k.next()
                };
                if (PARALLEL_MODE) {
                    Runnable runnable = new Evaluator(genome, redo);
                    synchronized(this) {
                        new Thread(getGenerationEvaluators(), runnable).start();
                    }
                } else {
                    double[] weights = weight(genome);
                    assert weights.length == 2 : "binary comparison";
                    for (int i = 0; i < weights.length; i++)
                        genome[i].setFitness(genome[i].getFitness() + weights[i]);
                }
            } 
        } 

        if (PARALLEL_MODE)
            waitForEvaluators();
        Collections.sort(getMyMembers(), Genome.comparator);
    } 
}
