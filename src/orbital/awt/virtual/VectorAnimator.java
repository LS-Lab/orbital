/*
 * @(#)VectorAnimator.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.awt.Color;
import java.io.*;
import java.net.URL;
import orbital.math.*;
import orbital.awt.Animation;

import orbital.moon.awt.virtual.VectorAnimationInterpreter;
import orbital.moon.io.ObjectDataInputStream;

/**
 * an Animator class that reads a world from a *.vec file and animates it
 * according to a *.ani file. It displays the world on a v_Canvas.
 * @version 0.9, 1996/03/02
 * @author  Andr&eacute; Platzer
 * @see <a href="../../../../examples/VectorAnimation/VectorAssembler.java">VectorAssembler utility</a>
 * @see <a href="doc-files/VecObj.txt">Vector object file format</a>
 */
public class VectorAnimator implements Animation, Runnable {
    protected v_Canvas			 vCanvas;
    protected VectorAnimationInterpreter animationFlow;
    private int				 delayTime = 500;
    private volatile boolean		 looping = false;
    private volatile Thread		 engine = null;

    /**
     * @param vectorWorld contains URL to the *.vec file
     * @param vectorAnimation contains URL to the *.ani file
     * @param vCanvas the canvas to display on
     */
    public VectorAnimator(URL vectorWorld, URL vectorAnimation, v_Canvas vCanvas) throws IOException {
	InputStream ifs = new BufferedInputStream(vectorWorld.openStream());
	v_View		view = readWorld(new ObjectDataInputStream(ifs));
	ifs.close();

	// if using sorted polygonlist at all
	view.initPolygonList();
	animationFlow = new VectorAnimationInterpreter(vectorAnimation);
	this.vCanvas = vCanvas;
	this.vCanvas.setWorld(view);
    }

    public void play() {
	animationFlow.start();
	engine = new Thread(this, "animation engine");
	//XXX: concurrent synchronization could be required since engine could have already changed again
	engine.start();
    } 

    public void stop() {
	looping = false;	// force run to exit
	Thread moribund = engine;
	engine = null;	  // engine.stop();
	if (moribund != null)
	    moribund.interrupt();
	// try {vCanvas.getWorld().writeExternal( new DataOutputStream(System.out) );}catch(IOException x) {x.printStackTrace();}
    } 

    public void loop() {
	looping = true;
	play();
    } 

    public void speed(int ms) {
	delayTime = ms;
    } 


    public v_Canvas getCanvas() {
	return vCanvas;
    } 

    public void run() {
	Thread thisThread = engine;
	try {
	    do {
		try {
		    while (engine == thisThread) {
			Matrix3D curMove = new Matrix3D();
			int		 steps = readAnimation(curMove);

			// System.out.println("Do "+curMove+" for "+steps+" times");

			for (; steps > 0 && engine == thisThread; steps--) {
			    long startTime = System.currentTimeMillis();
			    vCanvas.getWorld().move(curMove);
			    vCanvas.repaint();
			    long duration = startTime - System.currentTimeMillis();

			    // only wait the part of the delayTime that has not yet expired by calcs
			    if (delayTime - duration > 0)
				Thread.currentThread().sleep(delayTime - duration);
			} 
		    } 
		} catch (EOFException x) {
		    x.printStackTrace();
		} catch (InterruptedException irq) {}
		if (looping && engine == thisThread) {	  // nicht nochmal die start set positioning commands ausführen
		    animationFlow.start();
		    animationFlow.skipInitialMove();
		} 
	    } while (looping && engine == thisThread && !Thread.currentThread().isInterrupted());
	} catch (IOException x) {
	    x.printStackTrace();
	} 
	finally {
	    engine = null;
	} 
    } 


    /**
     * reads until execute command is found. returns nSteps
     */
    private int readAnimation(Matrix3D curMove) throws IOException {
	int steps;
	do {
	    if (!animationFlow.ready())
		return 0;
	    steps = animationFlow.interpretCommand(curMove);
	    if (steps < 0)
		speed(-steps);
	} while (steps <= 0);
	return steps;
    } 

