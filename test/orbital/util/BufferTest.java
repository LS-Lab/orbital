/**
 * @(#)BufferTest.java 1.1 2003-06-04 Andre Platzer
 * 
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import junit.framework.*;

/**
 * A sample test case, testing buffers.
 * @version 0.9, 2003-06-04
 * @internal-version may date back to up to 1998-13-11.
 */
public class BufferTest extends check.TestCase {
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(BufferTest.class);
    }
    protected void setUp() {
    }

    public void testAppendingEditing() {
	Buffer buf = new Buffer();
	buf.append("You know now".getBytes());
	assertEquals(new String(buf.getBytes()), "You know now");
	buf.append("here is it".getBytes());
	assertEquals(new String(buf.getBytes()), "You know nowhere is it");
	buf.append('X', 5);
	assertEquals(new String(buf.getBytes()), "You know nowhere is itXXXXX");
	buf.setBuffer("Hello".getBytes());
	buf.setSize(5);
	assertEquals(new String(buf.getBytes()), "Hello");
	buf.append("_World".getBytes());
	assertEquals(new String(buf.getBytes()), "Hello_World");
	buf.remove(5, 2);
	assertEquals(new String(buf.getBytes()), "Helloorld");
	buf.remove(7, 2);
	assertEquals(new String(buf.getBytes()), "Helloor");
    } 
}
