/**
 * @(#)AbstractComplex.java 1.0 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;


import orbital.math.functional.Operations;
import orbital.math.functional.Functions;

/*@todo add double and Real precision of implementation! */
abstract class AbstractComplex extends AbstractScalar implements Complex {
    private static final long serialVersionUID = 6174516422770428710L;

    protected AbstractComplex() {}

    /**
     * Complex numbers are <em>not</em> ordered.
     */
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("complex numbers not ordered");
    } 

    public boolean equals(Object o) {
        if (Complex.isa.apply(o)) {
            Complex b = (Complex) o;
            return re().equals(b.re()) && im().equals(b.im());
        } else if (o != null && Values.isPrimitiveWrapper(o.getClass())) {
            if (equals(Values.getDefaultInstance().valueOf((Number)o)))
                throw new IllegalArgumentException("comparing " + Scalar.class.getName() + "s with primitive wrapper type " + o.getClass() + " is not supported (since then Object.equals(Object) is symmetric)");
            else
                return false;
        } else
            return false;
    } 

    public int hashCode() {
        //@see Double#hashCode()
        long bits = java.lang.Double.doubleToLongBits(realValue());
        int hash = (int)(bits ^ (bits >>> 32));
        //@see Double#hashCode()
        bits = java.lang.Double.doubleToLongBits(imaginaryValue());
        hash = hash ^ (int)(bits ^ (bits >>> 32));
        assert imaginaryValue()!=0 || hash == Values.getDefaultInstance().valueOf(realValue()).hashCode() : "for im()=0, hash is already the same return value as for reals";
        return hash;
    } 

    public boolean isZero() {
	return re().isZero() && im().isZero();
    }
    public boolean isOne() {
	return re().isOne() && im().isZero();
    }

    
    //@todo think about these shortcuts to re().doubleValue() and im().doubleValue(). They effectively prevent new subclasses of AbstractComplex that wish to reuse some methods.
    abstract double realValue();
    abstract double imaginaryValue();

    public Real norm() {
        return Values.getDefaultInstance().valueOf(Math.sqrt(normSquare()));
    } 
    double normSquare() {
        double real = realValue();
        double imaginary = imaginaryValue();
        return real * real + imaginary * imaginary;
    } 

    /**
     * Returns the principal angle (argument) component of a polar complex.
     * But adding 2k&pi; to the principle angle will be an angle as well.
     * @return the angle &phi; of r*<b>e</b><sup><b>i</b>&phi;</sup>.
     *  The angle &ang; &phi; in radians is measured counter-clockwise from the real axis.
     *  Value will be in range [-&pi;,&pi;] and is not yet in the correct sector!
     * @see #norm()
     */
    public Real arg() {
        return Values.getDefaultInstance().valueOf(Math.atan2(imaginaryValue(), realValue()));
        //return Math.acos(realValue() / norm());
    } 

    /**
     * Whether this complex number is infinite.
     * @return whether the real or imaginary part is infinite.
     * @see java.lang.Double#isInfinite(double)
     */
    public boolean isInfinite() {
        return java.lang.Double.isInfinite(realValue()) || java.lang.Double.isInfinite(imaginaryValue());
    } 

    /**
     * Whether this complex number is NaN.
     * @return whether the real or imaginary part is NaN.
     * @see java.lang.Double#isNaN(double)
     */
    public boolean isNaN() {
        return java.lang.Double.isNaN(realValue()) || java.lang.Double.isNaN(imaginaryValue());
    } 
    
    // arithmetic operations
        
    public Arithmetic zero() {return Values.ZERO;}
    public Arithmetic one() {return Values.ONE;}
    
    /**
     * power of complex numbers.
     */
    public Complex power(Complex x) {
        return (Complex) Functions.exp.apply(x.multiply((Complex) Functions.log.apply(this)));
    }

    // Arithmetic implementation synonyms
    public Arithmetic add(Arithmetic b) {
        if (b instanceof Complex)
            return add((Complex) b);
        return (Arithmetic) Operations.plus.apply(this, b);
    } 
    public Arithmetic subtract(Arithmetic b) {
        if (b instanceof Complex)
            return subtract((Complex) b);
        return (Arithmetic) Operations.subtract.apply(this, b);
    } 

    /**
     * The negative of a complex number. Result has the same length but the angle differs by &pi;.
     */
    public Arithmetic multiply(Arithmetic b) {
        if (b instanceof Complex)
            return multiply((Complex) b);
        return (Arithmetic) Operations.times.apply(this, b);
    } 
    public Arithmetic divide(Arithmetic b) {
        if (b instanceof Complex)
            return divide((Complex) b);
        return (Arithmetic) Operations.divide.apply(this, b);
    } 

    public Arithmetic power(Arithmetic x) {
        return (Arithmetic) Functions.exp.apply(x.multiply((Complex) Functions.log.apply(this)));
    } 

    /**
     * Returns a string representation of the object.
     */
    /*public String toString() {
    // beautiful format  a+i*b, a-i*b, a, i*b, -i, i, etc.
    if (im() == 0 && !Logger.global.isLoggable(Level.DEBUG))
    return MathUtilities.format(re());
    String imformat = "i" + (Math.abs(im()) == 1 ? "" : "*" + MathUtilities.format(Math.abs(im())));
    if (re() == 0 && !Logger.global.isLoggable(Level.DEBUG))
    return (im() >= 0 ? "" : "-") + imformat;
    else
    return MathUtilities.format(re()) + (im() >= 0 ? "+" : "-") + imformat;
    }*/

    /**
     * Represents a complex number a + <b>i</b>*b&isin;<b>C</b>.
     * The real and imaginary component values are doubles.
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    static class ComplexImpl extends AbstractComplex {
        private static final long serialVersionUID = -4910740689136774872L;
    
        /**
         * The imaginary component of the Complex.
         * @serial
         */
        private double                          real;
    
        /**
         * The imaginary (imaginal) component of the Complex.
         * @serial
         */
        private double                          imaginary;
    
        /**
         * Creates a new complex number a + <b>i</b>*b
         * @param a real part of the complex number.
         * @param b imaginary part of the complex number.
         */
        public ComplexImpl(double a, double b) {
            real = a;
            imaginary = b;
        }
        public ComplexImpl(Number a, Number b) {
            this(a.doubleValue(), b.doubleValue());
        }
        public ComplexImpl(Real a, Real b) {
            //@xxx we might loose some precision here
            this(a.doubleValue(), b.doubleValue());
            if (!((a instanceof AbstractReal.Double) || (a instanceof AbstractReal.Float) || (a instanceof Rational)))
                //@xxx instances of Rational could still lead to a loss of precision (if outside of this package, for example)
                throw new UnsupportedOperationException("the precision of " + a.getClass() + " is currently not yet supported");
            else if (!((b instanceof AbstractReal.Double) || (b instanceof AbstractReal.Float) ||(b instanceof Rational)))
                throw new UnsupportedOperationException("the precision of " + b.getClass() + " is currently not yet supported");
        }
    
        /**
         * creates a new complex number with real part, only.
         */
        public ComplexImpl(double real) {
            this(real, 0);
        }
        public ComplexImpl(Number real) {
            this(real.doubleValue(), 0);
        }
    
        /**
         * creates a new complex, zero.
         */
        public ComplexImpl() {
            this(0, 0);
        }

        public int hashCode() {
            return super.hashCode();
        }
        
        public boolean equals(Object o) {
            if (o instanceof ComplexImpl) {
                // optimized version
                AbstractComplex b = (AbstractComplex) o;
                return java.lang.Double.doubleToLongBits(realValue()) == java.lang.Double.doubleToLongBits(b.realValue())
                    && java.lang.Double.doubleToLongBits(imaginaryValue()) == java.lang.Double.doubleToLongBits(b.imaginaryValue());
            } else
                return super.equals(o);
        } 
        public boolean equals(Object o, Real tolerance) {
            if (o instanceof ComplexImpl) {
                // optimized version
                AbstractComplex b = (AbstractComplex) o;
                final double deltare = realValue() - b.realValue();
                final double deltaim = imaginaryValue() - b.imaginaryValue();
                return deltare*deltare + deltaim*deltaim < tolerance.doubleValue();
            } else
                return super.equals(o, tolerance);
        }
        /**
         * creates a copy of a complex number.
         */
        public Object clone() {
            return new ComplexImpl(real, imaginary);
        } 
    
        /**
         * returns the real component.
         * @return re z = re (a + <b>i</b>b) = a = (z+<span class="conjugate">z</span>) / 2
         */
        public Real re() {
            return Values.getDefaultInstance().valueOf(real);
        } 
        double realValue() {
            return real;
        } 
    
        /**
         * returns the imaginar component.
         * @return im z = im (a + <b>i</b>b) = b = (z-<span class="conjugate">z</span>) / (2<b>i</b>)
         */
        public Real im() {
            return Values.getDefaultInstance().valueOf(imaginary);
        } 
        double imaginaryValue() {
            return imaginary;
        } 
    
	/**
	 * Throws an UnsupportedOperationException if this complex has an imaginary part.
	 */
	public double doubleValue() {
	    if (im().isZero())
		return re().doubleValue();
	    else
		throw new UnsupportedOperationException("complex value has no real value");

	    // return java.lang.Double.NaN;
	} 
	public float floatValue() {
	    if (im().isZero())
		return re().floatValue();
	    else
		throw new UnsupportedOperationException("complex value has no real value");

	    // return java.lang.Double.NaN;
	} 
	public long longValue() {
	    if (im().isZero())
		return ((Number)re()).longValue();
	    else
		throw new UnsupportedOperationException("complex value has no real value");

	    // return java.lang.Double.NaN;
	} 

        /**
         * Returns the complex conjugated <span class="conjugate">z</span> = z<sup>*</sup> = z'.
         * <p>
         * Conjugation is an involutive field-automorphism that is identical on <b>R</b>.</p>
         * @return the complex number a - <b>i</b>*b.
         */
        public Complex conjugate() {
            return new ComplexImpl(realValue(), -imaginaryValue());
        } 
    
        // arithmetic operations
        
        /**
         * adds two Complexes returning a third as a result
         */
        public Complex add(Complex b) {
            return new ComplexImpl(re().add(b.re()), im().add(b.im()));
        } 
    
        /**
         * subtracts two Complexes returning a third as a result
         */
        public Complex subtract(Complex b) {
            return new ComplexImpl(re().subtract(b.re()), im().subtract(b.im()));
        } 
    
        /**
         * multiplies two Complexes returning a third as a result
         */
        public Complex multiply(Complex b) {
            return new ComplexImpl(re().multiply(b.re()).subtract(im().multiply(b.im())), re().multiply(b.im()).add(im().multiply(b.re())));
        } 
    
        /**
         * divides two complex numbers.
         */
        public Complex divide(Complex b) {
            return multiply((Complex) b.inverse());
        } 
    
        /**
         * The negative of a complex number. Result has the same length but the angle differs by &pi;.
         */
        public Arithmetic minus() {
            return new ComplexImpl(-realValue(), -imaginaryValue());
        } 
    
        /**
         * Returns the inverse z<sup>-1</sup>.
         * @return z<sup>-1</sup> = <span class="conjugate">z</span>/|z|<sup>2</sup>.
         */
        public Arithmetic inverse() {
            double s = normSquare();
            return new ComplexImpl(realValue() / s, -imaginaryValue() / s);
        } 
    }
}
