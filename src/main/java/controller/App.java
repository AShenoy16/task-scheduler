package controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    private Scene scene;
    private VisualisationController visualisationController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/visualisation.fxml"));
        scene = new Scene(fxmlLoader.load(), 1200, 664);
        this.visualisationController = fxmlLoader.getController();

        Font.loadFont(
                App.class.getResource("/fonts/SHUTTLE-X.ttf").toExternalForm(),
                10
        );

        stage.setTitle("Scheduling Algorithm Visualisation");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        Platform.runLater(() -> {
            visualisationController.initialize();
        });
    }

    public void setControllerGraph(){
        visualisationController.setControllerGraph();
    }

    public static void main(String[] args) {
        launch();
    }
}
