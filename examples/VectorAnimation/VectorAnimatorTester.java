

import java.awt.*;
import java.net.URL;
import orbital.math.*;
import orbital.awt.virtual.*;
import orbital.awt.Closer;

/**
 * Unfortunately this program has a bug. Use full applet VectorAnimation instead.
 */
public class VectorAnimatorTester {
	public static void main(String argv[]) throws Exception {
		Frame	 f = new Frame("VectorAnimator Tester");
		v_Canvas vCanvas = new v_Canvas(new Point3D(300, 200, 400));
		f.add("Center", vCanvas);
		new Closer(f, true, true);
		f.resize(600, 400);
		VectorAnimator animator = new VectorAnimator(new URL("file:a.vec"), new URL("file:show.ani"), vCanvas);
		animator.speed(200);
		f.show();
		animator.loop();
	} 
}
