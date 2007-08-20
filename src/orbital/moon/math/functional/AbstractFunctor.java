/**
 * @(#)AbstractFuntor..java 1.0 2001/11/21 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math.functional;

import orbital.math.functional.MathFunctor;
import orbital.math.Arithmetic;

import orbital.math.Metric;
import orbital.math.functional.Operations;
import orbital.math.functional.Functionals;
import orbital.math.functional.Functions;
import orbital.math.Scalar;
import orbital.math.Real;
import orbital.math.Values;

/**
 * Abstract base class implementing default arithmetic operations.
 * It is implementing the usual pointwise arithmetic operations.
 * <p>
 * For an arithmetic operation &#8902;:B&times;B&rarr;B this will be a pointwise composition
 * of the operation &#8728; with the functor operands
 * <center>&#8902;:Map(A,B)&times;Map(A,B)&rarr;Map(A,B); (f,g) &#8614; f &#8902; g: A&rarr;B; x &#8614; (f &#8902; g)(x) := f(x) &#8902; g(x)</center>
 * </p>
 *
 * @structure inherits MathFunctor
 * @structure implements Arithmetic
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo privatize?
 * @todo couldn't we transform this into a mere aspect? See orbital.moon.aspects.PointwiseArithmetic
 * @see orbital.logic.functor.Functionals#genericCompose(orbital.logic.functor.Function, Object)
 * @see orbital.logic.functor.Functionals#genericCompose(orbital.logic.functor.BinaryFunction, Object, Object)
 * @todo optimize away multiple calls to Values.valueOf(...)
 */
public abstract class AbstractFunctor implements MathFunctor {
    public boolean equals(Object o, Real tolerance) {
        return Metric.INDUCED.distance(this, (MathFunctor)o).compareTo(tolerance) < 0;
    }

    //@internal modulo equality of functions not being decidable and modulo 0 being represented as Function, BinaryFunction, ...
    public boolean isZero() {
    	return equals(zero());
    }
    public boolean isOne() {
    	return equals(one());
    }

    //@todo note that we have a problem of arities here. if our subclass is a Function then we should return the Function 0:A->B
    // however, if it is a BinaryFunction then we should return the BinaryFunction 0:A1*A2->B
    // perhaps if we just could define constant functions to be VoidFunction&cap;Function&cap;BinaryFunction this would all be more easy?
    public Arithmetic zero() {return Functions.zero;}
    public Arithmetic one() {return Functions.one;}
    // pointwise Arithmetic implementation
    // @see orbital.moon.aspects.PointwiseArithmeticFunction
    // pointwise Arithmetic implementation (identical to @see orbital.math.Symbol)
    public Arithmetic add(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b instanceof Scalar)
            if (Values.ZERO.equals(b))
                return this;
        return Functionals.genericCompose(Operations.plus, this, b);
    } 
    public Arithmetic minus() throws ArithmeticException {
        return Functionals.genericCompose(Operations.minus, this);
    } 
    public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b instanceof Scalar)
            if (Values.ZERO.equals(b))
                return this;
        return Functionals.genericCompose(Operations.subtract, this, b);
    } 
    
    public Arithmetic scale(Arithmetic alpha) throws ArithmeticException {
        //@xxx not quite right
        return multiply(alpha);
    }

    public Arithmetic multiply(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b instanceof Scalar) {
            if (Values.ONE.equals(b))
                return this;
            else if (Values.MINUS_ONE.equals(b))
                return minus();
            else if (Values.ZERO.equals(b))
                return Values.ZERO;
        }
        return Functionals.genericCompose(Operations.times, this, b);
    } 
    public Arithmetic inverse() throws ArithmeticException {
        return Functionals.genericCompose(Operations.inverse, this);
    } 
    public Arithmetic divide(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b instanceof Scalar) {
            if (Values.ONE.equals(b))
                return this;
            else if (Values.MINUS_ONE.equals(b))
                return minus();
            else if (Values.ZERO.equals(b))
                throw new ArithmeticException("division by zero");
        }
        return Functionals.genericCompose(Operations.divide, this, b);
    } 
    
    public Arithmetic power(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b instanceof Scalar) {
            if (Values.ONE.equals(b))
                return this;
            else if (Values.MINUS_ONE.equals(b))
                return inverse();
            else if (Values.ZERO.equals(b))
                return Values.ONE;
        }
        return Functionals.genericCompose(Operations.power, this, b);
    } 
    
    //@todo should we remove this default implementation?
    public Real norm() {
        return Values.NaN;
    } 
}
        
