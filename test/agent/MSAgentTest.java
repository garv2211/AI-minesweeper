package agent;

import level.Board;
import level.RandomBoardGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Jonni on 3/22/2017.
 */
public class MSAgentTest {

    private PositionGrid grid;
    private RandomBoardGenerator bGen;

    @Before
    public void setUp() throws Exception {
        this.grid = new PositionGrid(24, 24);
        this.bGen = new RandomBoardGenerator();
    }

    @Test
    public void simpleGameTest() {
        Board board = new Board(15, 15);
        board.addBomb(0, 14);

        MSAgent agent = new MSAgent(15, 15,1, this.grid.getVariable(14, 0));

        boolean won = true;

        Set<Position> moves = new HashSet<>();

        Position next;
        while (moves.size() < 15 * 15 - 1) {
            next = agent.nextMove();
            if (board.containsBomb(next.getX(), next.getY())) {
                won = false;
                break;
            }
            agent.sendBackResult(next, board.adjacentBombs(next.getX(), next.getY()));
            moves.add(next);
        }

        assertTrue(won);

        assertTrue(agent.markBomb().equals(this.grid.getVariable(0, 14)));
        assertEquals(null, agent.markBomb());

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (i != 0 || j != 14) assertTrue(moves.contains(this.grid.getVariable(i, j)));
            }
        }
        assertEquals(15 * 15 - 1, moves.size());
    }

    @Test
    public void preferRandomUnknownTest() {
        Board board = new Board(8,8);
        board.addBomb(3,3);
        board.addBomb(1,1);
        // 8 more bombs that I won't add but tell the agent it has

        // Probability of bombs outside the constraint is
        // (10 - 2) / (8*8 - 9) which is ~14.5%
        // while the probability for a given square around the 2 is
        // 2 / 8 = 1 / 4 which is 25%
        // Thus we should always prefer to choose random unknowns here

        /*
        ########
        #X######
        ##2#####
        ###X####
        ########
        ########
        ########
        ########
        */

        for (int i = 0; i < 50; i++) {
            MSAgent agent = new MSAgent(8, 8, 10, this.grid.getVariable(2, 2));
            agent.sendBackResult(agent.nextMove(), 2);

            Set<Position> badProbability = new HashSet<>(
                    Arrays.asList(
                            this.grid.getVariable(1, 1),
                            this.grid.getVariable(2, 1),
                            this.grid.getVariable(3, 1),
                            this.grid.getVariable(1, 2),
                            this.grid.getVariable(3, 2),
                            this.grid.getVariable(1, 3),
                            this.grid.getVariable(2, 3),
                            this.grid.getVariable(3, 3)
                    )
            );

            assertFalse(badProbability.contains(agent.nextMove()));
        }
    }

    @Test
    public void simpleGuessTest() {
        // ####
        // X21X
        // X###
        Board b = new Board(5,3);
        b.addBomb(0,2);
        b.addBomb(0,1);
        b.addBomb(3,1);

        int total = 0, bombs = 0;
        for (int i = 0; i < 1000; i++) {

            MSAgent agent = new MSAgent(5, 3, 3, new Position(1, 1));
            agent.nextMove();
            agent.sendBackResult(this.grid.getVariable(2, 1), 1);

            if (agent.nextMove().equals(this.grid.getVariable(1, 1))) {
                total++;

                agent.sendBackResult(this.grid.getVariable(1, 1), 2);

                Position next = agent.nextMove();
                assertFalse(new HashSet<>(Arrays.asList(
                        this.grid.getVariable(0, 2),
                        this.grid.getVariable(0, 2),
                        this.grid.getVariable(0, 2))).contains(next));

                if (b.containsBomb(next.getX(), next.getY())) bombs++;
            }

        }

        // Probability is about 14.3%
        assertTrue("Probability is ~14, if fails, run several times and check if its fails again, if so something is probably wrong!",
                100.0 * bombs / total < 20);
    }

    @Test
    public void noGuessGameWin() {
        /*
        0000001X
        00000011
        00000011
        0000001X
        00000022
        0000001X
        00000011
        00000000
        */
        Board b = new Board(8,8);
        b.addBomb(7,0);
        b.addBomb(7,3);
        b.addBomb(7,5);
        Random random = new Random();

        ArrayList<Position> start = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                start.add(this.grid.getVariable(i, j));
            }
        }
        start.add(this.grid.getVariable(7,7));
        start.add(this.grid.getVariable(6,7));


        for (int experiment = 0; experiment < 1500; experiment++) {
            Position pos = start.get(random.nextInt(start.size()));
            MSAgent agent = new MSAgent(8, 8, 3, pos);

            int movesToWin = 8 * 8 - 3;
            boolean won = true;

            Position next;
            while (movesToWin-- > 0) {
                next = agent.nextMove();
                if (b.containsBomb(next.getX(), next.getY())) {
                    won = false;
                    break;
                }
                agent.sendBackResult(next, b.adjacentBombs(next.getX(), next.getY()));
            }

            if (!won) System.out.println(pos);
            assertTrue(won);

            Set<Position> expectedBombs = new HashSet<>(
                    Arrays.asList(
                            this.grid.getVariable(7, 0),
                            this.grid.getVariable(7, 3),
                            this.grid.getVariable(7, 5)
                    )
            );

            assertTrue(expectedBombs.contains(agent.markBomb()));
            assertTrue(expectedBombs.contains(agent.markBomb()));
            assertTrue(expectedBombs.contains(agent.markBomb()));
            assertEquals(null, agent.markBomb());
        }
    }

    @Test
    public void disjointConstraintTest() {
        /*
        X200002X
        x200002X
        11000011
        00000000
        00000000
        11000011
        X200002X
        X200002X
        */

        Board b = new Board(8,8);
        b.addBomb(0,0); b.addBomb(0,1);
        b.addBomb(7,0); b.addBomb(7,1);
        b.addBomb(0,6); b.addBomb(0,7);
        b.addBomb(7,6); b.addBomb(7,7);

        Set<Position> uniqueMoveCounter = new HashSet<>();
        uniqueMoveCounter.add(this.grid.getVariable(4,4));

        MSAgent a = new MSAgent(8,8,8, this.grid.getVariable(4,4));
        a.sendBackResult(a.nextMove(), 0);
        int movesToWin = 8 * 8 - 8 - 1;
        boolean win = true;
        while (movesToWin-- > 0) {
            Position p = a.nextMove();
            uniqueMoveCounter.add(p);
            if (b.containsBomb(p.getX(), p.getY())) {
                win = false;
                break;
            }
            a.sendBackResult(p, b.adjacentBombs(p.getX(), p.getY()));
        }

        assertTrue(win);
        Set<Position> expectedBombSet = new HashSet<>(
                Arrays.asList(
                        this.grid.getVariable(0,0),
                        this.grid.getVariable(0,1),
                        this.grid.getVariable(7,0),
                        this.grid.getVariable(7,1),
                        this.grid.getVariable(0,6),
                        this.grid.getVariable(0,7),
                        this.grid.getVariable(7,6),
                        this.grid.getVariable(7,7)
                )
        );
        for (int i = 0; i < 8; i++) assertTrue(expectedBombSet.contains(a.markBomb()));
        assertEquals(null, a.markBomb());

        assertEquals(8*8-8, uniqueMoveCounter.size());

    }

    @Test
    public void noGuessNeededGame() {
        /*
         01234567
        0##X###X#
        1#####X##
        2#######X
        3###X####
        4X######X
        5########
        6X###XX##
        7########
         */

        Board b = new Board(8,8);
        Set<Position> bombs = new HashSet<>(Arrays.asList(
                this.grid.getVariable(2,0),
                this.grid.getVariable(6,0),
                this.grid.getVariable(5,1),
                this.grid.getVariable(7,2),
                this.grid.getVariable(3,3),
                this.grid.getVariable(0,4),
                this.grid.getVariable(7,4),
                this.grid.getVariable(0,6),
                this.grid.getVariable(4,6),
                this.grid.getVariable(5,6)
        ));
        for (Position pos : bombs){
           b.addBomb(pos.getX(), pos.getY());
        }

        for (int n = 0; n < 50; n++) {
            MSAgent agent = new MSAgent(8, 8, 10, this.grid.getVariable(0, 0));
            int toWin = 8 * 8 - 10;
            boolean win = true;
            while (toWin-- > 0) {
                Position next = agent.nextMove();
                if (b.containsBomb(next.getX(), next.getY())) {
                    win = false;
                    break;
                }
                agent.sendBackResult(next, b.adjacentBombs(next.getX(), next.getY()));
            }

            assertTrue(win);
            for (int i = 0; i < 10; i++) {
                assertTrue(bombs.contains(agent.markBomb()));
            }
            assertEquals(null, agent.markBomb());
        }
    }

    @Test
    public void noGuessNeededGame2() {
        /*
         0123456789012345
        0#####x#x########
        1###X#x#x###xx###
        2#############x##
        3###x######x#x#x#
        4###x##xx#####x#x
        5##x####x##x#####
        6#####x##########
        7###########x###x
        8###x############
        9########x####x##
        0x##x#######xxx##
        1#x#x#########x##
        2#############xx#
        3###x############
        4############x###
        5#####x#####x####
         */
        Board b = new Board(16, 16);
        Set<Position> bombs = new HashSet<>(Arrays.asList(
                this.grid.getVariable(5,0),
                this.grid.getVariable(7,0),
                this.grid.getVariable(3,1),
                this.grid.getVariable(5,1),
                this.grid.getVariable(7,1),
                this.grid.getVariable(11,1),
                this.grid.getVariable(12,1),
                this.grid.getVariable(13,1),
                this.grid.getVariable(3,3),
                this.grid.getVariable(10,3),
                this.grid.getVariable(12,3),
                this.grid.getVariable(14,3),
                this.grid.getVariable(3,4),
                this.grid.getVariable(6,4),
                this.grid.getVariable(7,4),
                this.grid.getVariable(13,4),
                this.grid.getVariable(15,4),
                this.grid.getVariable(2,5),
                this.grid.getVariable(7,5),
                this.grid.getVariable(10,5),
                this.grid.getVariable(5,6),
                this.grid.getVariable(11,7),
                this.grid.getVariable(15,7),
                this.grid.getVariable(3,8),
                this.grid.getVariable(8,9),
                this.grid.getVariable(13,9),
                this.grid.getVariable(0,10),
                this.grid.getVariable(3,10),
                this.grid.getVariable(11,10),
                this.grid.getVariable(12,10),
                this.grid.getVariable(13,10),
                this.grid.getVariable(1,11),
                this.grid.getVariable(3,11),
                this.grid.getVariable(13,11),
                this.grid.getVariable(13,12),
                this.grid.getVariable(14,12),
                this.grid.getVariable(3,13),
                this.grid.getVariable(12,14),
                this.grid.getVariable(5,15),
                this.grid.getVariable(11,15)
        ));
        for (Position pos : bombs){
            b.addBomb(pos.getX(), pos.getY());
        }

        for (int n = 0; n < 50; n++) {
            MSAgent agent = new MSAgent(16, 16, 40, this.grid.getVariable(0, 0));
            int toWin = 16 * 16 - 40;
            boolean win = true;
            while (toWin-- > 0) {
                Position next = agent.nextMove();
                if (b.containsBomb(next.getX(), next.getY())) {
                    win = false;
                    break;
                }
                agent.sendBackResult(next, b.adjacentBombs(next.getX(), next.getY()));
            }

            assertTrue(win);
            for (int i = 0; i < 40; i++) {
                assertTrue(bombs.contains(agent.markBomb()));
            }
            assertEquals(null, agent.markBomb());
        }
    }
}