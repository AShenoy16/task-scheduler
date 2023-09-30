import algorithm.astar.AstarScheduler;
import io.IOHandler;
import model.Graph;
import model.Schedule;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAStarScheduling {
    final String directory = "src/test/graphs/";
    Graph graph;
    AstarScheduler scheduler;
    Schedule schedule;

    @Test
    public void TestAStarTwoProcessorsNodes7() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertEquals(28, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes7() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertEquals(22, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes8() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_8_Random.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertEquals(581, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes8() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_8_Random.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertEquals(581, schedule.getCost());
    }

    @Test
    public void TestAStarTwoProcessorsNodes9() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_9_SeriesParallel.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertEquals(55, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes9() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_9_SeriesParallel.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertEquals(55, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes10() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_10_Random.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertEquals(50, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes10() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_10_Random.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertEquals(50, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes11() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_11_OutTree.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertEquals(350, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNode11() throws IOException {
        graph = IOHandler.readDot(directory + "Nodes_11_OutTree.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertEquals(227, schedule.getCost());
    }
    @After
    public void tearDown(){
        graph = null;
        scheduler = null;
        schedule = null;
    }
}
