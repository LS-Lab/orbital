/**
 * @(#)Activation.java 0.9 1999/10/15 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * Activation is the base class for conditional exception handling.
 * It adds tracing, delegation and rejection to java's common terminal exception handling capabilities.
 * <p>
 * Conditional exceptions thrown with Activation semantics can be:<ul>
 * <li><i>handled</i> normally in the corresponding <span class="keyword">try</span>..<span class="keyword">catch</span> block where it occured in, with handle(<span class="Class">Object</span>).</li>
 * <li><i>delivered</i> to descendant conditional exception handlers up the activation chain with <span class="keyword">super</span>.raise(<span class="Class">Object</span>).</li>
 * <li><i>returned</i> to the part that caused it with <span class="keyword">return</span> <span class="Class">Object</span>, possibly after prophylaxis and global fixing changes.</li>
 * </ul>
 * by implementing the raise method.</p>
 * <p>
 * The creator should be set up as follows:<pre>
 * <span class="comment">// Activation derivative MyActivation</span>
 * <span class="Orbital">Activation</span> frame <span class="operator">=</span> <span class="keyword">new</span> <var>MyActivation</var>(myCaller);
 * <span class="keyword">try</span> {
 *     <span class="comment">// something like</span>
 *     <span class="keyword">if</span> (<var>some_condition</var>)
 *         frame.raise(<var>some_exception_info</var>);
 * }
 * <span class="keyword">catch</span> (<span class="Orbital">Activation</span> a) {
 *     <span class="keyword">if</span> (a <span class="operator">!=</span> frame)
 *         <span class="keyword">throw</span> a; <span class="comment">// rethrow foreign conditional exceptions</span>
 *     <span class="Class">Object</span> info <span class="operator">=</span> a.info();
 *     <span class="comment">// deal with information somehow</span>
 * }
 * </pre></p>
 * Conditional exceptions can be
 * <dl>
 *   <dt>resumed</dt> <dd>after where the exception occured.</dd>
 *   <dt>retried</dt> <dd>when the operation that caused the exception is repeated under the new circumstances.</dd>
 * </dl>
 * If their Activation instances are constructed such that they resume or retry the operations.
 * Even ordinary terminal ones can do this, but only when used within their own <span class="keyword">try</span>..<span class="keyword">catch</span> blocks (and loops).
 * 
 * @version $Id$
 * @author  Axel-Tobias Schreiner
 * @author  Bernd K&uuml;hl
 * @author  Andr&eacute; Platzer
 * @see java.lang.Throwable
 * @see java.lang.Exception
 */
public abstract class Activation extends Throwable {

    /**
     * Contains the descendant Activation object that called this activation.
     * @serial
     */
    private final Activation caller;

    /**
     * Creates a new activation.
     * @param caller the activation corresponding to the method that called this.
     */
    protected Activation(Activation caller) {
        this.caller = caller;
    }

    /**
     * Raises this conditional exception with additional information.
     * (Conditionally) sends an object up the activation chain.
     * <p>
     * This method should be overwritten in a concrete class to define reactional behaviour.
     * It is called by a descendant to try to send an object back the activation chain.
     * This implementation will deliver the conditional exception to the outer caller up the activation chain.</p>
     * Conditional exceptions thrown with Activation semantics can be:<ul>
     * <li><i>handled</i> normally in the corresponding <span class="keyword">try</span>..<span class="keyword">catch</span> block where it occured in, with handle(<span class="Class">Object</span>).</li>
     * <li><i>delivered</i> to descendant conditional exception handlers up the activation chain with <span class="keyword">super</span>.raise(<span class="Class">Object</span>).</li>
     * <li><i>returned</i> to the part that caused it with <span class="keyword">return</span> <span class="Class">Object</span>, possibly after prophylaxis and global fixing changes.</li>
     * </ul>
     * @param information to be sent up the chain.
     * @throws Activation to be caught by the creator who should retrieve the object by calling {@link #info()}.
     * @throws NullPointerException if this is the first activation in a chain.
     * @return the information sent back to descendant.
     */
    public Object raise(Object information) throws Activation {
        return caller.raise(information);
    } 

    /**
     * Initiate normal exception handling in the corresponding catch-clause.
     * Sends an object back to the creator of this conditional exception in order
     * to initiate normal exception handling in the corresponding <span class="keyword">try</span>..<span class="keyword">catch</span> block.
     * @param information any informational data the finite catch-handler needs to know about the exception.
     * @see #info()
     */
    protected final void handle(Object information) throws Activation {
        setInformation(information);
        throw this;
    } 

    /**
     * Keeps information data to be handled for retrieval by creator.
     * @serial
     */
    private Object information;

    /**
     * Get this exception's information that is to be handled.
     * @return information data to be handled, as stated in the call to {@link #handle(Object)}.
     * @see #handle(java.lang.Object)
     * @see #information
     */
    public final Object info() {
        return information;
    } 

    /**
     * Get this exception's information that is to be handled.
     * @return information data to be handled, as stated in the call to {@link #handle(Object)}.
     * @see #handle(java.lang.Object)
     * @see #information
     */
    private final void setInformation(Object newInformation) {
        this.information = newInformation;
    } 
}
