package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantManager
{
    private ArrayList<Restaurant> restaurantList;
    private int uniqueId;

    public RestaurantManager()
    {
        uniqueId = 0;
        restaurantList = new ArrayList<>();
    }

    public ArrayList<Restaurant> getRestaurantList()
    {
        return restaurantList;
    }

    public int generateUniqueId()
    {
        return uniqueId++;
    }

    ArrayList<Food> getFoodList()
    {
        ArrayList<Food> foodList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            foodList.addAll(restaurant.getFoodList());
        }
        return foodList;
    }

    // return  0 if success
    // return -1 if name exists
    // return -2 if id exists
    public int addRestaurant(Restaurant restaurant)
    {
        if (existsRestaurantName(restaurant.getName()))
        {
            return -1;
        }
        else if (existsRestaurantId(restaurant.getId()))
        {
            return -2;
        }
        else
        {
            restaurantList.add(restaurant);
            if (uniqueId <= restaurant.getId()) uniqueId = restaurant.getId() + 1;
            return 0;
        }
    }

    public boolean addFood(Food food)
    {
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getId() == food.getRestaurantId())
            {
                restaurant.addFood(food);
                return true;
            }
        }
        return false;
    }

    public int getRestaurantId(String name)
    {
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getName().equalsIgnoreCase(name))
            {
                return restaurant.getId();
            }
        }
        return -1;
    }

    public String getRestaurantName(int id)
    {
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getId() == id)
            {
                return restaurant.getName();
            }
        }
        return null;
    }

    public boolean existsRestaurantName(String name)
    {
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    public boolean existsRestaurantNameFuzzySearch(String name)
    {
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getName().toLowerCase().contains(name.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean existsRestaurantId(int id)
    {
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getId() == id)
            {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Restaurant> searchRestaurantByName(String name)
    {
        ArrayList<Restaurant> searchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getName().toLowerCase().contains(name.toLowerCase()))
            {
                searchList.add(restaurant);
            }
        }
        return searchList;
    }

    public ArrayList<Restaurant> searchRestaurantByScoreRange(double start, double end)
    {
        ArrayList<Restaurant> searchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            if (Double.compare(start, restaurant.getScore()) <= 0 && Double.compare(restaurant.getScore(), end) <= 0)
            {
                searchList.add(restaurant);
            }
        }
        return searchList;
    }

    public ArrayList<Restaurant> searchRestaurantByCategory(String category)
    {
        ArrayList<Restaurant> searchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            for (String restaurantCategory : restaurant.getCategories())
            {
                if (restaurantCategory.toLowerCase().contains(category.toLowerCase()))
                {
                    searchList.add(restaurant);
                }
            }
        }
        return searchList;
    }

    public ArrayList<Restaurant> searchRestaurantByPrice(String price)
    {
        ArrayList<Restaurant> searchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getPrice().equals(price))
            {
                searchList.add(restaurant);
            }
        }
        return searchList;
    }

    public ArrayList<Restaurant> searchRestaurantByZipcode(String zipcode)
    {
        ArrayList<Restaurant> searchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getZipCode().equalsIgnoreCase(zipcode))
            {
                searchList.add(restaurant);
            }
        }
        return searchList;
    }

    public Map<String, ArrayList<String>> listRestaurantByCategories()
    {
        Map<String, ArrayList<String>> mapRestaurantNameAndCategory = new HashMap<>();

        for (Restaurant restaurant : restaurantList)
        {
            for (String category : restaurant.getCategories())
            {
                ArrayList<String> list;
                if (mapRestaurantNameAndCategory.containsKey(category))
                {
                    list = mapRestaurantNameAndCategory.get(category);
                }
                else
                {
                    list = new ArrayList<>();
                }
                list.add(restaurant.getName());
                mapRestaurantNameAndCategory.put(category, list);
            }
        }

        return mapRestaurantNameAndCategory;
    }

    ArrayList<Food> searchFoodByName(String name)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            for (Food food : restaurant.getFoodList())
            {
                if (food.getName().toLowerCase().contains(name.toLowerCase()))
                {
                    foodSearchList.add(food);
                }
            }
        }
        return foodSearchList;
    }

    ArrayList<Food> searchFoodByNameAndRestaurantName(String name, String restaurantName)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getName().toLowerCase().contains(restaurantName.toLowerCase()))
            {
                for (Food food : restaurant.getFoodList())
                {
                    if (food.getName().toLowerCase().contains(name.toLowerCase()))
                    {
                        foodSearchList.add(food);
                    }
                }
            }
        }
        return foodSearchList;
    }

    ArrayList<Food> searchFoodByCategory(String category)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            for (Food food : restaurant.getFoodList())
            {
                if (food.getCategory().toLowerCase().contains(category.toLowerCase()))
                {
                    foodSearchList.add(food);
                }
            }
        }
        return foodSearchList;
    }

    ArrayList<Food> searchFoodByCategoryAndRestaurantName(String category, String restaurantName)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getName().toLowerCase().contains(restaurantName.toLowerCase()))
            {
                for (Food food : restaurant.getFoodList())
                {
                    if (food.getCategory().toLowerCase().contains(category.toLowerCase()))
                    {
                        foodSearchList.add(food);
                    }
                }
            }
        }
        return foodSearchList;
    }

    ArrayList<Food> searchFoodByPriceRange(double start, double end)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            for (Food food : restaurant.getFoodList())
            {
                if (Double.compare(start, food.getPrice()) <= 0 && Double.compare(food.getPrice(), end) <= 0)
                {
                    foodSearchList.add(food);
                }
            }
        }
        return foodSearchList;
    }

    ArrayList<Food> searchFoodByPriceRangeAndRestaurantName(double start, double end, String restaurantName)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getName().toLowerCase().contains(restaurantName.toLowerCase()))
            {
                for (Food food : restaurant.getFoodList())
                {
                    if (Double.compare(start, food.getPrice()) <= 0 && Double.compare(food.getPrice(), end) <= 0)
                    {
                        foodSearchList.add(food);
                    }
                }
            }
        }
        return foodSearchList;
    }

    ArrayList<Food> searchCostliestFoodItemOfRestaurant(String restaurantName)
    {
        ArrayList<Food> foodSearchList = new ArrayList<>();

        for (Restaurant restaurant : restaurantList)
        {
            if (restaurant.getName().toLowerCase().contains(restaurantName.toLowerCase()))
            {
                double max = 0;
                for (Food food : restaurant.getFoodList())
                {
                    if (Double.compare(max, food.getPrice()) < 0)
                    {
                        max = food.getPrice();

                        foodSearchList.clear();
                        foodSearchList.add(food);
                    }
                    else if (Double.compare(max, food.getPrice()) == 0)
                    {
                        foodSearchList.add(food);
                    }
                }
            }
        }
        return foodSearchList;
    }

    Map<String, Integer> listRestaurantByFoodCount()
    {
        Map<String, Integer> mapRestaurantNameAndFoodCount = new HashMap<>();
        for (Restaurant restaurant : restaurantList)
        {
            mapRestaurantNameAndFoodCount.put(restaurant.getName(), restaurant.getFoodList().size());
        }
        return mapRestaurantNameAndFoodCount;
    }
}
