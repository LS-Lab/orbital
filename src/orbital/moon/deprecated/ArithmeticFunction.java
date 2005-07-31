/**
 * @(#)ArithmeticFunction.java 0.9 2000/08/13 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.math.Arithmetic;

import orbital.math.Values;

// TODO: document

/**
 * ArithmeticFunction class (experimental).
 * <p>
 * ArithmeticFunctions f:A&rarr;B; x &#8614; f(x) form a vector space over K if the inner function returns elements in K.
 * ArithmeticFunctions form a field if and only if |A|=1 which is not a particulary exciting case.</p>
 * <p>
 * <b><i>Evolves</i>:</b> Could as well be unified with orbital.math.functional.Function
 * in this case we would need a class adapter for default arithmetic operations on functions.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @since Orbital1.0
 * @deprecated Integrated into orbital.math.functional.Function since Orbital1.0.
 */
public class ArithmeticFunction implements Arithmetic, Function {
    private Function function;
    public ArithmeticFunction(Function function) {
	this.function = function;
    }

    public Function getFunction() {
	return function;
    } 

    public Object apply(Object o) {
	return function.apply(o);
    } 
    
    public Function derive() {
	return arithmetized(function.derive());
    } 

    public Function integrate() {
	return arithmetized(function.integrate());
    } 

    // Arithmetic implementation
    public Arithmetic add(Arithmetic b) throws ArithmeticException {
	// simple-case optimization
	if (b instanceof Scalar)
	    if (Values.valueOf(0).equals(b))
		return this;
	return arithmetized(Functionals.genericCompose(Operations.plus, this, b));
    } 
    public Arithmetic minus() throws ArithmeticException {
	return arithmetized(Functionals.compose(Operations.minus, this));
    } 
    public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
	// simple-case optimization
	if (b instanceof Scalar)
	    if (Values.valueOf(0).equals(b))
		return this;
	return arithmetized(Functionals.genericCompose(Operations.subtract, this, b));
    } 

    public Arithmetic multiply(Arithmetic b) throws ArithmeticException {
	// simple-case optimization
	if (b instanceof Scalar) {
	    if (Values.valueOf(1).equals(b))
		return this;
	    else if (Values.valueOf(-1).equals(b))
		return minus();
	    else if (Values.valueOf(0).equals(b))
		return Values.valueOf(0);
	}
	return arithmetized(Functionals.genericCompose(Operations.times, this, b));
    } 
    public Arithmetic inverse() throws ArithmeticException {
	return arithmetized(Functionals.compose(Operations.inverse, this));
    } 
    public Arithmetic divide(Arithmetic b) throws ArithmeticException {
	// simple-case optimization
	if (b instanceof Scalar) {
	    if (Values.valueOf(1).equals(b))
		return this;
	    else if (Values.valueOf(-1).equals(b))
		return minus();
	    else if (Values.valueOf(0).equals(b))
		throw new ArithmeticException("division by zero");
	}
	return arithmetized(Functionals.genericCompose(Operations.divide, this, b));
    } 

    public Arithmetic power(Arithmetic b) throws ArithmeticException {
	// simple-case optimization
	if (b instanceof Scalar) {
	    if (Values.valueOf(1).equals(b))
		return this;
	    else if (Values.valueOf(-1).equals(b))
		return inverse();
	}
	return arithmetized(Functionals.genericCompose(Operations.power, this, b));
    } 

    public double norm() {
	// throw new UnsupportedOperationException("maximum norm for functions not implemented");
	return Double.NaN;
    } 

    public String toString() {
	return function + "";
    } 

    private static ArithmeticFunction arithmetized(Object o) {
	if (o instanceof ArithmeticFunction)
	    return (ArithmeticFunction)o;
	return new ArithmeticFunction((Function) o);
    } 
}
