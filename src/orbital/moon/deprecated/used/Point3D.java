/*
 * @(#)Point3D.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

/**
 * An x,y,z coordinate.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Vector
 * @see Point2D
 * @deprecated since 1.0 use more general orbital.math.Vector instead.
 */
public
class Point3D {
        public int x;
        public int y;
        public int z;
        public Point3D() {}
        public Point3D(int x, int y, int z) {
                this.x = x;
                this.y = y;
                this.z = z;
        }
        public Point3D(Point3D B) {
                x = B.x;
                y = B.y;
                z = B.z;
        }

        public boolean equals(Object o) {
                if (!(o instanceof Point3D))
                        return false;
                Point3D B = (Point3D) o;
                return x == B.x && y == B.y && z == B.z;
        } 

        public int hashCode() {
                return x ^ y ^ z;
        }

        public String toString() {
                return "(" + MathUtilities.format(x) + '|' + MathUtilities.format(y) + '|' + MathUtilities.format(z) + ")";
        } 
}
