/**
 * @(#)DataWriter.java 0.9 2000/03/27 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.DataOutput;
import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FilterWriter;
import java.io.UnsupportedEncodingException;

import java.io.StreamTokenizer;
import java.io.OutputStreamWriter;

/**
 * The <code>DataWriter</code> class is an implementation of the {@link java.io.DataOutput} interface
 * that provides for writing parsable formatted data to a character stream.
 * Thus it provides writing human-understandable output that is parsable again like
 * <tt>Hello World</tt>, <tt>12345</tt> or <tt>true</tt>.
 * <p>
 * For all the methods in this interface that
 * write characters, it is generally true that if
 * a character cannot be written for any reason,
 * an <code>IOException</code> is thrown.</p>
 * 
 * @version 0.9, 2000/03/27
 * @author  Andr&eacute; Platzer
 * @see java.io.DataOutput
 * @see DataWriter
 * @see EncodingWriter
 * @see java.io.PrintWriter
 * @see java.io.FilterWriter
 * @see java.io.FilterOutputStream
 */
public	  // TODO: is EncodingWriter desired?
class DataWriter extends /* EncodingWriter */ FilterWriter implements DataOutput {
	private static class Debug {
		private Debug() {}
		public static void main(String arg[]) throws Exception {
			DataWriter d = DataWriter.getInstance(new OutputStreamWriter(System.out), "basic");
			d.writeInt(12345);
			d.writeShort(217);
			d.writeBoolean(true);
			d.writeChars("Is it all well");
			d.writeChar('X');
			d.writeUTF("better does");
			d.writeUTF("Now look!");
			d.close();
		} 
	}	 // Debug


	/**
	 * Generates an DataWriter object for the specified type of encoding.
	 * <p>
	 * Call
	 * <kbd class="command">java orbital.io.encoding.DataWriter -?</kbd>
	 * to get an up to date list of the supported encodings on a system.
	 * </p>
	 * @param wr which writer to encode.
	 * @param encoding which encoding writer to instantiate with <code>wr</code>.
	 * @return an instance fitting the encoding
	 * or <code>null</code> if no encoding is necessary. (for efficiency reasons).
	 * @throws UnsupportedEncodingException if no writer is found for the given encoding.
	 * @throws IOException if the instantiation throws an IOException.
	 * @see DataReader#getInstance(Reader, String)
	 * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
	 */
	public static DataWriter getInstance(Writer wr, String encoding) throws UnsupportedEncodingException, IOException {
		if (encoding == null)
			return null;
		if ("".equals(encoding) || "default".equalsIgnoreCase(encoding) || "none".equalsIgnoreCase(encoding))
			return new DataWriter(wr);
		if ("basic".equalsIgnoreCase(encoding))
			return new BasicDataWriter(wr);
		throw new UnsupportedEncodingException("no writer found for encoding: " + encoding);
	} 

	/**
	 * Designates the type of a boolean.
	 * @see StreamTokenizer#TT_NUMBER
	 * @see StreamTokenizer#TT_WORD
	 */
	public static final int TT_BOOLEAN = -17;

	/**
	 * Create a DataWriter writing output to the specified character stream.
	 */
	public DataWriter(Writer output) {
		super(output);
	}

	/**
	 * Contained for compatibilitiy reasons only.
	 * The preferred way to read character data from an input stream is to convert it into a character stream (Reader).
	 * Use <code>DataWriter(new OutputStreamWriter(os))</code> instead.
	 * @see #DataWriter(java.io.Writer)
	 */
	public DataWriter(OutputStream output) {
		this(new OutputStreamWriter(output));
	}

	/**
	 * Returns the corresponding encoding format.
	 * @return a String that specifies the format that is supported by this writer.
	 */
	public String getFormat() {
		return "default";
	} 


	/**
	 * not supported.
	 * @throws java.lang.UnsupportedOperationException on every call.
	 */
	public void write(byte b[]) throws IOException {
		throw new UnsupportedOperationException();
	} 

	/**
	 * not supported.
	 * @throws java.lang.UnsupportedOperationException on every call.
	 */
	public void write(byte b[], int off, int len) throws IOException {
		throw new UnsupportedOperationException();
	} 


	// basic methods.

