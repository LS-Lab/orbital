/*
 * @(#)DelimiterBreakIterator.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.text;

import java.text.BreakIterator;
import java.text.CharacterIterator;

/**
 * A DelimiterBreakIterator is a {@link java.text.BreakIterator} breaking a source {@link java.text.CharacterIterator}
 * at certain delimiters.
 * 
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
public
class DelimiterBreakIterator extends BreakIterator {
	protected String			delimiters;
	protected CharacterIterator citerator;
	/**
	 * Create a DelimiterBreakIterator breaking at any character in the given string.
	 */
	public DelimiterBreakIterator(String delimiters) {
		this.delimiters = delimiters;
	}

	public CharacterIterator getText() {
		return citerator;
	} 
	public void setText(CharacterIterator citer) {
		citerator = citer;
	} 

	/**
	 * Checks whether a character is a delimiter.
	 */
	protected boolean isDelimiter(char c) {
		return delimiters.indexOf(c) >= 0;
	}

	public int first() {
		citerator.first();
		return citerator.getIndex();
	} 

	public int last() {
		citerator.last();
		return citerator.getIndex();
	} 

	public int next(int n) {
		while (n-- > 0)
			if (next() == BreakIterator.DONE)
				return BreakIterator.DONE;
		return citerator.getIndex();
	} 

	public int next() {
		while (citerator.next() != CharacterIterator.DONE)
			if (isDelimiter(citerator.current()))
				return citerator.getIndex();
		return BreakIterator.DONE;
	} 

	public int previous() {
		while (citerator.previous() != CharacterIterator.DONE)
			if (isDelimiter(citerator.current()))
				return citerator.getIndex();
		return BreakIterator.DONE;
	} 

	public int following(int offset) {
		citerator.setIndex(offset);
		while (citerator.next() != CharacterIterator.DONE)
			if (isDelimiter(citerator.current()))
				return citerator.getIndex();
		return BreakIterator.DONE;
	} 

	public int current() {
		return citerator.getIndex();
	} 
}
