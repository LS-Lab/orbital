/**
 * @(#)Type.java 1.1 2002-09-08 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.type;

import orbital.logic.functor.Predicate;

/**
 * Representation of a type.
 * <p id="type">
 * The types form a <a href="../Expression.html#freeAlgebraOfTerms">free algebra of terms</a>
 * &Tau; over {<span class="type">&iota;</span>,<span class="type">&rarr;</span>,<span class="type">()</span>}
 * plus perhaps <big>{</big><span class="type">&times;</span>,<span class="type">&cap;</span>,<span class="type">&cup;</span>,<span class="type">{}</span>,<span class="type">&lang;&rang;</span>,<span class="type">&#12308;&#12309;</span><big>}</big>.
 * Intuitively, {@link Types#INDIVIDUAL <span class="type">&iota;</span>} is the type for individuals,
 * {@link Types#map(Type,Type) <span class="type">&sigma;&rarr;&tau;</span>} the type for maps from <span class="type">&sigma;</span> to <span class="type">&tau;</span>,
 * and {@link Types#predicate(Type) <span class="type">(&sigma;)</span> = <span class="type">&sigma;&rarr;&omicron;</span>} the type for predicates of <span class="type">&sigma;</span>,
 * likewise {@link Types#TRUTH <span class="type">&omicron;</span> = <span class="type">()</span>} is the type of truth-values.
 * A type denotes the set of possible values for a quantity.
 * But there is also a precise <a href="../../imp/Interpretation.html#interpretation">semantics</a>.
 * </p>
 * <p>
 * <h5>Need for this class</h5>
 * Unfortunately, without creating classes for every combination of types,
 * Java class objects cannot represent types like
 * <span class="type">(&sigma;&rarr;&sigma;')&rarr;(&tau;&rarr;&tau;')</span>
 * even though they occur quite naturally in unifying approaches of expressions.
 * Additionally, the Java language specification disallows covariant return-types,
 * such that natural subtypes cannot be modelled with class inheritance.
 * Also, the Java Generics do not yet provide access to template parameter types
 * at runtime.
 * Therefore, this class needed being introduced as a supplement for strict typing information
 * at runtime.
 * </p>
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @see TypeSystem
 * @see Typed#getType()
 * @see java.lang.Class
 * @xxx some things have generic/parametric types like
 *  &forall; : (&Delta;&tau;.(&tau;&rarr;&omicron;)&rarr;&omicron;
 * @todo decouple from the type system such that a flexible type system can even be built at runtime. for example with pure type system?
 * @todo would Types.sup fulfill "Type balance(Type a, Type b)", or would we need another method?
 * @todo should we provide "TypeSystem getTypeSystem()"?
 * @internal note that, logically speaking, types have arity 0 but their corresponding predicative effect of course has arity 1. Both are useful.
 */
public interface Type extends Comparable, Predicate {
    /**
     * Checks two types for equality.
     * By antisymmetry, two types are equal if they are mutual subtypes of each other.
     * <div>
     *   <span class="type">&sigma;&rarr;&tau;</span> equals <span class="type">&sigma;'&rarr;&tau;'</span>
     *   :&hArr; <span class="type">&sigma;</span> = <span class="type">&sigma;'</span> &and; <span class="type">&tau;</span> = <span class="type">&tau;'</span>.
     * </div>
     * @todo assure canonical equality?
     */
    boolean equals(Object o);

    int hashCode();

    /**
     * Returns a string representation of this type.
     */
    String toString();
    
    /**
     * Get the domain <span class="type">&sigma;</span> of a type <span class="type">&sigma;&rarr;&tau;</span>.
     * @return the type of the parameter domain.
     *  {@link TypeSystem#NOTYPE()} if this type does not take parameters.
     */
    Type domain();

    /**
     * Get the co-domain <span class="type">&tau;</span> of a type <span class="type">&sigma;&rarr;&tau;</span>.
     * @return the type of the result value co-domain.
     */
    Type codomain();

    /**
     * Get the type-system that this type stems from.
     * @return the type-system that created this type.
     */
    TypeSystem typeSystem();

