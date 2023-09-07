package server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import models.Restaurant;
import util.FileOperations;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

public class ServerController implements Runnable
{
    @FXML
    public Label lastOperationText;
    public Label serverStatusText;
    public Label ipAddressText;
    public Label portText;
    public Button shutdownServerButton;
    public Button addRestaurantButton;
    // Server Thread
    Thread thread;
    // Application reference
    private ServerApplication application;
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

    public void setApplication(ServerApplication application)
    {
        this.application = application;
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

        thread = new Thread(this, "Server Thread");
        thread.start();
    }

    public void shutdownServerButtonClicked(ActionEvent actionEvent)
    {
        try
        {
            serverSocket.close();
            serverStatusText.setText("Server Offline");
            serverStatusText.setStyle("-fx-text-fill: red");
            updateLastOperationText("Server closed successfully.");
            System.out.println("Server closed successfully.");
        } catch (IOException e)
        {
            System.err.println("Class : ServerController | Method : shutdownServerButtonClicked");
            System.err.println("Error : " + e.getMessage());
        }
    }
    public void addRestaurantButtonClicked(ActionEvent actionEvent)
    {
    }
    public void updateLastOperationText(String lastOperation)
    {
        lastOperationText.setText("Last successful operation : " + lastOperation);
    }
    public void updateIPAdrressText()
    {
        try
        {
            InetAddress localAddress = InetAddress.getLocalHost();
            int serverPort = serverSocket.getLocalPort();
            ipAddressText.setText("IP Address : " + localAddress.getHostAddress());
        } catch (UnknownHostException e)
        {
            System.err.println("Class : ServerController | Method : getIPAddressAndPort");
            ipAddressText.setText("IP Address : ERROR");
        }
    }
    public void updatePortText()
    {
        portText.setText("Port : " + serverPort);
    }

    public void updateLastOperationTextThreadSafe(String lastOperation)
    {
        Platform.runLater(() -> {
            // The following is necessary to update JavaFX UI components from user created threads
            // You can't update java UI components from user created threads
            // This ensures that the UI components are updated from the JavaFX Application Thread
            updateLastOperationText(lastOperation);
        });
    }

    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(serverPort);
            updateIPAdrressText();
            updatePortText();
            updateLastOperationText("Server started successfully.");
            System.out.println("Server created successfully.");

            while (true)
            {
                System.out.println("Server is waiting...");

                Socket clientSocket = serverSocket.accept();
                SocketWrapper socketWrapper = new SocketWrapper(clientSocket);

                System.out.println("Client Connection Request Received");
                new ServerReadThread(this, socketWrapper);
                updateLastOperationTextThreadSafe("Server made a connection.");
                System.out.println("Server made a connection.");
            }
        } catch (IOException e)
        {
            System.err.println("Class : ServerController | Method : init");
            System.err.println("Error : " + e.getMessage());
        }
    }
}
