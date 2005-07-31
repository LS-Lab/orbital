/**
 * @(#)ClassConsistencyCheck..java 1.0 2001/12/08 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package check;

import java.lang.reflect.*;

import java.io.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * ClassConsistencyCheck class.
 * Checks all given classes for consistency (as far as implemented).
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class ClassConsistencyCheck implements Runnable {
    private static final Logger logger = Logger.getLogger(ClassConsistencyCheck.class.getName());
    public static void main(String arg[]) throws Exception {
	if (arg.length == 0 || "-?".equals(arg[0])) {
	    System.out.println(usage);
	    return;
	} 
	try {
	    new ClassConsistencyCheck(Class.forName(arg[0].replace('/', '.'))).run();
	}
	catch (Throwable e) {
	    System.out.println(arg[0]);
	    e.printStackTrace(System.out);
	}
    } 
    public static final String usage = "usage: " + ClassConsistencyCheck.class + " [className]" + System.getProperty("line.separator") + "Where className is a fully qualified class name";

    private Class subject;
    public ClassConsistencyCheck(Class subject) {
        this.subject = subject;
	if (!getClass().desiredAssertionStatus())
	    logger.log(Level.WARNING, "assertions may be disabled"); 
    }

    public void run() {
	//@fixme this somehow does not work, but ignores even Eicar insstead
	boolean hasEquals = hasDeclaredMethod("equals", new Class[] {Object.class}) != null;
	boolean hasHashCode = hasDeclaredMethod("hashCode", new Class[] {}) != null;
	if (hasEquals != hasHashCode)
	    logger.log(Level.SEVERE, "static consistency {0} has declard only one of boolean equals(Object), and int hashCode()", subject);
	boolean isSerializable = Serializable.class.isAssignableFrom(subject);
	boolean hasSerialVersionUID = hasDeclaredField("serialVersionUID") != null;
	if (isSerializable != hasSerialVersionUID)
	    logger.log(Level.SEVERE, "static consistency {0} has only of implements Serializable and declares serialVersionUID", subject);
	Field uniform = conformsUniformReferent();
	if (uniform != null)
	    logger.log(Level.WARNING, "uniform referent {0} possibly does not conform the concept of uniform referents it has declared a method and an attribute of the same name: {1}", new Object[] {subject, uniform});
    }
	
    private Method hasDeclaredMethod(String name, Class[] parameterTypes) {
	try {
	    Method m = subject.getDeclaredMethod(name, parameterTypes);
	    assert subject.equals(m.getDeclaringClass()) : "declaring class declares method";
	    return m;
	}
	catch (NoSuchMethodException trial) {return null;}
    }

    private Field hasDeclaredField(String name) {
	try {
	    Field m = subject.getDeclaredField(name);
	    assert subject.equals(m.getDeclaringClass()) : "declaring class declares field";
	    return m;
	}
	catch (NoSuchFieldException trial) {return null;}
    }
	
    private Field conformsUniformReferent() {
	Method[] m = subject.getMethods();
	for (int i = 0; i < m.length; i++) {
	    Field f = hasDeclaredField(m[i].getName());
	    if (f != null)
		return f;
	}
	return null;
    }
	
    /**
     * "Eicar" test class attracting errors.
     * It hurts almost all consistency checks.
     */
    class Eicar implements Serializable {
	public boolean equals(Object o) {
	    return false;
	}
    }
}
