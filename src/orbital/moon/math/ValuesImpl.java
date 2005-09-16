/**
 * @(#)ValuesImpl.java 1.1 2002-12-06 Andre Platzer
 * 
 * Copyright (c) 2000-2002 Andre Platzer. All Rights Reserved.
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

import java.util.List;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Arrays;
import orbital.util.Setops;
import orbital.util.Utility;

import orbital.math.functional.Functions;

/**
 * Default value constructor factory.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo Think about using a two-dimensional dynamic dispatch in two variables for
 * +:RxS->T. with plus[inf{r.typeId(),s.typeId()}][inf{r.precisionId(),s.precisionId()}]
 * Use internal interfaces orbital.moon.math.TypeIdentified {int typeId();}
 * This way we achieve a prioritized rule-based system for addition operation "rules".
 * Also achieve fast normalizers with one dimensional dynamich dispatch in two variables
 * getValueFactory().getNormalizer().apply((Arithmetic)x) performs internalTypeNormalizer[x.typeId()].apply(x) which only gets called with object of one fitting type.
 */
public class ValuesImpl extends AbstractValues {
    // instantiation

    public ValuesImpl() {
        //@internal with this extra method we circumvent the compile-time initializer problem of the super(...) solution, and the permission problem for applets in the setCoercer(...) solution-
        initialSetCoercer(new orbital.logic.functor.Function/*<Object[],Object[]>*/() {
            public Object/*>Object[]<*/ apply(Object/*>Object[]<*/ o) {
                if (o instanceof Arithmetic[]) {
                    Arithmetic operands[] = (Arithmetic[]) o;
                    if (operands.length <= 1)
                        return operands;
                    return minimumCoerced(operands);
                } 
                return o;
            } 
            });
        //@todo improve normalizer implementation and use it within our implementation
        initialSetNormalizer(orbital.logic.functor.Functions.id);
    }

    // scalar value constructors - facade factory
    // primitive type conversion methods

    // integer scalar value constructors - facade factory

    public Integer valueOf(int val) {
        // If -MAX_CONSTANT < val < MAX_CONSTANT, return stashed constant
        if (0 <= val && val <= MAX_CONSTANT)
            return posConst[val];
        else if (-MAX_CONSTANT <= val && val < 0)
            return negConst[-val];
        else
            return new AbstractInteger.Int(val);
    } 
    public Integer valueOf(long val) {
        return -MAX_CONSTANT < val && val < MAX_CONSTANT
            ? valueOf((int) val)
            : new AbstractInteger.Long(val);
    }
    public Integer valueOf(byte val) {
        return valueOf((int) val);
    }
    public Integer valueOf(short val) {
        return valueOf((int) val);
    }
    public Integer valueOf(java.math.BigInteger val) {
        return new AbstractInteger.Big(val);
    }

    // real scalar value constructors - facade factory

    public Real valueOf(double val) {
        return new AbstractReal.Double(val);
    } 
    public Real valueOf(float val) {
        //@xxx return new AbstractReal.Float(val)
        return new AbstractReal.Double(val);
    } 
    public Real valueOf(java.math.BigDecimal val) {
        return new AbstractReal.Big(val);
    }

    // "named" scalar value constructors

    public Rational rational(Integer p, Integer q) {
        return new AbstractRational.RationalImpl((AbstractInteger) p, (AbstractInteger) q);
    } 
    public Rational rational(int p, int q) {
        return new AbstractRational.RationalImpl(p, q);
    } 
    public Rational rational(Integer p) {
        return new AbstractRational.RationalImpl((AbstractInteger) p);
    } 
    public Rational rational(int p) {
        return new AbstractRational.RationalImpl(p);
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
        return complex(a, Values.ZERO);
    } 
    public Complex complex(double a) {
        return complex(a, 0);
    } 

    public Complex cartesian(Real a, Real b) {
        return new AbstractComplex.ComplexImpl(a, b);
    } 
    public Complex cartesian(double a, double b) {
        return new AbstractComplex.ComplexImpl(a, b);
    } 

    public Complex polar(Real r, Real phi) {
        return new AbstractComplex.ComplexImpl(r.multiply((Real) Functions.cos.apply(phi)), r.multiply((Real) Functions.sin.apply(phi)));
    } 
    public Complex polar(double r, double phi) {
        return new AbstractComplex.ComplexImpl(r * Math.cos(phi), r * Math.sin(phi));
    } 


