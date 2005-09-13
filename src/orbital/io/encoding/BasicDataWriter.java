/**
 * @(#)BasicDataWriter.java 0.9 2000/03/27 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.io.encoding;

import java.io.Writer;
import java.io.IOException;

import java.io.StreamTokenizer;


/**
 * Basic format is a comma or newline separated list of values
 * where each value is either a number, a boolean value (<tt>#true</tt> or <tt>#false</tt>) or a string
 * in quotes (like <tt>"a string value"</tt>).
 */
class BasicDataWriter extends DataWriter {
    public BasicDataWriter(Writer output) {
        super(output);
    }

    public String getFormat() {
        return "basic";
    } 

    protected void nextToken(int ttype, String value) throws IOException {
        switch (ttype) {
        case StreamTokenizer.TT_EOL:
            out.write(System.getProperty("line.separator"));
            break;
        case StreamTokenizer.TT_NUMBER:
            out.write(value);
            out.write(", ");
            break;
        case TT_BOOLEAN:
            out.write('#' + value.toUpperCase() + '#');
            out.write(", ");
            break;
        case StreamTokenizer.TT_WORD:
            if (value == null) {
                out.write("\"\"");
                out.write(", ");
                return;
            } else
                out.write('"' + value + '"');
            out.write(", ");
            break;
        default:
            if (value == null) {
                out.write("\"" + (char) ttype + "\"");
                out.write(", ");
                return;
            } else
                out.write('"' + value + '"');
            out.write(System.getProperty("line.separator"));
        }
    } 
}
