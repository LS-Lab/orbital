// TODO:
// mark will fail if chars have been unread before => after unread 2, match  mark is invalid since 2 will vanish
// mark&reset, read maxLen-i  instead of unread

// read maxLen, unread 2, match.
// read maxLen, unread maxLen, don't match,| mark, regex read, don't match, reset-clear

// mark,
// read maxLen, unread 2, match.
// mark, read maxLen, unread maxLen, don't match, reset-clear, mark, regex read, don't match, reset-clear


// inner mark will crash outer mark => reset crap

/*
 * @(#)RegScanner.java 0.9 1998/05/16 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

import orbital.logic.State;
import orbital.io.Token;
import orbital.io.ParseException;

import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import orbital.util.SuspiciousError;

/**
 * This class is a Scanner scanning definite Tokens and regular ones.
 * 
 * When reading Strings it will try to match it with the Tokens declared.
 * If longer Tokens match they will preceed shorter ones unless a call to
 * alternativeToken().
 * 
 * Either a Scanner uses unread for chars not really used, or he
 * employs reset onto a marked position and skips what has really been used.
 * As this implementation does.
 * 
 * RegScanner will return definite Tokens first, and then return regular ones. (slower)
 * 
 * @version 0.9, 17/05/98
 * @author  Andr&eacute; Platzer
 * @see java.util.regex.Pattern
 * @see java.util.regex.Match
 */
public
class RegScanner extends Scanner {
	private static class Debug {
		private Debug() {}
		public static void main(String arg[]) throws Exception {
			RegScanner scanner = new RegScanner(new FileReader("html.lex"));
			scanner.scan(new FileReader("html"));
			TokenSequence ts = new TokenSequence(scanner);
			Object		  o;
			while (ts.hasNext())
				System.out.println(o = ts.next());
		} 
	}


	/**
	 * Specify the Token Declarations the Scanner should recognize directly.
	 */
	public RegScanner(Token[] symbolDefs) {
		super(symbolDefs);
	}

	/**
	 * Read the Token Declarations from a Reader.
	 */
	public RegScanner(Reader symbolDefs) throws IOException {
		super(symbolDefs);
	}


	/**
	 * Implementation
	 */

	private StringBuffer part;	  // maxLen symbolPart  for Lexical ParseException
	private String		 symbolPart;	// current symbolPart trying to match as a Token

	/**
	 * return the next Token occuring in the Reader.
	 */
	protected Token readToken() throws ParseException, IOException {
		mark(maxLen);

		Token token = readTokenImpl(maxLen);
		if (token == null)
			token = alternativeToken();
		if (token.isType("SKIP"))
			return readToken();	   // repetitive recursion
		return token;
	} 

	/**
	 * returns the next alternative Token which could be interpreted
	 * from the symbolpart read.
	 * The longest possible match will be returned first.
	 * Chars read but not contained in Token will be unread.
	 */
	public Token alternativeToken() throws ParseException, IOException {
		Token token = null;

		// use longest Symbolmatch possible
		for (int tlen = symbolPart.length() - 1; tlen > 0 && token == null; tlen--) {
			reset();
			token = readTokenImpl(tlen);

			// XXX see below  lexical.unread(symbolPart.charAt(tlen));        // pushback last
		} 

		if (token == null) {
			symbolPart = null;
			reset();
			token = regexToken();
		} 
		if (token == null)
			throw new ParseException("lexical exception: No token for symbol: '" + part + "'");
		return token;
	} 

	/**
	 * implementation reading a symbolPart String (sized maxLen)
	 * and trying to match it via
	 * @see #alternativeToken()
	 * @see #regexToken()
	 * @return Token or null if it cannot be matched for current length. Then its in symbolPart.
	 */
	private Token readTokenImpl(int symbolLen) throws IOException {
		symbolPart = null;
		if (!ready())
			throw new EOFException("EOF");

		int			 len = 0;
		StringBuffer buff = new StringBuffer();
		while (len < symbolLen && ready()) {
			int c = lexical.read();
			if (c == -1)
				break;
			if (c != '\r') {
				buff.append((char) c);
				len++;
			} 
		} 

		/*
		 * char buff[] = new char[symbolLen];
		 * len = lexical.read(buff);
		 * symbolPart = new String(buff,0,len);
		 */

		if (symbolLen == maxLen)
			this.part = buff;	 // remember for Lexical ParseException

		symbolPart = buff.toString();
		Token token = matchSymbol(symbolPart);
		return token;
	} 

	/**
	 * returns the next Token matching a RegularExpression Token which
	 * results from a RegExAutomata processing over reader (as a Scanner-->TokenSeq).
	 * ??? The longest possible match will be returned first.
	 * Chars read but not contained in Token will be unread.
	 */
	private Token regexToken() throws ParseException, IOException {
		mark(30 /* maxRegLen */);
		for (int i = 0; i < symbols.length; i++) {
			if (!symbols[i].isVariable())
				continue;

			Scanner innerScanner = new AtomicScanner(false);
			innerScanner.scan(reader);	  // lexical
			TokenSequence innerTs = new TokenSequence(innerScanner);

			try {
				Object result = new RegExAutomata(symbols[i].symbol.substring(1, symbols[i].length() - 1)).processAutomata(state, innerTs);
				Token  token = new Token(symbols[i].type, symbols[i].symbol, result.toString());

				reset();
				lexical.skip(result.toString().length());	 // since inner has marked differently

				/*
				 * //XXX see above
				 * // unread(unconsumed Tokens);
				 * Enumeration e = innerTs.elements();
				 * while(e.hasMoreElements())
				 * try {
				 * unreadToken((Token)(e.nextElement()));
				 * } catch(ClassCastException imp) {throw new SuspiciousError("not a Token");}
				 */
				return token;
			} catch (ParseException not) {

				// reset Reader to retry at marked starting position
				if (markSupported())
					reset();
				else {
					System.err.println(not);

					// at least unread(unconsumed Tokens);
					Iterator e = innerTs.iterator();
					while (e.hasNext())
						try {
							unreadToken((Token) (e.next()));
						} catch (ClassCastException erm) {
							throw new SuspiciousError("not a Token? " + erm);
						} 
					throw not;
				} 
			} 

		}								  // for
		 throw new ParseException("lexical exception: No token for symbols in '" + symbolPart + "'");
	} 
}
