/*
 * @(#)Formula.java 1.0 1999/01/12 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.sign.Expression;
import orbital.logic.sign.Symbol;
import orbital.logic.sign.Signature;

import orbital.logic.functor.Function;
import java.util.Set;

/**
 * A formula interface for presentations of formal logic. This interface also defines an
 * encapsulation for basic logic junction operations (called <b>junctors</b>).
 * Logic representations must balance expressiveness and tractability.
 * <p>
 * <hr size="4" />
 * <a id="Properties"></a>Formulas can formally be classified with these essential characteristics.
 * With <span class="Formula">F&isin;Formula(&Sigma;)</span> the formula F is:
 * <dl class="def">
 *   <dt>valid (or tautological or universal or necessary or analytical)</dt>
 *   <dd>
 *     if <span class="Formula">Mod<sub>&Sigma;</sub>(F)=Int(&Sigma;)</span>,
 *     that is, if it is satisfied by all interpretations.
 *     A formula is tautological, if it is not falsifiable.
 *     <div>&hArr; {@link orbital.moon.logic.ClassicalLogic.Utilities#allClosure(Formula) Cl<sub>&forall;</sub>} F is valid</div>
 *   </dd>
 *   <dt>satisfiable (or consistent or possible)</dt>
 *   <dd>
 *     if <span class="Formula">Mod<sub>&Sigma;</sub>(F)&ne;&empty;</span>,
 *     that is, if it is satisfied by at least one interpretation
 *     (together with a variable assignement).
 *     <div>&hArr; <span class="inferenceOperation">C</span>({F}) &ne; Formula(&Sigma;)</div>
 *     <div>&hArr; F &#8872;&#824; false</div>
 *     <div>&hArr; there is no A&isin;Formula(&Sigma;) with F &#8872; A and F &#8872; &not;A</div>
 *     <div>&hArr; F has a class of models</div>
 *     <div>&hArr; {@link orbital.moon.logic.ClassicalLogic.Utilities#existenceClosure(Formula) Cl<sub>&exist;</sub>} F is satisfiable</div>
 *   </dd>
 *   <dt>falsifiable</dt>
 *   <dd>
 *     if <span class="Formula">Mod<sub>&Sigma;</sub>(F)&ne;Int(&Sigma;)</span>,
 *     that is, if it is falsified by at least one interpretation.
 *   </dd>
 *   <dt>unsatisfiable (or inconsistent)</dt>
 *   <dd>
 *     A formula is inconsistent, if it is not consistent,
 *     i.e. if <span class="Formula">Mod<sub>&Sigma;</sub>(F)=&empty;</span>,
 *     that is, if it is falsified by all interpretations.
 *   </dd>
 *   <dt>complete</dt>
 *   <dd>
 *     if for all closed A&isin;Formula(&Sigma;) it is F &#8872; A or F &#8872; &not;A
 *     <div>&hArr; F has at most one <em>class</em> of models</div>
 *   </dd>
 * </dl>
 * <table style="border: solid;">
 *   <caption>Table of essential characteristics of formulas</caption>
 *   <tr> <th>short</th> <th>prosa</th> <th>models</th> <th>prosa</th> </tr>
 *   <tr> <td>&#8872; F</td> <td>F is valid</td> <td>Mod<sub>&Sigma;</sub>(F)=Int(&Sigma;)</td> <td>all models</td> </tr>
 *   <tr> <td>&#8872;&#824; &not;F</td> <td>F is satisfiable</td> <td>Mod<sub>&Sigma;</sub>(F)&ne;&empty;</td> <td>some models</td> </tr>
 *   <tr> <td>&#8872;&#824; F</td> <td>F is falsifiable</td> <td>Mod<sub>&Sigma;</sub>(F)&ne;Int(&Sigma;)</td> <td>not all models</td> </tr>
 *   <tr> <td>&#8872; &not;F</td> <td>F is unsatisfiable</td> <td>Mod<sub>&Sigma;</sub>(F)=&empty;</td> <td>no models</td> </tr>
 * </table>
 * Satisfying <a href="Interpretation.html#Model">models</a> are interpretations that satisfy the formula.
 * All these definitions generalize to sets of formulas <span class="set">F</span>
 * instead of a single formula F.
 * </p>
 * <p>
 * Formulas are simply algebraic expressions, which define functions over individiuals of a universe.
 * Whereas logical junctors define functions over truth values, and
 * quantifiers define higher order functions, instead.
 * However, note that it is not an inherent property that formulas extend {@link orbital.logic.functor.Function},
 * but a matter of modelling. This view leads to an attracting simplicity,
 * although there is not necessarily a semantical connection categorizing formulas as functions.
 * The relation between formulas and functions is not necessarily one of specialization, but of
 * adopting a certain part of functions' behaviour under a single aspect.
 * An important advantage of this decision is that the machinery developed for logical functions,
 * like composition, binding etc. can be applied to (composed) formulas, as well.
 * Calling the formula's {@link Formula#apply(Object)} method will get the value of
 * this formula, given an interpretation that is passed as an argument.
 * </p>
 * 
 * @version 1.0, 1999/01/12
 * @author  Andr&eacute; Platzer
 * @structure extends Expression
 * @structure extends Function<Interpretation,Object>
 * @see Inference#infer
 * @see LogicBasis
 * @see ExpressionSyntax#createAtomic
 * @see <a href="{@docRoot}/Patterns/Design/Interpreter.html">Interpreter</a>
 * @see "Daniel Leivant. Higher order logic, Chapter 3.6 Formulas as higher order functions. In: Dov M. Gabbay, editor, Handbook of Logic in Artificial Intelligence and Logic Programming, pages 247-248. Oxford University Press. 1994"
 * @note boolean formulas (of propositional logic) can also be represented with (reduced) OBDDs, for performance in some applications.
 */
