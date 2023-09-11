package client;

import dto.DatabaseRequestDTO;
import dto.RestaurantListDTO;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import models.Food;
import models.Restaurant;
import models.RestaurantSearches;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHomeController
{
    // CHANGABLE LABELS
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
    public ScrollPane scrollpane;

    // CLIENT APPLICATION REFERENCE
    ClientApplication application;

    // SEARCH OPTIONS
    ArrayList<String> restaurantSearchOptions;
    ArrayList<String> foodSearchOptions;
    String currentViewType;
    String currentSearchFilterType;

    // MY CART //

    // CART DETAILS
    ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList; // Map<Restaurant ID, Map<Food, Count>>
    double cartTotalPrice = 0;
    int cartTotalItems = 0;

    // CART NOTIFICATION ICON
    public Circle cartItemCountBg;
    public Label cartItemCountLabel;

    // DATABASE
    ConcurrentHashMap<Integer, Restaurant> restaurantList;
    ArrayList<Food> foodList;

    // ASSETS //

    // IMAGES
    Image restaurantImageMedium;
    Image restaurantImageLarge;
    Image foodImage;

    // FONTS
    Font robotoBoldFont20;
    Font robotoBoldFont15;
    Font robotoRegularFont20;
    Font robotoRegularFont15;
    Font robotoRegularFont12;
    Font robotoLightFont20;
    Font robotoLightFont15;

    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        cartItemCountBg.setVisible(false);
        cartItemCountLabel.setVisible(false);
        cartFoodList = new ConcurrentHashMap<>();

        System.out.println("Client Home Page");

        // CHANGE USERNAME LABEL
        usernameLabel.setText(application.getUsername());

        // LOAD IMAGES
        restaurantImageMedium = new Image("file:src/main/resources/assets/RestaurantImage.jpg", 175, 125, false, false);
        restaurantImageLarge = new Image("file:src/main/resources/assets/RestaurantImage.jpg", 263, 188, false, false);
        foodImage = new Image("file:src/main/resources/assets/Burger.jpg", 175, 125, false, false);

        // LOAD FONTS
        robotoBoldFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Bold.ttf"), 15);
        robotoBoldFont20 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Bold.ttf"), 20);
        robotoLightFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Light.ttf"), 15);
        robotoLightFont20 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Light.ttf"), 20);
        robotoRegularFont12 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Regular.ttf"), 12);
        robotoRegularFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Regular.ttf"), 15);
        robotoRegularFont20 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Regular.ttf"), 20);

        // REQUEST FOR DATABASE
        DatabaseRequestDTO databaseRequestDTO = new DatabaseRequestDTO(DatabaseRequestDTO.RequestType.RESTAURANT_LIST);
        try
        {
            application.getSocketWrapper().write(databaseRequestDTO);
        } catch (IOException e)
        {
            System.err.println("Class : HomePageController | Method : init | While sending restaurant list request to server");
            System.err.println("Error : " + e.getMessage());
        }

        // READ DATABASE - RESTAURANT LIST
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
        restaurantSearchOptions.addAll(Arrays.asList(Options.RESTAURANT_NAME, Options.RESTAURANT_RATING, Options.RESTAURANT_PRICE, Options.RESTAURANT_CATEGORY, Options.RESTAURANT_ZIPCODE));

        // POPULATE FOOD SEARCH OPTIONS
        foodSearchOptions = new ArrayList<>();
        foodSearchOptions.addAll(Arrays.asList(Options.FOOD_NAME, Options.FOOD_CATEGORY, Options.FOOD_PRICE));

        // CREATE CHOICE BOXES WITH THE CHOICE OPTIONS AND ATTACH LISTENERS FOR CHANGES
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

        viewChoiceBox.getItems().addAll(Options.VIEW_RESTAURANT, Options.VIEW_FOOD);
        viewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                System.out.println("Selected item on view option box : " + newValue);
                handleViewOptionChange(newValue);
            }
        });

        // SET DEFAULT CHOICE BOX VALUES
        currentViewType = Options.VIEW_RESTAURANT;
        viewChoiceBox.setValue(Options.VIEW_RESTAURANT);
    }

    private void handleViewOptionChange(String newValue)
    {
        currentViewType = newValue;
        searchFilterChoiceBox.getItems().clear();

        if (newValue.equals(Options.VIEW_RESTAURANT))
        {
            resetFlowPane();
            addRestaurantListToFlowPane();
            searchFilterChoiceBox.getItems().addAll(restaurantSearchOptions);
            searchFilterChoiceBox.setValue(Options.RESTAURANT_NAME);
        }
        else if (newValue.equals(Options.VIEW_FOOD))
        {
            resetFlowPane();
            addFoodListToFlowPane();
            searchFilterChoiceBox.getItems().addAll(foodSearchOptions);
            searchFilterChoiceBox.setValue(Options.FOOD_NAME);
        }
    }

    private void handleFilterOptionChange(String newValue)
    {
        if (currentSearchFilterType.equals(Options.RESTAURANT_RATING) || currentSearchFilterType.equals(Options.FOOD_PRICE))
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

    public void restaurantClicked(Restaurant restaurant)
    {

        flowpaneTitleLabel.setText("Restaurant " + restaurant.getName());
        resetFlowPane();
        addRestaurantDetailHeading(restaurant);
        addFoodListToFlowPane(restaurant.getFoodList());
    }

    // Add to cart
    public void foodClicked(Food food)
    {
        System.out.println("Food clicked. Food name : " + food.getName());

        cartTotalItems++;
        cartTotalPrice += food.getPrice();
        cartItemCountLabel.setText(String.valueOf(cartTotalItems));
        cartItemCountLabel.setVisible(true);
        cartItemCountBg.setVisible(true);

        // Get or default : If the key is present, return the value, else return the default value
        // var x = getOrDefault(key, defaultValue);
        // So, x = key if key is present, else x = defaultValue

        Integer restaurantId = food.getRestaurantId();
        HashMap<Food, Integer> foodCountMap = cartFoodList.getOrDefault(restaurantId, new HashMap<>());
        cartFoodList.put(restaurantId, foodCountMap);
        foodCountMap.put(food, foodCountMap.getOrDefault(food, 0) + 1);

//        Large but simplistic code
//        if(cartFoodList.contains(food.getRestaurantId()))
//        {
//            HashMap<Food, Integer> foodCountMap = cartFoodList.get(food.getRestaurantId());
//            if(foodCountMap.containsKey(food))
//            {
//                foodCountMap.put(food, foodCountMap.get(food) + 1);
//            }
//            else
//            {
//                foodCountMap.put(food, 1);
//            }
//        }
//        else
//        {
//            HashMap<Food, Integer> foodCountMap = new HashMap<>();
//            foodCountMap.put(food, 1);
//            cartFoodList.put(food.getRestaurantId(), foodCountMap);
//        }
    }

    public void resetFlowPane()
    {
        flowpane.getChildren().clear();
        scrollpane.setVvalue(0);
    }

    public void addRestaurantListToFlowPane()
    {
        for (Restaurant restaurant : restaurantList.values())
        {
            addRestaurantToFlowPane(restaurant);
        }
    }

    public void addRestaurantListToFlowPane(ArrayList<Restaurant> restaurants)
    {
        for (Restaurant restaurant : restaurants)
        {
            addRestaurantToFlowPane(restaurant);
        }
    }

    public void addFoodListToFlowPane(ArrayList<Food> foods)
    {
        for (Food food : foods)
        {
            addFoodToFlowPane(food);
        }
    }

    public void addFoodListToFlowPane()
    {
        for (Food food : foodList)
        {
            addFoodToFlowPane(food);
        }
    }

    public void backButtonClickedOnRestaurantMenu()
    {
        resetFlowPane();
        addRestaurantListToFlowPane();
        viewChoiceBox.setValue(Options.VIEW_RESTAURANT);
        searchFilterChoiceBox.setValue(Options.RESTAURANT_NAME);
    }

    // FlowPane
    //     |---+ VBox
    //            |---+ HBox
    //                   |---+ Descriptor Label
    //                   |---+ Content Label

    public void addRestaurantDetailHeading(Restaurant restaurant)
    {
        Font descriptorFont = robotoBoldFont20;
        Font contentFont = robotoRegularFont20;
        int descriptorWidth = 150;

        GridPane imageContainer = new GridPane();
        ImageView restaurantImageView = new ImageView(restaurantImageLarge);
        imageContainer.getChildren().add(restaurantImageView);

        VBox restaurantInfoBox = new VBox();

        restaurantInfoBox.setPadding(new Insets(5, 150, 30, 30)); // top right bottom left
        restaurantInfoBox.setSpacing(12);

        // Descriptor : Content //
        // Name
        HBox restaurantNameContainer = new HBox();

        Label restaurantNameLabelDescriptor = new Label("Name : ");
        restaurantNameLabelDescriptor.setFont(descriptorFont);
        restaurantNameLabelDescriptor.setAlignment(Pos.CENTER_LEFT);
        restaurantNameLabelDescriptor.setMinWidth(descriptorWidth);

        Label restaurantNameLabelContent = new Label(restaurant.getName());
        restaurantNameLabelContent.setFont(contentFont);
        restaurantNameLabelContent.setAlignment(Pos.CENTER_LEFT);

        restaurantNameContainer.getChildren().addAll(restaurantNameLabelDescriptor, restaurantNameLabelContent);

        // Rating
        HBox restaurantRatingContainer = new HBox();

        Label restaurantRatingLabelDescriptor = new Label("Rating : ");
        restaurantRatingLabelDescriptor.setFont(descriptorFont);
        restaurantRatingLabelDescriptor.setAlignment(Pos.CENTER_LEFT);
        restaurantRatingLabelDescriptor.setMinWidth(descriptorWidth);

        Label restaurantRatingLabelContent = new Label( + restaurant.getScore() + " / 5");
        restaurantRatingLabelContent.setFont(contentFont);
        restaurantRatingLabelContent.setAlignment(Pos.CENTER_LEFT);

        restaurantRatingContainer.getChildren().addAll(restaurantRatingLabelDescriptor, restaurantRatingLabelContent);

        // Categories
        HBox restaurantCategoryContainer = new HBox();

        String categories = "";
        for(int i = 0; i < restaurant.getCategories().size(); i++)
        {
            categories = categories + restaurant.getCategories().get(i);
            if(i != restaurant.getCategories().size()-1) categories += ", ";
        }
        Label restaurantCategoryLabelDescriptor = new Label("Categories : ");
        restaurantCategoryLabelDescriptor.setFont(descriptorFont);
        restaurantCategoryLabelDescriptor.setAlignment(Pos.CENTER_LEFT);
        restaurantCategoryLabelDescriptor.setMinWidth(descriptorWidth);

        Label restaurantCategoryLabelContent = new Label(categories);
        restaurantCategoryLabelContent.setFont(contentFont);
        restaurantCategoryLabelContent.setAlignment(Pos.CENTER_LEFT);

        restaurantCategoryContainer.getChildren().addAll(restaurantCategoryLabelDescriptor, restaurantCategoryLabelContent);

        // Price
        HBox restaurantPriceContainer = new HBox();

        Label restaurantPriceLabelDescriptor = new Label("Price : ");
        restaurantPriceLabelDescriptor.setFont(descriptorFont);
        restaurantPriceLabelDescriptor.setAlignment(Pos.CENTER_LEFT);
        restaurantPriceLabelDescriptor.setMinWidth(descriptorWidth);

        Label restaurantPriceLabelContent = new Label(restaurant.getPrice());
        restaurantPriceLabelContent.setFont(contentFont);
        restaurantPriceLabelContent.setAlignment(Pos.CENTER_LEFT);

        restaurantPriceContainer.getChildren().addAll(restaurantPriceLabelDescriptor, restaurantPriceLabelContent);

        // Zipcode
        HBox restaurantZipcodeContainer = new HBox();

        Label restaurantZipcodeDescriptor = new Label("Zipcode : ");
        restaurantZipcodeDescriptor.setFont(descriptorFont);
        restaurantZipcodeDescriptor.setAlignment(Pos.CENTER_LEFT);
        restaurantZipcodeDescriptor.setMinWidth(descriptorWidth);

        Label restaurantZipcodeContent = new Label(restaurant.getZipcode());
        restaurantZipcodeContent.setFont(contentFont);
        restaurantZipcodeContent.setAlignment(Pos.CENTER_LEFT);

        restaurantZipcodeContainer.getChildren().addAll(restaurantZipcodeDescriptor, restaurantZipcodeContent);

        Button backButton = new Button("Back");
        backButton.setFont(robotoBoldFont15);
        backButton.setAlignment(Pos.TOP_LEFT);
        backButton.setOnMouseClicked(event -> backButtonClickedOnRestaurantMenu());

        restaurantInfoBox.getChildren().addAll(restaurantNameContainer, restaurantRatingContainer, restaurantCategoryContainer, restaurantPriceContainer, restaurantZipcodeContainer);
        flowpane.getChildren().addAll(imageContainer, restaurantInfoBox, backButton);
    }

    public void addRestaurantToFlowPane(Restaurant restaurant)
    {
        VBox restaurantBox = new VBox();

        ImageView imageView = new ImageView(restaurantImageMedium);
        imageView.setOnMouseClicked(event -> {
            System.out.println("Restaurant clicked. Restaurant name : " + restaurant.getName());
            restaurantClicked(restaurant);
        });

//        ImageView imageView = new ImageView("file:src/main/resources/assets/Burger.jpg");
//        imageView.setFitWidth(175);
//        imageView.setFitHeight(125);

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
        for (int i = 0; i < restaurant.getCategories().size(); i++)
        {
            categoriesString += restaurant.getCategories().get(i);
            if (i != restaurant.getCategories().size() - 1)  categoriesString += ", ";
        }

        Label categoryLabel = new Label(categoriesString);
        categoryLabel.setFont(robotoRegularFont12);
        categoryLabel.setAlignment(Pos.CENTER_LEFT);
        categoryLabel.setMaxWidth(175);

        Label zipcodeLabel = new Label("Zipcode " + restaurant.getZipcode());
        zipcodeLabel.setFont(robotoRegularFont12);
        zipcodeLabel.setAlignment(Pos.CENTER_LEFT);
        zipcodeLabel.setMaxWidth(175);

        restaurantBox.getChildren().addAll(imageView, restaurantNameLabel, ratingAndPriceLabel, categoryLabel, zipcodeLabel);
        restaurantBox.setPadding(new Insets(20, 30, 0, 10));

        flowpane.getChildren().add(restaurantBox);
    }

    public void addFoodToFlowPane(Food food)
    {
        VBox foodBox = new VBox();

        ImageView imageView = new ImageView(foodImage);
        imageView.setOnMouseClicked(event -> {
            foodClicked(food);
        });

//        ImageView imageView = new ImageView("file:src/main/resources/assets/Burger.jpg");
//        imageView.setFitWidth(175);
//        imageView.setFitHeight(125);

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

    public void homePageLogOutButtonClicked(ActionEvent actionEvent)
    {
        System.out.println("Logout button clicked");
    }

    public void cartButtonClicked(ActionEvent actionEvent)
    {
        System.out.println("Cart button clicked");
        application.showCartPage();
    }

    public void logoutButtonClicked(ActionEvent actionEvent)
    {
        System.out.println("Logout button clicked");
    }

    // ======================================================================================================
    // SEARCH RELATED METHODS
    // ======================================================================================================

    public void search()
    {
        System.out.println("Searching (filter : " + currentSearchFilterType + ") : " + searchTextField.getText());

        if (currentSearchFilterType.equals(Options.RESTAURANT_NAME))
        {
            flowpaneTitleLabel.setText("Restaurants with name " + searchTextField.getText());
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByName(searchTextField.getText(), restaurantList);
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_CATEGORY))
        {
            flowpaneTitleLabel.setText("Restaurants with category " + searchTextField.getText());
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByCattegory(searchTextField.getText(), restaurantList);
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_PRICE))
        {
            String opt = "";
            if ("cheap".contains(searchTextField.getText()) || "$".equals(searchTextField.getText()))
            {
                opt = "$";
            }
            else if ("medium".contains(searchTextField.getText()) || "$$".equals(searchTextField.getText()))
            {
                opt = "$$";
            }
            else if ("expensive".contains(searchTextField.getText()) || "$$$".equals(searchTextField.getText()))
            {
                opt = "$$$";
            }
            else
            {
                return;
            }
            flowpaneTitleLabel.setText("Restaurants with price category " + opt);

            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantByPrice(opt, restaurantList);
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_ZIPCODE))
        {
            flowpaneTitleLabel.setText("Restaurants with zipcode " + searchTextField.getText());
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByZipcode(searchTextField.getText(), restaurantList);
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_RATING))
        {
            ArrayList<Double> range = readRangeFromSearchBoxes();
            if (range.size() < 2) return;
            DecimalFormat decimalFormat = new DecimalFormat("#.##"); // to omit trailing .0 looks ugly
            flowpaneTitleLabel.setText("Restaurants with rating " + decimalFormat.format(range.get(0)) + " to " + decimalFormat.format(range.get(1)));
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByRating(range.get(0), range.get(1), restaurantList);
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.FOOD_NAME))
        {
            flowpaneTitleLabel.setText("Foods with name " + searchTextField.getText());
            ArrayList<Food> foods = RestaurantSearches.searchFoodByName(searchTextField.getText(), foodList);
            resetFlowPane();
            addFoodListToFlowPane(foods);
        }
        else if (currentSearchFilterType.equals(Options.FOOD_CATEGORY))
        {
            flowpaneTitleLabel.setText("Foods with category " + searchTextField.getText());
            ArrayList<Food> foods = RestaurantSearches.searchFoodByCategory(searchTextField.getText(), foodList);
            resetFlowPane();
            addFoodListToFlowPane(foods);
        }
        else if (currentSearchFilterType.equals(Options.FOOD_PRICE))
        {
            ArrayList<Double> range = readRangeFromSearchBoxes();
            if (range.size() < 2) return;
            DecimalFormat decimalFormat = new DecimalFormat("#.##"); // to omit trailing .0 looks ugly
            flowpaneTitleLabel.setText("Foods with price " + decimalFormat.format(range.get(0)) + "$ to " + decimalFormat.format(range.get(1)) + "$");
            ArrayList<Food> foods = RestaurantSearches.searchFoodByPriceRange(range.get(0), range.get(1), foodList);
            resetFlowPane();
            addFoodListToFlowPane(foods);
        }
        else
        {
            System.err.println("Class : HomePageController | Method : search | Invalid search filter type, How?? IMPOSSIBLE! CDI");
        }
    }

    public ArrayList<Double> readRangeFromSearchBoxes()
    {
        ArrayList<Double> range = new ArrayList<>();
        Double min, max;

        try
        {
            rangeSearchMinField.setStyle("");
            min = Double.parseDouble(rangeSearchMinField.getText());
            range.add(min);
        } catch (NumberFormatException e)
        {
            System.out.println("Class : HomePageController | Method : search | While parsing rating range, min is not a number");
            rangeSearchMinField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }

        try
        {
            rangeSearchMaxField.setStyle("");
            max = Double.parseDouble(rangeSearchMaxField.getText());
            range.add(max);
        } catch (NumberFormatException e)
        {
            System.out.println("Class : HomePageController | Method : search | While parsing rating range, max is not a number");
            rangeSearchMaxField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }
        return range;
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

    public void resetButtonClicked(ActionEvent actionEvent)
    {
        flowpaneTitleLabel.setText("All Restaurants");
        resetFlowPane();
        addRestaurantListToFlowPane();
        viewChoiceBox.setValue(Options.VIEW_RESTAURANT);
        searchFilterChoiceBox.setValue(Options.RESTAURANT_NAME);
        searchTextField.setText("");
        rangeSearchMinField.setText("");
        rangeSearchMaxField.setText("");
    }

    public void switchSearchBox()
    {
        searchTextField.setVisible(!searchTextField.isVisible());
        rangeSearchMinField.setVisible(!rangeSearchMinField.isVisible());
        rangeSearchSeparator.setVisible(!rangeSearchSeparator.isVisible());
        rangeSearchMaxField.setVisible(!rangeSearchMaxField.isVisible());

        searchTextField.setText("");
        rangeSearchMinField.setText("");
        rangeSearchMaxField.setText("");

        rangeSearchMinField.setStyle("");
        rangeSearchMaxField.setStyle("");
    }

    // ======================================================================================================
    // UTIL METHODS AND CLASSES
    // ======================================================================================================

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
        public static String RESTAURANT_RATING = "Rating Range";
        public static String RESTAURANT_PRICE = "Price Category";
        public static String RESTAURANT_CATEGORY = "Category";
        public static String RESTAURANT_ZIPCODE = "Zipcode";

        // SPACE AFTER FOOD TO DIFFERENTIATE THESE OPTIONS FROM RESTAURANT OPTIONS IN CHOICE BOX
        // THERE MIGHT BE A BETTER WAY TO DO THIS, BUT FEELLING LAZY :P
        // TODO : FIND A BETTER WAY TO DO THIS
        public static String FOOD_NAME = "Name ";
        public static String FOOD_CATEGORY = "Category ";
        public static String FOOD_PRICE = "Price Range ";

        public static String VIEW_RESTAURANT = "Restaurant";
        public static String VIEW_FOOD = "Food";
    }
}

