import java.util.*;

abstract class VariableDecorator implements Variable {

    protected static Map<Integer, VariableDecorator> cache = new HashMap<>();
    private ConcreteVariable var;
    private List<Clause> watchedCls = new ArrayList<>();

    public VariableDecorator(ConcreteVariable variable) {
        this.var = variable;
    }

    static VariableDecorator of(int num, boolean isPositive) {
        if (cache.containsKey(num * 2 + (isPositive ? 1 : 0))) {
            return cache.get(num * 2 + (isPositive ? 1 : 0));
        }
        var aVariable = new ConcreteVariable(num);
        var p = new PositiveLiteral(aVariable);
        var n = new NegativeLiteral(aVariable);
        cache.put(num * 2, n);
        cache.put(num * 2 + 1, p);
        return isPositive ? p : n;
    }

    public abstract VariableDecorator opposite();

    /**
     Возращает значение переменной с учётом присутствия/отсутствия знака минус
     */
    abstract boolean getValue();

    abstract boolean isPositive();

    List<Clause> getWatchedCls() {
        return watchedCls;
    }

    void addToWatchedCls(Clause cl) {
        watchedCls.add(cl);
    }

    @Override
    public int getNumber() {
        return var.getNumber();
    }

    @Override
    public boolean isAssigned() {
        return var.isAssigned();
    }

    @Override
    public boolean getAssignment() {
        return var.getAssignment();
    }

    @Override
    public void setAssignment(boolean assignment, int decisionLevel) {
        var.setAssignment(assignment, decisionLevel);
    }

    @Override
    public int getDecisionLevel() {
        return var.getDecisionLevel();
    }

    @Override
    public boolean isDecisionVar() {
        return var.isDecisionVar();
    }

    @Override
    public void setDecisionVar() {
        var.setDecisionVar();
    }

    @Override
    public int getVsidsScore() {
        return var.getVsidsScore();
    }

    @Override
    public void increaseVsidsScore() {
        var.increaseVsidsScore();
    }

    @Override
    public void decreaseVsidsScore() {
        var.decreaseVsidsScore();
    }

    @Override
    public boolean isLocked() {
        return var.isLocked();
    }

    @Override
    public void lock() {
        var.lock();
    }

    @Override
    public void flip() {
        var.flip();
    }

    @Override
    public void clean() {
        var.clean();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableDecorator variable = (VariableDecorator) o;
        return var.equals(variable.var);
    }
}
