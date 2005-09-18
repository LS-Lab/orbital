/**
 * @(#)GeneticAlgorithm.java 1.0 1998/08/01 Andre Platzer
 * 
 * Copyright (c) 1995-1998 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import orbital.algorithm.template.ProbabilisticAlgorithm;
import orbital.algorithm.template.AlgorithmicTemplate;
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
import orbital.algorithm.evolutionary.GeneticAlgorithmProblem;
import orbital.algorithm.evolutionary.Population;
import orbital.logic.functor.Function;

/**
 * A base class for genetic algorithms.
 * Genetic algorithms can be used as an evolutionary search algorithm
 * exploring (with a large population) or exploiting (with a small population)
 * an almost arbitrary search space.
 * <p>
 * A genetic algorithm provides the following operators:
 * <ul>
 *   <li><strong>evolve</strong> that will simulate the evolutionary search. (Decoupled into sub class)</li>
 *   <li><strong>select</strong> one of the Genomes. (Decoupled into selection function.)</li>
 * </ul>
 * </p>
 * <p>
 * After implementing the {@link GeneticAlgorithmProblem} interface use the
 * evolutionary genetic algorithm like:
 * <pre>
 * <span class="Orbital">Configuration</span> config <span class="assignment">=</span>
 *     <span class="keyword">new</span> <span class="Orbital">GeneticAlgorithm</span>.<span class="Orbital">Configuration</span>(<var>geneticAlgorithmProblem</var>,
 *         <span class="Orbital">Selectors</span>.rouletteWheel(),
 *         maximumRecombination,
 *         maximumMutation,
 *         IncrementalGeneticAlgorithm.class);
 * <span class="Class">Population</span> solution <span class="assignment">=</span> (<span class="Class">Population</span>) config.solve();
 * </pre>
 * Or, if you need any additional control of the single steps, use something like:
 * <pre>
 * ga = <span class="keyword">new</span> <span class="Orbital">IncrementalGeneticAlgorithm</span>();
 * ga.setEvaluation(<var>fitnessEvaluation</var>);
 * ga.setSelection(<span class="Orbital">Selectors</span>.rouletteWheel());
 * ga.setPopulation(<var>initialPopulation</var>);
 * <span class="Orbital">PopulationImpl</span> pop = (<span class="Orbital">PopulationImpl</span>) ga.getPopulation();
 * pop.setParentCount(<span class="Number">2</span>);
 * pop.setChildrenCount(<span class="Number">2</span>);
 * pop.setMaximumRecombination(maximumRecombination);
 * pop.setMaximumMutation(maximumMutation);
 * <span class="comment">// evolve until stop condition</span>
 * <span class="keyword">while</span> (<span class="operator">!</span><var>isSolution</var>()) {
 *     ga.evolve();
 *     <span class="Class">System</span>.out.println(ga.getPopulation());
 * }
 * </pre>
 * With a problem-specific implementation of the stop condition <code>isSolution()</code>
 * which could simply consider the number-of-generations, goodness-of-best-solution or convergence-of-population.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @invariants sub classes must support nullary constructor (for cloning)
 * @structure delegate population:Population
 * @structure delegate selection:Function
 * @see GeneticAlgorithmProblem
 * @see orbital.algorithm.template.HillClimbing
 * @see java.util.Random
 * @see "Goldberg, D. E. Genetic Algorithms in Search, Optimization and Machine Learning. .1989."
 * @see "Friedberg, R.M. A learning machine: Part I. IBM Journal, 2:2-13, 1958."
 * @see "Holland, J. H. Adaption in Natural and Artificial Systems. University of Michigan Press. 1975."
 * @todo improve algorithmic template implementation removing setXYZ things and constructor arguments?
 * @todo introduce sub classes ParallelGeneticAlgorithm (resembling ConcurrenceGeneticAlgorithm with parallel evaluation, but without Pair evaluation)
 *  DemeGeneticAlgorithm, ParallelDemeGeneticAlgorithm (with parallel population processed in parallel)
 * @todo introduce getConvergence() and getPopulationConvergence()
 */
