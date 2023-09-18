package dto;

import models.Review;

import java.io.Serializable;

public class NewReviewRequest implements Serializable
{
    Review review;

    public NewReviewRequest(Review review)
    {
        this.review = review;
    }

    public Review getReview()
    {
        return review;
    }

    public void setReview(Review review)
    {
        this.review = review;
    }
}
