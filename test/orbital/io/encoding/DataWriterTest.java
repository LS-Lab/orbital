/**
 * @(#)DataWriterTest.java 0.9 2001-03-27 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import junit.framework.*;
import java.io.*;

/**
 * A sample test case, testing data writers.
 * @version 0.9, 2000/03/27
 * @internal-version may date back to up to 1998-13-11.
 */
public class DataWriterTest extends check.TestCase {
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(DataWriterTest.class);
    }
    protected void setUp() {
    }

    public void testBasicEncoding() throws IOException {
	final String nl = System.getProperty("line.separator");
	StringWriter sw;
	DataWriter d = DataWriter.getInstance(sw = new StringWriter(), "basic");
	d.writeInt(12345);
	d.writeShort(217);
	d.writeBoolean(true);
	d.writeChars("Is it all well");
	d.writeChar('X');
	d.writeUTF("better does");
	d.writeUTF("Now look!");
	d.close();
	assertEquals(sw.getBuffer().toString(), "12345, 217, #TRUE#, \"Is it all well\", \"X\", \"better does\"" + nl
		     + "\"Now look!\"" + nl);
    } 
}
