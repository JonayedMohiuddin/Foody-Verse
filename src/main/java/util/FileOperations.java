package util;

import models.Food;
import models.Restaurant;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class FileOperations
{
    private static final String RESTAURANTS_FILE_NAME = "src/main/resources/database/restaurants-info.txt";
    private static final String FOODS_FILE_NAME = "src/main/resources/database/foods-info.txt";
    private static final String USERS_INFO_FILE_NAME = "src/main/resources/database/users-info.txt";
    private static final String RESTAURANTS_INFO_FILE_NAME = "src/main/resources/database/restaurants-info.txt";

    // TEST CODE
    public static void main(String[] args)
    {
        ConcurrentHashMap<Integer, Restaurant> restaurants = new ConcurrentHashMap<>();
        try
        {
            restaurants = readRestaurants();
            for (Restaurant restaurant : restaurants.values())
            {
                restaurant.print();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // read user info and restaurant info

        ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> restaurantsInfo = new ConcurrentHashMap<>();
        try
        {
            users = readUserinfo();
            restaurantsInfo = readRestaurantInfo();

            for (String key : users.keySet())
            {
                System.out.println("[" + key + "] : [" + users.get(key) + "]");
            }

            for (String key : restaurantsInfo.keySet())
            {
                System.out.println("[" + key + "] : [" + restaurantsInfo.get(key) + "]");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // write test
        /*
        restaurants.put(1, new Restaurant(1, "test", 4.5, "$", "12345", new String[]{"a", "b", "c"}));
        restaurants.put(2, new Restaurant(2, "test2", 4.5, "$", "12345", new String[]{"a", "b", "c"}));

        for (Restaurant restaurant : restaurants.values())
        {
            restaurant.print();
        }

        try
        {
            writeRestaurants(restaurants);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        */
    }

    public static ConcurrentHashMap<Integer, Restaurant> readRestaurants() throws IOException
    {
        ConcurrentHashMap<Integer, Restaurant> restaurants = new ConcurrentHashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(RESTAURANTS_FILE_NAME));

        while (true)
        {
            String line = reader.readLine();
            if (line == null) break;

            String[] tokens = line.split(",");

            int categoryCount = tokens.length - 6;
            String[] categories = new String[categoryCount];
            System.arraycopy(tokens, 6, categories, 0, categoryCount);

            restaurants.put(Integer.parseInt(tokens[0]), new Restaurant(Integer.parseInt(tokens[0]), tokens[1], Double.parseDouble(tokens[3]), tokens[4], tokens[5], categories));
        }
        reader.close();

        ArrayList<Food> foods = readFoods();
        for (Food food : foods)
        {
            restaurants.get(food.getRestaurantId()).addFood(food);
        }

        return restaurants;
    }

    private static ArrayList<Food> readFoods() throws IOException
    {
        ArrayList<Food> foods = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(FOODS_FILE_NAME));
        while (true)
        {
            String line = reader.readLine();
            if (line == null) break;
            String[] tokens = line.split(",");
            if (tokens.length != 4)
            {
                System.err.println("Error in reading foods. Line : " + line);
                continue;
            }
//            foods.add(new Food(Integer.parseInt(tokens[0]), tokens[1], tokens[2], Double.parseDouble(tokens[3])));
            foods.add(new Food(Integer.parseInt(tokens[0]), tokens[2], tokens[1], Double.parseDouble(tokens[3])));
        }
        reader.close();

        return foods;
    }

    public static void writeRestaurants(ConcurrentHashMap<Integer, Restaurant> restaurants, ConcurrentHashMap<String, String> restaurantInfos) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(RESTAURANTS_FILE_NAME));
        for (Restaurant restaurant : restaurants.values())
        {
            writer.write(restaurant.getId() + "," + restaurant.getName() + "," + restaurantInfos.get(restaurant.getName()) + "," + restaurant.getScore() + "," + restaurant.getPrice() + "," + restaurant.getZipcode() + ",");
            for (int i = 0; i < restaurant.getCategories().size(); i++)
            {
                writer.write(restaurant.getCategories().get(i));
                if (i != restaurant.getCategories().size() - 1)
                {
                    writer.write(",");
                }
            }
            writer.write("\n");
        }
        writer.close();

        ArrayList<Food> foods = new ArrayList<>();
        for (Restaurant restaurant : restaurants.values())
        {
            foods.addAll(restaurant.getFoodList());
        }
        writeFoods(foods);
    }

    private static void writeFoods(ArrayList<Food> foods) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FOODS_FILE_NAME));
        for (Food food : foods)
        {
            writer.write(food.getRestaurantId() + "," + food.getName() + "," + food.getCategory() + "," + food.getPrice());
            writer.write("\n");
        }
        writer.close();
    }

    public static ConcurrentHashMap<String, String> readUserinfo() throws IOException
    {
        ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(USERS_INFO_FILE_NAME));
        while (true)
        {
            String line = reader.readLine();
            if (line == null) break;
            String[] tokens = line.split(",");
            users.put(tokens[0], tokens[1]);
        }
        reader.close();

        return users;
    }

    public static void writeUserinfo(ConcurrentHashMap<String, String> users) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_INFO_FILE_NAME));
        for (String username : users.keySet())
        {
            writer.write(username + "," + users.get(username));
            writer.write("\n");
        }
        writer.close();
    }

    public static ConcurrentHashMap<String, String> readRestaurantInfo() throws IOException
    {
        ConcurrentHashMap<String, String> restaurantInfos = new ConcurrentHashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(RESTAURANTS_FILE_NAME));
        while (true)
        {
            String line = reader.readLine();
            if (line == null) break;
            String[] tokens = line.split(",");
            restaurantInfos.put(tokens[1], tokens[2]);
        }
        reader.close();

        return restaurantInfos;
    }
}
