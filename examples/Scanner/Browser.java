

import java.io.*;
import orbital.io.*;
import orbital.io.parsing.*;
import orbital.awt.Closer;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class Browser extends Frame implements Runnable {
    public static void main(String arg[]) throws Exception {
	new Browser().run();
    } 

    protected HtmlParser hp;

    protected ScrollPane pane;
    protected DocCanvas  doc;
    public Browser() {
	super("Simple HTML-Browser");
	Button c;
	new Closer(this, true);
	add("North", new Label("HTML-Document view"));
	add("South", c = new Button("Show"));
	pane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
	pane.add(doc = new DocCanvas());
	add("Center", pane);

	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    System.exit(0);
		} 
	    });
	addComponentListener(new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
		    try {
			Browser.this.invalidate();
			e.getComponent().repaint();
		    } catch (ClassCastException imp) {}
		} 
	    });
	c.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    Browser.this.repaint();
		    Browser.this.doc.repaint();
		} 
	    });
	setSize(400, 300);
	setVisible(true);
    }

    public void run() {
	try {
	    Scanner scanner = new RegScanner(new InputStreamReader(getClass().getResourceAsStream("html.lex")));
	    scanner.scan(new InputStreamReader(getClass().getResourceAsStream("html.htm")));
	    TokenSequence ts = new TokenSequence(scanner);
	    hp = new HtmlParser(doc);
	    try {
		hp.parse(ts);
		doc.setDocument((Vector) hp.nextSymbol().data);
		pane.invalidate();
	    }
	    catch (EOFException eof) {}
	    catch (ClassCastException imp) {}
	    repaint();
	    doc.repaint();
	} catch (IOException x) {
	    x.printStackTrace();
	} catch (ParseException x) {
	    x.printStackTrace();
	} 
    } 
}

class DocCanvas extends Canvas {
    private Vector doc;

    public void setDocument(Vector document) {
	this.doc = document;
    } 
	
    public Dimension getPreferredSize() {
	return new Dimension(300, 800);
    }

    public void paint(Graphics g) {
	this.x = 0;
	this.y = 20;	// cursor
	this.g = g;

	if (doc == null)
	    return;

	Font font = getFont();
	g.setColor(getForeground());
	g.setFont(font);

	boolean body = false;
	for (int i = 0; i < doc.size(); i++)
	    try {
		Token t = (Token) doc.elementAt(i);
                if ("<TAG>".equals(t.type))
                    if ("body".equals(t.token)) body = true;
                    else if ("h1".equals(t.token)) g.setFont(font = new Font(font.getName(),font.getStyle(),20));
                    else if ("h2".equals(t.token)) g.setFont(font = new Font(font.getName(),font.getStyle(),17));
                    else if ("h3".equals(t.token)) g.setColor(Color.cyan);
                    else if ("b".equals(t.token)) g.setFont(font = new Font(font.getName(),font.getStyle()|Font.BOLD,font.getSize()));
                    else if ("i".equals(t.token)) g.setFont(font = new Font(font.getName(),font.getStyle()|Font.ITALIC,font.getSize()));
                if ("<ETAG>".equals(t.type))
                    if ("body".equals(t.token)) body = false;
                    else if ("h1".equals(t.token)) {nl();g.setFont(font = new Font(font.getName(),font.getStyle(),14));}
                    else if ("h2".equals(t.token)) {nl();g.setFont(font = new Font(font.getName(),font.getStyle(),14));}
                    else if ("h3".equals(t.token)) {nl();g.setColor(getForeground());}
                    else if ("b".equals(t.token)) g.setFont(font = new Font(font.getName(),font.getStyle()&~Font.BOLD,font.getSize()));
                    else if ("i".equals(t.token)) g.setFont(font = new Font(font.getName(),font.getStyle()&~Font.ITALIC,font.getSize()));
                if ("<STAG>".equals(t.type))
                    if ("p".equals(t.token)) {nl();nl();}
                    else if ("br".equals(t.token)) nl();
                if ("<WORD>".equals(t.type) && body)
                    out(t.token);
	    } catch (ClassCastException imp) {}
    } 

    private Graphics g;

    // cursor
    private int		 x;
    private int		 y;

    private void out(String word) {
	String wort = word + " ";	 // show with correct spacing
	if (x + g.getFontMetrics(g.getFont()).stringWidth(wort) > getSize().width)
	    nl();
	g.drawString(wort, x, y);
	x += g.getFontMetrics(g.getFont()).stringWidth(wort);
    } 

    private void nl() {
	x = 0;
	y += g.getFontMetrics(g.getFont()).getHeight();
    } 
}

class HtmlParser extends Parser {
    private DocCanvas doc;
    public HtmlParser(DocCanvas d) {
	doc = d;
    }

    private Vector				cont = new Vector();


    // insert all valid Token-Types below
    private static final String tokenTypes = "<WORD><SCHAR><STAG><CIRCUM><TAG>";

    /**
     * the nextSymbol will be parsed Html.
     */
    public Symbol nextSymbol() throws ParseException, IOException {
	try {
	    String symbol = "";
	    Token  tok = (Token) tokens.next();
	    if (tok.isType("TAG")) {
		cont.addElement(tok);
		Token tok2 = (Token) tokens.next();
		while (tok2.isType(tokenTypes)) {
		    if (tok2.isType("WORD"))
			cont.addElement(tok2);		   // symbol += " "+tok2.token;
		    else if (tok2.isType("SCHAR"))	   // append to last WORD
			((Token) cont.lastElement()).token += tok2.token;	 // symbol += tok2.token;
		    else if (tok2.isType("CIRCUM"))	   // append to last WORD
			((Token) cont.lastElement()).token += tok2.token;	 // symbol += tok2.token;
		    else if (tok2.isType("STAG"))
			cont.addElement(tok2);
		    else if (tok2.isType("TAG")) {
			tokens.unconsume();
			symbol += nextSymbol().symbol;
		    } else
			break;
		    tok2 = (Token) tokens.next();
		} 
		Token tok3 = tok2;
		if (tok3.isType("ETAG"))
		    if (tok.token.equals(tok3.token))
			cont.addElement(tok3);
		    else
			throw new ParseException(tok3, "<ETAG>: '</" + tok.token + ">'", tokens);
		else
		    throw new ParseException(tok3, "<ETAG>", tokens);
		tokens.consume();
	    } 
	    return new Symbol(tok.token, symbol, cont);
	} catch (ClassCastException x) {
	    System.out.println(x);
	    return null;
	} 
    } 

    public Symbol parseTag(String tag) throws ParseException, IOException {
	Symbol s;
	do {
	    s = nextSymbol();
	} while (!tag.equals(s.type));
	return s;
    } 
}
