/*
 * @(#)Collection.java 0.9 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.setop;

import orbital.logic.Comparison;
import java.util.Enumeration;

/**
 * A Collection interface is a generic abstraction of any set of data (Container).
 * 
 * <i>a Collection may organise data in a List, Vector, Array ...</i>
 * 
 * <p>Operations:<ul>
 * <li>sort(Collection, Comparison)
 * </ul>
 * 
 * @deprecated since JDK1.2
 * @version 0.9, 07/06/97
 * @author  Andr&eacute; Platzer
 * @since   Orbital0.7
 * @see java.util.Collection
 */
public
interface OCollection {

	/**
	 * <b>Add</b> an Element to the Collection. The new Element will now be contained
	 * in the Enumeration of all Elements returned by elements().
	 * 
	 * @param element a reference to the Element which should be added.
	 */
	boolean add(Object element);

	/**
	 * <b>Remove</b> an Element from the Collection. The late Element will not be
	 * contained in the Enumeration of all Elements any more.
	 * 
	 * @param element a reference to the Element which should be removed.
	 */
	boolean remove(Object element);

	/**
	 * Get an Enumeration of all Elements currently
	 * contained in a Collection.
	 * 
	 * @return returns an Enumeration of all Elements in the Collection.
	 * @see java.util.Enumeration
	 */
	Enumeration elements();

	/**
	 * Searches for the first occurence of an Object, testing with .equals()
	 * 
	 * @return returns the index of the first occurrence of element in this Collection;
	 * returns -1 if the object is not found.
	 */
	int indexOf(Object element);


	/**
	 * @deprecated since JDK1.2
	 * @since   ORB0.7
	 * @see java.util.Collection
	 */
	static class Operations {

		/**
		 * Sort a Collection with regard to a Comparison between Objects.
		 * Using BubbleSort.
		 */
		public static OCollection sort(OCollection coll, Comparison comp) {
			boolean		inorder;
			OCollection ord_coll;
			do {
				inorder = true;
				ord_coll = new VectorCollection();

				Enumeration e = coll.elements();
				if (!e.hasMoreElements())
					break;
				Object o_el;
				for (o_el = e.nextElement(); e.hasMoreElements(); ) {
					Object el = e.nextElement();

					if (comp.less(el, o_el)) {
						Object t = o_el;	// swap
						o_el = el;
						el = t;
						inorder = false;
					} 
					ord_coll.add(o_el);

					o_el = el;
				} 
				ord_coll.add(o_el);

				coll = ord_coll;
			} while (!inorder);

			return coll;
		} 
	}
}
