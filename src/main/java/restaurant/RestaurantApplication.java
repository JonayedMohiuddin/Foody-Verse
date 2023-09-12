package restaurant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Restaurant;
import server.SocketWrapper;

import java.io.IOException;

public class RestaurantApplication extends Application
{
    // Application reference
    private Stage stage;
    public Stage getStage()
    {
        return stage;
    }

    private SocketWrapper socketWrapper;
    public SocketWrapper getSocketWrapper()
    {
        return socketWrapper;
    }

    public String username;

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
        showLoginPage();
    }

    public void connectToServer()
    {
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

    public void showLoginPage() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/restaurant-login-view.fxml"));
        Parent root = fxmlLoader.load();

        RestaurantLoginController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init();

        stage.setTitle("Login or Signup");
        stage.setScene(new Scene(root, 600, 400));
        stage.setMinWidth(400);
        stage.setMinHeight(300);
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
        stage.setScene(new Scene(root, 900, 650));
        stage.setResizable(false);
        stage.show();
    }

    public void showSignUpPage()
    {

    }

    public static void main(String[] args)
    {
        launch();
    }
}