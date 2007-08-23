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

    // provides no scalar constructors

    // vector constructors and conversion utilities
         
    public /*<R extends Arithmetic>*/ Vector/*<R>*/ valueOf(Arithmetic/*>R<*/[] values) {
        return new ArithmeticVector/*<R>*/(values);
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

    // @todo optimize by avoiding to create intermediate objects, f.ex. convert complex(2+i*0) -> real(2) -> rational(2) -> integer(2) also use OBDD
    public final Scalar narrow(Scalar val) {
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
            try {
		//@xxx also convert bigger integers down
                if (MathUtilities.isInteger(r.floatValue()))
                    return valueOf((long) r.floatValue());
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
     * @todo protected
     */
    public abstract Scalar[] minimumCoerced(Number a, Number b);

    // arithmetic widening coercer
        
    /*
     * @todo optimize hotspot
     */
    final Arithmetic[] minimumCoerced(Arithmetic[] a, boolean commutative) {
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
	}
        throw new IllegalArgumentException("the types of the arguments could not be coerced: " + (a == null ? "null" : a[0].getClass() + "") + " and " + (a[1] == null ? "null" : a[1].getClass() + ""));
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