/* TODO : SEE LATER
import javafx.animation.ScaleTransition;
        import javafx.application.Application;
        import javafx.scene.Scene;
        import javafx.scene.image.Image;
        import javafx.scene.image.ImageView;
        import javafx.scene.layout.StackPane;
        import javafx.stage.Stage;
        import javafx.util.Duration;

public class HoverZoomEffectExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hover Zoom Effect Example");

        Image image = new Image("file:src/main/resources/assets/Burger.jpg");
        ImageView imageView = new ImageView(image);

        // Set the initial scale
        double initialScale = 1.0;
        imageView.setScaleX(initialScale);
        imageView.setScaleY(initialScale);

        // Define the zoom-in and zoom-out scales
        double zoomInScale = 1.2; // Scale factor for zooming in
        double zoomOutScale = 1.0; // Initial scale (no zoom)

        // Create ScaleTransition for zooming in
        ScaleTransition zoomInTransition = new ScaleTransition(Duration.millis(300), imageView);
        zoomInTransition.setToX(zoomInScale);
        zoomInTransition.setToY(zoomInScale);

        // Create ScaleTransition for zooming out
        ScaleTransition zoomOutTransition = new ScaleTransition(Duration.millis(300), imageView);
        zoomOutTransition.setToX(zoomOutScale);
        zoomOutTransition.setToY(zoomOutScale);

        // Add mouse event handlers
        imageView.setOnMouseEntered(event -> {
            // Play zoom-in animation on mouse hover
            zoomInTransition.play();
        });

        imageView.setOnMouseExited(event -> {
            // Play zoom-out animation when the mouse moves away
            zoomOutTransition.play();
        });

        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
*/