/*
 * @(#)RandomInputStream.java 0.9 1999/01/11 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.InputStream;
import java.io.IOException;
import java.util.Random;


/**
 * A RandomInputStream is an InputStream from which random data
 * can be read.
 * <p>
 * It is especially useful for testing purposes where any data, no matter what, is needed.
 * This class behaves like the <code>random</code> device.
 * 
 * @version 0.9, 14/11/98
 * @author  Andr&eacute; Platzer
 * @see NullOutputStream
 */
public class RandomInputStream extends InputStream {

    /**
     * The Random source used while reading.
     * 
     * @serial
     */
    protected Random random;
    public RandomInputStream(Random random) {
	this.random = random;
    }

    /**
     * Changes the random generator used while reading data.
     */
    public void setRandom(Random random) {
	this.random = random;
    } 

    /**
     * Returns the number of bytes that can be read without blocking.
     * Of course, for RandomInputStreams that rather infinitive.
     */
    public int available() {
	return Integer.MAX_VALUE;
    } 

    public int read() {
	return random.nextInt(Byte.MAX_VALUE);
    } 
    public int read(byte[] b) {
	random.nextBytes(b);
	return b.length;
    } 
    public int read(byte[] b, int off, int len) {
	byte[] c = new byte[len];
	random.nextBytes(c);
	System.arraycopy(c, 0, b, off, len);
	return len;
    } 
}
