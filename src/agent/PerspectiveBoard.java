package agent;

import java.util.*;

/**
 * Created by Jonni on 3/20/2017.
 *
 * The board from the perspective of the agent.
 */


public class PerspectiveBoard {

    public static final byte UNKNOWN = -1;
    public static final byte BOMB = 10;

    private Map<Position, ConstraintInfo> constraintPositions;
    private byte[][] board;

    private Set<Position> containsBombSet;
    private Set<Position> removeSet;

    /**
     * Initializes the board with all squares set as unknown.
     *
     * @param width row length of actual board
     * @param height column length of actual board
     */
    public PerspectiveBoard(int width, int height) {
        this.containsBombSet = new HashSet<>();
        this.removeSet = new HashSet<>();
        this.constraintPositions = new HashMap<>();
        board = new byte[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                board[i][j] = UNKNOWN;
            }
        }
    }

    /**
     * Update knowledge. Set bomb at (x,y). Any constraint containing this position will
     * have this variable removed and reduced their bomb count by one. The neighbouring
     * constraint are sent to a helper method to explore further simplification.
     *
     * @param x coordinate
     * @param y coordinate
     * @param grid pre-allocated memory for all possible positions
     * @param moves the set of pending moves for the agent
     * @param bombs a set of position for the agent to mark on GUI
     */
    public void setBombAt(int x, int y, PositionGrid grid, Set<Position> moves, Set<Position> bombs) {
        board[x][y] = BOMB;
        bombs.add(grid.getVariable(x,y));
        for (Position position : grid.getNeighbours(x,y)) {
            ConstraintInfo info;
            if ((info = constraintPositions.get(position)) != null) {
                info.decrementAdjacentBombs();
                info.removeVariable(grid.getVariable(x, y));
                storeSimplifications(info, position, moves);
            }
        }
    }

    /**
     * Same as setBombAt but empties temp sets when done.
     *
     * @param x coordinate
     * @param y coordinate
     * @param grid pre-allocated memory for all possible positions
     * @param moves the set of pending moves for the agent
     * @param bombs a set of position for the agent to mark on GUI
     */
    public void manualSetBombAt(int x, int y, PositionGrid grid, Set<Position> moves, Set<Position> bombs) {
        setBombAt(x, y, grid, moves, bombs);
        emptyTempSets(grid, moves, bombs);
    }

    /**
     * Update knowledge. Mark (x,y) to have no bomb. Each neighbour is explored and each unknown is
     * made into a variable. The adjacent number is decreased for each adjacent bomb we already know
     * of. If there is a constraint in any of the neighbouring position, (x,y) is removed from them
     * as a variable and the constraint is sent to a helper function for further simplification. If
     * the newly formed constraint is trivial, we add them to pending moves if =0 or temp bomb set
     * if all bombs, otherwise a new constraint is added to our system.
     *
     * @param x coordinate
     * @param y coordinate
     * @param adjacent number of adjacent bombs in the actual board
     * @param grid pre-allocated position memory
     * @param moves set of pending moves
     * @param bombs set of bombs to mark in GUI
     */
    public void setAdjacent(int x, int y, int adjacent, PositionGrid grid, Set<Position> moves, Set<Position> bombs) {
        board[x][y] = (byte)adjacent;
        Set<Position> newVariables = new HashSet<>();
        for (Position position : grid.getNeighbours(x,y)) {
            if (board[position.getX()][position.getY()] == UNKNOWN) {
                newVariables.add(position);
            } else if (board[position.getX()][position.getY()] == BOMB) {
                adjacent--;
            } else {
                ConstraintInfo info;
                if ((info = constraintPositions.get(position)) != null) {
                    info.removeVariable(grid.getVariable(x, y));
                    storeSimplifications(info, position, moves);
                }
            }
        }
        if (newVariables.size() == adjacent) {
            for (Position position : newVariables) {
                containsBombSet.add(position);
            }
        }
        else if (adjacent == 0) moves.addAll(newVariables);
        else this.constraintPositions.put(grid.getVariable(x, y), new ConstraintInfo(newVariables, adjacent));

        // Handle all temps sets
        emptyTempSets(grid, moves, bombs);
    }

    /**
     * @return mapping from board position to the constraint it forms
     */
    public Map<Position, ConstraintInfo> getConstraintPositions() {
        return this.constraintPositions;
    }

    /**
     * @return the board from the perspective of the agent
     */
    public byte[][] getBoard() {
        return this.board;
    }

    /**
     * When we are done updating, we update our knowledge for all bombs we discovered. We do not
     * update the non-bombs since they will be updated when we pop them from the pending moves.
     *
     * @param grid allocated memory for positions
     * @param moves pending moves
     * @param bombs unmarked bombs
     */
    private void emptyTempSets(PositionGrid grid, Set<Position> moves, Set<Position> bombs) {
        while (!containsBombSet.isEmpty()) {
            Iterator<Position> it = containsBombSet.iterator();
            Position bomb = it.next();
            it.remove();
            if (this.board[bomb.getX()][bomb.getY()] != PerspectiveBoard.BOMB) {
                setBombAt(bomb.getX(), bomb.getY(), grid, moves, bombs);
            }
        }
        for (Position pos : this.removeSet) {
            this.constraintPositions.remove(pos);
        }
    }

    /**
     * Any constraints that have a variable removed are passed to this method. It check if it has become
     * trivial or empty. If no bombs, we add all it's variables to the set of pending moves. If all bombs,
     * we add all variables to a temporary bomb list which will be explored later. For all these 3 cases,
     * we add the position to a remove temp set, which we later use to remove the constraint.
     *
     * @param info constraint
     * @param position coordinates
     * @param moves set of pending moves
     */
    private void storeSimplifications(ConstraintInfo info, Position position, Set<Position> moves) {
        if (info.isEmpty()) {
            this.removeSet.add(position);
        } else if (info.noBombs()) {
            moves.addAll(info.getUnknownNeighbours());
            this.removeSet.add(position);
        } else if (info.allBombs()) {
            for (Position pos : info.getUnknownNeighbours()) this.containsBombSet.add(pos);
            this.removeSet.add(position);
        }
    }
}