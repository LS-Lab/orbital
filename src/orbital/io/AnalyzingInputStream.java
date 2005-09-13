/**
 * @(#)AnalyzingInputStream.java 0.9 1999/11/05 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Applies a specified DataAnalyzer
 * to all bytes read from the underlying InputStream.
 * The analyzer will only be notified when no Exception occured while reading.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class AnalyzingInputStream extends FilterInputStream {

    /**
     * The DataAnalyzer used while reading.
     * @serial
     */
    protected DataAnalyzer analyzer;

    public AnalyzingInputStream(InputStream is, DataAnalyzer analyzer) {
        super(is);
        this.analyzer = analyzer;
    }

    /**
     * Changes the DataAnalyzer used while reading data.
     */
    public void setAnalyzer(DataAnalyzer analyzer) {
        this.analyzer = analyzer;
    } 
    public DataAnalyzer getAnalyzer() {
        return analyzer;
    } 

    public int read() throws IOException {
        int b = super.read();
        if (b == -1)
            return -1;
        if (analyzer != null)
            analyzer.analyze(b);
        return b;
    } 
    public int read(byte[] b) throws IOException {
        int r = super.read(b);
        if (r == -1)
            return -1;
        if (analyzer != null)
            analyzer.analyze(b, 0, r);
        return r;
    } 
    public int read(byte[] b, int off, int len) throws IOException {
        int r = super.read(b, off, len);
        if (r == -1)
            return -1;
        if (analyzer != null)
            analyzer.analyze(b, off, r);
        return r;
    } 
}
