/*
 * @(#)QuotedPrintableWriter.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.FilterWriter;
import java.io.Writer;
import java.io.IOException;


/**
 * A QuotedPrintableWriter is a FilterWriter automatically converting
 * written chars into quoted-printable format.
 * 
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
class QuotedPrintableWriter extends EncodingWriter {
	public static void main(String arg[]) throws Exception {
		if (arg.length > 0 && "-?".equals(arg[0])) {
			System.out.println(usage);
			return;
		} 
		java.io.Reader rd = new java.io.FileReader(arg.length > 0 ? arg[0] : "con");
		EncodingWriter encoding = new QuotedPrintableWriter(new java.io.FileWriter(arg.length > 1 ? arg[1] : "t"));
		while (rd.ready()) {
			int c = rd.read();
			if (c == -1)
				break;
			encoding.write((char) c);
		} 
		encoding.close();
	} 
	public static final String usage = "usage: [inputfile] [outputfile]";


	public QuotedPrintableWriter(Writer wr) {
		super(wr);
	}

	public String getFormat() {
		return "quoted-printable";
	} 

	public void write(int b) throws IOException {
		if (b > 127 || b == '=' || b < ' ') {
			super.write('=');
			String paraphrase = Integer.toString(b, 16).toUpperCase();
			if (paraphrase.length() < 2)
				paraphrase = "0" + paraphrase;
			super.write(paraphrase.toCharArray());
		} else
			super.write(b);
	} 
}
