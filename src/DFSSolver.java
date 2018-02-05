import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Attempts to solve a Sokoban puzzle with DFS
 */
public class DFSSolver extends AbstractSolver {

	public DFSSolver(BoardState initialState) {
		super(initialState);
		queue = new LinkedList<BoardState>();
	}

	@Override
	protected void searchFunction(ArrayList<BoardState> validMoves) {
		for (BoardState move : validMoves) {
			backtrack.put(move, currentState);
			((LinkedList<BoardState>) queue).push(move);
		}
	}
}
