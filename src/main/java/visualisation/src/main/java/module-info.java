module com.example.visualisation {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.visualisation to javafx.fxml;
    exports com.example.visualisation;
}