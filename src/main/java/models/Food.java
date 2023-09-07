package models;

public class Food
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
}
