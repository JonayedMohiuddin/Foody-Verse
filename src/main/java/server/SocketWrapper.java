package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketWrapper
{
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public SocketWrapper(String ipAddress, int port) throws IOException
    {
        this.socket = new Socket(ipAddress, port);
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    SocketWrapper(Socket socket) throws IOException
    {
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    Object read() throws ClassNotFoundException, IOException
    {
        return objectInputStream.readUnshared();
    }

    public void write(Object obj) throws IOException
    {
        objectOutputStream.writeUnshared(obj);
    }

    public void closeConnection() throws IOException
    {
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();
    }
}
