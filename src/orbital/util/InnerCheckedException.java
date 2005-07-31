/**
 * @(#)InnerCheckedException.java 0.9 2000/03/11 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * This class is a InnerCheckedException which can be thrown if checked exceptions
 * occur which cannot be declared in the throws-clause of a method, and must
 * be wrapped in an unchecked RuntimeException to rethrow, instead.
 * <p>
 * This techique should <em>only</em> be used if a method-signature is required - for instance
 * by the <code>java.lang.Runnable</code> interface - that does not
 * allow the declaration of checked exceptions.
 * The rethrow technique breaks a good java feature of checked exceptions.
 * A method which uses InnerCheckedExceptions will break its exception contract.
 * It is not a good style to use it except for signature requirements.
 * <p>
 * For example:<pre>
 * <span class="keyword">public</span> <span class="keyword">void</span> run() {
 *     <span class="keyword">try</span> {
 *         ...
 *     }
 *     <span class="keyword">catch</span>(<var>AnyCheckedException</var> x) {<span class="keyword">throw</span> <span class="keyword">new</span> <span class="Orbital">InnerCheckedException</span>(x);}
 * }
 * </pre>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Throwable#getCause()
 * @see Throwable#initCause(Throwable)
 * @see java.lang.reflect.UndeclaredThrowableException
 * @see java.lang.reflect.InvocationTargetException
 */
public class InnerCheckedException extends RuntimeException {
    /**
     * nested inner exception that is checked and therefore cannot be thrown directly.
     * @serial
     */
    protected Throwable nested;
    public InnerCheckedException(Throwable inner) {
	this(inner, "unchecked");
    }
    public InnerCheckedException(String message, Throwable cause) {
	super(message, cause);
	this.nested = cause;
    }
    /**
     * @deprecated Since JDK1.4 use {@link InnerCheckedException#InnerCheckedException(String, Throwable)} instead.
     */
    public InnerCheckedException(Throwable cause, String message) {
	this(message, cause);
    }
    
    /**
     * Get the inner nested exception thrown.
     * @deprecated Use {@link Throwable#getCause()} instead.
     */
    //TODO: update for Merlin 1.4
    public Throwable getNextException() {
	return nested;
    } 

    /**
     * Prints the stack trace of the thrown nested exception to the specified
     * print stream.
     */
    public void printStackTrace() { 
    	synchronized (System.err) {
    	    System.err.println(this);
    	    printStackTrace(System.err);
    	}
    }

    /**
     * Prints the stack trace of the thrown nested exception to the specified
     * print stream.
     */
    public void printStackTrace(PrintStream ps) {
	synchronized (ps) {
	    if (nested != null) {
		ps.print(this);
		nested.printStackTrace(ps);
	    } else {
		super.printStackTrace(ps);
	    } 
	} 
    } 

    /**
     * Prints the stack trace of the thrown nested exception to the
     * specified print writer.
     */
    public void printStackTrace(PrintWriter pw) {
	synchronized (pw) {
	    if (nested != null) {
		pw.print(this);
		nested.printStackTrace(pw);
	    } else {
		super.printStackTrace(pw);
	    } 
	} 
    } 

    public String toString() {
    	String message = getLocalizedMessage();
    	return (message != null) ? message : super.toString();
    }
}
