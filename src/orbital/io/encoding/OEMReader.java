/**
 * @(#)OEMReader.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.Reader;
import java.io.IOException;

/**
 * OEM encoding like in DOS QuickBasic.
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 */
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
