package server;

import dto.ClientLoginRequestDTO;
import dto.RestaurantLoginRequestDTO;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ServerReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;
    ConcurrentHashMap<String, SocketWrapper> clientMap;
    ConcurrentHashMap<String, SocketWrapper> restaurantMap;

    ServerReadThread(SocketWrapper socketWrapper, ConcurrentHashMap<String, SocketWrapper> clientMap, ConcurrentHashMap<String, SocketWrapper> restaurantMap)
    {
        this.socketWrapper = socketWrapper;
        this.clientMap = clientMap;
        this.restaurantMap = restaurantMap;
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
                    System.out.println("Client Login Request Received");
                    System.out.println(loginRequest);
                }
                else if(obj instanceof RestaurantLoginRequestDTO)
                {
                    RestaurantLoginRequestDTO loginRequest = (RestaurantLoginRequestDTO) obj;
                    System.out.println("Restaurant Login Request Received");
                    System.out.println(loginRequest);
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