public abstract class GeneticAlgorithm implements ProbabilisticAlgorithm, AlgorithmicTemplate/*<GeneticAlgorithmProblem,Population>*/, Serializable {
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
     * The selection scheme to apply while evolving.
     * @serial
     */
    private Function/*<Population,Genome>*/ selection = null;

    /**
     * The Population for this GeneticAlgorithm.
     * @serial
     */
    private Population    population = null;

    /**
     * Specifies the algorithm for evaluation of a Genome's fitness.
     * It must be set before use and is concrete problem-specific.
     * @serial
     */
    private Function/*<Genome, Number>*/ fitnessEvaluation = null;

    /**
     * Construct a new GeneticAlgorithm.
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

        selection = (Function) fields.get("selection", null);
        population = (Population) fields.get("population", null);
        fitnessEvaluation = (Function) fields.get("fitnessEvaluation", null);
        random = (Random) fields.get("random", null);

        // read the alternate old persistent fields
        if (fields.defaulted("fitnessEvaluation"))
            fitnessEvaluation = (Function) fields.get("fitnessWeighting", null);

        // transfer old persistent fields to population
        PopulationImpl pop = (PopulationImpl) population;
        if (!fields.defaulted("parentCount"))
            pop.setParentCount(fields.get("parentCount", Integer.MIN_VALUE));
        if (!fields.defaulted("childrenCount"))
            pop.setChildrenCount(fields.get("childrenCount", Integer.MIN_VALUE));
        if (!fields.defaulted("maximumRecombination"))
            pop.setMaximumRecombination(fields.get("maximumRecombination", Double.NaN));
        if (!fields.defaulted("maximumCrossover")
            && fields.defaulted("maximumRecombination"))
            // read the alternate old persistent fields
            // also read maximumCrossover, if no maximumRecombination was available
            pop.setMaximumRecombination(fields.get("maximumCrossover", Double.NaN));
        if (!fields.defaulted("maximumMutation"))
            pop.setMaximumMutation(fields.get("maximumMutation", Double.NaN));
    }

    /**
     * Returns a deep copy of this exact type of genetic algorithm.
     */
    public Object clone() {
        GeneticAlgorithm c;
        try {
            c = (GeneticAlgorithm) getClass().newInstance();
        }
        catch (InstantiationException e) {throw new InnerCheckedException("invariant: sub classes of " + GeneticAlgorithm.class + " must support nullary constructor for cloning.", e);}
        catch (IllegalAccessException e) {throw new InnerCheckedException("invariant: sub classes of " + GeneticAlgorithm.class + " must support nullary constructor for cloning.", e);}
        c.setPopulation((Population) population.clone());
        //@todo clone is protected!! for c.setSelection((Function) selection.clone());
        return c;
    } 

    public boolean equals(Object o) {
        if (o != null && getClass() == o.getClass()) {
            GeneticAlgorithm b = (GeneticAlgorithm) o;
            // b's selection is allowed to have b assigned, so compare classes, only
            return population.equals(b.population) && selection.getClass() == b.selection.getClass();
        } 
        return false;
    } 
        
    public int hashCode() {
        throw new InternalError("not yet implemented");
    }

    public String toString() {
        return getClass().getName();
    } 

    // get/set methods
        
    /**
     * Get the population growth factor.
     * @return the factor by which the population size increases with each generation (or decreases if &lt; 1).
     */
    public abstract double getPopulationGrowth();
        