public interface Formula extends Expression, Function/*<Interpretation, Object>*/ {

    //@xxx move to Expression? and document (implementation necessary for TRS' fixedPoints and perhaps unification to work)
    boolean equals(Object o);
    int hashCode();
	
    // Get/Set Methods
	
    /**
     * Get the set of the free variables of this formula.
     * <p>
     * <dfn>free variables</dfn> FV(t) of a term t&isin;Term(&Sigma;)
     * with V&sube;&Sigma; being the set of variables are:
     * </p>
     * <!-- @todo use nameOfMap, leftOfMap etc. -->
     * <table>
     *   <tr>
     *     <td colspan="3">
     *       <p>FV:Term(&Sigma;)&#8594;&weierp;(V);</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td rowspan="3" style="width: 5%">
     *     </td>
     *     <td>
     *       <p>FV(<var>x</var>)</p>
     *     </td>
     *     <td>
     *       <p>= {<var>x</var>}</p>
     *     </td>
     *     <td>
     *       <p>if <var>x</var>&isin;V</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>FV(f(t<sub>1</sub>,...,t<sub>n</sub>))</p>
     *     </td>
     *     <td>
     *       <p>= FV(t<sub>1</sub>) &cup;...&cup; FV(t<sub>n</sub>)</p>
     *     </td>
     *     <td>
     *       <p>if f&isin;Func/n</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>FV(c)</p>
     *     </td>
     *     <td>
     *       <p>= &empty;</p>
     *     </td>
     *     <td>
     *       <p>if c&isin;Func/0</p>
     *     </td>
     *   </tr>
     * </table>
     * <p>
     * <dfn>free variables</dfn> FV(F) of a formula F&isin;Formula(&Sigma;)
     * with V&sube;&Sigma; being the set of variables are:
     * </p>
     * <table>
     *   <tr>
     *     <td colspan="3">
     *       <p>FV:Formula(&Sigma;)&#8594;&weierp;(V);</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td rowspan="5" style="width: 5%">
     *     </td>
     *     <td>
     *       <p>FV(P(t<sub>1</sub>,...,t<sub>n</sub>))</p>
     *     </td>
     *     <td>
     *       <p>= FV(t<sub>1</sub>) &cup;...&cup; FV(t<sub>n</sub>)</p>
     *     </td>
     *     <td>
     *       <p>if P&isin;Pred/n
     *       </p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>FV(&not;G)</p>
     *     </td>
     *     <td>
     *       <p>= FV(G)</p>
     *     </td>
     *     <td>
     *       <p>&nbsp;
     *       </p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>FV(G &#8902; H)</p>
     *     </td>
     *     <td>
     *       <p>= FV(G) &cup;
     *       FV(H)</p>
     *     </td>
     *     <td>
     *       <p>&#8902;&isin;{&and;,&or;,&rArr;,&hArr;}</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>FV(@<var>x</var>G)</p>
     *     </td>
     *     <td>
     *       <p>= FV(G) &#8726; {<var>x</var>}</p>
     *     </td>
     *     <td>
     *       <p>if bound by a @&isin;{&forall;,&exist;}</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>FV(P)</p>
     *     </td>
     *     <td>
     *       <p>= &empty;</p>
     *     </td>
     *     <td>
     *       <p>if P&isin;Pred/0</p>
     *     </td>
     *   </tr>
     * </table>
     * <p>
     * A formula F is ground or closed if it holds no free variables FV(F) = &empty;,
     * or open if it has free variables FV(F) &ne; &empty;.
     * </p>
     * <p>
     * A formula F can be closed by the universal closure Cl<sub>&forall;</sub>(F), and the
     * existence closure Cl<sub>&exist;</sub>(F) which bind all free variables of F by
     * &forall; resp. &exist; quantifiers.
     * </p>
     *
     * @return FV(this).
     */
    Set/*_<Symbol>_*/ getFreeVariables();
	
