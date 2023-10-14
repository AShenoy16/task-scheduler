package scheduler;
import algorithm.astar.AstarScheduler;
import com.sun.javafx.application.PlatformImpl;
import controller.App;
import controller.VisualisationController;
import io.IOHandler;
import model.Graph;
import javafx.stage.Stage;
import model.Schedule;

public class Main {
    public static void main(String[] args) {

        //visualise();


        int argsLength = args.length;
        if (args == null || argsLength < 2) {
            throw new RuntimeException("InputFileName or numProcessors arguments not supplied");
        }
        String inputFileName = args[0];
        String outputFileName = inputFileName + "-output.dot";

        // remove .dot extension from input file name when including in output file name
        int lastDotIndex = inputFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            // Remove the file extension
            outputFileName = inputFileName.substring(0, lastDotIndex) + "-output.dot";
        }

        int numProcessors = Integer.parseInt(args[1]);

        if(argsLength > 2){
            //Get option arguments
            String customName =getOutputFileName(args);
            if(customName != null){
                outputFileName = customName;
            }
        }

        System.out.println("Starting schedule creation...");

        IOHandler io = new IOHandler();
        Graph graph = io.readDot(inputFileName);
        AstarScheduler scheduler = new AstarScheduler();
        Schedule schedule = scheduler.run(graph, numProcessors);

        io.writeDot(schedule, outputFileName);

        System.out.println("created!");

        // just for us to see memory usage
        printMemoryUsage();
    }

    public static void visualise(){
        PlatformImpl.startup(() -> {
            App visualisation = new App();
            try {
                visualisation.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }});
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

    private static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        System.out.println("JVM Max Memory: " + maxMemory / 1024 + " KB");
        System.out.println("JVM Total Memory: " + totalMemory / 1024 + " KB");
        System.out.println("JVM Free Memory: " + freeMemory / 1024 + " KB");
        System.out.println("JVM Used Memory: " + usedMemory / 1024 + " KB");
    }
}