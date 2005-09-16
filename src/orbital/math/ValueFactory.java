/**
 * @(#)ValueFactory.java 1.1 2002-12-06 Andre Platzer
 * 
 * Copyright (c) 2000-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.awt.Dimension;
import java.text.ParseException;
import orbital.logic.functor.Function;

/**
 * Scalar value and arithmetic object value constructor factory.
 * <p>
 * This class is the central factory facade for instantiating new arithmetic objects. It also
 * provides "pluggable value factory implementation" that allow other vendor's implementation
 * of arithmetic objects to be used.
 * </p>
 * <p>
 * Since our general arithmetic objects are modelled as interfaces to provide a maximum of
 * flexibility, you need factory methods to create an arithmetic object value. The class
 * <tt><a href="ValueFactory.html">ValueFactory</a></tt> is that central factory class which can
 * create arithmetic object values from all kinds of primitive types.
 * This indirection introduces a more loosely coupled binding between users and providers
 * of arithmetic object classes.
 * When using a static final singleton factory like from {@link Values#getDefault()},
 * closed-world JIT compilers can optimize the resulting code to prevent performance loss.
 * Indeed, because of this delegative construction the
 * factory method can chose the best implementation class suitable for a specific primitive type
 * and size.
 * </p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see <a href="{@docRoot}/Patterns/Design/AbstractFactory.html">Abstract Factory</a>
 * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade</a>
 * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
 * @see Values
 * @see Values#getInstance()
 * @see Values#getDefault()
 * @see java.util.Arrays
 * @todo perhaps we should only call true primitive and java.lang.Number type conversion methods valueOf(...). rename the rest of them according to the type they return. 
 */
public interface ValueFactory {
    // Constants

    /**
     * 0&isin;<b>Z</b>.
     * The neutral element of addition in <b>Z</b>,<b>R</b> etc.
     * @postconditions RES == OLD(RES) &and; RES = valueOf(0)
     */
    Integer ZERO();

    /**
     * 1&isin;<b>Z</b>.
     * The neutral element of multiplication in <b>Z</b>,<b>R</b> etc.
     * @postconditions RES == OLD(RES) &and; RES = valueOf(1)
     */
    Integer ONE();

    /**
     * -1&isin;<b>Z</b>.
     * @postconditions RES == OLD(RES) &and; RES = valueOf(-1)
     */
    Integer MINUS_ONE();

    /**
     * +&infin;&isin;<b>R</b>.
     * @see #INFINITY
     * @see #NEGATIVE_INFINITY
     * @postconditions RES == OLD(RES)
     */
    Real POSITIVE_INFINITY();

    /**
     * -&infin;&isin;<b>R</b>.
     * @see #INFINITY
     * @see #POSITIVE_INFINITY
     * @postconditions RES == OLD(RES)
     */
    Real NEGATIVE_INFINITY();

    /**
     * &pi; = 3.14159265... .
     * The proportion of the circumference of a circle to its diameter.
     * &pi; is transcendental over <b>Q</b>.
     * @postconditions RES == OLD(RES)
     */
    Real PI();
    /**
     * <b>e</b> = 2.71828... .
     * The base of the natural logarithm.
     * <b>e</b> is transcendental over <b>Q</b>.
     * @postconditions RES == OLD(RES)
     */
    Real E();

    /**
     * not a number &perp;&isin;<b>R</b>&cup;{&perp;}.
     * @postconditions RES == OLD(RES)
     */
    Real NaN();

    /**
     * The imaginary unit <b>i</b>&isin;<b>C</b>.
     * Alternative name for {@link #i()}.
     * @see #i
     * @postconditions RES == OLD(RES) &and; RES = complex(0, 1)
     */
    Complex I();
    /**
     * The imaginary unit <b>i</b>&isin;<b>C</b>.
     * Alternative name for {@link #I()}.
     * @see #I
     * @postconditions RES == OLD(RES) &and; RES = complex(0, 1)
     */
    Complex i();

    /**
     * complex infinity &infin;&isin;<b>C</b>.
     * @see #INFINITY
     * @todo only in compactification of C.
     * @postconditions RES == OLD(RES)
     */
    Complex INFINITY();

    // scalar value constructors - facade factory
    // primitive type conversion methods

    // integer scalar value constructors - facade factory

