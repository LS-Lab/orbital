/**
 * @(#)Values.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

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
import java.util.ListIterator;
import orbital.math.Arithmetic;
import orbital.math.Real;
import orbital.math.Normed;
import java.util.Iterator;
import orbital.math.Tensor;

/**
 * Scalar value and arithmetic object value constructor and utilities class.
 * <p>
 * This class is the central factory facade for instantiating new arithmetic objects
 * from primitive types values.
 * </p>
 * <p>
 * Since most general arithmetic objects are modelled as interfaces to provide a maximum of
 * flexibility, you need factory methods to create an arithmetic object value. The class
 * <tt><a href="Values.html">Values</a></tt> is that central factory class which can
 * create arithmetic object values from all kinds of primitive types.
 * This indirection introduces a more loosely coupled binding between users and providers
 * of arithmetic object classes.
 * When using a static final singleton factory like from {@link #getDefaultInstance()},
 * JIT compilers can optimize the resulting code to prevent performance loss.
 * Indeed, because of this delegative construction the
 * factory method can chose the best implementation class suitable for a specific primitive type
 * and size.
 * </p>
 * 
 * @version 1.0, 2000/08/08
 * @author  Andr&eacute; Platzer
 * @see <a href="{@docRoot}/Patterns/Design/AbstractFactory.html">Abstract Factory</a>
 * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade</a>
 * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
 * @see #getInstance()
 * @see #getDefaultInstance()
 * @see java.util.Arrays
 * @todo perhaps we should only call true primitive and java.lang.Number type conversion methods valueOf(...). rename the rest of them according to the type they return. 
 */
public class Values {
    private orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer = new orbital.logic.functor.Function/*<Object[],Object[]>*/() {
	    public Object/*>Object[]<*/ apply(Object/*>Object[]<*/ o) {
		if (o instanceof Arithmetic[]) {
		    Arithmetic operands[] = (Arithmetic[]) o;
		    if (operands.length <= 1)
			return operands;
		    return minimumEqualized(operands);
		} 
		return o;
	    } 
	};

    // instantiation

    protected Values() {}

    /**
     * Returns a new value factory with default settings.
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @todo provide configurable settings like
     *  whether polynomials save their coefficients in tensors (like ArithmeticMultivariatePolynomial), or in Maps.
     *  whether Quotient or Fraction use lazy representatives, i.e. only calculate their representative when representative() (or numerator?) gets called.
     *  whether Real... automatically fallback to bigger or more general implementations (including Real.Big), instead of sticking to machine-size at the risk of overflows.
     *  whether Real... automatically narrows doens its results.
     */
    public static Values getInstance() {
	return new Values();
    }

    /**
     * Default instance.
     */
    private static final Values defaultValues = getInstance();
	
    /**
     * Get the (single) default instance of this factory.
     * @see <a href="{@docRoot}/Patterns/Design/Singleton.html">&quot;Singleton&quot;</a>
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static final Values getDefaultInstance() {
	return defaultValues;
    }

    // legacy conversion primitve wrapper utilities
    
    /**
     * Determines whether the specified class is an (old JDK1.0) wrapper for a primitive type.
     * Legacy query method.
     * @see Class#isPrimitive()
     * @see java.lang.Integer
     * @see java.lang.Long
     * @see java.lang.Double
     * @see java.lang.Float
     * @see java.lang.Byte
     * @see java.lang.Short
     */
    public static boolean isPrimitiveWrapper(Class clazz) {
	if (!Number.class.isAssignableFrom(clazz))
	    return false;
	else
	    //@internal note that those primitive wrapper types are final
	    return java.lang.Integer.class.equals(clazz)
		|| java.lang.Long.class.equals(clazz)
		|| java.lang.Double.class.equals(clazz)
		|| java.lang.Float.class.equals(clazz)
		|| java.lang.Byte.class.equals(clazz)
		|| java.lang.Short.class.equals(clazz);
    }

    /**
     * Returns a primitive type wrapper for the specified scalar.
     * Legacy conversion method.
     * Determines whether the specified class is an (old JDK1.0) wrapper for a primitive type.
     * @see #valueOf(Number)
     * @see #isPrimitiveWrapper(Class)
     * @todo when to return java.lang.Float etc.?
     */
    public static Number toPrimitiveWrapper(Scalar val) {
	if (Integer.hasType.apply(val))
	    return new java.lang.Integer(((Integer)val).intValue());
	else if (Real.hasType.apply(val))
	    return new java.lang.Double(((Real)val).doubleValue());
	else if (Rational.hasType.apply(val))
	    return new java.lang.Double(((Real)val).doubleValue());
	else
	    throw new IllegalArgumentException("cannot be wrapped in a primitive wrapper type " + val.getClass());
    }


    
    
    // scalar value constructors - facade factory
    // primitive type conversion methods

    // integer scalar value constructors - facade factory

    /**
     * Returns an Scalar whose value is equal to that of the specified primitive type.
     * @post RES.intValue() == val
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see java.math.BigInteger#valueOf(long)
     */
    public Integer valueOf(int val) {
	// If -MAX_CONSTANT < val < MAX_CONSTANT, return stashed constant
	if (val == 0)
	    return ZERO;
	if (val > 0 && val <= MAX_CONSTANT)
	    return posConst[val];
	else if (val < 0 && val >= -MAX_CONSTANT)
	    return negConst[-val];
	else
	    return new AbstractInteger.Int(val);
    } 
    public Integer valueOf(java.lang.Integer val) {
	return valueOf(val.intValue());
    }
    /**
     * Returns a Scalar whose value is equal to that of the specified primitive type.
     * @post RES.longValue() == val
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see java.math.BigInteger#valueOf(long)
     */
    public Integer valueOf(long val) {
	return -MAX_CONSTANT < val && val < MAX_CONSTANT
	    ? valueOf((int) val)
	    : new AbstractInteger.Long(val);
    }
    public Integer valueOf(java.lang.Long val) {
	return valueOf(val.longValue());
    }
    public Integer valueOf(byte val) {
	return valueOf((int) val);
    }
    public Integer valueOf(java.lang.Byte val) {
	return valueOf(val.byteValue());
    }
    public Integer valueOf(short val) {
	return valueOf((int) val);
    }
    public Integer valueOf(java.lang.Short val) {
	return valueOf(val.shortValue());
    }
    /**
     * Not yet supported.
     * Legacy conversion method.
     * @todo introduce valueOf(BigInteger) and valueOf(BigDecimal) some day. However, care about non-associative precisions.
     */
    public Integer valueOf(java.math.BigInteger val) {
	throw new UnsupportedOperationException("conversion from " + val.getClass() + " is not currently supported, first convert it to a primitive type, instead");
    }

    // real scalar value constructors - facade factory

    /**
     * Returns a Scalar whose value is equal to that of the specified primitive type.
     * @post RES.doubleValue() == val
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see java.math.BigInteger#valueOf(long)
     */
    public Real valueOf(double val) {
	return new AbstractReal.Double(val);
    } 
    public Real valueOf(java.lang.Double val) {
	return valueOf(val.doubleValue());
    }
    /**
     * Returns a Scalar whose value is equal to that of the specified primitive type.
     * @post RES.floatValue() == val
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see java.math.BigInteger#valueOf(long)
     */
    public Real valueOf(float val) {
	//@xxx return new AbstractReal.Float(val)
	return new AbstractReal.Double(val);
    } 
    public Real valueOf(java.lang.Float val) {
	return valueOf(val.floatValue());
    }
    /**
     * Not yet supported.
     * Legacy conversion method.
     */
    public Real valueOf(java.math.BigDecimal val) {
	throw new UnsupportedOperationException("conversion from " + val.getClass() + " is not currently supported, first convert it to a primitive type, instead");
    }

