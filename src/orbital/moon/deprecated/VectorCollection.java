/*
 * @(#)VectorCollection.java 0.9 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.setop;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A VectorCollection is a Collection organised as a {@link Vector}.
 * 
 * @deprecated since JDK1.2
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public
class VectorCollection extends Vector implements OCollection {
	public Vector getVector() {
		return (Vector) this;
	} 
}