    /**
     * Get the set of the bound variables of this formula.
     * <p>
     * <dfn>bound variables</dfn> BV(F) of a formula F&isin;Formula(&Sigma;)
     * with V&sube;&Sigma; being the set of variables are:
     * </p>
     * <table>
     *   <tr>
     *     <td colspan="3">
     *       <p>BV:Formula(&Sigma;)&#8594;&weierp;(V);</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td rowspan="5" style="width: 5%">
     *     </td>
     *     <td>
     *       <p>BV(P(t<sub>1</sub>,...,t<sub>n</sub>))</p>
     *     </td>
     *     <td>
     *       <p>= &empty;</p>
     *     </td>
     *     <td>
     *       <p>if P&isin;Pred/n
     *       </p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>BV(&not;G)</p>
     *     </td>
     *     <td>
     *       <p>= BV(G)</p>
     *     </td>
     *     <td>
     *       <p>&nbsp;
     *       </p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>BV(G &#8902; H)</p>
     *     </td>
     *     <td>
     *       <p>= BV(G) &cup; BV(H)</p>
     *     </td>
     *     <td>
     *       <p>&#8902;&isin;{&and;,&or;,&rArr;,&hArr;}</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>BV(@<var>x</var>G)</p>
     *     </td>
     *     <td>
     *       <p>= BV(G) &cup; {<var>x</var>}</p>
     *     </td>
     *     <td>
     *       <p>if bound by a @&isin;{&forall;,&exist;}</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>BV(P)</p>
     *     </td>
     *     <td>
     *       <p>= &empty;</p>
     *     </td>
     *     <td>
     *       <p>if P&isin;Pred/0</p>
     *     </td>
     *   </tr>
     * </table>
     *
     * @return BV(this).
     */
    Set/*_<Symbol>_*/ getBoundVariables();

    /**
     * Get the set of all variables of this formula.
     *
     * @return V(this) := FV(this)&cup;BV(this).
     * @see #getFreeVariables()
     * @see #getBoundVariables()
     */
    Set/*_<Symbol>_*/ getVariables();

    // interpretation continuation (or evaluation)
    
    /**
     * Interpret this formula.
     * <p>
     * Note that this method may choose to ignore any changes in the core interpretations,
     * and stick to the core interpretation at the time of formula construction.
     * <small>The later may be ensured by formulas of fixed interpretations.</small>
     * </p>
     * @param I the interpretation I.
     * @return I(this).
     * @see <a href="{@docRoot}/Patterns/Design/Interpreter.html">Interpreter</a>
     * @see <a href="{@docRoot}/Patterns/Design/Visitor.html">&quot;Visitor&quot;</a>
     * @see Logic#satisfy(Interpretation, Formula)
     */
    Object apply(Object/*>Interpretation<*/ I);

    // Basic logical operations (elemental junctors).

    /**
     * Negation not: &not;<span class="Formula">F</span>.
     * @throws UnsupportedOperationException if this junctor is not supported by the representation.
     */
    Formula not();

