/*
 * @(#)RegExAutomata.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

import orbital.logic.State;
import java.io.IOException;
import orbital.io.ParseException;
import java.util.List;
import java.util.Iterator;
import orbital.io.Token;

import orbital.util.Utility;
import java.util.LinkedList;

/**
 * RegExAutomata is an Automata capable of parsing a TokenSequence
 * according to a Regular Expression. It will return the longest Object (String)
 * which matches the Regular Expression (<var>RegEx</var>).
 * Mightyness: CH-3.
 * <p>
 * Regular Expressions are a sequence of:<blockquote>
 * ( &lt;char&gt;|&lt;char&gt;"-"&lt;char&gt; | "[" ( &lt;char&gt;|&lt;char&gt;"-"&lt;char&gt; )+ "]" ) [* + ?]
 * </blockquote>
 * like:<pre>
 * abbc[ax-z]_[a-z]+!z?
 * </pre>
 * <hr>
 * <a id="Syntax"></a>The Syntax of regular expression terms.
 * <table border="1">
 *   <tr>
 *     <td colspan="3">RE(&Sigma;) :=</td>
 *   </tr>
 *   <tr>
 *     <td rowspan="8" width="10%">&nbsp;</td>
 *     <td>&epsilon;</td>
 *     <td>empty word</td>
 *   </tr>
 *   <tr>
 *     <td>&cup; &Sigma;</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&cup; <big>{</big>RS <big>¦</big> R,S&isin;RE(&Sigma;)<big>}</big></td>
 *     <td>Concatenation</td>
 *   </tr>
 *   <tr>
 *     <td>&cup; <big>{</big>R|S <big>¦</big> R,S&isin;
 *       RE(&Sigma;)<big>}</big></td>
 *     <td>Alternative</td>
 *   </tr>
 *   <tr>
 *     <td>&cup; <big>{</big>[R] <big>¦</big> R&isin;RE(&Sigma;)<big>}</big></td>
 *     <td>Option</td>
 *   </tr>
 *   <tr>
 *     <td>&cup; <big>{</big>(R)<sup>*</sup> <big>¦</big>
 *       R&isin; RE(&Sigma;)<big>}</big></td>
 *     <td>Iteration</td>
 *   </tr>
 *   <tr>
 *     <td>&cup; <big>{</big>(R)<sup>+</sup> <big>¦</big>
 *       R&isin; RE(&Sigma;)<big>}</big></td>
 *     <td>Iteration (non zero)</td>
 *   </tr>
 *   <tr>
 *     <td>&cup; <big>{</big>(R) <big>¦</big> R&isin;RE(&Sigma;)<big>}</big></td>
 *     <td>Bracket grouping</td>
 *   </tr>
 *   <tr>
 *     <td colspan="3">be minimal</td>
 *   </tr>
 * </table>
 *
 * 
 * @version 0.9, 11/05/98
 * @author  Andr&eacute; Platzer
 * @see java.util.regex.Matcher
 */
public
class RegExAutomata implements Automata {
	private static class Debug {
		private Debug() {}
		public static void main(String arg[]) throws Exception {
			Scanner scanner = new AtomicScanner(false);
			System.out.println("scanner scanning reader");
			scanner.scan(new java.io.StringReader("42"));
			System.out.println("regex processing scanner");
			String m = matchingRegEx("4[0-9]+", new TokenSequence(scanner));
			System.out.println("regex finally found match " + m);
		} 
	}	 // Debug

	/**
	 * Returns the Match a TokenSequence has according to a given RegularExpression.
	 */
	public static String matchingRegEx(String regex, TokenSequence tokens) throws ParseException, IOException, ClassCastException {
		return (String) new RegExAutomata(regex).processAutomata(new State("START"), tokens);
	} 

	/**
	 * The RegularExpression scanned for. The current position in it will be keep track of.
	 */
	protected String regex;
	protected int	 pos;

	/**
	 * A "Sequence" over regex. Including fin() next() lookahead().
	 * These represent a simple Sequence an Automata scans over.
	 */
	private boolean fin() {
		return pos >= regex.length();
	} 
	private int next() {
		if (fin())
			return -1;
		return regex.charAt(pos++);
	} 
	private int lookahead() {
		if (fin())
			return -1;
		return regex.charAt(pos);
	} 

	/**
	 * Construct a RegExAutomata matching regex.
	 */
	public RegExAutomata(String regex) {
		this.regex = regex;
	}

