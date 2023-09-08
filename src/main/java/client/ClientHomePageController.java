package client;

import dto.DatabaseRequestDTO;
import dto.RestaurantListDTO;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import models.Restaurant;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

class Options
{
    public static String RESTAURANT_NAME = "Restaurant Name";
    public static String RESTAURANT_RATING = "Restaurant Rating";
    public static String RESTAURANT_PRICE = "Restaurant Price";
    public static String FOOD_NAME = "Food Name";

    public static boolean isRestaurantSearchType(String str)
    {
        if(str.equals(RESTAURANT_NAME) || str.equals(RESTAURANT_RATING) || str.equals(RESTAURANT_PRICE))
        {
            return true;
        }
        return false;
    }

    public static boolean isFoodSearchType(String str)
    {
        if(str.equals(FOOD_NAME))
        {
            return true;
        }
        return false;
    }
}

public class ClientHomePageController
{
    public Button homePageLogOutButton;
    public FlowPane homePageFlowPane;
    public TextField searchBoxField;
    public Label homepageUserNameText;
    public ChoiceBox<String> clientHomePageFilterChoiceBox;
    public Label homePageFlowPaneTitleText;

    ClientApplication application;
    ConcurrentHashMap<Integer, Restaurant> restaurantList;

    String currentSearchType;

    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        DatabaseRequestDTO databaseRequestDTO = new DatabaseRequestDTO(DatabaseRequestDTO.RequestType.RESTAURANT_LIST);
        try
        {
            application.getSocketWrapper().write(databaseRequestDTO);
        } catch (IOException e)
        {
            System.err.println("Class : HomePageController | Method : init | While sending restaurant list request to server");
            System.err.println("Error : " + e.getMessage());
        }

        try
        {
            Object obj = application.getSocketWrapper().read();
            if (obj instanceof RestaurantListDTO)
            {
                RestaurantListDTO restaurantListDTO = (RestaurantListDTO) obj;
                restaurantList = restaurantListDTO.getRestaurantList();
                System.out.println("RestaurantListDTO received");
            }
            else
            {
                System.err.println("Class : HomePageController | Method : init | While reading restaurant list from server");
                System.err.println("Expected Restaurant List DTO but got something else");
            }
        } catch (IOException | ClassNotFoundException e)
        {
            System.err.println("Class : HomePageController | Method : init | While reading restaurant list from server");
            System.err.println("Error : " + e.getMessage());
        }

        clientHomePageFilterChoiceBox.getItems().addAll(
                Options.RESTAURANT_NAME,
                Options.RESTAURANT_RATING,
                Options.RESTAURANT_PRICE,
                Options.FOOD_NAME
        );

        clientHomePageFilterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected item on option box : " + newValue);
                if(oldValue == null) oldValue = Options.RESTAURANT_NAME;
                filterOptionChanged(newValue, oldValue);
            }
        });

        clientHomePageFilterChoiceBox.setValue(Options.RESTAURANT_NAME);

        homepageUserNameText.setText(application.getUserName());

        addRestaurantListToFlowPane();
    }

    public void filterOptionChanged(String newValue, String oldValue)
    {
        currentSearchType = newValue;
        System.out.println("Current search type : " + currentSearchType);
        handleHomeListSwitch(newValue, oldValue);

        if(newValue.equals(Options.RESTAURANT_NAME))
        {
            searchBoxField.setPromptText("Search restaurants by name");
        }
        else if(newValue.equals(Options.RESTAURANT_PRICE))
        {
            searchBoxField.setPromptText("Search restaurants by price");
        }
        else if(newValue.equals(Options.RESTAURANT_RATING))
        {
            searchBoxField.setPromptText("Search restaurants by rating");
        }
        else if(newValue.equals(Options.FOOD_NAME))
        {
            searchBoxField.setPromptText("Search foods by name");
        }
    }

    public void handleHomeListSwitch(String newValue, String oldValue)
    {
        if(Options.isFoodSearchType(newValue) && Options.isRestaurantSearchType(oldValue))
        {
            resetFlowPane();
        }
        else if(Options.isRestaurantSearchType(newValue) && Options.isFoodSearchType(oldValue))
        {
            resetFlowPane();
            addRestaurantListToFlowPane();
        }
    }

    public void resetFlowPane()
    {
        homePageFlowPane.getChildren().clear();
    }

    public void addRestaurantListToFlowPane()
    {
        for (Restaurant restaurant : restaurantList.values())
        {
            addRestaurantToFlowPane(restaurant);
        }
    }

    public void addRestaurantToFlowPane(Restaurant restaurant)
    {
        VBox restaurantBox = new VBox();

        ImageView imageView = new ImageView("file:src/main/resources/assets/RestaurantIcon.png");
        imageView.setFitWidth(140);
        imageView.setFitHeight(140);

        Label restaurantNameLabel = new Label(restaurant.getName());
        restaurantNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; - fx-font-family: Arial;");
        restaurantNameLabel.setAlignment(Pos.CENTER_LEFT);
        restaurantNameLabel.setMaxWidth(140);

        String rating = "";
        for (int i = 0; i < Math.floor(restaurant.getScore()); i++)
        {
            rating += "⭐";
        }
        Label ratingLabel = new Label(rating + " (" + restaurant.getScore() + "/5) , " + restaurant.getPrice());
        restaurantNameLabel.setStyle("-fx-font-size: 14px;");
        ratingLabel.setAlignment(Pos.CENTER_LEFT);
        ratingLabel.setMaxWidth(140);

        restaurantBox.getChildren().addAll(imageView, restaurantNameLabel, ratingLabel);

        restaurantBox.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: #f4f4f4;");
        restaurantBox.setPadding(new Insets(20, 30, 0, 10));

        homePageFlowPane.getChildren().add(restaurantBox);
    }

    public void homePageLogOutButtonClicked(ActionEvent actionEvent)
    {

    }
}