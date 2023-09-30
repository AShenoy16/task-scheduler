import algorithms.BranchAndBound;
import io.IOHandler;
import model.Graph;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBranchAndBound {
    final String directory = "src/test/graphs/";
    @Test
    public void RunBranchAndBound() throws IOException {
        Graph g = IOHandler.readDot(directory + "example.dot");
        BranchAndBound branchAndBound = new BranchAndBound();
        branchAndBound.run(2, g);
    }
}
