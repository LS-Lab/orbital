/*
 * @(#)v_Canvas.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.awt.*;
import orbital.math.*;

/*
 * comment
 * <i><b>Evolves:</b>Optimize</i> possible Optimizations:
 * - suppress new Allocation via createImage
 * - QuickSort!
 * - Vector --> Array
 * - Sin[] Table
 * + BufferedInput/Output
 * - backface removal
 * - fixedpoint arithmetic
 * - zbuffer with fast line algo
 */

/**
 * a v_Canvas to display a v_View.
 */
public
class v_Canvas extends Canvas {
	private static final long serialVersionUID = -8388327611743054686L;

	/**
	 * @serial
	 */
	protected Point3D		  origin;

	/**
	 * @serial
	 */
	protected v_View		  view;

	/**
	 * The graphics prototype, or null.
	 * @serial
	 */
	private v_Graphics		  prototype;

	public v_Canvas(v_View view, Point3D origin) {
		this.view = view;
		this.origin = origin;
		setBackground(Color.black);
	}
	public v_Canvas(Point3D origin) {
		this(null, origin);
	}
	public v_Canvas(v_View view) {
		this(view, null);
	}
	public v_Canvas() {
		this(null, null);
	}

	public Point3D getOrigin() {
		return origin;
	} 
	public void setOrigin(Point3D origin) {
		this.origin = origin;
	} 

	public v_View getWorld() {
		return view;
	} 
	public void setWorld(v_View world) {
		view = world;
	} 
	
	/**
	 * Set the graphics prototype that determines which kind of v_Graphics to use for displaying.
	 * @param prototype a prototype object from which to clone a copy to draw on.
	 *  The concrete 2D graphics context and origin will be set for the clone, only.
	 */
	public void setGraphicsPrototype(v_Graphics prototype) {
		this.prototype = prototype;
	}

	public void update(Graphics g) {
		Rectangle bounds = new Rectangle(getSize());
		Image	  offScrImage = createImage(bounds.width, bounds.height);
		Graphics  offScrGC = offScrImage.getGraphics();

		// double buffering {

		offScrGC.setColor(getBackground());
		offScrGC.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		offScrGC.setColor(getForeground());

		v_Graphics vg;
		if (prototype != null) {
			vg = (v_Graphics) prototype.clone();
			vg.setOrigin(origin);
			vg.setGraphics(offScrGC);
		} else if (v_View.z_buffered)
			vg = new v_ZGraphics(offScrGC, origin);
		else
			vg = new v_PerspectiveGraphics(offScrGC, origin);

		/*
		 * vg.setColor(Color.green);
		 * for (int z=150,x=-50;z>0&&x<50;z--,x++) {
		 * vg.drawLine(x,-20,50 ,x,20,50);
		 * }
		 */
		view.display(vg);

		offScrGC.dispose();

		// } double buffering
		g.drawImage(offScrImage, 0, 0, null);
		offScrImage.flush();
	} 

	public void paint(Graphics g) {
		update(g);
	} 
}
