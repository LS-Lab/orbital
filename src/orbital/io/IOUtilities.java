/*
 * @(#)IOUtilities.java 0.9 1998/02/19 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.io.File;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * The IOUtilities contain useful static functions for IO-Handling.
 * 
 * @stereotype &laquo;Utilities&raquo;
 * @version 0.9, 2000/01/18
 * @author  Andr&eacute; Platzer
 * @see orbital.util.Utility
 */
public final class IOUtilities {

    /**
     * The size in bytes of a byte-value.
     * i.e. the number of bytes necessary to store it with a DataOutputStream.
     */
    public static final int	   BYTE_SIZE = 1;

    /**
     * The size in bytes of a short-value.
     * i.e. the number of bytes necessary to store it with a DataOutputStream.
     */
    public static final int	   SHORT_SIZE = 2;

    /**
     * The size in bytes of an int-value.
     * i.e. the number of bytes necessary to store it with a DataOutputStream.
     */
    public static final int	   INTEGER_SIZE = 4;

    /**
     * The size in bytes of a long-value.
     * i.e. the number of bytes necessary to store it with a DataOutputStream.
     */
    public static final int	   LONG_SIZE = 8;

    /**
     * The size in bytes of a char-value.
     * i.e. the number of bytes necessary to store it with a DataOutputStream.
     */
    public static final int	   CHARACTER_SIZE = 2;

    /**
     * The size in bytes of a float-value.
     * i.e. the number of bytes necessary to store it with a DataOutputStream.
     */
    public static final int	   FLOAT_SIZE = INTEGER_SIZE;

    /**
     * The size in bytes of a double-value.
     * i.e. the number of bytes necessary to store it with a DataOutputStream.
     */
    public static final int	   DOUBLE_SIZE = LONG_SIZE;

    /**
     * Contains the characters that are typical user whitespaces.
     * Unlike the whitespace characters as such, which are <code>\00</code> to <code>' '</code>.
     * @todo remove once no longer used
     */
    public static final String whitespaces = " \t\r\n";

    /**
     * prevent instantiation - final static class
     */
    private IOUtilities() {}

    /**
     * Read a line from an InputStream in human-readable-form as a String.
     * Reading stops at LF ('\n') or EOF and skips CR ('\r').
     * @return the line read or null if no single character could be read.
     * @see #readLine(java.io.InputStream)
     */
    public static String readLine(InputStream is) throws IOException {
	String input = "";
	while (!Thread.interrupted()) {
	    int ch = is.read();
	    if (ch == -1)
		if ("".equals(input))
		    return null;
		else
		    break;
	    if (ch == '\r')
		continue;
	    if (ch == '\n')
		break;
	    input += (char) ch;
	} 
	return input;
    } 

    /**
     * Read a line from an InputStream in human-readable-form as a String.
     * Reading stops at LF ('\n') or EOF and skips CR ('\r').
     * <p>Prefer using <pre>
     * BufferedReader rd = new BufferedReader(inner_reader);
     * // forget about using the inner reader since rd is buffered!
     * inner_reader = null;
     * String l = rd.readLine();
     * </pre>
     * whenever possible.
     * @return the line read or null if no single character could be read.
     * @see java.io.BufferedReader#readLine()
     */
    public static String readLine(Reader rd) throws IOException {
	String input = "";
	while (!Thread.interrupted()) {
	    int ch = rd.read();
	    if (ch == -1)
		if ("".equals(input))
		    return null;
		else
		    break;
	    if (ch == '\r')
		continue;
	    if (ch == '\n')
		break;
	    input += (char) ch;
	} 
	return input;
    } 



    /**
     * Pipe (copy) a Reader fully to a Writer. Copying is done char-wise.
     */
    public static void copy(Reader rd, Writer wr) throws IOException {
	while (rd.ready()) {
	    int c = rd.read();
	    if (c == -1)
		break;
	    if (wr != null)
		wr.write(c);
	} 
	if (wr != null)
	    wr.flush();
    } 

