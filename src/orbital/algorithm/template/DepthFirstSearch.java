/**
 * @(#)DepthFirstSearch.java 1.0 2000/09/17 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Collection;

import orbital.math.functional.Function;
import orbital.math.functional.Functions;
import java.util.Iterator;

import java.util.LinkedList;
import java.util.Collections;
import orbital.util.QueuedSequenceIterator;

import orbital.math.Values;

/**
 * DepthFirstSearch class (DFS). A blind search algorithm.
 * <p>
 * Expands deepest nodes first.</p>
 * <p>
 * DFS is neither complete nor optimal, and
 * has a time complexity of O(&infin;) and a space complexity of O(b*d).
 * However if the problem's expand function returns the same node only once
 * to prevent DFS from ending in a cycle (by keeping track of the nodes that were already visited)
 * and if the state space is finite, DFS can be made complete.</p>
 * <p>
 * Implementation data structure is a Stack (LIFO).</p>
 *
 * @version 1.0, 2000/09/17
 * @author  Andr&eacute; Platzer
 * @see Backtracking
 * @internal also has a simple recursive formulation.
 * @internal note Can be made complete for finite state space with bookkeeping to avoid loops.
 */
public class DepthFirstSearch extends GeneralSearch {
    private static final long serialVersionUID = -9123082892783190173L;
    //TODO: think about unifying! see Backtracking, Evaluation/ContainerWeighting

    /**
     * O(&infin;).
     * DepthFirstSearch might not find a solution for search space graphs with inifinite depth
     * or infinite breadth.
     * o(d+1) up to O(<span class="Formula">(b<sup>d+1</sup>-1)/(b-1)</span>) more precisely. On average &asym;<span class="Formula">b<sup>d</sup>/2</span>.
     * @todo O(|V|+|E|) on a graph (V,E) with vertexes V and edges E.
     */
    public Function complexity() {
	//TODO: think about changing all infinite functions to symbolic or anything which is not(!) constant, but still supports norm=infinity
	return Functions.constant(Values.POSITIVE_INFINITY);
    }
    /**
     * O(b*d) where b is the branching factor and d the solution depth.
     * O(<span class="Formula">(b-1)*d + 1</span>) more precisely.
     */
    public Function spaceComplexity() {
	return Functions.linear(Values.getDefaultInstance().symbol("b"));
    }
    public boolean isOptimal() {
    	return false;
    }

    protected Iterator createTraversal(GeneralSearchProblem problem) {
	return new OptionIterator(problem);
    }

    /**
     * An iterator over a state space in depth-first order.
     * @version 1.0, 2001/08/01
     * @author  Andr&eacute; Platzer
     */
    public static class OptionIterator extends GeneralSearch.OptionIterator {
	private static final long serialVersionUID = 4198888198183455112L;
	/**
	 * effectively, nodes is a stack of iterators.
	 * @serial
	 */
	private QueuedSequenceIterator/*_<S>_*/ nodes;
	public OptionIterator(GeneralSearchProblem problem) {
	    super(problem);
	    nodes = new QueuedSequenceIterator(new Iterator[] {Collections.singletonList(problem.getInitialState()).iterator()});
	}
        protected boolean isEmpty() {
	    return !nodes.hasNext();
        }
        protected Object/*>S<*/ select() {
	    return nodes.next();
        }
        protected boolean add(Iterator newNodes) {
	    nodes.add(0, newNodes);
	    return newNodes.hasNext();
        }
    };

    //	protected Collection createCollection() {
    //		// new Stack();
    //		return new LinkedList();
    //	}
    //
    //    protected GeneralSearchProblem.Option select(Collection nodes) {
    //    	Iterator i = nodes.iterator();
    //    	GeneralSearchProblem.Option sel = (GeneralSearchProblem.Option) i.next();
    //    	i.remove();
    //    	return sel;
    //    }
    //
    //    protected Collection add(Collection newNodes, Collection oldNodes) {
    //    	newNodes.addAll(oldNodes);
    //    	return newNodes;
    //    }
}
