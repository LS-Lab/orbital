/**
 * @(#)DecodingReader.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.FilterReader;
import java.io.Reader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * A DecodingReader is a FilterReader that automatically converts
 * chars from another format into the base encoding.
 * 
 * Sub classes providing functionality should at least override
 * <ul>
 * <li> read()</li>
 * <li> For efficiency read(char[],int,int) is recommended as well.</li>
 * <li> redefinition of ready() is required when the class buffers input data.</li>
 * </ul>
 * 
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 * @see java.nio.charset.CharsetDecoder
 * @see java.io.InputStreamReader
 */
public abstract class DecodingReader extends FilterReader {
    public static void main(String arg[]) throws Exception {
	if (orbital.signe.isHelpRequest(arg)) {
	    System.out.println(usage);
	    return;
	} 
	Reader rd = getInstance(new java.io.FileReader(arg.length > 1 ? arg[1] : "t"), arg.length > 0 ? arg[0] : "quoted-printable");
	if (rd == null) {
	    System.out.println("no decoding is necessary");
	    return;
	} 
	while (rd.ready())
	    System.out.print((char) rd.read());
	rd.close();
    } 
    public static final String usage = "usage: " + DecodingReader.class + " [encoding] [file]" + System.getProperty("line.separator") + "Where encoding is one of: default, none, quoted-printable, base64, 7bit, 8bit, oem, ansi";


    /**
     * Generates a DecodingReader object for the specified type of encoding.
     * <p>
     * Call <kbd>java orbital.io.encoding.DecodingReader --help</kbd> to get an up to date list
     * of the supported encodings on a system.
     * </p>
     * @param rd from which reader to encode.
     * @param encoding which decoding reader to instantiate with <code>rd</code>.
     * @return an instance of DecodingReader fitting the encoding
     *  or <code>null</code> if no decoding is necessary. (for efficiency reasons).
     * @throws UnsupportedEncodingException if no reader is found for the given encoding.
     * @throws IOException if the instantiation throws an IOException.
     * @see java.io.InputStreamReader#InputStreamReader(java.io.InputStream, java.lang.String)
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static DecodingReader getInstance(Reader rd, String encoding) throws UnsupportedEncodingException, IOException {
	if (encoding == null)
	    return null;
	if ("".equals(encoding) || "default".equalsIgnoreCase(encoding) || "none".equalsIgnoreCase(encoding))
	    return new NullDecodingReader(rd);
	if ("quoted-printable".equalsIgnoreCase(encoding))
	    return new QuotedPrintableReader(rd);
	if ("base64".equalsIgnoreCase(encoding))
	    return new Base64Reader(rd);
	if ("7bit".equalsIgnoreCase(encoding))
	    return null;
	if ("8bit".equalsIgnoreCase(encoding))
	    return null;
	if ("oem".equalsIgnoreCase(encoding))
	    return new OEMReader(rd);
	if ("ansi".equalsIgnoreCase(encoding))
	    return new AnsiReader(rd);
	throw new UnsupportedEncodingException("no reader found for encoding '" + encoding + "'");
    } 

    protected DecodingReader(Reader rd) {
	super(rd);
    }

    /**
     * Returns the corresponding encoding format.
     * @return a String that specifies the format that is decoded by this reader.
     */
    public abstract String getFormat();

    // public abstract int read() throws IOException;

    public int read(char[] b, int off, int len) throws IOException {
	for (int i = 0; i < len; i++) {
	    int c = read();
	    if (c == -1)
		return i - 1;
	    if (c < Character.MIN_VALUE || c > Character.MAX_VALUE)
		throw new orbital.util.SuspiciousError("read suspicious value (" + i + "==" + (char) i + ")");
	    b[off + i] = (char) c;
	} 
	return len;
    } 
}


class NullDecodingReader extends DecodingReader {
    public NullDecodingReader(Reader rd) {
	super(rd);
    }

