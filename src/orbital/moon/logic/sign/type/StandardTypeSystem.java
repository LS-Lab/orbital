/**
 * @(#)StandardTypeSystem.java 1.1 2003-01-18 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.imp;
import orbital.logic.imp.Type.Composite;
import orbital.logic.imp.*;

import java.util.Comparator;
import orbital.util.IncomparableException;

import orbital.logic.trs.Variable;
import java.io.Serializable;
import orbital.logic.functor.Functor;
import orbital.logic.functor.Predicate;
import orbital.logic.functor.BinaryPredicate;
import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;
import orbital.logic.functor.Functionals;
import java.util.Collection;
import java.util.Set;
import java.util.List;

import orbital.logic.functor.Notation;

import orbital.util.Utility;
import orbital.util.Setops;
import orbital.math.MathUtilities;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashSet;

import java.beans.IntrospectionException;
import orbital.util.InnerCheckedException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Provides type constructors, implementations, and factories for types
 * of the "standard" type-system.
 * Type constructors create new types depending on some existing types.
 * For example, there are type constructors for map types.
 * <p>
 * Major decisions for this implementation are
 * <ol>
 *   <li><span class="type">&perp;&rarr;&tau;</span> = <span class="type">&tau;</span></li>
 *   <li><span class="type">&rho;&times;&sigma;&rarr;&tau;</span> sometimes equals <span class="type">&rho;&rarr;(&sigma;&rarr;&tau;)</span> by currying</li>
 *   <li><span class="type">(&rho;&times;&sigma;)&times;&tau;</span> &ne; <span class="type">&rho;&times;&sigma;&times;&tau;</span>,
 *     since this additional structure may generally contain information that would get lost in the flat form.
 *   </li>
 * </ol>
 * </p>
 *
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version 1.1, 2003-01-18
 * @see Type
 */
public final class StandardTypeSystem implements TypeSystem {
    private static final char GROUPING_BRACKET_OPEN = '[';
    private static final char GROUPING_BRACKET_CLOSE = ']';
    //@internal tricky: we have to make sure the static initialization (which uses mymap...) can run before typeSystem is set. Java does not truely care about the static initialization order.
    private static final StandardTypeSystem typeSystem = new StandardTypeSystem();
    /**
     * @see Type#compareTo(Object)
     * @see StandardTypeSystem.TypeObject#compareToSemiImpl(Type)
     * @todo rename and perhaps even move to Type.
     */
    private static final Comparator subtypeOrder = new Comparator() {
	    public int compare(Object o1, Object o2) {
		if (!(o1 instanceof TypeObject && o2 instanceof TypeObject))
		    return ((Type)o1).compareTo((Type)o2);
		TypeObject a = (TypeObject)o1;
		TypeObject b = (TypeObject)o2;
		if (a.comparisonPriority() >= b.comparisonPriority())
		    return a.compareToSemiImpl(b);
		else
		    return -b.compareToSemiImpl(a);
	    }
	};
    /**
     * Predicate version of &le;.
     * @see Type#subtypeOf(Type)
     */
    private static final BinaryPredicate/*<Type,Type>*/ subtypeOf = new BinaryPredicate() {
	    public boolean apply(Object s, Object t) {
		return ((Type)s).subtypeOf((Type)t);
	    }
	};
    /**
     * Predicate version of &ge;.
     * @see #subtypeOf
     * @see Functionals#swap(BinaryPredicate)
     */
    private static final BinaryPredicate supertypeOf/*<Type,Type>*/ = Functionals.swap(subtypeOf);

    private static final Type _UNIVERSAL = new UniversalType();
    public final Type UNIVERSAL() {
	return _UNIVERSAL;
    }
    private static final class UniversalType extends FundamentalType {
	private static final long serialVersionUID = -7043311457660579287L;
	/**
	 * Maintains the guarantee that there is only a single object representing this type.
	 * @serialData canonicalized deserialization
	 */
	private Object readResolve() throws java.io.ObjectStreamException {
	    // canonicalize
	    return typeSystem.UNIVERSAL();
	} 
	public final boolean equals(Object o) {
	    //@internal assume canonical
	    return this == o;
	}
	public final int hashCode() {
	    return System.identityHashCode(this);
	}

	public Class getFundamental() {
	    //@internal returning null here is safe and assures that !INDIVIDUAL.equals(this);
	    return null;
	    //@internal the root of the Java class hierarchy
	    //return Object.class;
	}
	protected int comparisonPriority() {
	    return Integer.MAX_VALUE - 10;
	}
	public final int compareToSemiImpl(Type b) {
	    //@todo verify the converse
	    //@internal assume canonical
	    return this == b ? 0 : 1;
	}
	public final boolean apply(Object x) {
	    return true;
	}
	public String toString() {
	    return "univ";
	}
    };
    private static final Type _TYPE = new KindType();
    public final Type TYPE() {
	return _TYPE;
    }
    private static final class KindType extends FundamentalType {
	private static final long serialVersionUID = -7485537037074013524L;
	/**
	 * Maintains the guarantee that there is only a single object representing this type.
	 * @serialData canonicalized deserialization
	 */
	private Object readResolve() throws java.io.ObjectStreamException {
	    // canonicalize
	    return typeSystem.TYPE();
	} 
	public final boolean equals(Object o) {
	    //@internal assume canonical
	    return this == o;
	}
	public final int hashCode() {
	    return System.identityHashCode(this);
	}

