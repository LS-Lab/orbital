/**
 * @(#)ParallelOutputStream.java 0.9 1999/11/03 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;


/**
 * ParallelOutputStream can write to several binary streams simultanously.
 * <p>
 * All stream operations are performed for both OutputStreams, at once (but sequentially).
 * When an IOException occurs, the operation will be tried for the other stream as well
 * such that both streams throw exceptions independently. This is achieved using the following construct:
 * <pre>
 * <span class="keyword">try</span> {
 *     <span class="keyword">super</span>.write(b);
 * }
 * <span class="keyword">finally</span> {
 *     parallel.write(b);
 * }
 * </pre></p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class ParallelOutputStream extends FilterOutputStream {

    /**
     * The secondary (parallel) underlying output stream to write to.
     */
    protected OutputStream parallel;

    /**
     * Creates a parallel output stream built on top of the two specified
     * underlying output stream.
     * 
     * @param   out   the primary underlying output stream to be handled by
     * the superclass <tt>FilterOutputStream</tt>.
     * @param   parallelOut   the secondary (parallel) underlying output stream to be handled by
     * this class.
     */
    public ParallelOutputStream(OutputStream out, OutputStream parallelOut) {
	super(out);
	this.parallel = parallelOut;
    }

    /**
     * Writes the specified <code>byte</code> to both output streams.
     * 
     * @param      b   the <code>byte</code>.
     * @throws  IOException  if an I/O error occurs.
     */
    public void write(int b) throws IOException {
	try {
	    super.write(b);
	} 
	finally {
	    parallel.write(b);
	} 
    } 

    /**
     * Writes <code>b.length</code> bytes to both output streams.
     * 
     * @param      b   the data to be written.
     * @throws  IOException  if an I/O error occurs.
     */
    public void write(byte b[]) throws IOException {
	try {
	    super.write(b);
	} 
	finally {
	    parallel.write(b);
	} 
    } 

    /**
     * Writes <code>len</code> bytes from the specified
     * <code>byte</code> array starting at offset <code>off</code> to
     * both output streams.
     * 
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @throws  IOException  if an I/O error occurs.
     */
    public void write(byte b[], int off, int len) throws IOException {
	try {
	    super.write(b, off, len);
	} 
	finally {
	    parallel.write(b, off, len);
	} 
    } 

    /**
     * Flushes both output streams and forces any buffered output bytes
     * to be written out to the stream.
     * <p>
     * The <code>flush</code> method of <code>FilterOutputStream</code>
     * calls the <code>flush</code> method of its underlying output streams.
     * 
     * @throws  IOException  if an I/O error occurs.
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
     * Closes both output streams and releases any system resources
     * associated with the streams.
     * 
     * @throws  IOException  if an I/O error occurs.
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
