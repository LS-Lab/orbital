/*
 * @(#)Token.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.Reader;
import java.io.IOException;

import orbital.util.Utility;

/**
 * This class represents a Token. Tokens are either definite (fixed) or
 * variable (as with a RegularExpression).
 * It contains a Symbolstring which will be represented by the specified
 * Tokenstring. Every Token has a type.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see <a href="package-summary.html#Token">Token explanation</a>
 */
public class Token {

    /**
     * the type identifier
     */
    public String type;

    /**
     * specifying which input to recognize lexically.
     * Set regular expressions in brackets <code>([0-9]+)</code>.
     */
    public String symbol;

    /**
     * which token should be used to represent the lexical symbol recognized.
     */
    public String token;
    public Token(String type, String symbol, String token) {
        if (!isTypeSpec(type))
            type = "<" + type + ">";
        this.type = type;
        this.symbol = symbol;
        this.token = token;
    }
    public Token(String type, String symbol) {
        this(type, symbol, symbol);
    }
    public Token(String symbol) {
        this(null, symbol, symbol);
    }

    /**
     * checks whether this Token is of a type contained in types.
     * String(-part) contained
     * like: new Token("<OP>","->","@").isType("<NUMBER><OP><WHITESPACE>") == true
     */
    public boolean isType(String types) {
        if (!isTypeSpec(types))
            return ("<" + types + ">").indexOf(type) >= 0;
        return types.indexOf(type) >= 0;
    } 

    /**
     * Returns whether this is declared as a variable or
     * definite token.
     * Checks whether or not the symbol String is specified via a Regular Expression.
     */
    public boolean isVariable() {
        return symbol.startsWith("(") && symbol.endsWith(")");
    } 

    public int length() {
        return symbol.length();
    } 

    public boolean equals(Object arg) {
        return (arg instanceof String) && symbol.equals(arg)
            || (arg instanceof Token) && symbol.equals(((Token) arg).symbol);
    } 
    public int hashCode() {
        return Utility.hashCode(symbol);
    }

    /**
     * Reads one Token declaration from a Reader. declared in this form:<pre>
     * type|symbol|token|
     * </pre>
     */
    public Token(Reader from) throws IOException {
        while (from.ready()) {    // skip empty lines (linebreak) and comments #...
            type = readString(from, '|');
            if (type.trim().startsWith("#"))
                skipToEOL(from);
            else
                break;
        } 
        symbol = readString(from, '|');
        token = readString(from, '|');
        skipToEOL(from);
        if ("=".equals(token) &&!isVariable())
            token = symbol;
        if ("=".equals(type))
            type = token;
        if (!isTypeSpec(type))
            type = "<" + type + ">";
    }

    private static void skipToEOL(Reader rd) throws IOException {
        while (rd.ready()) {
            int c = rd.read();
            if (c == -1)
                break;
            if (c == '\n')
                break;
        } 
    } 

    public static String readString(Reader rd, char delimiter) throws IOException {
        String r = "";
        while (rd.ready()) {
            int c = rd.read();
            if (c == -1)
                break;
            char ch = (char) c;
            if (ch == delimiter)
                break;
            if (ch != '\r')
                r += ch;
        } 
        return r;
    } 

    public String toString() {
        return "[" + type + ": '" + symbol + "'->" + token + "]";
    } 

    private static boolean isTypeSpec(String type) {
        return type != null && type.startsWith("<") && type.endsWith(">");
    } 
}
