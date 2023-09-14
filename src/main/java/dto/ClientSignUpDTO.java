package dto;

import java.io.Serializable;

public class ClientSignUpDTO implements Serializable
{
    private String name;
    private String password;
    private boolean success;
    private String message;

    public ClientSignUpDTO(String name, String password)
    {
        this.name = name;
        this.password = password;
        this.success = false;
        this.message = "";
    }

    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean getSuccess()
    {
        return success;
    }
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "ClientSignUpDTO{" + "name='" + name + '\'' + ", password='" + password + '\'' + '}';
    }
}
