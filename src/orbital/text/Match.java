/*
 * @(#)PatternMatching.java 0.9 1999/02/10 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.text;

/**
 * This class represents a Match that results from a call to a PatternMatching algorithm.
 * It contains a pattern and a text part that are filled with spaces for inserts and deletes.
 * Replacements are simply contained with different characters in both strings.
 * <p>
 * Strings are kept in StringBuffers to speed up updates.
 * 
 * @version 0.9, 10/02/99
 * @author  Andr&eacute; Platzer
 */
public
class Match {
	public Match(StringBuffer pattern, StringBuffer text) {
		this.pattern = pattern;
		this.text = text;
	}

	public Match(String pattern, String text) {
		this.pattern = new StringBuffer(pattern);
		this.text = new StringBuffer(text);
	}

	public Object clone() {
		return new Match(pattern.toString(), text.toString());
	} 

	protected int		   index;
	protected StringBuffer pattern;
	protected StringBuffer text;

	/**
	 * Get the index that somehow specifies the position of the match in text.
	 * Depending upon implementation this might as well be the starting as the ending position.
	 */
	public int getIndex() {
		return index;
	} 
	public void setIndex(int index) {
		this.index = index;
	} 
	public String getPattern() {
		return new StringBuffer(pattern.toString()).reverse().toString();
	} 
	public void setPattern(StringBuffer buf) {
		pattern = buf;
	} 
	public String getText() {
		return new StringBuffer(text.toString()).reverse().toString();
	} 
	public void setText(StringBuffer buf) {
		text = buf;
	} 

	/**
	 * Get the length of the Match.
	 */
	public int length() {
		return text.length();
	} 

	public void append(char patt, char tex) {
		pattern.append(patt);
		text.append(tex);
	} 

	public String toString() {
		String nl = System.getProperty("line.separator");
		return "[Index=" + index + ", " + nl + "Text=\t" + getText() + ", " + nl + "Pattern=" + getPattern() + ']';
	} 
}
