/**
 * @(#)BreadthFirstSearch.java 1.0 2000/09/17 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Collection;
import orbital.math.functional.Function;
import java.util.Iterator;

import orbital.math.functional.Functions;
import java.util.LinkedList;
import java.util.Collections;
import orbital.util.QueuedSequenceIterator;

import orbital.math.functional.Operations;
import orbital.math.Values;

/**
 * BreadthFirstSearch class (BrFS). A blind search algorithm.
 * <p>
 * Expands shallowest nodes first.</p>
 * <p>
 * BrFS is complete, optimal for uniform costs, and
 * has a time and space complexity of O(b<sup>d</sup>).</p>
 * <p>
 * Implementation data structure is a Queue (FIFO).</p>
 *
 * @version 1.0, 2000/09/17
 * @author  Andr&eacute; Platzer
 */
public class BreadthFirstSearch extends GeneralSearch {
    private static final long serialVersionUID = -3246910930824688923L;
    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     * DepthFirstSearch might not find a solution for search space graphs with inifinite breadth.
     * O(<span class="Formula">(b<sup>d</sup>-1)/(b-1) + b<sup>d</sup></span>) more precisely.
     * @todo O(|V|+|E|) on a graph (V,E) with vertexes V and edges E.
     */
    public Function complexity() {
	return (Function) Operations.power.apply(Values.getDefaultInstance().symbol("b"),Functions.id);
    }
    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     * O(b<sup>d-1</sup>) more precisely.
     */
    public Function spaceComplexity() {
	return complexity();
    }
    /**
     * Optimal only for when costs are uniform.
     * Costs are uniform if they fulfill h<sup>*</sup>(n)=0.
     */
    public boolean isOptimal() {
    	return true;
    }

    protected Iterator createTraversal(GeneralSearchProblem problem) {
	return new OptionIterator(problem);
    }

    /**
     * An iterator over a state space in breadth-first order.
     * @version 1.0, 2001/08/01
     * @author  Andr&eacute; Platzer
     */
    public static class OptionIterator extends GeneralSearch.OptionIterator {
	private static final long serialVersionUID = -6989557875498264664L;
	/**
	 * effectively, nodes is a queue of iterators.
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
	    nodes.add(newNodes);
	    return newNodes.hasNext();
        }
    };


    //	protected Collection createCollection() {
    //		// new Queue();
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
    //    	oldNodes.addAll(newNodes);
    //    	return oldNodes;
    //    }
}
