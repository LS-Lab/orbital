/**
 * @(#)SPUsage.java 0.9 2000/10/15 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.aspects;

import orbital.SP;

/**
 * aspect with join points for assertion specification uses to manipulate assertion checking calls.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
abstract aspect SPUsage {
        /**
         * join on assertion specification join points.
         */
        pointcut assertions(): calls(SP, public static void assert(..))
                        || calls(SP, public static void post(..));

        pointcut feedbackAssertions(): calls(SP, public static boolean ask(..));
}