    public String getFormat() {
	return "default";
    } 
    public int read() throws IOException {
	return super.read();
    } 
}
class OEMReader extends DecodingReader {
    public OEMReader(Reader rd) {
	super(rd);
    }

    public String getFormat() {
	return "oem";
    } 

    public int read() throws IOException, NumberFormatException {
	int b = super.read();
	switch (b) {
	case '„':
	    b = 'ä';
	    break;
	case ' ':
	    b = 'á';
	    break;
	case '…':
	    b = 'à';
	    break;
	case 'ƒ':
	    b = 'â';
	    break;
	case '‚':
	    b = 'é';
	    break;
	case 'Š':
	    b = 'è';
	    break;
	case 'ˆ':
	    b = 'ê';
	    break;
	case '¡':
	    b = 'í';
	    break;

	    // case '?': b = 'ì'; break;
	case 'Œ':
	    b = 'î';
	    break;
	case '”':
	    b = 'ö';
	    break;
	case '¢':
	    b = 'ó';
	    break;
	case '•':
	    b = 'ò';
	    break;
	case '“':
	    b = 'ô';
	    break;
	case '?':
	    b = 'ü';
	    break;
	case '£':
	    b = 'ú';
	    break;
	case '—':
	    b = 'ù';
	    break;
	case '–':
	    b = 'û';
	    break;
	case 'á':
	    b = 'ß';
	    break;
	case 'Ž':
	    b = 'Ä';
	    break;
	case '™':
	    b = 'Ö';
	    break;
	case 'š':
	    b = 'Ü';
	    break;
	}
	return b;
    } 
}

class AnsiReader extends DecodingReader {
    public AnsiReader(Reader rd) {
	super(rd);
    }

    public String getFormat() {
	return "ansi";
    } 

    public int read() throws IOException, NumberFormatException {
	int b = super.read();
	switch (b) {
	case 'ä':
	    b = 'ä';
	    break;
	case 'á':
	    b = 'á';
	    break;
	case 'à':
	    b = 'à';
	    break;
	case 'â':
	    b = 'â';
	    break;
	case 'é':
	    b = 'é';
	    break;
	case 'è':
	    b = 'è';
	    break;
	case 'ê':
	    b = 'ê';
	    break;
	case 'í':
	    b = 'í';
	    break;
	case 'ì':
	    b = 'ì';
	    break;
	case 'î':
	    b = 'î';
	    break;
	case 'ö':
	    b = 'ö';
	    break;
	case 'ó':
	    b = 'ó';
	    break;
	case 'ò':
	    b = 'ò';
	    break;
	case 'ô':
	    b = 'ô';
	    break;
	case 'ü':
	    b = 'ü';
	    break;
	case 'ú':
	    b = 'ú';
	    break;
	case 'ù':
	    b = 'ù';
	    break;
	case 'û':
	    b = 'û';
	    break;
	case 'ß':
	    b = 'ß';
	    break;
	case 'Ä':
	    b = 'Ä';
	    break;
	case 'Á':
	    b = 'Á';
	    break;
	case 'À':
	    b = 'À';
	    break;
	case 'Â':
	    b = 'Â';
	    break;
	case 'É':
	    b = 'É';
	    break;
	case 'È':
	    b = 'È';
	    break;
	case 'Ê':
	    b = 'Ê';
	    break;
	case 'Í':
	    b = 'Í';
	    break;
	case 'Ì':
	    b = 'Ì';
	    break;
	case 'Î':
	    b = 'Î';
	    break;
	case 'Ö':
	    b = 'Ö';
	    break;
	case 'Ó':
	    b = 'Ó';
	    break;
	case 'Ò':
	    b = 'Ò';
	    break;
	case 'Ô':
	    b = 'Ô';
	    break;
	case 'Ü':
	    b = 'Ü';
	    break;
	case 'Ú':
	    b = 'Ú';
	    break;
	case 'Ù':
	    b = 'Ù';
	    break;
	case 'Û':
	    b = 'Û';
	    break;
	}
	return b;
    } 
}
