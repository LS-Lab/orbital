import orbital.io.*;
import orbital.io.parsing.*;
import orbital.logic.State;
import java.io.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Parser for (german) natural language numbers.
 */
public class Zahlen {
    static final Logger logger = Logger.global;
    
    public static void main(String arg[]) throws Exception {
	System.out.println("(German)");
	System.out.println("Liest eine als Wort formulierte, maximal dreistellige Zahl aus der Datei 'zahl'.");
	Scanner substitutor = new RegScanner(new FileReader("symbols.lex"));
	substitutor.scan(new FileReader("zahl"));
	Scanner atomizingscanner = new RegScanner(new FileReader("tokenizer.lex"));
	atomizingscanner.scan(substitutor);
	new ZahlParser().parseAll(new TokenSequence(atomizingscanner));
    } 
}



class ZahlParser extends Parser {

    /**
     * the nextSymbol will be a parsed Zahl.
     */
    public Symbol nextSymbol() throws ParseException, IOException {
	try {
	    return (Symbol) new ParseZahl().processAutomata(state, tokens);
	} catch (ClassCastException x) {
	    Zahlen.logger.log(Level.WARNING, "parse", x);
	    return null;
	} 
    } 


    class ParseZehner implements Automata {
	Symbol		  result;
	TokenSequence tokens;
	State		  state;
	private Object setResult(Symbol result) {
	    state.setState("ZEHNER");
	    tokens.consume();
	    this.result = result;
	    return result;
	} 
	public Object processAutomata(State state, TokenSequence tokens) throws ParseException, IOException, ClassCastException, IllegalArgumentException {
	    this.tokens = tokens;
	    this.state = state;
	    if (!tokens.hasNext())
		return null;
	    Token tok = (Token) tokens.next();
	    Zahlen.logger.log(Level.FINE, "token {0}", tok);

	    if (tok.isType("ZIFFER")) {
		Token tok2 = (Token) tokens.next();
		Zahlen.logger.log(Level.FINE, "ziffer {0}", tok2);
		if (tok2.isType("ZEHN"))
		    return setResult(new Symbol("1" + tok.token));
		else if (tok2.isType("ZIG"))
		    return setResult(new Symbol(tok.token + "0"));
		else if (tok2.isType("UND")) {
		    Token tok3 = (Token) tokens.next();
		    if (!tok3.isType("ZIFFER"))
			throw new ParseException(tok3, "<ZIFFER>", tokens);
		    Token tok4 = (Token) tokens.next();
		    if (!tok4.isType("ZIG"))
			throw new ParseException(tok4, "<ZIG>", tokens);
		    return setResult(new Symbol(tok3.token + tok.token));
		} else
		    tokens.unconsume();
		return setResult(new Symbol("0" + tok.token));
	    } else if (tok.isType("ZEHN"))
		return setResult(new Symbol(tok.token + "0"));
	    throw new ParseException("No Symbol for Token: " + tok);
	} 
    }

    class ParseZahl implements Automata {
	Symbol		  result;
	TokenSequence tokens;
	State		  state;

	/*
	 * private Object setResult(Symbol result) {
	 * state.setState("ZEHNER");
	 * tokens.consume();
	 * this.result = result;
	 * return result;
	 * }
	 */
	public Object processAutomata(State state, TokenSequence tokens) throws ParseException, IOException, ClassCastException, IllegalArgumentException {
	    this.tokens = tokens;
	    this.state = state;
	    if (!tokens.hasNext())
		return null;
	    Token tok = (Token) tokens.next();
	    Zahlen.logger.log(Level.FINE, "parse {0}", tok);

	    if (tok.isType("ZIFFER")) {
		Token tok2 = (Token) tokens.next();
		if (tok2.isType("HUNDERT")) {
		    tokens.consume();
		    return new Symbol(tok.token + ((Symbol) new ParseZehner().processAutomata(state, tokens)).getSymbol());
		} 
		tokens.unconsume();
		tokens.unconsume();

		// ((Scanner)tokens.getSource()).unreadToken(tok);	//tokens.pushback(tok);		// tokens.getScanner().unreadToken(tok);
		// ((Scanner)tokens.getSource()).unreadToken(tok2); //tokens.pushback(tok2);		// tokens.getScanner().unreadToken(tok2);
		return new ParseZehner().processAutomata(state, tokens);
	    } 
	    throw new ParseException("No Symbol for Token: " + tok);
	} 
    }
}
