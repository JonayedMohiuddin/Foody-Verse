package restaurant;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Food;
import models.Restaurant;
import server.SocketWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantApplication extends Application
{
    public String username;
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    private SocketWrapper socketWrapper;
    public SocketWrapper getSocketWrapper()
    {
        return socketWrapper;
    }

    public Stage getStage()
    {
        return stage;
    }
    private Stage stage;

    private Restaurant restaurant;
    public Restaurant getRestaurant()
    {
        return restaurant;
    }
    public void setRestaurant(Restaurant restaurant)
    {
        this.restaurant = restaurant;
    }

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        stage = primaryStage;

        connectToServer();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                disconnectFromServer();
            }
        });

        showLoginPage();
    }

    public void connectToServer()
    {
        System.out.println("Connecting to server");
        try
        {
            socketWrapper = new SocketWrapper("127.0.0.1", 44444);
        }
        catch (IOException e)
        {
            System.err.println("Class : RestaurantApplication | Method : connectToServer");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void disconnectFromServer()
    {
        System.out.println("Disconnecting from server");
        try
        {
            socketWrapper.closeConnection();
        }
        catch (IOException e)
        {
            System.err.println("Class : RestaurantApplication | Method : disconnectFromServer | While closing connection with server");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void showLoginPage() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/restaurant-login-view.fxml"));
        Parent root = fxmlLoader.load();

        RestaurantLoginController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init();

        stage.setTitle("Login or Signup");
        stage.setScene(new Scene(root, 600, 400));
        stage.setResizable(false);
        stage.show();
    }

    public void showHomePage() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/restaurant-home-view.fxml"));
        Parent root = fxmlLoader.load();

        RestaurantHomeController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init();

        stage.setTitle("Home");
        stage.setScene(new Scene(root, 800, 650));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}