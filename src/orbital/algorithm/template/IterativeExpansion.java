/**
 * @(#)IterativeExpanesion.java 1.0 2002-07-24 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import orbital.math.Real;

import java.util.Iterator;
import java.io.Serializable;

import java.util.List;
import java.util.Collections;
import orbital.util.Setops;

import orbital.logic.functor.Functionals;
import orbital.math.functional.Functions;
import orbital.math.functional.Operations;
import orbital.math.Values;

import java.util.LinkedList;

/**
 * Iterative Expansion (IE).
 * <p>
 * Iterative Expansion is a memory-bounded search algorithm that combines the space complexity
 * advantages of IDA<sup>*</sup> with A<sup>*</sup>'s ability of remembering more than just a bound
 * from the rest of the search tree. IE is inferior to SMA<sup>*</sup> which has a more flexible
 * tradeoff between reducing memory consumption and remembering parts of the search tree in order to
 * avoid re-expansion. However, IE has much less memory management overhead and thus has a comparable
 * performance to that of SMA<sup>*</sup>.
 * Nevertheless, all current memory-bounded algorithms have difficulties with problems having
 * too many distinct f-costs (i.e. |f(S)| is large).
 * </p>
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @internal also has a simple recursive formulation.
 * @see "Russel, S. (1992?) Efficient memory-bounded search methods."
 * @see "Korf, R.E. (1991) Best-first search with limited memory. UCLA Comp. Sci.Ann."
 * @todo derive IterativeExpansion from DepthFirstBoundingSearch, or doesn't that use DFS.OptionIterator, because it expands best local neighbour (also with varying f-costs on return of expansion)?
 * However, it's not BestFS.OptionIterator, either, since we don't consider backing up to some very different location somewhere in state space, but keep our mind focues on the current local neighbours.
 * So perhaps we should adapt BestFS.OptionIterator.add such that we forget about the old choices and simply sort the neighbourhood, and keep a stack of sorted local neighbourhoods. (remembering the second best, only, is perhaps a bad idea, since we may back up to the second (and third...) best multiple times if all neighbours are still below bound.).
 * On the current search path, we keep a stack of sorted lists of neighbours, and reinsert the last best element into the sorted list with new f-cost, on "backup".
 * @note memory-bounded algorithms suffer from transpositions in the search graph.
 * @internal we do not extend GeneralBoundingSearch, since the bounds vary from layer to layer (argument of the recursive call).
 */
