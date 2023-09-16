package dto;

import models.Food;

import java.io.Serializable;
import java.util.HashMap;

public class ServerToRestaurantCartOrderDTO implements Serializable
{
    String username;
    HashMap<Food, Integer> cartFoodList; // Map<Restaurant ID, Map<Food, Count>>
    boolean isEmpty;

    public ServerToRestaurantCartOrderDTO(String username, HashMap<Food, Integer> cartFoodList)
    {
        this.username = username;
        this.cartFoodList = cartFoodList;
    }

    public ServerToRestaurantCartOrderDTO()
    {
        this.username = null;
        this.cartFoodList = null;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public HashMap<Food, Integer> getCartFoodList()
    {
        return cartFoodList;
    }

    public void setCartFoodList(HashMap<Food, Integer> cartFoodList)
    {
        this.cartFoodList = cartFoodList;
    }

    @Override
    public String toString()
    {
        return "ServerToRestaurantCartOrderDTO{" + "username='" + username + '\'' + ", cartFoodList=" + cartFoodList.size() + '}';
    }
}
