/**
 * @(#)QuickUnitedInterpretation.java 1.0 2001/11/17 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.imp.Interpretation;
import orbital.logic.imp.InterpretationBase;
import orbital.logic.imp.Signature;
import java.util.Map;
import java.util.NoSuchElementException;

import orbital.logic.functor.Functor;

/**
 * An interpretation implementation that performs lookup in two other interpretations
 * thereby providing a faster version of {@link Interpretation#union(Interpretation)}.
 * This is intended to be used for getting the union of a problem-specific interpretation
 * and the Quick interpretation, quickly.
 * 
 * @version 1.0, 2001/11/17
 * @author  Andr&eacute; Platzer
 * @internal note the following construct will only assure core interpretation unless core is overwritten (which he should never be anyway)
 *  I = new QuickUnitedInterpretation(ClassicalLogic._coreInterpretation, I);
 */
class QuickUnitedInterpretation extends InterpretationBase {
	private Interpretation i1;
	private Interpretation i2;

	/**
	 * Construct a new interpretation which is the quick union of two other interpretations.
	 * @param i1 the first (preferred) interpretation to consider.
	 * @param i2 the second interpretation to consider if the first does not understand a symbol,
	 *  i.e. if the signature of the first does not contain a symbol.
	 */
	public QuickUnitedInterpretation(Interpretation i1, Interpretation i2) {
		super(i1.getSignature());
		this.i1 = i1;
		this.i2 = i2;
	}
    
	// Get/Set Properties

	public Signature getSignature() {
		throw new UnsupportedOperationException();
	} 

	public void setSignature(Signature sigma) {
		throw new UnsupportedOperationException();
	} 


	// Basic Map operations.

	public Object get(Object symbol) {
		try {
			return i1.get(symbol);
		}
		catch (NoSuchElementException trial) {
			return i2.get(symbol);
		}
	} 

	public Object remove(Object symbol) {
		throw new UnsupportedOperationException();
	} 

	public Object put(Object symbol, Object value) {
		throw new UnsupportedOperationException();
	} 

	public void putAll(Map associations) {
		throw new UnsupportedOperationException();
	} 

	public boolean containsKey(Object symbol) {
		try {
			return i1.containsKey(symbol);
		}
		catch (NoSuchElementException trial) {}
		return i2.containsKey(symbol);
	} 

	public boolean contains(Object symbol) {
		return containsKey(symbol);
	} 


	// Extended operations.

}
