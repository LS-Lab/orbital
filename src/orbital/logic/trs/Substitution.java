/**
 * @(#)Substitution.java 0.9 2001/06/20 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.trs;

import orbital.logic.functor.Function;
import java.util.Collection;

/**
 * Term substitution function.
 * <p>
 * <table>
 *   <tr>
 *     <td colspan="4">
 *       <p>A (uniform) <dfn>substitution</dfn> &sigma; is a total endomorphism &sigma;:Term(&Sigma;)&rarr;Term(&Sigma;)
 *       with</p>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <p>(m)</p>
 *     </td>
 *     <td>
 *       <p>&sigma;(f(t<sub>1</sub>,...,t<sub>n</sub>))</p>
 *     </td>
 *     <td>
 *       <p>= &sigma;(f)(&sigma;(t<sub>1</sub>),...,&sigma;(t<sub>n</sub>))</p>
 *     </td>
 *     <td>
 *       <p>&forall;f(t<sub>1</sub>,...,t<sub>n</sub>)&isin;Term(&Sigma;)</p>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <p>(fin)</p>
 *     </td>
 *     <td>
 *       <p>&sigma;|<sub>V</sub></p>
 *     </td>
 *     <td>
 *       <p>= id p.t.</p>
 *     </td>
 *     <td>
 *       <p>(&hArr; supp(&sigma;) := {x&isin;V &brvbar; &sigma;(x)&ne;x} is finite)</p>
 *     </td>
 *   </tr>
 * </table>
 * Note: Substitutions are usually restricted to "proper" <dfn>variable substitutions</dfn> that
 * only substitute variables, i.e. &sigma;(f)=f for functions and predicates.
 * Often, variable substitutions are even restricted to
 * variable substitutions that only substitute free variables,
 * such that it does not lead to collisions. Otherwise the application of a
 * variable substitution would possibly introduce new (illegal) bindings inside the scope of a
 * quantifier.
 * </p>
 * <p>
 * Notice the rough similarity of substitutions, and (the homomorphisms of) interpretations,
 * and Herbrand-interpretations.
 * </p>
 * <p>
 * <table>
 *   <tr>
 *     <td colspan="4">
 *       <p>A basic substitution, i.e. a mapping &sigma;<sub>0</sub>:V&rarr;Term(&Sigma;)
 *       with</p>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <p>(fin)</p>
 *     </td>
 *     <td>
 *       <p>&sigma;<sub>0</sub></p>
 *     </td>
 *     <td>
 *       <p>= id p.t.</p>
 *     </td>
 *     <td>
 *       <p>(&hArr; supp(&sigma;) := {x&isin;V &brvbar; &sigma;(x)&ne;x} is finite)</p>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td colspan="4" class="UniversalMappingProperty">
 *       <p>can be extended uniquely (UP) to a variable substitution &sigma;:Term(&Sigma;)&rarr;Term(&Sigma;)
 *       such that &sigma;|<sub>V</sub> = &sigma;<sub>0</sub>
 *       </p>
 *     </td>
 *   </tr>
 * </table>
 * </p>
 * <p>
 * We denote a substitution &sigma; of supp(&sigma;)={x<sub>1</sub>,...,x<sub>n</sub>}
 * replacing x<sub>i</sub> with t<sub>i</sub> by
 * <center>
 *   &sigma; = [x<sub>1</sub>&rarr;t<sub>1</sub>,x<sub>2</sub>&rarr;t<sub>2</sub>,...,x<sub>n</sub>&rarr;t<sub>n</sub>]
 *   = [t<sub>1</sub>/x<sub>1</sub>,t<sub>2</sub>/x<sub>2</sub>,...,t<sub>n</sub>/x<sub>n</sub>]
 * </center>
 * &sigma;(t) is called an instance of the term t.
 * </p>
 * <p>
 * <dl class="def">
 * Let &sigma; be a substitution.
 *   <dt>variable renaming</dt>
 *   <dd>
 *     &sigma; is a variable renaming :&hArr;
 *     &sigma;:V&rarr;V is injective &hArr;
 *     &sigma;(V)&sube;V &and; &sigma;|<sub>supp(&sigma;)</sub> is injective
 *   </dd>
 *   <dt>idempotent</dt>
 *   <dd>
 *     &sigma; is idempotent :&hArr; &sigma; &#8728; &sigma; = &sigma;
 *     &hArr; supp(&sigma;)&cap;&sigma;(supp(&sigma;))=&empty;
 *     &hArr; &sigma; = &sigma; &cap; &sigma;, i.e. no variable x<sub>i</sub> occurs in a t<sub>j</sub>.
 *   </dd>
 * </dl>
 * If a variable substitution has a left-inverse variable substitution,
 * then it is only a variable renaming.
 * For variable substitutions that have a right-inverse variable substitution
 * this is true if their supports are disjunct.
 * </p>
 * <blockquote>
 * Substitutions are for computer science what permutations (finite symmetric group) are for mathematics.
 * <!-- especially consider that Cayley proved that every group can be embedded into a permutation group -->
 * </blockquote>
 *
 * @version 1.0, 2002/02/19
 * @version 0.9, 2001/06/20
 * @author  Andr&eacute; Platzer
 * @see Substitutions
 * @see Substitutions#getInstance(Collection)
 * @todo perhaps introduce a general BoundingOperator (like in &lambda;,&forall;,&exist; which stops us substitutions from descending if we enter its scope.
 *  boolean BoundingOperator.isBounded(Variable x)
 * @todo Especially provide forall as a functional (higher-order function) of &lambda;-operator then
 *  &forall;x F := &forall;(&lambda;x.F)
 *  &exist;x F := &exist;(&lambda;x.F)
 *  With the {@link SubstitutionImpl#lambda &lambda;-operator}, and
 *    &forall;:(Variable&rarr;Formula)&rarr;Formula; (x&#8614;F) &#8614; "alle F"
 *    &forall; has type Function<Function<Variable,Formula>,Formula>
 */
