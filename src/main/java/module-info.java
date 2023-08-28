module com.jonayed.restaurantdatabasesystem {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.jonayed.restaurantdatabasesystem to javafx.fxml;
    exports com.jonayed.restaurantdatabasesystem;
}