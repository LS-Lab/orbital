/**
 * @(#)TypeSystem.java 1.1 2003-01-18 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.type;

import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;
/**
 * Provides type constructors, and factories for types of a
 * type-system. Type constructors create new types depending on some
 * existing types. Factories, instead, create completely new types.
 * For example, there are type constructors for map types.
 * <p>
 * <pre>
 * [A <def>type-system</def> is a] tractable syntactic method for proving the
 * absence of certain program behaviors by classifying phrases
 * according to the kinds of values they compute.<br />
 * (Types and Programming Languages, MIT Press, 2002)
 * </pre>
 * </p>
 *
 * @author <a href="mailto:">Andr&eacute; Platzer</a>
 * @version 1.1, 2003-01-18
 * @note Type constructors with list or set parameters usually also accept them as
 * {@link orbital.util.Utility#isIteratable(Object) generalized iteratable}
 * objects, including but not limited to {@link java.util.List}, {@link java.util.Set}.
 * @see Types#getDefault()
 * @see Type#typeSystem()
 * @see Type
 * @see <a href="http://www.omg.org/technology/documents/formal/mof.htm">Meta-Object Facility</a>
 * @internal &lambda;* with *:* is inconsistent: since every term is inhabited (Girard's paradox).
 * @todo @see "H.P. Barendregt. Lambda Calculi with Types. in: Handbook of"
 */
public interface TypeSystem {

    /**
     * Checks whether two type-systems are equal.
     * Two type-systems are equal if they produce completely compatible types on all
     * operations.
     * <p>
     * Type-system equality will often only depend on the implementation's classes.
     * </p>
     */
    boolean equals(Object o);

    int hashCode();

    // atomic types

    /**
     * The meta-type (kind) of types <span class="type">*</span>:&#9633;.
     * The type <span class="type">*</span> and every type containing <span class="type">*</span>
     * is a kind, with the latter being types for type constructors.
     * <i>Types containing meta-types as well as ordinary types are currently undefined.</i>
     * @postconditions RES == OLD(RES)
     */
    Type TYPE();

