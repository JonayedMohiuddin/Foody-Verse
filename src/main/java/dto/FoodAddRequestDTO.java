package dto;

import models.Food;

public class FoodAddRequestDTO
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
