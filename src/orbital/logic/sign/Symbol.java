/**
 * @(#)Symbol.java 1.0 2001/04/07 Andre Platzer
 *
 * Copyright (c) 2001-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.trs.Variable;
import orbital.logic.functor.Notation.NotationSpecification;
import orbital.logic.functor.Functor.Specification;

/**
 * Represents a symbol in a signature.
 * A (syntactic) symbol is a triple &lang;<var class="signifier">signifier</var>, spec, notation&rang;
 * consisting of a signifier, its (arity and) type specification, and its notation.
 * Each symbol is either a constant symbol or a variable symbol.
 * <p>
 * Symbols can be names for various kinds of objects in the (logical) universe:
 * <ul>
 *  <li>function-symbols for functions of type &sigma;&rarr;&tau;,
 *    with a specified arity denoted as function/<var>arity</var>,
 *    especially including
 *    <ul>
 *      <li>constant symbols (as functions of arity 0).
 *        <h5 class="compact">Note</h5>
 *        however, that we could as well avoid constant symbols, and just use defined free variables, instead.
 *        Or if we restrict sets of formulas to finite sets, then we could just as well turn constants
 *        into existentially quantified variables.
 *      </li>
 *    </ul>
 *  </li>
 *  <li>predicate-symbols for relations of type (&sigma;)
 *    <ul>
 *      <li>atomic propositions (as predicates of arity 0).</li>
 *      <li>properties of objects (represented as unary predicates)</li>
 *      <li>relations between objects (represented as n-ary predicates)</li>
 *    </ul>
 *  </li>
 *  <li>variables of a set V&sube;&Sigma;, where
 *    <ul>
 *      <li>object variables are variable terms of type t</li>
 *      <li>function-variables are variable "frames" of functions</li>
 *      <li>predicate-variables are variable "frames" of relations</li>
 *    </ul>
 *  </li>
 *  <li>(perhaps types &tau; and type identifier predicates &tau;)</li>
 * </ul>
 * </p>
 * <p>
 * There should never be two equal symbols (i.e. with equal signifier, spec, and notation)
 * with one being constant, and the other variable.
 * </p>
 * <p>
 * Depending upon terminology, the "name" of a symbol is known as
 * sign, signifier or token.
 * </p>
 *
 * @invariant true
 * @stereotype &laquo;Structure&raquo;
 * @structure is String&times;Functor.Specification&times;Notation.NotationSpecification
 * @structure extends orbital.logic.trs.Variable
 * @structure extends Comparable<Symbol,Symbol>
 * @version 1.0, 2001/04/07
 * @author  Andr&eacute; Platzer
 * @see Signature
 * @todo done? respect orbital.logic.trs.Variable
 * @todo is there a difference between symbol (Symbol) and sign (Zeichen) or signifier()? Should we rename this class to Sign, or keep the name Symbol in order to underline that the usual signs in logic are used purely symbolically and not with any necessity (either proper or lawful)
 * @todo perhaps we should let the user decide better whether a symbol is meant constant or variable for PL2 even for any arity and type or notation specifiers.
 */
public interface Symbol extends Variable, Comparable/*<Symbol>*/{
    // triple &lang;signifier, spec, notation&rang; constraints to equality
    
    /**
     * Compares two symbols for equality according to their three components.
     * Two symbols are equal if they have the same signifier, type specification and notation.
     * Then it should be asserted that they are either both variable, or both constant,
     * because the sets of variable symbols and constant symbols are usually assumed disjunct.
     * @post o instanceof Symbol &rarr; (RES &hArr; (getSignifier().equals(o.getSignifier()) &and; getSpecification().equals(o.getSpecification()) &and; getNotation().equals(o.getNotation())))
     */
    /*final*/ boolean equals(Object o);
    /**
     * Returns the hash code value for this symbol.
     * The hash code of a symbol is defined to be the bitwise exclusive or of its components:
     * signifier, type specification, and notation.
     * @pre true
     * @post getSignifier() xor getSpecification() xor getNotation()
     */
    /*final*/ int hashCode();

    // get/set properties
    /**
     * Get the signifier representing this symbol.
     * <p>
     * Synonym(!): sign (C.S.Peirce), signifier (Saussure), token.
     * </p>
     * @pre true
     */
    String getSignifier();
    /**
     * Set the signifier representing this symbol.
     * @pre signifier&ne;null
     */
    void setSignifier(String signifier);

    /**
     * Get the (arity and) type specification of this symbol.
     * @pre true
     * @return the (arity and) type specification &tau; of this symbol.
     */
    Specification getSpecification();
    /**
     * Set the (arity and) type specification of this symbol.
     * @param spec the (arity and) type specification of this symbol.
     * @pre spec&ne;null
     */
    void setSpecification(Specification spec);

    /**
     * Get the notation used when this symbol occurs.
     * This includes precedence and associativity information, as well.
     * @pre true
     * @return the notation used when this symbol occurs.
     */
    NotationSpecification getNotation();
    /**
     * Set the notation used when this symbol occurs.
     * This includes precedence and associativity information, as well.
     * @param notation the notation used when this symbol occurs.
     * @pre notation&ne;null
     */
    void setNotation(NotationSpecification notation);
    
    /**
     * Whether this symbol is a variable symbol.
     * <p>
     * Note that the distinction between variable symbols and constant symbols is independent
     * of the distinction between variable and constant interpretant functions.
     * Especially, it is independent of the arity of this symbol.
     * </p>
     * <p>
     * Even though in the case of first-order logic, only object variables can occur,
     * higher-order logic also provides function-variable symbols and predicate-variables.
     * Then there truly is the terminologically confusing special case of variable symbols for
     * constant functions (i.e. functions of arity 0).
     * </p>
     * @pre true
     * @post RES==OLD(RES)
     * @return <code>true</code> if this symbol is a variable symbol,
     *  and <code>false</code> if this symbol is a constant symbol.
     * @see orbital.logic.trs.Variable
     */
    boolean isVariable();
    
    // type compatibility query method

    /**
     * Checks whether our type specification is compatible with the given list of arguments.
     * @pre true
     * @param args the arguments to check for compatibility with this symbol.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @return whether the arguments are assignable to the required parameter types of this symbol.
     *  This especially includes whether the number of arguments matches this symbol's arity.
     * @see orbital.logic.functor.Functor.Specification#isCompatible(orbital.logic.functor.Functor.Specification)
     */
    public boolean isCompatible(Object[] args);
}
