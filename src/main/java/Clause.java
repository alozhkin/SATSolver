import java.util.Arrays;

class Clause {
    private VariableDecorator[] vars;
    private int indexOfWatchedLiteral1;
    private int indexOfWatchedLiteral2;

    int getIndexOfWatchedLiteral1() {
        return indexOfWatchedLiteral1;
    }

    void setIndexOfWatchedLiteral1(int indexOfWatchedLiteral1) {
        this.indexOfWatchedLiteral1 = indexOfWatchedLiteral1;
    }

    int getIndexOfWatchedLiteral2() {
        return indexOfWatchedLiteral2;
    }

    void setIndexOfWatchedLiteral2(int indexOfWatchedLiteral2) {
        this.indexOfWatchedLiteral2 = indexOfWatchedLiteral2;
    }

    VariableDecorator[] getVars() {
        return vars;
    }

    void setVars(VariableDecorator[] vars) {
        this.vars = vars;
    }

    void setWatchedVars() {
        indexOfWatchedLiteral1 = 0;
        vars[indexOfWatchedLiteral1].opposite().addToWatchedCls(this);
        indexOfWatchedLiteral2 = vars.length - 1;
        vars[indexOfWatchedLiteral2].opposite().addToWatchedCls(this);
    }

    Clause union(Clause o) {
        VariableDecorator[] ar = new VariableDecorator[vars.length + o.vars.length];
        System.arraycopy(vars, 0, ar, 0, vars.length);
        System.arraycopy(o.vars, 0, ar, vars.length, o.vars.length);
        var cl = new Clause();
        cl.setVars(ar);
        return cl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause = (Clause) o;
        return Arrays.equals(vars, clause.vars);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vars);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (VariableDecorator el: vars) {
            sb.append("(");
            sb.append(el.toString());
            sb.append(")");
            sb.append("; ");
        }
        return sb.toString();
    }
}
