/**
 * @(#)DataAnalyzer.java 0.9 1999/11/05 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import orbital.util.Callback;

/**
 * DataAnalyzer encapsulates a callback for byte stream analysis.
 * It works much like an <tt>orbital.logic.functor.Predicate</tt>.
 * 
 * @version 0.9, 1999/11/05
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.Predicate
 * @see orbital.util.Callback
 */
public
interface DataAnalyzer extends Callback {

	/**
	 * (Re-)Initializes the DataAnalyzer with a generic Object as an argument.
	 * This method should reset the analysis.
	 * Without a call to init(), the DataAnalyzer is initialized in default state.
	 */
	void init(Object para);

	/**
	 * Returns the current result of the analysis as a generic object.
	 */
	Object getAnalysis();

	/**
	 * Called with a single byte to analyze.
	 */
	void analyze(int b);

	/**
	 * Called with a byte-array to analyze within the given bounds.
	 */
	void analyze(byte[] b, int off, int len);
}