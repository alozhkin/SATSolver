import java.util.*;

public class VariablesStack {
    private List<VariableDecorator> allVars;
    private List<VariableDecorator> freeVars;
    Deque<VariableDecorator> assignmentStack;

    VariablesStack(Collection<VariableDecorator> vars) {
        allVars = new ArrayList<>(vars);
        freeVars = new ArrayList<>(vars);
        assignmentStack = new ArrayDeque<>(vars.size());
    }

    List<VariableDecorator> getVars() {
        return allVars;
    }

    private VariableDecorator pollFreeVar() {
        var t = freeVars.stream().max(Comparator.comparingInt(VariableDecorator::getVsidsScore)).get();
        freeVars.remove(t);
        return t;
    }

    void addFreeVar(VariableDecorator aVariable) {
        var variable = VariableDecorator.of(aVariable.getNumber(), true);
        int addingIndex = findFirstBiggerOrEqual(freeVars, variable, Comparator.comparingInt(VariableDecorator::getVsidsScore));
        freeVars.add(addingIndex, variable);
    }

    void removeFreeVar(VariableDecorator aVariable) {
        var variable = VariableDecorator.of(aVariable.getNumber(), true);
        freeVars.remove(variable);
    }

    VariableDecorator pollAssignment() {
        var t = assignmentStack.pollFirst();
        if (!t.isLocked()) {
            VariableDecorator.of(t.getNumber(), t.isPositive()).clean();
            addFreeVar(t);
        }
        return t;
    }

    private void addAssignment(VariableDecorator aVariable) {
        var variable = VariableDecorator.of(aVariable.getNumber(), true);
        assignmentStack.addFirst(variable);
    }

    VariableDecorator peekAssignment() {
        return assignmentStack.peekFirst();
    }

    Status decide(int decisionLevel) {
        if (freeVars.size() == 0) return Status.SATISFIABLE;
        var t = pollFreeVar();
        t.setAssignment(true, decisionLevel);
        t.setDecisionVar();
        addAssignment(t);
        return Status.UNKNOWN;
    }

    void assign(VariableDecorator variable, boolean assignment, int decisionLevel) {
        removeFreeVar(variable);
        variable.setAssignment(assignment, decisionLevel);
        addAssignment(variable);
    }

    private static <T> int findFirstBiggerOrEqual(List<T> list, T n, Comparator<T> comparator) {
        int l = 0;
        int r = list.size() - 1;
        while (l <= r) {
            int m = (l + r) >>> 1;
            int compare1 = comparator.compare(list.get(m), n);
            if (compare1 >= 0) {
                if (m == l) return m;
                r = m;
            } else {
                l = m + 1;
            }
        }
        return list.size();
    }
}
