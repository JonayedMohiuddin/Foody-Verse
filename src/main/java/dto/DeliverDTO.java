package dto;

import models.Food;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DeliverDTO implements Serializable
{
    int restaurantId;
    ConcurrentHashMap<String, HashMap<Food, Integer>> deliverList;

    public DeliverDTO(int restaurantId, ConcurrentHashMap<String, HashMap<Food, Integer>> deliverList)
    {
        this.restaurantId = restaurantId;
        this.deliverList = deliverList;
    }

    public int getRestaurantId()
    {
        return restaurantId;
    }

    public ConcurrentHashMap<String, HashMap<Food, Integer>> getDeliverList()
    {
        return deliverList;
    }

    @Override
    public String toString()
    {
        return "RestaurantToServerDeliverDTO{" + "restaurantId=" + restaurantId + ", deliverList=" + deliverList + '}';
    }
}
