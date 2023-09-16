package server;

import dto.*;
import models.Food;
import models.Restaurant;

import java.io.IOException;
import java.util.HashMap;
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

        thread = new Thread(this, "SRT #U UNDEFINED");
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
                    System.out.println(thread.getName() + " : " + loginRequest);
                    // CHECK IF USERNAME AND PASSWORD MATCH
                    if (loginRequest.getPassword().equals(serverController.getUserInfos().get(loginRequest.getUsername())))
                    {
                        // CHECK IF CLIENT ALREADY LOGGED IN
                        if (serverController.getClientMap().get(loginRequest.getUsername()) != null)
                        {
                            System.out.println(thread.getName() + " : Client already logged in");
                            socketWrapper.write(new LoginResponseDTO(false, "Client already logged in."));
                        }
                        else
                        {
                            clientName = loginRequest.getUsername();

                            System.out.println(thread.getName() + " : Authentication successful " + clientName);
                            socketWrapper.write(new LoginResponseDTO(true, "Authentication Successful"));
                            clientName = loginRequest.getUsername();
                            thread.setName("SRT C " + clientName);

                            serverController.getClientMap().put(loginRequest.getUsername(), socketWrapper);

                            serverController.updateClientCountDetails();
                            serverController.addClientToListView(clientName);
                            serverController.log(clientName + " logged in.");
                        }
                    }
                    else
                    {
                        System.out.println(thread.getName() + " : Client login failed");
                        socketWrapper.write(new LoginResponseDTO(false, "Wrong username or password."));
                    }
                }
                // RESTAURANT LOGIN REQUEST RECEIVED
                else if (obj instanceof RestaurantLoginRequestDTO)
                {
                    clientType = ClientType.RESTAURANT;

                    RestaurantLoginRequestDTO loginRequest = (RestaurantLoginRequestDTO) obj;
                    System.out.println(thread.getName() + " : loginRequest");
                    // CHECK IF USERNAME AND PASSWORD MATCH
                    if (loginRequest.getPassword().equals(serverController.getRestaurantInfos().get(loginRequest.getUsername())))
                    {
                        // CHECK IF RESTAURANT ALREADY LOGGED IN
                        if (serverController.getRestaurantMap().get(loginRequest.getUsername()) != null)
                        {
                            System.out.println(thread.getName() + " : Restaurant already logged in");
                            socketWrapper.write(new LoginResponseDTO(false, "Authentication failed. Restaurant already logged in."));
                        }
                        else
                        {
                            clientName = loginRequest.getUsername();
                            thread.setName("SRT R " + clientName);

                            // GET RESTAURANT ID
                            restaurantId = -1;
                            for (Restaurant restaurant : serverController.getRestaurantList().values())
                            {
                                if (restaurant.getName().equals(loginRequest.getUsername()))
                                {
                                    restaurantId = restaurant.getId();
                                    break;
                                }
                            }

                            // CHECK IF RESTAURANT ID FOUND
                            if (restaurantId == -1)
                            {
                                System.out.println(thread.getName() + " : Restaurant ID not found");
                                socketWrapper.write(new LoginResponseDTO(false, "Authentication failed. Restaurant ID not found."));
                                continue;
                            }

                            System.out.println(thread.getName() + " : Authentication successful " + clientName);
                            socketWrapper.write(new LoginResponseDTO(true, "Authentication Successful"));

                            serverController.getRestaurantMap().put(loginRequest.getUsername(), socketWrapper);

                            serverController.updateClientCountDetails();
                            serverController.addRestaurantToListView(clientName);
                            serverController.log(clientName + " logged in.");
                        }
                    }
                    else
                    {
                        System.out.println(thread.getName() + " Restaurant login failed");
                        socketWrapper.write(new LoginResponseDTO(false, "Wrong username or password."));
                    }
                }
                // CLIENT DATABASE REQUEST RECEIVED
                else if (obj instanceof DatabaseRequestDTO databaseRequestDTO)
                {
                    System.out.println(thread.getName() + " : " + databaseRequestDTO);
                    serverController.log(clientName + " requested database");

                    if (databaseRequestDTO.getRequestType() == DatabaseRequestDTO.RequestType.RESTAURANT_LIST)
                    {
                        // SEND THE WHOLE RESTAURANT LIST TO THE CLIENT
                        DatabaseDTO databaseDTO = new DatabaseDTO(serverController.getRestaurantList());
                        socketWrapper.write(databaseDTO);
                        System.out.println(thread.getName() + " : Database response sent. " + databaseDTO);
                        serverController.log(clientName + " received database");
                    }
                    else if (databaseRequestDTO.getRequestType() == DatabaseRequestDTO.RequestType.SINGLE_RESTAURANT)
                    {
                        // JUST SEND THE RESTAURANT WITH THE GIVEN ID - DON'T SEND THE WHOLE LIST
                        DatabaseDTO databaseDTO = new DatabaseDTO(serverController.getRestaurantList().get(restaurantId));
                        socketWrapper.write(databaseDTO);
                        System.out.println(thread.getName() + " : Database response sent. " + databaseDTO);
                        serverController.log(clientName + " received database");
                    }
                    else
                    {
                        System.out.println(thread.getName() + " Unknown database request type");
                    }
                }
                // CLIENT TO SERVER CART ORDER RECEIVED -> SEND THE ORDER TO THE RESTAURANT
                else if (obj instanceof ClientToServerCartOrderDTO cartOrderDTO)
                {
                    serverController.log(clientName + " sent order");
                    System.out.println(thread.getName() + " : " + cartOrderDTO);
                    ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList = cartOrderDTO.getCartFoodList();

                    for (Integer restaurantId : cartFoodList.keySet())
                    {
                        String restaurantName = serverController.getRestaurantList().get(restaurantId).getName();
                        ServerToRestaurantCartOrderDTO serverToRestaurantCartOrderDTO = new ServerToRestaurantCartOrderDTO(clientName, cartFoodList.get(restaurantId));

                        if (serverController.getRestaurantMap().containsKey(restaurantName))
                        {
                            serverController.log("Sending order to restaurant " + restaurantName);
                            serverController.getRestaurantMap().get(restaurantName).write(serverToRestaurantCartOrderDTO);
                            System.out.println(thread.getName() + " : Order sent to restaurant " + restaurantName);
                            System.out.println(thread.getName() + " : " + serverToRestaurantCartOrderDTO);
                            System.out.println();
                        }
                        else
                        {
                            serverController.log("Restaurant " + restaurantName + " is offline. Could not send order.");
                            System.out.println(thread.getName() + " : Restaurant " + restaurantName + " is offline");

                            // save them at offline map
                            HashMap<Food, Integer> foodCount = cartFoodList.get(restaurantId);
                            HashMap<String, HashMap<Food, Integer>> userFoodCount = new HashMap<>();
                            userFoodCount.put(clientName, foodCount);
                            serverController.offlineRestaurantCartList.put(restaurantId, userFoodCount);
                        }
                    }
                }
                else if (obj instanceof RequestOfflinePendingOrDeliveryDataDTO requestOfflinePendingOrDeliveryDataDTO)
                {
                    serverController.log(clientName + " requested offline data");
                    if (clientType == ClientType.RESTAURANT)
                    {
                        // CHECK IF THERE ARE ANY ORDERS FOR THIS RESTAURANT
                        if (serverController.offlineRestaurantCartList.containsKey(restaurantId))
                        {
                            HashMap<String, HashMap<Food, Integer>> userFoodCount = serverController.offlineRestaurantCartList.get(restaurantId);
                            for (String userName : userFoodCount.keySet())
                            {
                                ServerToRestaurantCartOrderDTO serverToRestaurantCartOrderDTO = new ServerToRestaurantCartOrderDTO(userName, userFoodCount.get(userName));
                                socketWrapper.write(serverToRestaurantCartOrderDTO);
                                System.out.println(thread.getName() + " : Offline order sent to restaurant " + clientName);
                                System.out.println(thread.getName() + " : " + serverToRestaurantCartOrderDTO);
                                System.out.println();
                            }
                            serverController.offlineRestaurantCartList.remove(restaurantId);
                        }

                        socketWrapper.write(new StopDTO());
                    }
                    else if (clientType == ClientType.CLIENT)
                    {
                        for (Integer restaurantId : serverController.deliveryList.keySet())
                        {
                            if (serverController.deliveryList.get(restaurantId).containsKey(clientName))
                            {
                                DeliverDTO deliverDTO = new DeliverDTO(restaurantId, new ConcurrentHashMap<>());
                                deliverDTO.getDeliverList().put(clientName, serverController.deliveryList.get(restaurantId).get(clientName));
                                socketWrapper.write(deliverDTO);
                                System.out.println("Sending delivery data to " + clientName);
                                System.out.println();
                            }
                        }

                        socketWrapper.write(new StopDTO());
                    }
                }
                // FOOD ADD REQUEST RECEIVED -> ADD THE FOOD TO THE RESTAURANT
                else if (obj instanceof FoodAddRequestDTO foodAddRequestDTO)
                {
                    serverController.log("Food " + foodAddRequestDTO.getFood().getName() + " added to restaurant " + clientName);
                    System.out.println(thread.getName() + " : " + foodAddRequestDTO);

                    serverController.getRestaurantList().get(restaurantId).getFoodList().add(foodAddRequestDTO.getFood());

                    // SEND THE UPDATED FOOD LIST TO ALL CLIENTS
                    for (SocketWrapper socketWrapper : serverController.getClientMap().values())
                    {
                        socketWrapper.write(foodAddRequestDTO);
                    }
                }
                // SIGN UP REQUEST RECEIVED -> ADD THE CLIENT TO THE DATABASE
                else if (obj instanceof ClientSignUpDTO clientSignUpDTO)
                {
                    if (serverController.getUserInfos().containsKey(clientSignUpDTO.getName()))
                    {
                        clientSignUpDTO.setSuccess(false);
                        clientSignUpDTO.setMessage("Username already exists");
                    }
                    else
                    {
                        serverController.getUserInfos().put(clientSignUpDTO.getName(), clientSignUpDTO.getPassword());
                        serverController.updateClientCountDetails();

                        System.out.println(thread.getName() + " : " + clientSignUpDTO);
                        serverController.log("Client " + clientSignUpDTO.getName() + " signed up");

                        clientSignUpDTO.setSuccess(true);
                        clientSignUpDTO.setMessage("Sign up successful");
                    }

                    socketWrapper.write(clientSignUpDTO);
                }
                // RESRTAURANT DELIVERY DTO -> SEND THIS TO CORRESPONDING CLIENTS
                // AND UPDATE LOCAL DATABASE ACCORDINGLY
                else if (obj instanceof DeliverDTO deliverDTO)
                {
                    System.out.println("Order delivery request received from " + clientName);
                    serverController.log("Order delivery request received from " + clientName);

                    // SEND THE UPDATED FOOD LIST TO ALL CLIENTS
                    for (String username : deliverDTO.getDeliverList().keySet())
                    {
                        if (serverController.getClientMap().containsKey(username))
                        {
                            serverController.getClientMap().get(username).write(deliverDTO);
                        }
                    }

                    if (!serverController.deliveryList.containsKey(deliverDTO.getRestaurantId()))
                    {
                        serverController.deliveryList.put(deliverDTO.getRestaurantId(), new HashMap<>());
                    }
                    serverController.deliveryList.get(deliverDTO.getRestaurantId()).putAll(deliverDTO.getDeliverList());

//                    for (Integer restaurantId : serverController.deliveryList.keySet())
//                    {
//                        System.out.println("Restaurant " + restaurantId);
//                        for (String username : serverController.deliveryList.get(restaurantId).keySet())
//                        {
//                            System.out.println("Restaurant " + restaurantId + " -> " + username);
//                            for (Food food : serverController.deliveryList.get(restaurantId).get(username).keySet())
//                            {
//                                System.out.println("Restaurant " + restaurantId + " -> " + username + " -> " + food.getName() + " -> " + serverController.deliveryList.get(restaurantId).get(username).get(food));
//                            }
//                        }
//                    }
                }
                // CLIENT LOGGING OUT
                else if (obj instanceof LogoutDTO logoutDTO)
                {
                    System.out.println("Server : " + clientName + " requested to logout.");
                    System.out.println();
                    serverController.log(clientName + " logged out.");

                    if (clientType == ClientType.CLIENT)
                    {
                        socketWrapper.write(logoutDTO); // send logout dto back , which will be received by thread to stop the thread
                        serverController.getClientMap().remove(clientName);
                        serverController.updateClientCountDetails();
                        serverController.removeClientFromListView(clientName);
                    }
                    else if (clientType == ClientType.RESTAURANT)
                    {
                        socketWrapper.write(logoutDTO); // send logout dto back , which will be received by thread to stop the thread
                        serverController.getRestaurantMap().remove(clientName);
                        serverController.updateClientCountDetails();
                        serverController.removeRestaurantFromListView(clientName);
                    }

                    socketWrapper.closeConnection();
                    break;
                }
                else
                {
                    System.out.println(thread.getName() + " : Unknown/unexpected object received");
                    serverController.log(clientName + " sent unknown/unexpected object");
                }
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            serverController.log(clientName + " closed connection.");

            System.out.println(thread.getName() + " : Class : ServerReadThread | Method : run | Error in " + thread.getName() + " while reading/writing from socket");
            System.out.println(thread.getName() + " : Error : " + e.getMessage());
            System.out.println(thread.getName() + " : Closing connection with client");

            try
            {
                if (clientType == ClientType.CLIENT)
                {
                    serverController.getClientMap().remove(clientName);
                    serverController.updateClientCountDetails();
                    serverController.removeClientFromListView(clientName);
                }
                else if (clientType == ClientType.RESTAURANT)
                {
                    serverController.getRestaurantMap().remove(clientName);
                    serverController.updateClientCountDetails();
                    serverController.removeRestaurantFromListView(clientName);
                }
                socketWrapper.closeConnection();
            }
            catch (IOException ex)
            {
                System.err.println("Class : ServerReadThread | Method : run | While closing connection");
                System.err.println("Error : " + ex.getMessage());
            }
        } finally
        {
            try
            {
                if (socketWrapper.isClosed)
                {
                    socketWrapper.closeConnection();
                }
            }
            catch (IOException e)
            {
                System.out.println("Couldn't properly close connection.");
            }
        }
    }

    static class ClientType
    {
        public static final int UNDEFINED = -1;
        public static final int CLIENT = 0;
        public static final int RESTAURANT = 1;
    }
}
