/**
 * @(#)Interpretation.java 1.0 1999/01/12 Andre Platzer
 * 
 * Copyright (c) 1999-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import java.util.Map;
import java.util.Iterator;
import orbital.logic.functor.Functor;

/**
 * An interpretation associates the symbols in a signature
 * with the entities in the world (for semantics).
 * The arbitrary symbols in the signature are given a meaning with an interpretation, only.
 * <p>
 * A (denotational) interpretation is a mapping from signs to interpretants (alias referents).
 * More precisely
 * <dl class="def">
 *   <dt id="interpretation">interpretation</dt>
 *   <dd>
 *     an interpretation I:&Sigma;&rarr;<span class="set">D</span> is a familiy of maps
 *     I:&Sigma;<sub>&tau;</sub>&rarr;<span class="set">D</span><sub>&tau;</sub>
 *     for each type &tau;, with
 *     <ul>
 *       <li>I maps symbols of type &tau; to elements of <span class="set">D</span><sub>&tau;</sub>.</li>
 *       <li><span class="set">D</span><sub>t</sub> := <span class="set">Boole</span> := {True,False} for the type t of truth-values.</li>
 *       <li><span class="set">D</span><sub>e</sub> = <span class="set">D</span> is a set called universe or domain of I for the type e of entities.
 *         It is non-empty by presupposition of existence.</li>
 *       <li><span class="set">D</span><sub>&sigma;&rarr;&tau;</sub> := Map(<span class="set">D</span><sub>&sigma;</sub>,<span class="set">D</span><sub>&tau;</sub>) = <span class="set">D</span><sub>&tau;</sub><sup><span class="set">D</span><sub>&sigma;</sub></sup>.</li>
 *       <li><span class="set">D</span><sub>(&sigma;)</sub> = <span class="set">D</span><sub>&sigma;&rarr;t</sub> &cong; &weierp;(<span class="set">D</span><sub>&sigma;</sub>)
 *         since the predicate type (&sigma;) abbreviates the function type &sigma;&rarr;t,
 *         and we can identify subsets &rho;&isin;&weierp;(<span class="set">D</span><sub>&sigma;</sub>)
 *         with their characterisitic functions &chi;<sub>&rho;</sub>&isin;Map(<span class="set">D</span><sub>&sigma;</sub>,{True,False}).</li>
 *     </ul>
 *     Also we can identify (<span class="set">D</span><sup>0</sup>&rarr;<span class="set">D</span>)&cong;<span class="set">D</span>, as well as &weierp;(<span class="set">D</span><sup>0</sup>)={&empty;,{()}}&cong;{True,False}.
 *     <!-- @todo is this a good idea in conjunction with the strong type system of Java? -->
 *   </dd>
 *   <dt>homomorphism</dt>
 *   <dd>
 *     Let I:&Sigma;&rarr;<span class="set">D</span> be an interpretation.
 *     <table border="0">
 *       <tr>
 *         <td colspan="4">&nbsp;&phi;:&Sigma;&cup;Term(&Sigma;)&cup;Formula(&Sigma;)&rarr;<span class="set">D</span>&cup;<span class="set">D</span>&cup;Boole
 *           is a homomorphism of &Sigma;-expressions, if</td>
 *       </tr>
 *       <tr>
 *         <td width="6%" rowspan="3">&nbsp;</td>
 *       </tr>
 *       <tr>
 *         <td>&phi;(f(t<sub>1</sub>,...,t<sub>n</sub>))</td>
 *         <td>= &phi;(f)<big>(</big>&phi;(t<sub>1</sub>),...,&phi;(t<sub>n</sub>)<big>)</big></td>
 *         <td>if f&isin;&Sigma;<sub>n</sub> is a function, t<sub>1</sub>,...,t<sub>n</sub>&isin;Term(&Sigma;)</td>
 *       </tr>
 *       <tr>
 *         <td>&phi;(P(t<sub>1</sub>,...,t<sub>n</sub>))</td>
 *         <td>&hArr; &phi;(P)<big>(</big>&phi;(t<sub>1</sub>),...,&phi;(t<sub>n</sub>)<big>)</big></td>
 *         <td>if P&isin;&Sigma;<sub>n</sub> is a predicate, t<sub>1</sub>,...,t<sub>n</sub>&isin;Term(&Sigma;)</td>
 *       </tr>
 *     </table>
 *   </dd>
 *   <dt>truth-functional</dt>
 *   <dd>
 *     If for every interpretation I:&Sigma;&rarr;<span class="set">D</span> there is a unique continuation
 *     &phi;:&Sigma;&cup;Term(&Sigma;)&cup;Formula(&Sigma;)&rarr;<span class="set">D</span>&cup;<span class="set">D</span>&cup;Boole
 *     that is a homomorphism of &Sigma;-expressions, i.e.
 *     <center>&phi;|&Sigma; = I and &phi; is homomorph</center>
 *     Then the logic is called truth-functional,
 *     and that unique homomorphism &phi; is called the (expression) evaluation or truth-function,
 *     which is again denoted by I.
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
 *     </p>
 *   </dd>
 * </dl>
 * </p>
 * <p id="satisfactionRelation">
 * If we have a truth-functional logic, we can define the satisfaction relation
 * with truth-functions
 *   <center>I &#8871; F :&hArr; I(F) = true</center>
 * The satisfaction relation defines the semantics of a formula.
 * In effect, it encapsulates the details of truth-functions into a single relation.
 * Although this may not instantly appear to be advantageous, we then receive the
 * freedom of interchanging the satisfaction relation (or even any truth-functions underlying it).
 * </p>
 * <p>
 * In principle, semantics of syntactic expressions are usually defined with denotational semantics for interpretations,
 * with operational semantics for a calculus, and sometimes with algebraic semantics or logical semantics.</p>
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
 *     For finite sets <span class="Formula"><span class="set">A</span> = {F<sub>1</sub>,...,F<sub>n</sub>}</span> it is true that
 *     <div class="Formula">I &#8871; <span class="set">A</span></span> if and only if <span class="Formula">I &#8871; F<sub>1</sub>&and;...&and;F<sub>n</sub></div>
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
 *   <dt>elementar equivalence</dt>
 *   <dd>
 *     Two interpretations I:&Sigma;&rarr;<span class="set">D</span>, and J:&Sigma;&rarr;<span class="set">E</span> are elementary equivalent, iff
 *     <center>Theory({I}) = Theory({J})</center>
 *     i.e. they satisfy the same formulas.
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
 *         <td>&phi;<big>(</big>I(f)(d<sub>1</sub>,...,d<sub>n</sub>)<big>)</big></td>
 *         <td>= J(f)<big>(</big>&phi;(d<sub>1</sub>),...,&phi;(d<sub>n</sub>)<big>)</big></td>
 *         <td>if f&isin;&Sigma;<sub>n</sub> is a function, d<sub>1</sub>,...,d<sub>n</sub>&isin;<span class="set">D</span></td>
 *       </tr>
 *       <tr>
 *         <td>I(p)<big>(</big>d<sub>1</sub>,...,d<sub>n</sub><big>)</big></td>
 *         <td>&hArr; J(p)<big>(</big>&phi;(d<sub>1</sub>),...,&phi;(d<sub>n</sub>)<big>)</big></td>
 *         <td>if p&isin;&Sigma;<sub>n</sub> is a predicate, d<sub>1</sub>,...,d<sub>n</sub>&isin;<span class="set">D</span></td>
 *       </tr>
 *     </table>
 *     The interpretations I:&Sigma;&rarr;<span class="set">D</span>, and J:&Sigma;&rarr;<span class="set">E</span>
 *     are <dfn>isomorph</dfn> if there is an
 *     isomorphism &phi;:<span class="set">D</span>&rarr;<span class="set">E</span>, i.e. a bijective homomorphism.
 *     Isomorph interpretations are elementary equivalent
 *   </dd>
 * </dl>
 * </p>
 *
 * <p> 
 * An interpretation associates each sign in the signature &Sigma; with an object value, its interpretation.
 * </p>
 *
 * @structure extends java.util.Map<Symbol,Object>
 * @invariant (&Sigma; == null &or; keySet() &sube; &Sigma;)
 *  		&and; &forall;(s,v)&isin;this s.getSpecification().isConform(v)
 * @version 1.0, 2001/01/12
 * @author  Andr&eacute; Platzer
 * @see Logic#satisfy
 * @see Signature
 * @see java.util.Map
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
     * @pre sigma == null || keySet() &sube; sigma
     * @throws IllegalArgumentException if sigma does not contain a symbol which is interpreted in the current assocation map.
     *  This is not checked if sigma is <code>null</code>.
     */
    void setSignature(Signature sigma);


    // Basic Map operations.

    /**
     * Get the object value associated with the given symbol in this interpretation.
     * <p>
     * Overwrite along with other map operations like {@link java.util.Set#contains(Object)} to implement
     * a different source for symbol associations.
     * </p>
     * @post symbol.getSpecification().isConform(RES)
     * @throws java.util.NoSuchElementException if the symbol is not in the current signature &Sigma;.
     */
    Object get(Object/*>Symbol<*/ symbol);

    /**
     * Set the object value associated with the given symbol in this interpretation.
     * @pre symbol.getSpecification().isConform(value)
     * @throws java.util.NoSuchElementException if the symbol is not in the current signature &Sigma;.
     */
    Object put(Object/*>Symbol<*/ symbol, Object value);

    /**
     * Copies all of the associations from the specified map to this interpretation.
     * @pre (&Sigma; == null &or; keySet() &sube; &Sigma;) &and; &forall;(s,v)&isin;associations {@pre #put(Symbol, Object) put(s,v)}
     * @throws IllegalArgumentException if associations does contain a symbol which is not contained in the signature.
     *  This is not checked if &Sigma; is <code>null</code>.
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
     * @post RES.getClass() == getClass() && this.equals(OLD)
     */
    Interpretation union(Interpretation i2);

}
