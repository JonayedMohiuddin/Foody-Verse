package util;

import models.Food;
import models.Restaurant;
import models.Review;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class FileOperations
{
    private static final String RESTAURANTS_FILE_NAME = "src/main/resources/database/restaurants-info.txt";
    private static final String FOODS_FILE_NAME = "src/main/resources/database/foods-info.txt";
    private static final String USERS_INFO_FILE_NAME = "src/main/resources/database/users-info.txt";
    private static final String RESTAURANTS_INFO_FILE_NAME = "src/main/resources/database/restaurants-info.txt";
    private static final String PENDING_ORDERS_FILE_NAME = "src/main/resources/database/pending-order-list.txt";
    private static final String DELIVERED_ORDERS_FILE_NAME = "src/main/resources/database/delivered-order-list.txt";
    private static final String REVIEW_LIST_FILE_NAME = "src/main/resources/database/reviews-list.txt";

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

    public static void writePendingOrdersList(ConcurrentHashMap<Integer, HashMap<String, HashMap<Food, Integer>>> pendingOrderRestaurantList) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(PENDING_ORDERS_FILE_NAME));
        for (Integer restaurantId : pendingOrderRestaurantList.keySet())
        {
            writer.write(restaurantId + "," + pendingOrderRestaurantList.get(restaurantId).size());
            writer.write("\n");

            for (String username : pendingOrderRestaurantList.get(restaurantId).keySet())
            {
                writer.write(username + "," + pendingOrderRestaurantList.get(restaurantId).get(username).size());
                writer.write("\n");

                for (Food food : pendingOrderRestaurantList.get(restaurantId).get(username).keySet())
                {
                    String foodName = food.getName();
                    String foodCategory = food.getCategory();
                    double foodPrice = food.getPrice();
                    int foodRestaurantId = food.getRestaurantId();
                    int foodQuantity = pendingOrderRestaurantList.get(restaurantId).get(username).get(food);

                    writer.write(foodName + "," + foodCategory + "," + foodPrice + "," + foodRestaurantId + "," + foodQuantity);
                    writer.write("\n");
                }
            }
        }
        writer.close();
    }

    public static void writeDeliveredOrderList(ConcurrentHashMap<Integer, HashMap<String, HashMap<Food, Integer>>> deliveredOrderRestaurantList) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(DELIVERED_ORDERS_FILE_NAME));
        for (Integer restaurantId : deliveredOrderRestaurantList.keySet())
        {
            writer.write(restaurantId + "," + deliveredOrderRestaurantList.get(restaurantId).size());
            writer.write("\n");

            for (String username : deliveredOrderRestaurantList.get(restaurantId).keySet())
            {
                writer.write(username + "," + deliveredOrderRestaurantList.get(restaurantId).get(username).size());
                writer.write("\n");

                for (Food food : deliveredOrderRestaurantList.get(restaurantId).get(username).keySet())
                {
                    String foodName = food.getName();
                    String foodCategory = food.getCategory();
                    double foodPrice = food.getPrice();
                    int foodRestaurantId = food.getRestaurantId();
                    int foodQuantity = deliveredOrderRestaurantList.get(restaurantId).get(username).get(food);

                    writer.write(foodName + "," + foodCategory + "," + foodPrice + "," + foodRestaurantId + "," + foodQuantity);
                    writer.write("\n");
                }
            }
        }
        writer.close();
    }

    public static ConcurrentHashMap<Integer, HashMap<String, HashMap<Food, Integer>>> readPendingOrderList() throws IOException
    {
        ConcurrentHashMap<Integer, HashMap<String, HashMap<Food, Integer>>> pendingOrderRestaurantList = new ConcurrentHashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(PENDING_ORDERS_FILE_NAME));
        while (true)
        {
            String line = reader.readLine();
            if (line == null) break;
            String[] tokens = line.split(",");

            int restaurantId = Integer.parseInt(tokens[0]);
            int userCount = Integer.parseInt(tokens[1]);

            HashMap<String, HashMap<Food, Integer>> userOrderList = new HashMap<>();

            for (int i = 0; i < userCount; i++)
            {
                line = reader.readLine();
                tokens = line.split(",");

                String username = tokens[0];
                int foodCount = Integer.parseInt(tokens[1]);

                HashMap<Food, Integer> foodList = new HashMap<>();

                for (int j = 0; j < foodCount; j++)
                {
                    line = reader.readLine();
                    tokens = line.split(",");

                    String foodName = tokens[0];
                    String foodCategory = tokens[1];
                    double foodPrice = Double.parseDouble(tokens[2]);
                    int foodRestaurantId = Integer.parseInt(tokens[3]);
                    int foodQuantity = Integer.parseInt(tokens[4]);

                    Food food = new Food(foodRestaurantId, foodCategory, foodName, foodPrice);

                    foodList.put(food, foodQuantity);
                }

                userOrderList.put(username, foodList);
            }

            pendingOrderRestaurantList.put(restaurantId, userOrderList);
        }
        reader.close();

        return pendingOrderRestaurantList;
    }

    public static ConcurrentHashMap<Integer, HashMap<String, HashMap<Food, Integer>>> readDeliveredOrderList() throws IOException
    {
        ConcurrentHashMap<Integer, HashMap<String, HashMap<Food, Integer>>> deliveredOrderRestaurantList = new ConcurrentHashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(DELIVERED_ORDERS_FILE_NAME));
        while (true)
        {
            String line = reader.readLine();
            if (line == null) break;
            String[] tokens = line.split(",");

            int restaurantId = Integer.parseInt(tokens[0]);
            int userCount = Integer.parseInt(tokens[1]);

            HashMap<String, HashMap<Food, Integer>> userOrderList = new HashMap<>();

            for (int i = 0; i < userCount; i++)
            {
                line = reader.readLine();
                tokens = line.split(",");

                String username = tokens[0];
                int foodCount = Integer.parseInt(tokens[1]);

                HashMap<Food, Integer> foodList = new HashMap<>();

                for (int j = 0; j < foodCount; j++)
                {
                    line = reader.readLine();
                    tokens = line.split(",");

                    String foodName = tokens[0];
                    String foodCategory = tokens[1];
                    double foodPrice = Double.parseDouble(tokens[2]);
                    int foodRestaurantId = Integer.parseInt(tokens[3]);
                    int foodQuantity = Integer.parseInt(tokens[4]);

                    Food food = new Food(foodRestaurantId, foodCategory, foodName, foodPrice);

                    foodList.put(food, foodQuantity);
                }

                userOrderList.put(username, foodList);
            }

            deliveredOrderRestaurantList.put(restaurantId, userOrderList);
        }
        reader.close();

        return deliveredOrderRestaurantList;
    }

    public static void writeReviewList(ConcurrentHashMap<Integer, ArrayList<Review>> restaurantReviews) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEW_LIST_FILE_NAME));
        for(Integer restaurantId : restaurantReviews.keySet())
        {
            writer.write(restaurantId + "," + restaurantReviews.get(restaurantId).size());
            writer.write("\n");

            for(Review review : restaurantReviews.get(restaurantId))
            {
                writer.write(review.getUsername() + "," + review.getRestaurantId() + "," + review.getClientType());
                writer.write("\n");
                writer.write(review.getMessage());
                writer.write("\n");
            }
        }
    }

    public static ConcurrentHashMap<Integer, ArrayList<Review>> readReviewList() throws IOException
    {
        ConcurrentHashMap<Integer, ArrayList<Review>> restaurantReviews = new ConcurrentHashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(REVIEW_LIST_FILE_NAME));

        while (true)
        {
            String line = reader.readLine();
            if (line == null) break;

            String[] tokens = line.split(",");

            int restaurantId = Integer.parseInt(tokens[0]);
            int reviewCount = Integer.parseInt(tokens[1]);

            ArrayList<Review> reviews = new ArrayList<>();

            for (int i = 0; i < reviewCount; i++)
            {
                line = reader.readLine();
                tokens = line.split(",");

                String username = tokens[0];
                int restaurantId2 = Integer.parseInt(tokens[1]);
                int clientType = Integer.parseInt(tokens[2]);

                line = reader.readLine();
                String message = line;

                Review review = new Review(message, username, restaurantId2, clientType);

                reviews.add(review);
            }

            restaurantReviews.put(restaurantId, reviews);
        }
        reader.close();

        return restaurantReviews;
    }
}
