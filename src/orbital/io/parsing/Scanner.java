/*
 * @(#)Scanner.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

import orbital.logic.State;
import orbital.io.Token;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.Reader;
import orbital.io.ParseException;

import java.io.PushbackReader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import orbital.util.SuspiciousError;

/**
 * This class is an abstract basic Scanner capable of scanning over a Reader or
 * inner Scanner.
 * <p>
 * The occuring Tokens in the Reader will be returned consecutively via
 * {@link #readToken()} which ought to be overwritten.</p>
 * 
 * <p>The Token Declarations are specified via a Token-array or a Reader declaration file.</p>
 * 
 * <p>When reading Strings it will try to match it with the Tokens declared.
 * If longer Tokens match they will preceed shorter ones unless a call to
 * {@link #alternativeToken()} occurs which will return the next shorter one, instead.</p>
 * 
 * @version 0.9, 11/05/98
 * @author  Andr&eacute; Platzer
 * @see <a href="package-summary.html#Scanner">Scanner explanation</a>
 */
public
abstract class Scanner implements Iterator {

	/**
	 * The Tokens declared which will be matched if they occur in the Reader.
	 * Tokens may be declared:<ul>
	 * <li>definite: fixed like <tt>endif</tt>
	 * <li>regular: variable like <tt>(A+[a-z]*)</tt> via regular Expressions.
	 * The maximum length is calced since required for longest match first.
	 * @serial
	 */
	protected Token[] symbols;

	/*
	 * The maximum length is calced since required for longest match first.
	 * @serial
	 */
	protected int	  maxLen = -1;

	/**
	 * The current lexical State.
	 * @serial
	 */
	protected State   state;

	/**
	 * Create an empty Scanner without Token Declarations.
	 */
	protected Scanner() {
		state = new State();
		symbols = null;
	}

	/**
	 * Specify the Token Declarations the Scanner should recognize, directly.
	 */
	protected Scanner(Token[] symbolDefs) {
		state = new State();
		symbols = symbolDefs;
		for (int i = 0; i < symbols.length; i++)
			if (symbols[i].length() > maxLen &&!symbols[i].isVariable())
				maxLen = symbols[i].length();
	}

	/**
	 * Read the Token Declarations from a Reader.
	 */
	protected Scanner(Reader symbolDefs) throws IOException {
		state = new State();
		LinkedList v = new LinkedList();
		while (symbolDefs.ready())
			v.add(new Token(symbolDefs));

		symbols = new Token[v.size()];
		int i = 0;
		for (Iterator it = v.iterator(); it.hasNext(); i++)
			try {
				symbols[i] = (Token) (it.next());
				if (symbols[i].length() > maxLen &&!symbols[i].isVariable())
					maxLen = symbols[i].length();
			} catch (ClassCastException oops) {
				throw new SuspiciousError("not a Token? " + oops);
			} 
	}


	/**
	 * The current Reader (specified in the call to scan(..) ) as a PushbackReader of reader.
	 * @serial
	 * @see #reader
	 */
	protected PushbackReader lexical = null;

	/**
	 * The real Reader will be refered to as reader.
	 * @serial
	 * @see #scan
	 */
	protected Reader		 reader = null;

	/**
	 * Start scanning over a Reader. Occuring Tokens will be returned
	 * progressively via next().
	 */
	public void scan(Reader text) throws IOException {

		// force markSupport
		if (text.markSupported())
			reader = text;
		else
			reader = new BufferedReader(text);
		lexical = new PushbackReader(reader, maxLen);
		state.setState("SCANNING");
	} 

	/**
	 * Start scanning over an underlying Scanner. Occuring Tokens will be returned
	 * progressively via next().
	 */
	public void scan(Scanner inscanner) throws IOException {
		StringBuffer text = new StringBuffer();
		while (inscanner.hasNext())
			text.append(((Token) inscanner.next()).token);
		reader = new StringReader(text.toString());
		scan(reader);
	} 

	// Implementation

