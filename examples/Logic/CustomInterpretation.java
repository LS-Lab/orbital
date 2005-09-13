import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.moon.logic.ClassicalLogic;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import orbital.awt.Closer;
import java.util.*;

/**
 * An example for logic evaluations that make use of a full custom interpretation.
 * A custom interpretation with a different source for logical data values adds flexibility.
 * We could as well set the values in an ordinary Interpretation instance,
 * but this demonstrates how to query data from another source.
 * To ask values from the user, or query values from a database, a class
 * like this could be very useful.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class CustomInterpretation extends JFrame {

    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) throws Exception {
	new CustomInterpretation().start();
    } 

    /**
     * The logic to use - classical logic.
     */
    protected Logic   logic = new ClassicalLogic();

    /**
     * the formula and its signature.
     */
    private Document  formula;
    private Signature sigma;

    /**
     * the container for the buttons used to adjust the truth value of the
     * variables in the formula.
     */
    private Container values;
    private Map		  adjustment;

    /**
     * The result of the formula, whether the truth values adjusted
     * satisfy the formula.
     */
    private Document  result;

    /**
     * Runnable-init entry point.
     */
    public CustomInterpretation() {
	super("Logic example with custom interpretation");
	adjustment = new HashMap();
	Closer	  closer = new Closer(this, true, true);
	Container pane = getContentPane();
	pane.setLayout(new BorderLayout());
	JPanel	   panel = new JPanel(new BorderLayout());
	JTextField f = new JTextField();
	panel.add(new JLabel("Formula: "), BorderLayout.WEST);
	panel.add(f, BorderLayout.CENTER);
	formula = f.getDocument();
	pane.add(panel, BorderLayout.NORTH);
	formula.addDocumentListener(new DocumentListener() {
		public void insertUpdate(javax.swing.event.DocumentEvent e) {
		    init();
		} 
		public void removeUpdate(javax.swing.event.DocumentEvent e) {
		    init();
		} 
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
		    init();
		} 
	    });
	values = new JPanel(new FlowLayout());
	pane.add(values, BorderLayout.CENTER);
	panel = new JPanel(new BorderLayout());
	f = new JTextField();
	panel.add(new JLabel("Result: "), BorderLayout.WEST);
	panel.add(f, BorderLayout.CENTER);
	f.setEditable(false);
	result = f.getDocument();
	pane.add(panel, BorderLayout.SOUTH);
    }

    public void init() {
	try {
	    values.removeAll();
	    adjustment.clear();
	    String formulaText = formula.getText(0, formula.getLength());
	    this.sigma = logic.scanSignature(formulaText);
	    for (Iterator i = sigma.iterator(); i.hasNext(); ) {
		Symbol		   o = (Symbol) i.next();
		AbstractButton button = new JCheckBox(o.getSignifier());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    update();
			} 
		    });
		values.add(button);
		adjustment.put(o, button);
	    } 
	} catch (Exception x) {
	    System.out.println(x);
	} 
	update();
	invalidate();
	repaint();
    } 

    public void paint(Graphics g) {
	paintComponents(g);
    } 

    public void start() {
	try {
	    formula.insertString(0, "~(a&~b) | c", null);
	} catch (Exception x) {x.printStackTrace();}
	setSize(400, 100);
	setVisible(true);
    } 

    private void update() {
	try {
	    String		   formulaText = formula.getText(0, formula.getLength());
	    Formula		   f = (Formula) logic.createExpression(formulaText);

	    // create our custom interpretation
	    Interpretation interpretation = new ButtonModelInterpretation(sigma);
	    boolean		   satisfied = logic.satisfy(interpretation, f);
	    result.remove(0, result.getLength());
	    result.insertString(0, satisfied ? "yes" : "no", null);
	} catch (Exception x) {x.printStackTrace();}
    } 

    /**
     * A custom interpretation with a different source for logical data values.
     * We could as well set the values in an ordinary Interpretation instance,
     * but this demonstrates how to query data from another source.
     * To ask values from the user, or query values from a database, a class
     * like this could be very useful.
     */
    private class ButtonModelInterpretation extends InterpretationBase {
	public ButtonModelInterpretation(Signature sigma) {
	    super(sigma);
	}
	private ButtonModelInterpretation(Signature sigma, Map assoc) {
	    super(sigma);
	    super.putAll(assoc);
	}

	public Object get(Object symbol) {
	    if (!getSignature().contains(symbol))
		throw new NoSuchElementException("Symbol '" + symbol + "' not in Signature");
	    return adjustment.containsKey(symbol)
		? new Boolean(((AbstractButton) adjustment.get(symbol)).isSelected())
		: super.get(symbol);
	} 

	public boolean containsKey(Object symbol) {
	    if (!getSignature().contains(symbol))
		throw new NoSuchElementException("Symbol '" + symbol + "' not in Signature");

	    // we interpret all values in the signature
	    return true;
	} 

	public void putAll(Map associations) {
	    throw new UnsupportedOperationException("readonly interpretation");
	} 

	public Object put(Object symbol, Object value) {
	    throw new UnsupportedOperationException("readonly interpretation");
	} 
		
	public String toString() {
	    return "custom " + super.toString();
	}
		
    	public Interpretation union(Interpretation i2) {
	    Map c = new TreeMap(this);
	    c.putAll(i2);
	    return new ButtonModelInterpretation(getSignature().union(i2.getSignature()), c);
    	}
    }
}

