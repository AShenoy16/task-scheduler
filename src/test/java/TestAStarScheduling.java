import algorithm.astar.AstarParallel;
import algorithm.astar.AstarScheduler;
import io.IOHandler;
import model.Graph;
import model.Schedule;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class TestAStarScheduling {
    final String directory = "src/test/graphs/";
    int numThreads = 4;
    IOHandler io;
    Graph graph;
    AstarScheduler scheduler;
    Schedule schedule;

    AstarParallel parallelSchduler;

    @Test
    public void TestAStarTwoProcessorsNodes7() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertEquals(28, schedule.getCost());
//        // Need to add output file name logic after CMD argument parsing is completed
//        // Example of how to write output. Possibly create separate IO test later.
//        io.writeDot(schedule,"Nodes_7_OutTree-OUTPUT.dot");
    }
    @Test
    public void TestAStarTwoProcessorsNodes7Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 2, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(28, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes7() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(22, schedule.getCost());
    }

    @Test
    public void TestAStarFourProcessorsNodes7Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 4, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(22, schedule.getCost());
    }

    @Test
    public void TestAStarTwoProcessorsNodes8() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_8_Random.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(581, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes8Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_8_Random.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 2, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(581, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes8() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_8_Random.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(581, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes8Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_8_Random.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 4, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(581, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes9() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_9_SeriesParallel.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(55, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes9Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_9_SeriesParallel.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 2, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(55, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes9() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_9_SeriesParallel.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(55, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes9Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_9_SeriesParallel.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 4, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(55, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes10() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_10_Random.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(50, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes10Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_10_Random.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 2, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(50, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes10() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_10_Random.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(50, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNodes10Paralell() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_10_Random.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 4, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(50, schedule.getCost());
    }
    @Test
    public void TestAStarTwoProcessorsNodes11() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 2);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(350, schedule.getCost());
    }

    @Test
    public void TestAStarTwoProcessorsNodes11Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 2, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(350, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNode11() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        scheduler = new AstarScheduler();
        schedule = scheduler.run(graph, 4);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(227, schedule.getCost());
    }
    @Test
    public void TestAStarFourProcessorsNode11Parallel() {
        io = new IOHandler();
        graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        parallelSchduler = new AstarParallel();
        schedule = parallelSchduler.run(graph, 4, numThreads);
        assertTrue(schedule.isValidScheduleNoOverlap());
        assertTrue(schedule.isValidScheduleSatisfyDependencies(graph));
        assertEquals(227, schedule.getCost());
    }


    @After
    public void tearDown(){
        graph = null;
        scheduler = null;
        schedule = null;
        parallelSchduler = null;
    }
}
