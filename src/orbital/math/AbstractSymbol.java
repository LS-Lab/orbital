/**
 * @(#)AbstractSymbol.java 1.0 2000/08/11 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.io.Serializable;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;

import orbital.math.functional.Function;
import orbital.util.Utility;

/**
 * 
 * @version 1.0, 2000/08/11
 * @author  Andr&eacute; Platzer
 * @see orbital.math.functional.MathFunctor.AbstractFunctor
 * @XXX: think about constant and doubly definition of pointwise operations
 */
class AbstractSymbol /*extends Functions.constant(signifier)*/ implements Symbol, Serializable {
    // maybe implement with (a stack/tree of) postfix operations
    // and print with infix-traversal
    // or define x+y as a function that has arithmetic behaviour (better do)
    private static class Debug {
	private Debug() {}
	//@todo class Debug produces an error with gjc error: type parameter orbital.math.Arithmetic[] is not within its bound orbital.math.Arithmetic
	public static void main(String arg[]) throws Exception {
	    Matrix M = Values.valueOf(new Arithmetic[][] {
		{Values.symbol("a"), Values.symbol("b")},
		{Values.symbol("c"), Values.symbol("d")}
	    });
	    Vector v = Values.valueOf(new Arithmetic[] {
		Values.valueOf(1), Values.valueOf(2)
	    });
	    System.out.println(M + "*" + v + "=" + M.multiply(v));
	    System.out.println(M + "^-1 =\n" + M.inverse());
	    M = Values.valueOf(new Arithmetic[][] {
		{Values.valueOf(2), Values.symbol("a")},
		{Values.symbol("d"), Values.valueOf(4)}
	    });
	    v = Values.valueOf(new Arithmetic[] {
		Values.valueOf(1), Values.valueOf(2)
	    });
	    System.out.println(M + "*" + v + "=" + M.multiply(v));
	} 
    }	 // Debug
    
    
    private static final long serialVersionUID = -3807941418810639427L;
    
    /**
     * the symbols signifier
     * @serial
     */
    private String signifier;
    public AbstractSymbol(String signifier) {
	this.signifier = signifier;
    }

    public String getSignifier() {
	return signifier;
    } 
	
    public boolean isVariable() {
	//@todo
	return true;
    }

    public boolean equals(Object o) {
	if (!isa.apply(o))
	    return false;
	return Utility.equals(getSignifier(), ((Symbol) o).getSignifier());
    } 

    public boolean equals(Object o, Real tolerance) {
	return tolerance.isInfinite() && tolerance.compareTo(Values.ZERO) > 0 ? true : equals(o);
    }

    public int hashCode() {
	return Utility.hashCode(signifier);
    } 

    // Arithmetic implementation

    public Arithmetic zero() {return Values.ZERO;}
    public Arithmetic one() {return Values.ONE;}

    //XXX: pointwise Arithmetic implementation (identical to @see orbital.math.functional.MathFunctor.AbstractFunctor)
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

    public Arithmetic multiply(Arithmetic b) throws ArithmeticException {
	// simple-case optimization
	if (b instanceof Scalar) {
	    if (Values.ONE.equals(b))
		return this;
	    else if (Values.valueOf(-1).equals(b))
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
	    else if (Values.valueOf(-1).equals(b))
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
	    else if (Values.valueOf(-1).equals(b))
		return inverse();
	    else if (Values.ZERO.equals(b))
		return Values.ONE;
	}
	return Functionals.genericCompose(Operations.power, this, b);
    }

    public Real norm() {
	//@xxx or should we  return Functions.abs.apply(this)
	return Values.NaN;
    } 

    public String toString() {
	//XXX: if (logger.isLoggable(Level.FINER))
	// return '"' + signifier + '"';
	return signifier;
    } 
}
