/**
 * @(#)ExpressionBuilder.java 1.1 2002/04/09 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

/**
 * Defines a general interface for constructing complex expressions.
 * Implementations of this interface are responsible for constructing compound expressions (like terms, formulas).
 * They usually correspond to a formal language of a corresponding abstract syntax for expressions.
 * This interface effectively works as a builder for syntactic expressions.
 * <p>
 * Refer to the {@link ParseException ParseException dilemma} to read about
 * why the exception types may have to change some day.
 * </p>
 * 
 * @invariants true
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
     * @preconditions null&ne;symbol&isin;&Sigma;&cup;V&cup;... is a syntactically valid symbol
     * @return an instance of Expression that represents the atomic symbol in this logic.
     * @postconditions RES&ne;null &and; RES.getType().equals(symbol.getType()) &and; (RES.isVariable() &hArr; symbol.isVariable())
     * @throws IllegalArgumentException if the symbol is illegal for some reasons.
     *  Note that this is a rather rare case and no parsing is involved at all,
     *  which is why this method does not throw a ParseException.
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @todo think about exceptions. Could also use ParseException instead of IllegalArgumentException also simplifying exception handling in createExpression(String).
     */
    Expression createAtomic(Symbol symbol) throws IllegalArgumentException;

    // Create a <dfn>compound term</dfn> expression

    //@TODO complete the design of any additional(?) methods
    /**
     * Create a compound expression representation with a composition operation.
     * Connects expressions with a compositor<!-- @todo always a connector or formator? -->
     * to form a <a href="Expression.html#compositeExpression">complex</a> expression.
     * <p>
     * {@link Signature#get(String,Object[])} may be useful for determining the right functor symbol
     * for a composition in case of an {@link #createAtomic(Symbol) atomic} compositor.
     * </p>
     * <p>
     * <span style="float: left; font-size: 200%">&#9761;</span>
     * Be aware that this method does a composition (in the sense of semiotics) of signs/expressions,
     * but not usually a composition (in the sense of mathematics) of functions. Mathematically
     * speaking, the composition that this method performs would usually be called application
     * instead of composition. Although composition (in the sense of mathematics) and application
     * are correlated, they have different types at first sight
     * <div style="text-align:center;">{@link orbital.logic.functor.Functionals#compose &#8728;}:<span class="type">(&sigma;&rarr;&tau;)&times;(&tau;'&rarr;&sigma;') &rarr; (&tau;&rarr;&tau;')</span>; (g,f) &#8614; g&#8728;f = (x&#8614;g(f(x))), provided that <span class="type">&sigma;'</span> &le; <span class="type">&sigma;</span></div>
     * <div style="text-align:center;">{@link orbital.logic.functor.Functionals#apply _(_)}:<span class="type">(&sigma;&rarr;&tau;)&times;&sigma;' &rarr; &tau;</span>; (f,x) &#8614; f(x) provided that <span class="type">&sigma;'</span> &le; <span class="type">&sigma;</span></div>
     * Yet together with {@link orbital.logic.trs.Substitutions#lambda &lambda;}-abstraction,
     * composition can be expressed in terms of application (as the definition above shows).
     * And in conjunction with the (selective) identification of type
     * <span class="type">void&rarr;&sigma;'</span> with <span class="type">&sigma;'</span>
     * application can also be expressed per composition.
     * </p>
     * @param compositor the expression that is used for composing the arguments.
     * @param arg the arguments <var>a</var> passed to the combining operation.
     * @preconditions compositor&ne;null
     *  &and; Types.isApplicableTo(compositor.getType(), arg)
     *  &and; compositor(arg)&isin;<i>L</i>
     *  "compositor applied to arg represents a syntactically well-formed expression"
     * @return an expression that represents the combined operation with its arguments, like in
     *  <div><code>compositor(<var>a</var><span class="operator">[</span><span class="number">0</span><span class="operator">]</span>,...,<var>a</var><span class="operator">[</span><var>a</var>.length<span class="operator">-</span><span class="number">1</span><span class="operator">]</span>)</code></div>
     * @postconditions RES&ne;null &and; RES.getType()=compositor.getType().codomain() &and; ....
     * @throws ParseException if the composition expression is syntactically malformed.
     *  Either due to a lexical or grammatical error (also due to wrong type of arguments).
     * @throws TypeException if the arguments have the wrong type for
     * composition, i.e.
     * &not;Types.isApplicableTo(compositor.getType(), arg).  Note
     * that type errors are still a kind of syntactic errors, but can be
     * separated from pure parse exceptions in order to simplify distinctions.
     * @internal this is a meta-operator. We could also choose a simpler compositor part orbital.logic.imp.Symbol but would then need an undefined language primitive "apply" for compose("apply",{f,a}) = f(a). So this formal trick soon looses its simplicity and thus is inferior to the approach of compositors in Term(&Sigma;) instead of just &Sigma;.
     * @internal understanding semiotic composition as functional apply is somewhat superior to understanding it as mathematical composition, since composition can in conjunction with lambda abstraction easily be expressed with application (also for higher arities whose composition is less canonical). Whereas mathematical composition would still need handling of the identification of void->t with t.
     * @internal understanding it as a combination of application and composition is always possible from a type theory point of view (apart from composing fg from g:X->{g} which is a kind of Russel paradoxon). However we chose to separate those (related but) distinct concepts.
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    Expression compose(Expression compositor, Expression[] arg) throws ParseException, TypeException;

}
