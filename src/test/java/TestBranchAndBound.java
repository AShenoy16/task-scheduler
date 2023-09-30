import algorithms.branchandbound.BranchAndBound;
import algorithms.branchandbound.Schedule;
import io.IOHandler;
import model.Graph;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBranchAndBound {
    final String directory = "src/test/graphs/";
    @Test
    public void RunBranchAndBound() throws IOException {
        Graph g = IOHandler.readDot(directory + "example.dot");
        BranchAndBound branchAndBound = new BranchAndBound();
        Schedule schedule = branchAndBound.run(2, g);
        assertEquals(schedule.getShortestPath(), 8);
        // need to test for tasks in schedules
    }
}
