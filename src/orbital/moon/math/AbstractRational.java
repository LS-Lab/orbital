/**
 * @(#)AbstractRational.java 1.0 2000/08/03 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;


import orbital.math.functional.Operations;
import orbital.util.Utility;

abstract class AbstractRational extends AbstractReal implements Rational {
    private static final long serialVersionUID = -9129018097987023600L;

    protected AbstractRational() {}

    public double doubleValue() {
	return (double) numeratorValue() / denominatorValue();
    } 

    //@todo think about these shortcuts to numerator().intValue() and dnominator().intValue(). They effectively prevent new subclasses of AbstractRational that wish to reuse some methods.
    abstract int numeratorValue();
    abstract int denominatorValue();

    public boolean equals(Object o) {
    	if (Rational.isa.apply(o)) {
	    return compareTo(o) == 0;
	} else
	    return super.equals(o);
    }

    public int hashCode() {
	return super.hashCode();
    }
    
    // order
    public int compareTo(Object o) {
	if (o instanceof AbstractRational) {
	    // optimized version of faster compare
	    AbstractRational b = (AbstractRational) o;
	    return numeratorValue() * b.denominatorValue() - b.numeratorValue() * denominatorValue();
	} else
	    if (Rational.isa.apply(o)) {
		// faster compare
		Rational b = (Rational) o;
		return numerator().multiply(b.denominator()).compareTo(b.numerator().multiply(denominator()));
	    } else
		return super.compareTo(o);
    } 

    public Real norm() {
	return Values.getDefaultInstance().rational((Integer)numerator().norm(), (Integer)denominator().norm());
    } 
    
    // Arithmetic implementation synonyms
    public Arithmetic add(Arithmetic b) {
	if (b instanceof Rational)
	    return add((Rational) b);
	return (Arithmetic) Operations.plus.apply(this, b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	if (b instanceof Rational)
	    return subtract((Rational) b);
	return (Arithmetic) Operations.subtract.apply(this, b);
    } 
    public Arithmetic multiply(Arithmetic b) {
	if (b instanceof Rational)
	    return multiply((Rational) b);
	return (Arithmetic) Operations.times.apply(this, b);
    } 
    public Arithmetic divide(Arithmetic b) {
	if (b instanceof Rational)
	    return divide((Rational) b);
	return (Arithmetic) Operations.divide.apply(this, b);
    } 
    //@todo reintroduce once covariant return-types are allowed for Integer. public abstract Rational power(Integer b);
    Rational power_(Integer b) {
	return (Rational) Operations.power.apply(this, b);
    }
    public Arithmetic power(Arithmetic b) {
	if (b instanceof Integer) {
	    return power_((Integer) b);
	} else if (b instanceof Rational)
	    return (Arithmetic) Operations.power.apply(this, Values.getDefaultInstance().valueOf(((Number)b).doubleValue()));
	return (Arithmetic) Operations.power.apply(this, b);
    } 

    // delegate super class operations
    public Real add(Real b) {
	return (Real) Operations.plus.apply(this, b);
    } 

    public Real subtract(Real b) {
	return (Real) Operations.subtract.apply(this, b);
    } 

    public Real multiply(Real b) {
	return (Real) Operations.times.apply(this, b);
    } 

    public Real divide(Real b) {
	return (Real) Operations.divide.apply(this, b);
    } 

    public Real power(Real b) {
	return (Real) Operations.power.apply(this, b);
    } 


    /**
     * Represents a rational number p/q in <b>Q</b>.
     * The numerator p and denominator q component values are int integer values in <b>Z</b>,
     * and q&ne;0.
     *
     * @version 1.0, 2000/08/03
     * @author  Andr&eacute; Platzer
     */
    static class RationalImpl extends AbstractRational {
    	private static final long serialVersionUID = -8091750706034605583L;
    
    	/**
    	 * The numerator of the Rational.
    	 * @serial
    	 */
    	private int numerator;
    
    	/**
    	 * The denominator of the Rational.
    	 * @serial
    	 * @invariants denominator>0
    	 */
    	private int denominator;
    
    	/**
    	 * creates a new rational number p/q.
    	 * @param p the numerator of p/q.
    	 * @param q the denominator p/q.
    	 * @postconditions this == normalize(this)
    	 * @see #representative()
    	 */
    	public RationalImpl(int p, int q) {
	    // normalize
	    if (q < 0) {
		this.numerator = -p;
		this.denominator = -q;
	    } else {
		this.numerator = p;
		this.denominator = q;
	    }
    	}
    
    	/**
    	 * creates a new rational number with numerator part, only.
    	 */
    	public RationalImpl(int p) {
	    this(p, 1);
    	}
    
    	/**
    	 * creates a new rational number with numerator part, only.
    	 * @preconditions MathUtilities.isInteger(p.doubleValue()) && p.intValue() == p.longValue()
    	 */
    	public RationalImpl(Number p) {
	    this(p.intValue(), 1);
	    Utility.pre(MathUtilities.isInteger(p.doubleValue()) && p.intValue() == p.longValue(), "integer value only");
    	}
    
    	/**
    	 * creates a new rational number p/q.
    	 * @param p the numerator of p/q.
    	 * @param q the denominator p/q.
    	 * @preconditions MathUtilities.isInteger(p.doubleValue()) && p.intValue() == p.longValue() && MathUtilities.isInteger(q.doubleValue()) && q.intValue() == q.longValue()
    	 */
    	public RationalImpl(Number p, Number q) {
	    this(p.intValue(), q.intValue());
	    Utility.pre(MathUtilities.isInteger(p.doubleValue()) && p.intValue() == p.longValue()
			&& MathUtilities.isInteger(q.doubleValue()) && q.intValue() == q.longValue(), "integer value only");
    	}
    
    	/**
    	 * creates a rational zero.
    	 */
    	public RationalImpl() {
	    this(0, 1);
    	}
    	public Object clone() {
	    return new RationalImpl(numerator, denominator);
    	} 
    
    	public Integer numerator() {
	    return Values.getDefaultInstance().valueOf(numerator);
    	} 
    	int numeratorValue() {
	    return numerator;
    	} 
    
    	public Integer denominator() {
	    return Values.getDefaultInstance().valueOf(denominator);
    	} 
    	int denominatorValue() {
	    return denominator;
    	} 
    
    	// Arithmetic implementation synonyms
    	public Rational add(Rational b) {
	    final int b_denominator = validate(b.denominator()).intValue();
	    int m = MathUtilities.lcm(denominatorValue(), b_denominator);
	    int f1 = m / denominatorValue(), f2 = m / b_denominator;
	    return new RationalImpl(f1 * numeratorValue() + f2 * validate(b.numerator()).intValue(),
				    m).representative();
    	} 
    	public Rational subtract(Rational b) {
	    return add((Rational) b.minus());
    	} 
    	public Arithmetic minus() {
	    return new RationalImpl(-numeratorValue(), denominatorValue());
    	} 
    	public Rational multiply(Rational b) {
	    return new RationalImpl(numeratorValue() * validate(b.numerator()).intValue(),
				    denominatorValue() * validate(b.denominator()).intValue()).representative();
    	} 
    	public Rational divide(Rational b) {
	    return new RationalImpl(numeratorValue() * validate(b.denominator()).intValue(),
				    denominatorValue() * validate(b.numerator()).intValue()).representative();
    	} 
    	public Arithmetic inverse() {
	    return new RationalImpl(denominatorValue(), numeratorValue());
    	} 
    	public Rational power_(Integer b) {
	    long k = b.longValue();
	    return new RationalImpl((int) Math.pow(numeratorValue(), k), (int) Math.pow(denominatorValue(), k)).representative();
    	}

	public Rational representative() {
	    int		p = numeratorValue();
	    int		q = denominatorValue();
	    boolean changed = false;
	    if (q < 0) {
		// normalize
		changed = true;
		p = -p;
		q = -q;
	    }
	    // cancel
	    int d = MathUtilities.gcd(p, q);
	    return d != 1
		? new RationalImpl(p / d, q / d)
		: changed
		? new RationalImpl(p, q)
		: this;
    	} 
    	
    	private Integer validate(Integer i) {
	    if (i instanceof AbstractInteger.Long)
		//@xxx long is not supported, so this may lead to a loss of precision
		return i;
	    if (!(i instanceof AbstractInteger.Int))
		throw new UnsupportedOperationException("the precision of " + i.getClass() + " is currently not yet supported");
	    else
		return i;
    	}
    }
}
