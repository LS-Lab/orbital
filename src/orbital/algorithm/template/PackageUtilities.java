/**
 * @(#)PackageUtilities.java 1.0 2002/07/06 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Iterator;
import orbital.math.Real;
import orbital.util.Pair;

import java.util.List;
import java.util.Random;

import orbital.util.Setops;
import java.util.LinkedList;
import java.util.ArrayList;


/**
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
final class PackageUtilities {
    /**
     * Get the minimum argument (and its f-value).
     * This method is a combination of min and argmin.
     * @param choices the available choices in M.
     * @param f the evaluation function f:M&rarr;<b>R</b>.
     * @return the Pair (a, f(a))&isin;M&times;<b>R</b> with minimum f(a).
     * @preconditions choices.hasNext()
     * @postconditions RES = (a,v) &and; a = argmin<sub>a'&isin;M</sub> f(a')
     *  &and; v = min<sub>a'&isin;M</sub> f(a').
     * @throws NoSuchElementException if !choices.hasNext()
     * @see orbital.util.Setops#argmin(Iterator,Function)
     * @todo replace by ordinary argmin and reevaluate the result's f-value.
     */
    public static final /*<M,R extends Comparable>*/
	Pair/*<M,R>*/ min(Iterator/*<M>*/ choices, Function/*<M,R>*/ f) {
        // (almost) identical to @see orbital.util.Setops#argmin(Iterator,Function)

        // search for minimum f in choices
        // current best choice of choices
        Object/*>M<*/ best = choices.next();
        // f(best)
        Comparable/*>R<*/ bestValue = (Comparable/*>R<*/) f.apply(best);
        while (choices.hasNext()) {
            final Object/*>M<*/ o = choices.next();
            final Comparable/*>R<*/ value = (Comparable/*>R<*/) f.apply(o);
            if (value.compareTo(bestValue) < 0) {
                bestValue = value;
                best = o;
            }
            assert true : "invariant: bestValue =< f-value of each choice seen so far & f(best)==bestValue";
        }

        // return the best choice along with its value
        return new Pair/*<M,R>*/(best, bestValue);
    }
    /**
     * @see orbital.util.Setops#argmax(Iterator,Function)
     */
    public static final /*<M,R extends Comparable>*/
	Pair/*<M,R>*/ max(Iterator/*<M>*/ choices, Function/*<M,R>*/ f) {
        // (almost) identical to @see orbital.util.Setops#argmin(Iterator,Function)

        // search for maximum f in choices
        // current best choice of choices
        Object/*>M<*/ best = choices.next();
        // f(best)
        Comparable/*>R<*/ bestValue = (Comparable/*>R<*/) f.apply(best);
        while (choices.hasNext()) {
            final Object/*>M<*/ o = choices.next();
            final Comparable/*>R<*/ value = (Comparable/*>R<*/) f.apply(o);
            if (value.compareTo(bestValue) > 0) {
                bestValue = value;
                best = o;
            }
            assert true : "invariant: bestValue >= f-value of each choice seen so far & f(best)==bestValue";
        }

        // return the best choice along with its value
        return new Pair/*<M,R>*/(best, bestValue);
    }

    //

    /**
     * Decorator restricting transitions of a GSP randomly.
     * This filters A(s) to a random subset of cardinality &le;numberOfChoices.
     * @param problem the problem to decorate.
     * @param numberOfChoices the (maximum) number of random choices for each transition.
     * @param algorithm the probabilistic algorithm using this restriction.
     *  That instance is needed for providing {@link ProbabilisticAlgorithm#getRandom()}.
     * @todo might also provide a Random generator explicitly, if we would not like changing it during a search, anyway.
     * @return the decorated problem.
     * @note aspect of local randomization.
     */
    public static final /*<A,S>*/ GeneralSearchProblem/*<A,S>*/
	restrictRandomly(GeneralSearchProblem/*<A,S>*/ problem, final int numberOfChoices, final ProbabilisticAlgorithm algorithm) {
        return new DelegateGeneralSearchProblem/*<A,S>*/(problem) {
                private static final long serialVersionUID = -4007975459550830964L;
                public Iterator/*<A>*/ actions(Object/*>S<*/ state) {
                    final List/*<A>*/ actions = Setops.asList(getDelegatee().actions(state));
                    final Random random = algorithm.getRandom();
                    final List/*<A>*/ restrictedActions = new ArrayList(numberOfChoices);
                    for (int i = 0; i < Math.min(numberOfChoices, actions.size()); i++) {
                        int index = random.nextInt(actions.size());
                        restrictedActions.add(actions.get(index));
                        actions.remove(index);
                        assert true : "invariant: restrictedActions contains a subset of OLD(actions) of the size i, actions does not contain those elements in restrictedActions any more (except for duplicats)";
                    }
                    assert true : "restrictedActions&sube;getDelegatee().actions(state)";
                    return Setops.unmodifiableIterator(restrictedActions.iterator());
                }
            };
    }

    /**
     * Decorator restricting transitions of a GSP to the best local transitions.
     * This filters A(s) to the subset of its best elements.
     * @param problem the problem to decorate.
     * @param evaluationFunction the function S&rarr;<b>R</b> used to evalute the state s&#697;=t(s,a)&isin;S
     *  resulting from each action a&isin;A(s). Used for selecting the best f-values.
     * @return the decorated problem.
     * @see PackageUtilities#min
     * @todo aspect (I) of locally restricting the search to the most promising actions.
     */
    public static final /*<A,S>*/ GeneralSearchProblem/*<A,S>*/
	restrictBest(GeneralSearchProblem/*<A,S>*/ problem, final Function/*<S,Real>*/ evaluationFunction) {
        return new DelegateGeneralSearchProblem(problem) {
                private static final long serialVersionUID = 549567555212455602L;
                public Iterator/*<A>*/ actions(Object/*>S<*/ state) {
                    final GeneralSearchProblem/*<A,S>*/ problem = getDelegatee();
                    // just a short name for evalutionFunction
                    final Function/*<S,Real>*/ f = evaluationFunction;
                    
                    Iterator/*<A>*/ choices = problem.actions(state);
                    if (!choices.hasNext())
                        return choices;

                    // search for all with minimum f in choices
                    
                    // contains all choices that are as good as (current) best choice of actions
                    List/*<A>*/ elite;
                    // f(best)
                    Comparable bestValue;
                    {
                        // initialize loop invariant
                        assert choices.hasNext() : "hasNext() pure, and has already been queried";
                        final Object/*>A<*/ a =  choices.next();
                        final Object/*>S<*/ sp = problem.states(a, state).next();
                        elite = new LinkedList();
                        elite.add(a);
                        bestValue = (Comparable) f.apply(sp);
                    }
                    while (choices.hasNext()) {
                        final Object/*>A<*/ a = choices.next();
                        final Object/*>S<*/ sp = problem.states(a, state).next();
                        //@internal we may call states(a,state) twice without caching sp. Once here, and once in the application using sp of the final choices
                        final Comparable value = (Comparable) f.apply(sp);
                        final int cmp = value.compareTo(bestValue);
                        // remember the better one
                        if (cmp < 0) {
                            // a is better, so throw away all that were just as good as best
                            bestValue = value;
                            elite.clear();
                            elite.add(a);
                        } else if (cmp == 0)
                            // collect all candidates as good as best
                            elite.add(a);
                        else
                            // forget about worse candidates
                            ;
                        assert true : "invariant: f-value of each element in elite is bestValue & bestValue =< f-value of each choice seen so far & elite contains all elements of choices seen so far that has an f-cost of bestValue";
                    }

                    assert true : "elite&sube;getDelegatee().actions(state)";
                    return Setops.unmodifiableIterator(elite.iterator());
                }
            };
    }

    /**
     * Decorator restricting transitions of a GSP to the best local transitions.
     * This filters A(s) to the subset of its best n elements. If there are more than n,
     * make a random choice.
     * Also called beam-search.
     * @param maximumBranchingFactor the (maximum) number n of local transitions allowed.
     * @param problem the problem to decorate.
     * @param evaluationFunction the function S&rarr;<b>R</b> used to evalute the state s&#697;=t(s,a)&isin;S
     *  resulting from each action a&isin;A(s). Used for selecting the best f-values.
     * @return the decorated problem.
     * @see PackageUtilities#min
     * @see IterativeBroadening
     * @todo aspect (II) of locally restricting the search to the most promising actions.
     */
    public static final /*<A,S>*/ GeneralSearchProblem/*<A,S>*/ restrictTop(final int maximumBranchingFactor, GeneralSearchProblem/*<A,S>*/ problem, final Function/*<S,Real>*/ evaluationFunction) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
