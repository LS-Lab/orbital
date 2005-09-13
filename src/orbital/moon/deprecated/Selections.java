/*
 * @(#)Selection.java 0.9 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import orbital.logic.functor.Function;
import orbital.logic.functor.Predicate;

import orbital.util.ReverseComparator;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Selection class encapsulates a generalization of queries over Collections.
 * These queries are build like data queries over tables with SQL.
 * In a selection query, a Collection is filtered to obtain the desired subset of data
 * which matches the criteria, with the order being induced by a Comparator.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @stereotype Module
 * @see orbital.util.Utility
 * @see java.util.Collection
 * @see orbital.algorithm.evolutionary.Selectors
 * @see <a href="http://www.sql.org">Structured Query Language (SQL)</a>
 * @deprecated Simply use the methods in {@link orbital.util.Setops} instead.
 */
public class Selections {
    /**
     * module class - prevent instantiation.
     */
    private Selections() {}

    /**
     * Creates a sophisticated selection filter.
     * <p>
     * When applied upon a collection of objects the filter will perform an operation like
     * <b>ResultSet</b>:  <span class="keyword">SELECT</span> <var>whatFilter</var>
     * <span class="keyword">FROM</span> <var>ObjectCollection</var>
     * <span class="keyword">WHERE</span> <var>Predicate</var>
     * <span class="keyword">ORDER BY</span> <var>Comparator</var> <span class="keyword">ASC</span>|<span class="keyword">DESC</span>.</p>
     * @param what states what data in the collection is requested. All if null.
     * @param where states what predicate is checked as condition for selecting data elements. None if null.
     * @param orderBy states how to sort every two data elements. No sorting if null.
     * @param asc whether to use ascending order, or descending.
     *  If false, orderBy comparator will be used reverse.
     * @return a filter that selects the specified data from the source of data it is applied upon.
     */
    public static final Function/*<Collection, Collection>*/ createSelection(final Function/*<Collection, Collection>*/ what,
                                                                             final Predicate where,
                                                                             final Comparator orderBy, final boolean asc) {
        return new Function/*<Collection, Collection>*/() {
                public Object/*>Collection<*/ apply(Object/*>Collection<*/ from) {
                    List sel = new LinkedList();        // selection of Elements with suited Adjectives
        
                    // for each in FROM
                    for (Iterator i = ((Collection) from).iterator(); i.hasNext(); ) {
        
                        // SELECT element
                        Object el = i.next();
        
                        // WHERE Adjective suits
                        if (where == null || where.apply(el))
                            sel.add(el);
                    } 
        
                    // ORDER BY sort ASC|DESC
                    if (orderBy != null)
                        Collections.sort(sel, asc ? orderBy : new ReverseComparator(orderBy));
        
                    // RESULTSET
                    return (what == null ? sel : what.apply(sel));         // filter the Data Collection of the selected Elements
                }
            };
    } 

    /**
     * Select Operation.
     * <p>
     * <b>ResultSet</b>:  <span class="keyword">SELECT</span> <var>whatFilter</var>
     * <span class="keyword">FROM</span> <var>ObjectCollection</var>
     * <span class="keyword">WHERE</span> <var>Predicate</var>
     * <span class="keyword">ORDER BY</span> <var>Comparator</var> <span class="keyword">ASC</span>|<span class="keyword">DESC</span>.</p>
     * @param what states what data in the collection is requested. All if null.
     * @param from sets the source of data.
     * @param where states what predicate is checked as condition for selecting data elements. None if null.
     * @param orderBy states how to sort every two data elements. No sorting if null.
     * @param asc whether to use ascending order, or descending.
     *  If false, orderBy comparator will be used reverse.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
     */
    public static final Collection select(Function/*<Collection, Collection>*/ what,
                                          Collection from,
                                          Predicate where,
                                          Comparator orderBy, boolean asc) {
        return (Collection) createSelection(what, where, orderBy, asc).apply(from);
    } 


    public static final Collection select(Function/*<Collection, Collection>*/ what, Collection from, Predicate where) {
        return select(what, from, where, (Comparator) null, true);
    } 
    public static final Collection select(Function/*<Collection, Collection>*/ what, Collection from) {
        return select(what, from, (Predicate) null);
    } 

    public static final Collection select(Function/*<Collection, Collection>*/ what, Collection from, Collection wherePredicates) {
        List sel = new LinkedList();    // selection of Elements with suited Adjectives

        // for each in FROM
        for (Iterator i = from.iterator(), c = wherePredicates.iterator(); i.hasNext(); ) {

            // SELECT element
            Object        el = i.next();

            Predicate where = (Predicate) c.next();

            // WHERE associated Adjective suits
            if (where == null || where.apply(el))
                sel.add(el);
        } 

        // RESULTSET
        return (what == null ? sel : (Collection) what.apply(sel));        // filter the Data Collection of the selected Elements
    } 
}
