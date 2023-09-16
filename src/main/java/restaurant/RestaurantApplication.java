package restaurant;

import dto.LogoutDTO;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Restaurant;
import server.SocketWrapper;

import java.io.IOException;

public class RestaurantApplication extends Application
{
    public String username;
    private Stage stage;
    private SocketWrapper socketWrapper;
    private Restaurant restaurant;

    public static void main(String[] args)
    {
        launch();
    }

    public Stage getStage()
    {
        return stage;
    }

    public SocketWrapper getSocketWrapper()
    {
        return socketWrapper;
    }

    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public Restaurant getRestaurant()
    {
        return restaurant;
    }
    public void setRestaurant(Restaurant restaurant)
    {
        this.restaurant = restaurant;
    }

    String ipAddress = "127.0.0.1";
    int serverPort = 44444;

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        stage = primaryStage;

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                logoutCleanup();
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
            System.out.println("Could not connect to server");
            System.out.println("[*] Check if server is online");
            System.out.println("[*] Check if server is running on ip " + ipAddress);
            System.out.println("[*] Check if server is running on port " + serverPort);
            System.out.println("Aborting application");
            System.exit(1);
        }
    }

    public void logoutCleanup()
    {
        System.out.println("Disconnecting from server");

        try
        {
            socketWrapper.write(new LogoutDTO());
            socketWrapper.closeConnection();
            showLoginPage();
        }
        catch (IOException e)
        {
            System.err.println("Class : RestaurantApplication | Method : disconnectFromServer | While closing connection with server");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void showLoginPage() throws IOException
    {
        connectToServer();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/restaurant-login-view.fxml"));
        Parent root = fxmlLoader.load();

        RestaurantLoginController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init();

        stage.setTitle("Login or Signup");
        stage.setScene(new Scene(root, 500, 600));
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
        stage.setScene(new Scene(root, 500, 650));
        stage.setResizable(false);
        stage.show();
    }
}