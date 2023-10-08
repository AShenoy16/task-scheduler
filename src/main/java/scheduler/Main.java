package scheduler;
import algorithm.astar.AstarScheduler;
import controller.App;
import io.IOHandler;
import model.Graph;
import model.Schedule;
import javafx.stage.Stage;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        visualise();
//        int argsLength = args.length;
//        if (args == null || argsLength < 2) {
//            throw new RuntimeException("InputFileName or numProcessors arguments not supplied");
//        }
//        String inputFileName = args[0];
//        String outputFileName = inputFileName + "-output.dot";
//
//        // remove .dot extension from input file name when including in output file name
//        int lastDotIndex = inputFileName.lastIndexOf('.');
//        if (lastDotIndex > 0) {
//            // Remove the file extension
//            outputFileName = inputFileName.substring(0, lastDotIndex) + "-output.dot";
//        }
//
//        int numProcessors = Integer.parseInt(args[1]);
//
//        if(argsLength > 2){
//            //Get option arguments
//            String customName =getOutputFileName(args);
//            if(customName != null){
//                outputFileName = customName;
//            }
//        }
//
//        System.out.println("Starting schedule creation...");
//
//        IOHandler io = new IOHandler();
//        Graph graph = io.readDot(inputFileName);
//        AstarScheduler scheduler = new AstarScheduler();
//        Schedule schedule = scheduler.run(graph, numProcessors);
//
//        io.writeDot(schedule, outputFileName);
//
//        System.out.println("created!");
//    }
//
//    private static String getOutputFileName(String[] args) {
//        for (int i = 2; i < args.length; i++) {
//            if (args[i].equals("-o")) {
//                if(i + 1 <= args.length){
//                    // get output file
//                    return args[i + 1] + ".dot";
//                } else {
//                    throw new RuntimeException("Output filename not specified");
//                }
//            }
//        }
//        return null; // Return an empty string if -o is not found or if it's not followed by a value
    }

    public static void visualise(){
        App visualisation = new App();
        try {
            visualisation.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String getOutputFileName(String[] args) {
        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("-o")) {
                if(i + 1 < args.length){
                    // get output file
                    return args[i + 1] + ".dot";
                } else {
                    // this throws when empty string after -o
                    throw new RuntimeException("Output filename not specified");
                }
            }
        }
        return null;
    }
}