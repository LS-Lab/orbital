/**
 * @(#)StrictDataReader.java 0.9 1999/11/03 Andre Platzer
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

import java.util.logging.Level;

/**
 * Alternative implementation of DataReader with strict use of the StreamTokenizer
 * ignoring underlying streams.
 */
class StrictDataReader extends DataReader {

    /**
     * Create a DataReader parsing input from the specified character stream.
     */
    public StrictDataReader(Reader input) {
	super(input);
    }

    /**
     * Contained for compatibilitiy reasons only.
     * The preferred way to read character data from an input stream is to convert it into a character stream (Reader).
     * Use <code>DataReader(new InputStreamReader(is))</code> instead.
     * Although this method is useful for direct input or reading from System.in.
     * @see #StrictDataReader(java.io.Reader)
     */
    public StrictDataReader(InputStream input) {
	super(input);
    }

    private boolean wouldAcceptEOL = false;

    /**
     * Returns the corresponding encoding format.
     * @return a String that specifies the format that is supported by this reader.
     */
    public String getFormat() {
	return "strict";
    } 


    /**
     * Reads the next line of text from the input stream.
     * 
     * @return     the line read.
     * @throws  IOException  if an I/O error occurs.
     * @throws  EOFException  if this stream reaches the end before reading
     * any character.
     */
    public String readLine() throws IOException {
	// initialize(getStreamTokenizer(), true);
	parse.resetSyntax();
	parse.eolIsSignificant(true);
	try {
	    String nl = System.getProperty("line.separator");
	    String line = "";
	    readLineLoop:
	    while (true) {
		int typ = parse.nextToken();
		switch (typ) {
		case StreamTokenizer.TT_EOF:
		    if ("".equals(line))
			line = null;
		    break readLineLoop;
		case StreamTokenizer.TT_EOL:
		    if (wouldAcceptEOL) {	 // might still be last EOL
			wouldAcceptEOL = false;
        
			// did we only read whitespaces?
			for (int i = 0; i < line.length(); i++)
			    if (IOUtilities.whitespaces.indexOf(line.charAt(i)) < 0)
				break readLineLoop;
        
			// then restart at next line
			line = "";
			continue readLineLoop;
		    } else
			break readLineLoop;
		}
    
		// EOL recognition has been done above, so
		// skip useless EOL elongation that we are not interested in
		if (typ == '\r' || typ == '\n')
		    continue;
		line += (char) typ;
	    } 
	    return line;
	}
	finally {
	    initialize(getStreamTokenizer(), false);
	}

	/*
	 * Alternative implementation
	 * // implementation using StreamTokenizer, only
	 * parse.nextToken();
	 * int line = parse.lineno();
	 * parse.pushBack();
	 * logger.log(Level.FINEST,"readLine()","start in line "+line);
	 * StringBuffer sb = new StringBuffer();
	 * reading: do {
	 * parse.nextToken();
	 * logger.log(Level.FINEST,"readLine()","in line "+parse.lineno());
	 * if (parse.lineno()>line) {
	 * parse.pushBack();
	 * break reading;
	 * }
	 * switch(parse.ttype) {
	 * case StreamTokenizer.TT_EOF:
	 * if (sb.length()==0)
	 * throw new EOFException("EOF during read");
	 * break reading;
	 * case StreamTokenizer.TT_EOL:
	 * break reading;
	 * case StreamTokenizer.TT_NUMBER:
	 * if (sb.length()>0)
	 * sb.append(' ');
	 * sb.append(parse.nval);
	 * break;
	 * case StreamTokenizer.TT_WORD:
	 * if (sb.length()>0)
	 * sb.append(' ');
	 * sb.append(parse.sval);
	 * break;
	 * default:
	 * sb.append((char)parse.ttype);
	 * }
	 * logger.log(Level.FINEST,"readLine()","\t..>'"+sb.toString()+"'");
	 * logger.log(Level.FINEST,"readLine()","and line "+parse.lineno());
	 * if (parse.lineno()>line) {
	 * parse.pushBack();
	 * break reading;
	 * }
	 * } while(parse.ttype!=StreamTokenizer.TT_EOL && parse.ttype!=StreamTokenizer.TT_EOF);
	 * return sb.toString();
	 */
    } 

