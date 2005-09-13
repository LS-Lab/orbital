/**
 * @(#)InterpretationBase.java 1.0 2001/06/12 Andre Platzer
 * 
 * Copyright (c) 1999-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.sign.Signature;
import orbital.logic.sign.SignatureBase;
import orbital.logic.sign.Symbol;
import orbital.logic.sign.type.TypeException;
import orbital.logic.sign.type.Types;
import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;
import orbital.util.DelegateMap;
import java.util.SortedMap;

import java.util.Map.Entry;
import java.util.TreeMap;

import orbital.util.InnerCheckedException;
import orbital.util.Utility;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.Collections;

/**
 * A basic interpretation implementation.
 * 
 * @invariants sub classes support nullary constructor (for virtual new instance).
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Logic#satisfy
 * @see Signature
 * @see java.util.Map
 */
public class InterpretationBase extends DelegateMap/*<Symbol, Object>*/ implements Interpretation {
    private static final long serialVersionUID = 1211049244164642015L;
    /**
     * Whether to validate symbols and interpretation associations of this interpretation.
     */
    private static final boolean VALIDATION = true;
    /**
     * The signature &Sigma; to be interpreted.
     * @serial
     */
    private Signature sigma;

    /**
     * The empty interpretation &empty; for the given signature.
     * Note that it is generally not useful to use empty interpretations, however
     * there are some special applications.
     */
    public static final Interpretation EMPTY(Signature sigma) {
	return new InterpretationBase(sigma, Collections.EMPTY_MAP);
    }

    private InterpretationBase() {
	super(new TreeMap());
    }

    /**
     * Construct a problem specific interpretation source.
     * <p>
     * Overwrite map operations like {@link #get(Object)}, {@link #contains(Object)}
     * to implement a different source for symbol associations, than the map.
     * This is especially useful if a problem specific source for logical data values already
     * exists.</p>
     * @param sigma the signature &Sigma; whose symbols are interpreted by the custom interpretation source.
     *  If sigma is <code>null</code>, then it should be set later with {@link #setSignature(Signature)}.
     */
    protected InterpretationBase(Signature sigma) {
	this();
	this.sigma = sigma;
    }

    /**
     * Construct a new Interpretation with the given map of associations.
     * @param sigma the signature &Sigma; whose symbols to interpret.
     * @param associations the map that associates each symbol in sigma with a value object.
     */
    public InterpretationBase(Signature sigma, Map associations) {
	this(sigma);
	putAll(associations);
    }
    public InterpretationBase(Signature sigma, SortedMap associations) {
	this(sigma);
	putAll(associations);
    }

    /**
     * Only for delegation to interpretations used in
     * {@link #unmodifiableInterpretation(Interpretation)}.
     */
    private InterpretationBase(Interpretation delegatee) {
	super(delegatee);
	// getSignature() is overwritten, anyway
	this.sigma = null;
    }

    public boolean equals(Object o) {
    	if (o instanceof Interpretation) {
	    Interpretation b = (Interpretation) o;
	    return Utility.equals(getSignature(), b.getSignature()) && super.equals(o);
    	}
    	return false;
    }

    public int hashCode() {
	return Utility.hashCode(getSignature()) ^ super.hashCode();
    }

    // Get/Set Properties

    /**
     * Get the signature interpreted.
     */
    public Signature getSignature() {
	return sigma;
    } 

    /**
     * Set the signature interpreted.
     * @preconditions sigma == null || map == null || map.keySet() &sube; sigma
     * @throws IllegalArgumentException if sigma does not contain a symbol which is interpreted in the current assocation map.
     *  This is not checked if sigma is <code>null</code>.
     */
    public void setSignature(Signature sigma) {
	if (sigma != null) {
	    for (Iterator it = keySet().iterator(); it.hasNext(); ) {
		Object o = it.next();
		if (!sigma.contains(o)) {
		    throw new IllegalArgumentException("signature does not contain associated symbol " + o + ". Signature is invalid for this interpretation. You should clear association map first.");
		}
	    }
	}

	this.sigma = sigma;
    } 


    // Basic Map operations.

    public Object get(Object symbol) {
	validate(symbol);
	//@todo is this the referent or the signified?
	Object referent = super.get(symbol);
	assert validate(symbol, referent) : "referent " + referent + " must conform to the specification " + ((Symbol) symbol).getType() + " of the symbol " + symbol;
	return referent;
    } 

    public Object remove(Object symbol) {
	validate(symbol);
	return super.remove(symbol);
    } 

    public Object put(Object symbol, Object referent) {
	validate(symbol);
	if (!validate(symbol, referent))
	    throw new TypeException("referent " + referent + " must conform to the type specification " + ((Symbol) symbol).getType() + " of the symbol " + symbol, ((Symbol) symbol).getType(), Types.typeOf(referent));
	return super.put(symbol, referent);
    } 

