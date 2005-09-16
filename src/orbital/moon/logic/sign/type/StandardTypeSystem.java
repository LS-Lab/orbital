/**
 * @(#)StandardTypeSystem.java 1.1 2003-01-18 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.sign.type;
import orbital.logic.sign.type.Type.Composite;
import orbital.logic.sign.type.*;

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
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Set;
import java.util.List;

import orbital.logic.sign.concrete.Notation;

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
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @see Type
 */
public class StandardTypeSystem implements TypeSystem {
    /**
     * The characters used for grouping type expressions.
     */
    private static final char GROUPING_BRACKET_OPEN = '[';
    private static final char GROUPING_BRACKET_CLOSE = ']';
    //@internal tricky: we have to make sure the (static) initialization (which uses mymap...) can run before typeSystem is set. Java does not truely care about the static initialization order.
    private static final StandardTypeSystem typeSystem = new StandardTypeSystem();
    /**
     * Checks whether something is a <dfn>kind</dfn> k:&#9633;.
     * (does contain meta-types as well).
     * @todo if this is good, move to TypeSystem.
     * @xxx is the terminology ok?
     * @internal Functionals.banana(...)
     */
    public static final Predicate kind = new Predicate() {
            public boolean apply(Object expression) {
                if (expression instanceof Type.Composite) {
                    Type.Composite c = (Type.Composite)expression;
                    //@internal see import orbital.logic.sign.concrete.Notation.compositorTree(...)
                    Object     compositor = c.getCompositor();
                    Collection components = Utility.asCollection(c.getComponent());
                    if (components == null)
                        throw new NullPointerException(c + " of " + c.getClass() + " has compositor " + compositor + " and components " + components);
                    return apply(compositor) || Setops.some(components, this);
                } else
                    return Utility.isIteratable(expression)
                        ? Setops.some(Utility.asIterator(expression), this)
                        : typeSystem.TYPE().equals(expression);
            }
        };
    
