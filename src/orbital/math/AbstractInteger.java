/**
 * @(#)AbstractInteger.java 1.0 2000/08/03 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;


import orbital.math.functional.Operations;

// @todo non-associative precisions: note that Long+Int and Int+Long etc. may differ due to brutal casting per b.intValue() or even b.longValue().
abstract class AbstractInteger extends AbstractRational implements Integer {
    private static final long serialVersionUID = -5859818959999970653L;
    protected AbstractInteger() {}
    
    // order
    public int compareTo(Object o) {
    	if (Integer.isa.apply(o)) {
	    // faster compare
	    long thisVal = this.longValue();
	    long anotherVal = ((Integer) o).longValue();
	    return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	} else
	    return compareToImpl(o);
    } 

    // Arithmetic implementation synonyms
    public Arithmetic add(Arithmetic b) {
	if (b instanceof Integer)
	    return add((Integer) b);
	return (Arithmetic) Operations.plus.apply(this, b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	if (b instanceof Integer)
	    return subtract((Integer) b);
	return (Arithmetic) Operations.subtract.apply(this, b);
    } 
    public Arithmetic multiply(Arithmetic b) {
	if (b instanceof Integer)
	    return multiply((Integer) b);
	return (Arithmetic) Operations.times.apply(this, b);
    } 
    public Arithmetic divide(Arithmetic b) {
	if (b instanceof Integer) {
	    assert java.lang.Integer.MIN_VALUE <= longValue() && longValue() <= java.lang.Integer.MAX_VALUE && java.lang.Integer.MIN_VALUE <= ((Integer) b).longValue() && ((Integer) b).longValue() <= java.lang.Integer.MAX_VALUE : "avoid possible loss of precision";
	    final Values vf = Values.getDefaultInstance();
	    return vf.narrow(vf.rational((int) intValue(), ((Integer) b).intValue()));
	} 
	return (Arithmetic) Operations.divide.apply(this, b);
    } 
    public Arithmetic inverse() {
	assert java.lang.Integer.MIN_VALUE <= longValue() && longValue() <= java.lang.Integer.MAX_VALUE : "possible loss of precision";
	return Values.getDefaultInstance().rational(1, intValue());
    } 
    public Arithmetic power(Arithmetic b) {
	if (b instanceof Integer)
	    return power((Integer) b);
	return (Arithmetic) Operations.power.apply(this, b);
    } 

    // overwrite rational
    public final Integer numerator() {
	return this;
    }
    final int numeratorValue() {
	return intValue();
    }
    public final Integer denominator() {
	return Values.ONE;
    }
    final int denominatorValue() {
	return 1;
    }
    public final Rational representative() {
	return this;
    }
    // end of overwrite rational

    // Euclidean
    public Integer degree() {
	return (Integer) norm();
    }

    // delegate super class operations
    public Rational add(Rational b) {
	return (Rational) Operations.plus.apply(this, b);
    } 

    public Rational subtract(Rational b) {
	return (Rational) Operations.subtract.apply(this, b);
    } 

    public Rational multiply(Rational b) {
	return (Rational) Operations.times.apply(this, b);
    } 

    public Rational divide(Rational b) {
	return (Rational) Operations.divide.apply(this, b);
    } 

    public Rational power(Rational b) {
	return (Rational) Operations.power.apply(this, b);
    } 


    /**
     * Represents an integer number in <b>Z</b> as an int value.
     * 
     * @version 1.0, 2000/08/03
     * @author  Andr&eacute; Platzer
     * @see java.lang.Integer
     * @internal note we do not provide a faster hashCode() implementation via longValue(), since new Real(0) and new Integer(0) will have different hashes then, although they are equal.
     */
    static class Int extends AbstractInteger {
    	private static final long serialVersionUID = -6566214738338401035L;
    	/**
    	 * the value
    	 * @serial
    	 */
    	private int value;
    	public Int(int v) {
	    value = v;
    	}
    	public Int(Number v) {
	    value = v.intValue();
    	}
        
    	public Object clone() {
	    return new Int(intValue());
    	} 

    	public int intValue() {
	    return value;
    	} 
    	public double doubleValue() {
	    return intValue();
    	} 
    
	public Real norm() {
	    return Values.getDefaultInstance().valueOf(Math.abs(intValue()));
	} 

    	// Arithmetic implementation synonyms
    	public Integer add(Integer b) {
	    if (b instanceof Int)
		return new Int(intValue() + b.intValue());
	    else if (b instanceof Long)
		return new Long(intValue() + b.longValue());
	    return (Integer) Operations.plus.apply(this, b);
    	} 
    	public Integer subtract(Integer b) {
	    if (b instanceof Int)
		return new Int(intValue() - b.intValue());
	    else if (b instanceof Long)
		return new Long(intValue() - b.longValue());
	    return (Integer) Operations.subtract.apply(this, b);
    	} 
    	public Arithmetic minus() {
	    return new Int(-value);
    	} 
    	public Integer multiply(Integer b) {
	    if (b instanceof Int)
		return new Int(intValue() * b.intValue());
	    else if (b instanceof Long)
		return new Long(intValue() * b.longValue());
	    return (Integer) Operations.times.apply(this, b);
    	} 
    	public Integer power(Integer b) {
	    if (b instanceof Integer)
		return new Int((int) Math.pow(intValue(), b.intValue()));
	    else if (b instanceof Long)
		return new Long((long) Math.pow(intValue(), b.longValue()));
	    return (Integer) Operations.power.apply(this, b);
    	} 

    	// Euclidean implementation
    	public Integer quotient(Integer b) {
	    return new Int(intValue() / b.intValue());
    	}
    	public Euclidean quotient(Euclidean b) {
	    return quotient((Integer)b);
    	}
    	public Integer modulo(Integer b) {
	    int m = b.intValue();
	    int v = intValue() % m;
	    //@internal assure mathematical nonnegative modulus
	    if (v < 0)
		v += m;
	    assert (v - (intValue() % m)) % m == 0 : "change of canonical representative, only";
	    assert v >= 0 : "nonnegative representative chosen";
	    return new Int(v);
    	}
    	public Euclidean modulo(Euclidean b) {
	    return modulo((Integer)b);
    	}
    }

    /**
     * Represents an integer number in <b>Z</b> as a long value.
     * 
     * @version 1.0, 2000/08/03
     * @author  Andr&eacute; Platzer
     * @see java.lang.Long
     * @internal note we do not provide a faster hashCode() implementation via longValue(), since new Real(0) and new Integer(0) will have different hashes then, although they are equal.
     */
    static class Long extends AbstractInteger {
    	private static final long serialVersionUID = 6559525715511278001L;
    	/**
    	 * the value
    	 * @serial
    	 */
    	private long value;
    	public Long(long v) {
	    value = v;
    	}
    	public Long(Number v) {
	    value = v.longValue();
    	}
        
    	public Object clone() {
	    return new Long(longValue());
    	} 

    	public long longValue() {
	    return value;
    	} 
    	public double doubleValue() {
	    return longValue();
    	} 
    
	public Real norm() {
	    return Values.getDefaultInstance().valueOf(Math.abs(longValue()));
	} 

    	// Arithmetic implementation synonyms
    	public Integer add(Integer b) {
	    return new Long(longValue() + b.longValue());
    	}
    	public Integer subtract(Integer b) {
	    return new Long(longValue() - b.longValue());
    	}
    	public Arithmetic minus() {
	    return new Long(-value);
    	} 
    	public Integer multiply(Integer b) {
	    return new Long(longValue() * b.longValue());
    	}
    	public Integer power(Integer b) {
	    return new Long((long) Math.pow(longValue(), b.longValue()));
    	}
    	
    	// Euclidean implementation
    	public Integer quotient(Integer b) {
	    return new Long(longValue() / b.longValue());
    	}
    	public Euclidean quotient(Euclidean b) {
	    return quotient((Integer)b);
    	}
    	public Integer modulo(Integer b) {
	    long m = b.longValue();
	    long v = longValue() % m;
	    //@internal assure mathematical nonnegative modulus
	    if (v < 0)
		v += m;
	    assert (v - (longValue() % m)) % m == 0 : "change of canonical representative, only";
	    assert v >= 0 : "nonnegative representative chosen";
	    return new Long(v);
    	}
    	public Euclidean modulo(Euclidean b) {
	    return modulo((Integer)b);
    	}
    }
}
