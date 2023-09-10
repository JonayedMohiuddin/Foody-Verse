package client;

import dto.DatabaseRequestDTO;
import dto.RestaurantListDTO;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.Food;
import models.Restaurant;
import models.RestaurantSearches;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHomePageController
{
    public Label usernameLabel;

    // SEARCH BAR
    public TextField searchTextField;
    public ChoiceBox<String> viewChoiceBox;
    public ChoiceBox<String> searchFilterChoiceBox;
    public TextField rangeSearchMinField;
    public Label rangeSearchSeparator;
    public TextField rangeSearchMaxField;

    // FLOW PANE
    public Label flowpaneTitleLabel;
    public FlowPane flowpane;

    ClientApplication application;
    ConcurrentHashMap<Integer, Restaurant> restaurantList;
    ArrayList<Food> foodList;
    ArrayList<String> restaurantSearchOptions;
    ArrayList<String> foodSearchOptions;
    String currentViewType;
    String currentSearchFilterType;
    Font robotoRegularFont15;
    Font robotoRegularFont12;
    Font robotoBoldFont15;
    Font robotoLightFont15;

    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        System.out.println("Client Home Page");
        usernameLabel.setText(application.getUserName());

        // LOAD FONTS
        robotoRegularFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Regular.ttf"), 15);
        robotoBoldFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Bold.ttf"), 15);
        robotoLightFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Light.ttf"), 15);
        robotoRegularFont12 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Regular.ttf"), 12);
        if (robotoRegularFont15 == null || robotoBoldFont15 == null || robotoLightFont15 == null || robotoRegularFont12 == null)
        {
            System.err.println("Class : HomePageController | Method : init | Couldn't load fonts.");
        }

        // Request for database
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
                foodList = generateFoodList();
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

        // POPULATE RESTAURANT SEARCH OPTIONS
        restaurantSearchOptions = new ArrayList<>();
        restaurantSearchOptions.addAll(Arrays.asList(
                Options.RESTAURANT_NAME,
                Options.RESTAURANT_RATING,
                Options.RESTAURANT_PRICE,
                Options.RESTAURANT_CATEGORY,
                Options.RESTAURANT_ZIPCODE
        ));

        // POPULATE FOOD SEARCH OPTIONS
        foodSearchOptions = new ArrayList<>();
        foodSearchOptions.addAll(Arrays.asList(
                Options.FOOD_NAME,
                Options.FOOD_CATEGORY,
                Options.FOOD_PRICE
        ));

        searchFilterChoiceBox.getItems().addAll(restaurantSearchOptions);
        searchFilterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                System.out.println("Selected item on filter option box : " + newValue);
                handleFilterOptionChange(newValue);
            }
        });
        currentSearchFilterType = Options.RESTAURANT_NAME;
        searchFilterChoiceBox.setValue(Options.RESTAURANT_NAME);

        viewChoiceBox.getItems().addAll(
                Options.VIEW_RESTAURANT,
                Options.VIEW_FOOD
        );
        viewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                System.out.println("Selected item on view option box : " + newValue);
                handleViewOptionChange(newValue);
            }
        });
        currentViewType = Options.VIEW_RESTAURANT;
        viewChoiceBox.setValue(Options.VIEW_RESTAURANT);
    }

    private void handleViewOptionChange(String newValue)
    {
        currentViewType = newValue;
        searchFilterChoiceBox.getItems().clear();

        if (newValue.equals(Options.VIEW_RESTAURANT))
        {
            addRestaurantListToFlowPane();
            searchFilterChoiceBox.getItems().addAll(restaurantSearchOptions);
            searchFilterChoiceBox.setValue(Options.RESTAURANT_NAME);
        }
        else if (newValue.equals(Options.VIEW_FOOD))
        {
            addFoodListToFlowPane();
            searchFilterChoiceBox.getItems().addAll(foodSearchOptions);
            searchFilterChoiceBox.setValue(Options.FOOD_NAME);
        }
    }

    private void handleFilterOptionChange(String newValue)
    {
        if(currentSearchFilterType.equals(Options.RESTAURANT_RATING) || currentSearchFilterType.equals(Options.FOOD_PRICE))
        {
            System.out.println("Switching search box");
            switchSearchBox();
        }
        currentSearchFilterType = newValue;

        if (currentSearchFilterType.equals(Options.RESTAURANT_NAME))
        {
            searchTextField.setPromptText("Search by restaurant name");
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_RATING))
        {
            switchSearchBox();
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_PRICE))
        {
            searchTextField.setPromptText("cheap, medium, expensive or $, $$, $$$");
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_CATEGORY))
        {
            searchTextField.setPromptText("Search by restaurant category");
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_ZIPCODE))
        {
            searchTextField.setPromptText("Search by restaurant zipcode");
        }
        else if (currentSearchFilterType.equals(Options.FOOD_NAME))
        {
            searchTextField.setPromptText("Search by food name");
        }
        else if (currentSearchFilterType.equals(Options.FOOD_CATEGORY))
        {
            searchTextField.setPromptText("Search by food category");
        }
        else if (currentSearchFilterType.equals(Options.FOOD_PRICE))
        {
            switchSearchBox();
        }
    }

    public void resetFlowPane()
    {
        flowpane.getChildren().clear();
    }

    public void addRestaurantListToFlowPane()
    {
        resetFlowPane();
        for (Restaurant restaurant : restaurantList.values())
        {
            addRestaurantToFlowPane(restaurant);
        }
    }

    public void addRestaurantListToFlowPane(ArrayList<Restaurant> restaurantList)
    {
        resetFlowPane();
        for (Restaurant restaurant : restaurantList)
        {
            addRestaurantToFlowPane(restaurant);
        }
    }

    public void addFoodListToFlowPane()
    {
        resetFlowPane();
        for (Food food : foodList)
        {
            addFoodToFlowPane(food);
        }
    }

    public void addFoodToFlowPane(Food food)
    {
        VBox foodBox = new VBox();

        ImageView imageView = new ImageView("file:src/main/resources/assets/Burger.jpg");
        imageView.setFitWidth(175);
        imageView.setFitHeight(125);

        Label foodNameLabel = new Label(food.getName());
//        restaurantNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; - fx-font-family: Calibri Light;");
        foodNameLabel.setFont(robotoBoldFont15);
        foodNameLabel.setAlignment(Pos.CENTER_LEFT);
        foodNameLabel.setMaxWidth(175);

        Label foodCategory = new Label(food.getCategory());
        foodCategory.setFont(robotoRegularFont12);
        foodCategory.setAlignment(Pos.CENTER_LEFT);
        foodCategory.setMaxWidth(175);

        Label foodPrice = new Label(food.getPrice() + "  $");
        foodCategory.setFont(robotoRegularFont12);
        foodCategory.setAlignment(Pos.CENTER_LEFT);
        foodCategory.setMaxWidth(175);

        foodBox.getChildren().addAll(imageView, foodNameLabel, foodCategory, foodPrice);
        foodBox.setPadding(new Insets(20, 30, 0, 10));

        flowpane.getChildren().add(foodBox);
    }

    public void addRestaurantToFlowPane(Restaurant restaurant)
    {
        VBox restaurantBox = new VBox();

        ImageView imageView = new ImageView("file:src/main/resources/assets/Burger.jpg");
        imageView.setFitWidth(175);
        imageView.setFitHeight(125);

        imageView.setOnMouseClicked(event -> {
            System.out.println("Restaurant image clicked. Restaurant name : " + restaurant.getName());
        });

        Label restaurantNameLabel = new Label(restaurant.getName());
//        restaurantNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; - fx-font-family: Calibri Light;");
        restaurantNameLabel.setFont(robotoBoldFont15);
        restaurantNameLabel.setAlignment(Pos.CENTER_LEFT);
        restaurantNameLabel.setMaxWidth(175);

        Label ratingAndPriceLabel = new Label(restaurant.getScore() + "/5 , " + restaurant.getPrice());
        ratingAndPriceLabel.setFont(robotoRegularFont15);
        ratingAndPriceLabel.setAlignment(Pos.CENTER_LEFT);
        ratingAndPriceLabel.setMaxWidth(175);

        String categoriesString = "";
        for (String category : restaurant.getCategories())
        {
            categoriesString += category + ", ";
        }

        Label categoryLabel = new Label(categoriesString);
        categoryLabel.setFont(robotoRegularFont12);
        categoryLabel.setAlignment(Pos.CENTER_LEFT);
        categoryLabel.setMaxWidth(175);

        Label zipcodeLabel = new Label("Zipcode " + restaurant.getZipCode());
        zipcodeLabel.setFont(robotoRegularFont12);
        zipcodeLabel.setAlignment(Pos.CENTER_LEFT);
        zipcodeLabel.setMaxWidth(175);

        restaurantBox.getChildren().addAll(imageView, restaurantNameLabel, ratingAndPriceLabel, categoryLabel, zipcodeLabel);
        restaurantBox.setPadding(new Insets(20, 30, 0, 10));

        flowpane.getChildren().add(restaurantBox);
    }

    public void homePageLogOutButtonClicked(ActionEvent actionEvent)
    {

    }

    public void searchResetButtonClicked(ActionEvent actionEvent)
    {
        addRestaurantListToFlowPane();
        viewChoiceBox.setValue(Options.VIEW_RESTAURANT);
        searchFilterChoiceBox.setValue(Options.RESTAURANT_NAME);
    }

    public void cartButtonClicked(ActionEvent actionEvent)
    {
        System.out.println("Cart button clicked");
    }

    public void logoutButtonClicked(ActionEvent actionEvent)
    {
        System.out.println("Logout button clicked");
    }

    public void search()
    {
        System.out.println("Search button pressed. Searching : " + searchTextField.getText());

        if(currentSearchFilterType.equals(Options.RESTAURANT_NAME))
        {
           ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByName(searchTextField.getText(), restaurantList);
           addRestaurantListToFlowPane(restaurants);
        }
        else if(currentSearchFilterType.equals(Options.RESTAURANT_CATEGORY))
        {
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByCattegory(searchTextField.getText(), restaurantList);
            addRestaurantListToFlowPane(restaurants);
        }
        else if(currentSearchFilterType.equals(Options.RESTAURANT_PRICE))
        {
            String opt = "";
            if("cheap".contains(searchTextField.getText()))
            {
                opt = "$";
            }
            else if("medium".contains(searchTextField.getText()))
            {
                opt = "$$";
            }
            else if("expensive".contains(searchTextField.getText()))
            {
                opt = "$$$";
            }
            else
            {
                opt = searchTextField.getText();
            }


        }
    }

    public void switchSearchBox()
    {
        searchTextField.setVisible(!searchTextField.isVisible());
        rangeSearchMinField.setVisible(!rangeSearchMinField.isVisible());
        rangeSearchSeparator.setVisible(!rangeSearchSeparator.isVisible());
        rangeSearchMaxField.setVisible(!rangeSearchMaxField.isVisible());
    }

    public void searchButtonClicked(ActionEvent actionEvent)
    {
        search();
    }

    public void searchFieldKeyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            searchTextField.getParent().requestFocus();
            search();
        }
    }

    public void rangeSearchMinFieldKeyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            System.out.println("Range search min field enter pressed");
            rangeSearchMaxField.requestFocus();
        }
    }

    public void rangeSearchMaxFieldKeyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            System.out.println("Range search max field enter pressed");
            rangeSearchMaxField.getParent().requestFocus();
            search();
        }
    }

    ArrayList<Food> generateFoodList()
    {
        ArrayList<Food> foodList = new ArrayList<>();

        for (Restaurant restaurant : restaurantList.values())
        {
            foodList.addAll(restaurant.getFoodList());
        }

        return foodList;
    }

    private static final class Options
    {
        public static String RESTAURANT_NAME = "Name";
        public static String RESTAURANT_RATING = "Rating";
        public static String RESTAURANT_PRICE = "Price";
        public static String RESTAURANT_CATEGORY = "Category";
        public static String RESTAURANT_ZIPCODE = "Zipcode";

        // SPACE AFTER FOOD TO DIFFERENTIATE THESE OPTIONS FROM RESTAURANT OPTIONS
        // THERE MIGHT BE A BETTER WAY TO DO THIS, BUT FEELLING LAZY
        // TODO : FIND A BETTER WAY TO DO THIS
        public static String FOOD_NAME = "Name ";
        public static String FOOD_CATEGORY = "Category ";
        public static String FOOD_PRICE = "Price Range ";

        public static String VIEW_RESTAURANT = "Restaurant";
        public static String VIEW_FOOD = "Food";
    }
}