public class IterativeExpansion/*<A,S>*/
    extends GeneralSearch/*<A,S>*/
    implements HeuristicAlgorithm/*<GeneralSearchProblem<A,S>,S>*/ {
    private static final long serialVersionUID = 4225973116092481279L;
    /**
     * The applied heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @serial
     */
    private Function/*<S,Real>*/ heuristic;
    /**
     * Create a new instance of IDA<sup>*</sup> search.
     * Which is a bounding search using the evaluation function f(n) = g(n) + h(n).
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @see #getEvaluation()
     */
    public IterativeExpansion(Function/*<S,Real>*/ heuristic) {
        setHeuristic(heuristic);
    }
    IterativeExpansion() {}

    public Function/*<S,Real>*/ getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(Function/*<S,Real>*/ heuristic) {
        Function/*<S,Real>*/ old = this.heuristic;
        this.heuristic = heuristic;
        firePropertyChange("heuristic", old, this.heuristic);
    }

    /**
     * f(n) = g(n) + h(n).
     */
    public Function/*<S,Real>*/ getEvaluation() {
        return evaluation;
    }
    private transient Function/*<S,Real>*/ evaluation;
    void firePropertyChange(String property, Object oldValue, Object newValue) {
        super.firePropertyChange(property, oldValue, newValue);
        if (!("heuristic".equals(property) || "problem".equals(property)))
            return;
        GeneralSearchProblem/*<A,S>*/ problem = getProblem();
        this.evaluation = problem != null
            ? Functionals.compose(Operations.plus, problem.getAccumulatedCostFunction(), getHeuristic())
            : null;
    }   
    /**
     * Sustain transient variable initialization when deserializing.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        firePropertyChange("heuristic", null, this.heuristic);
    }

    /**
     * O(b*d) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function spaceComplexity() {
        return Functions.linear(Values.getDefaultInstance().symbol("b"));
    }
    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function complexity() {
        return (orbital.math.functional.Function) Operations.power.apply(Values.getDefaultInstance().symbol("b"),Functions.id);
    }
    /**
     * Optimal if heuristic is admissible, and initial bound sufficiently large (usually &infin;).
     */
    public boolean isOptimal() {
        return true;
    }

    protected Object/*>S<*/ solveImpl(GeneralSearchProblem/*<A,S>*/ problem) {
        Object/*>S<*/ initial = problem.getInitialState();
        return solveByIterativeExpand(new NodeInfo/*<A,S>*/(initial, (Real)getEvaluation().apply(initial)), Values.getDefault().POSITIVE_INFINITY());
    }

    /**
     * @return the solution state (if any).
     * @postconditions node.getFCost() might have changed
     * @todo optimizable, also modularize to an OptionIterator?
     * @internal for bound comparisons we locally define not(POSITIVE_INFINITY=<POSITIVE_INFINITY)
     *  in order to ensure termination on unsolvable cases, where successors.isEmpty() has already been true.
     *  This differs from the paper.
     */
    private final Object/*>S<*/ solveByIterativeExpand(final NodeInfo/*<A,S>*/ node, final Real bound) {
        assert bound.compareTo(bound.zero()) >= 0 && !bound.isNaN() : "bound " + bound + " >= 0";
        //System.err.println(node + "/" + bound);
        if (boundCompare(node.getFCost(), bound) > 0)
            {//System.err.println("cut ");
            return null;}
        else if (getProblem().isSolution(node.getNode()))
            return node.getNode();
        //@internal optimizable by using a (min) heap instead of a list that is kept in sorted order
        // here, we currently use a (sorted) list of NodeInfo sorted by the f-costs
        final List/*<NodeInfo<A,S>>*/ successors = new LinkedList/*<NodeInfo<A,S>>*/();
        {
            final Function/*<S,Real>*/ f = getEvaluation();
            for (final Iterator/*<S>*/ i = GeneralSearch.expand(getProblem(), node.getNode()); i.hasNext(); ) {
                final Object/*>S<*/ o = i.next();
                // pathmax
                final Real fo = (Real) Operations.max.apply(node.getFCost(), f.apply(o));
                successors.add(new NodeInfo/*<A,S>*/(o, fo));
            }
        }
        if (successors.isEmpty()) {
            //System.err.println("\tupdated cost to " + node + " (due to no successors)");
            node.setCost(bound.valueFactory().POSITIVE_INFINITY());
            return null;
        }
        // sort successors in order to have fast access to min and second-best min
        Collections.sort(successors);
        assert orbital.util.Utility.sorted(successors, null) : "Collections.sort@post";
        while (boundCompare(node.getFCost(), bound) <= 0) {
            {
                final NodeInfo/*<A,S>*/ best = (NodeInfo)successors.get(0);
                assert !Setops.some(successors, new orbital.logic.functor.Predicate() { public boolean apply(Object o) {return ((NodeInfo)o).compareTo(best) < 0;} }) : "best has lowest f-cost";
                Real newbound = bound;
                if (successors.size() > 1) {
                    final NodeInfo/*<A,S>*/ secondBest = (NodeInfo)successors.get(1);
                    newbound = (Real) Operations.min.apply(bound, secondBest.getFCost());
                    assert !Setops.some(successors.subList(1, successors.size()), new orbital.logic.functor.Predicate() { public boolean apply(Object o) {return ((NodeInfo)o).compareTo(secondBest) < 0;} }) : "second best has second lowest f-cost";
                    //System.err.println(node + "/" + bound + "\t expanding to " + best + "/" + newbound + "\n\t\talternative " + secondBest);
                }
                
                final Object/*>S<*/ solution = solveByIterativeExpand(best, newbound);
                if (solution != null)
                    // success
                    return solution;

                // remove and reinsert best (which may have updated cost)
                successors.remove(0);
                Setops.insert(successors, best);
                assert orbital.util.Utility.sorted(successors, null) : "@postconditions Setops.insert";
            }
            // circumscription of getEvaluation().set(node.getNode(), node.getFCost());
            node.setCost((Real) ((NodeInfo)successors.get(0)).getFCost());
            //System.err.println("\tupdated cost to " + node + " (due to " + (NodeInfo)successors.get(0) + ")");
        }

        return null;
    }

    /**
     * compares a and b (with the addition that &not;(&infin;&le;&infin;) is defined here).
     * @see Comparator#compare(Object,Object)
     */
    private static final int boundCompare(Real a, Real b) {
        if (a.isInfinite() && a.compareTo(a.zero()) > 0) {
                //assert a.equals(a.valueFactory().POSITIVE_INFINITY());
            //@internal even for b == Values.POSITIVE_INFINITY (here)
            return 1;
        } else if (b.isInfinite() && b.compareTo(b.zero()) > 0) {
                return -1;
        }
        return a.compareTo(b);
    }
        

    /**
     * Keeps additional information about a node of a search graph.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @see orbital.util.KeyValuePair
     */
    private static final class NodeInfo/*<A,S>*/ implements Comparable/*<NodeInfo<A,S>>*/, Serializable {
        private static final long serialVersionUID = -4179466565509314106L;
        /**
         * The node about which this object contains information.
         */
        public final Object/*>S<*/ node;
        /**
         * the f-cost of node: f(node).
         */
        public Real cost;
        public NodeInfo(Object/*>S<*/ node, Real cost) {
            this.node = node;
            this.cost = cost;
        }

        /**
         * @attribute coarser than equals.
         */
        public int compareTo(Object/*>NodeInfo<A,S><*/ o) {
            NodeInfo b = (NodeInfo)o;
            return getFCost().compareTo(b.getFCost());
        }
        public Object/*>S<*/ getNode() {
            return node;
        }
        public Real getFCost() {
            return cost;
        }
        public void setCost(Real newcost) {
            this.cost = newcost;
        }
        public String toString() {
            return getNode() + "\t" + getFCost();
        }
    }

    protected Iterator/*<S>*/ createTraversal(GeneralSearchProblem/*<A,S>*/ problem) {
        //@todo could we transform the recursive algorithm into a traversal iterator?
        throw new AssertionError("should not get called");
    }
    
}// IterativeExpansion
