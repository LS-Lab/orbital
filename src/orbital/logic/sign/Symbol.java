/**
 * @(#)Symbol.java 1.0 2001/04/07 Andre Platzer
 *
 * Copyright (c) 2001-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.trs.Variable;
import orbital.logic.functor.Notation.NotationSpecification;

/**
 * Represents a symbol of a signature.
 * A symbol is a triple &lang;<var class="signifier">signifier</var>, type, notation&rang;
 * consisting of a signifier, its {@link Type type} specification, and its notation.
 * Each symbol is either a constant symbol or a variable symbol.
 * <p>
 * Symbols can be names for various kinds of objects in the (logical) universe:
 * <ul>
 *   <li>function-symbols for functions of type {@link Types#map(Type,Type) <span class="type">&sigma;&rarr;&tau;</span>}.
 *     A function <var class="meta">f</var> of arity (number of arguments) <var class="meta">n</var>
 *     is sometimes denoted as <var class="meta">f</var>/<var class="meta">n</var>.
 *     Function-symbols especially include
 *     <ul>
 *       <li>constant symbols of type {@link Types#INDIVIDUAL <span class="type">&iota;</span>} (as functions of arity 0).
 *         <h5 class="compact">Note</h5>
 *         however, that we could as well avoid constant symbols, and just use defined free variables, instead.
 *         Or if we restrict sets of formulas to finite sets, then we could just as well turn constants
 *         into existentially quantified variables.
 *       </li>
 *     </ul>
 *   </li>
 *   <li>predicate-symbols for relations of type {@link Types#predicate(Type) <span class="type">(&sigma;)</span>}
 *     <ul>
 *       <li>atomic propositions of type {@link Types#TRUTH <span class="type">&omicron;</span>} (as predicates of arity 0).</li>
 *       <li>properties of objects (represented as unary predicates).</li>
 *       <li>relations between objects (represented as n-ary predicates).</li>
 *     </ul>
 *   </li>
 *   <li>types <span class="type">&tau;</span> of type {@link Types#TYPE <span class="type">*</span>}
 *       and the corresponding type identifier predicates <span class="type">&tau;</span> of type {@link Types#predicate(Type) <span class="type">(&#8868;)</span>}.
 *   </li>
 *   <li>type constructors, f.ex. of type <span class="type">(*&rarr;*)&rarr;*</span> or any other kind.</li>
 *   <li>variables of a set V&sube;&Sigma;, where
 *     <ul>
 *       <li>object variables are variable terms of type <span class="type">&iota;</span>.</li>
 *       <li>function-variables are variable "frames" of functions.</li>
 *       <li>predicate-variables are variable "frames" of relations.</li>
 *     </ul>
 *   </li>
 * </ul>
 * </p>
 * <p>
 * There should never be two equal symbols (i.e. with equal signifier, type, and notation)
 * with one being constant, and the other variable. And it is also recommended not to use
 * symbols of equal signifier and type but with different notations (except perhaps if they
 * have the same semantics).
 * </p>
 *
 * @invariant true
 * @stereotype &laquo;Structure&raquo;
 * @structure is String&times;Type&times;Notation.NotationSpecification
 * @structure extends orbital.logic.trs.Variable
 * @structure extends Comparable<Symbol,Symbol>
 * @version 1.0, 2001/04/07
 * @author  Andr&eacute; Platzer
 * @see Signature
 * @todo done? respect orbital.logic.trs.Variable
 * @todo is there a difference between symbol (Symbol) and sign (Zeichen) or signifier()? Should we rename this class to Sign, or keep the name Symbol in order to underline that the usual signs in logic are used purely symbolically and not with any necessity (either proper or lawful)
 */
public interface Symbol extends Variable, Comparable/*<Symbol>*/{
    // triple &lang;signifier, type, notation&rang; constraints to equality
    
    /**
     * Compares two symbols for equality according to their three components.
     * Two symbols are equal if they have the same signifier, type specification and notation.
     * Then it should be asserted that they are either both variable, or both constant,
     * because the sets of variable symbols and constant symbols are usually assumed disjunct.
     * @post o instanceof Symbol &rarr; (RES &hArr; (getSignifier().equals(o.getSignifier()) &and; getType().equals(o.getType()) &and; getNotation().equals(o.getNotation())))
     */
    /*final*/ boolean equals(Object o);
    /**
     * Returns the hash code value for this symbol.
     * The hash code of a symbol is defined to be the bitwise exclusive or of its components:
     * signifier, type specification, and notation.
     * @pre true
     * @post getSignifier() xor getType() xor getNotation()
     */
    /*final*/ int hashCode();

    // get/set properties
    /**
     * Get the signifier representing this symbol.
     * <p>
     * Synonym(!): sign (C.S.Peirce), signifier (Saussure), token.
     * Depending upon terminology, the "name" of a symbol is known as
     * sign, signifier or token.
     * </p>
     * <p>
     * Generally the three constituents - according to C.S.Peirce - 
     * of a sign are (in their special notations, and with todays most common terminology)
     * <ul>
     *   <li>
     *     <var class="signifier">signifier</var>
     *     <!-- <span xml:lang="de">Signifikans</span> synonym: Repraesentamen (C.S.Peirce)  -->
     *   </li>
     *   <li>
     *     <var class="signified">signified</var> (especially in computer science these are often C.S.Peirce's interpretants).
     *     <!-- <span xml:lang="de">Signifikat</span> -->
     *   </li>
     *   <li>
     *     <var class="referent">object</var>
     *     <!-- synonym: referent -->
     *   </li>
     * </ul>
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
     * Get the type specification of this symbol.
     * @pre true
     * @return the type specification <span class="type">&tau;</span> of this symbol.
     * @see Expression#getType()
     */
    Type getType();
    /**
     * Set the type specification of this symbol.
     * @param type the type specification <span class="type">&tau;</span> of this symbol.
     * @pre type&ne;null
     * @todo wouldn't we prefer <span class="keyword">null</span> for the bottom type, or the undefined type, or perhaps the errorneous type Types.ERROR?
     */
    void setType(Type type);

    /**
     * Get the notation used when this symbol occurs.
     * This includes precedence and associativity information, as well.
     * The notation is not truely part of the abstract syntax of a formal language,
     * but still useful for formatting and parsing. For this reason, the notation is
     * included as a non-obligate recommendation.
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
}