    public void putAll(Map associations) {
	if (sigma != null) {
	    for (Iterator it = associations.entrySet().iterator(); it.hasNext(); ) {
		Map.Entry e = (Map.Entry) it.next();
		if (!validate(e.getKey(), e.getValue()))
		    throw new TypeException("referent " + e.getValue() + " of " + e.getValue().getClass() + " must conform to the type " + ((Symbol) e.getKey()).getType() + " of the symbol " + e.getKey(), ((Symbol) e.getKey()).getType(), Types.typeOf(e.getValue()));
		if (!sigma.contains(e.getKey()))
		    //@todo replace by throw new SignatureException
		    throw new IllegalArgumentException("symbol " + e.getKey() + " not in signature. Association map is invalid for this signature.");
	    }
	}
	super.putAll(associations);
    } 

    public boolean containsKey(Object symbol) {
	validate(symbol);
	return super.containsKey(symbol);
    } 

    public boolean contains(Object symbol) {
	return containsKey(symbol);
    } 


    // Extended operations.

    public Interpretation union(Interpretation i2) {
	InterpretationBase u = newInstance();
	u.setSignature(getSignature().union(i2.getSignature()));
	u.putAll(this);
	u.putAll(i2);
	return u;
    } 

    public String toString() {
	if (getDelegatee() == null)
	    return "I<null>";
	StringBuffer str = new StringBuffer("I<");
	Iterator     it = entrySet().iterator();
	while (it.hasNext())
	    try {
		Entry e = (Entry) it.next();
		str.append(e.getKey() + "/" + e.getValue() + (it.hasNext() ? "," : ""));
	    } catch (ClassCastException oops) {
		throw (AssertionError) new AssertionError("map does not contain Map.Entry").initCause(oops);
	    } 
	str.append('>');
	return str.toString();
    } 

    /**
     * Validate symbol as an element in the signature.
     * @preconditions symbol &isin; sigma
     * @throws NoSuchElementException if the symbol is not in the current signature sigma.
     * @todo optimize this hotspot during proving
     */
    private final void validate(Object symbol) {
	if (!VALIDATION)
	    return;
	try {
	    if (symbol == null)
		throw new NoSuchElementException("Symbol <null> not in signature");
	    else if (!sigma.contains(symbol))
		throw new NoSuchElementException("Symbol '" + symbol + "' not in signature " + sigma);
	}
	catch (ClassCastException ex) {
	    throw (ClassCastException) new ClassCastException("exception during validation of '" + symbol + "'").initCause(ex);
	}
    }

    /**
     * Validates that a referent has a valid type for the type specification of symbol.
     * @todo enhance type checks
     */
    private final boolean validate(Object symbol, Object referent) {
	if (!VALIDATION)
	    return true;
	return ((Symbol)symbol).getType().apply(referent);
    }


    /**
     * Create a new instance of the exact same type.
     * Used to create an object of the same type without copying its data.
     */
    private InterpretationBase newInstance() {
	try {
	    return (InterpretationBase) getClass().newInstance();
    	}
    	catch (InstantiationException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + InterpretationBase.class + " must support nullary constructor for virtual new instance.", nonconform);}
    	catch (IllegalAccessException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + InterpretationBase.class + " must support nullary constructor for virtual new instance.", nonconform);}
    }


    // Utilities methods
    
    /**
     * Returns an unmodifiable view of the specified interpretation.
     * The result is a <a href="../../math/Values.html#readOnlyView">read only view</a>.
     */
    public static final Interpretation unmodifiableInterpretation(final Interpretation i) {
	return /*refine/delegate Interpretation*/ new InterpretationBase(i) {
		private static final long serialVersionUID = 2999004456165993569L;
		// Code for delegation of orbital.logic.imp.Interpretation methods to i

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @return <description>
		 * @see orbital.logic.imp.Interpretation#put(Object, Object)
		 */
		public Object put(Object param1, Object param2)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @param param1 <description>
		 * @see orbital.logic.imp.Interpretation#putAll(Map)
		 */
		public void putAll(Map param1)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.logic.imp.Interpretation#getSignature()
		 */
		public Signature getSignature()
		{
		    return SignatureBase.unmodifiableSignature(i.getSignature());
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.logic.imp.Interpretation#union(Interpretation)
		 */
		public Interpretation union(Interpretation param1)
		{
		    return i.union(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @see orbital.logic.imp.Interpretation#setSignature(Signature)
		 */
		public void setSignature(Signature param1)
		{
		    throw new UnsupportedOperationException();
		}
		// Code for delegation of java.util.Map methods to i

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see java.util.Map#remove(Object)
		 */
		public Object remove(Object param1)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return <description>
		 * @see java.util.Map#values()
		 */
		public Collection values()
		{
		    return Collections.unmodifiableCollection(i.values());
		}

		/**
		 *
		 * @see java.util.Map#clear()
		 */
		public void clear()
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return <description>
		 * @see java.util.Map#keySet()
		 */
		public Set keySet()
		{
		    return Collections.unmodifiableSet(i.keySet());
		}

		/**
		 *
		 * @return <description>
		 * @see java.util.Map#entrySet()
		 */
		public Set entrySet()
		{
		    throw new UnsupportedOperationException("not yet implemented: unmodifiable view of entrySet() with unmodifiable entries");
		}

	    };
    }
}
