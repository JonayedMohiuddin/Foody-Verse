package models;

import java.lang.reflect.Array;
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
            for(String cat : restaurant.getCategories())
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
            if (restaurant.getPrice().toLowerCase().contains(price.toLowerCase()))
            {
                searchResults.add(restaurant);
            }
        }

        return searchResults;
    }
}
