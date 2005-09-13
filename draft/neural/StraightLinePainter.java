import java.awt.*;
import dkfz.collections.graph.view.*;
import dkfz.collections.graph.*;

/**
 * Edges are painted with Arrows, or with crossbeams, depending on whether
 * the Interaction has positive or negative strength.
 * Edges are drawn as straight lines between two vertices.
 * @author Ute Platzer
 */
public class StraightLinePainter extends ViewGraph.DefaultPainter {

    /** whether the arrowhead shall be filled */
    protected boolean fill = false;
    protected double frontlength;
    protected double midlength;
    protected double backlength;
    protected double width;

    protected Shape paint(Graphics2D g, ViewEdge e) {
      	Vertex v1 = e.getEdge().getFromVertex();
      	Vertex v2 = e.getEdge().getToVertex();
      	ViewVertex w1 = (ViewVertex) e.getViewGraph().getView(v1);
      	ViewVertex w2 = (ViewVertex) e.getViewGraph().getView(v2);
    
      	Rectangle rectangle  = new Rectangle(0,0, e.getViewGraph().getWidth(),
      					     e.getViewGraph().getHeight());
    
      	double pos[] = new double[4];
      	double dim[] = new double[4];
      	//from vertex
      	pos[0] = XYConverter.X(w1.getX(),rectangle);
      	pos[1] = XYConverter.Y(w1.getY(),rectangle);
      	//to vertex
      	pos[2] = XYConverter.X(w2.getX(),rectangle);
      	pos[3] = XYConverter.Y(w2.getY(),rectangle);
    
      	dim[0] = w1.getBounds().width / 2;
      	dim[1] = w1.getBounds().height / 2;
      	dim[2] = w2.getBounds().width / 2;
      	dim[3] = w2.getBounds().height / 2;
    
      	if ( Math.abs(((Number) e.getEdge()
    		       .getObject()).doubleValue()) < 1.0E-3) {
      	    g.setColor(Color.gray);
      	    Shape shape = new ViewGraph.DefaultPainter.ThickLine
    		(pos[0], pos[1], pos[2], pos[3]);
    	    g.draw(shape);
      	    return shape;
      	}
    
      	double angle= calcAngle(pos[0], pos[1], pos[2], pos[3]);
      	//winkel von der mitte bis zur ecke in dem to-vertex:
      	double phi  = calcAngle(0.0, 0.0, dim[2], -dim[3]);
    
      	double a = 0;
      	double b = 0;
      	boolean test = false;
      	if ( ! (Math.abs(pos[2] - pos[0]) < 1.0E-3) ) {
      	    a = (pos[3] - pos[1]) / (pos[2] - pos[0]);
      	    b = pos[1] - a * pos[0];
      	    test = true;
      	}
      	double x = pos[2];
      	double y =  pos[3];
      	if ( angle < phi || angle > 2*Math.PI  - phi) {
      	    //rechts
      	    x = x - dim[2] -2;
      	    if ( test) y = a*x + b;
      	} else if (angle >= phi && angle < Math.PI - phi) {
      	    //oben
      	    y = y + dim[3] +2;
      	    if (test) x = (y-b)/a;
      	} else if (angle >= Math.PI - phi && angle < Math.PI + phi) {
      	    //links
      	    x = x + dim[2] + 2;
      	    if (test) y = a*x + b;
      	} else {
      	    //unten
      	    y = y - dim[3] -2;
      	    if (test) x = (y-b)/a;
      	}
    
      	g.setColor(Color.blue);
    	Shape shape = new ViewGraph.DefaultPainter.ThickLine
    	    (pos[0],pos[1], pos[2], pos[3]);
      	g.draw(shape);
    
      	double factor = 8.0;
      	if ( ((Number) e.getEdge().getObject()).doubleValue() > 0) {
      	    this.frontlength = factor * 1;
      	    this.midlength   = factor * 0;
      	    this.backlength  = factor * 0;
      	    this.width       = factor * 1;
      	    this.fill = true;
      	} else {
      	    this.frontlength = factor * 0;
      	    this.midlength   = factor * .3;
      	    this.backlength  = factor * 0;
      	    this.width       = factor * 1;
      	    this.fill = true;
      	}
      	drawArrow(g, x, y, angle);
    
    	//write strength 
    	g.drawString("" + e.getEdge().getObject(), 
    		     (int)((pos[0]+pos[2])*.5),(int)((pos[1]+pos[3])*.5)); 
    
    	return shape;
    }

