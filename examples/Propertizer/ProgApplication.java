import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import orbital.awt.CustomizerViewController;
import orbital.awt.UIUtilities;

public class ProgApplication extends JFrame {

    protected CustomizerViewController custom;
    private ProgSettings                           settings = new ProgSettings();
    public ProgApplication() throws Exception {
        setLayout(new BorderLayout());
        custom = new CustomizerViewController(this);
        MenuBar  mb = new MenuBar();
        Menu     m = new Menu("Program");
        MenuItem mi;
        m.add(mi = new MenuItem("Settings..."));
        mi.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // first component to be customized: the settings
                    custom.showCustomizer(settings);
                                
                    // either we use bound properties to forward changes in the settings immediately
                    // or we use a simple style of explicit get after customization
                    // Using bound properties is usually preferred!
                    // However, since this should be a simple example, we use explicit get
                    setForeground(settings.getTextColor());
                } 
            });
        mb.add(m);
        setMenuBar(mb);

        // second component to be customized
        Component p = (Component) java.beans.Beans.instantiate(getClass().getClassLoader(), orbital.awt.NumberInput.class.getName());
        p.addMouseListener(custom);
        Component label;
        getContentPane();add(label = new JLabel("double-click on the panel to customize this view or choose from menu"), BorderLayout.NORTH);
        label.setForeground(null);
        getContentPane().add(p, BorderLayout.CENTER);
        pack();
    }

    public static void main(String[] args) throws Exception {
        UIUtilities.setDefaultLookAndFeel();
        ProgApplication f = new ProgApplication();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    } 
}
