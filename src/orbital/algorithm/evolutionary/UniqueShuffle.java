/*
 * @(#)UniqueShuffle.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class shuffles n values from 0 to n-1.
 * It generates a random order of n values from 0 to n-1 with presumption
 * to be unique.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see java.util.Collections#shuffle(java.util.List)
 * @todo substitute with Collections.shuffle
 * @deprecated Since JDK1.2
 */
class UniqueShuffle {
    public UniqueShuffle(int n) {
        order = new int[n];
        for (int i = 0; i < n; i++)
            order[i] = i;
        reShuffle();
        index = 0;
    }

    /**
     * The order in which numbers are returned.
     * 
     * @serial
     */
    private int order[];

    /**
     * shuffle values with Random-Generator.
     */
    public void reShuffle(Random random) {
        for (int i = order.length; i>1; i--) {
            int r = random.nextInt(i);
            int t = order[i-1];  // swap i-1, r
            order[i-1] = order[r];
            order[r] = t;                // don't use twice
        }
        index = 0;
    } 
    public void reShuffle() {
        reShuffle(new Random());
    } 

    /**
     * unshuffle values to normal order.
     * identical mapping 1 .. n.
     */
    public void unShuffle() {
        int n = order.length;
        for (int i = 0; i < order.length; i++)
            order[i] = i;
        index = 0;
    } 

    /**
     * return the shuffled i-th value.
     */
    public int shuffle(int i) {
        return order[i];
    } 

    /**
     * keep track of values already returned via next.
     * 
     * @serial
     */
    private int index;

    /**
     * Returns the next element shuffled.
     * 
     * @return     the next element of this enumeration.
     * @throws  NoSuchElementException  if no more elements exist.
     */
    public int next() {
        if (!hasMoreElements())
            throw new NoSuchElementException("UniqueShuffle finished. Must reShuffle()");
        return shuffle(index++);
    } 

    /**
     * Tests if this enumeration contains more elements.
     * 
     * @return  <code>true</code> if this enumeration contains more elements;
     * <code>false</code> otherwise.
     */
    public boolean hasMoreElements() {
        return index < order.length;
    } 
}
