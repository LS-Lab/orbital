/**
 * @(#)Type.java 1.1 2002-09-08 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.functor.Predicate;

/**
 * Representation of types.
 * <p id="type">
 * The types form a <a href="Expression.html#freeAlgebraOfTerms">free algebra of terms</a>
 * &Tau; over {<span class="type">&iota;</span>,<span class="type">&rarr;</span>,<span class="type">()</span>}.
 * Intuitively, {@link Types#INDIVIDUAL <span class="type">&iota;</span>} is the type for individuals,
 * {@link Types#map(Type,Type) <span class="type">&sigma;&rarr;&tau;</span>} the type for maps from <span class="type">&sigma;</span> to <span class="type">&tau;</span>,
 * and {@link Types#predicate(Type) <span class="type">(&sigma;)</span>=<span class="type">&sigma;&rarr;&omicron;</span>} the type for predicates of <span class="type">&sigma;</span>,
 * likewise {@link Types#TRUTH <span class="type">&omicron;</span>=<span class="type">()</span>} is the type of truth-values.
 * But there is also a precise <a href="Interpretation.html#interpretation">semantic</a>.
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
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version 1.1, 2002-09-08
 * @see Types
 * @see Symbol#getType()
 * @see Expression#getType()
 * @see java.lang.Class
 * @todo remove type documentation from Symbol, and use Type throughout
 */
public interface Type extends Comparable {
    /**
     * Checks two types for equality.
     * By antisymmetry, two types are equal if they are mutual subtypes of each other.
     * <div>
     *   <span class="type">&sigma;&rarr;&tau;</span> equals <span class="type">&sigma;'&rarr;&tau;'</span>
     *   :&hArr; <span class="type">&sigma;</span>=<span class="type">&sigma;'</span> &and; <span class="type">&tau;</span>=<span class="type">&tau;'</span>.
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
     * Get the codomain <span class="type">&sigma;</span> of a type <span class="type">&sigma;&rarr;&tau;</span>.
     * @return the type of the parameter domain.
     *  {@link Types#VOID} if this type does not take parameters.
     */
    Type codomain();

    /**
     * Get the domain <span class="type">&tau;</span> of a type <span class="type">&sigma;&rarr;&tau;</span>.
     * @return the type of the result value domain.
     */
    Type domain();

    /**
     * Compares two types for subtype inclusions.
     * Note that this is only a partial order, but it is still consistent with equals.
     * <p>
     * The subtype relation of types extends to map types as follows
     * <div>
     *   <span class="type">&sigma;&rarr;&tau;</span> &le; <span class="type">&sigma;'&rarr;&tau;'</span>
     *   :&hArr; <span class="type">&sigma;'</span>&le;<span class="type">&sigma;</span> &and; <span class="type">&tau;</span>&le;<span class="type">&tau;'</span>
     *   &hArr; <span class="type">&sigma;</span>&ge;<span class="type">&sigma;'</span> &and; <span class="type">&tau;</span>&le;<span class="type">&tau;'</span>.
     * </div>
     * This means that functor subtypes have contravariant parameters and covariant return-types.
     * </p>
     * @param tau the type <span class="type">&tau;</span> to check for being a supertype, subtype of us, or equals.
     * @return Returns an x &lt; 0 if this &lt; <span class="type">&tau;</span> (this is a proper subtype of <span class="type">&tau;</span>).
     *  Returns an x &gt; 0 if this &gt; <span class="type">&tau;</span> (this is a proper supertype of <span class="type">&tau;</span>).
     *  Returns 0 if this = <span class="type">&tau;</span> (which is the case if and only if this &le; <span class="type">&tau;</span> and this &ge; <span class="type">&tau;</span>).
     * @throws IncomparableException if the types are incomparable.
     * @see #subtypeOf(Type)
     */
    int compareTo(Object tau);
    /**
     * Checks whether this type is a subtype of tau.
     * @return whether this &le; <span class="type">&tau;</span>.
     *  Especially returns <span class="boolean">false</span> in case of incomparable types.
     * @see #compareTo(Object)
     * @see java.lang.Class#isAssignableFrom(java.lang.Class)
     */
    boolean subtypeOf(Type tau);

    /**
     * Checks whether x is an instance of this type.
     * Applies the type identifier predicate belonging to this type.
     * <p>
     * The type identifier predicate belonging to the type <span class="type">&tau;</span>, is:
     * <div><span class="type">&tau;</span>(x) &hArr; x is an instance of <span class="type">&tau;</span></div>
     * The extension, &delta;<span class="type">&tau;</span>, of this predicate is the set of all
     * actually existing elements of type <span class="type">&tau;</span>.
     * </p>
     * @see java.lang.Class#isInstance(Object)
     * @todo rename, clarify documentation
     * @todo introduce implements Predicate?
     * @todod could perhaps also introduce Set extension(); if we really have a need for it.
     */
    boolean apply(Object x);
}// Type
