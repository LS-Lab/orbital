/**
 * @(#)InterpretationBase.java 1.0 2001/06/12 Andre Platzer
 * 
 * Copyright (c) 1999-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import java.io.Serializable;
import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;
import orbital.util.DelegateMap;

import java.util.Map.Entry;
import java.util.TreeMap;

//TODO: check whether still needed:
import orbital.logic.functor.Functor;
import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;
import orbital.util.InnerCheckedException;
import orbital.util.Utility;

/**
 * A basic Interpretation implementation.
 * 
 * @invariant sub classes support no-arg constructor (for virtual new instance).
 * @version 1.0, 2001/06/12
 * @author  Andr&eacute; Platzer
 * @see Logic#satisfy
 * @see Signature
 * @see java.util.Map
 */
public class InterpretationBase extends DelegateMap/*<Symbol, Object>*/ implements Interpretation {
    private static final long serialVersionUID = 1211049244164642015L;
    /**
     * The signature &Sigma; to be interpreted.
     * @serial
     */
    private Signature sigma;

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
     * @pre sigma == null || map == null || map.keySet() &sube; sigma
     * @throws IllegalArgumentException if sigma does not contain a symbol which is interpreted in the current assocation map.
     *  This is not checked if sigma is <code>null</code>.
     */
    public void setSignature(Signature sigma) {
	if (sigma != null)
	    for (Iterator it = keySet().iterator(); it.hasNext(); ) {
		Object o = it.next();
		if (sigma.contains(o))
		    throw new IllegalArgumentException("signature does not contain associated symbol " + o + ". Signature is invalid for this interpretation. You should clear association map first.");
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

    public Object put(Object symbol, Object value) {
	validate(symbol);
	if (!validate(symbol, value))
	    throw new IllegalArgumentException("referent " + value + " must conform to the specification " + ((Symbol) symbol).getType() + " of the symbol " + symbol);
	return super.put(symbol, value);
    } 

    public void putAll(Map associations) {
	if (sigma != null)
	    for (Iterator it = associations.entrySet().iterator(); it.hasNext(); ) {
		Map.Entry e = (Map.Entry) it.next();
		if (!validate(e.getKey(), e.getValue()))
		    throw new IllegalArgumentException("referent " + e.getValue() + " must conform to the specification " + ((Symbol) e.getKey()).getType() + " of the symbol " + e.getKey());
		if (!sigma.contains(e.getKey()))
		    throw new IllegalArgumentException("symbol " + e.getKey() + " not in signature. Association map is invalid for this signature.");
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

    /**
     * Get the functor interpretation belonging to a specified signifier and arguments (experimental).
     * @param signifier the signifier of the functor.
     * @param arg the arguments that the functor belonging to the signifier is called with.
     * @return the functor belonging to the notation if interpreted, or <tt>null</tt>.
     * @post RES == null || RES.toString().equals(signifier)
     * @throws java.util.NoSuchElementException if the symbol is not in the current signature &Sigma;.
     * @see orbital.logic.functor.Notation#functorOf(String, Object[])
     * @internal see Signature#contains(String, Object[])
     * @todo should we change first argument to Symbol? No! simply remove this method again since RES == get(signature.get(signifier,arg))
     */
    /*public Functor get(String signifier, Object[] arg) {
      Symbol symbol = sigma.get(signifier, arg);
      if (symbol == null)
      throw new NoSuchElementException("Symbol '" + signifier + "'/" + arg.length + " not in signature");
      assert arg.length <= 2 : "functor notations are currently used for at most 2 arguments";
      Object value = super.get(symbol);
      if (!(value instanceof Functor))
      throw new IllegalStateException("interpretation of a functor symbol " + symbol + " expected to be an instance of orbital.logic.functor.Functor");
      assert (value + "").equals(symbol.getSignifier()) : "name matches string representation of the functor";
      return (Functor) value;
      //@xxx old implementation with loop but without performance. can it be removed?
      /*if (!sigma.contains(signifier, arg))
      throw new NoSuchElementException("Symbol '" + signifier + "'/" + arg.length + " not in Signature");
      assert arg.length <= 2 : "functor notations are currently used for at most 2 arguments";
      for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
      Map.Entry e = (Map.Entry) i.next();
      Object	  value = e.getValue();
      if (!(value instanceof Functor))
      continue;
      String	  representation = value + "";
      assert representation.equals(((Symbol) e.getKey()).symbol) : "name matches string representation of the functor";
      if (arg.length == 1 && !(value instanceof Function))
      continue;
      if (arg.length == 2 && !(value instanceof BinaryFunction))
      continue;
      if (signifier.equals(representation))
      return (Functor) value;
      }
      return null;*/
    //}

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
     * @pre symbol &isin; sigma
     * @throws NoSuchElementException if the symbol is not in the current signature sigma.
     */
    private final void validate(Object symbol) {
	try {
	    if (!sigma.contains(symbol))
		throw new NoSuchElementException("Symbol '" + symbol + "' not in signature");
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
	Functor.Specification spec = ((Symbol) symbol).getType();
	return spec.arity() != 0
	    ? referent instanceof Functor && spec.isConform((Functor) referent)
	    : referent instanceof Functor
	    ? spec.isConform((Functor) referent)
	    : spec.getReturnType().isInstance(referent);
    }

    /**
     * Create a new instance of the exact same type.
     * Used to create an object of the same type without copying its data.
     */
    private InterpretationBase newInstance() {
	try {
	    return (InterpretationBase) getClass().newInstance();
    	}
    	catch (InstantiationException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + InterpretationBase.class + " must support no-arg constructor for virtual new instance.", nonconform);}
    	catch (IllegalAccessException nonconform) {throw new InnerCheckedException("invariant: sub classes of " + InterpretationBase.class + " must support no-arg constructor for virtual new instance.", nonconform);}
    }
}
