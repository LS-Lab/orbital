/**
 * @(#)Combinatorical.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm;

import java.io.Serializable;

import java.util.NoSuchElementException;
import java.util.ListIterator;

/**
 * Class for combinatorical operations.
 * 
 * @version 0.9, 2000/11/29
 * @author  Andr&eacute; Platzer
 * @internal is an interface
 */
public abstract class Combinatorical /*implements ListIterator<int[]> like*/ implements Serializable {
    private static final long serialVersionUID = 8581671329920186455L;
    /**
     * Returns the number of combinatorical tuples that araise from this sequence.
     */
    public abstract int count();

    /**
     * Whether this sequence has a next combinatorical tuple.
     */
    public abstract boolean hasNext();

    /**
     * Get the next combinatorical tuple.
     * @return the next combinatorical tuple.
     *  <b>Note:</b> the array returned should <em>not</em> be modified.
     * @throws NoSuchElementException if not hasNext().
     */
    public abstract int[] next();

    
    /**
     * Whether this sequence has a previous combinatorical tuple.
     */
    public abstract boolean hasPrevious();

    /**
     * Get the previous combinatorical tuple.
     * @return the previous combinatorical tuple.
     *  <b>Note:</b> the array returned should <em>not</em> be modified.
     * @throws NoSuchElementException if not hasPrevious().
     */
    public abstract int[] previous();

    // facade factory
	
    /**
     * Get a combinatorical instance.
     * @param r the size of the tuples.
     *  The number of elements to choose out of n.
     * @param combinations whether only combinations are allowed, or every permutation.
     * @param n the number of elements choosable. n = |M|.
     * @param repetition whether elements in a tuple are allowed to repeat.
     * @see #getPermutations(int, int, boolean)
     * @see #getCombinations(int, int, boolean)
     */
    public static Combinatorical getInstance(int r, boolean combinations, int n, boolean repetition) {
	return combinations ? getCombinations(r, n, repetition) : getPermutations(r, n, repetition);
    }

    /**
     * Get all r-permutations of n elements.
     * @param r the size of the tuples to permute.
     *  The number of elements to choose out of n per tuple.
     * @param n the number of elements choosable. n = |M|.
     * @param repetition whether elements in a tuple are allowed to repeat.
     *  The permutations with repetition contain n<sup>r</sup>,
     *  those without repetition contain n<b>P</b>r tuples.
     */
    public static Combinatorical getPermutations(int r, int n, boolean repetition) {
	return repetition ? (Combinatorical) new RepetitivePermutation(r, n) : (Combinatorical) new NonrepetitivePermutation(r, n);
    }
    public static Combinatorical getPermutations(int n, boolean repetition) {
	return getPermutations(n, n, repetition);
    }
    /**
     * Get all (generalized) permutations elements.
     * @param n the numbers of elements choosable. r := n.length is the size of the tuples
     *  and n[i] is the number of elements choosable for the element at index i of the tuple.
     */
    public static Combinatorical getPermutations(int[] n) {
	return new GeneralizedPermutation(n);
    }

    /**
     * Get all r-combinations of n elements.
     * Combinations are those permutations ignoring order and(!) whose elements are always sorted.
     * So its like the difference between a set and a list.
     * @param r the size of the tuples to combinate.
     *  The number of elements to choose out of n per tuple.
     * @param n the number of elements choosable. n = |M|.
     * @param repetition whether elements in a tuple are allowed to repeat.
     *  The combinations with repetition contain <big>(</big><sup>n + <span class="doubleIndex">r</sup><sub>r</sub></span> <sup>-1</sup><big>)</big>,
     *  those without repetition contain n<b>C</b>r = <big>(</big><span class="doubleIndex"><sup>n</sup><sub>r</sub></span><big>)</big> tuples.
     */
    public static Combinatorical getCombinations(int r, int n, boolean repetition) {
	return repetition ? (Combinatorical) new RepetitiveCombination(r, n) : new NonrepetitiveCombination(r, n);
    }

    /**
     * Get an iterator view of a combinatorical iterator.
     */
    public static final ListIterator asIterator(final Combinatorical c) {
	return new ListIterator() {
		// Code for delegation of java.util.Iterator methods to c

		public String toString()
		{
		    return c.toString();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.algorithm.Combinatorical#next()
		 */
		public Object next()
		{
		    return c.next();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.algorithm.Combinatorical#hasNext()
		 */
		public boolean hasNext()
		{
		    return c.hasNext();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.algorithm.Combinatorical#previous()
		 */
		public Object previous()
		{
		    return c.previous();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.algorithm.Combinatorical#hasPrevious()
		 */
		public boolean hasPrevious()
		{
		    return c.hasPrevious();
		}

		// implementation of java.util.ListIterator interface

		/**
		 *
		 * @param param1 <description>
		 */
		public void add(Object param1)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 */
		public void remove()
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @param param1 <description>
		 */
		public void set(Object param1)
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return <description>
		 */
		public int previousIndex()
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return <description>
		 */
		public int nextIndex()
		{
		    throw new UnsupportedOperationException();
		}
	    };
    }
}
