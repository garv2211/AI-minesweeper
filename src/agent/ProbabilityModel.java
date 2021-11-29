package agent;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jonni on 3/22/2017.
 *
 * Sets up a Choco constraint model to find probability of variables.
 */
public class ProbabilityModel {

    private Model model;
    private Map<Position, IntVar> varMap;

    /**
     * Set up Choco model for the constraint group.
     *
     * @param info constraint group
     * @param variables all variables in the constraint group
     * @param varCollection a collection to store any variable for outside use
     * @throws ContradictionException should never happen
     */
    public ProbabilityModel(Set<ConstraintInfo> info, Set<Position> variables, Set<Position> varCollection) throws ContradictionException {
        this.model = new Model();
        this.varMap = new HashMap<>();

        for (Position pos : variables) {
            this.varMap.put(pos, this.model.intVar(pos.toString(), 0, 1));
            varCollection.add(pos);
        }
        for (ConstraintInfo c : info) {
            IntVar[] con = new IntVar[c.getUnknownNeighbours().size()];
            int index = 0;
            for (Position pos : c.getUnknownNeighbours()) {
                con[index] = this.varMap.get(pos);
                index++;
            }
            this.model.sum(con, "=", c.getAdjacentBombs()).post();
        }
        this.model.getSolver().propagate();
    }

    /**
     * Updates a map for probabilities for all variables in a constraint group.
     *
     * @param probabilityMap a map to update
     * @return minimum number of bombs for the constraint group
     */
    public int getProbabilities(Map<Position, Double> probabilityMap) {
        int minBombs = Integer.MAX_VALUE;

        // Initialize all probabilities as 0.0
        for (Position position : this.varMap.keySet()) probabilityMap.put(position, 0.0);

        // For a solution in the group of all solutions for the constraint group
        for (Solution solution : this.model.getSolver().findAllSolutions()) {
            int bombsThisSolution = 0;
            for (Position position : this.varMap.keySet()) {
                int value = solution.getIntVal(this.varMap.get(position));
                // If the position contains a bomb in this solution,
                // we add one to the probability map
                // The map works as a counter at this point.
                if (value == 1) {
                    probabilityMap.put(position, probabilityMap.get(position) + 1);
                    bombsThisSolution++;
                }
            }
            if (minBombs > bombsThisSolution) minBombs = bombsThisSolution;
        }
        // Convert counter to probabilities.
        long totalSolutions = this.model.getSolver().getSolutionCount();
        for (Position position : this.varMap.keySet()) {
            probabilityMap.put(position, 100.0 * probabilityMap.get(position) / totalSolutions);
        }

        return minBombs;
    }
}
