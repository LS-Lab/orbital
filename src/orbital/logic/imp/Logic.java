/**
 * @(#)Logic.java 1.0 1999/01/12 Andre Platzer
 * 
 * Copyright (c) 1999-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.sign.ExpressionSyntax;
import orbital.logic.sign.Expression;
import orbital.logic.sign.Symbol;
import orbital.logic.sign.ParseException;
import orbital.logic.sign.type.TypeException;

/**
 * Provides a unified encapsulation of logical systems.
 * This interface encapsulates access to the logic inference relations and satisfaction relations
 * defined for a logical system.
 * <p>
 * All subsequent logics define these relations for inference and reasoning:
 * <ul>
 *   <li>satisfaction relation: <span class="Formula">I &#8871; F</span>.</li>
 *   <li>inference relation: W <span class="inference">|~</span> F.</li>
 * </ul>
 * for a formula F over &Sigma;, a set W of formulas, and an interpretation I of
 * the signature &Sigma; of a language <i>L</i>=Formula(&Sigma;).
 * <hr size="4" />
 * For a logic, syntax of the representation and semantics for the world must be exactly defined.
 * A general logic consists of:
 * <h4>1 Syntax</h4>
 * <dl class="def">
 *   <dt id="Signature">Signature:</dt>
 *   <dd>A signature &Sigma;
 *   is the set of the syntactic symbols applicable for objects.
 *   Those are symbolic names for constants, functions<i>/arity</i>, variables, predicates<i>/arity</i>, objects, properties of objects, relations between objects.
 *   </dd>
 *   <dt id="Formula">Formulas:</dt>
 *   <dd>A &Sigma;-formula <span class="Formula">F&isin;Formula(&Sigma;)</span> over a signature &Sigma;
 *   is an element of all possible, well-formed formulas that can be build with this signature.
 *   The set of well-formed formulas is a formal language over the alphabet &Sigma; as defined in the super class.
 *   Formulas define the syntax for the representation of statements concerning the world.
 *   They are recursively build with atoms (of the signature) that are combined
 *   with <i>functors</i>: junctors <var>&#8902;</var> and quantifiers <var>@</var>.<br />
 *   Common basic junctors are logic junction operations like, for example,
 *   the binary conjunction <b>and</b> (short <b>&and;</b>), the binary disjunction <b>or</b> (short <b>&or;</b>) and the unary negation <b>not</b> (short <b>&not;</b>).
 *   Quantifiers are logic operations like, for example, the existence quantifier (short <b>&exist;</b>) and the all-quantifier (short <b>&forall;</b>).
 *   </dd>
 * </dl>
 * <h4>2 Semantics</h4>
 * <dl class="def">
 * <!-- single symbol interpretation is secondness, although full function I is thirdness because it mediates between syntactic formulas and individuals of the world. -->
 *   <dt id="Interpretation">Interpretations:</dt>
 *   <dd>A &Sigma;-interpretation <span class="Formula">I&isin;Int(&Sigma;)</span> of a signature &Sigma;
 *   is the association of the syntactic symbols in &Sigma; with the semantic elements in the world.
 *   Such elements are objects of the universe, functions within the model world and relations in it.
 *   Only the interpretation gives a semantic meaning to the arbitrary names of the signature.
 *   </dd>
 *   <dt id="SatisfactionRelation">Satisfaction Relations:</dt>
 *   <dd>A (semantic) satisfaction relation <span class="Formula">&#8871; &sube; Int(&Sigma;)&times;Formula(&Sigma;)</span> of a signature &Sigma;
 *   is the connection between syntactic representation formulas and their semantic interpretations for the world.
 *   <span class="Formula">I &#8871; F</span> says whether a (compound) formula F is valid in an interpretation I.
 *   </dd>
 * </dl>
 * Pragmatics comes into play when we consider the consequences of interpreting formulas
 * in the semantics of the specific application at hand.
 * <hr size="6" />
 * For a logic, one or more {@link Inference inference relations} between
 * existing knowledge and derived knowledge can be defined.
 * Logic systems can formally be classified with the characteristics of their inference relations.
 * </p>
 * <p>
 * One important semantic inference relation is the <dfn>logic sequence</dfn> (or entailment) between formulas
 * which is again denoted as &#8872;.
 * It can easily be adapted from the satisfaction relation.
 * Then for <span class="Formula">W &#8872; F</span> all those interpretations I that satisfy the Formulas in W are considered:
 * <small style="font-style: italic">
 * <ul>
 *   <li>
 *     If <i>sceptic inference</i> is applied, <span class="Formula">I &#8871; F</span>
 *     must be true for all Interpretations I that satisfy W.<br />
 *     For sets of formulas this is defined as:
 *     <center class="Formula">FM &#8872; GM</span> iff <span class="Formula">Mod<sub>&Sigma;</sub>(FM)&sube;Mod<sub>&Sigma;</sub>(GM)</center>
 *     This is the usual case.
 *   </li>
 *   <li>
 *     If <i>credulous inference</i> is applied, <span class="Formula">I &#8871; F</span>
 *     must match for at least one Interpretation I that satisfies W.
 *   </li>
 * </ul>
 * </small>
 * Now we can define logical equivalence of formulas with the logic sequence.
 * <dl class="def">
 *   <dt>logical equivalence</dt>
 *   <dd>
 *     &equiv; &sube; Formula(&Sigma;)&times;Formula(&Sigma;) with
 *     A&equiv;B :&hArr; &#8872; A&harr;B
 *     &hArr; for all interpretations I (with variable assignements) I(A) = I(B)
 *     &hArr; A &#8872; B and B &#8872; A
 *     <br /><div class="compact">Note:</div>
 *     The last equivalence is only true provided that we do not use global entailment
 *     but local entailment.
 *   </dd>
 * </dl>
 * </p>
 * <p>
 * Other inference relations <span class="inference">|~</span> can be defined with the logic sequence &#8872;.
 * Depending upon the inferential problem and whether the formulas are incomplete and which should be inferred,
 * <a href="doc-files/inferential.html">several inference relations</a> are possible.
 * </p>
 * <p>
 * <b>Note</b> that it is generally undefined to use formulas of one logic within another logic.
 * It may be possible, though, in some special cases of compatible logics.
 * </p>
 * 
 * @structure extends ExpressionSyntax
 * @invariants true
 * @version 1.0, 2001/01/14
 * @author  Andr&eacute; Platzer
 * @see Signature
 * @see Formula
 * @see Interpretation
 * @see Inference
 * @see <a href="{@docRoot}/Patterns/Design/Strategy.html">Strategy Pattern</a>
 */