	public Class getFundamental() {
	    return Type.class;
	}
	protected int comparisonPriority() {
	    return Integer.MAX_VALUE - 20;
	}
	public final int compareToSemiImpl(Type b) {
	    //@xxx should we explicitly exclude TYPE from the ordinary type hierarchy?
	    if (this == b)
		return 0;
	    else
		throw new IncomparableException();
	}
	public final boolean apply(Object x) {
	    //@internal interpretations of types are sets
	    // (or in implementation terms: their orbital.logic.functor.Predicate, c.f. &delta;extension)
	    return x instanceof Predicate || x instanceof Type;
	}
	public String toString() {
	    return "*";
	}
    };
    /**
     * The type <span class="type">&iota;</span> of individuals ({@link java.lang.Object objects}).
     * @xxx remove?
     */
    public static final Type INDIVIDUAL = new FundamentalTypeImpl(Object.class) {
	    public String toString() {return "individual";}
	};
    /**
     * The type <span class="type">&omicron;</span> = <span class="type">()</span> of truth-values.
     * @xxx for multi-valued logics this is not limited to boolean.
     * @todo what about Boolean.TYPE and Boolean.class? Should TRUTH =< INDIVIDUAL?
     * @xxx remove?
     */
    public static final Type TRUTH = new FundamentalTypeImpl(Boolean.class) {
	    //@xxx generalize and make accessible from outside (f.ex. from ClassicalLogic)
	    public String toString() {return "truth";}
	};
    private static final Type _ABSURD = new AbsurdType();
    public final Type ABSURD() {
	return _ABSURD;
    }
    private static final class AbsurdType extends FundamentalType {
	private static final long serialVersionUID = 7539731602290983194L;
	private transient final Class type = new Object() {}.getClass();
	/**
	 * Maintains the guarantee that there is only a single object representing this type.
	 * @serialData canonicalized deserialization
	 */
	private Object readResolve() throws java.io.ObjectStreamException {
	    // canonicalize
	    return typeSystem.ABSURD();
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
	protected int comparisonPriority() {
	    return Integer.MAX_VALUE - 10;
	}
	public final int compareToSemiImpl(Type b) {
	    return this == b ? 0 : -1;
	}
	public final boolean apply(Object x) {
	    return false;
	}
	public String toString() {
	    return "absurd";
	}
    };
    /**
     * @todo increase comparisonPriority for peformance reasons.
     */
    private static final Type _NOTYPE = new FundamentalTypeImpl(Void.TYPE);
    public final Type NOTYPE() {
	return _NOTYPE;
    }

    
    public StandardTypeSystem() {}

    public boolean equals(Object o) {
	return o instanceof TypeSystem && getClass().equals(o.getClass());
    }

    public int hashCode() {
	return getClass().hashCode();
    }


    
    // base classes
    
    /**
     * The root object for type implementations.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-11
     */
    private static abstract class TypeObject implements Type, Serializable {
	private static final long serialVersionUID = 3881468737970732586L;
	private static final Type logicalTypeDeclaration = _TYPE;
	protected TypeObject() {}

	public TypeSystem typeSystem() {
	    return typeSystem;
	}
	
	/**
	 * The priority of the comparison rule implemented in {@link #compareToImpl(Type)}.
	 * Higher values are for higher priorities.
	 * This method induces a total order on the individual comparison rules and decides
	 * which rule is applied.
	 * @postconditions RES==OLD(RES) && RES>=0
	 */
	protected abstract int comparisonPriority();
	/**
	 * Semi-implementation of {@link Type#compareTo(Object)}.
	 * Called from {@link StandardTypeSystem#subtypeOrder} with both orders of arguments.
	 * by types how do not always know themselves
	 * whether they are a subtype or supertype of another type unknown to them.
	 * @todo adapt documentation to actual implementation
	 */
	protected abstract int compareToSemiImpl(Type b);
	public final int compareTo(Object b) {
	    if (!(b instanceof TypeObject))
		if (b == null)
		    throw new NullPointerException("illegal type " + b + " to compare " + this + " of " + getClass() + " with");
		else
		    throw new InternalError("no rule for comparing " + this + " of " + getClass() + " with " + b + " of " + b.getClass());
	    int cmp = subtypeOrder.compare(this, (Type)b);
	    assert !((cmp == 0) ^ this.equals(b)) : "antisymmetry resp. consistent with equals: " + this + ", " + b + "\n" + this + toString(cmp) + b + "\n" + this + (this.equals(b) ? "=" : "!=") + b;
	    assert subtypeOrder.compare(this, this) == 0 : "reflexive: " + this;
	    assert MathUtilities.sign(cmp) == -MathUtilities.sign(subtypeOrder.compare((Type)b, this)) : "antisymmetry: " + this + ", " + b;
	    return cmp;
	}

