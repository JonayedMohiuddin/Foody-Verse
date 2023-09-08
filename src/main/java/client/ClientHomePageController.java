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
import javafx.scene.layout.Border;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import models.Restaurant;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHomePageController
{
    public Button homePageLogOutButton;
    public FlowPane onlineRestaurantFlowPane;
    public TextField searchBoxField;
    public Label homepageUserNameText;
    public ChoiceBox clientHomePageFilterChoiceBox;

    ClientApplication application;
    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    ConcurrentHashMap<Integer, Restaurant> restaurantList;

    public void init()
    {
        DatabaseRequestDTO databaseRequestDTO = new DatabaseRequestDTO(DatabaseRequestDTO.RequestType.RESTAURANT_LIST);
        try
        {
            application.getSocketWrapper().write(databaseRequestDTO);
        }
        catch (IOException e)
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
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.err.println("Class : HomePageController | Method : init | While reading restaurant list from server");
            System.err.println("Error : " + e.getMessage());
        }

        homepageUserNameText.setText(application.getUserName());
        addRestaurantsUI();
    }

    public void addRestaurantsUI()
    {
        for (Restaurant restaurant : restaurantList.values())
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

            onlineRestaurantFlowPane.getChildren().add(restaurantBox);
        }
    }

    public void homePageLogOutButtonClicked(ActionEvent actionEvent)
    {

    }
}