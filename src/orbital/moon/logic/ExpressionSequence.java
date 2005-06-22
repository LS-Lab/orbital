/**
 * @(#)ExpressionSequence.java 1.1 2002-11-28 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;
import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.logic.sign.type.*;

import orbital.logic.sign.concrete.Notation;
import orbital.logic.functor.Functionals;
import orbital.logic.functor.Function;
import java.util.Arrays;
import orbital.math.MathUtilities;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Encapsulates a whole sequence of expressions into a single compound expression.
 *
 *
 * @author Andr&eacute; Platzer
 * @version 1.1, 2002-11-28
 */
class ExpressionSequence implements Expression.Composite {
    private static final Logger logger = Logger.getLogger(ExpressionSequence.class.getName());

    //@xxx improve this dummy compositor
    private static final Object SEQUENCE = new Object() {
	    public String toString() {
		return "ExpressionSequence";
	    }
	};
    private final Expression expressions[];
    public ExpressionSequence(Expression expressions[]) {
	this.expressions = expressions;
    }

    public Type getType() {
	//@xxx shouldn't we return supremum type of expressions[i].getType()?
	//return Types.getDefault().list(Types.getDefault().objectType(Expression.class));
	Type t = Types.getDefault().list((Type)Types.getDefault().sup().apply(
	    Functionals.listable(
		new Function() {
		    public Object apply(Object o) {
			return ((Typed)o).getType();
		    }
		}).apply(Arrays.asList(expressions))
		//@internal see Functionals#map(Function,Object[]) ArrayStoreException
		));
	if (logger.isLoggable(Level.FINEST)) {
	    logger.log(Level.FINEST, "expression sequence {0} has type {1}", new Object[] {Types.toTypedString(expressions), t});
	}
	return t;
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

    public String toString() {
	return "<list " + MathUtilities.format(expressions) + ">";
    }
}// ExpressionSequence
