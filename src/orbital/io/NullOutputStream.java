/**
 * @(#)NullOutputStream.java 0.9 1999/03/21 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.OutputStream;

/**
 * This class is a NullOutputStream that behaves like a normal OutputStream
 * but in fact ignores all data written to it and discards it.
 * <p>
 * The bytes written to a NullOutputStream will be <em>lost</em>.
 * Though this class behaves like the <code>NUL</code> device it
 * checks arguments and throws exceptions normally. So it copies
 * interface behaviour of output streams exactly.
 * It is especially useful for testing purposes.
 * 
 * @version 0.9, 1999/03/21
 * @author  Andr&eacute; Platzer
 * @see RandomInputStream
 * @see <a href="{@docRoot}/DesignPatterns/Null.html">Null object</a>
 * @deprecated This class is not applicable in a broad range.
 *  So it is considered generally worthless
 *  and might be removed in future releases.
 */
public class NullOutputStream extends OutputStream {
    public NullOutputStream() {}

    public void write(int b) {}

    public void write(byte[] b, int off, int len) {
	if (b == null)
	    throw new NullPointerException();
	else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0))
	    throw new IndexOutOfBoundsException();
    } 
}
