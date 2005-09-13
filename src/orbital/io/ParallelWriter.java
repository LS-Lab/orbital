/**
 * @(#)ParallelWriter.java 0.9 1999/11/03 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.FilterWriter;
import java.io.Writer;
import java.io.IOException;


/**
 * ParallelWriter can write to several character streams simultanously.
 * <p>
 * All stream operations are performed for both Writers, at once (but sequentially).
 * When an IOException occurs, the operation will first be tried for the other stream as well
 * such that both streams throw exceptions independently. This is achieved using the following construct:
 * <pre>
 * <span class="keyword">try</span> {
 *     <span class="keyword">super</span>.write(c);
 * }
 * <span class="keyword">finally</span> {
 *     parallel.write(c);
 * }
 * </pre></p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class ParallelWriter extends FilterWriter {

    /**
     * The secondary (parallel) underlying character-output stream.
     */
    protected Writer parallel;

    /**
     * Create a new parallel writer that writes each character to both output writers simultanously.
     */
    public ParallelWriter(Writer out, Writer parallelOut) {
	super(out);
	this.parallel = parallelOut;
    }

    /**
     * Write a single character.
     * 
     * @throws  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
	try {
	    super.write(c);
	} 
	finally {
	    parallel.write(c);
	} 
    } 

    /**
     * Write a portion of an array of characters.
     * 
     * @param  cbuf  Buffer of characters to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     * 
     * @throws  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException {
	try {
	    super.write(cbuf, off, len);
	} 
	finally {
	    parallel.write(cbuf, off, len);
	} 
    } 

    /**
     * Write a portion of a string.
     * 
     * @param  str  String to be written
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     * 
     * @throws  IOException  If an I/O error occurs
     */
    public void write(String str, int off, int len) throws IOException {
	try {
	    super.write(str, off, len);
	} 
	finally {
	    parallel.write(str, off, len);
	} 
    } 

    /**
     * Flush the streams.
     * 
     * @throws  IOException  If an I/O error occurs
     */
    public void flush() throws IOException {
	try {
	    super.flush();
	} 
	finally {
	    parallel.flush();
	} 
    } 

    /**
     * Close the streams.
     * 
     * @throws  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	try {
	    super.close();
	} 
	finally {
	    parallel.close();
	} 
    } 

}
