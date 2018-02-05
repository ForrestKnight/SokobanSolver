import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents a single Sokoban BoardState
 */
public class BoardState implements Comparable<BoardState> {
	// Board position bitfields
	public static final byte PLAYER = 1 << 0;
	public static final byte WALL = 1 << 1;
	public static final byte BOX = 1 << 2;
	public static final byte GOAL = 1 << 3;
	// Character to bitfield bimapping
	private static HashMap<Character, Byte> charToField;
	private static HashMap<Byte, Character> fieldToChar;
	static {
		charToField = new HashMap<Character, Byte>();
		charToField.put('0', WALL);
		charToField.put('S', GOAL);
		charToField.put('R', PLAYER);
		charToField.put('+', (byte) (PLAYER | GOAL));
		charToField.put('B', BOX);
		charToField.put('*', (byte) (BOX | GOAL));
		charToField.put(' ', (byte) 0);

		fieldToChar = new HashMap<Byte, Character>();
		for (Entry<Character, Byte> entry : charToField.entrySet()) {
			fieldToChar.put(entry.getValue(), entry.getKey());
		}
	}

	private byte[][] board;
	private Point player;
	private Set<Point> goals;
	private Set<Point> boxes;
	private Point directionTaken;
	private int cost;

	public BoardState(byte[][] board, Point player, Set<Point> goals,
			Set<Point> boxes) {
		this(board, player, goals, boxes, null);
	}

	public BoardState(byte[][] board, Point player, Set<Point> goals,
			Set<Point> boxes, Point direction) {
		this.board = board;
		this.player = player;
		this.goals = goals;
		this.boxes = boxes;
		this.directionTaken = direction;
		cost = 0;
	}

