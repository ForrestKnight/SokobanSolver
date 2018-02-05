
/**
 * Command interface for varying heuristics in greedy BFS and A*
 *
 * All implementing classes:
 * 		BoxGoalHeuristic
 * 		ManhattanHeuristic
 */
public interface Heuristic {

	public void score(BoardState state);

}
