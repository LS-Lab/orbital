import java.awt.Color;

/**
 * The object keeping the program settings.
 * ProgSettings provides standard get/set accessor methods.
 * Since we want to provide a hint for users, we provide visible descriptions
 * of our properties in the ProgSettingsBeanInfo class.
 * Although this is not necessary, it helps the user.
 * @see ProgSettingsBeanInfo
 */
public class ProgSettings {
    private String			title;
    private int				priority;
    private boolean			backup;
    private int				usage;
    private String			comment;
    private Color			textColor;

    /**
     * possible values for usage
     */
    public static final int EVALUATION = 1;
    public static final int TESTING = 2;
    public static final int END_USER = 3;

    public ProgSettings() {
	title = "Application";
	comment = "simple demonstration of application settings";
	usage = END_USER;
    }

    public String getTitle() {
	return title;
    } 

    public void setTitle(String newTitle) {
	title = newTitle;
    } 

    public void setPriority(int newPriority) {
	priority = newPriority;
    } 

    public int getPriority() {
	return priority;
    } 

    public void setBackup(boolean newBackup) {
	backup = newBackup;
    } 

    public boolean isBackup() {
	return backup;
    } 

    public void setUsage(int newUsage) {
	usage = newUsage;
    } 

    public int getUsage() {
	return usage;
    } 

    public void setComment(String newComment) {
	comment = newComment;
    } 

    public String getComment() {
	return comment;
    } 

    public void setTextColor(Color newTextColor) {
	textColor = newTextColor;
    } 

    public Color getTextColor() {
	return textColor;
    } 
}
