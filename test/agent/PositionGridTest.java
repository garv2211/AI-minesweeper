package agent;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by jonsteinn on 21.3.2017.
 */
public class PositionGridTest {

    private PositionGrid grid;

    @Before
    public void setUp() throws Exception {
        this.grid = new PositionGrid(24, 24);
    }

    @Test
    public void getVariableTest() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 24; j++) {
                assertEquals(new Position(i, j), this.grid.getVariable(i, j));
            }
        }
    }

    @Test
    public void getCornerNeighboursTest() {
        Set<Position> expected = new HashSet<>();
        Set<Position> converted = new HashSet<>();

        expected.add(this.grid.getVariable(1,1));
        expected.add(this.grid.getVariable(1,0));
        expected.add(this.grid.getVariable(0,1));
        converted.addAll(this.grid.getNeighbours(0 ,0));
        assertEquals(expected, converted);
        expected.clear();
        converted.clear();

        expected.add(this.grid.getVariable(22,22));
        expected.add(this.grid.getVariable(23,22));
        expected.add(this.grid.getVariable(22,23));
        converted.addAll(this.grid.getNeighbours(23 ,23));
        assertEquals(expected, converted);
        expected.clear();
        converted.clear();

        expected.add(this.grid.getVariable(23,1));
        expected.add(this.grid.getVariable(22,1));
        expected.add(this.grid.getVariable(22,0));
        converted.addAll(this.grid.getNeighbours(23 ,0));
        assertEquals(expected, converted);
        expected.clear();
        converted.clear();


        expected.add(this.grid.getVariable(1,23));
        expected.add(this.grid.getVariable(1,22));
        expected.add(this.grid.getVariable(0,22));
        converted.addAll(this.grid.getNeighbours(0 ,23));
        assertEquals(expected, converted);
        expected.clear();
        converted.clear();
    }

    @Test
    public void getEdgeNeighboursTest() {
        Set<Position> expected = new HashSet<>();
        Set<Position> converted = new HashSet<>();

        expected.add(this.grid.getVariable(0,11));
        expected.add(this.grid.getVariable(1,11));
        expected.add(this.grid.getVariable(1,12));
        expected.add(this.grid.getVariable(1,13));
        expected.add(this.grid.getVariable(0,13));
        converted.addAll(this.grid.getNeighbours(0 ,12));
        assertEquals(expected, converted);
        expected.clear();
        converted.clear();

        expected.add(this.grid.getVariable(11,0));
        expected.add(this.grid.getVariable(11,1));
        expected.add(this.grid.getVariable(12,1));
        expected.add(this.grid.getVariable(13,1));
        expected.add(this.grid.getVariable(13,0));
        converted.addAll(this.grid.getNeighbours(12 ,0));
        assertEquals(expected, converted);
        expected.clear();
        converted.clear();

        expected.add(this.grid.getVariable(23,11));
        expected.add(this.grid.getVariable(22,11));
        expected.add(this.grid.getVariable(22,12));
        expected.add(this.grid.getVariable(22,13));
        expected.add(this.grid.getVariable(23,13));
        converted.addAll(this.grid.getNeighbours(23 ,12));
        assertEquals(expected, converted);
        expected.clear();
        converted.clear();

        expected.add(this.grid.getVariable(11,23));
        expected.add(this.grid.getVariable(11,22));
        expected.add(this.grid.getVariable(12,22));
        expected.add(this.grid.getVariable(13,22));
        expected.add(this.grid.getVariable(13,23));
        converted.addAll(this.grid.getNeighbours(12 ,23));
        assertEquals(expected, converted);
        expected.clear();
        converted.clear();
    }

    @Test
    public void getCenterNeighboursTest() {
        Set<Position> expected = new HashSet<>();
        Set<Position> converted = new HashSet<>();

        expected.add(this.grid.getVariable(11,11));
        expected.add(this.grid.getVariable(11,12));
        expected.add(this.grid.getVariable(11,13));
        expected.add(this.grid.getVariable(12,11));
        expected.add(this.grid.getVariable(12,13));
        expected.add(this.grid.getVariable(13,11));
        expected.add(this.grid.getVariable(13,12));
        expected.add(this.grid.getVariable(13,13));
        converted.addAll(this.grid.getNeighbours(12 ,12));
        assertEquals(expected, converted);
    }

}