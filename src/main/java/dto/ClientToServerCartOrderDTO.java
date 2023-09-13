package dto;

import models.Food;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientToServerCartOrderDTO implements Serializable
{
    private int totalItems;
    private Double totalPrice;
    private ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList; // Map<Restaurant ID, Map<Food, Count>>

    public ClientToServerCartOrderDTO(ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList, int totalItems, Double totalPrice)
    {
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.cartFoodList = cartFoodList;
    }

    public ClientToServerCartOrderDTO(ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList)
    {
        this.totalItems = 0;
        this.totalPrice = 0.0;
        this.cartFoodList = cartFoodList;
    }

    public int getTotalItems()
    {
        return totalItems;
    }

    public void setTotalItems(int totalItems)
    {
        this.totalItems = totalItems;
    }

    public Double getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public ConcurrentHashMap<Integer, HashMap<Food, Integer>> getCartFoodList()
    {
        return cartFoodList;
    }

    public void setCartFoodList(ConcurrentHashMap<Integer, HashMap<Food, Integer>> cartFoodList)
    {
        this.cartFoodList = cartFoodList;
    }

    @Override
    public String toString()
    {
        return "ClientCartOrderDTO{" + "totalItems=" + totalItems + ", totalPrice=" + totalPrice + ", cartFoodList=" + cartFoodList.size() + '}';
    }
}
