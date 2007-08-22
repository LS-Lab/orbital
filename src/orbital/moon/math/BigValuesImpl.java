/**
 * @(#)BigValuesImpl.java 1.2 2007-08-21 Andre Platzer
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
 * Default value constructor factory using big arbitrary precision arithmetics only.
 * Also activates partial caching/sharing of numer constants.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @invariant all Scalars returned by this class or contained in objects returned by this class are implements Big
 */
public class BigValuesImpl extends ValuesImpl {
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
        posConst[0] = negConst[0] = new AbstractInteger.Big(0);
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            posConst[i] = new AbstractInteger.Big(i);
            negConst[i] = new AbstractInteger.Big(-i);
        } 
    } 

    // instantiation

    public BigValuesImpl() {}

    // scalar value constructors - facade factory
    // primitive type conversion methods

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
    public Integer valueOf(byte val) {
        return valueOf((int) val);
    }
    public Integer valueOf(short val) {
        return valueOf((int) val);
    }
    public Integer valueOf(java.math.BigInteger val) {
        return new AbstractInteger.Big(val);
    }

    // real scalar value constructors - facade factory

    public Real valueOf(double val) {
        return new AbstractReal.Big(val);
    } 
    public Real valueOf(float val) {
        return valueOf((double) val);
    } 
    public Real valueOf(java.math.BigDecimal val) {
        return new AbstractReal.Big(val);
    }

    /*
     * optimized to only have Big precision such that we only have to
     * respect the type hierarchy but not the precision hierarchy
     * @todo optimize hotspot
     */
    public Scalar[] minimumCoerced(Number a, Number b) {
        //@xxx adapt better to new Complex>Real>Rational>Integer type hierarchy and conform to a new OBDD (ordered binary decision diagram)
        //@todo partial order with Arithmetic>Scalar>Complex>Real>Rational>Integer and greatest common super type of A,B being A&cup;B = sup {A,B}
        //@todo implement sup along with conversion routines. Perhaps introduce "int AbstractScalar.typeLevel()" and "int AbstractScalar.precisionLevel()" such that we can compute the maximum level of both with just two method calls. And introduce "Object AbstractScalar.convertTo(int typeLevel, int precisionLevel)" for conversion.
        if (Complex.hasType.apply(a) || Complex.hasType.apply(b))
            return new Complex[] {
                Complex.hasType.apply(a) ? (Complex) a : new AbstractComplex.ComplexImpl(a), Complex.hasType.apply(b) ? (Complex) b : new AbstractComplex.ComplexImpl(b)
            };

        // this is a tricky binary decision diagram (optimized), see documentation
        if (Integer.hasType.apply(a)) {
            if (Integer.hasType.apply(b))
                return new Integer[] {
		    new AbstractInteger.Big(a), new AbstractInteger.Big(b)
		};
        } else {        // a is no integer
            if (!Rational.hasType.apply(a))
                return new Real[] {
		    new AbstractReal.Big(a), new AbstractReal.Big(b)
		};
        }
	assert Integer.hasType.apply(a) && !Integer.hasType.apply(b) || Rational.hasType.apply(a) : a + " integer or rational but not both integers " + a + " and " + b;
        
        /* fall-through: all other cases come here */
        if (Rational.isa.apply(b))
            return new Rational[] {
                Rational.hasType.apply(a) ? (Rational) a : rational((Integer)a),
		Rational.hasType.apply(b) ? (Rational) b : rational((Integer)b)
            };
	return new Real[] {
	    new AbstractReal.Big(a), new AbstractReal.Big(b)
	};
    } 

}
