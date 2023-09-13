package restaurant;

import dto.ServerToRestaurantCartOrderDTO;
import javafx.application.Platform;
import models.Food;
import server.SocketWrapper;

import java.io.IOException;
import java.util.HashMap;

public class RestaurantReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;
    String restaurantName;
    String threadName;
    RestaurantApplication restaurantApplication;
    RestaurantHomeController restaurantHomeController;

    RestaurantReadThread(RestaurantApplication restaurantApplication, RestaurantHomeController restaurantHomeController)
    {
        this.restaurantApplication = restaurantApplication;
        this.restaurantHomeController = restaurantHomeController;
        this.socketWrapper = restaurantApplication.getSocketWrapper();
        this.restaurantName = restaurantApplication.username;
        this.threadName = restaurantName + " # ReadThread";

        thread = new Thread(this, threadName);
        thread.start();
    }

    public void run()
    {
        Object object;
        try
        {
            while (true)
            {
                System.out.println(threadName + " : Reading from socket");
                object = socketWrapper.read();

                if (object instanceof ServerToRestaurantCartOrderDTO serverToRestaurantCartOrderDTO)
                {
                    String username = serverToRestaurantCartOrderDTO.getUsername();
                    HashMap<Food, Integer> foodCountMap = serverToRestaurantCartOrderDTO.getCartFoodList();

                    System.out.println(threadName + " : Received cart order from server. User : " + username);
                    for (Food food : foodCountMap.keySet())
                    {
                        System.out.println(food.getName() + " : " + foodCountMap.get(food));
                    }
                    System.out.println();

                    Platform.runLater(() -> restaurantHomeController.updatePendingOrdersList(username, foodCountMap));

                    System.out.println(threadName + " : Received cart order from server. User : " + username);
                    System.out.println(threadName + " : " + serverToRestaurantCartOrderDTO);
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("Class : RestaurantReadThread | Method : run | While reading from socket");
            System.out.println(e.getMessage());
        } finally
        {
            try
            {
                socketWrapper.closeConnection();
                System.out.println("RestaurantReadThread: Connection closed");
            }
            catch (IOException e)
            {
                System.out.println("RestaurantReadThread: " + e.getMessage());
            }
        }
    }
}