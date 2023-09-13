package server;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import models.Restaurant;
import util.FileOperations;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

    // Observable Lists for GUI
    ObservableList<String> clients, restaurants, serverLog, restaurantCategories;

    public void addClientToListView(String client)
    {
        Platform.runLater(() -> {
            clients.add(client);
        });
    }

    public void removeClientFromListView(String client)
    {
        Platform.runLater(() -> {
            clients.remove(client);
        });
    }

    public void addRestaurantToListView(String restaurant)
    {
        Platform.runLater(() -> {
            restaurants.add(restaurant);
        });
    }

    public void removeRestaurantFromListView(String restaurant)
    {
        Platform.runLater(() -> {
            restaurants.remove(restaurant);
        });
    }

    // Initialization
    public void init()
    {
        serverPort = 44444;
        clientMap = new ConcurrentHashMap<>();
        restaurantMap = new ConcurrentHashMap<>();

        try
        {
            userInfos = FileOperations.readUserinfo();
            restaurantInfos = FileOperations.readRestaurantInfo();
            restaurantList = FileOperations.readRestaurants();
        }
        catch (IOException e)
        {
            System.out.println("Class : ServerController | Method : init");
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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutDownServerCleanup();
        }));

        thread = new Thread(this, "Server Thread");
        thread.start();
    }

    public void shutDownServerCleanup()
    {
        try
        {
            FileOperations.writeRestaurants(restaurantList);
        }
        catch (IOException e)
        {
            System.out.println("Class : ServerController | Method : shutDownServer | While writing restaurants to file");
            System.out.println("Error : " + e.getMessage());
        }
        try
        {
            FileOperations.writeUserinfo(userInfos);
        }
        catch (IOException e)
        {
            System.out.println("Class : ServerController | Method : shutDownServer | While writing userinfo to file");
            System.out.println("Error : " + e.getMessage());
        }

        try
        {
            serverSocket.close();
            serverStatusCircle.setStyle("-fx-text-fill: red");
            System.out.println("Server closed successfully.");
            log("Server closed successfully.");
        }
        catch (IOException e)
        {
            System.err.println("Class : ServerController | Method : shutdownServerButtonClicked");
            System.err.println("Error : " + e.getMessage());
        }
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

    public void updateClientCountDetails()
    {
        Platform.runLater(() -> {
            clientOnlineCount.setText(Integer.toString(clientMap.size()));
            restaurantOnlineCount.setText(Integer.toString(restaurantMap.size()));
            registeredClientLabel.setText(Integer.toString(userInfos.size()));
            registeredRestaurantLabel.setText(Integer.toString(restaurantInfos.size()));
        });
    }

    public void log(String log)
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
            System.err.println("Class : ServerController | Method : init");
            System.err.println("Error : " + e.getMessage());
        }
    }

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

        if(name.isEmpty() || priceCategory.isEmpty() || zipcode.isEmpty() || rating.isEmpty() || category.length == 0 || password.isEmpty())
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

            return;
        }

        double ratingDouble;
        try
        {
            ratingDouble = Double.parseDouble(rating);
        }
        catch (NumberFormatException e)
        {
            markErrorField(addRestaurantRatingTextField);
            return;
        }

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

        System.out.println("Restaurant Added : " + restaurant.toString());

        log("New Restaurant Registered : " + restaurant.toString());
        updateClientCountDetails();

//        try
//        {
//            FileOperations.writeRestaurants(restaurantList);
//        }
//        catch (IOException e)
//        {
//            System.err.println("Class : ServerController | Method : addRestaurantMenuAddButtonPressed");
//            System.err.println("Error : " + e.getMessage());
//        }

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