    /**
     * Compares two types for subtype inclusions.
     * Defines the subtype relation on types.
     * Note that this only is a <a href="../../functor/doc-files/Relations.html#PartialOrder">partial order</a>,
     * but it is still consistent with equals (thus not only a quasi-order).
     * @preconditions tau instanceof Type
     * @param tau the type <span class="type">&tau;</span> to check for being a supertype, subtype of us, or equals.
     * @return Returns a value &lt; 0 if this &lt; <span class="type">&tau;</span> (this is a proper subtype of <span class="type">&tau;</span>).<br />
     *  Returns a value &gt; 0 if this &gt; <span class="type">&tau;</span> (this is a proper supertype of <span class="type">&tau;</span>).<br />
     *  Returns 0 if this = <span class="type">&tau;</span> (which is the case if and only if this &le; <span class="type">&tau;</span> and this &ge; <span class="type">&tau;</span>).
     * @throws IncomparableException if the types are incomparable.
     * @see #subtypeOf(Type)
     * @see TypeSystem#inf()
     * @see TypeSystem#sup()
     */
    int compareTo(Object tau);
    /**
     * Checks whether this type is a subtype of tau. <span
     * class="type">&sigma;</span> is a <dfn>subtype</dfn> of <span
     * class="type">&tau;</span>, written <span
     * class="type">&sigma;</span> &le; <span
     * class="type">&tau;</span>, if in every context using values of
     * type <span class="type">&tau;</span>, any value of type <span
     * class="type">&sigma;</span> would be permitted as well.
     * Convenience method.
     * @return whether this &le; <span class="type">&tau;</span>.
     *  Especially returns <span class="boolean">false</span> in case of incomparable types.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     * @see #compareTo(Object)
     * @see java.lang.Class#isAssignableFrom(java.lang.Class)
     */
    boolean subtypeOf(Type tau);

    /**
     * Applies this type on sigma returning the resulting type.
     * <p>
     * Applying objects of type <span class="type">&tau;</span>&isin;&Tau; on
     * objects of type <span class="type">&sigma;</span>&isin;&Tau; gives objects of
     * type <span class="type">&tau;</span>&#8728;<span class="type">&sigma;</span>&isin;&Tau;.
     * For example,
     * <div>(<span class="type">&sigma;&rarr;&tau;</span>)&#8728;<span class="type">&sigma;</span> = <span class="type">&tau;</span></div>
     * </p>
     * @param sigma the type <span class="type">&sigma;</span> that this type is applied on.
     * @return this type applied on sigma, i.e. the type
     *  <span class="type">&tau;</span>&#8728;<span class="type">&sigma;</span>
     *  all objects f(x) will have if f:<span class="type">&tau;</span>
     *  and x:<span class="type">&sigma;</span>.
     * @throws TypeException if <span class="type">&tau;</span>&#8728;<span class="type">&sigma;</span>
     *  is undefined, i.e. objects of this type <span class="type">&tau;</span>
     *  cannot be applied to objects of type <span class="type">&sigma;</span>.
     * @todo rename, clarify documentation
     */
    Type on(Type sigma);

    /**
     * Checks whether an object x is an instance of this type.
     * Applies the type identifier predicate belonging to this type.
     * <p>
     * The type identifier predicate belonging to the type <span class="type">&tau;</span>, is:
     * <div><span class="type">&tau;</span>(x) &hArr; x is an instance of <span class="type">&tau;</span></div>
     * The extension, &delta;<span class="type">&tau;</span>, of this predicate is the set of all
     * actually existing elements of type <span class="type">&tau;</span>.
     * </p>
     * @return whether <span class="type">&tau;</span>(x), i.e. x is an instance of this type <span class="type">&tau;</span>.
     * @see java.lang.Class#isInstance(Object)
     * @todo rename, clarify documentation
     * @todo introduce implements Predicate?
     * @todod could perhaps also introduce Set extension(); if we really have a need for it.
     */
    boolean apply(Object x);

    /**
     * The base interface for all composite types that are composed of other types.
     * Type constructs consisting of a type constructor and argument types implement this interface.
     * 
     * @structure is {@link orbital.logic.Composite}&cap;{@link Type}
     * @structure extends Composite
     * @structure extends Type
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    static interface Composite extends orbital.logic.Composite, Type {}

}// Type