    // scalar value constructors - facade factory

    /**
     * Returns a Scalar whose value is equal to that of the specified number.
     * Legacy conversion method.
     * @return an instance of scalar that has the same value as the number.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see #valueOf(double)
     */
    public Scalar valueOf(Number val) {
	if (val == null)
	    return null;
	else if (val instanceof Scalar)
	    return (Scalar) val;
	else if (val instanceof java.lang.Integer)
	    return valueOf((java.lang.Integer) val);
	else if (val instanceof java.lang.Long)
	    return valueOf((java.lang.Long) val);
	else if (val instanceof java.lang.Double)
	    return valueOf((java.lang.Double) val);
	else if (val instanceof java.lang.Float)
	    return valueOf((java.lang.Float) val);
	else if (val instanceof java.lang.Byte)
	    return valueOf((java.lang.Byte) val);
	else if (val instanceof java.lang.Short)
	    return valueOf((java.lang.Short) val);
	else if (val instanceof java.math.BigInteger)
	    return valueOf((java.math.BigInteger) val);
	else if (val instanceof java.math.BigDecimal)
	    return valueOf((java.math.BigDecimal) val);
	else
	    return narrow(valueOf(val.doubleValue()));
    }

	
    // non-standard naming scalar value constructors

    /**
     * Returns a new rational whose value is equal to p/q.
     * @param p the numerator of p/q.
     * @param q the denominator p/q.
     */
    public Rational rational(Integer p, Integer q) {
	return new AbstractRational.RationalImpl((AbstractInteger) p, (AbstractInteger) q);
    } 
    public Rational rational(int p, int q) {
	return new AbstractRational.RationalImpl(p, q);
    } 
    /**
     * Returns a new (integer) rational whose value is equal to p/1.
     * @param p the numerator of p/1.
     */
    public Rational rational(Integer p) {
	return new AbstractRational.RationalImpl((AbstractInteger) p);
    } 
    public Rational rational(int p) {
	return new AbstractRational.RationalImpl(p);
    } 

    // complex scalar values constructors

    /**
     * Returns a new complex whose value is equal to a + <b>i</b>*b.
     * @param a real part.
     * @param b imaginary part.
     * @return a + <b>i</b>*b.
     * @see #cartesian(Real, Real)
     */
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

    /**
     * Creates a new complex from cartesian coordinates.
     * @param a real part.
     * @param b imaginary part.
     * @return a + <b>i</b>*b.
     * @see #polar(Real, Real)
     */
    public Complex cartesian(Real a, Real b) {
	return new AbstractComplex.ComplexImpl(a, b);
    } 
    public Complex cartesian(double a, double b) {
	return new AbstractComplex.ComplexImpl(a, b);
    } 

    /**
     * Creates a new complex from polar coordinates with r*<b>e</b><sup><b>i</b>&phi;</sup>.
     * @param r = |z| is the length.
     * @param phi = &phi; is the angle &ang; in radians.
     * @pre r&ge;0
     * @return r*<b>e</b><sup><b>i</b>&phi;</sup> = r * (cos &phi; + <b>i</b> sin &phi;).
     * @see #cartesian(Real, Real)
     */
    public Complex polar(Real r, Real phi) {
	return new AbstractComplex.ComplexImpl(r.multiply((Real) Functions.cos.apply(phi)), r.multiply((Real) Functions.sin.apply(phi)));
    } 
    public Complex polar(double r, double phi) {
	return new AbstractComplex.ComplexImpl(r * Math.cos(phi), r * Math.sin(phi));
    } 


    // vector constructors and conversion utilities
	 
