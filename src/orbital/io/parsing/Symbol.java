/*
 * @(#)Symbol.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

/**
 * Represents a parsed Symbol.
 * 
 * @version 0.9, 11/05/98
 * @author  Andr&eacute; Platzer
 */
public
class Symbol {

	/**
	 * the type of this symbol.
	 */
	public String type;

	/**
	 * String presenting this symbol.
	 */
	public String symbol;

	/**
	 * additional data associated with this symbol for further use.
	 */
	public Object data;
	public Symbol(String typ, String symb, Object dat) {
		type = typ;
		symbol = symb;
		data = dat;
	}
	public Symbol(String typ, String symb) {
		this(typ, symb, null);
	}
	public Symbol(String symb) {
		this(null, symb);
	}

	public void setSymbol(String symb) {
		symbol = symb;
	} 
	public String getSymbol() {
		return symbol;
	} 
	public void setType(String typ) {
		type = typ;
	} 
	public String getType() {
		return type;
	} 

	public int length() {
		return symbol.length();
	} 

	public boolean equals(Object arg) {
		if (arg instanceof Symbol)
			throw new UnsupportedOperationException("not yet implemented");
		if (arg instanceof String)
			return symbol.equals(arg);
		return false;
	} 
	
	public int hashCode() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public String toString() {
		return "'" + symbol + "'";
	} 
}
