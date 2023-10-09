package controller;

import algorithm.branchandbound.BranchAndBound;
import algorithm.branchandbound.Schedule;
import io.IOHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import model.Graph;

public class VisualisationController {
    @FXML
    private Label bestCurrentText;

    public void initialize() {
        bestCurrentText.setText("inf");
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
