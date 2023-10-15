package controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * This class is used as our main application
 */
public class App extends Application {



    private Scene scene;
    private VisualisationController visualisationController;

    /**
     * Creates the JaavaFx stage
     * @param stage stage where app will run
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/visualisation.fxml"));
        scene = new Scene(fxmlLoader.load(), 1200, 595);
        this.visualisationController = fxmlLoader.getController();

        // load fonts
        Font.loadFont(
                App.class.getResource("/fonts/SHUTTLE-X.ttf").toExternalForm(),
                10
        );

        // run visualisation
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
