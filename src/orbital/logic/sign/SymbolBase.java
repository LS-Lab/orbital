/**
 * @(#)SymbolBase.java 1.0 2001/04/07 Andre Platzer
 *
 * Copyright (c) 2001-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.functor.Notation.NotationSpecification;
import orbital.logic.functor.Functor.Specification;
import java.io.Serializable;

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
    private Specification			specification;
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
     * @param specification the (arity and) type specification of this symbol.
     * @param notation The notation used when this symbol occurs.
     *  This includes precedence and associativity information, as well.
     *  May be <code>null</code> for symbols with arity 0,
     *  which will be converted to the {@link NotationSpecification#NotationSpecification(int) default notation specification}, then.
     * @param variable whether this is a variable symbol.
     *  <code>true</code> if this symbol is a variable symbol,
     *  and <code>false</code> if this symbol is a constant symbol.
     * @todo could check whether specification and notation match in arity
     */
    public SymbolBase(String signifier, Specification specification, NotationSpecification notation, boolean variable) {
    	if (specification == null)
	    throw new IllegalArgumentException("not a valid arity and type specification: " + specification);
        this.signifier = signifier;
        this.specification = specification;
        this.notation = notation != null ? notation : new NotationSpecification(specification.arity());
        this.variable = variable;
    }

    // constructors for convenience

    /**
     * Construct a constant symbol with a signifier, type specification, and notation.
     * @param signifier the string representation of this symbol.
     * @param specification the (arity and) type specification of this symbol.
     * @param notation The notation used when this symbol occurs.
     *  This includes precedence and associativity information, as well.
     *  May be <code>null</code> for symbols with arity 0,
     *  which will be converted to the {@link NotationSpecification#NotationSpecification(int) default notation specification}, then.
     * @post &not;isVariable()
     */
    public SymbolBase(String signifier, Specification specification, NotationSpecification notation) {
        this(signifier, specification, notation, false);
    }

    /**
     * Construct a constant symbol with a signifier, and type specification.
     * The notation is chosen to be the {@link NotationSpecification#NotationSpecification(int) default notation specification}.
     * @param signifier the string representation of this symbol.
     * @param specification the (arity and) type specification of this symbol.
     * @post &not;isVariable()
     */
    public SymbolBase(String signifier, Specification specification) {
        this(signifier, specification, null);
    }
    
    public boolean equals(Object o) {
    	if (o instanceof Symbol) {
	    Symbol b = (Symbol) o;
	    if (Utility.equals(getSignifier(), b.getSignifier())
		&& Utility.equals(getSpecification(), b.getSpecification())
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
     * This implementation compares for notation (precedence) in favor of specification in favor of specification in favor of symbol name.
     * </p>
     * @post only <em>semi</em>-consistent with equals (since Notation is)
     */
    public int compareTo(Object o) {
	Symbol b = (Symbol) o;
	int a;
	a = Utility.compare(getNotation(), b.getNotation());
	if (a != 0)
	    return a;
	a = Utility.compare(getSpecification(), b.getSpecification());
	return a != 0 ? a : Utility.compare(getSignifier(), b.getSignifier());
    } 

    public int hashCode() {
    	return Utility.hashCode(getSignifier()) ^ Utility.hashCode(getSpecification()) ^ Utility.hashCode(getNotation());
    }
    
    // get/set properties
    public String getSignifier() {
    	return signifier;
    }
    public void setSignifier(String signifier) {
    	this.signifier = signifier;
    }
    public Specification getSpecification() {
    	return specification;
    }
    public void setSpecification(Specification spec) {
    	this.specification = spec;
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
    	Specification specification = getSpecification();
	if ((args == null || args.length == 0) && (specification == null || specification.arity() == 0))
	    return true;
	if (args.length != specification.arity())
	    return false;
	Class[] parameterTypes = specification.getParameterTypes();
	assert args.length == parameterTypes.length : "same arity same parameter length";
	for (int i = 0; i < parameterTypes.length; i++)
	    if (!(args[i] == null | parameterTypes[i].isInstance(args[i])))
		return false;
	return true;
    }
	
    public String toString() {
    	Specification specification = getSpecification();
    	// short representation for symbols of arity 0
    	return specification.arity() == 0
	    ? getSignifier()
	    : (getSignifier() + '/' + specification.arity());
    }
}
