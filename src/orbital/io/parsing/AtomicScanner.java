/*
 * @(#)AtomicScanner.java 0.9 1998/05/16 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

import orbital.logic.State;
import orbital.io.Token;
import orbital.io.ParseException;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 * This class is a Scanner scanning atomic (single-char) Tokens.
 * 
 * Factory which creates an atomic Scanner recognizing every single
 * character as a Token.
 * 
 * @version 0.9, 17/05/98
 * @author  Andr&eacute; Platzer
 */
public
class AtomicScanner extends Scanner {

	/**
	 * Specify whether this scanner should skip whitespaces.
	 * @param skipWhitespace true to skip whitespaces, false if tokens for whitespaces are desired.
	 */
	public AtomicScanner(boolean skipWhitespace) {
		Token[] atomics = new Token[256];
		for (char i = 0; i < 256; i++)
			if (skipWhitespace && Character.isWhitespace(i))
				atomics[i] = new Token("SKIP", "" + i, "" + i);
			else
				atomics[i] = new Token("" + i);
		symbols = atomics;
		maxLen = 1;
	}

	/**
	 * Create an AtomicScanner that does not skip whitespaces.
	 */
	public AtomicScanner() {
		this(false);
	}

	/**
	 * Implementation
	 */

	private char part;	  // current symbolPart trying to match as a Token

	/**
	 * return the next Token occuring in the Reader.
	 */
	public Token readToken() throws ParseException, IOException {
		if (!ready())
			throw new EOFException("EOF");

		int c = lexical.read();
		if (c == -1)
			throw new EOFException("EOF");
		part = (char) c;
		Token token = matchSymbol("" + part);

		if (token == null)
			throw new ParseException("lexical exception: No token for symbol: '" + part + "'");
		if (token.isType("SKIP"))
			return readToken();	   // repetitive recursion
		return token;
	} 

	/**
	 * No alternative Token for atomic single chars.
	 */
	public Token alternativeToken() throws ParseException, IOException {
		throw new ParseException("lexical exception: No Token for Symbol. Token is unique.");
	} 
}
