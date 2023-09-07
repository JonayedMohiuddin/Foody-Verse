package dto;

import java.io.Serializable;

public class StopDTO implements Serializable
{
    private boolean status;

    public StopDTO(boolean status)
    {
        this.status = status;
    }

    public boolean getStatus()
    {
        return status;
    }
}
