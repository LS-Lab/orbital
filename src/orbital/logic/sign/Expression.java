/**
 * @(#)Expression.java 1.0 2000/03/19 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.functor.Functor.Specification;

/**
 * An interface for representations of expressions.
 * <p>
 * This interface is implemented by objects representing syntactic expressions like those created by
 * {@link ExpressionBuilder#createAtomic(Symbol)}, {@link ExpressionBuilder#compose(Symbol,Expression[])},
 * or {@link ExpressionSyntax#createExpression(String)}.
 * </p>
 * Given a signature &Sigma; we can define a general term algebra. We use an abstract
 * notation not saying anything about the particular syntax.
 * <dl class="def">
 *   <dt>expressions</dt>
 *   <dd>The expressions over a signature &Sigma; of type <span class="type">&tau;</span> are
 *     <table>
 *       <tr>
 *         <td colspan="2">
 *           Term(&Sigma;)<sub class="type">&tau;</sub> :=
 *         </td>
 *       </tr>
 *       <tr>
 *         <td rowspan="2" style="width: 5%">
 *           <p>&nbsp;
 *           </p>
 *         </td>
 *         <td>
 *           &Sigma;<sub class="type">&tau;</span>
 *         </td>
 *         <td class="defTerm">
 *           atomic symbols
 *         </td>
 *       </tr>
 *       <tr>
 *         <td>
 *           &cup; {<var class="meta">&Phi;</var>(t) &brvbar; <var class="meta">&Phi;</var>&isin;Term(&Sigma;)<sub class="type">&sigma;&rarr;&tau;</sub> and t&isin;Term(&Sigma;)<sub class="type">&sigma;</sub>}
 *         </td>
 *         <td class="defTerm">
 *           composites
 *         </td>
 *       </tr>
 *       <tr>
 *         <td colspan="2">
 *           be minimal, i.e. min fix
 *         </td>
 *       </tr>
 *     </table>
 *   </dd>
 *   <dt>(untyped) expressions</dt>
 *   <dd>The expressions over a signature &Sigma; of arbitrary types are
 *     <table>
 *       <tr>
 *         <td colspan="2">
 *           Term(&Sigma;) := &#8899;<sub>&tau;</sub> Term(&Sigma;)<sub class="type">&tau;</sub>
 *         </td>
 *       </tr>
 *     </table>
 *   </dd>
 * </dl>
 * Note that the case of composite symbols also includes compound symbols with compositors of
 * higher arities <var class="meta">n</var> and type
 * <span class="type">&sigma;<sub>1</sub></span>&times;<span class="type">&sigma;<sub>2</sub></span>&times;&#8230;&times;<span class="type">&sigma;<sub><var class="meta">n</var></sub></span>&rarr;<span class="type">&tau;</span>
 * by formally setting <span class="type">&sigma;</span> := <span class="type">&sigma;<sub>1</sub></span>&times;<span class="type">&sigma;<sub>2</sub></span>&times;&#8230;&times;<span class="type">&sigma;<sub><var class="meta">n</var></sub></span>.
 * And the brief notation is justified formally by currying.
 * If we also identify {()}&rarr;<span class="type">&tau;</span> with &tau; it would even include
 * the case of atomic symbols.
 * Also note that <var class="meta">&Phi;</var> does not need to be a function or predicate,
 * but is a meta-variable that may stand for any syntactic composition.
 * 
 * @version 1.0, 2000/03/19
 * @author  Andr&eacute; Platzer
 * @see ExpressionBuilder
 * @see ExpressionBuilder#createAtomic(Symbol)
 * @see ExpressionBuilder#compose(Symbol,Expression[])
 * @see ExpressionSyntax#createExpression(String)
 */
public interface Expression {
    /**
     * Get the subsignature appearing in this expression.
     * @return the subset of &Sigma; consisting of those symbols that occur in this expression.
     */
    Signature getSignature();

    /**
     * Get the type of this expression.
     * @pre true
     * @return the type <span class="type">&tau;</span> of this expression.
     * @see Symbol#getType()
     */
    Specification getType();
}
