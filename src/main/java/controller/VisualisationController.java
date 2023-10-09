package controller;

import algorithm.branchandbound.BranchAndBound;
import algorithm.branchandbound.Schedule;
import io.IOHandler;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import model.Graph;

public class VisualisationController {
    @FXML
    private Label bestCurrentText;

    public void initialize(){
        bestCurrentText.setText("inf");
        final String directory = "src/test/graphs/";
        IOHandler io = new IOHandler();
        Graph graph = io.readDot(directory + "Nodes_8_Random.dot");
        BranchAndBound scheduler = new BranchAndBound();
        scheduler.setController(this);
        Schedule schedule = scheduler.run(graph, 4);
    }
    public void setBestText(int currentShortestPath){
        bestCurrentText.setText(String.valueOf(currentShortestPath));
    }

    public void setControllerGraph() {
    }

//    public void setBestText(int currentShortestPath) {
//        bestCurrentText.setText("inf");
//        Task<Void> setBest =
//                new Task<Void>() {
//                    @Override
//                    protected Void call() throws Exception {
//                        try {
//                            bestCurrentText.setText(String.valueOf(currentShortestPath));
//
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                        return null;
//                    }
//                };
//        Thread bestThread = new Thread(setBest);
//        bestThread.start();
//    }
}
