/**
 * @(#)Operable.java 0.9 1999/07/24 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.util.Callback;

/**
 * Operable is an interface used to tag operable objects.<p>
 * Operable objects are those with a public interface containing
 * methods for all junctors defined over these objects.
 * In other words, these objects specify operations that operate
 * on two objects of the same kind, returning a third of this kind.
 * All operable objects <code>T</code> contain one or more methods of the signature:<ul>
 * <code><i>T operation</i>(<i>T</i>, <i>T</i>) throws ArithmeticException
 * </ul>
 * 
 * @version 0.9, 1999/08/16
 * @author  Andr&eacute; Platzer
 * @see orbital.util.Callback
 * @deprecated Use orbital.util.Callback instead.
 */
public
abstract interface Operable {}
