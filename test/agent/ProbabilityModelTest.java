package agent;

import org.chocosolver.solver.exception.ContradictionException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created by Jonni on 3/22/2017.
 */
public class ProbabilityModelTest {

    private static final double EPSILON = 1E-10;

    private PositionGrid grid;


    @Before
    public void setUp() throws Exception {
        this.grid = new PositionGrid(24, 24);
    }

    @Test
    public void foo()  throws ContradictionException {
        // ###
        // #2#
        // #1#
        // ###
        Set<ConstraintInfo> constraints = new HashSet<>();
        Set<Position> s1 = new HashSet<>(), s2 = new HashSet<>(), s = new HashSet<>();
        s.addAll(Arrays.asList(
                this.grid.getVariable(1, 1),
                this.grid.getVariable(2, 1),
                this.grid.getVariable(3, 1),
                this.grid.getVariable(1, 2),
                this.grid.getVariable(3, 2),
                this.grid.getVariable(1, 3),
                this.grid.getVariable(3, 3),
                this.grid.getVariable(1, 4),
                this.grid.getVariable(2, 4),
                this.grid.getVariable(3, 4)
        ));
        s1.addAll(Arrays.asList(
                this.grid.getVariable(1, 1),
                this.grid.getVariable(2, 1),
                this.grid.getVariable(3, 1),
                this.grid.getVariable(1, 2),
                this.grid.getVariable(3, 2),
                this.grid.getVariable(1, 3),
                this.grid.getVariable(3, 3)
        ));
        s2.addAll(Arrays.asList(
                this.grid.getVariable(1, 2),
                this.grid.getVariable(3, 2),
                this.grid.getVariable(3, 3),
                this.grid.getVariable(1, 3),
                this.grid.getVariable(1, 4),
                this.grid.getVariable(2, 4),
                this.grid.getVariable(3, 4)
        ));
        constraints.addAll(Arrays.asList(
                new ConstraintInfo(s1, 2),
                new ConstraintInfo(s2, 1)
        ));

        // Nothing to conduct
        MSModel model = new MSModel(constraints, s);
        for (Position p : s) {
            assertFalse(model.hasBomb(p));
            assertFalse(model.hasNoBombs(p));
        }



        ProbabilityModel pModel = new ProbabilityModel(constraints, s, new HashSet<>() /* don't need that here */);
        Map<Position, Double> probabilities = new HashMap<>();
        assertEquals(2, pModel.getProbabilities(probabilities)); // can be 2 or 3, 2 being the minimum

        PriorityQueue<Map.Entry<Position, Double>> sorter = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        for (Map.Entry<Position, Double> entry : probabilities.entrySet()) sorter.add(entry);

        // Possible outcomes:
        /*
        2 in top level
        {
            2,3
            {
                #XX
                #2#
                #1#
                #X#

                #XX
                #2#
                #1#
                X##

                #XX
                #2#
                #1#
                ##X
            }
            1,3
            {
                X#X
                #2#
                #1#
                ##X

                X#X
                #2#
                #1#
                #X#

                X#X
                #2#
                #1#
                X##
            }
            1,2
            {
                XX#
                #2#
                #1#
                ##X

                XX#
                #2#
                #1#
                #X#

                XX#
                #2#
                #1#
                X##
            }
        }

        1 in top level
        {
            1
            {
                X##
                #2X
                #1#
                ###

                X##
                #2#
                #1X
                ###

                X##
                #2#
                X1#
                ###

                X##
                X2#
                #1#
                ###

            }
            2
            {
                #X#
                #2X
                #1#
                ###

                #X#
                #2#
                #1X
                ###

                #X#
                #2#
                X1#
                ###

                #X#
                X2#
                #1#
                ###
            }
            3
            {
                ##X
                #2X
                #1#
                ###

                ##X
                #2#
                #1X
                ###

                ##X
                #2#
                X1#
                ###

                ##X
                X2#
                #1#
                ###
            }
        }
         */

        // (1, 1) : 10
        // (2, 1) : 10
        // (3, 1) : 10
        // (1, 2) : 3
        // (3, 2) : 3
        // (1, 3) : 3
        // (3, 3) : 3
        // (1, 4) : 3
        // (2, 4) : 3
        // (3, 4) : 3

        // Expected probabilities: 10/21 and 3/21

        Set<Position> low = new HashSet<>(Arrays.asList(
                this.grid.getVariable(1,2),
                this.grid.getVariable(3,2),
                this.grid.getVariable(1,3),
                this.grid.getVariable(3,3),
                this.grid.getVariable(1,4),
                this.grid.getVariable(2,4),
                this.grid.getVariable(3,4)
        ));

        Set<Position> high = new HashSet<>(Arrays.asList(
                this.grid.getVariable(1,1),
                this.grid.getVariable(2,1),
                this.grid.getVariable(3,1)
        ));

        for (Position lowPos : low) assertEquals(100.0 * 3.0 / 21.0, probabilities.get(lowPos), EPSILON);
        for (Position highPos : high) assertEquals(100.0 * 10.0 / 21.0, probabilities.get(highPos), EPSILON);


        for (int i = 0; i < low.size(); i++) {
            assertTrue(low.contains(sorter.poll().getKey()));
        }

        for (int i = 0; i < high.size(); i++) {
            assertTrue(high.contains(sorter.poll().getKey()));
        }
    }

}