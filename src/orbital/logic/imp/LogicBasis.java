/*
 * @(#)LogicBasis.java 0.9 1999/01/12 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.logic.sign.Symbol;

/**
 * This abstract LogicBasis class derives the extended logic operations
 * depending upon basic logic operations.
 * <p>
 * Extended logic operations are emulated with the simpler logic operations.
 * Then only the simpler operations must be implemented for a logic.
 * This is not valid for all Logics, but for ClassicalLogic and derivatives.</p>
 * <p>
 * All formulas of classical logic can be transformed into formulas with a reduced
 * subset of logical operators applied. This is due to the fact that some logical operators
 * themselves can be written with a smaller subset of operators.
 * Some operator systems sufficient for classical first-order predicate logic are
 * <ul>
 *   <li>(&and;, &or;, &not;, true, false, &forall;)</li>
 *   <li>(&and;, &or;, &not;, &forall;)</li>
 *   <li>(&and;, &not;, &forall;)</li>
 *   <li>(&or;, &not;, &forall;)</li>
 *   <li>(&rarr;, false, &forall;)</li>
 *   <li>(&rarr;, &not;, &forall;) the Frege logic</li>
 *   <li>(<span style="text-decoration: overline">&and;</span>, &forall;) with a <span style="text-decoration: overline">&and;</span> b = <span style="text-decoration: overline">a &and; b</span> = &not;(a &and; b) denoting nand-operator.</li>
 *   <li>(<span style="text-decoration: overline">&or;</span>, &forall;) with a <span style="text-decoration: overline">&or;</span> b = <span style="text-decoration: overline">a &or; b</span> = &not;(a &or; b) denoting nor-operator.</li>
 * </ul>
 * </p>
 * 
 * @version 0.9, 1999/01/12
 * @author  Andr&eacute; Platzer
 * @see orbital.moon.logic.ClassicalLogic
 */
public abstract class LogicBasis implements Formula {

    // Derived logical operations. (in classical logic)

    /**
     * Exclusion xor: <code>A xor B</code> is calced <code>(A&and;&not;B) &or; (&not;A&and;B)</code>
     */
    public Formula xor(Formula B) {
	return and(B.not()).or(not().and(B));
    } 

    /**
     * Implication impl: <code>A &rarr; B</code> is calced <code>&not;A &or; B</code>
     */
    public Formula impl(Formula B) {
	return not().or(B);
    } 

    /**
     * Equivalence equiv: <code>A &hArr; B</code> is calced <code>(A&rarr;B) &and; (B&rarr;A)</code>
     */
    public Formula equiv(Formula B) {
	return impl(B).and(B.impl(this));
    } 


    /**
     * All-quantifier forall: <code>&forall;<var>x</var> A</code> is calced <code>&not;&exist;<var>x</var> &not;A</code>.
     * <code><var>x</var></code> for all elements of the world.
     * <p>
     * Should be overwritten to throw UnsupportedOperationException if neither
     * forall nor exists are supported.</p>
     */
    public synchronized Formula forall(Symbol x) {
	if (quantifier_called)
	    throw new UnsupportedOperationException("Neither 'forall' nor 'exists' quantifiers are supported, cannot emulate");
	try {
	    quantifier_called = true;
	    return not().exists(x).not();
	} 
	finally {
	    quantifier_called = false;
	} 
    } 

    /**
     * Mutex call marker for forall and exists methods. Both cannot be emulated with each other.
     * Avoid infinite recursion.
     */
    private boolean quantifier_called = false;

    /**
     * Existence-quantifier exists: <code>&exist;<var>x</var> A</code> is calced <code>&not;&forall;<var>x</var> &not;A</code>.
     * <code><var>x</var></code> is an element of the world.
     * <p>
     * Should be overwritten to throw UnsupportedOperationException if neither
     * forall nor exists are supported.</p>
     */
    public synchronized Formula exists(Symbol x) {
	if (quantifier_called)
	    throw new UnsupportedOperationException("Neither 'forall' nor 'exists' quantifiers are supported, cannot emulate");
	try {
	    quantifier_called = true;
	    return not().forall(x).not();
	} 
	finally {
	    quantifier_called = false;
	} 
    } 
}
