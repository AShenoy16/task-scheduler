import algorithms.BranchAndBound;
import algorithms.Schedule;
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
    public void TestBranchAndBound() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 1);
    }
    @Test
    public void TestBnBTwoProcessorsNodes7() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 2);
        assertEquals(28, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes7() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 4);
        assertEquals(22, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes8() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_8_Random.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 2);
        assertEquals(581, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes8() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_8_Random.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 4);
        assertEquals(581, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes9() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_9_SeriesParallel.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 2);
        assertEquals(55, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes9() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_9_SeriesParallel.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 4);
        assertEquals(55, schedule.getShortestPath());
    }

    @Test
    public void TestBnBTwoProcessorsNodes10() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_10_Random.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 2);
        assertEquals(50, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes10() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_10_Random.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 4);
        assertEquals(50, schedule.getShortestPath());
    }

    @Test
    public void TestBnBTwoProcessorsNodes11() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_11_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 2);
        assertEquals(350, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNode11() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_11_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        Schedule schedule = scheduler.run(g, 4);
        assertEquals(227, schedule.getShortestPath());
    }
}
