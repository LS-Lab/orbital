/**
 * @(#)Resolution.java 0.9 2001/07/30 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;

import orbital.logic.functor.*;

/**
 * A symbol that is distinct from all others.
 * DistinctSymbols will only be equal to themselves (compared with ==).
 * Note that the signifier IDs are used for printing, and for comparison
 * of Symbols with DistinctSymbols, but for comparisons
 * of DistinctSymbols with Symbols (then that's achieved by reference comparison).
 * @invariant (this.equals(b) &hArr; this == b) &and; this.hashCode() == System.identityHashCode(this)
 */
final class DistinctSymbol extends SymbolBase {
    /**
     * the next ID for distinct symbols.
     */
    private static /*transient*/ int NEXT_ID = 10;
	
    public DistinctSymbol(String signifierPrefix, Functor.Specification spec, Notation.NotationSpecification notation, boolean variable) {
	super(signifierPrefix + (NEXT_ID++), spec, notation, variable);
    }
    public DistinctSymbol(Functor.Specification spec, Notation.NotationSpecification notation, boolean variable) {
	this("_x", spec, notation, variable);
    }
    public final boolean equals(Object o) {
	assert this == o || !getSignifier().equals(((Symbol)o).getSignifier()) : "all other instances of Symbol are different from this DistinctVariableSymbol, so no other variable should pretend to use our signifier";
	// we "guarantee" that two different instances of DistinctVariableSymbol are distinct, and that instances of DistinctVariableSymbols and of other Symbols are different anyway
	return this == o;
    }
    public final int hashCode() {
	// we "guarantee" that two different instances of DistinctVariableSymbol are distinct, and that instances of DistinctVariableSymbols and of other Symbols are different anyway
	return System.identityHashCode(this);
    }
    public void setSignifier(String signifier) {
	throw new UnsupportedOperationException("never try to rename " + getClass().getName() + 's');
    }
    public void setSpecification(Functor.Specification spec) {
	throw new UnsupportedOperationException();
    }
    public void setNotation(Notation.NotationSpecification notation) {
	throw new UnsupportedOperationException();
    }
}
