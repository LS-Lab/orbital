package orbital.math;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.security.AccessControlException;;


/**
 * (almost) identical to
 * @see sun.security.action.GetPropertyAction
 * @version 1.1, 2002-12-06
 */
class GetPropertyAction implements PrivilegedAction  {
    public static final String getProperty(String propertyName, String defaultValue) {
	try {
	    return (String) AccessController.doPrivileged(
                            new GetPropertyAction(propertyName, defaultValue));
	}
	catch (AccessControlException denied) {
	    return defaultValue;
	}
    }

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
