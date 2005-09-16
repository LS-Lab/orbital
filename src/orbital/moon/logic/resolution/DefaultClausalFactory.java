/**
 * @(#)ClausalFactory.java 1.2 2004-01-07 Andre Platzer
 *
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import java.util.Set;
import orbital.logic.imp.Formula;

import orbital.logic.functor.Function;
import orbital.logic.functor.Functionals;
import orbital.moon.logic.ClassicalLogic.Utilities;


/**
 * Factory for clauses and clausalsets.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see <a href="{@docRoot}/Patterns/Design/AbstractFactory.html">Abstract Factory</a>
 */
public class DefaultClausalFactory implements ClausalFactory {
    private static final boolean ORDERED = false;
    /**
     * Whether or not to use simplified clausal forms.
     */
    private static final boolean SIMPLIFYING = false;
    static boolean isSIMPLIFYING() {
        return SIMPLIFYING;
    }

    private static boolean verbose = false;
    /**
     * Add verbosity, i.e. print out a proof tree.
     */
    public void setVerbose(boolean newVerbose) {
        this.verbose = newVerbose;
    }
    private static boolean isVerbose() {
        return verbose;
    }

    // factory-methods
    
    public Clause newClause() {
        //@todo 13 I'm not sure whether ordered resolution is complete for subsumption. Since conservative approximations suffice, this is not critical to proof completeness, but still a thing to find out about.
        //@todo indexed clauses don't really help better than memorizing (clause,literal) pairs except in the rare cases of factorization?
        //return new ClauseImpl();
        return ORDERED
            ? (Clause) new OrderedClauseImpl()
            : (Clause) new IndexedClauseImpl();
    }

    public Clause createClause(Set/*<Formula>*/ literals) {
        return isVerbose()
            ? (Clause)new TraceableClauseImpl(literals)
            //      : new ClauseImpl(literals);
            : ORDERED
            ? (Clause)new OrderedClauseImpl(literals)
            : (Clause)new IndexedClauseImpl(literals);
    }

    public ClausalSet newClausalSet() {
        //@internal only use indexed clausal sets for top-level knowledgebase and setOfSupport, not in every intermediate set
        return new ClausalSetImpl();
    }

    public ClausalSet createClausalSet(Set/*<Clause>*/ clauses) {
        return new ClausalSetImpl(clauses);
    }

    // utilities
    public ClausalSet asClausalSet(Formula f) {
        return createClausalSet
            (
             Functionals.map(new Function() {
                     public Object apply(Object C) {
                         return createClause((Set)C);
                     }
                 }, Utilities.clausalForm(f, SIMPLIFYING))
             );
    }
    
}
