/*
 * @(#)AnsiWriter.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.Writer;
import java.io.IOException;

/**
 * Ansi encoding
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
class AnsiWriter extends EncodingWriter {
    public AnsiWriter(Writer wr) {
	super(wr);
    }

    public String getFormat() {
	return "ansi";
    } 

    public void write(int b) throws IOException {
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
	super.write(b);
    } 
}
