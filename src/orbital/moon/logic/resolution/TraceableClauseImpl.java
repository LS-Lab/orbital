/** $Id$
 * @(#)$RCSfile$ 1.1 2003-11-10 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import orbital.logic.sign.*;
import orbital.logic.imp.Formula;
import java.util.*;
import dkfz.collections.graph.Vertex;
import dkfz.collections.graph.Edge;
import dkfz.collections.graph.Graph;
import dkfz.collections.graph.GraphFactory;
import dkfz.collections.graph.*;

import java.awt.*;
import orbital.awt.*;
import dkfz.collections.graph.view.*;

/**
 * Clauses that trace the proof for obtaining a proof DAG.
 *
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @version-revision $Revision$, $Date$
 */
public class TraceableClauseImpl extends ClauseImpl {
    private static Graph proofGraph = new GraphImpl();
    private static Vertex proofDAGRoot = null;
    private volatile int inferenceCount = 1;
    // the vertex that we correspond to in the proof graph
    private Vertex corresponding;

    //@xxx premature functionality and method
    public static void doShow() {
        Frame f = new Frame("Proof Graph");
        new Closer(f, true, true);
        ViewGraph view = new ViewGraph();
        view.setEdgePainter(new StraightLinePainter());
        view.setVertexPainter(new StraightLinePainter());
        //@todo reduce proofGraph to the relevant part, i.e. reachability closure of the resolvent CONTRADICTION
        Graph proofDAG = reachability(proofGraph, proofDAGRoot);
        view.setModel(proofDAG);
        f.add(view);
        f.setLocation(200, 100);
        f.setSize(400, 400);
        view.registerController(new GraphController(view,
                                                    new Class[] {TraceableClauseImpl.class},
                                                    new String[] {"Clause"},
                                                    new Class[] {Integer.class},
                                                    new String[] {"Proofstep"}));
        new RandomNetLayouter().layout(view);
        f.setVisible(true);
    }

    /**
     * Return a DAG containing all vertices reachable via edges from root.
     */
    private static Graph reachability(Graph graph, Vertex root) {
        Graph reachable = new GraphImpl();
        reachable.add(root);
        reachabilityHelper(graph, root, reachable);
        return reachable;
    }
    private static void reachabilityHelper(Graph graph, Vertex root, Graph reachable) {
        for (Iterator successors = graph.getToEdges(root).iterator();
             successors.hasNext(); ) {
            Edge e = (Edge) successors.next();
            if (!reachable.getVertices().contains(e.getFromVertex())) {
                reachable.add(e);
                reachabilityHelper(graph, e.getFromVertex(), reachable);
            } else {
                reachable.add(e);
            }
        }
    }
        
    
    public TraceableClauseImpl(Set/*<Formula>*/ literals) {
        super(literals);
        corresponding = proofGraph.getGraphFactory().createVertex(this);
    }
    public TraceableClauseImpl() {}
    
    // factory-methods

    protected Clause newInstance() {
        return new TraceableClauseImpl();
    }

    protected ClauseImpl construct(Set literals) {
        return new TraceableClauseImpl(literals);
    }

    // add traces

    public Iterator/*<Clause>*/ resolveWith(Clause G) {
        final GraphFactory graphFactory = proofGraph.getGraphFactory();
        final TraceableClauseImpl G2 = (TraceableClauseImpl) G;
        final LinkedList r = new LinkedList();
        for (Iterator resolvents = super.resolveWith(G);
             resolvents.hasNext(); ) {
            TraceableClauseImpl resolvent = (TraceableClauseImpl) resolvents.next();
            ///proofGraph.add(resolvent.corresponding);
            final int count = inferenceCount++;
            proofGraph.add(graphFactory.createEdge(this.corresponding, resolvent.corresponding, new Integer(count)));
            proofGraph.add(graphFactory.createEdge(G2.corresponding, resolvent.corresponding, new Integer(count)));
            r.add(resolvent);
            if (resolvent.equals(Clause.CONTRADICTION)) {
                System.out.println("Resolved a contradiction");
                TraceableClauseImpl.proofDAGRoot = resolvent.corresponding;
                doShow();
            }
        }
        return r.iterator();
    }

    /*@todo factorization
    public Clause factorize() {
        final GraphFactory graphFactory = proofGraph.getGraphFactory();
        final TraceableClauseImpl factor = (TraceableClauseImpl) super.factorize();
        ///proofGraph.add(factor.corresponding);
        if (!factor.equals(this)) {
            // no trivial factorization edges of no new results, please
            proofGraph.add(graphFactory.createEdge(this.corresponding, factor.corresponding, new Integer(-(inferenceCount++))));
        }
        return factor;
        }*/

    // preserve traces

    public Clause variant(Signature disjointify) {
        final TraceableClauseImpl variant = (TraceableClauseImpl) super.variant(disjointify);
        variant.corresponding = this.corresponding;
        return variant;
    }
    
}// TraceableClauseImpl