    /**
     * Returns a scalar whose value is equal to that of the specified primitive type.
     * @postconditions RES.intValue() == val
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see java.math.BigInteger#valueOf(long)
     */
    Integer valueOf(int val);
    Integer valueOf(java.lang.Integer val);
    /**
     * Returns a scalar whose value is equal to that of the specified primitive type.
     * @postconditions RES.longValue() == val
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see java.math.BigInteger#valueOf(long)
     */
    Integer valueOf(long val);
    Integer valueOf(java.lang.Long val);
    Integer valueOf(byte val);
    Integer valueOf(java.lang.Byte val);
    Integer valueOf(short val);
    Integer valueOf(java.lang.Short val);
    /**
     * Returns a scalar whose value is equal to that of the specified big number.
     * Legacy conversion method.
     * @todo care about non-associative precisions.
     */
    Integer valueOf(java.math.BigInteger val);

    // real scalar value constructors - facade factory

    /**
     * Returns a scalar whose value is equal to that of the specified primitive type.
     * @postconditions RES.doubleValue() == val
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see java.math.BigInteger#valueOf(long)
     */
    Real valueOf(double val);
    Real valueOf(java.lang.Double val);
    /**
     * Returns a scalar whose value is equal to that of the specified primitive type.
     * @postconditions RES.floatValue() == val
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see java.math.BigInteger#valueOf(long)
     */
    Real valueOf(float val);
    Real valueOf(java.lang.Float val);
    /**
     * Returns a scalar whose value is equal to that of the specified big number.
     * Legacy conversion method.
     */
    Real valueOf(java.math.BigDecimal val);

    // scalar value constructors - facade factory

    /**
     * Returns a Scalar whose value is equal to that of the specified number.
     * Legacy conversion method.
     * @return an instance of scalar that has the same value as the number.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     * @see #valueOf(double)
     */
    Scalar valueOf(Number val);

        
    // "named" scalar value constructors

    /**
     * Returns a new rational whose value is equal to p/q.
     * @param p the numerator of p/q.
     * @param q the denominator p/q.
     */
    Rational rational(Integer p, Integer q);
    Rational rational(int p, int q);
    /**
     * Returns a new (integer) rational whose value is equal to p/1.
     * @param p the numerator of p/1.
     */
    Rational rational(Integer p);
    Rational rational(int p);

    // complex scalar values constructors

    /**
     * Returns a new complex whose value is equal to a + <b>i</b>*b.
     * @param a real part.
     * @param b imaginary part.
     * @return a + <b>i</b>*b.
     * @see #cartesian(Real, Real)
     */
    Complex complex(Real a, Real b);
    Complex complex(double a, double b);
    Complex complex(float a, float b);
    Complex complex(int a, int b);
    Complex complex(long a, long b);

    /**
     * Returns a new (real) complex whose value is equal to a + <b>i</b>*0.
     * @param a real part.
     * @return a + <b>i</b>*0.
     * @see #complex(Real, Real)
     */
    Complex complex(Real a);
    Complex complex(double a);

    /**
     * Creates a new complex from cartesian coordinates.
     * @param a real part.
     * @param b imaginary part.
     * @return a + <b>i</b>*b.
     * @see #polar(Real, Real)
     */
    public abstract Complex cartesian(Real a, Real b);
    public abstract Complex cartesian(double a, double b);

    /**
     * Creates a new complex from polar coordinates with r*<b>e</b><sup><b>i</b>&phi;</sup>.
     * @param r = |z| is the length.
     * @param phi = &phi; is the angle &ang; in radians.
     * @preconditions r&ge;0
     * @return r*<b>e</b><sup><b>i</b>&phi;</sup> = r * (cos &phi; + <b>i</b> sin &phi;).
     * @see #cartesian(Real, Real)
     */
    public abstract Complex polar(Real r, Real phi);
    public abstract Complex polar(double r, double phi);


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
    public abstract /*<R extends Arithmetic>*/ Vector/*<R>*/ valueOf(Arithmetic/*>R<*/[] values);
    //@todo couldn't we even return Vector<Real>?
    public abstract Vector valueOf(double[] values);
    public abstract Vector/*<Integer>*/ valueOf(int[] values);

    /**
     * Creates a new instance of vector with the specified dimension.
     * @param dimension the dimension of the vector.
     * @return a vector of the specified dimension, with undefined components.
     * @postconditions RES.dimension() == dimension()
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public abstract /*<R extends Arithmetic>*/ Vector/*<R>*/ newInstance(int dimension);

    /**
     * Gets zero Vector, with all elements set to <code>0</code>.
     * @internal could also call ZERO(int[]), but the CONST implementation may be faster.
     */
    Vector ZERO(int n);