    /**
     * Reads a single input <code>char</code> and returns the <code>char</code> value.
     * 
     * @return     <code>(char)parse.read()</code>, the <code>char</code> read.
     * @throws  EOFException  if this stream reaches the end before reading
     * all the bytes.
     * @throws  IOException   if an I/O error occurs.
     */
    public char readChar() throws IOException {

	// implementation using StreamTokenizer, only
	switch (nextToken()) {
	case StreamTokenizer.TT_EOF:
	    throw new EOFException("EOF during readChar");
	case StreamTokenizer.TT_EOL:
	    return '\n';
	case StreamTokenizer.TT_WORD: {
	    char ch = parse.sval.charAt(0);
	    if (parse.sval.length() > 1) {	  // XXX: debug. better check whether it works. what's up with "xy7"?
		logger.log(Level.FINEST, "readChar()", "pushback: '" + parse.sval.substring(1) + "'");
		parse.sval = parse.sval.substring(1);
		parse.pushBack();
	    } 
	    return ch;
	} 
	case StreamTokenizer.TT_NUMBER: {
	    String s = parse.nval + "";
	    char   ch = s.charAt(0);
	    if (s.length() > 1) {	 // XXX: debug. better check whether it works. what's up with "12347.01"?
		logger.log(Level.FINEST, "readChar()", "pushback: '" + s.substring(1) + "'");
		parse.nval = Double.parseDouble(s.substring(1));
		parse.pushBack();
	    } 
	    return ch;
	} 
	default:
	    return (char) parse.ttype;
	}

	/*
	  Alternative implementation:
	  // implementation using StreamTokenizer, only
	  switch(nextToken()) {
	  case StreamTokenizer.TT_EOF:
	  throw new EOFException("no more tokens");
	  case StreamTokenizer.TT_WORD: {
	  char ch = parse.sval.charAt(0);
	  if (parse.sval.length()>1) {	//XXX debug. better check whether it works. what's up with "xy7"?
	  logger.log(Level.FINEST,"readChar()","pushback: '"+parse.sval.substring(1)+"'");
	  parse.sval = parse.sval.substring(1);
	  parse.pushBack();
	  }
	  return ch;
	  }
	  case StreamTokenizer.TT_NUMBER:
	  //TODO
	  throw new UnsupportedOperationException("not yet implemented for number case");
	  default:
	  return (char)parse.ttype;
	  }
	*/
    } 



    // dependent derived methods. implemented in terms of those above.

    /**
     * Peeks for the nextToken parsed without consuming it.
     * Overwrite to change style.
     * <p>
     * Skips any EOLs occurring.</p>
     * @return nextToken of StreamTokenizer <code>parse</code>, or StreamTokenizer.TT_EOF.
     * @throws java.io.IOException if another input/output exception occurs.
     * @see #mark(int)
     * @see java.io.StreamTokenizer#nextToken()
     * @see #reset()
     */

    // XXX: debug for    -1|5,  will return -1, |, -1, | as tokens
    protected int peekNextToken() throws IOException {
	do {
	    if (parse.nextToken() == StreamTokenizer.TT_EOF)
		break;
	} while (parse.ttype == StreamTokenizer.TT_EOL || (skipWhitespaces && IOUtilities.whitespaces.indexOf(parse.ttype) >= 0));
	parse.pushBack();

	// System.err.println("peekNextToken(): ("+parse.ttype+") == "+parse);
	return parse.ttype;

	/*
	 * // unfortunately this does not conform with readChar, since that will forget StreamTokenizers internal buffer
	 * do {
	 * if (parse.nextToken()==StreamTokenizer.TT_EOF)
	 * break;
	 * } while(parse.ttype==StreamTokenizer.TT_EOL || (skipWhitespaces && IOUtilities.whitespaces.indexOf(parse.ttype)>=0));
	 * parse.pushBack();
	 * System.err.println("peekNextToken(): ("+parse.ttype+") == "+parse);
	 * return parse.ttype;
	 */

	// Alternative implementation:
	// mark, parse and reset
	// will solely forget streamtokenizers internal single character cache
    } 

    /**
     * Returns the nextToken in a parsed style.
     * Called by all reading methods that need content in a parsed style.
     * Overwrite to change style.
     * <p>
     * Skips any EOLs occurring.</p>
     * @return nextToken of StreamTokenizer <code>parse</code>.
     * @throws java.io.EOFException if <code>nextToken()==StreamTokenizer.TT_EOF</code>.
     * @throws java.io.IOException if another input/output exception occurs.
     * @see java.io.StreamTokenizer#nextToken()
     */
    protected int nextToken() throws IOException, EOFException {
	do {
	    if (parse.nextToken() == StreamTokenizer.TT_EOF)
		throw new EOFException("EOF during read");
	} while (parse.ttype == StreamTokenizer.TT_EOL || (skipWhitespaces && IOUtilities.whitespaces.indexOf(parse.ttype) >= 0));
	accepted();
	return parse.ttype;
    } 

    /**
     * Processes any cleanup after data has been read in a parsed style.
     * Called by all reading methods that need content in a parsed style after having matched.
     * Overwrite to change style.
     * <p>
     * Used to clean EOL character after parsed input from the user. Skips an EOL occurring.</p>
     * @see #nextToken()
     */
    private void accepted() throws IOException {
	wouldAcceptEOL = true;

	// TODO: implement with the streamtokenizer

	/*
	  eolIsSignificant(true)
	  whitespaceChars(none)
	  while EOL or whitespace
	  parse.nextToken()
	  parse.pushBack();
	*/

	// Alternative implementation
	// stream tokenizer is made eolSignificant(false) so this logic transfers to lineno() in readLine()
	// -> lineno() does not work
	// implementation using StreamTokenizer, only

	/*
	  if (!ready())
	  return;
	  logger.log(Level.FINEST,"accepted()","pending");
	  if (parse.nextToken()==StreamTokenizer.TT_EOF)
	  return;
	  if (parse.ttype!=StreamTokenizer.TT_EOL)                           // consume eols, but
	  parse.pushBack();												// pushback if input continues
	  logger.log(Level.FINEST,"accepted()","is: "+parse);
	*/
    } 
}

