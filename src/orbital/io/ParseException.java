/**
 * @(#)ParseException.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.IOException;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.StreamTokenizer;

/**
 * An exception thrown if an input could not be parsed as desired.
 * Either when a lexical error occurred, or because the input did not match grammatically.
 * <p>
 * <i><b>Note</b>: This exception will be replaced by a more general parse exception,
 * once a standard exception with enough features, like line and column number locator
 * are available.</i> In the meantime however, it is safe to use it and smoothly migrate to the
 * new exception once it is available from a standard organization.</p>
 * <p>
 * Unlike {@link orbital.logic.imp.ParseException}, this class will be removed once a standard
 * parse exception is available.
 * </p>
 * @version 0.9, 1998/05/11
 * @author  Andr&eacute; Platzer
 * @see java.text.ParseException
 * @see org.xml.sax.SAXParseException
 * @see orbital.logic.imp.ParseException
 * @see <a href="http://www.webgain.com/products/java_cc/">JavaCC's ParseException</a>
 * @xxx remove this ParseException and substitute it by a more general parse exception class. Neither java.text.ParseException nor org.xml.sax.SAXParseException will do. However, how do we reuse all them nice constructors, then?
 */
public class ParseException extends Exception {
    private static final long serialVersionUID = -7066057285484626905L;
    /**
     * @serial
     */
    private int columnNumber = -1;
    /**
     * @serial
     */
    private int lineNumber = -1;
    public ParseException(String spec, int lineNumber, int columnNumber) {
	super(spec);
	this.lineNumber = lineNumber;
	this.columnNumber = columnNumber;
    }
    public ParseException(String spec) {
	super(spec);
    }
    public ParseException(String scanned, String expected) {
	this(expected + " expected, found: " + scanned);
    }
    public ParseException(StreamTokenizer scanned, String expected) {
	this(scanned + "", expected);
    }
    public ParseException(Object scanned, String expected) {
	this(scanned + "", expected);
    }
    public ParseException(Token scanned, String expected, Token[] previous) {
	this(expected + " expected after " + tokens(previous) + ", found: " + scanned);
    }
    public ParseException(Token scanned, String expected, Iterator previous) throws ClassCastException {
	this(expected + " expected after " + tokens(previous) + ", found: " + scanned);
    }
    public ParseException(Token scanned, String expected, Enumeration previous) throws ClassCastException {
	this(expected + " expected after " + tokens(previous) + ", found: " + scanned);
    }
    public ParseException(Token scanned, String expected, Token prev1, Token prev2) {
	this(expected + " expected after " + prev1.type + " " + prev2.type + ", found: " + scanned);
    }
	
    public String getMessage() {
	return getLineNumber() + ":" + getColumnNumber() + ": " + super.getMessage();
    }
	
    /**
     * The line number of the end of the text where the exception occurred. 
     * @return An integer representing the line number, or -1 if none is available.
     */
    public int getLineNumber() {
	return lineNumber;
    }

    /**
     * The column number of the end of the text where the exception occurred. 
     * The first column in a line is position 1.
     * @return An integer representing the column number, or -1 if none is available.
     */
    public int getColumnNumber() {
	return columnNumber;
    }


    private static String tokens(Token[] previous) {
	String r = "";
	for (int i = 0; i < previous.length; i++)
	    r += previous[i].type + " ";
	return r;
    } 
    private static String tokens(Enumeration previous) throws ClassCastException {
	String r = "";
	while (previous.hasMoreElements())
	    r += ((Token) previous.nextElement()).type + " ";
	return r;
    } 
    private static String tokens(Iterator previous) throws ClassCastException {
	String r = "";
	while (previous.hasNext())
	    r += ((Token) previous.next()).type + " ";
	return r;
    } 
}
