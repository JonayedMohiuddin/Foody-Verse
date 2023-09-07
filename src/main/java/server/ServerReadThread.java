package server;

import dto.LoginRequestDTO;

import java.io.IOException;
import java.util.HashMap;

public class ServerReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;
    HashMap<String, SocketWrapper> clientMap;
    HashMap<String, SocketWrapper> anonymousClientMap;

    ServerReadThread(SocketWrapper socketWrapper, HashMap<String, SocketWrapper> clientMap, HashMap<String, SocketWrapper> anonymousClientMap)
    {
        this.socketWrapper = socketWrapper;
        this.clientMap = clientMap;
        this.anonymousClientMap = anonymousClientMap;
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
                    System.out.println("Login Request Received");
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
