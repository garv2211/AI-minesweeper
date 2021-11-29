package level;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jonni on 3/5/2017.
 */
public class BoardTest {

    private Board board;

    @Before
    public void setUp() throws Exception {
        this.board = new Board(8,5);
    }

    @Test
    public void sizeTest() {
        assertEquals("Width", 8, this.board.getWidth());
        assertEquals("Height", 5, this.board.getHeight());
    }

    @Test
    public void bombCountTest() {
        assertEquals("Init bomb count", 0, this.board.getBombCount());
        this.board.addBomb(0,0);
        assertEquals("After adding one", 1, this.board.getBombCount());
        this.board.addBomb(1,0);
        this.board.addBomb(7,4);
        this.board.addBomb(2,4);
        this.board.addBomb(7,1);
        assertEquals("After adding 5", 5, this.board.getBombCount());
        this.board.addBomb(7,4);
        this.board.addBomb(1,0);
        this.board.addBomb(7,1);
        assertEquals("Adding to same positions", 5, this.board.getBombCount());
    }

    @Test
    public void containsTest() {
        for (int i = 0; i < this.board.getWidth(); i++) {
            for (int j = 0; j < this.board.getHeight(); j++) {
                assertFalse("No bomb: (" + i + "," + j + ")", this.board.containsBomb(i, j));
            }
        }
        this.board.addBomb(5,2);
        assertTrue("When contains", this.board.containsBomb(5,2));
    }
}