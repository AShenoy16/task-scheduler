package controller;

import algorithm.branchandbound.BranchAndBound;
import algorithm.branchandbound.Schedule;
import algorithm.branchandbound.ScheduledTask;
import io.IOHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Graph;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.management.OperatingSystemMXBean;

public class VisualisationController {
    @FXML
    private Label bestCurrentText;

    @FXML
    private Canvas cpuWheel;
    @FXML
    private Canvas memoryWheel;
    @FXML
    private StackedBarChart<String, Number> scheduleBarChart;

    private GraphicsContext cpuGC;
    private GraphicsContext memoryGC;
    private OperatingSystemMXBean osBean;
    private MemoryMXBean memoryBean;
    private double cpuUsage;
    private double memoryUsage;
    private int numProcessors;
    private String[] processorNames;
    private int[] processorStartTimes;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean isFinished = false;
    private BranchAndBound bnb;


    public void initialize() {
        bestCurrentText.setText("inf");

        cpuGC = cpuWheel.getGraphicsContext2D();
        memoryGC = memoryWheel.getGraphicsContext2D();
        osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        memoryBean = ManagementFactory.getMemoryMXBean();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateWheels()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void initializeBarChart() {
        processorNames = new String[numProcessors];
        processorStartTimes = new int[numProcessors];
        // initialises array of processor names
        for (int i = 0; i < numProcessors; i++) {
            processorNames[i] = "P" + i;
        }
        // set processor names as x axis labels
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(processorNames)));

        // create a single thread schedule executor that periodically updates the schedule stacked bar chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            ScheduledTask currentScheduledTask = bnb.getCurrentDFSTask();
            ScheduledTask[] scheduledTasks = new ScheduledTask[currentScheduledTask.getTaskLength()];
            for (int i = scheduledTasks.length-1; i >= 0; i--) { // reverse order of task nodes
                scheduledTasks[i] = currentScheduledTask;
                currentScheduledTask = currentScheduledTask.getParent();
            }
            Platform.runLater(() -> {
                Arrays.fill(processorStartTimes, 0); // reset processor times
                scheduleBarChart.getData().clear(); // reset stacked bar chart
                for (ScheduledTask scheduledTask : scheduledTasks) { // create new series for each task
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    int taskTime = scheduledTask.getNode().getVal();
                    int startTime = scheduledTask.getStartTime();
                    int processorID = scheduledTask.getProcessorId();
                    String processorName = "P" + processorID;

                    // creates a series for when there is a time delay between current task start time and previous
                    // task finish time. Will be styled as transparent later on
                    if (scheduledTask.getStartTime() != processorStartTimes[processorID]) {
                        XYChart.Series<String, Number> seriesNone = new XYChart.Series<>();
                        seriesNone.getData().add(new XYChart.Data<>(processorName, startTime - processorStartTimes[processorID]));
                        seriesNone.setName("none");
                        scheduleBarChart.getData().addAll(seriesNone);
                    }

                    // creates series of this task
                    series.getData().add(new XYChart.Data<>(processorName, taskTime));
                    scheduleBarChart.getData().addAll(series);
                    processorStartTimes[scheduledTask.getProcessorId()] = scheduledTask.getStartTime() + taskTime;
                }

                // styles the previous mentioned series with time delay as transparent
                scheduleBarChart.getData().forEach((t) -> {
                    if (t.getName() != null && t.getName().equals("none")) {
                        t.getData().forEach((j) -> {
                            j.getNode().setStyle("-fx-background-color: transparent");
                        });
                    }
                });
            });
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void updateWheels() {




        System.out.println("cpu process " + osBean.getProcessCpuLoad());
        System.out.println("memory " + memoryUsage);

        updateCPU();
        updateMemory();
    }
    private void updateCPU(){
        // Calculate CPU usage (between 0.0 and 1.0)
        cpuUsage = osBean.getProcessCpuLoad();
        if (cpuUsage < 0) {
            cpuUsage = 0;
        }
        // Define the dimensions of the ring
        double ringWidth = 10.0; // Width of the ring (adjust as needed)
        double centerX = cpuWheel.getWidth() / 2.0;
        double centerY = cpuWheel.getHeight() / 2.0;
        double radius = Math.min(cpuWheel.getWidth(), cpuWheel.getHeight()) / 2.0 - ringWidth / 2.0;

        // Clear the memory canvas
        cpuGC.clearRect(0, 0, cpuWheel.getWidth(), cpuWheel.getHeight());

        // Draw the outer circle (ring)
        cpuGC.setStroke(Color.BLUEVIOLET); // Set the outline color
        cpuGC.setLineWidth(ringWidth); // Set the ring width
        cpuGC.strokeArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 0, 360, ArcType.OPEN);