    /**
     * Pipe (copy) an InputStream fully to an OutputStream.
     * Copying is done byte[]-wise, as available.
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
	while (is.available() > 0) {
	    int	buff = is.read();
	    if (buff == -1)
		break;
	    if (os != null)
		os.write(buff);
	} 
	/* //@xxx the following code somtimes reads data, twice
	   while (is.available() > 0) {
	   int	   len = is.available();
	   byte[] buff = new byte[len];
	   int	   rlen = is.read(buff);
	   if (rlen == -1)
	   break;
	   if (os != null)
	   os.write(buff, 0, rlen);
	   if (rlen != len)
	   break;
	   }*/
	if (os != null)
	    os.flush();
    } 

    /**
     * Fully read from a Reader.
     */
    public static String readFully(Reader rd) throws IOException {
	StringBuffer sb = new StringBuffer();
	while (rd.ready()) {
	    int c = rd.read();
	    if (c == -1)
		break;
	    sb.append((char) c);
	} 
	return sb.toString();
    } 



    /**
     * Print the result of a Process to System.err and System.out.
     */
    public static void printResult(Process proc) throws IllegalThreadStateException, IOException {
	if (proc.exitValue() != 0)
	    System.err.println("ERROR " + proc.exitValue() + " executing Process.");
	copy(proc.getErrorStream(), System.err);
	copy(proc.getInputStream(), System.out);
    } 

    /**
     * Print the ResultSet of an SQL-Statement to System.out.
     */
    public static void printResult(ResultSet rs) throws SQLException {
	ResultSetMetaData rsmd = rs.getMetaData();
	int				  cols = rsmd.getColumnCount();
	for (int i = 1; i <= cols; i++)
	    System.out.print(rsmd.getColumnName(i) + "\t| ");
	System.out.println();
	while (rs.next()) {
	    for (int i = 1; i <= cols; i++)
		System.out.print(rs.getObject(i) + "\t| ");
	    System.out.println();
	} 
    } 

    /**
     * Displays all columns and rows in the given result set as a HTML Table.
     */
    public static void writeResultView(ResultSet rs, Writer wr) throws SQLException, IOException {
	ResultSetMetaData rsmd = rs.getMetaData();
	int				  numCols = rsmd.getColumnCount();
	String			  nl = System.getProperty("line.separator");

	wr.write("<TABLE border=1><TR>");

	// Display column headings
	for (int i = 1; i <= numCols; i++) {
	    wr.write("<TH>" + rsmd.getColumnLabel(i) + "</TH>");
	} 
	wr.write("</TR>" + nl);

	// Display data, fetching until end of the result set
	while (rs.next()) {
	    wr.write("<TR>");

	    // Loop through each column, getting the column data and displaying
	    for (int i = 1; i <= numCols; i++) {
		wr.write("<TD>" + rs.getString(i) + "</TD>");
	    } 
	    wr.write("</TR>" + nl);
	} 

	wr.write("</TABLE>" + nl);
    } 


    // data type conversion
	
    /**
     * Convert a byte unsigned to an unsigned int value.
     * Will convert -128..-1 -> 128..255 and 0..127 -> 0..127.
     * @param val the byte value ranging from -128 to 127.
     * @return an int value equal to unsigned val and ranging from 0 to 255.
     * @see java.io.DataInput#readUnsignedByte()
     * @internal see MathUtilities#format(byte[])
     */
    public static final int byteToUnsigned(byte val) {
	// -128..-1 -> 128..255, 0..127 -> 0..127
	//return val < 0 ? ((int) Byte.MAX_VALUE - (int) Byte.MIN_VALUE) + (int) val : (int) val;
	return val&0xFF;
    } 

    /**
     * Convert a long-value to a byte[].
     */
    public static byte[] longToByteArray(long val) {
	try {
	    ByteArrayOutputStream res;
	    DataOutputStream	  os = new DataOutputStream(res = new ByteArrayOutputStream());
	    os.writeLong(val);
	    os.flush();
	    return res.toByteArray();
	} catch (IOException x) {
	    return null;
	} 
    } 

    /**
     * Convert a byte[] to a long-value.
     */
    public static long byteArrayToLong(byte[] val) {
	try {
	    return new DataInputStream(new ByteArrayInputStream(val)).readLong();
	} catch (IOException x) {
	    return Long.MIN_VALUE;
	} 
    } 

    /**
     * Convert an int-value to a byte[].
     */
    public static byte[] intToByteArray(int val) {
	try {
	    ByteArrayOutputStream res;
	    DataOutputStream	  os = new DataOutputStream(res = new ByteArrayOutputStream());
	    os.writeInt(val);
	    os.flush();
	    return res.toByteArray();
	} catch (IOException x) {
	    return null;
	} 
    } 

    /**
     * Convert a byte[] to a int-value.
     */
    public static int byteArrayToInt(byte[] val) {
	try {
	    return new DataInputStream(new ByteArrayInputStream(val)).readInt();
	} catch (IOException x) {
	    return Integer.MIN_VALUE;
	} 
    } 

    /**
     * Get the extension of a file name, if any.
     * @return extension of a file name (without '.'), or null.
     */
    public static String getExtension(File file) {
	String name = file.getName();
	int	   ext = name.lastIndexOf('.');
	if (ext < 0)
	    return null;
	else
	    return name.substring(ext + 1);
    } 

    /**
     * Change the extension of a file name to a new one.
     * @param extension the new extension (without '.').
     * @return file with new extension, replacing the last old extension, if any.
     */
    public static File changeExtension(File file, String extension) {
	String name = file.getName();
	int	   ext = name.lastIndexOf('.');
	if (ext < 0)
	    return new File(file.getParentFile(), name + "." + extension);
	else
	    return new File(file.getParentFile(), name.substring(0, ext) + "." + extension);
    } 
}
