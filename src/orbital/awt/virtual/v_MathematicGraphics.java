/*
 * @(#)v_MathematicGraphics.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.awt.Graphics;
import orbital.math.Point3D;

/**
 * Mathematical non-perspective projection graphics.
 * 
 * @version 0.9, 03/02/96
 * @author  Andr&eacute; Platzer
 */
public
class v_MathematicGraphics extends v_Graphics {
	//XXX: how to specify the distance or size or anything that says how near a visitor really is?
	//TODO: document and translate to english
	/**
	 * perspektivische Verk&uuml;rzung der Z-Achse.
	 */
	protected double kuerz;
	/**
	 * Radian Winkel der Z-Achse zur  X-Achse.
	 */
	protected double winkel;
	/**
	 * Scaling factor x direction.
	 */
	protected double scalx = 20;
	/**
	 * Scaling factor y direction.
	 */
	protected double scaly = 20;
	/**
	 * x-multiplier and y-multiplier, consits of kuerz and cos/sin winkel.
	 */
	private double   xmul, ymul;

	/**
	 * Constructs a new v_Graphics display being 2D projected to and
	 * displayed on real Graphics gr.
	 * It uses the Origin (0|0|0) of origin.
	 * @param gr the inner graphics used to display.
	 * @param origin which point to use as origin (0|0|0).
	 */
	public v_MathematicGraphics(Graphics gr, Point3D origin, double kuerz, double winkel) {
		super(gr, origin);
		setSight(kuerz, winkel);
	}
	public v_MathematicGraphics(Graphics gr, double kuerz, double winkel) {
		super(gr);
		setSight(kuerz, winkel);
	}
	public v_MathematicGraphics() {
		setSight(1.5, Math.PI/4);
	}
    
    public Object clone() {
    	return new v_MathematicGraphics(gr, origin, kuerz, winkel);
    }
	
	public void setSight(double kuerz, double winkel) {
		this.kuerz = kuerz;
		this.winkel = winkel;
		xmul = kuerz * Math.cos(winkel);
		ymul = kuerz * Math.sin(winkel);
	} 


	/**
	 * returns the screen coordinates (x|y) of a 3D Point (x|y|z).
	 */
	protected int xlate(int vx, int _vy, int vz) {
		return (int) ((-vx + vz * xmul) * scalx + origin.x);
	} 
	protected int ylate(int _vx, int vy, int vz) {
		return (int) ((+vy + vz * ymul) * scaly + origin.y);
	} 

}