/**
 * @(#)Utility.java 0.8 1998/05/18 Andre Platzer
 *
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Collection;
import java.util.List;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.lang.reflect.Field;
import orbital.logic.functor.Predicate;
import orbital.logic.functor.BinaryPredicate;
import java.util.Random;
import java.util.BitSet;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import orbital.math.MathUtilities;
import orbital.io.IOUtilities;
import java.text.NumberFormat;

import java.lang.reflect.Array;
import java.util.Arrays;

// only for implicit interface "Iteratable"
import orbital.math.Tensor;

import orbital.math.Values;

/**
 * A general Utility class containing static methods for common tasks like assertion checking etc.
 *
 * @stereotype &laquo;Utilities&raquo;
 * @version 0.9, 1998/05/18
 * @author  Andr&eacute; Platzer
 * @see orbital.math.MathUtilities
 * @see orbital.io.IOUtilities
 * @see orbital.util.Setops
 * @see orbital.awt.UIUtilities
 * @see orbital.moon.logic.ClassicalLogic.Utilities
 * @todo could this class be removed with its methods moved to another (better) class?
 */
public final class Utility {
    private static class Debug {
	private Debug() {}
	public static void main(String arg[]) throws Exception {
	    Object o = new Object() {
		    public int	  i = 12;
		    public float  f = 3.14159f;
		    public String s = "Just an object";
		};
	    forallMembers(o, new Predicate() {
		    public boolean apply(Object f) {
			System.out.println(f);
			return true;
		    }
		});
	}
    }	 // Debug


    /**
     * prevent instantiation - final static class
     */
    private Utility() {}
    
    
    // assertion checking utilites
    
    /**
     * Call to make a precondition assertion.
     * <p>
     * Can be used to check parameter restrictions of a method. It will throw
     * an IllegalArgumentException for invalid arguments.
     * <p><pre>
     * <span class="Orbital">Utility</span>.pre(0<span class="operator">&lt;=</span>x <span class="operator">&&</span> x<span class="operator">&lt;=</span>maximum_size, <span class="String">"numbers in range"</span>);
     * <span class="comment">// can be used instead of</span>
     * <span class="keyword">if</span> (x<span class="operator">&lt;</span>0 <span class="operator">||</span> x<span class="operator">&gt;</span>maximum_size)
     *     <span class="keyword">throw</span> <span class="keyword">new</span> <span class="Class">IllegalArgumentException</span>(<span class="String">"number should not exceed range"</span>);
     * </pre></p>
     * <p>
     * Note that per general contract of argument checking in Java this assertion will be performed
     * even if all other assertion checks are set to be skipped.
     * </p>
     * @param test assertion predicate expression.
     *  If this logical expression evaluates to <code>true</code>, the assertion passes.
     *  If it evaluates to <code>false</code>, the assertion fails.
     * @param description specifies additional information for the assertion that will appear in an exception.
     * @throws IllegalArgumentException if test evaluates to <code>false</code>.
     * @pre test
     * @post test
     */
    public static void pre(boolean test, String description) {
	if (!test)
	    throw new IllegalArgumentException("prerequisite failed: '" + description + "'");
    } 
	
    /**
     * Check whether an array contains an object that is <em>identical</em> to x.
     * @return true iff. &exist;i&isin;{0,..,a.length} a[i] == x.
     */
    public static final boolean containsIdenticalTo(Object[] a, Object x) {
	for (int i = 0; i < a.length; i++)
	    if (a[i] == x)
		return true;
	return false;
    }

    /**
     * Check whether two objects are equal.
     * <p>
     * This method is just a shortcut to simplify assertion statements.</p>
     * @pre a == b &rarr; a.equals(b)
     * @return an optimized version of a <span class="operator">==</span> <span class="keyword">null</span> <span class="operator">?</span> b <span class="operator">==</span> <span class="keyword">null</span> <span class="operator">:</span> a.equals(b).
     *  Namely a <span class="operator">==</span> b <span class="operator">||</span> (a <span class="operator">!=</span> <span class="keyword">null</span> <span class="operator">&&</span> a.equals(b)).
     * @post a == b || (a != null && a.equals(b))
     * @see #hashCode(Object)
     */
    public static final boolean equals(Object a, Object b) {
	return a == b || (a != null && a.equals(b));
    }

