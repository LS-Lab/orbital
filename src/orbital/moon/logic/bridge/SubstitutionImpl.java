/**
 * @(#)SubstitutionImpl.java 0.9 2001/06/20 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.bridge;
import orbital.logic.trs.*;

import orbital.logic.functor.Functor;
import orbital.logic.functor.Function;
import orbital.logic.functor.Functionals;

import java.util.List;
import java.io.Serializable;

import java.util.Iterator;
import java.util.Collection;

import java.util.Collections;
import java.util.Arrays;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Term substitution implementation.
 *
 * @version 0.9, 2001/06/20
 * @author  Andr&eacute; Platzer
 * @note package-level protected to orbital.logic.trs
 */
public class SubstitutionImpl implements Substitution, Serializable {
    private static final long serialVersionUID = 5782834146110405976L;
    /**
     * The set of elementary replacements.
     * @serial
     */
    private Collection/*_<Matcher>_*/ replacements;
    /**
     * Create a new substitution.
     * @param replacements the set of elementary replacements.
     * @pre s[i] instanceof {@link Substitution.Matcher}
     */
    public SubstitutionImpl(Collection/*_<Matcher>_*/ replacements) {
        this.replacements = replacements;
    }
    
    //@todo this implementation won't work since unmodifiableCollection does not pass on hashCode() ...
    /*public boolean equals(Object o) {
      return (o instanceof Substitution) && getReplacements().equals(((Substitution) o).getReplacements());
      }
      public int hashCode() {
      return getReplacements().hashCode();
      }*/

    public Collection/*_<Matcher>_*/ getReplacements() {
	return Collections.unmodifiableCollection(replacements);
    }
	
	
    //@todo we should not continue substituting bound variables, as in (&forall; x P(x,y))[x->t] = (&forall; x P(x,y))
    public Object apply(Object term) {
	// apply the first substitution that matches and do not descend
	for (Iterator/*_<Matcher>_*/ i = replacements.iterator(); i.hasNext(); ) {
	    Matcher s = (Matcher/*__*/) i.next();
	    if (s.matches(term))
		// matches
		/*
		 * x[x->s]	= s
		 */
		return s.replace(term);
	}

	// else if none did match
	if (term == null)
	    return term;
	else if (term instanceof Functor.Composite)
            /*
             * distribute substitution application to all sub terms
             * f(t1,...tn)[x->s] = f[x->s](t1[x->s],...tn[x->s])	if f/n &isin; &Sigma;
             * f(t)[x->s] = f[x->s](t[x->s])	if f/n &isin; &Sigma;, t is a generalized component term
             */
	    try {
                Functor.Composite f = (Functor.Composite) term;
                Functor.Composite g;
                try {
		    g = (Functor.Composite) f.getClass().newInstance();
                }
                catch (Throwable illegal) {
		    // try instantiate in another way
                    try {
                        Constructor no_arg = f.getClass().getDeclaredConstructor(null);
    	                //@xxx is there a better solution which does not require accessible tricks? Especially, this trick won't do if we want to use a TRS on maths and functions inside a Browser. See MathPlotter.html stuff
                        if (!no_arg.isAccessible())
			    no_arg.setAccessible(true);
                        g = (Functor.Composite) no_arg.newInstance(null);
                    }
                    catch (InvocationTargetException e) {throw (IllegalArgumentException) new IllegalArgumentException("the argument type no-arg constructor threw").initCause(e.getTargetException());}
                    catch (NoSuchMethodException e) {g = (Functor.Composite) f.getClass().newInstance();}
                    catch (SecurityException denied) {throw new orbital.util.InnerCheckedException("the argument type no-arg constructor is not accessible", denied);}
		}

		//@todo type-safe assert g.getClass() == f.getClass() : "g is a new object of the exact same type as f";
		g.setCompositor((Functor) apply(f.getCompositor()));
            	g.setComponent(apply(f.getComponent()));
            	return g;
            }
            catch (InstantiationException e) {throw (IllegalArgumentException) new IllegalArgumentException("the argument type does not support a no-arg constructor").initCause(e);}
            catch (IllegalAccessException e) {throw (IllegalArgumentException) new IllegalArgumentException("the argument type does not support a no-arg constructor").initCause(e);}
	// almost identical to @see Utility#asIterator, and @see Functionals.ListableFunction
	//@todo could we really use Functionals.ListableFunction instead? Would we benefit from that?
	else if (term instanceof Collection)
            /*
             * distribute substitution application to all sub terms
             * {t1,...tn}[x->s] = {t1[x->s],...tn[x->s]}	if {t1,...tn} is a collection
             */
	    return Functionals.map(this, (Collection) term);
	else if (term.getClass().isArray())
            /*
             * distribute substitution application to all sub terms
             * {t1,...tn}[x->s] = {t1[x->s],...tn[x->s]}	if {t1,...tn} is an array
             */
	    if (term instanceof Object[])
		// version for mere non-primitive type Object arrays
		return Functionals.map(this, (Object[]) term);
	    else
		throw new UnsupportedOperationException("substitution of primitive type arrays of " + term.getClass() + " is currently disabled @todo reenable");
	//@xxx a funny gjc error requires the cast to (Function).
	//return orbital.math.functional.Functionals.map((Function) this, (Object) term);
	else if (term instanceof Iterator)
	    throw new IllegalArgumentException("iterators are not supported, since they should not be required at all");
	else
	    // atomic
            /*
             * skip atomic parts that are not substituted
             * y[x->s]	= y	if x&ne;y &isin; Atoms
             * also skip the rest
             */
	    return term;
    }
	
