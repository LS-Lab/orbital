/*
 * @(#)Point2D.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

/**
 * An x,y coordinate.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see java.awt.Point
 * @see Vector
 * @deprecated since 1.0 use more general orbital.math.Vector instead.
 */
public
class Point2D {
        public int x;
        public int y;
        public Point2D() {}
        public Point2D(int x, int y) {
                this.x = x;
                this.y = y;
        }
        public Point2D(Point2D B) {
                x = B.x;
                y = B.y;
        }

        public boolean equals(Object o) {
                if (!(o instanceof Point2D))
                        return false;
                Point2D B = (Point2D) o;
                return x == B.x && y == B.y;
        } 
        
        public int hashCode() {
                return x ^ y;
        }

        public String toString() {
                return getClass().getName() + "[" + MathUtilities.format(x) + '|' + MathUtilities.format(y) + "]";
        } 
}
