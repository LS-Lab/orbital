/**
 * @(#)AbstractFraction.java 1.1 2002/06/18 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;


import orbital.math.functional.Operations;
import java.io.Serializable;

class AbstractFraction/*<M extends Arithmetic,S extends Arithmetic>*/ extends AbstractArithmetic implements Fraction/*<M,S>*/, Serializable {
    private static final long serialVersionUID = 5889020654090355567L;

    /**
     * The numerator of the fraction.
     * @serial
     */
    private Arithmetic/*>M<*/ numerator;
    
    /**
     * The denominator of the fraction.
     * @serial
     * @invariant denominator&isin;S
     */
    private Arithmetic/*>S<*/ denominator;
    
    /**
     * Creates a new rational number with numerator part, only.
     */
    public AbstractFraction(Arithmetic/*>M<*/ a) {
	this(a, a.one());
    }
    
    /**
     * Creates a new fraction a&#8260;s.
     * @param a the numerator of a&#8260;s.
     * @param s the denominator a&#8260;s.
     */
    public AbstractFraction(Arithmetic/*>M<*/ a, Arithmetic/*>S<*/ s) {
	this.numerator = a;
	this.denominator = s;
    }
    
    private AbstractFraction() {
	this(null, null);
    }
    

    public boolean equals(Object o) {
    	if (o instanceof Fraction) {
	    Fraction b = (Fraction) o;
	    //@internal assuming integrity domain here
	    return numerator().multiply(b.denominator()).equals(b.numerator().multiply(denominator()));
	} else
	    return false;
    }

    public int compareTo(Object o) {
	//@todo instead of implementing Comparable statically, use java.lang.reflect.Proxy in Values.fraction(...) to dynamically extend it, if the underlying object is an instance of Comparable.
	Fraction b = (Fraction) o;
	//@internal assuming integrity domain here
	Arithmetic ad = numerator().multiply(b.denominator());
	if (ad instanceof Comparable)
	    return ((Comparable) ad).compareTo(b.numerator().multiply(denominator()));
	else
	    throw new UnsupportedOperationException("since the underlying integrity domain " + ad.getClass() + " does not support " + Comparable.class);
    } 
    
    public Arithmetic/*>M<*/ numerator() {
	return numerator;
    } 
    
    public Arithmetic/*>S<*/ denominator() {
	return denominator;
    } 

    public Real norm() {
	return numerator().norm().divide(denominator().norm());
    } 
    
    // Arithmetic implementation synonyms

    public Arithmetic zero() {
	return new AbstractFraction/*<M,S>*/(numerator().zero(), denominator().one());
    }

    public Arithmetic one() {
	return new AbstractFraction/*<M,S>*/(numerator().one(), denominator().one());
    }

    public Arithmetic add(Arithmetic b) {
	if (b instanceof Fraction)
	    return add((Fraction) b);
	return (Arithmetic) Operations.plus.apply(this, b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	if (b instanceof Fraction)
	    return subtract((Fraction) b);
	return (Arithmetic) Operations.subtract.apply(this, b);
    } 
    public Arithmetic multiply(Arithmetic b) {
	if (b instanceof Fraction)
	    return multiply((Fraction) b);
	return (Arithmetic) Operations.times.apply(this, b);
    } 
    public Arithmetic divide(Arithmetic b) {
	if (b instanceof Fraction)
	    return divide((Fraction) b);
	return (Arithmetic) Operations.divide.apply(this, b);
    } 
    public Arithmetic power(Arithmetic b) {
	if (b instanceof Integer) {
	    return power_((Integer) b);
	} else if (b instanceof Fraction)
	    throw new UnsupportedOperationException();
	return (Arithmetic) Operations.power.apply(this, b);
    }


    // Arithmetic implementation synonyms
    public Fraction/*<M,S>*/ add(Fraction/*<M,S>*/ b) {
	Arithmetic/*>S<*/ s = denominator();
	Arithmetic/*>S<*/ t = b.denominator();
	return new AbstractFraction/*<M,S>*/(t.multiply(numerator()).add(s.multiply(b.numerator())),
				s.multiply(t)).representative();
    } 
    public Fraction/*<M,S>*/ subtract(Fraction/*<M,S>*/ b) {
	return add((Fraction) b.minus());
    } 
    public Arithmetic minus() {
	return new AbstractFraction/*<M,S>*/(numerator().minus(), denominator());
    } 
    public Fraction/*<M,S>*/ multiply(Fraction/*<M,S>*/ b) {
	return new AbstractFraction/*<M,S>*/(numerator().multiply(b.numerator()),
				denominator().multiply(b.denominator())).representative();
    } 
    public Fraction/*<M,S>*/ divide(Fraction/*<M,S>*/ b) {
	//@note we do not detect at runtime whether b.numerator()&isin;S
	return new AbstractFraction/*<M,S>*/(numerator().multiply(b.denominator()),
				denominator().multiply(b.numerator())).representative();
    } 
    public Arithmetic inverse() {
	//@note we do not detect at runtime whether b.numerator()&isin;S
	return new AbstractFraction/*<M,S>*/(denominator(), numerator());
    } 
    public Fraction/*<M,S>*/ power_(Integer b) {
	return new AbstractFraction/*<M,S>*/(numerator().power(b), denominator().power(b)).representative();
    }

    public Arithmetic scale(Arithmetic alpha) {
	return new AbstractFraction/*<M,S>*/(numerator.scale(alpha), denominator()).representative();
    }

    public Fraction/*<M,S>*/ representative() {
	return this;
    }

    public String toString() {
	return ArithmeticFormat.getDefaultInstance().format(this);
    }
}
