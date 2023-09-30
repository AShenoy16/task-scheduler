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
    @Test
    public void TestAStarTwoProcessorsNodes7() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 2);
        assertEquals(28, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes7() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 4);
        assertEquals(22, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes8() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_8_Random.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 2);
        assertEquals(581, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes8() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_8_Random.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 4);
        assertEquals(581, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes9() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_9_SeriesParallel.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 2);
        assertEquals(55, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes9() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_9_SeriesParallel.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 4);
        assertEquals(55, schedule.getCost());
    }

    @Test
    public void TestAStarTwoProcessorsNodes10() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_10_Random.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 2);
        assertEquals(50, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes10() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_10_Random.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 4);
        assertEquals(50, schedule.getCost());
    }

    @Test
    public void TestAStarTwoProcessorsNodes11() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_11_OutTree.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 2);
        assertEquals(350, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNode11() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_11_OutTree.dot");
        AstarScheduler scheduler = new AstarScheduler();
        var schedule = scheduler.run(g, 4);
        assertEquals(227, schedule.getCost());
    }
}
