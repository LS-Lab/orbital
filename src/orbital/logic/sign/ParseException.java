/**
 * @(#)ParseException.java 1.0 2002-10-18 Andre Platzer
 * 
 * Copyright (c) 1998-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign;

/**
 * An exception thrown if an expression could not be parsed as desired.
 * This may be the case if a lexical error, or a syntax error, or a type error occurred
 * (which all are syntactical errors with respect to the Ch-0 grammar of a language).
 * <p>
 * <i><b>Note</b>: This exception may change its base class to a more general parse exception,
 * once a standard exception with enough features, like line-column number locator
 * and error offset locator are available.</i>
 * It shields users of the logic package from such a change and allows a smooth migration
 * to the new exception once it is available from a standard organization.
 * </p>
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see java.text.ParseException
 * @see org.xml.sax.SAXParseException
 * @xxx extend a more general parse exception class. Neither java.text.ParseException nor org.xml.sax.SAXParseException will do. However, how do we reuse all them nice constructors, then?
 * @todo make this a more verbose exception, one that knows about beginning and ending lines and columns, cause and ID.
 */
public class ParseException extends Exception {
    private static final long serialVersionUID = 9011745358925992935L;
    /**
     * @serial
     */
    private int errorOffset = -1;
    /**
     * @serial
     */
    private int columnNumber = -1;
    /**
     * @serial
     */
    private int lineNumber = -1;
    public ParseException(String message, int errorOffset, int lineNumber, int columnNumber, Throwable cause) {
        super(message, cause);
        this.errorOffset = errorOffset;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    public ParseException(String message, int lineNumber, int columnNumber, Throwable cause) {
        this(message, -1, lineNumber, columnNumber, cause);
    }
    public ParseException(String message, int errorOffset, Throwable cause) {
        this(message, errorOffset, -1, -1, cause);
    }
    public ParseException(String message, int errorOffset, int lineNumber, int columnNumber) {
        this(message, errorOffset, lineNumber, columnNumber, null);
    }
    public ParseException(String message, int lineNumber, int columnNumber) {
        this(message, -1, lineNumber, columnNumber);
    }
    public ParseException(String message, int errorOffset) {
        this(message, errorOffset, -1, -1);
    }
        
    public String getMessage() {
        return (getLineNumber() < 0 && getColumnNumber() < 0 ? "" : getLineNumber() + ":" + getColumnNumber())
            + (getErrorOffset() < 0 ? "" : "(@" + getErrorOffset() + ")")
            + ": " + super.getMessage();
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

    /**
     * Returns the position where the error was found.
     */
    public int getErrorOffset() {
        return errorOffset;
    }
}