    private double calcAngle(double x1, double y1, double x2, double y2) {
      	double angle = 0;
      	if (Math.abs(x2- x1) < 1.0E-3) {
      	    if (y2 < y1) angle = Math.PI * .5;
      	    else angle = Math.PI *1.5;
      	} else if (Math.abs(y2-y1)<1.0E-3)  {
      	    if (x2 < x1) angle = Math.PI;
      	    else angle = 0;
      	} else {
      	    double bruch = (y1 - y2)  / (x2 - x1 );
      	    angle = Math.atan( bruch );
      	    //problem: the angle that the atan function returns is
      	    //only between -pi/2 and +pi/2, not between 0 and 2pi.
      	    //solution: if x(from) > x(to) then angle = angle + pi
      	    if ( x1 > x2 ) angle = angle + Math.PI;
      	    while (angle < 0) {angle = angle + 2*Math.PI;}
      	}
      	return angle;
    }

    /**
     * @param g the Graphics object where the arrow is to be drawn
     * @param x the x value of the tip of the arrow
     * @param y the y value of the tip of the arrow
     * @param angle the angle about which the arrow is rotated with
     * respect to the x axis (0...2PI)
     * @param arrowParams array with arrow parameters.
     * @param fill if the arrow is to be filled
     */
    protected void drawArrow( Graphics g, double x, double y, double angle) {
      	try {
      	    double sin = Math.sin(angle);
      	    double cos = Math.cos(angle);
      	    //calculate left and right side points (seen in arrow direction)
      	    //l„ngen:
      	    double l[] = new double[3];
      	    l[0] = frontlength;
      	    l[1] = midlength;
      	    l[2] = backlength;
      	    /**
      	     * punkte des polygons:
      	     * 0 = Pfeilspitze, 1 = vordere linke ecke
      	     * 2 = hintere linke ecke, 3 = hinteres ende
      	     * 4 = hintere rechte ecke, 5 = vordere rechte ecke
      	     */
      	    double xp[] = new double[6];
      	    double yp[] = new double[6];
      	    xp[0] = x;
      	    yp[0] =  y;
    
      	    for (int i=1; i< xp.length; i++) {
      		xp[i] = xp[0];
      		yp[i] = yp[0];
      	    }
    
    
      	    //f’r die linken punkte:
      	    double dx = - sin * width;
      	    double dy = - cos * width;
      	    xp[3] = xp[3] - dx;
      	    yp[3] = yp[3] - dy;
      	    for (int i=1; i<=3; i++) {
      		dx = dx - cos * l[i-1];
      		dy = dy + sin * l[i-1];
      		xp[i] = xp[i] + dx;
      		yp[i] = yp[i] + dy;
      	    }
      	    //f’r die rechten punkte
      	    dx =  sin * width;
      	    dy =  cos * width;
      	    for (int i=5; i>= 4; i--) {
      		dx = dx - cos * l[5-i];
      		dy = dy + sin * l[5-i];
      		xp[i] = xp[i] + dx;
      		yp[i] = yp[i] + dy;
      	    }
      	    //System.out.println("angle " + angle );
      	    //System.out.println(" l1 " + l1 + " h1 " + h1 + " l2 " + l2
      	    //+ " h2 " + h2 );
      	    //System.out.println(" tip " + x + ", " + y + " leftPoint "
      	    // + leftPointX + ", " + leftPointY);
    
      	    //converting exerything to (int) :
      	    int intx[] = new int[xp.length];
      	    int inty[] = new int[yp.length];
      	    for (int i=0; i<xp.length; i++) {
      	        intx[i] = (int) xp[i];
      	        inty[i] = (int) yp[i];
      	    }
      	    //draw
      	    if (fill) {
      		Polygon p = new Polygon( intx, inty, intx.length);
      		g.fillPolygon(p);
      	    } else {
      		for (int i=0; i<6; i++) {
      		    /*
      		      System.out.println(" i = " + i +", (i+1)mod = "
      		      + ((i+1) % 6));
      		      System.out.println("drawing arrow line from "
      		      + intx[i] +", " + inty[i] +" to "
      		      + intx[ (i+1) % 6 ] + ", " + inty[ (i+1) % 6 ]);
      		    */
      		    g.drawLine( intx[i], inty[i], intx[ (i+1) % 6 ],
      				inty[ (i+1) % 6 ]);
      		}
      	    }
      	} catch (ArithmeticException e) {
      	    e.printStackTrace();
      	    System.exit(0);
      	}
    }
}
