/**
 * @(#)AbstractReal.java 1.0 1999/08/16 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.math;


import orbital.math.functional.Operations;

abstract class AbstractReal extends AbstractComplex implements Real {
    private static final long serialVersionUID = -4117614439306224843L;

    protected AbstractReal() {}

    public boolean equals(Object o) {
    	if (Real.isa.apply(o)) {
	    Real b = (Real) o;
	    //@see Double#compare(double,double)
	    return java.lang.Double.doubleToLongBits(doubleValue()) == java.lang.Double.doubleToLongBits(b.doubleValue());
	} else
	    return super.equals(o);
    }

    public int hashCode() {
	//@see Double#hashCode()
	long bits = java.lang.Double.doubleToLongBits(doubleValue());
	return (int)(bits ^ (bits >>> 32));
    }

    public Real norm() {
	return Values.valueOf(Math.abs(doubleValue()));
    } 

    // order
    public int compareTo(Object o) {
	return compareToImpl(o);
    }
    final int compareToImpl(Object o) {
	//@xxx avoid senseless constructor calls and copy implementation from java.lang.Double.compareTo(Double)
	//@see Double#compare(double,double)
	return new java.lang.Double(doubleValue()).compareTo(new java.lang.Double(((Real) o).doubleValue()));
    } 

    //TODO: optimize using direct + for all Scalars except Complex
    public Arithmetic add(Arithmetic b) {
	if (b instanceof Real)
	    return add((Real) b);
	return (Arithmetic) Operations.plus.apply(this, b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	if (b instanceof Real)
	    return subtract((Real) b);
	return (Arithmetic) Operations.subtract.apply(this, b);
    } 
    public Arithmetic multiply(Arithmetic b) {
	if (b instanceof Real)
	    return multiply((Real) b);
	return (Arithmetic) Operations.times.apply(this, b);
    } 
    public Arithmetic divide(Arithmetic b) {
	if (b instanceof Real)
	    return divide((Real) b);
	return (Arithmetic) Operations.divide.apply(this, b);
    } 
    public Arithmetic power(Arithmetic b) {
	if (b instanceof Real)
	    return power((Real) b);
	return (Arithmetic) Operations.power.apply(this, b);
    } 

    // overwrite complex
    public final Real re() {
	return this;
    } 
    final double realValue() {
	return doubleValue();
    } 

    public final Real im() {
	return Values.ZERO;
    } 

    final double imaginaryValue() {
	return 0;
    } 
    public final Complex conjugate() {
	return this;
    }
    // end of overwrite complex

    // delegate super class operations
    public Complex add(Complex b) {
	return (Complex) Operations.plus.apply(this, b);
    } 

    public Complex subtract(Complex b) {
	return (Complex) Operations.subtract.apply(this, b);
    } 

    public Complex multiply(Complex b) {
	return (Complex) Operations.times.apply(this, b);
    } 

    public Complex divide(Complex b) {
	return (Complex) Operations.divide.apply(this, b);
    } 


    //@xxx improve all implementations of add(*), subtract(*), etc. to work reliably with new type hierarchy and auto conversion
    
    /**
     * Represents a real number in <b>R</b> as a double value.
     * 
     * @version 1.0, 1999/08/16
     * @author  Andr&eacute; Platzer
     */
    static class Double extends AbstractReal {
    	private static final long serialVersionUID = 2011638443547790678L;
    
    	/**
    	 * the real value (with machine-sized double-precision, only, of course).
    	 * @serial
    	 */
    	private double			 value;
    	public Double(double v) {
	    value = v;
    	}
    	public Double(Number v) {
	    value = v.doubleValue();
    	}
    
    	public Object clone() {
	    return new Double(doubleValue());
    	} 

    	public double doubleValue() {
	    return value;
    	} 
    
    	public Real add(Real b) {
	    //@todo check whether b is a big arbitrary precision real
	    return new Double(doubleValue() + b.doubleValue());
    	}
    	public Real subtract(Real b) {
	    return new Double(doubleValue() - b.doubleValue());
    	}
    	public Arithmetic minus() {
	    return new Double(-doubleValue());
    	} 
    	public Real multiply(Real b) {
	    return new Double(doubleValue() * b.doubleValue());
    	}
    	public Real divide(Real b) {
	    return new Double(doubleValue() / b.doubleValue());
    	}
    	public Real power(Real b) {
	    return new Double(Math.pow(doubleValue(), b.doubleValue()));
    	}
    	public Arithmetic inverse() {
	    return new Double(1 / doubleValue());
    	} 

    	public Arithmetic add(Arithmetic b) {
	    if (b instanceof Real)
		return add((Real) b);
	    return super.add(b);
    	} 
    	public Arithmetic subtract(Arithmetic b) {
	    if (b instanceof Real)
		return subtract((Real) b);
	    return super.subtract(b);
    	} 
    	public Arithmetic multiply(Arithmetic b) {
	    if (b instanceof Real)
		return multiply((Real) b);
	    return super.multiply(b);
    	} 
    	public Arithmetic divide(Arithmetic b) {
	    if (b instanceof Real)
		return divide((Real) b);
	    return super.divide(b);
    	} 
    	public Arithmetic power(Arithmetic b) {
	    if (b instanceof Real)
		return power((Real) b);
	    return super.power(b);
    	} 
    }

    /**
     * Represents a real number in <b>R</b> as a float value.
     * 
     * @version 1.0, 1999/08/16
     * @author  Andr&eacute; Platzer
     */
    static class Float extends AbstractReal {
    	private static final long serialVersionUID = -206414766833581552L;
    
    	/**
    	 * the real value (with machine-sized float-precision, only, of course).
    	 * @serial
    	 */
    	private float			 value;
    	public Float(float v) {
	    value = v;
    	}
    	public Float(Number v) {
	    value = v.floatValue();
    	}
    
    	public Object clone() {
	    return new Float(floatValue());
    	} 

    	public float floatValue() {
	    return value;
    	} 

    	public double doubleValue() {
	    return value;
    	} 
    
    	public Real add(Real b) {
	    //@xxx what's up with b being an Integer.Int or Integer.Long?
	    if (b instanceof Float)
		return new Float(floatValue() + b.floatValue());
	    else if (b instanceof Double)
		//optimized widening
		return new Double(floatValue() + b.doubleValue());
	    return (Real) Operations.plus.apply(this, b);
	    //@todo check whether b is a big double precision real
    	}
    	public Real subtract(Real b) {
	    if (b instanceof Float)
		return new Float(floatValue() - b.floatValue());
	    else if (b instanceof Double)
		//optimized widening
		return new Double(floatValue() - b.doubleValue());
	    return (Real) Operations.subtract.apply(this, b);
    	}
    	public Arithmetic minus() {
	    return new Float(-floatValue());
    	} 
    	public Real multiply(Real b) {
	    if (b instanceof Float)
		return new Float(floatValue() * b.floatValue());
	    else if (b instanceof Double)
		//optimized widening
		return new Double(floatValue() * b.doubleValue());
	    return (Real) Operations.times.apply(this, b);
    	}
    	public Real divide(Real b) {
	    if (b instanceof Float)
		return new Float(floatValue() / b.floatValue());
	    else if (b instanceof Double)
		//optimized widening
		return new Double(floatValue() / b.doubleValue());
	    return (Real) Operations.divide.apply(this, b);
    	}
    	public Real power(Real b) {
	    if (b instanceof Float)
		return new Float((float) Math.pow(floatValue(), b.floatValue()));
	    else if (b instanceof Double)
		//optimized widening
		return new Double(Math.pow(floatValue(), b.doubleValue()));
	    return (Real) Operations.power.apply(this, b);
    	}
    	public Arithmetic inverse() {
	    return new Float(1 / floatValue());
    	} 
    }
}
