package controller;

import algorithm.branchandbound.BranchAndBound;
import algorithm.branchandbound.PartialSolution;
import algorithm.branchandbound.ScheduledTask;
import com.sun.management.OperatingSystemMXBean;
import io.IOHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Graph;
import model.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import visualisation.VisualiseGraph;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VisualisationController {
    private final String[] colours = new String[]{"03DAC6", "4895EF", "4361EE", "3F37C9", "3A0CA3", "480CA8", "560BAD",
        "7209B7", "B5179E", "F72585"};
    @FXML
    private Label bestCurrentText;
    @FXML
    private Canvas cpuWheel;
    @FXML
    private Canvas memoryWheel;
    @FXML
    private StackedBarChart<String, Number> scheduleBarChart;
    @FXML CategoryAxis scheduleXAxis;
    @FXML
    private Label timerLabel;
    @FXML
    private Label cpuText;
    @FXML
    private Label memoryText;
    @FXML
    private BorderPane graphContainer;
    private GraphicsContext cpuGC;
    private GraphicsContext memoryGC;
    private OperatingSystemMXBean osBean;
    private MemoryMXBean memoryBean;
    private double cpuUsage;
    private double memoryUsage;
    private int numProcessors;
    private int[] processorStartTimes;
    private ScheduledExecutorService scheduledExecutorService;
    private VisualiseGraph viewer;
    private ScheduledExecutorService scheduledExecutorServiceGraph;

    private double currentScale = 1.0;
    private double minScale = 0.65;
    private double maxScale = 1.025;

    private boolean isFinished = false;
    private BranchAndBound bnb;
    private int timerCounter;
    private org.graphstream.graph.Graph graphS;

    @FXML
    public void initialize() {
        final String directory = "src/test/graphs/";
        IOHandler io = new IOHandler();
        Graph graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        scheduler.setController(this);
        bnb = scheduler;
        numProcessors = 2;

        bestCurrentText.setText("inf");

        cpuGC = cpuWheel.getGraphicsContext2D();
        memoryGC = memoryWheel.getGraphicsContext2D();

        osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        memoryBean = ManagementFactory.getMemoryMXBean();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateWheels()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Start the scheduler in a separate thread
        Thread schedulerThread = new Thread(() -> scheduler.run(graph, numProcessors));
        schedulerThread.start();

        initGraph(graph);
//        initializeCharts();
        startTimer();
    }


    @FXML
    public void handleZoom(ScrollEvent event) {

        // if scroll > 0 zoomfactor = 1.05
        // otherwise scroll factors 0.95
        double zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.95; // Adjust zoom factor as needed

        double newScale = currentScale * zoomFactor;

        // Ensure the new scale is within the defined range
        if (newScale >= minScale && newScale <= maxScale) {
            graphContainer.setScaleX(newScale);
            graphContainer.setScaleY(newScale);
            currentScale = newScale;
        }

        event.consume();
    }

    private void initGraph(Graph graph){
        System.setProperty("org.graphstream.ui", "javafx");
        graphS = new SingleGraph("bnb");
        Node[] nodes = graph.getNodes();
        for (Node value : nodes) {
            org.graphstream.graph.Node node = graphS.addNode(String.valueOf(value.getId()));
        }
        int[][] edges = graph.getAdjacencyMatrix();
        for (int i = 0; i < edges.length; i++) {
            for (int j = 0; j < edges.length; j++){
                if (edges[i][j] != 0) {
                    graphS.addEdge(i +","+j, i, j);
                }
            }
        }
        graphS.setAttribute("ui.stylesheet", "graph { fill-color: #282828; }");
        visualiseSchedules();
        viewer = new VisualiseGraph(graphS, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        FxViewPanel viewPanel = (FxViewPanel) viewer.addDefaultView(false);
        graphContainer.setCenter(viewPanel);
    }

    /**
     * Visualise all schedules that were once the best schedule.
     *
     */
    private void visualiseSchedules() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() ->  {
            if (viewer.getPartialSolutionQueue().isEmpty()) {
                executorService.shutdownNow();
                return;
            }

            viewer.visualizeQueuedSchedule();
        }, 1000, 400, TimeUnit.MILLISECONDS);
    }

    /**
     * Add newly found best partial solution to queue to be visualized.
     *
     * @param partialSolution Partial solution to add to queue
     */
    public void queuePartialSolution(PartialSolution partialSolution) {
        viewer.getPartialSolutionQueue().offer(partialSolution);
    }

    public void initializeCharts() {
        String[] processorNames = new String[numProcessors];
        processorStartTimes = new int[numProcessors];
        // initialises array of processor names
        for (int i = 0; i < numProcessors; i++) {
            processorNames[i] = "P" + i;
        }
        // set processor names as x axis labels
        scheduleXAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(processorNames)));

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
                    series.setName(String.valueOf(scheduledTask.getNode().getId()));
                    scheduleBarChart.getData().addAll(series);
                    processorStartTimes[scheduledTask.getProcessorId()] = scheduledTask.getStartTime() + taskTime;
                }

                // styles the previous mentioned series with time delay as transparent
                scheduleBarChart.getData().forEach((t) -> {
                    if (t.getName() != null && t.getName().equals("none")) {
                        t.getData().forEach((j) -> j.getNode().setStyle("-fx-background-color: transparent"));
                    } else {
                        t.getData().forEach((j) -> {
                            String colourCSS = colours[Integer.parseInt(t.getName())%colours.length];
                            j.getNode().getStyleClass().add("dataSeries");
                            j.getNode().setStyle("-fx-background-color: #" +  colourCSS);

                            StackPane bar = (StackPane) j.getNode();
                            Text dataText = new Text(t.getName());
                            dataText.getStyleClass().add("dataValue");
                            bar.getChildren().add(dataText);
                        });
                    }
                });
                bestCurrentText.setText(String.valueOf(bnb.getShortestPathText()));
            });
            if (bnb.getIsFinished()) {
                scheduledExecutorService.shutdown();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void updateWheels() {
        // Calculate CPU usage (between 0.0 and 1.0)
        cpuUsage = osBean.getProcessCpuLoad();
        if (cpuUsage < 0) {
            cpuUsage = 0;
        }
        // Calculate memory usage
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        memoryUsage = (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax();

        cpuText.setText(String.format("%.2f", cpuUsage*100) + "%");
        memoryText.setText(String.format("%.2f", memoryUsage*100) + "%");

        updateCPU();
        updateMemory();
    }
    private void updateCPU(){
        // Define the dimensions of the ring
        double ringWidth = 10.0; // Width of the ring (adjust as needed)
        double centerX = cpuWheel.getWidth() / 2.0;
        double centerY = cpuWheel.getHeight() / 2.0;
        double radius = Math.min(cpuWheel.getWidth(), cpuWheel.getHeight()) / 2.0 - ringWidth / 2.0;

        // Clear the CPU canvas
        cpuGC.clearRect(0, 0, cpuWheel.getWidth(), cpuWheel.getHeight());

        // Draw the outer circle (ring)
        cpuGC.setStroke(Color.BLACK); // Set the outline color
        cpuGC.setLineWidth(ringWidth); // Set the ring width
        cpuGC.strokeArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 0, 360, ArcType.OPEN);

        // Draw the outline of the filled portion of the ring
        cpuGC.setStroke(Color.BLUE); // Set the outline color
        cpuGC.setLineWidth(ringWidth); // Set the outline width
        cpuGC.strokeArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 90, -360 * cpuUsage, ArcType.OPEN);
    }
    private void updateMemory(){
        // Define the dimensions of the ring
        double ringWidth = 10.0; // Width of the ring (adjust as needed)
        double centerX = memoryWheel.getWidth() / 2.0;
        double centerY = memoryWheel.getHeight() / 2.0;
        double radius = Math.min(memoryWheel.getWidth(), memoryWheel.getHeight()) / 2.0 - ringWidth / 2.0;

        // Clear the memory canvas
        memoryGC.clearRect(0, 0, memoryWheel.getWidth(), memoryWheel.getHeight());

        // Draw the outer circle (ring)
        memoryGC.setStroke(Color.BLACK); // Set the outline color
        memoryGC.setLineWidth(ringWidth); // Set the ring width
        memoryGC.strokeArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 0, 360, ArcType.OPEN);

        // Draw the outline of the filled portion of the ring
        memoryGC.setStroke(Color.BLUE); // Set the outline color
        memoryGC.setLineWidth(ringWidth); // Set the outline width
        memoryGC.strokeArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 90, -360 * memoryUsage, ArcType.OPEN);

    }

    private void startTimer() {
        timerCounter = 0;
        Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerCounter++;

                long minutes = timerCounter / (60*100);
                long seconds = (timerCounter - (minutes*60*100))/100;
                long milliseconds = timerCounter - (minutes*60*100) - (seconds*100);

                String minuteDigit = (minutes < 10) ? "0" : "";
                String secondDigit = (seconds < 10) ? "0" : "";
                String milliSecondDigit = (milliseconds < 10) ? "0" : "";

                String timeText = minuteDigit + minutes + ":" + secondDigit + seconds + ":" + milliSecondDigit + milliseconds;
                Platform.runLater(() -> timerLabel.setText(timeText));
                if (bnb.getIsFinished()) {
                    myTimer.cancel();
                }
            }
        }, 0, 10);
    }
}