    /**
     * @see Type#compareTo(Object)
     * @see StandardTypeSystem.TypeObject#compareToSemiImpl(Type)
     * @todo 19 optimize this hotspot during proving??
     */
    private static final Comparator subtypeOrder = new Comparator() {
            public int compare(Object o1, Object o2) {
                if (!(o1 instanceof TypeObject && o2 instanceof TypeObject))
                    return ((Type)o1).compareTo((Type)o2);
                TypeObject a = (TypeObject)o1;
                TypeObject b = (TypeObject)o2;
                if (a.comparisonPriority() >= b.comparisonPriority())
                    return +a.compareToSemiImpl(b);
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
    private static final BinaryPredicate/*<Type,Type>*/ supertypeOf = Functionals.swap(subtypeOf);

    //@internal tricky: we have to make sure the initialization runs in precisely the right order. Java does not truely care about the initialization order.
    private static final BinaryFunction/*<Type,Type,Type>*/ _map;
    private static final Function/*<Type,Type>*/ _predicate;
    private static final Function/*<Type[],Type>*/ _product;
    private static final Function/*<Type[],Type>*/ _inf;
    private static final Function/*<Type[],Type>*/ _sup;
    private static final CollectionTypeConstructor _collection;
    private static final CollectionTypeConstructor _set;
    private static final CollectionTypeConstructor _list;
    private static final Function/*<Type,Type>*/ _bag;
    
    
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
        public final int lexicographicCompareToTie(Type b) {
            assert b == this : "only one single universal type exists";
            return 0;
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
                throw new IncomparableException("incomparable types", this, b);
        }
        public final int lexicographicCompareToTie(Type b) {
            assert b == this : "only one single universal meta-type exists";
            return 0;
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
            return Integer.MAX_VALUE - 11;
        }
        public final int compareToSemiImpl(Type b) {
            return this == b ? 0 : -1;
        }
        public final int lexicographicCompareToTie(Type b) {
            assert b == this : "only one single absurd type exists";
            return 0;
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
     * The root class for type implementations.
     * @author Andr&eacute; Platzer
     * @version $Id$
     */
    private static abstract class TypeObject implements Type, Serializable {
        private static final long serialVersionUID = 3881468737970732586L;
        private static final Type logicalTypeDeclaration = _TYPE;
        protected TypeObject() {}

        public final TypeSystem typeSystem() {
            return typeSystem;
        }
        
        /**
         * The priority of the comparison rule implemented in {@link #compareToImpl(Type)}.
         * Higher values are for higher priorities.
         * This method induces a total order on the individual comparison rules and decides
         * which rule is applied.
         * This way we achieve a prioritized rule-based system for comparison rules.
         * @note For simple lexicographical comparison, comparisonPriorities are assumed distinct.
         * @postconditions RES==OLD(RES) && RES>=0
         */
        protected abstract int comparisonPriority();
        /**
         * Semi-implementation of {@link Type#compareTo(Object)}.
         * Called from {@link StandardTypeSystem#subtypeOrder} with both orders of arguments.
         * by types how do not always know themselves
         * whether they are a subtype or supertype of another type unknown to them.
         * @xxx adapt documentation to actual implementation
         */
        protected abstract int compareToSemiImpl(Type b);

        public final int compareTo(Object b) {
            if (!(b instanceof TypeObject)) {
                if (b == null)
                    throw new NullPointerException("illegal type " + b + " to compare " + this + " of " + getClass() + " with");
                else {
                    if (this instanceof TypeObject)
                        //@internal should always be satisfied, but to make it clear we do not end up in an infinite recursion.
                        return -((Type)b).compareTo(this);
                    else
                        throw new InternalError("no rule for comparing\n type " + this + " of " + getClass() + "\n with " + b + " of " + b.getClass());
                }
            }
            int cmp = subtypeOrder.compare(this, (Type)b);
            assert !((cmp == 0) ^ this.equals(b)) : "antisymmetry resp. consistent with equals: " + this + ", " + b + "\n" + this + toString(cmp) + b + "\n" + this + (this.equals(b) ? "=" : "!=") + b;
            assert subtypeOrder.compare(this, this) == 0 : "reflexive: " + this;
            assert MathUtilities.sign(cmp) == -MathUtilities.sign(subtypeOrder.compare((Type)b, this)) : "antisymmetry: " + this + ", " + b;

            //@internal we cannot assert this, here, since that would result in an infinite recursion of compareTo.
            //assert (cmp <= 0) == typeSystem().sup(new Type[] {this, (Type)b}).equals(b) : "TypeSystem.sup@postconditions " + this + "=<" + b + " iff " + typeSystem.sup(new Type[] {this, (Type)b}) + " = " + this + " sup " + b + " = " + b;
            //assert (cmp >= 0) == typeSystem().sup(new Type[] {(Type)b, this}).equals(b) : "TypeSystem.sup@postconditions " + b + "=<" + this + " iff " + typeSystem.sup(new Type[] {(Type)b, this}) + " = " + b + " sup " + this + " = " + this;
            //assert (cmp <= 0) == typeSystem().inf(new Type[] {this, (Type)b}).equals(b) : "TypeSystem.inf@postconditions " + this + "=<" + b + " iff " + typeSystem.inf(new Type[] {this, (Type)b}) + " = " + this + " inf " + b + " = " + this;
            //assert (cmp >= 0) == typeSystem().inf(new Type[] {(Type)b, this}).equals(b) : "TypeSystem.inf@postconditions " + b + "=<" + this + " iff " + typeSystem.inf(new Type[] {(Type)b, this}) + " = " + b + " inf " + this + " = " + b;
            return cmp;
        }

        /**
         * Semi-implementation of {@link Type#compareTo(Object)} for
         * lexicographical order (unlike subtype order).  Called from
         * {@link StandardTypeSystem#LEXICOGRAPHIC} in case comparison
         * priority does not impose an order.
         * @preconditions comparisonPriority() = ((TypeObject)b).comparisonPriority()
         */
        protected abstract int lexicographicCompareToTie(Type b);

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
     * Root class for composite types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     */
    private static abstract class AbstractCompositeType extends TypeObject implements Type.Composite {
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
    }
    
    /**
     * Basic implementation of map-like Types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @internal this implementation also makes sense for non-composite types, but single inheritance prevents this.
     */
    private static abstract class MapTypeBase extends TypeObject { /////AbstractCompositeType {
        private static final long serialVersionUID = -5981946381802191256L;
        protected MapTypeBase() {}
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
            final int doc = domain().compareTo(b.domain());
            final int coc = codomain().compareTo(b.codomain());
            if (coc == 0 && doc == 0)
                return 0;
            else if ((doc >= 0 && coc <= 0))
                return -1;
            else if (doc <= 0 && coc >= 0)
                return 1;
            else
                throw new IncomparableException("incomparable types", this, b);
        }

        public int lexicographicCompareToTie(Type b) {
            // compare for domain in favor of codomain
            int order = LEXICOGRAPHIC.compare(domain(), b.domain());
            if (order != 0)
                return order;
            else
                return LEXICOGRAPHIC.compare(codomain(), b.codomain());
        }

        public Type on(Type sigma) {
            if (sigma == typeSystem().ABSURD()) {
                // handle special sub-case first, since we do not yet support absurd maps for the check
                if (!sigma.subtypeOf(domain())) {
                    throw new TypeException("objects of type " + this + " not applicable to arguments of type " + sigma, domain(), sigma);
                } else {
                    return codomain();
                }
            } else if (!subtypeOf(typeSystem().map(sigma, typeSystem().UNIVERSAL()))) {
                throw new TypeException("objects of type " + this + " not applicable to arguments of type " + sigma, domain(), sigma);
            } else {
                return codomain();
            }
        }
    }

    /**
     * Base class for (composite) non-map types i.e. with void domain.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @see StandardTypeSystem.MapType
     */
    private static abstract class NonMapCompositeType extends AbstractCompositeType {
        private static final long serialVersionUID = -6241523127417780697L;
        public Type domain() {
            return typeSystem.NOTYPE();
        }
        public Type codomain() {
            return this;
        }

        public Type on(Type sigma) {
            if (sigma == typeSystem().NOTYPE()) {
                // since NOTYPE->t = t by uniform referent, we can be applied to empty objects of void type.
                return this;
            } else {
                throw new TypeException("non-map composite types cannot be applied to anything, not even arguments of type " + sigma, typeSystem().NOTYPE(), sigma);
            }
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
     * @version $Id$
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
            throw new IncomparableException("incomparable types", this, b);
        }
        public int lexicographicCompareToTie(Type b) {
            // lexicographic compare of names
            return this.getFundamental().getName().compareTo(((FundamentalType)b).getFundamental().getName());
        }

        public boolean apply(Object x) {
            //@xxx check that all tests are really correct. What's up with VoidFunction, and Function<Object,Boolean> etc?
            // if (x instanceof Functor && spec.isConform((Functor) x)) return true;
            return Functionals.bindSecond(Utility.instanceOf, getFundamental()).apply(x);
        }

        public Type on(Type sigma) {
            if (sigma == typeSystem().NOTYPE()) {
                // since NOTYPE->t = t by uniform referent, we can be applied to empty objects of void type.
                return this;
            } else {
                throw new TypeException("fundamental types cannot be applied to anything, not even arguments of type " + sigma, typeSystem().NOTYPE(), sigma);
            }
        }

        //@internal identical to @see NonMapCompositeType
        public Type domain() {
            return typeSystem.NOTYPE();
        }
        public Type codomain() {
            return this;
        }
    }

    /**
     * Implementation of fundamental types already available at the language level ({@link java.lang.Class}).
     * @author Andr&eacute; Platzer
     * @version $Id$
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

        protected final int comparisonPriority() {
            return 50;
        }
    }

    // special type factories

    /**
     * Creates a new special type. Special types <span
     * class="type">&tau;</span> are distinguished by their signifier.
     * Their only type hierarchy relations are 
     * <span class="type">&perp;</span>&le;<span class="type">&tau;</span>&le;<span class="type">&#8868;</span>.
     * @postconditions forall s ((RES.equals(s) &hArr; s=specialType(signifier))
     *   &and; (RES.compareTo(s) < 0 &hArr; s=UNIVERSAL())
     *   &and; (RES.compareTo(s) > 0 &hArr; s=ABSURD()))
     */
    public Type specialType(String signifier) {
        return new SpecialType(signifier);
    }

    /**
     * Implementation of special types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     */
    private static class SpecialType extends TypeObject {
        //private static final long serialVersionUID = 0;
        private final String signifier;
        public SpecialType(String signifier) {
            if (signifier == null)
                throw new NullPointerException("illegal type name " + signifier);
            this.signifier = signifier;
        }
        public String toString() {
            return "`" + signifier + "�";
        }
        public boolean equals(Object o) {
            return (o instanceof SpecialType)
                && signifier.equals(((SpecialType)o).signifier);
        }

        public int hashCode() {
            return signifier.hashCode();
        }

        public String getSignifier() {
            return signifier;
        }


        protected int comparisonPriority() {
            return 10070;
        }
        public int compareToSemiImpl(Type b) {
            if (this.equals(b)) {
                return 0;
            } else {
                throw new IncomparableException("incomparable types", this, b);
            }
        }
        public final int lexicographicCompareToTie(Type b) {
            return this.getSignifier().compareTo(((SpecialType)b).getSignifier());
        }

        public boolean apply(Object x) {
            throw new UnsupportedOperationException("special types do not support membership checks");
        }

        public Type on(Type sigma) {
            if (sigma == typeSystem().NOTYPE()) {
                // since NOTYPE->t = t by uniform referent, we can be applied to empty objects of void type.
                return this;
            } else {
                throw new TypeException("special types cannot be applied to anything, not even arguments of type " + sigma, typeSystem().NOTYPE(), sigma);
            }
        }

        //@internal identical to @see NonMapCompositeType
        public Type domain() {
            return typeSystem.NOTYPE();
        }
        public Type codomain() {
            return this;
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
            throw new UnsupportedOperationException(_ABSURD + " maps from absurdity to anywhere not yet supported");
        return domain.equals(_NOTYPE)
            //@internal because supplying nothing when nothing is needed, seems simple
            ? codomain
            : new MapType(domain, codomain);
    }
    public BinaryFunction/*<Type,Type,Type>*/ map() {
        return _map;
    };

    public Type predicate(Type domain) {
        return map(domain, TRUTH);
    }
    public final Function/*<Type,Type>*/ predicate() {
        return _predicate;
    }

    /**
     * Implementation of map types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @see StandardTypeSystem.NonMapType
     * @todo extend AbstractCompositeType, or at least Composite
     */
    private static final class MapType extends MapTypeBase implements Type.Composite {
        private static final long serialVersionUID = 9148024444083534208L;
        private final Type dom;
        private final Type codom;
        public MapType(Type domain, Type codomain) {
            this.dom = domain;
            this.codom = codomain;
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

        public Object getCompositor() {
            return typeSystem.map();
        }
        public Object getComponent() {
            return new Type[] {domain(), codomain()};
        }
        public void setCompositor(Object compositor) {
            if (compositor != getCompositor())
                throw new IllegalArgumentException("special compositor of " + getClass() + " expected");
        }
        public void setComponent(Object component) {
            if (((Object[])component).length != 2)
                throw new IllegalArgumentException();
            throw new UnsupportedOperationException("not yet implemented");
            //this.dom = ((Type[]) component)[0];
            //this.codom = ((Type[]) component)[1];
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
            assert Types.arityOf(dom) > 0 : "map(Type,Type) canonically filters domain=NOTYPE. But the domain of " + this + " of " + getClass() + " has arity " + Types.arityOf(dom);
            //@xxx originally was  referent instanceof Functor && spec.isConform((Functor) referent)
            if (false && Types.arityOf(dom) <= 1)
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
        //@internal also assures strict
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
    public final Function/*<Type[],Type>*/ product() {
        return _product;
    }

    /**
     * Implementation of product types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @internal which interface for product types? The answer may be: nothing particular, since the typical operations should rely on compareTo and equals.
     */
    private static final class ProductType extends NonMapCompositeType {
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
            return Utility.hashCodeArray(components);
        }
        
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(GROUPING_BRACKET_OPEN);
            sb.append(components[0].toString());
            for (int i = 1; i < components.length; i++) {
                sb.append('\u00d7');
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
                    throw new IncomparableException("incomparable types", this, tau);
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
                        throw new IncomparableException("incomparable types", this, tau);
                }
                return cmp;
            } else if (tau.equals(typeSystem.UNIVERSAL()))
                //@todo still needed?
                return 0;
            else
                throw new IncomparableException("incomparable types", this, tau);
        }

        public final int lexicographicCompareToTie(Type b) {
            Type as[] = (Type[]) this.getComponent();
            Type bs[] = (Type[]) ((ProductType)b).getComponent();
            int order = as.length - bs.length;
            if (order != 0)
                return order;
            assert as.length == bs.length : "equal arities means equal number of components";
            for (int i = 0; i < as.length; i++) {
                order = LEXICOGRAPHIC.compare(as[i], bs[i]);
                if (order != 0)
                    return order;
            }
            return 0;
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
        return infImpl(Arrays.asList(components));
    }
    private Type infImpl(List/*<Type>*/ components) {
        //@internal although we still work on components, t keeps the simplified version of components.
        List/*<Type>*/ t = new LinkedList(components);
        //@internal this canonical simplification also assures strict
        for (ListIterator i = components.listIterator(); i.hasNext(); ) {
            Type ti = (Type) i.next();
            if (ti == null)
                throw new NullPointerException("illegal arguments containing " + ti);
            if (ti instanceof InfimumType) {
                // associative
                t.remove(ti);
                t.addAll(Arrays.asList(((InfimumType)ti).components));
                return infImpl(t);
            }
                
            for (Iterator j = components.listIterator(i.previousIndex() + 1); j.hasNext(); ) {
                Type tj = (Type) j.next();
                try {
                    int cmp = ti.compareTo(tj);
                    // remove redundant supertypes of comparable types in t
                    if (cmp <= 0)
                        t.remove(tj);
                    else if (cmp > 0)
                        t.remove(ti);
                } catch (IncomparableException test) {}
            }
        }
        Type components2[] = (Type[])t.toArray(new Type[0]);
        switch (components2.length) {
        case 0: return typeSystem.UNIVERSAL();
        case 1: return components2[0];
        default: return new InfimumType(components2);
        }
    }

    public final Function/*<Type[],Type>*/ inf() {
        return _inf;
    }

    /**
     * Implementation of infimum types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @todo should we extend AbstractCompositeType?
     * @todo could change internal representation to "LinkedHashSet<Type> components".
     */
    private static final class InfimumType extends NonMapCompositeType {
        private static final long serialVersionUID = -6251593765414274805L;
        private Type components[];
        private InfimumType() {}
        public InfimumType(Type components[]) {
            this.setComponent(components);
        }
        public boolean equals(Object o) {
            // commutative
            return (o instanceof InfimumType) && new HashSet(Arrays.asList(components)).equals(new HashSet(Arrays.asList(((InfimumType)o).components)));
        }
        
        public int hashCode() {
            return 1 ^ Utility.hashCodeSet(components);
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
            return 200-1;
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
                throw new IncomparableException("incomparable types", this, tau);
        }
        private boolean compareSubtypeOf(Type tau) {
            return Setops.some(Arrays.asList(components), Functionals.bindSecond(subtypeOf, tau));
        }
        private boolean compareSupertypeOf(Type tau) {
            return Setops.all(Arrays.asList(components), Functionals.bindFirst(subtypeOf, tau));
        }

        public final int lexicographicCompareToTie(Type b) {
            throw new UnsupportedOperationException("not yet implemented");
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
        return supImpl(Arrays.asList(components));
    }
    private Type supImpl(List/*<Type>*/ components) {
        //@internal although we still work on components, t keeps the simplified version of components.
        List/*<Type>*/ t = new LinkedList(components);
        //@internal this canonical simplification also assures strict
        for (ListIterator i = components.listIterator(); i.hasNext(); ) {
            Type ti = (Type) i.next();
            if (ti == null)
                throw new NullPointerException("illegal arguments containing " + ti);
            if (ti instanceof SupremumType) {
                // associative
                t.remove(ti);
                t.addAll(Arrays.asList(((SupremumType)ti).components));
                return supImpl(t);
            }
            for (Iterator j = components.listIterator(i.previousIndex() + 1); j.hasNext(); ) {
                Type tj = (Type) j.next();
                try {
                    int cmp = ti.compareTo(tj);
                    // remove redundant subtypes of comparable types in t
                    if (cmp <= 0)
                        t.remove(ti);
                    else if (cmp > 0)
                        t.remove(tj);
                } catch (IncomparableException test) {}
            }
        }
        Type components2[] = (Type[])t.toArray(new Type[0]);
        switch (components2.length) {
        case 0: return typeSystem.ABSURD();
        case 1: return components2[0];
        default: return new SupremumType(components2);
        }
    }

    public final Function/*<Type[],Type>*/ sup() {
        return _sup;
    }

    /**
     * Implementation of supremum types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     */
    private static final class SupremumType extends NonMapCompositeType {
        private static final long serialVersionUID = 2673832577121308931L;
        private Type components[];
        private SupremumType() {}
        public SupremumType(Type components[]) {
            this.setComponent(components);
        }
        public boolean equals(Object o) {
            // commutative
            return (o instanceof SupremumType) && new HashSet(Arrays.asList(components)).equals(new HashSet(Arrays.asList(((SupremumType)o).components)));
        }
        
        public int hashCode() {
            return 7 ^ Utility.hashCodeSet(components);
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
                throw new IncomparableException("incomparable types", this, tau);
        }
        private boolean compareSubtypeOf(Type tau) {
            return Setops.all(Arrays.asList(components), Functionals.bindSecond(subtypeOf, tau));
        }
        private boolean compareSupertypeOf(Type tau) {
            return Setops.some(Arrays.asList(components), Functionals.bindFirst(subtypeOf, tau));
        }

        public final int lexicographicCompareToTie(Type b) {
            throw new UnsupportedOperationException("not yet implemented");
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
     * Internal super class for type constructors of collection types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     */
    private static abstract class CollectionTypeConstructor implements Function/*<Type,Type>*/ {
        private final Class collection;
        protected CollectionTypeConstructor(Class collection) {
            this.collection = collection;
        }
        /**
         * Geth the Java class that objects must be an
         * instance of for counting as a collection of our type.
         * @internal could be replaced by boolean isInstance(Object x), which is (almost)
         * the only thing used by CollectionType.
         */
        Class getCollectionClass() {
            return collection;
        }
    }
    
    public Type collection(Type element) {
        return new CollectionType(_collection, element, "collection(", ")");
    }
    public final Function/*<Type,Type>*/ collection() {
        return _collection;
    }
    public Type set(Type element) {
        return new CollectionType(_set, element, "{", "}");
    }
    public final Function/*<Type,Type>*/ set() {
        return _set;
    }
    public final Type list(Type element) {
        return new CollectionType(_list, element, "<", ">");
    }
    public final Function/*<Type,Type>*/ list() {
        return _list;
    }
    public final Type bag(Type element) {
        throw new UnsupportedOperationException("bag interface is not part of Java 1.4");
    }
    public final Function/*<Type,Type>*/ bag() {
        return _bag;
    }

    /**
     * Collection types.
     * @author Andr&eacute; Platzer
     * @version $Id$
     */
    private static class CollectionType extends NonMapCompositeType {
        private static final long serialVersionUID = -1113530540489964295L;
        /**
         * The type constructor that would be required for constructing the type we are.
         */
        private final CollectionTypeConstructor constructor;
        private final Type element;
        private final String toStringPrefix;
        private final String toStringSuffix;
        public CollectionType(CollectionTypeConstructor constructor, Type element, String toStringPrefix, String toStringSuffix) {
            this.constructor = constructor;
            this.element = element;
            this.toStringPrefix = toStringPrefix;
            this.toStringSuffix = toStringSuffix;
            if (constructor == null)
                throw new IllegalArgumentException("illegal constructor null (perhaps wrong static initialization order?)");
        }
        public boolean equals(Object b) {
            if (b instanceof CollectionType) {
                CollectionType tau = (CollectionType)b;
                return constructor.equals(tau.constructor) && element.equals(tau.element);
            } else
                return false;
        }
        public int hashCode() {
            return 13 ^ constructor.hashCode() ^ element.hashCode();
        }
        public String toString() {
            return toStringPrefix + element + toStringSuffix;
        }

        public Object getCompositor() {
            return constructor;
        }
        public Object getComponent() {
            return element;
        }
        public void setCompositor(Object compositor) {
            if (compositor != getCompositor())
                throw new IllegalArgumentException("special compositor of " + getClass() + " expected");
        }
        public void setComponent(Object component) {
            throw new UnsupportedOperationException("not yet implemented");
            //this.element = (Type) component;
        }
        
        protected int comparisonPriority() {
            return 60;
        }
        public int compareToSemiImpl(Type b) {
            if (b instanceof CollectionType) {
                CollectionType tau = (CollectionType)b;
                return comparisonInternalRepresentation().compareTo(tau.comparisonInternalRepresentation());
//              if (!collection.equals(tau.collection))
//                  //@xxx return -1 if collection < tau.collection and element < tau.element etc.
//                  throw new UnsupportedOperationException("comparing different collection types");
//              assert toStringPrefix.equals(tau.toStringPrefix) && toStringSuffix.equals(tau.toStringSuffix) : "equal collection type constructors imply equal notation"; 
//              return element.compareTo(tau.element);
            } else
                throw new IncomparableException("incomparable types", this, b);
        }

        public final int lexicographicCompareToTie(Type b) {
            return LEXICOGRAPHIC.compare(((CollectionType)this).comparisonInternalRepresentation(),
                                   ((CollectionType)b).comparisonInternalRepresentation());
        }

        public boolean apply(Object x) {
            return constructor.getCollectionClass().isInstance(x)
                && Setops.all((Collection)x, element);
        }
        /**
         * @internal collection types and product types are different, but they compare in the same way
         */
        private Type comparisonInternalRepresentation() {
            return new ProductType(new Type[] {typeSystem.objectType(constructor.getCollectionClass()), element});
        }
    }

    
    
    // Stuff

    /**
     * Lexicographic order on types.
     * <p>
     * This implementation compares for arity in favor of domain-type in favor of codomain-type.
     * </p>
     * @see orbital.logic.functor.Functor.Specification#compareTo(Object)
     * @todo 19 optimize this hotspot during proving. Done?
     */
    public static final Comparator LEXICOGRAPHIC = new Comparator() {
            public int compare(Object a, Object b) {
                return compare((Type)a, (Type)b);
            }
            private final int compare(Type a, Type b) {
                if (a instanceof TypeObject && b instanceof TypeObject) {
                    TypeObject ta = (TypeObject)a;
                    TypeObject tb = (TypeObject)b;
                    //@internal we abuse the comparisonPriority for establishing a quick lexicographical order as well. Only types of equal comparisonPriority need a lexicographical comparison routine, then. The implementation is faster, simpler and more reliable than an explicit if-cascade.
                    int order = ta.comparisonPriority() - tb.comparisonPriority();
                    if (order != 0)
                        return order;
                    else
                        return ta.lexicographicCompareToTie(tb);
                }
                
                if (a.equals(b))
                    return 0;

                if (true)
                    return a.toString().compareTo(b.toString());
                throw new IllegalArgumentException("unknown types to compare lexicographically:\n type " + a + " of " + a.getClass() + "\n and  " + b + " of " + b.getClass());
            }
        };


    /**
     * Explicit Initialization Order.  The dependencies are (according
     * to uses in logicalTypeDeclaration initialization, and, perhaps
     * use in list(Type), etc.):
     * <ul>
     *   <li>_map &larr; map, product</li>
     *   <li>_list &larr; map</li>
     *   <li>list(Type) &larr; _list</li>
     *   <li>_product &larr; map, list</li>
     *   <li>_predicate &larr; map</li>
     *   <li>_inf &larr; map, set</li>
     *   <li>_sup &larr; map, set</li>
     *   <li>_collection &larr; map</li>
     *   <li>collection(Type) &larr; _collection</li>
     *   <li>_set &larr; map</li>
     *   <li>set(Type) &larr; _set</li>
     *   <li>_bag &larr; map</li>
     *   <li>bag(Type) &larr; _bag</li>
     *   <li>typeSystem &larr; _*</li>
     * </ul>
     *@internal tricky: we have to make sure the initialization runs in precisely the right order. Java does not truely care about the initialization order.
     */
    static {
        _map = new BinaryFunction/*<Type,Type,Type>*/() {
                private final Type logicalTypeDeclaration = mymap(myproduct(new Type[] {_TYPE,_TYPE}), _TYPE);
                public Object apply(Object s, Object t) {
                    return typeSystem.map((Type)s, (Type)t);
                }
                public String toString() {
                    return "->";
                }
            };
        _list = new CollectionTypeConstructor(List.class) {
                private final Type logicalTypeDeclaration = typeSystem.map(_TYPE, _TYPE);
                public Object apply(Object o) {
                    return typeSystem.list((Type)o);
                }
                public String toString() {
                    return "list";
                }
            };
        _product = new Function/*<Type[],Type>*/() {
                private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.list(_TYPE), _TYPE);
                public Object apply(Object t) {
                    return typeSystem.product(t instanceof Type[]
                                              ? (Type[]) t
                                              : (Type[]) Setops.asList(Utility.asIterator(t)).toArray(new Type[0])
                                              );
                }
                public String toString() {
                    return "\u00d7";
                }
            };

        _collection = new CollectionTypeConstructor(Collection.class) {
                private final Type logicalTypeDeclaration = typeSystem.map(_TYPE, _TYPE);
                public Object apply(Object o) {
                    return typeSystem.collection((Type)o);
                }
                public String toString() {
                    return "collection";
                }
            };
        _set = new CollectionTypeConstructor(Set.class) {
                private final Type logicalTypeDeclaration = typeSystem.map(_TYPE, _TYPE);
                public Object apply(Object o) {
                    return typeSystem.set((Type)o);
                }
                public String toString() {
                    return "set";
                }
            };
        _bag = new Function() {
                private final Type logicalTypeDeclaration = typeSystem.map(_TYPE, _TYPE);
                public Object apply(Object o) {
                    return typeSystem.bag((Type)o);
                }
                public String toString() {
                    return "bag";
                }
            };
        
        _predicate = new Function/*<Type,Type>*/() {
                private final Type logicalTypeDeclaration = typeSystem.map(_TYPE, _TYPE);
                public Object apply(Object t) {
                    return typeSystem.predicate((Type)t);
                }
                public String toString() {
                    return "pred";
                }
            };
        _inf = new Function/*<Type[],Type>*/() {
                private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.set(_TYPE), _TYPE);
                public Object apply(Object t) {
                    return typeSystem.infImpl(Setops.asList(Utility.asIterator(t)));
                }
                public String toString() {
                    return "&";
                }
            };
        _sup = new Function/*<Type[],Type>*/() {
                private final Type logicalTypeDeclaration = typeSystem.map(typeSystem.set(_TYPE), _TYPE);
                public Object apply(Object t) {
                    return typeSystem.supImpl(Setops.asList(Utility.asIterator(t)));
                }
                public String toString() {
                    return "|";
                }
            };
    }   
}// StandardTypeSystem
