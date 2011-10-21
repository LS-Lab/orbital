/**
 * @(#)FastValuesImpl.java 1.1 2007-08-21 Andre Platzer
 * 
 * Copyright (c) 2000-2007 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    private  Integer posConst[] = new Integer[MAX_CONSTANT + 1];
    private Integer negConst[] = new Integer[MAX_CONSTANT + 1];

    // instantiation

    public FastValuesImpl() {
                // this static initialization somehow is not yet executed before the super constructor is called. Move constants here, thus lazy init cache too
        initCache(); 
    }

        private void initCache() {
                // these too steps shouldn't be necessary but they are
            this.posConst = new Integer[MAX_CONSTANT + 1];
            this.negConst = new Integer[MAX_CONSTANT + 1];
        posConst[0] = negConst[0] = new AbstractInteger.Long(0, this);
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            posConst[i] = new AbstractInteger.Long(i, this);
            negConst[i] = new AbstractInteger.Long(-i, this);
        }
        }

    // scalar value constructors - facade factory
    // primitive type conversion methods

    public Rational rational(int p, int q) {
        return new AbstractRational.Int(p, q, this);
    } 
    public Rational rational(int p) {
        return new AbstractRational.Int(p, this);
    } 

    public Complex cartesian(double a, double b) {
        return new AbstractComplex.Double(a, b, this);
    } 

    // integer scalar value constructors - facade factory

    public Integer valueOf(int val) {
        try {
                // If -MAX_CONSTANT < val < MAX_CONSTANT, return stashed constant
                if (0 <= val && val <= MAX_CONSTANT)
                        return posConst[val];
                else if (-MAX_CONSTANT <= val && val < 0)
                        return negConst[-val];
        } catch (NullPointerException ex) {
                initCache();
                return valueOf(val);
        }
        return new AbstractInteger.Int(val, this);
    } 
    public Integer valueOf(long val) {
        return -MAX_CONSTANT <= val && val <= MAX_CONSTANT
            ? valueOf((int) val)
            : new AbstractInteger.Long(val, this);
    }

    // vector constructors and conversion utilities
         
    //@todo couldn't we even return Vector<Real>?
    public Vector valueOf(double[] values) {
        return new RVector(values, this);
    } 

    // matrix constructors and conversion utilities

    public Matrix valueOf(double[][] values) {
        return new RMatrix(values, this);
    } 
    
    public Integer valueOf(BigInteger val) {
        // possible loss of precision (intended for fast)
        return valueOf(val.longValue());
    }
    public Real valueOf(BigDecimal val) {
        // possible loss of precision (intended for fast)
        return valueOf(val.doubleValue());
    }
}
