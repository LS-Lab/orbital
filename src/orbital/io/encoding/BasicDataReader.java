/**
 * @(#)BasicDataReader.java 0.9 1999/11/03 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.Reader;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.IOException;
import java.io.EOFException;
import java.io.UnsupportedEncodingException;

import orbital.io.ParseException;
import orbital.io.IOUtilities;
import orbital.math.MathUtilities;

/**
 * Basic format is a comma or newline separated list of values
 * where each value is either a number, a boolean value (<tt>#true</tt> or <tt>#false</tt>) or a string
 * in quotes (like <tt>"a string value"</tt>).
 * <p>
 * You may wish to wrap this inside an <code>oem</code> DecodingReader.
 * </p>
 */
class BasicDataReader extends DataReader {
    public BasicDataReader(Reader input) {
	super(input);
	parse.eolIsSignificant(false);
	parse.whitespaceChars(',', ',');
    }

    /**
     * Contained for compatibilitiy reasons only.
     * @deprecated The preferred way to read character data from an input stream is to convert it into a character stream (Reader).
     * Use <code>BasicDataReader(new InputStreamReader(is))</code> instead.
     * @see #BasicDataReader(java.io.Reader)
     */
    public BasicDataReader(InputStream input) {
	super(input);
	parse.eolIsSignificant(false);
	parse.whitespaceChars(',', ',');
    }

    public String getFormat() {
	return "basic";
    } 

    protected int nextToken() throws IOException, EOFException {
	if (parse.nextToken() == StreamTokenizer.TT_EOF)
	    throw new EOFException("EOF during read");
	return parse.ttype;
    } 

    protected int peekNextToken() throws IOException, EOFException {
	if (!markSupported())
	    throw new IllegalStateException("underlying stream does not support marking");
	mark(MARK_LIMIT);
	try {
	    return parse.nextToken();
	} 
	finally {
	    reset();
	} 
    } 

    public Class nextType() throws IOException {
	switch (peekNextToken()) {
	case StreamTokenizer.TT_EOF:
	    return null;
	case StreamTokenizer.TT_NUMBER:
	    if (MathUtilities.fract(parse.nval) == .0)
		return (Integer.MIN_VALUE < parse.nval && parse.nval < Integer.MAX_VALUE) ? Integer.TYPE : Long.TYPE;
	    else
		return Double.TYPE;
	case '#':
	    return Boolean.TYPE;
	case '"':
	    return String.class;
	default:
	    return Character.TYPE;
	}
    } 

    public boolean readBoolean() throws IOException {
	boolean v;
	switch (nextToken()) {
	case '#':
	    if (nextToken() != StreamTokenizer.TT_WORD)
		throw newParseException(parse, "boolean value");
	    if ("true".equalsIgnoreCase(parse.sval))
		v = true;
	    else if ("false".equalsIgnoreCase(parse.sval))
		v = false;
	    else
		throw newParseException(parse, "boolean value");
	    if (nextToken() != '#')
		throw newParseException(parse, "boolean value");
	    break;
	case StreamTokenizer.TT_NUMBER:
	    v = (0 != parse.nval);
	    break;
	default:
	    throw newParseException(parse, "boolean value");
	}
	return v;
    } 

    public char readChar() throws IOException {
	String s = readUTF();
	if (s.length() != 1)
	    throw newParseException(parse, "single character string");
	return s.charAt(0);
    } 

    /**
     * Reads in a basic formatted String.
     * format should be: <tt>"<var>any characters</var>"</tt> where a <tt>"</tt>
     * in the string characters is encoded as <tt>\"</tt> or sometimes <tt>""</tt> to distinguish from delimiters.
     */
    public String readUTF() throws IOException {
	switch (parse.nextToken()) {
	case StreamTokenizer.TT_EOF:
	    throw new EOFException("EOF during readUTF");
	case '"': {
	    // XXX: recognize strings like "alles ist ""wunderbar"" hier", too
	    String s = parse.sval;
	    return s;
	}
	default:
	    throw newParseException(parse, "string delimiter \"");
        }
    } 
}
