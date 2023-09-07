package server;

import dto.LoginRequestDTO;

import java.io.IOException;
import java.util.HashMap;

public class ServerLoginReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;
    ServerController serverController;

    ServerLoginReadThread(SocketWrapper socketWrapper, ServerController serverController)
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
                if(obj instanceof LoginRequestDTO)
                {
                    LoginRequestDTO loginRequest = (LoginRequestDTO) obj;
                    System.out.print("Login Request Received, ");
                    System.out.println(loginRequest);
                }
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            System.err.println("Class : ServerLoginReadThread | Method : run");
            System.err.println("Error : " + e.getMessage());
        }
    }
}
