/**
 * @(#)SymbolBase.java 1.0 2001/04/07 Andre Platzer
 *
 * Copyright (c) 2001-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.functor.Notation.NotationSpecification;
import orbital.logic.functor.Functor.Specification;
import java.io.Serializable;

import orbital.logic.functor.Notation;

import orbital.util.Utility;
import orbital.logic.imp.Symbol;
import orbital.logic.functor.Notation$NotationSpecification;
import orbital.logic.functor.Functor$Specification;
import orbital.logic.imp.Symbol;
import orbital.logic.functor.Notation$NotationSpecification;
import orbital.logic.functor.Functor$Specification;
import orbital.logic.imp.Symbol;
import orbital.logic.functor.Notation$NotationSpecification;
import orbital.logic.functor.Functor$Specification;

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
     * @todo sure?
     */
    public static final Specification BOOLEAN_ATOM = new Specification(0, Boolean.class);
    /**
     * A type specification for logical atomic terms (object variables and object constants) of universal type <span class="Class">Object</span> and arity <span class="number">0</span>.
     * The universal type &#8868; which has no differentiae satisfies
     * <ul>
     *   <li>(&exist;x) &#8868;(x)</li>
     *   <li>(&forall;x) &#8868;(x)</li>
     *   <li>(&forall;t:Type) t&le;&#8868;</li>
     * </ul>
     * However be aware that this is not <em>the</em> universal type, but only the type
     * of terms of arity <span class="number">0</span> mapping into the universal type.
     * <p>
     * convenience constant for constructor calls.
     * </p>
     */
    public static final Specification UNIVERSAL_ATOM = new Specification(0, Object.class);

    // properties

    /**
     * The String representing this symbol.
     * @serial
     */
    private String					signifier;
    /**
     * The (arity and) type specification of this symbol.
     * @serial
     */
    private Specification			type;
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
    private final boolean			variable;

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
    public SymbolBase(String signifier, Specification type, NotationSpecification notation, boolean variable) {
        this.signifier = signifier;
        this.setType(type);
        this.notation = notation != null ? notation : new NotationSpecification(type.arity());
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
    public SymbolBase(String signifier, Specification type, NotationSpecification notation) {
        this(signifier, type, notation, false);
    }

    /**
     * Construct a constant symbol with a signifier, and type specification.
     * The notation is chosen to be the {@link Notation.NotationSpecification#Notation.NotationSpecification(int) default notation specification}.
     * @param signifier the string representation of this symbol.
     * @param type the type specification of this symbol.
     * @post &not;isVariable()
     */
    public SymbolBase(String signifier, Specification type) {
        this(signifier, type, null);
    }
    
    public boolean equals(Object o) {
    	if (o instanceof Symbol) {
	    Symbol b = (Symbol) o;
	    if (Utility.equals(getSignifier(), b.getSignifier())
		&& Utility.equals(getType(), b.getType())
		&& Utility.equals(getNotation(), b.getNotation())) {
		assert isVariable() == b.isVariable() : "same symbols are consistently either both variable or both constant";
		return true;
	    } else
		return false;
    	}
    	return false;
    }
    
    /**
     * Compares two symbols.
     * <p>
     * This implementation compares for notation (precedence) in favor of type in favor of symbol name.
     * </p>
     * @post only <em>semi</em>-consistent with equals (since Notation is)
     */
    public int compareTo(Object o) {
	Symbol b = (Symbol) o;
	int a;
	a = Utility.compare(getNotation(), b.getNotation());
	if (a != 0)
	    return a;
	a = Utility.compare(getType(), b.getType());
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
    public Specification getType() {
    	return type;
    }
    public void setType(Specification type) {
    	if (type == null)
	    throw new IllegalArgumentException("invalid type specification: " + type);
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
    
    public boolean isCompatible(Object[] args) {
    	Specification type = getType();
	if ((args == null || args.length == 0) && (type == null || type.arity() == 0))
	    return true;
	//@internal now could as well call isCompatible(Specification) with a specification that we generate from args, setting the expected return type of getType().
	if (args.length != type.arity())
	    return false;
	Class[] spec_parameterTypes = type.getParameterTypes();
	assert args.length == spec_parameterTypes.length : "same arity same parameter length";
	for (int i = 0; i < spec_parameterTypes.length; i++)
	    if (!(args[i] == null | spec_parameterTypes[i].isInstance(args[i])))
		return false;
	return true;
    }

    public String toString() {
    	Specification type = getType();
	if (Logger.global.isLoggable(Level.FINEST))
	    return getSignifier() + type;
    	// short representation for symbols of arity 0
    	return type.arity() == 0
	    ? getSignifier()
	    : (getSignifier() + '/' + type.arity());
    }
}
