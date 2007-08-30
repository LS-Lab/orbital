/**
 * @(#)AbstractInteger.java 1.0 2000/08/03 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;


import java.math.BigInteger;
import java.math.BigDecimal;
import orbital.math.functional.Operations;

// @todo non-associative precisions: note that Long+Int and Int+Long etc. may differ due to brutal casting per b.intValue() or even b.longValue().
abstract class AbstractInteger extends AbstractRational implements Integer {
    private static final long serialVersionUID = -5859818959999970653L;
    protected AbstractInteger() {}
    
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
            final Values vf = Values.getDefaultInstance();
            return vf.narrow(vf.rational(this, (Integer)b));
        } 
        return (Arithmetic) Operations.divide.apply(this, b);
    } 
    public Arithmetic inverse() {
        return Values.getDefaultInstance().rational((Integer)one(), this);
    } 
    public Arithmetic power(Arithmetic b) {
        if (b instanceof Integer)
            //@xxx due to newly compiler error (bug?): call Rational Rational.power(Integer) or Rational Integer.power(Rational)? or Rational Integer.power(Integer)?
            return ((Integer)this).power((Integer) b);
        return (Arithmetic) Operations.power.apply(this, b);
    } 
    public Real power(Rational b) {
	if (b instanceof Integer)
	    return power((Integer)b);
	else
	    throw new UnsupportedOperationException("This subclass should overwrite power(Rational) correspondingly " + getClass());
    }

    // overwrite rational
    public final Integer numerator() {
        return this;
    }
    final int numeratorValue() {
        return intValue();
    }
    public final Integer denominator() {
        return (Integer)numerator().one();
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

    /**
     * Turn numbers a and b into integers of appropriate (compatible) precision.
     * @return an array of the converted versions of a and b respectively.
     */
    static Integer[] makeInteger(Number a, Number b) {
	if (a instanceof orbital.moon.math.Big || b instanceof orbital.moon.math.Big) {
	    return new Integer[] {
		a instanceof Big ? (Integer)a : new Big(a),
		b instanceof Big ? (Integer)b : new Big(b)
	    };
	} else {
	    //@todo could also check whether Int would be sufficient
	    return new Integer[] {
		a instanceof Long ? (Integer)a : new Long(a),
		b instanceof Long ? (Integer)b : new Long(b)
	    };
	}
    }
    static Big makeBigInteger(Integer a) {
	return a instanceof Big ? (Big)a : new Big((Number)a);
    }


    /**
     * Represents an integer number in <b>Z</b> as an int value.
     * 
     * @version $Id$
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

	public boolean equals(Object v) {
	    if (v instanceof Int || v instanceof Long) {
		return value == ((Number)v).longValue();
	    }
            return Operations.equal.apply(this, v);
	}
	public int compareTo(Object o) {
	    if (o instanceof Int || o instanceof Long) {
		// faster compare
		long thisVal = this.longValue();
		long anotherVal = ((Integer) o).longValue();
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	    } else
		return ((Integer) Operations.compare.apply(this, o)).intValue();
	} 

        public Real norm() {
            return Values.getDefaultInstance().valueOf(Math.abs(intValue()));
        } 

	public boolean isZero() {
	    return value == 0;
	} 
	public boolean isOne() {
	    return value == 1;
	} 

        public int intValue() {
            return value;
        } 
        public long longValue() {
            return value;
        } 
        public double doubleValue() {
            return intValue();
        } 
    
        // Arithmetic implementation synonyms
        public Integer add(Integer b) {
            if (b instanceof Int)
                return new Int(intValue() + b.intValue());
            else if (b instanceof Long)
                return new Long(intValue() + b.longValue());
            else if (b instanceof Big)
                return new Big(intValue()).add(b);
            return (Integer) Operations.plus.apply(this, b);
        } 
        public Integer subtract(Integer b) {
            if (b instanceof Int)
                return new Int(intValue() - b.intValue());
            else if (b instanceof Long)
                return new Long(intValue() - b.longValue());
            else if (b instanceof Big)
                return new Big(intValue()).subtract(b);
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
            else if (b instanceof Big)
                return new Big(intValue()).multiply(b);
            return (Integer) Operations.times.apply(this, b);
        } 
        public Rational power(Integer b) {
            if (b instanceof Int) {
                return b.compareTo(zero()) >= 0
                    ? new Int((int) Math.pow(intValue(), b.intValue()))
                    : Values.getDefault().rational(1, (int) Math.pow(intValue(), -b.intValue()));
            } else if (b instanceof Long) {
                return b.compareTo(zero()) >= 0
                    ? new Long((long) Math.pow(intValue(), b.longValue()))
                    : Values.getDefault().rational((Integer/*__*/)one(), Values.getDefault().valueOf((long) Math.pow(intValue(), -b.longValue())));
            } else if (b instanceof Big) {
                return new Big(intValue()).power(b);
	    }
            return (Integer) Operations.power.apply(this, b);
        }
	public Real power(Rational b) {
	    if (b instanceof Integer)
		return power((Integer)b);
	    else
		return new AbstractReal.Double(value).power(b);
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
            int r = intValue() % m;
            /*@todo nonnegative representatives and implementation of quotient() collide with property of Euclidean rings for -8 divmod -6.
	       On the other hand, chinese remainder relies on positive representatives.
	     */
            //@internal assure mathematical nonnegative modulus
            /*if (r < 0)
                r += m;
            assert (r - (intValue() % m)) % m == 0 : "change of canonical representative, only";
	    */
            //assert r >= 0 : "nonnegative representative chosen";
	    assert value == (value / m) * m + r : "modulo post: " + this + " = " + (value / m) + "*" + b + " + " + r;
            return new Int(r);
        }
        public Euclidean modulo(Euclidean b) {
            return modulo((Integer)b);
        }

	public Rational divide(Rational b) {
	    if (b instanceof Integer) {
		if (b instanceof Int) {
		    int v = ((Number)b).intValue();
		    if (value % v == 0)
			return new Int(value / v);
		}
		return Values.getDefault().rational(value, ((Number)b).intValue());
	    }
	    return super.divide(b);
	}

	// optimizations
	public Arithmetic divide(Arithmetic b) {
	    if (b instanceof Integer) {
		final Values vf = Values.getDefaultInstance();
		return vf.narrow(vf.rational(intValue(), ArithmeticValuesImpl.intValueExact((Integer) b)));
	    } 
	    return (Arithmetic) Operations.divide.apply(this, b);
	} 
	public Arithmetic inverse() {
	    return Values.getDefaultInstance().rational(1, intValue());
	} 
    }

    /**
     * Represents an integer number in <b>Z</b> as a long value.
     * 
     * @version $Id$
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
    
	public boolean isZero() {
	    return value == 0;
	} 
	public boolean isOne() {
	    return value == 1;
	} 

	public boolean equals(Object v) {
	    if (v instanceof Int || v instanceof Long) {
		return value == ((Number)v).longValue();
	    }
            return Operations.equal.apply(this, v);
	}
	public int compareTo(Object o) {
	    if (o instanceof Int || o instanceof Long) {
		// faster compare
		long thisVal = this.longValue();
		long anotherVal = ((Integer) o).longValue();
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	    } else
		return ((Integer) Operations.compare.apply(this, o)).intValue();
	} 
        public Real norm() {
            return Values.getDefaultInstance().valueOf(Math.abs(longValue()));
        } 

        // Arithmetic implementation synonyms
        public Integer add(Integer b) {
	    if (b instanceof Long || b instanceof Int)
                return new Long(longValue() + b.longValue());
            else if (b instanceof Big)
                return new Big(longValue()).add(b);
            return (Integer) Operations.plus.apply(this, b);
        }
        public Integer subtract(Integer b) {
	    if (b instanceof Long || b instanceof Int)
                return new Long(longValue() - b.longValue());
            else if (b instanceof Big)
                return new Big(longValue()).subtract(b);
            return (Integer) Operations.subtract.apply(this, b);
        }
        public Arithmetic minus() {
            return new Long(-value);
        } 
        public Integer multiply(Integer b) {
	    if (b instanceof Long || b instanceof Int)
                return new Long(longValue() * b.longValue());
            else if (b instanceof Big)
                return new Big(longValue()).multiply(b);
            return (Integer) Operations.times.apply(this, b);
        }
        public Rational power(Integer b) {
            if (b instanceof Long || b instanceof Int) {
                return b.compareTo(zero()) >= 0
                    ? new Long((long) Math.pow(longValue(), b.longValue()))
                    : Values.getDefault().rational((Integer/*__*/)one(), Values.getDefault().valueOf((long) Math.pow(longValue(), -b.longValue())));
            } else if (b instanceof Big) {
                return new Big(longValue()).power(b);
            }
            return (Integer) Operations.power.apply(this, b);
        }
	public Real power(Rational b) {
	    if (b instanceof Integer)
		return power((Integer)b);
	    else
		return new AbstractReal.Double(value).power(b);
	}
        
        // Euclidean implementation
        public Integer quotient(Integer b) {
            return new Long(longValue() / b.longValue());
        }
        public Euclidean quotient(Euclidean b) {
            return quotient((Integer)b);
        }
        public Integer modulo(Integer b) {
            final long m = b.longValue();
            long r = longValue() % m;
            /*@todo nonnegative representatives and implementation of quotient() collide with property of Euclidean rings for -8 divmod -6.
	       On the other hand, chinese remainder relies on positive representatives.
	     */
	    //@internal assure mathematical nonnegative modulus
            /*if (r < 0)
		//@internal Math.abs(m)?
                r += m;
            assert (r - (longValue() % m)) % m == 0 : "change of canonical representative, only";
            //assert r >= 0 : "nonnegative representative chosen for " + this + " modulo " + b + " = " + (longValue() % m) + " = " + r;
	    */
	    assert value == (value / m) * m + r : "modulo post: " + this + " = " + (value / m) + "*" + b + " + " + r;
            return new Long(r);
        }
        public Euclidean modulo(Euclidean b) {
            return modulo((Integer)b);
        }

	public Rational divide(Rational b) {
	    if (b instanceof Integer) {
		if (b instanceof Int || b instanceof Long) {
		    long v = ((Number)b).longValue();
		    if (value % v == 0)
			return new Long(value / v);
		}
		ValueFactory vf = Values.getDefault();
		return vf.rational(vf.valueOf(value), vf.valueOf(((Number)b).longValue()));
	    }
	    return super.divide(b);
	}
    }

    /**
     * Represents an integer number in <b>Z</b> as an arbitrary-precision value.
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see java.math.BigInteger
     * @internal note we do not provide a faster hashCode() implementation via longValue(), since new Real(0) and new Integer(0) will have different hashes then, although they are equal.
     */
    static class Big extends AbstractInteger implements orbital.moon.math.Big {
        private static final long serialVersionUID = -5189252512503918277L;
        /**
         * the value
         * @serial
         */
        private BigInteger value;
        public Big(int v) {
            this(BigInteger.valueOf(v));
        }
        public Big(long v) {
            this(BigInteger.valueOf(v));
        }
        public Big(BigInteger v) {
            value = v;
        }
        public Big(Number v) {
	    if (v instanceof BigInteger)
		value = (BigInteger)v;
	    else if (v instanceof orbital.moon.math.Big) {
		if (v instanceof Big)
		    value = ((Big)v).value;
		else
		    throw new IllegalArgumentException("unknown arbitrary precision type " + v.getClass() + " " + v);
	    } else if (v instanceof Int || v instanceof Long
		       || v instanceof java.lang.Integer || v instanceof java.lang.Long) {
		value = BigInteger.valueOf(((Integer)v).longValue());
	    } else
		value = BigInteger.valueOf(ArithmeticValuesImpl.longValueExact(v));
        }

        BigInteger getValue() {
	    return value;
	}

	public boolean equals(Object v) {
	    if (v instanceof orbital.moon.math.Big) {
		if (v instanceof Big)
		    return value.equals(((Big)v).value);
		else if (v instanceof AbstractReal.Big)
		    //@internal BigDecimal.equals is mincing with scales. Prefer comparaTo
		    return new BigDecimal(value).compareTo(((AbstractReal.Big)v).getValue()) == 0;
		else
		    throw new IllegalArgumentException("unknown arbitrary precision type " + v.getClass() + " " + v);
	    } else if (v instanceof Int || v instanceof Long) {
		return value.equals(BigInteger.valueOf(((Integer)v).longValue()));
	    }
            return Operations.equal.apply(this, v);
	}
	public int compareTo(Object v) {
	    if (v instanceof orbital.moon.math.Big) {
		if (v instanceof Big)
		    return value.compareTo(((Big)v).value);
		else if (v instanceof AbstractReal.Big)
		    return new BigDecimal(value).compareTo(v);
		else
		    throw new IllegalArgumentException("unknown arbitrary precision type " + v.getClass() + " " + v);
	    } else if (v instanceof Int || v instanceof Long) {
		return value.compareTo(BigInteger.valueOf(((Integer)v).longValue()));
	    }
            return ((Integer) Operations.compare.apply(this, v)).intValue();
	}

	public Real norm() {
	    return new Big(value.abs());
	} 


        public Object clone() {
            return new Big(value);
        } 

	public boolean isZero() {
	    return value.equals(BigInteger.ZERO);
	} 
	public boolean isOne() {
	    return value.equals(BigInteger.ONE);
	} 

        public int intValue() {
            return value.intValue();
        } 
        public long longValue() {
            return value.longValue();
        } 
        public double doubleValue() {
            return value.doubleValue();
        } 
    
        // Arithmetic implementation synonyms
        public Integer add(Integer b) {
            if (b instanceof Big)
                return new Big(value.add(((Big)b).value));
            else if (b instanceof Int || b instanceof Long)
                return new Big(value.add(BigInteger.valueOf(b.longValue())));
            return (Integer) Operations.plus.apply(this, b);
        } 
        public Integer subtract(Integer b) {
            if (b instanceof Big)
                return new Big(value.subtract(((Big)b).value));
            else if (b instanceof Int || b instanceof Long)
                return new Big(value.subtract(BigInteger.valueOf(b.longValue())));
            return (Integer) Operations.subtract.apply(this, b);
        } 
        public Arithmetic minus() {
            return new Big(value.negate());
        } 
        public Integer multiply(Integer b) {
            if (b instanceof Big)
                return new Big(value.multiply(((Big)b).value));
            else if (b instanceof Int || b instanceof Long)
                return new Big(value.multiply(BigInteger.valueOf(b.longValue())));
            return (Integer) Operations.times.apply(this, b);
        } 
        public Rational power(Integer b) {
	    try {
		int bv = ArithmeticValuesImpl.intValueExact(b);
		return bv >= 0
                    ? new Big(value.pow(bv))
                    : Values.getDefault().rational((Integer/*__*/)one(), Values.getDefault().valueOf(value.pow(-bv)));
	    } catch(ArithmeticException ex) {
		throw new ArithmeticException("exponentation is possibly too big: " + this + " ^ " + b);
		//return (Integer) Operations.power.apply(this, b);
	    }
        } 
	public Real power(Rational b) {
	    if (b instanceof Integer)
		return power((Integer)b);
	    else
		return new AbstractReal.Big(value).power(b);
	}

        // Euclidean implementation
        public Integer quotient(Integer b) {
            if (b instanceof Big)
                return new Big(value.divide(((Big)b).value));
            else if (b instanceof Int || b instanceof Long)
                return new Big(value.divide(BigInteger.valueOf(b.longValue())));
            throw new UnsupportedOperationException("opertion cannot be applied on " + this + " and " + b);
        }
        public Euclidean quotient(Euclidean b) {
            return quotient((Integer)b);
        }
        public Integer modulo(Integer b) {
	    Integer r = moduloImpl(b);
	    assert this.equals(this.quotient(b).multiply(b).add(r)) : "modulo post: " + this + " = " + this.quotient(b) + "*" + b + " + " + r;
	    return r;
	}
        private Integer moduloImpl(Integer b) {
	    //@xxx do we need BigInteger.remainder or BigInteger.mod?
            if (b instanceof Big)
                return new Big(value.remainder(((Big)b).value));
            else if (b instanceof Int || b instanceof Long)
                return new Big(value.remainder(BigInteger.valueOf(b.longValue())));
            throw new UnsupportedOperationException("opertion cannot be applied on " + this + " and " + b);
        }
        public Euclidean modulo(Euclidean b) {
            return modulo((Integer)b);
        }

	
	public Rational divide(Rational b) {
	    if (b instanceof Integer) {
		if (b instanceof Big) {
		    BigInteger bv = ((Big)b).value;
		    BigInteger d[] = value.divideAndRemainder(bv);
		    if (d[1].equals(BigInteger.ZERO))
			return new Big(d[0]);
		}
		return Values.getDefault().rational(this, (Integer)b);
	    }
	    return super.divide(b);
	}
    }
}
