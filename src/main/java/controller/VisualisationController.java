package controller;

import algorithm.branchandbound.*;
import com.sun.management.OperatingSystemMXBean;
import io.IOHandler;
import io.SchedulingOptions;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VisualisationController {
    private final String[] colours = new String[]{"03DAC6", "4895EF", "4361EE", "3F37C9", "3A0CA3", "480CA8", "560BAD",
        "7209B7", "B5179E", "F72585"};
    @FXML
    private HBox parallelContainer;
    @FXML
    private HBox sequentialContainer;
    @FXML
    private HBox timeSection;
    @FXML
    private HBox bestSection;
    @FXML
    private HBox cpuSection;
    @FXML
    private HBox memorySection;
    @FXML
    private StackedBarChart<Number, String> parallelBarChart;
    @FXML
    private Label bestCurrentText;
    @FXML
    private Canvas cpuWheel;
    @FXML
    private Canvas memoryWheel;
    @FXML
    private StackedBarChart<String, Number> scheduleBarChart;
    @FXML
    private StackedBarChart<String, Number> scheduleBarChartPara;
    @FXML CategoryAxis parallelYAxis;
    @FXML CategoryAxis scheduleXAxis;
    @FXML CategoryAxis scheduleXAxisPara;
    @FXML
    private Label timerLabel;
    @FXML
    private Label cpuText;
    @FXML
    private Label memoryText;
    @FXML
    private BorderPane graphContainer;
    @FXML
    private BorderPane graphContainerPara;
    @FXML
    private Button startBtn;
    @FXML
    Pane startPage;
    @FXML
    Label homeArgsLabel;
    @FXML
    Label homeGraphLabel;
    private StackedBarChart<String, Number> currentScheduleBarChart;
    private CategoryAxis currentScheduleAxis;
    private BorderPane currentGraphContainer;
    private GraphicsContext cpuGC;
    private GraphicsContext memoryGC;
    private OperatingSystemMXBean osBean;
    private MemoryMXBean memoryBean;
    private double cpuUsage;
    private double memoryUsage;
    private int numProcessors;
    private int numCores;
    private boolean isParallel;
    private Graph graph;
    private int[] processorStartTimes;
    private IOHandler io;
    private ScheduledExecutorService scheduledExecutorService;
    private VisualiseGraph viewer;
    private ScheduledExecutorService scheduledExecutorServiceParallel;
    private BranchAndBoundAlgorithm bnb;
    private Schedule finalSchedule;
    private String outputFileName;
    private int timerCounter;
    private boolean isStarted = false;

    @FXML
    public void initialize(SchedulingOptions options) {
        io = new IOHandler();
        graph = io.readDot(options.inputFileName);
        numProcessors = options.numProcessors;
        numCores = options.numCores;
        isParallel = options.isParallel;
        outputFileName = options.outputFileName;

        updateHomeScreen(options.inputFileName, numProcessors, numCores, isParallel);

        if (isParallel) {
            bnb = new BranchAndBoundParallel();
            sequentialContainer.setVisible(false);
            currentScheduleBarChart = scheduleBarChartPara;
            currentScheduleAxis = scheduleXAxisPara;
            currentGraphContainer = graphContainerPara;
        } else {
            bnb = new BranchAndBound();
            parallelContainer.setVisible(false);
            currentScheduleBarChart = scheduleBarChart;
            currentScheduleAxis = scheduleXAxis;
            currentGraphContainer = graphContainer;
        }

        // store this controller in the branch and bound algo
        bnb.setController(this);

        // cpu and memory configurations and set up
        cpuGC = cpuWheel.getGraphicsContext2D();
        memoryGC = memoryWheel.getGraphicsContext2D();
        osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        memoryBean = ManagementFactory.getMemoryMXBean();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateWheels()));
        timeline.setCycleCount(Animation.INDEFINITE);

        // Start the scheduler in a separate thread, and run depending on user's parallel arg
        Thread schedulerThread = new Thread(() -> {
            if (isParallel) {
                finalSchedule = bnb.run(graph, numProcessors, numCores);
            } else {
                finalSchedule = bnb.run(graph, numProcessors);
            }
        });

        // initialise schedule charts and graphs beforehand
        initGraphVisualisation(graph);
        initializeCharts();

        // start cpu and memory updates
        timeline.play();

        // button to switch from home screen to main screen and start updating charts/graphs
        startBtn.setOnAction(event -> {
            startPage.setVisible(false);
            schedulerThread.start();

            if (isParallel) {
                updateParallelChart();
            }
            updateScheduleChart();
            startTimer();
            visualiseSchedules();
        });
    }

    private void updateHomeScreen(String inputFileName, int numProcessors, int numCores, boolean isParallel) {
        String argsLabel = "PROCESSORS - " + numProcessors + " | ";
        if (isParallel) {
            argsLabel += "CORES -" + numCores + " | PARALLEL - TRUE";
        } else {
            argsLabel += "PARALLEL - FALSE";
        }
        homeArgsLabel.setText(argsLabel);
        homeGraphLabel.setText("GRAPH - " + inputFileName);
    }

    /**
     * Initialises the graph visualisations for the input graph, including nodes and edges
     * @param graph - input graph provided by user
     */
    private void initGraphVisualisation(Graph graph){
        // initialise GraphStream graph
        System.setProperty("org.graphstream.ui", "javafx");
        org.graphstream.graph.Graph graphS = new SingleGraph("bnb");

        // convert custom graph class, nodes and edges to graphstream graph
        Node[] nodes = graph.getNodes();
        for (Node value : nodes) {
            graphS.addNode(String.valueOf(value.getId()));
        }
        int[][] edges = graph.getAdjacencyMatrix();
        for (int i = 0; i < edges.length; i++) {
            for (int j = 0; j < edges.length; j++){
                if (edges[i][j] != 0) {
                    org.graphstream.graph.Edge edge = graphS.addEdge(i + "," + j, i, j, true);
                    edge.setAttribute("ui.style", "fill-color: #EFEFEF; size: 2px;");
                    edge.setAttribute("ui.arrow-shape", "arrow;");
                }
            }
        }
        // display graph and set layout in javafx
        graphS.setAttribute("ui.stylesheet", "graph { fill-color: #282828; }");
        viewer = new VisualiseGraph(graphS, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        FxViewPanel viewPanel = (FxViewPanel) viewer.addDefaultView(false);

        currentGraphContainer.setCenter(viewPanel);
    }

    /**
     * Visualise all schedules that were once the best schedule.
     *
     */
    private void visualiseSchedules() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() ->  {
            if (!viewer.visualizeQueuedSchedule()) {
                executorService.shutdownNow();
            }
        }, 200, 320, TimeUnit.MILLISECONDS);
    }

    /**
     * Add newly found best partial solution to queue to be visualized.
     *
     * @param partialSolution Partial solution to add to queue
     */
    public void queuePartialSolution(PartialSolution partialSolution) {
        viewer.getPartialSolutionQueue().offer(partialSolution);
    }

    /**
     * Initialises the schedule bar chart from the initialization of this application but before algo starts
     */
    private void initializeCharts() {
        String[] processorNames = new String[numProcessors];
        processorStartTimes = new int[numProcessors];
        // initialises array of processor names
        for (int i = 0; i < numProcessors; i++) {
            processorNames[i] = "P" + i;
        }
        // set processor names as x axis labels for schedule bar chart
        currentScheduleAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(processorNames)));

        String[] threadNames = new String[numCores];
        for (int i = 0; i < numCores; i++) {
            threadNames[i] = "T" + i;
        }
        // set thread names as y-axis labels for parallel bar chart
        parallelYAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(threadNames)));
    }

    /**
     * updates the schedule bar chart periodically with the pulled current DFS task.
     */
    private void updateScheduleChart() {
        // create a single thread schedule executor that periodically updates the schedule stacked bar chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // only start updating when the algo has started dfs recursion
            if (isStarted) {
                ScheduledTask currentScheduledTask = bnb.getCurrentDFSTask();
                ScheduledTask[] scheduledTasks = new ScheduledTask[currentScheduledTask.getTaskLength()];
                for (int i = scheduledTasks.length - 1; i >= 0; i--) { // reverse order of task nodes
                    scheduledTasks[i] = currentScheduledTask;
                    currentScheduledTask = currentScheduledTask.getParent();
                }

                Platform.runLater(() -> {
                    bestCurrentText.setText(String.valueOf(bnb.getShortestPathText()));
                    Arrays.fill(processorStartTimes, 0); // reset processor times
                    currentScheduleBarChart.getData().clear(); // reset stacked bar chart
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
                            currentScheduleBarChart.getData().addAll(seriesNone);
                        }

                        // creates series of this task
                        series.getData().add(new XYChart.Data<>(processorName, taskTime));
                        series.setName(String.valueOf(scheduledTask.getNode().getId()));
                        currentScheduleBarChart.getData().addAll(series);
                        processorStartTimes[scheduledTask.getProcessorId()] = scheduledTask.getStartTime() + taskTime;
                    }

                    currentScheduleBarChart.getData().forEach((t) -> {
                        if (t.getName() != null && t.getName().equals("none")) {
                            // styles the previous 'none' series with time delay as transparent
                            t.getData().forEach((j) -> j.getNode().setStyle("-fx-background-color: transparent"));
                        } else {
                            //styles the previous series with colour from predefined colour set and also show task ID
                            //also check out dataSeries and dataValue css classes in visualisation.css for more styles
                            t.getData().forEach((j) -> {
                                String colourCSS = colours[Integer.parseInt(t.getName()) % colours.length];
                                j.getNode().getStyleClass().add("dataSeries");
                                j.getNode().setStyle("-fx-background-color: #" + colourCSS);

                                StackPane bar = (StackPane) j.getNode();
                                Text dataText = new Text(t.getName());
                                dataText.getStyleClass().add("dataValue");
                                bar.getChildren().add(dataText);
                            });
                        }
                    });
                    //shutdown scheduler when algo is finished
                    if (bnb.getIsFinished()) {
                        scheduledExecutorService.shutdown();

                    }
                });
            }
        }, 0, 350, TimeUnit.MILLISECONDS);
    }

    /**
     * updates the parallel bar chart periodically with the pulled thread times.
     */
    private void updateParallelChart() {
        scheduledExecutorServiceParallel = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorServiceParallel.scheduleAtFixedRate(() -> {
            if (isStarted) {
                Platform.runLater(() -> {
                    BranchAndBoundParallel bnbParallel = (BranchAndBoundParallel) bnb;
                    int[] threadTimes = bnbParallel.getParallelThreadTimes();
                    parallelBarChart.getData().clear(); // clear existing chart data
                    // add a bar chart for each active thread
                    for (int i = 0; i < threadTimes.length; i++) {
                        if (threadTimes[i] != Integer.MAX_VALUE) { //skip inactive threads
                            XYChart.Series<Number, String> series = new XYChart.Series<>();
                            series.getData().add(new XYChart.Data<>(threadTimes[i], "T" + i));
                            series.setName(String.valueOf(i));
                            parallelBarChart.getData().addAll(series);
                        }
                    }
                    // styles each bar with colour as well as assigning css style class.
                    parallelBarChart.getData().forEach((t) -> {
                        t.getData().forEach((j) -> {
                            String colourCSS = colours[Integer.parseInt(t.getName())%colours.length];
                            j.getNode().getStyleClass().add("dataSeries");
                            j.getNode().setStyle("-fx-background-color: #" +  colourCSS);

                            StackPane bar = (StackPane) j.getNode();
                            Text dataText = new Text("CORE " + t.getName() + ": " + j.getXValue().toString());
                            dataText.getStyleClass().add("dataValue");
                            bar.getChildren().add(dataText);
                        });
                    });
                    //shutdown scheduler when algo is finished
                    if (bnb.getIsFinished()) {
                        scheduledExecutorServiceParallel.shutdown();
                    }
                });
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * updates cpu and memory wheels, and text elements displayed on the GUI
     */
    private void updateWheels() {
        // Calculate CPU usage (between 0.0 and 1.0)
        cpuUsage = osBean.getProcessCpuLoad();
        if (cpuUsage < 0) {
            cpuUsage = 0;
        }
        // Calculate memory usage
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        memoryUsage = (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax();

        // update the cpu and memory in UI
        cpuText.setText(String.format("%.2f", cpuUsage*100) + "%");
        memoryText.setText(String.format("%.2f", memoryUsage*100) + "%");
        updateCPU();
        updateMemory();
    }

    /**
     * updates cpu wheel to better visualise its performance
     */
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

    /**
     *updates memory wheel to better visualise its performance
     */
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

    /**
     * Starts the running timer when user starts the algorithm
     */
    private void startTimer() {
        timerCounter = 0;
        final int runTimesPerSecond = 100;
        Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            /**
             * Timer currently configured to run every 10ms, hence will run 100 times for each second
             */
            @Override
            public void run() {
                timerCounter++;

                // runTimesPerSecond is essentially a constant conversion value for calculation
                long minutes = timerCounter / (60*runTimesPerSecond);
                long seconds = (timerCounter - (minutes*60*runTimesPerSecond))/runTimesPerSecond;
                long milliseconds = timerCounter - (minutes*60*runTimesPerSecond) - (seconds*runTimesPerSecond);

                String minuteDigit = (minutes < 10) ? "0" : "";
                String secondDigit = (seconds < 10) ? "0" : "";
                String milliSecondDigit = (milliseconds < 10) ? "0" : "";

                String timeText = minuteDigit + minutes + ":" + secondDigit + seconds + ":" + milliSecondDigit + milliseconds;
                Platform.runLater(() -> timerLabel.setText(timeText));
                if (bnb.getIsFinished()) {
                    myTimer.cancel();
                    updateFinish();
                    io.bnbWriteDot(finalSchedule, outputFileName);
                }
            }
        }, 0, 10);
    }

    /**
     * modifies styles (effect and text fill) when the algo finishes, to inform user"
     */
    private void updateFinish() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(72, 255, 157));
        dropShadow.setHeight(30);
        dropShadow.setWidth(30);
        dropShadow.setRadius(14.5);

        bestSection.setEffect(dropShadow);
        timeSection.setEffect(dropShadow);
        cpuSection.setEffect(dropShadow);
        memorySection.setEffect(dropShadow);

        bestCurrentText.setTextFill(Color.rgb(72, 255, 157));
        timerLabel.setTextFill(Color.rgb(72, 255, 157));
    }

    /**
     * inform this controller that the bnb algorithm dfs recursion has started
     */
    public void setStarted() {
        isStarted = true;
    }
}
