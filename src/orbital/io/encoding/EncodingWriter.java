/*
 * @(#)EncodingWriter.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.FilterWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * A EncodingWriter is a FilterWriter that automatically converts
 * chars from the base encoding into another format.
 * 
 * Sub classes classes providing functionality should at least override
 * <ul>
 * <li> write(int)</li>
 * <li> For efficiency write(char[],int,int) is recommended as well.</li>
 * </ul>
 * 
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 * @see java.nio.charset.CharsetEncoder
 * @see java.io.OutputStreamWriter
 */
public abstract class EncodingWriter extends FilterWriter {
    public static void main(String arg[]) throws Exception {
	if (orbital.signe.isHelpRequest(arg)) {
	    System.out.println(usage);
	    return;
	} 
	java.io.Reader rd = new java.io.FileReader(arg.length > 1 ? arg[1] : "con");
	EncodingWriter wr = getInstance(new java.io.FileWriter(arg.length > 2 ? arg[2] : "t"), arg.length > 0 ? arg[0] : "quoted-printable");
	if (wr == null) {
	    System.out.println("no encoding is necessary");
	    return;
	} 
	while (rd.ready()) {
	    int c = rd.read();
	    if (c == -1)
		break;
	    wr.write((char) c);
	} 
	wr.close();
    } 
    public static final String usage = "usage: " + EncodingWriter.class + " [encoding] [inputfile] [outputfile]" + System.getProperty("line.separator") + "Where encoding is one of: default, none, quoted-printable, base64, 7bit, 8bit, oem, ansi";

    protected EncodingWriter(Writer wr) {
	super(wr);
    }

    /**
     * Returns the corresponding encoding format.
     * @return a String that specifies the encoding format of this writer.
     */
    public abstract String getFormat();

    // public abstract void write(int b) throws IOException;

    public void write(String s, int off, int len) throws IOException {
	write(s.toCharArray(), off, len);
    } 
    public void write(char[] b, int off, int len) throws IOException {
	for (int i = 0; i < len; i++)
	    write(b[off + i]);
    } 

    /**
     * Generates an EncodingWriter object for the specified type of encoding.
     * <p>
     * Call
     * <kbd class="command">java orbital.io.encoding.EncodingWriter --help</kbd>
     * to get an up to date list of the supported encodings on a system.
     * </p>
     * @param wr which writer to encode.
     * @param encoding which encoding writer to instantiate with <code>wr</code>.
     * @return an instance of EncodingWriter fitting the encoding
     * or <code>null</code> if no encoding is necessary. (for efficiency reasons).
     * @throws UnsupportedEncodingException if no writer is found for the given encoding.
     * @throws IOException if the instantiation throws an IOException.
     * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream, java.lang.String)
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static EncodingWriter getInstance(Writer wr, String encoding) throws UnsupportedEncodingException, IOException {
	if (encoding == null)
	    return null;
	if ("".equals(encoding) || "default".equalsIgnoreCase(encoding) || "none".equalsIgnoreCase(encoding))
	    return new NullEncodingWriter(wr);
	if ("quoted-printable".equalsIgnoreCase(encoding))
	    return new QuotedPrintableWriter(wr);
	if ("base64".equalsIgnoreCase(encoding))
	    return new Base64Writer(wr);
	if ("7bit".equalsIgnoreCase(encoding))
	    return null;
	if ("8bit".equalsIgnoreCase(encoding))
	    return null;
	if ("oem".equalsIgnoreCase(encoding))
	    return new OEMWriter(wr);
	if ("ansi".equalsIgnoreCase(encoding))
	    return new AnsiWriter(wr);
	throw new UnsupportedEncodingException("no writer found for encoding '" + encoding + "'");
    } 
}
