/**
 * @(#)ModernLogic.java 0.7 1999/01/16 Andre Platzer
 * 
 * Copyright (c) 1999-2002 Andre Platzer. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;
import orbital.logic.imp.ParseException;

import orbital.logic.functor.Functor;
import java.util.Iterator;
import java.util.NoSuchElementException;

import orbital.logic.trs.Variable;

import orbital.logic.functor.*;

import java.io.StringReader;
import orbital.util.Utility;
import orbital.math.MathUtilities;
import orbital.math.Arithmetic;
import orbital.math.Values;

/**
 * A support class for implementing modern logic.
 * <p>
 * Deriving a concrete logic from this class may save some implementation effort.
 * This class uses {@link ModernFormula} and may pass its instance <code class="keyword">this</code>
 * as an argument to the constructor methods like {@link ModernFormula#createSymbol(Logic,Symbol)}.
 * </p>
 * @version 0.8, 1999/01/16
 * @author  Andr&eacute; Platzer
 * @see ModernFormula
 */
abstract class ModernLogic implements Logic {
    /**
     * A complex error offset that is not representable by a locator for ParseException.
     */
    static final int COMPLEX_ERROR_OFFSET = -1;

    // heavy implementation

    public Expression createAtomic(Symbol symbol) {
	Expression RES = createAtomicImpl(symbol);
	assert RES != null : "@post RES != null";	     
	assert RES.getType().equals(symbol.getType()) && (((RES instanceof Variable) && ((Variable)RES).isVariable()) == symbol.isVariable()) : "@post " + RES.getType() + "=" + symbol.getType() + " & (" + ((RES instanceof Variable) && ((Variable)RES).isVariable()) + "<->" + symbol.isVariable() + ") for " + symbol + " = " + RES;
	return RES;
    }

    private Expression createAtomicImpl(Symbol symbol) {
	if (symbol == null)
	    throw new NullPointerException("illegal symbol: " + symbol);
	final String signifier = symbol.getSignifier();
	assert signifier != null;

	// check if it's already predefined in the coreSignature() but perhaps in a different notation and a more general type than parsed
	for (Iterator i = coreSignature().iterator(); i.hasNext(); ) {
	    Object o = i.next();
	    assert o instanceof Symbol : "signature isa Set<" + Symbol.class.getName() + '>';
	    Symbol s = (Symbol) o;
	    //@xxx use s.equals(symbol) instead, also checking notation?
	    if (s.getSignifier().equals(signifier) && s.getType().equals(symbol.getType())) {
		// fixed interpretation of core signature
		final Object ref = coreInterpretation().get(s);
		return createFixedSymbol(s, ref, true);
	    }
	}
	// ordinary (new) symbols
	assert !("true".equals(signifier) || "false".equals(signifier)) : "true and false are in core signature and no ordinary symbols";

	// test for syntactically legal <INTEGER_LITERAL> | <FLOATING_POINT_LITERAL>
	//@todo could also move to an infinite coreInterpretation()
	if (symbol.getType().subtypeOf(Types.type(Arithmetic.class))
	    || symbol.getType().subtypeOf(Types.type(Number.class)))
	    try {
		return createFixedSymbol(symbol, Values.getDefaultInstance().valueOf(signifier), false);
	    }
	    catch (NumberFormatException trial) {}

	// test for syntactically legal <IDENTIFIER> @todo can't we use new LogicParserTokenManager(signifier).getNextToken()?
	for (int i = 0; i < signifier.length(); i++) {
	    char ch = signifier.charAt(i);
	    if ((i > 0 && !(ch == '_' || Character.isLetterOrDigit(ch)))
		|| (i == 0 && !(ch == '_' || Character.isLetter(ch))))
		throw new IllegalArgumentException("illegal character `" + ch + "\' for symbol " + symbol);
	}
	return createSymbol(symbol);
    } 

    public Expression compose(Expression compositor, Expression arguments[]) throws ParseException {
	if (compositor == null)
	    throw new NullPointerException("illegal arguments: compositor " + compositor + " composed with " + MathUtilities.format(arguments));
        if (!Types.isApplicableTo(compositor.getType(), arguments))
	    throw new ParseException("compositor " + compositor + ":" + compositor.getType() + " not applicable to the " + arguments.length + " arguments " + MathUtilities.format(arguments) + ":" + Types.typeOf(arguments), COMPLEX_ERROR_OFFSET);

	Expression RES = composeImpl(compositor, arguments);
	assert RES != null : "@post RES != null";	     
	assert RES.getType().equals(compositor.getType().domain()) : "@post " + RES.getType() + "=" + compositor.getType().domain();
	return RES;
    }
    Expression composeImpl(Expression op, Expression arguments[]) throws ParseException {
	if (!(op instanceof ModernFormula.FixedAtomicSymbol))
	    return composeDelayed((Formula) op,
				  arguments,
				  op instanceof ModernFormula.AtomicSymbol
				  ? ((ModernFormula.AtomicSymbol)op).getSymbol().getNotation().getNotation()
				  : Notation.DEFAULT);
	else {
	    // optimized composition for fixed interpretation compositors
	    ModernFormula.FixedAtomicSymbol opfix = (ModernFormula.FixedAtomicSymbol) op;
	    Functor ref = (Functor) opfix.getReferent();
	    assert ref.toString().equals(opfix.getSymbol().getSignifier()) : "interprets with a functor of the same string representation (functor " + ref + " for symbol " + opfix.getSymbol() + ")";
	    try {
		// core-symbols
		// fixed interpretation of core signature
		Symbol s2 = null;
		assert (s2 = coreSignature().get(ref.toString(), arguments)) != null : "composition functors occur in the signature";
		assert opfix.getSymbol().equals(s2) : "enforce any potential unambiguities of operators";
		return composeFixed(opfix.getSymbol(), (Functor)ref, arguments);
	    }
	    catch (IllegalArgumentException ex) {throw new ParseException(ex.getMessage(), COMPLEX_ERROR_OFFSET);}
	}
    }

