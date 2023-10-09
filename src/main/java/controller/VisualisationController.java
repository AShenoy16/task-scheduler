package controller;

import algorithm.branchandbound.BranchAndBound;
import algorithm.branchandbound.Schedule;
import io.IOHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
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
import com.sun.management.OperatingSystemMXBean;

public class VisualisationController {
    @FXML
    private Label bestCurrentText;

    @FXML
    private Canvas cpuWheel;
    @FXML
    private Canvas memoryWheel;

    private GraphicsContext cpuGC;
    private GraphicsContext memoryGC;
    private OperatingSystemMXBean osBean;
    private MemoryMXBean memoryBean;
    private double cpuUsage;

    private double memoryUsage;


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
        Graph graph = io.readDot(directory + "Nodes_7_OutTree.dot");
        BranchAndBound scheduler = new BranchAndBound();
        scheduler.setController(this);

        // Start the scheduler in a separate thread
        Thread schedulerThread = new Thread(() -> {
            Schedule schedule = scheduler.run(graph, 4);
        });
        schedulerThread.start();
    }
    public void setBestText(int currentShortestPath) {
        Platform.runLater(() -> {
            bestCurrentText.setText(String.valueOf(currentShortestPath));
        });
    }

    public void setControllerGraph() {
    }
}
