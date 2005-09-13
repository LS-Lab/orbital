/**
 * @(#)Interpretation.java 1.0 1999/01/12 Andre Platzer
 * 
 * Copyright (c) 1999-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.sign.Signature;
import orbital.logic.sign.Symbol;
import java.util.Map;
import java.util.Iterator;
import orbital.logic.functor.Functor;

/**
 * An interpretation associates the symbols in a signature
 * with the entities in the world (for semantics).
 * The arbitrary symbols in the signature are given a meaning with an interpretation, only.
 * <p>
 * In principle, semantics of syntactic expressions are usually defined with denotational semantics for interpretations,
 * with operational semantics for a calculus, and sometimes with algebraic semantics or logical semantics.</p>
 * </p>
 * <p>
 * A (denotational) interpretation is a mapping from signs to referents.
 * More precisely
 * <dl class="def">
 *   <dt id="interpretation">interpretation</dt>
 *   <dd>
 *     an interpretation I:&Sigma;&rarr;<span class="set">D</span> is a family of maps
 *     I:&Sigma;<sub class="type">&tau;</sub>&rarr;<span class="set">D</span><sub class="type">&tau;</sub>
 *     for each type <span class="type">&tau;</span>, with
 *     <ul>
 * <!-- @todo use I(<span class="type">&sigma;</span>) instead of <span class="set">D</span><sub class="type">&tau;</sub> througout? -->
 *       <li>I maps symbols of {@link orbital.logic.sign.type.Type type} <span class="type">&tau;</span> to elements of the class I(<span class="type">&tau;</span>):=<span class="set">D</span><sub class="type">&tau;</sub> &ne; &empty;.
 *          Especially in computer science, the class I(<span class="type">&tau;</span>) is often assumed to be a set, even though this is rather irrelevant.
 *          <cite>"Wilfrid Hodges. Elementary Predicate Logic. In: Dov M. Gabbay and F. Guenther. Handbook of philosophical logic Volume 1 2nd edition. paragraph 17 theorem 10"</cite>
 *       </li>
 *       <li>I respects subtypes: for types <!-- <span class="type">&sigma;</span>,<span class="type">&tau;</span>:{@link orbital.logic.sign.type.TypeSystem#TYPE() <span class="type">*</span>} with -->
 *         <span class="type">&sigma;</span>&le;<span class="type">&tau;</span> the sets satisfy I(<span class="type">&sigma;</span>)&sube;I(<span class="type">&tau;</span>).</li>
 *       <li><span class="set">D</span><sub class="type">&omicron;</sub> is the set of truth-values for the type <span class="type">&omicron;</span> = <span class="type">()</span> of truth-values
 *         (also the type of atomic formulas).
 *         For two-valued logics this means <span class="set">D</span><sub class="type">&omicron;</sub> := <span class="set">Boole</span> := {True,False}.
 *       </li>
 *       <li><span class="set">D</span><sub class="type">&iota;</sub> := <span class="set">D</span> is a set called universe or domain of I for the type <span class="type">&iota;</span> of individuals.
 *         It is non-empty by presupposition of existence.</li>
 *       <li><span class="set">D</span><sub class="type">&sigma;&rarr;&tau;</sub> := Map(<span class="set">D</span><sub class="type">&sigma;</sub>,<span class="set">D</span><sub class="type">&tau;</sub>) = <span class="set">D</span><sub class="type">&tau;</sub><sup><span class="set">D</span><sub class="type">&sigma;</sub></sup>.</li>
 *       <li><span class="set">D</span><sub class="type">(&sigma;)</sub> = <span class="set">D</span><sub class="type">&sigma;&rarr;&omicron;</sub> &cong; &weierp;(<span class="set">D</span><sub class="type">&sigma;</sub>)
 *         because the predicate type <span class="type">(&sigma;)</span> abbreviates the function type <span class="type">&sigma;&rarr;&omicron;</span>,
 *         and we can identify predicates &rho;&isin;&weierp;(<span class="set">D</span><sub class="type">&sigma;</sub>)
 *         with their <a href="../sign/type/Types.html#extension">extension</a> &delta;&rho; and those sets
 *         with their characterisitic functions &chi;<sub>&delta;&rho;</sub>&isin;Map(<span class="set">D</span><sub class="type">&sigma;</sub>,{True,False}).</li>
 *     </ul>
 *     Also we can identify (<span class="set">D</span><sup>0</sup>&rarr;<span class="set">D</span>)&cong;<span class="set">D</span>, as well as &weierp;(<span class="set">D</span><sup>0</sup>)={&empty;,{()}}&cong;{True,False}.
 *     <!-- @todo make sure this uniform referent is a good idea in conjunction with the strong type system of Java? -->
 *     Interpretations are homomorphisms.
 *   </dd>
 *   <dt>homomorphism</dt>
 *   <dd>
 *     <table>
 *       <tr>
 *         <td colspan="4">&phi;:<span class="UniversalAlgebra">T</span>(&Sigma;)&rarr;<span class="set">D</span>
 *           is a <dfn>homomorphism of (typed) &Sigma;-algebras</dfn>, i.e. a family of maps
 *           &phi;:<span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&tau;</sub>&rarr;<span class="set">D</span><sub class="type">&tau;</sub>,
 *           if
 *         </td>
 *       </tr>
 *       <tr>
 *         <td width="6%" rowspan="4">&nbsp;</td>
 *       </tr>
 *       <tr>
 *         <td>&phi;(<var class="meta.disabled">&upsilon;</var>(t))</td>
 *         <td>= &phi;(<var class="meta.disabled">&upsilon;</var>)<big>(</big>&phi;(t)<big>)</big></td>
 *         <td>for <var class="meta.disabled">&upsilon;</var>&isin;&Sigma;<sub class="type">&sigma;&rarr;&tau;</sub>, t&isin;<span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&le;&sigma;</sub></td>
 * <!-- @todo or   <var class="meta.disabled">&upsilon;</var>&isin;<span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&sigma;&rarr;&tau;</sub>? -->
 *       </tr>
 *       <tr>
 *         <td colspan="3">Especially</td>
 *       </tr>
 *       <tr>
 *         <td>&phi;(&upsilon;(t<sub>1</sub>,&#8230;,t<sub>n</sub>))</td>
 *         <td>= &phi;(&upsilon;)<big>(</big>&phi;(t<sub>1</sub>),&#8230;,&phi;(t<sub>n</sub>)<big>)</big></td>
 *         <td>for &upsilon;&isin;&Sigma;<sub>n</sub> is a function, t<sub>1</sub>,&#8230;,t<sub>n</sub>&isin;<span class="UniversalAlgebra">T</span>(&Sigma;)</td>
 *       </tr>
 *     </table>
 *   </dd>
 *   <dt>truth-functional</dt>
 *   <dd class="UniversalMappingProperty">
 *     If for every interpretation I:&Sigma;&rarr;<span class="set">D</span> there is a unique continuation
 *     &phi;:<span class="UniversalAlgebra">T</span>(&Sigma;)&rarr;<span class="set">D</span>
 *     that is a homomorphism of &Sigma;-algebras, i.e.
 *     <center>&phi;|<sub>&Sigma;</sub> = I and &phi; is homomorphic</center>
 *     Then the logic is called truth-functional,
 *     and that unique homomorphism &phi; is called the (expression) valuation
 *     or truth-function, which is again denoted by I.
 *     <blockquote>
 *     "Evaluations are the homomorphic continuation of symbol interpretations."
 *     </blockquote>
 *     Note that the homomorphism conditions for &phi; include
 *     <div>
 *     &phi;(&not;P) = &not;&phi;(P),
 *     &phi;(P&and;Q) = &phi;(P)&and;&phi;(Q),
 *     &phi;(P&or;Q) = &phi;(P)&or;&phi;(Q),
 *     &phi;(P&rarr;Q) = &phi;(P)&rarr;&phi;(Q) etc.
 *     </div>
 *     <p>
 *     If a logic is truth-functional, the semantics of a combined formula
 *     can be defined with the combined semantics of the components and junctors.
 *     Then the junctors are treated truth-functionally, that is, every junctor is defined
 *     by its corresponding function that maps the combined truth-values to resulting truth-values.
 *     This is denoted with truth-tables.
 *     </p>
 *   </dd>
 * </dl>
 * </p>
 * <p>
 * If we have a truth-functional logic, we can define the <dfn id="satisfactionRelation">satisfaction relation</dfn>
 * with truth-functions
 *   <center>I &#8871; F :&hArr; I(F) = true</center>
 * The satisfaction relation defines the semantics of a formula.
 * In effect, it encapsulates the details of truth-functions into a single relation.
 * Although this may not instantly appear to be of advantage, we then receive the
 * freedom of interchanging the satisfaction relation (or even any truth-functions underlying it).
 * </p>
 * <p>
 * <a id="Model"></a>
 * An interpretation I is a satisfying &Sigma;-<dfn>Model</dfn> of F, if:
 * <center class="Formula">I &#8871; F</span>, i.e. the interpretation satisfies the formula.</center>
 * <ul>
 *   <li>
 *     The set of all satisfying &Sigma;-Models of F is
 *     <div class="Formula">Mod<sub>&Sigma;</sub>(F) := {I&isin;Int(&Sigma;) &brvbar; I &#8871; F} &sube; Int(&Sigma;)</div>
 *   </li>
 *   <li>
 *     For a set of formulas <span class="set">A</span>&sube;Formula(&Sigma;) it is
 *     <div class="Formula">Mod<sub>&Sigma;</sub>(<span class="set">A</span>) := {I&isin;Int(&Sigma;) &brvbar; for all F&isin;<span class="set">A</span> I &#8871; F}</div>
 *   </li>
 *   <li>
 *     For finite sets <span class="Formula"><span class="set">A</span> = {F<sub>1</sub>,&#8230;,F<sub>n</sub>}</span> it is true that
 *     <div class="Formula">I &#8871; <span class="set">A</span></span> if and only if <span class="Formula">I &#8871; F<sub>1</sub>&and;&#8230;&and;F<sub>n</sub></div>
 *   </li>
 * </ul>
 * </p>
 * <p>
 * <dl class="def">
 *   <dt>theory</dt>
 *   <dd>
 *     A set of formulas <span class="set">T</span>&sube;Formula(&Sigma;) is a <dfn>theory</dfn> :&hArr;
 *     <center><span class="set">T</span> &supe; <span class="inferenceOperation">C</span>(<span class="set">T</span>) = {F&isin;Formula(&Sigma;) &brvbar; <span class="set">T</span> <span class="inference">&#8872;</span> F}</center>
 *     &rArr; <span class="Formula"><span class="set">T</span>&cap;{F&isin;Formula(&Sigma;) &brvbar; Mod<sub>&Sigma;</sub>(F)=&empty;} = &empty; xor <span class="set">T</span> = Formula(&Sigma;)
 *   </dd>
 *   <dt>theory of a model set</dt>
 *   <dd>
 *     The theory of a set of models <span class="set">M</span>&sube;Int(&Sigma;) is
 *     <center><span class="set">T</span> := Theory(<span class="set">M</span>) := {F&isin;Formula(&Sigma;) &brvbar; &forall;I&isin;<span class="set">M</span> I &#8871; F}</center>
 *     This is the model-theoretic way of defining theories.<br />
 *     &rArr; <span class="set">T</span> is <a id="complete"><dfn>complete</dfn></a>, i.e. &forall;F&isin;Formula(&Sigma;) (F&isin;<span class="set">T</span> xor &not;F&isin;<span class="set">T</span>).
 *   </dd>
 *   <dt>theory of a formula set</dt>
 *   <dd>
 *     The theory represented by the decidable set of formulas <span class="set">A</span>&sube;Formula(&Sigma;) is
 *     <center><span class="set">T</span> := Theory(<span class="set">A</span>) := <span class="inferenceOperation">C</span>(<span class="set">A</span>) = {F&isin;Formula(&Sigma;) &brvbar; <span class="set">A</span> <span class="inference">&#8872;</span> F} = Theory(Mod(<span class="set">A</span>))</center>
 *     <span class="set">A</span> is called the presentation or <dfn>axiomatization</dfn> of the theory <span class="set">T</span>
 *     which is said to be axiomatizable.
 *     The definition as consequence closure is the axiomatic way of defining theories.<br />
 *     &rArr; <span class="set">T</span> is <a href="../../algorithm/doc-files/computability.html#semi-decidable">semi-decidable</a>.
 *   </dd>
 * </dl>
 * </p>
 * <p>
 * In addition to this syntactic aspect, theories, when applied to a particular domain, should just
 * as well explain observed facts.
 * </p>
 * <p>
 * Now we consider possible equivalence relations of interpretations.
 * <dl class="def">
 *   <dt>elementary equivalent</dt>
 *   <dd>
 *     Two interpretations I:&Sigma;&rarr;<span class="set">D</span>, and J:&Sigma;&rarr;<span class="set">E</span> are elementary equivalent, iff
 *     <center>Theory({I}) = Theory({J})</center>
 *     i.e. they satisfy the same formulas of the logic <var>L</var>.
 *   </dd>
 *   <dt>homomorphism</dt>
 *   <dd>
 *     Let I:&Sigma;&rarr;<span class="set">D</span>, J:&Sigma;&rarr;<span class="set">E</span> be two interpretations.
 *     <table border="0">
 *       <tr>
 *         <td colspan="4">&nbsp;&phi;:<span class="set">D</span>&rarr;<span class="set">E</span>
 *           is a homomorphism of interpretations, if</td>
 *       </tr>
 *       <tr>
 *         <td width="6%" rowspan="3">&nbsp;</td>
 *       </tr>
 *       <tr>
 *         <td>&phi;<big>(</big>I(f)(d<sub>1</sub>,&#8230;,d<sub>n</sub>)<big>)</big></td>
 *         <td>= J(f)<big>(</big>&phi;(d<sub>1</sub>),&#8230;,&phi;(d<sub>n</sub>)<big>)</big></td>
 *         <td>if f&isin;&Sigma;<sub>n</sub> is a function, d<sub>1</sub>,&#8230;,d<sub>n</sub>&isin;<span class="set">D</span></td>
 *       </tr>
 *       <tr>
 *         <td>I(p)<big>(</big>d<sub>1</sub>,&#8230;,d<sub>n</sub><big>)</big></td>
 *         <td>&hArr; J(p)<big>(</big>&phi;(d<sub>1</sub>),&#8230;,&phi;(d<sub>n</sub>)<big>)</big></td>
 *         <td>if p&isin;&Sigma;<sub>n</sub> is a predicate, d<sub>1</sub>,&#8230;,d<sub>n</sub>&isin;<span class="set">D</span></td>
 *       </tr>
 *     </table>
 *     The interpretations I:&Sigma;&rarr;<span class="set">D</span>, and J:&Sigma;&rarr;<span class="set">E</span>
 *     are <dfn>isomorphic</dfn> if there is an
 *     isomorphism &phi;:<span class="set">D</span>&rarr;<span class="set">E</span>, i.e. a bijective homomorphism.
 *     Isomorphic interpretations are elementary equivalent.
 *   </dd>
 * </dl>
 * </p>
 *
 * <p> 
 * An interpretation associates each sign in the signature &Sigma; with an object value, its interpretation.
 * Especially, <em>our</em> interpretations include valuations (variable assignments).
 * Valuations are a technique introduced by Tarski, for dealing with the semantics of quantifiers.
 * <!-- The sentence &forall;x p(x) is true if the formula p(x) with the free variable x
 * is true no matter what value in the domain has been assigned to x by the valuation.
 * [see Melvin Fitting, and Richard L. Mendelsohn. First-Order Modal Logic. 4.6 Constant Domain Models, p. 97. 1998]
 * -->
 * </p>
 *
 * @structure extends java.util.Map<Symbol,Object>
 * @invariants (&Sigma; == null &or; keySet() &sube; &Sigma;)
 *  		&and; &forall;(s,v)&isin;this s.getType().apply(v)
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Logic#satisfy
 * @see Signature
 * @see java.util.Map
 * @see InterpretationBase#EMPTY(Signature)
 * @see InterpretationBase#unmodifiableInterpretation(Interpretation)
 * @note This interface could be strengthened by extending SortedMap instead of just Map.
 * @todo specialize superclass to SortedMap because Signature is sorted?
 */