	/**
	 * Writes the representation of an <code>int</code> value to the output stream.
	 * Basic method.
	 * <p>
	 * The bytes written by this method may be read
	 * by the <code>readInt</code> method of interface
	 * <code>DataReader</code> , which will then
	 * return an <code>int</code> equal to <code>v</code>.</p>
	 * 
	 * @param      v   the <code>int</code> value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 */
	public void writeInt(int v) throws IOException {
		nextToken(StreamTokenizer.TT_NUMBER, "" + v);
	} 

	/**
	 * Writes the representaiton of an <code>long</code> value output stream.
	 * Basic method.
	 * <p>
	 * The bytes written by this method may be
	 * read by the <code>readLong</code> method
	 * of interface <code>DataInput</code> , which
	 * will then return a <code>long</code> equal
	 * to <code>v</code>.</p>
	 * 
	 * @param      v   the <code>long</code> value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 */
	public void writeLong(long v) throws IOException {
		nextToken(StreamTokenizer.TT_NUMBER, "" + v);
	} 

	/**
	 * Writes the representation of a <code>double</code> value to the output stream.
	 * Basic method.
	 * <p>
	 * The bytes written by this method
	 * may be read by the <code>readDouble</code>
	 * method of interface <code>DataInput</code>,
	 * which will then return a <code>double</code>
	 * equal to <code>v</code>.</p>
	 * 
	 * @param      v   the <code>double</code> value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 */
	public void writeDouble(double v) throws IOException {
		nextToken(StreamTokenizer.TT_NUMBER, "" + v);
	} 

	/**
	 * Writes the representation of a <code>boolean</code> value to this output stream.
	 * Basic method.
	 * <p>
	 * The byte written by this method may
	 * be read by the <code>readBoolean</code>
	 * method of interface <code>DataInput</code>,
	 * which will then return a <code>boolean</code>
	 * equal to <code>v</code>.</p>
	 * 
	 * @param      v   the boolean to be written.
	 * @throws  IOException  if an I/O error occurs.
	 */
	public void writeBoolean(boolean v) throws IOException {
		nextToken(TT_BOOLEAN, "" + v);
	} 


	/**
	 * Writes the representation of a <code>char</code> value to the
	 * output stream.
	 * Basic method.
	 * <p>
	 * The bytes written by this method may be
	 * read by the <code>readChar</code> method
	 * of interface <code>DataInput</code> , which
	 * will then return a <code>char</code> equal
	 * to <code>(char)v</code>.</p>
	 * 
	 * @param      v   the <code>char</code> value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 */
	public void writeChar(int v) throws IOException {
		nextToken(v, null);
	} 

	/**
	 * Writes every character in the string <code>s</code>,
	 * to the output stream in a parsable way.
	 * 
	 * @param      s   the string value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 */
	public void writeChars(String s) throws IOException {
		nextToken(StreamTokenizer.TT_WORD, s);
	} 

	/**
	 * Writes a string to the output stream bytewise regardless of any special parsable delimiters (excep, f.ex. a trailing space).
	 * 
	 * @param      s   the string of bytes to be written.
	 * @throws  IOException  if an I/O error occurs.
	 * @see #writeChars(java.lang.String)
	 */
	public void writeBytes(String s) throws IOException {
		nextToken('@', s);
	} 

	/**
	 * Reads a String in a well defined way.
	 * <p>
	 * UTF-encoding is not defined for character streams.
	 * Sub classes may decide to read in a String in another well defined format
	 * that does not require additional formatting conventions by the user.</p>
	 * 
	 * @param      str   the string value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 * @throws UnsupportedOperationException if no such encoding is defined for this character stream.
	 */
	public void writeUTF(String str) throws IOException {
		nextToken('"', str);
	} 



	// dependent derived methods. implemented in terms of those above.

	/**
	 * Writes a <code>short</code> value.
	 * 
	 * @param      v   the <code>short</code> value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 * @see #writeInt(int)
	 */
	public void writeShort(int v) throws IOException {
		writeInt(v);
	} 

	/**
	 * Writes <code>byte</code> value.
	 * 
	 * @param      v   the byte value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 * @see #writeInt(int)
	 */
	public void writeByte(int v) throws IOException {
		writeInt(v);
	} 

