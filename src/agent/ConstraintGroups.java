package agent;

import java.util.*;

/**
 * Created by Jonni on 3/20/2017.
 *
 * Data structure for a groups of constraints. For each group, all constraints
 * have some variable in common with some other constraint in the group. There
 * is no common variables between any two groups. This makes them independent
 * and we can search one without using the variables of the other. Having n
 * boolean variables, we have 2^n possible assignments (with brute force) but
 * say we can split them into 4 equal independent groups, then each group
 * has 2^(n/4) possible assignment of which there are 4, a total of 4*2^(n/4),
 * a considerable improvement.
 */
public class ConstraintGroups {

    // A map to all variables for the group is used to simplify usage of the Choco framework.
    private Map<Set<ConstraintInfo>, Set<Position>> groups;

    /**
     * Constructs constraints group for a given agent knowledge.
     * @param board The board as the agent sees it.
     */
    public ConstraintGroups(PerspectiveBoard board) {
        this.groups = new HashMap<>();
        for (ConstraintInfo info : board.getConstraintPositions().values()) add(info);
    }

    /**
     * @return A map with constraint groups as keys, all variables of each group as key.
     */
    public Map<Set<ConstraintInfo>, Set<Position>> getGroups() {
        return this.groups;
    }

    /**
     * @return true if agent's current knowledge do not result in any constraints.
     */
    public boolean isEmpty() {
        return this.groups.isEmpty();
    }

    /**
     * Adds a new constraint to the groups of constraint group.
     * Any group that contains any of its variable is added to
     * a stack and passed to another method to merge with all
     * groups in the stack, into which we add the new constraint.
     * If no such group exist, we create a new group.
     *
     * @param info Constraint
     */
    private void add(ConstraintInfo info) {
        Stack<Set<ConstraintInfo>> toMerge = new Stack<>();
        for (Map.Entry<Set<ConstraintInfo>, Set<Position>> entry : groups.entrySet()) {
            for (Position variable : info.getUnknownNeighbours()) {
                if (entry.getValue().contains(variable)) {
                    toMerge.push(entry.getKey());
                    break;
                }
            }
        }
        if (toMerge.isEmpty()) {
            Set<ConstraintInfo> newSet = new HashSet<>();
            newSet.add(info);
            groups.put(newSet, info.getUnknownNeighbours());
        } else {
            merge(toMerge, info);
        }
    }

    /**
     * Removes all sets in the stack from the map, merges them, adds the
     * new constraint to them and adds the merged set into the map.
     *
     * @param toMerge Stack of constraint groups
     * @param info constraint
     */
    private void merge(Stack<Set<ConstraintInfo>> toMerge, ConstraintInfo info) {
        Set<ConstraintInfo> keySet = new HashSet<>();
        Set<Position> valueSet = new HashSet<>();
        while (!toMerge.isEmpty()) {
            Set<ConstraintInfo> temp = toMerge.pop();
            valueSet.addAll(groups.get(temp));
            keySet.addAll(temp);
            groups.remove(temp);
        }
        keySet.add(info);
        valueSet.addAll(info.getUnknownNeighbours());
        groups.put(keySet, valueSet);
    }
}
