/**
 * @(#)Substitutions.java 0.9 2001/06/20 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.trs;
import orbital.moon.logic.bridge.SubstitutionImpl;
import orbital.logic.trs.Substitution.Matcher;

import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;

import orbital.logic.functor.Functor;
import orbital.logic.functor.Predicate;

import java.util.List;

import java.util.Iterator;
import java.util.Collection;

import java.util.Set;
import java.util.ListIterator;

import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import orbital.util.Setops;

import orbital.util.Utility;

/**
 * Provides term substitution and unification methods, and the &lambda;-operator.
 * <p>
 * You can easily run a (possibily even infinite) Term Rewrite System (TRS) with substitutions,
 * like
 * <pre>
 * <span class="comment">// instantiate a substitution performing elemental term rewrite rules</span>
 * <span class="Orbital">Substitution</span> &sigma; <span class="operator">=</span> ...;
 * <span class="comment">// for example, use explicit &sigma; = [x*e&rarr;x]</span>
 * &sigma; <span class="operator">=</span> <span class="Orbital">Substitutions</span>.getInstance(<span class="Class">Arrays</span>.asList(<span class="keyword">new</span> <span class="Class">Object</span><span class="operator">[]</span> {
 *	<span class="Orbital">Substitutions</span>.createExactMatcher(<span class="Orbital">Operations</span>.times.apply(<span class="Orbital">Values</span>.symbol(<span class="String">"x"</span>), <span class="Orbital">Values</span>.symbol(<span class="String">"e"</span>)), <span class="Orbital">Functions</span>.constant(<span class="Orbital">Values</span>.symbol(<span class="String">"x"</span>)))
 * }));
 * <span class="comment">// run the Term Rewrite System with <var>argument</var> as input, upon termination</span>
 * <span class="Class">Object</span> result <span class="operator">=</span> <span class="Orbital">Functionals</span>.fixedPoint(&sigma;, <var>argument</var>);
 * </pre>
 * However, you might prefer {@link orbital.moon.logic.MathParser#createExpression(String) parsing expressions}
 * for some elements of the substitution list, like in
 * <pre>
 * <span class="comment">// for example, use parsed &sigma; = [x*e&rarr;x]</span>
 * &sigma; <span class="operator">=</span> <span class="Orbital">Substitutions</span>.getInstance(<span class="Class">Arrays</span>.asList(<span class="keyword">new</span> <span class="Class">Object</span><span class="operator">[]</span> {
 *	<span class="Orbital">Substitutions</span>.createExactMatcher(<span class="Orbital">MathParser</span>.createExpression(<span class="String">"x*e"</span>), <span class="Orbital">Functions</span>.constant(<span class="Orbital">Values</span>.symbol(<span class="String">"x"</span>)))
 * }));
 * </pre>
 * </p>
 * <p>
 * Term rewriting systems are often used to define operational semantics.
 * </p>
 * <p>
 * Additionally, there are a incredibly many applications of the {@link #lambda &lambda;-operator}
 * in various kinds of context.
 * </p>
 *
 * @version 0.9, 2001/06/20
 * @author  Andr&eacute; Platzer
 * @see <a href="{@docRoot}/DesignPatterns/Facade.html">Facade Pattern</a>
 * @see #lambda
 */
public class Substitutions {

    /**
     * prevent instantiation - module class
     */
    private Substitutions() {}

    /**
     * The identical substitution id=[].
     * <p>
     * This substitution has an empty list of replacements and thus performs nothing.
     * </p>
     */
    public static final Substitution id = new Substitution() {
	    public Collection/*_<Matcher>_*/ getReplacements() {
		return Collections.EMPTY_SET;
	    }
	    public Object apply(Object a) {
		return a;
	    }
	};


    // facade factory
	
