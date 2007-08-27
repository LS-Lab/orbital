/**
 * @(#)Values.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Function;
import java.util.Map;

// not a strict import. @todo could get rid of this, if we introduce somewhere (not directly in ValueFactory) a way of passing the parameters map.
import orbital.moon.math.AbstractValues;
import orbital.moon.GetPropertyAction;

/**
 * Manager for scalar value and arithmetic object value constructor factories.
 * Manages the current {@link ValueFactory value factories} of your system.
 * <p>
 * Simple applications will generally use {@link #getDefault()} to get the current
 * system default value factory. While applications that require more control over
 * the particular arithmetic object implementations will {@link #getInstance() get their own instance}
 * and configure it according to their needs.
 * </p>
 * <p>
 * <table id="SystemProperties" border="2">
 *   <caption>Properties: math implementation settings</caption>
 *   <tr>
 *     <th>Property Name</th>
 *     <th>Property Value</th>
 *   </tr>
 *   <tr>
 *     <td><tt>orbital.math.Values.implementation</tt></td>
 *     <td>class name of the ValuesFactory implementation used for creating values and thus returned by {@link Values#getInstance()}.</td>
 *   </tr>
 *   <tr>
 *     <td><tt>orbital.math.Values.default</tt></td>
 *     <td>class name of the initial default ValueFactory instance returned by {@link Values#getDefault()}.
 *       If not set defaults to value of <tt>orbital.math.Values.implementation</tt>.
 *     </td>
 *   </tr>
 * </table>
 * These properties allow a different vendor's factory implementation of arithmetic objects
 * to be "plugged in".
 * </p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see ValueFactory
 * @todo somehow dynamically load services like sun.misc.Service does. But that doesn't improve things except if those providers somehow tell what they can do for us. So this is not a very important todo.
 */
public abstract class Values implements ValueFactory {
    // instantiation

    protected Values() {}

    /**
     * Returns a new value factory with default settings.
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @see #getInstance(Map)
     */
    public static ValueFactory getInstance() {
        //@internal notice the explicit mention of the ValuesImpl class (outside a string) to ease the job of packagers and deployment wizards.
        return instantiate(GetPropertyAction.getProperty(Values.class.getName() + ".implementation",
                                                         orbital.moon.math.ValuesImpl.class.getName()));
    }

