/*
 * @(#)Buffer.java 0.9 1998/11/13 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

/**
 * This class encapsulates a Buffer containing bytes.
 * Those bytes in the Buffer are kept as a byte[].
 * 
 * @version 0.9, 13/11/98
 * @author  Andr&eacute; Platzer
 * @see java.nio.Buffer
 */
public class Buffer {
    /**
     * The index one greater than the index of the last valid byte in
     * the buffer. It is always true that <code>count<=buf.length</code>.
     * @serial
     */
    protected int  count;

    /**
     * The buffer where data is stored.
     * @serial
     */
    protected byte buf[];

    /**
     * The current position in the buffer. This is the index of the next
     * character to be read from the <code>buf</code> array.
     * 
     * @see     java.io.BufferedInputStream
     */

    // protected int pos;

    public Buffer() {
	this(0);
    }

    /**
     * Creates a new Buffer with an initial capacity, but empty (of size 0).
     */
    public Buffer(int initialCapacity) {
	buf = new byte[initialCapacity];
	count = 0;
    }

    /**
     * Creates a new Buffer with an initial content specified by a part of a byte-array.
     * @param initialContent the byte-array containing the data.
     * @param off the index where the relevant data start in initialContent
     * @param len the size of the buffer. len is also the number of bytes that are copied from initialContent.
     */
    public Buffer(byte[] initialContent, int off, int len) {
	buf = new byte[len];
	System.arraycopy(initialContent, off, buf, 0, len);
	count = len;
    }
    public Buffer(byte[] initialContent) {
	this(initialContent, 0, initialContent.length);
    }

    /**
     * Get a copy of the buffer.
     * @return a copy of the whole buffer.
     */
    public byte[] getBytes() {
	return getBytes(count);
    } 

    /**
     * Get a part of the buffer, beginning at front (index 0).
     * @return a copy of the part with length len starting at offset 0.
     */
    public byte[] getBytes(int len) {
	return getBytes(0, len);
    } 

    /**
     * Get a part of the buffer.
     * @return a copy of the part with length len starting at off is returned,
     * <code>null</code> for length <code>0</code>.
     */
    public byte[] getBytes(int off, int len) {
	byte[] buffer = new byte[len];
	getBytes(off, buffer, 0, len);
	return buffer;
    } 

    /**
     * Get a part of the buffer into a specified byte-array.
     * @param dst the byte-array to be filled with the part desired. Its dimension must be at least len.
     * @param dstOff the destination offset in dst.
     * @param srcOff the source offset in the Buffer array.
     * @param len the number of bytes to be copied.
     * @throws ArrayIndexOutOfBoundsException if the part to be get exceeds the current dimension.
     * @throws IllegalArgumentException if the offset or length are negative, or the length of dst is less than len.
     * @see java.lang.System#arraycopy(Object,int,Object,int,int)
     */
    public void getBytes(int srcOff, byte[] dst, int dstOff, int len) {
	if (len < 0)
	    throw new IllegalArgumentException("length negative");
	if (len == 0)
	    return;
	if (dst.length < len)
	    throw new IllegalArgumentException("byte-array too small");
	if (srcOff + len > count)
	    throw new ArrayIndexOutOfBoundsException("get exceeds size");

	System.arraycopy(buf, srcOff, dst, dstOff, len);
    } 

    /**
     * Set a part of the buffer.
     * Sets the part with length len starting at off.
     * @param part the bytes to be set as new part of the Buffer
     * @param srcOff the source offset in the part array.
     * @param dstOff the destination offset in the Buffer.
     * @param len the number of bytes to be copied.
     * @throws ArrayIndexOutOfBoundsException if the part to be set exceeds the current dimension.
     * @throws IllegalArgumentException if the offset or length are negative.
     * @see java.lang.System#arraycopy(byte[],int,byte[],int,int)
     */
    public synchronized void setBytes(byte[] part, int srcOff, int dstOff, int len) {
	if (len < 0)
	    throw new IllegalArgumentException("length negative");
	if (len == 0)
	    return;
	if (srcOff + len > count)
	    throw new ArrayIndexOutOfBoundsException("set exceeds size");

	System.arraycopy(part, srcOff, buf, dstOff, len);
    } 
    public void setBytes(byte[] part, int dstOff) {
	setBytes(part, 0, dstOff, part.length);
    } 

