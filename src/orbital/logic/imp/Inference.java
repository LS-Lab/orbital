/**
 * @(#)Inference.java 1.0 1999/02/09 Andre Platzer
 * 
 * Copyright (c) 1999-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.Relation;

/**
 * Provides a unified encapsulation for inference relations <span class="inference">|~</span> used for any logic reasoning.
 * It encapsulates a general <dfn>inference relation</dfn>:
 * <center>
 *   <span class="Formula"><span class="inference">|~</span> &sube; &weierp;<big>(</big>Formula(&Sigma;)<big>)</big>&times;Formula(&Sigma;)</span>
 * </center>
 * An inference relation is a relation between existing knowledge and the knowledge to be derived.
 * Every inference over syntactic symbols of the representation must preserve structure
 * for the elements of the world represented.
 * <p>
 * The intuition of an inference <span class="Formula"><span class="set">W</span> <span class="inference">|~</span> F</span>
 * to hold is that
 * on the basis of the set of formulas <span class="set">W</span> known (or assumed) to be true
 * one can conclude that F&isin;Formula(&Sigma;) is true as well.
 * (<a href="#deduction">See below</a> for a constructive definition applicable in most cases).
 * Often, the signature &Sigma; is not specified if it is implicitly obvious from the context.
 * </p>
 * <p>
 * <dl class="def">
 *   <dt id="InferenceOperation">inference operation of <span class="inference">|~</span></dt>
 *   <dd>
 *     An inference operation that belongs to an inference relation <span class="inference">|~</span>
 *     can be defined as the consequence closure
 *     <center><span class="Formula"><span class="inferenceOperation">C</span>: &weierp;<big>(</big>Formula(&Sigma;)<big>)</big>&rarr;&weierp;<big>(</big>Formula(&Sigma;)<big>)</big>; <span class="set">W</span>&#8614;<span class="inferenceOperation">C</span>(<span class="set">W</span>) := {F&isin;Formula(&Sigma;) &brvbar; <span class="set">W</span> <span class="inference">|~</span> F}</span></center>
 *   </dd>
 *   <dt>deductively closed</dt>
 *   <dd>
 *     A set of formulas <span class="set">F</span> is called deductively closed if it is a fixed point of <span class="inferenceOperation">C</span>
 *     <center><span class="Formula"><span class="inferenceOperation">C</span>(<span class="set">F</span>) = <span class="set">F</span></span></center>
 *   </dd>
 * </dl>
 * <p id="Properties">Inference relations of a logic can formally be classified with these properties
 * of the corresponding inference operation <span class="inferenceOperation">C</span>.
 * <dl class="def">
 * The inference relation <span class="inference">|~</span> is:
 *   <dt>reflexive</dt> <dd>if <span class="Formula"><span class="set">A</span>&sube;<span class="inferenceOperation">C</span>(<span class="set">A</span>)</span>.</dd>
 *   <dt>sectioning</dt> <dd>if <span class="Formula"><span class="set">A</span>&sube;<span class="set">B</span>&sube;<span class="inferenceOperation">C</span>(<span class="set">A</span>)</span> implies <span class="Formula"><span class="inferenceOperation">C</span>(<span class="set">B</span>)&sube;<span class="inferenceOperation">C</span>(<span class="set">A</span>)</span>.</dd>
 *   <dt>cautiously monotonic</dt> <dd>if <span class="Formula"><span class="set">A</span>&sube;<span class="set">B</span>&sube;<span class="inferenceOperation">C</span>(<span class="set">A</span>)</span> implies <span class="Formula"><span class="inferenceOperation">C</span>(<span class="set">A</span>)&sube;<span class="inferenceOperation">C</span>(<span class="set">B</span>)</span>.</dd>
 *   <dt>cumulative</dt> <dd>if it is sectioning and cautiously monotonic.<br />
 *   (thus <span class="Formula"><span class="set">A</span>&sube;<span class="set">B</span>&sube;<span class="inferenceOperation">C</span>(<span class="set">A</span>)</span> implies <span class="Formula"><span class="inferenceOperation">C</span>(<span class="set">B</span>)=<span class="inferenceOperation">C</span>(<span class="set">A</span>)</span>).</dd>
 *   <dt>monotonic</dt> <dd>if <span class="Formula"><span class="set">A</span>&sube;<span class="set">B</span></span> implies <span class="Formula"><span class="inferenceOperation">C</span>(<span class="set">A</span>)&sube;<span class="inferenceOperation">C</span>(<span class="set">B</span>)</span>.
 *     <div>&lArr; <span class="inference">|~</span> is reflexive and transitive</div>
 *   </dd>
 *   <dt>transitive</dt>
 *   <dd>if <span class="Formula"><span class="set">A</span>&sube;<span class="inferenceOperation">C</span>(<span class="set">B</span>)</span> implies <span class="Formula"><span class="inferenceOperation">C</span>(<span class="set">A</span>)&sube;<span class="inferenceOperation">C</span>(<span class="set">B</span>)</span>.</dd>
 *   <dt>structural</dt>
 *   <dd>if <span class="Formula">&forall;&sigma;&isin;{@link orbital.logic.trs.Substitution SUB} &sigma;(<span class="inferenceOperation">C</span>(<span class="set">A</span>)) &sube; <span class="inferenceOperation">C</span>(&sigma;(<span class="set">A</span>)))</span></dd>
 *   <dt>compact</dt>
 *   <dd>if
 *     <center class="Formula"><span class="set">B</span> <span class="inference">|~</span> F  &hArr;  there exists a finite <span class="set">E</span>&sube;<span class="set">B</span> with <span class="set">E</span> <span class="inference">|~</span> F</center>
 *     <div>("&lArr;" if <span class="inference">|~</span> is monotonic)</div>
 *   </dd>
 *   <dt>uniform</dt>
 *   <dd>if
 *     provided that G and <span class="set">B</span>&cup;{F} do not have any variables (or (atomic) propositional variables) in common
 *     and G is not inconsistent (i.e. <span class="inferenceOperation">C</span>({G})&ne;Formula(&Sigma;)),
 *     then
 *     <center class="Formula"><span class="set">B</span>&cup;{G} <span class="inference">|~</span> F  &rArr;  <span class="set">B</span> <span class="inference">|~</span> F</center>
 *   </dd>
 * </dl>
 * <span class="Formula">&forall;<span class="set">A</span>,<span class="set">B</span>&sube;Formula(&Sigma;)</span>.
 * </p>
 *
 * <hr size="6" />
 * <h3 id="Calculus">Calculus</h3>
 * <p>
 * Semantic inference relations <span class="satisfaction">|&asymp;</span>
 * are implemented with a syntactic calculus <i>K</i> whose application in relational form
 * is denoted as <span class="inference">|~</span>.
 * Such a calculus deduces <span class="Formula"><span class="set">W</span> <span class="inference">|~</span> F</span>,
 * if F can be deduced with a sequence of <dfn>inference rules</dfn> applied on the formulas in <span class="set">W</span></span>.
 * A syntactic calculus K should be correlated with the semantic inference relation.
 * <dl class="def">
 * Let V be an alphabet that is provided effectively (f.ex. V=Formula(&Sigma;)),
 * and L be a language over V, i.e. a decidable set of finite objects over V
 * (usually words in L=V<sup>*</sup>).
 *   <dt>inference rule</dt>
 *   <dd>
 *     An n-ary inference rule is a decidable (n+1)-ary relation &rho;&sube;L<sup>n+1</sup>.
 *     For an instance (u<sub>0</sub>,...,u<sub>n-1</sub>,u<sub>n</sub>)&isin;&rho;,
 *     the u<sub>0</sub>,...,u<sub>n-1</sub> are called premises, and
 *     u<sub>n</sub> is called the conclusion.
 *   <dt>axiom</dt>
 *   <dd>
 *     Inference rules of arity 0 are called logical axioms since they do not depend on any premises.
 *   </dd>
 *   <dt>calculus</dt>
 *   <dd>
 *     A calculus <i>K</i> of the language L over V
 *     is a finite set of inference rules in this language L.
 *   </dd>
 * </dl>
 * <dl class="def">
 * Let <i>K</i> be a calculus of the language L over V.
 *   <dt id="deduction">deduction</dt>
 *   <dd>
 *     A deduction of F&isin;L from <span class="set">W</span>&sube;L is a repeated application of the inference relations of a calculus <i>K</i>.
 *     More formally, a deduction of F&isin;L from <span class="set">W</span>&sube;L is a finite sequence
 *     (u<sub>1</sub>,...,u<sub>n</sub>)&sube;L with u<sub>n</sub>=F such that for all i&isin;{1,...,n}
 *     <ol type="I">
 *       <li>u<sub>i</sub>&isin;<span class="set">W</span> is part of the knowledge,</li>
 *       <li>or there is an inference rule &rho;&isin;<i>K</i> with an arity of n&ge;0 and there are
 *         j<sub>1</sub>,...,j<sub>n</sub>&isin;{1,...,i-1} which justify
 *         (u<sub>j<sub>1</sub></sub>,...,u<sub>j<sub>n</sub></sub>,u<sub>i</sub>)&isin;&rho;.
 *       </li>
 *     </ol>
 *     We then call F deducable from <span class="set">W</span> in <i>K</i> which we denote by the inference relation
 *     <center class="Formula"><span class="set">W</span> <span class="inference">|~</span> F</center>
 *   </dd>
 * </dl>
 * Note that this notion of deduction is only a minor generalization (in arity)
 * of the syntactic deduction in formal languages specified by a Chomsky grammar.
 * It still can be reduced to the normal case of formal languages.
 * </p>
 * <dl class="def">
 * Let <i>K</i> be a calculus of the language L over V, then the corresponding
 * inference relation <span class="inference">|~</span> is monotonic, transitive, compact
 * and
 *   <dt>compositional</dt>
 *   <dd>
 *     <center>W<sub>i</sub> <span class="inference">|~</span> F<sub>i</sub> for i=1,...,n, and (F<sub>1</sub>,...,F<sub>n</sub>,F)&isin;&rho;&isin;<i>K</i> &rArr; &#8899;<sub>i=1,...,n</sub> W<sub>i</sub> <span class="inference">|~</span> F</center>
 *   </dd>
 * </dl>
 * These are true due to the above definition of deduction, cf. <a href="#Properties">monotonic</a> inference relations.
 * <dl class="def">
 * The calculus <i>K</i> having a syntactic inference relation <span class="inference">|~</span> is <i>consistent</i> if it is both:
 *   <dt>sound</dt> <dd>if <span class="inference">|~</span> &sube; <span class="satisfaction">|&asymp;</span>,
 *     i.e. all that is deducable is a logical consequence.</dd>
 *   <dt>complete</dt> <dd>if <span class="satisfaction">|&asymp;</span> &sube; <span class="inference">|~</span>,
 *     i.e all that is a logical consequence is deducable.</dd>
 * </dl>
 * 
 * @version 1.0, 1999/02/09
 * @author  Andr&eacute; Platzer
 * @invariant true
 * @see Formula
 * @see Signature
 * @see Logic#inference()
 * @see <a href="doc-files/inferential.html">several inference relations</a>
 */
