/** $Id$
 * @(#)$RCSfile$ 1.1 2003-02-03 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.sign.Signature;
import orbital.logic.imp.Formula;
import orbital.logic.sign.type.Type;
import java.util.Set;
import orbital.logic.functor.Functor;
import orbital.logic.sign.type.Typed;
import orbital.logic.sign.Symbol;
import orbital.logic.sign.Expression;


/**
 * A product/tuple of formulas (or even expressions) that also plays the role of a formula.
 * Most method calls do not (yet) make sense on this object.
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @version-revision $Revision$, $Date$
 */
class ProductFormula extends ProductExpression implements Formula  {
    public ProductFormula(Expression components[]) {
	super(components);
    }
    // implementation of orbital.logic.functor.Functor interface

    public Formula exists(Symbol param1)
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Formula and(Formula param1)
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Formula or(Formula param1)
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Formula xor(Formula param1)
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Set getFreeVariables()
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Set getBoundVariables()
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Set getVariables()
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Object apply(Object param1)
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Formula not()
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Formula impl(Formula param1)
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Formula equiv(Formula param1)
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }

    public Formula forall(Symbol param1)
    {
	// TODO: implement this orbital.logic.imp.Formula method
	return null;
    }
    // implementation of orbital.logic.sign.Expression interface

    public Signature getSignature()
    {
	// TODO: implement this orbital.logic.sign.Expression method
	return null;
    }

}// ProductFormula
