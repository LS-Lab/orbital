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
public interface ClausalFactory {

    // factory-methods
    
    /**
     * Instantiates a new clause.
     * @return a new (yet empty) clause.
     * @postconditions RES&ne;RES
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    Clause newClause();

    /**
     * Instantiates a new clause.
     * @param literals the set of literals for the new clause.
     * @return a new clause, with the specified literals.
     * @postconditions RES&ne;RES &and; "RES=literals"
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    Clause createClause(Set/*_<Formula>_*/ literals);

    /**
     * Instantiates a new clausal set.
     * @return a new (yet empty) clausal set.
     * @postconditions RES&ne;RES
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    ClausalSet newClausalSet();

    /**
     * Instantiates a new clausal set.
     * @param clauses the set of clauses for the new clausal set.
     * @return a new clause, with the specified clauses.
     * @postconditions RES&ne;RES &and; "RES=clauses"
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    ClausalSet createClausalSet(Set/*_<Clause>_*/ clauses);

}