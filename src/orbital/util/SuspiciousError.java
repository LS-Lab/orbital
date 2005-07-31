/**
 * @(#)SuspiciousError.java 1.0 1999/08/07 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * This class is a SuspiciousError thrown like an InternalError whenever logical errors raise
 * which should not normally occur for correct semantics.<p>
 * This particular error should raise if a cautious assumption fails even after
 * it should have already been tested.
 * <p>
 * As an example for distinction consider: <pre class="@todo syntax highlight">
 * <code>
 * if (a.still_available())
 *     try {
 *         assert a.count()!=0 : "still_available() and count() consistent";
 *         processAll(a);
 *         assert a.count()==0 : "all data processed";
 *     }
 *     // exceptions which simply cannot occur semantically correct
 *     catch(SemanticallyUnavailableException oops) {throw new SuspiciousError("exception unavailable");}
 *     // exceptions that are semantically and pragmatically impossible
 *     catch(ImpossibleException imp) {throw new InternalError("panic");}
 * else
 *     assert a.count()==0 : "still_available() and count() consistent";
 * </code></pre>
 * So you should distinguish two situations: <pre><code>
 * try {
 *     if (a instanceof Thing)
 *         ((Thing)a).setImportantAspect(42);
 * } // exceptions that are semantically and pragmatically impossible
 * catch(ClassCastException imp) {throw new InternalError("panic: classcast garbage");}
 * 
 * Object arg;
 * arg = new Thing("something");
 * try {
 *     // could do assert a instanceof Thing,"a Thing remains a Thing");
 *     ((Thing)a).setImportantAspect(6*9);
 * }
 * // exceptions which simply cannot occur semantically correct
 * catch(ClassCastException oops) {throw new SuspiciousError("should not happen");}
 * </code></pre>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see java.lang.InternalError
 */
public class SuspiciousError extends InternalError {
    public SuspiciousError(String message) {
	super(message);
    }

    public SuspiciousError() {}
}
