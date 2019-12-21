import java.io.IOException;
import java.util.*;

public class SATSolver {

    private VariablesStack vars;
    private int decisionLevel = 0;
    private Deque<VariableDecorator> unitVars = new ArrayDeque<>();
    private Map<Integer, Clause> unitCls = new HashMap<>();
    private Clause conflictedClause;

    public Status parseAndGo(String fn) throws IOException {
        Stopwatch.start();
        var parser = new Parser();
        var solver = new SATSolver();
        parser.parse(fn);
        solver.vars = parser.getVars();
        var res = solver.CDCL();
        VariableDecorator.cache.clear();
        Stopwatch.stop("SAT solver");
        System.out.println(res);
        return res;
    }

    Status CDCL() {
        var status = Status.UNKNOWN;
        while (true) {
            if (decide() == Status.SATISFIABLE) {
                return Status.SATISFIABLE;
            }
            while (true) {
                status = deduce();
                if (status == Status.CONFLICT) {
                    var backLevel = analyzeConflict();
                    if (backLevel == 0) {
                        return Status.UNSATISFIABLE;
                    }
                    backtrack(backLevel);
                } else {
                    break;
                }
            }
        }
    }

    private Status decide() {
        return vars.decide(++decisionLevel);
    }

    private Status deduce() {
        var t = vars.peekAssignment();
        var decidedVar = VariableDecorator.of(t.getNumber(), t.getAssignment());
        var watchedCls = decidedVar.getWatchedCls();
        var watchedClsIter = watchedCls.listIterator();
        while (watchedClsIter.hasNext()) {
            var watchedCl = watchedClsIter.next();
            var watchedClVars = watchedCl.getVars();
            int beginIndex = decidedVar.getNumber() == watchedClVars[watchedCl.getIndexOfWatchedLiteral1()].getNumber()
                    ? watchedCl.getIndexOfWatchedLiteral1() :
                    watchedCl.getIndexOfWatchedLiteral2();
            int endIndex = beginIndex == watchedCl.getIndexOfWatchedLiteral1() ?
                    watchedCl.getIndexOfWatchedLiteral2() :
                    watchedCl.getIndexOfWatchedLiteral1();
            int pointer = (beginIndex + 1) % watchedClVars.length;
            boolean onlyFalseVarsFound = true;
            while (pointer != beginIndex) {
                var variable = watchedClVars[pointer];
                if (pointer != endIndex && (!variable.isAssigned() || variable.getValue())) {
                    watchedClsIter.remove();
                    variable.opposite().addToWatchedCls(watchedCl);
                    if (beginIndex == watchedCl.getIndexOfWatchedLiteral1()) {
                        watchedCl.setIndexOfWatchedLiteral1(pointer);
                    } else {
                        watchedCl.setIndexOfWatchedLiteral2(pointer);
                    }
                    onlyFalseVarsFound = false;
                    break;
                }
                ++pointer;
                pointer %= watchedClVars.length;
            }
            if (onlyFalseVarsFound) {
                var variable = watchedClVars[endIndex];
                if (!variable.isAssigned()) {
                    if (unitCls.containsKey(variable.getNumber())) {
                        unitCls.get(variable.getNumber()).union(watchedCl);
                    } else {
                        unitVars.addLast(variable);
                        unitCls.put(variable.getNumber(), watchedCl);
                    }
                } else if (!variable.getValue()) {
                    conflictedClause = watchedCl;
                    return Status.CONFLICT;
                }
            }
        }
        if (unitVars.size() != 0) return unitPropagate();
        unitCls.clear();
        return Status.UNKNOWN;
    }

    private Status unitPropagate() {
        var variable = unitVars.pollFirst();
        vars.assign(variable, variable.isPositive(), decisionLevel);
        return deduce();
    }

    private int analyzeConflict() {
        var cl = conflictedClause;
        while (!stopCriterionMet(cl)) {
            var temp = vars.peekAssignment();
            var ante = unitCls.get(temp.getNumber());
            var lastAssignedVar = vars.pollAssignment();
            cl = resolve(cl, ante, lastAssignedVar);
        }

        for (VariableDecorator variable: vars.getVars()) {
            variable.decreaseVsidsScore();
        }

        int maxLevel = -1;
        int indexOfMaxLevel = -1;
        var varsArray = cl.getVars();

        for (int i = 0; i < varsArray.length; ++i) {
            if (varsArray[i].getDecisionLevel() > maxLevel
                    && !varsArray[i].isLocked())
            {
                maxLevel = varsArray[i].getDecisionLevel();
                indexOfMaxLevel = i;
            }
        }
        if (indexOfMaxLevel == -1) {
            return 0;
        }

        int assertionLevel = -1;
        int indexOfAssertionVariable = -1;
        for (int i = 0; i < varsArray.length; ++i) {
            if (varsArray[i].getDecisionLevel() > assertionLevel
                    && varsArray[i].getDecisionLevel() != maxLevel
                    && !varsArray[i].isLocked())
            {
                assertionLevel = varsArray[i].getDecisionLevel();
                indexOfAssertionVariable = i;
            }
        }

        if (indexOfAssertionVariable == -1) {
            varsArray[indexOfMaxLevel].flip();
            varsArray[indexOfMaxLevel].lock();
            return maxLevel;
        } else {
            cl.setIndexOfWatchedLiteral1(indexOfMaxLevel);
            cl.setIndexOfWatchedLiteral2(indexOfAssertionVariable);
            varsArray[indexOfMaxLevel].opposite().addToWatchedCls(cl);
            varsArray[indexOfAssertionVariable].opposite().addToWatchedCls(cl);
            return assertionLevel;
        }
    }

    Clause resolve(Clause cl1, Clause cl2, VariableDecorator variable) {
        var set = new HashSet<VariableDecorator>();
        for (VariableDecorator el: cl1.getVars()) {
            if (el.getNumber() != variable.getNumber()) {
                set.add(el);
            }
        }
        for (VariableDecorator el: cl2.getVars()) {
            if (el.getNumber() != variable.getNumber()) {
                set.add(el);
            }
        }
        var vars = set.toArray(new VariableDecorator[0]);
        var cl = new Clause();
        cl.setVars(vars);
        return cl;
    }

    private boolean stopCriterionMet(Clause cl) {
        int onDecisionLevelCount = 0;
        boolean dvFlag = false;
        for (VariableDecorator variable: cl.getVars()) {
            if (variable.isDecisionVar() && variable.getDecisionLevel() == decisionLevel) {
                dvFlag = true;
            } else if (!variable.isLocked() && variable.getDecisionLevel() == decisionLevel) {
                onDecisionLevelCount++;
            }
        }
        return onDecisionLevelCount == 0 && dvFlag;
    }

    private void backtrack(int backLevel) {
        var t = vars.peekAssignment();
        while (t.getDecisionLevel() != backLevel || !t.isDecisionVar()) {
            vars.pollAssignment();
            t = vars.peekAssignment();
        }
        decisionLevel = backLevel;
        unitCls.clear();
        unitVars.clear();
    }
}
