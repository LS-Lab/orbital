/**
 * @(#)GeneralSearch.java 1.0 2000/09/17 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;
import orbital.algorithm.template.GeneralSearchProblem.Transition;

import java.util.Collection;
import java.io.Serializable;
import java.util.Iterator;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.LinkedList;

import orbital.logic.functor.MutableFunction;
import orbital.math.Real;
import orbital.math.Values;

/**
 * Abstract general search algorithm scheme.
 * <p>
 * The various subclasses implement different search strategies, but follow this coherent framework.</p>
 * <p>
 * Apart from the reference to the problem to solve, GeneralSearch implementations are usually stateless.</p>
 *
 * @version 1.0, 2000/09/17
 * @author  Andr&eacute; Platzer
 * @see GeneralSearchProblem
 * @see <a href="http://www.ldc.usb.ve/~hector">Hector Geffner. Modelling and Problem Solving</a>
 * @see <a href="{@docRoot}/Patterns/Design/Strategy.html">Strategy</a>
 * @see <a href="doc-files/AlgorithmicTable.png">Table of Algorithms in Comparison</a>
 * @todo introduce BidirectionalSearch and a BidirectionalSearchProblem (either extending GeneralSearchProblem complementing expand and getInitialState with methods working backwards from the goal, or feed BidirectionalSearch with two complementary GeneralSearchProblem objects). They work like Backchaining from the goal instead of "usual" Forward chaining from the initial state.
 * @todo introduce TabuSearch
 * @todo once we have decided once and for all against expand-Collections we can get rid of createCollection(), select(Collection), add(Collection,Collection), and search(Collection) in our subclasses.
 * @todo should we split this package into orbital.algorithm.template, and orbital.algorithm.search('n'planning) ?
 */
public abstract class GeneralSearch implements AlgorithmicTemplate/*<GeneralSearchProblem,Object>*/, Serializable {
    private static final long serialVersionUID = -2839281671298699169L;
    
    /**
     * The search problem to solve.
     * @serial
     */
    private GeneralSearchProblem problem = null;
	
    // get/set properties
	
    /**
     * Get the current problem.
     * @preconditions true
     * @return the problem specified in the last call to solve,
     *  or <code>null</code> if there is no current problem (since solve has not yet been called etc.).
     */
    protected final GeneralSearchProblem getProblem() {
	return problem;
    }
    /**
     * Set the current problem.
     */
    private final void setProblem(GeneralSearchProblem newProblem) {
	GeneralSearchProblem old = this.problem;
	this.problem = newProblem;
	firePropertyChange("problem", old, problem);
    }
    
    /**
     * Whether this search algorithm is optimal.
     * <p>
     * If a search algorithm is not optimal, i.e. it might be content with solutions that are
     * sub optimal only, then it can at most reliably find solutions, not best solutions.
     * However, those solutions found still provide an upper bound to the optimal solution.
     * </p>
     * @return whether this search algorithm is optimal, i.e. whether solutions found are guaranteed to be optimal.
     * @preconditions true
     * @postconditions RES == OLD(RES) && OLD(this) == this
     */
    public abstract boolean isOptimal();

    // helper methods

    /**
     * (Internal) property change notifications.
     * Called when bound properties change.
     * @see java.beans.PropertyChangeSupport#firePropertyChange(String,Object,Object)
     * @todo protectedize?
     */
    void firePropertyChange(String property, Object oldValue, Object newValue) {}
    
    // solution operations for search problems
    
    /**
     * Solves a general search problem.
     * @preconditions p instanceof GeneralSearchProblem.
     * @throws ClassCastException if p is not an instance of GeneralSearchProblem.
     * @see #solve(GeneralSearchProblem)
     */
    public final Object solve(AlgorithmicProblem p) {
    	return solve((GeneralSearchProblem)p);
    }
    
