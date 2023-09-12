package server;

import dto.*;
import models.Restaurant;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ServerReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;
    ServerController serverController;
    String clientName;
    int restaurantId; // if client is a restaurant
    int clientType;

    ServerReadThread(ServerController serverController, SocketWrapper socketWrapper)
    {
        this.socketWrapper = socketWrapper;
        this.serverController = serverController;
        this.clientType = ClientType.UNDEFINED;

        thread = new Thread(this, "UNDEFINED # Server Read Thread");
        thread.start();
    }

    public void run()
    {
        Object obj;

        try
        {
            while ((obj = socketWrapper.read()) != null)
            {
                // CLIENT LOGIN REQUEST RECEIVED
                if (obj instanceof ClientLoginRequestDTO)
                {
                    clientType = ClientType.CLIENT;

                    ClientLoginRequestDTO loginRequest = (ClientLoginRequestDTO) obj;
                    System.out.println(loginRequest);
                    // CHECK IF USERNAME AND PASSWORD MATCH
                    if (loginRequest.getPassword().equals(serverController.getUserInfos().get(loginRequest.getUsername())))
                    {
                        // CHECK IF CLIENT ALREADY LOGGED IN
                        if (serverController.getClientMap().get(loginRequest.getUsername()) != null)
                        {
                            System.out.println("Client already logged in");
                            socketWrapper.write(new LoginResponseDTO(false, "Client already logged in."));
                        }
                        else
                        {
                            clientName = loginRequest.getUsername();

                            System.out.println("Authentication successful " + clientName);
                            socketWrapper.write(new LoginResponseDTO(true, "Authentication Successful"));
                            clientName = loginRequest.getUsername();
                            thread.setName(clientName + " # Server Read Thread");
                            serverController.getClientMap().put(loginRequest.getUsername(), socketWrapper);
                            serverController.updateLastOperationTextThreadSafe("Client " + loginRequest.getUsername() + " logged in.");
                        }
                    }
                    else
                    {
                        System.out.println("Client login failed");
                        socketWrapper.write(new LoginResponseDTO(false, "Wrong username or password."));
                    }
                }
                // RESTAURANT LOGIN REQUEST RECEIVED
                else if (obj instanceof RestaurantLoginRequestDTO)
                {
                    clientType = ClientType.RESTAURANT;

                    RestaurantLoginRequestDTO loginRequest = (RestaurantLoginRequestDTO) obj;
                    System.out.println(loginRequest);
                    // CHECK IF USERNAME AND PASSWORD MATCH
                    if (loginRequest.getPassword().equals(serverController.getRestaurantInfos().get(loginRequest.getUsername())))
                    {
                        // CHECK IF RESTAURANT ALREADY LOGGED IN
                        if (serverController.getRestaurantMap().get(loginRequest.getUsername()) != null)
                        {
                            System.out.println("Restaurant already logged in");
                            socketWrapper.write(new LoginResponseDTO(false, "Authentication failed. Restaurant already logged in."));
                        }
                        else
                        {
                            clientName = loginRequest.getUsername();
                            thread.setName(clientName + " # Server Read Thread");

                            // GET RESTAURANT ID
                            for (Restaurant restaurant : serverController.getRestaurantList().values())
                            {
                                if (restaurant.getName().equals(loginRequest.getUsername()))
                                {
                                    restaurantId = restaurant.getId();
                                    break;
                                }
                            }

                            System.out.println("Authentication successful " + clientName);
                            socketWrapper.write(new LoginResponseDTO(true, "Authentication Successful"));
                            serverController.getRestaurantMap().put(loginRequest.getUsername(), socketWrapper);
                            serverController.updateLastOperationTextThreadSafe("Restaurant " + loginRequest.getUsername() + " logged in.");
                        }
                    }
                    else
                    {
                        System.out.println("Restaurant login failed");
                        socketWrapper.write(new LoginResponseDTO(false, "Wrong username or password."));
                    }
                }
                // CLIENT DATABASE REQUEST RECEIVED
                else if (obj instanceof DatabaseRequestDTO)
                {
                    DatabaseRequestDTO databaseRequestDTO = (DatabaseRequestDTO) obj;
                    System.out.println(databaseRequestDTO);

                    if (databaseRequestDTO.getRequestType() == DatabaseRequestDTO.RequestType.RESTAURANT_LIST)
                    {
                        // SEND THE WHOLE RESTAURANT LIST TO THE CLIENT
                        RestaurantListDTO restaurantListDTO = new RestaurantListDTO(serverController.getRestaurantList());
                        socketWrapper.write(restaurantListDTO);
                    }
                    else if (databaseRequestDTO.getRequestType() == DatabaseRequestDTO.RequestType.SINGLE_RESTAURANT)
                    {
                        // JUST SEND THE RESTAURANT WITH THE GIVEN ID - DONT SEND THE WHOLE LIST
                        ConcurrentHashMap<Integer, Restaurant> restaurantList = new ConcurrentHashMap<>();
                        restaurantList.put(restaurantId, serverController.getRestaurantList().get(restaurantId));
                        RestaurantListDTO restaurantListDTO = new RestaurantListDTO(restaurantList);
                        socketWrapper.write(restaurantListDTO);
                    }
                }
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            System.out.println("Class : ServerReadThread | Method : run | Error in " + thread.getName() + " while reading from socket");
            System.out.println("Error : " + e.getMessage());
            System.out.println("Closing connection with client");

            try
            {
                if (clientType == ClientType.CLIENT)
                {
                    serverController.getClientMap().remove(clientName);
                }
                else if (clientType == ClientType.RESTAURANT)
                {
                    serverController.getRestaurantMap().remove(clientName);
                }
                socketWrapper.closeConnection();
            }
            catch (IOException ex)
            {
                System.err.println("Class : ServerReadThread | Method : run | While closing connection");
                System.err.println("Error : " + ex.getMessage());
            }
        }
    }

    class ClientType
    {
        public static final int UNDEFINED = -1;
        public static final int CLIENT = 0;
        public static final int RESTAURANT = 1;
    }
}
