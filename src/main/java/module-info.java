module com.jonayed.restaurantdatabasesystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens controllers to javafx.fxml;
    exports controllers;
}