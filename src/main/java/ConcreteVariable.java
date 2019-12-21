class ConcreteVariable implements Variable {

    /**
     * Значение, которое присвоили переменной
    */
    private boolean assignment;
    private boolean assigned;
    private int number;
    private int decisionLevel;
    private boolean isDecisionVar;
    private int vsidsScore;
    private boolean locked;

    ConcreteVariable(int number) {
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public boolean isAssigned() {
        return assigned;
    }

    @Override
    public boolean getAssignment() {
        return assignment;
    }

    @Override
    public void setAssignment(boolean assignment, int decisionLevel) {
        this.assignment = assignment;
        this.decisionLevel = decisionLevel;
        assigned = true;
    }

    @Override
    public int getDecisionLevel() {
        return decisionLevel;
    }

    @Override
    public boolean isDecisionVar() {
        return isDecisionVar;
    }

    @Override
    public void setDecisionVar() {
        isDecisionVar = true;
    }

    @Override
    public int getVsidsScore() {
        return vsidsScore;
    }

    @Override
    public void increaseVsidsScore() {
        ++vsidsScore;
    }

    @Override
    public void decreaseVsidsScore() {
        vsidsScore >>= 1;
    }

    @Override
    public boolean isLocked() { return locked; }

    @Override
    public void lock() { locked = true; }

    @Override
    public void clean() {
        assigned = false;
        decisionLevel = 0;
        isDecisionVar = false;
    }

    @Override
    public void flip() {
        assignment = !assignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcreteVariable variable = (ConcreteVariable) o;
        return number == variable.number;
    }
}