    /**
     * Returns the number of bytes in this buffer.
     * 
     * @return  the number of bytes in this buffer.
     */
    public final int size() {
	return count;
    } 

    /**
     * Sets the size of this vector. If the new size is greater than the
     * current size, new <code>null</code> items are added to the end of
     * the vector. If the new size is less than the current size, all
     * components at index <code>newSize</code> and greater are discarded.
     * 
     * @param   newSize   the new size of this vector.
     */
    public final synchronized void setSize(int newSize) {
	if (newSize < 0)
	    throw new IllegalArgumentException("negative size");
	if (newSize > count) {
	    ensureCapacity(newSize);
	} 
	count = newSize;
    } 

    /**
     * Trims the capacity of this buffer to be the buffer's current
     * size. An application can use this operation to minimize the
     * storage of a buffer.
     */
    public final synchronized void trimToSize() {
	int oldCapacity = buf.length;
	if (count < oldCapacity) {
	    byte[] oldData = buf;
	    buf = new byte[count];
	    System.arraycopy(oldData, 0, buf, 0, count);
	} 
    } 

    /**
     * Increases the capacity of this Buffer, if necessary, to ensure
     * that it can hold at least the number of bytes specified by
     * the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     * @throws IllegalArgumentException if desired minimum capacity is negative.
     */
    public synchronized void ensureCapacity(int minCapacity) {
	if (minCapacity < 0)
	    throw new IllegalArgumentException("negative size");
	if (buf.length >= minCapacity)
	    return;
	byte[] oldData = buf;
	buf = new byte[minCapacity];
	System.arraycopy(oldData, 0, buf, 0, count);
    } 

    /**
     * Append a byte[] to the end of this Buffer,
     */
    public void append(byte[] b) {
	append(b, 0, b.length);
    } 

    /**
     * Append a part of a byte[] (starting at off) to the end of this Buffer,
     * increasing its size by the length of b. The capacity of this vector is
     * increased if its size becomes greater than its capacity.
     */
    public synchronized void append(byte[] b, int off, int len) {
	if (len < 0)
	    throw new IllegalArgumentException("length negative");
	ensureCapacity(count + len);
	System.arraycopy(b, off, buf, count, len);
	count += len;
    } 

    /**
     * Append a byte value len times to the end of this Buffer,
     * increasing its size by len. The capacity of this vector is
     * increased if its size becomes greater than its capacity.
     */
    public synchronized void append(int val, int len) {
	if (len < 0)
	    throw new IllegalArgumentException("length negative");
	ensureCapacity(count + len);
	for (int i = 0; i < len; i++)
	    buf[count + i] = (byte) val;
	count += len;
    } 

    /**
     * Remove len beginning at front (index 0).
     */
    public void remove(int len) {
	remove(0, len);
    } 

    /**
     * Remove len bytes starting at off from this Buffer,
     * decreasing its size by the len.
     */
    public synchronized void remove(int off, int len) {
	if (len < 0)
	    throw new IllegalArgumentException("length negative");

	// buffer empty?
	if (len == 0) {
	    buf = new byte[0];
	    return;
	} 
	if (off + len > count)
	    throw new ArrayIndexOutOfBoundsException("Remove exceeds end of buffer.");

	if (off + len < count) {	// remove middle of buffer
	    System.arraycopy(buf, off + len, buf, off, count - off - len);
	} 
	count -= len;
    } 

    /**
     * Gets the whole underlying byte-buffer containing the data (size like the whole capacity).
     */
    public final byte[] getBuffer() {
	return buf;
    } 

    /**
     * Sets the underlying byte-buffer containing the data (without setting the byte-count).
     * So you should call setSize() separately.
     * 
     * @see #setSize
     */
    public synchronized final void setBuffer(byte[] buffer) {
	ensureCapacity(buffer.length);
	System.arraycopy(buffer, 0, buf, 0, buffer.length);
    } 

}