public interface Logic extends ExpressionSyntax {

    // get/set Properties

    /**
     * Get the core interpretation which is fixed for this logic.
     * <p>
     * This will usually contain the interpretation functors of logical operators like &not;, &and;, &or;, &rarr;, &hArr;, &forall; and &exist;.
     * </p>
     * @preconditions true
     * @return the core interpretation that is valid for every expression, for fixed interpretation semantics.
     *  Elements in the core signature all have a fixed interpretation.
     * @postconditions RES == OLD(RES) &and; RES unmodifiable &and; RES.getSignature() == coreSignature()
     * @see ExpressionSyntax#coreSignature()
     */
    //TODO: document that we get a list of handlers (for fixed interpretation semantics) of the core signature?
    // like "&"/2 |-> LogicBinaryFunction.and
    //      "="/2 |-> LogicBinaryFunction.equals
    // could map & to Formula.and(Formula) then
    Interpretation coreInterpretation();

    // operation methods

    /**
     * Defines the semantic satisfaction relation &#8871;.
     *   <center class="Formula">I &#8871; F, which is usually iff I(F) = true</center>
     * In other words, returns whether I is a satisfying &Sigma;-<a href="Interpretation.html#Model">Model</a> of F.
     * <p>
     * For multi-valued logics, the above definition of a semantic satisfaction relation
     * would experience a small generalization
     *   <center class="Formula">I &#8871; F, iff I(F) &isin; <span class="set">D</span></center>
     * for a fixed set <span class="set">D</span> of designated truth-values.
     * </p>
     * <p>
     * Unlike the implementation method {@link Formula#apply(Object)}, this surface method
     * must automatically consider the {@link #coreInterpretation() core interpretation}
     * of this logic for symbol interpretations (and possible redefinitions) as well.
     * </p>
     * @param I the interpretation within which to evaluate F.
     * @param F the formula to check whether it is satisfied in I.
     * @preconditions F&isin;<i>L</i>(F.getSignature()) &quot;F is a formula in this logic&quot;
     * @return whether I &#8871; F, i.e. whether I satisfies F.
     * @throws IncompleteCalculusException  if calculus for this logic is not complete.
     * @throws LogicException  if an exception related to logic occurs.
     * @see #coreInterpretation()
     */
    boolean satisfy(Interpretation I, Formula F);

    /**
     * Get the inference relation <span class="inference">|~</span><sub>K</sub>
     * according to the implementation calculus K.
     * @return the inference relation <span class="inference">|~</span><sub>K</sub> of logical inference.
     * @throws IncompleteCalculusException  if calculus for this logic is not complete.
     * @throws LogicException  if an exception related to logic occurs.
     */
    Inference inference();

    
    // additional preconditions and postconditions, only

    /**
     * {@inheritDoc}
     * @postconditions RES instanceof Formula
     * @todo use covariant return-types?
     */
    Expression createAtomic(Symbol symbol) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @postconditions RES instanceof Formula
     * @todo use covariant return-types?
     */
    Expression.Composite compose(Expression compositor, Expression[] arg) throws ParseException, TypeException;

    /**
     * {@inheritDoc}
     * @preconditions expression&ne;<span class="String">""</span>
     * @postconditions RES instanceof Formula
     * @todo use covariant return-types?
     */
    Expression createExpression(String expression) throws ParseException, IllegalArgumentException;

}
