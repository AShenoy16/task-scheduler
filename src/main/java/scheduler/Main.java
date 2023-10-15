package scheduler;
import algorithm.astar.AstarParallel;
import algorithm.astar.AstarScheduler;
import com.sun.javafx.application.PlatformImpl;
import controller.App;
import io.CMDArgumentHandler;
import io.IOHandler;
import io.SchedulingOptions;
import model.Graph;
import javafx.stage.Stage;
import model.Schedule;

public class Main {
    public static void main(String[] args) {

        // Construct options from command line arguments
        var options = CMDArgumentHandler.getSchedulingOptions(args);
        if (options.isVisualised) {
            // Run visualisation
            visualise(options);
            return;
        }

        System.out.println("Starting schedule creation...");

        // Create graph from dot file
        IOHandler io = new IOHandler();
        Graph graph = io.readDot(options.inputFileName);

        Schedule schedule;
        // Find schedule
        if (options.isParallel) {
            AstarParallel parallelScheduler = new AstarParallel();
            schedule = parallelScheduler.run(graph, options.numProcessors, options.numCores);
        } else {
            AstarScheduler sequentialScheduler = new AstarScheduler();
            schedule = sequentialScheduler.run(graph, options.numProcessors);
        }

        // Write output
        io.writeDot(schedule, options.outputFileName);

        System.out.println("created!");
    }

    /**
     * Setup visualisation application
     *
     * @param options Options to use for visualisation
     */
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
}