package agent;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jonni on 3/21/2017.
 */
public class ConstraintGroupsTest {

    private PerspectiveBoard board;
    private PositionGrid grid;

    @Before
    public void setUp() {
        this.board = new PerspectiveBoard(10, 10);
        grid = new PositionGrid(10, 10);
    }

    @Test
    public void duplicatesTest() {
        /*
        ##
        11
         */
        Set<Position> var1 = new HashSet<>(), var2 = new HashSet<>();
        var1.add(this.grid.getVariable(0,0)); var2.add(this.grid.getVariable(0,0));
        var1.add(this.grid.getVariable(1,0)); var2.add(this.grid.getVariable(1,0));
        getMap().put(new Position(0,1), new ConstraintInfo(var1, 1));
        getMap().put(new Position(1,1), new ConstraintInfo(var2, 1));
        assertEquals(1, new ConstraintGroups(this.board).getGroups().keySet().size());
    }

    @Test
    public void singleJoinedGroup() {
        /*
        #####
        #A1##
        ##B##
        #2C##
        ##DE#
        ##1##
         */
        Set<Position> var1 = new HashSet<>(Arrays.asList(p(1,1),p(2,2)));
        Set<Position> var3 = new HashSet<>(Arrays.asList(p(2,4),p(3,4)));
        Set<Position> var2 = new HashSet<>(Arrays.asList(p(2,2),p(2,3),p(2,4)));
        getMap().put(new Position(2,1), new ConstraintInfo(var1, 1));
        getMap().put(new Position(2,5), new ConstraintInfo(var3, 1));
        getMap().put(new Position(1,3), new ConstraintInfo(var2, 2));

        ConstraintGroups grp = new ConstraintGroups(this.board);
        assertEquals(1, grp.getGroups().size());
        Set<ConstraintInfo> _set = grp.getGroups().keySet().iterator().next();
        assertEquals(3, _set.size());
        for (ConstraintInfo info : _set) {
            assertTrue(info.getUnknownNeighbours().equals(var1) || info.getUnknownNeighbours().equals(var2) || info.getUnknownNeighbours().equals(var3));
            assertTrue(info.getUnknownNeighbours().equals(var1) || info.getUnknownNeighbours().equals(var2) || info.getUnknownNeighbours().equals(var3));
            assertTrue(info.getUnknownNeighbours().equals(var1) || info.getUnknownNeighbours().equals(var2) || info.getUnknownNeighbours().equals(var3));
        }
    }

    @Test
    public void empty() {
        this.board.setAdjacent(5, 5, 0, this.grid, new HashSet<>(), new HashSet<>());
        this.board.setBombAt(3,3, this.grid, new HashSet<>(), new HashSet<>());
        assertTrue(new ConstraintGroups(this.board).isEmpty());
    }

    @Test
    public void disjointGroups() {
        /*
        abc#de
        #2###1
        ######
        ###f##
        ###g1#
         */
        Set<Position> var1 = new HashSet<>(), var2 = new HashSet<>(), var3 = new HashSet<>();
        var1.add(this.grid.getVariable(0,0));
        var1.add(this.grid.getVariable(1,0));
        var1.add(this.grid.getVariable(2,0));
        var2.add(this.grid.getVariable(4,0));
        var2.add(this.grid.getVariable(5,0));
        var3.add(this.grid.getVariable(3,3));
        var3.add(this.grid.getVariable(3,4));
        getMap().put(new Position(1,1), new ConstraintInfo(var1, 2));
        getMap().put(new Position(5,1), new ConstraintInfo(var2, 1));
        getMap().put(new Position(4,4), new ConstraintInfo(var3, 1));

        ConstraintGroups grp = new ConstraintGroups(this.board);
        assertEquals(3, grp.getGroups().size());
    }

    private Position p(int x, int y) {
        return this.grid.getVariable(x, y);
    }

    private Map<Position, ConstraintInfo> getMap() {
        return this.board.getConstraintPositions();
    }
}