/**
 * @(#)AbstractSymbol.java 1.0 2000/08/11 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import java.io.Serializable;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;

import orbital.math.functional.Function;
import orbital.util.Utility;

/**
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.math.functional.MathFunctor.AbstractFunctor
 * @XXX: think about constant and doubly definition of pointwise operations
 */
class AbstractSymbol /*extends Functions.constant(signifier)*/ implements Symbol, Serializable {
    // maybe implement with (a stack/tree of) postfix operations
    // and print with infix-traversal
    // or define x+y as a function that has arithmetic behaviour (better do)
    private static final long serialVersionUID = -3807941418810639427L;
    
    /**
     * the symbols signifier
     * @serial
     */
    private String signifier;

        private final ValueFactory valueFactory;
    public AbstractSymbol(String signifier, ValueFactory vf) {
        this.signifier = signifier;
        this.valueFactory = vf;
    }
    
    public ValueFactory valueFactory() { return valueFactory; }

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
        return tolerance.isInfinite() && tolerance.compareTo(tolerance.zero()) > 0 ? true : equals(o);
    }

    public int hashCode() {
        return Utility.hashCode(signifier);
    } 

    //@internal we are never zero or one as these are Integers but not Symbols
    public boolean isZero() {return false;}
    public boolean isOne() {return false;}
    
    // Arithmetic implementation

    public Arithmetic zero() {return valueFactory().ZERO();}
    public Arithmetic one() {return valueFactory().ONE();}

    //XXX: pointwise Arithmetic implementation (identical to @see orbital.math.functional.MathFunctor.AbstractFunctor)
    public Arithmetic add(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b.isZero())
            return this;
        return Functionals.genericCompose(Operations.plus, this, b);
    } 
    public Arithmetic minus() throws ArithmeticException {
        return Functionals.genericCompose(Operations.minus, this);
    } 
    public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b.isZero())
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
            if (b.isZero())
                return zero();
            else if (b.isOne())
                return this;
            else if (b.one().minus().equals(b))
                return minus();
        }
        return Functionals.genericCompose(Operations.times, this, b);
    } 
    public Arithmetic inverse() throws ArithmeticException {
        return Functionals.genericCompose(Operations.inverse, this);
    } 
    public Arithmetic divide(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b instanceof Scalar) {
            if (b.isZero())
                throw new ArithmeticException("division by zero");
            else if (b.isOne())
                return this;
            else if (b.one().minus().equals(b))
                return minus();
        }
        return Functionals.genericCompose(Operations.divide, this, b);
    } 

    public Arithmetic power(Arithmetic b) throws ArithmeticException {
        // simple-case optimization
        if (b instanceof Scalar) {
            if (b.isZero()) {
                if (isZero()) {
                    assert false : "this never happens as symbols are not identical to 0";
                    throw new ArithmeticException("0^0 is not uniquely defined");
                } else
                    return one();
            } else if (b.isOne())
                return this;
            else if (b.one().minus().equals(b))
                return inverse();
        }
        return Functionals.genericCompose(Operations.power, this, b);
    }

    public Real norm() {
        //@xxx or should we  return Functions.norm.apply(this), which isn't a real?
        return valueFactory().NaN();
    } 

    public String toString() {
        return ArithmeticFormat.getDefaultInstance().format(this);
    } 
}
