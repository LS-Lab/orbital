/**
 * @(#)LogicSupport.java 0.7 2002/05/09 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;
import orbital.logic.functor.Functor;
import orbital.logic.functor.Notation.NotationSpecification;

import orbital.logic.functor.Notation;

import java.util.Map;
import java.util.TreeMap;

import java.beans.IntrospectionException;
import orbital.util.InnerCheckedException;

/**
 * @version 0.7, 2002/05/09
 * @author  Andr&eacute; Platzer
 * @stereotype Module
 * @todo provide a basis to derive from, such that impl is defined in terms of not and or.
 */
final class LogicSupport {
    /**
     * prevent instantiation - currently module class
     */
    private LogicSupport() {}

    /**
     * Converts an array to an interpretation (adding true, false).
     * <p>
     * Apart from adding true and false, this method also registers the functor's
     * notations at {@link Notation#setNotation(Functor,Notation.NotationSpecification)}.
     * </p>
     * @param functors Contains functors and their notation specifications.
     *  Stored as an array of length-2 arrays
     *  with functors[i][0] being the interpretation {@link Functor},
     *  and functor[i][1] being a {@link NotationSpecification}.
     * @param skipNull whether to skip functors for which the array contains <code>null</code>.
     * @param useRegisteredNotationsOnNull whether to use {@link Notation#getNotation(Functor) regisitered notations}
     *  if the array contains <code>null</code> as notation specification.
     * @param appendTrueFalse whether to introduce true false constants.
     * @throws NullPointerException if skipNull==<code>false</code> but a functor of the array is <code>null</code>.
     */
    public static final Interpretation arrayToInterpretation(Object[][] functors, boolean skipNull, boolean useRegisteredNotationsOnNull, boolean appendTrueFalse) {
	Map assoc = new TreeMap();
	if (appendTrueFalse) {
	    //@todo explicitly let our callers include that
	    assoc.put(new SymbolBase("true", Types.TRUTH), Boolean.TRUE);
	    assoc.put(new SymbolBase("false", Types.TRUTH), Boolean.FALSE);
	}
	for (int i = 0; i < functors.length; i++) {
	    if (functors[i].length != 2)
		throw new IllegalArgumentException("array of dimension [][2] expected");
	    final Functor f = (Functor)functors[i][0];
	    NotationSpecification notation = (NotationSpecification)functors[i][1];
	    if (f == null)
		if (skipNull)
		    continue;
		else
		    throw new NullPointerException("illegal functor " + f + " for " + notation);
	    if (notation == null)
		if (useRegisteredNotationsOnNull)
		    notation = Notation.getNotation(f);
		else
		    throw new NullPointerException("illegal notation " + notation + " for " + f);
	    try {
		assoc.put(new SymbolBase(f.toString(),
					 Types.declaredTypeOf(f),
					 notation),
			  f);
		if (notation != null)
		    Notation.setNotation(f, notation);
	    }
	    catch (IntrospectionException ex) {throw new InnerCheckedException("could not detect specification", ex);}
	}
	Signature signature = new SignatureBase(assoc.keySet());
	return new InterpretationBase(signature, assoc);
    }
}