	public final boolean subtypeOf(Type tau) {
	    try {
		return compareTo(tau) <= 0;
	    }
	    catch (IncomparableException incomparable) {
		return false;
	    }
	}

	private static final String toString(int comparisonResult) {
	    return new String[] {"=<", "=", ">="} [MathUtilities.sign(comparisonResult) + 1];
	}
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
	    return "[" + domain() + "->" + codomain() + "]";
	}
	
	public int compareToSemiImpl(Type b) {
	    assert codomain() != typeSystem.ABSURD() && b.codomain() != typeSystem.ABSURD() : "s->ABSURD = ABSURD is no map type (and has higher comparisonPriority)";
	    int doc = domain().compareTo(b.domain());
	    int coc = codomain().compareTo(b.codomain());
	    if (coc == 0 && doc == 0)
		return 0;
	    else if ((doc >= 0 && coc <= 0)
		     || b.equals(typeSystem.UNIVERSAL())) //@todo still needed?
		return -1;
	    else if (doc <= 0 && coc >= 0)
		return 1;
	    else
		throw new IncomparableException(this + " is incomparable with " + b);
	}
    }

    /**
     * Base class for non-map types i.e. with void domain.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002/10/06
     * @see StandardTypeSystem.MapType
     */
    private static abstract class NonMapType extends TypeObject {
	private static final long serialVersionUID = -6241523127417780697L;
	public Type domain() {
	    return typeSystem.NOTYPE();
	}
	public Type codomain() {
	    return this;
	}
    }

    /**
     * .
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-11-24
     */
    private static abstract class AbstractCompositeType extends NonMapType implements Type.Composite {
	private static final long serialVersionUID = 5980179612049115956L;
	// identical copy under @see orbital.logic.functor.Functor.Composite.Abstract
	/**
	 * the current notation used for displaying this composite functor.
	 * @serial
	 */
	private Notation notation;
	protected AbstractCompositeType(Notation notation) {
	    setNotation(notation);
	}
	protected AbstractCompositeType() {
	    this(null);
	}
    
	public orbital.logic.Composite construct(Object f, Object g) {
	    try {
		orbital.logic.Composite c = (orbital.logic.Composite) getClass().newInstance();
		c.setCompositor(f);
		c.setComponent(g);
		return c;
	    }
	    catch (InstantiationException ass) {
		throw (UnsupportedOperationException) new UnsupportedOperationException("invariant: sub classes of " + Functor.Composite.class + " must either support nullary constructor for modification cloning or overwrite construct(Object,Object)").initCause(ass);
	    }
	    catch (IllegalAccessException ass) {
		throw (UnsupportedOperationException) new UnsupportedOperationException("invariant: sub classes of " + Functor.Composite.class + " must either support nullary constructor for modification cloning or overwrite construct(Object,Object)").initCause(ass);
	    }
	}

	/**
	 * Get the notation used for displaying this composite functor.
	 */
	public Notation getNotation() {
	    return notation;
	}
	/**
	 * Set the notation used for displaying this composite functor.
	 */
	public void setNotation(Notation notation) {
	    this.notation = notation == null ? Notation.DEFAULT : notation;
	}
    		
	/**
	 * Checks for equality.
	 * Two CompositeFunctors are equal iff their classes,
	 * their compositors and their components are equal.
	 */
	public boolean equals(Object o) {
	    if (o == null || getClass() != o.getClass())
		return false;
	    // note that it does not matter to which .Composite we cast since we have already checked for class equality
	    Composite b = (Composite) o;
	    return Utility.equals(getCompositor(), b.getCompositor())
		&& Utility.equalsAll(getComponent(), b.getComponent());
	}
    
	public int hashCode() {
	    return Utility.hashCode(getCompositor()) ^ Utility.hashCodeAll(getComponent());
	}
    
	/**
	 * Get a string representation of the composite functor.
	 * @return <code>{@link Notation#format(Object, Object) notation.format}(getCompositor(), getComponent())</code>.
	 */
	public String toString() {
	    return getNotation().format((Functor)getCompositor(), getComponent());
	}
    }
    

    // factories
    
