/*
 * @(#)QuotedPrintableReader.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.FilterReader;
import java.io.Reader;
import java.io.IOException;


/**
 * A QuotedPrintableReader is a FilterReader automatically converting
 * chars read from quoted-printable format source.
 * 
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
class QuotedPrintableReader extends DecodingReader {
	public static void main(String arg[]) throws Exception {
		if (arg.length > 0 && "-?".equals(arg[0])) {
			System.out.println(usage);
			return;
		} 
		Reader rd = new QuotedPrintableReader(new java.io.FileReader(arg.length > 0 ? arg[0] : "t"));
		while (rd.ready())
			System.out.print((char) rd.read());
		rd.close();
	} 
	public static final String usage = "usage: [file]";


	public QuotedPrintableReader(Reader rd) {
		super(rd);
	}

	public String getFormat() {
		return "quoted-printable";
	} 

	public int read() throws IOException, NumberFormatException {
		int b = super.read();
		if (b == '=')
			b = Integer.parseInt(String.valueOf((char) super.read()) + (char) super.read(), 16);
		return b;
	} 
}
