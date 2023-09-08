package server;

import dto.*;
import javafx.scene.chart.PieChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ServerReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;
    ServerController serverController;

    ServerReadThread(ServerController serverController,SocketWrapper socketWrapper)
    {
        this.socketWrapper = socketWrapper;
        this.serverController = serverController;
        thread = new Thread(this, "Server Read Thread");
        thread.start();
    }

    public void run()
    {
        Object obj;

        try
        {
            while ((obj = socketWrapper.read()) != null)
            {
                if(obj instanceof ClientLoginRequestDTO)
                {
                    ClientLoginRequestDTO loginRequest = (ClientLoginRequestDTO) obj;
                    System.out.println(loginRequest);
                    if (serverController.getClientMap().get(loginRequest.getUsername()) != null)
                    {
                        System.out.println("Client already logged in");
                        socketWrapper.write(new LoginResponseDTO(false, "Client already logged in."));
                    }
                    else if (loginRequest.getPassword().equals(serverController.getUserInfos().get(loginRequest.getUsername())))
                    {
                        System.out.println("Client logged in");
                        socketWrapper.write(new LoginResponseDTO(true, "Authentication Successful"));
                        serverController.getClientMap().put(loginRequest.getUsername(), socketWrapper);
                        serverController.updateLastOperationTextThreadSafe("Client " + loginRequest.getUsername() + " logged in.");
                    }
                    else
                    {
                        System.out.println("Client login failed");
                        socketWrapper.write(new LoginResponseDTO(false, "Wrong username or password."));
                    }
                }
                else if(obj instanceof RestaurantLoginRequestDTO)
                {
                    RestaurantLoginRequestDTO loginRequest = (RestaurantLoginRequestDTO) obj;
                    System.out.println(loginRequest);
                    if (serverController.getRestaurantMap().get(loginRequest.getUsername()) != null)
                    {
                        System.out.println("Restaurant already logged in");
                        socketWrapper.write(new LoginResponseDTO(false, "Authentication failed. Restaurant already logged in."));
                    }
                    else if (loginRequest.getPassword().equals(serverController.getRestaurantInfos().get(loginRequest.getUsername())))
                    {
                        System.out.println("Restaurant logged in");
                        socketWrapper.write(new LoginResponseDTO(true, "Authentication Successful"));
                        serverController.getRestaurantMap().put(loginRequest.getUsername(), socketWrapper);
                        serverController.updateLastOperationTextThreadSafe("Restaurant " + loginRequest.getUsername() + " logged in.");
                    }
                    else
                    {
                        System.out.println("Restaurant login failed");
                        socketWrapper.write(new LoginResponseDTO(false, "Wrong username or password."));
                    }
                }
                else if(obj instanceof DatabaseRequestDTO)
                {
                    DatabaseRequestDTO databaseRequestDTO = (DatabaseRequestDTO) obj;
                    System.out.println(databaseRequestDTO);

                    if(databaseRequestDTO.getRequestType() == DatabaseRequestDTO.RequestType.RESTAURANT_LIST)
                    {
                        RestaurantListDTO restaurantListDTO = new RestaurantListDTO(serverController.getRestaurantList());
                        System.out.println(restaurantListDTO);
                        socketWrapper.write(restaurantListDTO);
                    }
                }
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            System.err.println("Class : ServerReadThread | Method : run");
            System.err.println("Error : " + e.getMessage());
        }
    }
}
