

import java.awt.*;
import java.net.*;
import java.io.IOException;
import orbital.math.Point3D;
import orbital.awt.virtual.*;

public class VectorAnimation extends java.applet.Applet {

	/**
	 * The directory or URL from which the vectordatas are loaded
	 */
	protected URL			 vectorSource = null;

	/**
	 * Vector world .VecObj URL
	 */
	protected URL			 vectorWorldURL = null;

	/**
	 * Vector Animation .VecAni URL
	 */
	protected URL			 vectorAnimationURL = null;

	/**
	 * The default number of milliseconds to wait between frames.
	 */
	public static final int  DefaultPause = 1900;

	/**
	 * The global delay between images, which can be overridden by
	 * the PAUSE parameter.
	 */
	protected int			 globalPause;

	/**
	 * Repeat the animation?  If false, just play it once.
	 */
	protected boolean		 repeat;

	/**
	 * The default number of pixels the user distances from the screen.
	 */
	public static final int  DefaultDistance = 300;

	/**
	 * The default number of pixels the user distances from the screen.
	 * it can be overridden by the USERDISTANCE parameter.
	 */
	protected int			 userDistance;

	protected VectorAnimator animator;
	public void init() {
		try {
			String param = getParameter("VECTORSOURCE");
			vectorSource = (param == null) ? getDocumentBase() : new URL(getDocumentBase(), param + "/");

			param = getParameter("PAUSE");
			globalPause = (param != null) ? Integer.parseInt(param) : DefaultPause;

			param = getParameter("REPEAT");
			repeat = (param == null) ? true : (param.equalsIgnoreCase("yes") || param.equalsIgnoreCase("true"));

			param = getParameter("WORLD");
			if (param != null)
				vectorWorldURL = new URL(vectorSource, param);

			param = getParameter("ANIMATION");
			if (param != null)
				vectorAnimationURL = new URL(vectorSource, param);

			param = getParameter("USERDISTANCE");
			userDistance = (param != null) ? Integer.parseInt(param) : DefaultDistance;

			String	  projection = getParameter("PROJECTION");

			Dimension dim = size();
			v_Canvas  vCanvas = new v_Canvas(new Point3D(dim.width / 2, dim.height / 2, userDistance));
			if (projection != null)
				try {
					vCanvas.setGraphicsPrototype((v_Graphics) Class.forName(projection).newInstance());
                }
                catch(ClassNotFoundException x) {showError(x);}
                catch(InstantiationException x) {showError(x);}
                catch(IllegalAccessException x) {showError(x);}
			animator = new VectorAnimator(vectorWorldURL, vectorAnimationURL, vCanvas);
			animator.speed(globalPause);
			setLayout(new BorderLayout());
			add(new Button("Stop"), BorderLayout.NORTH);
			add(new Button("Slower"), BorderLayout.EAST);
			add(new Button("Faster"), BorderLayout.WEST);
			add(vCanvas, BorderLayout.CENTER);
		} catch (MalformedURLException e) {
			showParseError(e);
		} catch (IOException e) {
			showParseError(e);
		} 
	} 
	public void start() {
		if (repeat)
			animator.loop();
		else
			animator.play();
	} 

	public void stop() {
		animator.stop();
	} 

	public boolean action(Event event, Object arg) {
		if ("Stop".equals(arg))
			stop();
		else if ("Slower".equals(arg)) {
			globalPause += 40;
			animator.speed(globalPause);
		} else if ("Faster".equals(arg)) {
			globalPause -= 40;
			animator.speed(globalPause);
		} 
		return super.action(event, arg);
	} 

	void showParseError(Exception e) {
		String errorMsg = "VectorAnimation: Parse error: " + e;
		showStatus(errorMsg);
		System.err.println(errorMsg);
		repaint();
	} 
	void showError(Exception e) {
		String errorMsg = "VectorAnimation: " + e;
		showStatus(errorMsg);
		System.err.println(errorMsg);
		repaint();
	} 

	/**
	 * Info.
	 */
	public String getAppletInfo() {
		return "VectorAnimation by Andre Platzer";
	} 

	/**
	 * Parameter Info
	 */
	public String[][] getParameterInfo() {
    	String[][] info = {
            {"vectorsource",    "url",      "directory where the files reside"},
            {"world",       	"url",      "vector object world file"},
            {"animation",       "url",      "animation file"},
            {"userdistance",    "int",   	"distance of an imaginary viewer from screen in pixels"},
//            {"background",  "String",      "displayed as background"},
            {"pause",           "int",      "delay time in milliseconds between animation frames"},
            {"repeat",          "boolean",  "whether to repeat the animation"},
            {"projection",		"String",   "which 3D projection to use"}
    	};
		return info;
	} 

}
