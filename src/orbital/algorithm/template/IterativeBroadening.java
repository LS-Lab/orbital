/**
 * @(#)IterativeBroadening.java 1.0 2001/05/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Iterator;

import orbital.math.functional.Functions;
import orbital.math.functional.Operations;
import orbital.math.Values;
import java.util.NoSuchElementException;

/**
 * Iterative Broadening (IB). A blind search algorithm.
 * <p>
 * Iterative Broadening uses increasing bounds for the breadth of the search space
 * that is subject to expansion.
 * </p>
 * <p>
 * This algorithm is often inferior to {@link IterativeDeepening iterative deepening}, but
 * may be useful for search graphs that are not <a href="GeneralSearchProblem.html#locallyFinite">locally finite</a>.
 * </p>
 *
 * @version 0.8, 2001/05/09
 * @author  Andr&eacute; Platzer
 * @see PackageUtilities#restrictTop(int,GeneralSearchProblem,Function)
 * @attribute usually inferior to {@link IterativeDeepening}
 * @todo aspect of iterative broadening of search space.
 */
public class IterativeBroadening extends DepthFirstBoundingSearch {
    private static final long serialVersionUID = 7810245539762321618L;
    /**
     * Whether we have pruned a node during the last call to super.search.
     * @serial
     * @see IterativeDeepeningAStar#nextBound
     */
    private boolean havePruned;
    public IterativeBroadening() {
	// better solutions could be found more to the left
	setContinuedWhenFound(true);
    }
	
    public Function getEvaluation() {
	throw new UnsupportedOperationException("implicit evaluation function used for iterative broadening");
    }

    public orbital.math.functional.Function complexity() {
	//TODO: think about changing all infinite functions to symbolic or anything which is not constant, but supports norm=infinity
	return Functions.constant(Values.POSITIVE_INFINITY);
    }
    public boolean isOptimal() {
    	return false;
    }

    protected final boolean isOutOfBounds(Object/*>S<*/ node) {
	// never decided here, but already in IterativeBroadening.OptionIterator#isOutOfBounds()
	return false;
    }

    /**
     * Solve with bounds 1, 3, 4, 5, ... until a solution is found.
     */
    protected Object/*>S<*/ solveImpl(GeneralSearchProblem problem) {
	int i = 1;
	while (true) {
	    setBound(i++);
	    havePruned = false;
	    Object/*>S<*/ solution = super.search(createTraversal(problem));
	    if (solution != null)
		return solution;
	    //@xxx if (isContinuedWhenFound()) continue broadening to find a better solution? But where's the advantage, then?
	    else if (!havePruned)
		// the search options have been fully exhausted and an increase of bound would not make sense anymore because the search has completely failed
		return null;
	}
    }

    protected Iterator createTraversal(GeneralSearchProblem problem) {
	return new OptionIterator(problem);
    }

    /**
     * An iterator over a state space in depth-first order
     * respecting the current bounds for the breadth of the search space that is subject to expansion.
     * @version 1.0, 2001/08/01
     * @author  Andr&eacute; Platzer
     */
    public class OptionIterator extends DepthFirstSearch.OptionIterator {
	private static final long serialVersionUID = 3559635773974511101L;
	public OptionIterator(GeneralSearchProblem problem) {
	    super(problem);
	}
        protected boolean add(final Iterator newNodes) {
	    return super.add(new Iterator() {
		    // @see <a href="{@docRoot}/Patterns/Design/Decorator.html">Decorator</a>
		    /**
		     * How often we have successfully expanded by a call to next().
		     */
		    private int expansionCount = 0;
		    private boolean isOutOfBounds() {
			return !(expansionCount < getBound().doubleValue());
		    }
		    public boolean hasNext() {
			if (isOutOfBounds()) {
			    if (newNodes.hasNext())
				havePruned = true;
			    return false;
			} else
			    return newNodes.hasNext();
		    }
		    public Object next() {
			if (isOutOfBounds()) {
			    if (newNodes.hasNext()) {
				havePruned = true;
				throw new NoSuchElementException("out of bounds for breadth of expansion");
			    } else
				throw new NoSuchElementException();
			} else {
			    Object o = newNodes.next();
			    expansionCount++;
			    return o;
			}
		    }
		    public void remove() {
			newNodes.remove();
		    }
        	});
        }
    }

    /**
     * Solve with bounds 1, 3, 4, 5, ... until a solution is found.
     */
    //	protected GeneralSearchProblem.Option search(Collection nodes) {
    //		int i = 1;
    //		while (true) {
    //			setBound(i++);
    //			Collection n = createCollection();
    //			n.addAll(nodes);
    //			GeneralSearchProblem.Option solution = super.search(n);
    //			if (solution != null)
    //				return solution;
    //				//@xxx if (isContinuedWhenFound()) continue broadening to find a better solution? But where's the advantage, then?
    //		}
    //	}
    //
    //    protected Collection add(Collection newNodes, Collection oldNodes) {
    //    	((java.util.List) newNodes).subList(0, (int) getBound()).addAll(oldNodes);
    //    	return newNodes;
    //    }
}
