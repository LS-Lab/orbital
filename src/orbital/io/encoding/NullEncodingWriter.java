/*
 * @(#)NullEncodingWriter.java 0.9 1998/02/02 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.Writer;
import java.io.IOException;

/**
 * no encoding
 * @version 0.9, 02/02/98
 * @author  Andr&eacute; Platzer
 * @see <a href="{@docRoot}/Patterns/Design/NullObject.html">Null Object</a>
 */
class NullEncodingWriter extends EncodingWriter {
    public NullEncodingWriter(Writer wr) {
	super(wr);
    }

    public String getFormat() {
	return "default";
    } 

    public void write(int b) throws IOException {
	super.write(b);
    } 
}
