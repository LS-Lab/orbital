/**
 * @(#)Types.java 1.1 2002-09-08 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import java.util.Comparator;

import java.io.Serializable;
import orbital.logic.functor.Functor;
import orbital.logic.functor.Predicate;
import orbital.logic.functor.Function;
import orbital.logic.functor.Functionals;
import orbital.util.Utility;

import java.beans.IntrospectionException;
import orbital.util.InnerCheckedException;
import java.lang.reflect.*;

/**
 * Provides implementations and factories for types.
 *
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version 1.1, 2002-09-08
 */
public final class Types {
    /**
     * The universal type <span class="type">&#8868;</span>.
     * It is the top element of the lattice &Tau; of types, has no differentiae and satisfies
     * <ul>
     *   <li>(&exist;x) <span class="type">&#8868;</span>(x)</li>
     *   <li>(&forall;x) <span class="type">&#8868;</span>(x)</li>
     *   <li>(&forall;<span class="type">&tau;</span>:Type) <span class="type">&tau;</span>&le;<span class="type">&#8868;</span></li>
     * </ul>
     * @see #ABSURD
     * @internal everything (besides primitive types) is an instance of or a subclass of our fundamental class.
     * @todo how to distinguish fundamental types UNIVERSAL and INDIVIDUAL on the object level?
     */
    public static final Type UNIVERSAL = new UniversalType();
    private static final class UniversalType extends FundamentalType {
	    private static final long serialVersionUID = -7043311457660579287L;
	    /**
	     * Maintains the guarantee that there is only a single object representing this type.
	     * @serialData canonicalized deserialization
	     */
	    private Object readResolve() throws java.io.ObjectStreamException {
		// canonicalize
		return UNIVERSAL;
	    } 
	    public final boolean equals(Object o) {
		//@internal assume canonical
		return this == o;
	    }
	    public final int hashCode() {
		return System.identityHashCode(this);
	    }

	    public Class getFundamental() {
		return Object.class;
	    }
	    public final boolean subtypeOf(Type b) {
		//@todo verify the converse
		//@internal assume canonical
		return this == b;
	    }
	    public final boolean apply(Object x) {
		return true;
	    }
	    public String toString() {
		return "univ";
	    }
	};
    /**
     * The void type <span class="type">&prod;<sub>i&isin;&empty;</sub></span>.
     * The extension of the type void is void, i.e. it does not have any elements at all.
     */
    public static final Type VOID = new FundamentalTypeImpl(Void.TYPE);
    /**
     * The type <span class="type">&iota;</span> of individuals ({@link java.lang.Object objects}).
     */
    public static final Type INDIVIDUAL = type(Object.class);
    /**
     * The type <span class="type">&omicron;</span>=<span class="type">()</span> of truth-values.
     * @xxx for multi-valued logics this is not limited to boolean.
     * @todo what about Boolean.TYPE and Boolean.class?
     */
    public static final Type TRUTH = new FundamentalTypeImpl(Boolean.class);
    /**
     * The absurd type <span class="type">&perp;</span>.
     * It is the bottom element of the lattice &Tau; of types,
     * it cannot be the type of anything that exists, and it satisfies.
     * <ul>
     *   <li>&not;(&exist;x) <span class="type">&perp;</span>(x)</li>
     *   <li>(&forall;<span class="type">&tau;</span>:Type) <span class="type">&perp;</span>&le;<span class="type">&tau;</span></li>
     * </ul>
     * @see #UNIVERSAL
     * @internal nothing (besides that single object whose reference no one knows) is an instance of or a subclass of our fundamental anonymous class.
     */
    public static final Type ABSURD = new AbsurdType();
    private static final class AbsurdType extends FundamentalType {
	    private static final long serialVersionUID = 7539731602290983194L;
	    private transient Class type = new Object() {}.getClass();
	    /**
	     * Maintains the guarantee that there is only a single object representing this type.
	     * @serialData canonicalized deserialization
	     */
	    private Object readResolve() throws java.io.ObjectStreamException {
		// canonicalize
		return ABSURD;
	    } 

