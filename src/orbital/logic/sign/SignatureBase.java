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
import java.util.Collection;
import orbital.logic.imp.Signature;
import java.util.Set;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Comparator;
import orbital.logic.imp.Type;
import orbital.logic.imp.Symbol;
import java.util.Collection;
import orbital.logic.imp.Signature;
import java.util.Set;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Comparator;
import orbital.logic.imp.Type;
import orbital.logic.imp.Symbol;

/**
 * A basic signature implementation.
 * 
 * @invariant sub classes support nullary constructor (for virtual new instance)
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

    /**
     * @deprecated Since Orbital1.1 use {@link #get(String,Object[])}&ne;null instead.
     */
    public boolean contains(String signifier, Object[] arg) {
	return get(signifier, arg) != null;
    }
    public Symbol get(String signifier, Object[] arg) {
	//@todo should we call get(signifier, Types.map(Types.typeOf((Expression[])arg)), Types.UNIVERSAL), which was possible.
	for (Iterator i = iterator(); i.hasNext(); ) {
	    final Object o = i.next();
	    assert o instanceof Symbol : "signature isa " + SortedSet.class.getName() + '<' + Symbol.class.getName() + '>';
	    final Symbol s = (Symbol) o;
	    if (signifier.equals(s.getSignifier()))
		if (arg instanceof Expression[]) {
		    if (Types.isApplicableTo(s.getType(), (Expression[])arg))
			//@xxx if multiple symbols are applicable use dynamic dispatch / overloading etc.
			//TODO: assert check arity of s.notation with arg.length, as well?
			return s;
		} else
		    //@todo how to check in case of !(arg instanceof Expression[])?
		    throw new UnsupportedOperationException("type checking requires that the arguments are instances of " + Expression.class);
	}
	return null;
    }
    public Symbol get(String signifier, Type maxType) {
	for (Iterator i = iterator(); i.hasNext(); ) {
	    final Object o = i.next();
	    assert o instanceof Symbol : "signature isa " + SortedSet.class.getName() + '<' + Symbol.class.getName() + '>';
	    final Symbol s = (Symbol) o;
	    if (signifier.equals(s.getSignifier()))
		if (s.getType().subtypeOf(maxType))
		    //@xxx if multiple symbols are compatible use dynamic dispatch / overloading etc.
		    return s;
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
    	catch (InstantiationException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + SignatureBase.class + " must support nullary constructor for virtual new instance.", nonconform);}
    	catch (IllegalAccessException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + SignatureBase.class + " must support nullary constructor for virtual new instance.", nonconform);}
    }

    // some type-safe checks (ensuring implements SortedSet<Symbol>)

    public boolean add(Object o) {
	if (o instanceof Symbol)
	    return super.add(o);
	else
	    throw new ClassCastException(o + " is not an instance of " + Symbol.class);
    }

    // Utilities methods

    /**
     * Returns an unmodifiable view of the specified signature.
     * The result is a <a href="../../math/Values.html#readOnlyView">read only view</a>.
     * @todo or only structurally unmodifiable because iterator().next().setSignifier will still work?
     */
    public static final Signature unmodifiableSignature(final Signature s) {
	return /*refine/delegate Signature*/ new SignatureBase(s) {
		private static final long serialVersionUID = -7777832542719541528L;
		// Code for delegation of java.util.Set methods to s

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see java.util.Set#addAll(Collection)
		 */
		public boolean addAll(Collection param1)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see java.util.Set#add(Object)
		 */
		public boolean add(Object param1)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return <description>
		 * @see java.util.Set#iterator()
		 */
		public Iterator iterator()
		{
		    return Setops.unmodifiableIterator(s.iterator());
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see java.util.Set#remove(Object)
		 */
		public boolean remove(Object param1)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @see java.util.Set#clear()
		 */
		public void clear()
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see java.util.Set#removeAll(Collection)
		 */
		public boolean removeAll(Collection param1)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see java.util.Set#retainAll(Collection)
		 */
		public boolean retainAll(Collection param1)
		{
		    throw new UnsupportedOperationException();
		}

		// Code for delegation of orbital.logic.imp.Signature methods to s

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @return <description>
		 * @see orbital.logic.imp.Signature#get(String, Type)
		 */
		public Symbol get(String param1, Type param2)
		{
		    return s.get(param1, param2);
		}

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @return <description>
		 * @see orbital.logic.imp.Signature#get(String, Object[])
		 */
		public Symbol get(String param1, Object[] param2)
		{
		    return s.get(param1, param2);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.logic.imp.Signature#union(Signature)
		 */
		public Signature union(Signature param1)
		{
		    return s.union(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.logic.imp.Signature#intersection(Signature)
		 */
		public Signature intersection(Signature param1)
		{
		    return s.intersection(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.logic.imp.Signature#difference(Signature)
		 */
		public Signature difference(Signature param1)
		{
		    return s.difference(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.logic.imp.Signature#symmetricDifference(Signature)
		 */
		public Signature symmetricDifference(Signature param1)
		{
		    return s.symmetricDifference(param1);
		}

	    };
    }
}