    /**
     * Gets a vector <span class="vector">e<sub>i</sub></span> of an ONB of <b>R</b><sup>n</sup>.
     * It has all elements set to <code>0</code> except element <code>i</code> set to <code>1</code>.
     * These <span class="vector">e<sub>i</sub></span> are the standard base of <b>R</b><sup>n</sup>:
     * &forall;<span class="vector">x</span>&isin;<b>R</b><sup>n</sup> &exist;! x<sub>k</sub>&isin;<b>R</b>: <span class="vector">x</span> = x<sub>1</sub>*<span class="vector">e<sub>1</sub></span> + ... + x<sub>n</sub>*<span class="vector">e<sub>n</sub></span>.
     */
    public abstract /*<R extends Scalar>*/ Vector/*<R>*/ BASE(int n, int i);

    /**
     * Gets a constant Vector, with all elements set to <code>c</code>.
     */
    public abstract /*<R extends Arithmetic>*/ Vector/*<R>*/ CONST(int n, Arithmetic/*>R<*/ c);


    /**
     * Returns an unmodifiable view of the specified vector.
     * The result is a <a href="ValueFactory.html#readOnlyView">read only view</a>.
     */
    /*<R extends Arithmetic>*/ Vector/*<R>*/ constant(Vector/*<R>*/ v);

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
    public abstract /*<R extends Arithmetic>*/ Matrix/*<R>*/ valueOf(Arithmetic/*>R<*/[][] values);
    public abstract Matrix valueOf(double[][] values);
    public abstract Matrix/*<Integer>*/ valueOf(int[][] values);

    /**
     * Creates a new instance of matrix with the specified dimension.
     * @param dimension the dimension of the matrix.
     * @return a matrix of the specified dimensions, with undefined components.
     * @postconditions RES.dimension().equals(dimension)
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public abstract /*<R extends Arithmetic>*/ Matrix/*<R>*/ newInstance(Dimension dimension);
    public abstract /*<R extends Arithmetic>*/ Matrix/*<R>*/ newInstance(int height, int width);

    /**
     * Gets zero Matrix, with all elements set to 0.
     */
    /*<R extends Arithmetic>*/ Matrix/*<R>*/ ZERO(Dimension dim);
    /*<R extends Arithmetic>*/ Matrix/*<R>*/ ZERO(int height, int width);

    /**
     * Gets the identity Matrix, with all elements set to 0, except the leading diagonal m<sub>i,i</sub> set to 1.
     * @preconditions dim.width == dim.height
     */
    /*<R extends Arithmetic>*/ Matrix/*<R>*/ IDENTITY(Dimension dim);
    /**
     * Gets the identity Matrix, with all elements set to 0, except the leading diagonal m<sub>i,i</sub> set to 1.
     * @preconditions width == height
     * @see orbital.math.functional.Functions#delta
     * @see #IDENTITY(Dimension)
     */
    /*<R extends Scalar>*/ Matrix/*<R>*/ IDENTITY(int height, int width);

    /**
     * Gets diagonal Matrix, with all elements set to 0, except the leading diagonal m<sub>i,i</sub> set to v<sub>i</sub>.
     * @see orbital.math.functional.Functions#delta
     * @todo turn into a true view?
     */
    /*<R extends Scalar>*/ Matrix/*<R>*/ DIAGONAL(Vector/*<R>*/ diagon);

    /**
     * Returns an unmodifiable view of the specified matrix.
     * The result is a <a href="ValueFactory.html#readOnlyView">read only view</a>.
     */
    /*<R extends Arithmetic>*/ Matrix/*<R>*/ constant(Matrix/*<R>*/ m);
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
    /*<R extends Arithmetic>*/ Vector/*<R>*/ tensor(Arithmetic/*>R<*/[] values);

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
    /*<R extends Arithmetic>*/ Matrix/*<R>*/ tensor(Arithmetic/*>R<*/[][] values);
    /*<R extends Arithmetic>*/ Tensor/*<R>*/ tensor(Arithmetic/*>R<*/[][][] values);
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
     * @preconditions values is a rectangular multi-dimensional array of {@link Arithmetic arithmetic objects}
     *  or of primitive types
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    Tensor tensor(Object values);
    