    /**
     * Check whether two objects are equal (and their array components, as well).
     * @pre a == b &rarr; a.equals(b)
     * @return whether a and b are equal.
     *  Or if a and b are both arrays, whether they are equal element-wise,
     *  i.e. all their elements are equal.
     * @post ?
     * @see #hashCodeAll(Object)
     */
    public static final boolean equalsAll(Object a, Object b) {
	if (equals(a, b))
	    return true;
	if (a == null)
	    return false;
	if ((a instanceof Object[]) && (b instanceof Object[]))
	    //@todo recursively check with equalsAll to ensure element-wise equality of multi-dimensional arrays, as well?
	    return Arrays.equals((Object[]) a, (Object[]) b);
	else if (a.getClass().isArray() && b.getClass().isArray()) {
	    // additionally(!) check for equality of primitive type arrays with java.lang.reflect.Array
	    int len = Array.getLength(a);
	    if (Array.getLength(b) != len)
		return false;
	    for (int i = 0; i < len; i++)
		if (!equals(Array.get(a, i), Array.get(b, i)))
		    return false;
	    return true;
	} else
	    return false;
    }

    /**
     * Get the hashCode of an object.
     * <p>
     * This method is just a shortcut to simplify assertion statements.</p>
     * @return an optimized version of a <span class="operator">==</span> <span class="keyword">null</span> <span class="operator">?</span> 0 <span class="operator">:</span> a.hashCode().
     * @see #equals(Object, Object)
     */
    public static final int hashCode(Object a) {
	return a == null ? 0 : a.hashCode();
    }

    /**
     * Compares its two arguments for order.
     * Returns a negative integer, zero, or a positive integer as the first argument is
     * less than, equal to, or greater than the second.
     * <p>
     * Compares two objects with <code>null</code> being the smallest object.
     * Contrary to the specified NullPointerException behaviour f {@link Comparable#compareTo(Object)}!
     * </p>
     * @see #equals(Object, Object)
     * @todo document
     */
    public static final int compare(Object a, Object b) {
	return a == null ? (b == null ? 0 : -1) : (b == null ? 1 : ((Comparable) a).compareTo(b));
    }

    /**
     * Get the hashCode of an object (considering its all array components, as well).
     * @param o the object whose hashCode to calculate.
     * @return the hashCode of o, or all its elements if o is a (multi-dimensional) array.
     * @see #equalsAll(Object, Object)
     * @see java.util.Arrays#equals(Object[], Object[])
     * @see orbital.logic.functor.Functionals#foldRight(orbital.logic.functor.BinaryFunction, Object, Object[])
     * @note this implementation does not necessarily to fit the hashCode defined by {@link java.util.Set}, or {@link java.util.List}.
     */
    public static final int hashCodeAll(Object o) {
	//@todo functional?
	if (o instanceof Object[]) {
	    final Object a[] = (Object[]) o;
	    int          hash = 0;
	    for (int i = 0; i < a.length; i++) {
		// recursively hashCodeAll to ensure element-wise hashCodes of (multi-dimensional) arrays, as well?
		final int h = hashCodeAll(a[i]);
		//@internal hash ^= h rotl i
		hash ^= (h << i) | (h >>> (INTEGER_BITS - i));
	    }
	    return hash;
	} else if (o != null && o.getClass().isArray()) {
	    // additionally(!) check for hashCode primitive type arrays with java.lang.reflect.Array
	    final int len = Array.getLength(o);
	    int       hash = 0;
	    for (int i = 0; i < len; i++) {
		// recursively hashCodeAll to ensure element-wise hashCodes of (multi-dimensional) arrays, as well?
		final int h = hashCodeAll(Array.get(o, i));
		//@internal hash ^= h rotl i
		hash ^= (h << i) | (h >>> (INTEGER_BITS - i));
	    }
	    return hash;
	} else
	    return hashCode(o);
    }
    /**
     * the number of bits that an integer has
     */
    private static final int INTEGER_BITS = IOUtilities.INTEGER_SIZE << 3;

    /**
     * Predicate for x instanceof y.
     * @see Class#isInstance(Object)
     */
    public static final BinaryPredicate instanceOf = new BinaryPredicate() {
	    public boolean apply(Object obj, Object clazz) {
		return ((Class)clazz).isInstance(obj);
	    }
	};
	
