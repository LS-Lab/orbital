/*
 * @(#)Evaluation.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic.strategy;

import orbital.logic.functor.Function;
import java.util.List;
import orbital.util.Pair;
import java.util.Collection;

import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedList;
import orbital.robotic.strategy.Selection.Selecting;

/**
 * Evaluates and selects from a set of weighted objects.
 * <p>
 * An Evaluation is a Weighting-Filter that, after having weighed several
 * situations with the given weighting function, evaluates the
 * preferred object according to a given selection implementation.</p>
 * 
 * @structure implement Function<Object, Number>
 * @structure aggregate weighting:Function<Object, Number>
 * @structure aggregate selection:Selection
 * @version 0.9, 04/03/96
 * @author  Andr&eacute; Platzer
 */
public
class Evaluation implements Function/*<Object, Number>*/ {
	//TODO: unify with orbital.algorithm.template.DepthFirstSearch, ...?

	/**
	 * The weighting mechanism used to calculate the weight for any element.
	 * @serial
	 */
	protected Function/*<Object, Number>*/ weighting;

	/**
	 * The Selection mechanism used to select from the elements.
	 * @serial
	 */
	protected Selection  selection;

	/**
	 * The set of Object-elements to be selected from.
	 * is synchronized thread-safe.
	 * @serial
	 * @see #weights
	 */
	protected final List elements;

	/**
	 * The set of Double-weights calced for the elements associated with.
	 * is synchronized thread-safe.
	 * @serial
	 * @see #elements
	 */
	//TODO: use List(new KeyValuePair())
	protected final List weights;

	/**
	 * constructs an evaluation with a specified selection of a given weighting function.
	 */
	public Evaluation(Selection selection, Function/*<Object, Number>*/ weighting) {
		this.selection = selection;
		this.weighting = weighting;
		// TODO: synchronize
		this.elements = /* Collections.synchronizedList */ (new LinkedList());
		this.weights = /* Collections.synchronizedList */ (new LinkedList());
	}
	public Evaluation(Function/*<Object, Number>*/ weighting) {
		this(Selecting.best(), weighting);
	}
	public Evaluation() {
		this(Selecting.best(), new NumberWeighting());
	}

	// get/set methods

	public Function/*<Object, Number>*/ getWeighting() {
		return weighting;
	} 
	public void setWeighting(Function/*<Object, Number>*/ w) {
		weighting = w;
	} 
	public Selection getSelection() {
		return selection;
	} 
	public void setSelection(Selection select) {
		selection = select;
	} 

	/**
	 * Get the weights list.
	 */
	public List getWeights() {
		return Collections.unmodifiableList(weights);
	} 

	/**
	 * Get the elements list.
	 */
	public List getElements() {
		return Collections.unmodifiableList(elements);
	} 

    
    //  Collection methods
	/**
	 * Clears the list of objects to be selected from.
	 */
	public void clear() {
		weights.clear();
		elements.clear();
	} 

	/**
	 * Weight an object and add it to the list of selectable objects.
	 * Weights one Object and keeps its weight for later Selection.
	 * might be threaded parallel to evaluate().
	 */
	public boolean add(Object arg) {
		weightImpl(arg);
		return true;
	} 

	/**
	 * Weight a whole list of objects and add them to the list of selectable objects.
	 * Weights one Object and keeps its weight for later Selection.
	 * might be threaded parallel to evaluate().
	 */
	public void addAll(Collection list) {
		for (Iterator i = list.iterator(); i.hasNext(); )
			add(i.next());
	} 

	/**
	 * Weight an object and add it to the list of selectable objects.
	 * Weights one Object and keeps its weight for later Selection.
	 * might be threaded parallel to evaluate().
	 * @see #add
	 */
	public Object/*>Number<*/ apply(Object arg) {
		return weightImpl(arg);
	} 

	/**
	 * Evaluates with the default selection specified in the constructor.
	 * @see #evaluate(Selection)
	 */
	public Object evaluate() {
		return evaluate(selection);
	} 

	/**
	 * Returns the bottommost argument of an evaluation tree.
	 */
	public static Object getArg(Object evaluated) {
		if (evaluated instanceof Pair)
			for (Object h = ((Pair) evaluated).B; h instanceof Pair; h = ((Pair) h).B)
				evaluated = h;
		return evaluated;
	} 

	/**
	 * add a weighted element synchronized for threading reasons.
	 * same index for elements and weights is required.
	 */
	protected synchronized void add(Object el, Object weight) {
		elements.add(el);
		weights.add(weight);
	} 

	protected Object/*>Number<*/ weightImpl(final Object arg) {
		Object/*>Number<*/ weight;

		/*concurrent*/ {
			weight = weighting.apply(arg);	   // TODO: might be threaded
		} 

		add(arg, weight);
		return weight;
	} 

	/*
	 * threaded
	 * 
	 * ThreadGroup wtg = new ThreadGroup("WeightThreadGroup");
	 * void weightT(final Object arg) {
	 * Thread weightThread = new Thread( wtg, new Runnable() {
	 * public void run() {
	 * double weight = weighting.weight(arg);
	 * add(arg,new Double(weight));
	 * }
	 * },"weighting")
	 * weightThread.start();
	 * }
	 * evaluate() {
	 * wtg.join()
	 * }
	 */


	/**
	 * Evaluates by selecting the from the weights and returning the associated Object.
	 * Those objects that were arguments in a call to weight are considered.
	 * <p>
	 * If weighting is in turn an Evaluation then this might be threaded parallel to weight().
	 * @return the selection - the Pair of Objects (evaluated,subevaluated) which
	 * has the selected weight.
	 * If weighting is no sub-evaluation, then the object evaluated is returned directly.
	 * Returns <code>null</code> if nothing was selected at all.
	 */
	private Object evaluate(Selection selection) {
		Object ev = evaluateMe(selection);
		if (weighting instanceof Evaluation) {	  // Pair additional Evaluation Objects
			Object subev = ((Evaluation) weighting).evaluate();
			return new Pair(ev, subev);
		} else
			return ev;
	} 

	private Object evaluateMe(Selection selection) {
		int selected = selection.select(Collections.unmodifiableList(weights));
		if (selected < 0)
			return null;
		return elements.get(selected);
	} 
}


final class NumberWeighting implements Function/*<Object, Number>*/ {

	/**
	 * returns weight value of an arg by Number.
	 */
	public Object/*>Number<*/ apply(Object arg) {
		return (Number) arg;
	} 
}
