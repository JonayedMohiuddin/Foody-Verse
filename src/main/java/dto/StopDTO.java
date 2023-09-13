package dto;

import java.io.Serializable;

public class StopDTO implements Serializable
{
    private final boolean status;

    public StopDTO(boolean status)
    {
        this.status = status;
    }

    public boolean getStatus()
    {
        return status;
    }

    @Override
    public String toString()
    {
        return "StopDTO {}";
    }
}
