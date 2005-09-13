/**
 * @(#)AnalyzingOutputStream.java 0.9 1999/11/05 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Applies a specified DataAnalyzer
 * to all bytes written to the underlying OutputStream.
 * The analyzer will only be notified when no exception occured while writing.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class AnalyzingOutputStream extends FilterOutputStream {

    /**
     * The DataAnalyzer used while writing.
     * @serial
     */
    protected DataAnalyzer analyzer;

    public AnalyzingOutputStream(OutputStream os, DataAnalyzer analyzer) {
	super(os);
	this.analyzer = analyzer;
    }

    public DataAnalyzer getAnalyzer() {
	return analyzer;
    } 

    /**
     * Changes the DataAnalyzer used while reading data.
     */
    public void setAnalyzer(DataAnalyzer analyzer) {
	this.analyzer = analyzer;
    } 

    public void write(int b) throws IOException {
	super.write(b);
	analyzer.analyze(b);
    } 
    public void write(byte[] b) throws IOException {
	super.write(b);
	analyzer.analyze(b, 0, b.length);
    } 
    public void write(byte[] b, int off, int len) throws IOException {
	super.write(b, off, len);
	analyzer.analyze(b, off, len);
    } 
}
