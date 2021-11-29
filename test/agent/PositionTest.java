package agent;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jonsteinn on 21.3.2017.
 */
public class PositionTest {

    private Position base;

    @Before
    public void setUp() {
         this.base = new Position(23,10);
    }

    @Test
    public void testGetters() {
        assertEquals(23, this.base.getX());
        assertEquals(10, this.base.getY());
    }

    @Test
    public void testEquals() {
        assertEquals(this.base, this.base);
        assertEquals(new Position(23, 10), this.base);
        assertNotEquals(new Position(10,23), this.base);
        assertNotEquals(new Position(10,10), this.base);
        assertNotEquals(new Position(23,23), this.base);
    }

    @Test
    public void testParse() {
        assertEquals(Position.fromString(this.base.toString()), this.base);
    }
}