	/**
	 * Writes a <code>float</code> value,
	 * 
	 * @param      v   the <code>float</code> value to be written.
	 * @throws  IOException  if an I/O error occurs.
	 * @see #writeDouble(double)
	 */
	public void writeFloat(float v) throws IOException {
		writeDouble(v);
	} 

	/**
	 * Write an object to the underlying storage or stream.
	 * @throws IOException	if any of the usual Input/Output related exceptions occurs.
	 */
	public void writeObject(Object o) throws IOException {
		Class c = o.getClass();
		if (c.equals(Byte.TYPE))
			writeByte(((Byte) o).byteValue());
		else if (c.equals(Short.TYPE))
			writeShort(((Short) o).shortValue());
		else if (c.equals(Integer.TYPE))
			writeInt(((Integer) o).intValue());
		else if (c.equals(Long.TYPE))
			writeLong(((Long) o).longValue());
		else if (c.equals(Float.TYPE))
			writeFloat(((Float) o).floatValue());
		else if (c.equals(Double.TYPE))
			writeDouble(((Double) o).doubleValue());
		else if (o instanceof Number)
			;

		else if (c.equals(Boolean.TYPE))
			writeBoolean(((Boolean) o).booleanValue());
		else if (c.equals(Character.TYPE))
			writeChar(((Character) o).charValue());
		else if (c.equals(String.class))
			writeUTF((String) o);
		throw new IllegalArgumentException("could not handle type of object " + o + " of type " + c);
	} 


	// implementation helpers

	/**
	 * Writes the nextToken in the representation style.
	 * Called by all writing methods that provide content in a parsable style.
	 * Overwrite to change style.
	 * <p>
	 * Writes to out converting a ttype of
	 * <code>TT_EOL</code> into the value of system property <code>line.separator</code>.
	 * Will write the value if it is not null, and the ttype otherwise.</p>
	 * @param ttype the type of the token to write, according to {@link StreamTokenizer#ttype}.
	 * @param value the string representation to write as the token.
	 * If it is <code>null</code>, will write ttype, instead.
	 * @throws java.io.IOException if an input/output exception occurs.
	 * @see java.io.StreamTokenizer#TT_NUMBER
	 * @see java.io.StreamTokenizer#TT_WORD
	 * @see #TT_BOOLEAN
	 * @see java.io.StreamTokenizer#TT_EOL
	 */
	protected void nextToken(int ttype, String value) throws IOException {
		switch (ttype) {
			case StreamTokenizer.TT_EOF:
				throw new IllegalArgumentException("eof should not be written. Simply use close instead");
			case StreamTokenizer.TT_EOL:
				out.write(System.getProperty("line.separator"));
				break;
			case StreamTokenizer.TT_NUMBER:
				out.write(value);
				out.write(' ');
				break;
			case TT_BOOLEAN:
			case StreamTokenizer.TT_WORD:
				out.write(value);
				out.write(System.getProperty("line.separator"));
				break;
			case '\'':
			case '"':
				if (value == null)
					out.write(ttype);
				else
					out.write((char) ttype + value + (char) ttype);
				out.write(' ');	   // write a separator
				break;
			default:
				if (value == null)
					out.write(ttype);
				else
					out.write(value);
				out.write(' ');	   // write a separator
		}
	} 
}

class BasicDataWriter extends DataWriter {
	public BasicDataWriter(Writer output) {
		super(output);
	}

	public String getFormat() {
		return "basic";
	} 

	protected void nextToken(int ttype, String value) throws IOException {
		switch (ttype) {
			case StreamTokenizer.TT_EOL:
				out.write(System.getProperty("line.separator"));
				break;
			case StreamTokenizer.TT_NUMBER:
				out.write(value);
				out.write(", ");
				break;
			case TT_BOOLEAN:
				out.write('#' + value.toUpperCase() + '#');
				out.write(", ");
				break;
			case StreamTokenizer.TT_WORD:
				if (value == null) {
					out.write("\"\"");
					out.write(", ");
					return;
				} else
					out.write('"' + value + '"');
				out.write(", ");
				break;
			default:
				if (value == null) {
					out.write("\"" + (char) ttype + "\"");
					out.write(", ");
					return;
				} else
					out.write('"' + value + '"');
				out.write(System.getProperty("line.separator"));
		}
	} 
}
