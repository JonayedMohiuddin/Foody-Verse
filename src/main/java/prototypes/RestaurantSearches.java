package prototypes;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantSearches
{
    public static synchronized ArrayList<Restaurant> searchRestaurantsByName(String name, ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        ArrayList<Restaurant> searchResults = new ArrayList<>();

        for (Restaurant restaurant : restaurantList.values())
        {
            if (restaurant.getName().toLowerCase().contains(name.toLowerCase()))
            {
                searchResults.add(restaurant);
            }
        }

        return searchResults;
    }

    public static synchronized ArrayList<Restaurant> searchRestaurantsByCattegory(String category, ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        ArrayList<Restaurant> searchResults = new ArrayList<>();

        for (Restaurant restaurant : restaurantList.values())
        {
            for (String cat : restaurant.getCategories())
            {
                if (cat.toLowerCase().contains(category.toLowerCase()))
                {
                    searchResults.add(restaurant);
                }
            }
        }

        return searchResults;
    }

    public static synchronized ArrayList<Restaurant> searchRestaurantByPrice(String price, ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        ArrayList<Restaurant> searchResults = new ArrayList<>();

        for (Restaurant restaurant : restaurantList.values())
        {
            if (restaurant.getPrice().equals(price))
            {
                searchResults.add(restaurant);
            }
        }

        return searchResults;
    }

    public static synchronized ArrayList<Restaurant> searchRestaurantsByZipcode(String price, ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        ArrayList<Restaurant> searchResults = new ArrayList<>();

        for (Restaurant restaurant : restaurantList.values())
        {
            if (restaurant.getZipcode().equals(price))
            {
                searchResults.add(restaurant);
            }
        }

        return searchResults;
    }

    public static synchronized ArrayList<Restaurant> searchRestaurantsByRating(double min, double max, ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        ArrayList<Restaurant> searchResults = new ArrayList<>();

        for (Restaurant restaurant : restaurantList.values())
        {
            if (Double.compare(min, restaurant.getScore()) <= 0 && Double.compare(restaurant.getScore(), max) <= 0)
            {
                searchResults.add(restaurant);
            }
        }

        return searchResults;
    }

    public static synchronized ArrayList<Food> searchFoodByName(String score, ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        ArrayList<Food> searchResults = new ArrayList<>();

        for (Restaurant restaurant : restaurantList.values())
        {
            for (Food food : restaurant.getFoodList())
            {
                if (food.getName().toLowerCase().contains(score.toLowerCase()))
                {
                    searchResults.add(food);
                }
            }
        }

        return searchResults;
    }

    public static synchronized ArrayList<Food> searchFoodByCategory(String category, ConcurrentHashMap<Integer, Restaurant> restaurantList)
    {
        ArrayList<Food> searchResults = new ArrayList<>();

        for (Restaurant restaurant : restaurantList.values())
        {
            for (Food food : restaurant.getFoodList())
            {
                if (food.getCategory().toLowerCase().contains(category.toLowerCase()))
                {
                    searchResults.add(food);
                }
            }
        }
        return searchResults;
    }

    public static synchronized ArrayList<Food> searchFoodByName(String name, ArrayList<Food> foodList)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();
        for (Food food : foodList)
        {
            if (food.getName().toLowerCase().contains(name.toLowerCase()))
            {
                foodSearchList.add(food);

            }
        }
        return foodSearchList;
    }

    public static synchronized ArrayList<Food> searchFoodByCategory(String category, ArrayList<Food> foodList)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();
        for (Food food : foodList)
        {
            if (food.getCategory().toLowerCase().contains(category.toLowerCase()))
            {
                foodSearchList.add(food);
            }
        }
        return foodSearchList;
    }

    public static synchronized ArrayList<Food> searchFoodByPriceRange(double min, double max, ArrayList<Food> foodList)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();

        for (Food food : foodList)
        {
            if (Double.compare(min, food.getPrice()) <= 0 && Double.compare(food.getPrice(), max) <= 0)
            {
                foodSearchList.add(food);
            }
        }

        return foodSearchList;
    }
}
