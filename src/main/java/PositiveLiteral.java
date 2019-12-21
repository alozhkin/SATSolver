public class PositiveLiteral extends VariableDecorator {
    PositiveLiteral(ConcreteVariable variable) {
        super(variable);
    }

    @Override
    boolean isPositive() {
        return true;
    }

    @Override
    boolean getValue() {
        return super.getAssignment();
    }

    @Override
    public VariableDecorator opposite() {
        return cache.get(super.getNumber() << 1);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode((super.getNumber() << 1) + 1);
    }

    @Override
    public String toString() {
        return String.format("num=%d pos=%d as=%s dl=%d dv=%b",
                super.getNumber(), 1, super.isAssigned() ? super.getAssignment() ? 1 : 0 : "not", getDecisionLevel(),
                isDecisionVar());
    }
}
