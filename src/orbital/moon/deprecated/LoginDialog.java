/*
 * @(#)LoginDialog.java 0.9 1999/01/16 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Frame;

import java.awt.TextField;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.BorderLayout;

/**
 * A LoginDialog displays a prompt for username and password, waiting modally for input.
 * 
 * @version 0.9, 16/01/99
 * @author  Andr&eacute; Platzer
 */
public
class LoginDialog extends PasswordDialog {

	/**
	 * @serial
	 */
	protected TextField name;
	public LoginDialog(Frame parent, String title) {
		super(parent, title);
		Panel panel;
		add(panel = new Panel(), BorderLayout.CENTER);
		panel.setLayout(new GridLayout(2, 1, 10, 5));
		panel.add(new LabelledComponent("User name", name = new TextField()));
		panel.add(new LabelledComponent("Password", input));
		name.setColumns(8);
		input.setColumns(8);
	}

	public LoginDialog(Frame parent, String title, String defaultName) {
		this(parent, title);
		name.setText(defaultName);
	}

	public LoginDialog(Frame parent) {
		this(parent, "Login Information");
	}

	public String getResult() {
		return name.getText() + ':' + super.getResult();
	} 
}
