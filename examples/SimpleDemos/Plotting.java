import orbital.math.Vector;
import orbital.math.Matrix;
import orbital.math.Scalar;
import orbital.math.Values;
import orbital.math.functional.Function;
import orbital.math.functional.Functions;
import orbital.awt.*;
import java.awt.*;
import java.beans.*;
import javax.swing.*;

public class Plotting {
    public static void main(String arg[]) throws Exception {
        // get us a value factory for creating arithmetic objects
        final Values vf = Values.getDefaultInstance();
        double     data[][] = {
            {-3, -5},
            {1, 0},
            {2, 0},
            {3, 2},
            {4, 6},
            {5, 12}
        };
        Matrix     Ex = (Matrix) vf.valueOf(data);
        ChartModel model = (ChartModel) Beans.instantiate(Plotting.class.getClassLoader(), ChartModel.class.getName());
        model.setRainbow(true);
        model.add(Ex);

        // since we only want to plot a single function, its enough to write a logical function here
        // although we could as well implement a full mathematical function
        model.add(new orbital.logic.functor.Function() {
                public Object apply(Object arg) {
                    double x = ((Number) arg).doubleValue();
                    return vf.valueOf(2 - 3 * x + x * x);
                } 
            });

        // since we want a more sophisticated parametrical function here,
        // we use full mathematical functions
        model.add(new Function[] {
            Functions.cos, Functions.sin
        });
        model.setAutoScaling();
        model.setScale(vf.CONST(2, vf.valueOf(1)));
        System.out.println("Range: " + model.getRange() + " Scalings: " + model.getScale());
        JFrame f = new JFrame();
        f.setLayout(new BorderLayout());
        Plot2D view = (Plot2D) Beans.instantiate(Plotting.class.getClassLoader(), Plot2D.class.getName());
        view.setModel(model);
        view.setAutoScaling(true);
        view.addMouseListener(new CustomizerViewController(f));
        f.getContentPane().add(view, java.awt.BorderLayout.CENTER);
        f.setSize(300, 200);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    } 
}
