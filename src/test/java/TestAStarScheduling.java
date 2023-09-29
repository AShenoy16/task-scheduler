import algorithm.astar.AstarScheduler;
import io.IOHandler;
import model.Graph;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAStarScheduling {
    final String directory = "src/test/graphs/";

    @Test
    public void TestAStar() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 1);

    }
}
