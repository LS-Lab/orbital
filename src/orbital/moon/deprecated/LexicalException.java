/*
 * @(#)LexicalException.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io;

import java.io.IOException;

/**
 * An Exception thrown if an input read doesn't match lexically, i.e.
 * no Token declarations match it.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see ParseException
 * @deprecated Since Orbital1.0
 */
public
class LexicalException extends IOException {
        public LexicalException() {}

        public LexicalException(String spec) {
                super(spec);
        }
}
