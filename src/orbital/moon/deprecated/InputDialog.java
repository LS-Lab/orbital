/*
 * @(#)InputDialog.java 0.9 1998/02/15 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Frame;
import java.awt.TextField;
import java.awt.Label;
import java.awt.Container;

import java.awt.Panel;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

/**
 * A InputDialog displays a prompt waiting modally for input.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see javax.swing.JOptionPane#showInputDialog(java.awt.Component, Object, String, int)
 */
public
class InputDialog extends UserDialog {
        private static class Debug {
                private Debug() {}
                public static void main(String arg[]) throws Exception {
                        System.err.println("Debug");
                        UserDialog dlg = new InputDialog(new Frame(), "InputDialog", "Type input here?");
                        dlg.start();
                        System.out.println(dlg.getResult());
                } 
        }

        /**
         * Contains the label view of the message displayed.
         * @serial
         */
        protected Label         messageView;

        /**
         * Contains the TextField prompting for input.
         * @serial
         */
        protected TextField input;
        public InputDialog(Frame parent, String title, String message, String defaultInput) {
                this(parent, title, message);
                input.setText(defaultInput);
        }
        public InputDialog(Frame parent, String title, String message) {
                this(parent, title);
                add(messageView = new Label(message), BorderLayout.NORTH);
        }

        public InputDialog(Frame parent, String title) {
                super(parent, title);
                add(input = new TextField(), BorderLayout.CENTER);
                input.addActionListener(this);
                input.requestFocus();
        }

        protected Container createControl() {
                Container control = new Panel();
                control.setLayout(new FlowLayout(FlowLayout.CENTER));
                Button c;
                control.add(c = new Button("Ok"));
                c.addActionListener(this);
                control.add(c = new Button("Cancel"));
                c.addActionListener(this);
                return control;
        } 

        protected void setResult(String result) {
                if ("Cancel".equals(result))
                        super.setResult(null);
                else
                        super.setResult(input.getText());
        } 
}