    /**
     * Returns a new value factory with specified settings.
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @param parameters a map containing settings for string
     * parameters configuring the behaviour of the value factory.
     * These settings configure the value factory implementation
     * instantiated. The precise effect of parameters are therefore
     * implementation-dependent. Some parameters, however, have a
     * standard semantics.
     * Implementations do not need to provide all combinations of parameters, though.
     * <table id="ParameterProperties" border="2">
     *   <caption>Parameters: value factory implementation standard settings</caption>
     *   <tr>
     *     <th>Parameter Name</th>
     *     <th>Parameter Value</th>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Vector.sparse</tt></td>
     *     <td>Whether vectors use a sparse (=store only non-zero components with location)
     *       or dense (=store all components without locations) representation for their components.</td>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Matrix.sparse</tt></td>
     *     <td>Whether matrices use a sparse (=store only non-zero components with location)
     *       or dense (=store all components without locations) representation for their components.</td>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Tensor.sparse</tt></td>
     *     <td>Whether tensors use a sparse (=store only non-zero components with location)
     *       or dense (=store all components without locations) representation for their components.</td>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Polynomial.sparse</tt></td>
     *     <td>Whether polynomials use a sparse (=store only non-zero coefficients with exponents)
     *       or dense (=store all coefficients without exponents) representation for their coefficients.</td>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Polynomial.recursive</tt></td>
     *     <td>Whether multivariate polynomials use a recursive
     *       (f.ex. (y<sup>2</sup>+2y+3)x<sup>2</sup> + (2y<sup>2</sup>-1)x + (-2y+1)1 as R[X<sub>0</sub>][X<sub>1</sub>]...[X<sub>n-1</sub>])
     *       or distributive
     *       (f.ex. x<sup>2</sup>y<sup>2</sup>+2x<sup>2</sup>y+2xy<sup>2</sup>+3x<sup>2</sup>-x-2y+1 as R<sup>(N<sup>(I)</sup>)</sup>) representation.</td>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.UnivariatePolynomial.sparse</tt></td>
     *     <td>Whether univariate polynomials use a sparse (=store only non-zero coefficients with exponents)
     *       or dense (=store all coefficients without exponents) representation for their coefficients.</td>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Fraction.normalize</tt></td>
     *     <td>Use <code>"lazy"</code> for lazy normalization only when representative or query is invoked,
     *       <code>"eager"</code> for eager normalization after each operation.</td>
     *   <!-- @internal lazy normalization prohibits concurrent access, since when
     *    thread P1 calls p = x.numerator()
     *    then thread P2 invokes an operation performing transparent normalization and cancelling of x
     *    and then thread P1 calls q = x.denominator() again,
     *    then p and q will come from inconsistent states.
     *   -->
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Quotient.normalize</tt></td>
     *     <td>Use <code>"lazy"</code> for lazy normalization only when representative or query is invoked,
     *       <code>"eager"</code> for eager normalization after each operation.</td>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Scalar.narrow</tt></td>
     *     <td>Use <code>"auto"</code> for automatic narrowing after each operation,
     *       <code>"none"</code> for {@link ValueFactory#narrow(Scalar) explicit normalization}, only.</td>
     *   </tr>
     *   <tr>
     *     <td><tt>orbital.math.Scalar.precision</tt></td>
     *     <td><ul>
     *       <li><code>"big"</code> for sticking to arbitrary big precision.</li>
     *       <li><code>"auto"</code> for automatic fallback to bigger precision when result could otherwise overflow</li>
     *       <li><code>"dynamic"</code> sticks to machine-size for initially machine-sized numbers at the risk of overflows and uses big precision during big precision computations.</li>
     *       <li><code>"machine"</code> always sticks to machine-size at the risk of overflows regardless of their source (even bigs are reduced to machine size).</li>
     *     </ul></td>
     *   </tr>
     * </table>
     */
    public static ValueFactory getInstance(Map/*<String,Object>*/ parameters) {
        ValueFactory factory = getInstance();
        if (factory instanceof AbstractValues) {
            return ((AbstractValues)factory).adjustToParameters(parameters);
        } else if (!parameters.isEmpty()) {
            throw new UnsupportedOperationException("Passing parameters to general " + ValueFactory.class + " is not yet supported. Use Values.getInstance() for ignoring the parameter settings, or stick to an implementation extending " + AbstractValues.class);
        }
        return factory;
    }

    /**
     * @internal see javax.xml.parser.FactoryFinder#findClassLoader() for a version that - supposedly - runs under every JVM.
     */
    private static final Values instantiate(String className) {
        ClassLoader cl = Values.class.getClassLoader();
        if (cl == null)
            cl = ClassLoader.getSystemClassLoader();

        try {
            return (Values) Class.forName(className, true, cl).newInstance();
        } catch (ClassNotFoundException ex) {
            try {
                cl = Thread.currentThread().getContextClassLoader();
                if (cl == null)
                    cl = ClassLoader.getSystemClassLoader();
                return (Values) Class.forName(className, true, cl).newInstance();
            } catch (Exception ex_again) {
                throw new FactoryConfigurationError("can't instantiate Values implementation " + className, ex_again);
            }
        } catch (Exception ex) {
            throw new FactoryConfigurationError("can't instantiate Values implementation " + className, ex);
        }
    }
    
    /**
     * Default instance.
     */
    private static Values defaultValueFactory;
    static {
        String defaultValueFactoryClass =
            GetPropertyAction.getProperty(Values.class.getName() + ".default", null);
        defaultValueFactory = defaultValueFactoryClass != null
            ? instantiate(defaultValueFactoryClass)
            : (Values) getInstance();
    }
        
    /**
     * Get the (single) default instance of this factory.
     * <p>
     * This is the old name for {@link #getDefault()}.
     * </p>
     * @see <a href="{@docRoot}/Patterns/Design/Singleton.html">&quot;Singleton&quot;</a>
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @see #setDefault(ValueFactory)
     */
    public static final Values getDefaultInstance() {
        return defaultValueFactory;
    }