    /**
     * The universal type
     * <span class="type">&#8868;</span> = <span class="type">&#8898;<sub>&empty;</sub></span>.
     * It is the top element of the lattice &Tau; of types, has no differentiae and is characterized by
     * <ul>
     *   <li>(&exist;x) <span class="type">&#8868;</span>(x)</li>
     *   <li>(&forall;x) <span class="type">&#8868;</span>(x)</li>
     *   <li>(&forall;<span class="type">&tau;</span>:Type) <span class="type">&tau;</span>&le;<span class="type">&#8868;</span></li>
     * </ul>
     * This would be the only type of uniform type-systems.
     * @postconditions RES == OLD(RES)
     * @see #ABSURD()
     * @internal everything (besides primitive types) is an instance of or a subclass of our fundamental class.
     * @todo how to distinguish fundamental types UNIVERSAL and INDIVIDUAL on the object level?
     */
    Type UNIVERSAL();
    /**
     * The absurd type
     * <span class="type">&perp;</span> = <span class="type">&#8899;<sub>&empty;</sub></span>.
     * It is the bottom element of the lattice &Tau; of types,
     * it cannot be the type of anything that exists, and it is characterized by
     * <ul>
     *   <li>&not;(&exist;x) <span class="type">&perp;</span>(x)</li>
     *   <li>(&forall;<span class="type">&tau;</span>:Type) <span class="type">&perp;</span>&le;<span class="type">&tau;</span></li>
     * </ul>
     * @postconditions RES == OLD(RES)
     * @see #UNIVERSAL()
     * @internal nothing (besides that single object whose reference no one knows) is an instance of or a subclass of a fundamental anonymous class. But the same already goes true for Void.TYPE, so let's use that.
     */
    Type ABSURD();
    /**
     * Not a type.
     * The type of expressions that do not have any type at all.
     * This type has extension &empty;.
     * @postconditions RES == OLD(RES)
     * @see #ABSURD()
     * @xxx is NOTYPE=ABSURD? For product,sup,inf,collection and extension they have are equal.
     *  So at most the condition <li>(&forall;<span class="type">&tau;</span>:Type) <span class="type">&perp;</span>&le;<span class="type">&tau;</span></li> might be different.
     * @internal if we cannot distinguish NOTYPE (as domain of NonMapType) from ABSURD we would make &iota; and &iota;->string comparable.
     *  NOTYPE is incomparable with all types except itself (and of course ABSURD, and UNIVERSAL)
     *  NOTYPE would not be necessary if Type did not prefer half being a MapType and a check predicate for instanceof MapType were available. But this would seem more ugly.
     */
    Type NOTYPE();

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
    public Type objectType(Class type);
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
    public Type objectType(Class type, String signifier);
    
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
     * @param codomain the {@link Type#codomain() co-domain} <span class="type">&tau;</span>.
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
    public Type map(Type domain, Type codomain);
    /**
     * map: <span class="type">*&times;* &rarr; *</span>; (<span class="type">&sigma;</span>,<span class="type">&tau;</span>) &#8614; <span class="type">&sigma;&rarr;&tau;</span>.
     * <p>
     * The map type constructor.
     * </p>
     * @postconditions RES == OLD(RES)
     * @see #map(Type,Type)
     */
    BinaryFunction/*<Type,Type,Type>*/ map();

    /**
     * Get the predicate type <span class="type">(&sigma;)</span> = <span class="type">&sigma;&rarr;&omicron;</span>.
     * <p>
     * <span style="float: left; font-size: 200%">&#9761;</span>
     * Note that this type depends on the specific truth values.
     * </p>
     * @param domain the {@link Type#domain() co-domain} <span class="type">&sigma;</span>.
     * @see #map(Type,Type)
     * @todo assure canonical identity?
     */
    public Type predicate(Type domain);
    /**
     * predicate: <span class="type">* &rarr; *</span>; <span class="type">&sigma;</span> &#8614; <span class="type">(&sigma;)</span>.
     * <p>
     * The predicate type constructor.
     * </p>
     * @postconditions RES == OLD(RES)
     * @see #predicate(Type)
     */
    Function/*<Type,Type>*/ predicate();

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
     * @see #list(Type)
     */
    public Type product(Type components[]);
    /**
     * product: <span class="type">&lang;*&rang; &rarr; *</span>;
     * <big>(</big><span class="type">&tau;<sub>i</sub></span><big>)</big> &#8614; <span class="type">&prod;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&times;&#8230;&times;&tau;<sub>n</sub></span>.
     * <p>
     * The product type constructor.
     * </p>
     * @postconditions RES == OLD(RES)
     * @see #product(Type[])
     * @todo what exactly is the difference/commons of <span class="type">&lang;*&rang;</span> and
     *  <span class="type">*&times;...&times;*</span>?
     */
    Function/*<Type[],Type>*/ product();

    // type constructors in type lattice
    
    /**
     * Get the infimum type <span class="type">&#8898;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&cap;&#8230;&cap;&tau;<sub>n</sub></span>.
     * inf is the most general common subtype in the type lattice, also called intersection.
     * Especially, it is &forall;i <span class="type">&#8898;<sub>i</sub>&tau;<sub>i</sub></span> &le; <span class="type">&tau;<sub>i</sub></span>.
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
     * @postconditions RES is infimum of components with respect to {@link Type#compareTo(Object) &le;}
     * @attribute associative
     * @attribute neutral {@link #UNIVERSAL <span class="type">&#8868;</span>}
     * @attribute commutative
     * @attribute distributive {@link #sup}
     * @attribute idempotent
     * @attribute 
     *   <span class="type">&sigma;</span> &le; <span class="type">&tau;</span>
     *   &hArr; <span class="type">&sigma;&cap;&tau;</span> = <span class="type">&sigma;</span>
     * @see Type#compareTo(Object)
     */
    public Type inf(Type components[]);
    /**
     * inf: <span class="type">{*} &rarr; *</span>;
     * <big>(</big><span class="type">&tau;<sub>i</sub></span><big>)</big> &#8614; <span class="type">&#8898;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&cap;&#8230;&cap;&tau;<sub>n</sub></span>.
     * <p>
     * The infimum type constructor.
     * </p>
     * @postconditions RES == OLD(RES)
     * @see #inf(Type[])
     * @see Type#compareTo(Object)
     */
    Function/*<Type[],Type>*/ inf();

    /**
     * Get the supremum type <span class="type">&#8899;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&cup;&#8230;&cup;&tau;<sub>n</sub></span>.
     * sup is the most special common supertype in the type lattice, also called union.
     * Especially, it is &forall;i <span class="type">&tau;<sub>i</sub></span> &le; <span class="type">&#8899;<sub>i</sub>&tau;<sub>i</sub></span>.
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
     * @postconditions RES is supremum of components with respect to {@link Type#compareTo(Object) &le;}
     * @attribute associative
     * @attribute neutral {@link #ABSURD <span class="type">&perp;</span>}
     * @attribute commutative
     * @attribute distributive {@link #inf}
     * @attribute idempotent
     * @attribute 
     *   <span class="type">&sigma;</span> &le; <span class="type">&tau;</span>
     *   &hArr; <span class="type">&sigma;&cup;&tau;</span> = <span class="type">&tau;</span>
     * @todo what's this property called? somewhat like absorption?
     * @see Type#compareTo(Object)
     * @todo strict or ignore absurd?
     */
    public Type sup(Type components[]);

    /**
     * sup: <span class="type">{*} &rarr; *</span>;
     * <big>(</big><span class="type">&tau;<sub>i</sub></span><big>)</big> &#8614; <span class="type">&#8899;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&cup;&#8230;&cup;&tau;<sub>n</sub></span>.
     * <p>
     * The supremum type constructor.
     * </p>
     * @postconditions RES == OLD(RES)
     * @see #sup(Type[])
     * @see Type#compareTo(Object)
     */
    Function/*<Type[],Type>*/ sup();

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
    public Type collection(Type element);
    /**
     * collection: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">collection(&tau;)</span>.
     * <p>
     * The collection type constructor.
     * </p>
     * @postconditions RES == OLD(RES)
     * @see #collection(Type)
     */
    Function/*<Type,Type>*/ collection();
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
    public Type set(Type element);
    /**
     * set: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">{&tau;}</span>.
     * <p>
     * The set type constructor.
     * </p>
     * @postconditions RES == OLD(RES)
     * @see #set(Type)
     */
    Function/*<Type,Type>*/ set();
    /**
     * The list type <span class="type">&lang;&tau;&rang;</span>.
     * Lists are ordered (but not sorted) and not limited to containing unique elements.
     * @see #collection(Type)
     * @see java.util.List
     * @see #product(Type[])
     */
    public Type list(Type element);
    /**
     * list: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">&lang;&tau;&rang;</span>.
     * <p>
     * The list type constructor.
     * </p>
     * @postconditions RES == OLD(RES)
     * @see #list(Type)
     */
    Function/*<Type,Type>*/ list();
    /**
     * The bag/multiset type <span class="type">&#12308;&tau;&#12309;</span>.
     * Bags are orderless and not limited to containing unique elements.
     * So each element of a bag has a certain multiplicity.
     * @see #collection(Type)
     */
    public Type bag(Type element);
    /**
     * bag: <span class="type">* &rarr; *</span>; <span class="type">&tau;</span> &#8614; <span class="type">&#12308;&tau;&#12309;</span>.
     * <p>
     * The bag type constructor.
     * </p>
     * @see #bag(Type)
     */
    Function/*<Type,Type>*/ bag();

}// TypeSystem
