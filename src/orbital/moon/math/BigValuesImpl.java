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
import java.math.MathContext;

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
public class BigValuesImpl extends ArithmeticValuesImpl {
    // Constants

    /**
     * Initialize static constant array when class is loaded.
     * @invariants 0 < MAX_CONSTANT < Integer.MAX_VALUE
     * @todo this static initialisation surprisingly will not be completed until the constructor call. Hence use instance variables instead.
     */
    /*private static final int     MAX_CONSTANT = 10;
    private static final Integer posConst[] = new Integer[MAX_CONSTANT + 1];
    private static final Integer negConst[] = new Integer[MAX_CONSTANT + 1];
    static {
        System.err.println("HERE");
        posConst[0] = negConst[0] = new AbstractInteger.Big(0);
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            posConst[i] = new AbstractInteger.Big(i);
            negConst[i] = new AbstractInteger.Big(-i);
        }
    }*/

    // instantiation

    private static final int     MAX_CONSTANT = 10;
    private final Integer posConst[] = new Integer[MAX_CONSTANT + 1];
    private final Integer negConst[] = new Integer[MAX_CONSTANT + 1];
    public BigValuesImpl() {
        posConst[0] = negConst[0] = new AbstractInteger.Big(0, this);
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            posConst[i] = new AbstractInteger.Big(i, this);
            negConst[i] = new AbstractInteger.Big(-i, this);
        }
        ZEROImpl = valueOf(0);
        ONEImpl = valueOf(1);
        MINUS_ONEImpl = valueOf(-1);
        POSITIVE_INFINITYImpl = new AbstractReal.Double(java.lang.Double.POSITIVE_INFINITY, this);
        NEGATIVE_INFINITYImpl = new AbstractReal.Double(java.lang.Double.NEGATIVE_INFINITY, this);
        PIImpl = valueOf(Math.PI);
        EImpl = valueOf(Math.E);
        NaNImpl = new AbstractReal.Double(java.lang.Double.NaN, this);
        IImpl = complex(0, 1);
        INFINITYImpl = new AbstractComplex.Double(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.NaN, this);
    }

    // Constants

    public Integer ZERO() {
        return ZEROImpl;
    }
    private Integer ZEROImpl;

    public Integer ONE() {
        return ONEImpl;
    }
    private Integer ONEImpl;

    public Integer MINUS_ONE() {
        return MINUS_ONEImpl;
    }
    private Integer MINUS_ONEImpl;

    public Real POSITIVE_INFINITY() {
        return POSITIVE_INFINITYImpl;
    }
    private Real POSITIVE_INFINITYImpl = new AbstractReal.Double(Double.POSITIVE_INFINITY, this);

    public Real NEGATIVE_INFINITY() {
        return NEGATIVE_INFINITYImpl;
    }
    private Real NEGATIVE_INFINITYImpl = new AbstractReal.Double(Double.NEGATIVE_INFINITY, this);

    public Real PI() {
        return PIImpl;
    }
    private Real PIImpl = valueOf(Math.PI);
    public Real E() {
        return EImpl;
    }
    private Real EImpl = valueOf(Math.E);

    public Real NaN() {
        return NaNImpl;
    }
    private Real NaNImpl = new AbstractReal.Double(Double.NaN, this);

    public Complex I() {
        return IImpl;
    }
    private Complex IImpl;
    public Complex i() {
        return IImpl;
    }

    public Complex INFINITY() {
        return INFINITYImpl;
    }
    private Complex INFINITYImpl = complex(POSITIVE_INFINITY(), NaN());


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
            return new AbstractInteger.Big(val, this);
    } 
    public Integer valueOf(long val) {
        return -MAX_CONSTANT <= val && val <= MAX_CONSTANT
            ? valueOf((int) val)
            : new AbstractInteger.Big(val, this);
    }
    public Integer valueOf(java.math.BigInteger val) {
        return new AbstractInteger.Big(val, this);
    }

    // real scalar value constructors - facade factory

    public Real valueOf(double val) {
        try {
            return new AbstractReal.Big(val, this);
        } catch (NumberFormatException ex) {
                if (val == Double.POSITIVE_INFINITY)
                        return POSITIVE_INFINITY();
                else if (val == Double.NEGATIVE_INFINITY)
                        return NEGATIVE_INFINITY();
                else if (Double.isNaN(val))
                        return NaN();
                else
                        throw ex;
        }
    } 
    public Real valueOf(java.math.BigDecimal val) {
        return new AbstractReal.Big(val, this);
    }

    public Rational rational(Integer p, Integer q) {
        return new AbstractRational.Impl(p, q, this);
    } 

    public Complex cartesian(Real a, Real b) {
        return new AbstractComplex.Impl(a, b, this);
    } 

    /*
     * optimized to only have Big precision such that we only have to
     * respect the type hierarchy but not the precision hierarchy
     * @todo optimize hotspot
     */
    public Scalar[] minimumCoerced(Number a, Number b) {
        final Scalar[] r = minimumCoercedImpl(a, b);
        assert isBig(r[0]) && isBig(r[1]) : "makes big arbitrary precision contents: " + r[0] + "@" + r[0].getClass() + (isBig(r[0]) ? "(big) " : "") + " and " + r[1] + "@" + r[1].getClass() + (isBig(r[1]) ? "(big) " : "") + " out of " + a + " and " + b;
        return r;
    }
    Arithmetic[] minimumCoerced(Arithmetic[] a, boolean commutative) {
        Arithmetic[] r = super.minimumCoerced(a, commutative);
        assert !(r[0] instanceof Scalar && r[1] instanceof Scalar) || isBig((Scalar)r[0]) && isBig((Scalar)r[1]) : "makes big arbitrary precision contents: " + r[0] + "@" + r[0].getClass() + (isBig((Scalar)r[0]) ? "(big) " : "") + " and " + r[1] + "@" + r[1].getClass() + (isBig((Scalar)r[1]) ? "(big) " : "") + " out of " + a[0] + " and " + a[1];
        return r;
    }
    private Scalar[] minimumCoercedImpl(Number a, Number b) {
        //@xxx adapt better to new Complex>Real>Rational>Integer type hierarchy and conform to a new OBDD (ordered binary decision diagram)
        //@todo partial order with Arithmetic>Scalar>Complex>Real>Rational>Integer and greatest common super type of A,B being A&cup;B = sup {A,B}
        //@todo implement sup along with conversion routines. Perhaps introduce "int AbstractScalar.typeLevel()" and "int AbstractScalar.precisionLevel()" such that we can compute the maximum level of both with just two method calls. And introduce "Object AbstractScalar.convertTo(int typeLevel, int precisionLevel)" for conversion.
        if (Complex.hasType.apply(a) || Complex.hasType.apply(b))
            return new Complex[] {
                Complex.hasType.apply(a) ? (Complex) makeBig(a) : new AbstractComplex.Impl(makeBig(a), this),
                Complex.hasType.apply(b) ? (Complex) makeBig(b) : new AbstractComplex.Impl(makeBig(b), this)
            };

        // this is a tricky binary decision diagram (optimized), see documentation
        if (Integer.hasType.apply(a)) {
            if (Integer.hasType.apply(b))
                return new Integer[] {
                    a instanceof AbstractInteger.Big ? (Integer)a : new AbstractInteger.Big(a, this),
                    b instanceof AbstractInteger.Big ? (Integer)b : new AbstractInteger.Big(b, this)
                };
        } else {        // a is no integer
            if (!Rational.hasType.apply(a))
                // a is no integer, no rational, no complex, hence real
                return new Real[] {
                    a instanceof AbstractReal.Big ? (Real)a : new AbstractReal.Big(a, this),
                    b instanceof AbstractReal.Big ? (Real)b : new AbstractReal.Big(b, this)
                };
        }
        assert Integer.hasType.apply(a) && !Integer.hasType.apply(b) || Rational.hasType.apply(a) : a + " integer or rational but not both integers " + a + " and " + b;
        
        /* fall-through: all other cases come here */
        if (Rational.isa.apply(b))
            return new Rational[] {
                Rational.hasType.apply(a) ? (Rational) makeBig(a) : rational((Integer)makeBig(a)),
                Rational.hasType.apply(b) ? (Rational) makeBig(b) : rational((Integer)makeBig(b))
            };
        return new Real[] {
            a instanceof AbstractReal.Big ? (Real)a : new AbstractReal.Big(a, this),
            b instanceof AbstractReal.Big ? (Real)b : new AbstractReal.Big(b, this)
        };
    }
    
    private Number makeBig(Number a) {
        if (a instanceof orbital.moon.math.Big || (a instanceof Scalar && isBig((Scalar)a))) {
                return a;
        } else if (a instanceof Integer) {
                return new AbstractInteger.Big(a, this);
        } else if (a instanceof Rational) {
                Rational r = (Rational) a;
                return (Number)rational((Integer)makeBig((Number)r.numerator()), (Integer)makeBig((Number)r.denominator()));
        } else if (a instanceof Real) {
                return new AbstractReal.Big(a, this);
        } else if (a instanceof Complex) {
                Complex r = (Complex) a;
                return (Number)complex((Real)makeBig((Number)r.re()), (Real)makeBig((Number)r.im()));
        } else
                throw new IllegalArgumentException("Don't know how to handle case " + a + "@" + a.getClass());
    }


    /**
     * Checks that a scalar is all big, i.e., all its number parts are bigs.
     */
    private static final boolean isBig(Scalar x) {
        if (x instanceof orbital.moon.math.Big) {
            return true;
        } else if (x instanceof Integer) {
            assert !(x instanceof orbital.moon.math.Big) : "already checked " + x.getClass();
                assert !(x instanceof AbstractInteger.Big) : "Big implementation hierarchy " + x.getClass();
            return false;
        } else if (x instanceof Rational) {
            Rational r = (Rational)x;
            return r.numerator() instanceof orbital.moon.math.Big && r.denominator() instanceof orbital.moon.math.Big;
        } else if (x instanceof Real) {
            assert !(x instanceof orbital.moon.math.Big) : "already checked " + x.getClass();
                assert !(x instanceof AbstractReal.Big) : "Big implementation hierarchy " + x.getClass();
            return false;
        } else if (x instanceof Complex) {
            Complex r = (Complex)x;
            return isBig(r.re()) && isBig(r.im());
        } else {
            System.err.println("ERROR:\t" + x + "@" + x.getClass() + " is not big");
            return false;
        }
    }
}