    /**
     * Checks whether an array is sorted.
     * @param a the array to be checked.
     * @param asc whether to check for ascending order (<code>true</code>),
     *  or descending order (<code>false</code>).
     * @return whether a is sorted.
     */
    public static final boolean sorted(double a[], boolean asc) {
	if (asc) {	// unrolled for performance
	    for (int i = 1; i < a.length; i++)
		if (a[i - 1] > a[i])
		    return false;
	} else {
	    for (int i = 1; i < a.length; i++)
		if (a[i - 1] < a[i])
		    return false;
	}
	return true;
    }

    /**
     * Checks whether a list is sorted.
     * @param a the list to be checked.
     * @param cmp the comparator used (for ascending order).
     *  If <code>null</code>, natural order is used (by using {@link Comparable}).
     * @return whether a is sorted.
     * @see ReverseComparator
     */
    public static final boolean sorted(List a, Comparator cmp) {
	if (cmp == null)
	    cmp = new NaturalComparator();
	if (a.isEmpty())
	    return true;
	Iterator i = a.iterator();
	Object last = i.next();
	while (i.hasNext()) {
	    Object o = i.next();
	    if (cmp.compare(last, o) > 0)
		return false;
	    last = o;
	}
	return true;
    }

    // multi-indices

    /**
     * @see #rank(Object)
     */
    public static final int rank(Object[] a) {
        int r = 1;
	while (a[0] instanceof Object[]) {
	    a = (Object[])a[0];
	    r++;
	}
	return r;
    }
    /**
     * Get the number of indices required to reach the component type.
     * @post RES == dimensions(a).length
     * @note does not check rectangularity of a
     */
    public static final int rank(Object a) {
        int r = 0;
	while (a.getClass().isArray()) {
	    a = Array.get(a, 0);
	    r++;
	}
	return r;
    }
    /**
     * Get the (final) component type of a multi-dimensional array.
     * @note does not check rectangularity of a
     */
    private static final Class getComponentType(Object a) {
	Class component = a.getClass();
	while (a.getClass().isArray()) {
	    component = a.getClass().getComponentType();
	    a = Array.get(a, 0);
	}
	return component;
    }
    /**
     * @see #dimensions(Object)
     */
    public static final int[] dimensions(Object[] a) {
	int[] dim = new int[rank(a)];
	for (int i = 0; i < dim.length - 1; i++) {
	    dim[i] = a.length;
	    assert a[0] instanceof Object[] : "by definition of rank";
	    a = (Object[])a[0];
	}
	dim[dim.length - 1] = a.length;
	return dim;
    }
    /**
     * Get the dimensions of a multi-dimensional array.
     * @note does not check rectangularity of a
     */
    public static final int[] dimensions(Object a) {
	int[] dim = new int[rank(a)];
	for (int i = 0; i < dim.length; i++) {
	    assert a.getClass().isArray() : "by definition of rank";
	    dim[i] = Array.getLength(a);
	    a = Array.get(a, 0);
	}
	assert !a.getClass().isArray() : "by definition of rank";
	return dim;
    }

    /**
     * @see #getPart(Object,int[])
     */
    public static Object getPart(Object[] a, int[] partSpecification) {
	Object o = a;
	for (int i = 0; i < partSpecification.length; i++)
	    o = ((Object[]) o)[partSpecification[i]];
	return o;
    }
    /**
     * Get the element in the (possibly multi-dimensional) array <code>a</code> specified by the part specification.
     * Contrary to {@link #getPart(Object[],int[])}, also accepts primitive type arrays.
     * @param partSpecification the part specification <code>p</code> (multi-index into the multi-dimensional array <code>a</code>).
     * @pre partSpecification.length is not lower than the number of dimensions for partialSolutions.
     * @return a[p[0]][p[1]]...[p[p.length-1]]
     */
    public static Object getPart(Object a, int[] partSpecification) {
	Object o = a;
	for (int i = 0; i < partSpecification.length; i++)
	    o = Array.get(o, partSpecification[i]);
	return o;
    } 

