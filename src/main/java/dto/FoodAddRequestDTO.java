package dto;

import models.Food;

public class FoodAddRequestDTO implements java.io.Serializable
{
    Food food;

    public FoodAddRequestDTO(Food food)
    {
        this.food = food;
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
        return "FoodAddDTO{" + "food=" + food + '}';
    }
}
