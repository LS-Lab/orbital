/**
 * @(#)SubstitutionImpl.java 0.9 2001/06/20 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.bridge;
import orbital.logic.trs.*;
import orbital.logic.Composite;

import orbital.logic.functor.Functor;
import orbital.logic.functor.Function;
import orbital.logic.functor.Functionals;
import orbital.logic.functor.Predicate;

import java.util.List;
import java.io.Serializable;

import java.util.Iterator;
import java.util.Collection;

import orbital.util.Setops;
import java.util.Arrays;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Term substitution implementation.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @note package-level protected to orbital.logic.trs
 */
public class SubstitutionImpl implements Substitution, Serializable {
    private static final long serialVersionUID = 5782834146110405976L;
    private static final Logger logger = Logger.getLogger(SubstitutionImpl.class.getName());
    /**
     * The set of elementary replacements.
     * @serial
     */
    private Collection/*<Matcher>*/ replacements;
    /**
     * Create a new substitution.
     * @param replacements the set of elementary replacements.
     * @preconditions s[i] instanceof {@link Substitution.Matcher}.
     */
    public SubstitutionImpl(Collection/*<Matcher>*/ replacements) {
        if (replacements == null)
            throw new NullPointerException("set of elementary replacements of a substituion cannot be " + replacements);
        this.replacements = replacements;
    }
    
    //@todo this implementation won't work since unmodifiableCollection does not pass on hashCode() ...
    /*public boolean equals(Object o) {
      return (o instanceof Substitution) && getReplacements().equals(((Substitution) o).getReplacements());
      }
      public int hashCode() {
      return getReplacements().hashCode();
      }*/
    public boolean equals(Object o) {
        return (o instanceof Substitution) && getReplacements().equals(((Substitution) o).getReplacements());
    }
    public int hashCode() {
        return replacements.hashCode();
    }