    public String toString() {
	return getClass().getName() + replacements;
    }

    /**
     * Simple Matcher implementation.
     * <p>
     * This class performs matching with means of {@link Object#equals(Object)}, only.
     * Additionally it will directly replace with the specified object, if substituting is enabled.</p>
     *
     * @version 0.9, 2001/06/21
     * @author  Andr&eacute; Platzer
     */
    public static class MatcherImpl implements Matcher, Serializable {
	private static final long serialVersionUID = -5676492558169571904L;
    	/**
    	 * The object against which to match with {@link Object#equals(Object)}.
    	 * @serial
    	 */
    	private Object pattern;
    	/**
    	 * Whether to perform substitution at all.
    	 * @serial
    	 */
    	private boolean substituting;
    	/**
    	 * The substitute substituting a match is only used if substituting == true.
    	 * @serial
    	 */
    	private Object substitute;
    	/**
    	 * Create a new matcher that performs substitution.
    	 * @param pattern The object against which to match with {@link Object#equals(Object)}.
    	 * @substitute The substitute substituting terms that matched.
    	 * @post substituting == true
    	 */
        public MatcherImpl(Object pattern, Object substitute) {
            this(pattern, true, substitute);
        }
    
    	/**
    	 * Create a new matcher that does not perform substitution.
    	 * @param pattern The object against which to match with {@link Object#equals(Object)}.
    	 * @post substituting == false
    	 */
        public MatcherImpl(Object pattern) {
            this(pattern, false, null);
        }
        
        private MatcherImpl(Object pattern, boolean substituting, Object substitute) {
            this.pattern = pattern;
            this.substituting = substituting;
            this.substitute = substitute;
        }
        
        public boolean equals(Object o) {
	    //@todo implement better
	    return (o instanceof Matcher) && pattern().equals(((Matcher) o).pattern());
        }
        public int hashCode() {
	    return pattern().hashCode();
        }

        // get/set methods
        
        /**
         * Get the pattern to match for.
         */
        public Object pattern() {
	    return pattern;
        }
    
        /**
         * Get the substitute to replace a match with.
         * @return the substitute, or <code>null</code> if this matcher does not perform substitutions.
         * @post RES == null &hArr; &not;isSubstituting()
         */
        public Object substitute() {
	    return substitute;
        }
        
        /**
         * Whether this matcher performs substitutions.
         * @return <code>true</code> if this matcher performs substitutions on some patterns.
         *  <code>false</code> if this matcher will not change any matches at all, but only match them.
         */
        protected boolean isSubstituting() {
	    return substituting;
        }

        // central methods
    	
    	public boolean matches(Object t) {
	    return pattern().equals(t);
    	}
    
    	public Object replace(Object t) {
	    return isSubstituting() ? substitute() : t;
    	}
    	
    	public String toString() {
	    return pattern() + (isSubstituting() ? "->" + substitute() : "");
    	}
    } 

    /**
     * Single sided matcher implementation with unification.
     * <p>
     * This class performs (single sided) matching with means of {@link Substitutions#unify(Collection)}.
     * (See there for a definition of single sided matchers).
     * Additionally, if &mu;&isin;mgU({pattern, t}) is the unifier,
     * it will use &mu;(substitute) as a replacement for the specified object t,
     * if substituting is enabled.</p>
     * <p>
     * <span style="float: left; font-size: 200%">&#9761;</span>
     * Beware of patterns for single sided matchers, that have variables in common
     * with the terms it is applied on. This will most possibly lead to unexpected results.
     * It is generally recommended to use uncommon variable names for these patterns, like
     * <code>_X1, _X2, _X3, ...</code> or <code>$X1, $X2, $X3, ...</code>
     * which do not occur in regular terms.</p>
     *
     * @version 0.9, 2001/06/21
     * @author  Andr&eacute; Platzer
     */
    public static class UnifyingMatcher extends MatcherImpl {
	private static final long serialVersionUID = 8361601987955616874L;
    	/**
    	 * Create a new matcher that performs substitution.
    	 * @param pattern The object against which to (single side) match with {@link Substitutions#unify(Collection)}.
    	 * @substitute The substitute substituting terms that matched, after transforming substitute
    	 *  with the unifier that performed the matching.
    	 * @post substituting == true
    	 */
        public UnifyingMatcher(Object pattern, Object substitute) {
            super(pattern, substitute);
        }
    
    	/**
    	 * Create a new matcher that does not perform substitution.
    	 * @param pattern The object against which to (single side) match with {@link Substitution#unify(Collection)}.
    	 * @post substituting == false
    	 */
        public UnifyingMatcher(Object pattern) {
            super(pattern);
        }

    	/**
    	 * The unifier of the last {@link #matches(Object)}-operation for use in {@link #replace(Object)}.
    	 */
    	private transient Substitution unifier = null;

    	/**
    	 * Get the unifier of the last {@link #matches(Object)}-operation.
    	 */
    	protected Substitution getUnifier() {
	    return unifier;
    	}

    	//@todo single side match test could be optimized with its own implementation method
    	public boolean matches(Object t) {
	    return (this.unifier = Substitutions.unify(Arrays.asList(new Object[] {pattern(), t}))) != null
		&& (this.unifier.apply(t).equals(t));
    	}
    
    	public Object replace(Object t) {
	    return isSubstituting() ? getUnifier().apply(substitute()) : t;
    	}
    }
}
