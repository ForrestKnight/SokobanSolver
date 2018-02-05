import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Attempts to solve a Sokoban puzzle with uniform cost search, having push as cost 2 and move as cost 1
 */

public class UniformCostSolver extends AbstractSolver {

	public UniformCostSolver(BoardState initialState) {
		super(initialState);
		queue = new PriorityQueue<BoardState>();
	}

	@Override
	protected void searchFunction(ArrayList<BoardState> moves) {
		for (BoardState move : moves) {
			backtrack.put(move, currentState);
			uniformCostFunction(move, currentState.getCost());
			queue.add(move);
		}
	}

	private void uniformCostFunction(BoardState state, int baseCost) {
		if (currentState.nextMoveHas(BoardState.BOX, state.getDirectionTaken()))
			state.setCost(baseCost + 2);
		else
			state.setCost(baseCost + 1);
	}
}
