/**
 * @(#)DepthFirstBoundingSearch.java 1.0 2000/09/20 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Collection;

import orbital.math.functional.Function;

import orbital.math.functional.Functions;
import java.util.Iterator;
import java.util.LinkedList;

import orbital.math.Values;

/**
 * Abstract bounding search scheme that expands nodes depth first.
 *
 * @structure concretize GeneralBoundingSearch
 * @structure inherit DepthFirstSearch
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see DepthFirstSearch
 * @internal also has a simple recursive formulation.
 * @TODO derive subclass SMA*  (Simple Memory-Bound A*)
 * @todo remove? since the DepthFirst behaviour is encapsuled in DepthFirstSearch.OptionIterator.
 */
abstract class DepthFirstBoundingSearch extends GeneralBoundingSearch {
    private static final long serialVersionUID = -2607359820984135258L;

    //inherit like DepthFirstSearch

    /**
     * O(b*d) where b is the branching factor and d the solution depth.
     */
    public Function spaceComplexity() {
        return Functions.linear(Values.getDefaultInstance().symbol("b"));
    }

    protected Iterator createTraversal(GeneralSearchProblem problem) {
        return new DepthFirstSearch.OptionIterator(problem);
    }

    //  protected Collection createCollection() {
    //          // new Stack();
    //          return new LinkedList();
    //  }
    //
    //    protected GeneralSearchProblem.Option select(Collection nodes) {
    //          Iterator i = nodes.iterator();
    //          GeneralSearchProblem.Option sel = (GeneralSearchProblem.Option) i.next();
    //          i.remove();
    //          return sel;
    //    }
    //
    //    protected Collection add(Collection newNodes, Collection oldNodes) {
    //          newNodes.addAll(oldNodes);
    //          return newNodes;
    //    }
}