	    public final boolean equals(Object o) {
		//@internal assume canonical
		return this == o;
	    }
	    public final int hashCode() {
		return System.identityHashCode(this);
	    }

	    public Class getFundamental() {
		return type;
	    }
	    public final boolean subtypeOf(Type b) {
		return true;
	    }
	    public final boolean apply(Object x) {
		return false;
	    }
	    public String toString() {
		return "absurd";
	    }
	};

    /**
     * prevent instantiation - module class
     */
    private Types() {}

    // Utility methods

    /**
     * Checks whether the type specification is compatible with the given list of arguments.
     * Convenience method.
     * @pre true
     * @param args the arguments to check for compatibility with this symbol.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @return whether the arguments are assignable to the required parameter types of this symbol.
     *  This especially includes whether the number of arguments matches this symbol's arity.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     * @see Type#subtypeOf(Type)
     */
    public static final boolean isApplicableTo(Type type, Expression[] args) {
	return typeOf(args).subtypeOf(type.codomain());
    }

    // factories
    
    /**
     * The root object for type implementations.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-11
     */
    private static abstract class TypeObject implements Type, Serializable {
	private static final long serialVersionUID = 3881468737970732586L;
	protected TypeObject() {}
    }

    /**
     * Basic implementation of Type.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-08
     */
    private static abstract class TypeBase extends TypeObject {
	private static final long serialVersionUID = -5981946381802191256L;
	protected TypeBase() {}
	public boolean equals(Object o) {
	    if (o instanceof Type) {
		Type b = (Type)o;
		return codomain().equals(b.codomain())
		    && domain().equals(b.domain());
	    } else
		return false;
	}
	
	public int hashCode() {
	    return (codomain().hashCode()<<1) ^ domain().hashCode();
	}

	public String toString() {
	    return "[" + codomain() + "->" + domain() + "]";
	}
	
	public boolean subtypeOf(Type b) {
	    return (b.codomain().subtypeOf(codomain())
		&& domain().subtypeOf(b.domain()))
		|| b.equals(UNIVERSAL);
	}
    }

    /**
     * Get the fundamental type described by a class.
     * Converts a native Java class object to a type.
     * @param type the type <span class="type">&tau;</span> represented as a class object.
     * @return <span class="type">&tau;</span>=<span class="type">void&rarr;&tau;</span>.
     * @todo assure canonical identity?
     * @todo rename
     */
    public static final Type type(Class type) {
	return type.equals(Boolean.class) || type.equals(Boolean.TYPE)
	    ? TRUTH
	    : new FundamentalTypeImpl(type);
    }
    
    /**
     * Base implementation for fundamental types already available at the language level ({@link java.lang.Class}).
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-11
     */
    private static abstract class FundamentalType extends TypeObject {
	private static final long serialVersionUID = 7364608656416736898L;
	/**
	 * Get the class underlying this fundamental type.
	 */
	public abstract Class getFundamental();
	
	public boolean equals(Object o) {
	    return (o instanceof FundamentalType)
		&& getFundamental().equals(((FundamentalType)o).getFundamental());
	}

	public int hashCode() {
	    return getFundamental().hashCode();
	}
	
	public String toString() {
	    return getFundamental().getName().toString();
	}

	public Type codomain() {
	    return VOID;
	}
	
	public Type domain() {
	    return this;
	}
	
	public boolean subtypeOf(Type tau) {
	    return (tau instanceof FundamentalType)
		&& ((FundamentalType)tau).getFundamental().isAssignableFrom(getFundamental());
	}

	public boolean apply(Object x) {
	    //@xxx check that all tests are really correct. What's up with VoidFunction, and Function<Object,Boolean> etc?
	    // if (referent instanceof Functor && spec.isConform((Functor) referent)) return true;
	    return Functionals.bindSecond(Utility.instanceOf, getFundamental()).apply(x);
	}
    }

