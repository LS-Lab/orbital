/**
 * @(#)Base64Reader.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.FilterReader;
import java.io.Reader;
import java.io.IOException;

import orbital.util.SuspiciousError;

/**
 * A Base64Reader is a FilterReader automatically converting
 * chars read from base64 format.
 * 
 * QP (base64-encoded) contains uppercase, lowercase, numbers, '+', '/' and '='.
 * 
 * Take the encoded stuff in groups of 4 characters and turn each character into a code 0 to 63 thus:
 * <pre>
 * A means of labelling the content of mail messages.
 * A-Z map to 0 to 25
 * a-z map to 26 to 51
 * 0-9 map to 52 to 61
 * + maps to 62
 * / maps to 63
 * 
 * Express the four numbers thus found (all 0 to 63) in binary:
 * 
 * 00aaaaaa 00bbbbbb 00cccccc 00dddddd
 * 
 * This then maps to _three_ real bytes formed thus:
 * 
 * aaaaaabb bbbbcccc ccdddddd
 * 
 * Equality signs (one or two) are used at the end of the encoded block to indicate that the text was not an integer multiple of three bytes long.
 * </pre>
 * 
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
class Base64Reader extends DecodingReader {
	public static void main(String arg[]) throws Exception {
		if (arg.length > 0 && "-?".equals(arg[0])) {
			System.out.println(usage);
			return;
		} 
		Reader rd = new Base64Reader(new java.io.FileReader(arg.length > 0 ? arg[0] : "t"));
		while (rd.ready())
			System.out.print((char) rd.read());
		rd.close();
	} 
	public static final String usage = "usage: [file]";

	public Base64Reader(Reader rd) {
		super(rd);
	}

	public String getFormat() {
		return "base64";
	} 

	private int   buff[] = new int[3];
	private short ibuff = 3;
	public int read() throws IOException {
		if (ibuff >= 3)
			doread();
		return buff[ibuff++];
	} 

	public boolean ready() throws IOException {
		if (ibuff < 3 && buff[ibuff] != -1)
			return true;
		return super.ready();
	} 

	private synchronized void doread() throws IOException {
		int val = 0;
		int l;
		for (l = 0; l < 4; l++) {
			int c = super.read();
			if (c == -1)
				break;
			if (c == '\r' || c == '\n')
				continue;
			if (c == '=') {	   // break on boundary sign ====
				for (int i = l; i < 4; i++)
					if ((c = super.read()) != '=')
						break;
				if (c == '=')
					break;
			} 
			val += map(c) << (6 * l);
		} 
		int sz = (int) ((l * 6 - 1) / 8 + 1);	 // 3
		for (int i = 0; i < 3; i++)
			buff[i] = (i <= sz) ? ((val >> (8 * i)) & 0xFF) : -1;	 // EOF reached
		ibuff = 0;
	} 

	private int map(int c) {
		if ('A' <= c && c <= 'Z')
			return c - 'A';
		if ('a' <= c && c <= 'z')
			return c - 'a' + 26;
		if ('0' <= c && c <= '9')
			return c - '0' + 52;
		if (c == '+')
			return 62;
		if (c == '/')
			return 63;
		if (c == '=')
			return -1;
		throw new SuspiciousError("wrong character '" + (char) c + "' for base64");
	} 
}
