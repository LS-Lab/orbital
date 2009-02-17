/**
 * @(#)ArithmeticValuesImpl.java 1.2 2007-08-21 Andre Platzer
 * 
 * Copyright (c) 2000-2007 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import java.awt.Dimension;
import java.text.ParseException;
import orbital.logic.functor.Function;

import orbital.math.functional.MathFunctor;

import orbital.logic.functor.Predicate;
import orbital.logic.functor.Functor;
import java.util.Collection;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.text.ParsePosition;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Arrays;
import orbital.util.Setops;
import orbital.util.Utility;

import orbital.math.functional.Functions;

/**
 * Basis for default value constructor factory using ArithmeticVectors etc.
 * but no concrete scalar representation yet.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public abstract class ArithmeticValuesImpl extends AbstractValues {
    private static final Logger logger = Logger.getLogger(ArithmeticValuesImpl.class.getName());
    // instantiation

    public ArithmeticValuesImpl() {
        //@internal with this extra method we circumvent the compile-time initializer problem of the super(...) solution, and the permission problem for applets in the setCoercer(...) solution-
        initialSetCoercer(new orbital.logic.functor.Function/*<Object[],Object[]>*/() {
            public Object/*>Object[]<*/ apply(Object/*>Object[]<*/ o) {
                if (o instanceof Arithmetic[]) {
                    Arithmetic operands[] = (Arithmetic[]) o;
                    if (operands.length <= 1)
                        return operands;
                    return minimumCoerced(operands, false);
                } 
                return o;
            } 
            },
            new orbital.logic.functor.Function/*<Object[],Object[]>*/() {
            public Object/*>Object[]<*/ apply(Object/*>Object[]<*/ o) {
                if (o instanceof Arithmetic[]) {
                    Arithmetic operands[] = (Arithmetic[]) o;
                    if (operands.length <= 1)
                        return operands;
                    return minimumCoerced(operands, true);
                } 
                return o;
            } 
            });
        //@todo improve normalizer implementation and use it within our implementation
        initialSetNormalizer(orbital.logic.functor.Functions.id);
    }

    
    public int getPrecision() {
        return AbstractReal.Big.getPrecision().getPrecision();
    }
    public void setPrecision(int precision) {
        AbstractReal.Big.setPrecision(new MathContext(precision, AbstractReal.Big.getPrecision().getRoundingMode()));
    }


    
    // scalar value constructors - facade factory
    // primitive type conversion methods
    // deferring to the most general such method

    // integer scalar value constructors - facade factory

    public Integer valueOf(int val) {
        return valueOf((long)val);
    } 
    public Integer valueOf(byte val) {
        return valueOf((int) val);
    }
    public Integer valueOf(short val) {
        return valueOf((int) val);
    }

    // real scalar value constructors - facade factory

    public Real valueOf(float val) {
        return valueOf((double)val);
    } 

    // "named" scalar value constructors

    public Rational rational(int p, int q) {
        return rational(valueOf(p), valueOf(q));
    } 
    public Rational rational(Integer p) {
        return rational(p, (Integer)p.one());
    } 
    public Rational rational(int p) {
        return rational(valueOf(p));
    } 

    // complex scalar values constructors

    public Complex complex(Real a, Real b) {
        return cartesian(a, b);
    } 
    public Complex complex(double a, double b) {
        return cartesian(a, b);
    } 
    public Complex complex(float a, float b) {
        return complex((double)a, (double)b);
    }
    public Complex complex(int a, int b) {
        return complex((double)a, (double)b);
    }
    public Complex complex(long a, long b) {
        return complex((double)a, (double)b);
    }

    /**
     * Returns a new (real) complex whose value is equal to a + <b>i</b>*0.
     * @param a real part.
     * @return a + <b>i</b>*0.
     * @see #complex(Real, Real)
     */
    public Complex complex(Real a) {
        return complex(a, (Real)a.zero());
    } 
    public Complex complex(double a) {
        return complex(a, 0);
    } 

    public Complex cartesian(double a, double b) {
        return cartesian(valueOf(a), valueOf(b));
    } 

    public Complex polar(Real r, Real phi) {
        return new AbstractComplex.Impl(r.multiply((Real) Functions.cos.apply(phi)), r.multiply((Real) Functions.sin.apply(phi)), this);
    } 
    public Complex polar(double r, double phi) {
        return new AbstractComplex.Double(r * Math.cos(phi), r * Math.sin(phi), this);
    } 

    // provides no scalar constructors

    // vector constructors and conversion utilities
         
    public /*<R extends Arithmetic>*/ Vector/*<R>*/ valueOf(Arithmetic/*>R<*/[] values) {
        return new ArithmeticVector/*<R>*/(values, this);
    } 
    public Vector valueOf(double[] values) {
        // kind of map valueOf
        Vector/*<Real>*/ v = newInstance(values.length);
        for (int i = 0; i < values.length; i++)
            v.set(i, valueOf(values[i]));
        return v;
    } 
    public Vector/*<Integer>*/ valueOf(int[] values) {
        // kind of map valueOf
        Vector/*<Integer>*/ v = newInstance(values.length);
        for (int i = 0; i < values.length; i++)
            v.set(i, valueOf(values[i]));
        return v;
    } 

    /*<R extends Arithmetic>*/ Vector/*<R>*/ vector(List/*<R>*/ values) {
        Vector/*<R>*/   r = newInstance(values.size());
        Iterator/*<R>*/   it = values.iterator();
        for (int i = 0; i < values.size(); i++)
            r.set(i, (Arithmetic/*>R<*/) it.next());
        assert !it.hasNext() : "iterator should be finished after all elements";
        return r;
    }

    public /*<R extends Arithmetic>*/ Vector/*<R>*/ newInstance(int dim) {
        return new ArithmeticVector/*<R>*/(dim, this);
    }

    public /*<R extends Scalar>*/ Vector/*<R>*/ BASE(int n, int i) {
        ArithmeticVector/*<R>*/ base = (ArithmeticVector/*<R>*/) (Vector/*<R>*/) newInstance(n);
        for (int j = 0; j < base.dimension(); j++)
            base.D[j] = (Arithmetic/*>R<*/) (j == i ? ONE() : ZERO());
        return base;
    } 

    public /*<R extends Arithmetic>*/ Vector/*<R>*/ CONST(int n, Arithmetic/*>R<*/ c) {
        ArithmeticVector/*<R>*/ constant = (ArithmeticVector/*<R>*/) (Vector/*<R>*/) newInstance(n);
        Arrays.fill(constant.D, c);
        return constant;
    } 


    // matrix constructors and conversion utilities

    public /*<R extends Arithmetic>*/ Matrix/*<R>*/ valueOf(Arithmetic/*>R<*/[][] values) {
        return new ArithmeticMatrix/*<R>*/(values, this);
    } 
    // matrix constructors and conversion utilities

    public Matrix valueOf(double[][] values) {
        for (int i = 1; i < values.length; i++)
            Utility.pre(values[i].length == values[i - 1].length, "rectangular array required");
        // kind of map valueOf
        Matrix/*<Real>*/ v = newInstance(values.length, values[0].length);
        for (int i = 0; i < values.length; i++)
            for (int j = 0; j < values[0].length; j++)
                v.set(i, j, valueOf(values[i][j]));
        return v;
    } 
    public Matrix/*<Integer>*/ valueOf(int[][] values) {
        for (int i = 1; i < values.length; i++)
            Utility.pre(values[i].length == values[i - 1].length, "rectangular array required");
        // kind of map valueOf
        Matrix/*<Integer>*/ v = newInstance(values.length, values[0].length);
        for (int i = 0; i < values.length; i++)
            for (int j = 0; j < values[0].length; j++)
                v.set(i, j, valueOf(values[i][j]));
        return v;
    } 


    static /*<R extends Arithmetic>*/ Matrix/*<R>*/ matrix(List/*<List<R>>*/ values) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public /*<R extends Arithmetic>*/ Matrix/*<R>*/ newInstance(Dimension dimension) {
        return new ArithmeticMatrix/*<R>*/(dimension, this);
    } 
    public /*<R extends Arithmetic>*/ Matrix/*<R>*/ newInstance(int height, int width) {
        return new ArithmeticMatrix/*<R>*/(height, width, this);
    } 

    public /*<R extends Scalar>*/ Matrix/*<R>*/ IDENTITY(int height, int width) {
        if (!(width == height))
            throw new IllegalArgumentException("identity matrix is square");
        ArithmeticMatrix/*<R>*/ identity = (ArithmeticMatrix/*<R>*/) (Matrix/*<R>*/) newInstance(height, width);
        for (int i = 0; i < identity.dimension().height; i++)
            for (int j = 0; j < identity.dimension().width; j++)
                identity.D[i][j] = (Arithmetic/*>R<*/) valueOf(orbital.math.functional.Functions.delta(i, j));
        return identity;
    } 

    // tensor constructors
    
    public /*<R extends Arithmetic>*/ Vector/*<R>*/ tensor(Arithmetic/*>R<*/[] values) {
        return valueOf(values);
    }
    public /*<R extends Arithmetic>*/ Matrix/*<R>*/ tensor(Arithmetic/*>R<*/[][] values) {
        return valueOf(values);
    }
    public /*<R extends Arithmetic>*/ Tensor/*<R>*/ tensor(Arithmetic/*>R<*/[][][] values) {
        return new ArithmeticTensor(values, this);
    }
    public Tensor tensor(Object values) {
        AbstractTensor t = new ArithmeticTensor(values, this);
        // tensors of rank 1 or rank 2 are converted to vectors or matrices
        switch (t.rank()) {
        case 0:
            throw new IllegalArgumentException("tensor rank 0 undefined from empty dimensions of length 0");
        case 1:
            return validate(tensor((Arithmetic[]) (values instanceof Arithmetic[] ? values : t.toArray__Tensor())),
                            t.dimensions());
        case 2:
            return validate(tensor((Arithmetic[][]) (values instanceof Arithmetic[][] ? values : t.toArray__Tensor())),
                            t.dimensions());
        default:
            return validate(t, t.dimensions());
        }
    }
    
    public Tensor newInstance(int[] dimensions) {
        // tensors of rank 1 or rank 2 are converted to vectors or matrices
        switch (dimensions.length) {
        case 0:
            throw new IllegalArgumentException("tensor rank 0 undefined with empyt dimensions of length 0");
        case 1:
            return validate(newInstance(dimensions[0]), dimensions);
        case 2:
            return validate(newInstance(dimensions[0], dimensions[1]), dimensions);
        default:
            return validate(new ArithmeticTensor(dimensions, this), dimensions);
        }
    }
    /**
     * Validate the dimensions of a tensor.
     */
    private final Tensor validate(final Tensor t, final int[] dimensions) {
        assert t.rank() == dimensions.length : "correct rank";
        assert Utility.equalsAll(t.dimensions(), dimensions) : "correct dimensions";
        assert t.rank() == 1 ? t instanceof Vector : true : "rank 1 is vector";
        assert t.rank() == 2 ? t instanceof Matrix : true : "rank 2 is matrix";
        return t;
    }

    public /*<R extends Arithmetic>*/ Tensor/*<R>*/ ZERO(int[] dimensions) {
        Tensor zero = newInstance(dimensions);
        for (ListIterator i = zero.iterator(); i.hasNext(); ) {
            i.next();
            i.set(ZERO());
        }
        return zero;
    }

    // polynomial constructors and utilities

    public /*_<R extends Arithmetic>_*/ Polynomial/*_<R>_*/ polynomial(Object coefficients) {
        return asPolynomial(tensor(coefficients));
    }

    public /*<R extends Arithmetic>*/ Polynomial/*<R,Vector<Integer>>*/ asPolynomial(Tensor/*<R>*/ coefficients) {
        // polynomials in 1 variable are converted to UnivariatePolynomials
        switch (coefficients.rank()) {
        // turn off this implementation until MONOMIAL also obeys it
        case 1:
            // @todo implement a true view flexible for changes (but only if Polynomial.set(...) has been introduced)
            return polynomial((Arithmetic[]) ((AbstractTensor)coefficients).toArray__Tensor());
        default:
            return new ArithmeticMultivariatePolynomial(coefficients);
        }
    }

    public /*<R extends Arithmetic>*/ Tensor/*<R>*/ asTensor(Polynomial/*<R,Vector<Integer>>*/ p) {
        if (p instanceof UnivariatePolynomial) {
                return ((UnivariatePolynomial)p).getCoefficientVector();
        } else {
            return ((AbstractMultivariatePolynomial)p).tensorViewOfCoefficients();
        }
    }

    public /*<R extends Arithmetic, S extends Arithmetic>*/ Polynomial/*<R,S>*/ constant(Polynomial/*<R,S>*/ p) {
        // Polynomials are currently unmodifiable anyhow.
        //@xxx except via iterator()
        return p;
    }

    // @internal horribly complicate implementation
    public final /*<R extends Arithmetic>*/ Polynomial/*<R,Vector<Integer>>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, int[] exponents) {
        if (exponents.length == 1) {
                return MONOMIAL(coefficient, exponents[0]);
        }
        int[] dim = new int[exponents.length];
        for (int k = 0; k < dim.length; k++)
            dim[k] = exponents[k] + 1;
        AbstractMultivariatePolynomial m = new ArithmeticMultivariatePolynomial(dim, this);
        m.set(m.CONSTANT_TERM, coefficient.zero());
        m.setZero();
        m.set(exponents, coefficient);
        return m;
    }
    public final /*<R extends Arithmetic>*/ UnivariatePolynomial/*<R>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, int exponent) {
        Arithmetic v[] = new Arithmetic[exponent + 1];
        Arrays.fill(v, coefficient.zero());
        v[exponent] = coefficient;
        return polynomial(v);
    }

    // univariate polynomial constructors and utilities

    public /*<R extends Arithmetic>*/ UnivariatePolynomial/*<R>*/ polynomial(Arithmetic/*>R<*/[] coefficients) {
        return new ArithmeticUnivariatePolynomial/*<R>*/(coefficients, this);
    }

    // @todo implement a true view flexible for changes (but only if Polynomial.set(...) has been introduced)
    public /*<R extends Arithmetic>*/ UnivariatePolynomial/*<R>*/ asPolynomial(Vector/*<R>*/ a) {
        return polynomial((Arithmetic/*>R<*/[])a.toArray());
    }

    public /*<R extends Arithmetic>*/ UnivariatePolynomial/*<R>*/ constant(UnivariatePolynomial/*<R>*/ p) {
        // Polynomials are currently unmodifiable anyhow.
        //@xxx except via iterator()
        return p;
    }


    // quotient constructors

    public /*<M extends Arithmetic>*/ Quotient/*<M>*/ quotient(Arithmetic/*>M<*/ a, Function/*<M,M>*/ mod) {
        return new AbstractQuotient/*<M>*/(a, mod);
    }
    public /*<M extends Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, Euclidean/*>M<*/ m) {
        return new AbstractQuotient/*<M>*/(a, m);
    }
    public /*<R extends Arithmetic, S extends Arithmetic>*/ Quotient/*<Polynomial<R,S>>*/ quotient(Polynomial/*<R,S>*/ a, java.util.Set/*<Polynomial<R,S>>*/ m, java.util.Comparator/*<S>*/ monomialOrder) {
        assert m.equals(AlgebraicAlgorithms.groebnerBasis(m,monomialOrder)) : m + " is a Groebner basis with respect to " + monomialOrder;
        return quotient(a, AlgebraicAlgorithms.reduce(m, monomialOrder));
    }

    // fraction constructors

    public /*<M extends Arithmetic, S extends M>*/ Fraction/*<M,S>*/ fraction(Arithmetic/*>M<*/ a, Arithmetic/*>S<*/ s) {
        return new AbstractFraction/*<M,S>*/(a, s);
    }

    // symbol constructors

    public Symbol symbol(String signifier) {
        return new AbstractSymbol(signifier, this);
    }

    /**
     * Checks whether an expression is symbolic
     * (does not only contain numeric quantities, but also symbols).
     * @todo if this is good, move to Symbol.
     * @internal Functionals.banana(...)
     */
    static final Predicate symbolic = new Predicate() {
            public boolean apply(Object expression) {
                if (expression instanceof Functor.Composite) {
                    Functor.Composite c = (Functor.Composite)expression;
                    //@internal see import orbital.logic.sign.concrete.Notation.compositorTree(...)
                    Object     compositor = c.getCompositor();
                    Collection components = Utility.asCollection(c.getComponent());
                    if (components == null)
                        throw new NullPointerException(c + " of " + c.getClass() + " has compositor " + compositor + " and components " + components);
                    return apply(compositor) || Setops.some(components, this);
                } else if (expression instanceof orbital.logic.functor.VoidFunction)
                    //@internal accept also Functions$ConstantFunction
                    return apply(((orbital.logic.functor.VoidFunction)expression).apply());
                else
                    return Utility.isIteratable(expression)
                        ? Setops.some(Utility.asIterator(expression), this)
                        : Symbol.isa.apply(expression);
            }
        };
    
    
    // general static methods for scalar values

    public Arithmetic parse(String s) throws NumberFormatException {
        try {
            final ParsePosition status = new ParsePosition(0);
            final Arithmetic v = ArithmeticFormat.getDefaultInstance().parse(s, status);
            if (status.getIndex() != s.length()) {
                if (status.getIndex() == 0) {
                    throw new NumberFormatException("ArithmeticFormat.parse(String) failed at " + status + (status.getErrorIndex() < s.length() ? " '" + s.charAt(status.getErrorIndex()) + "'" : " <beyond length>") + " in " + s);
                } else {
                    throw new NumberFormatException("ArithmeticFormat.parse(String) could only parse partial string. Partial result " + v + " up to position " + status + " '" + s.charAt(status.getIndex()) + "' in " + s);
                }
            }
            return v;
        }
        catch(ClassCastException x) {throw new NumberFormatException("found " + x.getMessage());}
    }
    public Arithmetic valueOf(String s) throws NumberFormatException {
        return parse(s);
    }


    // precision-safe conversion helpers

    /**
     * Converts the given scalar to int exactly, throwing exceptions if impossible without loss of precision.
     * @throws ArithmeticException if conversion results in loss of precision.
     */
    static final int intValueExact(Scalar b) {
        if (b instanceof AbstractInteger.Int) {
            return ((Integer)b).intValue();
        } else if (b instanceof AbstractReal.Big) {
            return ((AbstractReal.Big)b).getValue().intValueExact();
        } else {
            // convert to int and check for equality
            int l;
            if (b instanceof Number)
                l = ((Number)b).intValue();
            else if (b instanceof Integer)
                l = ((Integer)b).intValue();
            else
                throw new IllegalArgumentException("cannot convert to intValue() at all");
            Integer i = b.valueFactory().valueOf(l);
            if (i.equals(b))
                return l;
            else
                throw new ArithmeticException("cannot convert to intValueExact: " + b + " differs from int " + l);
        }
        /*if (b instanceof AbstractInteger.Int) {
            return ((Integer)b).intValue();
        } else {
            //@xxx check implementation
            final long bv = b.longValue();
            if (java.lang.Integer.MIN_VALUE <= bv && bv <= java.lang.Integer.MAX_VALUE)
                return (int)bv;
            else
                throw new ArithmeticException("intValueExact conversion with possible loos of precision");
                }*/
    }
    static final int intValueExact(Number b) {
    	if (b instanceof Scalar) {
    		return intValueExact((Scalar)b);
    	}
        if (b instanceof AbstractInteger.Int) {
            return ((Integer)b).intValue();
        } else if (b instanceof java.lang.Integer) {
            return ((Integer)b).intValue();
        } else if (b instanceof AbstractReal.Big) {
            return ((AbstractReal.Big)b).getValue().intValueExact();
        } else {
            // convert to int and check for equality
            int l = b.intValue();
            Integer i = Values.getDefault().valueOf(l);
            if (i.equals(b))
                return l;
            else
                throw new ArithmeticException("cannot convert to intValueExact: " + b + " differs from int " + l);
        }
    }
    /**
     * Converts the given scalar to long exactly, throwing exceptions if impossible without loss of precision.
     * @throws ArithmeticException if conversion results in loss of precision.
     */
    static final long longValueExact(Scalar b) {
        if (b instanceof AbstractInteger.Int || b instanceof AbstractInteger.Long) {
            return ((Integer)b).longValue();
        } else if (b instanceof AbstractReal.Big) {
            return ((AbstractReal.Big)b).getValue().longValueExact();
        } else {
            // convert to long and check for equality
            long l;
            if (b instanceof Number)
                l = ((Number)b).longValue();
            else if (b instanceof Integer)
                l = ((Integer)b).longValue();
            else
                throw new IllegalArgumentException("cannot convert to longValue() at all");
            Integer i = b.valueFactory().valueOf(l);
            if (i.equals(b))
                return l;
            else
                throw new ArithmeticException("cannot convert to longValueExact: " + b + " differs from long " + l);
            /* // the following alternative code is crap as it doesn't really check for success
               final float f = r.floatValue();
               //@xxx also convert bigger integers down
               if (f in range and MathUtilities.isInteger(f))
               return valueOf((long) f);
            */
        }
    }
    static final long longValueExact(Number b) {
    	if (b instanceof Scalar) {
    		return longValueExact((Scalar)b);
    	}
        if (b instanceof AbstractInteger.Int || b instanceof AbstractInteger.Long) {
            return ((Integer)b).longValue();
        } else if (b instanceof java.lang.Integer || b instanceof java.lang.Long) {
            return ((Number)b).longValue();
        } else if (b instanceof AbstractReal.Big) {
            return ((AbstractReal.Big)b).getValue().longValueExact();
        } else {
            // convert to long and check for equality
            long l = b.longValue();
            Integer i = Values.getDefault().valueOf(l);
            if (i.equals(b))
                return l;
            else
                throw new ArithmeticException("cannot convert to longValueExact: " + b + " differs from long " + l);
        }
    }
    
    /**
     * Converts the given scalar to double exactly, throwing exceptions if impossible without loss of precision.
     * @throws ArithmeticException if conversion results in loss of precision.
     */
    static final double doubleValueExact(Real b) {
        if (b instanceof AbstractReal.Float || b instanceof AbstractReal.Double || b instanceof AbstractInteger.Int || b instanceof AbstractInteger.Int) {
            return ((Real)b).doubleValue();
        } else {
            // convert to double and check for equality
            double l;
            if (b instanceof Number)
                l = ((Number)b).doubleValue();
            else if (b instanceof Real)
                l = ((Real)b).doubleValue();
            else
                throw new IllegalArgumentException("cannot convert to doubleValue() at all");
            Real i = b.valueFactory().valueOf(l);
            if (i.equals(b))
                return l;
            else
                throw new ArithmeticException("cannot convert to doubleValueExact: " + b + " differs from double " + l);
            /*//@xxx check implementation
        final double bv = b.doubleValue();
        if (!java.lang.Double.isInfinite(bv))
            return bv;
        else
            throw new ArithmeticException("doubleValueExact conversion with possible loos of precision");
        */
        }
    }
    static final double doubleValueExact(Number b) {
    	if (b instanceof Real) {
    		return doubleValueExact((Real)b);
    	}
        if (b instanceof AbstractReal.Float || b instanceof AbstractReal.Double || b instanceof AbstractInteger.Int) {
            return ((Real)b).doubleValue();
        } else if (b instanceof java.lang.Double || b instanceof java.lang.Float || b instanceof java.lang.Integer) {
            return ((Number)b).doubleValue();
        } else {
            // convert to double and check for equality
            double l = b.doubleValue();
            Real i = Values.getDefault().valueOf(l);
            if (i.equals(b))
                return l;
            else
                throw new ArithmeticException("cannot convert to doubleValueExact: " + b + " differs from double " + l);
        }
    }

    // @todo optimize by avoiding to create intermediate objects, f.ex. convert complex(2+i*0) -> real(2) -> rational(2) -> integer(2) also use OBDD
    public final Scalar narrow(Scalar val) {
        Scalar r = narrowImpl(val);
        assert val.equals(r) : "narrowing does not change content";
        return r;
    }
    private final Scalar narrowImpl(Scalar val) {
        if (val instanceof Integer)
            return val;
        if (Complex.hasType.apply(val)) {
            Complex c = (Complex) val;
            if (!c.im().isZero())
                return val;
            else
                val = c.re();
        }
        if (Real.isa.apply(val)) {
            Real r = (Real) val;
            // real to int narrowing attempts
            if (r instanceof AbstractReal.Big) {
                try {
                    return valueOf(((AbstractReal.Big)r).getValue().toBigIntegerExact());
                }
                catch (ArithmeticException fractional) {/*ignore*/}
                try {
                    assert !r.equals(valueOf(((Number)r).longValue())) : "conversion to BigInteger failed hence no conversion to long needs to be attempted " + r;
                }
                catch (ArithmeticException nonconform_trial) {/*ignore*/}
                catch (UnsupportedOperationException nonconform_trial) {/*ignore*/}
            } else {
                try {
                    return valueOf(longValueExact(r));
                }
                catch (ArithmeticException nonconform_trial) {/*ignore*/}
                catch (UnsupportedOperationException nonconform_trial) {/*ignore*/}
            }
            // rational discovery
            if (Rational.isa.apply(val)) {
                Rational f = (Rational) val;
                Rational c = f.representative();
                if (c.denominator().isOne()) {
                        // rational to integer narrowing
                        return c.numerator();
                } else {
                        // could return canceled out or original representative
                        return (Rational) c;
                }
            } else {
                return val;
            }
        } else
            // some other unknown scalar thing
            return val;
    } 


    /**
     * Get two minimized Arithmetic objects whose values equal the specified numbers,
     * while both values returned will have the same type.
     * @return an array with two elements,
     * the first being an Arithmetic object of the same value as a,
     * the second being an Arithmetic object of the same value as b,
     * that both have the same minimum (that is most restrictive) type.
     * This means that an integer will be returned instead of a real whenever possible,
     * a real instead of a complex and so on.
     * But it will always be true that both elements returned have exactly the same type:
     * the common superclass of the classes of a and b.
     * @see #getCoercer()
     * @postconditions RES[0].getClass() == RES[1].getClass() == a.getClass()&cup;b.getClass()
     * @todo protected
     */
    public abstract Scalar[] minimumCoerced(Number a, Number b);

    // arithmetic widening coercer
        
    /*
     * @todo optimize hotspot
     */
    Arithmetic[] minimumCoerced(Arithmetic[] a, boolean commutative) {
        assert a.length == 2 : "currently for binary operations, only";
        assert a[0] != null && a[1] != null : "coercing non-null values " + a[0] + " and " + a[1];
        if (a[0].getClass() == a[1].getClass())
            return a;
        else if (a[0] instanceof Number && a[1] instanceof Number)
            return minimumCoerced((Number) a[0], (Number) a[1]);
        else if (a[0] instanceof Scalar && a[1] instanceof Scalar)
            throw new IllegalArgumentException("Scalar types are assumed to be instances of Number");
        else if (a[0] instanceof Tensor || a[1] instanceof Tensor) {
            if (!(a[0] instanceof Tensor)) {
                if (commutative)
                    //@xxx assuming that non-tensors with tensors are commutative (though tensors themselves is not w.r.t. multiplication).
                    // Would need Arithmetic.isCommutative() and Arithmetic.isRingCommutative() checks for this
                    return new Arithmetic[] {a[1], a[0]};
                else
                    logger.log(Level.FINE, "cannot coerce non-tensor with tensor without commutativity {0} with {1}", a);
            }
            return a;
        } else if (a[0] instanceof MathFunctor || a[0] instanceof Symbol)
                return a;
        else if (a[1] instanceof MathFunctor || a[1] instanceof Symbol) {
            if (commutative)
                //@xxx do we commute in this case or not? Results are better if we do but less deterministic
                return new Arithmetic[] {a[1], a[0]};
            else
                return new Arithmetic[] {
                    makeSymbolAware(a[0]), a[1]
                };  //XXX: how exactly?
        } else if (a[0] instanceof Fraction) {
                if (a[1] instanceof Fraction) {
                throw new IllegalArgumentException("the types of the arguments could not be coerced: " + (a == null ? "null" : a[0].getClass() + "") + " and " + (a[1] == null ? "null" : a[1].getClass() + ""));
            } else {
                return new Fraction[] {(Fraction)a[0], fraction(a[1])};
            }
        } else if (a[1] instanceof Fraction) {
                if (a[0] instanceof Fraction) {
                throw new IllegalArgumentException("the types of the arguments could not be coerced: " + (a == null ? "null" : a[0].getClass() + "") + " and " + (a[1] == null ? "null" : a[1].getClass() + ""));
            } else {
                return new Fraction[] {fraction(a[0]), (Fraction)a[1]};
            }
        } else {
            throw new IllegalArgumentException("the types of the arguments could not be coerced: " + (a == null ? "null" : a[0].getClass() + "") + " and " + (a[1] == null ? "null" : a[1].getClass() + ""));
        } 
    }

    /**
     * @todo beautify and check whether it is necessary to convert numbers to those symbolic arithmetic function trucs!
     * @todo xxx see Functionals.genericCompose(*Function, ...) calls to constant(...)
     */
    private static final Arithmetic makeSymbolAware(Arithmetic x) {
        assert !(x instanceof MathFunctor || x instanceof Symbol) : "math functors and symbols are already aware of symbols";
        //TODO: think about
        return orbital.math.functional.Functions.constant(x);
    } 
    
}
