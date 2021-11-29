package agent;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jonni on 3/20/2017.
 *
 * A CSP  model.
 */
public class MSModel {

    private Model model;
    private Map<Position, IntVar> varMap;

    /**
     * Creates a Choco model using the constraint in a given constraint group.
     *
     * @param constraints Constraints in a constraint group
     * @param variables Variables in a constraint group
     * @throws ContradictionException should never happen
     */
    public MSModel(Set<ConstraintInfo> constraints, Set<Position> variables) throws ContradictionException {
        this.model = new Model();
        this.varMap = new HashMap<>();

        // Map each variable to a Choco variable
        for (Position pos : variables) this.varMap.put(pos, this.model.intVar(pos.toString(), 0, 1));

        // Create Choco constraints from our constraints
        for (ConstraintInfo c : constraints) {
            IntVar[] con = new IntVar[c.getUnknownNeighbours().size()];
            int index = 0;
            for (Position pos : c.getUnknownNeighbours()) {
                con[index] = this.varMap.get(pos);
                index++;
            }
            this.model.sum(con, "=", c.getAdjacentBombs()).post();
        }

        // constraint propagation
        this.model.getSolver().propagate();
    }

    /**
     * Constructor that adds end game constraint, sum of all unknowns = remaining bombs
     *
     * @param constraint endgame constraint
     * @throws ContradictionException should never happen
     */
    public MSModel(EndGameConstraint constraint) throws ContradictionException {
        this.model = new Model();
        this.varMap = new HashMap<>();

        // Map each variable to a Choco variable
        for (Position pos : constraint.getVariables()) this.varMap.put(pos, this.model.intVar(pos.toString(), 0, 1));

        // Create Choco constraints from our constraints
        for (ConstraintInfo c : constraint.getConstraints()) {
            IntVar[] con = new IntVar[c.getUnknownNeighbours().size()];
            int index = 0;
            for (Position pos : c.getUnknownNeighbours()) {
                con[index] = this.varMap.get(pos);
                index++;
            }
            this.model.sum(con, "=", c.getAdjacentBombs()).post();
        }

        IntVar[] con = new IntVar[constraint.getVariables().size()];
        int index = 0;
        for (Position pos : constraint.getVariables()) {
            con[index] = varMap.get(pos);
            index++;
        }
        this.model.sum(con, "=", constraint.getBombsRemaining());

        // constraint propagation
        this.model.getSolver().propagate();
    }

    /**
     * Assume that a position does not contain a bomb, check if it
     * leads to a contradiction. If so, it must contain a bomb.
     *
     * @param position Variable
     * @return true if we can guarantee that it contains a bomb.
     */
    public boolean hasBomb(Position position) {
        return containsContradiction(model.arithm(varMap.get(position), "=", 0));
    }

    /**
     * Assume that a position contains a bomb, check if it
     * leads to a contradiction. If so, it must not contain a bomb.
     *
     * @param position Variable
     * @return true if we can guarantee that it contains no bomb.
     */
    public boolean hasNoBombs(Position position) {
        return containsContradiction(model.arithm(varMap.get(position), "=", 1));
    }

    /**
     * Use of Choco solver to see if we can find a solution given an assumption.
     *
     * @param assumption Constraint (Choco)
     * @return true iff no solution is found
     */
    private boolean containsContradiction(Constraint assumption) {
        model.getEnvironment().worldPush();
        model.post(assumption);
        Solution sol = model.getSolver().findSolution();
        model.getEnvironment().worldPop();
        model.unpost(assumption);
        model.getSolver().hardReset();
        return sol == null;
    }
}
