/**
 * @(#)AnyInputStream.java 0.9 2000/03/27 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.BufferedInputStream;

/**
 * This class represents an any input stream of either character or binary format.
 * It comprehends character input streams as well as binary input streams.
 * <p>
 * It is <em><strong>only</strong></em> used for cases where both, Reader as well as InputStream compatibility
 * are required without platform independent encoding compatibility.
 * Otherwise, the use of {@link java.io.InputStreamReader} is preferred.</p>
 * <p>
 * This implementation will first try to delegate to the character input stream
 * and if that is not available, to the binary input stream.
 * 
 * @structure delegate: java.io.Reader
 * @structure delegate: java.io.InputStream
 * @version 0.9, 2000/03/27
 * @author  Andr&eacute; Platzer
 * @see java.io.InputStreamReader
 * @see java.io.Reader
 * @see java.io.InputStream
 */
public
class AnyInputStream extends Reader /* , InputStream */ {

	/**
	 * The Reader where data comes from.
	 * Preferred over underlying_inputstream.
	 * @see #underlying_inputstream
	 */
	protected Reader	  underlying_reader = null;

	/**
	 * The InputStream where data comes from (if no reader is specified).
	 * @see #underlying_reader
	 */
	protected InputStream underlying_inputstream = null;

	/**
	 * Create an any input stream from a character input stream.
	 */
	public AnyInputStream(Reader input, boolean forceMarkSupport) {
		if (forceMarkSupport &&!input.markSupported())
			underlying_reader = new BufferedReader(input);
		else
			underlying_reader = input;
		underlying_inputstream = null;
	}
	public AnyInputStream(Reader input) {
		this(input, false);
	}

	/**
	 * Create an any input stream from a binary input stream.
	 */
	public AnyInputStream(InputStream input, boolean forceMarkSupport) {
		underlying_reader = null;
		if (forceMarkSupport &&!input.markSupported())
			underlying_inputstream = new BufferedInputStream(input);
		else
			underlying_inputstream = input;
	}
	public AnyInputStream(InputStream input) {
		this(input, false);
	}

	/**
	 * Tell whether the underlying stream is ready to be read.
	 * Queries underlying Readers or InputStreams if specified in the constructor.
	 */
	public boolean ready() throws IOException {
		if (underlying_reader != null)
			return underlying_reader.ready();
		if (underlying_inputstream != null)
			return underlying_inputstream.available() > 0;
		throw new IllegalStateException("neither reader nor input stream is set");
	} 

	public int available() throws IOException {
		if (underlying_reader != null)
			return underlying_reader.ready() ? 1 : 0;
		if (underlying_inputstream != null)
			return underlying_inputstream.available();
		throw new IllegalStateException("neither reader nor input stream is set");
	} 

	public int read() throws IOException {
		if (underlying_reader != null)
			return underlying_reader.read();
		if (underlying_inputstream != null)
			return underlying_inputstream.read();
		throw new IllegalStateException("neither reader nor input stream is set");
	} 

	public int read(char cbuf[], int off, int len) throws IOException {
		if (underlying_reader != null)
			return underlying_reader.read(cbuf, off, len);
		if (underlying_inputstream != null) {
			byte[] b = new byte[len];
			for (int i = 0; i < len; i++)
				b[i] = (byte) cbuf[off + i];
			return underlying_inputstream.read(b, 0, len);
		} 
		throw new IllegalStateException("neither reader nor input stream is set");
	} 

	/**
	 * Tests if this input stream supports the mark and reset methods.
	 * @see java.io.Reader#markSupported()
	 */
	public boolean markSupported() {
		if (underlying_reader != null)
			return underlying_reader.markSupported();
		if (underlying_inputstream != null)
			return underlying_inputstream.markSupported();
		throw new IllegalStateException("neither reader nor input stream is set");
	} 

	/**
	 * Mark the present position in the stream.  Subsequent calls to reset()
	 * will attempt to reposition the stream to this point.  Not all
	 * character input streams support the mark() operation.
	 * @see java.io.Reader#mark(int)
	 */
	public void mark(int readAheadLimit) throws IOException {
		if (underlying_reader != null)
			underlying_reader.mark(readAheadLimit);
		else if (underlying_inputstream != null)
			underlying_inputstream.mark(readAheadLimit);
		else
			throw new IOException("mark() not supported");
	} 

	/**
	 * Reset the stream.  If the stream has been marked, then attempt to
	 * reposition it at the mark.  If the stream has not been marked, then
	 * attempt to reset it in some way appropriate to the particular stream,
	 * for example by repositioning it to its starting point.  Not all
	 * character input streams support the reset() operation, and some support
	 * reset() without supporting mark().
	 * @see java.io.Reader#reset()
	 */
	public void reset() throws IOException {
		if (underlying_reader != null)
			underlying_reader.reset();
		else if (underlying_inputstream != null)
			underlying_inputstream.reset();
		else
			throw new IOException("reset() not supported");
	} 

	/**
	 * Closes any underlying Readers or InputStreams if specified in the constructor.
	 */
	public void close() throws IOException {
		if (underlying_reader != null)
			underlying_reader.close();
		underlying_reader = null;
		if (underlying_inputstream != null)
			underlying_inputstream.close();
		underlying_inputstream = null;
	} 
}
