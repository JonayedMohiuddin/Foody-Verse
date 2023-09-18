package prototypes;

import java.io.Serializable;

public class Review implements Serializable
{
    String message;
    String username;
    int restaurantId;
    int clientType;

    public static class ClientType
    {
        public static final int USER = 0;
        public static final int RESTAURANT = 1;
        public static final int ADMIN = 2;
    }

    public Review(String message, String username, int restaurantId, int clientType)
    {
        this.message = message;
        this.username = username;
        this.restaurantId = restaurantId;
        this.clientType = clientType;
    }

    public String getMessage()
    {
        return message;
    }

    public String getUsername()
    {
        return username;
    }

    public int getRestaurantId()
    {
        return restaurantId;
    }

    public int getClientType()
    {
        return clientType;
    }
}
