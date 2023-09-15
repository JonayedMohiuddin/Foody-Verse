package client;

import dto.ClientToServerCartOrderDTO;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.Food;
import util.ImageTransitions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class ClientCartController
{
    public Label usernameLabel;
    public ListView<HBox> cartItemListView;

    //    ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList; // Map<Restaurant ID, Map<Food, Count>>
    double cartTotalPrice = 0;
    int cartTotalItems = 0;

    Image foodImageMedium;
    Image restaurantImageMedium;

    Image addButtonImage, removeButtonImage;

    Font robotoBoldFont20;
    Font robotoBoldFont15;
    Font robotoRegularFont20;
    Font robotoRegularFont15;
    Font robotoRegularFont12;
    Font robotoLightFont20;
    Font robotoLightFont15;
    DecimalFormat decimalFormat;
    private ClientApplication application;

    public void init()
    {
        addButtonImage = new Image("file:src/main/resources/assets/add-button-icon.png");
        removeButtonImage = new Image("file:src/main/resources/assets/remove-button-icon.png");

        decimalFormat = new DecimalFormat("#.##");

        foodImageMedium = new Image("file:src/main/resources/assets/Burger.jpg", 175, 125, false, false);
        restaurantImageMedium = new Image("file:src/main/resources/assets/RestaurantImage.jpg", 175, 125, false, false);

        robotoBoldFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Bold.ttf"), 15);
        robotoBoldFont20 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Bold.ttf"), 20);
        robotoLightFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Light.ttf"), 15);
        robotoLightFont20 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Light.ttf"), 20);
        robotoRegularFont12 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Regular.ttf"), 12);
        robotoRegularFont15 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Regular.ttf"), 15);
        robotoRegularFont20 = Font.loadFont(getClass().getResourceAsStream("/assets/RobotoFonts/Roboto-Regular.ttf"), 20);

        usernameLabel.setText(application.getUsername());

        cartItemListView.setStyle("-fx-selection-bar : false;");

        for (Integer restaurantID : application.cartFoodList.keySet())
        {
            for (Food food : application.cartFoodList.get(restaurantID).keySet())
            {
                cartTotalPrice += food.getPrice() * application.cartFoodList.get(restaurantID).get(food);
                cartTotalItems += application.cartFoodList.get(restaurantID).get(food);
            }
        }
        fillListView();
    }

    public void backButtonClicked(Event event)
    {
        System.out.println("Back button clicked");
        try
        {
            application.showHomePage(true);
        }
        catch (IOException e)
        {
            System.err.println("Class : ClientCartController| Method : backButtonClicked | Couldn't show home page");
            System.err.println(e.getMessage());
        }
    }

    public void fillListView()
    {
        for (Integer restaurantID : application.cartFoodList.keySet())
        {
            addRestaurantNameHeader(application.getRestaurantList().get(restaurantID).getName());
            for (Food food : application.cartFoodList.get(restaurantID).keySet())
            {
                addFoodToListView(food, application.cartFoodList.get(restaurantID).get(food));
            }
//            addRestaurantFooter();
        }
    }

    public void addRestaurantNameHeader(String restaurantName)
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
        for (HashMap<Food, Integer> foodCount : application.cartFoodList.values())
        {
            if (foodCount.containsKey(food))
            {
                foodCount.put(food, foodCount.get(food) + 1);
            }
        }
        foodCountLabel.setText("X " + application.cartFoodList.get(food.getRestaurantId()).get(food));
        String formattedFoodPrice = decimalFormat.format(food.getPrice() * application.cartFoodList.get(food.getRestaurantId()).get(food));
        foodPriceLabel.setText(formattedFoodPrice + " $");
    }

    private void cartItemRemoveButtonClicked(Food food, Label foodCountLabel, Label foodPriceLabel)
    {
        System.out.println("Remove button clicked");
        cartTotalItems--;
        cartTotalPrice -= food.getPrice();

        for (HashMap<Food, Integer> foodCount : application.cartFoodList.values())
        {
            if (foodCount.containsKey(food))
            {
                if (foodCount.get(food) == 1)
                {
                    application.cartFoodList.get(food.getRestaurantId()).remove(food);
                    cartItemListView.getItems().clear();
                    if (application.cartFoodList.get(food.getRestaurantId()).isEmpty())
                    {
                        application.cartFoodList.remove(food.getRestaurantId());
                    }
                    fillListView();
                    return;
                }
                else
                {
                    foodCount.put(food, foodCount.get(food) - 1);
                }
            }
        }

        foodCountLabel.setText("X " + application.cartFoodList.get(food.getRestaurantId()).get(food));
        String formattedFoodPrice = decimalFormat.format(food.getPrice() * application.cartFoodList.get(food.getRestaurantId()).get(food));
        foodPriceLabel.setText(formattedFoodPrice + " $");
    }

    public void addFoodToListView(Food food, int foodCount)
    {
        // <HBox>
        HBox row = new HBox();
        row.setMinWidth(580);

        // <StackPane>
        StackPane foodImageContainer = new StackPane();
        ImageView foodSmallImageView = new ImageView("file:src/main/resources/food-images/" + food.getName() + ".jpg");
        if(foodSmallImageView.getImage().isError())
        {
            foodSmallImageView = new ImageView(foodImageMedium);
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
//        Button removeButton = new Button("-");
//        removeButton.setFont(robotoBoldFont20);
//        removeButton.setPrefWidth(buttonSide);
//        removeButton.setPrefHeight(buttonSide);
//        removeButton.setMinHeight(buttonSide);
//        removeButton.setMinWidth(buttonSide);
//        removeButton.setMaxHeight(buttonSide);
//        removeButton.setMaxWidth(buttonSide);
//        removeButtonContainer.setAlignment(Pos.CENTER);

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

        Label foodPriceLabel = new Label(food.getPrice() * foodCount + " $");
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

    public void addRestaurantFooter()
    {
        HBox row = new HBox();
        row.setMinWidth(580);

        VBox totalItemsAndPriceContainer = new VBox();

        HBox totalItemsContainer = new HBox();
        Label totalItemsLabel = new Label("Total items : " + cartTotalItems);
        totalItemsLabel.setFont(robotoBoldFont15);
        totalItemsLabel.setMinWidth(100);
        totalItemsLabel.setPadding(new Insets(0, 0, 0, 0)); // top right bottom left
        totalItemsLabel.setAlignment(Pos.CENTER);
        totalItemsContainer.setAlignment(Pos.CENTER);
        totalItemsContainer.getChildren().add(totalItemsLabel);

        HBox totalPriceContainer = new HBox();
        Label totalPriceLabel = new Label("Total Price : " + decimalFormat.format(cartTotalPrice) + " $");
        totalPriceLabel.setFont(robotoBoldFont15);
        totalPriceLabel.setMinWidth(100);
        totalPriceLabel.setPadding(new Insets(0, 0, 0, 0)); // top right bottom left
        totalPriceLabel.setAlignment(Pos.CENTER);
        totalPriceContainer.setAlignment(Pos.CENTER);
        totalPriceContainer.getChildren().add(totalPriceLabel);

        totalItemsAndPriceContainer.setPadding(new Insets(0, 0, 0, 0)); // top right bottom left
        totalItemsAndPriceContainer.setAlignment(Pos.CENTER_RIGHT);
        totalItemsAndPriceContainer.getChildren().addAll(totalItemsContainer, totalPriceContainer);

        row.getChildren().add(totalItemsAndPriceContainer);

        cartItemListView.getItems().add(row);
    }

    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    public void orderAllButtonClicked(ActionEvent event)
    {
        System.out.println("Order all button clicked");

        try
        {
            ClientToServerCartOrderDTO clientToServerCartOrderDTO = new ClientToServerCartOrderDTO(application.cartFoodList, cartTotalItems, cartTotalPrice);
            application.getSocketWrapper().write(clientToServerCartOrderDTO);

            application.cartFoodList = new ConcurrentHashMap<>();

            cartItemListView.getItems().clear();
            fillListView();
            cartTotalItems = 0;
            cartTotalPrice = 0;

            System.out.println("Order sent to server");
        }
        catch (IOException e)
        {
            System.err.println("Class : ClientCartController | Method : orderAllButtonClicked | While ordering all items");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void buttonHoverEntered(MouseEvent event)
    {
        ImageTransitions.imageMouseHoverEntered(event);
    }

    public void buttonHoverExited(MouseEvent event)
    {
        ImageTransitions.imageMouseHoverExited(event);
    }
}
