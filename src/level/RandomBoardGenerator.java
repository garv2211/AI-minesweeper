package level;

import java.util.Random;

/**
 * Created by Jonni on 3/5/2017.
 *
 * Generator for Minesweeper boards.
 */
public class RandomBoardGenerator {

    private static final int SPLIT_BOUNDARY = 5;
    private Random random;

    /**
     * Constructor.
     */
    public RandomBoardGenerator() {
        this.random = new Random();
    }

    /**
     * Creates a random board with given parameters. There are 4 different options,
     * set by the possible values of the boolean parameters.
     *
     * @param width row length of board
     * @param height column length of board
     * @param bombs number of bombs to be included in the board
     * @param noSurrounded true => no bomb is surrounded only by bombs
     * @param spreadEven true => recursively: split board and generate in parts
     * @return Board that is generated
     */
    public Board create(int width, int height, int bombs, boolean noSurrounded, boolean spreadEven) {
        return (noSurrounded && spreadEven) ?
                createEvenSpreadNoSurrounded(width, height, bombs) :
                (noSurrounded ?
                        createNoSurrounded(width, height, bombs) :
                        (spreadEven ?
                                createEvenSpread(width, height, bombs) :
                                create(width, height, bombs)
                        )
                );
    }


    /**
     * No restrictions.
     *
     * @param width row length
     * @param height column length
     * @param bombs number of bombs
     * @return generated board
     */
    private Board create(int width, int height, int bombs) {
        Board board = new Board(width, height);
        while (board.getBombCount() < bombs) {
            int x = this.random.nextInt(width);
            int y = this.random.nextInt(height);
            board.addBomb(x, y);
        }
        return board;
    }

    /**
     *
     * @param width row length
     * @param height column length
     * @param bombs number of bombs
     * @return generated board
     */
    private Board createNoSurrounded(int width, int height, int bombs) {
        Board board = new Board(width, height);
        while (board.getBombCount() < bombs) {
            int x = this.random.nextInt(width);
            int y = this.random.nextInt(height);
            if (surrounded(board, x, y)) continue;
            board.addBomb(x, y);
        }
        return board;
    }

    /**
     *
     * @param width row length
     * @param height column length
     * @param bombs number of bombs
     * @return generated board
     */
    private Board createEvenSpread(int width, int height, int bombs) {
        Board board = new Board(width, height);
        spreadEvenHelper(board, 0, width - 1, 0, height - 1, bombs, false);
        return board;
    }

    /**
     *
     * @param width row length
     * @param height column length
     * @param bombs number of bombs
     * @return generated board
     */
    private Board createEvenSpreadNoSurrounded(int width, int height, int bombs) {
        Board board = new Board(width, height);
        spreadEvenHelper(board, 0, width - 1, 0, height - 1, bombs, true);
        return board;
    }

    /**
     * A helper function for recursion that takes boundaries as parameter which it
     * changes and calls self with.
     *
     * @param board the board which is added to
     * @param minX lower horizontal bound
     * @param maxX upper horizontal bound
     * @param minY lower vertical bound
     * @param maxY upper vertical bound
     * @param bombs number of bombs
     * @param noSurrounded true => no bomb is surrounded only by bombs
     */
    private void spreadEvenHelper(Board board, int minX, int maxX, int minY, int maxY, int bombs, boolean noSurrounded) {
        if ((maxX - minY > SPLIT_BOUNDARY) && (maxY - minY > SPLIT_BOUNDARY)) {
            // Split both horizontally and vertically

            int halfX = (maxX - minX) >> 1;
            int halfY = (maxY - minY) >> 1;
            int quartBombs = bombs >> 2;
            int bombReminder = bombs % 4; // reminder, if any, is spread to first, second and/or third

            // Each quadrant
            spreadEvenHelper(board, minX, minX + halfX, minY, minY + halfY,
                    bombReminder > 0 ? quartBombs + 1: quartBombs, noSurrounded);
            spreadEvenHelper(board, minX, minX + halfX , minY + halfY + 1, maxY,
                    bombReminder > 1 ? quartBombs + 1 : quartBombs, noSurrounded);
            spreadEvenHelper(board, minX + halfX + 1, maxX, minY, minY + halfY,
                    bombReminder > 2 ? quartBombs + 1 : quartBombs, noSurrounded);
            spreadEvenHelper(board, minX + halfX + 1, maxX, minY + halfY + 1, maxY,
                    quartBombs, noSurrounded);

        } else if (maxX - minX > SPLIT_BOUNDARY) {
            // Split horizontally

            int half = (maxX - minX) >> 1;
            int halfBombs = bombs >> 1;
            spreadEvenHelper(board, minX, minX + half, minY, maxY, bombs - halfBombs, noSurrounded);
            spreadEvenHelper(board, minX + half + 1, maxX, minY, maxY, halfBombs, noSurrounded);

        } else if (maxY - minY > SPLIT_BOUNDARY) {
            // Split vertically

            int half = (maxY - minY) >> 1;
            int halfBombs = bombs >> 1;
            spreadEvenHelper(board, minX, maxX, minY, minY + half, bombs - halfBombs, noSurrounded);
            spreadEvenHelper(board, minX, maxX, minY + half + 1, maxY, halfBombs, noSurrounded);

        } else {
            // Split neither horizontally and vertically

            int goalBombs = board.getBombCount() + bombs;
            if (noSurrounded) {
                while (board.getBombCount() < goalBombs) {
                    int x = intervalRandom(minX, maxX);
                    int y = intervalRandom(minY, maxY);
                    if (surrounded(board, x, y)) continue;
                    board.addBomb(x, y);
                }
            } else {
                while (board.getBombCount() < goalBombs) {
                    board.addBomb(intervalRandom(minX, maxX), intervalRandom(minY, maxY));
                }
            }
        }
    }

    /**
     * @param board the board to check
     * @param x coordinate
     * @param y coordinate
     * @return false iff at least one of (x,y)'s neighbours is not a bomb
     */
    private boolean surrounded(Board board, int x, int y) {
        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (board.outOfBounds(i,j) && !board.containsBomb(i,j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a random integer value within boundaries.
     *
     * @param a lower bound
     * @param b upper bound
     * @return a random int from [a,b]
     */
    private int intervalRandom(int a, int b) {
        return this.random.nextInt(b-a+1)+a;
    }
}
