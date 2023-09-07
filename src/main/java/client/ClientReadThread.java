package client;

import server.SocketWrapper;

public class ClientReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;

    ClientReadThread(SocketWrapper socketWrapper)
    {
        this.socketWrapper = socketWrapper;
        thread = new Thread(this);
        thread.start();
    }

    public void run()
    {

    }
}