	/**
	 * process.
	 */
	public Object processAutomata(State state, TokenSequence tokens) throws ParseException, IOException, ClassCastException, IllegalArgumentException {
		pos = 0;
		String matching = "";

		// MatchExpressions will subsequently be tried to match the incoming TokenSequence
		while (!fin()) {
			TokenMatch match = regExMatch(state);	 // what is desired next in the regular expression?
			assert match != null : "if not fin, TokenMatch should not be null. OutOfCheeseError";

			switch (lookahead()) {
				case '?':	 // Option: match is optional and also fits empty word
					next();
					if (!match.matches(tokens))
						tokens.unconsume();	   // also fulfills the regex
					else
						matching += match.getMatching();
					break;
				case '*':					   // Iteration: last match 0..* times
					next();
					while (match.matches(tokens))
						matching += match.getMatching();
					tokens.unconsume();
					break;
				case '+':					   // Iteration: last match 1..* times
					next();
					if (!match.matches(tokens))
						throw new ParseException(match + ". Does not match Regular Expression '" + regex + "' @" + pos + "(" + (fin() ? ' ' : regex.charAt(pos)) + ") in state " + state + ". Though '" + matching + "' has been matched.");
					matching += match.getMatching();
					while (match.matches(tokens))
						matching += match.getMatching();

					// try{System.out.println("\tunconsume: "+tokens.element(tokens.max));}catch(Exception x) {}
					tokens.unconsume();
					break;
				default:					   // Concatenation: character

					// Alternative: set of characters

					if (!match.matches(tokens))	   // check if it matches, normally
						throw new ParseException("Tokenseq {.." + match.getCurrentToken() + "..} doesn't match Regular Expression '" + regex + "' @" + pos + "(" + (fin() ? ' ' : regex.charAt(pos)) + ") in state " + state + ". Though '" + matching + "' has been matched.");
					matching += match.getMatching();
			}

			state.setState("MATCH");
			tokens.consume();
		}									   // while regexs
 
		return matching;
	} 

	// get the next unit of a matching expression
	private TokenMatch regExMatch(final State state) /* implements Automata */ throws ParseException, IllegalArgumentException {
		switch (lookahead()) {
			case '(':			// Brackets: group blocks
				int z = Utility.matchingBrace(regex, pos);
				if (z < 0)
					throw new IllegalArgumentException("Regular Expression '" + regex + "' has illegal brackets");
				next();
				String innerRegEx = regex.substring(pos, z);
				pos = z + 1;	// skip regex to () end
				final RegExAutomata inner = new RegExAutomata(innerRegEx);
				return new TokenMatch("(" + innerRegEx + ")") {
					public boolean matches(TokenSequence tokens) throws IOException, ClassCastException {
						try {
							matching = (String) inner.processAutomata(state, tokens);
							return true;
						} catch (ParseException notmatch) {
							return false;
						} 
					} 
				};
			case '[':			// Alternative: fits if is one of ...
				next();
				state.setState("SET_PART");
				final List set = new LinkedList();
				while (lookahead() != ']')
					if (fin())
						throw new IllegalArgumentException("Regular Expression '" + regex + "' has illegal set brackets []");
					else
						set.add(regExAtom(state));
				next();
				state.setState("SET");

				// check for inclusion in the set of possible alternatives
				return new TokenMatch("(RegEx)") {
					public boolean matches(TokenSequence tokens) throws ParseException, IOException, ClassCastException {
						Iterator e = set.iterator();
						while (e.hasNext()) {
							TokenMatch o = (TokenMatch) e.next();
							if (o.matches(tokens)) {
								matching = o.getMatching();
								tok = o.getCurrentToken();
								return true;
							} else if (e.hasNext())	   // reuse: ever but last time
								tokens.unconsume();
						} 
						return false;
					} 
				};
			default:	// ordinary character
				return regExAtom(state);
		}
	} 

	// get the next atomic character match
	private TokenMatch regExAtom(State state) /* implements Automata */ throws ParseException, IllegalArgumentException {
		if (fin())
			return null;
		final char a = (char) next();
		state.setState("SINGLE");
		if (lookahead() == '-') {
			next();
			state.setState("RANGE_ID");
			final char z = (char) next();
			;
			if (z < 0)
				throw new IllegalArgumentException("Regular Expression '" + regex + "' requires a char after '-'");
			state.setState("RANGE");

			// check for inclusion in character range
			return new TokenMatch("(" + a + "-" + z + ")") {
				public boolean matches(TokenSequence tokens) throws ParseException, IOException, ClassCastException {
					if (!tokens.hasNext())
						return false;
					tok = (Token) tokens.next();
					if (tok.symbol.length() != 1)
						throw new ParseException("lexical exception: IllegalArgumentException: " + tok + " is not atomic.");
					matching = tok.token;
					return a <= tok.symbol.charAt(0) && tok.symbol.charAt(0) <= z;
				} 
			};
		} 

		// check for ordinary character identity
		return new TokenMatch("(" + a + ")") {
			public boolean matches(TokenSequence tokens) throws ParseException, IOException, ClassCastException {
				if (!tokens.hasNext())
					return false;
				tok = (Token) tokens.next();
				if (tok.symbol.length() != 1)
					throw new ParseException("lexical exception: IllegalArgumentException: " + tok + " is not atomic.");
				matching = tok.token;
				return tok.symbol.charAt(0) == a;
			} 
		};
	} 
	private static abstract class TokenMatch extends Token {
		protected Token  tok = null;
		protected String matching = null;
		protected TokenMatch(String spec) {
			super("regex", spec);
		}
		public abstract boolean matches(TokenSequence tokens) throws ParseException, IOException, ClassCastException;

		public String getMatching() {
			return matching;
		} 
		public Token getCurrentToken() {
			return tok;
		} 
		public String toString() {
			return "TokenMatch " + symbol + " in TokenSeq {.." + tok + "..} matched '" + matching + "'.";
		} 
	}
}
