package orbital.math;

import java.security.PrivilegedAction;


/**
 * identical to
 * @see sun.security.action.GetPropertyAction
 * @version 1.1, 2002-12-06
 */
class GetPropertyAction implements PrivilegedAction  {
    private final String propertyName;
    private final String defaultValue;
    public GetPropertyAction(String propertyName, String defaultValue){
	this.propertyName = propertyName;
	this.defaultValue = defaultValue;
    }
    // implementation of java.security.PrivilegedAction interface

    public Object run()
    {
	return System.getProperty(propertyName, defaultValue);
    }
    
}// GetPropertyAction
