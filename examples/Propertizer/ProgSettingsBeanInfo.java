

// Description:  Simple management of program properties

import java.beans.*;
import orbital.awt.TaggedPropertyEditorSupport;

public class ProgSettingsBeanInfo extends SimpleBeanInfo {
    Class beanClass = ProgSettings.class;

    public ProgSettingsBeanInfo() {}

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor _title = new PropertyDescriptor("title", beanClass, "getTitle", "setTitle");
            _title.setDisplayName("title");
            _title.setShortDescription("the title of this application");
            PropertyDescriptor _priority = new PropertyDescriptor("priority", beanClass, "getPriority", "setPriority");
            _priority.setDisplayName("priority");
            _priority.setShortDescription("thread priority value");
            PropertyDescriptor _backup = new PropertyDescriptor("backup", beanClass, "isBackup", "setBackup");
            _backup.setDisplayName("backup");
            _backup.setShortDescription("whether to keep backup files");
            PropertyDescriptor _usage = new PropertyDescriptor("usage", beanClass, "getUsage", "setUsage");
            _usage.setDisplayName("usage");
            _usage.setShortDescription("primary usage descriptor");
            _usage.setPropertyEditorClass(UsagePropertyEditor.class);
            PropertyDescriptor _comment = new PropertyDescriptor("comment", beanClass, "getComment", "setComment");
            _comment.setDisplayName("comment");
            _comment.setShortDescription("additional comment property");
            PropertyDescriptor _textColor = new PropertyDescriptor("textColor", beanClass, "getTextColor", "setTextColor");
            _textColor.setDisplayName("text color");
            _textColor.setShortDescription("foreground text color property");
            PropertyDescriptor[] pds = new PropertyDescriptor[] {
                _title, _priority, _backup, _usage, _comment, _textColor,
            };
            return pds;
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            return null;
        } 
    } 

    public static class UsagePropertyEditor extends TaggedPropertyEditorSupport {
        public UsagePropertyEditor() {
            super(new String[] {
                "evaluation", "testing", "end user"
            }, new Object[] {
                new Integer(ProgSettings.EVALUATION), new Integer(ProgSettings.TESTING), new Integer(ProgSettings.END_USER)
            }, new String[] {
                "ProgSettings.EVALUATION", "ProgSettings.TESTING", "ProgSettings.END_USER"
            });
        }
    }

}