    public Type objectType(Class type) {
	return type.equals(Boolean.class) || type.equals(Boolean.TYPE)
	    ? TRUTH
	    : type.equals(Void.TYPE)
	    ? typeSystem.ABSURD()
	    : type.equals(Object.class)
	    ? INDIVIDUAL
	    : new FundamentalTypeImpl(type);
    }
    public Type objectType(Class type, String signifier) {
	return new FundamentalTypeImpl(type, signifier);
    }
    
    /**
     * Base implementation for fundamental types already available at the language level ({@link java.lang.Class}).
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-11
     */
    private static abstract class FundamentalType extends NonMapType {
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

	protected int comparisonPriority() {
	    return 50;
	}
	public int compareToSemiImpl(Type b) {
	    if (b instanceof FundamentalType) {
		FundamentalType tau = (FundamentalType)b;
		if (getFundamental().equals(tau.getFundamental()))
		    return 0;
		else if (tau.getFundamental().isAssignableFrom(getFundamental()))
		    return -1;
		else if (getFundamental().isAssignableFrom(tau.getFundamental()))
		    return 1;
	    }
	    throw new IncomparableException(this + " is incomparable with " + b);
	}

	public boolean apply(Object x) {
	    //@xxx check that all tests are really correct. What's up with VoidFunction, and Function<Object,Boolean> etc?
	    // if (x instanceof Functor && spec.isConform((Functor) x)) return true;
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
	private final String signifier;
	public FundamentalTypeImpl(Class type) {
	    this(type, type.toString());
	}
	public FundamentalTypeImpl(Class type, String signifier) {
	    if (type == null)
		throw new NullPointerException("illegal class " + type);
	    this.type = type;
	    this.signifier = signifier;
	}
	public Class getFundamental() {
	    return type;
	}
	public String toString() {
	    return signifier;
	}
    }

    // type constructors
    
    public Type map(Type domain, Type codomain) {
	return mymap(domain, codomain);
    }
    private static Type mymap(Type domain, Type codomain) {
	if (codomain == _ABSURD)
	    //@internal because from a (OE nonempty) set there is no (left total) _function_ into the empty set.
	    // for ABSURD->ABSURD an alternative could in principle be, to return a type of a codomain with a single element.
	    // however, in either case, this has an impact on the usual subtype relationship
	    return _ABSURD;
	if (domain == _ABSURD)
	    // strict? after change also @see MathExpressionSyntax.createAtomic
	    // or ABSURD->t = t is true in some type systems, = ABSURD in others.
	    throw new UnsupportedOperationException(_ABSURD + " maps not yet supported");
	return domain.equals(_NOTYPE) ? codomain : new MapType(domain, codomain);
    }
    private static final BinaryFunction/*<Type,Type,Type>*/ _map = new BinaryFunction/*<Type,Type,Type>*/() {
	    private final Type logicalTypeDeclaration = mymap(myproduct(new Type[] {_TYPE,_TYPE}), _TYPE);
	    public Object apply(Object s, Object t) {
		return typeSystem.map((Type)s, (Type)t);
	    }
	    public String toString() {
		return "->";
	    }
	};
    public BinaryFunction/*<Type,Type,Type>*/ map() {
	return _map;
    };

    public Type predicate(Type domain) {
	return map(domain, TRUTH);
    }
    private static final Function/*<Type,Type>*/ _predicate = new Function/*<Type,Type>*/() {
	    private final Type logicalTypeDeclaration = mymap(_TYPE, _TYPE);
	    public Object apply(Object t) {
		return typeSystem.predicate((Type)t);
	    }
	    public String toString() {
		return "pred";
	    }
	};
    public final Function/*<Type,Type>*/ predicate() {
	return _predicate;
    }

    /**
     * Implementation of map types.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-08
     * @see StandardTypeSystem.NonMapType
     */
    private static final class MapType extends TypeBase {
	private static final long serialVersionUID = 9148024444083534208L;
	private final Type codom;
	private final Type dom;
	public MapType(Type domain, Type codomain) {
	    this.dom = domain;
	    this.codom = codomain;
	}

	public Type codomain() {
	    return codom;
	}
	
	public Type domain() {
	    return dom;
	}

	protected int comparisonPriority() {
	    return 400;
	}

	public boolean apply(Object x) {
	    assert StandardTypeSystem.arityOf(dom) > 0 : "map(Type,Type) canonically filters domain=NOTYPE. But the domain of " + this + " of " + getClass() + " has arity " + StandardTypeSystem.arityOf(dom);
	    //@xxx originally was  referent instanceof Functor && spec.isConform((Functor) referent)
	    if (false && StandardTypeSystem.arityOf(dom) <= 1)
		return Functionals.bindSecond(Utility.instanceOf, codom.equals(TRUTH)
					      ? Predicate.class
					      : Function.class).apply(x);
	    else if (x instanceof Functor) {
		try {
		    final Type argType = Types.declaredTypeOf((Functor)x);
		    return argType.subtypeOf(this);
		}
		catch (IntrospectionException ex) {throw new InnerCheckedException("could not detect specification", ex);}
	    } else
		return false;
	}
    }

