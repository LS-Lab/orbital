/*
 * @(#)Base64Writer.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.FilterWriter;
import java.io.Writer;
import java.io.IOException;

import orbital.util.SuspiciousError;

/**
 * A Base64Writer is a FilterWriter automatically converting
 * chars from the base Base64 into another format.
 * 
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
class Base64Writer extends EncodingWriter {
	public static void main(String arg[]) throws Exception {
		if (arg.length > 0 && "-?".equals(arg[0])) {
			System.out.println(usage);
			return;
		} 
		java.io.Reader rd = new java.io.FileReader(arg.length > 0 ? arg[0] : "con");
		EncodingWriter encoding = new Base64Writer(new java.io.FileWriter(arg.length > 1 ? arg[1] : "t"));
		while (rd.ready()) {
			int c = rd.read();
			if (c == -1)
				break;
			encoding.write((char) c);
		} 
		encoding.close();
	} 
	public static final String usage = "usage: [inputfile] [outputfile]";

	public Base64Writer(Writer wr) {
		super(wr);
	}

	public String getFormat() {
		return "base64";
	} 

	private byte  buff[] = new byte[3];
	private short ibuff = 0;
	public synchronized void write(int b) throws IOException {
		buff[ibuff++] = (byte) b;
		if (ibuff >= 3)
			dowrite();
	} 

	public synchronized void flush() throws IOException {
		dowrite();
		super.flush();
	} 

	private synchronized void dowrite() throws IOException {
		if (ibuff <= 0)
			return;
		int val = 0;
		int l;
		for (l = 0; l < ibuff; l++)
			val += buff[l] << (8 * l);
		int sz = (int) ((l * 8 - 1) / 6 + 1);	 // 4
		for (int i = 0; i < sz; i++)
			super.write(map((val >> (6 * i)) & 63));
		for (int i = sz; i < 4; i++)	// fill with '=' to 4 bytes
			super.write('=');
		ibuff = 0;
	} 

	private int map(int c) {
		assert 0 <= c && c <= 63 : "valid base64 range";
		if (c < 26)
			return 'A' + c;
		if (c < 52)
			return 'a' + c - 26;
		if (c < 62)
			return '0' + c - 52;
		if (c == 62)
			return '+';
		if (c == 63)
			return '/';
		throw new SuspiciousError("panic");
	} 
}
