import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Attempts to solve a Sokoban puzzle with BFS
 */
public class BFSSolver extends AbstractSolver {

	public BFSSolver(BoardState initialState) {
		super(initialState);
		queue = new LinkedList<BoardState>();
	}

	@Override
	protected void searchFunction(ArrayList<BoardState> validMoves) {
		for (BoardState move : validMoves) {
			backtrack.put(move, currentState);
			queue.add(move);
		}
	}
}
