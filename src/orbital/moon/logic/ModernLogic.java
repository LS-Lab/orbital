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
		return ModernFormula.createFixedSymbol(s, coreInterpretation().get(s), true);
	}
	// ordinary (new) symbols
	assert !("true".equals(signifier) || "false".equals(signifier)) : "true and false are in core signature and no ordinary symbols";

	// test for syntactically legal <INTEGER_LITERAL> | <FLOATING_POINT_LITERAL>
	//@todo could also move to an infinite coreInterpretation()
	try {
	    return ModernFormula.createFixedSymbol(symbol, Double.valueOf(signifier), false);
	}
	catch (NumberFormatException trial) {}

	// test for syntactically legal <IDENTIFIER> @todo can't we use new LogicParserTokenManager(signifier).getNextToken()?
	for (int i = 0; i < signifier.length(); i++) {
	    char ch = signifier.charAt(i);
	    if ((i > 0 && !(ch == '_' || Character.isLetterOrDigit(ch)))
		|| (i == 0 && !(ch == '_' || Character.isLetter(ch))))
		throw new IllegalArgumentException("illegal character `" + ch + "\' for symbol " + symbol);
	}
	return ModernFormula.createSymbol(symbol);
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
	    return ModernFormula.composeDelayed((Formula) ModernFormula.createSymbol(op), op, arguments);
        }
        assert f.toString().equals(op) : "get returns the right functor for the string representation";
        try {
	    // core-symbols
	    // fixed interpretation of core signature
	    Symbol op2;
	    assert (op2 = coreSignature().get(f.toString(), arguments)) != null : "composition functors occur in the signature";
	    assert op.equals(op2) : "enforce any potential unambiguities of operators";
	    return ModernFormula.composeFixed((Functor)f, op, arguments);
	    //return composeDelayed((Formula)createFixedSymbol(op, f, true), op, arguments);
        }
        catch (IllegalArgumentException ex) {throw new java.text.ParseException(ex.getMessage(), COMPLEX_ERROR_OFFSET);}
    }
   
}
