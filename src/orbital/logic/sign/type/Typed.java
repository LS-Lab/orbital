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
 * @author <a href="mailto:NOSPAM@functologic.com">Andr&eacute; Platzer</a>
 * @version 1.1, 2003-01-18
 * @version-revision $Revision$, $Date$
 * @see Type
 */
public interface Typed {
    /**
     * Get the type of this object.
     * @preconditions true
     * @return the type <span class="type">&tau;</span> of this expression in <span class="UniversalAlgebra">T</span>(&Sigma;)<sub class="type">&tau;</sub>.
     * @see Symbol#getType()
     */
    Type getType();

}// Typed
