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

import orbital.logic.functor.Functor;
import java.util.Iterator;
import java.util.NoSuchElementException;
import orbital.math.MathUtilities;


import orbital.logic.functor.Functor.Composite;
import orbital.logic.functor.*;
import orbital.logic.functor.Predicates;

import java.text.ParseException;

import java.util.Set;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

import java.util.HashSet;
import java.util.IdentityHashMap;

import java.util.Collections;
import orbital.util.Setops;
import orbital.util.Utility;

/**
 * A support class for implementing modern logic.
 * <p>
 * Deriving a concrete logic from this class may save some implementation effort.
 * This class uses {@link ModernFormula} and may pass its instance <code class="keyword">this</code>
 * as an argument to the constructor methods like {@link ModernFormula#symbol(Symbol)}.
 * </p>
 * @version 0.8, 1999/01/16
 * @author  Andr&eacute; Platzer
 * @see ModernFormula
 */
abstract class ModernLogic implements Logic {
    /**
     * A complex error offset consisting of begin line:column to end line:column that is
     * not representable by an int for java.text.ParseException.
     */
    static final int COMPLEX_ERROR_OFFSET = -1;

    // heavy implementation

    public Expression createAtomic(Symbol symbol) {
	final String signifier = symbol.getSignifier();
	assert signifier != null;

	// check if it's already predefined in the coreSignature() but perhaps in a different notation and spec than parsed
	for (Iterator i = coreSignature().iterator(); i.hasNext(); ) {
	    Object o = i.next();
	    assert o instanceof Symbol : "signature isa Set<" + Symbol.class.getName() + '>';
	    Symbol s = (Symbol) o;
	    if (s.getSignifier().equals(signifier))
		//@todo should we check for compatibility of symbol and s so as to detect misunderstandings during parse?
		// fixed interpretation of core signature
		return createFixedSymbol(s, coreInterpretation().get(s), true);
	}
	// ordinary (new) symbols
	assert !("true".equals(signifier) || "false".equals(signifier)) : "true and false are in core signature and no ordinary symbols";

	// test for syntactically legal <INTEGER_LITERAL> | <FLOATING_POINT_LITERAL>
	//@todo could also move to an infinite coreInterpretation()
	try {
	    return createFixedSymbol(symbol, Double.valueOf(signifier), false);
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

    public Expression compose(Symbol op, Expression arguments[]) throws java.text.ParseException {
	if (op == null)
	    throw new NullPointerException("illegal arguments " + op + " composed with " + MathUtilities.format(arguments));
        if (!op.isCompatible(arguments))
	    throw new java.text.ParseException("operator " + op + " not applicable to " + arguments.length + " arguments " + MathUtilities.format(arguments), ClassicalLogic.COMPLEX_ERROR_OFFSET);

        Functor f;
        try {
	    f = (Functor) coreInterpretation().get(op);
        }
        catch (NoSuchElementException nocore) {
	    // non-core symbols
	    return composeDelayed((Formula) createSymbol(op), op, arguments);
        }
        assert f.toString().equals(op) : "get returns the right functor for the string representation";
        try {
	    // core-symbols
	    // fixed interpretation of core signature
	    Symbol op2;
	    assert (op2 = coreSignature().get(f.toString(), arguments)) != null : "composition functors occur in the signature";
	    assert op.equals(op2) : "enforce any potential unambiguities of operators";
	    return composeFixed((Functor)f, op, arguments);
	    //return composeDelayed((Formula)createFixedSymbol(op, f, true), op, arguments);
        }
        catch (IllegalArgumentException ex) {throw new java.text.ParseException(ex.getMessage(), COMPLEX_ERROR_OFFSET);}
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
     * @param f the formula really performing the (outer part of the) composition by op.
     * @param op the (outer part of the) composing symbol.
     * @param arguments the arguments to the composition by op.
     */
    public Formula composeDelayed(Formula f, Symbol op, Expression arguments[]) {
	return ModernFormula.composeDelayed(this, f, op, arguments);
    }

    /**
     * Instant composition of functors with a fixed core interperation
     * Usually for predicates etc. subject to fixed core interpretation.
     * @param f the functor really performing the (outer part of the) composition by op.
     * @param op the (outer part of the) composing symbol.
     * @param arguments the arguments to the composition by op.
     */
    public Formula composeFixed(Functor f, Symbol op, Expression arguments[]) {
	return ModernFormula.composeFixed(this, f, op, arguments);
    }
}
