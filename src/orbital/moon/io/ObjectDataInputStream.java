/**
 * @(#)ObjectDataInputStream.java 0.9 2001/07/13 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.io;

import java.io.ObjectInput;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * workaround for converting data input like Streamable to Externalizable.
 *
 * @version 0.9, 2001/07/13
 * @author  Andr&eacute; Platzer
 */
public class ObjectDataInputStream extends DataInputStream implements ObjectInput {
    public ObjectDataInputStream(InputStream is) {
	super(is);
    }
	
    public Object readObject() throws ClassNotFoundException, IOException {
	throw new UnsupportedOperationException();
    }
}

