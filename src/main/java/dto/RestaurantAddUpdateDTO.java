package dto;

import models.Restaurant;

public class RestaurantAddUpdateDTO implements java.io.Serializable
{
    Restaurant restaurant;

    public RestaurantAddUpdateDTO(Restaurant restaurant)
    {
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant()
    {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant)
    {
        this.restaurant = restaurant;
    }

    @Override
    public String toString()
    {
        return "RestaurantAddUpdateDTO{" + "restaurant=" + restaurant + '}';
    }
}
