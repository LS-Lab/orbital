/*
 * @(#)AtomicTokenSequence.java 0.9 1998/04/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

import orbital.io.Token;
import java.util.NoSuchElementException;
import java.io.IOException;

/**
 * This class cuts a TokenSequence into a sequence of atomic tokens
 * (each sized exactly one character).
 * 
 * @version 0.9, 11/05/98
 * @author  Andr&eacute; Platzer
 */
public
class AtomicTokenSequence extends TokenSequence {
	protected int   tokenPos;
	protected Token currentToken;
	protected Object readToken() throws NoSuchElementException {
		if (currentToken == null || tokenPos >= currentToken.token.length()) {
			currentToken = (Token) super.readToken();
			tokenPos = 0;
		} 
		return new Token(currentToken.type, currentToken.symbol, "" + currentToken.token.charAt(tokenPos++));
	} 

	public AtomicTokenSequence(Scanner scanner) {
		super(scanner);
		currentToken = null;
	}

	public boolean hasNext() {
		return (currentToken != null && tokenPos < currentToken.token.length()) || super.hasNext();
	} 
}
