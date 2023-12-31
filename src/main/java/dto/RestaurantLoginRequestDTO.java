package dto;

import java.io.Serializable;

public class RestaurantLoginRequestDTO implements Serializable
{
    private final String username;
    private final String password;

    public RestaurantLoginRequestDTO(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    @Override
    public String toString()
    {
        return "RestaurantLoginRequestDTO {" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
    }
}