    /**
     * Creates a new instance of tensor with the specified dimensions.
     * <p>
     * Tensors of type {@link Vector}, and {@link Matrix} are returned for tensors of rank 1 or 2.
     * </p>
     * @param dimensions the dimensions n<sub>1</sub>&times;n<sub>2</sub>&times;&#8230;&times;n<sub>r</sub>
     *  of the tensor.
     * @return a tensor of the specified dimensions, with undefined components.
     * @postconditions Utilities.equalsAll(RES.dimensions(), dimensions)
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    public abstract Tensor newInstance(int[] dimensions);

    /**
     * Gets zero tensor, with all elements set to 0.
     */
    /*<R extends Arithmetic>*/ Tensor/*<R>*/ ZERO(int[] dimensions);

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
    /*<R extends Arithmetic>*/ Tensor/*<R>*/ constant(Tensor/*<R>*/ t);

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
     * @preconditions coefficients is a rectangular multi-dimensional array of {@link Arithmetic arithmetic objects}
     *  or of primitive types
     * @return the polynomial <var>a</var><sub>0,...,0</sub> + <var>a</var><sub>1,0,...,0</sub>X<sub>1</sub> + <var>a</var><sub>1,1,0,....,0</sub>X<sub>1</sub>X<sub>2</sub> + ... + <var>a</var><sub>2,1,0,....,0</sub>X<sub>1</sub><sup>2</sup>X<sub>2</sub> + ... + <var>a</var><sub>d<sub>1</sub>,...,d<sub>n</sub></sub>X<sub>1</sub><sup>d<sub>1</sub></sup>...&X<sub>n</sub><sup>d<sub>n</sub></sup>.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    /*_<R extends Arithmetic>_*/ Polynomial/*_<R>_*/ polynomial(Object coefficients);

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
    /*<R extends Arithmetic>*/ Polynomial/*<R,Vector<Integer>>*/ asPolynomial(Tensor/*<R>*/ coefficients);
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
    /*<R extends Arithmetic>*/ Tensor/*<R>*/ asTensor(Polynomial/*<R,Vector<Integer>>*/ p);

    /**
     * Returns an unmodifiable view of the specified polynomial.
     * The result is a <a href="ValueFactory.html#readOnlyView">read only view</a>.
     */
    /*<R extends Arithmetic, S extends Arithmetic>*/ Polynomial/*<R,S>*/ constant(Polynomial/*<R,S>*/ p);