public interface Interpretation extends Map/*<Symbol, Object>*/ {

    /**
     * Checks two interpretations for extensional equality.
     * Two interpretations are equal if both their signatures and interpretation objects
     * are equal.
     */
    boolean equals(Object o);
    
    /**
     * Get a hash code fitting extensional equality.
     * {@inheritDoc}.
     */
    int hashCode();

    // Get/Set Properties

    /**
     * Get the signature interpreted.
     */
    Signature getSignature();

    /**
     * Set the signature interpreted.
     * @preconditions sigma == null || keySet() &sube; sigma
     * @throws IllegalArgumentException if sigma does not contain a symbol which is interpreted in the current assocation map.
     *  This is not checked if sigma is <code>null</code>.
     */
    void setSignature(Signature sigma);


    // Basic Map operations.

    /**
     * Get the referent associated with the given symbol in this interpretation.
     * <p>
     * Overwrite along with other map operations like {@link java.util.Set#contains(Object)} to implement
     * a different source for symbol associations.
     * </p>
     * @postconditions symbol.getType().apply(RES)
     * @throws java.util.NoSuchElementException if the symbol is not in the current signature &Sigma;.
     */
    Object get(Object/*>Symbol<*/ symbol);

    /**
     * Set the referent associated with the given symbol in this interpretation.
     * @preconditions symbol.getType().apply(referent)
     * @throws java.util.NoSuchElementException if the symbol is not in the current signature &Sigma;.
     * @throws orbital.logic.sign.type.TypeException if the referent is not of the type of symbol.
     */
    Object put(Object/*>Symbol<*/ symbol, Object referent);

