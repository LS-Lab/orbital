/**
 * @(#)DataReaderTest.java 0.9 1999-11-03 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import junit.framework.*;
import java.io.*;

/**
 * A sample test case, testing data writers.
 * @version $Id$
 * @internal-version may date back to up to 1998-13-11.
 */
public class DataReaderTest extends check.TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(DataReaderTest.class);
    }
    protected void setUp() {
    }

    public void consoleTestStrictEncoding() throws IOException {
        DataReader d = DataReader.getInstance(System.in, "strict");
        System.out.print("Type an int: ");
        System.out.flush();
        int i = d.readInt();
        System.out.println("int was: '" + i + "'");
        System.out.print("Type a short: ");
        System.out.flush();
        short s = d.readShort();
        System.out.println("short was: '" + s + "'");
        System.out.print("Type a boolean: ");
        System.out.flush();
        boolean b = d.readBoolean();
        System.out.println("boolean was: '" + b + "'");
        System.out.print("Type a line: ");
        System.out.flush();
        String l = d.readLine();
        System.out.println("line was: '" + l + "'");
        System.out.print("Type a char: ");
        System.out.flush();
        char c = d.readChar();
        System.out.println("char was: '" + c + "'");
        System.out.print("Type a second line: ");
        System.out.flush();
        l = d.readLine();
        System.out.println("line was: '" + l + "'");
        System.out.println("Anything more to say?");
        while (d.ready()) {
            Object o = d.readObject();
            System.out.println(o.getClass() + ":" + o);
        } 
    } 
    public void testStrictEncoding1() throws IOException {
        String s = "1234 55 true\r\nline contents\nXsecondary line contents";
        testStrictEncoding(DataReader.getInstance(new StringReader(s), "strict"), new Object[0]);
    }

    public void testStrictEncoding2() throws IOException {
        String s = "1234   55     true\nline contents\nX\nsecondary line contents";
        testStrictEncoding(DataReader.getInstance(new StringReader(s), "strict"), new Object[0]);
    }

    public void testStrictEncoding3() throws IOException {
        String s = "1234\n  55\n  TrUe\n\n\nline contents\n  \t  X\n\n\nsecondary line contents\n\n";
        testStrictEncoding(DataReader.getInstance(new StringReader(s), "strict"), new Object[0]);
    }

    protected void testStrictEncoding(DataReader d, Object additionalExpected[]) throws IOException {
        System.out.print("Type an int: ");
        System.out.flush();
        assertEquals(d.readInt(), 1234);
        System.out.print("Type a short: ");
        System.out.flush();
        assertEquals(d.readShort(), 55);
        System.out.print("Type a boolean: ");
        System.out.flush();
        assertTrue(d.readBoolean());
        System.out.print("Type a line: ");
        System.out.flush();
        assertEquals(d.readLine(), "line contents");
        System.out.print("Type a char: ");
        System.out.flush();
        assertEquals(d.readChar(), 'X');
        System.out.print("Type a second line: ");
        System.out.flush();
        assertEquals(d.readLine(), "secondary line contents");
        System.out.println("Anything more to say?");
        int i = 0;
        while (d.ready()) {
            Object o = d.readObject();
            assertTrue(i < additionalExpected.length, "not more read than expected");
            assertEquals(o, additionalExpected[i]);
            i++;
        } 
    } 
}
