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

    protected AbstractRational(ValueFactory valueFactory) {
        super(valueFactory);
    }

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
        if (Rational.isa.apply(o)) {
            // faster and more precise comparison
            Rational b = (Rational) o;
            assert denominator().compareTo(denominator().zero()) > 0 : "normalized to denominator > 0 " + this;
            assert b.denominator().compareTo(b.denominator().zero()) > 0 : "normalized to denominator > 0 " + b;
            return numerator().multiply(b.denominator()).compareTo(b.numerator().multiply(denominator()));
        } else
            return super.compareTo(o);
    } 

    public Real norm() {
        return valueFactory().rational((Integer)numerator().norm(), (Integer)denominator().norm());
    }

    public boolean isZero() {
        return numerator().isZero();
    }
    public boolean isOne() {
        return numerator().equals(denominator());
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
    /*public Rational power(Integer b) {
        return (Rational) Operations.power.apply(this, b);
        }*/
    public Arithmetic power(Arithmetic b) {
        if (b instanceof Integer) {
            return power((Integer) b);
        } else if (b instanceof Rational)
            return power((Rational)b);
        return (Arithmetic) Operations.power.apply(this, b);
    } 

    public Real power(Rational b) {
        if (b instanceof Integer)
            return power((Integer)b);
        return numerator().power(b).divide(denominator().power(b));
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
     * Represents a rational number p/q in <b>Q</b> with machine-sized numerator, denominator.
     * The numerator p and denominator q component values are int integer values in <b>Z</b>,
     * and q&ne;0.
     *
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    static class Int extends AbstractRational {
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
        public Int(int p, int q, ValueFactory valueFactory) {
                super(valueFactory);
            // normalize
            if (q < 0) {
                this.numerator = -p;
                this.denominator = -q;
            } else if (q > 0) {
                this.numerator = p;
                this.denominator = q;
            } else {
                throw new ArithmeticException("DivisionByZero: Not a rational number: " + p + "/" + q);
            }
        }
    
        /**
         * creates a new rational number with numerator part, only.
         */
        public Int(int p, ValueFactory valueFactory) {
            this(p, 1, valueFactory);
        }
    
        /**
         * creates a new rational number with numerator part, only.
         * @preconditions MathUtilities.isInteger(p.doubleValue()) && p.intValue() == p.longValue()
         */
        public Int(Number p, ValueFactory valueFactory) {
            this(p.intValue(), 1, valueFactory);
            Utility.pre(MathUtilities.isInteger(p.doubleValue()) && p.intValue() == p.longValue(), "integer value only");
        }
    
        /**
         * creates a new rational number p/q.
         * @param p the numerator of p/q.
         * @param q the denominator p/q.
         * @preconditions MathUtilities.isInteger(p.doubleValue()) && p.intValue() == p.longValue() && MathUtilities.isInteger(q.doubleValue()) && q.intValue() == q.longValue()
         */
        public Int(Number p, Number q, ValueFactory valueFactory) {
            this(p.intValue(), q.intValue(), valueFactory);
            Utility.pre(MathUtilities.isInteger(p.doubleValue()) && p.intValue() == p.longValue()
                        && MathUtilities.isInteger(q.doubleValue()) && q.intValue() == q.longValue(), "integer value only");
        }
    
        /**
         * creates a rational zero.
         */
        public Int(ValueFactory valueFactory) {
            this(0, 1, valueFactory);
        }
        public Object clone() {
            return new Int(numerator, denominator, valueFactory());
        } 
    
        public int compareTo(Object o) {
            if (o instanceof Int) {
                // optimized version of faster compare
                Int b = (Int) o;
                assert denominatorValue() > 0 : "normalized to denominator > 0 " + this;
                assert b.denominatorValue() > 0 : "normalized to denominator > 0 " + b;
                return numeratorValue() * b.denominatorValue() - b.numeratorValue() * denominatorValue();
            } else {
                return super.compareTo(o);
            }
        } 

        public Integer numerator() {
            return valueFactory().valueOf(numerator);
        } 
        int numeratorValue() {
            return numerator;
        } 
    
        public Integer denominator() {
            return valueFactory().valueOf(denominator);
        } 
        int denominatorValue() {
            return denominator;
        } 
    
        public long longValue() {
            return (long) numerator / denominator;
        } 
        public double doubleValue() {
            return (double) numerator / denominator;
        } 

        // Arithmetic implementation synonyms
        public Rational add(Rational b) {
            final int b_denominator = validate(b.denominator()).intValue();
            int m = MathUtilities.lcm(denominatorValue(), b_denominator);
            int f1 = m / denominatorValue(), f2 = m / b_denominator;
            return new Int(f1 * numeratorValue() + f2 * validate(b.numerator()).intValue(),
                                    m, valueFactory()).representative();
        } 
        public Rational subtract(Rational b) {
            return add((Rational) b.minus());
        } 
        public Arithmetic minus() {
            return new Int(-numeratorValue(), denominatorValue(), valueFactory());
        } 
        public Rational multiply(Rational b) {
            return new Int(numeratorValue() * validate(b.numerator()).intValue(),
                                    denominatorValue() * validate(b.denominator()).intValue(), valueFactory()).representative();
        } 
        public Rational divide(Rational b) {
            return new Int(numeratorValue() * validate(b.denominator()).intValue(),
                                    denominatorValue() * validate(b.numerator()).intValue(), valueFactory()).representative();
        } 
        public Arithmetic inverse() {
            return new Int(denominatorValue(), numeratorValue(), valueFactory());
        } 
        public Rational power(Integer b) {
            long k = b.longValue();
            if (k<0)
                throw new UnsupportedOperationException("not yet implemented");
            return new Int((int) Math.pow(numeratorValue(), k), (int) Math.pow(denominatorValue(), k), valueFactory()).representative();
        }

        public Rational representative() {
            int         p = numeratorValue();
            int         q = denominatorValue();
            boolean changed = false;
            if (q < 0) {
                // normalize
                changed = true;
                p = -p;
                q = -q;
            }
            // cancel
            int d = MathUtilities.gcd(p, q);
            //@todo somehow use cofactors instead of division by d
            return d != 1
                ? new Int(p / d, q / d, valueFactory())
                : changed
                ? new Int(p, q, valueFactory())
                : this;
        } 
        
        private Integer validate(Integer i) {
            return new AbstractInteger.Int(ArithmeticValuesImpl.intValueExact(i), valueFactory());
        }
    }

    /**
     * Represents a rational number p/q in <b>Q</b> with arbitrary Integers.
     * The numerator p and denominator q component values are Integer values in <b>Z</b>,
     * and q&ne;0.
     *
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @invariant this.normalized(), i.e., denominator() > 0
     */
    static class Impl extends AbstractRational {
        private static final long serialVersionUID = 959027468840426219L;
    
        /**
         * The numerator of the Rational.
         * @serial
         */
        private Integer numerator;
    
        /**
         * The denominator of the Rational.
         * @serial
         * @invariants denominator>0
         */
        private Integer denominator;

        /**
         * creates a new rational number p/q.
         * @param p the numerator of p/q.
         * @param q the denominator p/q.
         * @postconditions this == normalize(this)
         * @see #representative()
         */
        public Impl(Integer p, Integer q, ValueFactory valueFactory) {
                super(valueFactory);
            // normalize
            int cmp = q.compareTo(q.zero());
            if (cmp < 0) {
                this.numerator = (Integer)p.minus();
                this.denominator = (Integer)q.minus();
            } else if (cmp > 0) {
                this.numerator = p;
                this.denominator = q;
            } else {
                throw new ArithmeticException("DivisionByZero: Not a rational number: " + p + "/" + q);
            }
        }
    
        /**
         * creates a new rational number with numerator part, only.
         */
        public Impl(Integer p, ValueFactory valueFactory) {
            this(p, (Integer)p.one(), valueFactory);
        }

        public Object clone() {
            return new Impl(numerator, denominator, valueFactory());
        } 
    
        public Integer numerator() {
            return numerator;
        } 
    
        public Integer denominator() {
            return denominator;
        } 
    
        public double doubleValue() {
            return numerator().doubleValue() / denominator().doubleValue();
        }
        public long longValue() {
            return (long) doubleValue();
        } 

        // Arithmetic implementation synonyms
        public Rational add(Rational b) {
            //@todo optimizable?
            Integer m = (Integer)AlgebraicAlgorithms.lcm(denominator(), b.denominator());
            //@todo somehow use cofactors instead of division by m
            Integer f1 = (Integer)m.quotient(denominator());
            Integer f2 = (Integer)m.quotient(b.denominator());
            assert m.modulo(denominator()).isZero() : "lcm is divisible by its factors: " + m + " divisible by " + denominator() + " and " + b.denominator();
            assert m.modulo(b.denominator()).isZero() : "lcm is divisible by its factors: " + m + " divisible by " + denominator() + " and " + b.denominator();
            return new Impl(f1.multiply(numerator()).add(f2.multiply(b.numerator())),
                            m, valueFactory()).representative();
        } 
        public Rational subtract(Rational b) {
            return add((Rational) b.minus());
        } 
        public Arithmetic minus() {
            return new Impl((Integer)numerator().minus(), denominator(), valueFactory());
        } 
        public Rational multiply(Rational b) {
            return new Impl(numerator().multiply(b.numerator()),
                            denominator().multiply(b.denominator()), valueFactory()).representative();
        } 
        public Rational divide(Rational b) {
            return new Impl(numerator().multiply(b.denominator()),
                            denominator().multiply(b.numerator()), valueFactory()).representative();
        } 
        public Arithmetic inverse() {
            return new Impl(denominator(), numerator(), valueFactory());
        } 
        public Rational power(Integer b) {
            if (b.compareTo(b.zero()) < 0)
                return new Impl((Integer)denominator().power((Integer)b.minus()), (Integer)numerator().power((Integer)b.minus()), valueFactory()).representative();
            else
                return new Impl((Integer)numerator().power(b), (Integer)denominator().power(b), valueFactory()).representative();
        }

        public Rational representative() {
            Integer p = numerator();
            Integer q = denominator();
            boolean changed = false;
            if (q.compareTo(q.zero()) < 0) {
                // normalize
                changed = true;
                p = (Integer)p.minus();
                q = (Integer)q.minus();
            }
            // cancel
            Integer d = (Integer) AlgebraicAlgorithms.gcd(p, q);
            //@todo somehow use cofactors instead of division by d
            if (!d.isOne()) {
                assert p.modulo(d).isZero() : "gcd divides its factors: " + p + " divides " + p + " and " + q;
                assert q.modulo(d).isZero() : "gcd divides its factors: " + p + " divides " + p + " and " + q;
                return new Impl((Integer)p.quotient(d), (Integer)q.quotient(d), valueFactory());
            } else
                return changed ? new Impl(p, q, valueFactory()) : this;
        } 
    }

}
