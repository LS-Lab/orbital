/** $Id$
 * @(#)$RCSfile$ 1.1 2003-01-18 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.type;

/**
 * An interface for typed objects.  All objects that have an
 * (explicitly modelled) {@link Type type} implement this
 * interface. This especially includes expressions and symbols.
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @version-revision $Revision$, $Date$
 * @see Type
 */
public interface Typed {
    /**
     * Get the propert type of this object.
     * Proper type means the most specific type, not only a supertype.
     * @preconditions true
     * @return the type <span class="type">&tau;</span> of this expression in <span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&tau;</sub>.
     */
    Type getType();

}// Typed
