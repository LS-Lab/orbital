/*
 * @(#)Parser.java 0.9 1998/04/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

import orbital.logic.State;
import java.io.IOException;
import orbital.io.ParseException;

/**
 * This class is an abstract basic Parser capable of parsing over a Scanner's TokenSequence
 * as specified in the call to parse(ts). The Parser keeps track of its
 * current State.
 * As a result it will return a continous flow of Symbols via the
 * nextSymbol() function which ought to be overwritten.
 * 
 * @version 0.9, 11/05/98
 * @author  Andr&eacute; Platzer
 * 
 * @see <a href="package-summary.html#Parser">Parser explanation</a>
 */
public
abstract class Parser {
	//TODO: introduce a subclass parsing SLL(1) specified by BNF. Important: First, Follow sets

	/**
	 * The TokenSequence over which the Parser runs.
	 * 
	 * @serial
	 */
	protected TokenSequence tokens;

	/**
	 * The Parser's current State (for Automata...).
	 * 
	 * @serial
	 */
	protected State			state;

	/**
	 * start parsing from a Scanner's TokenSequence.
	 * Subsequently call nextSymbol();
	 */
	public void parse(TokenSequence tokens) throws IOException {
		this.tokens = tokens;
		state = new State();
	} 
	public void parseAll(TokenSequence tokens) throws ParseException, IOException {
		parse(tokens);
		Symbol symbol;
		while (tokens.hasNext()) {
			symbol = nextSymbol();
			if (symbol != null)
				System.out.println("Parsed Symbol: " + symbol);
		} 
	} 


	/**
	 * consecutively scan a Token via nextToken() and return the next Symbol
	 * parsed for it.
	 * The TokenSequence will be checked for correct grammar.
	 * 
	 * @throws ParseException otherwise, when TokenSequence is grammatically incorrect.
	 */
	public abstract Symbol nextSymbol() throws ParseException, IOException;
}
