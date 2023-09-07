package server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class ServerController implements Runnable
{
    @FXML
    public Label lastOperationText;
    public Label serverStatusText;
    public Label ipAddressText;
    public Label portText;
    public Button shutdownServerButton;
    public Button addRestaurantButton;

    // Application reference
    private ServerApplication application;
    public void setApplication(ServerApplication application)
    {
        this.application = application;
    }

    // Server Thread
    Thread thread;

    // Servers variables
    private int serverPort;
    private ServerSocket serverSocket;

    // Client Maps
    private HashMap<String, SocketWrapper> clientMap; // logged in clients

    public void init()
    {
        serverPort = 44444;
        clientMap = new HashMap<>();
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
        }
        catch (IOException e)
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
        }
        catch (UnknownHostException e)
        {
            System.err.println("Class : ServerController | Method : getIPAddressAndPort");
            ipAddressText.setText("IP Address : ERROR");
        }
    }

    public void updatePortText()
    {
        portText.setText("Port : " + serverPort);
    }

    // Server Thread related methods
    public void userLoginComplete()
    {

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
                new ServerLoginReadThread(socketWrapper, clientMap);

                // The following is necessary to update JavaFX UI components from user created threads
                // You can't update java UI components from user created threads
                // This ensures that the UI components are updated from the JavaFX Application Thread
                Platform.runLater(() -> {
                    updateLastOperationText("Server accepted a client.");
                });
                System.out.println("Server accepts a client...");
            }
        }
        catch (IOException e)
        {
            System.err.println("Class : ServerController | Method : init");
            System.err.println("Error : " + e.getMessage());
        }
    }
}