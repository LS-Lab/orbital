/**
 * @(#)Values.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Function;

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
 * @version 1.0, 2000/08/08
 * @author  Andr&eacute; Platzer
 * @see ValueFactory
 * @todo somehow dynamically load services like sun.misc.Service does. But that doesn't improve things except if those providers somehow tell what they can do for us. So this is not a very important todo.
 */
public abstract class Values implements ValueFactory {
    private orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer = null;

    // instantiation

    protected Values() {}

    /**
     * Create a new value factory where the sub class already sets the equalizer.
     * @see #setEqualizer(orbital.logic.functor.Function)
     */
    protected Values(orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer) {
	this.equalizer = equalizer;
    }

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
	//@internal notice the explicit mention of the ValuesImpl class (outside a string) to ease the job of packagers and deployment wizards.
	return instantiate(GetPropertyAction.getProperty(Values.class.getName() + ".implementation",
							 orbital.moon.math.ValuesImpl.class.getName()));
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
	    : getInstance();
    }
	
    /**
     * Get the (single) default instance of this factory.
     * <p>
     * This is the old name for {@link #getDefault()}.
     * </p>
     * @see <a href="{@docRoot}/Patterns/Design/Singleton.html">&quot;Singleton&quot;</a>
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @see #setDefault(Values)
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
     * @see #setDefault(Values)
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
	if (Integer.hasType.apply(val))
	    return new java.lang.Integer(((Integer)val).intValue());
	else if (Real.hasType.apply(val))
	    return new java.lang.Double(((Real)val).doubleValue());
	else if (Rational.hasType.apply(val))
	    return new java.lang.Double(((Real)val).doubleValue());
	else
	    throw new IllegalArgumentException("cannot be wrapped in a primitive wrapper type " + val.getClass());
    }
}
