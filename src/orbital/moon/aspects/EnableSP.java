/**
 * @(#)EnableSP.java 0.9 2000/10/15 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.aspects;

import orbital.SP;

/**
 * Enables all calls to assert ...) but wraps them inside a check for skipping
 * to get rid of the performance drawback of assertion checking calls.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
aspect EnableSP extends SPUsage {
        /**
         * ignore all assertion calls.
         */
        static around() returns void: assertions() {
                if (SP.reporter != null && !SP.reporter.isSkipping())
                        thisJoinPoint.runNext();
        }
        static around() returns boolean: feedbackAssertions() {
                if (SP.reporter != null && !SP.reporter.isSkipping())
                        return thisJoinPoint.runNext();
                else
                        return true;
        }
}
