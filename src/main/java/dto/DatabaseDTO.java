package dto;

import prototypes.Restaurant;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseDTO implements Serializable
{
    ConcurrentHashMap<Integer, Restaurant> restaurantList;
    Restaurant singleRestaurant;

    public DatabaseDTO(ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        this.restaurantList = restaurantList;
    }

    public DatabaseDTO(Restaurant singleRestaurant)
    {
        this.singleRestaurant = singleRestaurant;
    }

    public ConcurrentHashMap<Integer, Restaurant> getRestaurantList()
    {
        return restaurantList;
    }

    public void setRestaurantList(ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        this.restaurantList = restaurantList;
    }

    public Restaurant getSingleRestaurant()
    {
        return singleRestaurant;
    }

    public void setSingleRestaurant(Restaurant singleRestaurant)
    {
        this.singleRestaurant = singleRestaurant;
    }

    @Override
    public String toString()
    {
        if (singleRestaurant == null)
            return "RestaurantListDTO{" + "restaurantList=[Items=" + restaurantList.size() + "]}";
        if (restaurantList == null) return "RestaurantListDTO{" + "singleRestaurant=" + singleRestaurant + '}';
        return null;
    }
}
