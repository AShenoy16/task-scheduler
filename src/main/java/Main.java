import algorithm.astar.AstarScheduler;
import io.IOHandler;
import model.Graph;
import model.Schedule;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args == null || args.length < 2) {
            throw new RuntimeException("InputFileName or numProcessors arguments not supplied");
        }
        String inputFileName = args[0];
        String outputFileName = inputFileName + "-output.dot";
        int numProcessors = Integer.parseInt(args[1]);

        System.out.println("Starting schedule creation...");


        IOHandler io = new IOHandler();
        Graph graph = io.readDot(inputFileName);
        AstarScheduler scheduler = new AstarScheduler();
        Schedule schedule = scheduler.run(graph, numProcessors);


        io.writeDot(schedule, outputFileName);
    }
}