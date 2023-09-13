package dto;

import models.Food;
import models.Restaurant;

public class FoodAddDTO
{
    Restaurant restaurant;
    Food food;

    public FoodAddDTO(Restaurant restaurant, Food food)
    {
        this.restaurant = restaurant;
        this.food = food;
    }

    public Restaurant getRestaurant()
    {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant)
    {
        this.restaurant = restaurant;
    }

    public Food getFood()
    {
        return food;
    }

    public void setFood(Food food)
    {
        this.food = food;
    }

    @Override
    public String toString()
    {
        return "FoodAddDTO{" + "restaurant=" + restaurant + ", food=" + food + '}';
    }
}
