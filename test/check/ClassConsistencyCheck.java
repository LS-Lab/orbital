/**
 * @(#)ClassConsistencyCheck..java 1.0 2001/12/08 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package check;

import java.lang.reflect.*;
import orbital.Adjoint;
import orbital.SP;

import java.io.*;

/**
 * ClassConsistencyCheck class.
 * Checks all given classes for consistency (as far as implemented).
 *
 * @version 1.0, 2001/12/08
 * @author  Andr&eacute; Platzer
 */
public
class ClassConsistencyCheck implements Runnable {
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
    }

	public void run() {
		//@fixme this somehow does not work, but ignore even Eicar insstead
		boolean hasEquals = hasDeclaredMethod("equals", new Class[] {Object.class}) != null;
		boolean hasHashCode = hasDeclaredMethod("hashCode", new Class[] {}) != null;
		if (hasEquals != hasHashCode)
			Adjoint.print(Adjoint.ERROR, "static consistency", subject + " has declard only one of boolean equals(Object), and int hashCode()");
		boolean isSerializable = Serializable.class.isAssignableFrom(subject);
		boolean hasSerialVersionUID = hasDeclaredField("serialVersionUID") != null;
		if (isSerializable != hasSerialVersionUID)
			Adjoint.print(Adjoint.ERROR, "static consistency", subject + " has only of implements Serializable and declares serialVersionUID");
		Field uniform = conformsUniformReferent();
		if (uniform != null)
			Adjoint.print(Adjoint.WARNING, "uniform referent", subject + " possibly does not conform the concept of uniform referents it has declared a method and an attribute of the same name: " + uniform);
	}
	
	private Method hasDeclaredMethod(String name, Class[] parameterTypes) {
		try {
			Method m = subject.getDeclaredMethod(name, parameterTypes);
			SP.assert(subject.equals(m.getDeclaringClass()), "declaring class declares method");
			return m;
		}
		catch (NoSuchMethodException trial) {return null;}
	}

	private Field hasDeclaredField(String name) {
		try {
			Field m = subject.getDeclaredField(name);
			SP.assert(subject.equals(m.getDeclaringClass()), "declaring class declares field");
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