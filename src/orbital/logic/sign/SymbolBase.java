/**
 * @(#)SymbolBase.java 1.0 2001/04/07 Andre Platzer
 *
 * Copyright (c) 2001-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.functor.Notation.NotationSpecification;
import java.io.Serializable;

import orbital.logic.functor.Notation;

import orbital.util.Utility;
import orbital.logic.imp.Symbol;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Default representation of a syntactic symbol in a signature.
 * A (syntactic) symbol is a triple &lang;name, spec, notation&rang;
 * consisting of a name, its (arity and) type specification, and its notation.
 *
 * @stereotype &laquo;Structure&raquo;
 * @structure implements Symbol
 * @structure implements java.io.Serializable
 * @version 1.0, 2001/04/07
 * @author  Andr&eacute; Platzer
 * @internal this is not the most beautiful name, but InterpretationBase and SignatureBase are much more suggestive than DefaultInterpretation and DefaultSignature which were really the wrong names from a logical point of view.
 */
public class SymbolBase implements Symbol, Serializable {
    private static final long serialVersionUID = 4003299661212808663L;
    // convenience constants for constructor calls
    /**
     * A type specification for logical atoms of type <span class="keyword">boolean</span> and arity <span class="number">0</span>.
     * <p>
     * convenience constant for constructor calls.
     * </p>
     * @deprecated Since Orbital1.1 use {@link Types#TRUTH} instead.
     */
    public static final Type BOOLEAN_ATOM = Types.TRUTH;
    /**
     * A type specification for logical atomic terms (object variables and object constants) of universal type <span class="Class">Object</span> and arity <span class="number">0</span>.
     * However be aware that this is not <em>the</em> universal type, but only the type
     * of terms of arity <span class="number">0</span> mapping into object.
     * <p>
     * convenience constant for constructor calls.
     * </p>
     * @deprecated Since Orbital1.1 use {@link Types#INDIVIDUAL} instead.
     */
    public static final Type UNIVERSAL_ATOM = Types.INDIVIDUAL;

    // properties

    /**
     * The String representing this symbol.
     * @serial
     */
    private String signifier;
    /**
     * The (arity and) type specification of this symbol.
     * @serial
     */
    private Type type;
    /**
     * The notation used when this symbol occurs.
     * This includes precedence and associativity information, as well.
     * @serial
     */
    private NotationSpecification notation;
    /**
     * Whether this is a variable symbol.
     * <code>true</code> if this symbol is a variable symbol,
     * and <code>false</code> if this symbol is a constant symbol.    
     * @serial
     */
    private final boolean variable;

    /**
     * Construct a symbol with a signifier, type specification, and notation.
     * @param signifier the string representation of this symbol.
     * @param type the type specification of this symbol.
     * @param notation The notation used when this symbol occurs.
     *  This includes precedence and associativity information, as well.
     *  May be <code>null</code> for symbols with arity 0,
     *  which will be converted to the {@link Notation.NotationSpecification#Notation.NotationSpecification(int) default notation specification}, then.
     * @param variable whether this is a variable symbol.
     *  <code>true</code> if this symbol is a variable symbol,
     *  and <code>false</code> if this symbol is a constant symbol.
     * @todo could check whether type and notation match in arity
     */
    public SymbolBase(String signifier, Type type, NotationSpecification notation, boolean variable) {
        this.signifier = signifier;
        this.setType(type);
        this.notation = notation != null ? notation : new NotationSpecification(Types.arityOf(type.domain()));
        this.variable = variable;
    }

    // constructors for convenience

    /**
     * Construct a constant symbol with a signifier, type specification, and notation.
     * @param signifier the string representation of this symbol.
     * @param type the type specification of this symbol.
     * @param notation The notation used when this symbol occurs.
     *  This includes precedence and associativity information, as well.
     *  May be <code>null</code> for symbols with arity 0,
     *  which will be converted to the {@link Notation.NotationSpecification#Notation.NotationSpecification(int) default notation specification}, then.
     * @post &not;isVariable()
     */
    public SymbolBase(String signifier, Type type, NotationSpecification notation) {
        this(signifier, type, notation, false);
    }

    /**
     * Construct a constant symbol with a signifier, and type specification.
     * The notation is chosen to be the {@link Notation.NotationSpecification#Notation.NotationSpecification(int) default notation specification}.
     * @param signifier the string representation of this symbol.
     * @param type the type specification of this symbol.
     * @post &not;isVariable()
     */
    public SymbolBase(String signifier, Type type) {
        this(signifier, type, null);
    }
    
    public boolean equals(Object o) {
    	if (o instanceof Symbol) {
	    Symbol b = (Symbol) o;
	    if (Utility.equals(getSignifier(), b.getSignifier())
		&& Utility.equals(getType(), b.getType())
		&& Utility.equals(getNotation(), b.getNotation())) {
		assert isVariable() == b.isVariable() : "same symbols (" + this + " (" + (isVariable() ? "variable" : "constant") + ") and " + b  + " (" + (isVariable() ? "variable" : "constant") + ") ) are consistently either both variable or both constant.";
		return true;
	    } else
		return false;
    	}
    	return false;
    }
    
    /**
     * Compares two symbols.
     * <p>
     * This implementation compares for notation (precedence) in favor of type (lexicographical) in favor of symbol name.
     * </p>
     * @post only <em>semi</em>-consistent with equals (since Notation is)
     */
    public int compareTo(Object o) {
	Symbol b = (Symbol) o;
	int a;
	a = Utility.compare(getNotation(), b.getNotation());
	if (a != 0)
	    return a;
	a = Types.LEXICOGRAPHIC.compare(getType(), b.getType());
	return a != 0 ? a : Utility.compare(getSignifier(), b.getSignifier());
    } 

    public int hashCode() {
    	return Utility.hashCode(getSignifier()) ^ Utility.hashCode(getType()) ^ Utility.hashCode(getNotation());
    }
    
    // get/set properties
    public String getSignifier() {
    	return signifier;
    }
    public void setSignifier(String signifier) {
    	this.signifier = signifier;
    }
    public Type getType() {
    	return type;
    }
    public void setType(Type type) {
    	if (type == null)
	    throw new IllegalArgumentException("invalid type specification: " + type + " for " + getSignifier());
    	this.type = type;
    }
    public NotationSpecification getNotation() {
    	return notation;
    }
    public void setNotation(NotationSpecification notation) {
    	this.notation = notation;
    }
    
    public boolean isVariable() {
	return variable;
    }
    
    public String toString() {
	if (Logger.global.isLoggable(Level.FINEST)
	    || getType().equals(Types.TYPE))
	    return toFullString();
	return toShortString();
    }

    private String toFullString() {
	return getSignifier() + ':' + getType();
    }
    private String toShortString() {
	Type type = getType();
    	// short representation
    	return type.equals(Types.TRUTH)
	    ? getSignifier()
	    : (getSignifier() + '/' + Types.arityOf(type.domain()));
    }
}