    /**
     * Solves a general search problem.
     * <p>
     * This method does not need to be overwritten.
     * Overwrite {@link #solveImpl(GeneralSearchProblem)}, instead.</p>
     * @return the solution found (represented as an option with solution state, final action and accumulated cost),
     *  or <code>null</code> if no solution was found at all.
     * @preconditions true
     * @postconditions solution == null &or; p.isSolution(solution)
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     * @see #solveImpl(GeneralSearchProblem)
     */
    public final Object/*>S<*/ solve(GeneralSearchProblem/*<A,S>*/ p) {
    	setProblem(p);
	Object/*>S<*/ solution;
	//    	if (false) {
	//    		Collection/*<Option>*/ nodes = createCollection();
	//    		nodes.add(new Option(getProblem().getInitialState()));
	//    		solution = search(nodes);
	//    	} else
	solution = solveImpl(p);
	boolean incorrect = true;
	assert (incorrect = this instanceof ProbabilisticAlgorithm && !((ProbabilisticAlgorithm) this).isCorrect()) | true : "initialize for restricting solve@postconditions to correct algorithms";
    	assert incorrect || (solution == null || p.isSolution(solution)) : "post: solution == null || getProblem().isSolution(solution)";
    	return solution;
    }

    // central primitive operations
    
    /**
     * Implements the solution policy.
     * <p>
     * Like the default solution policies, this implementation will
     * <ol>
     *   <li>
     *     Fetch a traversal order policy by calling {@link #createTraversal(GeneralSearchProblem)}.
     *   </li>
     *   <li>
     *     Then call {@link #search(Iterator)} to search through the state space in the traversal order.
     *   </li>
     * </ol>
     * However, sophisticated search algorithms may want to change that policy and iterate the
     * above process, resulting in a sequence of calls to those methods.
     * They may do so by by overwriting this method.
     * </p>
     * @return the solution found by {@link #search(Iterator)}.
     * @preconditions problem == getProblem()
     * @postconditions solution == null &or; p.isSolution(solution)
     * @see #search(Iterator)
     * @xxx what <em>exactly</em> is the conceptual difference between solveImpl(GeneralSearchProblem) and search(Iterator). Perhaps we could get rid of this method?
     */
    protected Object/*>S<*/ solveImpl(GeneralSearchProblem/*<A,S>*/ problem) {
	return search(createTraversal(problem));
    }

    /**
     * Define a traversal order by creating an iterator for the problem's state space.
     * <p>
     * Lays a linear order through the state space which the search
     * can simply follow sequentially. Thus a traversal policy effectively reduces a search
     * problem through a graph to a search problem through a linear sequence of states.
     * Of course, the mere notion of a traversal policy does not yet solve the task of finding
     * a good order of states, but only encapsulate it.
     * Complete search algorithms result from traversal policies that have a linear sequence
     * through the whole state space.
     * </p>
     * @param problem the problem whose state space to create a traversal iterator for.
     * @return an iterator over the options
     *  of the problem's state space thereby encapsulating and hiding the traversal order.
     * @attribute secret traversal order
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see GeneralSearch.OptionIterator
     */
    protected abstract Iterator/*<S>*/ createTraversal(GeneralSearchProblem/*<A,S>*/ problem);

    // central virtual methods

    /**
     * Run the general search algorithm scheme.
     * <p>
     * This method only needs to be overwritten to provide a completely different search scheme.
     * Usually, the default search algorithm scheme is sufficient.
     * </p>
     * @param nodes is the iterator over the nodes to visit (sometimes called open set)
     *  which determines the traversal order.
     * @return the solution found searching the state space via <var>nodes</var>.
     * @postconditions solution == null &or; p.isSolution(solution)
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     * @internal
     *  Implemented as an iterative unrolling of a right-linear tail-recursion.
     *  Otherwise it would be important to shallow-clone the nodes list
     *  for byvalue semantics of recursion call.
     * @internal see orbital.util.Setops#find(Iterator, Predicate) identical implementation
     */
    protected Object/*>S<*/ search(Iterator/*<S>*/ nodes) {
	while (nodes.hasNext()) {
	    Object/*>S<*/ node = nodes.next();

	    if (getProblem().isSolution(node))
		return node;
    	}

    	// fail
    	return null;
    }

    /**
     * Abstract implementation base class for iterators determining the traversal order.
     * Concrete implementations define the traversal order for the state space by providing
     * an iterator over the state options of a search graph (as induced by a {@link GeneralSearchProblem state-model}).
     * <p>
     * Although it is not required that option iterators extend this class, it usually comes as
     * a great relief since sub classes will not have to deal with too much details.
     * Full-blown option iterators only have to implement the abstract methods of this
     * class and determine the data collection implementation maintained.
     * </p>
     * <p>
     * Particularly, this iterator approach has the advantage of promising that an option node
     * will not be required again, when the next element has already been requested.
     * Those search algorithms that have a need to keep intermediate states for later comparison,
     * will need to remember the states themselves.
     * </p>
     *
     * @attribute secret traversal order
     * @invariants sub classes maintain a collection of nodes to select from
     * @version 0.8, 2001/08/01
     * @author  Andr&eacute; Platzer
     * @see <a href="{@docRoot}/Patterns/Design/Strategy.html">Strategy</a>
     * @see GeneralSearch#createTraversal(GeneralSearchProblem)
     */
    public static abstract class OptionIterator implements Iterator/*_<S>_*/, Serializable {
	private static final long serialVersionUID = 6410799454884265654L;
    	/**
    	 * The search problem to solve.
    	 * @serial
    	 */
    	private final GeneralSearchProblem/*<A,S>*/ problem;
    	
