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
	case 'ä':
	    b = '„';
	    break;
	case 'á':
	    b = ' ';
	    break;
	case 'à':
	    b = '…';
	    break;
	case 'â':
	    b = 'ƒ';
	    break;
	case 'é':
	    b = '‚';
	    break;
	case 'è':
	    b = 'Š';
	    break;
	case 'ê':
	    b = 'ˆ';
	    break;
	case 'í':
	    b = '¡';
	    break;
	case 'ì':
	    b = '?';
	    break;
	case 'î':
	    b = 'Œ';
	    break;
	case 'ö':
	    b = '”';
	    break;
	case 'ó':
	    b = '¢';
	    break;
	case 'ò':
	    b = '•';
	    break;
	case 'ô':
	    b = '“';
	    break;
	case 'ü':
	    b = '?';
	    break;
	case 'ú':
	    b = '£';
	    break;
	case 'ù':
	    b = '—';
	    break;
	case 'û':
	    b = '–';
	    break;
	case 'ß':
	    b = 'á';
	    break;
	case 'Ä':
	    b = 'Ž';
	    break;
	case 'Ö':
	    b = '™';
	    break;
	case 'Ü':
	    b = 'š';
	    break;
	}
	super.write(b);
    } 
}