    /**
     * Get the (single) default value factory instance.
     * <p>
     * This is the new name for {@link #getDefaultInstance()}.
     * </p>
     * @see <a href="{@docRoot}/Patterns/Design/Singleton.html">&quot;Singleton&quot;</a>
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @see #setDefault(ValueFactory)
     */
    public static final ValueFactory getDefault() {
        return defaultValueFactory;
    }

    /**
     * Set the (single) default instance of this factory.
     * <p>
     * Since changing the default factory may affect many different areas of functionality,
     * this method should only be used if the caller is prepared to reinitialize values
     * which ought to use the new kind of factory.
     * </p>
     * @param newValueFactory the new default value factory of this Virtual Machine.
     * @see #getDefault()
     * @permission orbital.math.Values.default write
     */
    public static final void setDefaultInstance(Values newValueFactory) {
        if (newValueFactory == null)
            throw new NullPointerException("Can't set default value factory to " + newValueFactory);
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) sm.checkPermission(new java.util.PropertyPermission
                        ("orbital.math.Values.default", "write"));
        defaultValueFactory = newValueFactory;
    }

    /**
     * Set the (single) default value factory instance.
     * <p>
     * This is the new name for {@link #setDefaultInstance(Values)}.
     * </p>
     * @see #getDefault()
     */
    public static final void setDefault(ValueFactory newValueFactory) {
        setDefaultInstance((Values)newValueFactory);
    }


    // Constants

    /**
     * 0&isin;<b>Z</b>.
     * The neutral element of addition in <b>Z</b>,<b>R</b> etc.
     */
    public static final Integer ZERO = Values.getDefault().ZERO();

    /**
     * 1&isin;<b>Z</b>.
     * The neutral element of multiplication in <b>Z</b>,<b>R</b> etc.
     */
    public static final Integer ONE = Values.getDefault().ONE();

    /**
     * -1&isin;<b>Z</b>.
     */
    public static final Integer MINUS_ONE = Values.getDefault().MINUS_ONE();

    /**
     * +&infin;.
     * @see #INFINITY
     * @see #NEGATIVE_INFINITY
     */
    public static final Real POSITIVE_INFINITY = Values.getDefault().POSITIVE_INFINITY();

    /**
     * -&infin;.
     * @see #INFINITY
     * @see #POSITIVE_INFINITY
     */
    public static final Real NEGATIVE_INFINITY = Values.getDefault().NEGATIVE_INFINITY();

    /**
     * &pi; = 3.14159265... .
     * The proportion of the circumference of a circle to its diameter. 
     */
    public static final Real PI = Values.getDefault().PI();
    /**
     * <b>e</b> = 2.71828... .
     * The base of the natural logarithm.
     */
    public static final Real E = Values.getDefault().E();

    /**
     * not a number &perp;&isin;<b>R</b>&cup;{&perp;}.
     */
    public static final Real NaN = Values.getDefault().NaN();

    /**
     * The imaginary unit <b>i</b>&isin;<b>C</b>.
     * @see #i
     */
    public static final Complex I = Values.getDefault().I();
    /**
     * The imaginary unit <b>i</b>&isin;<b>C</b>.
     * @see #I
     */
    public static final Complex i = I;

    /**
     * complex infinity &infin;&isin;<b>C</b>.
     * @see #INFINITY
     * @todo only in compactification of C.
     */
    public static final Complex INFINITY = Values.getDefault().INFINITY();


    // legacy conversion primitive wrapper utilities
    
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
        if (Integer.hasType.apply(val)) {
	    //@see duplicate code from ArithmeticValuesImpl.intValueExact()
	    // convert to int and check for equality
	    int l = ((Integer)val).intValue();
	    Integer i = Values.getDefault().valueOf(l);
	    if (i.equals(val))
		return new java.lang.Integer(l);
	    else
		//@xxx possible loss of precision for big arbitrary precision
		return new java.lang.Long(((Integer)val).longValue());
        } else if (Real.hasType.apply(val))
            return new java.lang.Double(((Real)val).doubleValue());
        else if (Rational.hasType.apply(val))
            return new java.lang.Double(((Real)val).doubleValue());
        else
            throw new IllegalArgumentException("cannot be wrapped in a primitive wrapper type " + val.getClass());
    }
}
