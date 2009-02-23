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

    protected AbstractComplex(ValueFactory valueFactory) {
        super(valueFactory);
    }

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
            if (equals(valueFactory().valueOf((Number)o)))
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
        assert imaginaryValue()!=0 || hash == valueFactory().valueOf(realValue()).hashCode() : "for im()=0, hash is already the same return value as for reals";
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
        return (Real)Functions.sqrt.apply(re().multiply(re()).add(im().multiply(im())));
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
    	//@xxx possible loss of precision
        return valueFactory().valueOf(Math.atan2(ArithmeticValuesImpl.doubleValueExact(im()), ArithmeticValuesImpl.doubleValueExact(re())));
        //return Math.acos(realValue() / norm());
    } 

    /**
     * Whether this complex number is infinite.
     * @return whether the real or imaginary part is infinite.
     * @see java.lang.Double#isInfinite(double)
     */
    public boolean isInfinite() {
        return re().isInfinite() || im().isInfinite();
    } 

    /**
     * Whether this complex number is NaN.
     * @return whether the real or imaginary part is NaN.
     * @see java.lang.Double#isNaN(double)
     */
    public boolean isNaN() {
        return re().isNaN() || im().isNaN();
    } 
    
    // arithmetic operations
        
    public Arithmetic zero() {return valueFactory().ZERO();}
    public Arithmetic one() {return valueFactory().ONE();}
    
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
     * Turn numbers a and b into reals of appropriate (compatible) precision.
     * @return an array of the converted versions of a and b respectively.
     */
    static Complex[] makeComplex(Number a, Number b) {
        //@xxx valueFactory precision compatbility
        ValueFactory vf = a instanceof Arithmetic ? ((Arithmetic)a).valueFactory() : b instanceof Arithmetic ? ((Arithmetic)b).valueFactory() : Values.getDefault();
        if (a instanceof Impl || b instanceof Impl) {
            return new Complex[] {
                a instanceof Impl ? (Complex)a : new Impl(a, vf),
                b instanceof Impl ? (Complex)b : new Impl(b, vf)
            };
        } else {
            //@todo could also check whether Float would be sufficient
            return new Complex[] {
                a instanceof Double ? (Complex)a : new Double(a, vf),
                b instanceof Double ? (Complex)b : new Double(b, vf)
            };
        }
    }

    /**
     * Represents a complex number a + <b>i</b>*b&isin;<b>C</b>.
     * The real and imaginary component values are doubles.
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    static class Double extends AbstractComplex {
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
        public Double(double a, double b, ValueFactory valueFactory) {
                super(valueFactory);
            real = a;
            imaginary = b;
        }
        public Double(Number a, Number b, ValueFactory valueFactory) {
            this(a.doubleValue(), b.doubleValue(), valueFactory);
        }
        public Double(Real a, Real b, ValueFactory valueFactory) {
            this(a.doubleValue(), b.doubleValue(), valueFactory);
            if (!((a instanceof AbstractReal.Double) || (a instanceof AbstractReal.Float) || (a instanceof Rational)))
                throw new UnsupportedOperationException("the precision of " + a.getClass() + " is not yet supported for type " + getClass().getName());
            else if (!((b instanceof AbstractReal.Double) || (b instanceof AbstractReal.Float) ||(b instanceof Rational)))
                throw new UnsupportedOperationException("the precision of " + b.getClass() + " is not yet supported for type " + getClass().getName());
        }
    
        /**
         * creates a new complex number with real part, only.
         */
        public Double(double real, ValueFactory valueFactory) {
            this(real, 0, valueFactory);
        }
        public Double(Number a, ValueFactory valueFactory) {
                super(valueFactory);
            if (a instanceof Complex) {
                real = ((Complex)a).re().doubleValue();
                imaginary = ((Complex)a).im().doubleValue();
            } else {
                real = a.doubleValue();
                imaginary = 0;
            }
        }
    
        /**
         * creates a new complex, zero.
         */
        public Double(ValueFactory valueFactory) {
            this(0, 0, valueFactory);
        }

        public int hashCode() {
            return super.hashCode();
        }
        
        public boolean equals(Object o) {
            if (o instanceof Double) {
                // optimized version
                AbstractComplex.Double b = (AbstractComplex.Double) o;
                return java.lang.Double.doubleToLongBits(realValue()) == java.lang.Double.doubleToLongBits(b.realValue())
                    && java.lang.Double.doubleToLongBits(imaginaryValue()) == java.lang.Double.doubleToLongBits(b.imaginaryValue());
            } else
                return super.equals(o);
        } 
        public boolean equals(Object o, Real tolerance) {
            if (o instanceof Double) {
                // optimized version
                AbstractComplex.Double b = (AbstractComplex.Double) o;
                final double deltare = realValue() - b.realValue();
                final double deltaim = imaginaryValue() - b.imaginaryValue();
                final double tol = tolerance.doubleValue();
                return deltare*deltare + deltaim*deltaim <= tol*tol;
            } else
                return super.equals(o, tolerance);
        }
        /**
         * creates a copy of a complex number.
         */
        public Object clone() {
            return new Double(real, imaginary, valueFactory());
        } 
    
        /**
         * returns the real component.
         * @return re z = re (a + <b>i</b>b) = a = (z+<span class="conjugate">z</span>) / 2
         */
        public Real re() {
            return valueFactory().valueOf(real);
        } 
        double realValue() {
            return real;
        } 
    
        /**
         * returns the imaginar component.
         * @return im z = im (a + <b>i</b>b) = b = (z-<span class="conjugate">z</span>) / (2<b>i</b>)
         */
        public Real im() {
            return valueFactory().valueOf(imaginary);
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
            return new Double(realValue(), -imaginaryValue(), valueFactory());
        } 
    
        // arithmetic operations
        
        /**
         * adds two Complexes returning a third as a result
         */
        public Complex add(Complex b) {
            return new Double(re().add(b.re()), im().add(b.im()), valueFactory());
        } 
    
        /**
         * subtracts two Complexes returning a third as a result
         */
        public Complex subtract(Complex b) {
            return new Double(re().subtract(b.re()), im().subtract(b.im()), valueFactory());
        } 
    
        /**
         * multiplies two Complexes returning a third as a result
         */
        public Complex multiply(Complex b) {
            return new Double(re().multiply(b.re()).subtract(im().multiply(b.im())), re().multiply(b.im()).add(im().multiply(b.re())), valueFactory());
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
            return new Double(-realValue(), -imaginaryValue(), valueFactory());
        } 
    
        /**
         * Returns the inverse z<sup>-1</sup>.
         * @return z<sup>-1</sup> = <span class="conjugate">z</span>/|z|<sup>2</sup>.
         */
        public Arithmetic inverse() {
            double real = realValue();
            double imaginary = imaginaryValue();
            double s = real * real + imaginary * imaginary;
            return new Double(realValue() / s, -imaginaryValue() / s, valueFactory());
        } 
    }

    /**
     * Represents a complex number a + <b>i</b>*b&isin;<b>C</b>.
     * The real and imaginary component values are Reals.
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    static class Impl extends AbstractComplex {
        private static final long serialVersionUID = -6051859562016470453L;
    
        /**
         * The real component of the Complex.
         * @serial
         */
        private Real real;
    
        /**
         * The imaginary component of the Complex.
         * @serial
         */
        private Real imaginary;
    
        public Impl(Real a, Real b, ValueFactory valueFactory) {
        	super(valueFactory);
            this.real = a;
            this.imaginary = b;
        }
        public Impl(Number a, ValueFactory valueFactory) {
        	super(valueFactory);
            if (a instanceof Complex) {
                real = ((Complex)a).re();
                imaginary = ((Complex)a).im();
            } else {
                real = (Real)a;
                imaginary = valueFactory.ZERO();
            }
        }
    
    
        /**
         * creates a copy of a complex number.
         */
        public Object clone() {
            return new Impl(real, imaginary, valueFactory());
        } 
    
        /**
         * returns the real component.
         * @return re z = re (a + <b>i</b>b) = a = (z+<span class="conjugate">z</span>) / 2
         */
        public Real re() {
            return real;
        } 
    
        /**
         * returns the imaginar component.
         * @return im z = im (a + <b>i</b>b) = b = (z-<span class="conjugate">z</span>) / (2<b>i</b>)
         */
        public Real im() {
            return imaginary;
        } 
        double realValue() {
            return re().doubleValue();
        }
        double imaginaryValue() {
            return im().doubleValue();
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
            return new Impl(re(), (Real)im().minus(), valueFactory());
        } 
    
        // arithmetic operations
        
        /**
         * adds two Complexes returning a third as a result
         */
        public Complex add(Complex b) {
            return new Impl(re().add(b.re()), im().add(b.im()), valueFactory());
        } 
    
        /**
         * subtracts two Complexes returning a third as a result
         */
        public Complex subtract(Complex b) {
            return new Impl(re().subtract(b.re()), im().subtract(b.im()), valueFactory());
        } 
    
        /**
         * multiplies two Complexes returning a third as a result
         */
        public Complex multiply(Complex b) {
            return new Impl(re().multiply(b.re()).subtract(im().multiply(b.im())), re().multiply(b.im()).add(im().multiply(b.re())), valueFactory());
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
            return new Impl((Real)re().minus(), (Real)im().minus(), valueFactory());
        } 
    
        /**
         * Returns the inverse z<sup>-1</sup>.
         * @return z<sup>-1</sup> = <span class="conjugate">z</span>/|z|<sup>2</sup>.
         */
        public Arithmetic inverse() {
            Real s = re().multiply(re()).add(im().multiply(im()));
            return new Impl(re().divide(s), (Real)im().minus().divide(s), valueFactory());
        } 
    }

}
