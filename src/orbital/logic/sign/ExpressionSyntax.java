/**
 * @(#)ExpressionSyntax.java 1.0 2000/02/23 Andre Platzer
 * 
 * Copyright (c) 2000-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

/**
 * Defines general methods for constructing and handling expressions of a language.
 * Implementations of this interface are responsible for analyzing and constructing expressions (like terms, formulas)
 * in the language of the corresponding term algebra defining a specific syntax.
 * <p>
 * ExpressionSyntax defines a formal language over an alphabet and thus has a corresponding
 * description with a Chomsky grammar.
 * Implementations define the concrete syntax for the set of legal expressions
 * over a given signature &Sigma; (including a set of variables V).
 * Its language <i>L</i>(&Sigma;) is the set of all well-formed expressions according to this syntax.
 * It is usually a requirement that this language <i>L</i>(&Sigma;) is a decidable set of finite objects
 * over the alphabet &Sigma;, and the alphabet &Sigma; itself is decidable, as well.
 * </p>
 * <p>
 * Note that the null pointer <code class="keyword">null</code> is neither an expression
 * nor a symbol,
 * i.e. we generally assume that <code class="keyword">null</code>&notin;<i>L</i>(&Sigma;)&cup;&Sigma;.
 * </p>
 * 
 * @invariant true
 * @version 1.0, 2000/02/23
 * @author  Andr&eacute; Platzer
 */
public interface ExpressionSyntax extends ExpressionBuilder {
	
    // get/set Properties
	
    /**
     * Get the core signature which is supported by the language of this expression syntax.
     * <p>
     * The core "signature" contains the logical signs that inherently belong to this term algebra
     * and are not subject to interpretation.
     * Logical signs are logical constants like <span class="keyword">true</span>, <span class="keyword">false</span>,
     * and logical operators like &not;, &and;, &or;, &rarr;, &forall;, &exist;.
     * The latter are also called logical junctors.
     * </p>
     * <p>
     * Note that some authors do not count the core "signature" as part of the proper signature &Sigma;
     * but would rather call it "meta"-signature.
     * </p>
     * @return the core signature that is valid for every expression following this syntax.
     *  Elements in the core signature all have a fixed interpretation.
     * @post RES == OLD(RES) &and; RES unmodifiable
     * @see Logic#coreInterpretation()
     */
    Signature coreSignature();

    // methods for querying information about an expression
	
    /**
     * Scan for the signature &Sigma; of all syntactic symbols in an expression.
     * <p>
     * However, note that this method does not necessarily perform rich type querying.
     * Especially for user-defined functions with an arbitrary argument-type structure,
     * it is generally recommended to construct the relevant signature entries explicitly.
     * </p>
     * @param expression the expression that should be scanned for symbol names.
     * @pre expression&isin;<i>L</i>
     * @return Signature of the syntactic symbols in expression except those of the core signature.
     * @post createExpression(expression) instanceof Formula &rArr; createExpression(expression).getSignature().equals(scanSignature(expression))
     * @throws ParseException when the expression is syntactically malformed.
     *  Either due to a lexical or grammatical error.
     *  (optional behaviour for performance reasons)
     *  Will not throw ParseExceptions if createExpression would not either.
     * @see #coreSignature()
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">&quot;Factory Method&quot;</a>
     */
    Signature scanSignature(String expression) throws ParseException;

    /**
     * Create a term representation by parsing a (compound) expression.
     * @param expression the compound expression to parse.
     *  A string of <code class="String">""</code> denotes the empty expression.
     *  However note that the empty expression may not be accepted in some term algebras.
     *  Those parsers rejecting the empty expression are then inclined to throw a ParseException,
     *  instead.
     * @pre expression&isin;<i>L</i>(scanSignature(expression))
     * @return an instance of Expression that represents the given expression string in this logic.
     * @throws ParseException when the expression is syntactically malformed.
     *  Either due to a lexical or grammatical error.
     * @throws IllegalArgumentException if the symbol is illegal for some reasons.
     *  This may occur like in {@link ExpressionBuilder#createAtomic(Symbol)}, and due to the same reasons.
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @note could just as well be renamed to parseExpression(String)
     */
    Expression createExpression(String expression) throws ParseException, IllegalArgumentException;

    //@todo introduce? Expression[] createAllExpressions(String expressions) throws ParseException; No its not very useful in general case.

}
