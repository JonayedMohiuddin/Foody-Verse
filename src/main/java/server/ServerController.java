package server;

import dto.RestaurantAddUpdateDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.WindowEvent;
import prototypes.Food;
import prototypes.Restaurant;
import prototypes.Review;
import misc.FileOperations;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ServerController implements Runnable
{
    @FXML
    public Label clientOnlineCount;
    public Label restaurantOnlineCount;
    public AnchorPane addRestaurantMenu;
    public TextField addRestaurantNameTextField;
    public ChoiceBox<String> priceCategoryChoiceBox;
    public TextField addRestaurantZipcodeTextField;
    public TextField addRestaurantRatingTextField;
    public TextField addRestaurantCategoryTextField;
    public Button addRestaurantAddCategory;
    public ListView<String> addRestaurantCategoryListView;
    public Button addRestaurantRemoveCategory;
    public Circle serverStatusCircle;
    public Label registeredRestaurantLabel;
    public ListView<String> clientListView;
    public ListView<String> serverLogListView;
    public ListView<String> restaurantListView;
    public Label registeredClientLabel;
    public PasswordField addRestaurantPasswordField;

    public Label ipAddressLabel;
    public Label portAddressLabel;

    // Server Thread
    Thread thread;

    // Observable Lists for GUI
    ObservableList<String> clients, restaurants, serverLog, restaurantCategories;

    // Servers variables
    private int serverPort;
    private ServerSocket serverSocket;

    // Client Maps
    private ConcurrentHashMap<String, SocketWrapper> clientMap;
    private ConcurrentHashMap<String, SocketWrapper> restaurantMap;

    // Modules
    private ConcurrentHashMap<Integer, Restaurant> restaurantList;

    // User and Restaurant Infos
    private ConcurrentHashMap<String, String> userInfos;
    private ConcurrentHashMap<String, String> restaurantInfos;

    // Server Application Reference
    private ServerApplication serverApplication;

    // Cart and Deliver list
    ConcurrentHashMap<Integer, HashMap<String, HashMap<Food, Integer>>> offlineRestaurantCartList;
    ConcurrentHashMap<Integer, HashMap<String, HashMap<Food, Integer>>> deliveryList;
    ConcurrentHashMap<Integer, ArrayList<Review>> restaurantReviews;

    // Getters and Setters
    public ServerSocket getServerSocket()
    {
        return serverSocket;
    }

    public ConcurrentHashMap<String, SocketWrapper> getClientMap()
    {
        return clientMap;
    }

    public ConcurrentHashMap<String, SocketWrapper> getRestaurantMap()
    {
        return restaurantMap;
    }

    public ConcurrentHashMap<Integer, Restaurant> getRestaurantList()
    {
        return restaurantList;
    }

    public ConcurrentHashMap<String, String> getUserInfos()
    {
        return userInfos;
    }

    public ConcurrentHashMap<String, String> getRestaurantInfos()
    {
        return restaurantInfos;
    }

    public ServerApplication getServerApplication()
    {
        return serverApplication;
    }

    public void setServerApplication(ServerApplication serverApplication)
    {
        this.serverApplication = serverApplication;
    }

    // Initialization
    public void init()
    {
        serverPort = 44444;
        clientMap = new ConcurrentHashMap<>();
        restaurantMap = new ConcurrentHashMap<>();
        offlineRestaurantCartList = new ConcurrentHashMap<>();
        deliveryList = new ConcurrentHashMap<>();
        restaurantReviews = new ConcurrentHashMap<>();

        try
        {
            userInfos = FileOperations.readUserinfo();
            restaurantInfos = FileOperations.readRestaurantInfo();
            restaurantList = FileOperations.readRestaurants();
            offlineRestaurantCartList = FileOperations.readPendingOrderList();
            deliveryList = FileOperations.readDeliveredOrderList();
            restaurantReviews = FileOperations.readReviewList();
        }
        catch (IOException e)
        {
            System.out.println("Class : ServerController | Method : init | while reading from file");
            System.out.println("Error : " + e.getMessage());
        }

        clients = FXCollections.observableArrayList();
        restaurants = FXCollections.observableArrayList();
        serverLog = FXCollections.observableArrayList();
        restaurantCategories = FXCollections.observableArrayList();

        clientListView.setItems(clients);
        restaurantListView.setItems(restaurants);
        serverLogListView.setItems(serverLog);
        addRestaurantCategoryListView.setItems(restaurantCategories);

        priceCategoryChoiceBox.getItems().addAll("$", "$$", "$$$");

        addRestaurantMenu.setVisible(false);

        serverApplication.getStage().setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                shutDownServerCleanup();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutDownServerCleanup();
        }));

        thread = new Thread(this, "Main Server Thread");
        thread.start();
    }

    public void shutDownServerCleanup()
    {
        System.out.println("Shutting down server...");
        log("Shutting down server...");

        System.out.println("Saving data to files...");
        log("Saving data to files...");

        try
        {
            FileOperations.writeRestaurants(restaurantList, restaurantInfos);
            FileOperations.writeUserinfo(userInfos);
            FileOperations.writePendingOrdersList(offlineRestaurantCartList);
            FileOperations.writeDeliveredOrderList(deliveryList);
            FileOperations.writeReviewList(restaurantReviews);

            for (Integer restaurantId : restaurantReviews.keySet())
            {
                System.out.println("Restaurant ID : " + restaurantId);
                for (Review review : restaurantReviews.get(restaurantId))
                {
                    System.out.println("Username : " + review.getUsername() + " | Message : " + review.getMessage());
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Class : ServerController | Method : shutDownServer | While writing to file");
            System.out.println("Error : " + e.getMessage());
        }

        System.out.println("Closing all client connections...");
        log("Closing all client connections...");

        try
        {
            serverSocket.close();
            serverStatusCircle.setStyle("-fx-text-fill: red");
            System.out.println("Server closed successfully.");
            log("Server closed successfully.");
        }
        catch (IOException e)
        {
            System.err.println("Class : ServerController | Method : shutdownServerButtonClicked | While closing server socket");
            System.err.println("Error : " + e.getMessage());
        }

        serverStatusCircle.setStyle("-fx-fill: red;");
    }

    public void shutdownServer(ActionEvent actionEvent)
    {
        shutDownServerCleanup();
    }

    public void addRestaurantButtonClicked(ActionEvent actionEvent)
    {
        resetFieldStyle();
        resetField();
        addRestaurantMenu.setVisible(true);
    }

    public void addRestaurantMenuCancelButtonPressed(ActionEvent event)
    {
        addRestaurantMenu.setVisible(false);
        resetFieldStyle();
        resetField();
    }

    public void updateIPandPort()
    {
        try
        {
            InetAddress localAddress = InetAddress.getLocalHost();
            int serverPort = serverSocket.getLocalPort();
            ipAddressLabel.setText(localAddress.getHostAddress());
        }
        catch (UnknownHostException e)
        {
            System.err.println("Class : ServerController | Method : getIPAddressAndPort | Could not get IP Address");
            ipAddressLabel.setText("ERROR");
        }
        portAddressLabel.setText(Integer.toString(serverPort));
    }

    public synchronized void addClientToListView(String client)
    {
        Platform.runLater(() -> {
            clients.add(client);
        });
    }

    // Multiple threads can access these methods at the same time so made them synchronized
    public synchronized void removeClientFromListView(String client)
    {
        Platform.runLater(() -> {
            clients.remove(client);
        });
    }

    public synchronized void addRestaurantToListView(String restaurant)
    {
        Platform.runLater(() -> {
            restaurants.add(restaurant);
        });
    }

    public synchronized void removeRestaurantFromListView(String restaurant)
    {
        Platform.runLater(() -> {
            restaurants.remove(restaurant);
        });
    }

    public synchronized void updateClientCountDetails()
    {
        Platform.runLater(() -> {
            clientOnlineCount.setText(Integer.toString(clientMap.size()));
            restaurantOnlineCount.setText(Integer.toString(restaurantMap.size()));
            registeredClientLabel.setText(Integer.toString(userInfos.size()));
            registeredRestaurantLabel.setText(Integer.toString(restaurantInfos.size()));
        });
    }

    public synchronized void log(String log)
    {
        Platform.runLater(() -> {
            serverLog.add(log);
            serverLogListView.scrollTo(serverLog.size() - 1);
        });
    }

    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(serverPort);
            updateIPandPort();
            updateClientCountDetails();
            log("Server created successfully.");
            System.out.println("Server created successfully.");

            while (true)
            {
                System.out.println("Server is waiting...");
                Socket clientSocket = serverSocket.accept();
                updateClientCountDetails();
                SocketWrapper socketWrapper = new SocketWrapper(clientSocket);

                System.out.println("Client Connection Request Received");
                new ServerReadThread(this, socketWrapper);
                System.out.println("Server made a connection.");

                log("Client Connection Established");
            }
        }
        catch (IOException e)
        {
//            System.err.println("Class : Main Server Thread | Method : Run");
//            System.err.println("Error : " + e.getMessage());
            System.out.println("Server closed successfully.");
        }
    }

    /// ADD RESTAURANT ///

    public void markErrorField(TextField textField)
    {
        textField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }

    public void resetField()
    {
        addRestaurantNameTextField.setText("");
        addRestaurantCategoryTextField.setText("");
        addRestaurantZipcodeTextField.setText("");
        addRestaurantRatingTextField.setText("");
        priceCategoryChoiceBox.setValue("");
        addRestaurantPasswordField.setText("");
    }

    public void resetFieldStyle()
    {
        addRestaurantNameTextField.setStyle("");
        addRestaurantCategoryTextField.setStyle("");
        addRestaurantZipcodeTextField.setStyle("");
        addRestaurantRatingTextField.setStyle("");
        priceCategoryChoiceBox.setStyle("");
        addRestaurantPasswordField.setStyle("");

        addRestaurantCategoryListView.setStyle("");
    }

    public void addRestaurantMenuAddButtonPressed(ActionEvent event)
    {
        resetFieldStyle();

        String name = addRestaurantNameTextField.getText();
        String password = addRestaurantPasswordField.getText();
        String priceCategory = priceCategoryChoiceBox.getValue();
        String zipcode = addRestaurantZipcodeTextField.getText();
        String rating = addRestaurantRatingTextField.getText();

        String category[] = new String[restaurantCategories.size()];
        for (int i = 0; i < restaurantCategories.size(); i++)
        {
            category[i] = restaurantCategories.get(i);
        }

        boolean isInputValid = true;

        if (name.isEmpty() || priceCategory.isEmpty() || zipcode.isEmpty() || rating.isEmpty() || category.length == 0 || password.isEmpty())
        {
            if (name.isEmpty())
            {
                markErrorField(addRestaurantNameTextField);
            }
            if (priceCategory.isEmpty())
            {
                priceCategoryChoiceBox.setStyle("-fx-border-width: 2px; -fx-border-color: red;");
            }
            if (zipcode.isEmpty())
            {
                markErrorField(addRestaurantZipcodeTextField);
            }
            if (rating.isEmpty())
            {
                markErrorField(addRestaurantRatingTextField);
            }
            if (category.length == 0)
            {
                addRestaurantCategoryListView.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }
            if (password.isEmpty())
            {
                addRestaurantPasswordField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }

            isInputValid = false;
        }

        double ratingDouble = 0;
        try
        {
            ratingDouble = Double.parseDouble(rating);
        }
        catch (NumberFormatException e)
        {
            markErrorField(addRestaurantRatingTextField);
            isInputValid = false;
        }

        if (isInputValid == false) return;

        int maxId = 0;
        for (Integer id : restaurantList.keySet())
        {
            if (id > maxId)
            {
                maxId = id;
            }
        }

        Restaurant restaurant = new Restaurant(maxId + 1, name, ratingDouble, priceCategory, zipcode, category);
        restaurantList.put(restaurant.getId(), restaurant);
        restaurantInfos.put(name, password);

        // SEND THIS UPDATE TO ALL CLIENTS
        RestaurantAddUpdateDTO restaurantAddUpdateDTO = new RestaurantAddUpdateDTO(restaurant);
        for (SocketWrapper socketWrapper : clientMap.values())
        {
            try
            {
                socketWrapper.write(restaurantAddUpdateDTO);
            }
            catch (IOException e)
            {
                System.err.println("Class : ServerController | Method : addRestaurantMenuAddButtonPressed | While sending RestaurantAddUpdateDTO to client");
                System.err.println("Error : " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Restaurant Added : " + restaurant.toString());

        log("New Restaurant Registered : " + restaurant.toString());
        updateClientCountDetails();

        addRestaurantMenu.setVisible(false);
    }

    public void addRestaurantAddCategoryButtonPressed(ActionEvent event)
    {
        String category = addRestaurantCategoryTextField.getText();
        if (category.isEmpty())
        {
            markErrorField(addRestaurantCategoryTextField);
        }
        else
        {
            restaurantCategories.add(category);
            addRestaurantCategoryTextField.setText("");
            addRestaurantCategoryTextField.setStyle("");
        }
    }

    public void addRestaurantRemoveCategoryButtonPressed(ActionEvent event)
    {
        String category = addRestaurantCategoryListView.getSelectionModel().getSelectedItem();
        if (category == null)
        {
            addRestaurantCategoryListView.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }
        else
        {
            restaurantCategories.remove(category);
            addRestaurantCategoryListView.setStyle("");
        }
    }
}