    /**
     * Conjunction and: <span class="Formula">F</span> &and; <span class="Formula">G</span>.
     * <p>
     * Also denoted as <span class="Formula">F</span> &amp; <span class="Formula">G</span>.
     * Sometimes even as <span class="Formula">F</span>&#8201;<span class="Formula">G</span>, <span class="Formula">F</span>.<span class="Formula">G</span>, <span xml:lang="pl">K</span> <span class="Formula">F</span><span class="Formula">G</span>.
     * </p>
     * @throws UnsupportedOperationException if this junctor is not supported by the representation.
     */
    Formula and(Formula B);

    /**
     * Disjunction or: <span class="Formula">F</span> &or; <span class="Formula">G</span>.
     * <p>
     * Sometimes also denoted as <span xml:lang="pl">A</span> <span class="Formula">F</span><span class="Formula">G</span>.
     * </p>
     * @throws UnsupportedOperationException if this junctor is not supported by the representation.
     */
    Formula or(Formula B);

    // Extended logical operations.

    /**
     * Exclusion xor: <span class="Formula">F</span> &or;&#775; <span class="Formula">G</span> = <span class="Formula">F</span> xor <span class="Formula">G</span>.
     * <p>
     * Xor is also called antivalence and sometimes denoted as <span class="Formula">F</span> &#8622; <span class="Formula">G</span>.</p>
     * @throws UnsupportedOperationException if this junctor is not supported by the representation.
     */
    Formula xor(Formula B);

    /**
     * Implication impl: <span class="Formula">F</span> &rarr; <span class="Formula">G</span>.
     * <p>
     * This implication is also called subjunction and denoted as <span class="Formula">F</span> <!-- @todo is there a better turned consequence sign for subjunctor than subset -->&#8835;</span> <span class="Formula">G</span>.
     * Sometimes implication is also denoted as <span class="Formula">F</span> &rArr; <span class="Formula">G</span> to underline that it is a
     * material implications.
     * Sometimes also denoted as <span xml:lang="pl">C</span> <span class="Formula">F</span><span class="Formula">G</span>.
     * </p>
     * <p>
     * &not;<span class="Formula">G</span>&rarr;&not;<span class="Formula">F</span> is called contra position of <span class="Formula">F</span>&rarr;<span class="Formula">G</span>.
     * <span class="Formula">G</span>&rarr;<span class="Formula">F</span> is called reciprocal of <span class="Formula">F</span>&rarr;<span class="Formula">G</span>.
     * </p>
     * @throws UnsupportedOperationException if this junctor is not supported by the representation.
     */
    Formula impl(Formula B);

    /**
     * Equivalence equiv: <span class="Formula">F</span> &harr; <span class="Formula">G</span>.
     * <p>
     * Sometimes this is also called bisubjunction and denoted as <span class="Formula">F</span> &hArr; <span class="Formula">G</span>, or even <span class="Formula">F</span> &#8801; <span class="Formula">G</span>.
     * Sometimes also denoted as <span xml:lang="pl">E</span> <span class="Formula">F</span><span class="Formula">G</span>.
     * </p>
     * @throws UnsupportedOperationException if this junctor is not supported by the representation.
     */
    Formula equiv(Formula B);

    // Basic logical operations (elemental quantifiers).

    /**
     * Universal-quantifier forall: &forall;<var>x</var> <span class="Formula">F</span>.
     * <p>
     * Sometimes, this is also denoted as &#8896;<sub><var>x</var></sub> <span class="Formula">F</span>.
     * </p>
     * <p>
     * &forall; is not (compositional or) truth-functional.
     * </p>
     * @param x is a symbol <var>x</var> for all elements of the world.
     * @preconditions <var>x</var>.isVariable()
     * @throws UnsupportedOperationException if this quantifier is not supported by the representation.
     */
    Formula forall(Symbol x);

    /**
     * Existential-quantifier exists: &exist;<var>x</var> <span class="Formula">F</span>.
     * <p>
     * Sometimes, this is also denoted as &#8897;<sub><var>x</var></sub> <span class="Formula">F</span>.
     * </p>
     * <p>
     * &forall; is not (compositional or) truth-functional.
     * </p>
     * @param x is a symbol <var>x</var> for an element of the world.
     * @preconditions <var>x</var>.isVariable()
     * @throws UnsupportedOperationException if this quantifier is not supported by the representation.
     */
    Formula exists(Symbol x);
}
