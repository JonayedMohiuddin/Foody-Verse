package restaurant;

import dto.DatabaseRequestDTO;
import dto.DatabaseDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import models.Food;
import models.Restaurant;

import java.io.IOException;
import java.util.ArrayList;

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

    int currentWindow = WindowType.UNDEFINED;
    private FXMLLoader homePageLoader;

    private RestaurantApplication application;

    public void setApplication(RestaurantApplication application)
    {
        this.application = application;
    }

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

    private Restaurant restaurant;
    private String restaurantName;

    public void init()
    {
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

        restaurantNameLabel.setText(restaurantName);

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

        addFoodListRow(new Food(1, "Spicy", "Burger", 120), 23);

        switchInternalWindow(WindowType.ORDERS);
        new RestaurantReadThread(application);
    }

    public void switchInternalWindow(int window)
    {
        System.out.println("Switching to window " + window);
        if (currentWindow == window) return;

        if (currentWindow != WindowType.UNDEFINED)
        {
            if (currentWindow == WindowType.ORDERS)
            {
                pendingOrdersButton.setStyle("");
            }
            else if (currentWindow == WindowType.FOOD_LIST)
            {
                foodListButton.setStyle("");
            }
            else if (currentWindow == WindowType.HISTORY)
            {
                historyButton.setStyle("");
            }
            else if (currentWindow == WindowType.ADD_FOODS)
            {
                addFoodButton.setStyle("");
            }
        }

        if (window == WindowType.ORDERS)
        {
            pendingOrdersButton.setStyle("-fx-background-color: #c7a84a; -fx-text-fill: #000000;");
            displayVBox.getChildren().clear();
            displayVBox.setSpacing(10);
            fillPendingRequest();
        }
        else if (window == WindowType.FOOD_LIST)
        {
            foodListButton.setStyle("-fx-background-color: #c7a84a; -fx-text-fill: #000000;");
            displayVBox.setSpacing(15);
            displayVBox.getChildren().clear();

            fillFoodList(application.getRestaurant().getFoodList());
        }
        else if (window == WindowType.HISTORY)
        {
            historyButton.setStyle("-fx-background-color: #c7a84a; -fx-text-fill: #000000;");
            displayVBox.setSpacing(10);
        }
        else if (window == WindowType.ADD_FOODS)
        {
            addFoodButton.setStyle("-fx-background-color: #c7a84a; -fx-text-fill: #000000;");
            displayVBox.setSpacing(10);
        }

        currentWindow = window;
    }

    public void addFoodButtonClicked(ActionEvent event)
    {
        switchInternalWindow(WindowType.ADD_FOODS);
    }

    public void historyButtonClicked(ActionEvent event)
    {
        switchInternalWindow(WindowType.HISTORY);
    }

    public void foodListButtonClicked(ActionEvent event)
    {
        switchInternalWindow(WindowType.FOOD_LIST);
    }

    public void pendingOrdersButtonClicked(ActionEvent event)
    {
        switchInternalWindow(WindowType.ORDERS);
    }

    public void logoutButtonClicked(ActionEvent event)
    {
    }

    public void fillPendingRequest()
    {

    }

    public void fillFoodList(ArrayList<Food> foodList)
    {
        for (Food food : foodList)
        {
            addFoodListRow(food, -1);
        }
    }

    public void addPendingOrderRow(Food food, int orderCount)
    {
        HBox row = new HBox();
        row.setPrefWidth(750);
        row.setPrefHeight(80);
        row.setMinWidth(750);
        row.setMinHeight(80);
        row.setStyle("-fx-border-insets: 0 20px 20px 0px;");

        StackPane rowStackPane = new StackPane();
        rowStackPane.setPrefWidth(750);
        rowStackPane.setPrefHeight(80);

        Rectangle rowBackgroundRect = new Rectangle();
        rowBackgroundRect.setWidth(750);
        rowBackgroundRect.setHeight(80);
        rowBackgroundRect.setArcWidth(10);
        rowBackgroundRect.setArcHeight(10);
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#4027ff", 0.4)), new Stop(1, Color.web("#ada3f7", 0.4)));
        rowBackgroundRect.setFill(linearGradient);
        rowBackgroundRect.setStroke(Color.web("#000000"));

        HBox rowHBoxContent = new HBox();
        rowHBoxContent.setPrefWidth(200);
        rowHBoxContent.setPrefHeight(100);

        VBox foodImageContainer = new VBox();
        ImageView rowImageView = new ImageView(new Image("file:src/main/resources/assets/Burger.jpg", 175, 125, false, false));
        rowImageView.setFitWidth(80);
        rowImageView.setFitHeight(60);
        rowImageView.minWidth(80);
        rowImageView.minHeight(60);
        rowImageView.maxWidth(80);
        rowImageView.maxHeight(60);
        rowImageView.setPreserveRatio(false);
        foodImageContainer.setPadding(new Insets(10, 0, 0, 10));
        foodImageContainer.getChildren().addAll(rowImageView);

        VBox foodDetailsContainer = new VBox();
        foodDetailsContainer.setPadding(new Insets(10, 0, 0, 20));
        foodDetailsContainer.setPrefWidth(319);
        foodDetailsContainer.setPrefHeight(80);
        foodDetailsContainer.setSpacing(10);

        Label foodNameLabel = new Label(food.getName());
        foodNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: Calibri;");

        Label foodCategory = new Label(food.getCategory());
        foodCategory.setStyle("-fx-font-size: 15px; -fx-font-family: Roboto;");

        foodDetailsContainer.getChildren().addAll(foodNameLabel, foodCategory);

        VBox separatorContainer = new VBox();
        Rectangle separator = new Rectangle();
        separator.setWidth(2);
        separator.setHeight(70);
        separatorContainer.setPadding(new Insets(5, 0, 0, 0));
        separatorContainer.getChildren().add(separator);

        VBox orderDetailsContainer = new VBox();
        orderDetailsContainer.setPadding(new Insets(0, 0, 0, 15));
        orderDetailsContainer.setPrefWidth(160);
        orderDetailsContainer.setPrefHeight(80);
        orderDetailsContainer.setSpacing(10);
        orderDetailsContainer.setAlignment(Pos.CENTER);

        Label orderCountLabel = new Label("X " + orderCount);
        orderCountLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: Calibri;");

        Label orderPriceLabel = new Label(food.getPrice() * orderCount + "$");
        orderPriceLabel.setStyle("-fx-font-size: 18px; -fx-font-family: 'Roboto Black';");

        orderDetailsContainer.getChildren().addAll(orderCountLabel, orderPriceLabel);

        VBox orderButtonContainer = new VBox();
        orderButtonContainer.setPrefWidth(178);
        orderButtonContainer.setPrefHeight(80);
        orderButtonContainer.setAlignment(Pos.CENTER);

        Button acceptOrderButton = new Button("Deliver Order");
        acceptOrderButton.setPrefWidth(126);
        acceptOrderButton.setPrefHeight(38);
        acceptOrderButton.setStyle("-fx-font-size: 18px; -fx-font-family: 'Corbel';");

        orderButtonContainer.getChildren().add(acceptOrderButton);

        rowHBoxContent.getChildren().addAll(foodImageContainer, foodDetailsContainer, separatorContainer, orderDetailsContainer, orderButtonContainer);

        rowStackPane.getChildren().addAll(rowBackgroundRect, rowHBoxContent);



        row.getChildren().add(rowStackPane);
        displayVBox.getChildren().add(row);

//        StackPane rowStackPane = new StackPane();
//        rowStackPane.setPrefWidth(800);
//        rowStackPane.setPrefHeight(80);

//        Rectangle rowBackgroundRect = new Rectangle();
//        rowBackgroundRect.setWidth(800);
//        rowBackgroundRect.setHeight(80);
//        rowBackgroundRect.setArcWidth(10);
//        rowBackgroundRect.setArcHeight(10);
//        rowBackgroundRect.setFill(javafx.scene.paint.Color.valueOf(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.RED), new Stop(1, Color.BLUE))
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
        ImageView rowImageView = new ImageView(new Image("file:src/main/resources/assets/Burger.jpg", 175, 125, false, false));
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

//        StackPane rowStackPane = new StackPane();
//        rowStackPane.setPrefWidth(800);
//        rowStackPane.setPrefHeight(80);

//        Rectangle rowBackgroundRect = new Rectangle();
//        rowBackgroundRect.setWidth(800);
//        rowBackgroundRect.setHeight(80);
//        rowBackgroundRect.setArcWidth(10);
//        rowBackgroundRect.setArcHeight(10);
//        rowBackgroundRect.setFill(javafx.scene.paint.Color.valueOf(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.RED), new Stop(1, Color.BLUE))
    }

    public class WindowType
    {
        public static final int UNDEFINED = -1;
        public static final int ORDERS = 0;
        public static final int FOOD_LIST = 1;
        public static final int HISTORY = 2;
        public static final int ADD_FOODS = 3;
    }
}