    /**
     * Get the selection scheme to apply while evolving.
     */
    public Function/*<Population,Genome>*/ getSelection() {
        return selection;
    } 
    /**
     * Set the selection scheme to apply while evolving.
     * @param selector the selection function Population&rarr;Genome for selecting parents.
     * @see Selectors
     */
    public void setSelection(Function/*<Population,Genome>*/ selector) {
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
        if (!(pop instanceof PopulationImpl))
            //@xxx
            throw new IllegalArgumentException("use an instance of " + PopulationImpl.class + " having all its properties set.");
        if (this.population != null)
            this.population.setGeneticAlgorithm(null);
        this.population = pop;
        this.population.setGeneticAlgorithm(this);
        this.population.evaluate(false);
    } 
    /**
     * Get the evaluation function.
     * @return the evaluation function that specifies the algorithm for evaluation of a Genome's fitness.
     *  It is concrete problem-specific.
     */
    public Function/*<Genome, Number>*/ getEvaluation() {
        return fitnessEvaluation;
    } 
    /**
     * Set the evaluation function.
     * @param fitnessEvaluation the evaluation function that specifies the algorithm for evaluation of a Genome's fitness.
     *  It must be set before use and is concrete problem-specific.
     */
    public void setEvaluation(Function/*<Genome, Number>*/ fitnessEvaluation) {
        this.fitnessEvaluation = fitnessEvaluation;
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
        return (orbital.math.functional.Function) Operations.power.apply(Values.getDefaultInstance().valueOf(getPopulationGrowth()), orbital.math.functional.Functions.id);
    }



    // problem solving methods
    /**
     * Solve a genetic algorithm problem.
     * Assuming that the {@link #setSelection(Function) selector} has already been set.
     * @preconditions getSelection() != null
     * @return the population with the solution that was accepted by {@link GeneticAlgorithmProblem#isSolution(Population) isSolution}.
     * @see #setEvaluation(Function)
     * @see GeneticAlgorithmProblem#getEvaluation()
     * @see #setPopulation(Population)
     * @see GeneticAlgorithmProblem#getPopulation()
     * @see #evolve()
     */
    public Object/*>Population<*/ solve(AlgorithmicProblem/*>GeneticAlgorithmProblem<*/ gproblem) {
	GeneticAlgorithmProblem problem = (GeneticAlgorithmProblem) gproblem;
        if (getSelection() == null)
            throw new IllegalStateException("no selection function is set");
        this.setEvaluation(problem.getEvaluation());
        this.setPopulation(problem.getPopulation());
        logger.log(Level.FINER, "created", this + System.getProperty("line.separator") + getPopulation());
        // while stop condition is not true
        while (!problem.isSolution(getPopulation())) {
            evolve();
            logger.log(Level.FINER, "evolve", getPopulation());
        } 
        return getPopulation();
    } 


    // central virtual methods

    /**
     * <strong>evolves</strong> to the next generation for this population.
     * Parents are selected and will recombine and mutate to produce children genomes
     * who will replace some genomes in this population.
     * This operation is sometimes called breeding.
     * @see #selection
     * @see Genome#recombine(Gene[],int,double)
     * @see PopulationImpl#getMaximumRecombination()
     * @see PopulationImpl#getMaximumMutation()
     */
    public abstract void evolve();

    /**
     * Algorithmic configuration objects for genetic algorithms.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @todo what about SteadyStateGeneticAlgorithm.setNumberOfReplacements()
     */
    public static class Configuration extends AlgorithmicTemplate.Configuration/*<GeneticAlgorithmProblem,Population>*/ {
        private static final long serialVersionUID = 5516965797776057474L;
        
        /**
         * The selection scheme to apply while evolving.
         * @serial
         */
        private Function/*<Population,Genome>*/ selection;
        /**
         * The number of abstract parents required to produce children.
         * @serial
         */
        private int                       parentCount = Integer.MIN_VALUE;

        /**
         * The number of children produced with one reproduction involving parentCount parents.
         * @serial
         */
        private int                       childrenCount = Integer.MIN_VALUE;

        /**
         * Maximum probability rating of recombining parental genomes per production.
         * @serial
         */
        private double            maximumRecombination = Double.NaN;

