import java.io.Serializable;

class ChessAspects implements Serializable {
    public double offensiveFigure = 0.1;			// offensive kind of figure
    public double beatingEnemies = 3.6;				// beating enemies now
    public double stormingOffensive = 1.0;			// storming to front and further
    public double topStorm = 0.01;					// storm from top flanke down
    public int	  maxDepth = 1;						// maximum search depth
}

