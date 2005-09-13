/**
 * @(#)PointwiseArithmetic.java 0.9 2001/03/04 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.aspects;

import orbital.math.Arithmetic;

import orbital.math.functional.Operations;
import orbital.math.functional.Functionals;

import orbital.math.Values;

/**
 * Base aspect implementing default arithmetic operations for (math) functors.
 * It is implementing pointwise arithmetic operations.
 * <p>
 * For an arithmetic operation &#8902;:B&times;B&rarr;B this will be a pointwise composition
 * of the operation &#8728; with the functor operands
 * <center>&#8902;:Map(A,B)&times;Map(A,B)&rarr;Map(A,B); (f,g) &#8614; f &#8902; g: A&rarr;B; x &#8614; (f &#8902; g)(x) := f(x) &#8902; g(x)</center>
 * </p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.math.MathFunctor.AbstractFunctor
 */
aspect PointwiseArithmetic /*implements Operations*/ of eachJVM() {
    /**
     * Introduce the arithmetic operation implementations.
     * <p>
     * For code efficiency the methods introduced delegate to one single static method in
     * PointwiseArithmetic class.</p>
     */
    introduction(PointwiseTest) {
  
        implements Arithmetic;
  
    	// pointwise Arithmetic implementation
    	public Arithmetic add(Arithmetic b) throws ArithmeticException {
    		return PointwiseArithmetic.aspectOf().plus(this, b);
    	} 
    	public Arithmetic minus() throws ArithmeticException {
    		return PointwiseArithmetic.aspectOf().minus(this);
    	} 
    	public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
    		return PointwiseArithmetic.aspectOf().subtract(this, b);
    	} 
    
    	public Arithmetic multiply(Arithmetic b) throws ArithmeticException {
    		return PointwiseArithmetic.aspectOf().times(this, b);
    	} 
    	public Arithmetic inverse() throws ArithmeticException {
    		return PointwiseArithmetic.aspectOf().inverse(this);
    	} 
    	public Arithmetic divide(Arithmetic b) throws ArithmeticException {
    		return PointwiseArithmetic.aspectOf().divide(this, b);
    	} 
    
    	public Arithmetic power(Arithmetic b) throws ArithmeticException {
    		return PointwiseArithmetic.aspectOf().power(this, b);
    	} 
    
    	public double norm() {
    		// throw new UnsupportedOperationException("maximum norm for functions is not implemented");
    		return Double.NaN;
    	} 
  
	}

	// pointwise Arithmetic implementation
	// pointwise Arithmetic implementation (identical to @see orbital.math.functional.MathFunctor.AbstractFunctor, ...)
	public static Arithmetic plus(Arithmetic a, Arithmetic b) throws ArithmeticException {
		// simple-case optimization
		if (b instanceof Scalar)
			if (Values.valueOf(0).equals(b))
				return a;
		return Functionals.genericCompose(Operations.plus, a, b);
	} 
	public static Arithmetic minus(Arithmetic a) throws ArithmeticException {
		return Functionals.genericCompose(Operations.minus, a);
	} 
	public static Arithmetic subtract(Arithmetic a, Arithmetic b) throws ArithmeticException {
		// simple-case optimization
		if (b instanceof Scalar)
			if (Values.valueOf(0).equals(b))
				return a;
		return Functionals.genericCompose(Operations.subtract, a, b);
	} 

	public static Arithmetic times(Arithmetic a, Arithmetic b) throws ArithmeticException {
		// simple-case optimization
		if (b instanceof Scalar) {
    		if (Values.valueOf(1).equals(b))
    			return a;
    		else if (Values.valueOf(-1).equals(b))
    			return a.minus();
    		else if (Values.valueOf(0).equals(b))
    			return Values.valueOf(0);
		}
		return Functionals.genericCompose(Operations.times, a, b);
	} 
	public static Arithmetic inverse(Arithmetic a) throws ArithmeticException {
		return Functionals.genericCompose(Operations.inverse, a);
	} 
	public static Arithmetic divide(Arithmetic a, Arithmetic b) throws ArithmeticException {
		// simple-case optimization
		if (b instanceof Scalar) {
    		if (Values.valueOf(1).equals(b))
    			return a;
    		else if (Values.valueOf(-1).equals(b))
    			return a.minus();
    		else if (Values.valueOf(0).equals(b))
    			throw new ArithmeticException("division by zero");
		}
		return Functionals.genericCompose(Operations.divide, a, b);
	} 

	public static Arithmetic power(Arithmetic a, Arithmetic b) throws ArithmeticException {
		// simple-case optimization
		if (b instanceof Scalar) {
    		if (Values.valueOf(1).equals(b))
    			return a;
    		else if (Values.valueOf(-1).equals(b))
    			return a.inverse();
		}
		return Functionals.genericCompose(Operations.power, a, b);
	} 
}