public interface Inference extends Relation {

    /**
     * Apply the inference relation <span class="inference">|~</span>
     * according to the implementation calculus K.
     * @param w the basic knowledege formulas assumed true for the inference.
     *  Use an array of length <code class="number">0</code> or <code class="keyword">null</code> to denote the inference
     *  "&empty; <span class="inference">|~</span> w"
     *  from the empty set of knowledge &empty;. Only axioms are available then, and thus the result
     *  is equal to that of "<code class="keyword">true</code> <span class="inference">|~</span> d".
     *  This inference from the empty set is abbreviated as "<span class="inference">|~</span> w"
     *  because it will only infer tautologies.
     * @param d the formula to deduce from w, if possible.
     * @return whether w <span class="inference">|~</span> d, that is whether d can be inferred from the facts in w, or not.
     * @throws LogicException if an exception related to the logic syntax or semantics or the calculus execution occurs.
     * @pre true
     * @post ( (RES==true && isSound()) => w <span class="satisfaction">|&asymp;</span> d )
     *  && ( (w <span class="satisfaction">|&asymp;</span> d && isComplete()) => RES==true )
     * @see <a href="{@docRoot}/DesignPatterns/Strategy.html">Strategy Pattern</a>
     * @todo should we change the return-value to Object for probabilistic logic to return a Double? Or is it still a boolean, then?
     */
    boolean infer(Formula[] w, Formula d) throws LogicException;

    /**
     * Whether the calculus <i>K</i> underlying this object to implement the inference relation is sound.
     * <dl class="def">
     * The calculus <i>K</i> is
     *   <dt>sound</dt> <dd>if <span class="inference">|~</span> &sube; <span class="satisfaction">|&asymp;</span>, i.e. if <span class="Formula"><span class="set">W</span> <span class="inference">|~</span> F</span> implies <span class="Formula"><span class="set">W</span> <span class="satisfaction">|&asymp;</span> F</span>.</dd>
     * </dl>
     * @pre true
     * @post RES == OLD(RES)
     */
    boolean isSound();

    /**
     * Whether the calculus <i>K</i> underlying this object to implement the inference relation is complete.
     * <dl class="def">
     * The calculus <i>K</i> is
     *   <dt>complete</dt> <dd>if <span class="satisfaction">|&asymp;</span> &sube; <span class="inference">|~</span>, i.e. if <span class="Formula"><span class="set">W</span> <span class="satisfaction">|&asymp;</span> F</span> implies <span class="Formula"><span class="set">W</span> <span class="inference">|~</span> F</span>.</dd>
     * </dl>
     * @pre true
     * @post RES == OLD(RES)
     */
    boolean isComplete();
}
