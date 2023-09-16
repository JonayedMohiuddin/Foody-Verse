package client;

import dto.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import models.Food;
import models.Restaurant;
import models.RestaurantSearches;
import util.ImageTransitions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHomeController
{
    // CHANGEABLE LABELS
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
    // CART NOTIFICATION ICON
    public Circle cartItemCountBg;
    public Label cartItemCountLabel;
    public Circle deliveredItemCountBg;
    public Label deliveredItemCountLabel;

    // DYNAMIC IMAGES
    public ImageView restaurantViewBackButton;
    public ImageView searchFilterImageView;
    public ImageView viewImageView;

    // HOME
    public AnchorPane homeMenu;
    // CART MENU
    public StackPane cartMenu;
    public ListView<HBox> cartItemListView;
    public Label cartMenuUsernameLabel;
    // DELIVERED MENU
    public StackPane deliveredMenu;
    public ListView deliveredMenuListView;
    public ImageView deliveredMenuBackButton;
    public Label deliveredMenuUsernameLabel;
    // CLIENT APPLICATION REFERENCE
    ClientApplication application;
    ArrayList<String> restaurantSearchOptions;
    ArrayList<String> foodSearchOptions;
    // SEARCH TYPE //
    String currentViewType;
    String currentSearchFilterType;
    String currentWindowType;
    int currentRestaurantWindowID = -1;
    // CART DETAILS
    ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList; // Map<Restaurant ID, Map<Food, Count>>
    double cartTotalPrice = 0;
    int cartTotalItems = 0;
    // DELIVERED DETAILS
    ConcurrentHashMap<Integer, HashMap<Food, Integer>> deliveredFoodList; // Map<Restaurant ID, Map<Food, Count>>
    int deliveredTotalItems = 0;
    double deliveredTotalPrice = 0;

    // IMAGES
    Image restaurantImage_175by125;
    // ASSETS //
    Image restaurantImageLarge;
    Image foodImage;
    // FONTS
    Font robotoBoldFont20;
    //    HashMap<String, Image> restaurantImage;
    Font robotoBoldFont15;
    Font robotoRegularFont20;
    Font robotoRegularFont15;
    Font robotoRegularFont12;
    Font robotoLightFont20;
    Font robotoLightFont15;
    // IMAGES
    Image searchByNameImage, searchByRatingImage, searchByPriceImage, searchByCategoryImage, searchByZipcodeImage;
    Image viewRestaurantImage, viewFoodImage;
    Image addButtonImage, removeButtonImage;
    DecimalFormat decimalFormat;
    int currentMenuWindow = Menu.HOME;

    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        deliveredFoodList = new ConcurrentHashMap<>();

        System.out.println("Client Home Page");

        // SET MENU VISIBILITY
        homeMenu.setVisible(true);
        deliveredMenu.setVisible(false);
        cartMenu.setVisible(false);

        restaurantViewBackButton.setVisible(false);

        cartFoodList = new ConcurrentHashMap<>();

        decimalFormat = new DecimalFormat("#.#");

        // CHANGE USERNAME LABEL
        usernameLabel.setText(application.getUsername());
        cartMenuUsernameLabel.setText(application.getUsername());
        deliveredMenuUsernameLabel.setText(application.getUsername());

        // LOAD IMAGES
        searchByNameImage = new Image("file:src/main/resources/assets/name-search-icon.png");
        searchByRatingImage = new Image("file:src/main/resources/assets/rating-search-icon.png");
        searchByPriceImage = new Image("file:src/main/resources/assets/price-search-icon.png");
        searchByCategoryImage = new Image("file:src/main/resources/assets/category-search-icon.png");
        searchByZipcodeImage = new Image("file:src/main/resources/assets/zipcode-search-icon.png");

        viewRestaurantImage = new Image("file:src/main/resources/assets/restaurant-icon.png");
        viewFoodImage = new Image("file:src/main/resources/assets/food-icon.png");

        restaurantImage_175by125 = new Image("file:src/main/resources/assets/RestaurantImage.jpg", 175, 125, false, false);
        restaurantImageLarge = new Image("file:src/main/resources/assets/RestaurantImage.jpg", 263, 188, false, false);
        foodImage = new Image("file:src/main/resources/assets/Burger.jpg", 175, 125, false, false);

        addButtonImage = new Image("file:src/main/resources/assets/add-button-icon.png");
        removeButtonImage = new Image("file:src/main/resources/assets/remove-button-icon.png");


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
        }
        catch (IOException e)
        {
            System.err.println("Class : HomePageController | Method : init | While sending restaurant list request to server");
            System.err.println("Error : " + e.getMessage());
        }

        // READ DATABASE - RESTAURANT LIST
        try
        {
            Object obj = application.getSocketWrapper().read();
            if (obj instanceof DatabaseDTO databaseDTO)
            {
                application.setRestaurantList(databaseDTO.getRestaurantList());
                application.setFoodList(generateFoodList());
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

        // READ OFFLINE DATA
        try
        {
            application.getSocketWrapper().write(new RequestOfflinePendingOrDeliveryDataDTO());

            Object obj;
            while ((obj = application.getSocketWrapper().read()) != null)
            {
                if (obj instanceof DeliverDTO deliverDTO)
                {
                    int restaurantId = deliverDTO.getRestaurantId();
                    if (!deliveredFoodList.containsKey(restaurantId))
                    {
                        deliveredFoodList.put(deliverDTO.getRestaurantId(), new HashMap<>());
                    }
                    HashMap<Food, Integer> deliveredFoodCount = deliveredFoodList.get(restaurantId);
                    for (Food food : deliverDTO.getDeliverList().get(application.getUsername()).keySet())
                    {
                        deliveredFoodCount.put(food, deliveredFoodCount.getOrDefault(food, 0) + deliverDTO.getDeliverList().get(application.getUsername()).get(food));
                    }

                    for (Integer restId : deliveredFoodList.keySet())
                    {
                        for (Food food : deliveredFoodList.get(restId).keySet())
                        {
                            System.out.println("Food name " + food.getName() + " count " + deliveredFoodList.get(restId).get(food));
                        }
                    }
                }
                else if (obj instanceof StopDTO stopDTO)
                {
                    break;
                }
                else
                {
                    System.out.println("Unknown DTO, expected StopDTO or RequestOfflinePendingOrDeliveryDataDTO");
                    System.out.println("Class : HomePageController | Method : init | While reading offline data from server");
                    application.logoutCleanup();
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.err.println("Class : HomePageController | Method : init | While reading offline data from server");
            System.err.println("Error : " + e.getMessage());
        }

        updateNotification();


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

        currentWindowType = Options.HOME_WINDOW;

        System.out.println("Creating new thread to read from server");
        System.out.println();
        new ClientReadThread(this, application.getUsername() + " ##C RT");

    }

    public void newDelivery(DeliverDTO deliverDTO)
    {
        int restaurantId = deliverDTO.getRestaurantId();
        if (!deliveredFoodList.containsKey(restaurantId))
        {
            deliveredFoodList.put(deliverDTO.getRestaurantId(), new HashMap<>());
        }
        HashMap<Food, Integer> deliveredFoodCount = deliveredFoodList.get(restaurantId);
        for (Food food : deliverDTO.getDeliverList().get(application.getUsername()).keySet())
        {
            deliveredFoodCount.put(food, deliveredFoodCount.getOrDefault(food, 0) + deliverDTO.getDeliverList().get(application.getUsername()).get(food));
        }

        for (Integer restId : deliveredFoodList.keySet())
        {
            System.out.println("Restaurant ID " + restId);
            for (Food food : deliveredFoodList.get(restId).keySet())
            {
                System.out.println("Food name " + food.getName() + " count " + deliveredFoodList.get(restId).get(food));
            }
        }

        if(currentMenuWindow == Menu.DELIVERED)
        {
            deliveredMenuInit();
        }

        updateNotification();
    }

    public void newFoodAdded(Food food)
    {
        application.getFoodList().add(food);

        // If viewing window of recent added food restaurant, update UI
        if (currentWindowType.equals(Options.RESTAURANT_WINDOW) && currentRestaurantWindowID == food.getRestaurantId())
        {
            System.out.println("UPDATING UI : New food added : " + food.getName());

            resetFlowPane();
            addRestaurantDetailHeading(application.getRestaurantList().get(food.getRestaurantId()));
            addFoodListToFlowPane(application.getRestaurantList().get(food.getRestaurantId()).getFoodList());

            System.out.println("UI updated");

        }
        else if (currentViewType.equals(Options.VIEW_FOOD) && currentWindowType.equals(Options.HOME_WINDOW))
        {
            System.out.println("UPDATING UI : New food added : " + food.getName());


            resetFlowPane();
            addFoodListToFlowPane();

            System.out.println("UI UPDATED");
        }
    }

    public void newRestaurantAdded(Restaurant restaurant)
    {
        application.getRestaurantList().put(restaurant.getId(), restaurant);

        for (Food food : restaurant.getFoodList())
        {
            application.getFoodList().add(food);
        }

        if (currentWindowType.equals(Options.HOME_WINDOW) && currentViewType.equals(Options.VIEW_RESTAURANT))
        {
            System.out.println("UPDATING UI : New restaurant added : " + restaurant.getName());
//            Platform.runLater(() -> {
            resetFlowPane();
            addRestaurantListToFlowPane();
//            });
        }
    }

    private void handleViewOptionChange(String newValue)
    {
        currentViewType = newValue;
        searchFilterChoiceBox.getItems().clear();

        if (newValue.equals(Options.VIEW_RESTAURANT))
        {
            viewImageView.setImage(viewRestaurantImage);
            resetFlowPane();
            addRestaurantListToFlowPane();
            searchFilterChoiceBox.getItems().addAll(restaurantSearchOptions);
            searchFilterChoiceBox.setValue(Options.RESTAURANT_NAME);
            flowpaneTitleLabel.setText("All Restaurants");
        }
        else if (newValue.equals(Options.VIEW_FOOD))
        {
            if (currentWindowType.equals(Options.RESTAURANT_WINDOW))
            {
                searchTextField.setText("");
                rangeSearchMinField.setText("");
                rangeSearchMaxField.setText("");
                restaurantViewBackButton.setVisible(false);
                currentWindowType = Options.HOME_WINDOW;
                currentRestaurantWindowID = -1;
            }
            viewImageView.setImage(viewFoodImage);
            resetFlowPane();
            addFoodListToFlowPane();
            searchFilterChoiceBox.getItems().addAll(foodSearchOptions);
            searchFilterChoiceBox.setValue(Options.FOOD_NAME);
            flowpaneTitleLabel.setText("All Foods");
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
            searchFilterImageView.setImage(searchByNameImage);
            searchTextField.setPromptText("Search by restaurant name");
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_RATING))
        {
            searchFilterImageView.setImage(searchByRatingImage);
            switchSearchBox();
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_PRICE))
        {
            searchFilterImageView.setImage(searchByPriceImage);
            searchTextField.setPromptText("cheap, medium, expensive or $, $$, $$$");
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_CATEGORY))
        {
            searchFilterImageView.setImage(searchByCategoryImage);
            searchTextField.setPromptText("Search by restaurant category");
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_ZIPCODE))
        {
            searchFilterImageView.setImage(searchByZipcodeImage);
            searchTextField.setPromptText("Search by restaurant zipcode");
        }
        else if (currentSearchFilterType.equals(Options.FOOD_NAME))
        {
            searchFilterImageView.setImage(searchByNameImage);
            searchTextField.setPromptText("Search by food name");
        }
        else if (currentSearchFilterType.equals(Options.FOOD_CATEGORY))
        {
            searchFilterImageView.setImage(searchByCategoryImage);
            searchTextField.setPromptText("Search by food category");
        }
        else if (currentSearchFilterType.equals(Options.FOOD_PRICE))
        {
            searchFilterImageView.setImage(searchByPriceImage);
            switchSearchBox();
        }
    }

    public void updateNotification()
    {
        if (cartTotalItems == 0)
        {
            cartItemCountBg.setVisible(false);
            cartItemCountLabel.setVisible(false);
        }
        else
        {
            cartItemCountLabel.setText(String.valueOf(cartTotalItems));
            cartItemCountLabel.setVisible(true);
            cartItemCountBg.setVisible(true);
        }

//        if (deliveredTotalItems == 0)
//        {
//            deliveredItemCountBg.setVisible(false);
//            deliveredItemCountLabel.setVisible(false);
//        }
//        else
//        {
//            System.out.println("Delivered total items : " + deliveredTotalItems);
//            deliveredItemCountLabel.setText(String.valueOf(deliveredTotalItems));
//            deliveredItemCountLabel.setVisible(true);
//            deliveredItemCountBg.setVisible(true);
//        }
    }

    // Open restaurant window
    public void restaurantClicked(Restaurant restaurant)
    {
        flowpaneTitleLabel.setText("Restaurant " + restaurant.getName());
        resetFlowPane();
        addRestaurantDetailHeading(restaurant);
        addFoodListToFlowPane(restaurant.getFoodList());
        restaurantViewBackButton.setVisible(true);
        currentWindowType = Options.RESTAURANT_WINDOW;
        currentRestaurantWindowID = restaurant.getId();
    }

    // Add to cart
    public void foodClicked(Food food)
    {
        System.out.println("Food clicked. Food name : " + food.getName());

        cartTotalItems++;
        cartTotalPrice += food.getPrice();
        updateNotification();

        // Get or default : If the key is present, return the value, else return the default value
        // var x = getOrDefault(key, defaultValue);
        // So, x = key if key is present, else x = defaultValue
        Integer restaurantId = food.getRestaurantId();
        HashMap<Food, Integer> foodCountMap = cartFoodList.getOrDefault(restaurantId, new HashMap<>());
        cartFoodList.put(restaurantId, foodCountMap);
        foodCountMap.put(food, foodCountMap.getOrDefault(food, 0) + 1);
    }

    public void resetFlowPane()
    {
        flowpane.getChildren().clear();
        scrollpane.setVvalue(0);
    }

    public void addRestaurantListToFlowPane()
    {
        for (Restaurant restaurant : application.getRestaurantList().values())
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
        for (Food food : application.getFoodList())
        {
            addFoodToFlowPane(food);
        }
    }

    // ======================================================================================================
    // SEARCH RELATED METHODS
    // ======================================================================================================

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
//        ImageView restaurantImageView = new ImageView(restaurantImageLarge);

        ImageView restaurantImageView = new ImageView("file:src/main/resources/restaurant-images/" + restaurant.getName() + ".jpg");
        if (restaurantImageView.getImage().isError())
        {
            restaurantImageView = new ImageView(restaurantImage_175by125);
        }

        restaurantImageView.setFitHeight(188);
        restaurantImageView.setFitWidth(263);
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

        Label restaurantRatingLabelContent = new Label(restaurant.getScore() + " / 5");
        restaurantRatingLabelContent.setFont(contentFont);
        restaurantRatingLabelContent.setAlignment(Pos.CENTER_LEFT);

        restaurantRatingContainer.getChildren().addAll(restaurantRatingLabelDescriptor, restaurantRatingLabelContent);

        // Categories
        HBox restaurantCategoryContainer = new HBox();

        StringBuilder categories = new StringBuilder();
        for (int i = 0; i < restaurant.getCategories().size(); i++)
        {
            categories.append(restaurant.getCategories().get(i));
            if (i != restaurant.getCategories().size() - 1) categories.append(", ");
        }
        Label restaurantCategoryLabelDescriptor = new Label("Categories : ");
        restaurantCategoryLabelDescriptor.setFont(descriptorFont);
        restaurantCategoryLabelDescriptor.setAlignment(Pos.CENTER_LEFT);
        restaurantCategoryLabelDescriptor.setMinWidth(descriptorWidth);

        Label restaurantCategoryLabelContent = new Label(categories.toString());
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
        restaurantInfoBox.getChildren().addAll(restaurantNameContainer, restaurantRatingContainer, restaurantCategoryContainer, restaurantPriceContainer, restaurantZipcodeContainer);
        flowpane.getChildren().addAll(imageContainer, restaurantInfoBox); // ), backButtonContainer);
    }

    public void addRestaurantToFlowPane(Restaurant restaurant)
    {
        VBox restaurantBox = new VBox();

        // If image not found, use default image
        ImageView imageView = new ImageView("file:src/main/resources/restaurant-images/" + restaurant.getName() + ".jpg");
        if (imageView.getImage().isError())
        {
            imageView = new ImageView(restaurantImage_175by125);
        }
        imageView.setFitHeight(125);
        imageView.setFitWidth(175);
        imageView.setOnMouseClicked(event -> {
            restaurantClicked(restaurant);
        });
        imageView.setOnMouseEntered(event -> {
            ImageTransitions.imageMouseHoverEntered(event, 1.05, 0.15);
        });
        imageView.setOnMouseExited(event -> {
            ImageTransitions.imageMouseHoverExited(event, 1.05, 0.15);
        });


        Label restaurantNameLabel = new Label(restaurant.getName());
        restaurantNameLabel.setFont(robotoBoldFont15);
        restaurantNameLabel.setAlignment(Pos.CENTER_LEFT);
        restaurantNameLabel.setMaxWidth(175);

        Label ratingAndPriceLabel = new Label(restaurant.getScore() + "/5 , " + restaurant.getPrice());
        ratingAndPriceLabel.setFont(robotoRegularFont15);
        ratingAndPriceLabel.setAlignment(Pos.CENTER_LEFT);
        ratingAndPriceLabel.setMaxWidth(175);

        StringBuilder categoriesString = new StringBuilder();
        for (int i = 0; i < restaurant.getCategories().size(); i++)
        {
            categoriesString.append(restaurant.getCategories().get(i));
            if (i != restaurant.getCategories().size() - 1) categoriesString.append(", ");
        }

        Label categoryLabel = new Label(categoriesString.toString());
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

//        ImageView imageView = new ImageView("file:src/main/resources/food-images/" + food.getName() + ".jpg");
        ImageView imageView = new ImageView("file:src/main/resources/food-images/" + food.getName() + ".jpg");
        if (imageView.getImage().isError())
        {
            imageView = new ImageView(foodImage);
        }
        imageView.setFitWidth(175);
        imageView.setFitHeight(125);
        imageView.setOnMouseClicked(event -> foodClicked(food));
        imageView.setOnMouseEntered(event -> {
            ImageTransitions.imageMouseHoverEntered(event, 1.05, 0.15);
        });
        imageView.setOnMouseExited(event -> {
            ImageTransitions.imageMouseHoverExited(event, 1.05, 0.15);
        });

        Label foodNameLabel = new Label(food.getName());
//      restaurantNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; - fx-font-family: Calibri Light;");
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

    public void homePageLogOutButtonClicked()
    {
        System.out.println("Logout button clicked");
        application.logoutCleanup();
    }

    public void cartButtonClicked()
    {
        System.out.println("Cart button clicked");

        if (currentMenuWindow == Menu.HOME)
        {
            cartMenu.setVisible(true);
            currentMenuWindow = Menu.CART;
            cartMenuInit();
        }
    }

    public void deliveredIconClicked(MouseEvent event)
    {
        System.out.println("Delivered icon clicked");
        if (currentMenuWindow == Menu.HOME)
        {
            deliveredMenu.setVisible(true);
            currentMenuWindow = Menu.DELIVERED;
            deliveredMenuInit();
        }
        deliveredMenuInit();
    }

    public void search()
    {
        System.out.println("Searching (filter : " + currentSearchFilterType + ") : " + searchTextField.getText());

        if (currentSearchFilterType.equals(Options.RESTAURANT_NAME))
        {
            flowpaneTitleLabel.setText("Restaurants with name " + searchTextField.getText());
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByName(searchTextField.getText(), application.getRestaurantList());
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_CATEGORY))
        {
            flowpaneTitleLabel.setText("Restaurants with category " + searchTextField.getText());
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByCattegory(searchTextField.getText(), application.getRestaurantList());
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_PRICE))
        {
            String opt;
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

            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantByPrice(opt, application.getRestaurantList());
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_ZIPCODE))
        {
            flowpaneTitleLabel.setText("Restaurants with zipcode " + searchTextField.getText());
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByZipcode(searchTextField.getText(), application.getRestaurantList());
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.RESTAURANT_RATING))
        {
            ArrayList<Double> range = readRangeFromSearchBoxes();
            if (range.size() < 2) return;
            DecimalFormat decimalFormat = new DecimalFormat("#.##"); // to omit trailing .0 looks ugly
            flowpaneTitleLabel.setText("Restaurants with rating " + decimalFormat.format(range.get(0)) + " to " + decimalFormat.format(range.get(1)));
            ArrayList<Restaurant> restaurants = RestaurantSearches.searchRestaurantsByRating(range.get(0), range.get(1), application.getRestaurantList());
            resetFlowPane();
            addRestaurantListToFlowPane(restaurants);
        }
        else if (currentSearchFilterType.equals(Options.FOOD_NAME))
        {
            flowpaneTitleLabel.setText("Foods with name " + searchTextField.getText());
            ArrayList<Food> foods = RestaurantSearches.searchFoodByName(searchTextField.getText(), application.getFoodList());
            resetFlowPane();
            addFoodListToFlowPane(foods);
        }
        else if (currentSearchFilterType.equals(Options.FOOD_CATEGORY))
        {
            flowpaneTitleLabel.setText("Foods with category " + searchTextField.getText());
            ArrayList<Food> foods = RestaurantSearches.searchFoodByCategory(searchTextField.getText(), application.getFoodList());
            resetFlowPane();
            addFoodListToFlowPane(foods);
        }
        else if (currentSearchFilterType.equals(Options.FOOD_PRICE))
        {
            ArrayList<Double> range = readRangeFromSearchBoxes();
            if (range.size() < 2) return;
            DecimalFormat decimalFormat = new DecimalFormat("#.##"); // to omit trailing .0 looks ugly
            flowpaneTitleLabel.setText("Foods with price " + decimalFormat.format(range.get(0)) + "$ to " + decimalFormat.format(range.get(1)) + "$");
            ArrayList<Food> foods = RestaurantSearches.searchFoodByPriceRange(range.get(0), range.get(1), application.getFoodList());
            resetFlowPane();
            addFoodListToFlowPane(foods);
        }
        else
        {
            System.err.println("Class : HomePageController | Method : search | Invalid search filter type, How?? IMPOSSIBLE! CDI");
        }
    }

    // ======================================================================================================
    // UTIL METHODS AND CLASSES
    // ======================================================================================================

    public ArrayList<Double> readRangeFromSearchBoxes()
    {
        ArrayList<Double> range = new ArrayList<>();
        double min, max;

        try
        {
            rangeSearchMinField.setStyle("");
            min = Double.parseDouble(rangeSearchMinField.getText());
            range.add(min);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Class : HomePageController | Method : search | While parsing rating range, min is not a number");
            rangeSearchMinField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }

        try
        {
            rangeSearchMaxField.setStyle("");
            max = Double.parseDouble(rangeSearchMaxField.getText());
            range.add(max);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Class : HomePageController | Method : search | While parsing rating range, max is not a number");
            rangeSearchMaxField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }
        return range;
    }

    public void searchButtonClicked()
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

    ArrayList<Food> generateFoodList()
    {
        ArrayList<Food> foodList = new ArrayList<>();

        for (Restaurant restaurant : application.getRestaurantList().values())
        {
            foodList.addAll(restaurant.getFoodList());
        }

        return foodList;
    }

    public void resetAll()
    {
        flowpaneTitleLabel.setText("All Restaurants");
        resetFlowPane();
        addRestaurantListToFlowPane();
        viewChoiceBox.setValue(Options.VIEW_RESTAURANT);
        searchFilterChoiceBox.setValue(Options.RESTAURANT_NAME);
        searchTextField.setText("");
        rangeSearchMinField.setText("");
        rangeSearchMaxField.setText("");
        restaurantViewBackButton.setVisible(false);
        currentWindowType = Options.HOME_WINDOW;
        currentRestaurantWindowID = -1;
    }

    public void resetButtonClicked()
    {
        resetAll();
    }

    public void restaurantViewBackButtonClicked()
    {
        resetAll();
    }

    public void imageMouseHoverEntered(MouseEvent event)
    {
        ImageTransitions.imageMouseHoverEntered(event, 1.2, 0.3);
    }

    public void imageMouseHoverExited(MouseEvent event)
    {
        ImageTransitions.imageMouseHoverExited(event, 1.2, 0.3);
    }

    public void fillCartMenuList()
    {
        for (Integer restaurantID : cartFoodList.keySet())
        {
            addRestaurantNameHeaderCartMenu(application.getRestaurantList().get(restaurantID).getName());
            for (Food food : cartFoodList.get(restaurantID).keySet())
            {
                addFoodToCartMenuListView(food, cartFoodList.get(restaurantID).get(food));
            }
//            addRestaurantFooter();
        }
    }

    public void addRestaurantNameHeaderCartMenu(String restaurantName)
    {
        HBox row = new HBox();

        StackPane restaurantImageContainer = new StackPane();
        ImageView foodSmallImageView = new ImageView("file:src/main/resources/restaurant-images/" + restaurantName + ".jpg");
        foodSmallImageView.setFitWidth(60);
        foodSmallImageView.setFitHeight(40);
        restaurantImageContainer.setPadding(new Insets(5, 20, 5, 5)); // top right bottom left
        restaurantImageContainer.getChildren().add(foodSmallImageView);

        Label restaurantNameLabel = new Label(restaurantName);
        restaurantNameLabel.setFont(robotoBoldFont20);
        restaurantNameLabel.setPadding(new Insets(13, 0, 10, 0)); // top right bottom left

        row.getChildren().addAll(restaurantImageContainer, restaurantNameLabel);

        cartItemListView.getItems().add(row);
    }

    private void cartItemAddButtonClicked(Food food, Label foodCountLabel, Label foodPriceLabel)
    {
        System.out.println("Add button clicked");
        cartTotalItems++;
        cartTotalPrice += food.getPrice();
        for (HashMap<Food, Integer> foodCount : cartFoodList.values())
        {
            if (foodCount.containsKey(food))
            {
                foodCount.put(food, foodCount.get(food) + 1);
            }
        }
        foodCountLabel.setText("X " + cartFoodList.get(food.getRestaurantId()).get(food));
        String formattedFoodPrice = decimalFormat.format(food.getPrice() * cartFoodList.get(food.getRestaurantId()).get(food));
        foodPriceLabel.setText(formattedFoodPrice + " $");
    }

    private void cartItemRemoveButtonClicked(Food food, Label foodCountLabel, Label foodPriceLabel)
    {
        System.out.println("Remove button clicked");
        cartTotalItems--;
        cartTotalPrice -= food.getPrice();

        for (HashMap<Food, Integer> foodCount : cartFoodList.values())
        {
            if (foodCount.containsKey(food))
            {
                if (foodCount.get(food) == 1)
                {
                    cartFoodList.get(food.getRestaurantId()).remove(food);
                    cartItemListView.getItems().clear();
                    if (cartFoodList.get(food.getRestaurantId()).isEmpty())
                    {
                        cartFoodList.remove(food.getRestaurantId());
                    }
                    fillCartMenuList();
                    return;
                }
                else
                {
                    foodCount.put(food, foodCount.get(food) - 1);
                }
            }
        }

        foodCountLabel.setText("X " + cartFoodList.get(food.getRestaurantId()).get(food));
        String formattedFoodPrice = decimalFormat.format(food.getPrice() * cartFoodList.get(food.getRestaurantId()).get(food));
        foodPriceLabel.setText(formattedFoodPrice + " $");
    }

    public void addFoodToCartMenuListView(Food food, int foodCount)
    {
        // <HBox>
        HBox row = new HBox();
        row.setMinWidth(580);

        // <StackPane>
        StackPane foodImageContainer = new StackPane();
        ImageView foodSmallImageView = new ImageView("file:src/main/resources/food-images/" + food.getName() + ".jpg");
        if (foodSmallImageView.getImage().isError())
        {
            foodSmallImageView = new ImageView(foodImage);
        }
        foodSmallImageView.setFitWidth(84);
        foodSmallImageView.setFitHeight(60);
        foodImageContainer.setPadding(new Insets(10, 20, 10, 10)); // top right bottom left
        foodImageContainer.getChildren().add(foodSmallImageView);
        // </StackPane>

        // <VBox>
        VBox foodDetailsContainer = new VBox();

        Label foodNameLabel = new Label(food.getName());
        foodNameLabel.setWrapText(true);
        foodNameLabel.setFont(robotoBoldFont15);
        foodNameLabel.setMaxWidth(250);
        foodNameLabel.setPadding(new Insets(15, 0, 10, 0)); // top right bottom left

        Label foodCategoryLabel = new Label(food.getCategory());
        foodCategoryLabel.setWrapText(true);
        foodCategoryLabel.setFont(robotoRegularFont12);

        foodDetailsContainer.getChildren().addAll(foodNameLabel, foodCategoryLabel);
        // </VBox>

        // <HBox>
        HBox foodButtonsContainer = new HBox();

        int buttonSide = 45;

        VBox removeButtonContainer = new VBox();
        removeButtonContainer.setAlignment(Pos.CENTER);

        ImageView removeButton = new ImageView(removeButtonImage);
        removeButton.setFitWidth(buttonSide);
        removeButton.setFitHeight(buttonSide);

        removeButtonContainer.getChildren().add(removeButton);

        // <VBox>
        VBox foodCountAndPriceContainer = new VBox();

        Label foodCountLabel = new Label("X " + foodCount);
        foodCountLabel.setFont(robotoBoldFont15);
        foodCountLabel.setAlignment(Pos.CENTER);
        foodCountLabel.setMinWidth(70);

        Label foodPriceLabel = new Label(decimalFormat.format(food.getPrice() * foodCount) + " $");
        foodPriceLabel.setFont(robotoBoldFont15);
        foodPriceLabel.setAlignment(Pos.CENTER);
        foodPriceLabel.setMinWidth(70);

        foodCountAndPriceContainer.setAlignment(Pos.CENTER);
        foodCountAndPriceContainer.getChildren().addAll(foodCountLabel, foodPriceLabel);
        // </VBox>

        VBox addButtonContainer = new VBox();
//        Button addButton = new Button("+");
//        addButton.setFont(robotoBoldFont20);
//        addButton.setPrefWidth(buttonSide);
//        addButton.setPrefHeight(buttonSide);
//        addButton.setMinWidth(buttonSide);
//        addButton.setMinHeight(buttonSide);
//        addButton.setMaxHeight(buttonSide);
//        addButton.setMaxWidth(buttonSide);

        ImageView addButton = new ImageView(addButtonImage);
        removeButton.setFitWidth(buttonSide);
        removeButton.setFitHeight(buttonSide);


        addButton.setOnMouseClicked(event -> {
            cartItemAddButtonClicked(food, foodCountLabel, foodPriceLabel);
        });
        addButton.setOnMouseEntered(event -> {
            ImageTransitions.imageMouseHoverEntered(event, 1.2, 0.3);
        });
        addButton.setOnMouseExited(event -> {
            ImageTransitions.imageMouseHoverExited(event, 1.2, 0.3);
        });

        removeButton.setOnMouseClicked(event -> {
            cartItemRemoveButtonClicked(food, foodCountLabel, foodPriceLabel);
        });
        removeButton.setOnMouseEntered(event -> {
            ImageTransitions.imageMouseHoverEntered(event, 1.2, 0.3);
        });
        removeButton.setOnMouseExited(event -> {
            ImageTransitions.imageMouseHoverExited(event, 1.2, 0.3);
        });

        addButtonContainer.setAlignment(Pos.CENTER);
        addButtonContainer.getChildren().add(addButton);

        HBox.setHgrow(foodDetailsContainer, Priority.ALWAYS);
        foodButtonsContainer.setSpacing(18);
        foodButtonsContainer.setAlignment(Pos.CENTER_RIGHT);
        foodButtonsContainer.setPadding(new Insets(0, 20, 0, 0)); // top right bottom left
        foodButtonsContainer.getChildren().addAll(removeButtonContainer, foodCountAndPriceContainer, addButtonContainer);
        // </HBox>

        row.getChildren().addAll(foodImageContainer, foodDetailsContainer, foodButtonsContainer);
        // </HBox>

        cartItemListView.getItems().add(row);
    }

    public void orderAllButtonClicked(ActionEvent event)
    {
        System.out.println("Order all button clicked");

        try
        {
            ClientToServerCartOrderDTO clientToServerCartOrderDTO = new ClientToServerCartOrderDTO(cartFoodList, cartTotalItems, cartTotalPrice);
            application.getSocketWrapper().write(clientToServerCartOrderDTO);
            cartFoodList = new ConcurrentHashMap<>();
            cartItemListView.getItems().clear();
            fillCartMenuList();
            cartTotalItems = 0;
            cartTotalPrice = 0;
            updateNotification();

            System.out.println("Order sent to server");
        }
        catch (IOException e)
        {
            System.err.println("Class : ClientCartController | Method : orderAllButtonClicked | While ordering all items");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void cartMenuInit()
    {
        cartItemListView.getItems().clear();
        fillCartMenuList();
    }

    public void fillDeliveredMenuList()
    {
        for (Integer restaurantID : deliveredFoodList.keySet())
        {
            addRestaurantNameHeaderDeliveredMenu(application.getRestaurantList().get(restaurantID).getName());
            for (Food food : deliveredFoodList.get(restaurantID).keySet())
            {
                addFoodToDeliveredMenuListView(food, deliveredFoodList.get(restaurantID).get(food));
            }
        }
    }

    public void addRestaurantNameHeaderDeliveredMenu(String restaurantName)
    {
        HBox row = new HBox();

        StackPane restaurantImageContainer = new StackPane();
        ImageView foodSmallImageView = new ImageView("file:src/main/resources/restaurant-images/" + restaurantName + ".jpg");
        foodSmallImageView.setFitWidth(60);
        foodSmallImageView.setFitHeight(40);
        restaurantImageContainer.setPadding(new Insets(5, 20, 5, 5)); // top right bottom left
        restaurantImageContainer.getChildren().add(foodSmallImageView);

        Label restaurantNameLabel = new Label(restaurantName);
        restaurantNameLabel.setFont(robotoBoldFont20);
        restaurantNameLabel.setPadding(new Insets(13, 0, 10, 0)); // top right bottom left

        row.getChildren().addAll(restaurantImageContainer, restaurantNameLabel);

        deliveredMenuListView.getItems().add(row);
    }

    public void addFoodToDeliveredMenuListView(Food food, int foodCount)
    {
        // <HBox>
        HBox row = new HBox();
        row.setMinWidth(580);

        // <StackPane>
        StackPane foodImageContainer = new StackPane();
        ImageView foodSmallImageView = new ImageView("file:src/main/resources/food-images/" + food.getName() + ".jpg");
        if (foodSmallImageView.getImage().isError())
        {
            foodSmallImageView = new ImageView(foodImage);
        }
        foodSmallImageView.setFitWidth(84);
        foodSmallImageView.setFitHeight(60);
        foodImageContainer.setPadding(new Insets(10, 20, 10, 10)); // top right bottom left
        foodImageContainer.getChildren().add(foodSmallImageView);
        // </StackPane>

        // <VBox>
        VBox foodDetailsContainer = new VBox();

        Label foodNameLabel = new Label(food.getName());
        foodNameLabel.setWrapText(true);
        foodNameLabel.setFont(robotoBoldFont15);
        foodNameLabel.setMaxWidth(250);
        foodNameLabel.setPadding(new Insets(15, 0, 10, 0)); // top right bottom left

        Label foodCategoryLabel = new Label(food.getCategory());
        foodCategoryLabel.setWrapText(true);
        foodCategoryLabel.setFont(robotoRegularFont12);

        foodDetailsContainer.getChildren().addAll(foodNameLabel, foodCategoryLabel);
        // </VBox>

        // <HBox>
        HBox foodButtonsContainer = new HBox();

        int buttonSide = 45;

        VBox removeButtonContainer = new VBox();
        removeButtonContainer.setAlignment(Pos.CENTER);

        removeButtonContainer.setPrefWidth(buttonSide);
        removeButtonContainer.setPrefHeight(buttonSide);

        // <VBox>
        VBox foodCountAndPriceContainer = new VBox();

        Label foodCountLabel = new Label("X " + foodCount);
        foodCountLabel.setFont(robotoBoldFont15);
        foodCountLabel.setAlignment(Pos.CENTER);
        foodCountLabel.setMinWidth(70);

        Label foodPriceLabel = new Label(decimalFormat.format(food.getPrice() * foodCount) + " $");
        foodPriceLabel.setFont(robotoBoldFont15);
        foodPriceLabel.setAlignment(Pos.CENTER);
        foodPriceLabel.setMinWidth(70);

        foodCountAndPriceContainer.setAlignment(Pos.CENTER);
        foodCountAndPriceContainer.getChildren().addAll(foodCountLabel, foodPriceLabel);
        // </VBox>

        VBox addButtonContainer = new VBox();
        addButtonContainer.setPrefWidth(buttonSide);
        addButtonContainer.setPrefHeight(buttonSide);
        addButtonContainer.setAlignment(Pos.CENTER);

        HBox.setHgrow(foodDetailsContainer, Priority.ALWAYS);
        foodButtonsContainer.setSpacing(18);
        foodButtonsContainer.setAlignment(Pos.CENTER_RIGHT);
        foodButtonsContainer.setPadding(new Insets(0, 20, 0, 0)); // top right bottom left
        foodButtonsContainer.getChildren().addAll(removeButtonContainer, foodCountAndPriceContainer, addButtonContainer);
        // </HBox>

        row.getChildren().addAll(foodImageContainer, foodDetailsContainer, foodButtonsContainer);
        // </HBox>

        deliveredMenuListView.getItems().add(row);
    }

    public void deliveredMenuInit()
    {
        deliveredMenuListView.getItems().clear();
        fillDeliveredMenuList();
    }

    // BOTH MENU COMMON FUNCTIONALITY
    public void menuBackButtonClicked(MouseEvent event)
    {
        if (currentMenuWindow == Menu.CART)
        {
            System.out.println("Back button clicked on cart menu");
            cartMenu.setVisible(false);
            deliveredMenu.setVisible(false);
        }
        else if (currentMenuWindow == Menu.DELIVERED)
        {
            System.out.println("Back button clicked on delivered menu");
            cartMenu.setVisible(false);
            deliveredMenu.setVisible(false);
        }
        currentMenuWindow = Menu.HOME;
    }

    private static class Menu
    {
        public static final int HOME = 1;
        public static final int CART = 2;
        public static final int DELIVERED = 3;
    }

    // SEARCH OPTIONS
    private static final class Options
    {
        public static String RESTAURANT_NAME = "Name";
        public static String RESTAURANT_RATING = "Rating Range";
        public static String RESTAURANT_PRICE = "Price Category";
        public static String RESTAURANT_CATEGORY = "Category";
        public static String RESTAURANT_ZIPCODE = "Zipcode";

        // SPACE AFTER FOOD TO DIFFERENTIATE THESE OPTIONS FROM RESTAURANT OPTIONS IN CHOICE BOX
        // THERE MIGHT BE A BETTER WAY TO DO THIS, BUT FEELING LAZY :P
        // Should use invisible space 'ㅤ'
        // TODO : FIND A BETTER WAY TO DO THIS
        public static String FOOD_NAME = "Name ";
        public static String FOOD_CATEGORY = "Category ";
        public static String FOOD_PRICE = "Price Range ";

        public static String VIEW_RESTAURANT = "Restaurant";
        public static String VIEW_FOOD = "Food";

        public static String RESTAURANT_WINDOW = "RestaurantWindow";
        public static String HOME_WINDOW = "HomeWindow";
    }
}