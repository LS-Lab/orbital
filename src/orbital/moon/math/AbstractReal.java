/**
 * @(#)AbstractReal.java 1.0 1999/08/16 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import orbital.math.Integer;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.math.BigInteger;
import orbital.math.functional.Operations;

abstract class AbstractReal extends AbstractComplex implements Real {
    private static final long serialVersionUID = -4117614439306224843L;

    protected AbstractReal(ValueFactory valueFactory) {
    	super(valueFactory);
    }

    
    public boolean equals(Object o, Real tolerance) {
        if (o instanceof Real) {
            return subtract((Real)o).norm().compareTo(tolerance) <= 0;
        }
        return super.equals(o, tolerance);
    }
    public boolean equals(Object o) {
        if (o instanceof Real) {
            return subtract((Arithmetic)o).isZero();
        } else
            return super.equals(o);
    }
    public int hashCode() {
        //@internal identical to @see Double#hashCode()
        long bits = java.lang.Double.doubleToLongBits(doubleValue());
        return (int)(bits ^ (bits >>> 32));
    }
    public int compareTo(Object o) {
        if (o instanceof Real) {
            return (int)Math.signum(subtract((Real)o).doubleValue());
        } else
            return super.compareTo(o);
    }
    public Real norm() {
        if (compareTo(zero()) < 0)
            return (Real)minus();
        else 
            return this;
    } 

    // order

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
        return (Real)zero();
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


    /**
     * Turn numbers a and b into reals of appropriate (compatible) precision.
     * @return an array of the converted versions of a and b respectively.
     */
    static Real[] makeReal(Number a, Number b) {
    	//@xxx valueFactory precision compatbility
    	ValueFactory vf = a instanceof Arithmetic ? ((Arithmetic)a).valueFactory() : b instanceof Arithmetic ? ((Arithmetic)b).valueFactory() : Values.getDefault();
       if (a instanceof orbital.moon.math.Big || b instanceof orbital.moon.math.Big) {
            return new Real[] {
                a instanceof Big ? (Real)a : new Big(a, vf),
                b instanceof Big ? (Real)b : new Big(b, vf)
            };
        } else {
            //@todo could also check whether Float would be sufficient
            return new Real[] {
                a instanceof Double ? (Real)a : new Double(a, vf),
                b instanceof Double ? (Real)b : new Double(b, vf)
            };
        }
    }

    /**
     * Represents a real number in <b>R</b> as a float value.
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    static class Float extends AbstractReal {
        private static final long serialVersionUID = -206414766833581552L;
    
        /**
         * the real value (with machine-sized float-precision, only, of course).
         * @serial
         */
        private float                    value;
        public Float(float v, ValueFactory valueFactory) {
            super(valueFactory);
        	value = v;
        }
        public Float(Number v, ValueFactory valueFactory) {
        	super(valueFactory);
            value = v.floatValue();
        }
    
        public Object clone() {
            return new Float(floatValue(), valueFactory());
        } 

        public boolean equals(Object o) {
            if (o instanceof Double || o instanceof Float) {
                //@internal identical to @see Double#equals(Object)
                return java.lang.Double.doubleToLongBits(doubleValue()) == java.lang.Double.doubleToLongBits(((Real) o).doubleValue());
            } else
                return Operations.equal.apply(this, o);
        }

        public int hashCode() {
            //@internal identical to @see Double#hashCode()
            long bits = java.lang.Double.doubleToLongBits(doubleValue());
            return (int)(bits ^ (bits >>> 32));
        }
        public int compareTo(Object o) {
            if (o instanceof Double || o instanceof Float) {
                return Double.compareDouble(value, ((Number)o).doubleValue());
            } else
                return ((Integer) Operations.compare.apply(this, o)).intValue();
        } 

        public Real norm() {
            return Values.getDefaultInstance().valueOf(Math.abs(floatValue()));
        }

        public boolean isZero() {
            return value == 0;
        } 
        public boolean isOne() {
            return value == 1;
        } 

        public float floatValue() {
            return value;
        } 

        public double doubleValue() {
            return value;
        } 
    
        public long longValue() {
            return (long)value;
        } 

        public Real add(Real b) {
            //@xxx what's up with b being an Integer.Int or Integer.Long?
            if (b instanceof Float)
                return new Float(floatValue() + b.floatValue(), valueFactory());
            else if (b instanceof Double)
                //optimized widening
                return new Double(floatValue() + b.doubleValue(), valueFactory());
            else if (b instanceof Big)
                return new Big(floatValue(), valueFactory()).add(b);
            return (Real) Operations.plus.apply(this, b);
        }
        public Real subtract(Real b) {
            if (b instanceof Float)
                return new Float(floatValue() - b.floatValue(), valueFactory());
            else if (b instanceof Double)
                //optimized widening
                return new Double(floatValue() - b.doubleValue(), valueFactory());
            else if (b instanceof Big)
                return new Big(floatValue(), valueFactory()).subtract(b);
            return (Real) Operations.subtract.apply(this, b);
        }
        public Arithmetic minus() {
            return new Float(-floatValue(), valueFactory());
        } 
        public Real multiply(Real b) {
            if (b instanceof Float)
                return new Float(floatValue() * b.floatValue(), valueFactory());
            else if (b instanceof Double)
                //optimized widening
                return new Double(floatValue() * b.doubleValue(), valueFactory());
            else if (b instanceof Big)
                return new Big(floatValue(), valueFactory()).multiply(b);
            return (Real) Operations.times.apply(this, b);
        }
        public Real divide(Real b) {
            if (b instanceof Float)
                return new Float(floatValue() / b.floatValue(), valueFactory());
            else if (b instanceof Double)
                //optimized widening
                return new Double(floatValue() / b.doubleValue(), valueFactory());
            else if (b instanceof Big)
                return new Big(floatValue(), valueFactory()).divide(b);
            return (Real) Operations.divide.apply(this, b);
        }
        public Real power(Real b) {
            if (b instanceof Float)
                return new Float((float) Math.pow(floatValue(), b.floatValue()), valueFactory());
            else if (b instanceof Double)
                //optimized widening
                return new Double(Math.pow(floatValue(), b.doubleValue()), valueFactory());
            else if (b instanceof Big)
                return new Big(floatValue(), valueFactory()).power(b);
            return (Real) Operations.power.apply(this, b);
        }
        public Arithmetic inverse() {
            return new Float(1 / floatValue(), valueFactory());
        } 
    }

    /**
     * Represents a real number in <b>R</b> as a double value.
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    static class Double extends AbstractReal {
        private static final long serialVersionUID = 2011638443547790678L;
    
        /**
         * the real value (with machine-sized double-precision, only, of course).
         * @serial
         */
        private double                   value;
        public Double(double v, ValueFactory valueFactory) {
        	super(valueFactory);
            value = v;
        }
        public Double(Number v, ValueFactory valueFactory) {
        	super(valueFactory);
            value = v.doubleValue();
        }
    
        public Object clone() {
            return new Double(doubleValue(), valueFactory());
        } 

        public boolean equals(Object o) {
            if (o instanceof Double || o instanceof Float) {
                //@internal identical to @see Double#equals(Object)
                return java.lang.Double.doubleToLongBits(doubleValue()) == java.lang.Double.doubleToLongBits(((Real) o).doubleValue());
            } else
                return Operations.equal.apply(this, o);
        }

        public int hashCode() {
            //@internal identical to @see Double#hashCode()
            long bits = java.lang.Double.doubleToLongBits(doubleValue());
            return (int)(bits ^ (bits >>> 32));
        }
        public int compareTo(Object o) {
            if (o instanceof Double || o instanceof Float) {
                return compareDouble(value, ((Number)o).doubleValue());
            } else
                return ((Integer) Operations.compare.apply(this, o)).intValue();
        } 

        //@internal identical to @see Double#compare(double,double)
        static int compareDouble(double d1, double d2) {
            if (d1 < d2)
                return -1;           // Neither val is NaN, thisVal is smaller
            if (d1 > d2)
                return 1;            // Neither val is NaN, thisVal is larger
            long thisBits = java.lang.Double.doubleToLongBits(d1);
            long anotherBits = java.lang.Double.doubleToLongBits(d2);

            return (thisBits == anotherBits ?  0 : // Values are equal
                    (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                     1));                          // (0.0, -0.0) or (NaN, !NaN)
        }

        public Real norm() {
            return Values.getDefaultInstance().valueOf(Math.abs(doubleValue()));
        }

        public boolean isZero() {
            return value == 0;
        } 
        public boolean isOne() {
            return value == 1;
        } 

        public double doubleValue() {
            return value;
        } 
        public long longValue() {
            return (long)value;
        } 
    
        public Real add(Real b) {
            //@xxx what's up with b being an Integer.Int or Integer.Long?
            if (b instanceof Double || b instanceof Float)
                return new Double(doubleValue() + b.doubleValue(), valueFactory());
            else if (b instanceof Big)
                return new Big(doubleValue(), valueFactory()).add(b);
            return (Real) Operations.plus.apply(this, b);
        }
        public Real subtract(Real b) {
            if (b instanceof Double || b instanceof Float)
                return new Double(doubleValue() - b.doubleValue(), valueFactory());
            else if (b instanceof Big)
                return new Big(doubleValue(), valueFactory()).subtract(b);
            return (Real) Operations.subtract.apply(this, b);
        }
        public Arithmetic minus() {
            return new Double(-doubleValue(), valueFactory());
        } 
        public Real multiply(Real b) {
            if (b instanceof Double || b instanceof Float)
                return new Double(doubleValue() * b.doubleValue(), valueFactory());
            else if (b instanceof Big)
                return new Big(doubleValue(), valueFactory()).multiply(b);
            return (Real) Operations.times.apply(this, b);
        }
        public Real divide(Real b) {
            if (b instanceof Double || b instanceof Float)
                return new Double(doubleValue() / b.doubleValue(), valueFactory());
            else if (b instanceof Big)
                return new Big(doubleValue(), valueFactory()).divide(b);
            return (Real) Operations.divide.apply(this, b);
        }
        public Real power(Real b) {
            if (b instanceof Double || b instanceof Float)
                return new Double(Math.pow(doubleValue(), b.doubleValue()), valueFactory());
            else if (b instanceof Big)
                return new Big(doubleValue(), valueFactory()).power(b);
            return (Real) Operations.power.apply(this, b);
        }
        public Arithmetic inverse() {
            return new Double(1 / doubleValue(), valueFactory());
        } 

    }

    /**
     * Represents a real number in <b>R</b> as an arbitrary-precision value.
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @todo respect new java.math.MathContext introduced with Java 1.5
     */
    static class Big extends AbstractReal implements orbital.moon.math.Big {
        private static final long serialVersionUID = -5801439569926611104L;
        //@xxx change to working precision and dynamically query
        private static MathContext precision = MathContext.DECIMAL128;
            // = new MathContext(Math.max(17,MathUtilities.getDefaultPrecisionDigits()), RoundingMode.HALF_UP);
        static MathContext getPrecision() {
            return precision;
        }
        static void setPrecision(MathContext ctx) {
            precision = ctx;
        }
    
        /**
         * the real value (with machine-sized arbitrary-precision, only, of course).
         * @serial
         */
        private BigDecimal value;
        public Big(double v, ValueFactory valueFactory) {
                this(convert(v), valueFactory);
        }
                private static BigDecimal convert(double v) {
                        try {
                return BigDecimal.valueOf(v);
                } catch(NumberFormatException ex) {
                        throw (NumberFormatException)new NumberFormatException("Cannot represent " + v).initCause(ex);
                }
                }
        public Big(BigDecimal v, ValueFactory valueFactory) {
        	super(valueFactory);
            value = v;
        }
        public Big(BigInteger v, ValueFactory valueFactory) {
        	super(valueFactory);
            value = new BigDecimal(v);
        }
        public Big(Number v, ValueFactory valueFactory) {
        	super(valueFactory);
            if (v instanceof BigDecimal)
                value = (BigDecimal)v;
            else if (v instanceof orbital.moon.math.Big) {
                if (v instanceof Big)
                    value = ((Big)v).value;
                else if (v instanceof AbstractInteger.Big)
                    value = new BigDecimal(((AbstractInteger.Big)v).getValue());
                else
                    throw new IllegalArgumentException("unknown arbitrary precision type " + v.getClass() + " " + v);
            } else if (v instanceof Rational) {
                Rational r = (Rational)v;
                //@internal we could also convert numerator() and denominator() to reals and divide
                value = getPrecision() != null
                    ? new BigDecimal(AbstractInteger.makeBigInteger(r.numerator()).getValue())
                    .divide(new BigDecimal(AbstractInteger.makeBigInteger(r.denominator()).getValue()), getPrecision())
                    : new BigDecimal(AbstractInteger.makeBigInteger(r.numerator()).getValue())
                    .divide(new BigDecimal(AbstractInteger.makeBigInteger(r.denominator()).getValue()));
            } else if (v instanceof Double || v instanceof Float || v instanceof AbstractInteger.Int
                       || v instanceof java.lang.Double || v instanceof java.lang.Float || v instanceof java.lang.Integer) {
                value = BigDecimal.valueOf(((Number)v).doubleValue());
            } else if (v instanceof AbstractInteger.Long
                       || v instanceof java.lang.Long) {
                value = BigDecimal.valueOf(((Number)v).longValue());
            } else {
                assert !java.lang.Double.isNaN(v.doubleValue()) && !java.lang.Double.isInfinite(v.doubleValue()) : v + " should neither be NaN nor infinite";
                value = BigDecimal.valueOf(ArithmeticValuesImpl.doubleValueExact(v));
            }
        }
    
        public Object clone() {
            return new Big(value, valueFactory());
        } 

        public boolean equals(Object v) {
            if (v instanceof orbital.moon.math.Big) {
                if (v instanceof Big)
                    //@internal BigDecimal.equals is mincing with scales. Prefer comparaTo
                    return value.compareTo(((Big)v).value) == 0;
                else if (v instanceof AbstractInteger.Big)
                    //@internal BigDecimal.equals is mincing with scales. Prefer comparaTo
                    return value.compareTo(new BigDecimal(((AbstractInteger.Big)v).getValue())) == 0;
                else
                    throw new IllegalArgumentException("unknown arbitrary precision type " + v.getClass() + " " + v);
            } else if (v instanceof Double || v instanceof Float || v instanceof AbstractInteger.Int) {
                return value.compareTo(BigDecimal.valueOf(((Real)v).doubleValue())) == 0;
            } else if (v instanceof AbstractInteger.Long) {
                return value.compareTo(BigDecimal.valueOf(((Integer)v).longValue())) == 0;
            }
            return Operations.equal.apply(this, v);
        }
        public int compareTo(Object v) {
            if (v instanceof orbital.moon.math.Big) {
                if (v instanceof Big)
                    return value.compareTo(((Big)v).value);
                else if (v instanceof AbstractInteger.Big)
                    return value.compareTo(new BigDecimal(((AbstractInteger.Big)v).getValue()));
                else
                    throw new IllegalArgumentException("unknown arbitrary precision type " + v.getClass() + " " + v);
            } else if (v instanceof Double || v instanceof Float || v instanceof AbstractInteger.Int) {
                return value.compareTo(BigDecimal.valueOf(((Real)v).doubleValue()));
            } else if (v instanceof AbstractInteger.Long) {
                return value.compareTo(BigDecimal.valueOf(((Integer)v).longValue()));
            }
            return ((Integer) Operations.compare.apply(this, v)).intValue();
        }

        BigDecimal getValue() {
            return value;
        }
        
        public int intValue() {
            return value.intValueExact();
        } 
        public long longValue() {
            return value.longValueExact();
        } 
        public double doubleValue() {
            return value.doubleValue();
        } 
    
        public boolean isZero() {
            return value.compareTo(BigDecimal.ZERO) == 0;
        } 
        public boolean isOne() {
            return value.compareTo(BigDecimal.ONE) == 0;
        } 

        public Real norm() {
            return new Big(value.abs(), valueFactory());
        } 

        public Real add(Real b) {
            if (b instanceof Big)
                return new Big(value.add(((Big)b).value), valueFactory());
            else if (b instanceof Float || b instanceof Double)
                return new Big(value.add(BigDecimal.valueOf(b.doubleValue())), valueFactory());
            return (Real) Operations.plus.apply(this, b);
        }
        public Real subtract(Real b) {
            if (b instanceof Big)
                return new Big(value.subtract(((Big)b).value), valueFactory());
            else if (b instanceof Float || b instanceof Double)
                return new Big(value.subtract(BigDecimal.valueOf(b.doubleValue())), valueFactory());
            return (Real) Operations.subtract.apply(this, b);
        }
        public Arithmetic minus() {
            return new Big(value.negate(), valueFactory());
        } 
        public Real multiply(Real b) {
            if (b instanceof Big)
                return new Big(value.multiply(((Big)b).value), valueFactory());
            else if (b instanceof Float || b instanceof Double)
                return new Big(value.multiply(BigDecimal.valueOf(b.doubleValue())), valueFactory());
            return (Real) Operations.times.apply(this, b);
        }
        public Real divide(Real b) {
            if (b instanceof Big)
                return getPrecision() != null
                    ? new Big(value.divide(((Big)b).value, getPrecision()), valueFactory())
                    : new Big(value.divide(((Big)b).value), valueFactory());
            else if (b instanceof Float || b instanceof Double)
                return getPrecision() != null
                    ? new Big(value.divide(BigDecimal.valueOf(b.doubleValue()), getPrecision()), valueFactory())
                    : new Big(value.divide(BigDecimal.valueOf(b.doubleValue())), valueFactory());
            return (Real) Operations.divide.apply(this, b);
        }
        public Real power(Real b) {
            if (b instanceof Integer) {
                return power((Integer)b);
            }
            if (isZero() && !b.isZero()) {
            	return (Real)zero();
            } else if (isOne()) {
            	return (Real)one();
            }
            Real bc = (Real) Values.getDefault().narrow(b);
            if (bc instanceof Integer) {
                return power((Integer)bc);
            } else {
                try {
                    return valueFactory().valueOf(Math.pow(ArithmeticValuesImpl.doubleValueExact((Real)this), ArithmeticValuesImpl.doubleValueExact(b)));
                } catch(ArithmeticException ex) {
                    throw (ArithmeticException) new ArithmeticException("exponentation is possibly too big: " + this + " ^ " + b + " where " + (b.isZero() ? "zero" : "non-zero")).initCause(ex);
                }
            }
        }
        private Real power(Integer b) {
            if (isZero() && !b.isZero()) {
            	return (Real)zero();
            } else if (isOne()) {
            	return (Real)one();
            }
            try {
                return new Big(getValue().pow(ArithmeticValuesImpl.intValueExact(b)), valueFactory());
            } catch(ArithmeticException ex) {
                throw new ArithmeticException("exponentation is possibly too big: " + this + " ^ " + b);
            }
        }
        public Arithmetic inverse() {
            return one().divide(this);
        } 
    }
}
