/**
 * @(#)MathExpressionSyntax.java 1.0 2002-10-17 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

package orbital.moon.logic;

import orbital.math.*;
import orbital.math.functional.*;
import orbital.logic.imp.*;
import orbital.logic.imp.Symbol;
import orbital.logic.imp.ParseException;
import orbital.logic.functor.Functor;
import orbital.logic.functor.Notation;

import orbital.logic.trs.Substitutions;
import java.lang.reflect.Field;

/**
 * This class implements an expression syntax for mathematical expressions.
 * Those expressions currently get parsed as a composition of functions.
 * An alternative implementation would already evaluate the expression instead of explicitly
 * constructing a representation as a composition of functions.
 * @version 1.1, 2002-10-17
 * @author  Andr&eacute; Platzer
 * @structure depends orbital.logic.functor.Notation Uses registered notation functors reverse.
 */
public class MathExpressionSyntax implements ExpressionBuilder {
    private static final Values valueFactory = Values.getDefaultInstance();
	
    public Expression createAtomic(Symbol symbol) {
	Type cod = symbol.getType().codomain();
	Type doc = symbol.getType().domain();
	assert cod != Types.ABSURD;
	if (cod.equals(Types.VOID))
	    if (doc.equals(Types.INDIVIDUAL))
		return new MathExpression(valueFactory.symbol(symbol.getSignifier()), symbol.getType());
	    else
		return new MathExpression(valueFactory.valueOf(symbol.getSignifier()), symbol.getType());
	else {
	    Functor referee = null;
	    if (cod.subtypeOf(Types.product(new Type[] {Types.UNIVERSAL, Types.UNIVERSAL})))
		// binary 
		//@internal use any arguments of the right arity (since Notation.functorOf does not check for more)
		referee = Notation.functorOf(symbol.getSignifier(), new Object[2]);
	    else if (cod.subtypeOf(Types.product(new Type[] {Types.UNIVERSAL})))
		//@xxx this test does not really work since cod.subtypeOf(UNIVERSAL) is always true. So it is true for all arities (except binary above).
		// unary
		referee = Notation.functorOf(symbol.getSignifier(), new Object[1]);
	    if (referee == null)
		referee = findFunction(symbol.getSignifier());
	    return new MathExpression(referee, symbol.getType());
	}
    }
    
    public Expression compose(Expression compositor, Expression arguments[]) throws ParseException {
	MathExpression op = (MathExpression)compositor;
	if (!Types.isApplicableTo(compositor.getType(), arguments))
	    throw new ParseException("compositor " + compositor + ":" + compositor.getType() + " not applicable to the " + arguments.length + " arguments " + MathUtilities.format(arguments) + ":" + Types.typeOf(arguments), ModernLogic.COMPLEX_ERROR_OFFSET);
	MathFunctor f = (MathFunctor) op.getValue();
	Object[] arg = new Object[arguments.length];
	for (int i = 0; i < arg.length; i++)
	    arg[i] = ((MathExpression)arguments[i]).getValue();
	return new MathExpression(apply(f, arg), op.getType().domain());
    }

    static final Object getValueOf(Expression x) {
	return ((MathExpression)x).getValue();
    }

    /**
     * Find the function for a function call.
     * <p>
     * Will lookup known functions in {@link orbital.math.functional.Functions}.
     * </p>
     */
    private static MathFunctor findFunction(String fname) throws IllegalArgumentException {
	final String f = fname;
	try {
	    final Field referee = Functions.class.getField(f);
	    return (MathFunctor) referee.get(null);
	} catch(NoSuchFieldException notdefined) {
	    throw new IllegalArgumentException("Function '" + f + "' not defined");
	} catch(IllegalAccessException ex) {
	    throw (InternalError) new InternalError(ex.getMessage()).initCause(ex);
	} catch(SecurityException ex) {
	    throw (InternalError) new InternalError(ex.getMessage()).initCause(ex);
	}
    }

    /**
     * function application.
     * <p>
     * Will compose the function with its arguments in order to represent the function
     * application parsed now.
     * </p>
     * @note a functor is returned since the function application is not evaluated, but
     *  only <em>parsed</em>.
     *  This method results in a composition of function and arguments instead of
     *  the value of the function at the specified arguments.
     * @todo perhaps weaken to Functor for lambda expressions?
     */
    private static MathFunctor apply(MathFunctor f, Object[] arg) throws IllegalArgumentException {
	switch(arg.length) {
	case 1:
	    if (f instanceof Function)
		return Functionals.genericCompose((Function) f, arg[0]);
	    else
		throw new IllegalStateException("Function '" + f + "/" + arg.length + "' not defined as unary");
	case 2: {
	    if (f instanceof BinaryFunction)
		return Functionals.genericCompose((BinaryFunction) f, arg[0], arg[1]);
	    else
		throw new IllegalStateException("Function '" + f + "/" + arg.length + "' not defined as binary");
	}
	default:
	    throw new IllegalArgumentException("Calling a function '" + f  + "/" + arg.length + "' is not supported. Only 1 or two arguments can be used, yet");
	}
    }

    private static class MathExpression implements Expression {
	private Object referee;
	private Type type;
	public MathExpression(Object referee, Type type) {
	    this.referee = referee;
	    this.type = type;
	}
	
	public Object getValue() {
	    return referee;
	}

	public Type getType() {
	    return type;
	}

	public Signature getSignature() {
	    throw new UnsupportedOperationException("not yet implemented");
	}

	public String toString() {
	    return referee.toString();
	}
    }
}