public interface Substitution extends Function/*<Object, Object>*/ {
    /**
     * Get the set of elementary replacements.
     * <p>
     * For a substitution &sigma; = [x<sub>1</sub>&rarr;t<sub>1</sub>,x<sub>2</sub>&rarr;t<sub>2</sub>,...,x<sub>n</sub>&rarr;t<sub>n</sub>]
     * the set of elementary replacements is
     * {x<sub>1</sub>&rarr;t<sub>1</sub>,x<sub>2</sub>&rarr;t<sub>2</sub>,...,x<sub>n</sub>&rarr;t<sub>n</sub>}.
     * </p>
     * <p>
     * Note that the return-type is not fixed to sets, but would just as well allow lists
     * as implementations although the order is not relevant for variable substitutions.
     * </p>
     * @return the substitution list of matchers used for replacement.
     */
    Collection/*_<Matcher>_*/ getReplacements();
	
    /**
     * Apply this substitution &sigma; to term.
     * <p>
     * A (uniform) substitution [x&rarr;t] replaces all occurrences of x with t.
     * Whereas for substitutions with multiple replacement directions
     * [x<sub>1</sub>&rarr;t<sub>1</sub>,x<sub>2</sub>&rarr;t<sub>2</sub>,... x<sub>n</sub>&rarr;t<sub>n</sub>],
     * only the first applicable replacement will be applied on subterms.
     * </p>
     * @return &sigma;(term)
     * @pre the {@link orbital.logic.functor.Functor.Composite}s occuring in term support a no-arg constructor
     *  in order to allow being substituted by an object of equal class
     * @throws ArrayStoreException if this method tried to store a part of the result in an array,
     *  but the substitution list produced a replacement of an illegal type.
     * @throws ClassCastException if the substitution list produced a replacement of an illegal type.
     */
    Object apply(Object term);

    /**
     * Interface for matching and replacing terms.
     * <p>
     * Matcher is called to check whether a given term matches it, and can then be used
     * to replace it with another term, if required.</p>
     *
     * @version 0.9, 2001/06/21
     * @author  Andr&eacute; Platzer
     * @see Substitutions#createExactMatcher(Object, Object)
     * @see Substitutions#createExactMatcher(Object)
     * @see Substitutions#createSingleSidedMatcher(Object, Object)
     * @see Substitutions#createSingleSidedMatcher(Object, Object)
     * @todo improve design, document, rename, move?
     */
    public static interface Matcher {
    	
    	/**
    	 * Whether this matcher equals another.
    	 * Two matchers are equal if they describe the same elementary replacements.
    	 * <p>
    	 * Note that it is usually safe to compare for type and pattern only, regardless
    	 * of the substitute. But carefully avoid using "substitutions" with multiple
    	 * replacements specified for a single pattern.
    	 * </p>
    	 */
    	boolean equals(Object o);
    	
    	int hashCode();
    
        // get/set methods

        /**
         * Get the pattern descriptor.
         * @return an object that describes the pattern against which to match.
         * @internal note Also required for Substitutions.compose(Substitution,Substitution) to work.
         */
        Object pattern();

        /**
         * Get the substitute to replace a match with.
         * @return the substitute, or <code>null</code> if this matcher does not perform substitutions.
         * @post RES == null &hArr; &not;isSubstituting()
         */
        //@todo ? Object substitute();

    	/**
    	 * Attempts to match t against this.
    	 * @return whether t matches this Matcher, which is thus applicable, here.
    	 */
    	boolean matches(Object t);
    	
    	/**
    	 * Replace a matched term.
    	 * @param t the term matched per {@link #matches(Object)}.
    	 * @return the substitute for t. Simply returns t if no replacement should occur at all.
    	 * @pre matches(t)
    	 */
    	Object replace(Object t);
        
    }
}