	/**
	 * Read the next Token occuring in the Reader.
	 * <p>
	 * Try to match it via {@link #alternativeToken()}
	 * if <code>Token.isType("SKIP")</code> then skip it andd return next Token.</p>
	 * <p>Caution: do not mix calls with {@link #next() next()} for caching reasons.</p>
	 * @return returns the next Token occuring in the Reader.
	 * @see #alternativeToken()
	 * @throws   java.io.EOFException	if the reader is at EOF, so no more tokens are available.
	 */
	protected abstract Token readToken() throws ParseException, IOException;

	/**
	 * Gets the next alternative Token which could be interpreted for the symbolpart read last.
	 * The longest possible match (shorter than the previous) will be returned first.
	 * @return returns the next alternative Token occuring in the Reader.
	 * @throws   java.io.EOFException	if the reader is at EOF, so no more tokens are available.
	 */
	public abstract Token alternativeToken() throws ParseException, IOException;


	/**
	 * one-cache object used for safe hasNext() which would trap over skipped input otherwise
	 */
	private Object cache = null;

	/**
	 * Returns the next Token occuring in the Reader.
	 * <p>Caution: do not mix calls with {@link #readToken() readToken()} for caching reasons.</p>
	 * @throws NoSuchElementException on IOExceptions.
	 * @see #readToken()
	 */
	public Object next() throws NoSuchElementException {
		try {
			if (cache != null) {
				Object r = cache;
				cache = null;
				return r;
			} 
			return readToken();
		} catch (ParseException x) {
			throw new NoSuchElementException(x.toString());
		} catch (IOException x) {
			throw new NoSuchElementException(x.toString());
		} 
	} 

	/**
	 * Tests if this Scanner has more chars to scan.
	 * On exception returns false.
	 */
	public boolean hasNext() {
		try {
			cache = next();
			return true;
		} catch (NoSuchElementException x) {
			return false;
		} 
	} 

	/**
	 * Operation not supported. throws UnsupportedOperationException.
	 * @throws java.lang.UnsupportedOperationException
	 */
	public void remove() {
		throw new UnsupportedOperationException("unmodifiable scanner: no remove");
	} 

	// Support Methods.

	/**
	 * Unread a Token to the PushbackReader lexical as the next Chars available
	 * for reading once.
	 */
	public void unreadToken(Token token) throws IOException {
		lexical.unread(token.symbol.toCharArray());
	} 

	/**
	 * Tell whether this Scanner is ready for further charactes to be read.
	 * @throws  java.io.IOException  If an I/O error occurs
	 */
	public boolean ready() throws IOException {
		return lexical.ready();
	} 

	/**
	 * Tests if this input stream supports the mark and reset methods.
	 * @see java.io.Reader#markSupported()
	 */
	public boolean markSupported() {
		if (lexical.markSupported())
			return true;
		return reader.markSupported();
	} 

	/**
	 * Mark the present position in the stream.  Subsequent calls to reset()
	 * will attempt to reposition the stream to this point.  Not all
	 * character input streams support the mark() operation.
	 * @see java.io.Reader#mark(int)
	 */
	public void mark(int readAheadLimit) throws IOException {
		if (lexical.markSupported())
			lexical.mark(readAheadLimit);
		else if (reader.markSupported())
			reader.mark(readAheadLimit);
		else
			throw new IOException("mark() not supported");
	} 

	/**
	 * Reset the stream.  If the stream has been marked, then attempt to
	 * reposition it at the mark.  If the stream has not been marked, then
	 * attempt to reset it in some way appropriate to the particular stream,
	 * for example by repositioning it to its starting point.  Not all
	 * character input streams support the reset() operation, and some support
	 * reset() without supporting mark().
	 * @see java.io.Reader#reset()
	 */
	public void reset() throws IOException {
		if (lexical.markSupported())
			lexical.reset();
		else if (reader.markSupported()) {
			reader.reset();

			// Pushback will return unread data first and use resetted chars then.
			lexical = new PushbackReader(reader, maxLen);
		} else
			throw new IOException("reset() not supported");
	} 

	/**
	 * Checks the definite Token that matches the specified Symbol. Returns null otherwise.
	 * Utilitiy method.
	 */
	protected Token matchSymbol(String symbPart) {
		for (int i = 0; i < symbols.length; i++)
			if (symbols[i].equals(symbPart) &&!symbols[i].isVariable())
				return symbols[i];
		return null;
	} 
}
