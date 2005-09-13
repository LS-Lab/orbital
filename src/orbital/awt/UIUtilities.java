/**
 * @(#)UIUtilities.java 0.9 2000/03/08 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.awt.Component;
import java.awt.Window;
import java.awt.Frame;

import java.awt.Graphics;
import java.awt.FontMetrics;
import java.io.PrintStream;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.FeatureDescriptor;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import orbital.util.InnerCheckedException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;

/**
 * Utilities for User-Interface.
 * 
 * @stereotype Utilities
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.util.Utility
 * @see javax.swing.SwingUtilities
 */
public final class UIUtilities {

    /**
     * prevent instantiation - final static class
     */
    private UIUtilities() {}

    /**
     * Center a component within a parental component.
     * @param comp the component to be centered.
     * @param parent center relative to what. <code>null</code> to center relative to screen.
     * @see #setCenter(Component)
     */
    public static void setCenter(Component comp, Component parent) {
        if (parent == null) {
            setCenter(comp);
            return;
        } 
        Dimension dlgSize = comp.getPreferredSize();
        Dimension frmSize = parent.getSize();
        Point     loc = parent.getLocation();
        if (dlgSize.width < frmSize.width && dlgSize.height < frmSize.height)
            comp.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        else
            setCenter(comp);
    } 

    /**
     * Center a component on the screen.
     * @param comp the component to be centered relative to the screen.
     *  It must already have its final size set.
     * @preconditions comp.getSize() as on screen.
     */
    public static void setCenter(Component comp) {
        Dimension screenSize = comp.getToolkit().getScreenSize();
        Dimension frameSize = comp.getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        comp.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    } 

    /**
     * draws a centered String.
     */
    public static void drawCenteredString(Graphics g, String text, int cx, int cy) {
        FontMetrics fm = g.getFontMetrics();
        int sWidth = fm.stringWidth(text);
        int sHeight = -8;    //XXX: fm.getHeight();
        g.drawString(text, cx - sWidth/2, cy - sHeight/2);
    }

    /**
     * Get the Window a Component is contained in.
     * <p>
     * Traverses parents until a Window is found.
     * @return the first parent window or <code>null</code> if none.
     * @deprecated Use {@link javax.swing.SwingUtilities#getAncestorOfClass(Class, Component) SwingUtilities.getAncestorOfClass(Window.class, n)} instead.
     * @see javax.swing.SwingUtilities#windowForComponent(java.awt.Component)
     * @see javax.swing.SwingUtilities#getAncestorOfClass(Class, Component)
     */
    public static Window getParentalWindow(Component n) {
        while (!(n instanceof Window) && n != null)
            n = n.getParent();
        return (Window) n;
    } 

    /**
     * Get the Frame a Component is contained in.
     * <p>
     * Traverses parents until a Frame is found.
     * @return the first parent frame or <code>null</code> if none.
     * @deprecated Use {@link javax.swing.SwingUtilities#getAncestorOfClass(Class, Component) SwingUtilities.getAncestorOfClass(Frame.class, n)} instead.
     * @see javax.swing.SwingUtilities#getAncestorOfClass(Class, Component)
     */
    public static Frame getParentalFrame(Component n) {
        while (!(n instanceof Frame) && n != null)
            n = n.getParent();
        return (Frame) n;
    } 
    
    // PLAF utilities
        
    /**
     * Adds pluggable look-and-feel menu items to a menu.
     * @param root which root component to set the look and feel for.
     * @param view the menu to add the Look and Feel chooser items.
     */
    public static void addLookAndFeelMenuItems(Component root, JMenu view) {

        // Look and Feel Radio control
        ButtonGroup              group = new ButtonGroup();
        ToggleUIListener toggleUIListener = new ToggleUIListener(root);

        metalMenuItem = (JRadioButtonMenuItem) view.add(new JRadioButtonMenuItem("Java Look and Feel"));
        metalMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("Metal"));