    /**
     * The monomial c&middot;X<sup>i</sup>.
     * @param coefficient the coefficient c of the monomial.
     * @param exponent the exponent i of the monomial.
     * @todo implementation could be generalized to non-AbstractMultivariatePolynomials.
     */
    /*<R extends Arithmetic, S extends Arithmetic>*/ Polynomial/*<R,S>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, Arithmetic/*>S<*/ exponent);
    /**
     * The monomial c&middot;X<sub>0</sub><sup>i[0]</sup>...X<sub>n-1</sub><sup>i[n-1]</sup>.
     * @param coefficient the coefficient c of the monomial.
     * @param exponents the exponents i of the monomial.
     *  The number of variables is <code>n:=exponents.length</code>.
     * @internal horribly complicate implementation
     */
    /*<R extends Arithmetic, S extends Arithmetic>*/ Polynomial/*<R,S>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, int[] exponents);
    /**
     * The monomial 1&middot;X<sup>i</sup>.
     * Note that the coefficient is {@link #ONE 1}&isin;<b>Z</b>.
     * @param exponent the exponent i of the monomial.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see #MONOMIAL(Arithmetic,Arithmetic)
     */
    /*<R extends Scalar,S>*/ Polynomial/*<R,S>*/ MONOMIAL(Arithmetic/*>S<*/ exponent);
    /**
     * The monomial 1&middot;X<sub>0</sub><sup>i[0]</sup>...X<sub>n-1</sub><sup>i[n-1]</sup>.
     * Note that the coefficient is {@link #ONE 1}&isin;<b>Z</b>.
     * @param exponents the exponents i of the monomial.
     *  The number of variables is <code>n:=exponents.length</code>.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see #MONOMIAL(Arithmetic,int[])
     */
    /*<R extends Scalar,S>*/ Polynomial/*<R,S>*/ MONOMIAL(int[] exponents);

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
    /*<R extends Arithmetic>*/ UnivariatePolynomial/*<R>*/ polynomial(Arithmetic/*>R<*/[] coefficients);
    /**
     * @see #polynomial(Arithmetic[])
     * @see #polynomial(Object)
     */
    UnivariatePolynomial/*<Real>*/ polynomial(double[] coefficients);
    /**
     * @see #polynomial(Arithmetic[])
     * @see #polynomial(Object)
     */
    UnivariatePolynomial/*<Integer>*/ polynomial(int[] coefficients);

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
    /*<R extends Arithmetic>*/ UnivariatePolynomial/*<R>*/ asPolynomial(Vector/*<R>*/ a);

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
    /*<R extends Arithmetic>*/ Vector/*<R>*/ asVector(UnivariatePolynomial/*<R>*/ p);

    /**
     * Returns an unmodifiable view of the specified polynomial.
     * The result is a <a href="ValueFactory.html#readOnlyView">read only view</a>.
     */
    /*<R extends Arithmetic>*/ UnivariatePolynomial/*<R>*/ constant(UnivariatePolynomial/*<R>*/ p);


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
    /*<M extends Arithmetic>*/ Quotient/*<M>*/ quotient(Arithmetic/*>M<*/ a, Function/*<M,M>*/ mod);
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
    /*<M extends Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, Euclidean/*>M<*/ m);
    /**
     * Returns a new quotient a&#772;=[a]&isin;M/(m) of the given
     * value reduced modulo (m).
     * <p> Will use special remainder classes modulo (m) for a Groebner
     * basis m of (multivariate) polynomial rings. These remainder
     * classes are those induced by the
     * {@link AlgebraicAlgorithms#reduce(java.util.Collection,java.util.Comparator) reduction}
     * operator.</p>
     * @param m the {@link AlgebraicAlgorithms#groebnerBasis(Set,Comparator) Groebner basis}
     *  modulo whose generated ideal (m) to form the quotients.
     * @param monomialOrder the monomial order applied for reducing polynomials.
     * @preconditions m = AlgebraicAlgorithms.groebnerBasis(m,monomialOrder)
     * @postconditions RES = quotient(a, AlgebraicAlgorithms.reduce(m, monomialOrder))
     */
    /*<R extends Arithmetic, S extends Arithmetic>*/ Quotient/*<Polynomial<R,S>>*/ quotient(Polynomial/*<R,S>*/ a, java.util.Set/*<Polynomial<R,S>>*/ m, java.util.Comparator/*<S>*/ monomialOrder);

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
    /*<M extends Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, UnivariatePolynomial m);
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
     * @param a the value
     * @param mod is the quotient operator applied (see {@link Quotient#getQuotientOperator()}).
     * @see #quotient(Arithmetic,Function)
     * @internal only for provoking a compile time type ambiguity error for (Euclidean,Polynomial).
     */
    /*<M extends Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, Function/*<M,M>*/ mod);
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
    Quotient quotient(Arithmetic a, Polynomial m);

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
    Quotient/*<Integer>*/ quotient(int a, int m);

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
    /*<M extends Arithmetic, S extends Arithmetic>*/ Fraction/*<M,S>*/ fraction(Arithmetic/*>M<*/ a, Arithmetic/*>S<*/ s);

    // symbol constructors

    /**
     * Returns a new algebraic symbol.
     * @return the algebraic symbol <var class="signifier">signifier</var>.
     */
    Symbol symbol(String signifier);

    // general static methods for scalar values

    /**
     * Returns an arithmetic object whose value is equal to that of the
     * representation in the specified string.
     * @param s the string to be parsed.
     * @return an instance of arithmetic that is equal to the representation in s.
     * @throws NumberFormatException if the string does not contain a parsable arithmetic object.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade (method)</a>
     */
    Arithmetic valueOf(String s) throws NumberFormatException;

    // conversion methods

    /**
     * Returns a vector view of the specified matrix.
     * The result is a structurally unmodifiable <a href="Tensor.html#view">view</a>.
     * @see #asVector(Tensor)
     */
    /*<R extends Arithmetic>*/ Vector/*<R>*/ asVector(Matrix/*<R>*/ m);
    /**
     * Returns a vector view of the specified tensor.
     * The result is a structurally unmodifiable <a href="Tensor.html#view">view</a>.
     * <p>
     * The tensor is interpreted row-wise as a vector.
     * </p>
     */
    /*<R extends Arithmetic>*/ Vector/*<R>*/ asVector(Tensor/*<R>*/ t);


    /**
     * Returns a minimized Scalar whose value is equal to that of the specified scalar.
     * @return an instance of scalar that is most restrictive.
     * This means that an integer will be returned instead of a real whenever possible
     * and so on.
     * @postconditions RES.equals(val)
     * @todo optimize by avoiding to create intermediate objects, f.ex. convert complex(2+i*0) -> real(2) -> rational(2) -> integer(2) also use OBDD
     */
    Scalar narrow(Scalar val);


    // arithmetic widening coercer
        
    /**
     * Get the transformation function for coercing arithmetic
     * objects.  This transformation is a function that transforms an
     * array of arithmetic objects into an array of coerced arithmetic
     * objects whose values are equal to the original ones.
     * <dl class="def">
     *   <dt>coerce</dt>
     *   <dd>arithmetic objects have been coerced if either
     *     <ul class="or">
     *       <li>they have the same type, and this type is the
     *       {@link orbital.logic.sign.type.TypeSystem#inf() infimum type}
     *       (the most restrictive one).
     *       So whenever possible an integer will be preferred over a rational,
     *       a rational over a real and that over a complex.
     *       That is they are instances of the common superclass.
     *       </li>
     *       <li>or they have otherwise minimum compatible types, such as a matrix and a vector.</li>
     *     </ul>
     *   </dd>
     * </dl>
     * <p>
     * This transformation function is often used to implement sly arithmetic operations with
     * full dynamic dispatch by {@link orbital.math.functional.Operations}.
     * </p>
     * @return a transformation function that takes an array of objects (usually Arithmetic objects)
     * and returns an array of the same length (usually 2).
     * The elements returned have the same value as the elements in the argument array.
     * And all will have the coerced type.
     * This means that an integer will be returned instead of a real whenever possible,
     * a real instead of a complex and so on.
     * But it will always be true that both elements returned have compatible types, or
     * a {@link RuntimeException} will be thrown.
     * @preconditions 0<=RES.args.length && RES.args.length<=2 (currently)
     * @postconditions REs.RES.length==RES.args.length
     *   && (RES.RES[0].getClass() "compatible to" RES.RES[1].getClass() || RES.RES[0].getClass() == RES.RES[1].getClass())
     * @see orbital.math.functional.Operations
     * @see #setCoercer(orbital.logic.functor.Function)
     * @internal dynamic resolution of + like in script languages has two major drawbacks
     *  double memory expenses (since pairs of <typeid,value> are required, recursively)
     *  3-5 fold time (since for a+b we need
     *  <pre>
     *     if (a typeof C) {
     *         if (b typeOf C)...
     *         else if (b typeOf D)...
     *         else if (b typeOf E)...
     *         else error
     *     } else if (a typeof D)....
     *  </pre>
     *  )
     *  Therefore, we need as much of static dispatch preparation at compile-time
     *  as possible.
     */
    Function/*<Object[],Object[]>*/ getCoercer();

    /**
     * Set the transformation function for coercion.
     * <p>
     * The transformation function set here must fulfill the same criteria that the default one
     * does as described in the {@link #getCoercer()} method. To simply hook an additional
     * transformation, implement your transformation function on top of the one got from
     * {@link #getCoercer()}.</p>
     * @see #getCoercer()
     */
    void setCoercer(Function/*<Object[],Object[]>*/ coerce) throws SecurityException;

    // normalizer
    
    /**
     * Get the transformation function for normalizing arithmetic
     * objects. This transformation is a function that transforms an
     * arithmetic object into normalized representation of the same
     * arithmetic object. Whenever possible and feasible, this
     * normalization function will even assure canonical
     * representations.
     * @todo define normalizer and canonicalizer formally
     * @return a transformation function that takes arithmetic objects
     * to arithmetic objects.
     * @postconditions 
     *   && (RES.RES.getClass() "compatible to" RES.arg.getClass())
     * @see #setNormalizer(orbital.logic.functor.Function)
     * @todo may also want to sharpen return value to an interface extending Function by more
     * apply(Integer) apply(Rational) methods. However, note that we need dynamic dispatch
     * in all variables and all methods!
     */
    Function/*<Arithmetic,Arithmetic>*/ getNormalizer();

    /**
     * Set the transformation function for normalizing arithmetic
     * objects. 
     * <p>
     * The transformation function set here must fulfill the same criteria that the default one
     * does as described in the {@link #getNormalizer()} method. To simply hook an additional
     * transformation, implement your transformation function on top of the one got from
     * {@link #getNormalizer()}.</p>
     * @see #getNormalizer()
     */
    void setNormalizer(Function/*<Arithmetic,Arithmetic>*/ normalizer) throws SecurityException;
}
