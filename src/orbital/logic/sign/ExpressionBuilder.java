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
 * @see <a href="{@docRoot}/DesignPatterns/Builder.html">Builder</a>
 * @todo perhaps import coreSignature() from ExpressionSyntax, because LogicParser needs it?
 *  However conceptually, that method does not really fit.
 * @todo improve documentation above
 */
public interface ExpressionBuilder {
	
    // Create an <dfn>atomic</dfn> expression

    /**
     * Create an <dfn>atomic</dfn> expression representation of a non-compound sign.
     * <p>
     * Atomic symbols are either elemental atoms, strings or numbers.
     * An atom is a logical formula that is not compound of something.
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
     * @post RES.isVariable() &hArr; symbol.isVariable()
     * @throws IllegalArgumentException if the symbol is illegal for some reasons.
     *  Note that this is a rather rare case and no parsing is involved at all,
     *  which is why this method does not throw a ParseException.
     * @todo think about exceptions
     * @see <a href="{@docRoot}/DesignPatterns/FactoryMethod.html">Factory Method</a>
     */
    Expression createAtomic(Symbol symbol);

    // Create a compound <dfn>term</dfn> expression

    //@TODO complete the design of any additional(?) methods
    /**
     * Create a compound expression representation with a composition operation.
     * Connects expressions with a composing functor<!-- @todo always a connector or formator? -->
     * to a <dfn>complex</dfn> expression (<dfn>ascriptor</dfn>).
     * <p>
     * {@link Signature#get(String,Object[])} may be useful for determining the right functor symbol
     * for a composition.
     * </p>
     * @param functor the symbol for the functor that performs the desired composition of arguments.
     * @param arg the arguments <var>a</var> passed to the combining operation.
     * @pre functor&ne;null &and; functor(arg)&isin;<i>L</i>
     *  "functor applied to arg represents a syntactically well-formed expression"
     * @return an instance of Expression that represents the combined operation with arguments, like in
     *  <div><code>functor(<var>a</var><span class="operator">[</span><span class="number">0</span><span class="operator">]</span>,...,<var>a</var><span class="operator">[</span><var>a</var>.length<span class="operator">-</span><span class="number">1</span><span class="operator">]</span>)</code></div>
     * @throws ParseException when the composition expression is syntactically malformed.
     *  Either due to a lexical or grammatical error (also due to wrong arity and type of the arguments).
     * @internal this is a meta-operator. We could also choose a simpler compositor part orbital.logic.imp.Symbol but would then need an undefined language primitive "apply" for compose("apply",{f,a}) = f(a). So this formal trick soon looses its simplicity and thus is inferior to the approach of compositors in Term(&Sigma;) instead of just &Sigma;.
     * @see <a href="{@docRoot}/DesignPatterns/FactoryMethod.html">Factory Method</a>
     */
    Expression compose(Symbol functor, Expression[] arg) throws ParseException;

}