    /**
     * Copies all of the associations from the specified map to this interpretation.
     * @preconditions (&Sigma; == null &or; keySet() &sube; &Sigma;) &and; &forall;(s,v)&isin;associations {@preconditions #put(Symbol, Object) put(s,v)}
     * @throws IllegalArgumentException if associations does contain a symbol which is not contained in the signature.
     *  This is not checked if &Sigma; is <code>null</code>.
     * @throws orbital.logic.sign.type.TypeException if one of the values is not of the type of its symbol.
     * @throws NullPointerException if associations is <code>null</code>.
     */
    void putAll(Map/*<Symbol, Object>*/ associations);

    /**
     * Returns whether the specified symbol is contained in this interpretation assocation map.
     * @throws java.util.NoSuchElementException if the symbol is not in the current signature &Sigma;.
     */
    boolean containsKey(Object/*>Symbol<*/ symbol);

    // Extended operations.

    /**
     * Returns the union of two interpretations.
     * @see orbital.util.Setops#union(java.util.Collection,java.util.Collection)
     * @param i2 the interpretation to merge with this one, resulting in a new interpretation.
     *  (If a symbol is contained in both interpretations, the value of i2 will precede over
     *  the value of this.)
     * @return i &cup; i2.
     * @postconditions RES.getClass() == getClass() && this.equals(OLD)
     */
    Interpretation union(Interpretation i2);

}
