

import orbital.game.*;
import orbital.robotic.*;
import java.awt.Image;

public class ChessFigure extends Figure {
	private ChessFigure(Field fld, int x, int y, Direction dir, int leag, int typ, Image img, Move[] legalMoves) {
		super(fld, x, y, dir, leag, typ, img, legalMoves);
	}

	public ChessFigure(int x, int y, int leag, int typ) {
		super(x, y, leag, typ);
	}

	public Object clone() {
		return new ChessFigure(getField(), x, y, (Direction) direction.clone(), league, type, image, getLegalMoves());
	} 

	protected boolean moving(Move move, Position dst) {
		if (!super.moving(move, dst))
			return false;	 // super says valid Move?
        final ChessField field = (ChessField) getField();
		if (field.getTurn() != league)
			return false;	 // it's my turn at all?
		Figure to = field.getFigure(dst);

		if (!to.isEmpty()) {	 // is it beating?
			if (league == to.league)
				return false;	 // cannot beat allys

				// move is valid

				// clear opposite figure that is beaten
			to.setEmpty();
		} 
		field.rules.doTurn();
		return true;
	} 

	/**
	 * How far we already have stormed to the front.
	 * @return value ranging from 0 (at home) to dimension.width-1==7 (at front).
	 */
	public int front() {
        final ChessField field = (ChessField) getField();
		if (!field.inRange(this))
			throw new IllegalArgumentException("nonsense position to compare with: " + this + " not in range " + field.getDimension());
		if (league == ChessRules.BLACK)
			return x;
		if (league == ChessRules.WHITE)
			return field.getDimension().width - 1 - x;
		throw new IllegalStateException("league must exist");
	} 

	/**
	 * How far we would be storming to the front if we went to the specified position.
	 * @para pos the new position to be checked for forward/backward movement.
	 * @return value that is positive if its forward and negative if its backward.
	 */
	public int stormFront(Position pos) {
        final ChessField field = (ChessField) getField();
		if (!field.inRange(pos))
			throw new IllegalArgumentException("nonsense position to compare with: " + pos + " not in range " + field.getDimension());
		if (league == ChessRules.BLACK)
			return pos.x - x;
		if (league == ChessRules.WHITE)
			return x - pos.x;
		throw new IllegalStateException("wrong league - does not exist");
	} 
}
