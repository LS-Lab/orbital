/*
 * @(#)QuestionDialog.java 0.9 1998/02/15 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Frame;
import java.awt.Panel;
import java.awt.Button;
import java.awt.Container;
import java.awt.Component;

import java.awt.FlowLayout;

/**
 * A QuestionDialog displays a Message and waits modal for confirmation.
 * <p>
 * Accepts <code>Yes</code>, <code>No</code> or <code>Cancel</code>, which represent the set of possible results.
 * Various styles exist, but the results will always be as above, even if the
 * dialog is displayed with <code>Ok</code>, <code>Cancel</code> or <code>Oui</code>, <code>Non</code>.
 * So you can change the style without the need to update the interpretation of results.
 * A style either allows a binary decision (Yes or No) or a trinary decision (Yes, No or Cancel) depending
 * on the number of choices displayed.
 * 
 * @version 0.9, 02/15/98
 * @author  Andr&eacute; Platzer
 * @see javax.swing.JOptionPane#showConfirmDialog(java.awt.Component, Object, String, int, int)
 * @deprecated Since JDK1.1 use {@link javax.swing.JOptionPane#showConfirmDialog(java.awt.Component, Object, String, int, int)} instead.
 */
public class QuestionDialog extends MessageDialog {
    private static class Debug {
	private Debug() {}
	public static void main(String arg[]) throws Exception {
	    System.err.println("Debug");
	    UserDialog dlg = new QuestionDialog(new Frame(), "QuestionDialog", "What about this question?", YES_NO);
	    dlg.start();
	    System.out.println(dlg.getResult());
	    System.exit(0);
	} 
    }

    // enumeration of styles
    public static final int		NONE = 0;

    /**
     * Display Yes and No resulting in <code>"Yes"</code> and <code>"No"</code>.
     */
    public static final int		YES_NO = 2;

    /**
     * Display Ok and Cancel resulting in <code>"Yes"</code> and <code>"No"</code>.
     */
    public static final int		OK_CANCEL = 1;

    /**
     * Display Yes, No and Cancel resulting in <code>"Yes"</code>, <code>"No"</code> and <code>"Cancel"</code>.
     */
    public static final int		YES_NO_CANCEL = 3;

    private static final String buttons[][] = {
	{
	    "No"
	}, {
	    "Ok", "Cancel"
	}, {
	    "Yes", "No"
	}, {
	    "Yes", "No", "Cancel"
	}
    };
    private static final int	result_actions_style = buttons.length - 1;

    public QuestionDialog(Frame parent, String title, String message) {
	this(parent, title, message, YES_NO);
    }
    public QuestionDialog(Frame parent, String title, String message, int questionStyle) {
	super(parent, title, message);
	setStyle(questionStyle);
    }
    public QuestionDialog(Frame parent, String title, Component message, int questionStyle) {
	super(parent, title, message);
	setStyle(questionStyle);
    }

    /**
     * sets the style of the QuestionDialog.
     * @serial
     */
    protected int style = NONE;

    public void setStyle(int style) {
	if (style < 0 || buttons.length <= style)
	    throw new IllegalArgumentException("invalid style");
	this.style = style;
    } 

    protected Container createControl() {
	Container control = new Panel();
	control.setLayout(new FlowLayout(FlowLayout.CENTER));
	for (int i = 0; i < buttons[style].length; i++) {
	    Button c;
	    control.add(c = new Button(buttons[style][i]));
	    c.setActionCommand(buttons[result_actions_style][i]);
	    c.addActionListener(this);
	} 
	return control;
    } 
}
