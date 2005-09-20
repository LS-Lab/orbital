/**
 * @(#)ParallelBranchAndBound.java 1.0 2000/09/22 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Iterator;
import java.util.Collection;

import java.util.Collections;
import orbital.math.functional.Operations;
import orbital.math.functional.Functions;
import orbital.math.Values;
import orbital.math.Real;

/**
 * Parallel branch-and-bound algorithm.
 * <p>
 * Visits the next nodes in parallel and bound by the best solution
 * known. Even though the implementation uses a stack of nodes, due
 * to the parallel nature this algorithm behaves much like {@link
 * BreadthFirstSearch}. However, the scheduling policy of the thread
 * scheduler may introduce a probabilistic behaviour in execution
 * speed.
 * </p>
 * <h3 id="theory">Multi-Processors versus Multi-Threading on a Single-Processor</h3>
 * <p>
 * Parallel algorithms profit much from multi-processors, but their
 * benefits are nevertheless not limited to those machines. Even
 * single-processor systems can experience a speed-up when using a
 * parallel variant of a simple algorithm.
 * </p>
 * <p>
 * The <dfn>speed-up</dfn> <var>S(n)</var> := T(1)/T(n)
 * in execution time <var>T(n)</var>
 * on an <var>n</var> processor system is in general in
 * 1&le;S(n)&le;I(n)&le;<var>n</var>, with
 * I(n) := P(n)/T(n) being the <dfn>parallel index</dfn> of mean
 * parallelization on an <var>n</var> processor system
 * (P(n) := number of operations on <var>n</var> processors).
 * </p>
 * <p>
 * However, in rare cases super-linear speed-ups of <var>S(n)</var>&gt;<var>n</var>
 * can be observed due to synergy effects, even though they impossible
 * from a theoretical perspective. In fact, they result from comparing
 * slightly different(!) algorithms, or from more memory or larger caches.
 * Yet, when they occur these advantages should be exploited.
 * </p>
 * <p>
 * ParallelBranchAndBound very often is such a case where super-linear
 * speed-ups occur, because a parallel depth-first search no longer is
 * depth-first in the large. In combination with the bounding, the
 * synergetic effects result from the fact that the one thread's
 * success may simplify another thread's job. By its sheer
 * parallelity, the parallel depth-first branch-and-bound more
 * resembles breadth-first search inspite of its implementation
 * similarity with depth-first search. And this explains why using
 * ParallelBranchAndBound can result in a better performance even on
 * single-processor systems.
 * </p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see BreadthFirstSearch
 * @todo aspect of parallel exploration.
 */
public class ParallelBranchAndBound/*<A,S>*/ extends BranchAndBound/*<A,S>*/ {
    private static final long serialVersionUID = -7665864997088831748L;
    public ParallelBranchAndBound(Function/*<S,Real>*/ heuristic, double bound) {
        super(heuristic, bound);
    }
    ParallelBranchAndBound() {}

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
        return (orbital.math.functional.Function) Operations.power.apply(Values.getDefaultInstance().symbol("b"), Functions.id);
    }

    protected Object/*>S<*/ solveImpl(GeneralSearchProblem/*<A,S>*/ problem) {
        setBound(getMaxBound());
        return search(Collections.singletonList(getProblem().getInitialState()).iterator());
    }
        
    protected final Iterator/*<S>*/ createTraversal(GeneralSearchProblem/*<A,S>*/ problem) {
        //@todo could we transforme the search algorithm to a traversal iterator or modularize the parallel aspect in another way?
        throw new AssertionError(ParallelBranchAndBound.class + " defines its own search and solveImpl, (currently) without the aid of a traversal iterator");
    }

    protected Object/*>S<*/ search(Iterator/*<S>*/ nodes) {
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
     * Contains current best solution.
     * @serial
     * @todo serialization?
     */
    /*volatile for Double-checked locking with new semantics*/
    private Object/*>S<*/ bestSolution;
    /**
     * Contains the accumulated cost of {@link #bestSolution}, thus the current best accumulated cost.
     * @serial
     * @todo serialization?
     */
    private Real bestAccumulatedCost = Values.NaN;

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
        private final Iterator/*<S>*/ nodes;
        /**
         * Explore a branch.
         * @param nodes the list of the nodes to explore.
         */
        public ExploreBranch(Iterator/*<S>*/ nodes) {
            this.nodes = nodes;
        }

        public final void run() {
            final Function/*<S,Real>*/ g = getProblem().getAccumulatedCostFunction();
            while (nodes.hasNext()) {
                // this will lead to DepthFirstSearch selection, although we work on the expanded iterator, directly!
                Object/*>S<*/ node = nodes.next();
                
                if (isOutOfBounds(node))
                    continue;                                                                   // prune node
                        
                if (getProblem().isSolution(node)) {
                    Object/*>S<*/ solution = processSolution(node);
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
                    final Real accumulatedCost = castedApply(g, solution);
                    synchronized(bestSolutionLock) {
                        // comparison is not necessary for BranchAndBound(!), but its more safe to
                        // no Double-checked locking due to errors until after new semantics @see #bestSolution
                        //@link orbital.util.Setops#argmin
                        if (bestSolution == null || accumulatedCost.compareTo(bestAccumulatedCost) < 0) {
                            bestSolution = solution;
                            bestAccumulatedCost = accumulatedCost;
                        }
                    }
                }
                Iterator/*<S>*/ children = expand(getProblem(), node);
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

    //  protected Option search(Collection nodes) {
    //          return search(nodes.iterator());
    //  }
}
