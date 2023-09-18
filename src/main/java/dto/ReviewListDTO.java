package dto;

import prototypes.Review;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ReviewListDTO implements Serializable
{
    ConcurrentHashMap<Integer, ArrayList<Review>> restaurantReviews;
    ArrayList<Review> reviews;

    public ReviewListDTO(ConcurrentHashMap<Integer, ArrayList<Review>> restaurantReviews)
    {
        this.restaurantReviews = restaurantReviews;
    }

    public ReviewListDTO(ArrayList<Review> reviews)
    {
        this.reviews = reviews;
    }

    public ArrayList<Review> getReviews()
    {
        return reviews;
    }

    public ConcurrentHashMap<Integer, ArrayList<Review>> getRestaurantReviews()
    {
        return restaurantReviews;
    }

    public void setReviews(ArrayList<Review> reviews)
    {
        this.reviews = reviews;
    }

    public void setRestaurantReviews(ConcurrentHashMap<Integer, ArrayList<Review>> restaurantReviews)
    {
        this.restaurantReviews = restaurantReviews;
    }
}
