/**
 * @(#)ClausalFactory.java 1.2 2004-01-07 Andre Platzer
 *
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import java.util.Set;

/**
 * Factory for clauses and clausalsets.
 * @version 1.2, 2004-01-07
 * @author  Andr&eacute; Platzer
 * @see <a href="{@docRoot}/Patterns/Design/AbstractFactory.html">Abstract Factory</a>
 */
public class DefaultClausalFactory implements ClausalFactory {

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
	return new ClauseImpl();
    }

    public Clause createClause(Set/*_<Formula>_*/ literals) {
	return isVerbose()
	    ? new TraceableClauseImpl(literals)
	    : new ClauseImpl(literals);
    }

    public ClausalSet newClausalSet() {
	return new IndexedClausalSetImpl();
    }

    public ClausalSet createClausalSet(Set/*_<Clause>_*/ clauses) {
	return new IndexedClausalSetImpl(clauses);
    }

}
