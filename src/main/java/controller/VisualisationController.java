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
import javafx.scene.shape.ArcType;
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
        // Calculate CPU usage (between 0.0 and 1.0)
        cpuUsage = osBean.getProcessCpuLoad();
        if (cpuUsage < 0) {
            cpuUsage = 0;
        }

        // Calculate memory usage
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        memoryUsage = (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax();

        System.out.println("cpu process " + osBean.getProcessCpuLoad());
        System.out.println("memory " + memoryUsage);
        // Clear the CPU canvas
        cpuGC.clearRect(0, 0, cpuWheel.getWidth(), cpuWheel.getHeight());

        // Clear the memory canvas
        memoryGC.clearRect(0, 0, memoryWheel.getWidth(), memoryWheel.getHeight());

        // Draw CPU usage arc with outline
        cpuGC.setFill(Color.BLUE); // Set the fill color
        cpuGC.setStroke(Color.BLACK); // Set the outline color
        cpuGC.setLineWidth(2); // Set the outline width
        cpuGC.fillArc(0, 0, cpuWheel.getWidth(), cpuWheel.getHeight(), 90, -360 * cpuUsage, ArcType.ROUND);
        cpuGC.strokeArc(0, 0, cpuWheel.getWidth(), cpuWheel.getHeight(), 90, -360, ArcType.ROUND);

        // Draw memory usage arc with outline
        memoryGC.setFill(Color.GREEN); // Set the fill color
        memoryGC.setStroke(Color.BLACK); // Set the outline color
        memoryGC.setLineWidth(2); // Set the outline width
        memoryGC.fillArc(0, 0, memoryWheel.getWidth(), memoryWheel.getHeight(), 90, -360 * memoryUsage, ArcType.ROUND);
        memoryGC.strokeArc(0, 0, memoryWheel.getWidth(), memoryWheel.getHeight(), 90, -360, ArcType.ROUND);
    }

    public void startScheduler() {
        final String directory = "src/test/graphs/";
        IOHandler io = new IOHandler();
        Graph graph = io.readDot(directory + "Nodes_11_OutTree.dot");
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
