/*
 * @(#)SignatureBase.java 1.0 2001/06/12 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import java.util.SortedSet;
import orbital.util.DelegateSortedSet;
import java.util.Collection;

import java.util.Iterator;
import java.util.TreeSet;

import java.util.Collections;
import orbital.util.Setops;
import orbital.util.InnerCheckedException;

/**
 * A basic signature implementation.
 * 
 * @invariant sub classes support no-arg constructor (for virtual new instance)
 * @structure implements java.util.SortedSet
 * @structure delegates super:java.util.SortedSet
 * @structure inherits orbital.util.DelegateSortedSet
 * @version 1.0, 2001/06/12
 * @author  Andr&eacute; Platzer
 * @see ExpressionSyntax#scanSignature(java.lang.String)
 */
public class SignatureBase extends DelegateSortedSet/*<Symbol>*/ implements Signature {
    private static final long serialVersionUID = -2651634605539964276L;
    /**
     * Empty signature &empty;.
     * @attribute immutable
     */
    public static final SignatureBase EMPTY = new SignatureBase(Collections.unmodifiableSortedSet(new TreeSet()));

    /**
     * Construct a new signature with the given set of symbols.
     * <p>
     * The argument is a collection as per general contract of the interface java.util.Set,
     * but will be transformed into this signature set with the call to this constructor.</p>
     * @param symbols the set of symbols in the signature.
     * @pre &forall;s&isin;symbols: s instanceof orbital.logic.imp.Symbol
     */
    public SignatureBase(Collection/*<Symbol>*/ symbols) {
	this(new TreeSet/*<Symbol>*/(symbols));
    }

    /**
     * Construct an empty signature &empty;.
     */
    public SignatureBase() {
	super(new TreeSet/*<Symbol>*/());
    }

    private SignatureBase(SortedSet/*<Symbol>*/ symbols) {
	super(symbols);
	assert Setops.all(getDelegatee(), new orbital.logic.functor.Predicate() { public boolean apply(Object s) {return s instanceof Symbol;} }) : "instanceof SortedSet<Symbol>";
    }
    
    public boolean equals(Object o) {
	return (o instanceof Signature) && getDelegatee().equals(o);
    } 

    public int hashCode() {
	return getDelegatee().hashCode();
    } 

    // Get/Set Properties

    /**
     * Get the set of symbols in this signature.
     */
    public SortedSet/*<Symbol>*/ getSymbols() {
	return (SortedSet/*<Symbol>*/) getDelegatee();
    } 

    /**
     * Set the set of symbols in this signature.
     * @param symbols the new set of symbols in this signature.
     * @pre &forall;s&isin;symbols: s instanceof orbital.logic.imp.Symbol
     */
    public void setSymbols(SortedSet/*<Symbol>*/ symbols) {
	setDelegatee(symbols);
    } 
    
    // Extended operations for functor symbols

    public boolean contains(String signifier, Object[] arg) {
	return get(signifier, arg) != null;
    }
    public Symbol get(String signifier, Object[] arg) {
	for (Iterator i = iterator(); i.hasNext(); ) {
	    Object o = i.next();
	    if (!(o instanceof Symbol))
		continue;
	    Symbol s = (Symbol) o;
	    if (signifier.equals(s.getSignifier()))
		if (arg instanceof Expression[]) {
		    if (Types.isApplicableTo(s.getType(), (Expression[])arg))
			//TODO: check arity of s.notation with arg.length, as well?
			return s;
		} else
		    //@todo how to check in case of !(arg instanceof Expression[])?
		    throw new UnsupportedOperationException("type checking requires that the arguments are instances of " + Expression.class);
	}
	return null;
    }

    // Extended Set operations.

    public Signature union(Signature sigma2) {
	SignatureBase u = newInstance();
	u.setSymbols(Setops.union(this, sigma2));
	return u;
    } 

    public Signature intersection(Signature sigma2) {
	SignatureBase s = newInstance();
	s.setSymbols(Setops.intersection(this, sigma2));
	return s;
    } 

    public Signature difference(Signature sigma2) {
	SignatureBase d = newInstance();
	d.setSymbols(Setops.difference(this, sigma2));
	return d;
    } 

    public Signature symmetricDifference(Signature sigma2) {
	SignatureBase d = newInstance();
	d.setSymbols(Setops.symmetricDifference(this, sigma2));
	return d;
    } 

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	StringBuffer str = new StringBuffer("sigma={");
	Iterator	 it = iterator();
	while (it.hasNext())
	    str.append(it.next() + (it.hasNext() ? "," : ""));
	str.append('}');
	return getClass().getName() + "[" + str + "]";
    } 

    /**
     * Create a new instance of the exact same type.
     * Used to create an object of the same type without copying its data.
     */
    private SignatureBase newInstance() {
	try {
	    return (SignatureBase) getClass().newInstance();
    	}
    	catch (InstantiationException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + SignatureBase.class + " must support no-arg constructor for virtual new instance.", nonconform);}
    	catch (IllegalAccessException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + SignatureBase.class + " must support no-arg constructor for virtual new instance.", nonconform);}
    }

    // some type-safe checks (ensuring implements SortedSet<Symbol>)

    public boolean add(Object o) {
	if (o instanceof Symbol)
	    return super.add(o);
	else
	    throw new ClassCastException(o + " is not an instance of " + Symbol.class);
    }
}