    /**
     * @deprecated Use {@link #compose(Expression,Expression[])} instead, converting op via {@link ExpressionBuilder#createAtomic(Symbol)}.
     * @todo remove deprecated
     */
    public Expression compose(Symbol op, Expression arguments[]) throws ParseException {
	if (true)
	    return compose(createAtomic(op), arguments);
	if (op == null)
	    throw new NullPointerException("illegal arguments: operator " + op + " composed with " + MathUtilities.format(arguments));
        if (!Types.isApplicableTo(op.getType(), arguments))
	    throw new ParseException("operator " + op + ":" + op.getType() + " not applicable to the " + arguments.length + " arguments " + MathUtilities.format(arguments) + ":" + Types.typeOf(arguments), ClassicalLogic.COMPLEX_ERROR_OFFSET);
	return composeImpl(op, arguments);
    }
    /**
     * @deprecated Use {@link #compose(Expression,Expression[])} instead, converting op via {@link ExpressionBuilder#createAtomic(Symbol)}.
     * @todo remove deprecated
     */
    Expression composeImpl(Symbol op, Expression arguments[]) throws ParseException {
        Functor f;
        try {
	    f = (Functor) coreInterpretation().get(op);
        }
        catch (NoSuchElementException nocore) {
	    // non-core symbols
	    return composeDelayed((Formula) createSymbol(op), arguments, op.getNotation().getNotation());
        }
        assert f.toString().equals(op) : "get returns the right functor for the string representation";
        try {
	    // core-symbols
	    // fixed interpretation of core signature
	    Symbol op2 = null;
	    assert (op2 = coreSignature().get(f.toString(), arguments)) != null : "composition functors occur in the signature";
	    assert op.equals(op2) : "enforce any potential unambiguities of operators";
	    return composeFixed(op, (Functor)f, arguments);
	    //return composeDelayed((Formula)createFixedSymbol(op, f, true), arguments, op.getNotation().getNotation());
        }
        catch (IllegalArgumentException ex) {throw new ParseException(ex.getMessage(), COMPLEX_ERROR_OFFSET);}
    }

    // delegation helper methods
    
    // base case atomic symbols

    /**
     * Construct (a formula view of) an atomic symbol.
     * @param symbol the symbol for which to create a formula representation
     * @see Logic#createAtomic(Symbol)
     */
    public Formula createSymbol(Symbol symbol) {
	return ModernFormula.createSymbol(this, symbol);
    }
    /**
     * Construct (a formula view of) an atomic symbol with a fixed interpretation.
     * @param symbol the symbol for which to create a formula representation
     * @param referent the fixed interpretation of this symbol
     * @param core whether symbol is in the core such that it does not belong to the proper signature.
     * @see Logic#createAtomic(Symbol)
     */
    public Formula createFixedSymbol(Symbol symbol, Object referent, boolean core) {
	return ModernFormula.createFixedSymbol(this, symbol, referent, core);
    }

    // composition
    
    /**
     * Delayed composition of a symbol with some arguments.
     * Usually for user-defined predicates etc. or predicates subject to interpretation.
     * @param f the compositing formula.
     * @param arguments the arguments to the composition by f.
     * @param notation the notation for the composition (usually determined by the composing symbol).
     */
    public Formula composeDelayed(Formula f, Expression arguments[], Notation notation) {
	return ModernFormula.composeDelayed(this, f, arguments, notation);
    }

    /**
     * Instant composition of functors with a fixed core interperation
     * Usually for predicates etc. subject to fixed core interpretation.
     * @param f the compositing formula.
     * @param arguments the arguments to the composition by f.
     * @param fsymbol the symbol with with the fixed interpretation f.
     */
    public Formula composeFixed(Symbol fsymbol, Functor f, Expression arguments[]) {
	return ModernFormula.composeFixed(this, fsymbol, f, arguments);
    }

    // parsing
    
    // this is an additional method related to createExpression(String)
    public Expression[] createAllExpressions(String expressions) throws ParseException {
	if (expressions == null)
	    throw new NullPointerException("null is not an expression");
	try {
	    LogicParser parser = new LogicParser(new StringReader(expressions));
	    parser.setSyntax(this);
	    Expression B[] = parser.parseFormulas();
	    assert !Utility.containsIdenticalTo(B, null) : "empty string \"\" is not a formula, but only an empty set of formulas.";
	    return B;
	} catch (orbital.moon.logic.ParseException ex) {
	    throw new ParseException(ex.getMessage() + "\nin expressions: " + expressions, ex.currentToken.next.beginLine, ex.currentToken.next.beginColumn, ex);
	}                                                                                                                                      
    }
    public Expression createExpression(String expression) throws ParseException {
	if (expression == null)
	    throw new NullPointerException("null is not an expression");
	try {
	    LogicParser parser = new LogicParser(new StringReader(expression));
	    parser.setSyntax(this);
	    Expression x = parser.parseFormula();
	    if (x == null) {
		assert "".equals(expression) : "only the empty formula \"\" can lead to the forbidden case of a null expression";
		throw new ParseException("empty string \"\" is not a formula", COMPLEX_ERROR_OFFSET);
	    } else
		return x;
	} catch (orbital.moon.logic.ParseException ex) {
	    //@todo use a more verbose exception than ParseException. One that knows about beginning and ending lines and columns, cause and id.
	    throw new ParseException(ex.getMessage() + "\nin expression: " + expression, ex.currentToken.next.beginLine, ex.currentToken.next.beginColumn, ex);
	} 
    }
    
}
