/**
 * @(#)ObjectDataOutputStream.java 0.9 2001/07/13 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.io;

import java.io.ObjectOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.io.Externalizable;

/**
 * workaround for converting data output like Streamable to Externalizable.
 *
 * @version 0.9, 2001/07/13
 * @author  Andr&eacute; Platzer
 */
public class ObjectDataOutputStream extends DataOutputStream implements ObjectOutput {
    public ObjectDataOutputStream(OutputStream os) {
	super(os);
    }

    public void writeObject(Object o) throws IOException {
	if (o instanceof Externalizable)
	    ((Externalizable) o).writeExternal(this);
	else
	    throw new UnsupportedOperationException();
    } 
}
