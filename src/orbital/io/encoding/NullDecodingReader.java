/**
 * @(#)NullDecodingReader.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.Reader;
import java.io.IOException;

/**
 * no encoding.
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 * @see <a href="{@docRoot}/DesignPatterns/NullObject.html">Null Object</a>
 */
class NullDecodingReader extends DecodingReader {
    public NullDecodingReader(Reader rd) {
	super(rd);
    }

    public String getFormat() {
	return "default";
    } 
    public int read() throws IOException {
	return super.read();
    } 
}