    /**
     * reads the v_World from a .Vec file.
     */
    private v_View readWorld(ObjectInput is) throws IOException {
	try {
	    // object relational storage:
	    v_View world = new v_View();
	    //world.load(is);
	    world.readExternal(is);
	    return world;
    	}
    	catch (ClassNotFoundException x) {throw new java.io.StreamCorruptedException("illegal data stream");}

	// relational storage:

	/*
	 * v_View world;
	 * VectorGraphicsHeader header = new VectorGraphicsHeader();
	 * header.load(is);
	 * 
	 * world = new v_View(header.nObjects);
	 * for (short iObject=0;iObject<header.nObjects;iObject++) {
	 * VectorGraphicsObject object = new VectorGraphicsObject();
	 * object.load(is);
	 * 
	 * v_Object obj = new v_Object(object.nPolygons,object.nPoints);
	 * for (int iPoint=0;iPoint<object.nPoints;iPoint++) {
	 * VectorGraphicsPoint point = new VectorGraphicsPoint();
	 * point.load(is);
	 * 
	 * v_Vertex vertex = new v_Vertex(point.x,point.y,point.z);
	 * obj.setVertex(vertex,iPoint);
	 * }   // iPoints
	 * 
	 * for(int iPolygon=0;iPolygon<object.nPolygons;iPolygon++) {
	 * VectorGraphicsPolygon polygon = new VectorGraphicsPolygon();
	 * polygon.load(is);
	 * 
	 * v_Polygon poly = new v_Polygon(polygon.nPoints);
	 * poly.setColor(polygon.color);
	 * for(int iPointIndex=0;iPointIndex<polygon.nPoints;iPointIndex++) {
	 * int pointIndex = is.readShort();
	 * poly.setVertex(obj.getVertex(pointIndex),iPointIndex);
	 * }   // iPointIndexes
	 * obj.setPolygon(poly,iPolygon);
	 * }   // iPolygons
	 * world.setObject(obj,iObject);
	 * }   // iObjects
	 * return world;
	 */
    }	 // readWorld
 
}









//delegated into the Serializable objects

/**
 * File Datastructure representation of *.Vec files.
 */

/*
 * class VectorGraphicsHeader {
 * static VectorGraphicsHeader header = null;
 * int reserved;
 * byte id;                // ==0x3D
 * short version;          // in hex notation z.B. 0x0100
 * short HeaderSize;       // inkl. feld "HeaderSize"
 * short ObjectSize;       // groesse object header
 * short PolygonSize;      // groesse polygon header
 * boolean polysorted;     // whether uses polygonsorting
 * boolean z_buffered;     // whether displays on a ZBuffer
 * int ObjectOffset;       // offset zum ersten objekt (rel. zum dateianfang)
 * short nObjects;
 * 
 * //VectorGraphicsObject Objects[];
 * public void load(DataInput is) throws IOException {
 * reserved = is.readInt();
 * id = is.readByte();
 * if (id!=0x3D) throw new IOException("wrong VecObj 3D format");
 * version = is.readShort();
 * HeaderSize = is.readShort();
 * ObjectSize = is.readShort();
 * PolygonSize = is.readShort();
 * polysorted = is.readBoolean();
 * z_buffered = is.readBoolean();
 * ObjectOffset = is.readInt();
 * 
 * is.skipBytes( ObjectOffset - 19 );
 * nObjects = is.readShort();
 * header = this;
 * }
 * }
 * 
 * class VectorGraphicsObject {
 * short nPolygons;
 * short nPoints;
 * 
 * //VectorGraphicsPoint Points[];
 * //VectorGraphicsPolygon Polygons[];
 * public void load(DataInput is) throws IOException {
 * nPolygons = is.readShort();
 * nPoints = is.readShort();
 * 
 * is.skipBytes( VectorGraphicsHeader.header.ObjectSize - 4 );
 * }
 * }
 * 
 * class VectorGraphicsPoint {
 * short x;
 * short y;
 * short z;
 * public void load(DataInput is) throws IOException {
 * x = is.readShort();
 * y = is.readShort();
 * z = is.readShort();
 * }
 * }
 * 
 * class VectorGraphicsPolygon {
 * short nPoints;
 * Color color;
 * 
 * //short Points[];
 * public void load(DataInput is) throws IOException {
 * nPoints = is.readShort();
 * color = new Color( is.readInt() );
 * is.skipBytes( VectorGraphicsHeader.header.PolygonSize - 6 );
 * }
 * }
 */
