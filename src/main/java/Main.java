import algorithm.astar.AstarScheduler;
import io.IOHandler;
import model.Graph;
import model.Schedule;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int argsLength = args.length;
        if (args == null || argsLength < 2) {
            throw new RuntimeException("InputFileName or numProcessors arguments not supplied");
        }
        String inputFileName = args[0];
        String outputFileName = inputFileName + "-output.dot";
        int numProcessors = Integer.parseInt(args[1]);

        if(argsLength > 2){
            //Get option arguments
            outputFileName = getOutputFileName(args);
        }

        System.out.println("Starting schedule creation...");

        IOHandler io = new IOHandler();
        Graph graph = io.readDot(inputFileName);
        AstarScheduler scheduler = new AstarScheduler();
        Schedule schedule = scheduler.run(graph, numProcessors);


        io.writeDot(schedule, outputFileName);
    }

    public static String getOutputFileName(String[] args) {
        for (int i = 2; i < args.length; i++) {
            if ("-o".equals(args[i]) && i + 1 < args.length) {
                // get output file
                return args[i + 1];
            }
        }
        return ""; // Return an empty string if -o is not found or if it's not followed by a value
    }
}