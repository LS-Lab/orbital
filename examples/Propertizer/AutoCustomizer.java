import orbital.awt.*;
import java.awt.*;
import java.beans.*;

/**
 * A very simple example of the default customizer's capabilites.
 */
public class AutoCustomizer {
    public static void main(String arg[]) throws Exception {
        java.awt.Frame f = new java.awt.Frame();
        f.setLayout(new java.awt.BorderLayout());
        Component p = (Component) java.beans.Beans.instantiate(AutoCustomizer.class.getClassLoader(), orbital.awt.NumberInput.class.getName());
        p.addMouseListener(new orbital.awt.CustomizerViewController(f));
        f.add(new java.awt.Label("double-click on the panel to customize"), BorderLayout.NORTH);
        f.add(p, BorderLayout.CENTER);
        f.setSize(300, 200);
        new orbital.awt.Closer(f, true, true);
        f.setVisible(true);
    } 
}