	public boolean isSolved() {
		for (Point p : goals) {
			if (!(pointHas(p.x, p.y, GOAL) && pointHas(p.x, p.y, BOX))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether or not the player can move in a certain direction
	 * @param direction the row/col direction
	 * @return True if player can move, false otherwise
	 */
	public boolean canMove(Point direction) {
		Point newPos = new Point(player.x + direction.x, player.y + direction.y);
		Point oneOutPos = new Point(newPos.x + direction.x, newPos.y + direction.y);
		if (pointHas(newPos, BOX)) {
			// Box can't be pushed if there's a wall or box
			if (pointHas(oneOutPos, WALL) || pointHas(oneOutPos, BOX))
				return false;
			// Shouldn't ever happen
//			else if (pointHas(oneOutPos, PLAYER))
//				throw new IllegalStateException(
//						String.format("Player shouldn't be there row: %d col: %d",
//								oneOutPos.x, oneOutPos.y));
			// Goal or empty
			else
				return true;
		}
		else if (pointHas(newPos, WALL))
			return false;
//		else if (pointHas(newPos, PLAYER))
//			throw new IllegalStateException(
//					String.format("Player shouldn't be there row: %d col: %d",
//							newPos.x, newPos.y));
		// Goal or empty
		else
			return true;
	}

	/**
	 * Returns the new BoardState after moving a certain direction
	 * @param direction the direction to move
	 * @return the new BoardState
	 * @pre must be called only if canMove is true
	 */
	public BoardState getMove(Point direction) {
		Point newPos = new Point(player.x + direction.x, player.y + direction.y);
		Point oneOutPos = new Point(newPos.x + direction.x, newPos.y + direction.y);
		Set<Point> newBoxes = boxes;

		// Deep copy board
		byte[][] newBoard = new byte[board.length][];
		for (int i = 0; i < newBoard.length; i++)
			newBoard[i] = board[i].clone();

		// Take player off at current position
		byte playerBitField = newBoard[player.x][player.y];
		newBoard[player.x][player.y] = toggleField(playerBitField, PLAYER);

		// Turn player on at new position
		byte newPlayerBitField = newBoard[newPos.x][newPos.y];
		newBoard[newPos.x][newPos.y] = toggleField(newPlayerBitField, PLAYER);

		// If pushing a box, move box
		if (pointHas(newPos, BOX)) {
			byte oldBoxBitfield = newBoard[newPos.x][newPos.y];
			byte newBoxBitfield = newBoard[oneOutPos.x][oneOutPos.y];
			newBoard[newPos.x][newPos.y] = toggleField(oldBoxBitfield, BOX);
			newBoard[oneOutPos.x][oneOutPos.y] = toggleField(newBoxBitfield, BOX);
			// TODO big potential for a copy bug here?
			newBoxes = new HashSet<Point>(boxes);
			newBoxes.remove(newPos);
			newBoxes.add(oneOutPos);
		}

		// Not copying goals because they SHOULD be the same anyways...
		return new BoardState(newBoard, newPos, goals, newBoxes, direction);
	}

	/**
	 * Returns true if the next move has the input bitfield. False otherwise.
	 * @param field the next move's bitfield check
	 * @return True if the next move has the input bitfield. False otherwise.
	 */
	public boolean nextMoveHas(byte field, Point direction) {
		Point nextPos = new Point(player.x + direction.x, player.y + direction.y);
		return pointHas(nextPos, field);
	}

	/**
	 * Gets the byte board representation used for search hashing
	 * @return the byte board representation
	 */
	public byte[][] getBoard() {
		return board;
	}

	/**
	 * Gets the direction that the player made to get to the boardstate
	 * @return the direction that the player made to get to the boardstate
	 */
	public Point getDirectionTaken() {
		return directionTaken;
	}

	/**
	 * Sets the current state's cost
	 * @param cost the current state's cost
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 * Gets the current state's cost
	 * @return the current state's cost
	 */
	public int getCost() {
		return cost;
	}

	public Set<Point> getGoals() {
		return new HashSet<Point>(goals);
	}

	public Set<Point> getBoxes() {
		return new HashSet<Point>(boxes);
	}

	@Override
	public int compareTo(BoardState other) {
		if (this.getCost() < other.getCost())
			return -1;
		else if (this.getCost() > other.getCost())
			return 1;
		else
			return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				builder.append(fieldToChar.get(board[row][col]));
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(board);
		result = prime * result + ((goals == null) ? 0 : goals.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoardState other = (BoardState) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;
		if (goals == null) {
			if (other.goals != null)
				return false;
		} else if (!goals.equals(other.goals))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		return true;
	}

	/**
	 * Checks if a row/col pair has a certain bitfield
	 * @param row the board row
	 * @param col the board column
	 * @param field the bitfield to check
	 * @return True if the board row/col has the field. False otherwise.
	 */
	private boolean pointHas(int row, int col, byte field) {
		return (board[row][col] & field) == field;
	}

	/**
	 * Checks if a Point coordinate has a certain field
	 * @param pos the Point coordinate, where x is row and y is column
	 * @param field the bitfield to check
	 * @return True if the Point coordinate has the field. False otherwise.
	 */
	private boolean pointHas(Point pos, byte field) {
		return pointHas(pos.x, pos.y, field);
	}

	/**
	 * Toggles the bitfield with a field.
	 * @param bitfield the bitfield to toggle
	 * @param field the field to toggle
	 * @return the new, toggled bitfield
	 */
	private byte toggleField(byte bitfield, byte field) {
		return (byte) (bitfield ^ field);
	}

	/**
	 * Parses a Sokoban text file into a Board object
	 * @param boardInput the Sokoban text file
	 * @return the Board state object
	 * @throws IOException if the Sokoban file does not exist
	 */
	public static BoardState parseBoardInput(String boardInput) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(boardInput));
		int width = Integer.parseInt(reader.readLine());
		int height = Integer.parseInt(reader.readLine());
		byte[][] boardPoints = new byte[height][width];
		Point player = new Point();
		Set<Point> goals = new HashSet<Point>();
		Set<Point> boxes = new HashSet<Point>();

		String line;
		for (int row = 0; row < height && (line = reader.readLine()) != null; row++) {
			for (int col = 0; col < width && col < line.length(); col++) {
				byte field = charToField.get(line.charAt(col));
				boardPoints[row][col] = field;
				if ((field & PLAYER) == PLAYER)
					player = new Point(row, col);
				if ((field & GOAL) == GOAL)
					goals.add(new Point(row, col));
				if ((field & BOX) == BOX)
					boxes.add(new Point(row, col));
			}
		}

		reader.close();
		return new BoardState(boardPoints, player, goals, boxes);
	}
}
