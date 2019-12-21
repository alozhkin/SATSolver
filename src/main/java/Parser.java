import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {
    private VariablesStack vars;
    private List<Clause> cls;

    VariablesStack getVars() {
        return vars;
    }

    public List<Clause> getCls() {
        return cls;
    }

    void parse(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        int i = -1;
        byte sym = bytes[++i];

        //skip comments
        while (sym == 'c') {
            while (sym != '\n')
                sym = bytes[++i];
            sym = bytes[++i];
        }

        //parse number of clauses and number of variables
        while (sym == 'p') {
            i += 2;
            sym = bytes[i];
            while (sym != ' ')
                sym = bytes[++i];

            sym = bytes[++i];
            while (sym != ' ') {
                sym = bytes[++i];
            }

            while (sym != '\n') {
                sym = bytes[++i];
            }
            ++i;
        }

        var clsSet = new HashSet<Clause>();
        Set<VariableDecorator> varsSet = new HashSet<>();

        //create set of variables. All clauses should be distributed between variables and stored in watchedCls
        //ignore: 1. repeats of clauses, 2. repeats of variables in one clause, 3. clauses that always true
        while (i <= bytes.length - 1) {

            sym = bytes[i];
            boolean clauseIsAlwaysTrue = false;
            Set<VariableDecorator> varsOfCl = new HashSet<>();

            while (sym != '0') {

                int varNum = 0;

                //x is positive, -x is not
                boolean isPositive = true;
                if (sym == '-') {
                    isPositive = false;
                    sym = bytes[++i];
                }

                while (sym != ' ') {
                    varNum *= 10;
                    varNum += sym - '0';
                    sym = bytes[++i];
                }

                //check if x and -x are in a same clause
                if (varsOfCl.contains(VariableDecorator.of(varNum, !isPositive))) {
                    while (sym != '0') {
                        while (sym != ' ') {
                            sym = bytes[++i];
                        }
                        sym = bytes[++i];
                    }
                    clauseIsAlwaysTrue = true;
                    break;
                }

                varsOfCl.add(VariableDecorator.of(varNum, isPositive));
                sym = bytes[++i];
            }

            if (!clauseIsAlwaysTrue) {
                var cl = new Clause();
                cl.setVars(varsOfCl.toArray(VariableDecorator[]::new));
                for (VariableDecorator variable: cl.getVars()) {
                    variable.increaseVsidsScore();
                }
                clsSet.add(cl);
            }

            for (VariableDecorator el: varsOfCl) {
                varsSet.add(VariableDecorator.of(el.getNumber(), true));
            }
            i += 2;
        }

        for (Clause cl: clsSet) {
            cl.setWatchedVars();
        }
        vars = new VariablesStack(varsSet);
        cls = new ArrayList<>(clsSet);
    }
}
