package level;

/**
 * Created by Jonni on 3/5/2017.
 *
 * Represents the actual board and its bombs.
 */
public class Board {
    private boolean[][] board;
    private int bombCount;

    /**
     * @param width row length
     * @param height column length
     */
    public Board(int width, int height) {
        this.bombCount = 0;
        this.board = new boolean[height][width];
    }

    /**
     * @return number of squares in a row
     */
    public int getWidth() {
        return this.board[0].length;
    }

    /**
     * @return number of squares in a column
     */
    public int getHeight() {
        return this.board.length;
    }

    /**
     * @return number of bombs
     */
    public int getBombCount() {
        return this.bombCount;
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return true iff there is a bomb at (x,y)
     */
    public boolean containsBomb(int x, int y) {
        return board[y][x];
    }

    /**
     * Checks if the given position is outside the board.
     *
     * @param x coordinate
     * @param y coordinate
     * @return true iff (x,y) is outside the board
     */
    public boolean outOfBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight();
    }

    /**
     * Adds a bomb to the given position.
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void addBomb(int x, int y) {
        if (!board[y][x]) bombCount++;
        board[y][x] = true;
    }

    /**
     * Counts the number of adjacent bombs to a square.
     *
     * @param x coordinate
     * @param y coordinate
     * @return number of bombs next to (x,y)
     */
    public int adjacentBombs(int x, int y) {
        int counter = 0;
        for (int i = x-1; i < x+2; i++) {
            for (int j = y-1; j < y+2; j++) {
                if (outOfBounds(i, j) && containsBomb(i,j)) {
                    counter++;
                }
            }
        }
        return counter;
    }
}
