/**
 * @(#)Types.java 1.1 2002-09-08 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import java.util.Comparator;
import orbital.util.IncomparableException;

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
import java.lang.reflect.*;

/**
 * Provides type constructors, implementations, and factories for types
 * of the "standard" type-system.
 * Type constructors create new types depending on some existing types.
 * For example, there are type constructors for map types.
 *
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version 1.1, 2002-09-08
 * @see Type
 * @internal major decisions
 * <ol>
 *   <li><span class="type">&perp;&rarr;&tau;</span> = <span class="type">&tau;</span></li>
 *   <li><span class="type">&rho;&times;&sigma;&rarr;&tau;</span> sometimes equals <span class="type">&rho;&rarr;(&sigma;&rarr;&tau;)</span> by currying</li>
 *   <li><span class="type">(&rho;&times;&sigma;)&times;&tau;</span> &ne; <span class="type">&rho;&times;&sigma;&times;&tau;</span>,
 *     since this additional structure may generally contain information that would get lost in the flat form.
 *   </li>
 * </ol>
 */
public final class Types {
    private static final char GROUPING_BRACKET_OPEN = '[';
    private static final char GROUPING_BRACKET_CLOSE = ']';
    /**
     * @see Type#compareTo(Object)
     * @see Types.TypeObject#compareToSemiImpl(Type)
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
     * @see #subtypeOf
     * @see Functionals#swap(BinaryPredicate)
     */
    private static final BinaryPredicate supertypeOf/*<Type,Type>*/ = Functionals.swap(subtypeOf);

