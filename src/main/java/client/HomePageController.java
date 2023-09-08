package client;

import dto.DatabaseRequestDTO;
import dto.RestaurantListDTO;
import javafx.event.ActionEvent;
import models.Restaurant;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class HomePageController
{
    ClientApplication application;
    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    ConcurrentHashMap<Integer, Restaurant> restauratnList;

    public void init()
    {
        DatabaseRequestDTO databaseRequestDTO = new DatabaseRequestDTO(DatabaseRequestDTO.RequestType.RESTAURANT_LIST);
        try
        {
            application.getSocketWrapper().write(databaseRequestDTO);
        }
        catch (IOException e)
        {
            System.err.println("Class : HomePageController | Method : init | While sending restaurant list request to server");
            System.err.println("Error : " + e.getMessage());
        }

        try
        {
            Object obj = application.getSocketWrapper().read();
            if (obj instanceof RestaurantListDTO)
            {
                RestaurantListDTO restaurantListDTO = (RestaurantListDTO) obj;
                restauratnList = restaurantListDTO.getRestaurantList();
                System.out.println("Restaurant List Received");
                System.out.println("Size : " + restauratnList.size());
                for (Restaurant restaurant : restauratnList.values())
                {
                    System.out.println("Restaurant Name : " + restaurant.getName());
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.err.println("Class : HomePageController | Method : init | While reading restaurant list from server");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void homePageLogOutButtonClicked(ActionEvent actionEvent)
    {

    }
}