        /**
         * Maximum probability rating of mutation level for reproduction.
         * @serial
         */
        private double            maximumMutation = Double.NaN;
        /**
         * Construct a new configuration.
         * @param problem the problem to solve.
         * @param selector the selection function Population&rarr;Genome for selecting parents.
         * @param parentCount The number of abstract parents required to produce children.
         * @param childrenCount The number of children produced with one reproduction involving parentCount parents.
         * @param maximumRecombination Maximum recombination rating.
         *  Maximum probability rating of recombining parental genomes per production.
         * @param maximumMutation Maximum mutation rating.
         *  Maximum probability rating of mutation level for reproduction.
         * @param algorithm the class of the AlgorithmicTemplate to instantiate for solving the problem.
         * @see Selectors
         */
        public Configuration(GeneticAlgorithmProblem problem, Function selector, int parentCount, int childrenCount, double maximumRecombination, double maximumMutation, Class algorithm) {
            this(problem, selector, algorithm);
            this.parentCount = parentCount;
            this.childrenCount = childrenCount;
            setMaximumRecombination(maximumRecombination);
            setMaximumMutation(maximumMutation);
        }
        public Configuration(GeneticAlgorithmProblem problem, Function selector, double maximumRecombination, double maximumMutation, Class algorithm) {
            this(problem, selector, 2, 2, maximumRecombination, maximumMutation, algorithm);
        }
        public Configuration(GeneticAlgorithmProblem problem, Function selector, Class algorithm) {
            super(problem, algorithm, GeneticAlgorithm.class);
            this.selection = selector;
        }

        /**
         * Get the selection scheme to apply while evolving.
         * @see Selectors
         */
        // roughly corresponds to a (heuristic) evaluation function (that already minimizing choices)
        public Function/*<Population,Genome>*/ getSelection() {
            return selection;
        }
        // TODO: perhaps: setSelection(Selection) as well?

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


        /**
         * Get GeneticAlgorithm constructor data as well?
         */
        //@todo introduce getMaximumMutation() or even getParentCount()?
        // TODO: s.a. is no good idea, or what?

        /**
         * Get a modified view of the problem.
         * Modifies the problem by letting its {@link GeneticAlgorithmProblem#getPopulation()}
         * return a PopulationImpl with all properties set according to this configuration.
         */
        public AlgorithmicProblem/*>GeneticAlgorithmProblem<*/ getProblem() {
            final GeneticAlgorithmProblem p = (GeneticAlgorithmProblem) super.getProblem();
            return new /*refine/delegate*/ GeneticAlgorithmProblem() {
                    // Code for delegation of orbital.algorithm.evolutionary.GeneticAlgorithmProblem methods to p

                    /**
                     *
                     * @return <description>
                     * @see orbital.algorithm.evolutionary.GeneticAlgorithmProblem#getEvaluation()
                     */
                    public Function getEvaluation()
                    {
                        return p.getEvaluation();
                    }

                    /**
                     *
                     * @return <description>
                     * @see orbital.algorithm.evolutionary.GeneticAlgorithmProblem#getPopulation()
                     * @see Population#clone()
                     * @see PopulationImpl#clone()
                     */
                    public Population getPopulation()
                    {
                        Population pop = p.getPopulation();
                        PopulationImpl c;
                        if (pop instanceof PopulationImpl)
                            c = (PopulationImpl) pop;
                        else {
                            c = new PopulationImpl(pop.size());
                            c.setGeneration(pop.getGeneration());
                            List members = c.getMyMembers();
                            //@internal direct copy to members without merge is stable and will not reorder equal fitnesses
                            for (Iterator i = pop.iterator(); i.hasNext(); )
                                members.add((Genome) i.next());
                        }
                        c.setParentCount(getParentCount());
                        c.setChildrenCount(getChildrenCount());
                        c.setMaximumRecombination(getMaximumRecombination());
                        c.setMaximumMutation(getMaximumMutation());
                        return c;
                    }

                    /**
                     *
                     * @param param1 <description>
                     * @return <description>
                     * @see orbital.algorithm.evolutionary.GeneticAlgorithmProblem#isSolution(Population)
                     */
                    public boolean isSolution(Population param1)
                    {
                        return p.isSolution(param1);
                    }

                };
        }
        
        public AlgorithmicTemplate getAlgorithm() {
            GeneticAlgorithm algo = (GeneticAlgorithm) super.getAlgorithm();
            algo.setSelection(getSelection());
            return algo;
        }

        /**
         * @posconditions RES instanceof Population
         */
        public Object/*>Population<*/ solve() {
            return super.solve();
        }
    }
}