    /**
     * Implementation of fundamental types already available at the language level ({@link java.lang.Class}).
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-08
     */
    private static class FundamentalTypeImpl extends FundamentalType {
	private static final long serialVersionUID = -5129887328859119221L;
	private final Class type;
	public FundamentalTypeImpl(Class type) {
	    if (type == null)
		throw new NullPointerException("illegal class " + type);
	    this.type = type;
	}
	public Class getFundamental() {
	    return type;
	}
    }
    
    /**
     * Get the map type <span class="type">&sigma;&rarr;&tau;</span>.
     * @param codomain the {@link Type#codomain() codomain} <span class="type">&sigma;</span>.
     * @param domain the {@link Type#domain() domain} <span class="type">&tau;</span>.
     * @todo assure canonical identity?
     */
    public static final Type map(Type codomain, Type domain) {
	if (codomain == ABSURD || domain == ABSURD)
	    // strict
	    return ABSURD;
	return codomain.equals(VOID) ? domain : new MapType(codomain, domain);
    }

    /**
     * Get the predicate type <span class="type">(&sigma;)</span>=<span class="type">&sigma;&rarr;&omicron;</span>.
     * @param codomain the {@link Type#codomain() codomain} <span class="type">&sigma;</span>.
     * @todo assure canonical identity?
     * @todo check that empty predicate type () equals TRUTH.
     */
    public static final Type predicate(Type codomain) {
	if (codomain == ABSURD)
	    // strict
	    return ABSURD;
	return codomain.equals(VOID) ? TRUTH : new MapType(codomain, TRUTH);
    }

    /**
     * Implementation of map types.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-08
     */
    private static final class MapType extends TypeBase {
	private static final long serialVersionUID = 9148024444083534208L;
	private final Type codom;
	private final Type dom;
	public MapType(Type codomain, Type domain) {
	    this.codom = codomain;
	    this.dom = domain;
	}

	public Type codomain() {
	    return codom;
	}
	
	public Type domain() {
	    return dom;
	}

	public boolean apply(Object x) {
	    assert arityOf(codom) > 0 : "map(Type,Type) canonically filters codomain=VOID. But the codomain of " + this + " of " + getClass() + " has arity " + arityOf(codom);
	    //@xxx originally was  referent instanceof Functor && spec.isConform((Functor) referent)
	    if (false && arityOf(codom) <= 1)
		return Functionals.bindSecond(Utility.instanceOf, dom.equals(TRUTH)
					      ? Predicate.class
					      : Function.class).apply(x);
	    else if (x instanceof Functor) {
		try {
		    final Type argType = declaredTypeOf((Functor)x);
		    return argType.subtypeOf(this);
		}
		catch (IntrospectionException ex) {throw new InnerCheckedException("could not detect specification", ex);}
	    } else
		return false;
	}
    }

    /**
     * Get the product type <span class="type">&prod;<sub>i</sub>&tau;<sub>i</sub></span>=<span class="type">&tau;<sub>1</sub>&times;&#8230;&times;&tau;<sub>n</sub></span>.
     * <p>
     * </p>
     * @param components the components <span class="type">&tau;<sub>i</sub></span> of the product type.
     */
    public static final Type product(Type components[]) {
	for (int i = 0; i < components.length; i++) {
	    Type ti = components[i];
	    if (ti == null)
		throw new NullPointerException("illegal components containing " + ti);
	    else if (ti == ABSURD)
		// strict
		return ABSURD;
	}
	switch (components.length) {
	case 0: return VOID;
	case 1: return components[0];
	default: return new ProductType(components);
	}
    }

