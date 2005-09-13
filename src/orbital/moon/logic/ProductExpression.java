/**
 * @(#)ProductExpression.java 1.2 2003-11-15 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
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
 * Encapsulates a product/tuple of expressions forming a single compound expression.
 *
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 */
class ProductExpression implements Expression.Composite {
    private static final Logger logger = Logger.getLogger(ProductExpression.class.getName());

    //@xxx improve this dummy compositor
    private static final Object PRODUCT = new Object() {
            public String toString() {
                return "ProductExpression";
            }
        };
    private final Expression expressions[];
    public ProductExpression(Expression expressions[]) {
        this.expressions = expressions;
    }

    public Type getType() {
        Type t = Types.typeOf(expressions);
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "product expression {0} has type {1}", new Object[] {Types.toTypedString(expressions), t});
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
        return PRODUCT;
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
        return "[prod " + MathUtilities.format(expressions) + "]";
    }
}// ProductExpression
