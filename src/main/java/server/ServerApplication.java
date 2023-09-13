package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerApplication extends Application
{
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        stage = primaryStage;
        showServerPage();
    }

    public void showServerPage() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/server-admin-view.fxml"));
        Parent root = fxmlLoader.load();

        ServerController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init();

        stage.setTitle("Admin Page");
        stage.setScene(new Scene(root, 900, 650));
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}