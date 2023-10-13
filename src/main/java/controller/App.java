package controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
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
        scene = new Scene(fxmlLoader.load(), 1200, 700);
        this.visualisationController = fxmlLoader.getController();
        stage.setTitle("Scheduling Algorithm Visualisation");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        Platform.runLater(() -> {
            visualisationController.initialize();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
