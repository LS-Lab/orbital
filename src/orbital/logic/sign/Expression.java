/**
 * @(#)Expression.java 1.0 2000/03/19 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

/**
 * An interface for presentation of expressions.
 * <p>
 * This interface is implemented by objects representing syntactic expressions like those created by
 * {@link ExpressionSyntax#createAtomic(Symbol)}, or {@link ExpressionSyntax#createExpression(String)}.
 * </p>
 * 
 * @version 1.0, 2000/03/19
 * @author  Andr&eacute; Platzer
 * @see ExpressionSyntax
 * @see ExpressionSyntax#createAtomic(Symbol)
 * @see ExpressionSyntax#createExpression(String)
 */
public interface Expression {
    /**
     * Get the subsignature appearing in this expression.
     * @return the subset of &Sigma; consisting of those symbols that occur in this expression.
     */
    Signature getSignature();
}
