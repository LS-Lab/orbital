/**
 * @(#)LexicalConditionalUnifyingMatcher.java 0.9 2001/07/14 Andre Platzer
 * 
 * Copyright (c) 2001-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import orbital.logic.trs.*;
import orbital.moon.logic.bridge.SubstitutionImpl.UnifyingMatcher;

/**
 * Conditional unifying matcher with lexicographical comparison.
 * <p>
 * Will only match per unification if two sub terms are in lexicographical order.
 * </p>
 *
 * @version 0.9, 2001/07/14
 * @author  Andr&eacute; Platzer
 */
class LexicalConditionalUnifyingMatcher extends UnifyingMatcher {
    private static final long serialVersionUID = 1239472905613684204L;
    /**
     * The first sub term whose string representation after applying the unifier
     * is considered for condition.
     * @serial
     */
    private final Object compare1;
    /**
     * The second sub term whose string representation after applying the unifier
     * is considered for condition.
     * @serial
     */
    private final Object compare2;

    /**
     * Conditional unifying matcher with lexicographical comparison.
     * <p>
     * Will conditionally match, if the string representation of compare1 is lexicographically smaller
     * than the string representation of compare2, both after applying the unifier of
     * the pattern and expression.</p>
     * @param compare1 the first sub term whose string representation after applying the unifier
     *  is considered for condition.
     * @param compare2 the second sub term whose string representation after applying the unifier
     *  is considered for condition.
     */
    public LexicalConditionalUnifyingMatcher(Object pattern, Object substitute, Object compare1, Object compare2) {
        super(pattern, substitute);
        this.compare1 = compare1;
        this.compare2 = compare2;
    }

    public boolean matches(Object t) {
	if (!super.matches(t))
	    return false;
	Substitution mu = getUnifier();
	return compare(mu.apply(compare1), mu.apply(compare2)) < 0;
    }
   	
    /**
     * Compare regardless of negations, first,
     * and then with respect to negations thereafter.
     */
    int compare(Object o1, Object o2) {
	String s1 = o1 + "";
	String s2 = o2 + "";
	int c = smallerReprOf(s1).compareTo(smallerReprOf(s2));
	if (c != 0)
	    return c;
	else
	    return s1.compareTo(s2);
    }
    
    private String smallerReprOf(String str) {
    	StringBuffer s = new StringBuffer(str);
    	for (int i = 0; i < s.length(); i++)
	    if (s.charAt(i) == '~')
		s.deleteCharAt(i);
    	return s.toString();
    }
}