        // You can set the memory usage as the angle to fill the ring
        double angleToFill = 360 * cpuUsage;

        // Calculate the starting and ending angles for the filled arc
        double startAngle = 90; // Start from the top
        double endAngle = startAngle + angleToFill; // Calculate the end angle

        // Calculate the coordinates of the points on the arc
        double startX = centerX - radius * Math.cos(Math.toRadians(startAngle));
        double startY = centerY - radius * Math.sin(Math.toRadians(startAngle));
        double endX = centerX - radius * Math.cos(Math.toRadians(endAngle));
        double endY = centerY - radius * Math.sin(Math.toRadians(endAngle));

        // Draw the outline of the filled portion of the ring
        cpuGC.setStroke(Color.BLUE); // Set the outline color
        cpuGC.setLineWidth(ringWidth); // Set the outline width
        cpuGC.strokeLine(startX, startY, endX, endY);

    }
    private void updateMemory(){
        // Calculate memory usage
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        memoryUsage = (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax();

        // Define the dimensions of the ring
        double ringWidth = 10.0; // Width of the ring (adjust as needed)
        double centerX = memoryWheel.getWidth() / 2.0;
        double centerY = memoryWheel.getHeight() / 2.0;
        double radius = Math.min(memoryWheel.getWidth(), memoryWheel.getHeight()) / 2.0 - ringWidth / 2.0;

        // Clear the memory canvas
        memoryGC.clearRect(0, 0, memoryWheel.getWidth(), memoryWheel.getHeight());

        // Draw the outer circle (ring)
        memoryGC.setStroke(Color.BLUEVIOLET); // Set the outline color
        memoryGC.setLineWidth(ringWidth); // Set the ring width
        memoryGC.strokeArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 0, 360, ArcType.OPEN);

        // You can set the memory usage as the angle to fill the ring
        double angleToFill = 360 * memoryUsage;

        // Calculate the starting and ending angles for the filled arc
        double startAngle = 90; // Start from the top
        double endAngle = startAngle + angleToFill; // Calculate the end angle

        // Calculate the coordinates of the points on the arc
        double startX = centerX - radius * Math.cos(Math.toRadians(startAngle));
        double startY = centerY - radius * Math.sin(Math.toRadians(startAngle));
        double endX = centerX - radius * Math.cos(Math.toRadians(endAngle));
        double endY = centerY - radius * Math.sin(Math.toRadians(endAngle));

        // Draw the outline of the filled portion of the ring
        memoryGC.setStroke(Color.GREEN); // Set the outline color
        memoryGC.setLineWidth(ringWidth); // Set the outline width
        memoryGC.strokeLine(startX, startY, endX, endY);

    }

    public void startScheduler() {
        final String directory = "src/test/graphs/";
        IOHandler io = new IOHandler();
        Graph graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        scheduler.setController(this);
        bnb = scheduler;
        numProcessors = 4;

        // Start the scheduler in a separate thread
        Thread schedulerThread = new Thread(() -> {
            Schedule schedule = scheduler.run(graph, numProcessors);
        });
        schedulerThread.start();

        initializeBarChart();
    }

    public void setBestText(int currentShortestPath) {
        Platform.runLater(() -> {
            bestCurrentText.setText(String.valueOf(currentShortestPath));
        });
    }

    public void setControllerGraph() {
    }
}
