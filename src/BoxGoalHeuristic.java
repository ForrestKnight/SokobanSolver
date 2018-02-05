import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class BoxGoalHeuristic implements Heuristic {

	@Override
	public void score(BoardState state) {
		Set<Point> goals = state.getGoals();
		Set<Point> boxes = state.getBoxes();

		Set<Point> intersection = new HashSet<Point>(goals);
		intersection.retainAll(boxes);

		// Difference because lower costs are better
		state.setCost(goals.size() - intersection.size());
	}

}
