import algorithm.branchandbound.BranchAndBound;
import algorithm.branchandbound.BranchAndBoundParallel;
import algorithm.branchandbound.Schedule;
import io.IOHandler;
import model.Graph;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class TestBranchAndBound {
    final String directory = "src/test/graphs/";
    IOHandler io;
    Graph graph;
    BranchAndBound scheduler;
    BranchAndBoundParallel parallelScheduler;
    Schedule schedule;

    @Test
    public void TestBnBTwoProcessorsNodes7() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 2);
        assertEquals(28, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes7Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 2, 3);
        assertEquals(28, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes7() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 4);
        assertEquals(22, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes7Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 4, 3);
        assertEquals(22, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes8() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_8_Random.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 2);
        assertEquals(581, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes8Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_8_Random.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 2, 3);
        assertEquals(581, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes8() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_8_Random.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 4);
        assertEquals(581, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes8Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_8_Random.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 4, 3);
        assertEquals(581, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes9() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_9_SeriesParallel.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 2);
        assertEquals(55, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes9Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_9_SeriesParallel.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 2, 3);
        assertEquals(55, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes9() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_9_SeriesParallel.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 4);
        assertEquals(55, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes9Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_9_SeriesParallel.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 4, 3);
        assertEquals(55, schedule.getShortestPath());
    }

    @Test
    public void TestBnBTwoProcessorsNodes10() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_10_Random.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 2);
        assertEquals(50, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes10Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_10_Random.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 2, 3);
        assertEquals(50, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes10() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_10_Random.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 4);
        assertEquals(50, schedule.getShortestPath());
    }
    @Test
    public void TestBnBFourProcessorsNodes10Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_10_Random.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 4, 3);
        assertEquals(50, schedule.getShortestPath());
    }

    @Test
    public void TestBnBTwoProcessorsNodes11() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 2);
        assertEquals(350, schedule.getShortestPath());
    }
    @Test
    public void TestBnBTwoProcessorsNodes11Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 2, 3);
        assertEquals(350, schedule.getShortestPath());
    }

    @Test
    public void TestBnBFourProcessorsNode11() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 4);
        assertEquals(227, schedule.getShortestPath());
    }

    @Test
    public void TestBnBFourProcessorsNode11Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        parallelScheduler = new BranchAndBoundParallel();
        schedule = parallelScheduler.run(graph, 4, 3);
        assertEquals(227, schedule.getShortestPath());
    }

    @Test
    public void TestBNBOneProcessorsExample() {
        io = new IOHandler();
        graph = io.readDot(directory + "example.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 1);
        assertEquals(10, schedule.getShortestPath());
    }

    @Test
    public void TestBNBTwoProcessorsExample() {
        io = new IOHandler();
        graph = io.readDot(directory + "example.dot");
        scheduler = new BranchAndBound();
        schedule = scheduler.run(graph, 2);
        assertEquals(8, schedule.getShortestPath());
    }

    @After
    public void tearDown(){
        graph = null;
        scheduler = null;
        schedule = null;
        parallelScheduler = null;
    }
}
