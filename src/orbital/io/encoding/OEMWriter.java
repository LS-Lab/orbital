/*
 * @(#)OEMWriter.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.Writer;
import java.io.IOException;

/**
 * OEM encoding
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
class OEMWriter extends EncodingWriter {
    public OEMWriter(Writer wr) {
	super(wr);
    }

    public String getFormat() {
	return "oem";
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
	    b = '?';
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
	    b = '?';
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
