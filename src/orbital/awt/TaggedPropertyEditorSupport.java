/**
 * @(#)TaggedPropertyEditorSupport.java 0.9 2000/03/13 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import orbital.util.Utility;
import orbital.math.MathUtilities;

/**
 * A PropertyEditor base for tagged properties.
 * Derive to generate a concrete PropertyEditor class.
 * 
 * @version 0.9, 2000/03/13
 * @author  Andr&eacute; Platzer
 */
public class TaggedPropertyEditorSupport extends PropertyEditorSupport implements PropertyEditor {
    private String[] tags;
    private Object[] values;
    private String[] initializers;

    /**
     * Create a TaggedPropertyEditor for the given tags representing the specified values.
     * @param tags a list of tags that can be a value for the property.
     * @param values a list of values corresponding to the tags at the same index.
     * Must be in same order as tags.
     * @param javaInitilizers a list of java initialization strings that represent the values for the property.
     * Must be in same order as tags.
     * @pre tags.length == values.length && (javaInitializers == null || javaInitializers.length == values.length)
     * @throws IllegalArgumentException if the arrays have incompatible dimensions
     */
    protected TaggedPropertyEditorSupport(String[] tags, Object[] values, String[] javaInitializers) {
	if (tags.length != values.length)
	    throw new IllegalArgumentException("incompatible dimensions");
	if (javaInitializers != null && javaInitializers.length != values.length)
	    throw new IllegalArgumentException("incompatible dimensions");
	this.tags = tags;
	this.values = values;
	this.initializers = javaInitializers;
    }
    protected TaggedPropertyEditorSupport(String[] tags, Object[] values) {
	this(tags, values, null);
    }

    public String[] getTags() {
	return tags;
    } 
    
    public String getAsText() {
	Object v = getValue();
	for (int i = 0; i < values.length; i++)
	    if (Utility.equals(values[i], v))
		return tags[i];
	throw new IllegalStateException("an illegal value is set that is not an allowed tag: value=" + v + ", tags=" + MathUtilities.format(tags));
    } 

    public String getJavaInitializationString() {
	if (initializers == null)
	    return super.getJavaInitializationString();
	Object v = getValue();
	for (int i = 0; i < values.length; i++)
	    if (Utility.equals(values[i], v))
		return initializers[i];
	throw new IllegalStateException("an illegal value is set that is not an allowed tag: value=" + v + ", tags=" + MathUtilities.format(tags));
    } 

    public void setValue(Object value) {
	for (int i = 0; i < values.length; i++)
	    if (Utility.equals(values[i], value)) {
		super.setValue(value);
		return;
	    }
	throw new IllegalArgumentException("invalid value specified: '" + value + "'");
    }
	
    public void setAsText(String text) throws IllegalArgumentException {
	for (int i = 0; i < tags.length; i++)
	    if (tags[i].equals(text)) {
		setValue(values[i]);
		return;
	    } 
	throw new IllegalArgumentException("invalid tag specified: '" + text + "'");
    } 
}
