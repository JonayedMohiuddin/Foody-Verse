package models;

import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Restaurant implements Serializable
{
    private int id;
    private String name;
    private double score;
    private String price;
    private String zipCode;
    private ArrayList<String> categories;
    private final ArrayList<Food> foodList;

    public Restaurant(int id, String name, double score, String price, String zipCode, String[] categories)
    {
        this.id = id;
        this.name = name;
        this.score = score;
        this.price = price;
        this.zipCode = zipCode;
        this.categories = new ArrayList<>();
        this.categories.addAll(Arrays.asList(categories));
        foodList = new ArrayList<>();
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setScore(float score)
    {
        this.score = score;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public void setCategories(String[] categories)
    {
        this.categories = new ArrayList<>();
        this.categories.addAll(Arrays.asList(categories));
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public double getScore()
    {
        return score;
    }

    public String getPrice()
    {
        return price;
    }

    public String getZipcode()
    {
        return zipCode;
    }

    public ArrayList<String> getCategories()
    {
        return categories;
    }

    public ArrayList<Food> getFoodList()
    {
        return foodList;
    }

    public void addFood(Food newFood)
    {
        foodList.add(newFood);
    }

    public void removeFood(Food food)
    {
        foodList.remove(food);
    }

    public void print()
    {
        System.out.print("[" + id + "] " + name + " : Score = " + score + ", Price = " + price + ", Zip-Code = " + zipCode + ", Categories = ");
        for (int i = 0; i < categories.size(); i++)
        {
            System.out.print(categories.get(i));
            if (i != categories.size() - 1)
            {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public void printWithFoods()
    {
        System.out.print("[" + id + "] " + name + " : Score = " + score + ", Price = " + price + ", Zip-Code = " + zipCode + ", Categories = ");
        for (int i = 0; i < categories.size(); i++)
        {
            System.out.print(categories.get(i));
            if (i != categories.size() - 1)
            {
                System.out.print(", ");
            }
        }
        System.out.println();
        System.out.print("[Foods] :");

        for (int i = 0; i < foodList.size(); i++)
        {
            System.out.print(" ");
            foodList.get(i).print(name);
        }
        System.out.println();
    }
}