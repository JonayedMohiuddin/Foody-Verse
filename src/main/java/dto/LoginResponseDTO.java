package dto;

import java.io.Serializable;

public class LoginResponseDTO implements Serializable
{
    private boolean status;
    private String message;
//    private UserProfileDTO userProfileDTO; // Optional : Might add later.

    public LoginResponseDTO(boolean status, String message)
    {
        this.status = status;
        this.message = message;
    }

    public boolean getStatus()
    {
        return status;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public String toString()
    {
        return "LoginResponseDTO{" + "status=" + status + ", message='" + message + '\'' + '}';
    }
}