    // vector constructors and conversion utilities
         
    public /*<R extends Arithmetic>*/ Vector/*<R>*/ valueOf(Arithmetic/*>R<*/[] values) {
        return new ArithmeticVector/*<R>*/(values);
    } 
    //@todo couldn't we even return Vector<Real>?
    public Vector valueOf(double[] values) {
        return new RVector(values);
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
        return new ArithmeticVector/*<R>*/(dim);
    }

    public /*<R extends Scalar>*/ Vector/*<R>*/ BASE(int n, int i) {
        ArithmeticVector/*<R>*/ base = (ArithmeticVector/*<R>*/) (Vector/*<R>*/) newInstance(n);
        for (int j = 0; j < base.dimension(); j++)
            base.D[j] = (Arithmetic/*>R<*/) (j == i ? ONE : ZERO);
        return base;
    } 

    public /*<R extends Arithmetic>*/ Vector/*<R>*/ CONST(int n, Arithmetic/*>R<*/ c) {
        ArithmeticVector/*<R>*/ constant = (ArithmeticVector/*<R>*/) (Vector/*<R>*/) newInstance(n);
        Arrays.fill(constant.D, c);
        return constant;
    } 


    // matrix constructors and conversion utilities

    public /*<R extends Arithmetic>*/ Matrix/*<R>*/ valueOf(Arithmetic/*>R<*/[][] values) {
        return new ArithmeticMatrix/*<R>*/(values);
    } 
    public Matrix valueOf(double[][] values) {
        return new RMatrix(values);
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
        return new ArithmeticMatrix/*<R>*/(dimension);
    } 
    public /*<R extends Arithmetic>*/ Matrix/*<R>*/ newInstance(int height, int width) {
        return new ArithmeticMatrix/*<R>*/(height, width);
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
        return new ArithmeticTensor(values);
    }
    public Tensor tensor(Object values) {
        AbstractTensor t = new ArithmeticTensor(values);
        // tensors of rank 1 or rank 2 are converted to vectors or matrices
        switch (t.rank()) {
        case 0:
            assert false;
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
            assert false;
        case 1:
            return validate(newInstance(dimensions[0]), dimensions);
        case 2:
            return validate(newInstance(dimensions[0], dimensions[1]), dimensions);
        default:
            return validate(new ArithmeticTensor(dimensions), dimensions);
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
            i.set(Values.ZERO);
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
        case 1:
            // @todo implement a true view flexible for changes (but only if Polynomial.set(...) has been introduced)
            return polynomial((Arithmetic[]) ((AbstractTensor)coefficients).toArray__Tensor());
        default:
            return new ArithmeticMultivariatePolynomial(coefficients);
        }
    }

    public /*<R extends Arithmetic>*/ Tensor/*<R>*/ asTensor(Polynomial/*<R,Vector<Integer>>*/ p) {
        return ((AbstractMultivariatePolynomial)p).tensorViewOfCoefficients();
    }

    public /*<R extends Arithmetic, S extends Arithmetic>*/ Polynomial/*<R,S>*/ constant(Polynomial/*<R,S>*/ p) {
        // Polynomials are currently unmodifiable anyhow.
        //@xxx except via iterator()
        return p;
    }