    public Type product(Type components[]) {
	return myproduct(components);
    }
    private static Type myproduct(Type components[]) {
	for (int i = 0; i < components.length; i++) {
	    Type ti = components[i];
	    if (ti == null)
		throw new NullPointerException("illegal components containing " + ti);
	    else if (ti == typeSystem.ABSURD())
		// strict
		return typeSystem.ABSURD();
	}
	switch (components.length) {
	case 0: return typeSystem.NOTYPE(); //@xxx was ABSURD; but we need to result in a nonmap type for 0-arity functions. (resolution-fol)
	case 1: return components[0];
	default: return new ProductType(components);
	}
    }
    private static final Function/*<Type[],Type>*/ _product = new Function/*<Type[],Type>*/() {
	    private final Type logicalTypeDeclaration = null;
	    public Object apply(Object t) {
		return typeSystem.product((Type[])t);
	    }
	    public String toString() {
		return "*";
	    }
	};
    public final Function/*<Type[],Type>*/ product() {
	return _product;
    }

    /**
     * Implementation of product types.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-08
     * @internal which interface for product types? The answer may be: nothing particular, since the typical operations should rely on compareTo and equals.
     */
    private static final class ProductType extends AbstractCompositeType {
	private static final long serialVersionUID = -6667362786622508551L;
	private Type components[];
	private ProductType() {
	}
	public ProductType(Type components[]) {
	    this.setComponent(components);
	}
	//@internal almost like in AbstractCompositeFunctor
	public boolean equals(Object o) {
	    return (o instanceof ProductType) && Utility.equalsAll(components, ((ProductType)o).components);
	}
	
	public int hashCode() {
	    return Utility.hashCodeAll(components);
	}
	
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(GROUPING_BRACKET_OPEN);
	    sb.append(components[0].toString());
	    for (int i = 1; i < components.length; i++) {
		sb.append('*');
		sb.append(components[i]);
	    }
	    sb.append(GROUPING_BRACKET_CLOSE);
	    return sb.toString();
	}

	public Object getCompositor() {
	    return typeSystem.product();
	}
	public Object getComponent() {
	    return components;
	}
	public void setCompositor(Object compositor) {
	    if (compositor != getCompositor())
		throw new IllegalArgumentException("special compositor of " + getClass() + " expected");
	}
	public void setComponent(Object component) {
	    if (((Object[])component).length == 0)
		throw new IllegalArgumentException();
	    this.components = (Type[]) component;
	}

