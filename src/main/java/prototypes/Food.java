package prototypes;

import java.io.Serializable;

public class Food implements Serializable
{
    private int restaurantId;
    private String category;
    private String name;
    private double price;

    public Food(int restaurantId, String category, String name, double price)
    {
        this.restaurantId = restaurantId;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public void setRestaurantId(int restaurantId)
    {
        this.restaurantId = restaurantId;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public int getRestaurantId()
    {
        return restaurantId;
    }

    public String getCategory()
    {
        return category;
    }

    public String getName()
    {
        return name;
    }

    public double getPrice()
    {
        return price;
    }

    public void print(String restaurantName)
    {
        System.out.println("[" + restaurantName + "] : " + name + " (" + category + "), " + price + " $");
    }

    @Override
    public String toString()
    {
        return name + " (" + category + "), " + price + " $";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof Food)) return false;
        Food food = (Food) obj;
        // FOOD SAME CRITERIA : restaurantId, category, name. EXCLUDING PRICE : same food can not have different prices
        return restaurantId == food.getRestaurantId() && category.equals(food.getCategory()) && name.equals(food.getName());
    }

    @Override
    public int hashCode()
    {
        return restaurantId + category.hashCode() + name.hashCode();
    }
}