    // @internal horribly complicate implementation
    public final /*<R extends Arithmetic>*/ Polynomial/*<R,Vector<Integer>>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, int[] exponents) {
        int[] dim = new int[exponents.length];
        for (int k = 0; k < dim.length; k++)
            dim[k] = exponents[k] + 1;
        AbstractMultivariatePolynomial m = new ArithmeticMultivariatePolynomial(dim);
        m.set(m.CONSTANT_TERM, coefficient.zero());
        m.setAllZero(m);
        m.set(exponents, coefficient);
        return m;
    }

    // univariate polynomial constructors and utilities

    public /*<R extends Arithmetic>*/ UnivariatePolynomial/*<R>*/ polynomial(Arithmetic/*>R<*/[] coefficients) {
        return new ArithmeticUnivariatePolynomial/*<R>*/(coefficients);
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
        return new AbstractSymbol(signifier);
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

    public Arithmetic valueOf(String s) throws NumberFormatException {
        try {
            return ArithmeticFormat.getDefaultInstance().parse(s);
        }
        catch(ClassCastException x) {throw new NumberFormatException("found " + x.getMessage());}
        catch(ParseException x) {throw new NumberFormatException(x.toString());}
    }

    // @todo optimize by avoiding to create intermediate objects, f.ex. convert complex(2+i*0) -> real(2) -> rational(2) -> integer(2) also use OBDD
    public final Scalar narrow(Scalar val) {
        if (val instanceof Integer)
            return val;
        if (Complex.hasType.apply(val)) {
            Complex c = (Complex) val;
            if (!c.im().equals(Values.ZERO))
                return val;
            else
                val = c.re();
        }
        if (Real.isa.apply(val)) {
            Real r = (Real) val;
            try {
                if (MathUtilities.isInteger(r.doubleValue()))
                    return valueOf((long) r.doubleValue());
            } catch (UnsupportedOperationException nonconform_trial) {
                // ignore
            } 
            if (Rational.isa.apply(val))
                return (Rational) val;
            else
                return val;
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
     * @todo privatize
     * @todo optimize hotspot
     */
    public Scalar[] minimumCoerced(Number a, Number b) {
        //@xxx adapt better to new Complex>Real>Rational>Integer type hierarchy and conform to a new OBDD (ordered binary decision diagram)
        //@todo partial order with Arithmetic>Scalar>Complex>Real>Rational>Integer and greatest common super type of A,B being A&cup;B = sup {A,B}
        //@todo implement sup along with conversion routines. Perhaps introduce "int AbstractScalar.typeLevel()" and "int AbstractScalar.precisionLevel()" such that we can compute the maximum level of both with just two method calls. And introduce "Object AbstractScalar.convertTo(int typeLevel, int precisionLevel)" for conversion.
        if (Complex.hasType.apply(a) || Complex.hasType.apply(b))
            return new Complex[] {
                Complex.hasType.apply(a) ? (Complex) a : new AbstractComplex.ComplexImpl(a), Complex.hasType.apply(b) ? (Complex) b : new AbstractComplex.ComplexImpl(b)
            };

        // this is a tricky binary decision diagram (optimized), see documentation
        if (Integer.hasType.apply(a)) {
            if (Integer.hasType.apply(b))
                return new Integer[] {
                    new AbstractInteger.Long(a), new AbstractInteger.Long(b)
                };
        } else {        // a is no integer
            if (!Rational.hasType.apply(a))
                return new Real[] {
                    new AbstractReal.Double(a), new AbstractReal.Double(b)
                };
        } 
        
        /* fall-through: all other cases come here */
        if (Rational.hasType.apply(b))
            return new Rational[] {
                Rational.hasType.apply(a) ? (Rational) a : rational(a.intValue()), Rational.hasType.apply(b) ? (Rational) b : rational(b.intValue())
            };
        //@xxx Rational + Integer != Real
        return new Real[] {
            new AbstractReal.Double(a), new AbstractReal.Double(b)
        };
    } 

    // arithmetic widening coercer
        
    /*
     * @todo optimize hotspot
     */
    private final Arithmetic[] minimumCoerced(Arithmetic[] a) {
        assert a.length == 2 : "currently for binary operations, only";
        //@todo!
        if (a[0].getClass() == a[1].getClass())
            return a;
        else if (a[0] instanceof Number && a[1] instanceof Number)
            return minimumCoerced((Number) a[0], (Number) a[1]);
        else if (a[0] instanceof Tensor || a[1] instanceof Tensor)
            return a;
        else if (a[0] instanceof MathFunctor || a[0] instanceof Symbol)
                return a;
        else if (a[1] instanceof MathFunctor || a[1] instanceof Symbol)
            return new Arithmetic[] {
                makeSymbolAware(a[0]), a[1]
            };  //XXX: how exactly?
        throw new AssertionError("the types of the arguments could not be coerced: " + (a == null ? "null" : a[0].getClass() + "") + " and " + (a[1] == null ? "null" : a[1].getClass() + ""));
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
    
    // Constants

    /**
     * Initialize static constant array when class is loaded.
     * @invariants 0 < MAX_CONSTANT < Integer.MAX_VALUE
     * @xxx note that we should think about the order of static initialization.
     */
    private static final int     MAX_CONSTANT = 10;
    private static final Integer posConst[] = new Integer[MAX_CONSTANT + 1];
    private static final Integer negConst[] = new Integer[MAX_CONSTANT + 1];
    static {
        posConst[0] = negConst[0] = new AbstractInteger.Long(0);
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            posConst[i] = new AbstractInteger.Long(i);
            negConst[i] = new AbstractInteger.Long(-i);
        } 
    } 
}