	protected int comparisonPriority() {
	    return 180;
	}
	public int compareToSemiImpl(Type tau) {
	    if (tau instanceof ProductType) {
		Type taui[] = ((ProductType)tau).components;
		if (components.length != taui.length)
		    throw new IncomparableException(this + " is incomparable with " + tau);
		int cmp = 0;
		for (int i = 0; i < components.length; i++) {
		    final int cmpi = MathUtilities.sign(components[i].compareTo(taui[i]));
		    if (cmpi == cmp || cmpi == 0)
			// always ok
			;
		    else if (cmpi < cmp && cmp == 0)
			cmp = cmpi;
		    else if (cmpi > cmp && cmp == 0)
			cmp = cmpi;
		    else
			throw new IncomparableException(this + " is incomparable with " + tau);
		}
		return cmp;
	    } else if (tau.equals(typeSystem.UNIVERSAL()))
		//@todo still needed?
		return 0;
	    else
		throw new IncomparableException(this + " is incomparable with " + tau);
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


    public Type inf(Type components[]) {
	//@internal although we still work on components, t keeps the simplified version of components.
	List/*<Type>*/ t = new LinkedList(Arrays.asList(components));
	//@internal this canonical simplification also assures strict
	for (int i = 0; i < components.length; i++) {
	    Type ti = components[i];
	    if (ti == null)
		throw new NullPointerException("illegal arguments containing " + ti);
	    if (ti instanceof InfimumType) {
		// associative
		t.remove(ti);
		t.addAll(Arrays.asList(((InfimumType)ti).components));
		return inf((Type[])t.toArray(new Type[0]));
	    }
		
	    for (int j = i + 1; j < components.length; j++) {
		Type tj = components[j];
		try {
		    int cmp = ti.compareTo(tj);
		    // remove redundant supertypes of comparable types in t
		    if (cmp < 0)
			t.remove(tj);
		    else if (cmp > 0)
			t.remove(ti);
		} catch (IncomparableException test) {}
	    }
	}
	components = (Type[])t.toArray(new Type[0]);
	switch (components.length) {
	case 0: return typeSystem.UNIVERSAL();
	case 1: return components[0];
	default: return new InfimumType(components);
	}
    }

    private static final Function/*<Type[],Type>*/ _inf = new Function/*<Type[],Type>*/() {
	    private final Type logicalTypeDeclaration = null; //@xxx
	    public Object apply(Object t) {
		return typeSystem.inf((Type[])t);
	    }
	    public String toString() {
		return "&";
	    }
	};
    public final Function/*<Type[],Type>*/ inf() {
	return _inf;
    }

    /**
     * Implementation of infimum types.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-10-04
     * @todo should we extend AbstractCompositeType?
     */
    private static final class InfimumType extends NonMapType {
	private static final long serialVersionUID = -6251593765414274805L;
	private Type components[];
	private InfimumType() {}
	public InfimumType(Type components[]) {
	    this.setComponent(components);
	}
	public boolean equals(Object o) {
	    return (o instanceof InfimumType) && new HashSet(Arrays.asList(components)).equals(new HashSet(Arrays.asList(((InfimumType)o).components)));
	}
	
	public int hashCode() {
	    return 1 ^ Utility.hashCodeAll(components);
	}
	
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(GROUPING_BRACKET_OPEN);
	    sb.append(components[0].toString());
	    for (int i = 1; i < components.length; i++) {
		sb.append('&');
		sb.append(components[i]);
	    }
	    sb.append(GROUPING_BRACKET_CLOSE);
	    return sb.toString();
	}

	public Object getCompositor() {
	    return typeSystem.inf();
	}
	public Object getComponent() {
	    return components;
	}
	public void setCompositor(Object compositor) {
	    if (compositor != getCompositor())
		throw new IllegalArgumentException("special compositor of " + getClass() + " expected");
	}
	public void setComponent(Object component) {
	    if (((Object[])component).length == 0)
		throw new IllegalArgumentException();
	    this.components = (Type[]) component;
	}

	protected int comparisonPriority() {
	    return 200;
	}
	public int compareToSemiImpl(Type tau) {
	    if (equals(tau))
		return 0;
	    if (tau instanceof InfimumType)
		throw new UnsupportedOperationException("comparing infimum types is not yet supported");
	    else if (tau instanceof SupremumType && Setops.some(Arrays.asList(((SupremumType)tau).components), Functionals.bindSecond(Utility.instanceOf, InfimumType.class)))
		throw new UnsupportedOperationException("comparing infimum types with supremum types of infimum types not yet supported");

	    if (compareSubtypeOf(tau))
		return -1;
	    else if (compareSupertypeOf(tau))
		return 1;
	    else
		throw new IncomparableException(this + " is incomparable with " + tau);
	}
	private boolean compareSubtypeOf(Type tau) {
	    return Setops.some(Arrays.asList(components), Functionals.bindSecond(subtypeOf, tau));
	}
	private boolean compareSupertypeOf(Type tau) {
	    return Setops.all(Arrays.asList(components), Functionals.bindFirst(subtypeOf, tau));
	}

	public boolean apply(Object x) {
	    //@todo rewrite pure functional
	    for (int i = 0; i < components.length; i++)
		if (!components[i].apply(x))
		    return false;
	    return true;
	}
    }

    public Type sup(Type components[]) {
	//@internal although we still work on components, t keeps the simplified version of components.
	List/*<Type>*/ t = new LinkedList(Arrays.asList(components));
	//@internal this canonical simplification also assures strict
	for (int i = 0; i < components.length; i++) {
	    Type ti = components[i];
	    if (ti == null)
		throw new NullPointerException("illegal arguments containing " + ti);
	    if (ti instanceof SupremumType) {
		// associative
		t.remove(ti);
		t.addAll(Arrays.asList(((SupremumType)ti).components));
		return sup((Type[])t.toArray(new Type[0]));
	    }
	    for (int j = i + 1; j < components.length; j++) {
		Type tj = components[j];
		try {
		    int cmp = ti.compareTo(tj);
		    // remove redundant subtypes of comparable types in t
		    if (cmp < 0)
			t.remove(ti);
		    else if (cmp > 0)
			t.remove(tj);
		} catch (IncomparableException test) {}
	    }
	}
	components = (Type[])t.toArray(new Type[0]);
	switch (components.length) {
	case 0: return typeSystem.ABSURD();
	case 1: return components[0];
	default: return new SupremumType(components);
	}
    }

    private static final Function/*<Type[],Type>*/ _sup = new Function/*<Type[],Type>*/() {
	    private final Type logicalTypeDeclaration = null;
	    public Object apply(Object t) {
		return typeSystem.sup((Type[])t);
	    }
	    public String toString() {
		return "|";
	    }
	};
    public final Function/*<Type[],Type>*/ sup() {
	return _sup;
    }