    /**
     * Create a new substitution.
     * <p>
     * Note that you should not try to instantiate a "substitution" with multiple replacements
     * specified for a single pattern. Those are not even endomorphisms anyway.
     * </p>
     * @param replacements the set of elementary replacements.
     * @pre replacements[i] instanceof {@link Substitution.Matcher} &and; &forall;i&ne;j replacements[i].pattern()&ne;replacements[j].pattern()
     * @return &sigma; = [replacements].
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static final Substitution getInstance(Collection/*_<Matcher>_*/ replacements) {
    	assert validateDistinctPatterns(replacements) : "multiple elementary replacements with the same pattern do not form a true substitution: " + replacements;
        return new SubstitutionImpl(replacements);
    }
    // assert precondition of Substitutions.newInstance()
    private static final boolean validateDistinctPatterns(Collection/*_<Matcher>_*/ replacements) {
	final List/*_<Matcher>_*/ r = (replacements instanceof List)
	    ? (List) replacements
	    : new LinkedList(replacements);
	// compare each with all subsequent ones (round-robin)
	for (ListIterator i = r.listIterator(); i.hasNext(); ) {
	    final Object pattern = ((Matcher/*__*/) i.next()).pattern();
	    for (ListIterator j = r.listIterator(i.nextIndex()); j.hasNext(); )
		// compare any distinct two
		if (pattern.equals(((Matcher/*__*/) j.next()).pattern()))
		    return false;
	}
	return true;
    }
	

    /**
     * Create a new exact matcher that performs substitution.
     * <p>
     * This matcher performs exact matching with means of {@link Object#equals(Object)}, only.
     * Additionally it will directly replace with the specified substitute object.
     * </p>
     * @param pattern The object against which to match with {@link Object#equals(Object)}.
     * @substitute The substitute substituting terms that matched.
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static final Matcher createExactMatcher(Object pattern, Object substitute) {
    	return new SubstitutionImpl.MatcherImpl(pattern, substitute);
    }
    /**
     * Create a new exact matcher that does not perform substitution.
     * <p>
     * This matcher performs exact matching with means of {@link Object#equals(Object)}, only.
     * </p>
     * @param pattern The object against which to match with {@link Object#equals(Object)}.
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static final Matcher createExactMatcher(Object pattern) {
    	return new SubstitutionImpl.MatcherImpl(pattern);
    }

    /**
     * Create a new single sided matcher with unification that performs substitution.
     * <p>
     * This matcher performs (single sided) matching with means of {@link Substitutions#unify(Collection)}.
     * (See there for a formal definition of single sided matchers).
     * Additionally, if &mu;&isin;mgU({pattern, t}) is the unifier,
     * it will use &mu;(substitute) as a replacement for the specified object t.
     * </p>
     * <p>
     * <span style="float: left; font-size: 200%">&#9761;</span>
     * Beware of patterns for single sided matchers, that have variables in common
     * with the terms it is applied on. This will most possibly lead to unexpected results.
     * It is generally recommended to use uncommon variable names for these internal patterns,
     * like
     * <code>_X1, _X2, _X3, ...</code> or <code>$X1, $X2, $X3, ...</code>.
     * These uncommon variable names should not occur in regular terms then.
     * </p>
     * @param pattern The object against which to (single side) match with {@link Substitutions#unify(Collection)}.
     * @param substitute The substitute substituting terms that matched, after transforming <code>substitute</code>
     *  with the unifier that performed the matching.
     * @todo improve name (and concept)
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static final Matcher createSingleSidedMatcher(Object pattern, Object substitute) {
    	return new SubstitutionImpl.UnifyingMatcher(pattern, substitute);
    }
    /**
     * Create a new single sided matcher with unification that does not perform substitution.
     * @param pattern The object against which to (single side) match with {@link Substitution#unify(Collection)}.
     * @see #createSingleSidedMatcher(Object, Object)
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static final Matcher createSingleSidedMatcher(Object pattern) {
    	return new SubstitutionImpl.UnifyingMatcher(pattern);
    }

    // Substitution operations
	
    /**
     * compose two substitutions &sigma; &#8728; &tau;.
     * <p>
     * Get a performance optimized version of a composition of two substitutions.
     * </p>
     * @param sigma the outer substitution &sigma;.
     * @param tau the inner substitution &tau;.
     * @return &sigma; &#8728; &tau; = [x&#8614;&sigma;(t) &brvbar; (x&#8614;t) &isin; &tau;] &cup; {(x&#8614;t) &isin; &sigma; &brvbar; &not;&exist;r (x&#8614;r) &isin; &tau;}.
     * @see orbital.logic.functor.Functionals#compose(Function, Function)
     * @todo test write a test driver that asserts that Substitutions.compose equals Functional.compose
     */
    public static final Substitution compose(Substitution sigma, Substitution tau) {
	Collection/*_<Matcher>_*/ r = new ArrayList/*_<Matcher>_*/(tau.getReplacements().size() + sigma.getReplacements().size());
	// the list of patterns that tau searches for (and could thus must be ignored in sigma)
	Set/*_<Object>_*/ tauPatterns = new HashSet/*_<Object>_*/(tau.getReplacements().size() << 1);
	// apply sigma to substitutes of tau
	for (Iterator/*_<Matcher>_*/ i = tau.getReplacements().iterator(); i.hasNext(); ) {
	    Object o = i.next();
	    if (o.getClass() != SubstitutionImpl.MatcherImpl.class)
		throw new UnsupportedOperationException("currently, only exact matchers are supported");
	    SubstitutionImpl.MatcherImpl s = (SubstitutionImpl.MatcherImpl/*__*/) o;
	    //@todo is there any generic way, too, not relying on MatcherImpl? Perhaps clone and/or set... but with modified substitue, oh my...
	    r.add(createExactMatcher(s.pattern(), sigma.apply(s.substitute())));
	    tauPatterns.add(s.pattern());
	}
	// union with those replacements in sigma that do not have a pattern that tau already replaced
	for (Iterator/*_<Matcher>_*/ i = sigma.getReplacements().iterator(); i.hasNext(); ) {
	    Matcher s = (Matcher/*__*/) i.next();
	    if (tauPatterns.contains(s.pattern()))
		continue;
	    else
		r.add(s);
	}
	Substitution sigma_tau = getInstance(r);
	assert null != (sigma_tau = new ResultCheckingSubstition(sigma, tau, r)) : "if we use assertions than also assert the results of the composition returned";
	return sigma_tau;
    }
    private static final class ResultCheckingSubstition extends SubstitutionImpl {
	private final Function composed;
	private ResultCheckingSubstition(Substitution sigma, Substitution tau, Collection/*_<Matcher>_*/ r) {
	    super(r);
	    this.composed = orbital.logic.functor.Functionals.compose(sigma, tau);
	}
	public Object apply(Object o) {
	    final Object os = super.apply(o);
	    final Object oc = composed.apply(o);
	    assert orbital.util.Utility.equalsAll(os, oc) : "Substitution composition and Function composition are extensionally equal. So the results " + os + "=" + oc + " are equal.";
	    return os;
	}
    }
	
    // the lambda operator

    /**
     * The &lambda;-operator of &lambda;-Calculus.
     * <div>&lambda;:Variable&times;Expression&rarr;(Variable&rarr;Expression); (&lambda;x.f) &#8614; (x&#8614;f)</div>
     * <p>
     * The &lambda;-Calculus of Alonzo Church (1930) has the following inference rules
     * called &alpha;-conversion, &beta;-conversion, and &eta;-conversion.
     * <table>
     *   <tr>
     *     <td>
     *       (&alpha;)
     *     </td>
     *     <td>
     *       &lambda;v.t = &lambda;w.t[v&rarr;w]
     *     </td>
     *     <td>
     *       if [v&rarr;w] admissible
     *     </td>
     *     <td>
     *       "bound rename"
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       (&beta;)
     *     </td>
     *     <td>
     *       (&lambda;v.t) s = t[v&rarr;s]
     *     </td>
     *     <td>
     *       if [v&rarr;s] admissible
     *     </td>
     *     <td>
     *       "apply"
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       (&eta;)
     *     </td>
     *     <td>
     *       &lambda;v.(t v) = t
     *     </td>
     *     <td>
     *       if v&notin;FV(t)
     *     </td>
     *     <td>
     *       &nbsp;
     *     </td>
     *   </tr>
     * </table>
     * The &eta;-conversion leads to extensional equality.
     * A substitution t[v&rarr;s] is <dfn>admissible</dfn> if it does not introduce new bindings,
     * i.e. no free variable of s would be bound by a &lambda;-operator in t[v&rarr;s].
     * </p>
     * <p>
     * Applying the &lambda;-operator to a variable x and an expression f
     * results in the &lambda;-abstraction (&lambda;x.f) which is a unary function.
     * This &lambda;-abstraction could be circumscribed as the "function f with respect to x".
     * </p>
     * <p>
     * The &lambda;-operator is of course implemented as the substitution f[x&rarr;#0].
     * Note that this implementation does not strictly depend on x being an instance of {@link Variable}
     * and f being an instance of {@link orbital.logic.imp.Expression}. Otherwise it will
     * work fine just as well.
     * </p>
     * <p>
     * <!-- @xxx horrible can't we avoid this by letting constant functions vanish "just at the right moment"? -->
     * If you experience troubles in the context of composite functions, then make sure which way
     * of composition you have applied for f.
     * Due to the mechanism of composition you may have to wrap,
     * say a {@link orbital.math.Symbol symbol} x inside a {@link orbital.math.functional.Functions#constant(Object) constant function}
     * in order to get a function of f with respect to x as expected.
     * So then you could try using
     * <pre>
     * <span class="Orbital">Function</span> h = <span class="Orbital">Substitutions</span>.lambda(<span class="Orbital">Functions</span>.constant(x), f);
     * </pre>
     * instead.
     * </p>
     * @param x the variable from whose exact name to abstract the term f.
     *  This means that the resulting &lambda;-abstraction is a function in the variable x.
     * @param f the term from which to build a &lambda;-abstraction.
     * @pre true (and usually x instanceof Variable &and; f instanceof Expression)
     * @return the &lambda;-abstraction &lambda;x.f
     * @post RES.apply(a) = f[x->a]
     * @todo publizice somewhere once finished
     * @xxx solve the "problem with reverting from constant functions back to normal" perhaps by changing orbital.math.functional.Functionals#genericCompose and orbital.logic.functor.Functionals#genericCompose
     */
    public static final BinaryFunction/*<Variable,Expression, Function<Variable,Expression>>*/ lambda = new BinaryFunction/*<Variable,Expression, Function<Variable,Expression>>*/() {
	    public Object apply(Object x, Object f) {
    		if (!((x instanceof Variable) && ((Variable)x).isVariable()))
		    throw new IllegalArgumentException("usually x should be a " + Variable.class.getName() + " with x.isVariable()==true, however this is not a strict requirement");
		Substitution sigma = Substitutions.getInstance(Arrays.asList(new Object[] {
		    //@xxx sure that shouldn't at least add Substitutions.createExactMatcher(x, Functions.id)? or remove the constant wrapping alltogether
		    Substitutions.createExactMatcher(orbital.math.functional.Functions.constant(x), orbital.math.functional.Functions.id),
		    Substitutions.createExactMatcher(x, orbital.math.functional.Functions.id)
		    // we could as well substitute to orbital.logic.functor.Functions.id?
		}));
    		return sigma.apply(f);
	    }
	};
    /**
     * Get the &lambda;-abstraction of f with respect to x.
     * <p>
     * This is only a shortcut for applying {@link #lambda lambda}(x,f) and solely for convenience.
     * </p>
     * @param x the variable from whose exact name to abstract the term f.
     *  This means that the resulting &lambda;-abstraction is a function in the variable x.
     * @param f the term from which to build a &lambda;-abstraction.
     * @pre true (and usually x instanceof Variable, f instanceof Expression)
     * @return the &lambda;-abstraction &lambda;x.f
     * @post RES.apply(a) = f[x->a]
     * @see #lambda
     * @todo publizice somewhere once finished
     */
    //	public static final Function/*<Variable,Object>*/ lambda(Variable x, Expression f) {
    public static final Function/*<Variable,Object>*/ lambda(Object x, Object f) {
	return (Function/*<Variable,Expression>*//*__*/) lambda.apply(x, f);
    }

	
    // Unification

    /**
     * Unifies terms and returns the mgU.
     * <p>
     * <table>
     *   <tr>
     *     <th>
     *       <p>as set</p>
     *     </th>
     *     <th>
     *     </th>
     *     <th>
     *       <p>prosa</p>
     *     </th>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>U(T) :=</p>
     *     </td>
     *     <td>
     *       <p>{&sigma; &brvbar; 1 = |&sigma;(T)|}</p>
     *     </td>
     *     <td>
     *       <p>&sigma; is a <dfn>unifier</dfn> of T&sube;Term(&Sigma;)</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>&nbsp;</p>
     *     </td>
     *     <td>
     *       <p>&sigma;(s)=&sigma;(t)=t i.e. &sigma;(s) is a sub term of t</p>
     *     </td>
     *     <td>
     *       <p>&sigma; is a <dfn>single side matcher</dfn> of s&isin;Term(&Sigma;) on t&isin;Term(&Sigma;)</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>mgU(T) :=</p>
     *     </td>
     *     <td>
     *       <p>{&mu; &brvbar; &forall;&sigma;&isin;U(T) &exist;&sigma;’&isin;U(T) &sigma; =
     *       &sigma;’ &#8728; &mu;}</p>
     *     </td>
     *     <td>
     *       <p>&mu; is a <dfn>most general unifier</dfn> of T&sube;Term(&Sigma;).
     *       In fact, &mu; is a maximal element with respect to "&sigma;|&tau; :&hArr; &exist;&sigma;' &tau; = &sigma;' &#8728; &sigma;"</p>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>
     *       <p>U<sub>E</sub>(T) :=</p>
     *     </td>
     *     <td>
     *       <p>{&sigma; &brvbar; &forall;i&isin;{1,..,n} &sigma;(s<sub>i</sub>) =<sub>E</sub>
     *       &sigma;(t<sub>i</sub>)}</p>
     *     </td>
     *     <td>
     *       <p>&sigma; is an <dfn>E-Unifier</dfn> of &Gamma;=&lang;s<sub>1</sub>=t<sub>1</sub>,...s<sub>
     *       n</sub>=t<sub>n</sub>&rang;</p>
     *     </td>
     *   </tr>
     * </table>
     * </p>
     * <p>
     * T unifiable (i.e. U(T)&ne;&empty;) &rArr; &exist;&mu;&isin;mgU(T)
     * &mu; is idempotent
     * </p>
     * <p>
     * &mu;,&mu;‘&isin;mgU(T) &rArr; &exist;&sigma; &sigma; is a variable renaming &and; &mu; = &sigma; &#8728; &mu;‘
     * </p>
     * <p>
     * Note that unification depends heavily on the identification of {@link Variable variables} vs. constants.
     * </p>
     * 
     * @param T a collection of terms to try to unify.
     * @return mgU(T), the most-general unifier of T,
     *  or <code>null</code> if the terms in T are not unifiable.
     * @post unifiable(T) &hArr; RES&ne;null &hArr; &forall;t1,t2&isin;T RES.apply(t1).equals(RES.apply(t2))
     * @internal implementation is Robinson-Unification
     * @todo reformat table to the form U(T) := {&mu; &brvbar; &mu; is unifier, i.e. |&mu;(T)| = 1}
     */
    public static final Substitution unify(Collection T) {
	switch (T.size()) {
	case 0: /* fall-through */
	case 1: return id;
	case 2: {
	    Object[] t = T.toArray();
	    return unify(t[0], t[1]);
	}
	default:
	    //@todo at least fold T with unify(Object,Object) provided that mgU is associative
	    throw new UnsupportedOperationException("unification currently does not support more than two terms. @todo");
	}
    }
    /**
     * @todo generalize on sets and not only two terms t1, t2
     * @todo could possibly implement the almost linear unification algorithm
     */
    static final Substitution unify(Object t1, Object t2) {
	Object x;
	Object t;
	if (t1 == null || t2 == null)
	    throw new NullPointerException("cannot unify (" + t1 + "," + t2 +") null does not unify anything.");
	// if one of the two terms is a variable x, call the other term t
       	if ((isVariable(x = t1) && other(t = t2))
	    || (isVariable(x = t2) && other(t = t1))) {
	    if (x.equals(t))
		return id;
	    else if (occur(x, t)) // checks whether x occurs in t
		return null;
	    else
		// return [x->t]
		return getInstance(Collections.singletonList(createExactMatcher(x, t)));
        } else {
	    // let t1=:f(x1,...xm), t2=:g(y1,...yn)
	    if (!((t1 instanceof Functor.Composite) && (t2 instanceof Functor.Composite))) {
		// catch case m=0 first, since it's no true decomposition then
            	if (!(t1.getClass() == t2.getClass() && t1.equals(t2)))
		    return null;
            	else
		    return id;
	    }
	    // true decomposition case
	    Functor.Composite c1 = (Functor.Composite) t1;
	    Functor			  f = c1.getCompositor();
	    Collection		  xs = Utility.asCollection(c1.getComponent());
	    Functor.Composite c2 = (Functor.Composite) t2;
	    Functor			  g = c2.getCompositor();
	    Collection		  ys = Utility.asCollection(c2.getComponent());
	    if (!(c1.getClass() == c2.getClass() && f.equals(g)))
		return null;
	    else {
		// f=g und daher auch m=n
		assert xs.size() == ys.size() : "f==g implies m==n for the number of arguments m resp. n";
		Substitution s = id;
                for (Iterator xi = xs.iterator(), yi = ys.iterator(); xi.hasNext(); ) {
		    // (<var>unifiable</var>, &sigma;<sub>1</sub>) := <i>unify</i>(&sigma;(x<sub>i</sub>), &sigma;(y<sub>i</sub>))
		    Substitution s1 = unify(s.apply(xi.next()), s.apply(yi.next()));
		    if (s1 == null)
			return null;
		    // s := s1 &#8728; s
		    s = compose(s1, s);
                }
                return s;
	    }
        }
    }
	
    /**
     * Checks whether x is a variable.
     * @xxx find a far better definition? ClassicalLogic atomics can be variables, as well!
     *  Whilst 'true' and 'false' are not.
     */
    private static final boolean isVariable(Object x) {
	return (x instanceof Variable) && ((Variable) x).isVariable();
    }

    /**
     * Checks whether x occurs somewhere in t.
     * <p>
     * Note that this method results in Robinson-Unification having exponential time complexity.
     * </p>
     * @param x the variable x checked for occurrence in t.
     *  Note that x should better not be a compound term.
     * @param t the term checked whether it contains x.
     * @return whether x occurs somewhere in t.
     * @internal non optimized occur-check
     */
    private static boolean occur(final Object x, Object t) {
	if (t instanceof Functor.Composite)
	    return occur(x, ((Functor.Composite) t).getComponent());
	else if (t.getClass().isArray())
	    if (t instanceof Object[])
		// return &exist;i occur(x, t[i])
		// version for mere non-primitive type Object arrays
		return Setops.some(Arrays.asList((Object[]) t), new Predicate/*<Object>*/() {
			public boolean apply(Object ti) {
			    return occur(x, ti);
			}
		    });
	    else
		// could additionally(!) occur check in primitive type arrays with java.lang.reflect.Array
		throw new IllegalArgumentException("illegal argument type " + t.getClass() + " is not yet supported");
	return false;
    }

    // helper method

    private static final boolean other(Object o) {
	return true;
    }
}
