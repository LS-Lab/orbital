/**
 * @(#)ParellelEvaluationPopulation.java 0.7 2001/03/30 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import java.io.Serializable;

import java.util.Collections;
import java.util.Iterator;

import java.io.IOException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A Population that evaluates its members fitness-weights in parallel.
 * <p>
 * When using the ParallelEvaluationPopulation <strong><em>always</em></strong> make sure that you
 * call {@link ParallelEvaluationPopulation#evaluate(boolean)} after having added some Genomes,
 * and prior to relying on it being sorted.
 * Especially call evaluate after creation or evolving (with {@link GeneticAlgorithm#evolve()}).</p>
 * <p>
 * Better not try calling add() again while evaluate() is still running, although it should work.</p>
 * 
 * @version 0.7, 2001/03/30
 * @author  Andr&eacute; Platzer
 * @invariant evaluators != null is final-like (either due to constructor call, or after a call to readObject).
 * @todo test
 * @see #ParallelEvaluationPopulation(Population)
 */
public class ParallelEvaluationPopulation extends Population implements Serializable {
    private static final Logger logger = Logger.getLogger(ParallelEvaluationPopulation.class.getName());
    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = -2229283476470476347L;

    /**
     * The master thread group for all evaluation threads started
     * to evaluate some fitness values of the members.
     * @see #generationEvaluators
     * @internal see #readObject(java.io.ObjectOutputStream)
     * @todo evaluators should have a good uncaughtException(Thread t, Throwable e) implementation
     */
    private transient /*final*/ ThreadGroup evaluators = new EvaluatorsGroup(this);
    /**
     * The current thread group for all evaluation threads started
     * during this generation to evaluate some fitness values of the members.
     * Exchanged whenever {@link #waitForEvaluators()} or {@link #exchangeEvaluators()} is called.
     * @see #evaluators
     * @see #exchangeEvaluators()
     * @see #waitForEvaluators()
     * @todo protectedize accessor method?
     */
    private transient volatile ThreadGroup generationEvaluators;

    private static class EvaluatorsGroup extends ThreadGroup {
	private ParallelEvaluationPopulation population;
	public EvaluatorsGroup(ParallelEvaluationPopulation population) {
	    super(population.getClass().getName());
	    this.population = population;
	}
		
	/**
	 * Stops the whole evaluation since it is undefined, now.
	 */
	public void uncaughtException(Thread t, Throwable e) {
	    if (!(e instanceof ThreadDeath)) {
		logger.log(Level.WARNING, "uncaught exception in " + t, e);
		population.evaluators.stop();
	    }
	}
    }

    public ParallelEvaluationPopulation() {
        generationEvaluators = new ThreadGroup(evaluators, evaluators.getName()+ '-' + getGeneration());
    }

    /**
     * Convert a population into a parallel evaluating population using its members list per reference.
     * To parallelize the evaluation in create as well consider using {@link Population#create(Population, Genome, int)} instead.
     * @see Population#create(Population, Genome, int)
     */
    public ParallelEvaluationPopulation(Population original) {
	this();
	this.setGeneration(original.getGeneration());
	this.setMyMembers(original.getMyMembers());
    }

    /**
     * Get the master thread group for all evaluation threads started
     * to evaluate some fitness values of the members.
     * @see #getGenerationEvaluators()
     */
    protected final ThreadGroup getEvaluators() {
	return evaluators;
    }
	
    /**
     * Get the current thread group for all evaluation threads started
     * during this generation to evaluate some fitness values of the members.
     * Exchanged whenever {@link #waitForEvaluators()} or {@link #exchangeEvaluators()} is called.
     * @see #getEvaluators()
     * @see #exchangeEvaluators()
     * @see #waitForEvaluators()
     */
    protected final ThreadGroup getGenerationEvaluators() {
	return generationEvaluators;
    }


    /**
     * Adds at an arbitrary position since fitness evaluation is done later.
     * Threads fitness calculation.
     * @see #evaluate(boolean)
     * @internal #evaluate(Object, boolean)
     */
    public boolean add(Object o) {
	Genome g = (Genome) o;
	if (!getMyMembers().add(g))
	    return false;
	else {
	    evaluate(g, false);
	    g.setPopulation(this);
	    return true;
	}
    } 

