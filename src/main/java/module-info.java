module com.jonayed.restaurantdatabasesystem {
    requires javafx.controls;
    requires javafx.fxml;

    exports client;
    opens client to javafx.fxml;

    exports dto;
    opens dto to javafx.fxml;

    exports prototypes;
    opens prototypes to javafx.fxml;

    exports server;
    opens server to javafx.fxml;

    exports misc;
    opens misc to javafx.fxml;
    exports restaurant;
    opens restaurant to javafx.fxml;
}