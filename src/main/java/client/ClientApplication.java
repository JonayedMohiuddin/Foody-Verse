package client;

import dto.LogoutDTO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientApplication extends Application
{
    // Window or Stage reference so that we can change the scene
    private Stage stage;
    public Stage getStage()
    {
        return stage;
    }

    // Socket Wrapper for communication with server
    private SocketWrapper socketWrapper;
    public SocketWrapper getSocketWrapper()
    {
        return socketWrapper;
    }

    // Username of the client
    private String userName;
    public String getUsername()
    {
        return userName;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    // DATABASE //
    // RESTAURANT LIST
    private ConcurrentHashMap<Integer, Restaurant> restaurantList;
    public ConcurrentHashMap<Integer, Restaurant> getRestaurantList()
    {
        return restaurantList;
    }
    public void setRestaurantList(ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        this.restaurantList = restaurantList;
    }

    // FOOD LIST
    private ArrayList<Food> foodList;
    public ArrayList<Food> getFoodList()
    {
        return foodList;
    }
    public void setFoodList(ArrayList<Food> foodList)
    {
        this.foodList = foodList;
    }

    // CART ITEMS LIST
    public ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList; // Map<Restaurant ID, Map<Food, Count>>
    public ConcurrentHashMap<Integer, HashMap<Food, Integer>> getCartFoodList()
    {
        return cartFoodList;
    }
    public void setCartFoodList(ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList)
    {
        this.cartFoodList = cartFoodList;
    }

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        stage = primaryStage;
        restaurantList = new ConcurrentHashMap<>();

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
        try
        {
            socketWrapper = new SocketWrapper("127.0.0.1", 44444);
        }
        catch (IOException e)
        {
            System.err.println("Class : ClientApplication | Method : connectToServer | While connecting to server");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void logoutCleanup()
    {
        System.out.println("Log out cleanup");
        try
        {
            socketWrapper.write(new LogoutDTO());
            socketWrapper.closeConnection();
            showLoginPage();
        }
        catch (IOException e)
        {
            System.err.println("Class : RestaurantApplication | Method : logoutCleanup | While logging out");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void showLoginPage() throws IOException
    {
        connectToServer();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/client-login-view.fxml"));
        Parent root = fxmlLoader.load();

        ClientLoginController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init();

        stage.setTitle("Login or Signup");
        stage.setScene(new Scene(root, 500, 600));
        stage.setResizable(false);
        stage.show();
    }

    // isDatabaseLoaded is true if database is loaded from server (or any other source) so that we don't have to load it again
    // We might call showHomePage() from multiple times we don't want to load database again and again
    // Reference of database is stored in ClientApplication class so that any controller can access it
    // without having to load it again.
    public void showHomePage(boolean isDatabaseLoaded) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/client-home-view.fxml"));
        Parent root = fxmlLoader.load();

        ClientHomeController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init(isDatabaseLoaded);

        stage.setTitle("Home");
        stage.setScene(new Scene(root, 900, 650));
        stage.setResizable(false);
        stage.show();
    }

    public void showCartPage() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/client-cart-view.fxml"));
        Parent root = fxmlLoader.load();

        ClientCartController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init();

        stage.setTitle("My Cart");
        stage.setScene(new Scene(root, 600, 650));
        stage.setResizable(false);
        stage.show();
    }

    public void showSignUpPage() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/client-signup-view.fxml"));
        Parent root = fxmlLoader.load();

        ClientSignupController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.init();

        stage.setTitle("Sign Up");
        stage.setScene(new Scene(root, 500, 600));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }

    public class FoodHBox
    {

    }
}