        // metalMenuItem.setSelected(true);
        group.add(metalMenuItem);
        metalMenuItem.addItemListener(toggleUIListener);
        metalMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));

        JRadioButtonMenuItem motifMenuItem = (JRadioButtonMenuItem) view.add(new JRadioButtonMenuItem("Motif Look and Feel"));
        motifMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("CDE/Motif"));
        group.add(motifMenuItem);
        motifMenuItem.addItemListener(toggleUIListener);
        motifMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));

        JRadioButtonMenuItem windowsMenuItem = (JRadioButtonMenuItem) view.add(new JRadioButtonMenuItem("Windows Style Look and Feel"));
        windowsMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("Windows"));
        group.add(windowsMenuItem);
        windowsMenuItem.addItemListener(toggleUIListener);
        windowsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
    } 

    /**
     * Set the system default look and feel.
     * @see javax.swing.UIManager
     */
    public static void setDefaultLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
    } 

    private static JRadioButtonMenuItem metalMenuItem;

    /**
     * Switch between the Windows, Motif, Mac, and the Java Look and Feel
     */
    static class ToggleUIListener implements ItemListener {
        protected Component root;
        public ToggleUIListener(Component root) {
            this.root = root;
        }
        public void itemStateChanged(ItemEvent e) {
            root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
            try {
                if (rb.isSelected() && rb.getText().equals("Windows Style Look and Feel")) {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(root);
                } else if (rb.isSelected() && rb.getText().equals("Macintosh Look and Feel")) {
                    UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(root);
                } else if (rb.isSelected() && rb.getText().equals("Motif Look and Feel")) {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(root);
                } else if (rb.isSelected() && rb.getText().equals("Java Look and Feel")) {

                    // javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(
                    // new javax.swing.plaf.metal.DefaultMetalTheme());
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(root);
                } 
            } catch (UnsupportedLookAndFeelException exc) {
                rb.setEnabled(false);
                System.err.println("Unsupported LookAndFeel: " + rb.getText());

                // Set L&F to JLF
                try {
                    metalMenuItem.setSelected(true);
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    SwingUtilities.updateComponentTreeUI(root);
                } catch (Exception exc2) {
                    exc2.printStackTrace();
                    System.err.println("Could not load LookAndFeel: " + exc2);
                    exc2.printStackTrace();
                } 
            } catch (Exception exc) {
                rb.setEnabled(false);
                exc.printStackTrace();
                System.err.println("Could not load LookAndFeel: " + rb.getText());
                exc.printStackTrace();
            } 

            root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } 

    }

    public static void printBean(Class beanClass) {
        printBean(beanClass, System.out);
    } 

    /**
     * Print Information on a bean.
     */
    public static void printBean(Class beanClass, PrintStream ps) {
        try {
            BeanInfo info = Introspector.getBeanInfo(beanClass, Introspector.USE_ALL_BEANINFO);
            if (info == null)
                throw new NullPointerException("no BeanInfo for class: " + beanClass);
            BeanDescriptor desc = info.getBeanDescriptor();
            if (desc == null)
                throw new NullPointerException("no BeanDescriptor for class: " + beanClass);
            ps.println(info(desc) + " " + desc.getBeanClass() + " " + info);
            PropertyDescriptor[] beanProperties = info.getPropertyDescriptors();
            if (beanProperties == null)
                throw new NullPointerException("no PropertyDescriptors for class: " + beanClass);

            for (int i = 0; i < beanProperties.length; i++) {
                if (beanProperties[i].isHidden())
                    continue;
                ps.println(info(beanProperties[i]));
            } 
        } catch (IntrospectionException e) {
            throw new InnerCheckedException("bean not introspectable", e);
        } 
    } 

    /**
     * Get Information on a feature descriptor.
     */
    public static String info(FeatureDescriptor desc) {
        StringBuffer sb = new StringBuffer();
        sb.append(desc.getName());
        if (!desc.getName().equals(desc.getDisplayName()))
            sb.append(" '" + desc.getDisplayName() + "'");
        if (!desc.getName().equals(desc.getShortDescription()))
            sb.append(System.getProperty("line.separator") + "  \"" + desc.getShortDescription() + '"');
        return sb.toString();
    } 

    /**
     * Get Information on a property descriptor.
     */
    public static String info(PropertyDescriptor desc) {
        String           nl = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer(info((FeatureDescriptor) desc) + nl + "  " + desc.getPropertyType() + ", ");
        if (desc.getReadMethod() != null)                 // we have a reader
            if (desc.getWriteMethod() != null)    // and we have a writer
                sb.append("readwrite");
            else                                                                  // but we have no writer
                sb.append("readonly");
        else                                                                      // we have no reader
            if (desc.getWriteMethod() != null)            // but we have a writer
                sb.append("writeonly");
            else                                                                          // and we have no writer
                sb.append("inaccessible");
        sb.append(", " + (desc.isBound() ? "bound" : "not bound") + ", " + (desc.isConstrained() ? "constrained" : "not constrained"));
        return sb.toString();
    } 
}
