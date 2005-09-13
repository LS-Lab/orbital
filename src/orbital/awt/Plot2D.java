/*
 * @(#)Plot2D.java 0.9 1999/03/16 Andre Platzer
 * 
 * Copyright (c) 1996-1999 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.*;
import java.io.Serializable;

import orbital.math.Arithmetic;
import orbital.math.Real;
import orbital.math.Values;
import orbital.math.Vector;
import orbital.math.Matrix;
import orbital.math.functional.Functionals;
import orbital.logic.functor.Function;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.geom.*;
import java.awt.print.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.InputEvent;

import java.awt.Dimension;
import java.util.Map;
import java.util.Iterator;
import orbital.math.MathUtilities;
import orbital.awt.ChartModel.Entry;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class is a View-Bean for a ChartModel, it displays 2-dimensional graphs.
 * <p>
 * Attributes for {@link orbital.math.Matrix}
 * <ul>
 *   <li><code>color</code> - <code>java.awt.Color</code> object that defines the painting color.
 * </ul>
 * Attributes for {@link orbital.logic.functor.Function}
 * <ul>
 *   <li><code>color</code> - <code>java.awt.Color</code> object that defines the painting color.</li>
 *   <li><code>precisionPoints</code> - <code>java.lang.Integer</code> object that defines the number of points
 *    used for intermediate values and thus defines a precision.
 *    If, however, a value for <code>precision</code> is specified this option is ignored.</li>
 *   <li><code>precision</code> - <code>java.lang.Double</code> object that defines the precision with which
 *    intermediate values will be calculated.</li>
 * </ul>
 * Attributes for {@link orbital.logic.functor.Function}[] resp. vectorial Function are the same as for Function.
 * An instance of Function[] represents a parametric plot, a vectorial Function (one that returns Vectors)
 * is displayed like a parametric plot.
 * For each of those the element 0 specifies the function for the x-value at the times t
 * and the element 1 specifies the function for the y-value at the times t.
 * The time t is then in the specified range of the component with index 0.
 * 
 * @see ChartModel
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class Plot2D extends Canvas implements Printable, Serializable {
    private static final Logger logger = Logger.getLogger(Plot2D.class.getName());
    private static final long serialVersionUID = 6993708298034247585L;
        
    private static final int DEFAULT_PRECISION_POINTS = 200;

    /**
     * @serial
     */
    private ChartModel            model = null;

    /**
     * @serial
     */
    private boolean                       autoScaling;

    /**
     * @serial
     */
    private boolean                       fullScaling;

    public Plot2D(ChartModel model) {
        setModel(model);
        addMouseListener(new MouseAdapter() {

                // Zoom on Ctrl-Click, Unzoom on Ctrl-RightClick or Ctrl-Shift-Click
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 1 || (e.getModifiers() & InputEvent.CTRL_MASK) == 0)
                        return;
                    if (Plot2D.this.model == null)
                        return;
                    e.translatePoint(-getBounds().x, -getBounds().y);
                    double zoom = (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK ? 2.0 : 0.5;
                    Range  range = Plot2D.this.model.getRange();
                    double dx = range.getLength(0);
                    double dy = range.getLength(1);
                    double m_x = vx(e.getX());
                    double m_y = vy(e.getY());
                    dx *= zoom;
                    dy *= zoom;
                    range.min = Values.getDefaultInstance().valueOf(new double[] {
                        m_x - dx / 2, m_y - dy / 2
                    });
                    range.max = Values.getDefaultInstance().valueOf(new double[] {
                        m_x + dx / 2, m_y + dy / 2
                    });
                    Plot2D.this.model.setRange(range);

                    // adapt precision while zooming
                    for (Iterator i = Plot2D.this.model.getGraphs().iterator(); i.hasNext(); ) {
                        Entry et = (Entry) i.next();
                        if ((et.getGraph() instanceof Function || et.getGraph() instanceof Function[]) && et.getAttributes() != null) {
                            Number prec = (Number) et.getAttributes().get("precision");
                            if (prec != null)
                                et.getAttributes().put("precision", new Double(prec.doubleValue() * zoom));
                            prec = (Number) et.getAttributes().get("precisionPoints");
                            if (prec != null)
                                et.getAttributes().put("precisionPoints", new Integer((int) Math.ceil(prec.intValue() / zoom)));
                        } 
                    } 
                    Plot2D.this.invalidate();
                } 
            });
    }
    public Plot2D() {
        this(null);
    }

    public ChartModel getModel() {
        return model;
    } 
    public void setModel(ChartModel model) {
        ChartModel old = this.model;

        // vetos.fireVetoableChange("model",old,model);
        doRegisterAtChart(false);
        this.model = model;
        doRegisterAtChart(true);
        if (autoScaling)
            model.setAutoScaling();
        propertyChangeListeners.firePropertyChange("model", old, model);
        invalidate();
    } 
    private void doRegisterAtChart(boolean registering) {
        if (model == null)
            return;
        if (registering) {
            model.addPropertyChangeListener(listening = new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        if (Plot2D.this.autoScaling && !e.getPropertyName().equals("range"))
                            Plot2D.this.model.setAutoScaling();
                        Plot2D.this.invalidate();
                        Graphics g = Plot2D.this.getGraphics();
                        if (g == null)
                            // not yet displayed
                            return;
                        g.clearRect(0, 0, Plot2D.this.getSize().width, Plot2D.this.getSize().height);
                        Plot2D.this.paint(g);
                        logger.log(Level.FINEST, "invalidate graphics to redraw");
                    } 
                });
        } else if (listening != null) {
            model.removePropertyChangeListener(listening);
            listening = null;
        } 
    } 

    public boolean isAutoScaling() {
        return autoScaling;
    } 
    public void setAutoScaling(boolean fixed_autoScalingOnly) {
        boolean old = autoScaling;
        autoScaling = fixed_autoScalingOnly;
        if (autoScaling && getModel() != null)
            getModel().setAutoScaling();
        propertyChangeListeners.firePropertyChange("autoScaling", old, autoScaling);
    } 

    /**
     * @serial
     */
    private PropertyChangeListener listening = null;

    public boolean isFullScaling() {
        return fullScaling;
    } 
    public void setFullScaling(boolean fullScalingMarks) {
        boolean old = fullScaling;
        fullScaling = fullScalingMarks;
        propertyChangeListeners.firePropertyChange("fullScaling", old, fullScaling);
        invalidate();
    } 
        
    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }

    /**
     * @serial
     */
    private final PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);

    // private VetoableChangeSupport vetos = new VetoableChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListeners.addPropertyChangeListener(l);
    } 
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListeners.removePropertyChangeListener(l);
    } 
    
    /*
     * public void addVetoableChangeListener(VetoableChangeListener l) {
     * vetos.addVetoableChangeListener(l);
     * }
     * public void removeVetoableChangeListener(VetoableChangeListener l) {
     * vetos.removeVetoableChangeListener(l);
     * }
     */

    // painting routines
    
    public int print(Graphics gg, PageFormat pf, int pi) throws PrinterException {
        if (pi >= 1)
            return Printable.NO_SUCH_PAGE;

        Graphics2D g = (Graphics2D) gg;

        // To see the chart on the printed page, you
        // must translate the printer graphics context
        // into the imageable area
        g.translate(pf.getImageableX(), pf.getImageableY());
        g.setColor(Color.black);
        paint(g);
        return Printable.PAGE_EXISTS;
    } 

    public void paint(Graphics gg) {
        logger.log(Level.FINEST, "Plot2D.paint()");
        if (model == null)
            return;
        Graphics2D g = (Graphics2D) gg;
        Range      range = model.getRange();
        if (range == null || model.getScale() == null)
            return;
        setWindow(range.min.get(0), range.min.get(1), range.max.get(0), range.max.get(1));

        calcSettings();
        drawAxis(g);
        update(g);
    } 

    public void update(Graphics g) {
        logger.log(Level.FINEST, "update() graphs");
        if (model == null)
            return;
        for (Iterator i = model.getGraphs().iterator(); i.hasNext(); ) {
            Entry  e = (Entry) i.next();
            Object o = e.getGraph();
            if (o instanceof Matrix)
                drawGraph(g, (Matrix) o, e.getAttributes());
            else if (o instanceof Function)
                drawGraph(g, (Function) o, e.getAttributes());
            else if (o instanceof orbital.math.functional.Function[])
                // convert to ComponentComposedFunction
                drawGraph(g, (orbital.math.functional.Function) Functionals.genericCompose((orbital.math.functional.Function[]) o), e.getAttributes());
            else
                throw new IllegalStateException("unsupported type to plot: "+o.getClass());
        } 
    } 



    /**
     * Y-Offset of x-axis.
     */
    private transient double x_axis;

    /**
     * X-Offset of y-axis.
     */
    private transient double y_axis;

    protected void window(Graphics2D g, double minx, double miny, double maxx, double maxy) {
        Dimension d = getSize();
        double    scalex = (double) (d.width - 0.) / (maxx - minx);
        double    scaley = (double) (d.height - 0.) / (maxy - miny);
        double    orgx = minx * scalex, orgy = miny * scaley;
        g.translate(orgx, orgy);
        g.scale(scalex, scaley);
    } 

    protected void setWindow(Arithmetic minxo, Arithmetic minyo, Arithmetic maxxo, Arithmetic maxyo) {
        double    minx = ((Number) minxo).doubleValue();
        double    miny = ((Number) minyo).doubleValue();
        double    maxx = ((Number) maxxo).doubleValue();
        double    maxy = ((Number) maxyo).doubleValue();
        Dimension d = getSize();
        scalex = (double) (d.width - 0.) / (maxx - minx);
        scaley = (double) (0. - d.height) / (maxy - miny);
        orgx = minx;
        orgy = maxy;
    } 

    private void calcSettings() {
        Range  range = model.getRange();
        Vector scale = model.getScale();
        x_axis = 0.0;
        if (((Real)range.min.get(1)).doubleValue() * ((Real)range.max.get(1)).doubleValue() > 0 || ((Real)range.min.get(1)).doubleValue() == x_axis || ((Real)range.max.get(1)).doubleValue() == x_axis) {        // offset for x-axis?
            if (((Real)range.min.get(1)).doubleValue() > 0)     // above 0
                x_axis = MathUtilities.ceily(((Real)range.min.get(1)).doubleValue() + ((Real)scale.get(1)).doubleValue(), MathUtilities.precisionFor(range.getLength(1)));
            else                                                                        // below 0
                x_axis = MathUtilities.floory(((Real)range.max.get(1)).doubleValue() - ((Real)scale.get(1)).doubleValue(), MathUtilities.precisionFor(range.getLength(1)));
        } 
        y_axis = 0.0;
        if (((Real)range.min.get(0)).doubleValue() * ((Real)range.max.get(0)).doubleValue() > 0) {      // offset for y-axis? !=0
            if (((Real)range.min.get(0)).doubleValue() > 0)     // above 0
                y_axis = MathUtilities.ceily(((Real)range.min.get(0)).doubleValue() + ((Real)scale.get(0)).doubleValue(), MathUtilities.precisionFor(range.getLength(0)));
            else                                                                        // below 0
                y_axis = MathUtilities.floory(((Real)range.max.get(0)).doubleValue() - ((Real)scale.get(0)).doubleValue(), MathUtilities.precisionFor(range.getLength(0)));
        } 
    } 

    private static final int MARK_RADIUS = 1;
    private static final int POINTER_RADIUS = 5;

    private static final int POINT_RADIUS = 2;

    private transient double orgx = 0;
    private transient double orgy = 0;
    private transient double scalex = 1.;
    private transient double scaley = 1.;

    /**
     * calculate screen coordinates of a virtual point.
     */
    private int sx(double xv) {
        return (int) (scalex * (xv - orgx));
    } 
    private int sy(double yv) {
        return (int) (scaley * (yv - orgy));
    } 
    private int sx(Arithmetic xv) {
        return sx(((Number) xv).doubleValue());
    } 
    private int sy(Arithmetic yv) {
        return sy(((Number) yv).doubleValue());
    } 

    /**
     * calculate virtual point of screen coordinates.
     */
    private double vx(int sx) {
        return ((double) sx / scalex + orgx);
    } 
    private double vy(int sy) {
        return ((double) sy / scaley + orgy);
    } 
    protected void drawAxis(Graphics g) {
        Range  range = model.getRange();
        Vector scale = model.getScale();
        g.setColor(Color.darkGray);

        // x-axis
        g.drawLine(sx(range.min.get(0)), sy(x_axis), sx(range.max.get(0)), sy(x_axis));

        // x-axis marks for scaling unit
        for (double x = y_axis - ((Real)scale.get(0)).doubleValue(); x > ((Real)range.min.get(0)).doubleValue(); x -= ((Real)scale.get(0)).doubleValue()) {
            g.drawLine(sx(x), sy(x_axis) - MARK_RADIUS, sx(x), sy(x_axis) + MARK_RADIUS);
        } 
        for (double x = y_axis + ((Real)scale.get(0)).doubleValue(); x < ((Real)range.max.get(0)).doubleValue(); x += ((Real)scale.get(0)).doubleValue()) {
            g.drawLine(sx(x), sy(x_axis) - MARK_RADIUS, sx(x), sy(x_axis) + MARK_RADIUS);
        } 

        // y-axis
        g.drawLine(sx(y_axis), sy(range.min.get(1)), sx(y_axis), sy(range.max.get(1)));

        // y-axis marks for scaling unit
        for (double y = x_axis - ((Real)scale.get(1)).doubleValue(); y > ((Real)range.min.get(1)).doubleValue(); y -= ((Real)scale.get(1)).doubleValue()) {
            g.drawLine(sx(y_axis) - MARK_RADIUS, sy(y), sx(y_axis) + MARK_RADIUS, sy(y));
        } 
        for (double y = x_axis + ((Real)scale.get(1)).doubleValue(); y < ((Real)range.max.get(1)).doubleValue(); y += ((Real)scale.get(1)).doubleValue()) {
            g.drawLine(sx(y_axis) - MARK_RADIUS, sy(y), sx(y_axis) + MARK_RADIUS, sy(y));
        } 

        // offset for axis? draw break-symbol
        if (x_axis != 0.0) {
            g.drawLine(sx(y_axis) - POINTER_RADIUS, sy(x_axis + ((Real)scale.get(1)).doubleValue() * 2 / 5) - 1, sx(y_axis) + POINTER_RADIUS, sy(x_axis + ((Real)scale.get(1)).doubleValue() * 3 / 5) - 1);
            g.drawLine(sx(y_axis) - POINTER_RADIUS, sy(x_axis + ((Real)scale.get(1)).doubleValue() * 2 / 5) + 1, sx(y_axis) + POINTER_RADIUS, sy(x_axis + ((Real)scale.get(1)).doubleValue() * 3 / 5) + 1);
        } 
        if (y_axis != 0.0) {
            g.drawLine(sx(y_axis + ((Real)scale.get(0)).doubleValue() * 2 / 5) - 1, sy(x_axis) + POINTER_RADIUS, sx(y_axis + ((Real)scale.get(0)).doubleValue() * 3 / 5) - 1, sy(x_axis) - POINTER_RADIUS);
            g.drawLine(sx(y_axis + ((Real)scale.get(0)).doubleValue() * 2 / 5) + 1, sy(x_axis) + POINTER_RADIUS, sx(y_axis + ((Real)scale.get(0)).doubleValue() * 3 / 5) + 1, sy(x_axis) - POINTER_RADIUS);
        } 

        FontMetrics fm = g.getFontMetrics();

        // x-axis scaling marks unit label
        double          o_xfinish = Double.NaN;
        for (double x = y_axis - ((Real)scale.get(0)).doubleValue(); x > ((Real)range.min.get(0)).doubleValue(); x -= ((Real)scale.get(0)).doubleValue()) {
            String label = MathUtilities.format(x);
            int    width = fm.stringWidth(label);
            int    xpos = sx(x);
            if (xpos + width / 2 > o_xfinish)
                continue;
            g.drawString(label, xpos - width / 2, sy(x_axis) + MARK_RADIUS + fm.getHeight());
            if (!isFullScaling() && (o_xfinish == o_xfinish))    // !NaN
                break;
            o_xfinish = xpos + width / 2;        // here label drawing was finished
        } 
        o_xfinish = Double.NaN;
        for (double x = y_axis + ((Real)scale.get(0)).doubleValue(); x < ((Real)range.max.get(0)).doubleValue(); x += ((Real)scale.get(0)).doubleValue()) {
            String label = MathUtilities.format(x);
            int    width = fm.stringWidth(label);
            int    xpos = sx(x);
            if (xpos - width / 2 < o_xfinish)
                continue;
            g.drawString(label, xpos - width / 2, sy(x_axis) + MARK_RADIUS + fm.getHeight());
            if (!isFullScaling() && (o_xfinish == o_xfinish))    // !NaN
                break;
            o_xfinish = xpos + width / 2;        // here label drawing was finished
        } 

        // font.deriveFont(new AffineTransform().rotate(theta));
        // y-axis scaling marks unit label
        double o_yfinish = Double.NaN;
        for (double y = x_axis - ((Real)scale.get(1)).doubleValue(); y > ((Real)range.min.get(1)).doubleValue(); y -= ((Real)scale.get(1)).doubleValue()) {
            String label = MathUtilities.format(y);
            int    height = fm.getHeight();
            int    ypos = sy(y);
            if (ypos - height / 2 < o_yfinish)
                continue;
            g.drawString(label, sx(y_axis) - MARK_RADIUS - fm.stringWidth(label), ypos + height / 2);
            if (!isFullScaling() && (o_yfinish == o_yfinish))    // !NaN
                break;
            o_yfinish = ypos + height / 2;        // here label drawing was finished
        } 
        o_yfinish = Double.NaN;
        for (double y = x_axis + ((Real)scale.get(1)).doubleValue(); y < ((Real)range.max.get(1)).doubleValue(); y += ((Real)scale.get(1)).doubleValue()) {
            String label = MathUtilities.format(y);
            int    height = fm.getHeight();
            int    ypos = sy(y);
            if (ypos + height / 2 > o_yfinish)
                continue;
            g.drawString(label, sx(y_axis) - MARK_RADIUS - fm.stringWidth(label), ypos + height / 2);
            if (!isFullScaling() && (o_yfinish == o_yfinish))    // !NaN
                break;
            o_yfinish = ypos + height / 2;        // here label drawing was finished
        } 

        // x-axis pointers
        g.drawLine(sx(range.max.get(0)) - POINTER_RADIUS, sy(x_axis) - POINTER_RADIUS, sx(range.max.get(0)), sy(x_axis));
        g.drawLine(sx(range.max.get(0)) - POINTER_RADIUS, sy(x_axis) + POINTER_RADIUS, sx(range.max.get(0)), sy(x_axis));

        // y-axis pointers
        g.drawLine(sx(y_axis) - POINTER_RADIUS, sy(range.max.get(1)) + POINTER_RADIUS, sx(y_axis), sy(range.max.get(1)));
        g.drawLine(sx(y_axis) + POINTER_RADIUS, sy(range.max.get(1)) + POINTER_RADIUS, sx(y_axis), sy(range.max.get(1)));
    } 

    protected void drawGraph(Graphics g, Matrix A, Map attribs) {
        if (A.dimension().width != 2)
            throw new IllegalStateException("Diagramm must contain 2D Matrix n by 2, only");
        g.setColor(Color.blue);
        if (attribs != null) {
            Object col = attribs.get("color");
            if (col != null)
                g.setColor((Color) col);
        } 
        for (int i = 0; i < A.dimension().height; i++) {
            g.drawLine(sx(A.get(i, 0)) - POINT_RADIUS, sy(A.get(i, 1)) - POINT_RADIUS, sx(A.get(i, 0)) + POINT_RADIUS, sy(A.get(i, 1)) + POINT_RADIUS);
            g.drawLine(sx(A.get(i, 0)) - POINT_RADIUS, sy(A.get(i, 1)) + POINT_RADIUS, sx(A.get(i, 0)) + POINT_RADIUS, sy(A.get(i, 1)) - POINT_RADIUS);
            g.drawLine(sx(A.get(i, 0)), sy(A.get(i, 1)), sx(A.get(i, 0)), sy(A.get(i, 1)));
        } 
    } 

    protected void drawGraph(Graphics g, Function f, Map attribs) {
        Range  range = model.getRange();

        g.setColor(Color.black);
        if (attribs != null) {
            Color col = (Color) attribs.get("color");
            if (col != null)
                g.setColor((Color) col);
        } 
        double precision = model.getSpecifiedPrecision(attribs, DEFAULT_PRECISION_POINTS);

        final Values vf = Values.getDefaultInstance();
        // will always contain next x|y value
        Vector n = applyFunction(f, range.min.get(0));
        for (double t = ((Real)range.min.get(0)).doubleValue(); t < ((Real)range.max.get(0)).doubleValue(); t += precision) {
            Vector v = n;
            n = applyFunction(f, vf.valueOf(t + precision));

            // TODO: check for NaN and do not draw then
            // TODO: think if it is useful to interpolate with cubical polynoms, ... here
            // perhaps we could already have replaced the actual function with interpolating polynomials and use that information here?
            g.drawLine(sx(v.get(0)), sy(v.get(1)), sx(n.get(0)), sy(n.get(1)));

            // n will fall through and continue to be used as new v, above
        } 
    } 

    private Vector applyFunction(Function f, Arithmetic arg) {
        Arithmetic value = (Arithmetic) f.apply(arg);
        if (value instanceof Vector) {
            Vector vector = (Vector) value;
            assert vector.dimension() == 2 : "Plot2D only handles 2-dimensional vectors";
            return vector;
        } else
            return Values.getDefaultInstance().valueOf(new Arithmetic[] {
                arg, value
            });
    } 
}
