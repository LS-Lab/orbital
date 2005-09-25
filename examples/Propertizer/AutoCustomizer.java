import orbital.awt.*;
import java.awt.*;
import java.beans.*;
import javax.swing.*;

/**
 * A very simple example of the default customizer's capabilites.
 */
public class AutoCustomizer {
    public static void main(String arg[]) throws Exception {
        javax.swing.JFrame f = new javax.swing.JFrame();
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new java.awt.BorderLayout());
        Component p = (Component) java.beans.Beans.instantiate(AutoCustomizer.class.getClassLoader(), orbital.awt.NumberInput.class.getName());
        p.addMouseListener(new orbital.awt.CustomizerViewController(f));
        f.getContentPane().add(new java.awt.Label("double-click on the panel to customize"), BorderLayout.NORTH);
        f.getContentPane().add(p, BorderLayout.CENTER);
        f.setSize(300, 200);
        f.setVisible(true);
    } 
}
