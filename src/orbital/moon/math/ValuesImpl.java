/**
 * @(#)ValuesImpl.java 1.1 2002-12-06 Andre Platzer
 * 
 * Copyright (c) 2000-2002 Andre Platzer. All Rights Reserved.
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
 * Default value constructor factory.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo Think about using a two-dimensional dynamic dispatch in two variables for
 * +:RxS->T. with plus[inf{r.typeId(),s.typeId()}][inf{r.precisionId(),s.precisionId()}]
 * Use internal interfaces orbital.moon.math.TypeIdentified {int typeId();}
 * This way we achieve a prioritized rule-based system for addition operation "rules".
 * Also achieve fast normalizers with one dimensional dynamich dispatch in two variables
 * getValueFactory().getNormalizer().apply((Arithmetic)x) performs internalTypeNormalizer[x.typeId()].apply(x) which only gets called with object of one fitting type.
 */
public class ValuesImpl extends ArithmeticValuesImpl {
    // instantiation

    public ValuesImpl() {}

    // Constants

    public Integer ZERO() {
        return ZEROImpl;
    }
    private final Integer ZEROImpl = valueOf(0);

    public Integer ONE() {
        return ONEImpl;
    }
    private final Integer ONEImpl = valueOf(1);

    public Integer MINUS_ONE() {
        return MINUS_ONEImpl;
    }
    private final Integer MINUS_ONEImpl = valueOf(-1);

    public Real POSITIVE_INFINITY() {
        return POSITIVE_INFINITYImpl;
    }
    private final Real POSITIVE_INFINITYImpl = valueOf(java.lang.Double.POSITIVE_INFINITY);

    public Real NEGATIVE_INFINITY() {
        return NEGATIVE_INFINITYImpl;
    }
    private final Real NEGATIVE_INFINITYImpl = valueOf(java.lang.Double.NEGATIVE_INFINITY);

    public Real PI() {
        return PIImpl;
    }
    private final Real PIImpl = valueOf(Math.PI);
    public Real E() {
        return EImpl;
    }
    private final Real EImpl = valueOf(Math.E);

    public Real NaN() {
        return NaNImpl;
    }
    private final Real NaNImpl = valueOf(java.lang.Double.NaN);

    public Complex I() {
        return IImpl;
    }
    private final Complex IImpl = complex(0, 1);
    public Complex i() {
        return IImpl;
    }

    public Complex INFINITY() {
        return INFINITYImpl;
    }
    private final Complex INFINITYImpl = complex(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.NaN);


    // scalar value constructors - facade factory
    // primitive type conversion methods

    // integer scalar value constructors - facade factory

    public Integer valueOf(int val) {
        return new AbstractInteger.Int(val);
    } 
    public Integer valueOf(long val) {
        return new AbstractInteger.Long(val);
    }
    public Integer valueOf(java.math.BigInteger val) {
        return new AbstractInteger.Big(val);
    }

    // real scalar value constructors - facade factory

    public Real valueOf(double val) {
        return new AbstractReal.Double(val);
    } 
    public Real valueOf(float val) {
        //@xxx return new AbstractReal.Float(val)
        return new AbstractReal.Double(val);
    } 
    public Real valueOf(java.math.BigDecimal val) {
        return new AbstractReal.Big(val);
    }

    // "named" scalar value constructors

    public Rational rational(Integer p, Integer q) {
        return new AbstractRational.Impl(p, q);
    } 

    // complex scalar values constructors

    public Complex cartesian(Real a, Real b) {
        return new AbstractComplex.Impl(a, b);
    } 
    public Complex cartesian(double a, double b) {
        return new AbstractComplex.Double(a, b);
    } 


    //

    public Scalar[] minimumCoerced(Number a, Number b) {
        //@todo optimize hotspot
        //@xxx adapt better to new Complex>Real>Rational>Integer type hierarchy and conform to a new OBDD (ordered binary decision diagram)
        //@todo partial order with Arithmetic>Scalar>Complex>Real>Rational>Integer and greatest common super type of A,B being A&cup;B = sup {A,B}
        //@todo implement sup along with conversion routines. Perhaps introduce "int AbstractScalar.typeLevel()" and "int AbstractScalar.precisionLevel()" such that we can compute the maximum level of both with just two method calls. And introduce "Object AbstractScalar.convertTo(int typeLevel, int precisionLevel)" for conversion.
        //@xxx respect precisions, e.g. make Big sticky
        if (Complex.hasType.apply(a) || Complex.hasType.apply(b))
            return AbstractComplex.makeComplex(a, b);

        // this is a tricky binary decision diagram (optimized), see documentation
        if (Integer.hasType.apply(a)) {
            if (Integer.hasType.apply(b))
                return AbstractInteger.makeInteger(a, b);
            else if (Rational.hasType.apply(b))
                //@xxx inserted to fix the case Rational + Integer != Real of our BDD. Find a faster BDD solution!
                return new Rational[] {
                    rational((Integer)a),
                    (Rational) b
                };
        } else {        // a is no integer
            if (!Rational.hasType.apply(a))
                return AbstractReal.makeReal(a, b);
        } 
        assert Integer.hasType.apply(a) && !Integer.hasType.apply(b) || Rational.hasType.apply(a) : a + " integer or rational but not both integers " + a + " and " + b;
        
        /* fall-through: all other cases come here */
        if (Rational.isa.apply(b))
            return new Rational[] {
                Rational.hasType.apply(a) ? (Rational) a : rational((Integer)a),
                Rational.hasType.apply(b) ? (Rational) b : rational((Integer)b)
            };
        return AbstractReal.makeReal(a, b);
    } 
}
