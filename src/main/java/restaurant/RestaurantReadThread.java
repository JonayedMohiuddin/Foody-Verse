package restaurant;

import server.SocketWrapper;

import java.io.IOException;

public class RestaurantReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;
    String restaurantName;
    String threadName;

    RestaurantReadThread(RestaurantApplication restaurantApplication)
    {
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
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("Class : RestaurantReadThread | Method : run | While reading from socket");
            System.out.println(e.getMessage());
        }
        finally
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