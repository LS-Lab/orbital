import orbital.io.parsing.*;
import orbital.logic.State;
import java.io.*;

public class Ausdruck {
    public static void main(String arg[]) throws Exception {
	e_main(arg);
	r_main(arg);
    } 
    public static void e_main(String arg[]) throws Exception {
	System.out.println("scanner lexical settings");
	Scanner scanner = new RegScanner(new FileReader("regsymb.lex"));
	System.out.println("scanner scanning file 'expr'");
	scanner.scan(new FileReader("expr"));
	System.out.println("scanner process token sequence");
	TokenSequence ts = new TokenSequence(scanner);
	while (ts.hasNext())
	    System.out.println(ts.next());
    } 

    public static void r_main(String arg[]) throws Exception {
	Scanner scanner = new AtomicScanner(false);
	System.out.println("scanner scanning file 'number'");
	scanner.scan(new FileReader("number"));
	TokenSequence ts;
	System.out.println("scanner process regular expression automata");
	Object r = new RegExAutomata("[0-9]*(.[0-9]*(e[+-][0-9]*)?)?").processAutomata(new State(), ts = new TokenSequence(scanner));
	System.out.println("matched '" + r + "'");
    } 
}