    /**
     * Evaluate and resort population waiting for all current evaluator threads to stop.
     * @see #add(Object)
     * @internal #waitForEvaluators()
     * @param redo force whole evaluation again, even for cached fitness values.
     *  If <code>true</code> we will {@link Thread#stop()}(!) all running evaluator threads.
     */
    public void evaluate(boolean redo) {
	if (redo) {
	    // stop generationEvaluators and restart all calculation
	    ThreadGroup oldSnapshot = exchangeEvaluators();
	    oldSnapshot.stop();
	    // oldSnapshot is no daemon group so destroy explicitly
	    oldSnapshot.destroy();
	    //@todo also stop old evaluators, already exchanged by evaluate(boolean), but still running?
	    for (Iterator i = iterator(); i.hasNext(); )
		evaluate((Genome) i.next(), redo);
	}
	waitForEvaluators();
	Collections.sort(getMyMembers(), Genome.comparator);
    } 
    

    // concurrent implementation methods
    
    /**
     * Concurrently evaluate a genome.
     * When called threads fitness calculation.
     * @see #waitForEvaluators()
     */
    protected void evaluate(Genome g, boolean redo) {
	Runnable runnable = new Evaluator(g, redo);
	Thread evaluator;
	//@todo we don't actually need to synchronize two threads from adding but just
	// one thread from adding and another one from exchaning evaluators
	// in fact, we do not need to synchronize here at all, since the evaluator assignement is
	// atomical
	/*synchronized(this)*/ {
	    evaluator = new Thread(generationEvaluators, runnable);
	}
	evaluator.setDaemon(true);
	evaluator.start();
    }
    /**
     * Evaluates the fitness of the given Genome.
     * @see ParallelEvaluationPopulation#add(Object)
     */
    private class Evaluator implements Runnable {
	/**
	 * The genome to evaluate
	 * @serial
	 */
	private Genome  genome;
	/**
	 * argument to pass to genome.evaluate(boolean)
	 * @serial
	 */
	private boolean redo;
	public Evaluator(Genome genome, boolean redo) {
	    this.genome = genome;
	    this.redo = redo;
	}
	/*concurrent*/
	public void run() {
	    genome.evaluate(ParallelEvaluationPopulation.this, redo);
	}
    };

    /**
     * Waits for all current evaluator threads to stop.
     * @internal #exchangeEvaluators()
     */
    protected void waitForEvaluators() {
	ThreadGroup running = exchangeEvaluators();
        // loop waiting for all generationEvaluators to stop
        while (running.activeCount() > 0)
	    try {
            	Thread t[] = new Thread[1];
            	if (running.enumerate(t) == 0)
		    break;
            	t[0].join();
            }
            catch (InterruptedException irq) {
		logger.log(Level.WARNING, "had wait interrupted", irq);
            	Thread.currentThread().interrupt();
	    }
       	// running is no daemon group so destroy explicitly (otherwise it would destroy itself meanwhile)
       	running.destroy();
    }

    /**
     * Exchanges evaluators for this generation.
     * @see #generationEvaluators
     */
    protected final ThreadGroup exchangeEvaluators() {
	ThreadGroup groupSnapshot;
	// synchronize such that
	//  1) no other thread exchanges the generationEvaluators, now
	//  2) no other thread calls add using generationEvaluators, now (not really necessary)
	synchronized(this) {
	    // exchange generationEvaluators. We will not wait for evaluations from future calls to add() any more
	    groupSnapshot = generationEvaluators;
	    generationEvaluators = new ThreadGroup(evaluators, evaluators.getName()+ '-' + getGeneration());
    	}
    	return groupSnapshot;
    }

    /**
     * @serialData serialization should only occur once all <em>evaluation has finished</em>
     *  because neither evaluators, nor generationEvaluators and its threads are serializable.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
	s.defaultWriteObject();
    }

    /**
     * @serialData serialization should only occur once all <em>evaluation has finished</em>
     *  because neither evaluators, nor generationEvaluators and its threads are serializable.
     *  Exchanges evaluators.
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
	// synchronize such that
	//  1) no other thread uses evaluators, now
	synchronized(this) {
	    evaluators = new EvaluatorsGroup(this);
	}
        exchangeEvaluators();
	s.defaultReadObject();
    }
}
