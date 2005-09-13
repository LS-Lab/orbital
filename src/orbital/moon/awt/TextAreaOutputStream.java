/**
 * @(#)TextAreaOutputStream.java 1.0 1997/06/07 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.io.OutputStream;

import java.awt.TextArea;

/**
 * This class is a TextAreaOutputStream.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
class TextAreaOutputStream extends OutputStream {

        /**
         * The TextArea where written output is displayed.
         * @serial
         */
        protected TextArea textComp;
        public TextAreaOutputStream(TextArea textComp) {
                this.textComp = textComp;
        }

        public void write(int c) {
                textComp.appendText("" + (char) c);        // @deprecated
        } 
        public void write(byte[] b, int off, int len) {
                textComp.appendText(new String(b, off, len));
        } 

        public void close() {
                textComp.setVisible(false);
                textComp = null;
        } 
}
