/**
 * @(#)ExpressionBuilder.java 1.1 2002/04/09 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import java.text.ParseException;

/**
 * Defines a general interface for constructing complex expressions.
 * Implementations of this interface are responsible for constructing compound expressions (like terms, formulas).
 * They usually correspond to a formal language of a corresponding expression syntax.
 * This interface effectively works as a builder for syntactic expressions.
 * <p>
 * Refer to the {@link orbital.io.ParseException ParseException dilemma} to read about
 * why the exception types may have to change some day.
 * </p>
 * 
 * @invariant true
 * @version 1.1, 2002/04/09
 * @author  Andr&eacute; Platzer
 * @see Expression
 * @see <a href="{@docRoot}/Patterns/Design/Builder.html">Builder</a>
 * @todo perhaps import coreSignature() from ExpressionSyntax, because LogicParser needs it?
 *  However conceptually, that method does not really fit.
 * @todo improve documentation above
 */
public interface ExpressionBuilder {
	
    // Create an <dfn>atomic</dfn> expression

    /**
     * Create an <dfn>atomic</dfn> expression representation of a non-compound sign.
     * <p>
     * <a href="Expression.html#atomicSymbol">Atomic symbols</a> are either
     * elemental atoms, strings or numbers.
     * <span style="float: left; font-size: 200%">&#9761;</span>
     * In contrast, a logical formula that is not compound of something
     * (on the level of logical junctors) like "P(x,y)" is sometimes called atom.
     * </p>
     * <p>
     * <h5>Note</h5>
     * A compound expression like "P(x)" will <em>not</em> be atomic symbols
     * (although a logic might consider such single predicate applications as atomic
     * in the sense of atomicity on the level of logical junctors).
     * However, the variable "x", and the predicate symbol "P" are atomic symbols.
     * </p>
     * @param symbol the symbol whose atomic expression representation to create.
     * @pre symbol&isin;&Sigma;&cup;V&cup;... is a syntactically valid symbol
     * @return an instance of Expression that represents the atomic symbol in this logic.
     * @post RES.getType().equals(symbol.getType()) &and; (RES.isVariable() &hArr; symbol.isVariable())
     * @throws IllegalArgumentException if the symbol is illegal for some reasons.
     *  Note that this is a rather rare case and no parsing is involved at all,
     *  which is why this method does not throw a ParseException.
     * @todo think about exceptions
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    Expression createAtomic(Symbol symbol);

    // Create a <dfn>compound term</dfn> expression

    //@TODO complete the design of any additional(?) methods
    /**
     * Create a compound expression representation with a composition operation.
     * Connects expressions with a compositor<!-- @todo always a connector or formator? -->
     * to a <a href="Expression.html#compositeExpression">complex</a> expression.
     * <p>
     * {@link Signature#get(String,Object[])} may be useful for determining the right functor symbol
     * for a composition with an {@link #createAtomic(Symbol) atomic} compositor.
     * </p>
     * @param compositor the expression that is used for composing the arguments.
     * @param arg the arguments <var>a</var> passed to the combining operation.
     * @pre compositor&ne;null &and; compositor(arg)&isin;<i>L</i>
     *  "compositor applied to arg represents a syntactically well-formed expression"
     * @return an instance of Expression that represents the combined operation with arguments, like in
     *  <div><code>compositor(<var>a</var><span class="operator">[</span><span class="number">0</span><span class="operator">]</span>,...,<var>a</var><span class="operator">[</span><var>a</var>.length<span class="operator">-</span><span class="number">1</span><span class="operator">]</span>)</code></div>
     * @throws ParseException when the composition expression is syntactically malformed.
     *  Either due to a lexical or grammatical error (also due to wrong type of arguments).
     * @internal this is a meta-operator. We could also choose a simpler compositor part orbital.logic.imp.Symbol but would then need an undefined language primitive "apply" for compose("apply",{f,a}) = f(a). So this formal trick soon looses its simplicity and thus is inferior to the approach of compositors in Term(&Sigma;) instead of just &Sigma;.
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    Expression compose(Expression compositor, Expression[] arg) throws ParseException;

}
