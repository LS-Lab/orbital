/**
 * @(#)Expression.java 1.0 2000-03-19 Andre Platzer
 * 
 * Copyright (c) 2000-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign;

import orbital.logic.sign.type.Typed;

/**
 * An interface for representations of expressions.
 * <p>
 * This interface is implemented by objects representing syntactic expressions like those created by
 * {@link ExpressionBuilder#createAtomic(Symbol)}, {@link ExpressionBuilder#compose(Expression,Expression[])},
 * or {@link ExpressionSyntax#createExpression(String)}.
 * </p>
 * <p>
 * Given a signature &Sigma; we define a general term algebra and thus the (abstract) syntax of the
 * expressions. However, the abstract syntax notation does not say anything about the particular syntax.
 * <dl class="def">
 *   <dt id="freeAlgebraOfTerms">free algebra of terms</dt>
 *   <dd>The free algebra of terms over a signature &Sigma; is a universal &Sigma;-algebra
 *     (with &empty; as axioms) and also an instance of it.
 *     It is defined by
 *     <table>
 *       <tr>
 *         <td>
 *           <span class="UniversalAlgebra">T</span>(&Sigma;) :=
 *         </td>
 *         <td>
 *           &#8899;&#775;<sub class="type">&tau;</sub> <span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&tau;</sub>
 *         </td>
 *       </tr>
 *       <tr>
 *         <td colspan="3">The terms (or expressions) of {@link Type type} <span class="type">&tau;</span> are</td>
 *       </tr>
 *       <tr>
 *         <td colspan="3">
 *           <span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&tau;</sub> :=
 *         </td>
 *       </tr>
 *       <tr id="atomicSymbol">
 *         <td rowspan="3">
 *         </td>
 *         <td>
 *           &Sigma;<sub class="type">&tau;</span>
 *         </td>
 *         <td class="defTerm">
 *           {@link ExpressionBuilder#createAtomic(Symbol) atomic symbols}
 *         </td>
 *       </tr>
 *       <tr id="compositeExpression">
 *         <td>
 *           &cup; {<var class="meta">&upsilon;</var>(t) &brvbar; <var class="meta">&upsilon;</var>&isin;<span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&sigma;&rarr;&tau;</sub> and t&isin;<span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&le;&sigma;</sub>}
 *         </td>
 *         <td class="defTerm">
 *           {@link ExpressionBuilder#compose(Expression,Expression[]) composites} (<dfn>ascriptors</dfn>)
 *         </td>
 *       </tr>
 *       <tr>
 *         <td colspan="2">
 *           be minimal, i.e. min fix
 *         </td>
 *       </tr>
 *       <tr>
 *         <td colspan="2">
 *           The corresponding abstract syntax is
 *         </td>
 *       </tr>
 *       <tr>
 *         <td colspan="3">
 *           <span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&tau;</sub> &rarr;
 *         </td>
 *       </tr>
 *       <tr>
 *         <td rowspan="2">
 *         </td>
 *         <td>
 *           &Sigma;<sub class="type">&tau;</span>
 *         </td>
 *         <td class="defTerm">
 *           {@link ExpressionBuilder#createAtomic(Symbol) atomic symbols}
 *         </td>
 *       </tr>
 *       <tr>
 *         <td>
 *           | <span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&sigma;&rarr;&tau;</sub> <big>(</big><span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&le;&sigma;</sub><big>)</big>
 *         </td>
 *         <td class="defTerm">
 *           {@link ExpressionBuilder#compose(Expression,Expression[]) composites} (<dfn>ascriptors</dfn>)
 *         </td>
 *       </tr>
 *       <tr>
 *         <td colspan="3">The terms of subtypes of <span class="type">&tau;</span> are</td>
 *       </tr>
 *       <tr>
 *         <td>
 *           <span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&le;&tau;</sub> :=
 *         </td>
 *         <td>
 *           &#8899;&#775;<sub><span class="type">&rho;</span>&le;<span class="type">&tau;</span></sub> <span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&rho;</sub>
 *         </td>
 *       </tr>
 *     </table>
 *   </dd>
 * </dl>
 * Note that the case of composite symbols also includes compound symbols with compositors of
 * higher arity <var class="meta">n</var> and type
 * <span class="type">&sigma;<sub>1</sub></span>&times;<span class="type">&sigma;<sub>2</sub></span>&times;&#8230;&times;<span class="type">&sigma;<sub><var class="meta">n</var></sub></span>&rarr;<span class="type">&tau;</span>
 * by formally setting <span class="type">&sigma;</span> := <span class="type">&sigma;<sub>1</sub></span>&times;<span class="type">&sigma;<sub>2</sub></span>&times;&#8230;&times;<span class="type">&sigma;<sub><var class="meta">n</var></sub></span>.
 * This brief notation is justified formally by {@link orbital.logic.functor.Functionals#curry(BinaryFunction) currying}
 * or product construction.
 * Also note that <var class="meta">&upsilon;</var> is not restricted to functions and predicates,
 * but is a meta-variable that may stand for any syntactic compositor.
 * </p>
 * <p>
 * With the above decomposition, terms are a graded magma with the magma
 * &Tau; of types as graduation. Also &Sigma;&sube;<span class="UniversalAlgebra">T</span>(&Sigma;)
 * operates (left and right) on <span class="UniversalAlgebra">T</span>(&Sigma;)
 * with an action with which the graduation is compatible.
 * However the compositions involved are partial and may result in
 * errorneous type &perp; or undefined terms.
 * </p>
 * <p>
 * <!-- @todo clearify which parts in X and which in &Sigma; -->
 * For a set <span class="set">X</span> with &Sigma;&cap;<span class="set">X</span>&sube;&Sigma;<sub>0</sub>,
 * <span class="UniversalAlgebra">T</span>(&Sigma;&cup;<span class="set">X</span>)
 * is also a &Sigma;-algebra of terms over &Sigma; generated by <span class="set">X</span>.
 * </p>
 * 
 * @version 1.0, 2002-09-06
 * @version 1.0, 2000-03-19
 * @author  Andr&eacute; Platzer
 * @see ExpressionBuilder
 * @see ExpressionBuilder#createAtomic(Symbol)
 * @see ExpressionBuilder#compose(Expression,Expression[])
 * @see ExpressionSyntax#createExpression(String)
 * @todo everywhere distinguish "Term(&Sigma;)" of predicate logic from "<span class="UniversalAlgebra">T</span>(&Sigma;)" general expressions of a term algebra.
 * @todo could saw this package in two parts: the syntax (including types?) to orbital.logic.sign.* and the logic to orbital.logic.imp.*.
 *  Or into dependend packages orbital.logic.sign.* for syntax, orbital.logic.type.* for type-system, orbital.logic.imp.* for logic and semantics.
 */
public interface Expression extends Typed {
    /**
     * Get the subsignature appearing in this expression.
     * @return the subset of &Sigma; consisting of those symbols that occur in this expression.
     */
    Signature getSignature();


    /**
     * The base interface for all composite expressions that are composed of other expressions.
     * @structure is {@link orbital.logic.functor.Functor.Composite}&cap;{@link Expression}
     * @version 1.1, 2002-11-27
     * @author  Andr&eacute; Platzer
     * @see <a href="Expression.html#compositeExpression">composites</a>
     * @todo change base class to orbital.logic.sign.Composite
     */
    static interface Composite extends orbital.logic.functor.Functor.Composite, Expression {}
}






