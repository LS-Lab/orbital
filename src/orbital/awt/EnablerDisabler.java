/**
 * @(#)EnablerDisabler.java 0.9 1999/03/29 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import orbital.logic.functor.Predicate;
import orbital.util.DelegateCollection;
import java.util.Collection;
import java.util.Iterator;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

/**
 * Automatically enables or disables the objects registered in its corresponding list when applied.
 * These objects might especially be Components or MenuComponents.
 * <p>
 * With an EnablerDisabler, automation of updates can be promoted throughout a
 * GUI when data is opened or closed by the user.
 * 
 * @version 0.9, 1999/03/29
 * @author  Andr&eacute; Platzer
 * @see java.util.Observable
 */
public
class EnablerDisabler extends DelegateCollection implements Predicate {
	public EnablerDisabler() {
		super(new ArrayList());
	}

	/**
	 * @param enable <code>Boolean.TRUE</code> to enable all registered objects via <code>setEnabled(true)</code>.
	 * <code>Boolean.FALSE</code> to disable all registered objects via <code>setEnabled(false)</code>
	 */
	public boolean apply(Object enable) {
		Boolean[] b = {
			(Boolean) enable
		};
		for (Iterator i = iterator(); i.hasNext(); )
			try {
				Object o = i.next();
				Method m = o.getClass().getMethod("setEnabled", new Class[] {
					Boolean.TYPE
				});
				m.invoke(o, b);
			} catch (NoSuchMethodException x) {
				throw new IllegalStateException("Illegal object in list, it does not contain a method with a signature like setEnabled(boolean): " + x);
			} catch (InvocationTargetException x) {
				throw new IllegalStateException("Illegal object in list: " + x.getTargetException());
			} catch (IllegalAccessException x) {
				throw new IllegalStateException("IllegalAccess to object in list: " + x);
			} 
		return true;
	} 

}
