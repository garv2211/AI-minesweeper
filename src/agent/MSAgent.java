package agent;

import org.chocosolver.solver.exception.ContradictionException;

import java.util.*;

/**
 * Created by Jonni on 3/20/2017.
 *
 * The Minesweeper playing agent.
 */
public class MSAgent {

    private static final int END_GAME_MARK = 15;

    /* Temporary storage */
    private Set<Position> markedBombs;
    private Set<Position> unmarkedBombs;
    private Set<Position> history;
    private Set<Position> pendingMoves;

    private Random generator;
    private PositionGrid grid;
    private PerspectiveBoard board;

    private int width;
    private int height;
    private int bombs;
    private int initialBombCount;
    private int movesRemainingToWin;
    private boolean endgame;

    /**
     * Constructor for an agent with a random first move.
     *
     * @param width row size of actual board
     * @param height column size of actual board
     * @param bombs number of bombs in the actual board
     */
    public MSAgent(int width, int height, int bombs) {
        this.init(width, height, bombs);
        this.firstMove();
    }

    /**
     * Constructor for an agent with first move as parameter. This is
     * mainly for testing.
     *
     * @param width row size of actual board
     * @param height column size of actual board
     * @param bombs number of bombs in the actual board
     * @param first the first move for the player
     */
    public MSAgent(int width, int height, int bombs, Position first) {
        this.init(width, height, bombs);
        this.pendingMoves.add(first);
    }

    /**
     * Called by the GUI controller each turn until this returns null. Any
     * position passed to the controller will be marked.
     *
     * @return position to mark on GUI, null if none exist
     */
    public Position markBomb() {
        Position returnValue = null;
        while (!this.unmarkedBombs.isEmpty()) {
            Position bomb = nextBomb();
            if (!this.markedBombs.contains(bomb)) {
                this.markedBombs.add(bomb);
                returnValue = bomb;
                this.bombs--;
                break;
            }
        }
        return returnValue;
    }

    /**
     * Called by the GUI controller each turn.
     *
     * @return the next move from the agent.
     */
    public Position nextMove() {
        //stateCheck();

        Position next = null;

        // Are there any moves bending?
        while (!this.pendingMoves.isEmpty()) {
            Position nextMove = nextPending();
            if (!this.history.contains(nextMove)) {
                next = nextMove;
                this.history.add(nextMove);
                break;
            }
        }

        // If not, search for one
        if (next == null) {
            findMove();
            next = nextPending();
            this.history.add(next);
        }

        this.movesRemainingToWin--;
        return next;
    }

    /**
     * This is called by the GUI controller to pass back the adjacent number
     * for the square clicked by the agent. This is the only communication
     * from the GUI to the agent.
     *
     * @param position position of adjacent number
     * @param adjacent adjacent number in actual board
     */
    public void sendBackResult(Position position, int adjacent) {
        this.board.setAdjacent(
                position.getX(),
                position.getY(),
                adjacent,
                this.grid,
                this.pendingMoves,
                this.unmarkedBombs
        );

    }

    /**
     * Initializes data structures and counters.
     *
     * @param width row size of actual board
     * @param height column size of actual board
     * @param bombs number of bombs in the actual board
     */
    private void init(int width, int height, int bombs) {
        this.markedBombs = new HashSet<>();
        this.unmarkedBombs = new HashSet<>();
        this.history = new HashSet<>();
        this.generator = new Random();
        this.pendingMoves = new HashSet<>();
        this.board = new PerspectiveBoard(width, height);
        this.grid = new PositionGrid(width, height);
        this.width = width;
        this.height = height;
        this.bombs = bombs;
        this.initialBombCount = bombs;
        this.endgame = false;
        this.movesRemainingToWin = this.width * this.height - this.bombs;
    }

    /**
     * Adds the first move to the pending moves. It avoids edges.
     */
    private void firstMove() {
        this.pendingMoves.add(
                this.grid.getVariable(
                        1 + this.generator.nextInt(this.width - 2),
                        1 + this.generator.nextInt(this.height - 2)
                )
        );
    }

    /**
     * Search moves if non pending.
     */
    private void findMove() {
        if (!search()) {
            if (this.movesRemainingToWin <= MSAgent.END_GAME_MARK) {
                this.endgame = true;
            }
            if (this.endgame) {
                if (endGameSearch()) {
                    return;
                }
            }
            guess();
        }
    }

    private boolean endGameSearch() {
        boolean found = false;
        Stack<Position> bombs = new Stack<>();

        EndGameConstraint constraints = new EndGameConstraint(this.board, this.initialBombCount, this.width, this.height, this.grid);
        try {
            MSModel model = new MSModel(constraints);
            for (Position pos : constraints.getVariables()) {
                if (model.hasBomb(pos)) bombs.add(pos);
                else if (model.hasNoBombs(pos)) {
                    found = true;
                    this.pendingMoves.add(pos);
                }
            }
        } catch (ContradictionException ex) {
            System.out.println("something wrong with model! debug!");
        }

        boolean searchAgain = !found && !bombs.isEmpty();
        while (!bombs.isEmpty()) {
            Position position = bombs.pop();
            this.board.manualSetBombAt(position.getX(), position.getY(), this.grid, this.pendingMoves, this.unmarkedBombs);
        }
        return searchAgain ? endGameSearch() : found;
    }

