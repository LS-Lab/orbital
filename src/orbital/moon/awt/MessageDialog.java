/*
 * @(#)MessageDialog.java 0.9 1998/02/15 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Frame;
import java.awt.Component;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.TextComponent;
import java.awt.TextArea;

/**
 * A MessageDialog displays a Message and waits modal for confirmation.
 * 
 * @version 0.9, 02/15/98
 * @author  Andr&eacute; Platzer
 * @see javax.swing.JOptionPane#showMessageDialog(java.awt.Component, Object, String, int)
 * @deprecated Since JDK1.1 use {@link javax.swing.JOptionPane#showMessageDialog(java.awt.Component, Object, String, int)} instead.
 */
public class MessageDialog extends UserDialog {
    private static class Debug {
	private Debug() {}
	public static void main(String arg[]) throws Exception {
	    System.err.println("Debug");
	    UserDialog dlg = new MessageDialog(new Frame(), "MessageDialog", "This is a message.", true);
	    dlg.start();
	    System.out.println(dlg.getResult());
	    System.exit(0);
	} 
    }

    /**
     * Contains the message displayed.
     * @serial
     */
    protected String	message;

    /**
     * Contains the component view used to display the message.
     * @serial
     */
    protected Component messageView;
    public MessageDialog(Frame parent, String title) {
	super(parent, title);
    }
    public MessageDialog(Frame parent, String title, Component messageView) {
	super(parent, title, messageView);
	this.message = null;
	this.messageView = messageView;
    }
    public MessageDialog(Frame parent, String title, String message, boolean multiline) {
	this(parent, title);
	this.message = message;
	if (!multiline)
	    add(messageView = new Label(message), BorderLayout.CENTER);
	else
	    try {
		TextArea t;
		add(t = new TextArea(message, 5, 30, TextArea.SCROLLBARS_VERTICAL_ONLY), BorderLayout.CENTER);
		t.setEditable(false);
		// t.setCaretPosition(0);
		messageView = t;
	    } catch (ClassCastException x) {
		x.printStackTrace();
	    } 
    }
    public MessageDialog(Frame parent, String title, String message) {
	this(parent, title, message, message != null && (message.indexOf('\n') >= 0 || message.length() > 30));
    }

    public void setText(String message) throws ClassCastException {
	if (messageView instanceof Label)
	    ((Label) messageView).setText(message);
	else if (messageView instanceof TextComponent)
	    ((TextComponent) messageView).setText(message);
	this.message = message;
    } 

    /**
     * This will statically autowrap the current text according to getText().
     * Will only work when setColumns has been called.
     * 
     * @see java.awt.TextComponent#getText
     */

    /*
     * protected void autowrap() throws ClassCastException {
     * int cols = ((TextArea)messageView).getColumns();
     * System.out.println(cols);
     * if (cols==0 || message.length()<=cols) return;
     * 
     * StringBuffer wrappedMessage = new StringBuffer(message.substring(0,cols));
     * for (int i=cols;i<message.length();i+=cols) {
     * wrappedMessage.append('\n');
     * wrappedMessage.append(message.substring(i,Math.min(i+cols,message.length())));
     * }
     * 
     * if (messageView instanceof TextComponent)
     * ((TextComponent)messageView).setText(wrappedMessage.toString());
     * else
     * throw new IllegalStateException("Can't autowrap on other Components than TextComponents");
     * }
     */
}
