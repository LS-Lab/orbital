/**
 * @(#)UnaryFunction.java 0.9 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

/**
 * This interface is a Functor that encapsulates <code>"r = f(a)"</code>
 * It applies on
 * <ul>
 * <li><b>argument</b> of type <code>Object</code>.
 * <li><b>returns</b> a generic <code>Object</code>.
 * </ul>
 * <b><i>Evolved</i>:</b> The name of the class has changed to Function.
 * 
 * @deprecated Use orbital.logic.functor.Function instead.
 * @structure inherit orbital.logic.functor.Functor
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.Function
 */
public
interface UnaryFunction extends Functor {

	/**
	 * Called to apply the UnaryFunction. <code>f(a)</code>.
	 * 
	 * @param arg generic Object as argument
	 * @return returns a generic Object.
	 */
	Object apply(Object arg);
}
