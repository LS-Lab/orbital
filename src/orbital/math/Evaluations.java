/**
 * @(#)Evaluations.java 0.8 2000/02/05 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.math.functional.Function;
import orbital.math.functional.BinaryFunction;
import orbital.math.functional.Functionals;

import orbital.math.functional.Operations;
import orbital.math.functional.Functions;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;

import orbital.util.Setops;

//@TODO: define point-wise functionals which return the same structure (Func, Obj)->Obj

/**
 * Contains evaluation functions for mathematical objects.
 *
 * @deprecated since Orbital1.1 This class is deprecated since its (simple) methods are mere facades for convenience.
 * Directly use the functions in {@link orbital.math.functional.Operations}
 * and {@link orbital.logic.functor.Functionals}, instead.
 * @stereotype Utilities
 * @stereotype Module
 * @version 0.8, 2000/07/24
 * @author  Andr&eacute; Platzer
 * @see orbital.math.MathUtilities
 * @see orbital.math.functional.Operations
 * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade</a>
 */
public final class Evaluations {

    /**
     * prevent instantiation - module class.
     */
    private Evaluations() {}


    /**
     * Returns the total sum of all elements in this Vector.
     */
    public static /*<R implements Arithmetic>*/ Arithmetic/*>R<*/ sum(Vector/*<R>*/ v) {
	return (Arithmetic/*>R<*/) Operations.sum.apply(v);
    } 
    public static double sum(double[] v) {
	return Functionals.foldRight(Operations.plus, 0, v);
    } 

    /**
     * Returns the average of all elements in this Vector.
     */
    public static /*<R implements Arithmetic>*/ Arithmetic average(Vector/*<R>*/ v) {
	return sum(v).divide(Values.getDefaultInstance().valueOf(v.dimension()));
    } 
    public static double average(double[] v) {
	return sum(v) / v.length;
    } 

    /**
     * Returns the minimum value of all elements in this Vector.
     */
    public static /*<R implements Arithmetic>*/ Arithmetic/*>R<*/ min(Vector/*<R>*/ v) {
	return (Arithmetic/*>R<*/) Operations.inf.apply(v);
    } 

    /**
     * Returns the minimum value of an array of doubles.
     */
    public static double min(double[] v) {
	return Functionals.foldRight(Operations.min, v[0], v);
    } 

    /**
     * Return minimum of an array of integers.
     */
    public static int min(int[] vals) {
	//@todo return ((Number) Functionals.foldRight(Operations.min, v[0], v)).intValue();
	int min = vals[0];
	for (int i = 1; i < vals.length; i++)
	    if (vals[i] < min)
		min = vals[i];
	return min;
    } 

    /**
     * Returns the maximum value of all elements in this Vector.
     */
    public static /*<R implements Arithmetic>*/ Arithmetic/*>R<*/ max(Vector/*<R>*/ v) {
	return (Arithmetic/*>R<*/) Operations.sup.apply(v);
    } 

    /**
     * Returns the maximum value of an array of doubles.
     */
    public static double max(double[] v) {
	return Functionals.foldRight(Operations.max, v[0], v);
    } 

    /**
     * Return maximum of an array of integers.
     */
    public static int max(int[] vals) {
	int max = vals[0];
	for (int i = 1; i < vals.length; i++)
	    if (max < vals[i])
		max = vals[i];
	return max;
    } 

    /**
     * Returns the vector with the absolutes of all elements in this Vector.
     */
    public static /*<R implements Arithmetic>*/ Vector/*<R>*/ abs(Vector/*<R>*/ v) {
	return Functionals.map(Functions.norm, v);
    } 
    public static double[] abs(double[] v) {
	return Functionals.map(Functions.norm, v);
    } 


    /**
     * Returns the total sum of all elements in this Matrix.
     */
    public static /*<R implements Arithmetic>*/ Arithmetic/*>R<*/ sum(Matrix/*<R>*/ M) {
	return (Arithmetic/*>R<*/) Operations.sum.apply(M);
    } 

    /**
     * Returns the average of all elements in this Matrix.
     */
    public static /*<R implements Arithmetic>*/ Arithmetic average(Matrix/*<R>*/ M) {
	return sum(M).divide(Values.getDefaultInstance().valueOf(M.dimension().width * M.dimension().height));
    } 

    /**
     * Returns the minimum value of all elements in this Matrix.
     */
    public static /*<R implements Arithmetic>*/ Arithmetic/*>R<*/ min(Matrix/*<R>*/ M) {
	return (Arithmetic/*>R<*/) Operations.inf.apply(M);
    } 

    /**
     * Returns the maximum value of all elements in this Matrix.
     */
    public static /*<R implements Arithmetic>*/ Arithmetic/*>R<*/ max(Matrix/*<R>*/ M) {
	return (Arithmetic/*>R<*/) Operations.sup.apply(M);
    } 
}
