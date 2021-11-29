package agent;

import java.util.Set;

/**
 * Created by Jonni on 3/20/2017.
 *
 * A data structure for a single constraint.
 */
public class ConstraintInfo {

    private Set<Position> unknownNeighbours;
    private int adjacentBombs;

    /**
     * Constructs the constraint sum(unknownNeighbours) = adjacentBombs
     *
     * @param unknownNeighbours set of variables
     * @param adjacentBombs sum of variables
     */
    public ConstraintInfo(Set<Position> unknownNeighbours, int adjacentBombs) {
        this.unknownNeighbours = unknownNeighbours;
        this.adjacentBombs = adjacentBombs;
    }

    /**
     * @return sum of variables.
     */
    public int getAdjacentBombs() {
        return this.adjacentBombs;
    }

    /**
     * decreases constraint sum by one.
     */
    public void decrementAdjacentBombs() {
        this.adjacentBombs--;
    }

    /**
     * @return variables
     */
    public Set<Position> getUnknownNeighbours() {
        return this.unknownNeighbours;
    }

    /**
     * Removes a variable from the constraint.
     *
     * @param position variable
     */
    public void removeVariable(Position position) {
        this.unknownNeighbours.remove(position);
    }

    /**
     * Check if sum(variables) = count(variables) which means all must be bombs.
     *
     * @return true iff sum(variables) = count(variables)
     */
    public boolean allBombs() {
        return this.adjacentBombs == unknownNeighbours.size();
    }

    /**
     * Check if sum(variables) = 0 which means all must be clear (no bombs).
     *
     * @return true iff sum(variables) = 0
     */
    public boolean noBombs() {
        return this.adjacentBombs == 0;
    }

    /**
     * Check if constraint has variables.
     *
     * @return true if no variables remain in the constraint.
     */
    public boolean isEmpty() {
        return this.unknownNeighbours.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return this.unknownNeighbours.equals(((ConstraintInfo)o).unknownNeighbours);
    }

    @Override
    public int hashCode() {
        return this.unknownNeighbours.hashCode();
    }

    @Override
    public String toString() {
        return "[" + adjacentBombs + ", " + unknownNeighbours.toString() + "]";
    }
}
