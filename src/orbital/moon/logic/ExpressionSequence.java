/**
 * @(#)ExpressionSequence.java 1.1 2002-11-28 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;
import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.logic.sign.type.*;

import orbital.logic.functor.Notation;

/**
 * Encapsulates a whole sequence of expressions into a single compound expression.
 *
 *
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version 1.1, 2002-11-28
 */
class ExpressionSequence implements Expression.Composite {
    //@xxx improve this dummy compositor
    private static final Object SEQUENCE = new Object();
    private final Expression expressions[];
    public ExpressionSequence(Expression expressions[]) {
	this.expressions = expressions;
    }

    public Type getType() {
	return Types.getDefault().list(Types.getDefault().objectType(Expression.class));
    }

    public Signature getSignature() {
	Signature sigma = SignatureBase.EMPTY;
	for (int i = 0; i < expressions.length; i++) {
	    sigma = sigma.union(expressions[i].getSignature());
	}
	return sigma;
    }

    public Object getCompositor() {
	return SEQUENCE;
    }
    public Object getComponent() {
	return expressions;
    }
    public void setCompositor(Object o) {
	throw new UnsupportedOperationException();
    }
    public void setComponent(Object o) {
	throw new UnsupportedOperationException();
    }
    public orbital.logic.Composite construct(Object f, Object g) {
	throw new UnsupportedOperationException();
    }

    public Notation getNotation() {
	throw new UnsupportedOperationException();
    }
    public void setNotation(Notation notation) {
	throw new UnsupportedOperationException();
    }
}// ExpressionSequence
