/*
 * @(#)Graph.java 0.7 2000/11/12 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util.graph;

import java.util.Iterator;

/**
 * An interface for graphs.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
// TODO: enhance a little better
public interface Graph {
    Iterator getRoots();

    boolean addRoot(Node root);

    boolean removeRoot(Node root);
	
    //@todo introduce createNode(), createEdge(Node a, Node b) factory-methods
    Node createNode();
}
