package dto;

import java.io.Serializable;

public class ClientLoginRequestDTO implements Serializable
{
    private final String username;
    private final String password;

    public ClientLoginRequestDTO(String username, String password)
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
        return "LoginRequestDTO {" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
    }
}
