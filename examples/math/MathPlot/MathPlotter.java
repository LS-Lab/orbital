import orbital.awt.Plot2D;
import orbital.awt.ChartModel;
import orbital.awt.Range;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import orbital.awt.CustomizerViewController;

import orbital.math.functional.Function;
import orbital.math.Scalar;

import orbital.math.functional.Functions;

import orbital.moon.logic.MathExpressionSyntax;
import orbital.logic.sign.ParseException;
import orbital.logic.trs.Substitution;
import orbital.logic.trs.Substitutions;
import orbital.math.Symbol;
import orbital.math.Vector;
import orbital.math.Values;

import java.util.Arrays;

/**
 * More sophisticated mathematical plotting engine with expression parser.
 *
 * a nice example to start with is
 *    1 / cos(1/x*4)
 * with x and y ranging from -10 to 10.
 */
public class MathPlotter extends java.applet.Applet {
    public static void main(String args[]) throws Exception {
        orbital.moon.awt.AppletFrame.showApplet(new MathPlotter(), "Mathematical Plotting Engine", args);
    }

    private static final Values valueFactory = Values.getDefaultInstance();

    private ChartModel model;
    private Plot2D view;

    private TextField expression;
    
    public void init() {
        setBackground(Color.white);
        setLayout(new BorderLayout());
        
        try {
            //model = (ChartModel) Beans.instantiate(getClass().getClassLoader(), ChartModel.class.getName());
            model = new ChartModel();
            String param = getParameter("rainbow");
            model.setRainbow((param == null) ? true : (param.equalsIgnoreCase("yes") || param.equalsIgnoreCase("true")));
                
            //view = (Plot2D) Beans.instantiate(getClass().getClassLoader(), Plot2D.class.getName());
            view = new Plot2D();
            view.setModel(model);
            view.addMouseListener(new CustomizerViewController(/*cheat a bit*/new Frame()));
        
            Panel graph = new Panel();
            graph.setLayout(new BorderLayout());
            Button c;
            graph.add(c = new Button("^"), BorderLayout.NORTH);
            c.addActionListener(createIncreaseAction(0, +0.5));
            graph.add(c = new Button("v"), BorderLayout.SOUTH);
            c.addActionListener(createIncreaseAction(0, -0.5));
            graph.add(c = new Button("<"), BorderLayout.WEST);
            c.addActionListener(createIncreaseAction(-0.5, 0));
            graph.add(c = new Button(">"), BorderLayout.EAST);
            c.addActionListener(createIncreaseAction(+0.5, 0));
            graph.add(view, BorderLayout.CENTER);
            add(graph, BorderLayout.CENTER);
            Panel control = new Panel();
            control.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            control.add(new Label("Function:"));
            control.add(expression = new TextField(30));
            ActionListener fire = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        set(expression.getText());
                    }
                };
            expression.addActionListener(fire);
            control.add(c = new Button("draw"));
            c.addActionListener(fire);
            control.add(c = new Button("clear"));
            c.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        model.removeAll();
                    }
                });
            control.add(c = new Button("derive"));
            c.setEnabled(false);
            add(control, BorderLayout.SOUTH);

            param = getParameter("function");
            expression.setText((param == null) ? "sin(2*x)" : param);
            set(expression.getText());
        }
        catch (Exception x) {
            showStatus(x.toString());
            x.printStackTrace();
        }
    }
    
    protected void set(String expr) {
        try {
            Object p = new MathExpressionSyntax().createMathExpression(expr);
            System.out.println("Parsed function:\t" + p);
            if (!(p instanceof orbital.logic.functor.Function))
                // convert constants like 3 to constant functions if necessary
                p = Functions.constant(p);
            Object f = Substitutions.lambda(Functions.constant(valueFactory.symbol("x")), p);
            System.out.println("Replaced with:\t" + f);
            model.add(f);
            model.setAutoScaling();
            view.setModel(model);
        }
        catch (ParseException x) {
            showStatus(x.toString());
            x.printStackTrace();
        }
    }

        
    private ActionListener createIncreaseAction(final double deltax, final double deltay) {
        return new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    increase(deltax, deltay);
                }
            };
    }

    private void increase(double deltax, double deltay) {
        Vector delta = valueFactory.valueOf(new double[] {deltax, deltay});
        Range r = (Range) model.getRange().clone();
        r.min = r.min.subtract(delta);
        r.max = r.max.add(delta);
        model.setRange(r);
    }

    /**
     * Info.
     */
    public String getAppletInfo() {
        return "Mathematical Plotting Engine by Andre Platzer";
    } 

    /**
     * Parameter Info
     */
    public String[][] getParameterInfo() {
        String[][] info = {
            {"function",    "string",      "the function expression to display"},
            {"rainbow",     "boolean",     "whether to use rainbow colors"},
        };
        return info;
    } 
}