    /**
     * Implementation of supremum types.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-10-04
     */
    private static final class SupremumType extends NonMapType {
	private static final long serialVersionUID = 2673832577121308931L;
	private Type components[];
	private SupremumType() {}
	public SupremumType(Type components[]) {
	    this.setComponent(components);
	}
	public boolean equals(Object o) {
	    return (o instanceof SupremumType) && new HashSet(Arrays.asList(components)).equals(new HashSet(Arrays.asList(((SupremumType)o).components)));
	}
	
	public int hashCode() {
	    return 7 ^ Utility.hashCodeAll(components);
	}
	
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(GROUPING_BRACKET_OPEN);
	    sb.append(components[0].toString());
	    for (int i = 1; i < components.length; i++) {
		sb.append('|');
		sb.append(components[i]);
	    }
	    sb.append(GROUPING_BRACKET_CLOSE);
	    return sb.toString();
	}

	public Object getCompositor() {
	    return typeSystem.sup();
	}
	public Object getComponent() {
	    return components;
	}
	public void setCompositor(Object compositor) {
	    if (compositor != getCompositor())
		throw new IllegalArgumentException("special compositor of " + getClass() + " expected");
	}
	public void setComponent(Object component) {
	    if (((Object[])component).length == 0)
		throw new IllegalArgumentException();
	    this.components = (Type[]) component;
	}

	protected int comparisonPriority() {
	    return 200;
	}
	public int compareToSemiImpl(Type tau) {
	    if (equals(tau))
		return 0;
	    if (tau instanceof SupremumType)
		throw new UnsupportedOperationException("comparing supremum types is not yet supported");
	    else if (tau instanceof InfimumType && Setops.some(Arrays.asList(((InfimumType)tau).components), Functionals.bindSecond(Utility.instanceOf, SupremumType.class)))
		throw new UnsupportedOperationException("comparing supremum types with infimum types of supremum types not yet supported");

	    if (compareSubtypeOf(tau))
		return -1;
	    else if (compareSupertypeOf(tau))
		return 1;
	    else
		throw new IncomparableException(this + " is incomparable with " + tau);
	}
	private boolean compareSubtypeOf(Type tau) {
	    return Setops.all(Arrays.asList(components), Functionals.bindSecond(subtypeOf, tau));
	}
	private boolean compareSupertypeOf(Type tau) {
	    return Setops.some(Arrays.asList(components), Functionals.bindFirst(subtypeOf, tau));
	}

	public boolean apply(Object x) {
	    //@todo rewrite pure functional
	    for (int i = 0; i < components.length; i++)
		if (components[i].apply(x))
		    return true;
	    return false;
	}
    }

    // collection types
    
    public Type collection(Type element) {
	return new CollectionType(Collection.class, element, "collection(", ")");
    }
    private static final Function/*<Type,Type>*/ _collection = new Function() {
	    private final Type logicalTypeDeclaration = mymap(_TYPE, _TYPE);
	    public Object apply(Object o) {
		return typeSystem.collection((Type)o);
	    }
	    public String toString() {
		return "collection";
	    }
	};
    public final Function/*<Type,Type>*/ collection() {
	return _collection;
    }
    public Type set(Type element) {
	return new CollectionType(Set.class, element, "{", "}");
    }
    private final Function/*<Type,Type>*/ _set = new Function() {
	    private final Type logicalTypeDeclaration = mymap(_TYPE, _TYPE);
	    public Object apply(Object o) {
		return typeSystem.set((Type)o);
	    }
	    public String toString() {
		return "set";
	    }
	};
    public final Function/*<Type,Type>*/ set() {
	return _set;
    }
    public final Type list(Type element) {
	return new CollectionType(List.class, element, "<", ">");
    }
    /**
     * list: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">&lang;&tau;&rang;</span>.
     * <p>
     * The list type constructor.
     * </p>
     * @see #list(Type)
     */
    private static final Function/*<Type,Type>*/ _list = new Function() {
	    private final Type logicalTypeDeclaration = mymap(_TYPE, _TYPE);
	    public Object apply(Object o) {
		return typeSystem.list((Type)o);
	    }
	    public String toString() {
		return "list";
	    }
	};
    public final Function/*<Type,Type>*/ list() {
	return _list;
    }
    public final Type bag(Type element) {
	throw new UnsupportedOperationException("bag interface is not part of Java 1.4");
    }
    private static final Function/*<Type,Type>*/ _bag = new Function() {
	    private final Type logicalTypeDeclaration = mymap(_TYPE, _TYPE);
	    public Object apply(Object o) {
		return typeSystem.bag((Type)o);
	    }
	    public String toString() {
		return "bag";
	    }
	};
    public final Function/*<Type,Type>*/ bag() {
	return _bag;
    }