	/**
	 * The last node selected by {@link #next()}.
	 * @serial
	 */
	private Object/*>S<*/ lastRet = null;

	/**
	 * Whether lastRet has already been expanded by {@link #hasNext()}.
	 * @serial
	 */
	private boolean	hasExpanded = false;
		
	/**
	 * Create a traversal iterator over the problem's state options.
	 * <p>
	 * Sub classes <em>must</em> add the {@link GeneralSearchProblem#getInitialState() initial state}
	 * to their internal collection of nodes, such that it will be the (single) element
	 * expanded first.</p>
	 * @param problem the problem whose options to iterate in an iterator specific order.
	 * @postconditions must still add problem.getInitialState() to nodes, such that !isEmpty()
	 */
	protected OptionIterator(GeneralSearchProblem/*<A,S>*/ problem) {
	    this.problem = problem;
	}
		
    	/**
    	 * Get the current problem.
    	 * @preconditions true
    	 * @return the problem specified in the last call to solve.
    	 */
    	protected final GeneralSearchProblem/*<A,S>*/ getProblem() {
	    return problem;
    	}

        // central template methods

        /**
         * Returns <code>true</code> if this iterator's collection of nodes currently does not contain any elements.
         * @return <code>true</code> if this collection contains no elements.
         * @postconditions RES == nodes.isEmpty()
         */
        protected abstract boolean isEmpty();

        /**
         * Selects an option to visit from nodes.
         * @return the selected option after <em>removing</em> it from nodes.
         * @preconditions !isEmpty()
         * @postconditions OLD(nodes).contains(RES) && nodes == OLD(nodes) \ {RES}
         */
        protected abstract Object/*>S<*/ select();

        /**
         * Concatenate the new nodes and the old nodes.
         * Concatenates by some algorithm-dependant means.
         * @param newNodes the new nodes we apparently became aware of. (Might be modified by this method).
         * @return true if nodes changed as a result of the call.
         * @postconditions nodes &sube; OLD(nodes) &cup; newNodes && RES = nodes&ne;OLD(nodes)
         */
        protected abstract boolean add(Iterator/*<S>*/ newNodes);

        // interface Iterator implementation
        
    	/**
    	 * {@inheritDoc}
    	 * @preconditions true
    	 * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
    	 */
    	public boolean hasNext() {
	    if (!isEmpty())
		// we still have some nodes in petto
		return true;
	    else if (lastRet == null)
		// we have no more nodes, and none to expand as well
		return false;
	    else
		// we have no more nodes, but we might get some if we expand lastRet
		return expand();
    	}

    	/**
    	 * {@inheritDoc}
    	 * <p>
    	 * Will expand the last element returned, and select a state option to visit.</p>
    	 * @preconditions hasNext()
    	 * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
	 * @todo optimize
    	 */
    	public Object/*_>S<_*/ next() {
	    if (lastRet != null)
		expand();
	    if (isEmpty())
		throw new NoSuchElementException();
	    hasExpanded = false;
	    return lastRet = select();
    	}

    	/**
    	 * Expands lastRet.
    	 * <p>
    	 * Will only expand lastRet, if it has not already been expanded.
    	 * Particularly, if lastRet has already been expanded, this method will only return <code>false</code>.</p>
    	 * @preconditions lastRet != null
    	 * @return whether new nodes were added by this call to expand.
    	 */
    	private boolean expand() {
	    if (lastRet == null)
		throw new IllegalStateException("cannot expand without a node returned last!");
	    if (hasExpanded)
		return false;
	    Iterator/*<S>*/ children = GeneralSearch.expand(getProblem(), lastRet);
	    hasExpanded = true;
	    return add(children);
    	}

