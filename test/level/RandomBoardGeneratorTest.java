package level;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jonni on 3/5/2017.
 */
public class RandomBoardGeneratorTest {

    // Update if updated in generator
    private static final int SPLIT_BOUNDARY = 5;

    private RandomBoardGenerator generator;

    @Before
    public void setUp() throws Exception {
        this.generator = new RandomBoardGenerator();
    }

    @Test
    public void noRestrictionsTest() {
        Board board = generator.create(10, 10, 50, false, false);
        int bombCount = 0;
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (board.containsBomb(i, j)) bombCount++;
            }
        }
        assertEquals(50, bombCount);
    }

    @Test
    public void noSurroundedTest() {
        for (int k = 0; k < 100; k++) {
            Board board = generator.create(8, 8, 32, true, false);
            int bombCount = 0;
            boolean noSurrounded = true;
            for (int i = 0; i < board.getWidth(); i++) {
                for (int j = 0; j < board.getHeight(); j++) {
                    if (board.containsBomb(i, j)) {
                        bombCount++;
                        if (noSurrounded) {
                            noSurrounded = notSurrounded(board, i, j);
                        }
                    }
                }
            }
            assertEquals("Bomb count", 32, bombCount);
            assertTrue("No bomb surrounded by bombs", noSurrounded);
        }
    }

    @Test
    public void spreadTest1() {
        for (int n = 0; n < 100; n++) {
            Board board = generator.create(
                    SPLIT_BOUNDARY << 1,
                    SPLIT_BOUNDARY << 1,
                    SPLIT_BOUNDARY << 2,
                    false,
                    true
            );
            int counter = 0;
            for (int i = 0; i < SPLIT_BOUNDARY; i++) {
                for (int j = 0; j < SPLIT_BOUNDARY; j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("1/4", SPLIT_BOUNDARY, counter);
            counter = 0;
            for (int i = SPLIT_BOUNDARY; i < (SPLIT_BOUNDARY << 1); i++) {
                for (int j = 0; j < SPLIT_BOUNDARY; j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("2/4", SPLIT_BOUNDARY, counter);
            counter = 0;
            for (int i = 0; i < SPLIT_BOUNDARY; i++) {
                for (int j = SPLIT_BOUNDARY; j < (SPLIT_BOUNDARY << 1); j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("3/4", SPLIT_BOUNDARY, counter);
            counter = 0;
            for (int i = SPLIT_BOUNDARY; i < (SPLIT_BOUNDARY << 1); i++) {
                for (int j = SPLIT_BOUNDARY; j < (SPLIT_BOUNDARY << 1); j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("4/4", SPLIT_BOUNDARY, counter);
        }
    }

    @Test
    public void spreadTest2() {
        for (int n = 0; n < 100; n++) {
            Board board = this.generator.create(7, 4, 11, false, true);
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("Left", 6, counter);
            counter = 0;
            for (int i = 4; i < 7; i++) {
                for (int j = 0; j < 4; j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("Right", 5, counter);
        }
    }

    @Test
    public void spreadTest3() {
        for (int n = 0; n < 100; n++) {
            Board board = this.generator.create(4, 13, 15, false, true);
            // 0-1-2-4 : 4-5-6 : 7-8-9-10-11-12
            // 4 bombs : 4 bombs : 7 bombs
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("Up", 4, counter);
            counter = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 4; j < 7; j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("Right", 4, counter);
            assertEquals("Up", 4, counter);
            counter = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 7; j < 13; j++) {
                    if (board.containsBomb(i, j)) counter++;
                }
            }
            assertEquals("Right", 7, counter);
        }
    }

    @Test
    public void spreadAndNotSurroundedTest() {
        for (int n = 0; n < 100; n++) {
            Board board = generator.create(
                    SPLIT_BOUNDARY << 1,
                    SPLIT_BOUNDARY << 1,
                    SPLIT_BOUNDARY << 2,
                    true,
                    true
            );
            int counter = 0;
            for (int i = 0; i < SPLIT_BOUNDARY; i++) {
                for (int j = 0; j < SPLIT_BOUNDARY; j++) {
                    if (board.containsBomb(i, j)) {
                        counter++;
                        assertTrue("1/4", notSurrounded(board, i, j));
                    }
                }
            }
            assertEquals("1/4", SPLIT_BOUNDARY, counter);
            counter = 0;
            for (int i = SPLIT_BOUNDARY; i < (SPLIT_BOUNDARY << 1); i++) {
                for (int j = 0; j < SPLIT_BOUNDARY; j++) {
                    if (board.containsBomb(i, j)) {
                        counter++;
                        assertTrue("2/4", notSurrounded(board, i, j));
                    }
                }
            }
            assertEquals("2/4", SPLIT_BOUNDARY, counter);
            counter = 0;
            for (int i = 0; i < SPLIT_BOUNDARY; i++) {
                for (int j = SPLIT_BOUNDARY; j < (SPLIT_BOUNDARY << 1); j++) {
                    if (board.containsBomb(i, j)) {
                        counter++;
                        assertTrue("3/4", notSurrounded(board, i, j));
                    }
                }
            }
            assertEquals("3/4", SPLIT_BOUNDARY, counter);
            counter = 0;
            for (int i = SPLIT_BOUNDARY; i < (SPLIT_BOUNDARY << 1); i++) {
                for (int j = SPLIT_BOUNDARY; j < (SPLIT_BOUNDARY << 1); j++) {
                    if (board.containsBomb(i, j)) {
                        counter++;
                        assertTrue("4/4", notSurrounded(board, i, j));
                    }
                }
            }
            assertEquals("4/4", SPLIT_BOUNDARY, counter);
        }
    }

    private boolean notSurrounded(Board board, int i, int j) {
        int neighbourBombCounter = 0; // and self
        for (int a = i - 1; a < i + 2; a++) {
            for (int b = j - 1; b < j + 2; b++) {
                if (a < 0 && a >= board.getWidth() && b < 0 && b >= board.getHeight()) {
                    if (board.containsBomb(a, b)) {
                        neighbourBombCounter++;
                    }
                }
            }
        }
        return neighbourBombCounter != 9;
    }

}