    /**
     * Implementation of product types.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-08
     * @internal which interface for product types? The answer may be: nothing particular, since the typical operations should rely on subtypeOf and equals.
     */
    private static final class ProductType extends TypeObject {
	private static final long serialVersionUID = -6667362786622508551L;
	private final Type components[];
	public ProductType(Type components[]) {
	    if (components.length == 0)
		throw new IllegalArgumentException();
	    this.components = components;
	}
	public boolean equals(Object o) {
	    return (o instanceof ProductType) && Utility.equalsAll(components, ((ProductType)o).components);
	}
	
	public int hashCode() {
	    return Utility.hashCodeAll(components);
	}
	
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append('[');
	    sb.append(components[0].toString());
	    for (int i = 1; i < components.length; i++) {
		sb.append('*');
		sb.append(components[i]);
	    }
	    sb.append(']');
	    return sb.toString();
	}

	public Type[] getComponent() {
	    return components;
	}

	public Type codomain() {
	    return VOID;
	}
	
	public Type domain() {
	    return this;
	}
	
	public boolean subtypeOf(Type tau) {
	    if (tau instanceof ProductType) {
		Type taui[] = ((ProductType)tau).components;
		if (components.length != taui.length)
		    return false;
		for (int i = 0; i < components.length; i++)
		    if (!components[i].subtypeOf(taui[i]))
			return false;
		return true;
	    } else
		return tau.equals(UNIVERSAL);
	}