    /**
     * Returns a Vector containing the specified arithmetic objects.
     * <p>
     * Note that the resulting vector may or may not be backed by the
     * specified array.
     * </p>
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see #tensor(Arithmetic[])
     */
    public /*<R implements Arithmetic>*/ Vector/*<R>*/ valueOf(Arithmetic/*>R<*/[] values) {
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

    /*<R implements Arithmetic>*/ Vector/*<R>*/ vector(List/*_<R>_*/ values) {
	Vector/*<R>*/   r = newInstance(values.size());
	Iterator/*_<R>_*/   it = values.iterator();
	for (int i = 0; i < values.size(); i++)
	    r.set(i, (Arithmetic/*>R<*/) it.next());
	assert !it.hasNext() : "iterator should be finished after all elements";
	return r;
    }

    /**
     * Creates a new instance of vector with the specified dimension.
     * @param dimensions the dimension of the vector.
     * @return a vector of the specified dimension, with undefined components.
     * @post RES.dimension() == dimension()
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public /*<R implements Arithmetic>*/ Vector/*<R>*/ newInstance(int dim) {
	return new ArithmeticVector/*<R>*/(dim);
    }

    /**
     * Gets zero Vector, with all elements set to <code>0</code>.
     * @internal could also call ZERO(int[]), but the CONST implementation may be faster.
     */
    public Vector ZERO(int n) {
	return CONST(n, Values.ZERO);
    } 

    /**
     * Gets a vector <span class="vector">e<sub>i</sub></span> of an ONB of R<sup>n</sup>.
     * It has all elements set to <code>0</code> except element <code>i</code> set to <code>1</code>.
     * These <span class="vector">e<sub>i</sub></span> are the standard base of <b>R</b><sup>n</sup>:
     * &forall;<span class="vector">x</span>&isin;<b>R</b><sup>n</sup> &exist;! x<sub>k</sub>&isin;<b>R</b>: <span class="vector">x</span> = x<sub>1</sub>*<span class="vector">e<sub>1</sub></span> + ... + x<sub>n</sub>*<span class="vector">e<sub>n</sub></span>.
     */
    public /*<R implements Scalar>*/ Vector/*<R>*/ BASE(int n, int i) {
	ArithmeticVector/*<R>*/ base = (ArithmeticVector/*<R>*/) (Vector/*<R>*/) newInstance(n);
	for (int j = 0; j < base.dimension(); j++)
	    base.D[j] = (Arithmetic/*>R<*/) (j == i ? ONE : ZERO);
	return base;
    } 

    /**
     * Gets a constant Vector, with all elements set to <code>c</code>.
     */
    public /*<R implements Arithmetic>*/ Vector/*<R>*/ CONST(int n, Arithmetic/*>R<*/ c) {
	ArithmeticVector/*<R>*/ constant = (ArithmeticVector/*<R>*/) (Vector/*<R>*/) newInstance(n);
	Arrays.fill(constant.D, c);
	return constant;
    } 


    /**
     * Returns an unmodifiable view of the specified vector.
     * The result is a <a href="Values.html#readOnlyView">read only view</a>.
     */
    public static /*<R implements Arithmetic>*/ Vector/*<R>*/ constant(final Vector/*<R>*/ v) {
	return /*refine/delegate Vector*/ new AbstractVector/*<R>*/() {
		private static final long serialVersionUID = 4473448798599904941L;
		protected Vector/*<R>*/ newInstance(int d) {
		    assert false : "this method should never get called in this context of constant(...)";
		    //@todo there are still some methods missing for the above assertion to prove true. transpose, Tensor add(Tensor);
		    // so we simply pass it through until all these have been added
		    return ((AbstractVector)v).newInstance(d);
		}
		public int dimension() { return v.dimension(); }
		public Arithmetic/*>R<*/ get(int i) { return v.get(i); }
		public void set(int i, Arithmetic/*>R<*/ vi) { throw new UnsupportedOperationException(); }
		protected void set(Arithmetic/*>R<*/ vs[]) { throw new UnsupportedOperationException(); }
		public ListIterator iterator() { return Setops.unmodifiableListIterator((ListIterator)v.iterator()); }
		public boolean equals(Object b) { return v.equals(b); }
		public int hashCode() { return v.hashCode(); }
		public Object clone() { return v.clone(); }
		public Real norm() { return v.norm(); }
		public Real norm(double p) { return v.norm(p); }
		public Arithmetic add(Arithmetic b) { return v.add(b); }
		public Arithmetic subtract(Arithmetic b) { return v.subtract(b); }
		public Arithmetic minus() { return v.minus(); }
		public Arithmetic multiply(Arithmetic b) { return v.multiply(b); }
		public Arithmetic scale(Arithmetic b) { return v.scale(b); }
		public Arithmetic inverse() { return v.inverse(); }
		public Arithmetic divide(Arithmetic b) { return v.divide(b); }
		public Arithmetic power(Arithmetic b) { return v.power(b); }
		public Vector/*<R>*/ cross(Vector/*<R>*/ b) { return v.cross(b); }
		public Matrix/*<R>*/ transpose() { return v.transpose(); }
		public Vector/*<R>*/ insert(int i, Arithmetic/*>R<*/ b) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ insert(int i, Vector/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ append(Arithmetic/*>R<*/ b) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ append(Vector/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ remove(int i) { throw new UnsupportedOperationException(); }
		public Arithmetic/*>R<*/[] toArray() { return v.toArray(); }
		public String toString() { return v.toString(); }
	    };
    }

    // matrix constructors and conversion utilities

    /**
     * Returns a Matrix containing the specified arithmetic objects.
     * <p>
     * Matrix components are expected row-wise, which means that
     * as the first index in <code>values</code>, the row i is used
     * and as the second index in <code>values</code>, the column j is used.
     * </p>
     * <p>
     * Note that the resulting vector may or may not be backed by the
     * specified array.
     * </p>
     * @param values the element values of the matrix to create.
     *  The matrix may be backed by this exact array per reference.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see #tensor(Arithmetic[][])
     */
    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ valueOf(Arithmetic/*>R<*/[][] values) {
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

    static /*<R implements Arithmetic>*/ Matrix/*<R>*/ matrix(List/*_<List<R>>_*/ values) {
	throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Creates a new instance of matrix with the specified dimension.
     * @param dimension the dimension of the matrix.
     * @return a matrix of the specified dimensions, with undefined components.
     * @post RES.dimension().equals(dimension)
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ newInstance(Dimension dimension) {
	return new ArithmeticMatrix/*<R>*/(dimension);
    } 
    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ newInstance(int height, int width) {
	return new ArithmeticMatrix/*<R>*/(height, width);
    } 

    /**
     * Gets zero Matrix, with all elements set to 0.
     */
    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ ZERO(Dimension dim) {
	return ZERO(dim.height, dim.width);
    } 
    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ ZERO(int height, int width) {
	return (Matrix) ZERO(new int[] {height, width});
    }

    /**
     * Gets the identity Matrix, with all elements set to 0, except the leading diagonal m<sub>i,i</sub> set to 1.
     * @pre dim.width == dim.height
     */
    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ IDENTITY(Dimension dim) {
	return IDENTITY(dim.height, dim.width);
    } 
    /**
     * Gets the identity Matrix, with all elements set to 0, except the leading diagonal m<sub>i,i</sub> set to 1.
     * @pre width == height
     * @see Functions#delta
     * @see #IDENTITY(Dimension)
     */
    public /*<R implements Scalar>*/ Matrix/*<R>*/ IDENTITY(int height, int width) {
	if (!(width == height))
	    throw new IllegalArgumentException("identity matrix is square");
	ArithmeticMatrix/*<R>*/ identity = (ArithmeticMatrix/*<R>*/) (Matrix/*<R>*/) newInstance(height, width);
	for (int i = 0; i < identity.dimension().height; i++)
	    for (int j = 0; j < identity.dimension().width; j++)
		identity.D[i][j] = (Arithmetic/*>R<*/) valueOf(orbital.math.functional.Functions.delta(i, j));
	return identity;
    } 

    /**
     * Gets diagonal Matrix, with all elements set to 0, except the leading diagonal m<sub>i,i</sub> set to v<sub>i</sub>.
     * @see Functions#delta
     * @todo turn into a true view?
     */
    public /*<R implements Scalar>*/ Matrix/*<R>*/ DIAGONAL(Vector/*<R>*/ diagon) {
	Matrix/*<R>*/ diagonal = newInstance(new Dimension(diagon.dimension(), diagon.dimension()));
	for (int i = 0; i < diagonal.dimension().height; i++)
	    for (int j = 0; j < diagonal.dimension().width; j++)
		diagonal.set(i, j, i == j ? diagon.get(i) : (Arithmetic/*>R<*/) ZERO);
	return diagonal;
    } 

    /**
     * Returns an unmodifiable view of the specified matrix.
     * The result is a <a href="Values.html#readOnlyView">read only view</a>.
     */
    public static /*<R implements Arithmetic>*/ Matrix/*<R>*/ constant(final Matrix/*<R>*/ m) {
	return /*refine/delegate Matrix*/ new AbstractMatrix/*<R>*/() {
		private static final long serialVersionUID = 482711902153502751L;
		protected Matrix/*<R>*/ newInstance(Dimension d) {
		    assert false : "this method should never get called in this context of constant(...)";
		    //@todo there are still some methods missing for the above assertion to prove true. transpose, Tensor add(Tensor);
		    // so we simply pass it through until all these have been added
		    return ((AbstractMatrix)m).newInstance(d);
		}
		public Dimension dimension() { return m.dimension(); }
		public Arithmetic/*>R<*/ get(int i, int j) { return m.get(i,j); }
		public void set(int i, int j, Arithmetic/*>R<*/ v) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ getColumn(int j) { return m.getColumn(j); }
		public void setColumn(int j, Vector/*<R>*/ v) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ getRow(int i) { return m.getRow(i); }
		public void setRow(int i, Vector/*<R>*/ v) { throw new UnsupportedOperationException(); }
		public void set(Arithmetic/*>R<*/ v[][]) { throw new UnsupportedOperationException(); }
		public ListIterator getColumns() { return Setops.unmodifiableListIterator((ListIterator)m.getColumns()); }
		public ListIterator getRows() { return Setops.unmodifiableListIterator((ListIterator)m.getRows()); }
		public ListIterator iterator() { return Setops.unmodifiableListIterator(m.iterator()); }
		public Vector/*<R>*/ getDiagonal() { return m.getDiagonal(); }
		public boolean isSquare() { return m.isSquare(); }
		public boolean isSymmetric() throws ArithmeticException{ return m.isSymmetric(); }
		public boolean isInvertible() throws ArithmeticException{ return m.isInvertible(); }
		public int isDefinite() throws ArithmeticException{ return m.isDefinite(); }
		public boolean equals(Object b) { return m.equals(b); }
		public int hashCode() { return m.hashCode(); }
		public Object clone() { return m.clone(); }
		public Matrix/*<R>*/ subMatrix(int r1, int r2, int c1, int c2) { return m.subMatrix(r1,r2, c1,c2); }
		public Real norm() { return m.norm(); }
		public Real norm(double p) { return m.norm(p); }
		public Arithmetic/*>R<*/ trace() { return m.trace(); }
		public Arithmetic/*>R<*/ det() { return m.det(); }
		public Arithmetic add(Arithmetic b) {return m.add(b);}
		public Arithmetic minus() {return m.minus();}
		public Arithmetic subtract(Arithmetic b) {return m.subtract(b);}
		public Arithmetic multiply(Arithmetic b) {return m.multiply(b);}
		public Arithmetic scale(Arithmetic b) { return m.scale(b); }
		public Arithmetic inverse() {return m.inverse();}
		public Arithmetic divide(Arithmetic b) {return m.divide(b);}
		public Arithmetic power(Arithmetic b) {return m.power(b);}
		public Matrix add(Matrix b) {return m.add(b);}
		public Matrix subtract(Matrix b) {return m.subtract(b);}
		public Matrix multiply(Matrix b) {return m.multiply(b);}
		public Matrix scale(Scalar b) { return m.scale(b); }
		public Vector multiply(Vector b) {return m.multiply(b);}
		public Matrix/*<R>*/ transpose() { return m.transpose(); }
		public Matrix/*<R>*/ pseudoInverse() { return m.pseudoInverse(); }
		public Matrix/*<R>*/ appendColumns(Matrix/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ appendRows(Matrix/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ insertColumns(int i, Matrix/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ insertRows(int i, Matrix/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ removeColumn(int j) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ removeRow(int i) { throw new UnsupportedOperationException(); }
		public Arithmetic/*>R<*/[][] toArray() {return m.toArray(); }
		public String toString() { return m.toString(); }
	    };
    }

    // tensor constructors
    
    /**
     * Returns a vector containing the specified arithmetic objects.
     * Vectors are the tensors of rank 1.
     * <p>
     * Note that the resulting vector may or may not be backed by the
     * specified array.
     * </p>
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public /*<R implements Arithmetic>*/ Vector/*<R>*/ tensor(Arithmetic/*>R<*/[] values) {
	return valueOf(values);
    }
    /**
     * Returns a matrix containing the specified arithmetic objects.
     * Matrices are the tensors of rank 2.
     * <p>
     * Matrix components are expected row-wise, which means that
     * as the first index in <code>values</code>, the row i is used
     * and as the second index in <code>values</code>, the column j is used.
     * </p>
     * <p>
     * Note that the resulting vector may or may not be backed by the
     * specified array.
     * </p>
     * @param values the element values of the matrix to create.
     *  The matrix may be backed by this exact array per reference.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ tensor(Arithmetic/*>R<*/[][] values) {
	return valueOf(values);
    }
    public /*<R implements Arithmetic>*/ Tensor/*<R>*/ tensor(Arithmetic/*>R<*/[][][] values) {
	return new ArithmeticTensor(values);
    }
    /**
     * Returns a tensor of rank k containing the specified arithmetic objects.
     * <p>
     * Note that the resulting tensor may or may not be backed by the
     * specified array.
     * </p>
     * <p>
     * Tensors of type {@link Vector}, and {@link Matrix} are returned for tensors of rank 1 or 2.
     * </p>
     * @param values the element values of the tensor to create.
     *  The tensor may be backed by this exact array per reference.
     * @pre values is a rectangular multi-dimensional array of {@link Arithmetic arithmetic objects}
     *  or of primitive types
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public Tensor tensor(Object values) {
	AbstractTensor t = new ArithmeticTensor(values);
	// tensors of rank 1 or rank 2 are converted to vectors or matrices
	switch (t.rank()) {
	case 0:
	    assert false;
	case 1:
	    return tensor((Arithmetic[]) (values instanceof Arithmetic[] ? values : t.toArray__Tensor()));
	case 2:
	    return tensor((Arithmetic[][]) (values instanceof Arithmetic[][] ? values : t.toArray__Tensor()));
	default:
	    return t;
	}
    }
    
    /**
     * Creates a new instance of tensor with the specified dimensions.
     * <p>
     * Tensors of type {@link Vector}, and {@link Matrix} are returned for tensors of rank 1 or 2.
     * </p>
     * @param dimensions the dimensions n<sub>1</sub>&times;n<sub>2</sub>&times;&#8230;&times;n<sub>r</sub>
     *  of the tensor.
     * @return a tensor of the specified dimensions, with undefined components.
     * @post Utilities.equalsAll(RES.dimensions(), dimensions)
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public Tensor newInstance(int[] dimensions) {
	// tensors of rank 1 or rank 2 are converted to vectors or matrices
	switch (dimensions.length) {
	case 0:
	    assert false;
	case 1:
	    return newInstance(dimensions[0]);
	case 2:
	    return newInstance(dimensions[0], dimensions[1]);
	default:
	    return new ArithmeticTensor(dimensions);
	}
    }

    /**
     * Gets zero tensor, with all elements set to 0.
     */
    public /*<R implements Arithmetic>*/ Tensor/*<R>*/ ZERO(int[] dimensions) {
	Tensor zero = newInstance(dimensions);
	for (ListIterator i = zero.iterator(); i.hasNext(); ) {
	    i.next();
	    i.set(Values.ZERO);
	}
	return zero;
    }

    /**
     * Returns an unmodifiable view of the specified tensor.
     * <p>
     * The resulting tensor is a <dfn id="readOnlyView">read-only view</dfn> of this tensor.
     * So, this method allows modules to provide users with "read-only" access to constant tensors.
     * <p>
     * Query operations on the returned tensor "read through" to the specified tensor,
     * and attempts to modify the returned tensor, whether direct or via its iterator,
     * result in an {@link java.util.UnsupportedOperationException}.</p>
     * <p>
     * Note that cloning a constant tensor will not return a constant tensor, but a clone of the
     * specified tensor t.</p>
     */
    public static /*<R implements Arithmetic>*/ Tensor/*<R>*/ constant(final Tensor/*<R>*/ t) {
	//@todo would we simplify these by providing a hierarchy of delegation things?
	// but then some tensors would no longer be AbstractTensors.
	//@todo so perhaps just identify multiple delegations to Tensor, or to Polynomial etc.
	return /*refine/delegate Tensor*/ new AbstractTensor/*<R>*/() {
		protected Tensor/*<R>*/ newInstance(int[] dim) {
		    assert false : "this method should never get called in this context of constant(...)";
		    //@todo there are still some methods missing for the above assertion to prove true. transpose, Tensor add(Tensor);
		    // so we simply pass it through until all these have been added
		    return ((AbstractTensor)t).newInstance(dim);
		}
		// Code for delegation of orbital.math.Normed methods to t

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Normed#norm()
		 */
		public Real norm()
		{
		    return t.norm();
		}
		// Code for delegation of orbital.math.Tensor methods to t

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Tensor#clone()
		 */
		public Object clone()
		{
		    return t.clone();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#add(Tensor)
		 */
		public Tensor add(Tensor param1)
		{
		    return t.add(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#get(int[])
		 */
		public Arithmetic get(int[] param1)
		{
		    return t.get(param1);
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Tensor#iterator()
		 */
		public ListIterator iterator()
		{
		    return t.iterator();
		}

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Tensor#set(int[], Arithmetic)
		 */
		public void set(int[] param1, Arithmetic param2) throws UnsupportedOperationException
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Tensor#dimensions()
		 */
		public int[] dimensions()
		{
		    return t.dimensions();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Tensor#rank()
		 */
		public int rank()
		{
		    return t.rank();
		}

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#subTensor(int[], int[])
		 */
		public Tensor subTensor(int[] param1, int[] param2)
		{
		    return t.subTensor(param1, param2);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#subtract(Tensor)
		 */
		public Tensor subtract(Tensor param1)
		{
		    return t.subtract(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#multiply(Tensor)
		 */
		public Tensor multiply(Tensor param1)
		{
		    return t.multiply(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#tensor(Tensor)
		 */
		public Tensor tensor(Tensor param1)
		{
		    return t.tensor(param1);
		}
		// Code for delegation of orbital.math.Arithmetic methods to t

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @return <description>
		 * @see orbital.math.Arithmetic#equals(Object, Real)
		 */
		public boolean equals(Object param1, Real param2)
		{
		    return t.equals(param1, param2);
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Arithmetic#toString()
		 */
		public String toString()
		{
		    return t.toString();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @see orbital.math.Arithmetic#add(Arithmetic)
		 */
		public Arithmetic add(Arithmetic param1) throws ArithmeticException
		{
		    return t.add(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @see orbital.math.Arithmetic#subtract(Arithmetic)
		 */
		public Arithmetic subtract(Arithmetic param1) throws ArithmeticException
		{
		    return t.subtract(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#multiply(Arithmetic)
		 */
		public Arithmetic multiply(Arithmetic param1) throws ArithmeticException, UnsupportedOperationException
		{
		    return t.multiply(param1);
		}

		/**
		 *
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @see orbital.math.Arithmetic#zero()
		 */
		public Arithmetic zero() throws ArithmeticException
		{
		    return t.zero();
		}

		/**
		 *
		 * @return <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#one()
		 */
		public Arithmetic one() throws UnsupportedOperationException
		{
		    return t.one();
		}

		/**
		 *
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @see orbital.math.Arithmetic#minus()
		 */
		public Arithmetic minus() throws ArithmeticException
		{
		    return t.minus();
		}

		/**
		 *
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#inverse()
		 */
		public Arithmetic inverse() throws ArithmeticException, UnsupportedOperationException
		{
		    return t.inverse();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#divide(Arithmetic)
		 */
		public Arithmetic divide(Arithmetic param1) throws ArithmeticException, UnsupportedOperationException
		{
		    return t.divide(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#scale(Arithmetic)
		 */
		public Arithmetic scale(Arithmetic param1) throws ArithmeticException, UnsupportedOperationException
		{
		    return t.scale(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#power(Arithmetic)
		 */
		public Arithmetic power(Arithmetic param1) throws ArithmeticException, UnsupportedOperationException
		{
		    return t.power(param1);
		}

	    };
    }

    // polynomial constructors and utilities

    /**
     * Returns a polynomial with the specified coefficients.
     * The number of variables equals the rank (i.e. number of dimensions)
     * of the array of coefficients.
     * <p>
     * Note that the resulting polynomial may or may not be backed by the
     * specified array.
     * </p>
     * <p>
     * Polynomials of type {@link UnivariatePolynomial}, are returned for
     * polynomials in one variable.
     * </p>
     * @param coefficients a multi-dimensional array <var>a</var> containing the
     *  coefficients of the polynomial.
     * @pre coefficients is a rectangular multi-dimensional array of {@link Arithmetic arithmetic objects}
     *  or of primitive types
     * @return the polynomial <var>a</var><sub>0,...,0</sub> + <var>a</var><sub>1,0,...,0</sub>X<sub>1</sub> + <var>a</var><sub>1,1,0,....,0</sub>X<sub>1</sub>X<sub>2</sub> + ... + <var>a</var><sub>2,1,0,....,0</sub>X<sub>1</sub><sup>2</sup>X<sub>2</sub> + ... + <var>a</var><sub>d<sub>1</sub>,...,d<sub>n</sub></sub>X<sub>1</sub><sup>d<sub>1</sub></sup>...&X<sub>n</sub><sup>d<sub>n</sub></sup>.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public /*<R implements Arithmetic>*/ Polynomial/*<R>*/ polynomial(Object coefficients) {
	return asPolynomial(tensor(coefficients));
    }

    /**
     * Returns a polynomial view of a tensor.
     * <p>
     * The returned polynomial is a <!-- @todo? structurally unmodifiable--> <a href="Tensor.html#view">view</a> of the tensor.
     * It interprets the components of the tensor as the coefficients
     * of a polynomial.</p>
     * @param coefficients a tensor <var>a</var> containing the
     *  coefficients of the polynomial.
     * @return the polynomial <var>a</var><sub>0,...,0</sub> + <var>a</var><sub>1,0,...,0</sub>X<sub>1</sub> + <var>a</var><sub>1,1,0,....,0</sub>X<sub>1</sub>X<sub>2</sub> + ... + <var>a</var><sub>2,1,0,....,0</sub>X<sub>1</sub><sup>2</sup>X<sub>2</sub> + ... + <var>a</var><sub>d<sub>1</sub>,...,d<sub>n</sub></sub>X<sub>1</sub><sup>d<sub>1</sub></sup>...&X<sub>n</sub><sup>d<sub>n</sub></sup>.
     * @see #polynomial(Object)
     * @see #asTensor(Polynomial)
     */
    public /*<R implements Arithmetic>*/ Polynomial/*<R>*/ asPolynomial(Tensor/*<R>*/ coefficients) {
	// polynomials in 1 variable are converted to UnivariatePolynomials
	switch (coefficients.rank()) {
	case 1:
	    // @todo implement a true view flexible for changes (but only if Polynomial.set(...) has been introduced)
	    return polynomial((Arithmetic[]) ((AbstractTensor)coefficients).toArray__Tensor());
	default:
	    return new ArithmeticMultivariatePolynomial(coefficients);
	}
    }

    /**
     * Returns a vector view of the coefficients of a polynomial.
     * <p>
     * The returned tensor is a <!-- @todo? structurally unmodifiable--> <a href="Tensor.html#view">view</a> of the polynomial.
     * It interprets the coefficients of the polynomial as the components
     * of a tensor.</p>
     * @return the tensor (<var>a</var><sub>0,...,0</sub>,...,<var>a</var><sub>d<sub>1</sub>,...,d<sub>n</sub></sub>).
     * @throws ClassCastException if p does not have S=<b>N</b><sup>n</sup> as indices,
     *  and thus is not a multivariate polynomial in the proper sense.
     * @see #asPolynomial(Tensor)
     */
    public /*<R implements Arithmetic>*/ Tensor/*<R>*/ asTensor(Polynomial/*<R>*/ p) {
	return ((AbstractMultivariatePolynomial)p).tensorViewOfCoefficients();
    }

    /**
     * Returns an unmodifiable view of the specified polynomial.
     * The result is a <a href="Values.html#readOnlyView">read only view</a>.
     */
    public static /*<R implements Arithmetic>*/ Polynomial/*<R>*/ constant(Polynomial/*<R>*/ p) {
	// Polynomials are currently unmodifiable anyhow.
	//@xxx except via iterator()
	return p;
    }

    /**
     * The monomial c&middot;X<sup>i</sup>.
     * @param coefficient the coefficient c of the monomial.
     * @param exponent the exponent i of the monomial.
     * @todo implementation could be generalized to non-AbstractMultivariatePolynomials.
     */
    public final Polynomial/*<R,S>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, Arithmetic/*>S<*/ exponent) {
	return MONOMIAL(coefficient, ArithmeticMultivariatePolynomial.convertIndex(exponent));
    }
    /**
     * The monomial c&middot;X<sub>0</sub><sup>i[0]</sup>...X<sub>n-1</sub><sup>i[n-1]</sup>.
     * @param coefficient the coefficient c of the monomial.
     * @param exponents the exponents i of the monomial.
     *  The number of variables is <code>n:=exponents.length</code>.
     * @internal horribly complicate implementation
     */
    public final Polynomial/*<R>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, int[] exponents) {
	int[] dim = new int[exponents.length];
	for (int k = 0; k < dim.length; k++)
	    dim[k] = exponents[k] + 1;
	AbstractMultivariatePolynomial m = new ArithmeticMultivariatePolynomial(dim);
	m.set(m.CONSTANT_TERM, coefficient.zero());
	m.setAllZero(m);
	m.set(exponents, coefficient);
	return m;
    }
    /**
     * The monomial 1&middot;X<sup>i</sup>.
     * Note that the coefficient is {@link #ONE 1}&isin;<b>Z</b>.
     * @param exponent the exponent i of the monomial.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see #MONOMIAL(Arithmetic,Arithmetic)
     */
    public final Polynomial/*<R implements Scalar,S>*/ MONOMIAL(Arithmetic/*>S<*/ exponent) {
	return MONOMIAL(ONE, exponent);
    }
    /**
     * The monomial 1&middot;X<sub>0</sub><sup>i[0]</sup>...X<sub>n-1</sub><sup>i[n-1]</sup>.
     * Note that the coefficient is {@link #ONE 1}&isin;<b>Z</b>.
     * @param exponents the exponents i of the monomial.
     *  The number of variables is <code>n:=exponents.length</code>.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see #MONOMIAL(Arithmetic,int[])
     */
    public final Polynomial/*<R implements Scalar>*/ MONOMIAL(int[] exponents) {
	return MONOMIAL(ONE, exponents);
    }

    // univariate polynomial constructors and utilities

    /**
     * Returns a (univariate) polynomial with the specified coefficients.
     * <p>
     * Note that the resulting polynomial may or may not be backed by the
     * specified array.
     * </p>
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @param coefficients an array <var>a</var> containing the
     * coefficients of the polynomial.
     * @return the polynomial <var>a</var><sub>0</sub> + <var>a</var><sub>1</sub>X + <var>a</var><sub>2</sub>X<sup>2</sup> + ... + <var>a</var><sub>n</sub>X<sup>n</sup> for n=coefficients.length-1.
     * @see #asPolynomial(Vector)
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public /*<R implements Arithmetic>*/ UnivariatePolynomial/*<R>*/ polynomial(Arithmetic/*>R<*/[] coefficients) {
    	return new ArithmeticUnivariatePolynomial/*<R>*/(coefficients);
    }
    /**
     * @see #polynomial(Arithmetic[])
     * @see #polynomial(Object)
     */
    public UnivariatePolynomial/*<Real>*/ polynomial(double[] coefficients) {
	return (UnivariatePolynomial) polynomial((Object)coefficients);
    }
    /**
     * @see #polynomial(Arithmetic[])
     * @see #polynomial(Object)
     */
    public UnivariatePolynomial/*<Integer>*/ polynomial(int[] coefficients) {
	return (UnivariatePolynomial) polynomial((Object)coefficients);
    }

    /**
     * Returns a polynomial view of a vector.
     * <p>
     * The returned polynomial is a <!-- @todo? structurally unmodifiable--> <a href="Tensor.html#view">view</a> of the vector.
     * It interprets the components of the vector as the coefficients
     * of a polynomial.</p>
     * @return the polynomial <var>a</var><sub>0</sub> + <var>a</var><sub>1</sub>X + <var>a</var><sub>2</sub>X<sup>2</sup> + ... + <var>a</var><sub>n</sub>X<sup>n</sup> for n:=a.dimension()-1.
     * @see #polynomial(Arithmetic[])
     * @see #asVector(UnivariatePolynomial)
     * @todo implement a true view flexible for changes (but only if Polynomial.set(...) has been introduced)
     */
    public /*<R implements Arithmetic>*/ UnivariatePolynomial/*<R>*/ asPolynomial(Vector/*<R>*/ a) {
    	return polynomial((Arithmetic/*>R<*/[])a.toArray());
    }

    /**
     * Returns a vector view of the coefficients of a polynomial.
     * <p>
     * The returned vector is a <!-- @todo? structurally unmodifiable--> <a href="Tensor.html#view">view</a> of the polynomial.
     * It interprets the coefficients of the polynomial as the components
     * of a vector.</p>
     * @return the vector (<var>a</var><sub>0</sub>,<var>a</var><sub>1</sub>,<var>a</var><sub>2</sub>,...,<var>a</var><sub>n</sub>) of the polynomial <var>a</var><sub>0</sub> + <var>a</var><sub>1</sub>X + <var>a</var><sub>2</sub>X<sup>2</sup> + ... + <var>a</var><sub>n</sub>X<sup>n</sup> for n=a.degree().
     * @see UnivariatePolynomial#getCoefficients()
     * @see #asPolynomial(Vector)
     */
    public /*<R implements Arithmetic>*/ Vector/*<R>*/ asVector(UnivariatePolynomial/*<R>*/ p) {
    	return (Vector) asTensor(p);
    }

    /**
     * Returns an unmodifiable view of the specified polynomial.
     * The result is a <a href="Values.html#readOnlyView">read only view</a>.
     */
    public static /*<R implements Arithmetic>*/ UnivariatePolynomial/*<R>*/ constant(UnivariatePolynomial/*<R>*/ p) {
	// Polynomials are currently unmodifiable anyhow.
	//@xxx except via iterator()
	return p;
    }


    // quotient constructors

    /**
     * Returns a new quotient a&#772;=[a]&isin;M/mod
     * of the given value reduced with the quotient operator.
     * <p>
     * Note that unlike {@link #quotient(Euclidean,Euclidean) quotients of Euclidean rings}
     * and due to the black-box behaviour of the quotient operator
     * these quotients do not guarantee an implementation of {@link Arithmetic#inverse()}.
     * </p>
     * @param mod is the quotient operator applied (see {@link Quotient#getQuotientOperator()}).
     */
    public /*<M implements Arithmetic>*/ Quotient/*<M>*/ quotient(Arithmetic/*>M<*/ a, Function/*<M,M>*/ mod) {
	return new AbstractQuotient/*<M>*/(a, mod);
    }
    /**
     * Returns a new quotient a&#772;=[a]&isin;M/(m) of the given
     * value reduced modulo m.
     * <p> Will use special remainder classes
     * modulo m in Euclidean rings.  These remainder classes are those
     * induced by the {@link Euclidean#modulo(Euclidean) Euclidean remainder}
     * operator.  Quotients of Euclidean rings have the big
     * advantage of supporting a simple calculation of multiplicative
     * inverses modulo m.
     * </p>
     */
    public /*<M implements Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, Euclidean/*>M<*/ m) {
	return new AbstractQuotient/*<M>*/(a, m);
    }
    /**
     * Returns a new quotient a&#772;=[a]&isin;M/(m) of the given
     * value reduced modulo (m).
     * <p> Will use special remainder classes modulo (m) for a Groebner
     * basis m of (multivariate) polynomial rings. These remainder
     * classes are those induced by the {@link AlgebraicAlgorithms#reduce(java.util.Collection,java.util.Comparator) reduction}
     * operator.  </p>
     * @param m the {@link AlgebraicAlgorithms#groebnerBasis(Set,Comparator) Groebner basis}
     * modulo whose generated ideal (m) to form the quotients.
     * @param monomialOrder the monomial order applied for reducing polynomials.
     */
    public /*<R implements Arithmetic>*/ Quotient/*<Polynomial<R,S>>*/ quotient(Polynomial/*<R,S>*/ a, java.util.Set/*_<Polynomial<R,S>>_*/ m, java.util.Comparator/*_<S>_*/ monomialOrder) {
	return new AbstractQuotient(a, m, monomialOrder);
    }

    // quotient constructor synonyms

    /**
     * (disambiguates type unification).
     * Returns a new quotient a&#772;=[a]&isin;M/(m)
     * of the given value reduced modulo m.
     * <p>
     * <small>Being identical to {@link #quotient(Euclidean,Euclidean)},
     * this method only helps resolving the argument type ambiguity
     * for polynomials. This type ambiguity will not occur at all, if
     * templates have been enabled.</small>
     * </p>
     * @see #quotient(Euclidean,Euclidean)
     */
    public /*<M implements Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, UnivariatePolynomial m) {
	return quotient(a, (Euclidean)m);
    }
    /**
     * (disambiguates type unification).
     * Returns a new quotient a&#772;=[a]&isin;M/mod
     * of the given value reduced with the quotient operator.
     * <p>
     * <small>Being identical to {@link #quotient(Arithmetic,Function)},
     * this method only helps resolving the argument type ambiguity
     * for polynomials. This type ambiguity will not occur at all, if
     * templates have been enabled.</small>
     * </p>
     * @param m is the quotient operator applied (see {@link Quotient#getQuotientOperator()}).
     * @see #quotient(Arithmetic,Function)
     * @internal only for provoking a compile time type ambiguity error for (Euclidean,Polynomial).
     */
    public /*<M implements Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, Function/*<M,M>*/ mod) {
	return quotient((Arithmetic)a, mod);
    }
    /**
     * (traps type unification error).
     * <p>
     * <small>This method only helps resolving the argument type ambiguity
     * for polynomials. This type ambiguity will not occur at all, if
     * templates have been enabled.</small>
     * Even though Polynomials<R,S> extends Function<....,R>, it does not extend
     * Function<A,A> if A is the type of <code>a</code>. Therefore it (usually)
     * is not a quotient operator as expected by {@link #quotient(Arithmetic,Function)}.
     * However, if you have disabled templates, then your compiler will not be able
     * to detect the type unification error occurring in the generic arguments.
     * </p>
     * @throws ClassCastException if a and m are not both instances of Euclidean.
     * @see #quotient(Arithmetic,Function)
     */
    public Quotient quotient(Arithmetic a, Polynomial m) {
	if ((a instanceof Euclidean) && (m instanceof Euclidean))
	    return quotient((Euclidean)a, (Euclidean)m);
	else
	    throw new ClassCastException(m.getClass() + " most probably is no quotient operator.\nConsider using Values.quotient(Polynomial,Set,Comparator) instead.\nIf the instance " + m + " truely is a quotient operator\nand you really know what you are doing, then call Values.quotient(Arithmetic,Function) instead.");
    }

    /**
     * (Convenience) Returns a new quotient a&#772;=[a]&isin;M/(m)
     * of the given value reduced modulo m.
     * <p>
     * This is only a convenience constructor for a special case of
     * M=<b>Z</b>, M/(m)=<b>Z</b>/m<b>Z</b>.
     * Although this case appears rather often, it is by far not the
     * only case of quotients, of course.
     * </p>
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see #quotient(Euclidean,Euclidean)
     */
    public Quotient/*<Integer>*/ quotient(int a, int m) {
	return quotient(valueOf(a), valueOf(m));
    }

    // fraction constructors

    /**
     * Returns a new fraction <span class="Formula">a&#8260;s &isin; S<sup>-1</sup>M = M<sub>S</sub></span>.
     * <p>
     * Note that this implementation does not check whether denominators are in the
     * submonoid S, but only check for them to have the right type S. Since if S really is a
     * monoid, it would suffice to check this at the instantiation, here.
     * However, additionally this implementation does not check the prerequisite of {@link Arithmetic#inverse()}
     * to have a numerator in the submonoid S, but only check for its type, again.
     * </p>
     * <p>
     * The only alternative would require users to provide predicates checking for containement
     * in the submonoid S, all the time.
     * </p>
     * <p>
     * Also note that due to computational aspects, we generally assume the underlying
     * ring to be an integrity domain for equality checking, of course.
     * </p>
     * @todo introduce the second case with explicit checking via a third argument predicate?
     */
    public /*<M implements Arithmetic, S implements Arithmetic>*/ Fraction/*<M,S>*/ fraction(Arithmetic/*>M<*/ a, Arithmetic/*<S>*/ s) {
	return new AbstractFraction/*<M,S>*/(a, s);
    }

    // symbol constructors

    /**
     * Returns a new algebraic symbol.
     * @return the algebraic symbol <var class="signifier">signifier</var>.
     */
    public Symbol symbol(String signifier) {
	return new AbstractSymbol(signifier);
    }

    /**
     * Checks whether an expression is symbolic
     * (does not only contain numeric quantities, but also symbols).
     * @todo if this is good, move to Symbol.
     * @internal Functionals.banana(...)
     * @xxx test with AbstractSymbol$Debug
     */
    static final Predicate symbolic = new Predicate() {
	    public boolean apply(Object expression) {
		if (expression instanceof Functor.Composite) {
		    Functor.Composite c = (Functor.Composite)expression;
		    //@internal see orbital.logic.functor.Notation.asTree(...)
		    Functor    compositor = c.getCompositor();
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

    /**
     * Returns an arithmetic object whose value is equal to that of the
     * representation in the specified string.
     * @param s the string to be parsed.
     * @return an instance of arithmetic that is equal to the representation in s.
     * @throws NumberFormatException if the string does not contain a parsable arithmetic object.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public Arithmetic valueOf(String s) throws NumberFormatException {
	try {
	    return ArithmeticFormat.getDefaultInstance().parse(s);
	}
	catch(ClassCastException x) {throw new NumberFormatException("found " + x.getMessage());}
	catch(ParseException x) {throw new NumberFormatException(x.toString());}
    }

    // conversion methods

    /**
     * Returns a vector view of the specified matrix.
     * The result is a structurally unmodifiable <a href="Tensor.html#view">view</a>.
     * @see #asVector(Tensor)
     */
    public /*<R implements ListIterator,  Arithmetic>*/ Vector/*<R>*/ asVector(final Matrix/*<R>*/ m) {
	return /*refine/delegate Vector*/ new AbstractVector/*<R>*/() {
		private static final long serialVersionUID = 7697252236109892826L;
		protected Vector/*<R>*/ newInstance(int dim) {
		    return m instanceof RMatrix
			? (Vector) new RVector(dim)
			: (Vector) new ArithmeticVector/*<R>*/(dim);
		} 
		public int dimension() {
		    Dimension dim = m.dimension();
		    return dim.height * dim.width;
		}
		/**
		 * Return the corresponding row index of the given vector index.
		 */
		private int rowOf(int i) {
		    return i / m.dimension().width;
		}
		/**
		 * Return the corresponding column index of the given vector index.
		 */
		private int columnOf(int i) {
		    return i % m.dimension().width;
		}
		public Arithmetic/*>R<*/ get(int i) { return m.get(rowOf(i),columnOf(i)); }
		public void set(int i, Arithmetic/*>R<*/ v) {m.set(rowOf(i),columnOf(i),v);}
		protected void set(Arithmetic/*>R<*/ v[]) { throw new UnsupportedOperationException("not currently supported"); }
		public ListIterator iterator() { return m.iterator(); }
		public Object clone() { throw new UnsupportedOperationException("@xxx dunno"); }
		public Vector/*<R>*/ insert(int i, Arithmetic/*>R<*/ b) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
		public Vector/*<R>*/ insert(int i, Vector/*<R>*/ b) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
		public Vector/*<R>*/ append(Arithmetic/*>R<*/ b) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
		public Vector/*<R>*/ append(Vector/*<R>*/ b) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
		public Vector/*<R>*/ remove(int i) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
	    };
    }
    /**
     * Returns a vector view of the specified tensor.
     * The result is a structurally unmodifiable <a href="Tensor.html#view">view</a>.
     * <p>
     * The tensor is interpreted row-wise as a vector.
     * </p>
     */
    public /*<R implements ListIterator,  Arithmetic>*/ Vector/*<R>*/ asVector(final Tensor/*<R>*/ t) {
	if (t instanceof Vector)
	    return (Vector)t;
	else if (t instanceof Matrix)
	    return asVector((Matrix)t);
	else
	    throw new UnsupportedOperationException("not yet implemented");
    }


    /**
     * Returns a minimized Scalar whose value is equal to that of the specified scalar.
     * @return an instance of scalar that is most restrictive.
     * This means that an integer will be returned instead of a real whenever possible
     * and so on.
     * @post RES.equals(val)
     * @todo optimize by avoiding to create intermediate objects, f.ex. convert complex(2+i*0) -> real(2) -> rational(2) -> integer(2) also use OBDD
     */
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
     * @see #getEqualizer()
     * @post RES[0].getClass() == RES[1].getClass() == a.getClass()&cup;b.getClass()
     */
    public Scalar[] minimumEqualized(Number a, Number b) {
	//@xxx adapt better to new Complex>Real>Rational>Integer type hierarchy and conform to a new OBDD (ordered binary decision diagram)
	//@todo partial order with Arithmetic>Scalar>Complex>Real>Rational>Integer and greatest common super type of A,B being A&cup;B = sup {A,B}
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
	} else {	// a is no integer
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

    // arithmetic widening equalizer
	
    /**
     * Get the transformation function for minimum widening equalized Arithmetic objects.
     * This transformation is a logical function that transforms an array of arithmetic objects
     * into an array of minimum widening equalized arithmetic objects whose values are equal to the original ones.
     * <dl class="def">
     *   <dt>minimum widening equalized</dt>
     *   <dd>arithmetic objects are minimum widening equalized if either
     *     <ul class="or">
     *       <li>they have the same type, and this type is the minimum type (the most restrictive one).
     *       So whenever possible an integer will be preferred over a rational,
     *       a rational over a real and that over a complex.
     *       That is they are instances of the common superclass.
     *       </li>
     *       <li>or they have minimum compatible types, such as a matrix and a vector.</li>
     *     </ul>
     *   </dd>
     * </dl>
     * <p>
     * This transformation function is often used to implement sly arithmetic operations with
     * full dynamic dispatch by {@link orbital.math.functional.Operations}.
     * </p>
     * @return a logical transformation function that takes an array of objects (usually Arithmetic objects)
     * and returns an array of the same length (usually 2).
     * The elements returned have the same value as the elements in the argument array.
     * And all will have the same minimum (that is most restrictive) type.
     * This means that an integer will be returned instead of a real whenever possible,
     * a real instead of a complex and so on.
     * But it will always be true that both elements returned have exactly the same
     * or a very compatible type.
     * @pre 0<=args.length && args.length<=2 (currently)
     * @post RES.length==args.length
     *   && (RES[0].getClass() "compatible to" RES[1].getClass() || RES[0].getClass() == RES[1].getClass())
     * @see #minimumEqualized(Number, Number)
     * @see orbital.math.functional.Operations
     * @see #setEqualizer(orbital.logic.functor.Function)
     */
    public final orbital.logic.functor.Function/*<Object[],Object[]>*/ getEqualizer() {
	return equalizer;
    } 

    /**
     * Set the transformation function for minimum widening equalized Arithmetic objects.
     * <p>
     * The transformation function set here must fulfill the same criteria the default one
     * does as described in the getEqualizer() method. To simply hook an additional
     * transformation, implement your transformation function on top of the one got from
     * getEqualizer().</p>
     * @see #getEqualizer()
     */
    public final void setEqualizer(orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer) throws SecurityException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPermission(new RuntimePermission("setStatic.equalizer"));
	} 
	this.equalizer = equalizer;
    } 

    private final Arithmetic[] minimumEqualized(Arithmetic[] a) {
	assert a.length == 2 : "currently for binary operations, only";
	//@todo!
	if (a[0] == null || a[1] == null)
	    throw new NullPointerException("null is no true arithmetic object");
	if (/*(a[0] == null || a[1] == null)
	      ||*/ a[0].getClass() == a[1].getClass())
	    return a;
	else if (a[0] instanceof Number && a[1] instanceof Number)
	    return minimumEqualized((Number) a[0], (Number) a[1]);
	else if ((a[0] instanceof Matrix || a[1] instanceof Matrix)
		 || (a[0] instanceof Vector || a[1] instanceof Vector))
	    return a;
	else if ((a[0] instanceof MathFunctor || a[0] instanceof Symbol) || (a[1] instanceof MathFunctor || a[1] instanceof Symbol))
	    if (a[0] instanceof MathFunctor || a[0] instanceof Symbol)
		return a;
	    else
		return new Arithmetic[] {
		    makeSymbolAware(a[0]), a[1]
		};	//XXX: how exactly?
	throw new AssertionError("the types of the arguments could not be equalized: " + (a == null ? "null" : a[0].getClass() + "") + " and " + (a[1] == null ? "null" : a[1].getClass() + ""));
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
     * @invariant 0 < MAX_CONSTANT < Integer.MAX_VALUE
     * @xxx note that we should think about the order of static initialization.
     *  If Integer.ZERO uses Values.ZERO, then we must assure class Values is initialized first.
     */
    private static final int	 MAX_CONSTANT = 10;
    private static final Integer posConst[] = new Integer[MAX_CONSTANT + 1];
    private static final Integer negConst[] = new Integer[MAX_CONSTANT + 1];
    static {
	posConst[0] = negConst[0] = new AbstractInteger.Long(0);
	for (int i = 1; i <= MAX_CONSTANT; i++) {
	    posConst[i] = new AbstractInteger.Long(i);
	    negConst[i] = new AbstractInteger.Long(-i);
	} 
    } 

    /**
     * 0&isin;<b>Z</b>.
     * The neutral element of addition in <b>Z</b>,<b>R</b> etc.
     */
    public static final Integer ZERO = posConst[0];

    /**
     * 1&isin;<b>Z</b>.
     * The neutral element of multiplication in <b>Z</b>,<b>R</b> etc.
     */
    public static final Integer ONE = posConst[1];

    /**
     * -1&isin;<b>Z</b>.
     */
    public static final Integer MINUS_ONE = negConst[1];

    /**
     * +&infin;.
     * @see #INFINITY
     * @see #NEGATIVE_INFINITY
     */
    public static final Real POSITIVE_INFINITY = getDefaultInstance().valueOf(java.lang.Double.POSITIVE_INFINITY);

    /**
     * -&infin;.
     * @see #INFINITY
     * @see #POSITIVE_INFINITY
     */
    public static final Real NEGATIVE_INFINITY = getDefaultInstance().valueOf(java.lang.Double.NEGATIVE_INFINITY);

    /**
     * &pi;.
     * The proportion of the circumference of a circle to its diameter. 
     */
    public static final Real PI = getDefaultInstance().valueOf(Math.PI);
    /**
     * <b>e</b>.
     * The base of the natural logarithm.
     */
    public static final Real E = getDefaultInstance().valueOf(Math.E);

    /**
     * not a number &perp;&isin;<b>R</b>&cup;{&perp;}.
     */
    public static final Real NaN = getDefaultInstance().valueOf(java.lang.Double.NaN);

    /**
     * The imaginary unit <b>i</b>&isin;<b>C</b>.
     * @see #i
     */
    public static final Complex I = getDefaultInstance().complex(0, 1);
    /**
     * The imaginary unit <b>i</b>&isin;<b>C</b>.
     * @see #I
     */
    public static final Complex i = I;

    /**
     * complex infinity &infin;&isin;<b>C</b>.
     * @see #INFINITY
     */
    public static final Complex INFINITY = getDefaultInstance().complex(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.NaN);
}
