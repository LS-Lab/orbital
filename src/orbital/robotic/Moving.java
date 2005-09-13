/*
 * @(#)Moving.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

/**
 * Moving class is a Position keeping track of its Direction.
 * It is able to move and translate into a certain Direction, additionally.
 * It interprets Moves.
 * 
 * @structure inherit Position
 * @structure inherit Direction
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class Moving extends Position {
    private static final long serialVersionUID = 4953088411018850978L;
    /**
     * The current direction of this Moving Object.
     * @serial
     */
    private Direction direction;

    public Moving(int x, int y, Direction dir) {
        super(x, y);
        this.direction = dir;
    }
    public Moving(Position p, Direction dir) {
        this(p.x, p.y, dir);
    }
    public Moving(int x, int y, int dir) {
        this(x, y, new Direction(dir));
    }
    public Moving(Position p, int dir) {
        this(p.x, p.y, new Direction(dir));
    }
    public Moving(int x, int y) {
        this(x, y, new Direction(Direction.North));
    }
    public Moving(Position p) {
        this(p.x, p.y);
    }
    /**
     * Copy constructor.
     */
    public Moving(Moving p) {
        this(p.x, p.y, new Direction(p.getDirection()));
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction newDirection) {
        this.direction = newDirection;
    }

    /**
     * Creates a clone of the object. A new instance is allocated and a
     * copied clone of the current object is placed in the new object.
     * @return          a clone of this Object.
     * @postconditions RES.getClass() == getClass()
     * @throws  OutOfMemoryError If there is not enough memory.
     */
    public Object clone() {
        //return new Moving(x, y, (Direction) direction.clone());
        Moving c = (Moving) super.clone();
        c.setDirection((Direction) getDirection().clone());
        return c;
    } 

    /**
     * Checks whether two Moving things are the same. In the same state (position and direction).
     */
    public boolean equals(Object obj) {
        if (obj instanceof Moving)
            return super.equals(obj) && direction.equals(((Moving) obj).direction);
        return false;
    } 
        
    public int hashCode() {
        return super.hashCode() ^ direction.hashCode();
    }

    /**
     * Slide vertically into a specified absolute Direction whereas
     * the current Direction is kept.
     * @see Direction#getDirectionVector()
     * @todo optimize hotspot by table lookup of (&Delta;x,&Delta;y) with index (dir/45)?
     * @xxx should we better call setLocation(...)
     */
    public void slideDirection(final int dir) {
        // faster version of: translate(new Direction(dir).getDirectionVector());
        switch (dir) {    // move(x,y) :-(
        case Direction.East:
            x++;
            break;
        case Direction.SouthEast:
            x++;
            y++;
            break;
        case Direction.South:
            y++;
            break;
        case Direction.SouthWest:
            y++;
            x--;
            break;
        case Direction.West:
            x--;
            break;
        case Direction.NorthWest:
            y--;
            x--;
            break;
        case Direction.North:
            y--;
            break;
        case Direction.NorthEast:
            y--;
            x++;
            break;
        default:
            throw new IllegalStateException("Direction is invalid: " + dir);
        }
    } 

    /**
     * Slide vertically into a specified relative Direction whereas
     * the current Direction is kept.
     * @todo optimize since hotspot
     */
    public void slide(int dir) {
        /*
         * {
         * Direction step = (Direction)direction.clone();
         * step.turn(dir);
         * slideDirection(step.direction);
         * }
         */
        slideDirection(direction.getTurned(dir));        // optimized
    } 


    /**
     * Moves this moving thing a single step.
     * Basic central method interpreting Moves.
     * @see Move
     * @todo optimize since hotspot
     * @xxx should we better call setDirection(...)
     */
    public void move(final char mv) {
        switch (mv) {
            // special moves
        case Move.Rest:
            break;
        case Move.Teleport:
            break;
        case Move.Jumping:
            break;
        case Move.Sloping: /* //@TODO next char will not count as a step */
            break;
        case Move.Beating:
            break;

            // ordinary relative moves
        case Move.Right:
            direction.turn(Direction.Right);
            break;
        case Move.RightW:
            slide(Direction.Right);
            break;
        case Move.For:
            direction.turn(Direction.For);
            break;
        case Move.ForW:
            slide(Direction.For);
            break;
        case Move.Left:
            direction.turn(Direction.Left);
            break;
        case Move.LeftW:
            slide(Direction.Left);
            break;
        case Move.Back:
            direction.turn(Direction.Back);
            break;
        case Move.BackW:
            slide(Direction.Back);
            break;

            // ordinary absolute moves
        case Move.East:
            direction.setDirection(Direction.East);
            break;
        case Move.EastW:
            slideDirection(Direction.East);
            break;
        case Move.North:
            direction.setDirection(Direction.North);
            break;
        case Move.NorthW:
            slideDirection(Direction.North);
            break;
        case Move.West:
            direction.setDirection(Direction.West);
            break;
        case Move.WestW:
            slideDirection(Direction.West);
            break;
        case Move.South:
            direction.setDirection(Direction.South);
            break;
        case Move.SouthW:
            slideDirection(Direction.South);
            break;
        default:
            throw new IllegalArgumentException("Illegal move character: '" + mv + "'");
        }
    } 
    public void move(final String movement) {
        for (int i = 0; i < movement.length(); i++) {
            /*
             * if (movement.charAt(i)==Move.All) {
             * move(new Movement("e"+mv.movement.substring(i+1,mv.movement.length())));
             * move(new Movement("n"+mv.movement.substring(i+1,mv.movement.length())));
             * move(new Movement("w"+mv.movement.substring(i+1,mv.movement.length())));
             * move(new Movement("s"+mv.movement.substring(i+1,mv.movement.length())));
             * }
             * if (mv.movement.charAt(i)==Move.AllW) {
             * move(new Movement("E"+mv.movement.substring(i+1,mv.movement.length())));
             * move(new Movement("N"+mv.movement.substring(i+1,mv.movement.length())));
             * move(new Movement("W"+mv.movement.substring(i+1,mv.movement.length())));
             * move(new Movement("S"+mv.movement.substring(i+1,mv.movement.length())));
             * }
             */
            move(movement.charAt(i));
        } 
    } 

    public final void move(final Move movement) {
        move(movement.getMovementString());
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
        return getClass().getName() + "[" + x + '|' + y + ' ' + direction.toString() + "]";
    } 
}
