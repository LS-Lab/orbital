/**
 * @(#)OccurrenceAnalyzer.java 0.9 1999/11/05 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.text;

import orbital.io.DataAnalyzer;

import orbital.io.IOUtilities;

/**
 * This class is a OccurrenceAnalyzer that keeps track of the frequency with that
 * each character occurs.
 * 
 * @version 0.9, 1999/11/05
 * @author  Andr&eacute; Platzer
 */
public
class OccurrenceAnalyzer implements DataAnalyzer {
	public OccurrenceAnalyzer() {
		init(null);
	}

	public void init(Object para) {
		size = 0;
		count = new long[Byte.MAX_VALUE - Byte.MIN_VALUE + 1];
		for (int i = 0; i < count.length; i++)
			count[i] = 0;
	} 

	public Object getAnalysis() {
		return getOccurrences();
	} 


	/**
	 * Get an array of doubles containing the relative frequencies of occurrence
	 * of all chars at the corresponding index.
	 * @post RES[c] = (number of occurrences of c) / length = the relative frequency with that the character c occurred.
	 */
	public double[] getOccurrences() {
		double[] r = new double[count.length];
		for (int i = 0; i < count.length; i++)
			r[i] = (double) count[i] / size;
		return r;
	} 

	private long size = 0;
	private long count[];

	/**
	 * Basic Method, analyzes one byte.
	 */
	public void analyze(int b) {
		count[b]++;
		size++;
	} 

	/**
	 * iteratedly call analyze(int)
	 */
	public void analyze(byte[] b, int off, int len) {
		for (int i = 0; i < len; i++)
			analyze(IOUtilities.byteToUnsigned(b[off + i]));
	} 

}
