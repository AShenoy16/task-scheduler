package controller;

import algorithm.branchandbound.BranchAndBound;
import algorithm.branchandbound.Schedule;
import algorithm.branchandbound.ScheduledTask;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.sun.management.OperatingSystemMXBean;
import io.IOHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.util.Duration;
import model.Graph;
import model.Node;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

public class VisualisationController {
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
    private AnchorPane graphContainer;

    @FXML
    private SwingNode graphNode;

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
    private int timerCounter;

    @FXML
    public void initialize() {
        final String directory = "src/test/graphs/";
        IOHandler io = new IOHandler();
        Graph graph = io.readDot(directory + "Nodes_11_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        scheduler.setController(this);
        bnb = scheduler;
        numProcessors = 4;

//        createGraphstream(graph);
        createJGraphT(graph);

        bestCurrentText.setText("inf");

        cpuGC = cpuWheel.getGraphicsContext2D();
        memoryGC = memoryWheel.getGraphicsContext2D();

        osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        memoryBean = ManagementFactory.getMemoryMXBean();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateWheels()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Start the scheduler in a separate thread
        Thread schedulerThread = new Thread(() -> {
            Schedule schedule = scheduler.run(graph, numProcessors);
        });
        schedulerThread.start();

        initializeBarChart();
        startTimer();
    }
    private void createGraphstream(Graph graph) {
        org.graphstream.graph.Graph graphS = new MultiGraph("bnb");

        // Configure GraphStream's viewer
        Viewer viewer = new FxViewer(graphS, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        // Create a Swing-based view panel for the graph
        ViewPanel viewPanel = viewer.addDefaultView(false);

        // Create a SwingNode to embed the view panel in JavaFX
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(viewPanel);

        // Add the SwingNode to the JavaFX layout
        graphContainer.getChildren().add(swingNode);

        // Initialize the GraphStream viewer pipe
        ViewerPipe viewerPipe = viewer.newViewerPipe();

        // Start a thread to listen for events from GraphStream viewer
        Thread thread = new Thread(() -> {
            viewerPipe.addSink(graphS);
            while (true) {
                viewerPipe.pump();
            }
        });
        thread.start();


        // You can add nodes and edges to your GraphStream graph as needed
        // For example:
        // graph.addNode("Node1");
        // graph.addNode("Node2");
        // graph.addEdge("Edge1", "Node1", "Node2");
    }

    private void createJGraphT(Graph graph){
        // Create the JGraphXAdapter
        SwingUtilities.invokeLater(() -> {
            org.jgrapht.Graph graphT = convertToJGraphT(graph);
            JGraphXAdapter<Node,DefaultEdge> graphAdapter = new JGraphXAdapter<>(graphT);
            mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);

            mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);

            layout.execute(graphAdapter.getDefaultParent());

            graphComponent.setPreferredSize(new Dimension(300,300));
            graphComponent.setMaximumSize(new Dimension(300,300));
            graphComponent.setMinimumSize(new Dimension(250,250));
            graphComponent.zoomTo(1.5, false);
            graphNode.setContent(graphComponent);


//            org.jgrapht.Graph graphT = convertToJGraphT(graph);
//
//            JGraphModelAdapter<Node,DefaultEdge> graphAdapter = new JGraphModelAdapter<>(graphT);
//
//            JGraph graphComponent = new JGraph(graphAdapter);
//            graphContainer.setContent(graphComponent);
//
//            JGraphLayout layout = new JGraphHierarchicalLayout();
//            JGraphFacade facade = new JGraphFacade(graphComponent);
//            layout.run(facade);
//            Map nested = facade.createNestedMap(false, false);
//            graphComponent.getGraphLayoutCache().edit(nested);
        });
    }

    private org.jgrapht.Graph convertToJGraphT(Graph graph) {
        org.jgrapht.Graph jGraphTGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        // Add nodes and edges from your custom graph to JGraphT
        Node[] nodes = graph.getNodes();
        for(Integer i = 0; i < nodes.length; i++){
            jGraphTGraph.addVertex(nodes[i].getId());
        }
        int[][] edges = graph.getAdjacencyMatrix();
        for (Integer i = 0; i < edges.length; i++) {
            for (Integer j = 0; j < edges.length; j++){
                if (edges[i][j] != 0) {
                    jGraphTGraph.addEdge(i, j);
                }
            }
        }
        return jGraphTGraph;
    }

    public void initializeBarChart() {
        processorNames = new String[numProcessors];
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
        memoryText.setText(String.format("%.2f", memoryUsage*100) + "%");;

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
                Platform.runLater(() -> {
                    timerLabel.setText(timeText);
                });
                if (bnb.getIsFinished()) {
                    myTimer.cancel();
                }
            }
        }, 0, 10);
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
        startTimer();
    }

    public void setBestText(int currentShortestPath) {
        Platform.runLater(() -> {
            bestCurrentText.setText(String.valueOf(currentShortestPath));
        });
    }

    public void setControllerGraph() {
    }
}
