import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class SolverTests {
    private final static String DIR_WITH_TEST_FILES = "res/testfiles";

    @ParameterizedTest
    @MethodSource("getAllFilenamesInDir")
    public void test(String filename) throws IOException {
        SATSolver solver = new SATSolver();
        var status = filename.contains("yes") ? Status.SATISFIABLE : Status.UNSATISFIABLE;
        Assertions.assertEquals(status, solver.parseAndGo(filename));
    }

    static List<String> getAllFilenamesInDir() throws IOException {
        return Files.walk(Path.of(DIR_WITH_TEST_FILES))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
    }
}
