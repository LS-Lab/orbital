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