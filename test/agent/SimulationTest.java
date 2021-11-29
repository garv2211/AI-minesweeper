package agent;

import level.Board;
import level.RandomBoardGenerator;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jonni on 3/23/2017.
 */
public class SimulationTest {

    private RandomBoardGenerator bGen;
    private PositionGrid grid;

    @Before
    public void setUp() {
        this.grid = new PositionGrid(50, 50);
        this.bGen = new RandomBoardGenerator();
    }

    @Test
    public void playSimulations() {
        /* *************************** */
        /* Make true to run simulation */
        /* *************************** */
        boolean simulate = true;

        /* ********************************************** */
        /* Change to play different number of games       */
        /* The more the longer, but more accurate results */
        /* ********************************************** */
        int easyGamesToPlay = 1000;
        int mediumGamesToPlay = 1000;
        int hardGamesToPlay = 1000;


        if (simulate) {
            easy(easyGamesToPlay);
            medium(mediumGamesToPlay);
            hard(hardGamesToPlay);
        }
    }

    @Test
    public void customTest() {
        playManyGames(100, 50, 50, 400, false, false);
    }

    @Test
    public void customTest2() {
        playManyGames(1, 150, 150, 3800, false, false);
    }

    @Test
    public void customTest3() {
        // Takes time...
        //playManyGames(1, 500, 500, 40000, false, false);
    }

    private void easy(int games) {
        System.out.println("Easy");
        playManyGames(games, 10, 10, 8, false, false);
    }

    private void medium(int games) {
        System.out.println("Medium");
        playManyGames(games, 16, 16, 40, false, false);
    }

    private void hard(int games) {
        System.out.println("Hard");
        playManyGames(games, 24, 24, 99, false, false);
    }

    private void playManyGames(int games, int w, int h, int bombs, boolean flag1, boolean flag2) {
        int total = games;
        int won = 0;
        int lostOnFirst = 0;
        while (games-- > 0) {
            int result = playGame(w, h, bombs, flag1, flag2);
            if (result < 0) lostOnFirst++;
            else if (result > 0) won++;
        }
        showStats(total, won, lostOnFirst);
    }

    private void showStats(int total, int won, int lostFirst) {
        int totalLost = total - won;
        System.out.println("Total played: " + total);
        System.out.println("Total won: " + won);
        System.out.println("Total lost: " + totalLost);
        System.out.println("Total lost on first guess: " + lostFirst);
        System.out.println("Win ratio: " + (100.0 * won / total));
        System.out.println("Lost first out of lost games ratio: " + (100.0 * lostFirst / totalLost));
        System.out.println("Win ratio without first losses: " + ((100.0 * won) / (total - lostFirst)));
    }

    private int playGame(int w, int h, int bombs, boolean flag1, boolean flag2) {
        Board board = bGen.create(w, h, bombs, flag1, flag2);
        MSAgent agent = new MSAgent(w, h, bombs);

        int movesToWin = w * h - bombs;
        boolean won = true;

        movesToWin--;
        Position next = agent.nextMove();
        if (board.containsBomb(next.getX(), next.getY())) return -1;
        agent.sendBackResult(next, board.adjacentBombs(next.getX(), next.getY()));

        while (movesToWin-- > 0) {
            next = agent.nextMove();
            if (board.containsBomb(next.getX(), next.getY())) {
                won = false;
                break;
            }
            agent.sendBackResult(next, board.adjacentBombs(next.getX(), next.getY()));
        }

        System.out.println(movesToWin);
        return won ? 1 : 0;
    }
}