    /**
     * @return true if a move was found (that is, some square that must not contain a bombs)
     */
    private boolean search() {
        boolean found = false;
        // Any found bombs are set after the search
        Stack<Position> bombs = new Stack<>();
        ConstraintGroups cGroups = new ConstraintGroups(this.board);
        for (Map.Entry<Set<ConstraintInfo>, Set<Position>> entry : cGroups.getGroups().entrySet()) {
            found = searchGroup(entry, bombs);
        }
        boolean searchAgain = !found && !bombs.isEmpty();
        while (!bombs.isEmpty()) {
            Position position = bombs.pop();
            this.board.manualSetBombAt(position.getX(), position.getY(), this.grid, this.pendingMoves, this.unmarkedBombs);
        }
        // If only known bombs are found, we search again since some might result in a newly found 'known-no-bomb'
        return searchAgain ? search() : found;
    }

    /**
     * Searches for guarantees in a constraint group.
     *
     * @param entry a tuple of constraint group and all of its variables
     * @param bombs a collection of bombs which stores any found bombs
     * @return true iff a non-bomb square is found
     */
    private boolean searchGroup(Map.Entry<Set<ConstraintInfo>, Set<Position>> entry, Stack<Position> bombs) {
        boolean found = false;
        try {
            MSModel model = new MSModel(entry.getKey(), entry.getValue());
            for (Position position : entry.getValue()) {
                if (model.hasNoBombs(position)) {
                    this.pendingMoves.add(position);
                    found = true;
                }
                else if (model.hasBomb(position)) bombs.add(position);
            }
        } catch (ContradictionException e) {
            System.out.println("Something wrong with the csp model!");
        }
        return found;
    }

    /**
     * Adds the most likely non-bomb to the pending moves.
     */
    private void guess() {
        if (!this.pendingMoves.isEmpty()) return;
        Map<Position, Double> probabilities = new HashMap<>();                  // Probability map for variables
        Set<Position> variables = new HashSet<>();                              // Collects all variables in all groups
        ConstraintGroups cGroups = new ConstraintGroups(this.board);            // Constraint groups
        int bombsOutsideVariables = this.bombs - this.unmarkedBombsCounter();   // Bomb counter for squares outside variables

        // Get probability for each group of variables
        for (Map.Entry<Set<ConstraintInfo>, Set<Position>> entry : cGroups.getGroups().entrySet()) {
            try {
                ProbabilityModel pModel = new ProbabilityModel(entry.getKey(), entry.getValue(), variables);
                // Subtract the minimum amount of bombs a solution can have from the bomb counter for non-variables
                bombsOutsideVariables -= pModel.getProbabilities(probabilities);
            } catch (ContradictionException e) {
                System.out.println("Something wrong with the csp model!");
            }
        }

        // Sorting
        PriorityQueue<Map.Entry<Position, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        for (Map.Entry<Position, Double> entry : probabilities.entrySet()) {
            pq.add(entry);
        }


        // All unknown non variables
        ArrayList<Position> unknownNonVariables = getUnknownNonVariables(variables);

        // Cases:
        // 1: No variables, we add a random from unknown
        // 2: No unknown, we add the least likely bomb from the probability map
        // 3: Neither empty, we add the least likely out of [least likely variable, random unknown non-variable]

        if (pq.isEmpty()) {
            this.pendingMoves.add(unknownNonVariables.get(this.generator.nextInt(unknownNonVariables.size())));
        } else if (unknownNonVariables.isEmpty()) {
            this.pendingMoves.add(randomLowestProbability(pq));
        } else {
            double probabilityOfUnknowns = (100.0 * bombsOutsideVariables) / unknownNonVariables.size();
            if (probabilityOfUnknowns < pq.peek().getValue()) {
                this.pendingMoves.add(unknownNonVariables.get(this.generator.nextInt(unknownNonVariables.size())));
            } else {
                this.pendingMoves.add(randomLowestProbability(pq));
            }
        }
    }

    private Position randomLowestProbability(PriorityQueue<Map.Entry<Position, Double>> sortedProbabilities) {
        ArrayList<Map.Entry<Position, Double>> lowProb = new ArrayList<>();
        lowProb.add(sortedProbabilities.poll());
        double prob = lowProb.get(0).getValue();
        while (!sortedProbabilities.isEmpty() && sortedProbabilities.peek().getValue() == prob) {
            lowProb.add(sortedProbabilities.poll());
        }
        return lowProb.get(this.generator.nextInt(lowProb.size())).getKey();
    }

    /**
     * Pop mechanism for a set.
     *
     * @return some unmarked bomb
     */
    private Position nextBomb() {
        Iterator<Position> it = this.unmarkedBombs.iterator();
        Position bomb = it.next();
        it.remove();
        return bomb;
    }

    /**
     * Pop mechanism for a set.
     *
     * @return some pending move
     */
    private Position nextPending() {
        Iterator<Position> it = pendingMoves.iterator();
        Position pos = it.next();
        it.remove();
        return pos;
    }

    /**
     * @param variables the known variables
     * @return the unknown non variables as a list
     */
    public ArrayList<Position> getUnknownNonVariables(Set<Position> variables) {
        ArrayList<Position> unknownNonVars = new ArrayList<>();
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                if (
                        this.board.getBoard()[i][j] == PerspectiveBoard.UNKNOWN &&
                        !variables.contains(this.grid.getVariable(i, j)) &&
                        !this.unmarkedBombs.contains(this.grid.getVariable(i, j))
                ) {
                    unknownNonVars.add(this.grid.getVariable(i, j));
                }
            }
        }
        return unknownNonVars;
    }

    /**
     * Counts number of unmarked bombs
     *
     * @return number of unmarked bombs
     */
    private int unmarkedBombsCounter() {
        int counter = 0;
        for (Position position : this.unmarkedBombs) {
            if (!this.markedBombs.contains(position)) counter++;
        }
        return counter;
    }
}
