/*
 * @(#)UniqueVector.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Vector;

/**
 * This class unifies a Vector with unique Elements.
 * 
 * @deprecated since ORBITAL1.0 use unique collections like sets instead.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see java.util.Set
 */
public
class UniqueVector extends Vector {
	public UniqueVector() {}

	public final synchronized void addUniqueElement(Object obj) {
		if (contains(obj))
			return;
		addElement(obj);
	} 

	public final synchronized void insertUniqueElementAt(Object obj, int index) {
		if (contains(obj))
			return;
		insertElementAt(obj, index);
	} 
}
