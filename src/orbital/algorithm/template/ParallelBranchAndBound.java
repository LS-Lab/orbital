/**
 * @(#)ParallelBranchAndBound.java 1.0 2000/09/22 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;
import orbital.algorithm.template.GeneralSearchProblem.Option;

import orbital.logic.functor.Function;
import java.util.Iterator;
import java.util.Collection;

import java.util.Collections;
import orbital.math.functional.Operations;
import orbital.math.functional.Functions;
import orbital.math.Values;

/**
 * Parallel branch-and-bound algorithm.
 * <p>
 * Visits the next nodes parallely and bounds ot the best solution known.
 * Although the implementation uses a stack of nodes, due to the parallel nature
 * this algorithm behaves much like a {@link BreadthFirstSearch}.
 * However, the scheduling policy of the thread scheduler may introduce a probabilistic
 * behaviour in execution speed.</p>
 *
 * @version 1.0, 2000/09/22
 * @author  Andr&eacute; Platzer
 * @see BreadthFirstSearch
 */
public class ParallelBranchAndBound extends BranchAndBound {
    public ParallelBranchAndBound(Function heuristic, double bound) {
    	super(heuristic, bound);
    }

    /**
     * O(d) on parallel machines where d the solution depth.
     */
    public orbital.math.functional.Function complexity() {
	return Functions.id;
    }
    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function spaceComplexity() {
	return (orbital.math.functional.Function) Operations.power.apply(Values.symbol("b"), Functions.id);
    }

    protected Option solveImpl(GeneralSearchProblem problem) {
	setBound(getMaxBound());
	return search(Collections.singletonList(new Option(getProblem().getInitialState())).iterator());
    }
	
    protected final Iterator createTraversal(GeneralSearchProblem problem) {
	throw new UnsupportedOperationException(ParallelBranchAndBound.class + " defines its own search and solveImpl, (currently) without the aid of a traversal iterator");
    }

    protected Option search(Iterator nodes) {
	bestSolution = null;
        //@todo should we stop when a thread in tg throws an uncaught exception? Or ignore it?
        final ThreadGroup bnb = new ThreadGroup("BranchAndBound");
        // gescheiterte Suchprozesse am Ende automatisch schließen
        bnb.setDaemon(true);
        // start searching
        final ExploreBranch root = new ExploreBranch(nodes);
        new Thread(bnb, root).start();

        // loop waiting for best solution
        while (bnb.activeCount() > 0)
	    try {
            	Thread t[] = new Thread[1];
            	if (bnb.enumerate(t) == 0)
		    break;
            	t[0].join();
            }
            catch (InterruptedException irq) {
		Thread.currentThread().interrupt();
            }
        
        return bestSolution;
    }
	
    /**
     * The lock to obtain for sychronized access to bestSolution.
     * necessary since bestSolution might still be null.
     */
    private transient Object bestSolutionLock = new Object();
    /**
     * contains current best solution
     * @serial
     * @todo serialization?
     */
    /*volatile for Double-checked locking with new semantics*/
    private Option bestSolution;

    /**
     * Sustain transient variable initialization when deserializing.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	bestSolutionLock = new Object();
    }

    /**
     * Explore a list of nodes by spawning a new child thread for each node selected,
     * with the nodes that are expanded to explore.
     * Implemented as a multi-threaded version of a right-linear tail-recursion.
     */
    private final class ExploreBranch implements Runnable {
	private final Iterator nodes;
	/**
	 * Explore a branch.
	 * @param nodes the list of the nodes to explore.
	 */
	public ExploreBranch(Iterator nodes) {
	    this.nodes = nodes;
	}

	public final void run() {
	    while (nodes.hasNext()) {
		// this will lead to DepthFirstSearch selection, although we work on the expanded iterator, directly!
		Option node = (Option) nodes.next();
                
                if (isOutOfBounds(node))
		    continue;									// prune node
        		
		if (getProblem().isSolution(node)) {
		    Option solution = processSolution(node);
		    /*
		    // non-synchronized unwrapping pre-check reduces synchronization overhead (*) @see #bestSolutionLock
		    if (bestSolution == null || solution.compareTo(bestSolution) < 0)
		    synchronized(bestSolutionLock) {
		    // comparison is not necessary for BranchAndBound(!), but its more safe to
		    // synchronized Double-checked locking to ensure concurrent thread-safety (*) @see #bestSolutionLock
		    if (bestSolution == null || solution.compareTo(bestSolution) < 0)
		    bestSolution = solution;
		    }
		    */
		    synchronized(bestSolutionLock) {
			// comparison is not necessary for BranchAndBound(!), but its more safe to
			// no Double-checked locking due to errors until after new semantics @see #bestSolution
			if (bestSolution == null || solution.compareTo(bestSolution) < 0)
			    bestSolution = solution;
		    }
		}
		Iterator children = getProblem().expand(node);
		// since nodes implementation is a Stack (DepthFirstSearch)
		// we don't need to add and pass the rest of nodes on the child threads.
		Runnable child = new ExploreBranch(children);
		if (nodes.hasNext())
		    //TODO: could use a n-sized "singleton" ThreadPool coordinating threads such that at most n are running
		    // this would lead to an enormous decrease of thread construction and improve performance
		    new Thread(child).start();
		else
		    // reuse this thread for join to work efficiently, and performance reasons
		    child.run();
	    }
	}
    }

    //	protected Option search(Collection nodes) {
    //		return search(nodes.iterator());
    //	}
}
