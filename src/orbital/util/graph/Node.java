/*
 * @(#)Node.java 0.8 1998/11/14 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.util.graph;

import java.util.Iterator;

/**
 * This interface encapsulates a general node of a graph.
 * It is provided with a reference to its adjacent neighbours.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public interface Node {

    /**
     * Whether this node is a leaf, i.e. has no children.
     */
    boolean isLeaf();

    /**
     * Get the number of edges from this node.
     */
    int getEdgeCount();

    /**
     * Navigational get-method returning references to all direct neighbours
     * reachable from this node via edges.
     */
    Iterator/*<Node>*/ edges();
	
    //TODO: Node getNeighbourAtEdge(int index);

    // Net-Manipulation

    /**
     * Add an edge to a node. Appending the node to the list of neighbours.
     * @return whether the neighbours changed as a result of the call.
     * @todo change parameter type to Edge.
     */
    boolean add(Node n);

    /**
     * Remove an edge to a node. Removing the node from the list of neighbours.
     * @return whether the neighbours changed as a result of the call.
     */
    boolean remove(Node n);

    // Utilities

    /**
     * Returns the minimum subnode (i.e. the leftmost) subnode in this sub graph.
     * @return Returns the minimum subnode relative to this sub graph.
     * @todo move to a utility class, instead
     */
    Node min() throws UnsupportedOperationException;

    /**
     * Returns the maximum subnode (i.e. the rightmost) subnode in this sub graph.
     * @return Returns the maximum subnode relative to this sub graph.
     */
    Node max() throws UnsupportedOperationException;
}
