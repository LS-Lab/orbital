/*
 * @(#)JMessageDialog.java 0.9 1998/02/15 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Frame;
import java.awt.Component;
import java.awt.Label;
import java.awt.TextComponent;
import javax.swing.text.JTextComponent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

/**
 * A JMessageDialog displays a Message and waits modal for confirmation.
 * 
 * @version 0.9, 02/15/98
 * @author  Andr&eacute; Platzer
 */
public
class JMessageDialog extends UserDialog {
	private static class Debug {
		private Debug() {}
		public static void main(String arg[]) throws Exception {
			System.err.println("Debug");
			UserDialog dlg = new JMessageDialog(new Frame(), "JMessageDialog", "This is a very long message which will be displayed in various ways not directly specified here.");
			dlg.start();
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
	public JMessageDialog(Frame parent, String title, String message) {
		super(parent, title);
		this.message = message;
		JTextArea t;
		add(new JScrollPane(t = new JTextArea(message, 4, 40)), BorderLayout.CENTER);
		t.setEditable(false);
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		messageView = t;

		// new PlainDocument(new WrappedPlainView(el));
	}

	public void setMessage(String message) throws ClassCastException {
		if (messageView instanceof JTextComponent)
			((JTextComponent) messageView).setText(message);
		this.message = message;
	} 
}
