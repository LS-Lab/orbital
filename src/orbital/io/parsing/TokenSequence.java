/*
 * @(#)TokenSequence.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998-2000 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

import java.util.ListIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Collections;
import java.util.LinkedList;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class administers a sequence of Tokens. These Tokens are typically
 * read from a token iterator like a Scanner. The iterator source determines
 * the type of objects returned by this sequence.
 * <p>
 * The Tokens are queried on demand.
 * This uses the Lazy-Loading-Principle.
 * 
 * @version 0.9, 08/07/2000
 * @author  Andr&eacute; Platzer
 */
public class TokenSequence implements ListIterator/*<Token>*/ {
    private static final Logger logger = Logger.getLogger(TokenSequence.class.getName());

    /**
     * The underlying source used to get next Tokens.
     * 
     * @serial
     */
    protected Iterator source;

    /**
     * sequence buffer where data from source iterator is stored.
     * @serial
     */
    protected List	   tokenseq = new LinkedList();

    /**
     * next token that will be accessed.
     * @serial
     */
    protected int	   cursor = 0;

    /**
     * Maximum Token index already accessed.
     * @serial
     */
    protected int	   lastRet = -1;

    /**
     * create a TokenSequence of the specified source.
     */
    public TokenSequence(Iterator source) {
	this.source = source;
    }

    /**
     * get the source iterator where data originally comes from.
     */
    public Iterator getSource() {
	return source;
    } 


    /**
     * Returns the index Token not yet consumed ({@link #consume()}).
     * 
     * <p>impl: Reads Token from source if necessary.</p>
     */
    private Object element(int index) throws NoSuchElementException {

	// fill buffer as long as required
	while (tokenseq.size() <= index) {
	    Object next = readToken();
	    tokenseq.add(next);
	} 
	lastRet = index;
	cursor = lastRet + 1;
	return tokenseq.get(index);
    } 

    /**
     * Tests if this Sequence has more Elements.
     */
    public boolean hasNext() {
	return cursor < tokenseq.size() || source.hasNext();
    } 

    public boolean hasPrevious() {
	return cursor > 0 || source.hasNext();
    } 

    /**
     * Returns the next Element in the Sequence not yet returned by
     * next() or element(i).
     */
    public Object next() throws NoSuchElementException {
	return element(lastRet = cursor++);
    } 

    public void remove() {
	if (lastRet == -1)
	    throw new IllegalStateException();
	consume(lastRet);
    } 

    public Object previous() throws NoSuchElementException {
	return element(lastRet = --cursor);
    } 

    public int nextIndex() {
	return cursor;
    } 

    public int previousIndex() {
	return cursor - 1;
    } 

    public void set(Object o) {
	if (lastRet == -1)
	    throw new IllegalStateException();
	tokenseq.set(lastRet, o);
    } 

    public void add(Object o) {
	tokenseq.add(cursor++, o);
	lastRet = -1;
    } 

    /**
     * return an Iterator over the elements in the Sequence.
     */
    public Iterator iterator() {
	return tokenseq.iterator();
    } 

    /**
     * Consume n_th Element.
     * @param index the index of the element to be consumed.
     */
    public void consume(int index) {
	tokenseq.remove(index);
	if (lastRet >= index) {	   // decrease counter for next element
	    lastRet--;
	    cursor--;
	} 
    } 

    /**
     * Consumes the last set of Tokens requested from the Sequence
     */
    public void consume() {
	if (lastRet == -1)
	    throw new IllegalStateException();
	tokenseq.subList(0, lastRet + 1).clear();
	cursor = 0;
	lastRet = -1;
    } 

    /**
     * Consumes all Tokens from the Sequence and therefore clears any unprocessed tokens.
     */
    public void consumeAll() {
	tokenseq.clear();
	cursor = 0;
	lastRet = -1;
    } 

    /**
     * Unconsume the last token read via next() etc.
     * @see #pushback(Object)
     */
    public void unconsume() {
	lastRet = --cursor;
    } 

    /**
     * Pushback a specified token, i.e. prepend it to the sequence of tokens.
     * <b><i>Caution</i>:<b> function not checked.
     * @see #unconsume()
     */
    public void pushback(Object token) {
	logger.log(Level.FINER, "pushback", token);
	int i = tokenseq.indexOf(token);
	if (i < 0)					// not yet contained
	    tokenseq.add(token);	// append
	else
	    cursor = i;				// reduce consume-counter
    } 

    /**
     * This function is called to read the next Token from the source iterator
     * and return it. This function will be called consecutively for each
     * Token exactly one time in order. It can be overwritten if the Tokens
     * come elsewhere or are modified on read.
     */
    protected Object readToken() throws NoSuchElementException {
	return source.next();
    } 
}
