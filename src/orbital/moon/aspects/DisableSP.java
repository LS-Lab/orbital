/**
 * @(#)DisableSP.java 0.9 2000/10/15 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.aspects;

import orbital.SP;

/**
 * Disables all calls to assert ...) to get rid of the performance drawback of
 * assertion checking calls.
 *
 * @version 0.9, 2000/10/15
 * @author  Andr&eacute; Platzer
 */
aspect DisableSP extends SPUsage {
	pointcut skipping(): calls(SP, public static void skipIf(..));

	/**
	 * ignore all assertion calls.
	 */
	static around() returns void: assertions() || skipping() {

	}
	static around() returns boolean: feedbackAssertions() {
		return true;
	}
}
