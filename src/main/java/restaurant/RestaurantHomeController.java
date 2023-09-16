package restaurant;

import dto.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import models.Food;
import models.Restaurant;
import util.ImageTransitions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantHomeController
{
    public Label restaurantNameLabel;

    public Button pendingOrdersButton;
    public Button foodListButton;
    public Button historyButton;
    public Button addFoodButton;

    public Label pendingOrderCountLabel;
    public Rectangle pendingOrderCountBg;

    public VBox displayVBox;
    public TextField foodNameTextField;
    public TextField foodPriceTextField;
    public TextField foodCategoryField;
    public Rectangle pendingOrderSelectionBox;
    public Rectangle deliveredOrderSelectionBox;
    public Rectangle addFoodSelectionBox;
    public Rectangle infoSelectionBox;
    public VBox foodListVBox;

    // RESTAURANT DETAILS
    public ImageView restaurantImageView;
    public Label categoriesLabel;
    public Label zipcodeLabel;
    public Label priceLabel;
    public Label ratingLabel;
    public Label nameLabel;

    // WINDOWS
    public AnchorPane listView;
    public AnchorPane infoListView;
    public AnchorPane addNewFoodMenu;
    public Label noOrderMessage;

    public void mouseHoverEntered(MouseEvent event)
    {
        ImageTransitions.imageMouseHoverEntered(event);
    }

    public void mouseHoverExited(MouseEvent event)
    {
        ImageTransitions.imageMouseHoverExited(event);
    }

    // INTERNAL WINDOW SYSTEM
    public static class WindowType
    {
        public static final int UNDEFINED = -1;
        public static final int ORDERS = 0;
        public static final int INFO = 1;
        public static final int DELIVERED = 2;
        public static final int ADD_FOODS = 3;
    }
    int currentWindow = WindowType.UNDEFINED;
    // IMAGES
    Image restaurantImageMedium;

    // ASSETS //
    Image restaurantImageLarge;
    Image foodImage;
    Image userIconImage;
    Image deliverOrderButtonImage;
    Image orderAllImage;

    ConcurrentHashMap<String, HashMap<Food, Integer>> pendingOrdersList;
    ConcurrentHashMap<String, HashMap<Food, Integer>> historyOrdersList;

    int pendingOrderCount = 0;
    // DATABASE
    private Restaurant restaurant;
    private String restaurantName;
    private RestaurantApplication application;

    public void init()
    {
        pendingOrdersList = new ConcurrentHashMap<>();
        historyOrdersList = new ConcurrentHashMap<>();

        try
        {
            DatabaseRequestDTO databaseRequestDTO = new DatabaseRequestDTO(DatabaseRequestDTO.RequestType.SINGLE_RESTAURANT);
            application.getSocketWrapper().write(databaseRequestDTO);
            System.out.println("Restaurant database request sent");
        }
        catch (IOException e)
        {
            System.out.println("Class : RestaurantHomeController | Method : init | While writing to server");
            System.out.println("Error : " + e.getMessage());
        }

        try
        {
            DatabaseDTO databaseDTO = (DatabaseDTO) application.getSocketWrapper().read();
            this.restaurant = databaseDTO.getSingleRestaurant();

            application.setRestaurant(restaurant);
            this.restaurantName = restaurant.getName();

            System.out.println("Restaurant database received");
            restaurant.print();
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("Class : RestaurantHomeController | Method : init | While reading from server");
            System.out.println("Error : " + e.getMessage());
        }

        try
        {
            application.getSocketWrapper().write(new RequestOfflinePendingOrDeliveryDataDTO());
            Object obj;
            while((obj = application.getSocketWrapper().read()) != null)
            {
                if(obj instanceof ServerToRestaurantCartOrderDTO serverToRestaurantCartOrderDTO)
                {
                    String username = serverToRestaurantCartOrderDTO.getUsername();
                    HashMap<Food, Integer> foodCountMap = serverToRestaurantCartOrderDTO.getCartFoodList();

                     for (Food food : foodCountMap.keySet())
                    {
                        System.out.println(food.getName() + " : " + foodCountMap.get(food));
                    }
                    System.out.println();

                    updatePendingOrdersList(username, foodCountMap);
                }
                else if(obj instanceof StopDTO)
                {
                    break;
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("Class : RestaurantHomeController | Method : init | While requesting offline pending orders from server");
            System.out.println("Error : " + e.getMessage());
        }

        restaurantNameLabel.setText(restaurantName);

        // LOAD IMAGES
        restaurantImageMedium = new Image("file:src/main/resources/assets/RestaurantImage.jpg", 175, 125, false, false);
        restaurantImageLarge = new Image("file:src/main/resources/assets/RestaurantImage.jpg", 263, 188, false, false);
        foodImage = new Image("file:src/main/resources/assets/Burger.jpg", 175, 125, false, false);
        userIconImage = new Image("file:src/main/resources/assets/user-icon.png");
        deliverOrderButtonImage = new Image("file:src/main/resources/assets/deliver-order-icon.png");
        orderAllImage = new Image("file:src/main/resources/assets/order-all-icon.png");

        // RESTAURANT INFO
        restaurantImageView.setImage(new Image("file:src/main/resources/restaurant-images/" + restaurant.getName() + ".jpg"));
        if(restaurantImageView.getImage().isError())
        {
            restaurantImageView.setImage(restaurantImageLarge);
        }
        nameLabel.setText(restaurant.getName());
        zipcodeLabel.setText(restaurant.getZipcode());
        priceLabel.setText(restaurant.getPrice());
        ratingLabel.setText(Double.toString(restaurant.getScore()));
        StringBuilder cats = new StringBuilder();
        for(int i = 0; i < restaurant.getCategories().size(); i++)
        {
            cats.append(restaurant.getCategories().get(i));
            if(i != restaurant.getCategories().size() - 1) cats.append(", ");
        }
        categoriesLabel.setText(cats.toString());

        listView.setVisible(false);
        infoListView.setVisible(false);
        addNewFoodMenu.setVisible(false);
        switchInternalWindow(WindowType.ORDERS);

        updatePendingOrderNotification();

        addNewFoodMenu.setVisible(false);

        pendingOrderSelectionBox.setVisible(true);
        deliveredOrderSelectionBox.setVisible(false);
        addFoodSelectionBox.setVisible(false);
        infoSelectionBox.setVisible(false);

        new RestaurantReadThread(application, this);
    }

    public void switchInternalWindow(int window)
    {
        System.out.println("Switching to window " + window);
        if (currentWindow == window) return;

        // Unselect previous window button -> REMOVE CLICK EFFECT
        if (currentWindow != WindowType.UNDEFINED)
        {
            if (currentWindow == WindowType.ORDERS)
            {
                listView.setVisible(false);
                pendingOrderSelectionBox.setVisible(false);
            }
            else if (currentWindow == WindowType.INFO)
            {
                infoListView.setVisible(false);
                infoSelectionBox.setVisible(false);
            }
            else if (currentWindow == WindowType.DELIVERED)
            {
                listView.setVisible(false);
                deliveredOrderSelectionBox.setVisible(false);
            }
            else if (currentWindow == WindowType.ADD_FOODS)
            {
                // Clear add food menu
                addNewFoodMenu.setVisible(false);
                addFoodSelectionBox.setVisible(false);
            }
        }

        // Select new window button -> CLICK IT
        // And change window
        if (window == WindowType.ORDERS)
        {
            listView.setVisible(true);
            pendingOrderSelectionBox.setVisible(true);
            displayVBox.getChildren().clear();
            displayVBox.setSpacing(10);
            fillPendingRequest();
        }
        else if (window == WindowType.INFO)
        {
            infoListView.setVisible(true);
            infoSelectionBox.setVisible(true);
            displayVBox.setSpacing(15);
            displayVBox.getChildren().clear();

            fillFoodList(application.getRestaurant().getFoodList());
        }
        else if (window == WindowType.DELIVERED)
        {
            listView.setVisible(true);
            deliveredOrderSelectionBox.setVisible(true);
            displayVBox.setSpacing(10);
            displayVBox.getChildren().clear();
            fillDeliveredRequest();
        }
        else if (window == WindowType.ADD_FOODS)
        {
            addNewFoodMenu.setVisible(true);
            addFoodSelectionBox.setVisible(true);
            displayVBox.getChildren().clear();
            resetAddMenuFieldsStyle();
        }

        currentWindow = window;
        updateNoOrderText();
    }

    public void addFoodButtonClicked(MouseEvent event)
    {
        switchInternalWindow(WindowType.ADD_FOODS);
    }

    public void historyButtonClicked(MouseEvent event)
    {
        switchInternalWindow(WindowType.DELIVERED);
    }

    public void foodListButtonClicked(ActionEvent event)
    {
        switchInternalWindow(WindowType.INFO);
    }

    public void pendingOrdersButtonClicked(MouseEvent event)
    {
        switchInternalWindow(WindowType.ORDERS);
    }

    public void infoButtonPressed(MouseEvent event)
    {
        switchInternalWindow(WindowType.INFO);
    }

    public void resetAddMenuFieldsStyle()
    {
        foodNameTextField.setStyle("");
        foodCategoryField.setStyle("");
        foodPriceTextField.setStyle("");
    }

    public void addMenuAddFoodButtonPressed(ActionEvent event)
    {
        resetAddMenuFieldsStyle();

        String foodName = foodNameTextField.getText();
        String foodCategory = foodCategoryField.getText();
        String foodPrice = foodPriceTextField.getText();

        if (foodName.isEmpty() || foodCategory.isEmpty() || foodPrice.isEmpty())
        {
            if (foodName.isEmpty())
            {
                foodNameTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }
            if (foodCategory.isEmpty())
            {
                foodCategoryField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }
            if (foodPrice.isEmpty())
            {
                foodPriceTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }
            return;
        }
        try
        {
            double price = Double.parseDouble(foodPrice);
        }
        catch (Exception e)
        {
            foodPriceTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            return;
        }

        Food food = new Food(restaurant.getId(), foodCategory, foodName, Double.parseDouble(foodPrice));
        for (Food existingFood : application.getRestaurant().getFoodList())
        {
            if (existingFood.getName().equals(food.getName()) && existingFood.getPrice() == food.getPrice())
            {
                System.out.println("Food already exists");
                foodNameTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            }
        }
        application.getRestaurant().addFood(food);

        System.out.println("Food added to restaurant");
        food.print(restaurantName);

        try
        {
            application.getSocketWrapper().write(new FoodAddRequestDTO(food));
        }
        catch (IOException e)
        {
            System.out.println("Class : RestaurantHomeController | Method : addMenuAddFoodButtonPressed | While writing new food to server");
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updatePendingOrdersList(String username, HashMap<Food, Integer> foodCountMap)
    {
        if (pendingOrdersList.containsKey(username))
        {
            HashMap<Food, Integer> existingFoodCountMap = pendingOrdersList.get(username);
            for (Food food : foodCountMap.keySet())
            {
                if (existingFoodCountMap.containsKey(food))
                {
                    existingFoodCountMap.put(food, existingFoodCountMap.get(food) + foodCountMap.get(food));
                }
                else
                {
                    existingFoodCountMap.put(food, foodCountMap.get(food));
                }
            }
            pendingOrdersList.put(username, existingFoodCountMap);
        }
        else
        {
            pendingOrdersList.put(username, foodCountMap);
        }

        pendingOrderCount = 0;
        for (HashMap<Food, Integer> foodCount : pendingOrdersList.values())
        {
            for (Food food : foodCount.keySet())
            {
                pendingOrderCount += foodCount.get(food);
            }
        }
        updatePendingOrderNotification();

        if (currentWindow == WindowType.ORDERS)
        {
            displayVBox.getChildren().clear();
            fillPendingRequest();
        }
    }

    public void updateNoOrderText()
    {
        if(currentWindow == WindowType.ORDERS)
        {
            if(pendingOrdersList.size() == 0)
            {
                noOrderMessage.setText("No pending orders");
            }
            else
            {
                noOrderMessage.setText("");
            }
        }
        else if(currentWindow == WindowType.DELIVERED)
        {
            if(historyOrdersList.size() == 0)
            {
                noOrderMessage.setText("No orders delivered");
            }
            else
            {
                noOrderMessage.setText("");
            }
        }
        else
        {
            noOrderMessage.setText("");
        }
    }

    public void updatePendingOrderNotification()
    {
        if (pendingOrderCount == 0)
        {
            pendingOrderCountLabel.setVisible(false);
            pendingOrderCountBg.setVisible(false);
        }
        else
        {
            pendingOrderCountLabel.setText(Integer.toString(pendingOrderCount));
            pendingOrderCountLabel.setVisible(true);
            pendingOrderCountBg.setVisible(true);
        }
        updateNoOrderText();
    }

    public void logoutButtonClicked(MouseEvent event)
    {
        System.out.println("Logout button clicked");
        application.logoutCleanup();
    }

    public void fillDeliveredRequest()
    {
        for (String username : historyOrdersList.keySet())
        {
            addDeliveredOrderUsernameHeader(username);
            HashMap<Food, Integer> foodCountMap = historyOrdersList.get(username);
            for (Food food : foodCountMap.keySet())
            {
                addDeliveredOrderRow(food, foodCountMap.get(food), username);
            }
        }
    }

    public void addDeliveredOrderUsernameHeader(String username)
    {
        HBox headerRow = new HBox();

        HBox userDetailContainer = new HBox();
        userDetailContainer.setPrefWidth(460);
        userDetailContainer.setPrefHeight(50);
        userDetailContainer.setSpacing(10);
        userDetailContainer.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView(userIconImage);
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);

        headerRow.setPrefWidth(460);
        headerRow.setPrefHeight(50);
        headerRow.setMinWidth(460);
        headerRow.setMinHeight(50);
        headerRow.setStyle("-fx-border-insets: 0px 0px 10px 20px;");

        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Roboto Medium';");
        usernameLabel.setPadding(new Insets(0, 0, 0, 20));

        userDetailContainer.getChildren().addAll(imageView, usernameLabel);

        headerRow.getChildren().addAll(userDetailContainer);
        displayVBox.getChildren().add(headerRow);
    }

    public void addDeliveredOrderRow(Food food, int orderCount, String username)
    {
        HBox row = new HBox();
        row.setPrefWidth(460);
        row.setPrefHeight(70);
        row.setMaxWidth(460);
        row.setMaxHeight(70);

        StackPane rowStackPane = new StackPane();
        rowStackPane.setPrefWidth(460);
        rowStackPane.setPrefHeight(70);

        Rectangle rowBackgroundRect = new Rectangle();
        rowBackgroundRect.setWidth(460);
        rowBackgroundRect.setHeight(70);
        rowBackgroundRect.setArcWidth(10);
        rowBackgroundRect.setArcHeight(10);
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#4027ff", 0.4)), new Stop(1, Color.web("#ada3f7", 0.4)));
        rowBackgroundRect.setFill(linearGradient);
        rowBackgroundRect.setStroke(Color.web("#000000"));

        HBox rowHBoxContent = new HBox();
        rowHBoxContent.setPrefWidth(460);
        rowHBoxContent.setPrefHeight(70);

        VBox foodImageContainer = new VBox();
        ImageView rowImageView = new ImageView("file:src/main/resources/food-images/" + food.getName() + ".jpg");
        if (rowImageView.getImage().isError())
        {
            rowImageView = new ImageView("file:src/main/resources/assets/Burger.jpg");
        }
        rowImageView.setFitWidth(70);
        rowImageView.setFitHeight(50);
        rowImageView.minWidth(70);
        rowImageView.minHeight(50);
        rowImageView.maxWidth(70);
        rowImageView.maxHeight(50);
        rowImageView.setPreserveRatio(false);
        foodImageContainer.setPadding(new Insets(10, 0, 0, 10));
        foodImageContainer.getChildren().addAll(rowImageView);

        VBox foodDetailsContainer = new VBox();
        foodDetailsContainer.setPadding(new Insets(10, 0, 0, 10));
        foodDetailsContainer.setPrefWidth(255);
        foodDetailsContainer.setPrefHeight(70);
        foodDetailsContainer.setSpacing(10);

        Label foodNameLabel = new Label(food.getName());
        foodNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: Calibri;");

        Label foodCategory = new Label(food.getCategory());
        foodCategory.setStyle("-fx-font-size: 13px; -fx-font-family: Roboto;");

        foodDetailsContainer.getChildren().addAll(foodNameLabel, foodCategory);

        VBox separatorContainer = new VBox();
        Rectangle separator = new Rectangle();
        separator.setWidth(2);
        separator.setHeight(60);
        separatorContainer.setPadding(new Insets(5, 0, 0, 0));
        separatorContainer.getChildren().add(separator);

        VBox orderDetailsContainer = new VBox();
        orderDetailsContainer.setPrefWidth(120);
        orderDetailsContainer.setPrefHeight(70);
        orderDetailsContainer.setSpacing(10);
        orderDetailsContainer.setAlignment(Pos.CENTER);

        Label orderCountLabel = new Label("X " + orderCount);
        orderCountLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: Calibri;");

        Label orderPriceLabel = new Label(food.getPrice() * orderCount + "$");
        orderPriceLabel.setStyle("-fx-font-size: 18px; -fx-font-family: 'Roboto Black';");

        orderDetailsContainer.getChildren().addAll(orderCountLabel, orderPriceLabel);

        rowHBoxContent.getChildren().addAll(foodImageContainer, foodDetailsContainer, separatorContainer, orderDetailsContainer);

        rowStackPane.getChildren().addAll(rowBackgroundRect, rowHBoxContent);


        row.getChildren().add(rowStackPane);
        displayVBox.getChildren().add(row);
    }

    public void fillPendingRequest()
    {
        for (String username : pendingOrdersList.keySet())
        {
            addPendingOrderUsernameHeader(username);
            HashMap<Food, Integer> foodCountMap = pendingOrdersList.get(username);
            for (Food food : foodCountMap.keySet())
            {
                addPendingOrderRow(food, foodCountMap.get(food), username);
            }
        }
    }

    public void addPendingOrderUsernameHeader(String username)
    {
        HBox headerRow = new HBox();
        headerRow.setPrefWidth(460);
        headerRow.setPrefHeight(50);
        headerRow.setStyle("-fx-border-insets: 0px 0px 10px 20px;");

        HBox userDetailContainer = new HBox();
        userDetailContainer.setPrefWidth(380);
        userDetailContainer.setPrefHeight(50);
        userDetailContainer.setSpacing(10);
        userDetailContainer.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView(userIconImage);
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);


        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Roboto Medium';");
        usernameLabel.setPadding(new Insets(0, 0, 0, 20));

        userDetailContainer.getChildren().addAll(imageView, usernameLabel);

        HBox acceptOrderButtonContainer = new HBox();
        acceptOrderButtonContainer.setPrefWidth(80);
        acceptOrderButtonContainer.setPrefHeight(50);
        acceptOrderButtonContainer.setAlignment(Pos.CENTER_RIGHT);

        ImageView acceptOrderButton = new ImageView(orderAllImage);
        acceptOrderButton.setFitWidth(50);
        acceptOrderButton.setFitHeight(50);
        acceptOrderButton.preserveRatioProperty().setValue(false);
        acceptOrderButton.setOnMouseClicked((MouseEvent event) -> {
            deliverUserAllFood(username);
        });
        acceptOrderButton.setOnMouseEntered((MouseEvent event) -> {
            ImageTransitions.imageMouseHoverEntered(event);
        });
        acceptOrderButton.setOnMouseExited((MouseEvent event) -> {
            ImageTransitions.imageMouseHoverExited(event);
        });

        acceptOrderButtonContainer.getChildren().add(acceptOrderButton);

        headerRow.getChildren().addAll(userDetailContainer, acceptOrderButtonContainer);
        displayVBox.getChildren().add(headerRow);
    }

    public void addPendingOrderRow(Food food, int orderCount, String username)
    {
        HBox row = new HBox();
        row.setPrefWidth(460);
        row.setPrefHeight(70);
        row.setMaxWidth(460);
        row.setMaxHeight(70);

        StackPane rowStackPane = new StackPane();
        rowStackPane.setPrefWidth(460);
        rowStackPane.setPrefHeight(70);

        Rectangle rowBackgroundRect = new Rectangle();
        rowBackgroundRect.setWidth(460);
        rowBackgroundRect.setHeight(70);
        rowBackgroundRect.setArcWidth(10);
        rowBackgroundRect.setArcHeight(10);
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#4027ff", 0.4)), new Stop(1, Color.web("#ada3f7", 0.4)));
        rowBackgroundRect.setFill(linearGradient);
        rowBackgroundRect.setStroke(Color.web("#000000"));

        HBox rowHBoxContent = new HBox();
        rowHBoxContent.setPrefWidth(460);
        rowHBoxContent.setPrefHeight(70);

        VBox foodImageContainer = new VBox();
        ImageView rowImageView = new ImageView("file:src/main/resources/food-images/" + food.getName() + ".jpg");
        if (rowImageView.getImage().isError())
        {
            rowImageView = new ImageView("file:src/main/resources/assets/Burger.jpg");
        }
        rowImageView.setFitWidth(70);
        rowImageView.setFitHeight(50);
        rowImageView.minWidth(70);
        rowImageView.minHeight(50);
        rowImageView.maxWidth(70);
        rowImageView.maxHeight(50);
        rowImageView.setPreserveRatio(false);
        foodImageContainer.setPadding(new Insets(10, 0, 0, 10));
        foodImageContainer.getChildren().addAll(rowImageView);

        VBox foodDetailsContainer = new VBox();
        foodDetailsContainer.setPadding(new Insets(10, 0, 0, 10));
        foodDetailsContainer.setPrefWidth(190);
        foodDetailsContainer.setPrefHeight(70);
        foodDetailsContainer.setSpacing(10);

        Label foodNameLabel = new Label(food.getName());
        foodNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: Calibri;");

        Label foodCategory = new Label(food.getCategory());
        foodCategory.setStyle("-fx-font-size: 13px; -fx-font-family: Roboto;");

        foodDetailsContainer.getChildren().addAll(foodNameLabel, foodCategory);

        VBox separatorContainer = new VBox();
        Rectangle separator = new Rectangle();
        separator.setWidth(2);
        separator.setHeight(60);
        separatorContainer.setPadding(new Insets(5, 0, 0, 0));
        separatorContainer.getChildren().add(separator);

        VBox orderDetailsContainer = new VBox();
        orderDetailsContainer.setPrefWidth(100);
        orderDetailsContainer.setPrefHeight(70);
        orderDetailsContainer.setSpacing(10);
        orderDetailsContainer.setAlignment(Pos.CENTER);

        Label orderCountLabel = new Label("X " + orderCount);
        orderCountLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: Calibri;");

        Label orderPriceLabel = new Label(food.getPrice() * orderCount + "$");
        orderPriceLabel.setStyle("-fx-font-size: 18px; -fx-font-family: 'Roboto Black';");

        orderDetailsContainer.getChildren().addAll(orderCountLabel, orderPriceLabel);

        VBox orderButtonContainer = new VBox();
        orderButtonContainer.setPrefWidth(85);
        orderButtonContainer.setPrefHeight(70);
        orderButtonContainer.setAlignment(Pos.CENTER);

        ImageView acceptOrderButton = new ImageView(deliverOrderButtonImage);
        acceptOrderButton.setFitWidth(50);
        acceptOrderButton.setFitHeight(50);
        acceptOrderButton.preserveRatioProperty().setValue(false);
        acceptOrderButton.setOnMouseClicked((MouseEvent event) -> {
            deliverSingleFood(food, orderCount, username);
        });
        acceptOrderButton.setOnMouseEntered((MouseEvent event) -> {
            ImageTransitions.imageMouseHoverEntered(event);
        });
        acceptOrderButton.setOnMouseExited((MouseEvent event) -> {
            ImageTransitions.imageMouseHoverExited(event);
        });

        orderButtonContainer.getChildren().add(acceptOrderButton);

        rowHBoxContent.getChildren().addAll(foodImageContainer, foodDetailsContainer, separatorContainer, orderDetailsContainer, orderButtonContainer);

        rowStackPane.getChildren().addAll(rowBackgroundRect, rowHBoxContent);


        row.getChildren().add(rowStackPane);
        displayVBox.getChildren().add(row);
    }

    public void deliverUserAllFood(String username)
    {
        try
        {
            application.getSocketWrapper().write(new DeliverDTO(restaurant.getId(), pendingOrdersList));
        }
        catch (IOException e)
        {
            System.out.println("Class : RestaurantHomeController | Method : deliverUserAllFood | While writing deliver request to server");
            System.out.println("Error : " + e.getMessage());
        }

        System.out.println("Delivering all food for user " + username);

        if (historyOrdersList.containsKey(username))
        {
            HashMap<Food, Integer> existingFoodCountMap = historyOrdersList.get(username);
            for (Food food : pendingOrdersList.get(username).keySet())
            {
                if (existingFoodCountMap.containsKey(food))
                {
                    existingFoodCountMap.put(food, existingFoodCountMap.get(food) + pendingOrdersList.get(username).get(food));
                }
                else
                {
                    existingFoodCountMap.put(food, pendingOrdersList.get(username).get(food));
                }
            }
            historyOrdersList.put(username, existingFoodCountMap);
        }
        else
        {
            historyOrdersList.put(username, pendingOrdersList.get(username));
        }

        for (var entry : historyOrdersList.entrySet())
        {
            for (Food f : entry.getValue().keySet())
            {
                System.out.println("Food : " + f.getName() + " x " + entry.getValue().get(f));
            }
        }

        for (Food food : pendingOrdersList.get(username).keySet())
        {
            pendingOrderCount -= pendingOrdersList.get(username).get(food);
        }
        pendingOrdersList.remove(username);
        updatePendingOrderNotification();
        displayVBox.getChildren().clear();
        fillPendingRequest();
    }

    public void deliverSingleFood(Food food, int orderCount, String username)
    {
        try
        {
            ConcurrentHashMap<String, HashMap<Food, Integer>> deliveryList = new ConcurrentHashMap<>();
            HashMap<Food, Integer> foodCount = new HashMap<>();
            foodCount.put(food, orderCount);
            deliveryList.put(username, foodCount);
            application.getSocketWrapper().write(new DeliverDTO(restaurant.getId(), deliveryList));
        }
        catch (IOException e)
        {
            System.out.println("Class : RestaurantHomeController | Method : deliverUserAllFood | While writing deliver request to server");
            System.out.println("Error : " + e.getMessage());
        }

        System.out.println("Delivering food " + food.getName() + " x " + orderCount);

        pendingOrdersList.get(username).remove(food);
        if (pendingOrdersList.get(username).size() == 0)
        {
            pendingOrdersList.remove(username);
        }

        if (historyOrdersList.containsKey(username))
        {
            HashMap<Food, Integer> existingFoodCountMap = historyOrdersList.get(username);
            if (existingFoodCountMap.containsKey(food))
            {
                existingFoodCountMap.put(food, existingFoodCountMap.get(food) + orderCount);
            }
            else
            {
                existingFoodCountMap.put(food, orderCount);
            }
            historyOrdersList.put(username, existingFoodCountMap);
        }
        else
        {
            HashMap<Food, Integer> foodCountMap = new HashMap<>();
            foodCountMap.put(food, orderCount);
            historyOrdersList.put(username, foodCountMap);
        }

        for (var entry : historyOrdersList.entrySet())
        {
            for (Food f : entry.getValue().keySet())
            {
                System.out.println("Food : " + f.getName() + " x " + entry.getValue().get(f));
            }
        }

        pendingOrderCount -= orderCount;
        updatePendingOrderNotification();

        displayVBox.getChildren().clear();
        fillPendingRequest();
    }

    // ERROR FIXED : WARNING :
    // REMEMBER WHEN WORKING WITH HASH MAP ALWAYS OVERRIDE THE CLASSES EQUALS AND HASHCODE METHOD
    public void fillFoodList(ArrayList<Food> foodList)
    {
        int itemOrderCount = 0;

        System.out.println("Food list size : " + foodList.size());

        for (Food food : foodList)
        {
            itemOrderCount = 0;
            for(HashMap<Food, Integer> foodCount : historyOrdersList.values())
            {
                // IF HASH CODE IS NOT OVERRIDDEN THEN THIS WILL NOT WORK
                // AS THE OBJECTS WILL BE COMPARED BY THEIR MEMORY ADDRESS
                // SO EVEN IF THE OBJECTS ARE SAME BUT THEIR MEMORY ADDRESS IS DIFFERENT THEY WILL BE CONSIDERED DIFFERENT
                if (foodCount.containsKey(food))
                {
                    itemOrderCount += foodCount.get(food);
                }
            }
            addFoodListRow(food, itemOrderCount);
        }
    }

    public void addFoodListRow(Food food, int totalOrderCount)
    {
        HBox row = new HBox();
        row.setPrefWidth(780);
        row.setPrefHeight(200);
        row.setMinWidth(780);
        row.setMinHeight(200);
        row.setStyle("-fx-border-insets: 0 20px 20px 0px;");

        StackPane rowStackPane = new StackPane();
        rowStackPane.setPrefWidth(750);
        rowStackPane.setPrefHeight(200);

        Rectangle rowBackgroundRect = new Rectangle();
        rowBackgroundRect.setWidth(750);
        rowBackgroundRect.setHeight(200);
        rowBackgroundRect.setArcWidth(10);
        rowBackgroundRect.setArcHeight(10);
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#4027ff", 0.4)), new Stop(1, Color.web("#ada3f7", 0.4)));
        rowBackgroundRect.setFill(linearGradient);
        rowBackgroundRect.setStroke(Color.web("#000000"));

        HBox rowHBoxContent = new HBox();
        rowHBoxContent.setPrefWidth(750);
        rowHBoxContent.setPrefHeight(200);

        VBox foodImageContainer = new VBox();
        //ImageView rowImageView = new ImageView(new Image("file:src/main/resources/food-images/" + food.getName() + ".jpg", 175, 125, false, false));
        ImageView rowImageView = new ImageView("file:src/main/resources/food-images/" + food.getName() + ".jpg");
        if (rowImageView.getImage().isError())
        {
            rowImageView = new ImageView("file:src/main/resources/assets/Burger.jpg");
        }
        rowImageView.setFitWidth(250);
        rowImageView.setFitHeight(180);
        rowImageView.minWidth(250);
        rowImageView.minHeight(180);
        rowImageView.maxWidth(250);
        rowImageView.maxHeight(180);
        rowImageView.setPreserveRatio(false);
        foodImageContainer.setPadding(new Insets(10, 0, 0, 10));
        foodImageContainer.getChildren().addAll(rowImageView);

        VBox foodDetailsContainer = new VBox();
        foodDetailsContainer.setPadding(new Insets(10, 0, 0, 30));
        foodDetailsContainer.setPrefWidth(548);
        foodDetailsContainer.setPrefHeight(180);
        foodDetailsContainer.setSpacing(12);

        Label foodNameLabel = new Label(food.getName());
        foodNameLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-font-family: Calibri;");
        foodNameLabel.setMinWidth(450);

        HBox foodOtherDetailsContainer = new HBox();

        VBox foodDetailsDescriptionContainer = new VBox();
        foodDetailsDescriptionContainer.setPrefWidth(140);
        foodDetailsDescriptionContainer.setPrefHeight(120);
        foodDetailsDescriptionContainer.setSpacing(10);

        Label foodCategoryDescriptorLabel = new Label("Category : ");
        foodCategoryDescriptorLabel.setStyle("-fx-font-size: 20px;-fx-font-weight: bold; -fx-font-family: Calibri;");

        Label foodPriceDescriptorLabel = new Label("Price : ");
        foodPriceDescriptorLabel.setStyle("-fx-font-size: 20px;-fx-font-weight: bold;  -fx-font-family: Calibri");

        Label foodTotalOrderDescriptorLabel = new Label("Total Order : ");
        foodTotalOrderDescriptorLabel.setStyle("-fx-font-size: 20px;-fx-font-weight: bold;  -fx-font-family: Calibri;");

        Label foodTotalProfitDescriptorLabel = new Label("Total Profit : ");
        foodTotalProfitDescriptorLabel.setStyle("-fx-font-size: 20px;-fx-font-weight: bold;  -fx-font-family: Calibri;");

        foodDetailsDescriptionContainer.getChildren().addAll(foodCategoryDescriptorLabel, foodPriceDescriptorLabel, foodTotalOrderDescriptorLabel, foodTotalProfitDescriptorLabel);

        VBox foodDetailsContentContainer = new VBox();
        foodDetailsContentContainer.setSpacing(10);

        Label foodCategoryContentLabel = new Label(food.getCategory());
        foodCategoryContentLabel.setStyle("-fx-font-size: 20px; -fx-font-family: Roboto;");

        Label foodPriceContentLabel = new Label(food.getPrice() + " $");
        foodPriceContentLabel.setStyle("-fx-font-size: 20; -fx-font-family: Roboto;");

        Label foodTotalOrderContentLabel = new Label(Integer.toString(totalOrderCount));
        foodTotalOrderContentLabel.setStyle("-fx-font-size: 20px; -fx-font-family: Roboto;");

        Label foodTotalProfitContentLabel = new Label(food.getPrice() * totalOrderCount + " $");
        foodTotalProfitContentLabel.setStyle("-fx-font-size: 20px; -fx-font-family: Roboto;");

        foodDetailsContentContainer.getChildren().addAll(foodCategoryContentLabel, foodPriceContentLabel, foodTotalOrderContentLabel, foodTotalProfitContentLabel);

        foodOtherDetailsContainer.getChildren().addAll(foodDetailsDescriptionContainer, foodDetailsContentContainer);

        foodDetailsContainer.getChildren().addAll(foodNameLabel, foodOtherDetailsContainer);

        rowHBoxContent.getChildren().addAll(foodImageContainer, foodDetailsContainer);

        rowStackPane.getChildren().addAll(rowBackgroundRect, rowHBoxContent);

        row.getChildren().add(rowStackPane);
        displayVBox.getChildren().add(row);
    }

    public void setApplication(RestaurantApplication application)
    {
        this.application = application;
    }
}
