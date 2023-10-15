package scheduler;
import algorithm.astar.AstarParallel;
import algorithm.astar.AstarScheduler;
import com.sun.javafx.application.PlatformImpl;
import controller.App;
import controller.VisualisationController;
import io.CMDArgumentHandler;
import io.IOHandler;
import io.SchedulingOptions;
import model.Graph;
import javafx.stage.Stage;
import model.Schedule;

public class Main {
    public static void main(String[] args) {

        SchedulingOptions options = CMDArgumentHandler.getSchedulingOptions(args);
        if (options.isVisualised) {
            visualise(options);
            return;
        }

        System.out.println("Starting schedule creation...");

        IOHandler io = new IOHandler();
        Graph graph = io.readDot(options.inputFileName);

        Schedule schedule;
        if (options.isParallel) {
            AstarParallel parallelScheduler = new AstarParallel();
            schedule = parallelScheduler.run(graph, options.numProcessors, options.numCores);
        } else {
            AstarScheduler sequentialScheduler = new AstarScheduler();
            schedule = sequentialScheduler.run(graph, options.numProcessors);
        }

        io.writeDot(schedule, options.outputFileName);

        System.out.println("created!");
    }

    public static void visualise(SchedulingOptions options){
        PlatformImpl.startup(() -> {
            App visualisation = new App();
            try {
                visualisation.start(new Stage());
                visualisation.runVisualisation(options);
            } catch (Exception e) {
                e.printStackTrace();
            }});
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