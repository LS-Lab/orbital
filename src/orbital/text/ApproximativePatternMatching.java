/*
 * @(#)ApproximativePatternMatching.java 0.9 1998/08/21 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.text;

import java.util.Collection;

import java.util.Iterator;
import java.util.LinkedList;
import orbital.math.Evaluations;
import java.util.Arrays;
import orbital.util.SuspiciousError;

/**
 * Implements an approximative pattern matching algorithm.
 * <p>
 * approximative string searching</p>
 * 
 * @version 0.9, 21/18/98
 * @author  Andr&eacute; Platzer
 * @todo document
 */
public class ApproximativePatternMatching implements PatternMatching {
    private static class Debug {
	private Debug() {}
	public static void main(String arg[]) throws Exception {
	    // Compiler.disable();
	    ApproximativePatternMatching pm = new ApproximativePatternMatching("aaittctg");
	    Collection					 m = pm.matches("aaaatticrgggggaaaatticrgggggaaaatticrggggg");
	    System.out.println(m);
	} 
    }

    protected String pattern;
    public ApproximativePatternMatching() {
	this(null);
    }
    public ApproximativePatternMatching(String pattern) {
	this.pattern = pattern;
    }

    public void setPattern(String pattern) {
	this.pattern = pattern;
    } 

    protected String text;
    private int		 D[][];

    /**
     * Get a Collection of all Match instances where the pattern matches the text specified.
     */
    public Collection matches(String text) {
	this.text = text;
	int m = pattern.length();
	int n = text.length();

	// D[i][l] = (minimum) #differences between pattern[0..i] and any contiguous substring ending at text[l]
	D = new int[m + 1][n + 1];

	// init
	for (int l = 0; l <= n; l++)	// [0 0 0 ... n]
	    D[0][l] = 0;				// [1          ]
	for (int i = 1; i <= m; i++)	// [:          ]
	    D[i][0] = i;				// [m          ]

	// Complexity: O(m*n)
	for (int i = 1; i <= m; i++)
	    for (int l = 1; l <= n; l++) {
		int predecessor[] = {
		    D[i - 1][l - 1],	// replace(%): match or mismatch (from diagonal)
		    D[i - 1][l] + 1,	// insert(_):  skip in pattern, one difference (from left)
		    D[i][l - 1] + 1
		};	  // delete(?):  skip in text, one difference (from above)
		if (text.charAt(l - 1) != pattern.charAt(i - 1))
		    predecessor[0]++;	 // match->mismatch if chars are different, one difference
		D[i][l] = Evaluations.min(predecessor);
	    } 

	print();
	return getBest();
    } 

    /**
     * Of the current Matches, get the Matches ending at i|l.
     */
    public Collection getMatch(int i, int l) {
	matches = new LinkedList();
	Match match = new Match(new StringBuffer(), new StringBuffer());
	getMatch(i, l, match);
	return matches;
    } 
    private Collection matches;
    private void getMatch(int i, int l, Match match) {
	if (i > D.length || l > D[i].length)
	    throw new IllegalArgumentException("Position out of text or pattern length");
	if (i < 1 || l < 1) {
	    match.setIndex(l);
	    matches.add(match);
	    return;
	} 

	// TODO: remember minimal decision in another array during primary algorithm run, avoiding this secondary run
	int predecessor[] = {
	    D[i - 1][l - 1],	// replace(%): match or mismatch (from diagonal)
	    D[i - 1][l] + 1,	// insert(_):  skip in pattern, one difference (from left)
	    D[i][l - 1] + 1
	};	  // delete(?):  skip in text, one difference (from above)
	if (text.charAt(l - 1) != pattern.charAt(i - 1))
	    predecessor[0]++;	 // match->mismatch if chars are different, one difference
	int min = Evaluations.min(predecessor);
	loop:
	for (int p = 0; p < predecessor.length; p++)
	    if (predecessor[p] == min) {
		Match n_match = (Match) match.clone();
		switch (p) {
		case 0:
		    n_match.append(pattern.charAt(i - 1), text.charAt(l - 1));
		    getMatch(i - 1, l - 1, n_match);
		    break;
		case 1:
		    n_match.append(pattern.charAt(i - 1), ' ');
		    getMatch(i - 1, l, n_match);
		    break;
		case 2:
		    n_match.append(' ', text.charAt(l - 1));
		    getMatch(i, l - 1, n_match);
		    break;
		default:
		    throw new SuspiciousError("neither case selected");
		}
	    } 
    } 

    /**
     * Calculates the best matches - those with less faults and more adequate length.
     */
    protected Collection getBest() {
	int m = D.length - 1;
	int n = D[0].length - 1;
	int V[][] = new int[m + 1][n + 1];	  // value weighting matrix
	int desired = Math.min(pattern.length(), text.length());	 // desired matching range

	// find minimum fault-value
	int minx = -1, miny = -1;
	int min = D[m][n] + 1;
	for (int i = 1; i <= m; i++)
	    for (int l = 1; l <= n; l++) {
		int val = D[i][l];
		if (i < desired)
		    val += desired - i;	// penalty for short ranges
		if (l < desired)
		    val += desired - l;	// penalty for short ranges
		V[i][l] = val;
		if (val < min) {
		    min = val;
		    minx = i;
		    miny = l;
		} 
	    } 
	Collection best = new LinkedList();

	// get all minimum ending positions
	for (int i = 1; i <= m; i++)
	    for (int l = 1; l <= n; l++)
		if (V[i][l] == min)
		    best.addAll(getMatch(i, l));
	return best;
    } 


    private void print() {
	System.out.print("    ");
	for (int x = 0; x < text.length(); x++)
	    System.out.print(text.charAt(x) + " ");
	System.out.println();
	for (int x = 0; x <= pattern.length(); x++) {
	    if (x > 0)
		System.out.print(pattern.charAt(x - 1) + " ");
	    else
		System.out.print("  ");
	    print(D[x]);
	} 
    } 

    private void print(int[][] D) {
	for (int x = 0; x < D.length; x++) {
	    print(D[x]);
	} 
    } 
    private void print(int[] a) {
	String str = "";
	for (int y = 0; y < a.length; y++)
	    str += a[y] + ",";
	System.out.println(str.substring(0, str.length() - 1));
    } 
}
