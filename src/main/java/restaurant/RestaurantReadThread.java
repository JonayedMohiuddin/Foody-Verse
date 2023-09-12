package restaurant;

import server.SocketWrapper;

public class RestaurantReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;

    RestaurantReadThread(SocketWrapper socketWrapper)
    {
        this.socketWrapper = socketWrapper;
        thread = new Thread(this);
        thread.start();
    }

    public void run()
    {

    }
}