    /**
     * @see #setPart(Object[],int[],Object)
     */
    public static void setPart(Object[] a, int[] partSpecification, Object value) {
	Object[] o = a;
	for (int i = 0; i < partSpecification.length - 1; i++)
	    o = (Object[]) o[partSpecification[i]];
	o[partSpecification[partSpecification.length - 1]] = value;
    } 
    /**
     * Set the element in the (possibly multi-dimensional) array <code>a</code> specified by the part specification.
     * Contrary to {@link #setPart(Object[],int[],Object)}, also accepts primitive type arrays.
     * Sets a[p[0]][p[1]]...[p[p.length-1]] := value.
     * @param partSpecification the part specification <code>p</code> (multi-index into the multi-dimensional array <code>a</code>).
     * @pre partSpecification.length is not lower than the number of dimensions for partialSolutions.
     */
    public static void setPart(Object a, int[] partSpecification, Object value) {
	Object o = a;
	for (int i = 0; i < partSpecification.length - 1; i++)
	    o = Array.get(o, partSpecification[i]);
	Array.set(o, partSpecification[partSpecification.length - 1], value);
    } 

    // diverse

    /**
     * Flips to true with a given probability.
     * Works like flipping coins.
     * @param probability the probabilty ranging from 0 to 1 with that flip returns <code>true</code>.
     * @pre probability is a probability
     * @return <code>true</code> with a given probability (nondeterministic).
     * @see Random#nextDouble()
     */
    public static boolean flip(Random r, double probability) {
	pre(MathUtilities.isProbability(probability), probability + " is a probability");
	return r.nextDouble() <= probability;
    }
	


    /**
     * Utility returning the index of the brace matching the brace
     * at pos in text. f.ex. <b>(</b>a(b)c<b>)</b>
     */
    public static int matchingBrace(String text, int pos) {
	int level = 0;
	for (int i = pos; i < text.length(); i++) {
	    switch (text.charAt(i)) {
	    case '(':
		level++;
		break;
	    case ')':
		level--;
		break;
		//TODO: case '[':
	    default:
		break;
	    }
	    if (level == 0)
		return i;
	}
	return -1;
    }

    /**
     * Applies a Predicate to all public member fields of an object, as long as the predicate returns true.
     * @param o the object whose member public fields are desired.
     * @param pred the predicate to be applied for every public member field.
     * @return whether the predicate was true for all public member fields.
     */
    public static boolean forallPublicMembers(Object o, Predicate pred) throws IllegalAccessException {
	Class   cls = o.getClass();
	Field[] fields = cls.getFields();
	for (int i = 0; i < fields.length; i++) {
	    Object value = fields[i].get(o);
	    if (!pred.apply(value))
		return false;
	}
	return true;
    }

    /**
     * Applies a Predicate to all member fields of an object, as long as the predicate returns true.
     * Will loop over all superclasses declared fields.
     * This method requires a <code>java.lang.reflect.ReflectPermission</code> of <code>suppressAccessChecks</code> to work for all superclasses.
     * @param o the object whose member fields are desired.
     * @param pred the predicate to be applied for every member field.
     * @return whether the predicate was true for all member fields.
     * @throws java.lang.SecurityException if the <code>java.lang.reflect.ReflectPermission</code> of <code>suppressAccessChecks</code> is not permitted.
     */
    public static boolean forallMembers(Object o, Predicate pred) throws IllegalAccessException {
	for (Class cls = o.getClass(); cls != null; cls = cls.getSuperclass()) {
	    Field[] fields = cls.getDeclaredFields();
	    for (int i = 0; i < fields.length; i++) {
		if (!fields[i].isAccessible())
		    fields[i].setAccessible(true);
		Object value = fields[i].get(o);
		if (!pred.apply(value))
		    return false;
	    }
	}
	return true;
    }


    /**
     * Get a collection view of the object o.
     * @return a list of the elements of o, if o is an array,
     *  o, if o is a collection, 
     *  a list containing o, otherwise.
     * @post RES == null <=> o == null
     * @private
     * @todo perhaps move to another location?
     * @see #asIterator(Object)
     */
    public static Collection asCollection(Object o) {
	if (o == null)
	    return null;
	else if (o instanceof Collection)
	    return (Collection) o;
	else if (o instanceof Object[])
	    return java.util.Arrays.asList((Object[]) o);
	/*@todo else if (o.getClass().isArray())
	  throw new UnsupportedOperationException("primitive-type arrays not yet supported");*/
	else
	    //@todo throw exception instead?
	    return java.util.Collections.singletonList(o);
    }
	
