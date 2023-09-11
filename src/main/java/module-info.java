module com.jonayed.restaurantdatabasesystem {
    requires javafx.controls;
    requires javafx.fxml;

    exports client;
    opens client to javafx.fxml;

    exports controllers;
    opens controllers to javafx.fxml;

    exports dto;
    opens dto to javafx.fxml;

    exports models;
    opens models to javafx.fxml;

    exports server;
    opens server to javafx.fxml;

    exports util;
    opens util to javafx.fxml;
}