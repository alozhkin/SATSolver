interface Variable {
    int getNumber();

    boolean isAssigned();

    boolean getAssignment();

    void setAssignment(boolean assignment, int decisionLevel);

    int getDecisionLevel();

    boolean isDecisionVar();

    void setDecisionVar();

    int getVsidsScore();

    void increaseVsidsScore();

    void decreaseVsidsScore();

    boolean isLocked();

    void lock();

    void clean();

    void flip();
}