    public Collection/*<Matcher>*/ getReplacements() {
        return Setops.unmodifiableCollectionLike(replacements);
    }
        
        
    //@todo we should not continue substituting bound variables, as in (&forall; x P(x,y))[x->t] = (&forall; x P(x,y)) or (&lambda; x . f(x))[x->t] = &lambda; x . f(x)
    public Object apply(final Object term) {
        // apply the first substitution that matches and do not descend
        for (Iterator/*<Matcher>*/ i = replacements.iterator(); i.hasNext(); ) {
            Matcher s = (Matcher/*__*/) i.next();
            if (s.matches(term))
                // matches
                /*
                 * x[x->s]      = s
                 */
                return s.replace(term);
        }

        // else if none did match
        if (term == null)
            return term;
        else if (term instanceof orbital.logic.Composite)
            /*
             * distribute substitution application to all sub terms
             * f(t1,...tn)[x->s] = f[x->s](t1[x->s],...tn[x->s])        if f/n &isin; &Sigma;
             * f(t)[x->s] = f[x->s](t[x->s])    if f/n &isin; &Sigma;, t is a generalized component term
             */
            try {
                final orbital.logic.Composite f = (orbital.logic.Composite) term;
                final Object substCompositor = apply(f.getCompositor());
                final Object substComponent = apply(f.getComponent());
                try {
                    return f.construct(substCompositor,
                                       substComponent);
                }
                catch (Throwable illegal) {
                    orbital.logic.Composite fp;
                    // try instantiate in another way
                    try {
                        Constructor nullary = f.getClass().getDeclaredConstructor(new Class[0]);
                        //@xxx is there a better solution which does not require accessible tricks? Especially, this trick won't do if we want to use a TRS on maths and functions inside a Browser. See MathPlotter.html stuff
                        if (!nullary.isAccessible())
                            nullary.setAccessible(true);
                        fp = (orbital.logic.Composite) nullary.newInstance(new Object[0]);
                    }
                    catch (InvocationTargetException ex) {throw (IllegalArgumentException) new IllegalArgumentException("the argument type nullary constructor threw").initCause(ex.getTargetException());}
                    catch (SecurityException denied) {throw new orbital.util.InnerCheckedException("the argument type nullary constructor is not accessible", denied);}
                    catch (NoSuchMethodException ex) {throw (RuntimeException) illegal;}
                    fp.setCompositor(substCompositor);
                    fp.setComponent(substComponent);
                    assert f != null : "we could not have handled null that way";
                    logger.log(Level.WARNING, "composite object {0} of class {1} does not support construct(Object,Object) but has to be emulated with newInstance and setComponent(Object)/setCompositor(Object), due to\n{2}", new Object[] {f, f.getClass(), illegal});
                    return fp;
                }
            }
        //@todo finally type-safe assert g.getClass() == f.getClass() : "g is a new object of the exact same type as f";
            catch (InstantiationException e) {throw (IllegalArgumentException) new IllegalArgumentException("the argument type of " + term.getClass() + " does not support a nullary constructor which is required for substitution").initCause(e);}
            catch (IllegalAccessException e) {throw (IllegalArgumentException) new IllegalArgumentException("the argument type of " + term.getClass() + " does not support a nullary constructor which is requried for substitution").initCause(e);}
        
        // almost identical to @see Utility#asIterator, and @see Functionals.ListableFunction
        //@todo could we really use Functionals.ListableFunction instead? Would we benefit from that?
        else if (term instanceof Collection)
            /*
             * distribute substitution application to all sub terms
             * {t1,...tn}[x->s] = {t1[x->s],...tn[x->s]}        if {t1,...tn} is a collection
             */
            return Functionals.map(this, (Collection) term);
        else if (term.getClass().isArray())
            /*
             * distribute substitution application to all sub terms
             * {t1,...tn}[x->s] = {t1[x->s],...tn[x->s]}        if {t1,...tn} is an array
             */
            if (term instanceof Object[])
                // version for mere non-primitive type Object arrays
                return Functionals.map(this, (Object[]) term);
            else
                throw new UnsupportedOperationException("substitution of primitive type arrays of " + term.getClass() + " is currently disabled @todo reenable");
        //@xxx a funny gjc error requires the cast to (Function).
        //return orbital.math.functional.Functionals.map((Function) this, (Object) term);
        else if (term instanceof Iterator)
            throw new IllegalArgumentException("iterators are not supported, since they should not be necessary for substitution at all");
        else
            // atomic
            /*
             * skip atomic parts that are not substituted
             * y[x->s]  = y     if x&ne;y &isin; Atoms
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
     * @version $Id$
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
         * @postconditions substituting == true
         */
        public MatcherImpl(Object pattern, Object substitute) {
            this(pattern, true, substitute);
        }
    
        /**
         * Create a new matcher that does not perform substitution.
         * @param pattern The object against which to match with {@link Object#equals(Object)}.
         * @postconditions substituting == false
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
         * @postconditions RES == null &hArr; &not;isSubstituting()
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
     *
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see orbital.logic.trs.Substitutions#createSingleSidedMatcher(Object,Object)
     */
    public static class UnifyingMatcher extends MatcherImpl {
        private static final long serialVersionUID = 8361601987955616874L;
        /**
         * Create a new matcher that performs substitution.
         * @param pattern The object against which to (single side) match with {@link Substitutions#unify(Collection)}.
         * @substitute The substitute substituting terms that matched, after transforming substitute
         *  with the unifier that performed the matching.
         * @postconditions substituting == true
         */
        public UnifyingMatcher(Object pattern, Object substitute) {
            super(pattern, substitute);
        }
    
        /**
         * Create a new matcher that does not perform substitution.
         * @param pattern The object against which to (single side) match with {@link Substitutions#unify(Collection)}.
         * @postconditions substituting == false
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
            this.unifier = Substitutions.unify(Arrays.asList(new Object[] {pattern(), t}));
            return this.unifier != null
                && (this.unifier.apply(t).equals(t));
        }
    
        public Object replace(Object t) {
            return isSubstituting() ? getUnifier().apply(substitute()) : t;
        }
    }

    /**
     * Single sided matcher implementation with unification under conditions.
     *
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see orbital.logic.trs.Substitutions#createSingleSidedMatcher(Object,Object,Predicate)
     */
    public static class ConditionalUnifyingMatcher extends UnifyingMatcher {
        //private static final long serialVersionUID = 0L;
	/**
	 * The additional condition that the match has to satisfy.
	 * @serial
	 */
	private final Predicate/*<Substitution>*/ condition;
        /**
         * Create a new matcher that performs substitution.
         * @param pattern The object against which to (single side) match with {@link Substitutions#unify(Collection)}.
         * @substitute The substitute substituting terms that matched, after transforming substitute
         *  with the unifier that performed the matching.
	 * @param condition The additional condition that has to hold for occurrences that
	 *  match (single sidedly) with pattern. Hence, the matcher returned will only
	 *  match when condition.apply(&mu;) is true for the single sided matcher
	 *  (resp. unifier) &mu;.
         * @postconditions substituting == true
         */
        public ConditionalUnifyingMatcher(Object pattern, Object substitute, Predicate/*<Substitution>*/ condition) {
            super(pattern, substitute);
	    this.condition = condition;
        }
    
        public boolean matches(Object t) {
	    if (super.matches(t)) {
		final Substitution mu = getUnifier();
		return condition.apply(mu);
	    } else {
		return false;
	    }
        }
    
        public String toString() {
            return pattern() + (isSubstituting() ? "->" + substitute() + " /; " + condition : "");
        }
    }
}
