/*
 * @(#)ImageCanvas.java    0.9 98/03/03 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import java.awt.Dimension;

import java.beans.Beans;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * A Canvas displaying an Image.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class ImageCanvas extends Canvas implements ImageObserver, Serializable {
    private static final long serialVersionUID = 3005746876745573942L;

    /**
     * the image to be shown on the canvas.
     * @serial
     */
    protected Image                       image = null;

    /**
     * true if the image should be stretched to fit the whole canvas.
     * @serial
     */
    protected boolean             stretched = false;

    public ImageCanvas(Image image) {
        this.image = image;
    }
    public ImageCanvas() {
        this.image = null;
        if (Beans.isDesignTime())
            try {
                java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(getClass());
                this.image = info.getIcon(java.beans.BeanInfo.ICON_COLOR_32x32);
            } catch (java.beans.IntrospectionException x) {
                x.printStackTrace();
            } 
    }

    /**
     * get the image to be shown on the canvas.
     */
    public Image getImage() {
        return image;
    } 

    /**
     * set the image to be shown on the canvas.
     */
    public void setImage(Image next_image) {
        Image old = image;
        image = next_image;
        invalidate();
        propertyChangeListeners.firePropertyChange("image", old, image);
    } 

    /**
     * true if the image should be stretched to fit the whole canvas.
     */
    public boolean isStretched() {
        return stretched;
    } 

    /**
     * set whether the image should be stretched to fit the whole canvas.
     */
    public void setStretched(boolean stretch_image) {
        boolean old = stretched;
        stretched = stretch_image;
        invalidate();
        propertyChangeListeners.firePropertyChange("stretched", old, stretched);
    } 

    /**
     * @serial
     */
    private final PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListeners.addPropertyChangeListener(l);
    } 
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListeners.removePropertyChangeListener(l);
    } 

    public void paint(Graphics g) {
        update(g);
    } 

    public void update(Graphics g) {
        if (image == null)
            return;
        if (stretched) {
            Dimension d = getSize();
            g.drawImage(image, 0, 0, d.width, d.height, getBackground(), this);
        } else
            g.drawImage(image, 0, 0, getBackground(), this);
    } 

    /**
     * Returns the dimension of the Image displayed.
     * LayoutManagers may size this component according to these dimension.
     */
    public Dimension getPreferredSize() {
        if (image == null)
            return super.getPreferredSize();
        layouting = true;
        return new Dimension(image.getWidth(this), image.getHeight(this));
    } 

    protected transient boolean layouting = false;
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if (infoflags == ImageObserver.ALLBITS) {
            if (layouting)
                doLayout();
            else
                repaint();
            layouting = false;
            return false;
        } 
        return true;
    } 
}