    /**
     * The universal type
     * <span class="type">&#8868;</span> = <span class="type">&#8898;<sub>&empty;</sub></span>.
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
    /**
     * The meta-type (kind) of types <span class="type">*</span>:&#9633;.
     * The type <span class="type">*</span> and every type containing <span class="type">*</span>
     * is a kind, with the latter being types for type constructors.
     * <i>Types containing meta-types as well as ordinary types are currently undefined.</i>
     */
    public static final Type TYPE = new KindType();
    private static final class KindType extends FundamentalType {
	private static final long serialVersionUID = -7485537037074013524L;
	/**
	 * Maintains the guarantee that there is only a single object representing this type.
	 * @serialData canonicalized deserialization
	 */
	private Object readResolve() throws java.io.ObjectStreamException {
	    // canonicalize
	    return TYPE;
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
    /**
     * The absurd type
     * <span class="type">&perp;</span> = <span class="type">&#8899;<sub>&empty;</sub></span>.
     * It is the bottom element of the lattice &Tau; of types,
     * it cannot be the type of anything that exists, and it satisfies.
     * <ul>
     *   <li>&not;(&exist;x) <span class="type">&perp;</span>(x)</li>
     *   <li>(&forall;<span class="type">&tau;</span>:Type) <span class="type">&perp;</span>&le;<span class="type">&tau;</span></li>
     * </ul>
     * @see #UNIVERSAL
     * @internal nothing (besides that single object whose reference no one knows) is an instance of or a subclass of a fundamental anonymous class. But the same already goes true for Void.TYPE, so let's use that.
     */
    public static final Type ABSURD = new AbsurdType();
    private static final class AbsurdType extends FundamentalType {
	private static final long serialVersionUID = 7539731602290983194L;
	private transient final Class type = new Object() {}.getClass();
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
     * Not a type.
     * The type of expressions that do not have any type at all.
     * This type has extension &empty;.
     * @see #ABSURD
     * @xxx is NOTYPE=ABSURD? for product,sup,inf,collection and extension they have are equal.
     *  So at most the condition <li>(&forall;<span class="type">&tau;</span>:Type) <span class="type">&perp;</span>&le;<span class="type">&tau;</span></li> might be different.
     * @internal if we cannot distinguish NOTYPE (as domain of NonMapType) from ABSURD we would make &iota; and &iota;->string comparable.
     *  NOTYPE is incomparable with all types except itself (and of course ABSURD, and UNIVERSAL)
     *  NOTYPE would not be necessary if Type did not prefer half being a MapType and a check predicate for instanceof MapType were available. But this would seem more ugly.
     * @todo increase comparisonPriority for peformance reasons.
     */
    public static final Type NOTYPE = new FundamentalTypeImpl(Void.TYPE);

    /**
     * prevent instantiation - module class
     */
    private Types() {}

    
    // Utility methods

    /**
     * Checks whether the type specification is compatible with the given list of arguments.
     * Convenience method.
     * @pre true
     * @param compositorType the type of the compositor to apply to the arguments.
     * @param args the arguments to check for compatibility with this symbol.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @return whether a compositor of type compositorType is <a href="Expression.html#freeAlgebraOfTerms">applicable</a> to the given arguments.
     *  Which means that the arguments are assignable to the required parameter types of this symbol.
     *  This especially includes whether the number of arguments matches the arity of the compositorTypes' domain.
     * @post RES == (typeOf(args) &le; compositorType.domain()) == (compositorType &le; typeOf(args)<span class="type">&rarr;&#8868;</span>)
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     * @see Type#subtypeOf(Type)
     * @see orbital.logic.functor.Functor.Specification#isApplicableTo(Object[])
     * @todo introduce isComposableWith(Type,Type) or anything that uses argType.codomain() =< compositorType.domain()
     * @xxx remove convenience?
     */
    public static final boolean isApplicableTo(Type compositorType, Expression[] args) {
	return typeOf(args).subtypeOf(compositorType.domain());
    }

    // base classes
    
    /**
     * For composite types.
     * Type constructs consisting of a type constructor and argument types implement this interface.
     * 
     * @structure is {@link orbital.logic.functor.Functor.Composite}&cap;{@link Type}
     * @structure extends Functor.Composite
     * @structure extends Type
     * @version 1.1, 2002-11-24
     * @author  Andr&eacute; Platzer
     */
    private static interface Composite extends Functor.Composite, Type {}

    /**
     * The root object for type implementations.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-11
     */
    private static abstract class TypeObject implements Type, Serializable {
	private static final long serialVersionUID = 3881468737970732586L;
	private static final Type logicalTypeDeclaration = Types.TYPE;
	protected TypeObject() {}

	/**
	 * The priority of the comparison rule implemented in {@link #compareToImpl(Type)}.
	 * Higher values are for higher priorities.
	 * This method induces a total order on the individual comparison rules and decides
	 * which rule is applied.
	 * @post RES==OLD(RES) && RES>=0
	 */
	protected abstract int comparisonPriority();
	/**
	 * Semi-implementation of {@link Type#compareTo(Object)}.
	 * Called from {@link Types#subtypeOrder} with both orders of arguments.
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
	    assert codomain() != ABSURD && b.codomain() != ABSURD : "s->ABSURD = ABSURD is no map type (and has higher comparisonPriority)";
	    int doc = domain().compareTo(b.domain());
	    int coc = codomain().compareTo(b.codomain());
	    if (coc == 0 && doc == 0)
		return 0;
	    else if ((doc >= 0 && coc <= 0)
		     || b.equals(UNIVERSAL)) //@todo still needed?
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
     * @see Types.MapType
     */
    private static abstract class NonMapType extends TypeObject {
	private static final long serialVersionUID = -6241523127417780697L;
	public Type domain() {
	    return NOTYPE;
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
    private static abstract class AbstractCompositeType extends NonMapType implements Composite {
	//private static final long serialVersionUID = 0;
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
	    return getNotation().format(getCompositor(), getComponent());
	}
    }
    

    // factories
    
    /**
     * Get the object type described by a class.
     * Converts a native Java class object to a type.
     * These types are called object types since they directly are types of objects,
     * described by their classes.
     * @param type the type <span class="type">&tau;</span> represented as a class object.
     * @return <span class="type">&tau;</span> = <span class="type">void&rarr;&tau;</span>.
     * @todo assure canonical identity?
     * @todo rename?
     */
    public static final Type objectType(Class type) {
	return type.equals(Boolean.class) || type.equals(Boolean.TYPE)
	    ? TRUTH
	    : type.equals(Void.TYPE)
	    ? ABSURD
	    : type.equals(Object.class)
	    ? INDIVIDUAL
	    : new FundamentalTypeImpl(type);
    }
    /**
     * Get the object type described by a class.
     * Converts a native Java class object to a type.
     * These types are called object types since they directly are types of objects,
     * described by their classes.
     * <p>
     * <!-- @xxx is this a good idea that fuzzy-truth = number? -->
     * <h3>Still unspecified: type alias'</h3>
     * This method allows specifying unusual signifiers for types.
     * <span style="float: left; font-size: 200%">&#9761;</span>
     * But be aware that this only changes the displayed name of the type, not the type itself.
     * So it is
     * <pre>
     * objectType(<span class="Class">java.lang.Number</span>,<span class="string">"number"</span>).equals(objectType(<span class="Class">java.lang.Number</span>,<span class="string">"numericQuantity"</span>)
     * </pre>
     * </p>
     * @param type the type <span class="type">&tau;</span> represented as a class object.
     * @param signifier the representing the type.
     * @return <span class="type">&tau;</span> = <span class="type">void&rarr;&tau;</span>.
     * @todo assure canonical identity?
     * @see #objectType(Class)
     */
    public static final Type objectType(Class type, String signifier) {
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
    
    /**
     * Get the map type <span class="type">&sigma;&rarr;&tau;</span>.
     * In terms of &Pi;-abstraction, it is
     * <span class="type">&sigma;&rarr;&tau;</span> := <span class="type">&tau;</span><sup class="type">&sigma;</sup> := &Pi;x:<span class="type">&sigma;</span>.<span class="type">&tau;</span>.
     * <p>
     * The subtype relation of types extends to map types as follows
     * <div>
     *   <span class="type">&sigma;&rarr;&tau;</span> &le; <span class="type">&sigma;'&rarr;&tau;'</span>
     *   :&hArr; <span class="type">&sigma;'</span> &le; <span class="type">&sigma;</span> &and; <span class="type">&tau;</span> &le; <span class="type">&tau;'</span>
     *   &hArr; <span class="type">&sigma;</span> &ge; <span class="type">&sigma;'</span> &and; <span class="type">&tau;</span> &le; <span class="type">&tau;'</span>.
     * </div>
     * This means that map subtypes have contravariant parameters and covariant return-types.
     * With the exception of absurd types:
     * <div>
     *   <span class="type">&sigma;&rarr;&perp;</span> = <span class="type">&perp;</span>.
     * </div>
     * <div>
     *   <span class="type">&perp;&rarr;&sigma;</span> = undefined.
     * </div>
     * </p>
     * @param domain the {@link Type#domain() domain} <span class="type">&sigma;</span>.
     * @param codomain the {@link Type#codomain() codomain} <span class="type">&tau;</span>.
     * @todo assure canonical identity?
     * @xxx really strict? This contradicts the usual case, since ABSURD->t >= s->t' iff t' >= t. Although ABSURD =< s->t'.
     *  By the ususal formal rule (see above) it is
     *    ABSURD->t' >= s->t iff t' >= t
     *    s->ABSURD =< s'->t' iff s >= s'
     * @todo perhaps even
     *    s->ABSURD = ABSURD
     *    ABSURD->s = s      for call by name alias lazy evaluation
     * These do all form exceptions to the ususal subtype relation for map types.
     *  ?
     */
    public static final Type map(Type domain, Type codomain) {
	if (codomain == ABSURD)
	    //@internal because from a (OE nonempty) set there is no (left total) _function_ into the empty set.
	    // for ABSURD->ABSURD an alternative could in principle be, to return a type of a codomain with a single element.
	    // however, in either case, this has an impact on the usual subtype relationship
	    return ABSURD;
	if (domain == ABSURD)
	    // strict? after change also @see MathExpressionSyntax.createAtomic
	    // or ABSURD->t = t is true in some type systems, = ABSURD in others.
	    throw new UnsupportedOperationException(ABSURD + " maps not yet supported");
	return domain.equals(NOTYPE) ? codomain : new MapType(domain, codomain);
    }
    /**
     * map: <span class="type">*&times;* &rarr; *</span>; (<span class="type">&sigma;</span>,<span class="type">&tau;</span>) &#8614; <span class="type">&sigma;&rarr;&tau;</span>.
     * <p>
     * The map type constructor.
     * </p>
     * @see #map(Type,Type)
     */
    public static final BinaryFunction/*<Type,Type,Type>*/ map = new BinaryFunction/*<Type,Type,Type>*/() {
	    private final Type logicalTypeDeclaration = Types.map(Types.product(new Type[] {Types.TYPE,Types.TYPE}), Types.TYPE);
	    public Object apply(Object s, Object t) {
		return Types.map((Type)s, (Type)t);
	    }
	    public String toString() {
		return "->";
	    }
	};

    /**
     * Get the predicate type <span class="type">(&sigma;)</span> = <span class="type">&sigma;&rarr;&omicron;</span>.
     * <p>
     * <span style="float: left; font-size: 200%">&#9761;</span>
     * Note that this type depends on the specific truth values.
     * </p>
     * @param domain the {@link Type#domain() codomain} <span class="type">&sigma;</span>.
     * @see #map(Type,Type)
     * @todo assure canonical identity?
     */
    public static final Type predicate(Type domain) {
	return map(domain, TRUTH);
    }
    /**
     * predicate: <span class="type">* &rarr; *</span>; <span class="type">&sigma;</span> &#8614; <span class="type">(&sigma;)</span>.
     * <p>
     * The predicate type constructor.
     * </p>
     * @see #predicate(Type)
     */
    public static final Function/*<Type,Type>*/ predicate = new Function/*<Type,Type>*/() {
	    private final Type logicalTypeDeclaration = Types.map(Types.TYPE, Types.TYPE);
	    public Object apply(Object t) {
		return Types.predicate((Type)t);
	    }
	    public String toString() {
		return "pred";
	    }
	};

    /**
     * Implementation of map types.
     * @author Andr&eacute; Platzer
     * @version 1.1, 2002-09-08
     * @see Types.NonMapType
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
	    assert arityOf(dom) > 0 : "map(Type,Type) canonically filters domain=NOTYPE. But the domain of " + this + " of " + getClass() + " has arity " + arityOf(dom);
	    //@xxx originally was  referent instanceof Functor && spec.isConform((Functor) referent)
	    if (false && arityOf(dom) <= 1)
		return Functionals.bindSecond(Utility.instanceOf, codom.equals(TRUTH)
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
     * Get the product type <span class="type">&prod;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&times;&#8230;&times;&tau;<sub>n</sub></span>.
     * <p>
     * Also called tuple type.
     * </p>
     * <p>
     * The subtype relation of types extends to product types as follows
     * <div>
     *   <span class="type">&prod;<sub>i&isin;I</sub>&sigma;<sub>i</sub></span> &le; <span class="type">&prod;<sub>j&isin;J</sub>&tau;<sub>j</sub></span>
     *   :&hArr; I=J &and; &forall;i&isin;I <span class="type">&sigma;<sub>i</sub></span> &le; <span class="type">&tau;<sub>i</sub></span>
     * </div>
     * </p>
     * @param components the components <span class="type">&tau;<sub>i</sub></span> of the product type.
     * @xxx what's the relationship of the tuple product type and the product type &Pi;x:s.t?
     * @xxx and to the sum type to infimum type?
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
	case 0: return NOTYPE; //@xxx was ABSURD; but we need to result in a nonmap type for 0-arity functions. (resolution-fol)
	case 1: return components[0];
	default: return new ProductType(components);
	}
    }
    /**
     * product: <span class="type">*&times;...&times;* &rarr; *</span>; <big>(</big><span class="type">&tau;<sub>i</sub></span><big>)</big> &#8614; <span class="type">&tau;<sub>1</sub>&times;&#8230;&times;&tau;<sub>n</sub></span>.
     * <p>
     * The product type constructor.
     * </p>
     * @see #product(Type[])
     */
    public static final Function/*<Type[],Type>*/ product = new Function/*<Type[],Type>*/() {
	    private final Type logicalTypeDeclaration = null;
	    public Object apply(Object t) {
		return Types.product((Type[])t);
	    }
	    public String toString() {
		return "*";
	    }
	};

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

	public Functor getCompositor() {
	    return product;
	}
	public Object getComponent() {
	    return components;
	}
	public void setCompositor(Functor compositor) {
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
	    } else if (tau.equals(UNIVERSAL))
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


    /**
     * Get the infimum type <span class="type">&#8898;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&cap;&#8230;&cap;&tau;<sub>n</sub></span>.
     * inf is the most general common subtype, also called intersection.
     * <p>
     * The subtype relation of types extends to infimum types as follows
     * <div>
     *   <span class="type">&#8898;<sub>i&isin;I</sub>&sigma;<sub>i</sub></span> &le; <span class="type">&tau;</span>
     *   :&hArr; &exist;i&isin;I <span class="type">&sigma;<sub>i</sub></span> &le; <span class="type">&tau;</span>
     * </div>
     * <div>
     *   <span class="type">&tau;</span> &le; <span class="type">&#8898;<sub>i&isin;I</sub>&sigma;<sub>i</sub></span>
     *   :&hArr; &forall;i&isin;I <span class="type">&tau;</span> &le; <span class="type">&sigma;<sub>i</sub></span>
     * </div>
     * provided that <span class="type">&tau;</span> is not again an infimum type nor contains one.
     * </p>
     * @attribute neutral {@link #UNIVERSAL <span class="type">&#8868;</span>}
     * @attribute associative
     * @attribute distributive {@link #sup}
     * @attribute 
     *   <span class="type">&sigma;</span> &le; <span class="type">&tau;</span>
     *   &rArr; <span class="type">&sigma;&cap;&tau;</span> = <span class="type">&sigma;</span>
     */
    public static final Type inf(Type components[]) {
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
	case 0: return UNIVERSAL;
	case 1: return components[0];
	default: return new InfimumType(components);
	}
    }

    /**
     * inf: <span class="type">*&times;...&times;* &rarr; *</span>; <big>(</big><span class="type">&tau;<sub>i</sub></span><big>)</big> &#8614; <span class="type">&#8898;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&cap;&#8230;&cap;&tau;<sub>n</sub></span>.
     * <p>
     * The infimum type constructor.
     * </p>
     * @see #inf(Type[])
     */
    public static final Function/*<Type[],Type>*/ inf = new Function/*<Type[],Type>*/() {
	    private final Type logicalTypeDeclaration = null; //@xxx
	    public Object apply(Object t) {
		return Types.inf((Type[])t);
	    }
	    public String toString() {
		return "&";
	    }
	};

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

	public Functor getCompositor() {
	    return inf;
	}
	public Object getComponent() {
	    return components;
	}
	public void setCompositor(Functor compositor) {
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

    /**
     * Get the supremum type <span class="type">&#8899;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&cup;&#8230;&cup;&tau;<sub>n</sub></span>.
     * sup is the most special common supertype, also called union.
     * <p>
     * The subtype relation of types extends to supremum types as follows
     * <div>
     *   <span class="type">&#8899;<sub>i&isin;I</sub>&sigma;<sub>i</sub></span> &le; <span class="type">&tau;</span>
     *   :&hArr; &forall;i&isin;I <span class="type">&sigma;<sub>i</sub></span> &le; <span class="type">&tau;</span>
     * </div>
     * <div>
     *   <span class="type">&#8899;<sub>i&isin;I</sub>&sigma;<sub>i</sub></span> &ge; <span class="type">&tau;</span>
     *   :&hArr; &exist;i&isin;I <span class="type">&sigma;<sub>i</sub></span> &ge; <span class="type">&tau;</span>
     * </div>
     * provided that <span class="type">&tau;</span> is not again a supremum type nor contains one.
     * </p>
     * @attribute neutral {@link #ABSURD <span class="type">&perp;</span>}
     * @attribute associative
     * @attribute distributive {@link #inf}
     * @attribute 
     *   <span class="type">&sigma;</span> &le; <span class="type">&tau;</span>
     *   &rArr; <span class="type">&sigma;&cap;&tau;</span> = <span class="type">&tau;</span>
     * @todo what's this property called? somewhat like absorption?
     * @todo strict or ignore absurd?
     */
    public static final Type sup(Type components[]) {
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
	case 0: return ABSURD;
	case 1: return components[0];
	default: return new SupremumType(components);
	}
    }

    /**
     * sup: <span class="type">*&times;...&times;* &rarr; *</span>; <big>(</big><span class="type">&tau;<sub>i</sub></span><big>)</big> &#8614; <span class="type">&#8899;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&cup;&#8230;&cup;&tau;<sub>n</sub></span>.
     * <p>
     * The supremum type constructor.
     * </p>
     * @see #sup(Type[])
     */
    public static final Function/*<Type[],Type>*/ sup = new Function/*<Type[],Type>*/() {
	    private final Type logicalTypeDeclaration = null;
	    public Object apply(Object t) {
		return Types.sup((Type[])t);
	    }
	    public String toString() {
		return "|";
	    }
	};

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

	public Functor getCompositor() {
	    return sup;
	}
	public Object getComponent() {
	    return components;
	}
	public void setCompositor(Functor compositor) {
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
    
    /**
     * The general collection type <span class="type">collection(&tau;)</span>.
     * This is the supertype for all collections in the sense of mereology.
     * The subtype relation extends to collection types as follows
     * <div>
     *   <span class="type">c(&tau;)</span> &le; <span class="type">c'(&tau;')</span>
     *   :&hArr; <span class="type">c</span> &le; <span class="type">c'</span> &and; <span class="type">&tau;</span> &le; <span class="type">&tau;'</span>
     *   <br />
     *   where for <span class="type">c</span> the subtype relations are<br />
     *   collection &le; list, collection &le; set, collection &le; bag
     * </div>
     * @see #list(Type)
     * @see #set(Type)
     * @see #bag(Type)
     */
    public static final Type collection(Type element) {
	return new CollectionType(Collection.class, element, "collection(", ")");
    }
    /**
     * collection: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">collection(&tau;)</span>.
     * <p>
     * The collection type constructor.
     * </p>
     * @see #collection(Type)
     */
    public static final Function/*<Type,Type>*/ collection = new Function() {
	    private final Type logicalTypeDeclaration = Types.map(Types.TYPE, Types.TYPE);
	    public Object apply(Object o) {
		return Types.collection((Type)o);
	    }
	    public String toString() {
		return "collection";
	    }
	};
    /**
     * The set type <span class="type">{&tau;}</span>.
     * Sets are orderless and contain unique elements.
     * <p id="extension">
     * Sets and predicates are different, i.e. <span class="type">{&tau;}</span> &ne; <span class="type">(&sigma;)</span>,
     * because even though extensionally predicates correspond bijectively to sets,
     * they may differ intensionally.
     *  <table>
     *    <tr>
     *      <td class="nameOfMap" rowspan="3"><span class="function">&delta;</span></td>
     *      <td class="leftOfMap"><span class="set">predicate</span></td>
     *      <td class="arrowOfMap">&rarr;&#771;</td>
     *      <td class="rightOfMap"><span class="set">set</span></td>
     *    </tr>
     *    <tr>
     *      <td class="leftOfMap"><span class="predicate">&rho;</span></td>
     *      <td class="arrowOfMap">&#8614;</td>
     *      <td class="rightOfMap">{x &brvbar; <span class="predicate">&rho;</span>(x)}</td>
     *    </tr>
     *    <tr>
     *      <td class="leftOfMap">_&isin;<span class="set">M</span></td>
     *      <td class="arrowOfMap">&#8612;</td>
     *      <td class="rightOfMap"><span class="set">M</span></td>
     *    </tr>
     *  </table>
     * Also see <a href="Interpretation.html#interpretation">identification with characteristic function</a>.
     * </p>
     * @see #collection(Type)
     * @see java.util.Set
     */
    public static final Type set(Type element) {
	return new CollectionType(Set.class, element, "{", "}");
    }
    /**
     * set: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">{&tau;}</span>.
     * <p>
     * The set type constructor.
     * </p>
     * @see #set(Type)
     */
    public static final Function/*<Type,Type>*/ set = new Function() {
	    private final Type logicalTypeDeclaration = Types.map(Types.TYPE, Types.TYPE);
	    public Object apply(Object o) {
		return Types.set((Type)o);
	    }
	    public String toString() {
		return "set";
	    }
	};
    /**
     * The list type <span class="type">&lang;&tau;&rang;</span>.
     * Lists are ordered (but not sorted) and not limited to containing unique elements.
     * @see #collection(Type)
     * @see java.util.List
     */
    public static final Type list(Type element) {
	return new CollectionType(List.class, element, "<", ">");
    }
    /**
     * list: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">&lang;&tau;&rang;</span>.
     * <p>
     * The list type constructor.
     * </p>
     * @see #list(Type)
     */
    public static final Function/*<Type,Type>*/ list = new Function() {
	    private final Type logicalTypeDeclaration = Types.map(Types.TYPE, Types.TYPE);
	    public Object apply(Object o) {
		return Types.list((Type)o);
	    }
	    public String toString() {
		return "list";
	    }
	};
    /**
     * The bag/multiset type <span class="type">&#12308;&tau;&#12309;</span>.
     * Bags are orderless and not limited to containing unique elements.
     * So each element of a bag has a certain multiplicity.
     * @see #collection(Type)
     */
    public static final Type bag(Type element) {
	throw new UnsupportedOperationException("bag interface is not part of Java 1.4");
    }
    /**
     * bag: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">&#12308;&tau;&#12309;</span>.
     * <p>
     * The bag type constructor.
     * </p>
     * @see #bag(Type)
     */
    public static final Function/*<Type,Type>*/ bag = new Function() {
	    private final Type logicalTypeDeclaration = Types.map(Types.TYPE, Types.TYPE);
	    public Object apply(Object o) {
		return Types.bag((Type)o);
	    }
	    public String toString() {
		return "bag";
	    }
	};

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
	    return new ProductType(new Type[] {Types.objectType(collection), element});
	}
    }

    
    
    // Stuff

    //@xxx decide acccessibility (privatize or package-level protect if possible)
    
    /**
     * Get the number of components n of a product type <span class="type">&prod;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&times;&#8230;&times;&tau;<sub>n</sub></span>.
     * @todo rename
     * @todo improve concept to make product accessible (perhaps already in type?) or package-level-protectize
     */
    static final int arityOf(Type type) {
	return type == ABSURD
	    // strict
	    ? Integer.MIN_VALUE
	    : type.equals(NOTYPE)
	    ? 0
	    : type instanceof ProductType
	    ? ((Type[]) ((ProductType)type).getComponent() ).length
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
	    return NOTYPE;
	final Type argumentTypes[] = new Type[args.length];
	for (int i = 0; i < argumentTypes.length; i++)
	    argumentTypes[i] = args[i].getType();
	return product(argumentTypes);
    }

    /**
     * Converts a functor specification to a type (guesses it from the declared type information).
     * (experimental)
     * @xxx we cannot know that ClassicalLogic & Co implement AND as a BinaryFunction, not as a BinaryPredicate<Boolean,Boolean>
     */
    private static final Type declaredTypeOf(Functor.Specification spec) {
	return map(typeOf(spec.getParameterTypes()), objectType(spec.getReturnType()));
    }

    /**
     * Converts a functor specification to a type (guesses it from the declared type information).
     * Also looks for additional declarations of logical type.
     * (experimental)
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
	    return NOTYPE;
	final Type argumentTypes[] = new Type[args.length];
	for (int i = 0; i < argumentTypes.length; i++)
	    argumentTypes[i] = objectType(args[i]);
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
	if (f instanceof Type)
	    return Types.TYPE;
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
     * This implementation compares for arity in favor of domain-type in favor of codomain-type.
     * </p>
     * @see orbital.logic.functor.Functor.Specification#compareTo(Object)
     * @xxx rename, this does not have anything to do with lexicographic
     */
    static final Comparator LEXICOGRAPHIC = new Comparator() {
	    public int compare(Object a, Object b) {
		return compare((Type)a, (Type)b);
	    }
	    private final int compare(Type a, Type b) {
		int order = arityOf(a) - arityOf(b);
		if (order != 0)
		    return order;
		if (a == UNIVERSAL || b == UNIVERSAL)
		    return a == UNIVERSAL && b == UNIVERSAL ? 0 : a == UNIVERSAL ? 1 : -1;
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
}// Types
