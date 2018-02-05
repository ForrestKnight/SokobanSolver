import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Attempts to solve a Sokoban puzzle with greedy best-first search
 */
public class GreedyBFSSolver extends AbstractSolver {
	private Heuristic heuristic;

	private GreedyBFSSolver(BoardState initialState) {
		super(initialState);
		queue = new PriorityQueue<BoardState>();
	}

	public GreedyBFSSolver(BoardState initialState, Heuristic heuristic) {
		this(initialState);
		this.heuristic = heuristic;
	}

	@Override
	protected void searchStart() {
		super.searchStart();
		heuristic.score(currentState);
	}

	@Override
	protected void searchFunction(ArrayList<BoardState> validMoves) {
		for (BoardState move : validMoves) {
			backtrack.put(move, currentState);
			heuristic.score(move);
			if (move.getCost() < currentState.getCost()) {
				queue.add(currentState);
				queue.add(move);
				break;
			}
			queue.add(move);
		}
	}


}