	public boolean apply(Object x) {
	    if (x instanceof Object[]) {
		Object xs[] = (Object[])x;
		// component-wise
		if (xs.length != components.length)
		    return false;
		for (int i = 0; i < components.length; i++)
		    if (!components[i].apply(xs[i]))
			return false;
		return true;
	    } else
		return false;
	}
    }

    

    //@xxx decide acccessibility (privatize or package-level protect if possible)
    
    /**
     * Get the number of components n of a product type <span class="type">&prod;<sub>i</sub>&tau;<sub>i</sub></span>=<span class="type">&tau;<sub>1</sub>&times;&#8230;&times;&tau;<sub>n</sub></span>.
     * @todo rename
     */
    static final int arityOf(Type type) {
	return type == ABSURD
	    // strict
	    ? Integer.MIN_VALUE
	    : type.equals(VOID)
	    ? 0
	    : type instanceof ProductType
	    ? ((ProductType)type).components.length
	    : 1;
    }
    

    /**
     * Returns the type of a list of arguments.
     * Convenience method.
     * @pre true
     * @param args the arguments whose (combined) type to return.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     * @see #product(Type[])
     * @see Expression#getType()
     * @todo package protect but share with orbital.moon.logic.
     */
    /*private*/public static final Type typeOf(Expression[] args) {
	if (args == null || args.length == 0)
	    return VOID;
	final Type argumentTypes[] = new Type[args.length];
	for (int i = 0; i < argumentTypes.length; i++)
	    argumentTypes[i] = args[i].getType();
	return product(argumentTypes);
    }

    /**
     * Converts a functor specification to a type (guesses it from the declared type information).
     * @todo package protect but share with orbital.moon.logic
     * @fixme we cannot know that ClassicalLogic & Co implement AND as a BinaryFunction, not as a BinaryPRedicate<Boolean,Boolean>
     */
    private static final Type declaredTypeOf(Functor.Specification spec) {
	return map(typeOf(spec.getParameterTypes()), type(spec.getReturnType()));
    }

    /**
     * Converts a functor specification to a type (guesses it from the declared type information).
     * Also looks for additional declarations of logical type.
     * @throws IntrospectionException if an exception occurs during introspection.
     * @todo package protect but share with orbital.moon.logic
     * @fixme we cannot know that ClassicalLogic & Co implement AND as a BinaryFunction, not as a BinaryPRedicate<Boolean,Boolean>
     */
    /*private*/public static final Type declaredTypeOf(Functor f) throws IntrospectionException {
	Type type = getTypeDeclaration(f);
	return type != null ? type : declaredTypeOf(Functor.Specification.getSpecification(f));
    }

    /**
     * Returns the type of a list of classes.
     * Legacy conversion.
     * @pre true
     * @param args the arguments whose (combined) type to return.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @see Types#product(Type[])
     * @see Types#type(Class)
     */
    private static final Type typeOf(Class[] args) {
	if (args == null || args.length == 0)
	    return VOID;
	final Type argumentTypes[] = new Type[args.length];
	for (int i = 0; i < argumentTypes.length; i++)
	    argumentTypes[i] = type(args[i]);
	return product(argumentTypes);
    }

    /**
     * Get the logical type description specified in the given object.
     * <pre>
     * <span class="keyword">static</span> <span class="keyword">final</span> <span class="Orbital">Type</span> logicalTypeDeclaration;
     * </pre>
     * or <code>null</code> if no such field exists.
     * Implementations may also consider non-static fields of the same name.
     * @permission Needs access to the object's class and will therefore call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) setAccessible(true)}.
     */
    private static Type getTypeDeclaration(Object f) {
	Class c = f.getClass();
	try {
	    Field spec = getFieldOrInherited(c, "logicalTypeDeclaration");
	    int   requiredModifier = Modifier.FINAL;
	    if ((spec.getModifiers() & requiredModifier) == requiredModifier
		&& Type.class.isAssignableFrom(spec.getType())) {
		if (true || !spec.isAccessible())
		    spec.setAccessible(true);
		return (Type) spec.get(f);
	    }
	}
	catch (NoSuchFieldException trial) {}
	catch (IllegalAccessException trial) {}
	return null;
    }

    /**
     * Gets either a public or a declared field (not yet declared fields of super class).
     * @todo move to orbital.util.Utility
     */
    private static Field getFieldOrInherited(Class c, String name) throws NoSuchFieldException {
	try {
	    return c.getField(name);
	}
	catch (NoSuchFieldException trial) {}
	return c.getDeclaredField(name);
    }

    /**
     * Lexicographic order on types.
     * <p>
     * This implementation compares for arity in favor of codomain-type in favor of domain-type.
     * </p>
     * @see orbital.logic.functor.Functor.Specification#compareTo(Object)
     */
    static final Comparator LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object a, Object b) {
		return compare((Type)a, (Type)b);
	    }
	    private final int compare(Type a, Type b) {
		int order = arityOf(a) - arityOf(b);
		if (order != 0)
		    return order;
		if ((a instanceof FundamentalType) || (b instanceof FundamentalType))
		    return !(a instanceof FundamentalType)
			// fundamental types are smaller than others
			? 1
			: !(b instanceof FundamentalType)
			// fundamental types are smaller than others
			? -1
			// lexicographic compare of names
			: ((FundamentalType)a).getFundamental().getName().compareTo(((FundamentalType)b).getFundamental().getName());
		else if ((a instanceof MapType) || (b instanceof MapType)) {
		    //@internal could avoid checking for MapType and rely on domain() and codomain() instead
		    if (!(a instanceof MapType))
			// map types are smaller than product types
			return 1;
		    else if (!(b instanceof MapType))
			// map types are smaller than product types
			return -1;
		    order = compare(a.codomain(), b.codomain());
		    if (order != 0)
			return order;
		    else
			return compare(a.domain(), b.domain());
		} else if ((a instanceof ProductType) || (b instanceof ProductType))
		    if (!(a instanceof ProductType) || !(b instanceof ProductType))
			// fall-through
			;
		    else {
			Type as[] = ((ProductType)a).getComponent();
			Type bs[] = ((ProductType)b).getComponent();
			assert as.length == bs.length : "equal arities means equal number of components";
			for (int i = 0; i < as.length; i++) {
			    order = compare(as[i], bs[i]);
			    if (order != 0)
				return order;
			}
			return 0;
		    }

		throw new IllegalArgumentException("unknown types to compare " + a.getClass() + " " + b.getClass());
	    }
	};
}// Types
