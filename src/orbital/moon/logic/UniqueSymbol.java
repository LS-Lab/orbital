/**
 * @(#)UniqueSymbol.java 0.9 2001/07/30 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;

import orbital.logic.functor.*;

/**
 * A new unique symbol that is distinct from all others.
 * UniqueSymbols will only be equal to themselves (compared with ==).
 * Note that the signifier IDs are used for printing, and for comparison
 * of Symbols with UniqueSymbols, but for comparisons
 * of UniqueSymbols with Symbols (then that's achieved by reference comparison).
 * @invariants (this.equals(b) &hArr; this == b) &and; this.hashCode() == System.identityHashCode(this)
 */
final class UniqueSymbol extends SymbolBase {
    private static final long serialVersionUID = 6846367078565847459L;
    /**
     * the next ID for distinct symbols.
     * @internal volatile does not ensure that ++ is atomic.
     */
    private static /*transient*/ volatile int NEXT_ID = 10;

    /**
     * Create a new unique symbol of (display) signifier starting with signifierPrefix.
     */
    public UniqueSymbol(String signifierPrefix, Type type, Notation.NotationSpecification notation, boolean variable) {
	super(signifierPrefix + (NEXT_ID++), type, notation, variable);
    }
    public UniqueSymbol(Type type, Notation.NotationSpecification notation, boolean variable) {
	this(variable ? "_X" : "_x", type, notation, variable);
    }

    public final boolean equals(Object o) {
	assert this == o || !(o instanceof Symbol) || !getSignifier().equals(((Symbol)o).getSignifier()) : "all other instances of Symbol are different from this DistinctVariableSymbol, so no other variable should pretend to use our signifier";
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
