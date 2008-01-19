/**
 * @(#)Symbol.java 1.0 2000/08/11 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Predicate;

/**
 * Represents an algebraic or transcendental symbol.
 * <p>
 * Algebraic or transcendental symbols behave like arithmetic objects,
 * with the exception that the result of an operation may not have a
 * numeric value but require a symbolic representation again.
 * </p>
 * <p> Variables and constants are symbols, for example. Roots of
 * radicals can sometimes be represented as symbols. </p>
 * <p>Formally, symbols are elements of a term algebra, which is essentially
 * the free symbolic algebra without relations,
 * see, e.g., {@link ValueFactory#quotient(Arithmetic,orbital.logic.functor.Function)}
 * or {@link AlgebraicAlgorithms#reduce(Polynomial,java.util.Collection,java.util.Comparator)}
 * for generating relations.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Values#symbol(String)
 */
public interface Symbol extends Arithmetic, orbital.logic.trs.Variable {
    /**
     * Get the signifier representing this symbol (the symbol's name).
     * @preconditions true
     * @see orbital.logic.sign.Symbol#getSignifier()
     */
    String getSignifier();
        
    /**
     * Compares for tolerant equality.
     * @return true if this.equals(o), or if tolerance=&infin;.
     */
    /*final*/ boolean equals(Object o, Real tolerance);

    /**
     * @todo
     */
    /*final*/ boolean equals(Object o);

    /**
     * @return getSignifier().hashCode().
     */
    /*final*/ int hashCode();

    /**
     * Checks whether the given arithmetic object is a symbol.
     * @return whether v is a symbol.
     */
    public static final Predicate/*<Object>*/ isa = new Predicate/*<Object>*/() {
            public boolean apply(Object v) {
                return v instanceof Symbol;
            }
        };
}
