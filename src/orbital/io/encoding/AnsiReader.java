/**
 * @(#)AnsiReader.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.Reader;
import java.io.IOException;

/**
 * Ansi encoding.
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
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
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	case '�':
	    b = '�';
	    break;
	}
	return b;
    } 
}