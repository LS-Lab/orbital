/**
 * @(#)DirectionTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

import junit.framework.*;

/**
 * A sample test case, testing.
 * @version $Id$
 */
public class DirectionTest extends check.TestCase {
    private Direction dir;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(DirectionTest.class);
    }
    protected void setUp() {
    }

    public void testTurnLeft() {
        dir = new Direction(Direction.East);
        rtest(Direction.Left, 5);
        assertEquals(dir, new Direction(Direction.North));
    }
    public void testTurnRight() {
        dir = new Direction(Direction.East);
        rtest(Direction.Right, 5);
        assertEquals(dir, new Direction(Direction.South));
    }
    public void testTurnBack() {
        dir = new Direction(Direction.East);
        rtest(Direction.Back, 3);
        assertEquals(dir, new Direction(Direction.West));
    }
    public void testTurnFor() {
        dir = new Direction(Direction.East);
        rtest(Direction.For, 5);
        assertEquals(dir, new Direction(Direction.East));
    }
    public void testTurnHalfLeft() {
        dir = new Direction(Direction.East);
        rtest(Direction.HalfLeft, 9);
        assertEquals(dir, new Direction(Direction.NorthEast));
    }
    public void testTurnHalfRight() {
        dir = new Direction(Direction.East);
        rtest(Direction.HalfRight, 9);
        assertEquals(dir, new Direction(Direction.SouthEast));
    }
    private void rtest(int tdir, int count) {
        System.out.println(dir);
        for (int i = 0; i < count; i++) {
            dir.turn(tdir);
            System.out.println("-" + Direction.nameOfRelative(tdir) + "->" + dir);
        } 
    } 
}
