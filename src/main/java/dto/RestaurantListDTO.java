package dto;

import models.Restaurant;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantListDTO implements Serializable
{
    ConcurrentHashMap<Integer, Restaurant> restaurantList;

    public RestaurantListDTO(ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        this.restaurantList = restaurantList;
    }

    public ConcurrentHashMap<Integer, Restaurant> getRestaurantList()
    {
        return restaurantList;
    }

    public void setRestaurantList(ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        this.restaurantList = restaurantList;
    }

    @Override
    public String toString()
    {
        return "RestaurantListDTO{" + "restaurantList=[Items=" + restaurantList.size() + "]}";
    }
}
