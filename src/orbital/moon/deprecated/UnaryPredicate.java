/**
 * @(#)UnaryPredicate.java 0.9 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

/**
 * This interface is a Functor that encapsulates <code>"P(a)"</code>.
 * It applies on
 * <ul>
 * <li><b>argument</b> of type <code>Object</code>.
 * <li><b>returns</b> a <code>boolean</code> value.
 * </ul>
 * <b><i>Evolved</i>:</b> The name of the class has changed to Predicate.
 * 
 * @deprecated Use orbital.logic.functor.Predicate instead.
 * @structure inherit orbital.logic.functor.Functor
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.Predicate
 */
public
interface UnaryPredicate extends Functor {

        /**
         * Called to apply the UnaryPredicate. <code>P(a)</code>.
         * 
         * @param arg single Object argument
         * @return a boolean.
         */
        boolean apply(Object arg);
}
