/**
 * @(#)FastValuesImpl.java 1.1 2007-08-21 Andre Platzer
 * 
 * Copyright (c) 2000-2007 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import java.awt.Dimension;
import java.text.ParseException;
import orbital.logic.functor.Function;

import orbital.math.functional.MathFunctor;

import orbital.logic.functor.Predicate;
import orbital.logic.functor.Functor;
import java.util.Collection;

import java.util.List;

import java.text.ParsePosition;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Arrays;
import orbital.util.Setops;
import orbital.util.Utility;

import orbital.math.functional.Functions;

/**
 * Default value constructor factory as an optimized version using fast
 * machine-sized doubles, floats, ints, longs
 * etc. whereever possible and ignores precision overflow problems for speed.
 * This implementation also uses faster vector, matrix operations on doubles.
 * Also activates partial caching/sharing of numer constants.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class FastValuesImpl extends ValuesImpl {
    // Constants

    /**
     * Initialize static constant array when class is loaded.
     * @invariants 0 < MAX_CONSTANT < Integer.MAX_VALUE
     * @xxx note that we should think about the order of static initialization.
     */
    private static final int     MAX_CONSTANT = 10;
    private static final Integer posConst[] = new Integer[MAX_CONSTANT + 1];
    private static final Integer negConst[] = new Integer[MAX_CONSTANT + 1];
    static {
	//@fixme this static initialization somehow is not yet executed before the super constructor is called. Move constants here
        posConst[0] = negConst[0] = new AbstractInteger.Long(0);
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            posConst[i] = new AbstractInteger.Long(i);
            negConst[i] = new AbstractInteger.Long(-i);
        } 
    } 

    // instantiation

    public FastValuesImpl() {}

    // scalar value constructors - facade factory
    // primitive type conversion methods

    public Rational rational(int p, int q) {
        return new AbstractRational.Int(p, q);
    } 
    public Rational rational(int p) {
        return new AbstractRational.Int(p);
    } 

    // integer scalar value constructors - facade factory

    public Integer valueOf(int val) {
        // If -MAX_CONSTANT < val < MAX_CONSTANT, return stashed constant
        if (0 <= val && val <= MAX_CONSTANT)
            return posConst[val];
        else if (-MAX_CONSTANT <= val && val < 0)
            return negConst[-val];
        else
            return new AbstractInteger.Big(val);
    } 
    public Integer valueOf(long val) {
        return -MAX_CONSTANT <= val && val <= MAX_CONSTANT
            ? valueOf((int) val)
            : new AbstractInteger.Big(val);
    }

    // vector constructors and conversion utilities
         
    //@todo couldn't we even return Vector<Real>?
    public Vector valueOf(double[] values) {
        return new RVector(values);
    } 

    // matrix constructors and conversion utilities

    public Matrix valueOf(double[][] values) {
        return new RMatrix(values);
    } 
}
