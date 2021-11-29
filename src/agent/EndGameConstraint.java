package agent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jonni on 3/23/2017.
 */
public class EndGameConstraint {

    private Set<ConstraintInfo> constraints;
    private Set<Position> variables;
    private int bombsRemaining;

    public EndGameConstraint(PerspectiveBoard board, int totalBombs, int w, int h, PositionGrid grid) {
        this.constraints = new HashSet<>();
        this.variables = new HashSet<>();
        for (ConstraintInfo constraint : board.getConstraintPositions().values()) {
            this.constraints.add(constraint);
            this.variables.addAll(constraint.getUnknownNeighbours());
        }
        this.bombsRemaining = totalBombs;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (board.getBoard()[i][j] == PerspectiveBoard.BOMB) {
                    this.bombsRemaining--;
                } else if (board.getBoard()[i][j] == PerspectiveBoard.UNKNOWN) {
                    this.variables.add(grid.getVariable(i, j));
                }
            }
        }
    }

    public Set<Position> getVariables() {
        return this.variables;
    }

    public Set<ConstraintInfo> getConstraints() {
        return this.constraints;
    }

    public int getBombsRemaining() {
        return this.bombsRemaining;
    }
}