    /**
     * Collection types.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002/10/06
     */
    private static class CollectionType extends NonMapType {
	private static final long serialVersionUID = -1113530540489964295L;
	private final Class collection;
	private final Type element;
	private final String toStringPrefix;
	private final String toStringSuffix;
	public CollectionType(Class collection, Type element, String toStringPrefix, String toStringSuffix) {
	    this.collection = collection;
	    this.element = element;
	    this.toStringPrefix = toStringPrefix;
	    this.toStringSuffix = toStringSuffix;
	}
	public boolean equals(Object b) {
	    if (b instanceof CollectionType) {
		CollectionType tau = (CollectionType)b;
		return collection.equals(tau.collection) && element.equals(tau.element);
	    } else
		return false;
	}
	public int hashCode() {
	    return 13 ^ collection.hashCode() ^ element.hashCode();
	}
	public String toString() {
	    return toStringPrefix + element + toStringSuffix;
	}
	protected int comparisonPriority() {
	    return 60;
	}
	public int compareToSemiImpl(Type b) {
	    if (b instanceof CollectionType) {
		CollectionType tau = (CollectionType)b;
		return comparisonInternalRepresentation().compareTo(tau.comparisonInternalRepresentation());
// 		if (!collection.equals(tau.collection))
// 		    //@xxx return -1 if collection < tau.collection and element < tau.element etc.
// 		    throw new UnsupportedOperationException("comparing different collection types");
// 		assert toStringPrefix.equals(tau.toStringPrefix) && toStringSuffix.equals(tau.toStringSuffix) : "equal collection type constructors imply equal notation"; 
// 		return element.compareTo(tau.element);
	    } else
		throw new IncomparableException(this + " is incomparable with " + b);
	}
	public boolean apply(Object x) {
	    return collection.isInstance(x) && Setops.all((Collection)x, element);
	}
	/**
	 * @internal collection types and product types are different, but they compare in the same way
	 */
	private Type comparisonInternalRepresentation() {
	    return new ProductType(new Type[] {typeSystem.objectType(collection), element});
	}
    }

    
    
    // Stuff

    /**
     * Get the number of components n of a product type <span class="type">&prod;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&times;&#8230;&times;&tau;<sub>n</sub></span>.
     * @todo rename
     * @todo improve concept to make product accessible (perhaps already in type?) or package-level-protectize
     */
    public static final int arityOf(Type type) {
	return type == Types.getDefault().ABSURD()
	    // strict
	    ? Integer.MIN_VALUE
	    : type.equals(Types.getDefault().NOTYPE())
	    ? 0
	    : arityOf_perhapsProduct(type);
    }
    private static final int arityOf_perhapsProduct(Type type) {
	if (type instanceof Type.Composite) {
	    Type.Composite t = (Type.Composite)type;
	    if (t.getCompositor() == Types.getDefault().product())
		return ((Type[]) t.getComponent()).length;
	}
	return 1;
    }
    /**
     * Lexicographic order on types.
     * <p>
     * This implementation compares for arity in favor of domain-type in favor of codomain-type.
     * </p>
     * @see orbital.logic.functor.Functor.Specification#compareTo(Object)
     */
    public static final Comparator LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object a, Object b) {
		return compare((Type)a, (Type)b);
	    }
	    private final int compare(Type a, Type b) {
		int order = StandardTypeSystem.arityOf(a) - StandardTypeSystem.arityOf(b);
		if (order != 0)
		    return order;
		if (a == typeSystem.UNIVERSAL() || b == typeSystem.UNIVERSAL())
		    return a == typeSystem.UNIVERSAL() && b == typeSystem.UNIVERSAL() ? 0 : a == typeSystem.UNIVERSAL() ? 1 : -1;
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
		    order = compare(a.domain(), b.domain());
		    if (order != 0)
			return order;
		    else
			return compare(a.codomain(), b.codomain());
		} else if ((a instanceof ProductType) || (b instanceof ProductType))
		    if (!(a instanceof ProductType) || !(b instanceof ProductType))
			// fall-through
			;
		    else {
			Type as[] = (Type[]) ((ProductType)a).getComponent();
			Type bs[] = (Type[]) ((ProductType)b).getComponent();
			assert as.length == bs.length : "equal arities means equal number of components";
			for (int i = 0; i < as.length; i++) {
			    order = compare(as[i], bs[i]);
			    if (order != 0)
				return order;
			}
			return 0;
		    }

		if (a instanceof TypeObject && b instanceof TypeObject) {
		    //@internal abuse the comparisonPriority for lexicographical comparison. Much simpler
		    order = ((TypeObject)a).comparisonPriority() - ((TypeObject)b).comparisonPriority();
		    if (order != 0)
			return order;
		    // fall-through
		}
		if ((a instanceof CollectionType) && (b instanceof CollectionType)) {
		    return compare(((CollectionType)a).comparisonInternalRepresentation(),
				   ((CollectionType)b).comparisonInternalRepresentation());
		}
		    
		throw new IllegalArgumentException("unknown types to compare lexicographically, " + a.getClass() + " and " + b.getClass());
	    }
	};
}// StandardTypeSystem