    	/**
    	 * Removes from the list of exandable nodes the last element returned by the iterator.
    	 * <p>
    	 * When calling this method, the last node that was returned by this iterator will be pruned
    	 * and not expanded any further.
    	 * </p>
    	 * @throws UnsupportedOperationException if the <code>remove</code> operation is not supported by this Iterator.
	 * @throws IllegalStateException if the <code>next</code> method has not yet been called, or the <code>remove</code> method has already been called after the last call to the <code>next</code> method.
    	 */
    	public void remove() {
    	    if (lastRet == null)
		throw new IllegalStateException();
	    lastRet = null;
    	}
    }

    /**
     * Helper method returning all states reachable with any action from state s.
     * @return S(s) := {t(s,a) &brvbar; a&isin;A(s)}
     */
    static final Iterator expand(final GeneralSearchProblem problem, final Object/*>S<*/ state) {
	return new Iterator() {
		final Iterator/*<A>*/ actions = problem.actions(state);
		final MutableFunction g = problem.getAccumulatedCostFunction();
		final Real accumulatedCost = (Real/*__*/) g.apply(state);
		public boolean hasNext() {
		    return actions.hasNext();
		}
		public Object next() {
		    Object/*>A<*/ a = actions.next();
		    Iterator t = problem.states(a, state);
		    assert t.hasNext() : "@postconditions GeneralSearchProblem.states(...) non-empty";
		    Object/*>S<*/ sp = t.next();
		    assert !t.hasNext() : "@postconditions GeneralSearchProblem.states(...) has length 1";
		    g.set(sp,
			  accumulatedCost.add(((Transition)problem.transition(a,state,sp)).getCost()));
		    return sp;
		}
		public void remove() {
		    actions.remove();
		}
	    };
    }
    

    //    /**
    //     * Get a new instance of the implementation data structure.
    //     * <p>
    //     * Implementing methods could return special list implementations like stacks or queues.</p>
    //	 * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
    //	 * @todo remove
    //     */
    //	protected Collection/*<Option>*/ createCollection() {
    //		return new LinkedList/*<Option>*/();
    //	}
	

    //    /**
    //     * Selects an option to visit from nodes.
    //     * @return the selected option after <em>removing</em> it from nodes.
    //     * @preconditions !nodes.isEmpty()
    //     * @postconditions OLD(nodes).contains(RES) && nodes == OLD(nodes) \ {RES}
    //	 * @todo remove
    //     */
    //    protected abstract Option select(Collection/*<Option>*/ nodes);
    //    
    //    /**
    //     * Concatenate the new nodes and the old nodes.
    //     * Concatenates by some algorithm-dependant means.
    //     * @param newNodes the new nodes we apparently became aware of. (May be modified by this method).
    //     * @param oldNodes the old nodes we already knew of. (May be modified by this method).
    //     * @return a list of nodes to explore that contains oldNodes and newNodes.
    //     * @postconditions RES &sube; oldNodes &cup; newNodes
    //	 * @todo remove
    //     */
    //    protected abstract Collection/*<Option>*/ add(Collection/*<Option>*/ newNodes, Collection/*<Option>*/ oldNodes);
    //	
    //    // central virtual methods
    //
    //	/**
    //	 * run the general search algorithm scheme.
    //	 * <p>
    //	 * Overwrite to provide a whole different search scheme.</p>
    //	 * @param nodes is the list of nodes to visit (sometimes called open set).
    //	 * @return the solution found searching the state space starting with nodes.
    //	 * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
    //	 * @todo remove
    //	 */
    //	protected Option search(Collection/*<Option>*/ nodes) {
    //		while (!nodes.isEmpty()) {
    //    		Option node = select(nodes);
    //
    //    		if (getProblem().isSolution(node))
    //    			return node;
    //    		Collection/*<Option>*/ children = orbital.util.Setops.asList(getProblem().expand(node));
    //    		nodes = add(children, nodes);
    //    	}
    //
    //    	// fail
    //    	return null;
    //	}
}


//@internal Obsolete stuff, can be removed
//    /**
//     * Get the accumulated cost of the solution.
//	 * @preconditions {@link #solve(GeneralSearchProblem)} has finished successfully
//     * @return the accumulated cost that lead to the solution last returned by solve.
//     * @see #solve(GeneralSearchProblem)
//     */
//	public double getCost() {
//		return cost;
//	}
//    
//	/**
//	 * The accumulated cost that lead to the solution.
//	 * @serial
//	 */
//	private double cost;
	
