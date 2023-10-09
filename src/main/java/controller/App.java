package controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private Scene scene;
    private VisualisationController visualisationController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/visualisation.fxml"));
        scene = new Scene(fxmlLoader.load(), 640, 480);
        this.visualisationController = fxmlLoader.getController();
        this.visualisationController.initialize();
        stage.setTitle("Scheduling Algorithm Visualisation");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void setControllerGraph(){
        visualisationController.setControllerGraph();
    }

    public static void main(String[] args) {
        launch();
    }
}