    /**
     * Checks whether the given object is <dfn>generalized iteratable</dfn>.
     * <p>
     * Generalized iteratable objects are objects that somehow support iteration of their components,
     * like {@link java.util.Iterator}, {@link java.util.Collection}, {@link java.lang.Object Object[]},
     * or {@link orbital.math.Tensor} (including {@link orbital.math.Vector}, and {@link orbital.math.Matrix}),
     * as well as (even multi-dimensional) primitive type arrays.
     * Multi-dimensional arrays of primitive or structured component type are iterated
     * over component-wise. Although the order will usually be row-wise, this is not a
     * strict requirement.
     * </p>
     * <p>
     * Unfortunately, these classes do not implement a common interface "Iteratable", or
     * "Iterable", or "Enumerable" so we must rely on implicit interfaces.
     * Additionally, some iteratable classes may support {@link java.util.ListIterator}s
     * others {@link java.util.Iterator}s, so covariant return-types would be required for
     * defining such a common interface in a convenient way that avoids casting.
     * </p>
     * @internal Iteratable would have
     *  (List)Iterator iterator();
     *  Object structure(); // supporting equals like AbstractProductArithmetic#productIndexSet(), Multinomial#indexSet()
     *  Iteratable newInstance(Object structure)
     */
    public static boolean isIteratable(Object a) {
	return (a instanceof Iterator) || (a instanceof Collection)
		|| (a instanceof Tensor)
		|| a.getClass().isArray();
    }

    /**
     * Get an iterator view of a generalized iteratable object, if possible.
     * @post RES == null <=> o == null
     * @return an iterator or list iterator view of a, whenever possible.
     * @throws ClassCastException if a is not generalized iteratable.
     * @see #isIteratable(Object)
     * @see #asCollection(Object)
     * @see #newIteratableLike(Object)
     */
    public static /*_<A>_*/ Iterator/*_<A>_*/ asIterator(Object a) {
	if (a == null)
	    return null;
	else if (a instanceof Iterator/*_<A>_*/)
	    return (Iterator/*_<A>_*/) a;
	else if (a instanceof Collection/*_<A>_*/)
	    if (a instanceof List/*_<A>_*/)
		return ((List/*_<A>_*/) a).listIterator();
	    else
		return ((Collection/*_<A>_*/) a).iterator();
	else if (a instanceof Tensor/*_<A>_*/)
	    return ((Tensor/*_<A>_*/) a).iterator();
	else if ((a instanceof Object/*_>A<_*/[]) && !a.getClass().getComponentType().isArray())
	    return Arrays.asList((Object/*_>A<_*/[]) a).listIterator();
	else if (a.getClass().isArray())
	    return Values.tensor(a).iterator();
	throw new ClassCastException(a.getClass().getName() + " expected " + generalizedIteratableTypes);
    }

    /**
     * Get a new instance of generalized iteratable object of the same type as the one specified.
     * @post RES == null <=> a == null
     * @throws ClassCastException if a is not generalized iteratable.
     * @see #isIteratable(Object)
     * @see Setops#newCollectionLike(Collection)
     */
    public static Object newIteratableLike(Object a) {
	if (a == null)
	    return null;
	else if (a instanceof Iterator) {
	    if (a instanceof ListIterator)
		return new LinkedList().listIterator();
	    else
		return new LinkedList().iterator();
	} else if (a instanceof Collection)
	    return Setops.newCollectionLike((Collection) a);
	else if (a instanceof Tensor)
	    //@see AbstractTensor#newInstance
	    return Values.tensor(((Tensor)a).dimensions());
	else if (a.getClass().isArray())
	    return Array.newInstance(getComponentType(a), dimensions(a));
	throw new ClassCastException(a.getClass().getName() + " expected " + generalizedIteratableTypes);
    }
    
    private static final Set generalizedIteratableTypes = new HashSet(Arrays.asList(new Class[] {
	Iterator.class, Collection.class, Object[].class, Tensor.class , orbital.math.Vector.class, orbital.math.Matrix.class,
	Object[][].class, int[].class, double[][].class, double[][].class, Object[][][].class
    }));
}
